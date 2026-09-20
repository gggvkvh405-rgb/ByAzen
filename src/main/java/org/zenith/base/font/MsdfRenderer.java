package org.zenith.base.font;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.zenith.render.RenderCommandQueue;
import org.zenith.core.ShaderWrapper;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.GradientRadius;

public final class MsdfRenderer {
   public static final ShaderWrapper MSDF_FONT_SHADER = new ShaderWrapper(
      ResourceProvider.getShaderIdentifier("msdf_font/data"), VertexFormats.POSITION_TEXTURE_COLOR
   );
   public static final int VANILLA_TEXT_LIGHT = 15728880;
   public static final boolean USE_VANILLA_FALLBACK_FOR_TEXT = true;
   public static final int BATCH_INITIAL_BUFFER_SIZE = 65536;
   public static BufferAllocator batchAllocator;
   public static BufferBuilder batchBuilder;
   public static int batchTextureId;
   public static net.minecraft.client.texture.AbstractTexture batchTexture;
   public static float batchRange;
   public static float batchThickness;
   public static float batchSmoothness;
   public static boolean batchFadeout;
   public static float batchFadeoutStart;
   public static float batchFadeoutEnd;
   public static float batchMaxWidth;
   public static float batchTextPosX;
   public static float batchShaderColorR;
   public static float batchShaderColorG;
   public static float batchShaderColorB;
   public static float batchShaderColorA;
   public static float lastUniformRange;
   public static float lastUniformThickness;
   public static float lastUniformSmoothness;
   public static int lastUniformFadeout;
   public static float lastUniformFadeoutStart;
   public static float lastUniformFadeoutEnd;
   public static float lastUniformMaxWidth;
   public static float lastUniformTextPosX;

