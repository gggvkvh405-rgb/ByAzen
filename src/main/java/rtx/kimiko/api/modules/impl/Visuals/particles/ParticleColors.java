package rtx.kimiko.api.modules.impl.Visuals.particles;

import rtx.kimiko.utils.color.ColorUtil;
import rtx.kimiko.utils.color.RainbowLut;

public final class ParticleColors {
    private ParticleColors() {}

    public static int rainbow(int speed, int offset, float sat, float bright, float alpha) {
        int n3 = (int)((System.currentTimeMillis() / (long)Math.max(1, speed) + (long)offset) % 360L);
        int n4 = RainbowLut.sample(n3, sat, bright);
        return ColorUtil.rgba(n4 >>> 16 & 0xFF, n4 >>> 8 & 0xFF, n4 & 0xFF, Math.round(alpha * 255.0f));
    }

    public static int paletteFade(int speed, int offset, int[] colors) {
        int len = colors.length;
        int n = (int)((System.currentTimeMillis() / (long)Math.max(1, speed) + (long)offset) % 360L);
        float f = (float)n / 360.0f * (float)len;
        int i1 = (int)f % len;
        int i2 = (i1 + 1) % len;
        return ColorUtil.lerpColor(colors[i1], colors[i2], f - (float)Math.floor(f));
    }

    public static int fade(int speed, int offset, int c1, int c2) {
        double d = (Math.sin((double)(System.currentTimeMillis() / (long)Math.max(1, speed) + (long)offset) * 0.05) + 1.0) * 0.5;
        return ColorUtil.lerpColor(c1, c2, (float)d);
    }
}
