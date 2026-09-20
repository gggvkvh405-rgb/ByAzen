package org.zenith.client.screens.nlgui.elements.setting;

import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.setting.KeySetting;
import org.zenith.util.ArgbColor;
import org.zenith.util.ScoreboardUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiKeySetting extends GuiSetting<KeySetting> {
   public final UiAnimation animationEnable = new UiAnimation(200L, Easing.CloseScreenEvent);
   public CornerRadiusF bounds;
   public boolean binding;

   public GuiKeySetting(KeySetting var1) {
      super(166.0F, var1);
   }

   public GuiKeySetting(KeySetting var1, float var2) {
      super(var2, var1);
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.binding && var5.int203() >= 2) {
         this.setting.setKeyCode(var5.int203());
         this.binding = false;
         return true;
      } else if (this.bounds != null && this.bounds.PotionItemBuilder(var1, var3)) {
         this.binding = true;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.binding) {
         return super.keyPressed(var1, var2, var3);
      }

      if (var1 != 256 && var1 != 261 && var1 != 259) {
         this.setting.setKeyCode(var1);
      } else {
         this.setting.setKeyCode(-1);
      }

      this.binding = false;
      return true;
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      try {
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         if (zenithstyle == null) {
            return;
         }

         this.animationVisible.on23(this.setting.isVisible());
         var6 *= this.animationVisible.CancellableEvent();
         Font font = Fonts.NEW_MEDIUM.getFont(5.5F);
         Font font1 = Fonts.NEW_REGULAR.getFont(5.4F);
         float f = this.width / 1.4F - GuiStyle.PADDING.intValue();
         ArgbColor i11ii1llliilllii1i1 = zenithstyle.getTextEnable().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i11 = zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6);
         ArgbColor i11ii1llliilllii1i12 = zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6);
         this.drawDefault(
            var1,
            var2,
            var3,
            "N",
            this.setting.getName(),
            this.setting.getDescription(),
            font,
            font1,
            var4,
            var5,
            f,
            i11ii1llliilllii1i1,
            i11ii1llliilllii1i11,
            i11ii1llliilllii1i12
         );
         String s = this.getBindText();
         this.animationEnable.on23(this.binding || this.setting.getKeyCode() != -1);
         Font font2 = Fonts.NEW_MEDIUM.getFont(4.5F);
         Font font3 = Fonts.NEW_ICONS.getFont(4.2F);
         float f1 = font2.width(s);
         float f2 = GuiStyle.PADDING.intValue() / 2.0F + f1 + GuiStyle.PADDING.intValue() / 3.0F + 3.75F + GuiStyle.PADDING.intValue() / 2.0F;
         float f3 = 7.0F;
         float f4 = var4 + this.width - f2;
         float f5 = var5 + (this.getHeight() - f3) / 2.0F;
         this.bounds = new CornerRadiusF(f4, f5, f2, f3);
         var1.drawRoundedRectBatched(
            f4,
            f5,
            f2,
            f3,
            CornerRadius.MovementInputEvent(1.5F),
            zenithstyle.getDisableActiveBg()
               .getColor()
               .Easing(zenithstyle.getPrimaryColor().getColor().SprintStateEvent(0.15F), this.animationEnable.CancellableEvent())
               .SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         var1.drawText(
            font2,
            s,
            f4 + GuiStyle.PADDING.intValue() / 2.0F,
            f5 + (f3 - font2.height()) / 2.0F,
            zenithstyle.getTextTertiary().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), this.animationEnable.CancellableEvent()).SprintStateEvent(var6)
         );
         var1.drawText(
            font3,
            "N",
            f4 + GuiStyle.PADDING.intValue() / 2.0F + f1 + GuiStyle.PADDING.intValue() / 3.0F,
            f5 + GuiStyle.PADDING.intValue() / 2.0F,
            zenithstyle.getTextTertiary().getColor().Easing(zenithstyle.getPrimaryColor().getColor(), this.animationEnable.CancellableEvent()).SprintStateEvent(var6)
         );
      } catch (Exception exception) {
         exception.printStackTrace();
      }
   }

   public String getBindText() {
      if (this.binding) {
         return this.getBindingDots();
      }

      String s = "n/a";
      int i = this.setting.getKeyCode();
      if (i != -1 && i != 0) {
         try {
            String s1 = ScoreboardUtils.EventPosHook(i);
            if (s1 != null && !s1.isBlank()) {
               s = s1.toUpperCase();
            }
         } catch (Exception var4) {
         }
      }

      return s;
   }

   public String getBindingDots() {
      int i = (int)(System.currentTimeMillis() / 500L % 3L);
      if (i == 0) {
         return ".";
      } else {
         return i == 1 ? ".." : "...";
      }
   }
}
