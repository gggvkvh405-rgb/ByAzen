package rtx.kimiko.api.ui.theme;
import java.awt.Color;

public enum Theme {
    KIMIKO("\u041a\u043b\u0438\u0435\u043d\u0442\u0441\u043a\u0430\u044f", new int[]{9081843, 11838975}),
    BLUEPINK("\u0413\u043e\u043b\u0443\u0431\u043e-\u0440\u043e\u0437\u043e\u0432\u0430\u044f", new int[]{4776676, 14045679}),
    BLUEGREEN("\u0421\u0438\u043d\u0435-\u0437\u0435\u043b\u0451\u043d\u0430\u044f", new int[]{911192, 157920}),
    DARKBLUE("\u0422\u0451\u043c\u043d\u043e-\u0441\u0438\u043d\u044f\u044f", new int[]{4023774, 2166617}),
    POLARIZE("\u0427\u0451\u0440\u043d\u043e-\u0431\u0435\u043b\u0430\u044f", new int[]{0xC8C8C8, 0x7B7B7B}),
    WATER("\u0412\u043e\u0434\u044f\u043d\u0430\u044f", new int[]{6134508, 737480}),
    VIOLET("\u0424\u0438\u043e\u043b\u0435\u0442\u043e\u0432\u0430\u044f", new int[]{9443736, 4395888}),
    LILAC("\u041b\u0430\u0432\u0430\u043d\u0434\u043e\u0432\u0430\u044f", new int[]{12618202, 6892934}),
    SUNRISE("\u0420\u0430\u0441\u0441\u0432\u0435\u0442\u043d\u0430\u044f", new int[]{16087094, 14039456}),
    SUNSET("\u0417\u0430\u043a\u0430\u0442\u043d\u0430\u044f", new int[]{16742912, 9317858}),
    SPRING("\u0412\u0435\u0441\u0435\u043d\u043d\u044f\u044f", new int[]{11067491, 5679919}),
    SUMMER("\u041b\u0435\u0442\u043d\u044f\u044f", new int[]{16769625, 16754513}),
    WINTER("\u0417\u0438\u043c\u043d\u044f\u044f", new int[]{14740220, 13623027}),
    MIDNIGHT("\u041f\u043e\u043b\u0443\u043d\u043e\u0447\u043d\u0430\u044f", new int[]{991271, 2904932}),
    HALLOWEEN("\u0425\u044d\u043b\u043b\u043e\u0443\u0438\u043d\u0441\u043a\u0430\u044f", new int[]{16741656, 0x1A1A1A}),
    NEWYEAR("\u041d\u043e\u0432\u043e\u0433\u043e\u0434\u043d\u044f\u044f", new int[]{1981554, 12597547}),
    VALENTINE("\u0412\u043b\u044e\u0431\u043b\u0451\u043d\u043d\u0430\u044f", new int[]{16739229, 12862825}),
    FIRE("\u041e\u0433\u043d\u0435\u043d\u043d\u0430\u044f", new int[]{16765440, 0xFF0000}),
    EARTH("\u0417\u0435\u043c\u043b\u044f\u043d\u0430\u044f", new int[]{9132587, 4073251}),
    ICE("\u041b\u0435\u0434\u044f\u043d\u0430\u044f", new int[]{10616782, 46299}),
    FOREST("\u041b\u0435\u0441\u043d\u0430\u044f", new int[]{1265171, 3046706}),
    GALAXY("\u041a\u043e\u0441\u043c\u0438\u0447\u0435\u0441\u043a\u0430\u044f", new int[]{986153, 9055202}),
    DESERT("\u041f\u0443\u0441\u0442\u044b\u043d\u043d\u0430\u044f", new int[]{15254430, 12092939}),
    GOLD("\u0417\u043e\u043b\u043e\u0442\u0430\u044f", new int[]{16774839, 12092939}),
    EMERALD("\u0418\u0437\u0443\u043c\u0440\u0443\u0434\u043d\u0430\u044f", new int[]{5294200, 222768}),
    CORAL("\u041a\u043e\u0440\u0430\u043b\u043b\u043e\u0432\u0430\u044f", new int[]{16744272, 15287402}),
    MINT("\u041c\u044f\u0442\u043d\u0430\u044f", new int[]{11993051, 2074234}),
    PASTEL("\u041f\u0430\u0441\u0442\u0435\u043b\u044c\u043d\u0430\u044f", new int[]{16765404, 12710128}),
    TEAL("\u0411\u0438\u0440\u044e\u0437\u043e\u0432\u0430\u044f", new int[]{1022862, 23639}),
    BLOODY("\u041a\u0440\u043e\u0432\u0430\u0432\u0430\u044f", new int[]{12138082, 6757403}),
    NEON("\u041d\u0435\u043e\u043d\u043e\u0432\u0430\u044f", new int[]{16720128, 59903}),
    AUTUMN("\u041e\u0441\u0435\u043d\u043d\u044f\u044f", new int[]{59903, 12601856}),
    CHRISTMAS("\u0420\u043e\u0436\u0434\u0435\u0441\u0442\u0432\u0435\u043d\u0441\u043a\u0430\u044f", new int[]{14617355, 0xDCCBCB}),
    REVOLUT("\u041d\u0435\u043e\u043d\u043e\u0432\u0430\u044f \u0440\u0435\u0432\u043e\u043b\u044e\u0446\u0438\u044f", new int[]{5034078, 7881892}),
    WATERMEL("\u0410\u0440\u0431\u0443\u0437\u043d\u0430\u044f", new int[]{13453107, 10796658});

