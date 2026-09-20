package org.zenith.client.screens.nlgui.elements;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.Element;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiButtonSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiColorSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiItemSelectSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiKeySetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiModeSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiMultiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiNumberSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiStringSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiWindowSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.TextSetting;
import org.zenith.setting.SettingGroup;
import org.zenith.setting.ColorSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.StringListSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.setting.Setting;
import org.zenith.setting.ButtonSetting;
import org.zenith.setting.KeySetting;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.GradientRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiStyleElement extends Element {
   public static final float HEADER_HEIGHT = 23.0F;
   public static final int HEADER_SETTINGS_COUNT = 2;
   public final ZenithStyle style;
   public final UiAnimation animationEnable = new UiAnimation(200L, Easing.CloseScreenEvent);
   public final UiAnimation expandedAnimation = new UiAnimation(200L, Easing.CloseScreenEvent);
   public final UiAnimation animationPosX = new UiAnimation(150L, Easing.StopUsingItemEvent);
   public final UiAnimation animationPosY = new UiAnimation(150L, Easing.StopUsingItemEvent);
   public CornerRadiusF bounds;
   public boolean expanded;
   public boolean animated = false;
   public boolean positionInitialized = false;
   public float lastX;
   public float lastY;
   public int lastIndex;
   public final List<GuiSetting<?>> settings = new ArrayList<>();

   public void setPositionInitialized(boolean var1) {
      this.positionInitialized = var1;
   }

   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      if (!this.positionInitialized) {
         this.animationPosX.UiAnimation(var4);
         this.animationPosY.UiAnimation(var5);
         this.lastX = var4;
         this.lastY = var5;
         this.positionInitialized = true;
         this.lastIndex = var7;
      } else if ((var4 != this.lastX || var5 != this.lastY) && var7 != this.lastIndex) {
         this.animated = true;
         this.animationPosX.Easing(var4);
         this.animationPosY.Easing(var5);
         this.lastX = var4;
         this.lastY = var5;
      }

      if (this.animated) {
         var4 = this.animationPosX.on23(var4);
         var5 = this.animationPosY.on23(var5);
         if (this.animationPosX.isDone() && this.animationPosY.isDone()) {
            this.animated = false;
         }
      } else {
         this.animationPosX.UiAnimation(var4);
         this.animationPosY.UiAnimation(var5);
      }

      this.lastIndex = var7;
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         this.expandedAnimation.on23(this.expanded);
         float f = this.expandedAnimation.CancellableEvent();
         this.bounds = new CornerRadiusF(var4, var5, this.getWidth(), 23.0F);
         this.animationEnable.on23(this.style == zenithstyle);
         float f1 = this.bounds.width();
         float f2 = this.getHeight();
         var1.drawRoundedRectBatched(
            var4,
            var5,
            f1,
            f2,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getSurfaceDisableBackground().getColor().SprintStateEvent(var6)
         );
         var1.drawRoundedRectBatched(
            var4,
            var5,
            f1,
            23.0F,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getHeaderDisableBackground()
               .getColor()
               .Easing(zenithstyle.getSurfaceEnableBackground().getColor(), this.animationEnable.CancellableEvent())
               .SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         float f3 = 0.5F;
         float f4 = 2.0F;
         float f5 = 4.0F;
         var1.enableScissor(var4 + f5, var5 + 23.0F - 2.0F, var4 + f1 - f5, var5 + 23.0F + 10.0F);
         var1.drawRoundedBorder(
            var4,
            var5,
            f1,
            23.0F,
            0.15F,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            GradientRadius.on23(
               this.style.getPrimaryColor().getColor(var6),
               this.style.getPrimaryColor().getColor(var6),
               this.style.getSecondaryPrimaryColor().getColor(var6),
               this.style.getSecondaryPrimaryColor().getColor(var6)
            )
         );
         var1.disableScissor();
         f5 = var4 + GuiStyle.PADDING * 2 + MathHelper.lerp(this.animationEnable.CancellableEvent(), 0, 5 + GuiStyle.PADDING);
         Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
         Font font1 = Fonts.NEW_ICONS.getFont(6.0F);
         var1.drawText(
            font,
            this.style.getName(),
            f5,
            var5 + (23.0F - font.height()) / 2.0F,
            zenithstyle.getTextSecondary().getColor().Easing(zenithstyle.getTextEnable().getColor(), this.animationEnable.CancellableEvent()).SprintStateEvent(var6)
         );
         var1.drawText(
            font1,
            "`",
            var4 + GuiStyle.PADDING * 2,
            var5 + (23.0F - font1.height()) / 2.0F,
            ArgbColor.var11941.Easing(zenithstyle.getPrimaryColor().getColor(), this.animationEnable.CancellableEvent()).SprintStateEvent(var6)
         );
         this.renderHeaderSettings(var1, var2, var3, var4, var5, var6);

         try {
            if (f <= 0.01F) {
               return;
            }

            float f6 = var5 + 23.0F;
            float f7 = this.getExpandedSettingsHeight() * f;
            float f8 = var4 + GuiStyle.PADDING * 2;
            float f9 = var5 + 23.0F + GuiStyle.PADDING * 2;
            float f10 = GuiStyle.PADDING * 2;
            float f11 = (float)(MathHelper.lerp(this.animationEnable.CancellableEvent(), 0.5, 1.0) * var6);
            var1.enableScissor(var4, f6, var4 + f1, f6 + f7);

            for (int i = 2; i < this.settings.size(); i++) {
               GuiSetting guisetting = this.settings.get(i);
               if (guisetting.isVisible()) {
                  float f12 = guisetting.getAnimHeight() + 6.0F * guisetting.getVisibleProgress();
                  float f13 = MathHelper.clamp((f7 - f10) / Math.max(f12, 1.0E-4F), 0.0F, 1.0F);
                  if (f13 > 0.0F) {
                     guisetting.render(var1, var2, var3, f8, f9, f11 * f13);
                  }

                  f9 += f12;
                  f10 += f12;
               }
            }

            var1.disableScissor();
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      boolean flag = false;

      for (int i = 0; i < this.settings.size(); i++) {
         GuiSetting guisetting = this.settings.get(i);
         if (guisetting.isVisible() && guisetting.onMousePriorityClicked(var1, var3, var5)) {
            flag = true;
         }
      }

      return flag;
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      for (int i = Math.min(2, this.settings.size()) - 1; i >= 0; i--) {
         GuiSetting guisetting = this.settings.get(i);
         if (guisetting.isVisible() && guisetting.onMouseClicked(var1, var3, var5)) {
            return true;
         }
      }

      if (this.bounds != null && this.bounds.PotionItemBuilder(var1, var3)) {
         if (var5 == MenuScreenId.call004) {
            ZenithClient.on23().TextScanner().setCurrentStyle(this.style);
         } else if (var5 == MenuScreenId.call111) {
            this.expanded = !this.expanded;
         }

         return true;
      } else {
         if (this.expandedAnimation.CancellableEvent() <= 0.01F) {
            return false;
         }

         for (int j = 2; j < this.settings.size(); j++) {
            GuiSetting guisetting1 = this.settings.get(j);
            if (guisetting1.isVisible() && guisetting1.onMouseClicked(var1, var3, var5)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      for (int i = 0; i < Math.min(2, this.settings.size()); i++) {
         GuiSetting guisetting = this.settings.get(i);
         if (guisetting.isVisible() && guisetting.onMousePriorityScroll(var1, var3, var5, var7)) {
            return true;
         }
      }

      if (this.expandedAnimation.CancellableEvent() <= 0.01F) {
         return false;
      }

      boolean flag = false;

      for (int j = 2; j < this.settings.size(); j++) {
         GuiSetting guisetting1 = this.settings.get(j);
         if (guisetting1.isVisible() && guisetting1.onMousePriorityScroll(var1, var3, var5, var7)) {
            flag = true;
         }
      }

      return flag;
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      for (int i = 0; i < Math.min(2, this.settings.size()); i++) {
         this.settings.get(i).onMouseReleased(var1, var3, var5);
      }

      if (this.expandedAnimation.CancellableEvent() <= 0.01F) {
         super.onMouseReleased(var1, var3, var5);
      } else {
         for (int j = 2; j < this.settings.size(); j++) {
            GuiSetting guisetting = this.settings.get(j);
            guisetting.onMouseReleased(var1, var3, var5);
         }

         super.onMouseReleased(var1, var3, var5);
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      for (int i = 0; i < Math.min(2, this.settings.size()); i++) {
         GuiSetting guisetting = this.settings.get(i);
         if (guisetting.isVisible() && guisetting.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      if (this.expandedAnimation.CancellableEvent() <= 0.01F) {
         return super.keyPressed(var1, var2, var3);
      }

      for (int j = 2; j < this.settings.size(); j++) {
         GuiSetting guisetting1 = this.settings.get(j);
         if (guisetting1.isVisible() && guisetting1.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      for (int i = 0; i < Math.min(2, this.settings.size()); i++) {
         GuiSetting guisetting = this.settings.get(i);
         if (guisetting.isVisible() && guisetting.charTyped(var1, var2)) {
            return true;
         }
      }

      if (this.expandedAnimation.CancellableEvent() <= 0.01F) {
         return super.charTyped(var1, var2);
      }

      for (int j = 2; j < this.settings.size(); j++) {
         GuiSetting guisetting1 = this.settings.get(j);
         if (guisetting1.isVisible() && guisetting1.charTyped(var1, var2)) {
            return true;
         }
      }

      return super.charTyped(var1, var2);
   }

   public float getExpandedSettingsHeight() {
      if (!this.hasSettings()) {
         return 0.0F;
      }

      float f = 0.0F;
      int i = 0;

      for (int j = 2; j < this.settings.size(); j++) {
         GuiSetting guisetting = this.settings.get(j);
         if (guisetting.isVisible()) {
            f += guisetting.getAnimHeight() + 6.0F * guisetting.getVisibleProgress();
            i++;
         }
      }

      return i == 0 ? 0.0F : GuiStyle.PADDING * 2 + f - 6.0F + GuiStyle.PADDING * 2;
   }

   public void renderHeaderSettings(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float f = var4 + GuiStyle.PADDING * 2;
      float f1 = var5 + (23.0F - this.settings.getFirst().getHeight()) / 2.0F;
      var1.enableScissor(var4 + 150.0F, 0.0F, MinecraftClient.getInstance().getWindow().getScaledWidth(), MinecraftClient.getInstance().getWindow().getScaledHeight());
      GuiSetting guisetting = this.settings.get(0);
      guisetting.render(var1, var2, var3, f - 10.0F, f1, var6);
      guisetting = this.settings.get(1);
      guisetting.render(var1, var2, var3, f, f1, var6);
      var1.disableScissor();
   }

   public void renderHeaderSettingsPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float f = (float)(MathHelper.lerp(this.animationEnable.CancellableEvent(), 0.5, 1.0) * var6);
      float f1 = var4 + GuiStyle.PADDING * 2;
      float f2 = var5 + 8.0F;

      for (int i = 0; i < Math.min(2, this.settings.size()); i++) {
         GuiSetting guisetting = this.settings.get(i);
         if (guisetting.isVisible()) {
            guisetting.renderPriority(var1, var2, var3, f1, f2, var6, f);
         }
      }
   }

   public ZenithStyle getStyle() {
      return this.style;
   }

   public GuiStyleElement(ZenithStyle var1) {
      this.style = var1;
      float f = this.getWidth() - GuiStyle.PADDING * 4;

      for (Setting l1illl1lllllll1l1l1l1ili11l1 : var1.getSettings()) {
         if (l1illl1lllllll1l1l1l1ili11l1 instanceof NumberSetting lilliiill11llilll1ll1l) {
            this.settings.add(new GuiNumberSetting(lilliiill11llilll1ll1l, f));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof ModeSetting ill11ii1ilil1liili1iliil) {
            this.settings.add(new GuiModeSetting(ill11ii1ilil1liili1iliil, f));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof MultiSelectSetting i1i1lll1liii1il1llll1) {
            this.settings.add(new GuiMultiBooleanSetting(i1i1lll1liii1il1llll1, f));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof BooleanSetting iili1iilllliiil) {
            this.settings.add(new GuiBooleanSetting(iili1iilllliiil, f));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof ColorSetting llliili1l1ii11i1lii1) {
            this.settings.add(new GuiColorSetting(llliili1l1ii11i1lii1, f));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof ButtonSetting ilii1liilllilll11iil11lil1ll) {
            this.settings.add(new GuiButtonSetting(ilii1liilllilll11iil11lil1ll, f));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof StringListSetting ii1lllil11i1) {
            this.settings.add(new GuiItemSelectSetting(ii1lllil11i1, f));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof KeySetting l1ll111iiil) {
            this.settings.add(new GuiKeySetting(l1ll111iiil, f));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof TextSetting i1ll1llliii11l1) {
            this.settings.add(new GuiStringSetting(i1ll1llliii11l1, f));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof SettingGroup l1lili1ii11) {
            this.settings.add(new GuiWindowSetting(l1lili1ii11, f));
         }
      }
   }

   @Override
   public String getName() {
      return this.style.getName();
   }

   @Override
   public float getHeight() {
      this.expandedAnimation.on23(this.expanded);
      float f = this.expandedAnimation.CancellableEvent();
      return 23.0F + this.getExpandedSettingsHeight() * f;
   }

   public boolean hasSettings() {
      return !this.settings.isEmpty() && this.settings.stream().anyMatch(GuiSetting::isVisible);
   }

   @Override
   public float getWidth() {
      return 178.0F;
   }

   public boolean isPriority() {
      return this.expandedAnimation.CancellableEvent() > 0.01F;
   }

   public boolean isEnable() {
      return ZenithClient.on23().TextScanner().getCurrentStyle() == this.style;
   }

   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      this.expandedAnimation.on23(this.expanded);
      float f = this.expandedAnimation.CancellableEvent();
      this.renderHeaderSettingsPriority(var1, var2, var3, var4, var5, var6);
      if (!(f <= 0.01F)) {
         float f1 = var5 + 23.0F;
         float f2 = this.getExpandedSettingsHeight() * f;
         float f3 = var4 + GuiStyle.PADDING * 2;
         float f4 = var5 + 23.0F + GuiStyle.PADDING * 2;
         float f5 = GuiStyle.PADDING * 2;
         float f6 = (float)(MathHelper.lerp(this.animationEnable.CancellableEvent(), 0.5, 1.0) * var6);

         for (int i = 2; i < this.settings.size(); i++) {
            GuiSetting guisetting = this.settings.get(i);
            if (guisetting.isVisible()) {
               float f7 = guisetting.getAnimHeight() + 6.0F * guisetting.getVisibleProgress();
               float f8 = MathHelper.clamp((f2 - f5) / Math.max(f7, 1.0E-4F), 0.0F, 1.0F);
               guisetting.renderPriority(var1, var2, var3, f3, f4, var6, f6);
               f4 += f7;
               f5 += f7;
            }
         }
      }
   }
}
