package org.zenith.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public final class BoxShaderRenderer {
   public static SimpleFramebuffer simpleFramebuffer8;
   public static RawShaderProgram var056;
   public static RawShaderProgram var053;
   public static long long132 = -1L;
   public static float float185;
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static void on23(List<Box> var0, int var1, int var2, int var3, float var4, int var5, float var6) {
      Framebuffer framebuffer = minecraftClient3.getFramebuffer();
      if (framebuffer != null && var0 != null && !var0.isEmpty()) {
         try {
            List<BoxShaderUniforms> list = NbtEditor(var0);
            if (list.isEmpty()) {
               return;
            }

            NbtEditor(framebuffer.textureWidth, framebuffer.textureHeight);
            if (simpleFramebuffer8 == null || var056 == null || var053 == null) {
               return;
            }

            LegacyRenderBridge.setOutput(simpleFramebuffer8);
            org.zenith.render.LegacyRenderBridge.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            org.zenith.render.LegacyRenderBridge.clear(16384);
            ItemServiceBase(list);
            LegacyRenderBridge.setOutput(framebuffer);
            org.zenith.render.LegacyRenderBridge.enableBlend();
            org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
            org.zenith.render.LegacyRenderBridge.disableDepthTest();
            org.zenith.render.LegacyRenderBridge.depthMask(false);
            org.zenith.render.LegacyRenderBridge.activeTexture(33984);
            org.zenith.render.LegacyRenderBridge.bindTexture(simpleFramebuffer8.getColorAttachmentView());
            var056.bind();
            var056.ItemSpec("ColorTexture", 0);
            var056.on23("resolution", Math.max(1, simpleFramebuffer8.textureWidth), Math.max(1, simpleFramebuffer8.textureHeight));
            var056.on23("time", EventInteractBlock(var4));
            var056.ItemSpec("effectMode", var5);
            var056.on23("effectAlpha", Math.clamp(var6, 0.0F, 1.0F));
            on23(var056, "outlineColor", var1);
            on23(var056, "firstFillColor", var2);
            on23(var056, "secondFillColor", var3);
            HandShaderManager.var14336();
         } catch (Exception exception) {
            System.err.println("Failed to render block overlay shader: " + exception.getMessage());
            return;
         } finally {
            if (var056 != null) {
               var056.unbind();
            }

            org.zenith.render.LegacyRenderBridge.activeTexture(33984);
            org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
            org.zenith.render.LegacyRenderBridge.disableBlend();
            org.zenith.render.LegacyRenderBridge.depthMask(true);
            org.zenith.render.LegacyRenderBridge.enableDepthTest();
            if (minecraftClient3.getFramebuffer() != null) {
               LegacyRenderBridge.restoreMainOutput();
            }
         }
      }
   }

   public static void NbtEditor(int var0, int var1) {
      int i = Math.max(1, var0);
      int j = Math.max(1, var1);
      if (simpleFramebuffer8 == null || simpleFramebuffer8.textureWidth != i || simpleFramebuffer8.textureHeight != j) {
         if (simpleFramebuffer8 != null) {
            simpleFramebuffer8.delete();
         }

         simpleFramebuffer8 = new SimpleFramebuffer("Zenith box shader", i, j, false);
      }

      if (var056 == null) {
         var056 = new RawShaderProgram("hand", "block_overlay", "block_overlay");
      }

      if (var053 == null) {
         var053 = new RawShaderProgram("hand", "block_overlay_mask", "block_overlay_mask");
      }
   }

   public static void ItemServiceBase(List<BoxShaderUniforms> var0) {
      org.zenith.render.LegacyRenderBridge.disableBlend();
      org.zenith.render.LegacyRenderBridge.disableCull();
      org.zenith.render.LegacyRenderBridge.disableDepthTest();
      org.zenith.render.LegacyRenderBridge.depthMask(false);
      org.zenith.render.LegacyRenderBridge.colorMask(true, true, true, true);
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION);

      for (BoxShaderUniforms l1iil1iii_ii1il11l111ii11iil : var0) {
         Vec3d[] avec3d = l1iil1iii_ii1il11l111ii11iil.int329();
         on23(bufferbuilder, avec3d[0], avec3d[1], avec3d[2], avec3d[3]);
         on23(bufferbuilder, avec3d[0], avec3d[4], avec3d[5], avec3d[1]);
         on23(bufferbuilder, avec3d[1], avec3d[5], avec3d[6], avec3d[2]);
         on23(bufferbuilder, avec3d[3], avec3d[2], avec3d[6], avec3d[7]);
         on23(bufferbuilder, avec3d[0], avec3d[3], avec3d[7], avec3d[4]);
         on23(bufferbuilder, avec3d[4], avec3d[7], avec3d[6], avec3d[5]);
      }

      BuiltBuffer builtbuffer = bufferbuilder.endNullable();
      if (builtbuffer != null) {
         var053.bind();
         org.zenith.render.LegacyRenderBridge.draw(builtbuffer);
         var053.unbind();
      }

      org.zenith.render.LegacyRenderBridge.enableCull();
      org.zenith.render.LegacyRenderBridge.depthMask(true);
      org.zenith.render.LegacyRenderBridge.enableDepthTest();
   }

   public static List<BoxShaderUniforms> NbtEditor(List<Box> var0) {
      return var0.stream().map(BoxShaderRenderer::SimpleItemBuilder).filter(Objects::nonNull).toList();
   }

   public static BoxShaderUniforms SimpleItemBuilder(Box var0) {
      Box box = var0.expand(0.001);
      Vec3d[] avec3d = new Vec3d[]{
         new Vec3d(box.minX, box.minY, box.minZ),
         new Vec3d(box.maxX, box.minY, box.minZ),
         new Vec3d(box.maxX, box.minY, box.maxZ),
         new Vec3d(box.minX, box.minY, box.maxZ),
         new Vec3d(box.minX, box.maxY, box.minZ),
         new Vec3d(box.maxX, box.maxY, box.minZ),
         new Vec3d(box.maxX, box.maxY, box.maxZ),
         new Vec3d(box.minX, box.maxY, box.maxZ)
      };
      Vec3d[] avec3d1 = new Vec3d[avec3d.length];

      for (int i = 0; i < avec3d.length; i++) {
         avec3d1[i] = ServiceException(avec3d[i]);
         if (avec3d1[i] == null) {
            return null;
         }
      }

      return new BoxShaderUniforms(avec3d1);
   }

   public static Vec3d ServiceException(Vec3d var0) {
      Vec3d vec3d = ScreenProjection.BotDisconnectEvent(var0);
      if (!(vec3d.z <= 0.0) && !(vec3d.z >= 1.0)) {
         double d0 = Math.max(1, minecraftClient3.getWindow().getScaledWidth());
         double d1 = Math.max(1, minecraftClient3.getWindow().getScaledHeight());
         double d2 = vec3d.x / d0 * 2.0 - 1.0;
         double d3 = 1.0 - vec3d.y / d1 * 2.0;
         return new Vec3d(d2, d3, 0.0);
      } else {
         return null;
      }
   }

   public static void on23(BufferBuilder var0, Vec3d var1, Vec3d var2, Vec3d var3, Vec3d var4) {
      on23(var0, var1);
      on23(var0, var2);
      on23(var0, var3);
      on23(var0, var4);
   }

   public static void on23(BufferBuilder var0, Vec3d var1) {
      var0.vertex((float)var1.x, (float)var1.y, 0.0F);
   }

   public static float EventInteractBlock(float var0) {
      long i = System.nanoTime();
      if (long132 < 0L) {
         long132 = i;
         return float185;
      } else {
         float f = Math.min((float)(i - long132) / 1.0E9F, 0.1F);
         long132 = i;
         float185 = (float185 + f * Math.max(0.0F, var0)) % 100000.0F;
         return float185;
      }
   }

   public static void on23(RawShaderProgram var0, String var1, int var2) {
      var0.on23(var1, (var2 >> 16 & 0xFF) / 255.0F, (var2 >> 8 & 0xFF) / 255.0F, (var2 & 0xFF) / 255.0F, (var2 >> 24 & 0xFF) / 255.0F);
   }
}
