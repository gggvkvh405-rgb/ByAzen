package org.zenith.core;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.render.ShapeRenderer;
import org.zenith.util.ColorUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.GradientRadius;

public final class AvatarRenderer implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static HashMap<AvatarCrop, AvatarTexture> hashMap2 = new HashMap<>();
   public static HashMap<Integer, AvatarTexture> hashMap3 = new HashMap<>();
   static final Stack<AvatarUV> val539 = new Stack<>();
   public static final List<AvatarRect> list110 = new ArrayList<>();
   public static final int int447 = 5;
   public static final Set<AvatarCrop> set21 = ConcurrentHashMap.newKeySet();
   public static final ExecutorService executorService5 = Executors.newSingleThreadExecutor();

   public static void avatarLayer() {
      MsdfRenderer.flushBatch();
      org.zenith.render.LegacyRenderBridge.disableScissor();
   }

   public static void on23(BufferBuilder var0, Matrix4f var1, float var2, float var3, float var4, float var5, Color var6, Color var7, Color var8, Color var9) {
      var0.vertex(var1, var2, var5, 0.0F).color(var6.getRGB());
      var0.vertex(var1, var4, var5, 0.0F).color(var7.getRGB());
      var0.vertex(var1, var4, var3, 0.0F).color(var8.getRGB());
      var0.vertex(var1, var2, var3, 0.0F).color(var9.getRGB());
   }

   public static boolean on23(double var0, double var2, double var4, double var6, double var8, double var10) {
      return var0 >= var4 && var0 - var8 <= var4 && var2 >= var6 && var2 - var10 <= var6;
   }

   public static void on23(MatrixStack var0, float var1, float var2, float var3, float var4, int var5, Color var6) {
   }

   public static void UiAnimation(DrawContext var0) {
      MatrixStack matrixstack = org.zenith.render.GuiMatrixAdapter.toMatrixStack(var0.getMatrices());
      Matrix4f matrix4f = matrixstack.peek().getPositionMatrix();
      if (!list110.isEmpty()) {
         MsdfRenderer.flushBatch();
         org.zenith.render.LegacyRenderBridge.enableBlend();
         org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
         org.zenith.render.LegacyRenderBridge.usePositionColor();
         BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         list110.forEach(var2x -> UiAnimation(matrix4f, bufferbuilder, var2x.float108(), var2x.float109(), var2x.float110(), var2x.float111(), var2x.int188()));
         org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
         org.zenith.render.LegacyRenderBridge.disableBlend();
         list110.clear();
      }
   }

   public static void on23(float var0, float var1, float var2, float var3, int var4) {
      list110.add(new AvatarRect(var0, var1, var2, var3, ColorUtils.ColorAnimator(var4, org.zenith.render.LegacyRenderBridge.getShaderColor()[3])));
   }

   public static void on23(
      MatrixStack var0,
      double var1,
      double var3,
      double var5,
      double var7,
      float var9,
      float var10,
      double var11,
      double var13,
      double var15,
      double var17,
      int var19,
      int var20,
      int var21,
      int var22
   ) {
      MsdfRenderer.flushBatch();
      org.zenith.render.LegacyRenderBridge.usePositionTexColor();
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      on23(bufferbuilder, var0, var1, var3, var5, var7, var9, var10, var11, var13, var15, var17, var19, var20, var21, var22);
      org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
   }

   public static void on23(Matrix4f var0, BufferBuilder var1, float var2, float var3, float var4, float var5) {
      var1.vertex(var0, var2, var3, 0.0F);
      var1.vertex(var0, var2, var3 + var5, 0.0F);
      var1.vertex(var0, var2 + var4, var3 + var5, 0.0F);
      var1.vertex(var0, var2 + var4, var3, 0.0F);
   }

   public static void UiAnimation(Matrix4f var0, BufferBuilder var1, float var2, float var3, float var4, float var5, int var6) {
      var1.vertex(var0, var2, var3, 0.0F).color(var6);
      var1.vertex(var0, var2, var3 + var5, 0.0F).color(var6);
      var1.vertex(var0, var2 + var4, var3 + var5, 0.0F).color(var6);
      var1.vertex(var0, var2 + var4, var3, 0.0F).color(var6);
   }

   public static void on23(Matrix4f var0, float var1, float var2, float var3, float var4, int var5) {
      MsdfRenderer.flushBatch();
      org.zenith.render.LegacyRenderBridge.usePositionTexColor();
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      bufferbuilder.vertex(var0, var1, var2 + var4, 0.0F).texture(0.0F, 0.0F).color(var5);
      bufferbuilder.vertex(var0, var1 + var3, var2 + var4, 0.0F).texture(0.0F, 1.0F).color(var5);
      bufferbuilder.vertex(var0, var1 + var3, var2, 0.0F).texture(1.0F, 1.0F).color(var5);
      bufferbuilder.vertex(var0, var1, var2, 0.0F).texture(1.0F, 0.0F).color(var5);
      org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
   }

   public static void Easing(Matrix4f var0, BufferBuilder var1, float var2, float var3, float var4, float var5, int var6) {
      var1.vertex(var0, var2, var3 + var5, 0.0F).texture(0.0F, 0.0F).color(var6);
      var1.vertex(var0, var2 + var4, var3 + var5, 0.0F).texture(0.0F, 1.0F).color(var6);
      var1.vertex(var0, var2 + var4, var3, 0.0F).texture(1.0F, 1.0F).color(var6);
      var1.vertex(var0, var2, var3, 0.0F).texture(1.0F, 0.0F).color(var6);
   }

   public static AvatarCrop on23(float var0, float var1, int var2, CornerRadius var3) {
      int i = Math.max(1, Math.round(var0));
      int j = Math.max(1, Math.round(var1));
      int k = Math.max(1, i - var2 * 2);
      int l = Math.max(1, j - var2 * 2);
      CornerRadius ii1il11l111ii11iil = on23(k, l, var3);
      return new AvatarCrop(
         i,
         j,
         var2,
         Math.round(ii1il11l111ii11iil.var14311()),
         Math.round(ii1il11l111ii11iil.var14312()),
         Math.round(ii1il11l111ii11iil.itemStack9()),
         Math.round(ii1il11l111ii11iil.string63())
      );
   }

   public static AvatarCrop on23(AvatarCrop var0) {
      AvatarCrop li1liiliill11llllll11_ii1il11l111ii11iilx = null;
      int i = Integer.MAX_VALUE;

      for (AvatarCrop zenithCandidate : hashMap2.keySet()) {
         int j = on23(zenithCandidate, var0);
         if (j < i) {
            i = j;
            li1liiliill11llllll11_ii1il11l111ii11iilx = zenithCandidate;
         }
      }

      return li1liiliill11llllll11_ii1il11l111ii11iilx;
   }

   public static void on23(MatrixStack var0, float var1, float var2, float var3, float var4, int var5, CornerRadius var6, GradientRadius var7) {
      on23(var0.peek().getPositionMatrix(), var1, var2, var3, var4, var5, var6, var7);
   }

   public static void on23(Matrix4f var0, float var1, float var2, float var3, float var4, int var5, CornerRadius var6, GradientRadius var7) {
      var3 += var5 * 2;
      var4 += var5 * 2;
      var1 -= var5;
      var2 -= var5;
      AvatarCrop li1liiliill11llllll11_ii1il11l111ii11iilx = on23(var3, var4, var5, var6);
      AvatarTexture li1liiliill11llllll11_l1i1illlili = hashMap2.get(li1liiliill11llllll11_ii1il11l111ii11iilx);
      AvatarCrop li1liiliill11llllll11_ii1il11l111ii11iily = li1liiliill11llllll11_ii1il11l111ii11iilx;
      if (li1liiliill11llllll11_l1i1illlili == null) {
         li1liiliill11llllll11_ii1il11l111ii11iily = on23(li1liiliill11llllll11_ii1il11l111ii11iilx);
         if (li1liiliill11llllll11_ii1il11l111ii11iily != null) {
            li1liiliill11llllll11_l1i1illlili = hashMap2.get(li1liiliill11llllll11_ii1il11l111ii11iily);
         }
      }

      if (li1liiliill11llllll11_l1i1illlili == null
         || li1liiliill11llllll11_ii1il11l111ii11iily == null
         || on23(li1liiliill11llllll11_ii1il11l111ii11iilx, li1liiliill11llllll11_ii1il11l111ii11iily) >= 5
         || li1liiliill11llllll11_ii1il11l111ii11iilx.int185() != li1liiliill11llllll11_ii1il11l111ii11iily.int185()) {
         UiAnimation(li1liiliill11llllll11_ii1il11l111ii11iilx);
      }

      if (li1liiliill11llllll11_l1i1illlili != null) {
         li1liiliill11llllll11_l1i1illlili.reset();
         ShapeRenderer.on23(var0, li1liiliill11llllll11_l1i1illlili.var02.var14340(), var1, var2, var3, var4, var7);
      }
   }

   public static void UiAnimation(AvatarCrop var0) {
      if (var0 != null && !hashMap2.containsKey(var0) && set21.add(var0)) {
         executorService5.execute(() -> {
            try {
               BufferedImage bufferedimage = Easing(var0);
               BufferedImage bufferedimage1 = var0.long88() > 0 ? new ConvolveKernel(var0.long88()).on23(bufferedimage, (BufferedImage)null) : bufferedimage;
               minecraftClient3.execute(() -> {
                  try {
                     if (!hashMap2.containsKey(var0)) {
                        hashMap2.put(var0, new AvatarTexture(bufferedimage1));
                     }
                  } finally {
                     set21.remove(var0);
                  }
               });
            } catch (Exception exception) {
               set21.remove(var0);
               exception.printStackTrace();
            }
         });
      }
   }

   public static BufferedImage Easing(AvatarCrop var0) {
      BufferedImage bufferedimage = new BufferedImage(var0.width(), var0.height(), 2);
      int[] aint = new int[var0.width() * var0.height()];
      int i = Math.max(1, var0.width() - var0.long88() * 2);
      int j = Math.max(1, var0.height() - var0.long88() * 2);
      float f = i * 0.5F;
      float f1 = j * 0.5F;
      float f2 = Math.max(0.0F, f - 1.0F);
      float f3 = Math.max(0.0F, f1 - 1.0F);

      for (int k = 0; k < var0.height(); k++) {
         float f4 = k + 0.5F - var0.long88();

         for (int l = 0; l < var0.width(); l++) {
            float f5 = l + 0.5F - var0.long88();
            float f6 = f - f5;
            float f7 = f1 - f4;
            float f8 = on23(f6, f7, f2, f3, var0.list84(), var0.float257(), var0.int125(), var0.float256());
            float f9 = 1.0F - PotionItemBuilder(0.0F, 1.0F, f8);
            if (!(f9 <= 0.0F)) {
               int i1 = MathHelper.clamp(Math.round(f9 * 255.0F), 0, 255);
               aint[k * var0.width() + l] = i1 << 24 | 16777215;
            }
         }
      }

      bufferedimage.setRGB(0, 0, var0.width(), var0.height(), aint, 0, var0.width());
      return bufferedimage;
   }

   public static CornerRadius on23(int var0, int var1, CornerRadius var2) {
      float f = Math.max(0.0F, var0 * 0.5F - 1.0F);
      float f1 = Math.max(0.0F, var1 * 0.5F - 1.0F);
      return new CornerRadius(
         NbtEditor(var2.var14311(), f, f1), NbtEditor(var2.var14312(), f, f1), NbtEditor(var2.itemStack9(), f, f1), NbtEditor(var2.string63(), f, f1)
      );
   }

   public static float NbtEditor(float var0, float var1, float var2) {
      return Math.max(0.0F, Math.min(var0, Math.min(var1, var2)));
   }

   public static int on23(AvatarCrop var0, AvatarCrop var1) {
      return var0 != null && var1 != null
         ? Math.abs(var0.width() - var1.width())
            + Math.abs(var0.height() - var1.height())
            + Math.abs(var0.long88() - var1.long88())
            + Math.abs(var0.list84() - var1.list84())
            + Math.abs(var0.int125() - var1.int125())
            + Math.abs(var0.float256() - var1.float256())
            + Math.abs(var0.float257() - var1.float257())
         : Integer.MAX_VALUE;
   }

   public static float on23(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      float f = var0 > 0.0F ? var4 : var6;
      float f1 = var0 > 0.0F ? var5 : var7;
      float f2 = var1 > 0.0F ? f : f1;
      float f3 = Math.abs(var0) - var2 + f2;
      float f4 = Math.abs(var1) - var3 + f2;
      return Math.min(Math.max(f3, f4), 0.0F) + (float)Math.hypot(Math.max(f3, 0.0F), Math.max(f4, 0.0F)) - f2;
   }

   public static float PotionItemBuilder(float var0, float var1, float var2) {
      if (var0 == var1) {
         return var2 < var0 ? 0.0F : 1.0F;
      }

      float f = MathHelper.clamp((var2 - var0) / (var1 - var0), 0.0F, 1.0F);
      return f * f * (3.0F - 2.0F * f);
   }

   public static void on23(TextureIdFactory var0, BufferedImage var1) {
      try {
         ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
         ImageIO.write(var1, "png", bytearrayoutputstream);
         byte[] abyte = bytearrayoutputstream.toByteArray();
         UiAnimation(var0, abyte);
      } catch (Exception var4) {
      }
   }

   public static void on23(TextureIdFactory var0, byte[] var1) {
      try {
         ByteBuffer bytebuffer = BufferUtils.createByteBuffer(var1.length).put(var1);
         bytebuffer.flip();
         NativeImageBackedTexture nativeimagebackedtexture = new NativeImageBackedTexture(() -> "Zenith avatar", NativeImage.read(bytebuffer));
         minecraftClient3.getTextureManager().registerTexture(var0.var14340(), nativeimagebackedtexture);
      } catch (Exception var4) {
      }
   }

   public static void UiAnimation(TextureIdFactory var0, byte[] var1) {
      try {
         ByteBuffer bytebuffer = BufferUtils.createByteBuffer(var1.length).put(var1);
         bytebuffer.flip();
         NativeImageBackedTexture nativeimagebackedtexture = new NativeImageBackedTexture(() -> "Zenith avatar", NativeImage.read(bytebuffer));
         minecraftClient3.execute(() -> minecraftClient3.getTextureManager().registerTexture(var0.var14340(), nativeimagebackedtexture));
      } catch (Exception var4) {
      }
   }

   public static void on23(
      MatrixStack var0, double var1, double var3, double var5, double var7, float var9, float var10, double var11, double var13, double var15, double var17
   ) {
      MsdfRenderer.flushBatch();
      double d0 = var1 + var5;
      double d1 = var3 + var7;
      double d2 = 0.0;
      Matrix4f matrix4f = var0.peek().getPositionMatrix();
      org.zenith.render.LegacyRenderBridge.usePositionTexColor();
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
      bufferbuilder.vertex(matrix4f, (float)var1, (float)d1, (float)d2).texture(var9 / (float)var15, (var10 + (float)var13) / (float)var17);
      bufferbuilder.vertex(matrix4f, (float)d0, (float)d1, (float)d2)
         .texture((var9 + (float)var11) / (float)var15, (var10 + (float)var13) / (float)var17);
      bufferbuilder.vertex(matrix4f, (float)d0, (float)var3, (float)d2).texture((var9 + (float)var11) / (float)var15, var10 / (float)var17);
      bufferbuilder.vertex(matrix4f, (float)var1, (float)var3, (float)d2).texture(var9 / (float)var15, (var10 + 0.0F) / (float)var17);
      org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
   }

   public static void on23(
      MatrixStack var0,
      double var1,
      double var3,
      double var5,
      double var7,
      float var9,
      float var10,
      double var11,
      double var13,
      double var15,
      double var17,
      Color var19,
      Color var20,
      Color var21,
      Color var22
   ) {
      MsdfRenderer.flushBatch();
      org.zenith.render.LegacyRenderBridge.usePositionTexColor();
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      on23(bufferbuilder, var0, var1, var3, var5, var7, var9, var10, var11, var13, var15, var17, var19, var20, var21, var22);
      org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
   }

   public static void on23(
      BufferBuilder var0,
      MatrixStack var1,
      double var2,
      double var4,
      double var6,
      double var8,
      float var10,
      float var11,
      double var12,
      double var14,
      double var16,
      double var18,
      int var20,
      int var21,
      int var22,
      int var23
   ) {
      double d0 = var2 + var6;
      double d1 = var4 + var8;
      double d2 = 0.0;
      Matrix4f matrix4f = var1.peek().getPositionMatrix();
      var0.vertex(matrix4f, (float)var2, (float)d1, (float)d2)
         .texture(var10 / (float)var16, (var11 + (float)var14) / (float)var18)
         .color(var20);
      var0.vertex(matrix4f, (float)d0, (float)d1, (float)d2)
         .texture((var10 + (float)var12) / (float)var16, (var11 + (float)var14) / (float)var18)
         .color(var21);
      var0.vertex(matrix4f, (float)d0, (float)var4, (float)d2)
         .texture((var10 + (float)var12) / (float)var16, var11 / (float)var18)
         .color(var22);
      var0.vertex(matrix4f, (float)var2, (float)var4, (float)d2).texture(var10 / (float)var16, (var11 + 0.0F) / (float)var18).color(var23);
   }

   public static void on23(
      BufferBuilder var0,
      MatrixStack var1,
      double var2,
      double var4,
      double var6,
      double var8,
      float var10,
      float var11,
      double var12,
      double var14,
      double var16,
      double var18,
      Color var20,
      Color var21,
      Color var22,
      Color var23
   ) {
      double d0 = var2 + var6;
      double d1 = var4 + var8;
      double d2 = 0.0;
      Matrix4f matrix4f = var1.peek().getPositionMatrix();
      var0.vertex(matrix4f, (float)var2, (float)d1, (float)d2)
         .texture(var10 / (float)var16, (var11 + (float)var14) / (float)var18)
         .color(var20.getRGB());
      var0.vertex(matrix4f, (float)d0, (float)d1, (float)d2)
         .texture((var10 + (float)var12) / (float)var16, (var11 + (float)var14) / (float)var18)
         .color(var21.getRGB());
      var0.vertex(matrix4f, (float)d0, (float)var4, (float)d2)
         .texture((var10 + (float)var12) / (float)var16, var11 / (float)var18)
         .color(var22.getRGB());
      var0.vertex(matrix4f, (float)var2, (float)var4, (float)d2)
         .texture(var10 / (float)var16, (var11 + 0.0F) / (float)var18)
         .color(var23.getRGB());
   }

   public static void path() {
      org.zenith.render.LegacyRenderBridge.enableBlend();
      org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
      org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static void on23(MatrixStack var0, float var1, float var2, float var3, float var4, float var5, boolean var6, boolean var7, int var8) {
   }

   public static void on23(MatrixStack var0, float var1, float var2, float var3, Color var4) {
   }

   public static void UiAnimation(MatrixStack var0, float var1, float var2, float var3, float var4, float var5, boolean var6, boolean var7, int var8) {
      MsdfRenderer.flushBatch();
      if (var7) {
         on23(var0, var1 - var3 * var4, var2, var1 + var3 * var4 - (var1 - var3 * var4), var3, 10, on23(new Color(var8), 140));
      }

      var0.push();
      path();
      Matrix4f matrix4f = var0.peek().getPositionMatrix();
      org.zenith.render.LegacyRenderBridge.usePositionColor();
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
      bufferbuilder.vertex(matrix4f, var1, var2, 0.0F).color(var8);
      bufferbuilder.vertex(matrix4f, var1 - var3 * var4, var2 + var3, 0.0F).color(var8);
      bufferbuilder.vertex(matrix4f, var1, var2 + var3 - var5, 0.0F).color(var8);
      bufferbuilder.vertex(matrix4f, var1, var2, 0.0F).color(var8);
      var8 = UiAnimation(new Color(var8), 0.8F).getRGB();
      bufferbuilder.vertex(matrix4f, var1, var2, 0.0F).color(var8);
      bufferbuilder.vertex(matrix4f, var1, var2 + var3 - var5, 0.0F).color(var8);
      bufferbuilder.vertex(matrix4f, var1 + var3 * var4, var2 + var3, 0.0F).color(var8);
      bufferbuilder.vertex(matrix4f, var1, var2, 0.0F).color(var8);
      if (var6) {
         var8 = UiAnimation(new Color(var8), 0.6F).getRGB();
         bufferbuilder.vertex(matrix4f, var1 - var3 * var4, var2 + var3, 0.0F).color(var8);
         bufferbuilder.vertex(matrix4f, var1 + var3 * var4, var2 + var3, 0.0F).color(var8);
         bufferbuilder.vertex(matrix4f, var1, var2 + var3 - var5, 0.0F).color(var8);
         bufferbuilder.vertex(matrix4f, var1 - var3 * var4, var2 + var3, 0.0F).color(var8);
      }

      org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
      zClass024Var159();
      var0.pop();
   }

   public static void zClass024Var159() {
      org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
      org.zenith.render.LegacyRenderBridge.disableBlend();
      org.zenith.render.LegacyRenderBridge.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public static float ProfileItemBuilder(float var0, float var1, float var2) {
      boolean flag = var0 > var1;
      if (var2 < 0.0F) {
         var2 = 0.0F;
      } else if (var2 > 1.0F) {
         var2 = 1.0F;
      }

      float f = Math.max(var0, var1) - Math.min(var0, var1);
      float f1 = f * var2;
      return var1 + (flag ? f1 : -f1);
   }

   public static Color on23(Color var0, int var1) {
      return new Color(var0.getRed(), var0.getGreen(), var0.getBlue(), MathHelper.clamp(var1, 0, 255));
   }

   public static Color on23(Color var0, Color var1, double var2, double var4) {
      int i = (int)((System.currentTimeMillis() / var2 + var4) % 360.0);
      i = (i >= 180 ? 360 - i : i) * 2;
      return on23(var0, var1, i / 360.0F);
   }

   public static Color on23(boolean var0, int var1) {
      float f = var0 ? 3500.0F : 3000.0F;
      float f1 = (float)(System.currentTimeMillis() % (int)f + var1);
      if (f1 > f) {
         f1 -= f;
      }

      f1 /= f;
      if (f1 > 0.5F) {
         f1 = 0.5F - (f1 - 0.5F);
      }

      f1 += 0.5F;
      return Color.getHSBColor(f1, 0.4F, 1.0F);
   }

   public static Color UiAnimation(int var0, float var1, float var2) {
      double d0 = Math.ceil((float)(System.currentTimeMillis() + var0) / 16.0F);
      d0 %= 360.0;
      return Color.getHSBColor((float)(d0 / 360.0), var1, var2);
   }

   public static Color ServiceException(int var0, int var1) {
      int i = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      int j;
      return Color.getHSBColor((float)((j = i % 360) / 360.0) < 0.5 ? -((float)(j / 360.0)) : (float)(j / 360.0), 0.5F, 1.0F);
   }

   public static Color on23(Color var0) {
      float[] afloat = Color.RGBtoHSB(var0.getRed(), var0.getGreen(), var0.getBlue(), null);
      float f = 0.84F;
      float f1 = afloat[0] - f;
      return new Color(Color.HSBtoRGB(f1, afloat[1], afloat[2]));
   }

   public static Color on23(Color var0, float var1) {
      var1 = Math.min(1.0F, Math.max(0.0F, var1));
      return new Color(var0.getRed(), var0.getGreen(), var0.getBlue(), (int)(var0.getAlpha() * var1));
   }

   public static int Easing(int var0, float var1) {
      var1 = Math.min(1.0F, Math.max(0.0F, var1));
      Color color = new Color(var0);
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(color.getAlpha() * var1)).getRGB();
   }

   public static Color UiAnimation(Color var0, float var1) {
      return new Color(
         Math.max((int)(var0.getRed() * var1), 0), Math.max((int)(var0.getGreen() * var1), 0), Math.max((int)(var0.getBlue() * var1), 0), var0.getAlpha()
      );
   }

   public static Color on23(int var0, int var1, float var2, float var3, float var4) {
      int i = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      float f = i / 360.0F;
      Color color = new Color(Color.HSBtoRGB(f, var2, var3));
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.max(0, Math.min(255, (int)(var4 * 255.0F))));
   }

   public static Color on23(int var0, int var1, Color var2, Color var3, boolean var4) {
      int i = (int)((System.currentTimeMillis() / var0 + var1) % 360L);
      i = (i >= 180 ? 360 - i : i) * 2;
      return var4 ? UiAnimation(var2, var3, i / 360.0F) : on23(var2, var3, i / 360.0F);
   }

   public static Color on23(Color var0, Color var1, float var2) {
      var2 = Math.min(1.0F, Math.max(0.0F, var2));
      return new Color(
         on23(var0.getRed(), var1.getRed(), var2),
         on23(var0.getGreen(), var1.getGreen(), var2),
         on23(var0.getBlue(), var1.getBlue(), var2),
         on23(var0.getAlpha(), var1.getAlpha(), var2)
      );
   }

   public static Color UiAnimation(Color var0, Color var1, float var2) {
      var2 = Math.min(1.0F, Math.max(0.0F, var2));
      float[] afloat = Color.RGBtoHSB(var0.getRed(), var0.getGreen(), var0.getBlue(), null);
      float[] afloat1 = Color.RGBtoHSB(var1.getRed(), var1.getGreen(), var1.getBlue(), null);
      Color color = Color.getHSBColor(
         UiAnimation(afloat[0], afloat1[0], var2), UiAnimation(afloat[1], afloat1[1], var2), UiAnimation(afloat[2], afloat1[2], var2)
      );
      return new Color(color.getRed(), color.getGreen(), color.getBlue(), on23(var0.getAlpha(), var1.getAlpha(), var2));
   }

   public static double NbtEditor(double var0, double var2, double var4) {
      return var0 + (var2 - var0) * var4;
   }

   public static float UiAnimation(float var0, float var1, double var2) {
      return (float)NbtEditor((double)var0, (double)var1, (double)((float)var2));
   }

   public static int on23(int var0, int var1, double var2) {
      return (int)NbtEditor(var0, var1, (float)var2);
   }

   public static BufferBuilder on23(MatrixStack var0, float var1, float var2, float var3, float var4) {
      path();
      Matrix4f matrix4f = var0.peek().getPositionMatrix();
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION);
      on23(bufferbuilder, matrix4f, var1, var2, var1 + var3, var2 + var4);
      return bufferbuilder;
   }

   public static void on23(BufferBuilder var0, Matrix4f var1, float var2, float var3, float var4, float var5) {
      var0.vertex(var1, var2, var3, 0.0F);
      var0.vertex(var1, var2, var5, 0.0F);
      var0.vertex(var1, var4, var5, 0.0F);
      var0.vertex(var1, var4, var3, 0.0F);
   }

   public static boolean UiAnimation(Color var0) {
      return StringCodec(var0.getRed() / 255.0F, var0.getGreen() / 255.0F, var0.getBlue() / 255.0F);
   }

   public static boolean StringCodec(float var0, float var1, float var2) {
      return on23(var0, var1, var2, 0.0F, 0.0F, 0.0F) < on23(var0, var1, var2, 1.0F, 1.0F, 1.0F);
   }

   public static float on23(float var0, float var1, float var2, float var3, float var4, float var5) {
      float f = var3 - var0;
      float f1 = var4 - var1;
      float f2 = var5 - var2;
      return (float)Math.sqrt(f * f + f1 * f1 + f2 * f2);
   }

   public static Color on23(Color var0, Color var1, float var2, boolean var3) {
      if (!var3) {
         return var2 >= 0.95 ? var1 : var0;
      }

      int i = var1.getRed() - var0.getRed();
      int j = var1.getGreen() - var0.getGreen();
      int k = var1.getBlue() - var0.getBlue();
      int l = var1.getAlpha() - var0.getAlpha();
      return new Color(
         EventGetFogColorHook(var0.getRed() + (int)(i * var2)),
         EventGetFogColorHook(var0.getGreen() + (int)(j * var2)),
         EventGetFogColorHook(var0.getBlue() + (int)(k * var2)),
         EventGetFogColorHook(var0.getAlpha() + (int)(l * var2))
      );
   }

   public static int EventGetFogColorHook(int var0) {
      return var0 > 255 ? 255 : Math.max(var0, 0);
   }

   public static void on23(BufferBuilder var0) {
      MsdfRenderer.flushBatch();
      BuiltBuffer builtbuffer = var0.endNullable();
      if (builtbuffer != null) {
         org.zenith.render.LegacyRenderBridge.draw(builtbuffer);
      }
   }

   public AvatarRenderer() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
