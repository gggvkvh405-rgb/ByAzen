package org.zenith.client.screens.override.main;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.base.font.ResourceProvider;
import org.zenith.core.AvatarRenderer;
import org.zenith.core.ClientProvider;
import org.zenith.core.ShaderWrapper;
import org.zenith.render.LegacyRenderBridge;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public final class MainMenuBlurRenderer implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int DOWNSAMPLE_FACTOR = 2;
   public static final float[] KAWASE_OFFSETS = new float[]{0.5F, 1.5F, 2.5F, 3.0F};
   public SimpleFramebuffer backgroundFbo;
   public SimpleFramebuffer tempFbo;
   public ShaderWrapper kawaseShaderKey;
   public ShaderWrapper maskShaderKey;

   public void capture(HudDrawContext var1, float var2) {
      if (minecraftClient3.isFinishedLoading() && minecraftClient3.getOverlay() == null && minecraftClient3.getShaderLoader() != null && var2 > 0.0F) {
         MsdfRenderer.flushBatch();
         Framebuffer framebuffer = minecraftClient3.getFramebuffer();
         this.ensureFramebuffers(framebuffer);
         this.blurCurrentFramebuffer(framebuffer, org.zenith.render.GuiMatrixAdapter.toMatrix4f(var1.getMatrices()), var2);
      }
   }

   public void render(Matrix4f var1, float var2, float var3, float var4, float var5, CornerRadius var6, ArgbColor var7) {
      if (minecraftClient3.isFinishedLoading() && this.backgroundFbo != null && minecraftClient3.getShaderLoader() != null) {
         if (this.maskShaderKey == null) {
            this.maskShaderKey = new ShaderWrapper(ResourceProvider.getShaderIdentifier("wtf/data"), VertexFormats.POSITION_COLOR);
         }

         if (this.isShaderReady(this.maskShaderKey)) {
            MsdfRenderer.flushBatch();
            org.zenith.render.LegacyRenderBridge.enableBlend();
            org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
            org.zenith.render.LegacyRenderBridge.disableCull();

            try {
               LegacyRenderBridge.setTexture(0, this.backgroundFbo.getColorAttachmentView());
               this.maskShaderKey.float251();
               this.maskShaderKey.HudArmorPanel("Size").set(var4, var5);
               this.maskShaderKey.HudArmorPanel("Radius").set(var6.var14311(), var6.string63(), var6.var14312(), var6.itemStack9());
               this.maskShaderKey.HudArmorPanel("Smoothness").set(0.01F);
               BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
               bufferbuilder.vertex(var1, var2, var3, 0.0F).color(var7.call001());
               bufferbuilder.vertex(var1, var2, var3 + var5, 0.0F).color(var7.call001());
               bufferbuilder.vertex(var1, var2 + var4, var3 + var5, 0.0F).color(var7.call001());
               bufferbuilder.vertex(var1, var2 + var4, var3, 0.0F).color(var7.call001());
               org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
            } finally {
               LegacyRenderBridge.clearTexture();
               org.zenith.render.LegacyRenderBridge.enableCull();
               org.zenith.render.LegacyRenderBridge.disableBlend();
            }

            return;
         }
      }
   }

   public void blurCurrentFramebuffer(Framebuffer var1, Matrix4f var2, float var3) {
      if (!minecraftClient3.isFinishedLoading()) {
         return;
      }

      if (this.kawaseShaderKey == null) {
         this.kawaseShaderKey = new ShaderWrapper(ResourceProvider.getShaderIdentifier("kawase_blur/data"), VertexFormats.POSITION_COLOR);
      }

      if (this.isShaderReady(this.kawaseShaderKey)) {
         float f = 1.0F / this.backgroundFbo.textureWidth;
         float f1 = 1.0F / this.backgroundFbo.textureHeight;
         org.zenith.render.LegacyRenderBridge.disableBlend();
         org.zenith.render.LegacyRenderBridge.disableCull();

         try {
            this.downsample(var1, this.backgroundFbo);
            SimpleFramebuffer simpleframebuffer = this.backgroundFbo;
            SimpleFramebuffer simpleframebuffer1 = this.tempFbo;

            for (float f2 : KAWASE_OFFSETS) {
               float f3 = f2 * var3 / 10.0F;
               if (!this.kawasePass(simpleframebuffer.getColorAttachmentView(), simpleframebuffer1, var2, f, f1, f3)) {
                  return;
               }

               SimpleFramebuffer simpleframebuffer2 = simpleframebuffer;
               simpleframebuffer = simpleframebuffer1;
               simpleframebuffer1 = simpleframebuffer2;
            }
         } finally {
            org.zenith.render.LegacyRenderBridge.enableCull();
            LegacyRenderBridge.restoreMainOutput();
         }
      }
   }

   public void downsample(Framebuffer var1, Framebuffer var2) {
      var1.drawBlit(var2.getColorAttachmentView());
   }

   public boolean kawasePass(GpuTextureView var1, Framebuffer var2, Matrix4f var3, float var4, float var5, float var6) {
      LegacyRenderBridge.setOutput(var2);
      LegacyRenderBridge.setTexture(0, var1);
      this.kawaseShaderKey.float251();
      this.kawaseShaderKey.HudArmorPanel("Resolution").set(var4, var5);
      this.kawaseShaderKey.HudArmorPanel("Offset").set(var6);
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         AvatarRenderer.UiAnimation(var3, bufferbuilder, 0.0F, 0.0F, var2.textureWidth, var2.textureHeight, -1);
         org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
      LegacyRenderBridge.clearTexture();
      return true;
   }

   public void ensureFramebuffers(Framebuffer var1) {
      int i = Math.max(1, var1.textureWidth / 2);
      int j = Math.max(1, var1.textureHeight / 2);
      this.backgroundFbo = this.ensureFramebuffer(this.backgroundFbo, i, j);
      this.tempFbo = this.ensureFramebuffer(this.tempFbo, i, j);
   }

   public SimpleFramebuffer ensureFramebuffer(SimpleFramebuffer var1, int var2, int var3) {
      if (var1 == null) {
         var1 = new SimpleFramebuffer("Zenith main menu blur", var2, var3, false);
      } else if (var1.textureWidth != var2 || var1.textureHeight != var3) {
         var1.resize(var2, var3);
      }

      return var1;
   }

   public boolean isShaderReady(ShaderWrapper var1) {
      return var1 != null;
   }

   public void close() {
      if (this.backgroundFbo != null) {
         this.backgroundFbo.delete();
         this.backgroundFbo = null;
      }

      if (this.tempFbo != null) {
         this.tempFbo.delete();
         this.tempFbo = null;
      }
   }
}