   public static VertexConsumer beginBatch(MsdfFont var0, float var1, float var2, boolean var3, float var4, float var5, float var6, float var7) {
      if (!var3) {
         var4 = 0.0F;
         var5 = 0.0F;
         var6 = 0.0F;
         var7 = 0.0F;
      }

      int i = var0.getTextureId();
      float f = var0.getAtlas().range();
      float[] afloat = org.zenith.render.LegacyRenderBridge.getShaderColor();
      float f1 = afloat[0];
      float f2 = afloat[1];
      float f3 = afloat[2];
      float f4 = afloat[3];
      if (batchBuilder != null
         && (
            batchTextureId != i
               || batchRange != f
               || batchThickness != var1
               || batchSmoothness != var2
               || batchFadeout != var3
               || batchFadeoutStart != var4
               || batchFadeoutEnd != var5
               || batchMaxWidth != var6
               || batchTextPosX != var7
               || batchShaderColorR != f1
               || batchShaderColorG != f2
               || batchShaderColorB != f3
               || batchShaderColorA != f4
         )) {
         flushBatch();
      }

      if (batchBuilder == null) {
         if (batchAllocator == null) {
            batchAllocator = new BufferAllocator(65536);
         }

         batchBuilder = new BufferBuilder(batchAllocator, DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         batchTextureId = i;
         batchTexture = var0.texture;
         batchRange = f;
         batchThickness = var1;
         batchSmoothness = var2;
         batchFadeout = var3;
         batchFadeoutStart = var4;
         batchFadeoutEnd = var5;
         batchMaxWidth = var6;
         batchTextPosX = var7;
         batchShaderColorR = f1;
         batchShaderColorG = f2;
         batchShaderColorB = f3;
         batchShaderColorA = f4;
      }

      return batchBuilder;
   }

   public static void flushBatch() {
      RenderCommandQueue.var1439();
      if (batchBuilder != null) {
         BufferBuilder bufferbuilder = batchBuilder;
         batchBuilder = null;
         BuiltBuffer builtbuffer = bufferbuilder.endNullable();
         if (builtbuffer == null) {
            batchAllocator.clear();
         } else {
            float[] afloat = org.zenith.render.LegacyRenderBridge.getShaderColor();
            float f = afloat[0];
            float f1 = afloat[1];
            float f2 = afloat[2];
            float f3 = afloat[3];
            boolean flag = f != batchShaderColorR || f1 != batchShaderColorG || f2 != batchShaderColorB || f3 != batchShaderColorA;
            org.zenith.render.LegacyRenderBridge.enableBlend();
            org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
            org.zenith.render.LegacyRenderBridge.disableCull();
            if (flag) {
               org.zenith.render.LegacyRenderBridge.setShaderColor(batchShaderColorR, batchShaderColorG, batchShaderColorB, batchShaderColorA);
            }

            org.zenith.render.LegacyRenderBridge.setTexture(0, batchTexture);
            MSDF_FONT_SHADER.float251();
            MSDF_FONT_SHADER.HudArmorPanel("Range").set(batchRange);
            MSDF_FONT_SHADER.HudArmorPanel("Thickness").set(batchThickness);
            MSDF_FONT_SHADER.HudArmorPanel("Smoothness").set(batchSmoothness);
            int i = batchFadeout ? 1 : 0;
            MSDF_FONT_SHADER.HudArmorPanel("EnableFadeout").set(i);

            if (batchFadeout) {
               MSDF_FONT_SHADER.HudArmorPanel("FadeoutStart").set(batchFadeoutStart);
               MSDF_FONT_SHADER.HudArmorPanel("FadeoutEnd").set(batchFadeoutEnd);
               MSDF_FONT_SHADER.HudArmorPanel("MaxWidth").set(batchMaxWidth);
               MSDF_FONT_SHADER.HudArmorPanel("TextPosX").set(batchTextPosX);
            }

            try {
               org.zenith.render.LegacyRenderBridge.draw(builtbuffer);
            } finally {
               org.zenith.render.LegacyRenderBridge.clearTexture();
               if (flag) {
                  org.zenith.render.LegacyRenderBridge.setShaderColor(f, f1, f2, f3);
               }

               org.zenith.render.LegacyRenderBridge.enableCull();
               org.zenith.render.LegacyRenderBridge.disableBlend();
               batchAllocator.clear();
            }
         }
      }
   }

   public static void renderText(MsdfFont var0, String var1, float var2, int var3, Matrix4f var4, float var5, float var6, float var7) {
      renderText(var0, var1, var2, var3, var4, var5, var6, var7, false, 0.0F, 1.0F, 0.0F);
   }

   public static void renderText(
      MsdfFont var0, String var1, float var2, int var3, Matrix4f var4, float var5, float var6, float var7, boolean var8, float var9, float var10, float var11
   ) {
      float f = 0.0F;
      float f1 = 0.45F;
      float f2 = 0.0F;
      float f3 = var5;
      float f4 = var11;
      if (var8 && var11 > 0.0F) {
         Vector4f vector4f = new Vector4f(var5, 0.0F, 0.0F, 1.0F);
         Vector4f vector4f1 = new Vector4f(var5 + var11, 0.0F, 0.0F, 1.0F);
         var4.transform(vector4f);
         var4.transform(vector4f1);
         f3 = vector4f.x;
         f4 = vector4f1.x - vector4f.x;
      }

      VertexConsumer vertexconsumer = beginBatch(var0, f, f1, var8, var9, var10, f4, f3);
      float f5 = var6 + textLineOffset(var0, var2);
      var0.applyGlyphs(var4, vertexconsumer, var1, var2, f * 0.5F * var2, f2, var5, f5, var7, var3);
   }

   public static void renderText(
      MsdfFont var0, String var1, float var2, int var3, Matrix4f var4, float var5, float var6, float var7, boolean var8, float var9, float var10
   ) {
      float f = var0.getWidth(var1, var2) * 2.0F;
      renderText(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, f);
   }

   public static void renderText(MsdfFont var0, Text var1, float var2, Matrix4f var3, float var4, float var5, float var6) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void renderText(
      MsdfFont var0, Text var1, float var2, Matrix4f var3, float var4, float var5, float var6, boolean var7, float var8, float var9, float var10
   ) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void renderText(
      MsdfFont var0,
      Text var1,
      float var2,
      Matrix4f var3,
      float var4,
      float var5,
      float var6,
      boolean var7,
      float var8,
      float var9,
      float var10,
      int var11
   ) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6, var11);
   }

   public static void renderTextWithVanillaFallback(MsdfFont var0, Text var1, float var2, Matrix4f var3, float var4, float var5, float var6) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6, ArgbColor.var11934.call001());
   }

   public static void renderTextWithVanillaFallback(MsdfFont var0, Text var1, float var2, Matrix4f var3, float var4, float var5, float var6, int var7) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      renderTextWithVanillaFallback(var0, var1, var2, minecraftclient.textRenderer, minecraftclient.getBufferBuilders().getEntityVertexConsumers(), var3, var4, var5, var6, var7);
   }

   public static void renderTextWithVanillaFallback(
      MsdfFont var0, Text var1, float var2, TextRenderer var3, VertexConsumerProvider var4, Matrix4f var5, float var6, float var7, float var8, int var9
   ) {
      List<FormattedTextProcessor_TextSegment> list = FormattedTextProcessor.processText(var1, var9);
      float f = var6;

      for (FormattedTextProcessor_TextSegment formattedtextprocessor_textsegment : list) {
         f = renderTextSegmentWithFallback(
            var0,
            var3,
            var4,
            normalizeTextForMsdf(formattedtextprocessor_textsegment.text()),
            var2,
            formattedtextprocessor_textsegment.color(),
            var5,
            f,
            var7,
            var8
         );
      }
   }

   public static void renderText(
      MsdfFont var0, Text var1, float var2, Matrix4f var3, float var4, float var5, float var6, boolean var7, float var8, float var9
   ) {
      renderTextWithVanillaFallback(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void renderText(MsdfFont var0, String var1, float var2, GradientRadius var3, Matrix4f var4, float var5, float var6, float var7) {
      renderText(var0, var1, var2, var3, var4, var5, var6, var7, false, 0.0F, 1.0F, 0.0F);
   }

   public static void renderText(
      MsdfFont var0,
      String var1,
      float var2,
      GradientRadius var3,
      Matrix4f var4,
      float var5,
      float var6,
      float var7,
      boolean var8,
      float var9,
      float var10,
      float var11
   ) {
      var1 = var1.replace("і", "i").replace("І", "I");
      float f = 0.05F;
      float f1 = 0.5F;
      float f2 = 0.0F;
      float f3 = var5;
      float f4 = var11;
      if (var8 && var11 > 0.0F) {
         Vector4f vector4f = new Vector4f(var5, 0.0F, 0.0F, 1.0F);
         Vector4f vector4f1 = new Vector4f(var5 + var11, 0.0F, 0.0F, 1.0F);
         var4.transform(vector4f);
         var4.transform(vector4f1);
         f3 = vector4f.x;
         f4 = vector4f1.x - vector4f.x;
      }

      VertexConsumer vertexconsumer = beginBatch(var0, f, f1, var8, var9, var10, f4, f3);
      float f5 = var6 + textLineOffset(var0, var2);
      var0.applyGlyphs(var4, vertexconsumer, var1, var2, f * 0.5F * var2, f2, var5, f5, var7, var3);
   }

   public static float textLineOffset(MsdfFont var0, float var1) {
      float f = var0.getMetrics().ascender();
      return (f > 0.0F ? f : 0.7F) * var1;
   }

   public static float renderTextSegmentWithFallback(
      MsdfFont var0, TextRenderer var1, VertexConsumerProvider var2, String var3, float var4, int var5, Matrix4f var6, float var7, float var8, float var9
   ) {
      StringBuilder stringbuilder = new StringBuilder();
      boolean flag = false;
      boolean flag1 = false;
      int i = 0;

      while (i < var3.length()) {
         int j = var3.codePointAt(i);
         int k = i + Character.charCount(j);
         if (j == 167) {
            i = skipFormattingCode(var3, k);
         } else {
            boolean flag2 = canRenderWithMsdf(var0, j);
            if (flag && flag1 != flag2) {
               var7 = renderTextRun(var0, var1, var2, stringbuilder.toString(), flag1, var4, var5, var6, var7, var8, var9);
               stringbuilder.setLength(0);
            }

            stringbuilder.append(var3, i, k);
            flag = true;
            flag1 = flag2;
            i = k;
         }
      }

      if (flag) {
         var7 = renderTextRun(var0, var1, var2, stringbuilder.toString(), flag1, var4, var5, var6, var7, var8, var9);
      }

      return var7;
   }

   public static float renderTextRun(
      MsdfFont var0, TextRenderer var1, VertexConsumerProvider var2, String var3, boolean var4, float var5, int var6, Matrix4f var7, float var8, float var9, float var10
   ) {
      if (var3.isEmpty()) {
         return var8;
      } else if (var4) {
         renderText(var0, var3, var5, var6, var7, var8, var9, var10);
         return var8 + var0.getWidth(var3, var5);
      } else {
         renderVanillaText(var0, var1, var2, var3, var5, var6, var7, var8, var9, var10);
         return var8 + var1.getWidth(var3) * vanillaTextScale(var1, var5);
      }
   }

   public static void renderVanillaText(
      MsdfFont var0, TextRenderer var1, VertexConsumerProvider var2, String var3, float var4, int var5, Matrix4f var6, float var7, float var8, float var9
   ) {
      flushBatch();
      float f = vanillaTextScale(var1, var4);
      float f1 = var8 + (-textLineOffset(var0, var4) + 9.0F) * 0.5F;
      Matrix4f matrix4f = new Matrix4f(var6).translate(var7, f1, var9).scale(f, f, 1.0F);
      var1.draw(var3, 0.0F, 0.0F, var5, false, matrix4f, var2, TextLayerType.NORMAL, 0, 15728880);
   }

   public static float vanillaTextScale(TextRenderer var0, float var1) {
      return var1 / Math.max(1.0F, 9.0F);
   }

   public static boolean canRenderWithMsdf(MsdfFont var0, int var1) {
      return var1 <= 65535 && var0.hasGlyph(var1);
   }

   public static int skipFormattingCode(String var0, int var1) {
      return var1 >= var0.length() ? var1 : var1 + Character.charCount(var0.codePointAt(var1));
   }

   public static String normalizeTextForMsdf(String var0) {
      return MsdfFont.normalizeSmallCaps(var0);
   }

   public static void renderText(
      MsdfFont var0, String var1, float var2, GradientRadius var3, Matrix4f var4, float var5, float var6, float var7, boolean var8, float var9, float var10
   ) {
      float f = var0.getWidth(var1, var2) * 2.0F;
      renderText(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, f);
   }

   public MsdfRenderer() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
