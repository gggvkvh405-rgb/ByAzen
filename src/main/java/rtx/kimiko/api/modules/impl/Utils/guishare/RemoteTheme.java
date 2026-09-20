package rtx.kimiko.api.modules.impl.Utils.guishare;

public record RemoteTheme(
    int mode, int[] shades, int[] palette, boolean movement, boolean usesSecond,
    int gradientStyleId, float rainbowSpeed, float rainbowSpread, float rainbowSaturation,
    float rainbowBrightness, float sweepAngle, float sweepSpeed
) {
    public void update(long time) {}
    public int[] palette6() { return shades != null && shades.length >= 6 ? shades : new int[6]; }
    public float phase() { return 0.0f; }
    public int styleId() { return gradientStyleId; }
    public boolean closed() { return false; }
    public float cornerRadius() { return 6.0f; }
    public int primaryColor() { return shades != null && shades.length > 0 ? shades[0] : 0xFFFFFFFF; }
    public int secondaryColor() { return palette != null && palette.length > 1 ? palette[1] : primaryColor(); }
    public int accent() { return primaryColor(); }
    public int accent(float alpha) { return primaryColor(); }
    public int accentBright() { return primaryColor(); }
    public int accentBright(float alpha) { return primaryColor(); }
    public int accentSoft() { return primaryColor(); }
    public int accentSoft(float alpha) { return primaryColor(); }
    public int gradientA() { return primaryColor(); }
    public int gradientA(float alpha) { return primaryColor(); }
    public int gradientB() { return secondaryColor(); }
    public int gradientB(float alpha) { return secondaryColor(); }
    public int gradientColor(float t) { return primaryColor(); }
    public int gradientColor(float t, float alpha) { return primaryColor(); }
    public int colorOffset(float offset) { return primaryColor(); }
    public int colorOffset() { return primaryColor(); }
    public boolean isThemeMode() { return mode != 0; }
    public float glowIntensity() { return 1.0f; }
    public float glowRadius() { return 10.0f; }
    public float glowBlend() { return 1.0f; }
    public float backdropBlur() { return 10.0f; }
    public float edgeSharpness() { return 1.0f; }
    public float edgeStrength() { return 1.0f; }
    public float refraction() { return 0.0f; }

    public static RemoteTheme fromState(GuiShareThemeState state) {
        if (state == null) {
            return new RemoteTheme(0, new int[]{0xFFFFFFFF}, new int[]{0xFFFFFFFF}, false, false, 0, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 1.0f);
        }
        return new RemoteTheme(state.mode(), state.shades(), state.palette(), state.movement(), state.usesSecond(), state.gradientStyleId(), state.rainbowSpeed(), state.rainbowSpread(), state.rainbowSaturation(), 1.0f, state.gradientSweep(), 1.0f);
    }
}