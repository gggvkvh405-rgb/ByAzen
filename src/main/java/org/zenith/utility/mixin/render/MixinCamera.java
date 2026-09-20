package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.event.EventPosHook;
import org.zenith.event.SprintEvent;
import org.zenith.module.movement.Speed;
import org.zenith.rotation.Rotation;

@Mixin(Camera.class)
public abstract class MixinCamera {
   @Shadow
   public Vec3d pos;
   @Shadow
   public Entity focusedEntity;
   @Shadow
   @Final
   public Mutable blockPos;
   @Shadow
   public float yaw;
   @Shadow
   public float pitch;
   @Shadow
   public float cameraY;
   @Shadow
   public float lastCameraY;

   @Shadow
   protected abstract void setRotation(float var1, float var2);

   @Shadow
   protected abstract void moveBy(float var1, float var2, float var3);

   @Shadow
   protected abstract float clipToSpace(float var1);

   @Inject(method = "updateEyeHeight", at = @At("HEAD"), cancellable = true)
   public void zenith_keepStandingCameraHeight(CallbackInfo var1) {
      if (this.focusedEntity instanceof ClientPlayerEntity clientplayerentity
         && clientplayerentity == MinecraftClient.getInstance().player
         && Speed.speed11.isEnabled()
         && Speed.speed11.call016()) {
         float f = clientplayerentity.getEyeHeight(EntityPose.STANDING);
         this.cameraY = f;
         this.lastCameraY = f;
         var1.cancel();
      }
   }

   @Inject(method = "update", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/Camera;setPos(DDD)V", shift = Shift.AFTER), cancellable = true)
   public void updateHook(World var1, Entity var2, boolean var3, boolean var4, float var5, CallbackInfo var6) {
      SprintEvent ll1l1ii1ll1li1il = new SprintEvent(false, 4.0F, new Rotation(this.yaw, this.pitch));
      EventManager.call(ll1l1ii1ll1li1il);
      Rotation ililiiili1ll1li11 = ll1l1ii1ll1li1il.Velocity();
      if (ll1l1ii1ll1li1il.isCancelled() && var2 instanceof ClientPlayerEntity clientplayerentity && !clientplayerentity.isSleeping() && var3) {
         float f = var4 ? -ililiiili1ll1li11.GuiWalk() : ililiiili1ll1li11.GuiWalk();
         float f1 = ililiiili1ll1li11.GrimGlide() - (var4 ? 180 : 0);
         float f2 = ll1l1ii1ll1li1il.Timer();
         this.setRotation(f1, f);
         this.moveBy(ll1l1ii1ll1li1il.Strafe() ? -f2 : -this.clipToSpace(f2), 0.0F, 0.0F);
         var6.cancel();
      }
   }

   @Inject(method = "setPos(Lnet/minecraft/util/math/Vec3d;)V", at = @At("HEAD"), cancellable = true)
   public void posHook(Vec3d var1, CallbackInfo var2) {
      EventPosHook ii1iiiii1ll1iiil1li1l1iili1i = new EventPosHook(var1);
      EventManager.call(ii1iiiii1ll1iiil1li1l1iili1i);
      this.pos = var1 = ii1iiiii1ll1iiil1li1l1iili1i.WallBypass();
      this.blockPos.set(var1.x, var1.y, var1.z);
      var2.cancel();
   }
}
