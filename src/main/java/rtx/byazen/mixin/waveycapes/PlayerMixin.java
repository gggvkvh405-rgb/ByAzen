package rtx.byazen.mixin.waveycapes;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.ByAzen;
import rtx.byazen.api.mods.waveycapes.delegate.PlayerDelegate;
import rtx.byazen.api.mods.waveycapes.versionless.CapeHolder;
import rtx.byazen.api.mods.waveycapes.versionless.ModBase;
import rtx.byazen.api.mods.waveycapes.versionless.sim.BasicSimulation;
import rtx.byazen.api.mods.waveycapes.versionless.util.Vector3;

@Mixin(net.minecraft.client.network.AbstractClientPlayerEntity.class)

public abstract class PlayerMixin
extends Entity
implements CapeHolder {
    @Unique
    private BasicSimulation simulation;
    @Unique
    private Vector3 lastPlayerAnimatorPosition = new Vector3();
    @Unique
    private boolean dirty = false;
    @Unique
    private PlayerDelegate byazen_playerDelegate;

    public PlayerMixin(EntityType<?> entityType, World level) {
        super(entityType, level);
    }

    @Override
    public void setDirty() {
        this.dirty = true;
    }

    @Inject(method="tick", at={@At(value="TAIL")}, require = 0)
    private void moveCloakUpdate(CallbackInfo info) {
        if (ModBase.simulationBroken) {
            return;
        }
        if (!this.getEntityWorld().isClient()) {
            return;
        }
        if (!((Object)this instanceof PlayerLikeEntity)) {
            return;
        }
        PlayerLikeEntity entity = (PlayerLikeEntity)(Object)this;
        try {
            this.updateSimulation(16);
            PlayerDelegate playerDelegate = this.byazen_playerDelegate;
            if (playerDelegate == null) {
                this.byazen_playerDelegate = playerDelegate = new PlayerDelegate(entity);
            }
            BasicSimulation currentSimulation = this.getSimulation();
            if (this.dirty) {
                this.dirty = false;
                if (currentSimulation != null) {
                    currentSimulation.applyMovement(new Vector3(1.0f, 1.0f, 0.0f));
                    for (int i = 0; i < 5; ++i) {
                        this.simulate(playerDelegate);
                    }
                }
            }
            this.simulate(playerDelegate);
        }
        catch (Throwable throwable) {
            ModBase.simulationBroken = true;
            this.dirty = false;
            this.setSimulation(null);
            ByAzen.LOGGER.error("[WaveyCapes] Cape simulation failed and was disabled for this session", throwable);
        }
    }

    @Override
    public UUID getWCUUID() {
        return this.getUuid();
    }

    @Override
    public BasicSimulation getSimulation() {
        return this.simulation;
    }

    @Override
    public void setSimulation(BasicSimulation simulation) {
        this.simulation = simulation;
    }

    @Override
    public Vector3 getLastPlayerAnimatorPosition() {
        return this.lastPlayerAnimatorPosition;
    }

    @Override
    public void setLastPlayerAnimatorPosition(Vector3 lastPlayerAnimatorPosition) {
        this.lastPlayerAnimatorPosition = lastPlayerAnimatorPosition;
    }
}

