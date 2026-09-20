package org.zenith.base.bot.world;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.ClientPlayerLikeEntity;
import net.minecraft.client.network.ClientPlayerLikeState;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.zenith.base.bot.net.BotPlayHandler;

public class BotRemotePlayer extends PlayerEntity implements ClientPlayerLikeEntity {
   public final BotPlayHandler networkHandler;
   private final ClientPlayerLikeState renderState = new ClientPlayerLikeState();
   public Vec3d clientVelocity = Vec3d.ZERO;
   public int velocityLerpDivisor;

   public BotRemotePlayer(BotWorld var1, BotPlayHandler var2, GameProfile var3) {
      super(var1, var3);
      this.networkHandler = var2;
      this.noClip = true;
   }

   public GameMode getListedGameMode() {
      PlayerListEntry playerlistentry = this.networkHandler.getPlayerListEntry(this.getUuid());
      return playerlistentry == null ? null : playerlistentry.getGameMode();
   }

   @Override
   public GameMode getGameMode() {
      GameMode gameMode = this.getListedGameMode();
      return gameMode == null ? GameMode.SURVIVAL : gameMode;
   }

   public BotWorld getWorld() {
      return (BotWorld)this.getEntityWorld();
   }

   public boolean isSpectator() {
      return this.getListedGameMode() == GameMode.SPECTATOR;
   }

   public boolean isCreative() {
      return this.getListedGameMode() == GameMode.CREATIVE;
   }

   public boolean clientDamage(DamageSource source) {
      return true;
   }

   public void tick() {
      this.renderState.tick(this.getEntityPos(), this.getVelocity());
      super.tick();
      this.updateLimbs(false);
   }

   @Override
   public ClientPlayerLikeState getState() {
      return this.renderState;
   }

   @Override
   public SkinTextures getSkin() {
      PlayerListEntry entry = this.networkHandler.getPlayerListEntry(this.getUuid());
      return entry == null ? DefaultSkinHelper.getSkinTextures(this.getUuid()) : entry.getSkinTextures();
   }

   @Override
   public Text getMannequinName() {
      return null;
   }

   @Override
   public ParrotEntity.Variant getShoulderParrotVariant(boolean left) {
      return (left ? this.getLeftShoulderParrotVariant() : this.getRightShoulderParrotVariant()).orElse(null);
   }

   @Override
   public boolean hasExtraEars() {
      return false;
   }

   public void tickMovement() {
      if (this.isInterpolating()) {
         this.getInterpolator().tick();
      }

      if (this.headTrackingIncrements > 0) {
         this.lerpHeadYaw(this.headTrackingIncrements, this.serverHeadYaw);
         this.headTrackingIncrements--;
      }

      if (this.velocityLerpDivisor > 0) {
         this.addVelocityInternal(
            new Vec3d(
               (this.clientVelocity.x - this.getVelocity().x) / this.velocityLerpDivisor,
               (this.clientVelocity.y - this.getVelocity().y) / this.velocityLerpDivisor,
               (this.clientVelocity.z - this.getVelocity().z) / this.velocityLerpDivisor
            )
         );
         this.velocityLerpDivisor--;
      }

      this.tickHandSwing();
      this.tickCramming();
   }

   @Override
   public void setVelocityClient(Vec3d velocity) {
      this.clientVelocity = velocity;
      this.velocityLerpDivisor = this.getType().getTrackTickInterval() + 1;
   }

   public void setVelocityClient(double x, double y, double z) {
      this.setVelocityClient(new Vec3d(x, y, z));
   }

   protected void updatePose() {
   }

   public void onSpawnPacket(EntitySpawnS2CPacket packet) {
      super.onSpawnPacket(packet);
      this.resetPosition();
   }
}
