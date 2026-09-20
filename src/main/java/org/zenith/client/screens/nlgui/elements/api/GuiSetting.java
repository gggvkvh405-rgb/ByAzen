package org.zenith.client.screens.nlgui.elements.api;

import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.base.font.MsdfRenderer;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.setting.Setting;
import org.zenith.util.ArgbColor;
import org.zenith.util.MathUtils;
import org.zenith.utility.render.display.base.HudDrawContext;

public abstract class GuiSetting<T extends Setting> extends Element {
   public static final float MIN_DESC_SCROLL_SPEED = 0.028F;
   public static final float MAX_DESC_SCROLL_SPEED = 0.065F;
   public static final float TARGET_DESC_CHARS_PER_SECOND = 12.0F;
   protected final float width;
   protected final T setting;
   protected final UiAnimation animationVisible;
   public float scrollOffset = 0.0F;
   public int scrollDirection = 1;
   public long hoverStartTime = -1L;
   public long lastUpdateTime = -1L;
   public long pauseUntilTime = -1L;
   public boolean continueScroll = false;

   protected void drawDefault(
      HudDrawContext var1,
      float var2,
      float var3,
      String var4,
      String var5,
      String var6,
      Font var7,
      Font var8,
      float var9,
      float var10,
      float var11,
      ArgbColor var12,
      ArgbColor var13,
      ArgbColor var14
   ) {
      Font font = Fonts.NEW_ICONS.getFont(var7.getSize() - 0.5F);
      boolean flag = ZenithClient.on23().NbtEditor().isRenderIcon();
      float f = this.getHeight();
      float f1 = flag ? font.width(var4) + GuiStyle.PADDING.intValue() / 2.0F : 0.0F;
      if (this.isShort()) {
         if (flag) {
            var1.drawText(font, var4, var9, var10 + (f - font.height()) / 2.0F, var14);
         }

         var1.drawText(var7, var5, var9 + f1, var10 + (f - var7.height()) / 2.0F, var12);
      } else {
         if (flag) {
            var1.drawText(font, var4, var9, var10 + (font.height() / 2.0F - (var4.equals("u") ? 0.0F : 0.2F)), var14);
         }

         var1.drawText(var7, var5, var9 + f1, var10 + 1.1F, var12);
         float f2 = var10 + f - var8.height() - 1.0F;
         float f3 = var8.width(var6);
         float f4 = f3 - var11;
         boolean flag1 = MathUtils.on23(var2, var3, var9, f2 - 3.0F, var11 + 1.0F, var8.height() + 10.0F);
         if (f4 <= 1.0F) {
            var1.drawText(var8, var6, var9, f2, var13);
            this.resetDescScroll();
         } else {
            var1.enableScissor(var9 - 0.5F, f2, var9 + var11 + 2.0F, f2 + var8.height() + 4.0F);
            long i = System.currentTimeMillis();
            if (this.lastUpdateTime == -1L) {
               this.lastUpdateTime = i;
            }

            float f5 = (float)(i - this.lastUpdateTime);
            this.lastUpdateTime = i;
            if (flag1) {
               if (this.hoverStartTime == -1L) {
                  this.hoverStartTime = i;
                  this.pauseUntilTime = i + 300L;
               }

               this.continueScroll = true;
            } else if (this.continueScroll && this.scrollOffset <= 0.0F && this.scrollDirection == 1) {
               this.continueScroll = false;
            }

            if (!flag1 && !this.continueScroll) {
               this.hoverStartTime = -1L;
               this.pauseUntilTime = -1L;
            } else if (this.pauseUntilTime == -1L || i >= this.pauseUntilTime) {
               this.pauseUntilTime = -1L;
               float f6 = f4 + 10.0F;
               float f7 = this.getDescScrollSpeed(f3, var6);
               this.scrollOffset = this.scrollOffset + this.scrollDirection * f7 * f5;
               if (this.scrollOffset >= f6) {
                  this.scrollOffset = f6;
                  this.scrollDirection = -1;
                  this.pauseUntilTime = i + 900L;
               } else if (this.scrollOffset <= 0.0F) {
                  this.scrollOffset = 0.0F;
                  this.scrollDirection = 1;
                  this.pauseUntilTime = i + 300L;
               }
            }

            float f9 = 10.0F;
            float f10 = var11 + this.scrollOffset;
            float f8 = f10 > 0.0F ? (var11 - f9 + this.scrollOffset) / f10 : 1.0F;
            MsdfRenderer.renderText(
               var8.getFont(),
               var6,
               var8.getSize(),
               var13.call001(),
               org.zenith.render.GuiMatrixAdapter.toMatrix4f(var1.getMatrices()),
               var9 - this.scrollOffset,
               f2,
               0.0F,
               true,
               f8,
               1.0F,
               f10
            );
            var1.disableScissor();
         }
      }
   }

   protected GuiSetting(float var1, T var2) {
      this.width = var1;
      this.setting = var2;
      this.animationVisible = new UiAnimation(200L, var2.isVisible() ? 1.0F : 0.0F, Easing.CloseScreenEvent);
   }

   @Override
   public boolean isVisible() {
      return this.animationVisible.CancellableEvent() != 0.0F || this.setting.isVisible();
   }

   @Override
   public float getHeight() {
      return this.isShort() ? 7.0F : 14.0F;
   }

   public float getVisibleProgress() {
      return this.animationVisible.CancellableEvent();
   }

   public float getAnimHeight() {
      return this.getHeight() * this.animationVisible.CancellableEvent();
   }

   public boolean isShort() {
      return this.setting.getDescription().isEmpty() || ZenithClient.on23().NbtEditor().isShortMode();
   }

   public boolean onMousePriorityScroll(double var1, double var3, double var5, double var7) {
      return false;
   }

   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      return false;
   }

   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
   }

   public float getDescScrollSpeed(float var1, String var2) {
      float f = var1 / Math.max(var2.length(), 1);
      float f1 = f * 12.0F / 1000.0F;
      return Math.max(0.028F, Math.min(f1, 0.065F));
   }

   public void resetDescScroll() {
      this.scrollOffset = 0.0F;
      this.scrollDirection = 1;
      this.hoverStartTime = -1L;
      this.lastUpdateTime = -1L;
      this.pauseUntilTime = -1L;
      this.continueScroll = false;
   }

   @Override
   public float getWidth() {
      return this.width;
   }

   public T getSetting() {
      return this.setting;
   }
}
