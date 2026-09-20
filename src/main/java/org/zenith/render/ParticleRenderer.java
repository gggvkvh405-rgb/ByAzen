package org.zenith.render;

import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.zenith.core.AvatarRenderer;
import org.zenith.core.BotGuardEntity;
import org.zenith.core.PositionProvider;
import org.zenith.core.SpinMarker;
import org.zenith.util.ArgbColor;

public class ParticleRenderer {
   public static final MinecraftClient minecraftClient4 = MinecraftClient.getInstance();
   public static double double116;
   public static double double117;
   public static double double118;
   public static final Quaternionf quaternionf = new Quaternionf();
   public static final Quaternionf quaternionf2 = new Quaternionf();
   public static final MatrixStack matrixStack4 = new MatrixStack();
   public static final Map<String, List<PositionProvider>> map54 = new HashMap<>();

   @SafeVarargs
   public static void on23(MatrixStack var0, float var1, List<? extends PositionProvider>... var2) {
      map54.clear();
      int i = 0;

      for (List<? extends PositionProvider> list : var2) {
         if (list != null) {
            for (PositionProvider l1l11lii1ix : list) {
               if (!l1l11lii1ix.float304()) {
                  map54.computeIfAbsent(l1l11lii1ix.var111(), var0x -> new ArrayList<>()).add(l1l11lii1ix);
                  i++;
               }
            }
         }
      }

      if (i != 0) {
         var11914();
         Tessellator tessellator = Tessellator.getInstance();

         for (Entry<String, List<PositionProvider>> entry : map54.entrySet()) {
            Identifier identifier = ParticleTextures.ChatTagParser(entry.getKey());
            if (identifier != null) {
               org.zenith.render.LegacyRenderBridge.setTexture(0, identifier);
               List<PositionProvider> list1 = entry.getValue();
               BufferBuilder bufferbuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
               boolean flag = false;

               for (PositionProvider l1l11lii1ix : list1) {
                  if (l1l11lii1ix.EventPushOutOfBlocks(var1) >= 0.001F) {
                     on23(bufferbuilder, l1l11lii1ix, var1);
                     flag = true;
                  }
               }

               if (flag) {
                  org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
               }

               list1.clear();
            }
         }

         map54.clear();
         var11915();
      }
   }

   public static void on23(MatrixStack var0, List<SpinMarker> var1, float var2, float var3) {
      if (!var1.isEmpty()) {
         var11914();
         org.zenith.render.LegacyRenderBridge.setTexture(0, ParticleTextures.Texture.call410.boolean83().get());
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder bufferbuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
         float f = var3 + 2.5F;
         boolean flag = false;

         for (SpinMarker l11il1ilil1l : var1) {
            Vec3d vec3d = l11il1ilil1l.getModeSetting3().lerp(l11il1ilil1l.WallBypass(), var2);
            Vec3d vec3d1 = l11il1ilil1l.boolean86();
            double d0 = vec3d1.length();
            float f1 = 1.0F - (float)l11il1ilil1l.boolean87() / l11il1ilil1l.boolean149();
            float f2 = Math.min(f1 / 0.15F, 1.0F);
            float f3 = f1 > 0.8F ? 1.0F - (f1 - 0.8F) / 0.2F : 1.0F;
            float f4 = f2 * f3;
            Vec3d vec3d2 = d0 > 0.001 ? vec3d1.normalize() : new Vec3d(0.0, 1.0, 0.0);
            float f5 = (float)Math.min(d0 / 0.2, 1.0);
            float f6 = Math.max(f5 * f4, 0.1F);
            byte b0 = 80;

            for (int i = 0; i < b0; i++) {
               float f7 = (float)i / b0;
               if (!(f7 >= f6)) {
                  float f8 = f7 / f6;
                  float f9 = l11il1ilil1l.getSize() * (1.0F - f8 * 0.5F) * 2.0F * f4;
                  float f10 = (float)Math.pow(1.0F - f8, 0.4) * l11il1ilil1l.EventPushOutOfBlocks(var2) * f4 * 0.8F;
                  if (!(f10 < 0.01F)) {
                     Vec3d vec3d3 = vec3d.subtract(vec3d2.multiply(f8 * f6 * f));
                     float f11 = (float)Math.pow(1.0F - f8, 1.2) * f4;
                     float f12 = f9 * (1.8F + f11 * 0.8F);
                     float f13 = f10 * 0.5F * f11;
                     if (f13 > 0.01F) {
                        on23(bufferbuilder, vec3d3, f12, f13, l11il1ilil1l.getColor());
                        flag = true;
                     }

                     on23(bufferbuilder, vec3d3, f9, f10, l11il1ilil1l.getColor());
                     flag = true;
                  }
               }
            }
         }

         if (flag) {
            org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
         }

         var11915();
      }
   }

