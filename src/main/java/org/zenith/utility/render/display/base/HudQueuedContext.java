package org.zenith.utility.render.display.base;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix3x2f;
import org.zenith.base.font.Font;
import org.zenith.core.HudPreviewItem;
import org.zenith.core.HudPreviewType;
import org.zenith.render.GuiMatrixAdapter;
import org.zenith.render.HudPreviewRenderQueue;
import org.zenith.util.ArgbColor;

/**
 * Records HUD drawing in the preview queue while preserving the current GUI
 * transform. The original decompiled implementation duplicated almost every
 * DrawContext overload and encoded its arguments into numbered fields; this
 * version stores executable commands instead.
 */
public final class HudQueuedContext extends HudDrawContext {
   private boolean replaying;

   public HudQueuedContext(HudDrawContext source) {
      super(source, source.getMouseX(), source.getMouseY(), source.getDelta());
   }

   public static HudQueuedContext of(HudDrawContext context) {
      return context instanceof HudQueuedContext queued ? queued : new HudQueuedContext(context);
   }

   private void queueContent(Runnable draw) {
      Matrix3x2f matrix = new Matrix3x2f(this.getMatrices());
      HudPreviewRenderQueue.NbtItemSpec(() -> this.replay(matrix, draw));
   }

   private void queueBackground(Runnable draw) {
      Matrix3x2f matrix = new Matrix3x2f(this.getMatrices());
      HudPreviewRenderQueue.ItemServiceBase(() -> this.replay(matrix, draw));
   }

   private void replay(Matrix3x2f matrix, Runnable draw) {
      Matrix3x2f previous = new Matrix3x2f(this.getMatrices());
      boolean wasReplaying = this.replaying;
      this.getMatrices().set(matrix);
      this.replaying = true;
      try {
         draw.run();
      } finally {
         this.replaying = wasReplaying;
         this.getMatrices().set(previous);
      }
   }

   /** Compatibility for old queue entries used only by scissor boundaries. */
   public void replayCommand(HudPreviewItem command) {
      Matrix3x2f matrix = new Matrix3x2f();
      matrix.set(command.matrix4f11.m00(), command.matrix4f11.m01(), command.matrix4f11.m10(), command.matrix4f11.m11(), command.matrix4f11.m30(), command.matrix4f11.m31());
      this.replay(matrix, () -> {
         if (command.var13Var159 == HudPreviewType.val244) {
            super.enableScissor(command.int350, command.int351, command.int352, command.int353);
         } else if (command.var13Var159 == HudPreviewType.val245) {
            super.disableScissor();
         }
      });
   }

   @Override
   public void drawText(Font font, String text, float x, float y, ArgbColor color) {
      if (this.replaying) super.drawText(font, text, x, y, color);
      else this.queueContent(() -> super.drawText(font, text, x, y, color));
   }

   @Override
   public void drawText(Font font, String text, float x, float y, ArgbColor color, boolean fade, float fadeStart, float fadeEnd, float maxWidth) {
      if (this.replaying) super.drawText(font, text, x, y, color, fade, fadeStart, fadeEnd, maxWidth);
      else this.queueContent(() -> super.drawText(font, text, x, y, color, fade, fadeStart, fadeEnd, maxWidth));
   }

   @Override
   public void drawText(Font font, String text, float x, float y, int color, boolean fade, float fadeStart, float fadeEnd, float maxWidth) {
      if (this.replaying) super.drawText(font, text, x, y, color, fade, fadeStart, fadeEnd, maxWidth);
      else this.queueContent(() -> super.drawText(font, text, x, y, color, fade, fadeStart, fadeEnd, maxWidth));
   }

   @Override
   public void drawText(Font font, String text, float x, float y, GradientRadius color) {
      if (this.replaying) super.drawText(font, text, x, y, color);
      else this.queueContent(() -> super.drawText(font, text, x, y, color));
   }

   @Override
   public void drawText(Font font, Text text, float x, float y, int color) {
      if (this.replaying) super.drawText(font, text, x, y, color);
      else this.queueContent(() -> super.drawText(font, text, x, y, color));
   }

   @Override
   public void drawText(Font font, Text text, float x, float y) {
      if (this.replaying) super.drawText(font, text, x, y);
      else this.queueContent(() -> super.drawText(font, text, x, y));
   }

