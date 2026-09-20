package rtx.byazen.api.mods.waveycapes.versionless.util;

public class Vector2 {
    public float x;
    public float y;

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2() {
        this(0.0f, 0.0f);
    }

    public Vector2 rotateDegrees(float deg) {
        float f = this.x;
        float f2 = this.y;
        float rad = (float) Math.toRadians(deg);
        this.x = Mth.cos(rad) * f - Mth.sin(rad) * f2;
        this.y = Mth.sin(rad) * f + Mth.cos(rad) * f2;
        return this;
    }
}
