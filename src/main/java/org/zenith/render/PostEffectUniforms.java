package org.zenith.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.PostEffectPass;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import org.zenith.utility.mixin.accessors.PostEffectPassAccessor;

/** Writes Zenith's dynamic values to the std140 block used by post effects. */
public final class PostEffectUniforms {
   private PostEffectUniforms() {
   }

   public static Value floatValue(float value) {
      return new Value(Type.FLOAT, value, 0.0F, 0.0F, 0.0F);
   }

   public static Value vec2(float x, float y) {
      return new Value(Type.VEC2, x, y, 0.0F, 0.0F);
   }

   public static Value vec3(float x, float y, float z) {
      return new Value(Type.VEC3, x, y, z, 0.0F);
   }

   public static Value color(int color) {
      return new Value(
         Type.VEC4,
         (color >> 16 & 0xFF) / 255.0F,
         (color >> 8 & 0xFF) / 255.0F,
         (color & 0xFF) / 255.0F,
         (color >>> 24) / 255.0F
      );
   }

   public static void update(PostEffectPass pass, Value... values) {
      GpuBuffer buffer = ((PostEffectPassAccessor)pass).getUniformBuffers().get("ZenithData");
      if (buffer == null) {
         return;
      }

      Std140SizeCalculator size = new Std140SizeCalculator();
      for (Value value : values) {
         value.addSize(size);
      }

      try (MemoryStack stack = MemoryStack.stackPush()) {
         Std140Builder builder = Std140Builder.onStack(stack, size.get());
         for (Value value : values) {
            value.write(builder);
         }
         RenderSystem.getDevice().createCommandEncoder().writeToBuffer(buffer.slice(), builder.get());
      }
   }

   public record Value(Type type, float x, float y, float z, float w) {
      private void addSize(Std140SizeCalculator size) {
         switch (this.type) {
            case FLOAT -> size.putFloat();
            case VEC2 -> size.putVec2();
            case VEC3 -> size.putVec3();
            case VEC4 -> size.putVec4();
         }
      }

      private void write(Std140Builder builder) {
         switch (this.type) {
            case FLOAT -> builder.putFloat(this.x);
            case VEC2 -> builder.putVec2(new Vector2f(this.x, this.y));
            case VEC3 -> builder.putVec3(new Vector3f(this.x, this.y, this.z));
            case VEC4 -> builder.putVec4(new Vector4f(this.x, this.y, this.z, this.w));
         }
      }
   }

   public enum Type {
      FLOAT,
      VEC2,
      VEC3,
      VEC4
   }
}
