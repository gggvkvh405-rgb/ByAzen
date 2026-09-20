package org.zenith.utility.mixin.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.PlayerLikeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.managers.FriendFilter;
import org.zenith.module.render.EntityESP;
import org.zenith.module.movement.Speed;
import org.zenith.module.misc.StreamerMode;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerEntityRenderer {
   @Inject(
      method = "updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
      at = @At("RETURN")
   )
   public void zenith_updatePlayerRenderState(PlayerLikeEntity var1, PlayerEntityRenderState var2, float var3, CallbackInfo var4) {
      if (var1 == MinecraftClient.getInstance().player
         && Speed.speed11.isEnabled()
         && Speed.speed11.call016()
         && (var1.isGliding() || var1.getPose() == EntityPose.GLIDING)) {
         var2.isGliding = false;
         var2.glidingTicks = 0.0F;
         var2.leaningPitch = 0.0F;
         var2.pose = EntityPose.STANDING;
         var2.height = var1.getDimensions(EntityPose.STANDING).height();
         var2.width = var1.getDimensions(EntityPose.STANDING).width();
         var2.standingEyeHeight = var1.getEyeHeight(EntityPose.STANDING);
         var2.isInSneakingPose = false;
         var2.isSwimming = false;
         var2.applyFlyingRotation = false;
         var2.flyingRotation = 0.0F;
      }

      if (var2.playerName != null) {
         var2.playerName = StreamerMode.streamerMode.ItemRegistry(var2.playerName);
      }
      if (var2.displayName != null) {
         var2.displayName = StreamerMode.streamerMode.ItemRegistry(var2.displayName);
      }
   }

   @Inject(
      method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
      at = @At("HEAD"),
      cancellable = true
   )
   public void zenith_filterPlayerLabel(
      PlayerEntityRenderState var1,
      MatrixStack var2,
      OrderedRenderCommandQueue var3,
      CameraRenderState var4,
      CallbackInfo var5
   ) {
      if (EntityESP.entityESP.float55()) {
         var5.cancel();
      }

      if (FriendFilter.PotionItemBuilder(var1.id)) {
         var5.cancel();
      }
   }
}