   @Override
   public void drawTextWithShadow(TextRenderer renderer, String text, int x, int y, int color) {
      if (this.replaying) super.drawTextWithShadow(renderer, text, x, y, color);
      else this.queueContent(() -> super.drawTextWithShadow(renderer, text, x, y, color));
   }

   @Override
   public void drawText(TextRenderer renderer, String text, int x, int y, int color, boolean shadow) {
      if (this.replaying) super.drawText(renderer, text, x, y, color, shadow);
      else this.queueContent(() -> super.drawText(renderer, text, x, y, color, shadow));
   }

   @Override
   public void drawTextWithShadow(TextRenderer renderer, OrderedText text, int x, int y, int color) {
      if (this.replaying) super.drawTextWithShadow(renderer, text, x, y, color);
      else this.queueContent(() -> super.drawTextWithShadow(renderer, text, x, y, color));
   }

   @Override
   public void drawText(TextRenderer renderer, OrderedText text, int x, int y, int color, boolean shadow) {
      if (this.replaying) super.drawText(renderer, text, x, y, color, shadow);
      else this.queueContent(() -> super.drawText(renderer, text, x, y, color, shadow));
   }

   @Override
   public void drawTextWithShadow(TextRenderer renderer, Text text, int x, int y, int color) {
      if (this.replaying) super.drawTextWithShadow(renderer, text, x, y, color);
      else this.queueContent(() -> super.drawTextWithShadow(renderer, text, x, y, color));
   }

   @Override
   public void drawText(TextRenderer renderer, Text text, int x, int y, int color, boolean shadow) {
      if (this.replaying) super.drawText(renderer, text, x, y, color, shadow);
      else this.queueContent(() -> super.drawText(renderer, text, x, y, color, shadow));
   }

   @Override
   public void drawWrappedText(TextRenderer renderer, StringVisitable text, int x, int y, int width, int color, boolean shadow) {
      if (this.replaying) super.drawWrappedText(renderer, text, x, y, width, color, shadow);
      else this.queueContent(() -> super.drawWrappedText(renderer, text, x, y, width, color, shadow));
   }

   @Override
   public void drawRoundedRect(float x, float y, float width, float height, CornerRadius radius, ArgbColor color) {
      if (this.replaying) super.drawRoundedRect(x, y, width, height, radius, color);
      else HudPreviewRenderQueue.ItemRegistry(GuiMatrixAdapter.toMatrix4f(this.getMatrices()), x, y, width, height, radius, color);
   }

   @Override
   public void drawRect(float x, float y, float width, float height, ArgbColor color) {
      if (this.replaying) super.drawRect(x, y, width, height, color);
      else HudPreviewRenderQueue.UiAnimation(GuiMatrixAdapter.toMatrix4f(this.getMatrices()), x, y, width, height, color);
   }

   @Override
   public void drawBlurHudBooleanCheck(float x, float y, float width, float height, float blurRadius, CornerRadius radius, ArgbColor color, boolean blur, boolean background) {
      if (this.replaying) super.drawBlurHudBooleanCheck(x, y, width, height, blurRadius, radius, color, blur, background);
      else HudPreviewRenderQueue.on23(GuiMatrixAdapter.toMatrix4f(this.getMatrices()), x, y, width, height, blurRadius, radius, color, blur, background);
   }

   @Override
   public void drawSquircle(float x, float y, float width, float height, float smoothness, CornerRadius radius, ArgbColor color) {
      if (this.replaying) super.drawSquircle(x, y, width, height, smoothness, radius, color);
      else this.queueContent(() -> super.drawSquircle(x, y, width, height, smoothness, radius, color));
   }

   @Override
   public void drawRoundedRect(float x, float y, float width, float height, CornerRadius radius, GradientRadius color) {
      if (this.replaying) super.drawRoundedRect(x, y, width, height, radius, color);
      else this.queueContent(() -> super.drawRoundedRect(x, y, width, height, radius, color));
   }

   @Override
   public void drawItem(ItemStack stack, int x, int y) {
      if (this.replaying) super.drawItem(stack, x, y);
      else this.queueContent(() -> super.drawItem(stack, x, y));
   }

   @Override
   public void drawItem(ItemStack stack, int x, int y, int seed) {
      if (this.replaying) super.drawItem(stack, x, y, seed);
      else this.queueContent(() -> super.drawItem(stack, x, y, seed));
   }

   @Override
   public void drawItemWithoutEntity(ItemStack stack, int x, int y) {
      if (this.replaying) super.drawItemWithoutEntity(stack, x, y);
      else this.queueContent(() -> super.drawItemWithoutEntity(stack, x, y));
   }

