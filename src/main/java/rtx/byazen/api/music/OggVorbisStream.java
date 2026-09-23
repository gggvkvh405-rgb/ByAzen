package rtx.byazen.api.music;

import rtx.byazen.libs.audio.jogg.Packet;
import rtx.byazen.libs.audio.jogg.Page;
import rtx.byazen.libs.audio.jogg.StreamState;
import rtx.byazen.libs.audio.jogg.SyncState;
import rtx.byazen.libs.audio.jorbis.Block;
import rtx.byazen.libs.audio.jorbis.Comment;
import rtx.byazen.libs.audio.jorbis.DspState;
import rtx.byazen.libs.audio.jorbis.Info;
import java.io.InputStream;

/**
 * Потоковый декодер Ogg Vorbis для встроенного плеера (идея №21 из IDEAS.md).
 * <p>
 * Декодер — JOrbis: чистый Java-код, который уже лежит в библиотеках Minecraft (им сама игра читает
 * свои .ogg), поэтому клиенту не нужны ни новые зависимости, ни нативные библиотеки. Поток читается
 * страницами прямо из сети: сначала три служебных заголовка (идентификация, комментарии, кодовые
 * книги), дальше — пакеты звука, которые превращаются в 16-битные сэмплы и уходят в ту же звуковую
 * линию, что MP3 и WAV.
 * <p>
 * Живое радио в OGG работает так же, как одиночный файл: страницы читаются по мере поступления,
 * декодер отдаёт кадры, а плеер сам решает, когда остановиться.
 */
public final class OggVorbisStream {

    /** Куда пишутся готовые сэмплы: interleaved 16 бит, частота и число каналов потока. */
    public interface Sink {
        void frame(short[] samples, int length, int rate, int channels);
    }

    /** Проверка «плеер всё ещё ждёт этот поток» — вызывается между кадрами. */
    public interface Cancel {
        boolean cancelled();
    }

    private static final int CHUNK = 4096;

    private OggVorbisStream() {
    }

    /**
     * Декодирует один логический Ogg-поток до конца (или до отмены).
     *
     * @return true, если поток закончился сам (файл дочитан), false — если декодирование прервали
     *         или данные закончились раньше времени.
     */
    public static boolean decode(InputStream input, Sink sink, Cancel cancel) throws Exception {
        SyncState sync = new SyncState();
        StreamState stream = new StreamState();
        Page page = new Page();
        Packet packet = new Packet();
        Info info = new Info();
        Comment comment = new Comment();
        DspState dsp = new DspState();
        Block block = new Block(dsp);
        sync.init();
        try {
            // 1. Первая страница: по ней узнаём серийный номер логического потока.
            if (!OggVorbisStream.readPage(sync, page, input, cancel)) {
                return false;
            }
            stream.init(page.serialno());
            info.init();
            comment.init();
            if (stream.pagein(page) < 0) {
                return false;
            }
            if (stream.packetout(packet) != 1) {
                return false;
            }
            if (info.synthesis_headerin(comment, packet) < 0) {
                return false;
            }
            // 2. Ещё два заголовка: комментарии и кодовые книги.
            int headers = 0;
            while (headers < 2 && !cancel.cancelled()) {
                int result = sync.pageout(page);
                if (result == 0) {
                    if (!OggVorbisStream.refill(sync, input)) {
                        return false;
                    }
                    continue;
                }
                if (result < 0) {
                    continue;
                }
                stream.pagein(page);
                while (headers < 2) {
                    int packetResult = stream.packetout(packet);
                    if (packetResult == 0) {
                        break;
                    }
                    if (packetResult < 0) {
                        return false;
                    }
                    info.synthesis_headerin(comment, packet);
                    ++headers;
                }
            }
            if (cancel.cancelled()) {
                return false;
            }
            // 3. Готовим декодер и читаем звук до конца потока.
            dsp.synthesis_init(info);
            block.init(dsp);
            int channels = Math.max(1, info.channels);
            short[] out = new short[CHUNK * 2];
            float[][][] pcm = new float[1][][];
            int[] offsets = new int[channels];
            while (!cancel.cancelled()) {
                int result = sync.pageout(page);
                if (result == 0) {
                    if (!OggVorbisStream.refill(sync, input)) {
                        return true;
                    }
                    continue;
                }
                if (result < 0) {
                    continue;
                }
                stream.pagein(page);
                while (!cancel.cancelled()) {
                    int packetResult = stream.packetout(packet);
                    if (packetResult == 0) {
                        break;
                    }
                    if (packetResult < 0) {
                        continue;
                    }
                    if (block.synthesis(packet) != 0) {
                        continue;
                    }
                    dsp.synthesis_blockin(block);
                    int samples;
                    while ((samples = dsp.synthesis_pcmout(pcm, offsets)) > 0) {
                        float[][] data = pcm[0];
                        int limit = Math.max(1, out.length / channels);
                        int frames = Math.min(samples, limit);
                        for (int channel = 0; channel < channels; ++channel) {
                            int offset = offsets[channel];
                            for (int i = 0; i < frames; ++i) {
                                int value = (int) (data[channel][offset + i] * 32767.0f);
                                if (value > 32767) {
                                    value = 32767;
                                }
                                else if (value < -32768) {
                                    value = -32768;
                                }
                                out[i * channels + channel] = (short) value;
                            }
                        }
                        sink.frame(out, frames * channels, info.rate, channels);
                        dsp.synthesis_read(frames);
                        if (cancel.cancelled()) {
                            return false;
                        }
                    }
                }
                if (page.eos() != 0) {
                    return true;
                }
            }
        }
        finally {
            try {
                stream.clear();
                sync.clear();
            }
            catch (Throwable ignored) {
            }
        }
        return false;
    }

    /** Читает следующую страницу, подтягивая данные из сети, пока её не найдёт. */
    private static boolean readPage(SyncState sync, Page page, InputStream input, Cancel cancel) throws Exception {
        while (!cancel.cancelled()) {
            int result = sync.pageout(page);
            if (result == 1) {
                return true;
            }
            if (!OggVorbisStream.refill(sync, input)) {
                return false;
            }
        }
        return false;
    }

    /** Кладёт очередной кусок потока в буфер Ogg. */
    private static boolean refill(SyncState sync, InputStream input) throws Exception {
        int index = sync.buffer(CHUNK);
        byte[] buffer = sync.data;
        int bytes = input.read(buffer, index, CHUNK);
        if (bytes <= 0) {
            return false;
        }
        sync.wrote(bytes);
        return true;
    }
}
