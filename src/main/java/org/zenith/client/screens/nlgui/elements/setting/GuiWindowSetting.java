package org.zenith.client.screens.nlgui.elements.setting;

import java.util.ArrayList;
import java.util.List;
import org.zenith.ZenithClient;
import org.zenith.base.font.Font;
import org.zenith.base.font.Fonts;
import org.zenith.client.screens.nlgui.elements.api.GuiSetting;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.core.Easing;
import org.zenith.core.MenuScreenId;
import org.zenith.core.UiAnimation;
import org.zenith.render.ShapeRenderer;
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

public class GuiWindowSetting extends GuiSetting<SettingGroup> {
   public final UiAnimation animationExpanded = new UiAnimation(200L, Easing.StopUsingItemEvent);
   public final List<GuiSetting<?>> renderableSettings = new ArrayList<>();
   public CornerRadiusF bounds;
   public CornerRadiusF rectBounds;
   public CornerRadiusF exitBounds;
   public boolean expanded;

   public GuiWindowSetting(SettingGroup var1) {
      this(var1, 166.0F);
   }

   public GuiWindowSetting(SettingGroup var1, float var2) {
      super(var2, var1);
      var2 = 182.0F - GuiStyle.PADDING * 4;

      for (Setting l1illl1lllllll1l1l1l1ili11l1 : var1.getSettings()) {
         if (l1illl1lllllll1l1l1l1ili11l1 instanceof NumberSetting lilliiill11llilll1ll1l) {
            this.renderableSettings.add(new GuiNumberSetting(lilliiill11llilll1ll1l, var2));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof ModeSetting ill11ii1ilil1liili1iliil) {
            this.renderableSettings.add(new GuiModeSetting(ill11ii1ilil1liili1iliil, var2));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof MultiSelectSetting i1i1lll1liii1il1llll1) {
            this.renderableSettings.add(new GuiMultiBooleanSetting(i1i1lll1liii1il1llll1, var2));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof BooleanSetting iili1iilllliiil) {
            this.renderableSettings.add(new GuiBooleanSetting(iili1iilllliiil, var2));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof ColorSetting llliili1l1ii11i1lii1) {
            this.renderableSettings.add(new GuiColorSetting(llliili1l1ii11i1lii1, var2));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof ButtonSetting ilii1liilllilll11iil11lil1ll) {
            this.renderableSettings.add(new GuiButtonSetting(ilii1liilllilll11iil11lil1ll, var2));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof StringListSetting ii1lllil11i1) {
            this.renderableSettings.add(new GuiItemSelectSetting(ii1lllil11i1, var2));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof KeySetting l1ll111iiil) {
            this.renderableSettings.add(new GuiKeySetting(l1ll111iiil, var2));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof TextSetting i1ll1llliii11l1) {
            this.renderableSettings.add(new GuiStringSetting(i1ll1llliii11l1, var2));
         } else if (l1illl1lllllll1l1l1l1ili11l1 instanceof SettingGroup l1lili1ii11) {
            this.renderableSettings.add(new GuiWindowSetting(l1lili1ii11, var2));
         }
      }
   }

   @Override
   public String getName() {
      return this.setting.getName();
   }

   @Override
   public boolean onMouseClicked(double var1, double var3, MenuScreenId var5) {
      if (this.bounds != null && this.bounds.PotionItemBuilder(var1, var3)) {
         if (this.animationExpanded.BotDisconnectEvent() == 0.0F) {
            this.expanded = true;
         } else {
            this.expanded = false;
         }

         return true;
      } else {
         return this.expanded;
      }
   }