   public static void UiAnimation(MatrixStack var0, List<BotGuardEntity> var1, float var2, float var3) {
      if (!var1.isEmpty()) {
         var11914();
         Tessellator tessellator = Tessellator.getInstance();
         org.zenith.render.LegacyRenderBridge.setTexture(0, ParticleTextures.Texture.call437.boolean83().get());
         BufferBuilder bufferbuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

         for (BotGuardEntity ll1i1il1111liiilx : var1) {
            if (!ll1i1il1111liiilx.float304()) {
               Vec3d vec3d = ll1i1il1111liiilx.getModeSetting3().lerp(ll1i1il1111liiilx.WallBypass(), var2);
               float f = ll1i1il1111liiilx.EventPushOutOfBlocks(var2);
               if (!(f < 0.005F)) {
                  on23(bufferbuilder, vec3d, ll1i1il1111liiilx.EventWindowSizeChanged(var2), f * 0.22F, ll1i1il1111liiilx.getColor(), 0.0F);
                  on23(bufferbuilder, vec3d, ll1i1il1111liiilx.Event18Ext5(var2), f * 0.48F, ll1i1il1111liiilx.getColor(), 0.0F);
                  Vec3d vec3d1 = ll1i1il1111liiilx.boolean86();
                  double d0 = vec3d1.length();
                  if (!(d0 <= 5.0E-4)) {
                     Vec3d vec3d2 = vec3d1.normalize();
                     float f1 = ll1i1il1111liiilx.Event05(var3);
                     byte b0 = 16;

                     for (int i = 1; i <= b0; i++) {
                        float f2 = (float)i / b0;
                        float f3 = f * (float)Math.pow(1.0F - f2, 1.45F) * 0.42F;
                        if (!(f3 < 0.01F)) {
                           Vec3d vec3d3 = vec3d.subtract(vec3d2.multiply(f2 * f1));
                           float f4 = ll1i1il1111liiilx.Event18Ext5(var2) * (1.0F - f2 * 0.72F);
                           on23(bufferbuilder, vec3d3, f4, f3, ll1i1il1111liiilx.getColor(), 0.0F);
                        }
                     }
                  }
               }
            }
         }

         AvatarRenderer.on23(bufferbuilder);
         org.zenith.render.LegacyRenderBridge.setTexture(0, ParticleTextures.Texture.call438.boolean83().get());
         BufferBuilder bufferbuilder1 = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

         for (BotGuardEntity ll1i1il1111liiilx : var1) {
            if (!ll1i1il1111liiilx.float304()) {
               Vec3d vec3d4 = ll1i1il1111liiilx.getModeSetting3().lerp(ll1i1il1111liiilx.WallBypass(), var2);
               float f5 = ll1i1il1111liiilx.EventPushOutOfBlocks(var2);
               if (!(f5 < 0.005F)) {
                  on23(
                     bufferbuilder1,
                     vec3d4,
                     ll1i1il1111liiilx.AttackEntityEvent(var2),
                     f5,
                     ll1i1il1111liiilx.getColor().EventTick(0.35F),
                     ll1i1il1111liiilx.getRotation()
                  );
               }
            }
         }

         AvatarRenderer.on23(bufferbuilder1);
         var11915();
      }
   }

   public static void on23(Camera var0) {
      Vec3d vec3d = var0.getCameraPos();
      double116 = vec3d.x;
      double117 = vec3d.y;
      double118 = vec3d.z;
      float f = var0.getPitch();
      float f1 = var0.getYaw();
      quaternionf.identity();
      quaternionf.rotateX((float)Math.toRadians(f));
      quaternionf.rotateY((float)Math.toRadians(f1 + 180.0F));
      quaternionf2.identity();
      quaternionf2.rotateY((float)Math.toRadians(-f1));
      quaternionf2.rotateX((float)Math.toRadians(f));
   }

