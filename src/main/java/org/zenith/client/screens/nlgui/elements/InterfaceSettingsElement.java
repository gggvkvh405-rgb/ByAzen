package org.zenith.client.screens.nlgui.elements;

import java.util.ArrayList;
import java.util.List;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.elements.api.InterfaceElement;
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
import org.zenith.core.MenuScreenId;
import org.zenith.module.render.Interface;
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
import org.zenith.utility.render.display.base.HudDrawContext;

public class InterfaceSettingsElement extends InterfaceElement {
   public final List<GuiSetting<?>> settings = new ArrayList<>();
   public CornerRadiusF bounds;

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         this.bounds = new CornerRadiusF(var4, var5, this.getWidth(), 23.0F);
         float f = this.bounds.width();
         float f1 = this.getHeight();
         var1.drawRoundedRectBatched(
            var4,
            var5,
            f,
            f1,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getSurfaceDisableBackground().getColor().SprintStateEvent(var6)
         );
         var1.drawRoundedRectBatched(
            var4,
            var5,
            f,
            23.0F,
            CornerRadius.MovementInputEvent(GuiStyle.ROUND.intValue()),
            zenithstyle.getHeaderDisableBackground().getColor().Easing(zenithstyle.getSurfaceEnableBackground().getColor(), 1.0F).SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         float f2 = var4 + GuiStyle.PADDING * 2 + 5.0F + GuiStyle.PADDING.intValue();
         Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
         Font font1 = Fonts.NEW_ICONS.getFont(6.0F);
         var1.drawText(
            font,
            "Settings",
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

         try {
            float f3 = var4 + GuiStyle.PADDING * 2;
            float f4 = var5 + 23.0F + GuiStyle.PADDING * 2;

            for (GuiSetting guisetting : this.settings) {
               if (guisetting.isVisible()) {
                  guisetting.render(var1, var2, var3, f3, f4, var6);
                  f4 += guisetting.getAnimHeight() + 6.0F * guisetting.getVisibleProgress();
               }
            }
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
   }

   public InterfaceSettingsElement() {
      float f = 352.0F;

      for (Setting l1illl1lllllll1l1l1l1ili11l1 : Interface.interfaceField.getSettings()) {
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
      return "";
   }

   @Override
   public float getHeight() {
      return (float)(
         23.0
            + (
               this.hasSettings()
                  ? GuiStyle.PADDING * 2
                     + this.settings.stream().filter(GuiSetting::isVisible).mapToDouble(var0 -> var0.getAnimHeight() + 6.0F * var0.getVisibleProgress()).sum()
                     + -6.0
                     + GuiStyle.PADDING * 2
                  : 0.0
            )
      );
   }

   public boolean hasSettings() {
      return !this.settings.isEmpty() && this.settings.stream().anyMatch(GuiSetting::isVisible);
   }

   @Override
   public float getWidth() {
      return 368.0F;
   }

   @Override
   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float f = var4 + GuiStyle.PADDING * 2;
      float f1 = var5 + 23.0F + GuiStyle.PADDING * 2;
      float f2 = var6;

      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible()) {
            guisetting.renderPriority(var1, var2, var3, f, f1, var6, f2);
            f1 += guisetting.getAnimHeight() + 6.0F * guisetting.getVisibleProgress();
         }
      }
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      boolean flag = false;

      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible() && guisetting.onMousePriorityClicked(var1, var3, var5)) {
            flag = true;
         }
      }

      return flag;
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible() && guisetting.onMouseClicked(var1, var3, var5)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      boolean flag = false;

      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible() && guisetting.onMousePriorityScroll(var1, var3, var5, var7)) {
            flag = true;
         }
      }

      return flag;
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      for (GuiSetting guisetting : this.settings) {
         guisetting.onMouseReleased(var1, var3, var5);
      }

      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible() && guisetting.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public boolean charTyped(char var1, int var2) {
      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible() && guisetting.charTyped(var1, var2)) {
            return true;
         }
      }

      return super.charTyped(var1, var2);
   }
}
