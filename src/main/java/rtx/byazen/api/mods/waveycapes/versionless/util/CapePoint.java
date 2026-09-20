package rtx.byazen.api.mods.waveycapes.versionless.util;
import rtx.byazen.api.mods.waveycapes.versionless.util.Vector3;

public interface CapePoint {
    public float getLerpX(float var1);

    public float getLerpY(float var1);

    public float getLerpZ(float var1);

    default public Vector3 getLerpedPos(float f) {
        return new Vector3(this.getLerpX(f), this.getLerpY(f), this.getLerpZ(f));
    }
}

