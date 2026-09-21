import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

/**
 * Проверка аудио-тракта музыкального плеера в CI: тем же способом, что и в игре,
 * MP3-файл декодируется потоком (без записи на диск) в PCM-сэмплы.
 * Повторяет код {@code rtx.byazen.api.music.MusicEngine#playMp3}.
 */
public final class AudioSmokeTest {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("usage: AudioSmokeTest <file.mp3>");
            System.exit(2);
        }
        long total = 0L;
        long silent = 0L;
        int frames = 0;
        int rate = 0;
        int channels = 0;
        double peak = 0.0;
        double sumSquares = 0.0;
        ByteArrayOutputStream pcm = new ByteArrayOutputStream(1 << 20);
        try (InputStream raw = new BufferedInputStream(new FileInputStream(args[0]), 1 << 16)) {
            Bitstream bitstream = new Bitstream(raw);
            Decoder decoder = new Decoder();
            try {
                while (true) {
                    Header header = bitstream.readFrame();
                    if (header == null) {
                        break;
                    }
                    SampleBuffer samples = (SampleBuffer) decoder.decodeFrame(header, bitstream);
                    if (samples != null && samples.getBufferLength() > 0) {
                        rate = decoder.getOutputFrequency();
                        channels = decoder.getOutputChannels();
                        short[] buffer = samples.getBuffer();
                        for (int i = 0; i < samples.getBufferLength(); ++i) {
                            int value = buffer[i];
                            pcm.write(value & 0xFF);
                            pcm.write((value >> 8) & 0xFF);
                            peak = Math.max(peak, Math.abs(value) / 32768.0);
                            sumSquares += (double) value * (double) value;
                            if (value == 0) {
                                ++silent;
                            }
                        }
                        total += samples.getBufferLength();
                    }
                    ++frames;
                    bitstream.closeFrame();
                }
            }
            finally {
                try {
                    bitstream.close();
                }
                catch (Exception ignored) {
                }
            }
        }
        double rms = total == 0 ? 0.0 : Math.sqrt(sumSquares / (double) total);
        long milliseconds = rate == 0 || channels == 0 ? 0L : total * 1000L / (long) (rate * channels);
        System.out.println("frames   = " + frames);
        System.out.println("samples  = " + total);
        System.out.println("rate     = " + rate + " Hz");
        System.out.println("channels = " + channels);
        System.out.println("длит.    = " + (milliseconds / 1000.0) + " c");
        System.out.println("PCM      = " + pcm.size() + " байт");
        System.out.printf("peak     = %.4f%n", peak);
        System.out.printf("rms      = %.4f%n", rms);
        System.out.println("silent   = " + silent + " сэмплов нуля");
        boolean ok = frames > 0 && total > 0 && rate > 0 && channels >= 1 && pcm.size() > 0 && peak > 0.0;
        System.out.println(ok ? "AUDIO SMOKE: OK" : "AUDIO SMOKE: FAILED");
        if (!ok) {
            System.exit(1);
        }
    }
}
