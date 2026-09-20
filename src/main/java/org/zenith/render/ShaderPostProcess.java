package org.zenith.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.PostEffectPass;
import net.minecraft.client.gl.PostEffectProcessor;
import net.minecraft.client.gl.PostEffectProcessor.FramebufferSet;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DefaultFramebufferSet;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.zenith.module.render.ShaderFog;
import org.zenith.utility.mixin.accessors.PostEffectProcessorAccessor;

public final class ShaderPostProcess {
   public static final Identifier[] val484 = new Identifier[]{
      Identifier.of("zenith", "shader_fog_gradient"),
      Identifier.of("zenith", "shader_fog_galaxy"),
      Identifier.of("zenith", "shader_fog_aqua"),
      Identifier.of("zenith", "shader_fog_purple"),
      Identifier.of("zenith", "shader_fog_overcast")
   };
   public static long long132;
   public static float float185;

   public static boolean render(FrameGraphBuilder var0, int var1, int var2, FramebufferSet var3, Camera var4) {
      ShaderFog li111l1l1i1l1 = ShaderFog.shaderFog;
      if (li111l1l1i1l1.double156()) {
         PostEffectProcessor posteffectprocessor = MinecraftClient.getInstance()
            .getShaderLoader()
            .loadPostEffect(EventHookPacketProcess(li111l1l1i1l1.call224()), DefaultFramebufferSet.MAIN_ONLY);
         if (posteffectprocessor == null) {
            return false;
         }

         updateUniforms(posteffectprocessor, var1, var2, var4, EventInteractBlock(li111l1l1i1l1.call179()));
         posteffectprocessor.render(var0, var1, var2, var3);
         return true;
      } else {
         long132 = -1L;
         return false;
      }
   }

   public static Identifier EventHookPacketProcess(int var0) {
      int i = Math.clamp(var0, 0, val484.length - 1);
      return val484[i];
   }

   private static void updateUniforms(PostEffectProcessor var0, int var1, int var2, Camera var3, float var4) {
      ShaderFog li111l1l1i1l1 = ShaderFog.shaderFog;
      float f = var1 / Math.max(var2, 1.0F);
      float f1 = ((Integer)MinecraftClient.getInstance().options.getFov().getValue()).floatValue();
      float f2 = (float)Math.tan(Math.toRadians(f1) * 0.5);
      float f3 = (float)Math.toRadians(var3.getPitch());
      float f4 = (float)(Math.PI - Math.toRadians(var3.getYaw()));

      for (PostEffectPass posteffectpass : ((PostEffectProcessorAccessor)var0).getPasses()) {
         Vec3d cameraPos = var3.getCameraPos();
         PostEffectUniforms.update(
            posteffectpass,
            PostEffectUniforms.color(li111l1l1i1l1.call225()),
            PostEffectUniforms.color(li111l1l1i1l1.call226()),
            PostEffectUniforms.color(li111l1l1i1l1.call227()),
            PostEffectUniforms.floatValue(li111l1l1i1l1.call223()),
            PostEffectUniforms.floatValue(var4),
            PostEffectUniforms.vec2(var1, var2),
            PostEffectUniforms.vec3((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z),
            PostEffectUniforms.floatValue(f3),
            PostEffectUniforms.floatValue(f4),
            PostEffectUniforms.floatValue(f2),
            PostEffectUniforms.floatValue(f)
         );
      }
   }

   public static float EventInteractBlock(float var0) {
      long i = System.nanoTime();
      if (long132 < 0L) {
         long132 = i;
         return float185;
      } else {
         float f = Math.min((float)(i - long132) / 1.0E9F, 0.1F);
         long132 = i;
         float185 = (float185 + f * Math.max(0.0F, var0)) % 10000.0F;
         return float185;
      }
   }

}
