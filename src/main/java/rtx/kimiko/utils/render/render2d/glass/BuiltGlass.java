package rtx.kimiko.utils.render.render2d.glass;
import net.minecraft.client.gui.DrawContext;
import rtx.kimiko.utils.render.render2d.glass.GlassRenderer;

public record BuiltGlass(float x, float y, float width, float height, float radiusTopLeft, float radiusTopRight, float radiusBottomRight, float radiusBottomLeft, int color, float globalAlpha, float fresnelPower, int fresnelColor, float baseAlpha, boolean fresnelInvert, float fresnelMix, float distortStrength, float squirt, float z, float blurRadius, int secondColor, float colorOffset, int splitIndex, int paletteSlot) {
    private static final float DEFAULT_BLUR_RADIUS = 30.0f;

    public BuiltGlass(float f, float f2, float f3, float f4, float[] fArray, int n, float f5, float f6, int n2, float f7, boolean bl, float f8, float f9, float f10, float f11) {
        this(f, f2, f3, f4, BuiltGlass.radiusValue(fArray, 0), BuiltGlass.radiusValue(fArray, 1), BuiltGlass.radiusValue(fArray, 2), BuiltGlass.radiusValue(fArray, 3), n, f5, f6, n2, f7, bl, f8, f9, f10, f11);
    }

    public BuiltGlass(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n, float f9, float f10, int n2, float f11, boolean bl, float f12, float f13, float f14, float f15) {
        this(f, f2, f3, f4, f5, f6, f7, f8, n, f9, f10, n2, f11, bl, f12, f13, f14, f15, 30.0f, n, 0.0f);
    }

    public BuiltGlass(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n, float f9, float f10, int n2, float f11, boolean bl, float f12, float f13, float f14, float f15, float f16, int n3, float f17) {
        this(f, f2, f3, f4, f5, f6, f7, f8, n, f9, f10, n2, f11, bl, f12, f13, f14, f15, f16, n3, f17, 0, 0);
    }

    public BuiltGlass(float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int n, float f9, float f10, int n2, float f11, boolean bl, float f12, float f13, float f14, float f15, float f16, int n3, float f17, int n4) {
        this(f, f2, f3, f4, f5, f6, f7, f8, n, f9, f10, n2, f11, bl, f12, f13, f14, f15, f16, n3, f17, n4, 0);
    }

    public boolean visible() {
        return this.width > 0.0f && this.height > 0.0f && this.globalAlpha > 0.0f && (this.baseAlpha > 0.0f || this.fresnelColor >>> 24 != 0 || this.color >>> 24 != 0);
    }

    public void render(DrawContext drawContext) {
        GlassRenderer.getInstance().draw(drawContext, this);
    }

    public BuiltGlass withRainbow(float f, float f2) {
        int n = Math.max(0, Math.min(255, Math.round(f * 255.0f)));
        int n2 = Math.max(0, Math.min(255, Math.round(f2 * 255.0f)));
        int n3 = 0xFF000000 | n << 16 | n2 << 8;
        return new BuiltGlass(this.x, this.y, this.width, this.height, this.radiusTopLeft, this.radiusTopRight, this.radiusBottomRight, this.radiusBottomLeft, this.color, this.globalAlpha, this.fresnelPower, this.fresnelColor, this.baseAlpha, this.fresnelInvert, this.fresnelMix, this.distortStrength, this.squirt, 1.0f, this.blurRadius, n3, this.colorOffset, this.splitIndex, this.paletteSlot);
    }

    private static float radiusValue(float[] fArray, int n) {
        if (fArray == null || n < 0 || n >= fArray.length) {
            return 0.0f;
        }
        return fArray[n];
    }

    public BuiltGlass withSplitIndex(int n) {
        return new BuiltGlass(this.x, this.y, this.width, this.height, this.radiusTopLeft, this.radiusTopRight, this.radiusBottomRight, this.radiusBottomLeft, this.color, this.globalAlpha, this.fresnelPower, this.fresnelColor, this.baseAlpha, this.fresnelInvert, this.fresnelMix, this.distortStrength, this.squirt, this.z, this.blurRadius, this.secondColor, this.colorOffset, n, this.paletteSlot);
    }

    public BuiltGlass withPaletteSlot(int n) {
        return new BuiltGlass(this.x, this.y, this.width, this.height, this.radiusTopLeft, this.radiusTopRight, this.radiusBottomRight, this.radiusBottomLeft, this.color, this.globalAlpha, this.fresnelPower, this.fresnelColor, this.baseAlpha, this.fresnelInvert, this.fresnelMix, this.distortStrength, this.squirt, this.z, this.blurRadius, this.secondColor, this.colorOffset, this.splitIndex, n);
    }

    public BuiltGlass withSecondColor(int n, float f) {
        return new BuiltGlass(this.x, this.y, this.width, this.height, this.radiusTopLeft, this.radiusTopRight, this.radiusBottomRight, this.radiusBottomLeft, this.color, this.globalAlpha, this.fresnelPower, this.fresnelColor, this.baseAlpha, this.fresnelInvert, this.fresnelMix, this.distortStrength, this.squirt, this.z, this.blurRadius, n, f, this.splitIndex, this.paletteSlot);
    }

    public BuiltGlass withBlurRadius(float f) {
        return new BuiltGlass(this.x, this.y, this.width, this.height, this.radiusTopLeft, this.radiusTopRight, this.radiusBottomRight, this.radiusBottomLeft, this.color, this.globalAlpha, this.fresnelPower, this.fresnelColor, this.baseAlpha, this.fresnelInvert, this.fresnelMix, this.distortStrength, this.squirt, this.z, f, this.secondColor, this.colorOffset, this.splitIndex, this.paletteSlot);
    }
}

