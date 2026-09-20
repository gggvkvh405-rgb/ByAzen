package org.zenith.utility.mixin.entity;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.PositionInterpolator;
import net.minecraft.entity.player.PlayerEntity;
import java.util.Optional;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.ZenithClient;
import org.zenith.core.ClientProvider;
import org.zenith.core.TranslationKey;
import org.zenith.core.CloudResult;
import org.zenith.module.render.AntiInvisible;
import org.zenith.module.render.Predictions;
import org.zenith.module.render.ShaderESP;

@Mixin(Entity.class)
public abstract class MixinEntity implements ClientProvider {
   @Shadow
   public abstract String getNameForScoreboard();

   @Inject(method = "updateTrackedPositionAndAngles(Ljava/util/Optional;Ljava/util/Optional;Ljava/util/Optional;)V", at = @At("HEAD"))
   private void recordServerPosition(Optional<Vec3d> position, Optional<Float> yaw, Optional<Float> pitch, CallbackInfo callbackInfo) {
      if ((Object)this instanceof LivingEntity livingEntity && (Object)this instanceof CloudResult history) {
         PositionInterpolator interpolator = livingEntity.getInterpolator();
         history.zenithDLC_recordServerPosition(interpolator == null ? livingEntity.getEntityPos() : interpolator.getLerpedPos());
      }
   }

   @Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
   public void bypassSpeed(CallbackInfoReturnable<Boolean> var1) {
      if (AntiInvisible.antiInvisible.isEnabled() && ((Object)this == minecraftClient3.player || ZenithClient.on23().MediaTrackInfo().isFriend(this.getNameForScoreboard()))
         )
       {
         var1.setReturnValue(false);
      }
   }

   @Redirect(method = "updateVelocity", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getYaw()F"))
   public float movementCorrection(Entity var1) {
      return var1 instanceof ClientPlayerEntity && ZenithClient.on23().CloudRouter().ZClass092() != null
         ? ZenithClient.on23().CloudRouter().ZClass092().GrimGlide()
         : var1.getYaw();
   }

   @Inject(method = "onRemoved", at = @At("TAIL"))
   public void onRemoved(CallbackInfo var1) {
      if ((Object)this instanceof PlayerEntity playerentity) {
         TranslationKey.EventWindowSizeChanged(playerentity.getId());
      }
   }

   @Inject(method = "isGlowing", at = @At("HEAD"), cancellable = true)
   public void zenith_shaderEspGlowing(CallbackInfoReturnable<Boolean> var1) {
      Entity entity = (Entity)(Object)this;
      ShaderESP lii1l1ili11ill1l1 = ShaderESP.shaderESP;
      if (lii1l1ili11ill1l1 != null && lii1l1ili11ill1l1.isEnabled() && lii1l1ili11ill1l1.BotFeatureRegistry(entity)) {
         var1.setReturnValue(true);
      } else if (Predictions.predictions.MediaTrackInfo(entity)) {
         var1.setReturnValue(true);
      }
   }
}
