package org.zenith.render;

import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import org.joml.Matrix4f;
import org.zenith.ZenithClient;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.base.font.ResourceProvider;
import org.zenith.core.AvatarRenderer;
import org.zenith.core.ClientProvider;
import org.zenith.core.ShaderWrapper;
import org.zenith.event.EventRenderScreenHook;
import org.zenith.module.render.Interface;
import org.zenith.module.render.Menu;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class BlurRenderer implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int int415 = 2;
   /** Keep the blur mask edge identical to the rounded-rectangle renderer. */
   private static final float MASK_SMOOTHNESS = 0.8F;
   /** An even pass count leaves the final blurred image in the primary FBO. */
   public static final float[] val519 = new float[]{0.5F, 1.5F, 2.5F, 3.0F};
   public SimpleFramebuffer fbo;
   public SimpleFramebuffer simpleFramebuffer7;
   public SimpleFramebuffer tempFbo;
   public ShaderWrapper shaderProgramKey2;
   public ShaderWrapper shaderProgramKey3;
   public ShaderWrapper shaderProgramKey4;
   public int int416 = 0;
   public int int417 = 0;
   public boolean boolean186 = true;

   public void executorService4() {
      this.int416 = 3;
      this.int417 = 10;
   }

   public void call266() {
      this.int416++;
      this.int417++;
   }

   public SimpleFramebuffer getFbo() {
      return this.fbo;
   }

   public BlurRenderer() {
      EventManager.register(this);
   }

   @EventTarget(0)
   public void on23(EventRenderScreenHook var1) {
      if (this.canRenderBlur() && (ZenithClient.on23().NbtEditor().getBlurPower() != 0.0F || Interface.interfaceField.float30())) {
         Framebuffer framebuffer = minecraftClient3.getFramebuffer();
         this.on23(framebuffer);
         if (this.int416 % 3 == 0) {
            float f = ZenithClient.on23().NbtEditor().getBlurPower();
            if (f == 0.0F) {
               return;
            }

            this.on23(this.fbo, GuiMatrixAdapter.toMatrix4f(var1.WarpFarm().getMatrices()), f);
            this.int416 = 0;
         }
      }
   }

   public void UiAnimation(HudDrawContext var1) {
      if (minecraftClient3.world != null
         && this.canRenderBlur()
         && ZenithClient.on23().NbtEditor().getBlurPower() != 0.0F
         && this.boolean186) {
         Framebuffer framebuffer = minecraftClient3.getFramebuffer();
         this.UiAnimation(framebuffer);
         if (this.int417 % (this.concurrentHashMap() ? 2 : 10) == 0) {
            this.on23(this.simpleFramebuffer7, GuiMatrixAdapter.toMatrix4f(var1.getMatrices()), 10.0F);
            this.int417 = 0;
         }
      }
   }

   public boolean concurrentHashMap() {
      return Menu.menu.int467() && (ZenithClient.on23().NbtEditor().isClosing() || ZenithClient.on23().NbtEditor().isElementSwapBlurActive());
   }

   public void on23(Framebuffer var1, Matrix4f var2, float var3) {
      if (!this.canRenderBlur()) {
         return;
      }

      Framebuffer framebuffer = minecraftClient3.getFramebuffer();
      this.Easing(framebuffer);
      if (this.shaderProgramKey2 == null) {
         this.shaderProgramKey2 = new ShaderWrapper(ResourceProvider.getShaderIdentifier("kawase_blur/data"), VertexFormats.POSITION_COLOR);
      }

      if (this.isShaderReady(this.shaderProgramKey2)) {
         float f = 1.0F / var1.textureWidth;
         float f1 = 1.0F / var1.textureHeight;
         org.zenith.render.LegacyRenderBridge.disableBlend();
         org.zenith.render.LegacyRenderBridge.disableCull();

         try {
            this.downsample(framebuffer, var1);
            Object object = var1;
            Object object1 = this.tempFbo;

            for (float f2 : val519) {
               float f3 = f2 * var3 / 10.0F;
               if (!this.kawasePass(((Framebuffer)object).getColorAttachmentView(), (Framebuffer)object1, var2, f, f1, f3)) {
                  return;
               }

               Object object2 = object;
               object = object1;
               object1 = object2;
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
      this.shaderProgramKey2.float251();
      this.shaderProgramKey2.HudArmorPanel("Resolution").set(var4, var5);
      this.shaderProgramKey2.HudArmorPanel("Offset").set(var6);
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         AvatarRenderer.UiAnimation(var3, bufferbuilder, 0.0F, 0.0F, var2.textureWidth, var2.textureHeight, -1);
         org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
      LegacyRenderBridge.clearTexture();
      return true;
   }

   public void on23(Matrix4f var1, float var2, float var3, float var4, float var5, CornerRadius var6, ArgbColor var7) {
      this.on23(this.fbo, var1, var2, var3, var4, var5, var6, var7);
   }

   public void ServiceException(List<BlurRenderer.BlurCommand> var1) {
      MsdfRenderer.flushBatch();
      this.on23(this.fbo, var1);
   }

   public void UiAnimation(Matrix4f var1, float var2, float var3, float var4, float var5, CornerRadius var6, ArgbColor var7) {
      if (!this.canRenderBlur()) {
         return;
      }

      this.boolean186 = true;
      Framebuffer framebuffer = minecraftClient3.getFramebuffer();
      this.UiAnimation(framebuffer);
      this.on23(this.simpleFramebuffer7, var1, var2, var3, var4, var5, var6, var7);
   }

   public void on23(Framebuffer var1, Matrix4f var2, float var3, float var4, float var5, float var6, CornerRadius var7, ArgbColor var8) {
      if (var1 != null) {
         if (this.shaderProgramKey3 == null) {
            this.shaderProgramKey3 = new ShaderWrapper(ResourceProvider.getShaderIdentifier("wtf/data"), VertexFormats.POSITION_COLOR);
         }

         if (this.isShaderReady(this.shaderProgramKey3)) {
            org.zenith.render.LegacyRenderBridge.enableBlend();
            org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
            org.zenith.render.LegacyRenderBridge.disableCull();

            try {
               LegacyRenderBridge.setTexture(0, var1.getColorAttachmentView());
               this.shaderProgramKey3.float251();
               this.shaderProgramKey3.HudArmorPanel("Size").set(var5, var6);
               this.shaderProgramKey3.HudArmorPanel("Radius").set(var7.var14311(), var7.string63(), var7.var14312(), var7.itemStack9());
               this.shaderProgramKey3.HudArmorPanel("Smoothness").set(MASK_SMOOTHNESS);
               BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
               bufferbuilder.vertex(var2, var3, var4, 0.0F).color(var8.call001());
               bufferbuilder.vertex(var2, var3, var4 + var6, 0.0F).color(var8.call001());
               bufferbuilder.vertex(var2, var3 + var5, var4 + var6, 0.0F).color(var8.call001());
               bufferbuilder.vertex(var2, var3 + var5, var4, 0.0F).color(var8.call001());
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

   public void on23(Framebuffer var1, List<BlurRenderer.BlurCommand> var2) {
      if (var1 != null && !var2.isEmpty()) {
         if (this.shaderProgramKey4 == null) {
            this.shaderProgramKey4 = new ShaderWrapper(ResourceProvider.getShaderIdentifier("batch_wtf/data"), VertexFormats.POSITION_TEXTURE_COLOR);
         }

         if (!this.isShaderReady(this.shaderProgramKey4)) {
            for (BlurRenderer.BlurCommand iliili1lliii1i1il1iilil1lil1li_ii1il11l111ii11iil : var2) {
               this.on23(
                  var1,
                  iliili1lliii1i1il1iilil1lil1li_ii1il11l111ii11iil.matrix4f(),
                  iliili1lliii1i1il1iilil1lil1li_ii1il11l111ii11iil.float65(),
                  iliili1lliii1i1il1iilil1lil1li_ii1il11l111ii11iil.float66(),
                  iliili1lliii1i1il1iilil1lil1li_ii1il11l111ii11iil.float67(),
                  iliili1lliii1i1il1iilil1lil1li_ii1il11l111ii11iil.float68(),
                  iliili1lliii1i1il1iilil1lil1li_ii1il11l111ii11iil.val012(),
                  iliili1lliii1i1il1iilil1lil1li_ii1il11l111ii11iil.var1192()
               );
            }
         } else {
            org.zenith.render.LegacyRenderBridge.enableBlend();
            org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
            org.zenith.render.LegacyRenderBridge.disableCull();
            LegacyRenderBridge.setTexture(0, var1.getColorAttachmentView());

            try {
               int i = 0;

               while (i < var2.size()) {
                  CornerRadius ii1il11l111ii11iil = var2.get(i).val012();
                  int j = i + 1;

                  while (j < var2.size() && this.on23(ii1il11l111ii11iil, var2.get(j).val012())) {
                     j++;
                  }

                  this.on23(var2, i, j, ii1il11l111ii11iil);
                  i = j;
               }
            } finally {
               LegacyRenderBridge.clearTexture();
               org.zenith.render.LegacyRenderBridge.enableCull();
               org.zenith.render.LegacyRenderBridge.disableBlend();
            }
         }
      }
   }

   public void on23(List<BlurRenderer.BlurCommand> var1, int var2, int var3, CornerRadius var4) {
      if (this.shaderProgramKey4 != null) {
         this.shaderProgramKey4.float251();
         this.shaderProgramKey4.HudArmorPanel("Radius").set(var4.var14311(), var4.string63(), var4.var14312(), var4.itemStack9());
         this.shaderProgramKey4.HudArmorPanel("Smoothness").set(MASK_SMOOTHNESS);
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

         for (int i = var2; i < var3; i++) {
            this.on23(bufferbuilder, var1.get(i));
         }

         org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
      }
   }

   public void on23(BufferBuilder var1, BlurRenderer.BlurCommand var2) {
      int i = var2.var1192().call001();
      float f = var2.float65() + var2.float67();
      float f1 = var2.float66() + var2.float68();
      var1.vertex(var2.matrix4f(), var2.float65(), var2.float66(), 0.0F).texture(var2.float67(), var2.float68()).color(i);
      var1.vertex(var2.matrix4f(), var2.float65(), f1, 0.0F).texture(var2.float67(), var2.float68()).color(i);
      var1.vertex(var2.matrix4f(), f, f1, 0.0F).texture(var2.float67(), var2.float68()).color(i);
      var1.vertex(var2.matrix4f(), f, var2.float66(), 0.0F).texture(var2.float67(), var2.float68()).color(i);
   }

   public boolean on23(CornerRadius var1, CornerRadius var2) {
      return var1.var14311() == var2.var14311()
         && var1.string63() == var2.string63()
         && var1.var14312() == var2.var14312()
         && var1.itemStack9() == var2.itemStack9();
   }

   public boolean isShaderReady(ShaderWrapper var1) {
      return var1 != null;
   }

   private boolean canRenderBlur() {
      return minecraftClient3.isFinishedLoading() && minecraftClient3.getShaderLoader() != null;
   }

   public void on23(Framebuffer var1) {
      int i = this.EventGetBasicProjectionMatrixHook(var1.textureWidth);
      int j = this.EventGetBasicProjectionMatrixHook(var1.textureHeight);
      if (this.fbo == null) {
         this.fbo = this.BotFeatureRegistry(i, j);
      }

      if (this.fbo.textureWidth != i || this.fbo.textureHeight != j) {
         this.on23(this.fbo, i, j);
      }
   }

   public void UiAnimation(Framebuffer var1) {
      int i = this.EventGetBasicProjectionMatrixHook(var1.textureWidth);
      int j = this.EventGetBasicProjectionMatrixHook(var1.textureHeight);
      if (this.simpleFramebuffer7 == null) {
         this.simpleFramebuffer7 = this.BotFeatureRegistry(i, j);
      }

      if (this.simpleFramebuffer7.textureWidth != i || this.simpleFramebuffer7.textureHeight != j) {
         this.on23(this.simpleFramebuffer7, i, j);
      }
   }

   public void Easing(Framebuffer var1) {
      int i = this.EventGetBasicProjectionMatrixHook(var1.textureWidth);
      int j = this.EventGetBasicProjectionMatrixHook(var1.textureHeight);
      if (this.tempFbo == null) {
         this.tempFbo = this.BotFeatureRegistry(i, j);
      }

      if (this.tempFbo.textureWidth != i || this.tempFbo.textureHeight != j) {
         this.on23(this.tempFbo, i, j);
      }
   }

   public SimpleFramebuffer BotFeatureRegistry(int var1, int var2) {
      return new SimpleFramebuffer("Zenith blur", var1, var2, false);
   }

   public void on23(SimpleFramebuffer var1, int var2, int var3) {
      var1.resize(var2, var3);
   }

   public int EventGetBasicProjectionMatrixHook(int var1) {
      return Math.max(1, var1 / 2);
   }


   public record BlurCommand(Matrix4f matrix4f, float float65, float float66, float float67, float float68, CornerRadius val012, ArgbColor var1192) {
      public Matrix4f call439() {
         return this.matrix4f;
      }

      public float x() {
         return this.float65;
      }

      public float y() {
         return this.float66;
      }

      public float width() {
         return this.float67;
      }

      public float height() {
         return this.float68;
      }

      public CornerRadius call476() {
         return this.val012;
      }

      public ArgbColor list56() {
         return this.var1192;
      }
   }
}
