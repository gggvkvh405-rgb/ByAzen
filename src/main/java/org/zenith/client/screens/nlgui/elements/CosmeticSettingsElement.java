package org.zenith.client.screens.nlgui.elements;

import java.util.ArrayList;
import java.util.List;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiColorSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiModeSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiMultiBooleanSetting;
import org.zenith.client.screens.nlgui.elements.setting.GuiNumberSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.MenuScreenId;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ColorSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.setting.Setting;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public class CosmeticSettingsElement {
   public final List<GuiSetting<?>> settings = new ArrayList<>();

   public CosmeticSettingsElement() {
      this.rebuild();
   }

   public void rebuild() {
      this.settings.clear();
      float f = 352.0F;
      ArrayList<Setting> arraylist = new ArrayList<>();
      arraylist.addAll(ZenithClient.on23().SimpleItemBuilder().getSettings());
      arraylist.addAll(ZenithClient.on23().EnchantItemSpec().getSettings());
      arraylist.addAll(ZenithClient.on23().ItemServiceBase().getSettings());

      for (Setting l1illl1lllllll1l1l1l1ili11l1 : arraylist) {
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
         }
      }
   }

   public float getHeight() {
      return (float)(
         23.0
            + (
               this.hasSettings()
                  ? GuiStyle.PADDING * 2
                     + this.settings.stream().filter(GuiSetting::isVisible).mapToDouble(var0 -> var0.getAnimHeight() + 6.0F * var0.getVisibleProgress()).sum()
                     - 6.0
                     + GuiStyle.PADDING * 2
                  : 0.0
            )
      );
   }

   public boolean hasSettings() {
      return !this.settings.isEmpty() && this.settings.stream().anyMatch(GuiSetting::isVisible);
   }

   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         float f = 368.0F;
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
         Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
         Font font1 = Fonts.NEW_ICONS.getFont(6.0F);
         float f2 = var4 + GuiStyle.PADDING * 2 + font1.width("O") + GuiStyle.PADDING.intValue();
         var1.drawText(
            font1, "O", var4 + GuiStyle.PADDING * 2, var5 + (23.0F - font1.height()) / 2.0F, zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6)
         );
         var1.drawText(font, "Global Settings", f2, var5 + (23.0F - font.height()) / 2.0F, zenithstyle.getTextEnable().getColor().SprintStateEvent(var6));
         float f3 = var4 + GuiStyle.PADDING * 2;
         float f4 = var5 + 23.0F + GuiStyle.PADDING * 2;

         for (GuiSetting guisetting : this.settings) {
            if (guisetting.isVisible()) {
               guisetting.render(var1, var2, var3, f3, f4, var6);
               f4 += guisetting.getAnimHeight() + 6.0F * guisetting.getVisibleProgress();
            }
         }
      }
   }

   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float f = var4 + GuiStyle.PADDING * 2;
      float f1 = var5 + 23.0F + GuiStyle.PADDING * 2;

      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible()) {
            guisetting.renderPriority(var1, var2, var3, f, f1, var6, var6);
            f1 += guisetting.getAnimHeight() + 6.0F * guisetting.getVisibleProgress();
         }
      }
   }

   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible() && guisetting.onMouseClicked(var1, var3, var5)) {
            return true;
         }
      }

      return false;
   }

   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      boolean flag = false;

      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible() && guisetting.onMousePriorityClicked(var1, var3, var5)) {
            flag = true;
         }
      }

      return flag;
   }

   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      for (GuiSetting guisetting : this.settings) {
         guisetting.onMouseReleased(var1, var3, var5);
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      boolean flag = false;

      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible() && guisetting.onMousePriorityScroll(var1, var3, var5, var7)) {
            flag = true;
         }
      }

      return flag;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible() && guisetting.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return false;
   }

   public boolean charTyped(char var1, int var2) {
      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible() && guisetting.charTyped(var1, var2)) {
            return true;
         }
      }

      return false;
   }
}
