package org.zenith.utility.mixin.entity;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.zenith.ZenithClient;
import org.zenith.module.movement.ElytraBooster;
import org.zenith.rotation.Rotation;

@Mixin(FireworkRocketEntity.class)
public abstract class MixinFireworkRocketEntity {
   @Shadow
   public LivingEntity shooter;

   @ModifyExpressionValue(
      method = "tick",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getRotationVector()Lnet/minecraft/util/math/Vec3d;")
   )
   public Vec3d getRotationVector(Vec3d var1) {
      if (this.shooter != MinecraftClient.getInstance().player) {
         return var1;
      }

      Rotation ililiiili1ll1li11 = ZenithClient.on23().CloudRouter().ZClass092();
      return ililiiili1ll1li11 == null ? var1 : ililiiili1ll1li11.int202();
   }

   @ModifyArgs(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;add(DDD)Lnet/minecraft/util/math/Vec3d;", ordinal = 0))
   public void hookExtendedFirework(Args var1, @Local(ordinal = 0) Vec3d var2, @Local(ordinal = 1) Vec3d var3) {
      if (this.shooter == MinecraftClient.getInstance().player && ElytraBooster.elytraBooster.isEnabled()) {
         var1.set(0, var2.x * 0.1 + (var2.x * ElytraBooster.elytraBooster.call095() - var3.x) * 0.5);
         var1.set(1, var2.y * 0.1 + (var2.y * ElytraBooster.elytraBooster.call095() - var3.y) * 0.5);
         var1.set(2, var2.z * 0.1 + (var2.z * ElytraBooster.elytraBooster.call095() - var3.z) * 0.5);
      }
   }
}
