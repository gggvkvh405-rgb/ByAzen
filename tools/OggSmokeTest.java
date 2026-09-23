import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import rtx.byazen.api.music.OggVorbisStream;

/**
 * Проверка OGG-тракта музыкального плеера в CI (идея №21): поток Vorbis декодируется ровно тем же
 * кодом, что и в игре — {@code rtx.byazen.api.music.OggVorbisStream}, — и превращается в PCM-сэмплы.
 * <p>
 * Декодер (JOrbis, вендорный пакет {@code rtx.byazen.libs.audio}) собирается здесь же из исходников,
 * поэтому проверка не требует ни новых зависимостей, ни запущенного Minecraft.
 */
public final class OggSmokeTest {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("usage: OggSmokeTest <file.ogg> [минимальная длительность в секундах]");
            System.exit(2);
        }
        long[] counters = new long[4];
        double[] level = new double[3];
        int[] format = new int[2];
        boolean completed = OggVorbisStream.decode(
                new BufferedInputStream(new FileInputStream(args[0]), 1 << 16),
                (samples, length, rate, channels) -> {
                    ++counters[0];
                    counters[1] += length;
                    format[0] = rate;
                    format[1] = channels;
                    for (int i = 0; i < length; ++i) {
                        int value = samples[i];
                        double scaled = Math.abs(value) / 32768.0;
                        if (scaled > level[0]) {
                            level[0] = scaled;
                        }
                        level[1] += scaled * scaled;
                        if (value == 0) {
                            ++counters[2];
                        }
                    }
                },
                () -> false);
        counters[3] = counters[1] / 2L; // байт PCM (16 бит)
        long milliseconds = format[0] == 0 || format[1] == 0
                ? 0L : counters[1] * 1000L / (long) (format[0] * format[1]);
        double rms = counters[1] == 0 ? 0.0 : Math.sqrt(level[1] / (double) counters[1]);
        System.out.println("файл     = " + args[0]);
        System.out.println("кадров   = " + counters[0]);
        System.out.println("сэмплов  = " + counters[1]);
        System.out.println("rate     = " + format[0] + " Hz");
        System.out.println("channels = " + format[1]);
        System.out.println("длит.    = " + (milliseconds / 1000.0) + " c");
        System.out.println("PCM      = " + counters[3] + " байт");
        System.out.printf("peak     = %.4f%n", level[0]);
        System.out.printf("rms      = %.4f%n", rms);
        System.out.println("silent   = " + counters[2] + " сэмплов нуля");
        System.out.println("поток    = " + (completed ? "дочитан до конца" : "прерван"));
        // Критерий: поток Vorbis реально распознан и декодирован в звук. Уровень печатаем как справку:
        // короткая тестовая фикстура может быть почти тишиной, и это не ошибка плеера.
        boolean ok = counters[0] > 0 && counters[1] > 0 && format[0] > 0 && format[1] >= 1 && completed;
        if (args.length > 1) {
            double minimum = Double.parseDouble(args[1]);
            System.out.printf("минимум  = %.2f c%n", minimum);
            ok = ok && milliseconds / 1000.0 >= minimum;
        }
        System.out.println(ok ? "OGG SMOKE: OK" : "OGG SMOKE: FAILED");
        if (!ok) {
            System.exit(1);
        }
    }
}
