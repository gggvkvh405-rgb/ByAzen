package rtx.kimiko.api.mods.waveycapes.versionless.sim;
import java.util.List;
import rtx.kimiko.api.mods.waveycapes.versionless.util.CapePoint;
import rtx.kimiko.api.mods.waveycapes.versionless.util.Vector3;

public interface BasicSimulation {
    public boolean empty();

    public boolean init(int var1);

    public float getGravity();

    public List<CapePoint> getPoints();

    public boolean isSneaking();

    public void setGravity(float var1);

    public void simulate();

    public void applyMovement(Vector3 var1);

    public void setSneaking(boolean var1);

    public void setGravityDirection(Vector3 var1);
}

