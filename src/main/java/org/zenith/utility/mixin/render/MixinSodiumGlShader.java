package org.zenith.utility.mixin.render;

import java.util.regex.Matcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.zenith.utility.render.ShaderSaturationPatch;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.gl.shader.GlShader", remap = false)
public class MixinSodiumGlShader {
   @ModifyArg(
      method = "<init>",
      at = @At(
         value = "INVOKE",
         target = "Lnet/caffeinemc/mods/sodium/client/gl/shader/ShaderWorkarounds;safeShaderSource(ILjava/lang/CharSequence;)V",
         remap = false
      ),
      index = 1,
      remap = false
   )
   public CharSequence injectSaturation(CharSequence var1) {
      String s = var1.toString();
      if (s.contains("uniform sampler2D u_BlockTex;") && s.contains("out vec4 fragColor;") && !s.contains("ZenithSaturation")) {
         Matcher matcher = ShaderSaturationPatch.DIFFUSE_COLOR.matcher(s);
         if (!matcher.find()) {
            return var1;
         }

         String s1 = s.replace("uniform sampler2D u_BlockTex;", "uniform sampler2D u_BlockTex;\nuniform float ZenithSaturation;")
            .replace(
               "out vec4 fragColor;",
               "out vec4 fragColor;\n\nvec3 zenith_apply_saturation(vec3 color) {\n    float amount = max(ZenithSaturation, 0.0);\n    float luma = dot(color, vec3(0.299, 0.587, 0.114));\n    float boost = max(amount - 1.0, 0.0);\n    vec3 saturated = mix(vec3(luma), color, amount);\n    return clamp(saturated * (1.0 + boost * 0.35), 0.0, 1.0);\n}\n"
            );
         return ShaderSaturationPatch.DIFFUSE_COLOR.matcher(s1).replaceFirst("$1\n    diffuseColor.rgb = zenith_apply_saturation(diffuseColor.rgb);");
      } else {
         return var1;
      }
   }
}
