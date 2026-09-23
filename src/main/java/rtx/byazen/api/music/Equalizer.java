package rtx.byazen.api.music;

/**
 * 10-полосный эквалайзер встроенного плеера (идея №5 из IDEAS.md): «Бас», «Вокал», «Ночной»,
 * «Плоский» и «Электро» одним нажатием плюс ручная правка каждой полосы.
 * <p>
 * Полосы — стандартные частоты треть-октавной сетки от 31 Гц до 16 кГц. Каждая полоса — это
 * peaking-фильтр (RBJ cookbook), который мягко поднимает или опускает свой участок спектра, не
 * трогая остальные. Фильтры применяются к уже декодированным сэмплам до вывода на звуковую линию,
 * поэтому работают одинаково для MP3, OGG и локальных файлов.
 * <p>
 * Состояние (значения полос и выбранный пресет) хранится в модуле «Music Player» и попадает в конфиг
 * клиента как обычные настройки.
 */
public final class Equalizer {

    public static final int BANDS = 10;
    public static final float MIN_DB = -12.0f;
    public static final float MAX_DB = 12.0f;

    private static final float[] FREQUENCIES = {31.0f, 62.0f, 125.0f, 250.0f, 500.0f, 1000.0f, 2000.0f, 4000.0f, 8000.0f, 16000.0f};
    private static final String[] BAND_LABELS = {"31 Гц", "62 Гц", "125 Гц", "250 Гц", "500 Гц", "1 кГц", "2 кГц", "4 кГц", "8 кГц", "16 кГц"};

    private static final String[] PRESET_NAMES = {"Плоский", "Бас", "Вокал", "Ночной", "Электро"};
    private static final float[][] PRESET_GAINS = {
            {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f},
            {7.0f, 6.0f, 5.0f, 3.0f, 1.0f, 0.0f, -1.0f, -1.0f, 0.0f, 0.0f},
            {-3.0f, -2.0f, 0.0f, 2.0f, 4.0f, 5.0f, 4.0f, 2.0f, 0.0f, -1.0f},
            {4.0f, 3.0f, 2.0f, 1.0f, 0.0f, -1.0f, -2.0f, -3.0f, -3.0f, -3.0f},
            {5.0f, 4.0f, 2.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 3.0f, 4.0f}
    };

    private static final Equalizer INSTANCE = new Equalizer();

    private final float[] gains = new float[BANDS];
    private volatile boolean enabled;
    private volatile String preset = PRESET_NAMES[0];
    private volatile int revision;

    private Equalizer() {
    }

    public static Equalizer get() {
        return INSTANCE;
    }

