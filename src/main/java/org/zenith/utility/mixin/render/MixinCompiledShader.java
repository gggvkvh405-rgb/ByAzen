package org.zenith.utility.mixin.render;

import java.util.regex.Matcher;
import net.minecraft.client.gl.GlBackend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zenith.utility.render.ShaderSaturationPatch;

@Mixin(GlBackend.class)
public class MixinCompiledShader {
   @ModifyArg(
      method = "compileShader(Lnet/minecraft/client/gl/GlBackend$ShaderKey;Lnet/minecraft/client/gl/ShaderSourceGetter;)Lnet/minecraft/client/gl/CompiledShader;",
      at = @At(
         value = "INVOKE",
         target = "Lcom/mojang/blaze3d/opengl/GlStateManager;glShaderSource(ILjava/lang/String;)V",
         remap = false
      ),
      index = 1
   )
   private static String injectSaturation(String var0) {
      if (var0.contains("uniform sampler2D Sampler0;")
         && var0.contains("uniform vec4 ColorModulator;")
         && var0.contains("out vec4 fragColor;")
         && !var0.contains("ZenithSaturation")) {
         Matcher matcher = ShaderSaturationPatch.COLOR_SAMPLE.matcher(var0);
         if (!matcher.find()) {
            return var0;
         }

         String s = var0.replace("uniform vec4 ColorModulator;", "uniform vec4 ColorModulator;\nuniform float ZenithSaturation;")
            .replace(
               "out vec4 fragColor;",
               "out vec4 fragColor;\n\nvec3 zenith_apply_saturation(vec3 color) {\n    float amount = max(ZenithSaturation, 0.0);\n    float luma = dot(color, vec3(0.299, 0.587, 0.114));\n    float boost = max(amount - 1.0, 0.0);\n    vec3 saturated = mix(vec3(luma), color, amount);\n    return clamp(saturated * (1.0 + boost * 0.35), 0.0, 1.0);\n}\n"
            );
         return ShaderSaturationPatch.COLOR_SAMPLE.matcher(s).replaceFirst("$1\n    color.rgb = zenith_apply_saturation(color.rgb);");
      } else {
         return var0;
      }
   }
}
