package rtx.byazen.api.music;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;
import rtx.byazen.ByAzen;

/**
 * Streaming audio engine of the ByAzen music player.
 * <p>
 * Everything is decoded on the fly and written into a {@link SourceDataLine} - nothing is ever saved
 * to disk. MP3 (online radio and catalogue streams) goes through JLayer, WAV/AIFF/AU files through
 * {@code javax.sound}. The engine runs on its own daemon threads; every operation is guarded by a
 * playback "generation" counter, so switching tracks never blocks the game thread and a stale worker
 * can not touch the current playback.
 */
public final class MusicEngine {

    public enum State {
        IDLE,
        CONNECTING,
        PLAYING,
        PAUSED,
        ERROR
    }

    /** Что делать, когда трек закончился. */
    public enum Repeat {
        OFF("Без повтора"),
        ALL("Повтор списка"),
        ONE("Повтор трека");

        private final String label;

        Repeat(String label) {
            this.label = label;
        }

        public String label() {
            return this.label;
        }

        public Repeat next() {
            Repeat[] values = Repeat.values();
            return values[(this.ordinal() + 1) % values.length];
        }
    }

    private static final MusicEngine INSTANCE = new MusicEngine();
    /** Сколько раз пытаемся «догнать» живой поток, если декодер споткнулся о мусор в эфире. */
    private static final int MAX_RECONNECTS = 3;
    private static final long RECONNECT_DELAY_MS = 350L;
    /** Целевой RMS нормализации: разные станции звучат примерно одинаково громко. */
    private static final float TARGET_RMS = 0.16f;
    private static final String USER_AGENT = "ByAzen/1.3.0 (+https://github.com/gggvkvh405-rgb/ByAzen)";
    private static final HttpClient HTTP = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(12))
            .build();

    private final CopyOnWriteArrayList<MusicTrack> queue = new CopyOnWriteArrayList<MusicTrack>();
    private final AtomicLong generation = new AtomicLong();
    private volatile int index = -1;
    private volatile MusicTrack current;
    private volatile State state = State.IDLE;
    private volatile String detail = "";
    private volatile float volume = 0.7f;
    private volatile float gain = 0.7f;
    private volatile float level;
    private volatile long elapsedMs;
    private volatile boolean paused;
    private volatile boolean stopping;
    private volatile long connectingSince;
    private final CopyOnWriteArrayList<Session> sessions = new CopyOnWriteArrayList<Session>();
    private static final int MAX_SESSIONS = 4;
    private volatile String nowPlaying = "";
    private volatile Repeat repeat = Repeat.ALL;
    private volatile boolean shuffle;
    private volatile boolean normalize = true;
    private volatile float normGain = 1.0f;
    private volatile int crossfadeMs = 1200;
    private volatile boolean equalizerOn;
    private volatile int equalizerRevision = -1;
    private volatile int playbackRate = 44100;

    private MusicEngine() {
    }

    public static MusicEngine get() {
        return INSTANCE;
    }

    // ------------------------------------------------------------------ state for the ui

    public State state() {
        return this.state;
    }

    public String detail() {
        return this.detail;
    }

    public MusicTrack current() {
        return this.current;
    }

    /** Название трека, который прямо сейчас играет в эфире радиостанции (ICY-метаданные). */
    public String nowPlaying() {
        return this.nowPlaying;
    }

    public List<MusicTrack> queue() {
        return Collections.unmodifiableList(new ArrayList<MusicTrack>(this.queue));
    }

    public int index() {
        return this.index;
    }

    public long elapsedMs() {
        return this.elapsedMs;
    }

    public float level() {
        return this.level;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public boolean isPlaying() {
        return this.state == State.PLAYING || this.state == State.PAUSED;
    }

    public boolean isBusy() {
        return this.state == State.PLAYING || this.state == State.PAUSED || this.state == State.CONNECTING;
    }

    public float volume() {
        return this.volume;
    }

    public Repeat repeat() {
        return this.repeat;
    }

    public void setRepeat(Repeat value) {
        this.repeat = value == null ? Repeat.OFF : value;
    }

    public void cycleRepeat() {
        this.repeat = this.repeat.next();
    }

    public boolean shuffle() {
        return this.shuffle;
    }

    public void setShuffle(boolean value) {
        this.shuffle = value;
    }

    public void toggleShuffle() {
        this.shuffle = !this.shuffle;
    }

    public boolean normalize() {
        return this.normalize;
    }

    public void setNormalize(boolean value) {
        this.normalize = value;
        if (!value) {
            this.normGain = 1.0f;
        }
    }

    /** Текущий рабочий коэффициент нормализации (для индикации в интерфейсе). */
    public float normalizeGain() {
        return this.normGain;
    }

    /** 0..1 progress of the current track, or -1 for live streams. */
    public float progress() {
        MusicTrack track = this.current;
        if (track == null || track.isLive()) {
            return -1.0f;
        }
        return Math.min(1.0f, (float) this.elapsedMs / (float) track.durationMs());
    }

    // ------------------------------------------------------------------ commands

    public void setVolume(float value) {
        this.volume = Math.max(0.0f, Math.min(1.0f, value));
    }

    /**
     * Один активный поток плеера: своя звуковая линия, своя громкость и свой конверт появления.
     * <p>
     * Отдельная сессия нужна для кроссфейда (идея №4): пока новый трек плавно появляется, старый
     * ещё несколько секунд доигрывает свой хвост и сам закрывает линию. Два потока при этом звучат
     * одновременно, поэтому переход получается настоящим, а не «пауза — и новый трек».
     */
    private static final class Session {

        private final long token;
        private final MusicTrack track;
        private final int fadeMs;
        private final long startedAt = System.currentTimeMillis();
        private volatile SourceDataLine line;
        private volatile int lineChannels = 2;
        private volatile float gain = 0.08f;
        private volatile Equalizer.Processor processor;
        private volatile boolean orphan;
        private volatile long orphanUntil;
        private volatile boolean dead;

        Session(long token, MusicTrack track, int fadeMs) {
            this.token = token;
            this.track = track;
            this.fadeMs = Math.max(0, fadeMs);
        }

        /** Сессия ещё имеет право писать звук (в том числе затухающий хвост при кроссфейде). */
        boolean audible() {
            return !this.dead && (!this.orphan || System.currentTimeMillis() < this.orphanUntil);
        }

        /** Это по-прежнему текущий трек (а не хвост предыдущего). */
        boolean current() {
            return !this.dead && !this.orphan;
        }
    }

    /** Длительность кроссфейда в миллисекундах (0 — переключать мгновенно). */
    public int crossfadeMs() {
        return this.crossfadeMs;
    }

    public void setCrossfadeMs(int value) {
        this.crossfadeMs = Math.max(0, Math.min(8000, value));
    }

    public void play(List<MusicTrack> tracks, int startIndex) {
        if (tracks == null || tracks.isEmpty()) {
            return;
        }
        ArrayList<MusicTrack> copy = new ArrayList<MusicTrack>(tracks);
        this.queue.clear();
        this.queue.addAll(copy);
        this.startTrack(Math.max(0, Math.min(copy.size() - 1, startIndex)));
    }

    public void play(MusicTrack track) {
        if (track == null) {
            return;
        }
        List<MusicTrack> single = new ArrayList<MusicTrack>();
        single.add(track);
        this.play(single, 0);
    }

    /** Plays the given track inside its own list (used by the "play" buttons of the player window). */
    public void playFrom(List<MusicTrack> tracks, int startIndex, MusicTrack track) {
        if (track == null) {
            return;
        }
        ArrayList<MusicTrack> merged = new ArrayList<MusicTrack>();
        if (tracks != null) {
            merged.addAll(tracks);
        }
        if (!merged.contains(track)) {
            merged.add(track);
        }
        this.play(merged, merged.indexOf(track));
    }

    public void togglePause() {
        if (!this.isPlaying()) {
            if (this.current != null) {
                this.startTrack(this.index >= 0 ? this.index : 0);
            }
            return;
        }
        this.paused = !this.paused;
        this.state = this.paused ? State.PAUSED : State.PLAYING;
    }

    public void pause(boolean value) {
        if (this.isPlaying() && this.paused != value) {
            this.paused = value;
            this.state = value ? State.PAUSED : State.PLAYING;
        }
    }

    /** Stops playback immediately; never blocks the caller (the game thread included). */
    public void stop() {
        this.stopping = true;
        this.generation.incrementAndGet();
        for (Session session : this.sessions) {
            session.dead = true;
            this.closeSession(session);
        }
        this.sessions.clear();
        this.state = State.IDLE;
        this.detail = "";
        this.level = 0.0f;
        this.paused = false;
    }

    public void next() {
        this.advance(false);
    }

    public void previous() {
        if (this.shuffle && this.queue.size() > 1) {
            this.startTrack(this.randomIndex());
            return;
        }
        if (this.index > 0) {
            this.startTrack(this.index - 1);
            return;
        }
        if (this.current != null) {
            this.startTrack(Math.max(0, this.index));
        }
    }

    /** Переход к следующему треку: повтор, перемешивание, конец очереди. */
    private void advance(boolean automatic) {
        if (this.queue.isEmpty()) {
            this.stop();
            return;
        }
        if (this.repeat == Repeat.ONE && this.current != null && !automatic) {
            this.restart();
            return;
        }
        if (this.shuffle && this.queue.size() > 1) {
            this.startTrack(this.randomIndex());
            return;
        }
        if (this.index + 1 < this.queue.size()) {
            this.startTrack(this.index + 1);
            return;
        }
        if (this.repeat == Repeat.ALL || this.repeat == Repeat.ONE) {
            this.startTrack(0);
            return;
        }
        this.stop();
    }

    private int randomIndex() {
        int size = this.queue.size();
        if (size <= 1) {
            return 0;
        }
        int picked = this.index;
        for (int attempt = 0; attempt < 6 && picked == this.index; ++attempt) {
            picked = (int) (Math.random() * (double) size);
        }
        return Math.max(0, Math.min(size - 1, picked));
    }

    public void restart() {
        if (this.current != null) {
            this.startTrack(Math.max(0, this.index));
        }
    }

    /** Marks the playback as failed with a human readable reason. */
    public void fail(String reason) {
        this.detail = reason == null ? "неизвестная ошибка" : reason;
        this.state = State.ERROR;
        this.level = 0.0f;
        ByAzen.LOGGER.warn("[ByAzen] Music player: {}", this.detail);
    }

    public long connectingForMs() {
        return this.state == State.CONNECTING ? System.currentTimeMillis() - this.connectingSince : 0L;
    }

    // ------------------------------------------------------------------ playback

    private void startTrack(int newIndex) {
        MusicTrack track = newIndex >= 0 && newIndex < this.queue.size() ? this.queue.get(newIndex) : null;
        if (track == null) {
            return;
        }
        MusicTrack previous = this.current;
        boolean switching = previous != null && !previous.url().equals(track.url());
        // Кроссфейд включается только при живом проигрывании: при первом запуске или после паузы
        // трек просто появляется плавно.
        int fade = switching && this.state == State.PLAYING && !this.paused ? this.crossfadeMs : 0;
        this.pruneSessions(fade);
        for (Session session : this.sessions) {
            if (fade > 0) {
                // Старый поток доигрывает хвост и сам закрывает свою линию (идея №4).
                session.orphan = true;
                session.orphanUntil = System.currentTimeMillis() + fade + 250L;
            }
            else {
                session.dead = true;
                this.closeSession(session);
            }
        }
        this.stopping = false;
        long token = this.generation.incrementAndGet();
        Session session = new Session(token, track, fade);
        this.sessions.add(session);
        this.index = newIndex;
        this.current = track;
        this.elapsedMs = 0L;
        this.level = 0.0f;
        this.paused = false;
        this.detail = "";
        this.nowPlaying = "";
        this.state = State.CONNECTING;
        this.connectingSince = System.currentTimeMillis();
        Thread thread = new Thread(() -> this.run(track, session), "byazen-music");
        thread.setDaemon(true);
        thread.start();
    }

    /** Не даём накопиться звуковым линиям, если треки переключают очень быстро. */
    private void pruneSessions(int fade) {
        if (this.sessions.size() < MAX_SESSIONS) {
            return;
        }
        for (Session session : this.sessions) {
            if (session.orphan || fade == 0) {
                session.dead = true;
                this.closeSession(session);
                this.sessions.remove(session);
            }
        }
    }

    private void run(MusicTrack track, Session session) {
        long token = session.token;
        boolean finished = false;
        boolean opened = false;
        try (InputStream raw = this.openStream(track, token)) {
            if (raw == null) {
                return;
            }
            BufferedInputStream stream = new BufferedInputStream(raw, 1 << 16);
            int format = this.detectFormat(track, stream);
            if (format == FORMAT_OGG) {
                finished = this.playOgg(stream, session);
            }
            else if (format == FORMAT_MP3) {
                finished = this.playMp3(track, stream, session);
            }
            else {
                finished = this.playPcm(stream, session);
            }
            opened = session.line != null && session.current();
        }
        catch (Throwable throwable) {
            if (this.isCurrent(token) && !this.stopping) {
                this.fail(MusicEngine.describe(throwable));
            }
            return;
        }
        finally {
            this.closeSession(session);
            this.sessions.remove(session);
        }
        if (!this.isCurrent(token) || this.stopping || this.paused) {
            return;
        }
        if (this.state == State.ERROR) {
            return;
        }
        if (!opened) {
            this.fail(finished ? "Пустой поток" : "Не удалось декодировать аудио");
            return;
        }
        if (this.repeat == Repeat.ONE) {
            this.restart();
            return;
        }
        if (this.shuffle && this.queue.size() > 1) {
            this.advance(true);
            return;
        }
        if (this.index + 1 < this.queue.size()) {
            this.startTrack(this.index + 1);
            return;
        }
        if (this.repeat == Repeat.ALL) {
            this.startTrack(0);
            return;
        }
        this.state = State.IDLE;
        this.detail = "Поток завершён";
        this.level = 0.0f;
    }

    private boolean isCurrent(long token) {
        return token == this.generation.get();
    }

    private static final int FORMAT_PCM = 0;
    private static final int FORMAT_MP3 = 1;
    private static final int FORMAT_OGG = 2;

    /**
     * Определяет формат потока (идея №21): OGG узнаём по сигнатуре «OggS» в первых байтах, MP3 — по
     * расширению или кадру синхронизации, остальное отдаём {@code javax.sound} (WAV/AIFF/AU).
     */
    private int detectFormat(MusicTrack track, BufferedInputStream stream) {
        String extension = this.extension(track.url());
        if (extension.equals("ogg") || extension.equals("oga") || extension.equals("ogv") || extension.equals("opus")) {
            return FORMAT_OGG;
        }
        if (extension.equals("wav") || extension.equals("aiff") || extension.equals("aif") || extension.equals("au")) {
            return FORMAT_PCM;
        }
        byte[] head = new byte[4];
        int read = 0;
        try {
            stream.mark(head.length);
            read = stream.read(head, 0, head.length);
            stream.reset();
        }
        catch (Throwable ignored) {
            return extension.equals("mp3") ? FORMAT_MP3 : FORMAT_PCM;
        }
        if (read >= 4 && head[0] == 'O' && head[1] == 'g' && head[2] == 'g' && head[3] == 'S') {
            return FORMAT_OGG;
        }
        if (read >= 3 && head[0] == 'I' && head[1] == 'D' && head[2] == '3') {
            return FORMAT_MP3;
        }
        if (read >= 2 && (head[0] & 0xFF) == 0xFF && (head[1] & 0xE0) == 0xE0) {
            return FORMAT_MP3;
        }
        if (read >= 4 && head[0] == 'R' && head[1] == 'I' && head[2] == 'F' && head[3] == 'F') {
            return FORMAT_PCM;
        }
        if (read >= 4 && head[0] == 'F' && head[1] == 'O' && head[2] == 'R' && head[3] == 'M') {
            return FORMAT_PCM;
        }
        return extension.equals("mp3") || track.kind() == MusicTrack.Kind.RADIO || track.kind() == MusicTrack.Kind.TRACK
                ? FORMAT_MP3 : FORMAT_PCM;
    }

    private String extension(String url) {
        String lower = url == null ? "" : url.toLowerCase(Locale.ROOT);
        int query = lower.indexOf('?');
        if (query > 0) {
            lower = lower.substring(0, query);
        }
        int dot = lower.lastIndexOf('.');
        int slash = lower.lastIndexOf('/');
        return dot > slash ? lower.substring(dot + 1) : "";
    }

    /**
     * OGG Vorbis (идея №21): декодируется JOrbis — тем же декодером, которым Minecraft читает свои
     * звуки. Сэмплы идут в общую звуковую линию, поэтому эквалайзер, нормализация и кроссфейд
     * работают и здесь.
     */
    private boolean playOgg(BufferedInputStream stream, Session session) throws Exception {
        long token = session.token;
        return OggVorbisStream.decode(stream, (samples, length, rate, channels) -> {
            if (session.line == null && session.audible()) {
                this.openLine(session, rate, channels);
            }
            this.writeShort(samples, length, rate, channels, session);
        }, () -> !session.audible() || this.stopping || !this.isCurrent(token));
    }

    private InputStream openStream(MusicTrack track, long token) throws Exception {
        String url = track.url();
        if (url.isBlank()) {
            this.fail("Пустая ссылка");
            return null;
        }
        if (track.isLocal()) {
            return new FileInputStream(url);
        }
        HttpResponse<InputStream> response = HTTP.send(
                HttpRequest.newBuilder(URI.create(url))
                        .header("User-Agent", USER_AGENT)
                        .header("Accept", "*/*")
                        .header("Icy-MetaData", "1")
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() >= 400) {
            this.fail("Сервер ответил " + response.statusCode());
            return null;
        }
        if (!this.isCurrent(token)) {
            response.body().close();
            return null;
        }
        String icyHeader = response.headers().firstValue("icy-metaint").orElse("");
        int metaint = IcyStream.metadataInterval(icyHeader);
        if (metaint > 0 && track.isRadio()) {
            // Радиостанция подмешивает в поток блоки с названием трека — вырезаем их
            // и показываем «сейчас в эфире» (идея №1).
            return new IcyStream(response.body(), metaint, title -> {
                if (this.isCurrent(token)) {
                    this.nowPlaying = title;
                    this.detail = title;
                }
            });
        }
        return response.body();
    }

    /**
     * Decodes an MP3 stream frame by frame; returns true when the stream ended by itself.
     * <p>
     * Живые радиостанции иногда «спотыкаются»: декодер теряет синхронизацию, но поток ещё идёт.
     * Если после падения в буфере остались данные, соединение открывается заново - эфир продолжается,
     * а не обрывается.
     */
    private boolean playMp3(MusicTrack track, BufferedInputStream stream, Session session) throws Exception {
        long token = session.token;
        Bitstream bitstream = new Bitstream(stream);
        Decoder decoder = new Decoder();
        boolean reconnectable = track.kind() != MusicTrack.Kind.LOCAL;
        int reconnects = 0;
        long decodedFrames = 0L;
        int rate = 44100;
        int channels = 2;
        while (session.audible() && !this.stopping && !Thread.currentThread().isInterrupted()) {
            Header header;
            try {
                header = bitstream.readFrame();
            }
            catch (Throwable throwable) {
                header = null;
            }
            if (header == null) {
                if (!reconnectable || reconnects >= MAX_RECONNECTS || decodedFrames == 0L || !MusicEngine.hasBufferedData(stream)) {
                    return true;
                }
                ++reconnects;
                try {
                    bitstream.close();
                }
                catch (Throwable ignored) {
                }
                this.detail = "Переподключение к потоку…";
                Thread.sleep(RECONNECT_DELAY_MS);
                InputStream reopened = this.openStream(track, token);
                if (reopened == null || !session.audible() || this.stopping) {
                    return true;
                }
                stream = new BufferedInputStream(reopened, 1 << 16);
                bitstream = new Bitstream(stream);
                this.detail = "";
                continue;
            }
            ++decodedFrames;
            SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(header, bitstream);
            if (samples != null && samples.getBufferLength() > 0) {
                rate = decoder.getOutputFrequency();
                channels = decoder.getOutputChannels();
                if (session.line == null) {
                    this.openLine(session, rate, channels);
                }
                this.writeShort(samples.getBuffer(), samples.getBufferLength(), rate, channels, session);
            }
            bitstream.closeFrame();
        }
        try {
            bitstream.close();
        }
        catch (Throwable ignored) {
        }
        return false;
    }

    /** Есть ли ещё данные в буфере (значит поток не закончился, а просто потерял синхронизацию). */
    private static boolean hasBufferedData(BufferedInputStream stream) {
        try {
            return stream.available() > 0;
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    /** Decodes WAV/AIFF/AU (anything the JDK supports) into the same output line. */
    private boolean playPcm(BufferedInputStream stream, Session session) throws Exception {
        long token = session.token;
        try (AudioInputStream source = AudioSystem.getAudioInputStream(stream)) {
            AudioFormat base = source.getFormat();
            float rate = base.getSampleRate() <= 0.0f ? 44100.0f : base.getSampleRate();
            int channels = Math.max(1, base.getChannels());
            AudioFormat target = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, rate, 16, channels, channels * 2, rate, false);
            AudioInputStream pcm = AudioSystem.isConversionSupported(target, base)
                    ? AudioSystem.getAudioInputStream(target, source)
                    : source;
            byte[] buffer = new byte[8192];
            int read;
            while (session.audible() && !this.stopping && !Thread.currentThread().isInterrupted()
                    && (read = pcm.read(buffer, 0, buffer.length)) > 0) {
                if (session.line == null) {
                    this.openLine(session, (int) pcm.getFormat().getSampleRate(), pcm.getFormat().getChannels());
                }
                this.writeBytes(buffer, read, (int) pcm.getFormat().getSampleRate(), pcm.getFormat().getChannels(), session);
            }
        }
        return true;
    }

    private void openLine(Session session, int rate, int channelCount) {
        long token = session.token;
        if (!session.audible() || this.stopping) {
            return;
        }
        int sampleRate = rate <= 0 ? 44100 : rate;
        int channels = channelCount <= 0 ? 2 : channelCount;
        // Some mixers refuse mono lines - fall back to stereo and duplicate the samples.
        int[] candidates = channels == 2 ? new int[]{2} : new int[]{channels, 2};
        Throwable lastError = null;
        for (int candidate : candidates) {
            try {
                AudioFormat format = new AudioFormat((float) sampleRate, 16, candidate, true, false);
                SourceDataLine dataLine = AudioSystem.getSourceDataLine(format);
                dataLine.open(format, 1 << 17);
                dataLine.start();
                if (!session.audible() || this.stopping) {
                    dataLine.close();
                    return;
                }
                session.lineChannels = candidate;
                session.line = dataLine;
                this.playbackRate = sampleRate;
                Equalizer equalizer = Equalizer.get();
                this.equalizerOn = equalizer.enabled();
                session.processor = new Equalizer.Processor(sampleRate, candidate);
                if (session.current() && this.state == State.CONNECTING) {
                    this.state = State.PLAYING;
                    this.detail = "";
                }
                return;
            }
            catch (Throwable throwable) {
                lastError = throwable;
            }
        }
        session.line = null;
        if (session.current()) {
            this.fail("Нет доступа к звуковому устройству"
                    + (lastError == null ? "" : ": " + lastError.getClass().getSimpleName()));
        }
    }

    /** Закрывает линию одной сессии (хвост кроссфейда уходит тихо, без исключений). */
    private void closeSession(Session session) {
        SourceDataLine dataLine = session.line;
        session.line = null;
        if (dataLine == null) {
            return;
        }
        try {
            dataLine.stop();
            dataLine.flush();
            dataLine.close();
        }
        catch (Throwable ignored) {
        }
    }

    /** Обновляет эквалайзер, когда настройки поменяли прямо во время проигрывания. */
    private void ensureEqualizer() {
        Equalizer equalizer = Equalizer.get();
        boolean enabled = equalizer.enabled();
        int revision = equalizer.revision();
        if (enabled == this.equalizerOn && revision == this.equalizerRevision) {
            return;
        }
        this.equalizerOn = enabled;
        this.equalizerRevision = revision;
        for (Session session : this.sessions) {
            if (session.current()) {
                session.processor = new Equalizer.Processor(this.playbackRate, session.lineChannels);
            }
        }
    }

    /**
     * Общая часть записи: громкость, нормализация, эквалайзер и, главное, конверт кроссфейда
     * (идея №4) — новый трек входит за секунду с небольшим, старый за это же время уходит в тишину.
     */
    private float[] mixGain(Session session, int samples) {
        float target;
        if (!session.audible()) {
            target = 0.0f;
        }
        else if (this.paused) {
            target = 0.0f;
        }
        else {
            float base = this.volume * (session.orphan ? 1.0f : this.normGain);
            if (session.orphan) {
                long left = session.orphanUntil - System.currentTimeMillis();
                float fade = session.fadeMs <= 0 ? 0.0f : Math.max(0.0f, Math.min(1.0f, (float) left / session.fadeMs));
                target = base * fade;
            }
            else if (session.fadeMs > 0) {
                float fade = Math.max(0.0f, Math.min(1.0f, (float) (System.currentTimeMillis() - session.startedAt) / session.fadeMs));
                target = base * fade;
            }
            else {
                target = base;
            }
        }
        float current = session.gain;
        float step = (target - current) / (float) Math.max(1, samples);
        session.gain = target;
        return new float[]{current, step};
    }

    private void writeShort(short[] samples, int length, int rate, int channels, Session session) {
        SourceDataLine dataLine = session.line;
        if (dataLine == null || !session.audible()) {
            return;
        }
        this.ensureEqualizer();
        boolean duplicate = session.lineChannels == 2 && channels == 1;
        if (session.current()) {
            this.updateNormalizationShort(samples, length);
        }
        float[] ramp = this.mixGain(session, length);
        float currentGain = ramp[0];
        float step = ramp[1];
        int outLength = duplicate ? length * 2 : length;
        byte[] bytes = new byte[outLength * 2];
        double sum = 0.0;
        for (int i = 0; i < length; ++i) {
            currentGain += step;
            int value = Math.max(-32768, Math.min(32767, (int) ((float) samples[i] * currentGain)));
            if (this.equalizerOn && session.processor != null) {
                int channel = duplicate ? 0 : i % Math.max(1, channels);
                value = session.processor.processSample(channel, value);
            }
            bytes[i * 2] = (byte) value;
            bytes[i * 2 + 1] = (byte) (value >> 8);
            if (duplicate) {
                bytes[i * 4 + 2] = (byte) value;
                bytes[i * 4 + 3] = (byte) (value >> 8);
            }
            sum += (double) value * (double) value;
        }
        if (session.current()) {
            this.updateLevel(sum / (double) Math.max(1, length));
            if (!this.paused) {
                this.elapsedMs += (long) length * 1000L / Math.max(1, rate * channels);
            }
        }
        this.safeWrite(dataLine, bytes, outLength * 2, session);
    }

    private void writeBytes(byte[] source, int length, int rate, int channels, Session session) {
        SourceDataLine dataLine = session.line;
        if (dataLine == null || !session.audible()) {
            return;
        }
        this.ensureEqualizer();
        int samples = length / 2;
        boolean duplicate = session.lineChannels == 2 && channels == 1;
        if (session.current()) {
            this.updateNormalizationBytes(source, length);
        }
        float[] ramp = this.mixGain(session, samples);
        float currentGain = ramp[0];
        float step = ramp[1];
        byte[] bytes = new byte[duplicate ? length * 2 : length];
        double sum = 0.0;
        for (int i = 0; i < samples; ++i) {
            currentGain += step;
            int value = (source[i * 2 + 1] << 8) | (source[i * 2] & 0xFF);
            value = Math.max(-32768, Math.min(32767, (int) ((float) value * currentGain)));
            if (this.equalizerOn && session.processor != null) {
                int channel = duplicate ? 0 : i % Math.max(1, channels);
                value = session.processor.processSample(channel, value);
            }
            bytes[i * 2] = (byte) value;
            bytes[i * 2 + 1] = (byte) (value >> 8);
            if (duplicate) {
                bytes[i * 4 + 2] = (byte) value;
                bytes[i * 4 + 3] = (byte) (value >> 8);
            }
            sum += (double) value * (double) value;
        }
        if (session.current()) {
            this.updateLevel(sum / (double) Math.max(1, samples));
            if (!this.paused) {
                this.elapsedMs += (long) samples * 1000L / Math.max(1, rate * channels);
            }
        }
        this.safeWrite(dataLine, bytes, bytes.length, session);
    }

    private void safeWrite(SourceDataLine dataLine, byte[] bytes, int length, Session session) {
        try {
            dataLine.write(bytes, 0, length);
        }
        catch (Throwable ignored) {
            // the line was closed by a track switch - the worker is about to exit anyway
        }
        if (session.line != dataLine) {
            this.closeSession(session);
        }
    }

    /** Нормализация: считаем реальный RMS блока и медленно подтягиваем усиление к целевому. */
    private void updateNormalizationShort(short[] samples, int length) {
        if (!this.normalize) {
            this.normGain = 1.0f;
            return;
        }
        double sum = 0.0;
        for (int i = 0; i < length; ++i) {
            double value = samples[i];
            sum += value * value;
        }
        this.applyNormalization((float) Math.sqrt(sum / (double) Math.max(1, length)) / 32768.0f);
    }

    private void updateNormalizationBytes(byte[] source, int length) {
        if (!this.normalize) {
            this.normGain = 1.0f;
            return;
        }
        int samples = length / 2;
        double sum = 0.0;
        for (int i = 0; i < samples; ++i) {
            int value = (source[i * 2 + 1] << 8) | (source[i * 2] & 0xFF);
            sum += (double) value * (double) value;
        }
        this.applyNormalization((float) Math.sqrt(sum / (double) Math.max(1, samples)) / 32768.0f);
    }

    private void applyNormalization(float rms) {
        float targetGain = rms < 0.0008f ? this.normGain : TARGET_RMS / rms;
        targetGain = Math.max(0.35f, Math.min(2.4f, targetGain));
        this.normGain += (targetGain - this.normGain) * 0.03f;
    }

    private void updateLevel(double meanSquare) {
        float rms = (float) Math.sqrt(Math.max(0.0, meanSquare)) / 32768.0f;
        float scaled = Math.min(1.0f, rms * 3.2f);
        this.level = this.level + (scaled - this.level) * 0.35f;
    }

    private static String describe(Throwable throwable) {
        String message = throwable.getMessage();
        String type = throwable.getClass().getSimpleName();
        if (message == null || message.isBlank()) {
            return type;
        }
        if (message.length() > 90) {
            message = message.substring(0, 90) + "…";
        }
        return type + ": " + message;
    }
}
