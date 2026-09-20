package org.zenith.utility.mixin.entity;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.events.Event;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.ZenithClient;
import org.zenith.event.CloseScreenEvent;
import org.zenith.event.SprintPacketEvent;
import org.zenith.event.PushCollisionType;
import org.zenith.event.EventMotion;
import org.zenith.event.EventPushOutOfBlocks;
import org.zenith.event.EventTick;
import org.zenith.event.EventTickEnd;
import org.zenith.event.ItemUseEvent;
import org.zenith.event.PlayerMoveEvent;
import org.zenith.event.SprintStateEvent;
import org.zenith.rotation.Rotation;
import org.zenith.module.misc.NoFriendDamage;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
   @Shadow
   @Final
   protected MinecraftClient client;

   public MixinClientPlayerEntity(ClientWorld var1, GameProfile var2) {
      super(var1, var2);
   }

   @Shadow
   public abstract void sendSprintingPacket();

   @Shadow
   protected abstract void autoJump(float var1, float var2);

   @Inject(method = "tick", at = @At("HEAD"))
   public void tick(CallbackInfo var1) {
      dispatchEvent(new EventTick());
   }

   @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;tick()V", shift = Shift.AFTER))
   public void tickEnd(CallbackInfo var1) {
      dispatchEvent(new EventTickEnd());
   }

   @Inject(method = "sendMovementPackets", at = @At("RETURN"))
   public void motion(CallbackInfo var1) {
      dispatchEvent(new EventMotion());
   }

   @Redirect(method = "sendMovementPackets", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendSprintingPacket()V"))
   public void invokeSprintUpdate(ClientPlayerEntity var1) {
      SprintPacketEvent lillllii11iiill11i = new SprintPacketEvent();
      dispatchEvent(lillllii11iiill11i);
      if (!lillllii11iiill11i.isCancelled()) {
         this.sendSprintingPacket();
      }
   }

   @Inject(method = "canSprint", at = @At("RETURN"), cancellable = true)
   public void zenith_canSprint(boolean starting, CallbackInfoReturnable<Boolean> var1) {
      SprintStateEvent i1ilii1l1l1lll = new SprintStateEvent(var1.getReturnValue());
      dispatchEvent(i1ilii1l1l1lll);
      var1.setReturnValue(i1ilii1l1l1lll.isCancelled() || i1ilii1l1l1lll.Speed());
   }

   @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
   public void pushOutOfBlocks(double var1, double var3, CallbackInfo var5) {
      EventPushOutOfBlocks li1liiliill1 = new EventPushOutOfBlocks(PushCollisionType.BLOCKS);
      dispatchEvent(li1liiliill1);
      if (li1liiliill1.isCancelled()) {
         var5.cancel();
      }
   }

   @ModifyExpressionValue(
      method = {"sendMovementPackets", "tick"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getYaw()F")
   )
   public float hookSilentRotationYaw(float var1) {
      Rotation ililiiili1ll1li11 = ZenithClient.on23().CloudRouter().ZClass092();
      return ililiiili1ll1li11 == null ? var1 : ililiiili1ll1li11.GrimGlide();
   }

   @ModifyExpressionValue(
      method = {"sendMovementPackets", "tick"},
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getPitch()F")
   )
   public float hookSilentRotationPitch(float var1) {
      Rotation ililiiili1ll1li11 = ZenithClient.on23().CloudRouter().ZClass092();
      return ililiiili1ll1li11 == null ? var1 : ililiiili1ll1li11.GuiWalk();
   }

   @ModifyExpressionValue(
      method = "getCrosshairTarget(Lnet/minecraft/entity/Entity;DDF)Lnet/minecraft/util/hit/HitResult;",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;getRotationVec(F)Lnet/minecraft/util/math/Vec3d;")
   )
   private static Vec3d hookCrosshairRotation(Vec3d original, Entity cameraEntity, double blockRange, double entityRange, float tickProgress) {
      Rotation rotation = ZenithClient.on23().CloudRouter().ZClass092();
      return cameraEntity == MinecraftClient.getInstance().player && rotation != null ? rotation.int202() : original;
   }

   @ModifyExpressionValue(
      method = {
         "getCrosshairTarget(FLnet/minecraft/entity/Entity;)Lnet/minecraft/util/hit/HitResult;",
         "getCrosshairTarget(Lnet/minecraft/entity/Entity;DDF)Lnet/minecraft/util/hit/HitResult;"
      },
      at = @At(value = "FIELD", target = "Lnet/minecraft/predicate/entity/EntityPredicates;CAN_HIT:Ljava/util/function/Predicate;")
   )
   private static Predicate<Entity> hookCrosshairPredicate(Predicate<Entity> predicate) {
      return !NoFriendDamage.noFriendDamage.isEnabled()
         ? predicate
         : entity -> predicate.test(entity) && !ZenithClient.on23().MediaTrackInfo().UiAnimation(entity);
   }

   @Redirect(
      method = "applyMovementSpeedFactors(Lnet/minecraft/util/math/Vec2f;)Lnet/minecraft/util/math/Vec2f;",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z")
   )
   public boolean onIsUsingItemRedirect(ClientPlayerEntity var1) {
      if (var1.isUsingItem()) {
         ItemUseEvent li1ii1ll1iili1l1lil1 = new ItemUseEvent();
         dispatchEvent(li1ii1ll1iili1l1lil1);
         return var1.isUsingItem() && var1.getVehicle() == null && !li1ii1ll1iili1l1lil1.isCancelled();
      } else {
         return var1.isUsingItem() && var1.getVehicle() == null;
      }
   }

   @Inject(method = "closeHandledScreen", at = @At("HEAD"), cancellable = true)
   public void closeHandledScreenHook(CallbackInfo var1) {
      CloseScreenEvent i1l11ll1l1l11l1111li1 = new CloseScreenEvent(this.client.currentScreen);
      dispatchEvent(i1l11ll1l1l11l1111li1);
      if (i1l11ll1l1l11l1111li1.isCancelled()) {
         var1.cancel();
      }
   }

   @Inject(
      method = "move",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;move(Lnet/minecraft/entity/MovementType;Lnet/minecraft/util/math/Vec3d;)V"
      ),
      cancellable = true
   )
   public void onMoveHook(MovementType var1, Vec3d var2, CallbackInfo var3) {
      PlayerMoveEvent i1lil1ii11ll111l1li1il = new PlayerMoveEvent(var2);
      dispatchEvent(i1lil1ii11ll111l1li1il);
      double d0 = this.getX();
      double d1 = this.getZ();
      super.move(var1, i1lil1ii11ll111l1li1il.NoPush());
      this.autoJump((float)(this.getX() - d0), (float)(this.getZ() - d1));
      var3.cancel();
   }

   private static void dispatchEvent(Event var0) {
      EventManager.call(var0);
   }
}
