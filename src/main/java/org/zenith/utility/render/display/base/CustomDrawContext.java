package org.zenith.utility.render.display.base;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.joml.Matrix3x2f;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.zenith.base.font.Font;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.core.ClientProvider;
import org.zenith.render.RectBatch;
import org.zenith.render.RoundedRectBatch;
import org.zenith.render.GuiMatrixAdapter;
import org.zenith.render.LegacyImmediateRenderer;
import org.zenith.render.ShapeRenderer;
import org.zenith.util.ArgbColor;
import org.zenith.utility.mixin.accessors.DrawContextAccessor;

public class CustomDrawContext extends DrawContext implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public RoundedRectBatch roundedRectBatch = new RoundedRectBatch();
   public RectBatch rectBatch = new RectBatch();
   private final Deque<ScreenRect> legacyScissorStack;

   public CustomDrawContext(DrawContext var1) {
      super(minecraftClient3, var1.state, 0, 0);
      this.getMatrices().set(var1.getMatrices());
      this.scissorStack = var1.scissorStack;
      this.legacyScissorStack = var1 instanceof CustomDrawContext custom
         ? custom.legacyScissorStack
         : new ArrayDeque<>();
   }

   public static CustomDrawContext of(DrawContext var0) {
      return new CustomDrawContext(var0);
   }

   public void drawText(Font var1, String var2, float var3, float var4, ArgbColor var5) {
      MsdfRenderer.renderText(var1.getFont(), var2, var1.getSize(), var5.call001(), org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var3, var4, 0.0F);
   }

   public void drawText(Font var1, String var2, float var3, float var4, ArgbColor var5, boolean var6, float var7, float var8, float var9) {
      this.drawText(var1, var2, var3, var4, var5.call001(), var6, var7, var8, var9);
   }

   public void drawText(Font var1, String var2, float var3, float var4, int var5, boolean var6, float var7, float var8, float var9) {
      MsdfRenderer.renderText(
         var1.getFont(), var2, var1.getSize(), var5, org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var3, var4, 0.0F, var6, var7, var8, var9
      );
   }

   public void enableScissor(float var1, float var2, float var3, float var4) {
      this.enableScissor((int)Math.floor(var1), (int)Math.floor(var2), (int)Math.ceil(var3), (int)Math.ceil(var4));
   }

   public void enableScissor(int x1, int y1, int x2, int y2) {
      // Text is batched independently from shapes. Flush it while the previous
      // clip is still active before entering a nested scissor region.
      MsdfRenderer.flushBatch();
      super.enableScissor(x1, y1, x2, y2);
      ScreenRect scissor = new ScreenRect(x1, y1, x2 - x1, y2 - y1).transform(this.getMatrices());
      ScreenRect parent = this.legacyScissorStack.peekLast();
      if (parent != null) {
         scissor = Objects.requireNonNullElse(scissor.intersection(parent), ScreenRect.empty());
      }

      this.legacyScissorStack.addLast(scissor);
      LegacyImmediateRenderer.setGuiScissor(scissor);
   }

   public void disableScissor() {
      // Capture the active clip on the pending MSDF draw before popping it.
      MsdfRenderer.flushBatch();
      super.disableScissor();
      if (!this.legacyScissorStack.isEmpty()) {
         this.legacyScissorStack.removeLast();
      }
      LegacyImmediateRenderer.setGuiScissor(this.legacyScissorStack.peekLast());
   }

   public void drawText(Font var1, String var2, float var3, float var4, GradientRadius var5) {
      MsdfRenderer.renderText(var1.getFont(), var2, var1.getSize(), var5, org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var3, var4, 0.0F);
   }

   public void drawText(Font var1, Text var2, float var3, float var4, int var5) {
      MsdfRenderer.renderText(
         var1.getFont(), var2, var1.getSize(), org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var3, var4, 0.0F, false, 0.0F, 1.0F, 0.0F, var5
      );
   }

   public void drawText(Font var1, Text var2, float var3, float var4) {
      MsdfRenderer.renderText(var1.getFont(), var2, var1.getSize(), org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var3, var4, 0.0F);
   }

   public void drawSquircle(float var1, float var2, float var3, float var4, float var5, CornerRadius var6, ArgbColor var7) {
      ShapeRenderer.UiAnimation(GuiMatrixAdapter.toMatrixStack(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7);
   }

   public void drawRoundedRect(float var1, float var2, float var3, float var4, CornerRadius var5, ArgbColor var6) {
      ShapeRenderer.on23(GuiMatrixAdapter.toMatrixStack(this.getMatrices()), var1, var2, var3, var4, var5, var6);
   }

   public void drawRoundedRect(float var1, float var2, float var3, float var4, CornerRadius var5, GradientRadius var6) {
      ShapeRenderer.on23(GuiMatrixAdapter.toMatrixStack(this.getMatrices()), var1, var2, var3, var4, var5, var6);
   }

   public void drawRect(float var1, float var2, float var3, float var4, ArgbColor var5) {
      ShapeRenderer.on23(GuiMatrixAdapter.toMatrixStack(this.getMatrices()), var1, var2, var3, var4, var5);
   }

   public void beginRectBatch() {
      this.rectBatch.map44();
   }

   public void drawRectBatched(float var1, float var2, float var3, float var4, ArgbColor var5) {
      this.rectBatch.Easing(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var1, var2, var3, var4, var5);
   }

   public void flushRects() {
      MsdfRenderer.flushBatch();
      this.rectBatch.flush();
   }

   public void beginRoundedRectBatch(CornerRadius var1) {
      this.roundedRectBatch.on23(var1);
   }

   public void drawRoundedRectBatched(float var1, float var2, float var3, float var4, CornerRadius var5, ArgbColor var6) {
      this.roundedRectBatch.on23(var5);
      this.roundedRectBatch.Easing(org.zenith.render.GuiMatrixAdapter.toMatrix4f(this.getMatrices()), var1, var2, var3, var4, var6);
   }

   public void flushRoundedRects() {
      MsdfRenderer.flushBatch();
      this.roundedRectBatch.flush();
   }

   /** Flushes custom immediate batches before sampling an off-screen framebuffer. */
   public void draw() {
      MsdfRenderer.flushBatch();
      if (this.rectBatch.isStarted()) {
         this.rectBatch.flush();
      }
      if (this.roundedRectBatch.isStarted()) {
         this.roundedRectBatch.flush();
      }
   }

   public boolean isRoundedRectBatchActive() {
      return this.roundedRectBatch.isStarted();
   }

   public int drawTextWithBackground(TextRenderer var1, Text var2, int var3, int var4, int var5, CornerRadius var6, ArgbColor var7, ArgbColor var8) {
      int i = var3 - 3;
      int j = var4 - 2;
      int k = var5 + 6;
      Objects.requireNonNull(var1);
      this.drawRoundedRect(i, j, k, 13.0F, var6, var8);
      this.drawText(var1, var2, var3, var4, var7.call001(), true);
      return var1.getWidth(var2);
   }

   public void drawSprite(GuiSprite var1, float var2, float var3, float var4, float var5, ArgbColor var6) {
      ShapeRenderer.on23(GuiMatrixAdapter.toMatrixStack(this.getMatrices()), var1, var2, var3, var4, var5, var6);
   }

   public void drawRoundedCorner(float var1, float var2, float var3, float var4, float var5, float var6, ArgbColor var7, CornerRadius var8) {
      var3 = Math.round(var3);
      var4 = Math.round(var4);
      this.enableScissor((int)Math.ceil(var1 - 10.0F), (int)(var2 - 10.0F), (int)(var1 + var6), (int)(var2 + var6));
      this.drawRoundedBorder(var1, var2, var3, var4, var5, var8, var7);
      this.disableScissor();
      this.enableScissor((int)(var1 + var3 - var6), (int)(var2 - 10.0F), (int)(var1 + var3 + 10.0F), (int)(var2 + var6));
      this.drawRoundedBorder(var1, var2, var3, var4, var5, var8, var7);
      this.disableScissor();
      this.enableScissor((int)(var1 - 10.0F), (int)(var2 + var4 - var6), (int)(var1 + var6), (int)(var2 + var4 + 10.0F));
      this.drawRoundedBorder(var1, var2, var3, var4, var5, var8, var7);
      this.disableScissor();
      this.enableScissor((int)(var1 + var3 - var6), (int)(var2 + var4 - var6), (int)(var1 + var3 + 10.0F), (int)(var2 + var4 + 10.0F));
      this.drawRoundedBorder(var1, var2, var3, var4, var5, var8, var7);
      this.disableScissor();
   }

   public void drawRoundedBorder(float var1, float var2, float var3, float var4, float var5, CornerRadius var6, GradientRadius var7) {
      ShapeRenderer.on23(GuiMatrixAdapter.toMatrixStack(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7);
   }

   public void drawRoundedBorder(float var1, float var2, float var3, float var4, float var5, CornerRadius var6, ArgbColor var7) {
      ShapeRenderer.on23(GuiMatrixAdapter.toMatrixStack(this.getMatrices()), var1, var2, var3, var4, var5, var6, var7);
   }

   public void drawTexture(Identifier var1, float var2, float var3, float var4, float var5, ArgbColor var6) {
      ShapeRenderer.on23(GuiMatrixAdapter.toMatrixStack(this.getMatrices()), var1, var2, var3, var4, var5, var6);
   }

   public void drawBlurHud(float var1, float var2, float var3, float var4, float var5, CornerRadius var6, ArgbColor var7) {
      ShapeRenderer.ItemRegistry(this.getMatrices(), var1, var2, var3, var4, var5, var6, var7);
   }

   public void drawBlurHudBooleanCheck(
      float var1, float var2, float var3, float var4, float var5, CornerRadius var6, ArgbColor var7, boolean var8, boolean var9
   ) {
      ShapeRenderer.on23(this.getMatrices(), var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void drawArcBorder(float var1, float var2, float var3, float var4, float var5, float var6, float var7, ArgbColor var8) {
      ShapeRenderer.on23(this.getMatrices(), var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public void drawRoundedTexture(Identifier var1, float var2, float var3, float var4, float var5, CornerRadius var6) {
      ShapeRenderer.on23(this.getMatrices(), var1, var2, var3, var4, var5, var6);
   }

   public void drawRoundedTexture(Identifier var1, float var2, float var3, float var4, float var5, CornerRadius var6, ArgbColor var7) {
      ShapeRenderer.on23(this.getMatrices(), var1, var2, var3, var4, var5, var6, var7);
   }

   public void drawPlayerHeadWithRoundedShader(Identifier var1, float var2, float var3, float var4, CornerRadius var5, ArgbColor var6) {
      ShapeRenderer.on23(this.getMatrices(), var1, var2, var3, var4, var5, var6);
   }

   private void deferVanillaGuiDraw(Runnable draw) {
      if (!LegacyImmediateRenderer.isGuiDeferring()) {
         draw.run();
         return;
      }

      Matrix3x2f matrix = new Matrix3x2f(this.getMatrices());
      ScreenRect scissor = this.legacyScissorStack.peekLast();
      LegacyImmediateRenderer.deferGuiOverlay(() -> {
         Matrix3x2f previous = new Matrix3x2f(this.getMatrices());
         try {
            if (scissor != null) {
               this.getMatrices().identity();
               super.enableScissor(scissor.getLeft(), scissor.getTop(), scissor.getRight(), scissor.getBottom());
            }
            this.getMatrices().set(matrix);
            draw.run();
         } finally {
            if (scissor != null) {
               super.disableScissor();
            }
            this.getMatrices().set(previous);
         }
      });
   }

   /**
    * Draws a vanilla GUI-atlas sprite after Zenith's deferred shapes. This is
    * required for sprites such as status-effect icons, otherwise the HUD panel
    * background submitted later covers them on 1.21.11.
    */
   public void drawGuiTextureOverlay(RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
      this.deferVanillaGuiDraw(() -> super.drawGuiTexture(pipeline, sprite, x, y, width, height));
   }

   @Override
   public void drawItem(ItemStack stack, int x, int y) {
      ItemStack snapshot = stack.copy();
      this.deferVanillaGuiDraw(() -> super.drawItem(snapshot, x, y));
   }

   @Override
   public void drawItem(ItemStack stack, int x, int y, int seed) {
      ItemStack snapshot = stack.copy();
      this.deferVanillaGuiDraw(() -> super.drawItem(snapshot, x, y, seed));
   }

   @Override
   public void drawItemWithoutEntity(ItemStack stack, int x, int y) {
      ItemStack snapshot = stack.copy();
      this.deferVanillaGuiDraw(() -> super.drawItemWithoutEntity(snapshot, x, y));
   }

   @Override
   public void drawItemWithoutEntity(ItemStack stack, int x, int y, int seed) {
      ItemStack snapshot = stack.copy();
      this.deferVanillaGuiDraw(() -> super.drawItemWithoutEntity(snapshot, x, y, seed));
   }

   @Override
   public void drawItem(LivingEntity entity, ItemStack stack, int x, int y, int seed) {
      ItemStack snapshot = stack.copy();
      this.deferVanillaGuiDraw(() -> super.drawItem(entity, snapshot, x, y, seed));
   }

   @Override
   public void drawStackOverlay(TextRenderer renderer, ItemStack stack, int x, int y) {
      ItemStack snapshot = stack.copy();
      this.deferVanillaGuiDraw(() -> super.drawStackOverlay(renderer, snapshot, x, y));
   }

   @Override
   public void drawStackOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countText) {
      ItemStack snapshot = stack.copy();
      this.deferVanillaGuiDraw(() -> super.drawStackOverlay(renderer, snapshot, x, y, countText));
   }

   public void drawItemBar(ItemStack var1, int var2, int var3) {
      ((DrawContextAccessor)this).callDrawItemBar(var1, var2, var3);
   }

   public void drawCooldownProgress(ItemStack var1, int var2, int var3) {
      ((DrawContextAccessor)this).callDrawCooldownProgress(var1, var2, var3);
   }

   public void pushMatrix() {
      this.getMatrices().pushMatrix();
   }

   public void popMatrix() {
      this.getMatrices().popMatrix();
   }
}