    public boolean enabled() {
        return this.enabled;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public float gain(int band) {
        return band >= 0 && band < BANDS ? this.gains[band] : 0.0f;
    }

    /** Меняет усиление полосы (дБ). Значение применяется на лету, без перезапуска трека. */
    public void setGain(int band, float decibels) {
        if (band < 0 || band >= BANDS) {
            return;
        }
        float value = Math.max(MIN_DB, Math.min(MAX_DB, decibels));
        if (Math.abs(value - this.gains[band]) < 0.001f) {
            return;
        }
        this.gains[band] = value;
        this.preset = Equalizer.matchingPreset();
        ++this.revision;
    }

    public float[] gains() {
        float[] copy = new float[BANDS];
        System.arraycopy(this.gains, 0, copy, 0, BANDS);
        return copy;
    }

    public String preset() {
        return this.preset;
    }

    public static String[] presets() {
        return PRESET_NAMES.clone();
    }

    public static int presetCount() {
        return PRESET_NAMES.length;
    }

    public static String presetName(int index) {
        return index >= 0 && index < PRESET_NAMES.length ? PRESET_NAMES[index] : "";
    }

    public static String label(int band) {
        return band >= 0 && band < BANDS ? BAND_LABELS[band] : "";
    }

    public static float frequency(int band) {
        return band >= 0 && band < BANDS ? FREQUENCIES[band] : 0.0f;
    }

    /** Включает пресет: все десять полос выставляются одним движением. */
    public void applyPreset(String name) {
        int index = Equalizer.presetIndex(name);
        if (index < 0) {
            return;
        }
        float[] values = PRESET_GAINS[index];
        for (int band = 0; band < BANDS; ++band) {
            this.gains[band] = values[band];
        }
        this.preset = PRESET_NAMES[index];
        ++this.revision;
    }

    public static int presetIndex(String name) {
        if (name == null) {
            return -1;
        }
        for (int i = 0; i < PRESET_NAMES.length; ++i) {
            if (PRESET_NAMES[i].equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    /** Номер пресета, который сейчас совпадает с полосами (или -1, если полосы подкручены вручную). */
    private static String matchingPreset() {
        Equalizer equalizer = INSTANCE;
        for (int index = 0; index < PRESET_GAINS.length; ++index) {
            boolean same = true;
            for (int band = 0; band < BANDS && same; ++band) {
                same = Math.abs(PRESET_GAINS[index][band] - equalizer.gains[band]) < 0.01f;
            }
            if (same) {
                return PRESET_NAMES[index];
            }
        }
        return "Свои полосы";
    }

    /** Версия настроек: по ней процессор понимает, что пора пересчитать коэффициенты. */
    int revision() {
        return this.revision;
    }

    /**
     * Процессор одного потока: держит фильтры и историю сэмплов для каждого канала.
     * Создаётся при открытии звуковой линии, живёт вместе с треком.
     */
    public static final class Processor {

        private final float sampleRate;
        private final int channels;
        private final double[][] b0 = new double[BANDS][];
        private final double[][] b1 = new double[BANDS][];
        private final double[][] b2 = new double[BANDS][];
        private final double[][] a1 = new double[BANDS][];
        private final double[][] a2 = new double[BANDS][];
        private final double[][] x1 = new double[BANDS][];
        private final double[][] x2 = new double[BANDS][];
        private final double[][] y1 = new double[BANDS][];
        private final double[][] y2 = new double[BANDS][];
        private int appliedRevision = -1;

        public Processor(float sampleRate, int channels) {
            this.sampleRate = sampleRate <= 1000.0f ? 44100.0f : sampleRate;
            this.channels = Math.max(1, Math.min(2, channels));
            for (int band = 0; band < BANDS; ++band) {
                this.b0[band] = new double[this.channels];
                this.b1[band] = new double[this.channels];
                this.b2[band] = new double[this.channels];
                this.a1[band] = new double[this.channels];
                this.a2[band] = new double[this.channels];
                this.x1[band] = new double[this.channels];
                this.x2[band] = new double[this.channels];
                this.y1[band] = new double[this.channels];
                this.y2[band] = new double[this.channels];
            }
            this.recompute();
        }

        /** Прогоняет один сэмпл канала через все десять полос. */
        public float process(int channel, float sample) {
            Equalizer equalizer = Equalizer.get();
            int revision = equalizer.revision();
            if (this.appliedRevision != revision) {
                this.recompute();
            }
            int ch = channel < 0 || channel >= this.channels ? 0 : channel;
            double value = sample;
            for (int band = 0; band < BANDS; ++band) {
                double out = this.b0[band][ch] * value + this.b1[band][ch] * this.x1[band][ch] + this.b2[band][ch] * this.x2[band][ch]
                        - this.a1[band][ch] * this.y1[band][ch] - this.a2[band][ch] * this.y2[band][ch];
                this.x2[band][ch] = this.x1[band][ch];
                this.x1[band][ch] = value;
                this.y2[band][ch] = this.y1[band][ch];
                this.y1[band][ch] = out;
                if (out > 4.0 || out < -4.0) {
                    out = Math.max(-4.0, Math.min(4.0, out));
                    this.y1[band][ch] = out;
                }
                value = out;
            }
            return (float) value;
        }

        public int channels() {
            return this.channels;
        }

        /**
         * То же самое для 16-битных сэмплов из звуковой линии: движок работает с short, поэтому
         * преобразование живёт здесь, рядом с фильтрами.
         */
        public int processSample(int channel, int sample) {
            float value = this.process(channel, sample / 32768.0f);
            int result = (int) (value * 32768.0f);
            if (result > 32767) {
                return 32767;
            }
            if (result < -32768) {
                return -32768;
            }
            return result;
        }

        /** Пересчитывает коэффициенты всех полос (RBJ peaking EQ). */
        private void recompute() {
            Equalizer equalizer = Equalizer.get();
            double nyquist = this.sampleRate * 0.45;
            for (int band = 0; band < BANDS; ++band) {
                double frequency = FREQUENCIES[band];
                double decibels = equalizer.gain(band);
                boolean active = frequency < nyquist && Math.abs(decibels) > 0.05;
                double a = Math.pow(10.0, decibels / 40.0);
                double w0 = 2.0 * Math.PI * frequency / (double) this.sampleRate;
                double alpha = Math.sin(w0) / (2.0 * 1.1);
                double cos = Math.cos(w0);
                double bb0 = 1.0 + alpha * a;
                double bb1 = -2.0 * cos;
                double bb2 = 1.0 - alpha * a;
                double aa0 = 1.0 + alpha / a;
                double aa1 = -2.0 * cos;
                double aa2 = 1.0 - alpha / a;
                for (int ch = 0; ch < this.channels; ++ch) {
                    if (active) {
                        this.b0[band][ch] = bb0 / aa0;
                        this.b1[band][ch] = bb1 / aa0;
                        this.b2[band][ch] = bb2 / aa0;
                        this.a1[band][ch] = aa1 / aa0;
                        this.a2[band][ch] = aa2 / aa0;
                    }
                    else {
                        // Полоса выключена: фильтр пропускает сигнал как есть.
                        this.b0[band][ch] = 1.0;
                        this.b1[band][ch] = 0.0;
                        this.b2[band][ch] = 0.0;
                        this.a1[band][ch] = 0.0;
                        this.a2[band][ch] = 0.0;
                        this.x1[band][ch] = 0.0;
                        this.x2[band][ch] = 0.0;
                        this.y1[band][ch] = 0.0;
                        this.y2[band][ch] = 0.0;
                    }
                }
            }
            this.appliedRevision = equalizer.revision();
        }
    }
}