   @Override
   public void render(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6) {
      ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
      if (zenithstyle != null) {
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
            "t",
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
         float f1 = 6.0F;
         float f2 = 6.0F;
         this.bounds = new CornerRadiusF(var4 + this.width - f1, var5 + (this.getHeight() - f2) / 2.0F, f1, f2);
         var1.drawRoundedRect(
            this.bounds.x(),
            this.bounds.y(),
            f1,
            f2,
            CornerRadius.MovementInputEvent(1.0F),
            zenithstyle.getFieldSurfaceBackground().getColor().SprintStateEvent(var6)
         );
         var1.drawRoundedBorder(
            this.bounds.x(),
            this.bounds.y(),
            f1,
            f2,
            -0.5F,
            CornerRadius.MovementInputEvent(1.0F),
            zenithstyle.getFieldBorder().getColor().SprintStateEvent(var6)
         );
         Font font2 = Fonts.NEW_ICONS.getFont(5.5F);
         var1.drawText(
            font2,
            "w",
            this.bounds.x() + (f1 - font2.width("w")) / 2.0F,
            this.bounds.y() + (f2 - font2.height()) / 2.0F,
            zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6)
         );
      }
   }

   @Override
   public void renderPriority(HudDrawContext var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.animationExpanded.on23(this.expanded);
      if (!(this.animationExpanded.CancellableEvent() <= 0.0F)) {
         float f = GuiStyle.PADDING * 4 + this.getHeight();
         float f1 = GuiStyle.PADDING.intValue();
         float f2 = 182.0F;
         float f3 = 0.0F;

         for (GuiSetting guisetting : this.renderableSettings) {
            if (guisetting.isVisible()) {
               f3 += guisetting.getAnimHeight() + 6.0F;
            }
         }

         if (f3 > 0.0F) {
            f3 -= 6.0F;
         }

         float f9 = f + f1 * 2.0F + f3;
         float f10 = this.bounds.x() + GuiStyle.PADDING * 2;
         float f4 = this.bounds.y() - f9 / 2.0F;
         this.rectBounds = new CornerRadiusF(f10, f4, f2, f9);
         var6 *= this.animationExpanded.CancellableEvent();
         var1.getMatrices().pushMatrix();
         var1.getMatrices().translate(this.bounds.x(), this.bounds.y());
         var1.getMatrices().scale(this.animationExpanded.CancellableEvent(), this.animationExpanded.CancellableEvent());
         var1.getMatrices().translate(-this.bounds.x(), -this.bounds.y());
         ZenithStyle zenithstyle = ZenithClient.on23().TextScanner().getCurrentStyle();
         if (zenithstyle != null) {
            float popupCorner = GuiStyle.ROUND.intValue() / 2.0F;
            CornerRadius popupRadius = CornerRadius.MovementInputEvent(popupCorner);
            ShapeRenderer.ItemSpec(
               var1.getMatrices(),
               f10,
               f4,
               f2,
               f9,
               12.0F,
               popupRadius,
               ArgbColor.var11934.SprintStateEvent(var6)
            );
            var1.drawRoundedRect(
               f10,
               f4 + f,
               f2,
               f9 - f,
               CornerRadius.BotRespawnEvent(popupCorner, popupCorner),
               zenithstyle.getPanelLeftBackground().getColor().SprintStateEvent(var6)
            );
            var1.drawRoundedRect(
               f10,
               f4,
               f2,
               f,
               CornerRadius.BotPacketEvent(popupCorner, popupCorner),
               zenithstyle.getRightBackground().getColor().SprintStateEvent(var6)
            );
            Font font = Fonts.NEW_ICONS.getFont(4.0F);
            float f5 = f10 + f2 - font.width("2") - f1 * 2.0F;
            float f6 = f4 + f1 + font.height();
            this.exitBounds = new CornerRadiusF(f5, f6, 5.0F, 5.0F);
            var1.drawText(font, "2", f5, f6, zenithstyle.getTextTertiary().getColor().SprintStateEvent(var6));
            Font font1 = Fonts.NEW_MEDIUM.getFont(5.5F);
            Font font2 = Fonts.NEW_MEDIUM.getFont(5.4F);
            Font font3 = Fonts.NEW_REGULAR.getFont(5.3F);
            ArgbColor i11ii1llliilllii1i1 = zenithstyle.getTextEnable().getColor().SprintStateEvent(var6);
            ArgbColor i11ii1llliilllii1i11 = zenithstyle.getTextSecondary().getColor().SprintStateEvent(var6);
            ArgbColor i11ii1llliilllii1i12 = zenithstyle.getPrimaryColor().getColor().SprintStateEvent(var6);
            this.drawDefault(
               var1,
               var2,
               var3,
               "w",
               this.setting.getName(),
               this.setting.getDescription(),
               font2,
               font3,
               f10 + f1 * 2.0F,
               f4 + f1 * 2.0F,
               f2 / 2.0F,
               i11ii1llliilllii1i1,
               i11ii1llliilllii1i11,
               i11ii1llliilllii1i12
            );
            float f7 = f10 + f1 * 2.0F;
            float f8 = f4 + f + f1;

            for (GuiSetting guisetting1 : this.renderableSettings) {
               if (guisetting1.isVisible()) {
                  guisetting1.render(var1, var2, var3, f7, f8, var6);
                  f8 += guisetting1.getAnimHeight() + 6.0F;
               }
            }

            f8 = f4 + f + f1;

            for (GuiSetting guisetting2 : this.renderableSettings) {
               if (guisetting2.isVisible()) {
                  guisetting2.renderPriority(var1, var2, var3, f7, f8, var6, 1.0F);
                  f8 += guisetting2.getAnimHeight() + 6.0F;
               }
            }

            var1.getMatrices().popMatrix();
         }
      }
   }

   @Override
   public boolean onMousePriorityClicked(double var1, double var3, MenuScreenId var5) {
      if (this.expanded && this.rectBounds != null) {
         boolean flag = false;

         for (GuiSetting guisetting : this.renderableSettings) {
            if (guisetting.isVisible() && guisetting.onMousePriorityClicked(var1, var3, var5)) {
               flag = true;
            }
         }

         if (flag) {
            return true;
         }

         if (!this.rectBounds.PotionItemBuilder(var1, var3)) {
            this.expanded = false;
            return false;
         }

         if (this.exitBounds != null && this.exitBounds.on23(var1, var3, 2.0F)) {
            this.expanded = false;
            return true;
         }

         for (GuiSetting guisetting1 : this.renderableSettings) {
            if (guisetting1.isVisible() && guisetting1.onMouseClicked(var1, var3, var5)) {
               System.out.println(guisetting1);
               return true;
            }
         }

         return true;
      } else if (this.expanded) {
         this.expanded = false;
         return false;
      } else {
         return false;
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      for (GuiSetting guisetting : this.renderableSettings) {
         if (guisetting.isVisible() && guisetting.keyPressed(var1, var2, var3)) {
            return true;
         }
      }

      return super.keyPressed(var1, var2, var3);
   }

   @Override
   public void onMouseReleased(double var1, double var3, MenuScreenId var5) {
      for (GuiSetting guisetting : this.renderableSettings) {
         if (guisetting.isVisible()) {
            guisetting.onMouseReleased(var1, var3, var5);
         }
      }

      super.onMouseReleased(var1, var3, var5);
   }

   @Override
   public boolean onMousePriorityScroll(double var1, double var3, double var5, double var7) {
      if (!this.expanded) {
         return false;
      }

      this.expanded = false;

      for (GuiSetting guisetting : this.renderableSettings) {
         if (guisetting.isVisible() && guisetting.onMousePriorityScroll(var1, var3, var5, var7)) {
            return true;
         }
      }

      return false;
   }

   public void setExpanded(boolean var1) {
      this.expanded = var1;
   }
}
