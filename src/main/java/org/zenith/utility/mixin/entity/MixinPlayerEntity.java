package org.zenith.utility.mixin.entity;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.ZenithClient;
import org.zenith.event.AttackEntityEvent;
import org.zenith.event.PushCollisionType;
import org.zenith.event.EventPushOutOfBlocks;
import org.zenith.module.movement.AutoSprint;
import org.zenith.module.combat.Reach;
import org.zenith.rotation.Rotation;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity {
   @Inject(method = "isPushedByFluids", at = @At("HEAD"), cancellable = true)
   public void isPushedByFluids(CallbackInfoReturnable<Boolean> var1) {
      EventPushOutOfBlocks li1liiliill1 = new EventPushOutOfBlocks(PushCollisionType.FLUIDS);
      EventManager.call(li1liiliill1);
      if (li1liiliill1.isCancelled()) {
         var1.setReturnValue(false);
      }
   }

   @Redirect(
      method = "travel",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;")
   )
   public Vec3d fixSwing(PlayerEntity var1) {
      return (Object)this != MinecraftClient.getInstance().player ? var1.getRotationVector() : ZenithClient.on23().CloudRouter().LineShader().int202();
   }

   @Redirect(method = "knockbackTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(DDD)Lnet/minecraft/util/math/Vec3d;"))
   public Vec3d hookSlowVelocity(Vec3d var1, double var2, double var4, double var6) {
      if ((Object)this == MinecraftClient.getInstance().player && AutoSprint.autoSprint.isEnabled() && AutoSprint.autoSprint.call070()) {
         var2 = var6 = AutoSprint.autoSprint.call157();
      }

      return var1.multiply(var2, var4, var6);
   }

   @WrapWithCondition(method = "knockbackTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setSprinting(Z)V", ordinal = 0))
   public boolean hookSlowVelocity(PlayerEntity var1, boolean var2) {
      return (Object)this != MinecraftClient.getInstance().player ? true : AutoSprint.autoSprint.isEnabled() && AutoSprint.autoSprint.call070() || var2;
   }

   @ModifyExpressionValue(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSprinting()Z"))
   public boolean hookSlowVelocity(boolean var1) {
      return (Object)this == MinecraftClient.getInstance().player && AutoSprint.autoSprint.isEnabled() && AutoSprint.autoSprint.call070()
         ? MinecraftClient.getInstance().player.isSprinting()
         : var1;
   }

   @Inject(method = "getBlockInteractionRange", at = @At("RETURN"), cancellable = true)
   public void reachBlock(CallbackInfoReturnable<Double> var1) {
      if ((Object)this == MinecraftClient.getInstance().player
         && Reach.reach2.isEnabled()
         && Reach.reach2.modeSettingVar15914.isEnabled()
         && Reach.reach2.reachBlock.getCurrent() > (Double)var1.getReturnValue()) {
         var1.setReturnValue((double)Reach.reach2.reachBlock.getCurrent());
      }
   }

   @Inject(method = "attack", at = @At("RETURN"))
   public void eventAttackEnd(Entity var1, CallbackInfo var2) {
      EventManager.call(new AttackEntityEvent(var1, AttackEntityEvent.on23.call077));
   }

   @Inject(method = "attack", at = @At("HEAD"))
   public void eventAttackHEad(Entity var1, CallbackInfo var2) {
      EventManager.call(new AttackEntityEvent(var1, AttackEntityEvent.on23.call185));
   }

   @Inject(method = "getEntityInteractionRange", at = @At("RETURN"), cancellable = true)
   public void reach(CallbackInfoReturnable<Double> var1) {
      if ((Object)this == MinecraftClient.getInstance().player
         && Reach.reach2.isEnabled()
         && Reach.reach2.modeSettingVar15914.isEnabled()
         && Reach.reach2.reach.getCurrent() > (Double)var1.getReturnValue()) {
         var1.setReturnValue((double)Reach.reach2.reach.getCurrent());
      }
   }

   @ModifyExpressionValue(method = "knockbackTarget", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getYaw()F"))
   public float hookFixRotation(float var1) {
      if ((Object)this != MinecraftClient.getInstance().player) {
         return var1;
      }

      Rotation ililiiili1ll1li11 = ZenithClient.on23().CloudRouter().ZClass092();
      return ililiiili1ll1li11 == null ? var1 : ililiiili1ll1li11.GrimGlide();
   }
}
