package org.zenith.utility.mixin.render;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.systems.RenderPass;
import java.util.function.Consumer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.render.RenderPassSetup;

@Mixin(RenderLayer.class)
public abstract class MixinRenderLayer implements RenderPassSetup {
   @Unique
   private Consumer<RenderPass> zenith$renderPassSetup;

   @Override
   public RenderLayer zenith$withRenderPassSetup(Consumer<RenderPass> consumer) {
      this.zenith$renderPassSetup = consumer;
      return (RenderLayer)(Object)this;
   }

   @Inject(method = "draw", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderPass;drawIndexed(IIII)V"))
   private void zenith$applyRenderPassSetup(BuiltBuffer buffer, CallbackInfo callback, @Local RenderPass renderPass) {
      if (this.zenith$renderPassSetup != null) {
         this.zenith$renderPassSetup.accept(renderPass);
      }
   }
}
