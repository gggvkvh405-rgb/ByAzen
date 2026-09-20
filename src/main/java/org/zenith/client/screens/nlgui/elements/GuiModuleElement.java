package org.zenith.client.screens.nlgui.elements;

import java.util.ArrayList;
import java.util.List;
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
import org.zenith.module.Module;
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
import org.zenith.util.ScoreboardUtils;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public class GuiModuleElement extends Element {
   public final Module module;
   public final UiAnimation animationEnable = new UiAnimation(200L, Easing.CloseScreenEvent);
   public final UiAnimation animationHovered = new UiAnimation(200L, Easing.CloseScreenEvent);
   public final UiAnimation heartAnimation = new UiAnimation(300L, Easing.CloseScreenEvent);
   public final UiAnimation bindAnimation = new UiAnimation(300L, Easing.CloseScreenEvent);
   public final UiAnimation searchVisibleAnimation = new UiAnimation(180L, 1.0F, Easing.StopUsingItemEvent);
   public float animationPosX = 0.0F;
   public float animationPosY = 0.0F;
   public CornerRadiusF bounds;
   public CornerRadiusF heartBounds;
   public CornerRadiusF bindBounds;
   public boolean binding;
   public boolean animated = false;
   public boolean positionInitialized = false;
   public boolean searchVisible = true;
   public float lastX;
   public float lastY;
   public int lastIndex;
   public final List<GuiSetting<?>> settings = new ArrayList<>();

   public GuiModuleElement(Module var1) {
      this.module = var1;
      if (var1.isEnabled()) {
         this.animationEnable.setValue(1.0F);
      }

      if (var1.isPriority()) {
         this.heartAnimation.setValue(1.0F);
      }

      if (var1.isRenderSetting()) {
         for (Setting l1illl1lllllll1l1l1l1ili11l1 : var1.getSettings()) {
            if (l1illl1lllllll1l1l1l1ili11l1 instanceof NumberSetting lilliiill11llilll1ll1l) {
               this.settings.add(new GuiNumberSetting(lilliiill11llilll1ll1l));
            } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof ModeSetting ill11ii1ilil1liili1iliil) {
               this.settings.add(new GuiModeSetting(ill11ii1ilil1liili1iliil));
            } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof MultiSelectSetting i1i1lll1liii1il1llll1) {
               this.settings.add(new GuiMultiBooleanSetting(i1i1lll1liii1il1llll1));
            } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof BooleanSetting iili1iilllliiil) {
               this.settings.add(new GuiBooleanSetting(iili1iilllliiil));
            } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof ColorSetting llliili1l1ii11i1lii1) {
               this.settings.add(new GuiColorSetting(llliili1l1ii11i1lii1));
            } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof ButtonSetting ilii1liilllilll11iil11lil1ll) {
               this.settings.add(new GuiButtonSetting(ilii1liilllilll11iil11lil1ll));
            } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof StringListSetting ii1lllil11i1) {
               this.settings.add(new GuiItemSelectSetting(ii1lllil11i1));
            } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof KeySetting l1ll111iiil) {
               this.settings.add(new GuiKeySetting(l1ll111iiil));
            } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof TextSetting i1ll1llliii11l1) {
               this.settings.add(new GuiStringSetting(i1ll1llliii11l1));
            } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof SettingGroup l1lili1ii11) {
               this.settings.add(new GuiWindowSetting(l1lili1ii11));
            }
         }
      }
   }

   @Override
   public String getName() {
      return this.module.getName();
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
      return 182.0F;
   }

   public boolean isPriority() {
      return this.module.isPriority();
   }

   public boolean isEnable() {
      return this.module.isEnabled();
   }

   public void setSearchVisible(boolean var1) {
      this.searchVisible = var1;
   }

   public void resetSearchVisible(boolean var1) {
      this.searchVisible = var1;
      this.searchVisibleAnimation.setValue(var1 ? 1.0F : 0.0F);
   }

   public float updateSearchVisible() {
      return this.searchVisibleAnimation.on23(this.searchVisible ? 1.0F : 0.0F);
   }

   public float getSearchVisibleProgress() {
      return this.searchVisibleAnimation.CancellableEvent();
   }

   public boolean shouldRenderInSearch() {
      return this.searchVisible || this.searchVisibleAnimation.CancellableEvent() > 0.01F;
   }

   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      float f = var4 + GuiStyle.PADDING * 2;
      float f1 = var5 + 23.0F + GuiStyle.PADDING * 2;
      float f2 = (float)(MathHelper.lerp(this.animationEnable.CancellableEvent(), 0.5, 1.0) * var6);
      float f3 = 6.0F;

      for (GuiSetting guisetting : this.settings) {
         if (guisetting.isVisible()) {
            guisetting.renderPriority(var1, var2, var3, f, f1, var6, f2);
            f1 += guisetting.getAnimHeight() + f3 * guisetting.getVisibleProgress();
         }
      }
   }

   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7) {
      this.render(var1, var2, var3, var4, var5, var6, var7, null);
   }

   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, int var7, CornerRadiusF var8) {
      if (!this.positionInitialized) {
         this.animationPosX = var4;
         this.animationPosY = var5;
         this.lastX = var4;
         this.lastY = var5;
         this.positionInitialized = true;
         this.lastIndex = var7;
      } else if ((var4 != this.lastX || var5 != this.lastY) && var7 != this.lastIndex) {
         this.animated = true;
         this.lastX = var4;
         this.lastY = var5;
      }

      if (this.animated) {
         this.animationPosX = Math.round(MathHelper.lerp(0.4F, this.animationPosX, var4));
         this.animationPosY = Math.round(MathHelper.lerp(0.4F, this.animationPosY, var5));
         if (Math.abs(this.animationPosX - var4) < 2.0F && Math.abs(this.animationPosY - var5) < 2.0F) {
            this.animated = false;
         } else {
            var4 = this.animationPosX;
            var5 = this.animationPosY;
         }
      } else {
         this.animationPosX = var4;
         this.animationPosY = var5;
      }

      this.lastIndex = var7;
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
         this.bounds = new CornerRadiusF(var4, var5, this.getWidth(), 23.0F);
         float f = this.bounds.width();
         float f1 = this.getHeight();
         boolean flag = this.isEnable();
         this.animationEnable.on23(flag);
         boolean flag1 = this.bounds.PotionItemBuilder(var2, var3) && (var8 == null || var8.PotionItemBuilder(var2, var3));
         this.animationHovered.on23(flag ? 1.0F : (flag1 ? 0.6F : 0.0F));
         float f2 = this.animationHovered.CancellableEvent();
         float f3 = var5 + GuiStyle.PADDING * 2;
         float f4 = MathHelper.lerp(this.animationEnable.CancellableEvent(), 0, 5 + GuiStyle.PADDING);
         ArgbColor i11ii1llliilllii1i1 = zenithstyle.getPrimaryColor().getColor();
         ArgbColor i11ii1llliilllii1i11 = zenithstyle.getTextSecondary().getColor();
         ArgbColor i11ii1llliilllii1i12 = zenithstyle.getTextEnable().getColor();
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
            zenithstyle.getHeaderDisableBackground()
               .getColor()
               .Easing(zenithstyle.getSurfaceEnableBackground().getColor(), this.animationEnable.CancellableEvent())
               .SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         float f5 = var4 + GuiStyle.PADDING * 2 + f4;
         Font font = Fonts.NEW_MEDIUM.getFont(6.0F);
         Font font1 = Fonts.NEW_ICONS.getFont(6.0F);
         var1.drawText(
            font,
            this.module.getName(),
            f5,
            var5 + (23.0F - font.height()) / 2.0F,
            i11ii1llliilllii1i11.Easing(i11ii1llliilllii1i12, f2).SprintStateEvent(var6)
         );
         var1.drawText(
            font1,
            "O",
            var4 + GuiStyle.PADDING * 2,
            var5 + (23.0F - font1.height()) / 2.0F,
            ArgbColor.var11941.Easing(i11ii1llliilllii1i1, this.animationEnable.CancellableEvent()).SprintStateEvent(var6)
         );
         float f6 = 12.0F;
         float f7 = 7.0F;
         float f8 = var4 + f - GuiStyle.PADDING * 2 - f6;
         var1.drawRoundedRectBatched(
            f8,
            f3,
            f6,
            f7,
            CornerRadius.MovementInputEvent(2.5F),
            zenithstyle.getDisableActiveBg().getColor().Easing(i11ii1llliilllii1i1, this.animationEnable.CancellableEvent()).SprintStateEvent(var6)
         );
         float f9 = MathHelper.lerp(this.animationEnable.CancellableEvent(), 1.0F, f6 - 1.0F - 5.0F);
         var1.drawRoundedRectBatched(
            f8 + f9,
            f3 + 1.0F,
            5.0F,
            5.0F,
            CornerRadius.MovementInputEvent(1.5F),
            zenithstyle.getTextTertiary().getColor().Easing(i11ii1llliilllii1i12, f2).SprintStateEvent(var6)
         );
         this.heartAnimation.on23(this.module.isPriority());
         f9 = f8 - GuiStyle.PADDING.intValue() - 7.0F;
         this.heartBounds = new CornerRadiusF(f9, f3, 7.0F, f7);
         var1.drawRoundedRectBatched(
            f9,
            f3,
            7.0F,
            f7,
            CornerRadius.MovementInputEvent(1.5F),
            zenithstyle.getDisableActiveBg().getColor().Easing(zenithstyle.getHeartActiveBg().getColor(), this.heartAnimation.CancellableEvent()).SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         Font font3 = Fonts.NEW_ICONS.getFont(4.5F);
         Font font4 = Fonts.NEW_ICONS.getFont(5.2F);
         var1.drawText(
            font4,
            "U",
            f9 + 1.6F - 0.8F,
            f3 + 1.7F - 0.8F,
            ArgbColor.var11941.Easing(zenithstyle.getTextTertiary().getColor(), 1.0F - this.heartAnimation.CancellableEvent()).SprintStateEvent(var6)
         );
         var1.drawText(
            font3,
            "V",
            f9 + 1.7F - 0.8F,
            f3 + 1.9F - 0.8F,
            ArgbColor.var11941.Easing(zenithstyle.getHeartIcon().getColor(), this.heartAnimation.CancellableEvent()).SprintStateEvent(var6)
         );
         String s = this.getBindText();
         this.bindAnimation.on23(this.binding || this.module.getKeyCode() != -1);
         Font font2 = Fonts.NEW_MEDIUM.getFont(4.5F);
         font3 = Fonts.NEW_ICONS.getFont(4.2F);
         float f14 = font2.width(s);
         float f10 = GuiStyle.PADDING.intValue() / 2.0F + f14 + GuiStyle.PADDING.intValue() / 3.0F + 3.75F + GuiStyle.PADDING.intValue() / 2.0F;
         float f11 = f8 - GuiStyle.PADDING.intValue() - 7.0F - GuiStyle.PADDING.intValue() / 2.0F - f10;
         this.bindBounds = new CornerRadiusF(f11, f3, f10, f7);
         var1.drawRoundedRectBatched(
            f11,
            f3,
            f10,
            f7,
            CornerRadius.MovementInputEvent(1.5F),
            zenithstyle.getDisableActiveBg()
               .getColor()
               .Easing(i11ii1llliilllii1i1.SprintStateEvent(0.15F), this.bindAnimation.CancellableEvent())
               .SprintStateEvent(var6)
         );
         var1.flushRoundedRects();
         var1.drawText(
            font2,
            s,
            f11 + GuiStyle.PADDING.intValue() / 2.0F,
            f3 + (f7 - font2.height()) / 2.0F,
            zenithstyle.getTextTertiary().getColor().Easing(i11ii1llliilllii1i1, this.bindAnimation.CancellableEvent()).SprintStateEvent(var6)
         );
         var1.drawText(
            font3,
            "N",
            f11 + GuiStyle.PADDING.intValue() / 2.0F + f14 + GuiStyle.PADDING.intValue() / 3.0F,
            f3 + GuiStyle.PADDING.intValue() / 2.0F,
            zenithstyle.getTextTertiary().getColor().Easing(i11ii1llliilllii1i1, this.bindAnimation.CancellableEvent()).SprintStateEvent(var6)
         );

         try {
            f9 = var4 + GuiStyle.PADDING * 2;
            float f12 = var5 + 23.0F + GuiStyle.PADDING * 2;
            float f13 = MathHelper.lerp(this.animationEnable.CancellableEvent(), MathHelper.lerp(this.animationHovered.CancellableEvent(), 0.5F, 1.0F), 1.0F)
               * var6;
            float f15 = 6.0F;

            for (GuiSetting guisetting : this.settings) {
               if (guisetting.isVisible()) {
                  guisetting.render(var1, var2, var3, f9, f12, f13);
                  f12 += guisetting.getAnimHeight() + f15 * guisetting.getVisibleProgress();
               }
            }
         } catch (Exception exception) {
            exception.printStackTrace();
         }
      }
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

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.heartBounds != null && this.heartBounds.PotionItemBuilder(var1, var3)) {
         this.module.setPriority(!this.module.isPriority());
         return true;
      }

      if (this.bindBounds != null && this.bindBounds.PotionItemBuilder(var1, var3)) {
         this.binding = true;
         return true;
      }

      if (this.binding && var5.int203() > 2) {
         this.module.setKeyCode(var5.int203());
         this.binding = false;
         return true;
      }

      if (this.bounds != null && this.bounds.PotionItemBuilder(var1, var3)) {
         if (var5 == MenuScreenId.call004 || var5 == MenuScreenId.call111) {
            this.module.toggle();
         } else if (var5 == MenuScreenId.call470) {
            this.binding = true;
         }

         return true;
      } else {
         for (GuiSetting guisetting : this.settings) {
            if (guisetting.isVisible() && guisetting.onMouseClicked(var1, var3, var5)) {
               return true;
            }
         }

         return false;
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

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      for (GuiSetting guisetting : this.settings) {
         guisetting.onMouseReleased(var1, var3, var5);
      }

      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (!this.binding) {
         for (GuiSetting guisetting : this.settings) {
            if (guisetting.isVisible() && guisetting.keyPressed(var1, var2, var3)) {
               return true;
            }
         }

         return super.keyPressed(var1, var2, var3);
      } else {
         if (var1 != 256 && var1 != 261 && var1 != 259) {
            this.module.setKeyCode(var1);
         } else {
            this.module.setKeyCode(-1);
         }

         this.binding = false;
         return true;
      }
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

   public String getBindText() {
      if (this.binding) {
         return this.getBindingDots();
      }

      String s = "n/a";
      int i = this.module.getKeyCode();
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

   public Module getModule() {
      return this.module;
   }

   public void setPositionInitialized(boolean var1) {
      this.positionInitialized = var1;
   }
}
