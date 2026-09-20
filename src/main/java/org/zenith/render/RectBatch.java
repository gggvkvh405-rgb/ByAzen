package org.zenith.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.Tessellator;
import org.joml.Matrix4f;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.util.ArgbColor;

public class RectBatch {
   public BufferBuilder bufferBuilder;
   public boolean PlayerStateService;

   public boolean isStarted() {
      return this.PlayerStateService;
   }

   public void map44() {
      MsdfRenderer.flushBatch();
      if (!this.PlayerStateService) {
         org.zenith.render.LegacyRenderBridge.usePositionColor();
         org.zenith.render.LegacyRenderBridge.enableBlend();
         org.zenith.render.LegacyRenderBridge.defaultBlendFunc();
         this.bufferBuilder = Tessellator.getInstance().begin(DrawMode.QUADS, VertexFormats.POSITION_COLOR);
         this.PlayerStateService = true;
      }
   }

   public void Easing(Matrix4f var1, float var2, float var3, float var4, float var5, ArgbColor var6) {
      int i = var6.call001();
      this.bufferBuilder.vertex(var1, var2, var3 + var5, 0.0F).color(i);
      this.bufferBuilder.vertex(var1, var2 + var4, var3 + var5, 0.0F).color(i);
      this.bufferBuilder.vertex(var1, var2 + var4, var3, 0.0F).color(i);
      this.bufferBuilder.vertex(var1, var2, var3, 0.0F).color(i);
   }

   public void flush() {
      if (this.PlayerStateService) {
         org.zenith.render.LegacyRenderBridge.draw(this.bufferBuilder.end());
         org.zenith.render.LegacyRenderBridge.disableBlend();
         this.bufferBuilder = null;
         this.PlayerStateService = false;
      }
   }
}
