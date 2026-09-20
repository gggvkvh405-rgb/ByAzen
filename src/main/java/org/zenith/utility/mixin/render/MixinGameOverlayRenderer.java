package org.zenith.utility.mixin.render;

import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.module.render.NoRender;

@Mixin(InGameOverlayRenderer.class)
public class MixinGameOverlayRenderer {
   @Inject(method = "renderFireOverlay", at = @At("HEAD"), cancellable = true)
   private static void removeFireOverlay(MatrixStack var0, VertexConsumerProvider var1, Sprite var2, CallbackInfo var3) {
      if (NoRender.noRender.float379()) {
         var3.cancel();
      }
   }

   @Inject(method = "renderInWallOverlay", at = @At("HEAD"), cancellable = true)
   private static void renderInWallOverlayHook(Sprite var0, MatrixStack var1, VertexConsumerProvider var2, CallbackInfo var3) {
      if (NoRender.noRender.float377()) {
         var3.cancel();
      }
   }
}
