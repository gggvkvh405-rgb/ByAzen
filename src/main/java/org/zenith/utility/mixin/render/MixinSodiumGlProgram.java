package org.zenith.utility.mixin.render;

import org.lwjgl.opengl.GL20C;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.module.render.WorldTweaks;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gl.shader.GlProgram", remap = false)
public class MixinSodiumGlProgram {
   @Inject(method = "bind", at = @At("TAIL"), remap = false)
   public void uploadSaturation(CallbackInfo var1) {
      int i = GL20C.glGetInteger(35725);
      int j = GL20C.glGetUniformLocation(i, "ZenithSaturation");
      if (j != -1) {
         WorldTweaks il11llii1l1 = WorldTweaks.worldTweaks;
         float f = il11llii1l1.isEnabled() && il11llii1l1.modeSetting19.ConfigJsonUtil(3) ? il11llii1l1.contrastSetting.getCurrent() : 1.0F;
         GL20C.glUniform1f(j, f);
      }
   }
}