    private final String displayName;
    private final int accent;
    private final int accentBright;
    private final int accentSoft;
    private final int accentFill;
    private final int toggleOn;
    private final int gradientA;
    private final int gradientB;
    private final int[] palette;

    private Theme(String displayName, int[] palette) {
        this.displayName = displayName;
        int n2 = Math.max(1, palette.length);
        int[] nArray = new int[n2];
        for (int n = 0; n < n2; ++n) {
            nArray[n] = palette[n] & 0xFFFFFF;
        }
        this.palette = nArray;
        int n = nArray[0];
        this.accent = n;
        this.accentBright = Theme.lighten(n, 0.65f, 1.15f, 0.1f);
        this.accentSoft = Theme.lighten(n, 0.45f, 1.25f, 0.15f);
        this.accentFill = Theme.darken(n, 1.05f, 0.78f);
        this.toggleOn = Theme.darken(n, 1.1f, 0.55f);
        this.gradientA = n;
        this.gradientB = nArray[n2 - 1];
    }

    private Theme(String displayName, int accent, int accentBright, int accentSoft, int accentFill, int toggleOn) {
        this.displayName = displayName;
        this.accent = accent & 0xFFFFFF;
        this.accentBright = accentBright & 0xFFFFFF;
        this.accentSoft = accentSoft & 0xFFFFFF;
        this.accentFill = accentFill & 0xFFFFFF;
        this.toggleOn = toggleOn & 0xFFFFFF;
        this.gradientA = this.accent;
        this.gradientB = this.accent;
        this.palette = new int[]{this.accent};
    }

    public String displayName() {
        return this.displayName;
    }

    public int[] shades() {
        return new int[]{this.accent, this.accentBright, this.accentSoft, this.accentFill, this.toggleOn, this.gradientA, this.gradientB};
    }

    public int accentRgb() {
        return this.accent;
    }

    private static float[] toHsb(int n) {
        float[] fArray = new float[3];
        Color.RGBtoHSB(n >> 16 & 0xFF, n >> 8 & 0xFF, n & 0xFF, fArray);
        return fArray;
    }

    public int gradientA() {
        return this.gradientA;
    }

    public int gradientB() {
        return this.gradientB;
    }

    private static float clamp01(float f) {
        return f < 0.0f ? 0.0f : (f > 1.0f ? 1.0f : f);
    }

    private static int darken(int n, float f, float f2) {
        float[] fArray = Theme.toHsb(n);
        return Color.HSBtoRGB(fArray[0], Theme.clamp01(fArray[1] * f), fArray[2] * f2) & 0xFFFFFF;
    }

    public int[] palette() {
        return this.palette;
    }

    private static int lighten(int n, float f, float f2, float f3) {
        float[] fArray = Theme.toHsb(n);
        return Color.HSBtoRGB(fArray[0], Theme.clamp01(fArray[1] * f), Theme.clamp01(fArray[2] * f2 + f3)) & 0xFFFFFF;
    }

    public int accentSoftRgb() {
        return this.accentSoft;
    }

    public int accentFillRgb() {
        return this.accentFill;
    }

    public int accentBrightRgb() {
        return this.accentBright;
    }

    public int toggleOnRgb() {
        return this.toggleOn;
    }
}

