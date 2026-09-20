package org.zenith.utility.mixin.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.module.render.HandFire;

@Mixin(ItemRenderer.class)
public class MixinItemRenderer {
   @Inject(
      method = "getSpecialItemGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;Lnet/minecraft/client/util/math/MatrixStack$Entry;)Lnet/minecraft/client/render/VertexConsumer;",
      at = @At("HEAD"),
      cancellable = true
   )
   private static void noSpecialGlintInHandFirePass(
      VertexConsumerProvider var0, RenderLayer var1, MatrixStack.Entry var2, CallbackInfoReturnable<VertexConsumer> var3
   ) {
      if (HandFire.zClass101()) {
         var3.setReturnValue(var0.getBuffer(var1));
      }
   }

   @Inject(
      method = "getItemGlintConsumer(Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/render/RenderLayer;ZZ)Lnet/minecraft/client/render/VertexConsumer;",
      at = @At("HEAD"),
      cancellable = true
   )
   private static void noItemGlintInHandFirePass(VertexConsumerProvider var0, RenderLayer var1, boolean var2, boolean var3, CallbackInfoReturnable<VertexConsumer> var4) {
      if (HandFire.zClass101()) {
         var4.setReturnValue(var0.getBuffer(var1));
      }
   }
}
