package org.zenith.utility.render.display.base;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.zenith.base.font.Font;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.render.RenderCommandQueue;
import org.zenith.util.ArgbColor;

public class BlurHudDrawContext extends HudDrawContext {
   protected BlurHudDrawContext(HudDrawContext var1) {
      super(var1, var1.getMouseX(), var1.getMouseY(), var1.getDelta());
   }

   public static HudDrawContext TextScanner(HudDrawContext var0) {
      return RenderCommandQueue.enabled && !(var0 instanceof BlurHudDrawContext) ? new BlurHudDrawContext(var0) : var0;
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, ArgbColor var5) {
      if (RenderCommandQueue.set14()) {
         RenderCommandQueue.on23(var1, var2, org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var3, var4, var5.call001());
      } else {
         super.drawText(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, int var5, boolean var6, float var7, float var8, float var9) {
      if (RenderCommandQueue.set14()) {
         RenderCommandQueue.on23(var1, var2, org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var3, var4, var5, var6, var7, var8, var9);
      } else {
         super.drawText(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }

   @Override
   public void drawText(Font var1, String var2, float var3, float var4, GradientRadius var5) {
      if (RenderCommandQueue.set14()) {
         RenderCommandQueue.on23(var1, var2, org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var3, var4, var5);
      } else {
         super.drawText(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void drawRoundedRect(float var1, float var2, float var3, float var4, CornerRadius var5, ArgbColor var6) {
      if (RenderCommandQueue.set14()) {
         RenderCommandQueue.ColorAnimator(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var1, var2, var3, var4, var5, var6);
      } else {
         super.drawRoundedRect(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public void drawRect(float var1, float var2, float var3, float var4, ArgbColor var5) {
      if (RenderCommandQueue.set14()) {
         RenderCommandQueue.on23(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var1, var2, var3, var4, var5);
      } else {
         super.drawRect(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public void drawRoundedRect(float var1, float var2, float var3, float var4, CornerRadius var5, GradientRadius var6) {
      if (RenderCommandQueue.set14()) {
         RenderCommandQueue.on23(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var1, var2, var3, var4, var5, var6);
      } else {
         super.drawRoundedRect(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public void drawRoundedRectBatched(float var1, float var2, float var3, float var4, CornerRadius var5, ArgbColor var6) {
      if (RenderCommandQueue.set14()) {
         RenderCommandQueue.Easing(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var1, var2, var3, var4, var5, var6);
      } else {
         super.drawRoundedRectBatched(var1, var2, var3, var4, var5, var6);
      }
   }

   @Override
   public void beginRoundedRectBatch(CornerRadius var1) {
      if (!RenderCommandQueue.set14()) {
         super.beginRoundedRectBatch(var1);
      }
   }

   @Override
   public void flushRoundedRects() {
      if (!RenderCommandQueue.set14()) {
         super.flushRoundedRects();
      }
   }

   @Override
   public void drawRoundedBorder(float var1, float var2, float var3, float var4, float var5, CornerRadius var6, GradientRadius var7) {
      if (RenderCommandQueue.set14()) {
         RenderCommandQueue.on23(
            org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()),
            var1,
            var2,
            var3,
            var4,
            var5,
            var6,
            var7.call010(),
            var7.call014(),
            var7.call017(),
            var7.call052()
         );
      } else {
         super.drawRoundedBorder(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   @Override
   public void drawRoundedBorder(float var1, float var2, float var3, float var4, float var5, CornerRadius var6, ArgbColor var7) {
      if (RenderCommandQueue.set14()) {
         RenderCommandQueue.on23(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7, var7, var7, var7);
      } else {
         super.drawRoundedBorder(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   @Override
   public void enableScissor(int x1, int y1, int x2, int y2) {
      MsdfRenderer.flushBatch();
      super.enableScissor(x1, y1, x2, y2);
   }

   @Override
   public void disableScissor() {
      MsdfRenderer.flushBatch();
      super.disableScissor();
   }

   public void drawItem(ItemStack item, int x, int y) {
      MsdfRenderer.flushBatch();
      super.drawItem(item, x, y);
   }

   public void drawItem(ItemStack stack, int x, int y, int seed) {
      MsdfRenderer.flushBatch();
      super.drawItem(stack, x, y, seed);
   }

   public void drawItem(ItemStack stack, int x, int y, int seed, int z) {
      MsdfRenderer.flushBatch();
      super.drawItem(stack, x, y, seed);
   }

   public void drawItem(LivingEntity entity, ItemStack stack, int x, int y, int seed) {
      MsdfRenderer.flushBatch();
      super.drawItem(entity, stack, x, y, seed);
   }

   public void drawItemWithoutEntity(ItemStack stack, int x, int y) {
      MsdfRenderer.flushBatch();
      super.drawItemWithoutEntity(stack, x, y);
   }

   public void drawItemWithoutEntity(ItemStack stack, int x, int y, int seed) {
      MsdfRenderer.flushBatch();
      super.drawItemWithoutEntity(stack, x, y, seed);
   }

   public void drawStackOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y) {
      MsdfRenderer.flushBatch();
      super.drawStackOverlay(textRenderer, stack, x, y);
   }

   public void drawStackOverlay(TextRenderer textRenderer, ItemStack stack, int x, int y, String stackCountText) {
      MsdfRenderer.flushBatch();
      super.drawStackOverlay(textRenderer, stack, x, y, stackCountText);
   }
}
