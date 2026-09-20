package org.zenith.render;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;

public class HandShaderManager {
   public static MinecraftClient minecraftClient = MinecraftClient.getInstance();
   public static RawShaderProgram var057;
   public static final String string132 = "sirius_aqua";
   public static final Map<String, RawShaderProgram> map59 = new HashMap<>();
   public static final Set<String> set22 = new HashSet<>();
   public static SimpleFramebuffer simpleFramebuffer;
   public static boolean initialized = false;

   public static void float246() {
      if (!initialized) {
         try {
            var057 = HudTabList("sirius_aqua");
            initialized = true;
            System.out.println("[Zenith/ShaderHand] sirius_aqua loaded OK");
         } catch (Exception exception) {
            System.err.println("[Zenith/ShaderHand] Failed to initialize hand shaders!");
            exception.printStackTrace();
         }
      }
   }

   public static RawShaderProgram HudStatusPanel(String var0) {
      if (var0 == null || var0.isBlank()) {
         return var057;
      }

      if (set22.contains(var0)) {
         return var057;
      }

      try {
         boolean flag = !map59.containsKey(var0);
         RawShaderProgram var05 = HudTabList(var0);
         if (flag) {
            System.out.println("[Zenith/ShaderHand] shader " + var0 + " loaded OK");
         }

         return var05;
      } catch (Exception exception) {
         set22.add(var0);
         System.err.println("[Zenith/ShaderHand] Failed to initialize hand shader: " + var0);
         exception.printStackTrace();
         return var057;
      }
   }

   public static RawShaderProgram HudTabList(String var0) {
      RawShaderProgram lliii11l1lllil = map59.get(var0);
      if (lliii11l1lllil == null) {
         lliii11l1lllil = new RawShaderProgram("hand", var0, "smoke");
         map59.put(var0, lliii11l1lllil);
      }

      if ("sirius_aqua".equals(var0)) {
         var057 = lliii11l1lllil;
      }

      return lliii11l1lllil;
   }

   public static void float247() {
      if (minecraftClient != null && minecraftClient.getWindow() != null) {
         int i = minecraftClient.getWindow().getFramebufferWidth();
         int j = minecraftClient.getWindow().getFramebufferHeight();
         if (simpleFramebuffer == null || simpleFramebuffer.textureWidth != i || simpleFramebuffer.textureHeight != j) {
            if (simpleFramebuffer != null) {
               simpleFramebuffer.delete();
            }

            simpleFramebuffer = new SimpleFramebuffer("Zenith hand shader", i, j, true);
         }
      }
   }

   public static void var14336() {
      RenderSystem.assertOnRenderThread();
      BufferBuilder bufferbuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION);
      bufferbuilder.vertex(-1.0F, -1.0F, 0.0F);
      bufferbuilder.vertex(1.0F, -1.0F, 0.0F);
      bufferbuilder.vertex(1.0F, 1.0F, 0.0F);
      bufferbuilder.vertex(-1.0F, 1.0F, 0.0F);
      org.zenith.render.LegacyRenderBridge.draw(bufferbuilder.end());
   }

   public static RawShaderProgram var14337() {
      return var057;
   }

   public static SimpleFramebuffer string38() {
      return simpleFramebuffer;
   }

   public static boolean isInitialized() {
      return initialized;
   }
}
