package org.zenith.utility.render.display.base;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.joml.Matrix3x2f;
import org.zenith.base.font.Font;
import org.zenith.render.HudPreviewRenderQueue;
import org.zenith.util.ArgbColor;

public final class QueuedCustomDrawContext extends CustomDrawContext {
   public boolean replaying;

   public QueuedCustomDrawContext(CustomDrawContext var1) {
      super(var1);
   }

   public static QueuedCustomDrawContext of(CustomDrawContext var0) {
      return var0 instanceof QueuedCustomDrawContext ? (QueuedCustomDrawContext)var0 : new QueuedCustomDrawContext(var0);
   }

   public void queueContent(Runnable var1) {
      Matrix4f matrix4f = new Matrix4f(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()));
      HudPreviewRenderQueue.NbtItemSpec(() -> this.runWithMatrix(matrix4f, var1));
   }

   public void queueBackground(Runnable var1) {
      Matrix4f matrix4f = new Matrix4f(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()));
      HudPreviewRenderQueue.ItemServiceBase(() -> this.runWithMatrix(matrix4f, var1));
   }

   public void runWithMatrix(Matrix4f var1, Runnable var2) {
      Matrix3x2f previous = new Matrix3x2f(this.getMatrices());
      boolean flag = this.replaying;
      this.getMatrices().set(var1.m00(), var1.m01(), var1.m10(), var1.m11(), var1.m30(), var1.m31());
      this.replaying = true;

      try {
         var2.run();
      } finally {
         this.replaying = flag;
         this.getMatrices().set(previous);
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, ArgbColor var5) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5);
      } else {
         this.queueContent(() -> super.drawText(var1, var2, var3, var4, var5));
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, ArgbColor var5, boolean var6, float var7, float var8, float var9) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } else {
         this.queueContent(() -> super.drawText(var1, var2, var3, var4, var5, var6, var7, var8, var9));
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, int var5, boolean var6, float var7, float var8, float var9) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } else {
         this.queueContent(() -> super.drawText(var1, var2, var3, var4, var5, var6, var7, var8, var9));
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, GradientRadius var5) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5);
      } else {
         this.queueContent(() -> super.drawText(var1, var2, var3, var4, var5));
      }
   }

   @Override
   public void drawText(Font var1, Text var2, float var3, float var4, int var5) {
      if (this.replaying) {
         super.drawText(var1, var2, var3, var4, var5);
      } else {
         this.queueContent(() -> super.drawText(var1, var2, var3, var4, var5));
      }
   }

   @Override
   public void drawRoundedRect(float var1, float var2, float var3, float var4, CornerRadius var5, ArgbColor var6) {
      if (this.replaying) {
         super.drawRoundedRect(var1, var2, var3, var4, var5, var6);
      } else {
         HudPreviewRenderQueue.ItemRegistry(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public void drawRect(float var1, float var2, float var3, float var4, ArgbColor var5) {
      if (this.replaying) {
         super.drawRect(var1, var2, var3, var4, var5);
      } else {
         HudPreviewRenderQueue.UiAnimation(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void drawBlurHudBooleanCheck(
      float var1, float var2, float var3, float var4, float var5, CornerRadius var6, ArgbColor var7, boolean var8, boolean var9
   ) {
      if (this.replaying) {
         super.drawBlurHudBooleanCheck(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      } else {
         HudPreviewRenderQueue.on23(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   public void drawItem(ItemStack item, int x, int y) {
      if (this.replaying) {
         super.drawItem(item, x, y);
      } else {
         this.queueContent(() -> super.drawItem(item, x, y));
      }
   }

   public void drawItem(ItemStack stack, int x, int y, int seed) {
      if (this.replaying) {
         super.drawItem(stack, x, y, seed);
      } else {
         this.queueContent(() -> super.drawItem(stack, x, y, seed));
      }
   }

   public void fill(int x1, int y1, int x2, int y2, int color) {
      if (this.replaying) {
         super.fill(x1, y1, x2, y2, color);
      } else {
         this.queueRect(x1, y1, x2, y2, color);
      }
   }

   public void fill(int x1, int y1, int x2, int y2, int z, int color) {
      if (this.replaying) {
         super.fill(x1, y1, x2, y2, color);
      } else {
         this.queueRect(x1, y1, x2, y2, color);
      }
   }

   public void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int color) {
      if (this.replaying) {
         super.fill(layer.getRenderPipeline(), x1, y1, x2, y2, color);
      } else {
         this.queueRect(x1, y1, x2, y2, color);
      }
   }

   public void fill(RenderLayer layer, int x1, int y1, int x2, int y2, int z, int color) {
      if (this.replaying) {
         super.fill(layer.getRenderPipeline(), x1, y1, x2, y2, color);
      } else {
         this.queueRect(x1, y1, x2, y2, color);
      }
   }

   public void queueRect(int var1, int var2, int var3, int var4, int var5) {
      int i = Math.min(var1, var3);
      int j = Math.min(var2, var4);
      int k = Math.max(var1, var3);
      int l = Math.max(var2, var4);
      HudPreviewRenderQueue.UiAnimation(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), i, j, k - i, l - j, ArgbColor.HudRenderEvent(var5));
   }

   @Override
   public void enableScissor(int x1, int y1, int x2, int y2) {
      if (this.replaying) {
         super.enableScissor(x1, y1, x2, y2);
      } else {
         this.queueBackground(() -> super.enableScissor(x1, y1, x2, y2));
         this.queueContent(() -> super.enableScissor(x1, y1, x2, y2));
      }
   }

   @Override
   public void disableScissor() {
      if (this.replaying) {
         super.disableScissor();
      } else {
         this.queueBackground(() -> super.disableScissor());
         this.queueContent(() -> super.disableScissor());
      }
   }
}