   @Override
   public void drawItemWithoutEntity(ItemStack stack, int x, int y, int seed) {
      if (this.replaying) super.drawItemWithoutEntity(stack, x, y, seed);
      else this.queueContent(() -> super.drawItemWithoutEntity(stack, x, y, seed));
   }

   @Override
   public void drawItem(LivingEntity entity, ItemStack stack, int x, int y, int seed) {
      if (this.replaying) super.drawItem(entity, stack, x, y, seed);
      else this.queueContent(() -> super.drawItem(entity, stack, x, y, seed));
   }

   @Override
   public void drawStackOverlay(TextRenderer renderer, ItemStack stack, int x, int y) {
      if (this.replaying) super.drawStackOverlay(renderer, stack, x, y);
      else this.queueContent(() -> super.drawStackOverlay(renderer, stack, x, y));
   }

   @Override
   public void drawStackOverlay(TextRenderer renderer, ItemStack stack, int x, int y, String countText) {
      if (this.replaying) super.drawStackOverlay(renderer, stack, x, y, countText);
      else this.queueContent(() -> super.drawStackOverlay(renderer, stack, x, y, countText));
   }

   @Override
   public void fill(int x1, int y1, int x2, int y2, int color) {
      if (this.replaying) super.fill(x1, y1, x2, y2, color);
      else this.queueContent(() -> super.fill(x1, y1, x2, y2, color));
   }

   @Override
   public void fill(RenderPipeline pipeline, int x1, int y1, int x2, int y2, int color) {
      if (this.replaying) super.fill(pipeline, x1, y1, x2, y2, color);
      else this.queueContent(() -> super.fill(pipeline, x1, y1, x2, y2, color));
   }

   @Override
   public void fillGradient(int x1, int y1, int x2, int y2, int colorStart, int colorEnd) {
      if (this.replaying) super.fillGradient(x1, y1, x2, y2, colorStart, colorEnd);
      else this.queueContent(() -> super.fillGradient(x1, y1, x2, y2, colorStart, colorEnd));
   }

   @Override
   public void drawSpriteStretched(RenderPipeline pipeline, Sprite sprite, int x, int y, int width, int height) {
      if (this.replaying) super.drawSpriteStretched(pipeline, sprite, x, y, width, height);
      else this.queueContent(() -> super.drawSpriteStretched(pipeline, sprite, x, y, width, height));
   }

   @Override
   public void drawSpriteStretched(RenderPipeline pipeline, Sprite sprite, int x, int y, int width, int height, int color) {
      if (this.replaying) super.drawSpriteStretched(pipeline, sprite, x, y, width, height, color);
      else this.queueContent(() -> super.drawSpriteStretched(pipeline, sprite, x, y, width, height, color));
   }

   @Override
   public void drawGuiTexture(RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
      if (this.replaying) super.drawGuiTexture(pipeline, sprite, x, y, width, height);
      else this.queueContent(() -> super.drawGuiTexture(pipeline, sprite, x, y, width, height));
   }

   @Override
   public void drawGuiTexture(RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height, int color) {
      if (this.replaying) super.drawGuiTexture(pipeline, sprite, x, y, width, height, color);
      else this.queueContent(() -> super.drawGuiTexture(pipeline, sprite, x, y, width, height, color));
   }

   @Override
   public void drawTexture(RenderPipeline pipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight) {
      if (this.replaying) super.drawTexture(pipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight);
      else this.queueContent(() -> super.drawTexture(pipeline, texture, x, y, u, v, width, height, textureWidth, textureHeight));
   }

   @Override
   public void drawTexture(RenderPipeline pipeline, Identifier texture, int x, int y, float u, float v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight, int color) {
      if (this.replaying) super.drawTexture(pipeline, texture, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight, color);
      else this.queueContent(() -> super.drawTexture(pipeline, texture, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight, color));
   }

   @Override
   public void enableScissor(int x1, int y1, int x2, int y2) {
      if (this.replaying) super.enableScissor(x1, y1, x2, y2);
      else {
         this.queueBackground(() -> super.enableScissor(x1, y1, x2, y2));
         this.queueContent(() -> super.enableScissor(x1, y1, x2, y2));
      }
   }

   @Override
   public void disableScissor() {
      if (this.replaying) super.disableScissor();
      else {
         this.queueBackground(super::disableScissor);
         this.queueContent(super::disableScissor);
      }
   }
}
