package org.zenith.utility.mixin.render;

import net.minecraft.client.gl.GlCommandEncoder;
import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.module.render.WorldTweaks;

@Mixin(GlCommandEncoder.class)
public class MixinGlCommandEncoder {
   @Inject(method = "setupRenderPass", at = @At("RETURN"))
   private void zenith_uploadSaturation(CallbackInfoReturnable<Boolean> callback) {
      if (!callback.getReturnValue()) {
         return;
      }

      int program = GL20C.glGetInteger(GL20C.GL_CURRENT_PROGRAM);
      int uniform = GL20C.glGetUniformLocation(program, "ZenithSaturation");
      if (uniform != -1) {
         GL20C.glUniform1f(uniform, WorldTweaks.worldTweaks.int326());
      }
   }
}
