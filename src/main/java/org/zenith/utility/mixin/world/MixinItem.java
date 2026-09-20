package org.zenith.utility.mixin.world;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.zenith.ZenithClient;
import org.zenith.rotation.Rotation;

@Mixin(Item.class)
public class MixinItem {
   @ModifyExpressionValue(
      method = "raycast",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getRotationVector(FF)Lnet/minecraft/util/math/Vec3d;")
   )
   private static Vec3d hookFixRotation(Vec3d var0, World var1, PlayerEntity var2, FluidHandling var3) {
      Rotation ililiiili1ll1li11 = ZenithClient.on23().CloudRouter().ZClass092();
      return var2 == MinecraftClient.getInstance().player && ililiiili1ll1li11 != null ? ililiiili1ll1li11.int202() : var0;
   }
}
