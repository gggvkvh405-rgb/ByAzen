package rtx.byazen.api.mods.waveycapes.versionless;
import java.util.UUID;
import rtx.byazen.api.mods.waveycapes.versionless.CapeMovement;
import rtx.byazen.api.mods.waveycapes.versionless.ModBase;
import rtx.byazen.api.mods.waveycapes.versionless.nms.MinecraftPlayer;
import rtx.byazen.api.mods.waveycapes.versionless.sim.BasicSimulation;
import rtx.byazen.api.mods.waveycapes.versionless.sim.StickSimulation;
import rtx.byazen.api.mods.waveycapes.versionless.sim.StickSimulation3d;
import rtx.byazen.api.mods.waveycapes.versionless.sim.StickSimulationDungeons;
import rtx.byazen.api.mods.waveycapes.versionless.util.Vector2;
import rtx.byazen.api.mods.waveycapes.versionless.util.Mth;
import rtx.byazen.api.mods.waveycapes.versionless.util.Vector3;

public interface CapeHolder {
    default public void simulate(MinecraftPlayer abstractClientPlayer) {
        BasicSimulation simulation = this.getSimulation();
        if (simulation == null || simulation.empty()) {
            return;
        }
        double d = abstractClientPlayer.getXCloak() - abstractClientPlayer.getX();
        double m = abstractClientPlayer.getZCloak() - abstractClientPlayer.getZ();
        float n = abstractClientPlayer.getYBodyRotO() + abstractClientPlayer.getYBodyRot() - abstractClientPlayer.getYBodyRotO();
        double o = Mth.sin(n * ((float)Math.PI / 180));
        double p = -Mth.cos(n * ((float)Math.PI / 180));
        float heightMul = ModBase.config.heightMultiplier;
        float straveMul = ModBase.config.straveMultiplier;
        if (abstractClientPlayer.isUnderWater()) {
            heightMul *= 2.0f;
        }
        double fallHack = Mth.clamp((abstractClientPlayer.getYo() - abstractClientPlayer.getY()) * 10.0, 0.0, 1.0);
        if (abstractClientPlayer.isUnderWater()) {
            simulation.setGravity((float)ModBase.config.gravity / 10.0f);
        } else {
            simulation.setGravity(ModBase.config.gravity);
        }
        Vector3 gravity = new Vector3(0.0f, -1.0f, 0.0f);
        Vector2 strave = new Vector2((float)(abstractClientPlayer.getX() - abstractClientPlayer.getXo()), (float)(abstractClientPlayer.getZ() - abstractClientPlayer.getZo()));
        strave.rotateDegrees(-abstractClientPlayer.getYRot());
        double changeX = d * o + m * p + fallHack + (double)(abstractClientPlayer.isCrouching() && !simulation.isSneaking() ? 3 : 0);
        double changeY = (abstractClientPlayer.getY() - abstractClientPlayer.getYo()) * (double)heightMul + (double)(abstractClientPlayer.isCrouching() && !simulation.isSneaking() ? 1 : 0);
        double changeZ = -strave.x * straveMul;
        simulation.setSneaking(abstractClientPlayer.isCrouching());
        Vector3 change = new Vector3((float)changeX, (float)changeY, (float)changeZ);
        if (abstractClientPlayer.isVisuallySwimming()) {
            float rotation = abstractClientPlayer.getXRot();
            gravity.rotateDegrees(rotation += 90.0f);
            change.rotateDegrees(rotation);
        }
        simulation.setGravityDirection(gravity);
        change = ModBase.getINSTANCE().applyModAnimations(abstractClientPlayer, change);
        simulation.applyMovement(change);
        simulation.simulate();
    }

    public UUID getWCUUID();

    public void setDirty();

    public void setLastPlayerAnimatorPosition(Vector3 var1);

    public Vector3 getLastPlayerAnimatorPosition();

    default public void updateSimulation(int partCount) {
        BasicSimulation simulation = this.getSimulation();
        if (simulation == null || this.incorrectSimulation(simulation)) {
            simulation = this.createSimulation();
            this.setSimulation(simulation);
        }
        if (simulation == null) {
            return;
        }
        if (simulation.init(partCount)) {
            this.setDirty();
        }
    }

    default public BasicSimulation createSimulation() {
        CapeMovement style = ModBase.config.capeMovement;
        if (style == CapeMovement.BASIC_SIMULATION) {
            return new StickSimulation();
        }
        if (style == CapeMovement.BASIC_SIMULATION_3D) {
            return new StickSimulation3d();
        }
        if (style == CapeMovement.DUNGEONS) {
            return new StickSimulationDungeons();
        }
        return null;
    }

    public void setSimulation(BasicSimulation var1);

    default public boolean incorrectSimulation(BasicSimulation sim) {
        CapeMovement style = ModBase.config.capeMovement;
        if (style == CapeMovement.BASIC_SIMULATION && sim.getClass() != StickSimulation.class) {
            return true;
        }
        if (style == CapeMovement.BASIC_SIMULATION_3D && sim.getClass() != StickSimulation3d.class) {
            return true;
        }
        return style == CapeMovement.DUNGEONS && sim.getClass() != StickSimulationDungeons.class;
    }

    public BasicSimulation getSimulation();
}

