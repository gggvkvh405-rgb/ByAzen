package org.zenith.client.screens.nlgui.elements;

import java.util.ArrayList;
import java.util.List;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.InterfaceElement;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.MenuScreenId;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class InterfaceStyleContainerElement extends InterfaceElement {
   public static final float CONTENT_VIEW_HEIGHT = 120.0F;
   public static final float SCROLL_SPEED = 22.0F;
   public static final float SCROLL_SMOOTH = 0.25F;
   public final List<GuiStyleElement> styleElements;
   public CornerRadiusF bounds;
   public CornerRadiusF contentBounds;
   public float scroll = 0.0F;
   public float scrollTarget = 0.0F;

   public InterfaceStyleContainerElement() {
      this.styleElements = new ArrayList<>();

      for (ZenithStyle zenithstyle : ZenithClient.on23().TextScanner().getStyles()) {
         this.styleElements.add(new GuiStyleElement(zenithstyle));
      }
   }

   @Override
   public String getName() {
      return "";
   }

   @Override
   public float getHeight() {
      return 23.0F + GuiStyle.PADDING * 2 + 168.0F + GuiStyle.PADDING * 2;
   }

   @Override
   public float getWidth() {
      return 368.0F;
   }

   @Override
   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      if (!this.styleElements.isEmpty()) {
         float f = var4 + GuiStyle.PADDING.intValue();
         float f1 = var5 + 23.0F + GuiStyle.PADDING * 2 + GuiStyle.PADDING.intValue() + this.scroll;
         float f2 = this.getWidth() - GuiStyle.PADDING.intValue() * 2.0F;
         float f3 = GuiStyle.PADDING.intValue();
         float f4 = (f2 - f3) / 2.0F;
         float[] afloat = new float[]{0.0F, 0.0F};

         for (int i = 0; i < this.styleElements.size(); i++) {
            GuiStyleElement guistyleelement = this.styleElements.get(i);
            int j = afloat[0] <= afloat[1] ? 0 : 1;
            float f5 = f + j * (f4 + f3);
            float f6 = f1 + afloat[j];
            guistyleelement.renderPriority(var1, var2, var3, f5, f6, var6);
            afloat[j] += guistyleelement.getHeight() + f3;
         }
      }
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         this.bounds = new CornerRadiusF(var4, var5, this.getWidth(), 23.0F);
         float f = this.bounds.width();
         float f1 = this.getHeight();
         var1.drawRoundedRect(
            var4,
            var5,
            f,
            f1,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getSurfaceDisableBackground().getColor().SprintStateEvent(var6)
         );
         var1.drawRoundedRect(
            var4,
            var5,
            f,
            23.0F,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getHeaderDisableBackground().getColor().Easing(zenithstyle.getSurfaceEnableBackground().getColor(), 1.0F).SprintStateEvent(var6)
         );
         float f2 = var4 + GuiStyle.PADDING * 2 + 5.0F + GuiStyle.PADDING.intValue();
         Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
         Font font1 = Fonts.NEW_ICONS.getFont(6.0F);
         var1.drawText(
            font,
            "Styles",
            f2,
            var5 + (23.0F - font.height()) / 2.0F,
            zenithstyle.getTextSecondary().getColor().Easing(zenithstyle.getTextEnable().getColor(), 1.0F).SprintStateEvent(var6)
         );
         var1.drawText(
            font1,
            "O",
            var4 + GuiStyle.PADDING * 2,
            var5 + (23.0F - font1.height()) / 2.0F,
            ArgbColor.var11941.Easing(zenithstyle.getPrimaryColor().getColor(), 1.0F).SprintStateEvent(var6)
         );
         this.contentBounds = new CornerRadiusF(
            var4 + GuiStyle.PADDING.intValue(),
            var5 + 23.0F + GuiStyle.PADDING * 2 + GuiStyle.PADDING.intValue(),
            f - GuiStyle.PADDING.intValue() * 2.0F,
            168.0F
         );
         float f3 = this.getContentHeight();
         this.clampScroll(f3, this.contentBounds.height());
         this.scroll = this.scroll + Math.round((this.scrollTarget - this.scroll) * 0.25F);

         try {
            float f4 = this.contentBounds.x();
            float f5 = Math.round(this.contentBounds.y() + this.scroll);
            float f6 = GuiStyle.PADDING.intValue();
            float f7 = (this.contentBounds.width() - f6) / 2.0F;
            float[] afloat = new float[]{0.0F, 0.0F};
            var1.enableScissor(
               this.contentBounds.x(),
               this.contentBounds.y(),
               this.contentBounds.x() + this.contentBounds.width() + GuiStyle.PADDING.intValue(),
               this.contentBounds.y() + this.contentBounds.height()
            );

            for (int i = 0; i < this.styleElements.size(); i++) {
               GuiStyleElement guistyleelement = this.styleElements.get(i);
               int j = afloat[0] <= afloat[1] ? 0 : 1;
               float f8 = f4 + j * (f7 + f6);
               float f9 = f5 + afloat[j];
               guistyleelement.render(var1, var2, var3, f8, f9, var6, i + j);
               afloat[j] += guistyleelement.getHeight() + f6;
            }

            var1.disableScissor();
            this.renderScrollIndicator(var1, zenithstyle, var6, f3);
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      boolean flag = false;

      for (GuiStyleElement guistyleelement : this.styleElements) {
         if (guistyleelement.onMousePriorityClicked(var1, var3, var5)) {
            flag = true;
         }
      }

      return flag;
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.bounds != null && this.bounds.PotionItemBuilder(var1, var3)) {
         return true;
      }

      for (GuiStyleElement guistyleelement : this.styleElements) {
         if (guistyleelement.onMouseClicked(var1, var3, var5)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      boolean flag = false;

      for (GuiStyleElement guistyleelement : this.styleElements) {
         if (guistyleelement.mouseScrolled(var1, var3, var5, var7)) {
            flag = true;
         }
      }

      if (flag) {
         return true;
      }

      if (this.contentBounds != null && this.contentBounds.PotionItemBuilder(var1, var3)) {
         float f = this.getContentHeight();
         if (f <= this.contentBounds.height()) {
            return false;
         }

         this.scrollTarget += (float)var7 * 22.0F;
         this.clampScroll(f, this.contentBounds.height());
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      for (GuiStyleElement guistyleelement : this.styleElements) {
         guistyleelement.onMouseReleased(var1, var3, var5);
      }

      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      for (GuiStyleElement guistyleelement : this.styleElements) {
         if (guistyleelement.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      for (GuiStyleElement guistyleelement : this.styleElements) {
         if (guistyleelement.charTyped(var1, var2)) {
            return true;
         }
      }

      return super.charTyped(var1, var2);
   }

   public void clampScroll(float var1, float var2) {
      if (var1 <= var2) {
         this.scrollTarget = 0.0F;
         this.scroll = 0.0F;
      } else {
         float f = var2 - var1;
         if (this.scrollTarget < f) {
            this.scrollTarget = f;
         }

         if (this.scrollTarget > 0.0F) {
            this.scrollTarget = 0.0F;
         }

         if (this.scroll < f) {
            this.scroll = f;
         }

         if (this.scroll > 0.0F) {
            this.scroll = 0.0F;
         }
      }
   }

   public float getContentHeight() {
      if (this.styleElements.isEmpty()) {
         return 0.0F;
      }

      float f = GuiStyle.PADDING.intValue();
      float[] afloat = new float[]{0.0F, 0.0F};

      for (GuiStyleElement guistyleelement : this.styleElements) {
         int i = afloat[0] <= afloat[1] ? 0 : 1;
         afloat[i] += guistyleelement.getHeight() + f;
      }

      float f1 = Math.max(afloat[0], afloat[1]);
      return Math.max(0.0F, f1 - f);
   }

   public void renderScrollIndicator(HudDrawContext var1, ZenithStyle var2, float var3, float var4) {
   }
}
