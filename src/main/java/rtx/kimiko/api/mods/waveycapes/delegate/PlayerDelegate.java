package rtx.kimiko.api.mods.waveycapes.delegate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientMannequinEntity;
import net.minecraft.entity.PlayerLikeEntity;
import rtx.kimiko.api.mods.waveycapes.versionless.nms.MinecraftPlayer;

public class PlayerDelegate
implements MinecraftPlayer {
    private PlayerLikeEntity player;

    public PlayerDelegate(PlayerLikeEntity playerLikeEntity) {
        this.player = playerLikeEntity;
    }

    public PlayerLikeEntity getPlayer() {
        return this.player;
    }

    @Override
    public float getYRot() {
        return this.getPlayer().getYaw();
    }

    @Override
    public double getX() {
        return this.getPlayer().getX();
    }

    @Override
    public double getY() {
        return this.getPlayer().getY();
    }

    @Override
    public double getZCloak() {
        float f = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);
        PlayerLikeEntity playerLikeEntity = this.player;
        if (playerLikeEntity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)playerLikeEntity;
            return abstractClientPlayerEntity.getState().lerpZ(f);
        }
        playerLikeEntity = this.player;
        if (playerLikeEntity instanceof ClientMannequinEntity) {
            ClientMannequinEntity clientMannequinEntity = (ClientMannequinEntity)playerLikeEntity;
            return clientMannequinEntity.getState().lerpZ(f);
        }
        return 0.0;
    }

    @Override
    public double getXo() {
        return this.player.lastX;
    }

    @Override
    public double getZo() {
        return this.player.lastZ;
    }

    @Override
    public double getYo() {
        return this.player.lastY;
    }

    @Override
    public double getZ() {
        return this.getPlayer().getZ();
    }

    @Override
    public double getXCloak() {
        float f = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(false);
        PlayerLikeEntity playerLikeEntity = this.player;
        if (playerLikeEntity instanceof AbstractClientPlayerEntity) {
            AbstractClientPlayerEntity abstractClientPlayerEntity = (AbstractClientPlayerEntity)playerLikeEntity;
            return abstractClientPlayerEntity.getState().lerpX(f);
        }
        playerLikeEntity = this.player;
        if (playerLikeEntity instanceof ClientMannequinEntity) {
            ClientMannequinEntity clientMannequinEntity = (ClientMannequinEntity)playerLikeEntity;
            return clientMannequinEntity.getState().lerpX(f);
        }
        return 0.0;
    }

    @Override
    public float getXRot() {
        return this.getPlayer().getPitch();
    }

    @Override
    public boolean isVisuallySwimming() {
        return this.getPlayer().isInSwimmingPose();
    }

    @Override
    public float getYBodyRotO() {
        return this.player.lastBodyYaw;
    }

    @Override
    public float getYBodyRot() {
        return this.player.bodyYaw;
    }

    @Override
    public boolean isCrouching() {
        return this.getPlayer().isInSneakingPose();
    }

    @Override
    public boolean isUnderWater() {
        return this.getPlayer().isSubmergedInWater();
    }
}