   public static void on23(MatrixStack var0, Vec3d var1, Vec3d var2, float var3, float var4, ArgbColor var5, float var6) {
      var11914();
      org.zenith.render.LegacyRenderBridge.setTexture(0, ParticleTextures.Texture.call410.boolean83().get());
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.begin(DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
      double d0 = var2.length();
      Vec3d vec3d = d0 > 0.001 ? var2.normalize() : new Vec3d(0.0, 0.0, -1.0);
      byte b0 = 60;
      boolean flag = false;

      for (int i = 0; i < b0; i++) {
         float f = (float)i / b0;
         float f1 = var4 * (1.0F - f * 0.7F) * 2.0F;
         float f2 = (float)Math.pow(1.0F - f, 0.5) * 0.9F;
         if (!(f2 < 0.01F) && !(f1 < 0.01F)) {
            Vec3d vec3d1 = var1.subtract(vec3d.multiply(f * var3));
            on23(bufferbuilder, vec3d1, f1 * 1.8F, f2 * 0.4F, var5);
            on23(bufferbuilder, vec3d1, f1, f2, var5);
            flag = true;
         }
      }

      if (flag) {
         org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
      }

      var11915();
   }

   public static void on23(BufferBuilder var0, PositionProvider var1, float var2) {
      Vec3d vec3d = var1.getModeSetting3().lerp(var1.WallBypass(), var2);
      float f = (float)(vec3d.x - double116);
      float f1 = (float)(vec3d.y - double117);
      float f2 = (float)(vec3d.z - double118);
      float f3 = var1.EventPushOutOfBlocks(var2);
      if (!(f3 < 0.001F)) {
         float f4 = var1.getSize();
         ArgbColor i11ii1llliilllii1i1 = var1.getColor();
         int i = (int)(f3 * i11ii1llliilllii1i1.var14325()) << 24
            | i11ii1llliilllii1i1.float240() << 16
            | i11ii1llliilllii1i1.var14323() << 8
            | i11ii1llliilllii1i1.var14324();
         matrixStack4.push();
         matrixStack4.multiply(quaternionf);
         matrixStack4.translate(f, f1, f2);
         matrixStack4.multiply(quaternionf2);
         float f5 = var1.getRotation();
         if (f5 != 0.0F) {
            matrixStack4.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f5));
         }

         Matrix4f matrix4f = matrixStack4.peek().getPositionMatrix();
         var0.vertex(matrix4f, 0.0F, -f4, 0.0F).texture(0.0F, 1.0F).color(i);
         var0.vertex(matrix4f, -f4, -f4, 0.0F).texture(1.0F, 1.0F).color(i);
         var0.vertex(matrix4f, -f4, 0.0F, 0.0F).texture(1.0F, 0.0F).color(i);
         var0.vertex(matrix4f, 0.0F, 0.0F, 0.0F).texture(0.0F, 0.0F).color(i);
         matrixStack4.pop();
      }
   }

   public static void on23(BufferBuilder var0, Vec3d var1, float var2, float var3, ArgbColor var4) {
      on23(var0, var1, var2, var3, var4, 0.0F);
   }

   public static void on23(BufferBuilder var0, Vec3d var1, float var2, float var3, ArgbColor var4, float var5) {
      if (!(var3 < 0.001F)) {
         float f = (float)(var1.x - double116);
         float f1 = (float)(var1.y - double117);
         float f2 = (float)(var1.z - double118);
         int i = (int)(var3 * var4.var14325()) << 24 | var4.float240() << 16 | var4.var14323() << 8 | var4.var14324();
         float f3 = var2 / 2.0F;
         matrixStack4.push();
         matrixStack4.multiply(quaternionf);
         matrixStack4.translate(f, f1, f2);
         matrixStack4.multiply(quaternionf2);
         if (var5 != 0.0F) {
            matrixStack4.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(var5));
         }

         Matrix4f matrix4f = matrixStack4.peek().getPositionMatrix();
         var0.vertex(matrix4f, f3, -f3, 0.0F).texture(0.0F, 1.0F).color(i);
         var0.vertex(matrix4f, -f3, -f3, 0.0F).texture(1.0F, 1.0F).color(i);
         var0.vertex(matrix4f, -f3, f3, 0.0F).texture(1.0F, 0.0F).color(i);
         var0.vertex(matrix4f, f3, f3, 0.0F).texture(0.0F, 0.0F).color(i);
         matrixStack4.pop();
      }
   }

   public static void var11914() {
      org.zenith.render.LegacyRenderBridge.enableBlend();
      org.zenith.render.LegacyRenderBridge.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE);
      org.zenith.render.LegacyRenderBridge.enableDepthTest();
      org.zenith.render.LegacyRenderBridge.depthMask(false);
      org.zenith.render.LegacyRenderBridge.usePositionTexColor();
   }

   public static void var11915() {
      org.zenith.render.LegacyRenderBridge.depthMask(true);
      org.zenith.render.LegacyRenderBridge.disableBlend();
      org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
   }
}
