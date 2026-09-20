package org.zenith.render;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.OptionalInt;
import java.util.OptionalDouble;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.zenith.core.ShaderWrapper;

/**
 * Small compatibility facade for render calls emitted by the 1.21.4 client.
 * State is captured by {@link LegacyImmediateRenderer} and translated to an
 * explicit 1.21.11 render pipeline when a buffer is submitted.
 */
public final class LegacyRenderBridge {
   private LegacyRenderBridge() {
   }

   public static void usePositionColor() { LegacyImmediateRenderer.clearShader(); }
   public static void usePositionTexColor() { LegacyImmediateRenderer.clearShader(); }
   public static void useLines() { LegacyImmediateRenderer.clearShader(); }
   public static void useCustom(ShaderWrapper shader) { LegacyImmediateRenderer.useShader(shader); }

   public static void setTexture(int unit, Identifier texture) {
      if (unit == 0) LegacyImmediateRenderer.setTexture(texture);
   }

   public static void setTexture(int unit, AbstractTexture texture) {
      if (unit == 0) LegacyImmediateRenderer.setTexture(texture);
   }

   public static void setTexture(int unit, GpuTextureView texture) {
      if (unit == 0) LegacyImmediateRenderer.setTexture(texture);
   }

   /** Old OpenGL texture handles cannot be rebound in the new GPU API. */
   public static void setTexture(int unit, int ignoredTextureHandle) {
      if (unit == 0 && ignoredTextureHandle == 0) LegacyImmediateRenderer.clearTexture();
   }

   public static void setOutput(Framebuffer framebuffer) { LegacyImmediateRenderer.setTarget(framebuffer); }
   public static void restoreMainOutput() { LegacyImmediateRenderer.clearTarget(); }
   public static void clearTexture() { LegacyImmediateRenderer.clearTexture(); }
   public static void draw(BuiltBuffer buffer) { LegacyImmediateRenderer.draw(buffer); }

   public static void enableBlend() { }
   public static void disableBlend() { LegacyImmediateRenderer.defaultBlend(); }
   public static void defaultBlendFunc() { LegacyImmediateRenderer.defaultBlend(); }
   public static void blendFunc(SourceFactor source, DestFactor destination) {
      LegacyImmediateRenderer.setBlend(new BlendFunction(source, destination));
   }
   public static void blendFunc(int source, int destination) {
      blendFunc(sourceFactor(source), destFactor(destination));
   }
   public static void blendFuncSeparate(SourceFactor sourceColor, DestFactor destinationColor, SourceFactor sourceAlpha, DestFactor destinationAlpha) {
      LegacyImmediateRenderer.setBlend(new BlendFunction(sourceColor, destinationColor, sourceAlpha, destinationAlpha));
   }

   public static void enableDepthTest() { LegacyImmediateRenderer.enableDepthTest(); }
   public static void disableDepthTest() { LegacyImmediateRenderer.disableDepthTest(); }
   public static void depthMask(boolean enabled) { LegacyImmediateRenderer.depthMask(enabled); }
   public static void enableCull() { }
   public static void disableCull() { }
   public static void lineWidth(float ignoredWidth) { }
   public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) { }
   public static void disableScissor() { }
   public static void setShaderColor(float red, float green, float blue, float alpha) {
      LegacyImmediateRenderer.setShaderColor(red, green, blue, alpha);
   }
   public static float[] getShaderColor() { return LegacyImmediateRenderer.getShaderColor(); }

   public static void activeTexture(int ignoredUnit) { }
   public static void bindTexture(int ignoredTextureHandle) { }
   public static void bindTexture(GpuTextureView texture) { LegacyImmediateRenderer.setTexture(texture); }
   public static void viewport(int x, int y, int width, int height) { }
   public static void clearColor(float red, float green, float blue, float alpha) { }
   public static void clear(int mask) {
      Framebuffer framebuffer = MinecraftClientHolder.framebuffer();
      boolean color = (mask & 16384) != 0;
      boolean depth = (mask & 256) != 0 && framebuffer.useDepthAttachment;
      if (color && depth) {
         RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(
            framebuffer.getColorAttachment(), 0, framebuffer.getDepthAttachment(), 1.0
         );
      } else if (color) {
         try (var ignored = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
            () -> "Zenith clear color", framebuffer.getColorAttachmentView(), OptionalInt.of(0)
         )) {
         }
      } else if (depth) {
         LegacyImmediateRenderer.clearDepth();
      }
   }

   public static void clear(Framebuffer framebuffer) {
      setOutput(framebuffer);
      clear(framebuffer.useDepthAttachment ? 16640 : 16384);
   }

   public static void copyColor(Framebuffer source, Framebuffer target) {
      int width = Math.min(source.textureWidth, target.textureWidth);
      int height = Math.min(source.textureHeight, target.textureHeight);
      RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(
         source.getColorAttachment(), target.getColorAttachment(), 0, 0, 0, 0, 0, width, height
      );
   }

   private static final class MinecraftClientHolder {
      private static Framebuffer framebuffer() {
         return LegacyImmediateRenderer.getTargetOrMain();
      }
   }

   private static SourceFactor sourceFactor(int value) {
      return switch (value) {
         case 0 -> SourceFactor.ZERO;
         case 1 -> SourceFactor.ONE;
         case 770 -> SourceFactor.SRC_ALPHA;
         case 771 -> SourceFactor.ONE_MINUS_SRC_ALPHA;
         default -> SourceFactor.SRC_ALPHA;
      };
   }

   private static DestFactor destFactor(int value) {
      return switch (value) {
         case 0 -> DestFactor.ZERO;
         case 1 -> DestFactor.ONE;
         case 770 -> DestFactor.SRC_ALPHA;
         case 771 -> DestFactor.ONE_MINUS_SRC_ALPHA;
         default -> DestFactor.ONE_MINUS_SRC_ALPHA;
      };
   }
}
