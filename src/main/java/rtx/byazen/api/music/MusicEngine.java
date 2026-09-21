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

    private static final MusicEngine INSTANCE = new MusicEngine();
    /** Сколько раз пытаемся «догнать» живой поток, если декодер споткнулся о мусор в эфире. */
    private static final int MAX_RECONNECTS = 3;
    private static final long RECONNECT_DELAY_MS = 350L;
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
    private volatile SourceDataLine line;
    private volatile int lineChannels = 2;

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
        this.closeLine();
        this.state = State.IDLE;
        this.detail = "";
        this.level = 0.0f;
        this.paused = false;
    }

    public void next() {
        if (this.index + 1 < this.queue.size()) {
            this.startTrack(this.index + 1);
            return;
        }
        this.stop();
    }

    public void previous() {
        if (this.index > 0) {
            this.startTrack(this.index - 1);
            return;
        }
        if (this.current != null) {
            this.startTrack(Math.max(0, this.index));
        }
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
        this.stop();
        this.stopping = false;
        long token = this.generation.incrementAndGet();
        this.index = newIndex;
        this.current = track;
        this.elapsedMs = 0L;
        this.level = 0.0f;
        this.paused = false;
        this.detail = "";
        this.state = State.CONNECTING;
        this.connectingSince = System.currentTimeMillis();
        Thread thread = new Thread(() -> this.run(track, token), "byazen-music");
        thread.setDaemon(true);
        thread.start();
    }

    private void run(MusicTrack track, long token) {
        boolean finished = false;
        boolean opened = false;
        try (InputStream raw = this.openStream(track, token)) {
            if (raw == null) {
                return;
            }
            BufferedInputStream stream = new BufferedInputStream(raw, 1 << 16);
            if (this.isMp3(track)) {
                finished = this.playMp3(track, stream, token);
            }
            else {
                finished = this.playPcm(stream, token);
            }
            opened = this.line != null && token == this.generation.get();
        }
        catch (Throwable throwable) {
            if (this.isCurrent(token) && !this.stopping) {
                this.fail(MusicEngine.describe(throwable));
            }
            return;
        }
        finally {
            if (this.isCurrent(token)) {
                this.closeLine();
            }
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
        if (this.index + 1 < this.queue.size()) {
            this.startTrack(this.index + 1);
            return;
        }
        this.state = State.IDLE;
        this.detail = "Поток завершён";
        this.level = 0.0f;
    }

    private boolean isCurrent(long token) {
        return token == this.generation.get();
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
        return response.body();
    }

    private boolean isMp3(MusicTrack track) {
        String lower = track.url().toLowerCase(Locale.ROOT);
        int query = lower.indexOf('?');
        if (query > 0) {
            lower = lower.substring(0, query);
        }
        if (lower.endsWith(".mp3")) {
            return true;
        }
        if (lower.endsWith(".wav") || lower.endsWith(".aiff") || lower.endsWith(".aif") || lower.endsWith(".au")) {
            return false;
        }
        return !lower.endsWith(".ogg") && !lower.endsWith(".oga") && track.kind() != MusicTrack.Kind.LOCAL;
    }

    /**
     * Decodes an MP3 stream frame by frame; returns true when the stream ended by itself.
     * <p>
     * Живые радиостанции иногда «спотыкаются»: декодер теряет синхронизацию, но поток ещё идёт.
     * Если после падения в буфере остались данные, соединение открывается заново - эфир продолжается,
     * а не обрывается.
     */
    private boolean playMp3(MusicTrack track, BufferedInputStream stream, long token) throws Exception {
        Bitstream bitstream = new Bitstream(stream);
        Decoder decoder = new Decoder();
        boolean reconnectable = track.kind() != MusicTrack.Kind.LOCAL;
        int reconnects = 0;
        long decodedFrames = 0L;
        int rate = 44100;
        int channels = 2;
        while (this.isCurrent(token) && !this.stopping && !Thread.currentThread().isInterrupted()) {
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
                if (reopened == null || !this.isCurrent(token) || this.stopping) {
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
                if (this.line == null) {
                    this.openLine(rate, channels, token);
                }
                this.writeShort(samples.getBuffer(), samples.getBufferLength(), rate, channels, token);
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
    private boolean playPcm(BufferedInputStream stream, long token) throws Exception {
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
            while (this.isCurrent(token) && !this.stopping && !Thread.currentThread().isInterrupted()
                    && (read = pcm.read(buffer, 0, buffer.length)) > 0) {
                if (this.line == null) {
                    this.openLine((int) pcm.getFormat().getSampleRate(), pcm.getFormat().getChannels(), token);
                }
                this.writeBytes(buffer, read, (int) pcm.getFormat().getSampleRate(), pcm.getFormat().getChannels(), token);
            }
        }
        return true;
    }

    private void openLine(int rate, int channelCount, long token) {
        if (!this.isCurrent(token) || this.stopping) {
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
                if (!this.isCurrent(token) || this.stopping) {
                    dataLine.close();
                    return;
                }
                this.lineChannels = candidate;
                this.line = dataLine;
                if (this.state == State.CONNECTING) {
                    this.state = State.PLAYING;
                    this.detail = "";
                }
                return;
            }
            catch (Throwable throwable) {
                lastError = throwable;
            }
        }
        this.line = null;
        if (this.isCurrent(token)) {
            this.fail("Нет доступа к звуковому устройству"
                    + (lastError == null ? "" : ": " + lastError.getClass().getSimpleName()));
        }
    }

    private void writeShort(short[] samples, int length, int rate, int channels, long token) {
        SourceDataLine dataLine = this.line;
        if (dataLine == null || !this.isCurrent(token)) {
            return;
        }
        boolean duplicate = this.lineChannels == 2 && channels == 1;
        float target = this.paused ? 0.0f : this.volume;
        float currentGain = this.gain;
        float step = (target - currentGain) / (float) Math.max(1, length);
        int outLength = duplicate ? length * 2 : length;
        byte[] bytes = new byte[outLength * 2];
        double sum = 0.0;
        for (int i = 0; i < length; ++i) {
            currentGain += step;
            int value = Math.max(-32768, Math.min(32767, (int) ((float) samples[i] * currentGain)));
            bytes[i * 2] = (byte) value;
            bytes[i * 2 + 1] = (byte) (value >> 8);
            if (duplicate) {
                bytes[i * 4 + 2] = (byte) value;
                bytes[i * 4 + 3] = (byte) (value >> 8);
            }
            sum += (double) value * (double) value;
        }
        this.gain = currentGain;
        this.updateLevel(sum / (double) Math.max(1, length));
        if (!this.paused) {
            this.elapsedMs += (long) length * 1000L / Math.max(1, rate * channels);
        }
        this.safeWrite(dataLine, bytes, outLength * 2);
    }

    private void writeBytes(byte[] source, int length, int rate, int channels, long token) {
        SourceDataLine dataLine = this.line;
        if (dataLine == null || !this.isCurrent(token)) {
            return;
        }
        int samples = length / 2;
        boolean duplicate = this.lineChannels == 2 && channels == 1;
        float target = this.paused ? 0.0f : this.volume;
        float currentGain = this.gain;
        float step = (target - currentGain) / (float) Math.max(1, samples);
        byte[] bytes = new byte[duplicate ? length * 2 : length];
        double sum = 0.0;
        for (int i = 0; i < samples; ++i) {
            currentGain += step;
            int value = (source[i * 2 + 1] << 8) | (source[i * 2] & 0xFF);
            value = Math.max(-32768, Math.min(32767, (int) ((float) value * currentGain)));
            bytes[i * 2] = (byte) value;
            bytes[i * 2 + 1] = (byte) (value >> 8);
            if (duplicate) {
                bytes[i * 4 + 2] = (byte) value;
                bytes[i * 4 + 3] = (byte) (value >> 8);
            }
            sum += (double) value * (double) value;
        }
        this.gain = currentGain;
        this.updateLevel(sum / (double) Math.max(1, samples));
        if (!this.paused) {
            this.elapsedMs += (long) samples * 1000L / Math.max(1, rate * channels);
        }
        this.safeWrite(dataLine, bytes, bytes.length);
    }

    private void safeWrite(SourceDataLine dataLine, byte[] bytes, int length) {
        try {
            dataLine.write(bytes, 0, length);
        }
        catch (Throwable ignored) {
            // the line was closed by a track switch - the worker is about to exit anyway
        }
    }

    private void updateLevel(double meanSquare) {
        float rms = (float) Math.sqrt(Math.max(0.0, meanSquare)) / 32768.0f;
        float scaled = Math.min(1.0f, rms * 3.2f);
        this.level = this.level + (scaled - this.level) * 0.35f;
    }

    private void closeLine() {
        SourceDataLine dataLine = this.line;
        this.line = null;
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
