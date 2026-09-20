package org.zenith.client.screens.nlgui.style;

import com.google.gson.JsonObject;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.zenith.setting.ColorSetting;
import org.zenith.setting.Setting;
import org.zenith.util.ArgbColor;

public class ZenithStyle {
   public String name;
   public final ColorSetting primaryColor = new ColorSetting("nlgui.style.color.primaryColor", "nlgui.style.color.primaryColor.desc", GuiStyle.PRIMARY_COLOR);
   public final ColorSetting secondaryPrimaryColor = new ColorSetting(
      "nlgui.style.color.secondaryPrimaryColor", "nlgui.style.color.secondaryPrimaryColor.desc", GuiStyle.SECONDARY_PRIMARY_COLOR
   );
   public final ColorSetting glowColor1 = new ColorSetting(
      "nlgui.style.color.glowColor1", "nlgui.style.color.glowColor1.desc", GuiStyle.PRIMARY_COLOR.EventHookWorldRender(70)
   );
   public final ColorSetting glowColor2 = new ColorSetting(
      "nlgui.style.color.glowColor2", "nlgui.style.color.glowColor2.desc", GuiStyle.SECONDARY_PRIMARY_COLOR.EventHookWorldRender(70)
   );
   public final ColorSetting friendColor = new ColorSetting("nlgui.style.color.friendColor", "nlgui.style.color.friendColor.desc", GuiStyle.FRIEND_COLOR);
   public final ColorSetting glareColor = new ColorSetting("nlgui.style.color.glareColor", "nlgui.style.color.glareColor.desc", GuiStyle.GLARE_COLOR);
   public final ColorSetting leftBackground = new ColorSetting(
      "nlgui.style.color.leftBackground", "nlgui.style.color.leftBackground.desc", GuiStyle.LEFT_BACKGROUND
   );
   public final ColorSetting rightBackground = new ColorSetting(
      "nlgui.style.color.rightBackground", "nlgui.style.color.rightBackground.desc", GuiStyle.RIGHT_BACKGROUND
   );
   public final ColorSetting panelLeftBackground = new ColorSetting(
      "nlgui.style.color.panelLeftBackground", "nlgui.style.color.panelLeftBackground.desc", GuiStyle.PANEL_LEFT_BACKGROUND
   );
   public final ColorSetting surfaceEnableBackground = new ColorSetting(
      "nlgui.style.color.surfaceEnableBackground", "nlgui.style.color.surfaceEnableBackground.desc", GuiStyle.SURFACE_ENABLE_BACKGROUND
   );
   public final ColorSetting headerDisableBackground = new ColorSetting(
      "nlgui.style.color.headerDisableBackground", "nlgui.style.color.headerDisableBackground.desc", GuiStyle.HEADER_DISABLE_BACKGROUND
   );
   public final ColorSetting surfaceDisableBackground = new ColorSetting(
      "nlgui.style.color.surfaceDisableBackground", "nlgui.style.color.surfaceDisableBackground.desc", GuiStyle.SURFACE_DISABLE_BACKGROUND
   );
   public final ColorSetting fieldSurfaceBackground = new ColorSetting(
      "nlgui.style.color.fieldSurfaceBackground", "nlgui.style.color.fieldSurfaceBackground.desc", GuiStyle.FIELD_SURFACE_BACKGROUND
   );
   public final ColorSetting fieldBorder = new ColorSetting("nlgui.style.color.fieldBorder", "nlgui.style.color.fieldBorder.desc", GuiStyle.FIELD_BORDER);
   public final ColorSetting disableActiveBg = new ColorSetting(
      "nlgui.style.color.disableActiveBg", "nlgui.style.color.disableActiveBg.desc", GuiStyle.DISABLE_ACTIVE_BG
   );
   public final ColorSetting textEnable = new ColorSetting("nlgui.style.color.textEnable", "nlgui.style.color.textEnable.desc", GuiStyle.TEXT_ENABLE);
   public final ColorSetting textTertiary = new ColorSetting("nlgui.style.color.textTertiary", "nlgui.style.color.textTertiary.desc", GuiStyle.TEXT_TERTIARY);
   public final ColorSetting textSecondary = new ColorSetting(
      "nlgui.style.color.textSecondary", "nlgui.style.color.textSecondary.desc", GuiStyle.TEXT_SECONDARY
   );
   public final ColorSetting heartActiveBg = new ColorSetting(
      "nlgui.style.color.heartActiveBg", "nlgui.style.color.heartActiveBg.desc", GuiStyle.HEART_ACTIVE_BG
   );
   public final ColorSetting heartIcon = new ColorSetting("nlgui.style.color.heartIcon", "nlgui.style.color.heartIcon.desc", GuiStyle.HEART_ICON);
   public final ColorSetting hudBackground = new ColorSetting(
      "nlgui.style.color.hudBackground", "nlgui.style.color.hudBackground.desc", GuiStyle.HUD_BACKGROUND
   );
   public final ColorSetting headerHudBackground = new ColorSetting(
      "nlgui.style.color.headerHudBackground", "nlgui.style.color.headerHudBackground.desc", GuiStyle.HEADER_HUD_BACKGROUND
   );

   public ZenithStyle() {
      this("");
   }

   public ZenithStyle(String var1) {
      this.name = var1;
   }

   public void setDefaultsFromGuiStyle(ArgbColor var1) {
      this.setDefaultsFromGuiStyle();
      this.leftBackground.setColor(var1.EventHookWorldRender(this.leftBackground.getColor().var14325()));
      this.rightBackground.setColor(var1.EventHookWorldRender(this.rightBackground.getColor().var14325()));
      this.panelLeftBackground.setColor(var1.EventHookWorldRender(this.panelLeftBackground.getColor().var14325()));
      this.surfaceEnableBackground.setColor(var1.EventHookWorldRender(this.surfaceEnableBackground.getColor().var14325()));
      this.headerDisableBackground.setColor(var1.EventHookWorldRender(this.headerDisableBackground.getColor().var14325()));
      this.fieldSurfaceBackground.setColor(GuiStyle.FIELD_SURFACE_BACKGROUND);
      this.surfaceDisableBackground.setColor(var1.EventHookWorldRender(this.surfaceDisableBackground.getColor().var14325()));
      this.hudBackground.setColor(var1.EventHookWorldRender(this.hudBackground.getColor().var14325()));
      this.headerHudBackground.setColor(var1.EventHookWorldRender(this.headerHudBackground.getColor().var14325()));
   }

   public void setDefaultsFromGuiStyle() {
      this.leftBackground.setColor(GuiStyle.LEFT_BACKGROUND);
      this.rightBackground.setColor(GuiStyle.RIGHT_BACKGROUND);
      this.panelLeftBackground.setColor(GuiStyle.PANEL_LEFT_BACKGROUND);
      this.surfaceEnableBackground.setColor(GuiStyle.SURFACE_ENABLE_BACKGROUND);
      this.headerDisableBackground.setColor(GuiStyle.HEADER_DISABLE_BACKGROUND);
      this.fieldSurfaceBackground.setColor(GuiStyle.FIELD_SURFACE_BACKGROUND);
      this.fieldBorder.setColor(GuiStyle.FIELD_BORDER);
      this.surfaceDisableBackground.setColor(GuiStyle.SURFACE_DISABLE_BACKGROUND);
      this.textEnable.setColor(GuiStyle.TEXT_ENABLE);
      this.textTertiary.setColor(GuiStyle.TEXT_TERTIARY);
      this.textSecondary.setColor(GuiStyle.TEXT_SECONDARY);
      this.glowColor1.setColor(this.primaryColor.getColor().EventHookWorldRender(70));
      this.glowColor2.setColor(this.secondaryPrimaryColor.getColor().EventHookWorldRender(70));
      this.friendColor.setColor(GuiStyle.FRIEND_COLOR);
      this.glareColor.setColor(GuiStyle.GLARE_COLOR);
      this.disableActiveBg.setColor(GuiStyle.DISABLE_ACTIVE_BG);
      this.heartActiveBg.setColor(GuiStyle.HEART_ACTIVE_BG);
      this.heartIcon.setColor(GuiStyle.HEART_ICON);
   }

   public void safe(JsonObject var1) {
      var1.addProperty("name", this.name);
      this.leftBackground.safe(var1);
      this.rightBackground.safe(var1);
      this.panelLeftBackground.safe(var1);
      this.surfaceEnableBackground.safe(var1);
      this.headerDisableBackground.safe(var1);
      this.fieldSurfaceBackground.safe(var1);
      this.fieldBorder.safe(var1);
      this.surfaceDisableBackground.safe(var1);
      this.textEnable.safe(var1);
      this.textTertiary.safe(var1);
      this.textSecondary.safe(var1);
      this.primaryColor.safe(var1);
      this.secondaryPrimaryColor.safe(var1);
      this.glowColor1.safe(var1);
      this.glowColor2.safe(var1);
      this.friendColor.safe(var1);
      this.glareColor.safe(var1);
      this.disableActiveBg.safe(var1);
      this.heartActiveBg.safe(var1);
      this.heartIcon.safe(var1);
      this.hudBackground.safe(var1);
      this.headerHudBackground.safe(var1);
   }

   public void load(JsonObject var1) {
      if (var1.has("name")) {
         this.name = var1.get("name").getAsString();
      }

      this.loadColor(var1, this.leftBackground, "leftBackground");
      this.loadColor(var1, this.rightBackground, "rightBackground");
      this.loadColor(var1, this.panelLeftBackground, "panelLeftBackground");
      this.loadColor(var1, this.surfaceEnableBackground, "surfaceEnableBackground");
      this.loadColor(var1, this.headerDisableBackground, "headerDisableBackground");
      this.loadColor(var1, this.fieldSurfaceBackground, "fieldSurfaceBackground");
      this.loadColor(var1, this.fieldBorder, "fieldBorder");
      this.loadColor(var1, this.surfaceDisableBackground, "surfaceDisableBackground");
      this.loadColor(var1, this.textEnable, "textEnable");
      this.loadColor(var1, this.textTertiary, "textTertiary");
      this.loadColor(var1, this.textSecondary, "textSecondary");
      this.loadColor(var1, this.primaryColor, "primaryColor");
      this.loadColor(var1, this.secondaryPrimaryColor);
      this.loadColor(var1, this.glowColor1);
      this.loadColor(var1, this.glowColor2);
      this.loadColor(var1, this.friendColor);
      this.loadColor(var1, this.glareColor, "glareColor");
      this.loadColor(var1, this.disableActiveBg, "disableActiveBg");
      this.loadColor(var1, this.heartActiveBg, "heartActiveBg");
      this.loadColor(var1, this.heartIcon, "heartIcon");
      this.loadColor(var1, this.hudBackground, "hudBackground");
      this.loadColor(var1, this.headerHudBackground, "headerHudBackground");
   }

   public void loadColor(JsonObject var1, ColorSetting var2, String... var3) {
      if (var1.has(var2.getKey())) {
         var2.setColor(var1.get(var2.getKey()).getAsInt());
      } else {
         for (String s : var3) {
            if (var1.has(s)) {
               var2.setColor(var1.get(s).getAsInt());
               return;
            }
         }
      }
   }

   public List<Setting> getSettings() {
      return Arrays.stream(this.getClass().getDeclaredFields()).map(var1 -> {
         try {
            var1.setAccessible(true);
            return var1.get(this);
         } catch (IllegalAccessException illegalaccessexception) {
            illegalaccessexception.printStackTrace();
            return null;
         }
      }).filter(var0 -> var0 instanceof Setting).map(var0 -> (Setting)var0).collect(Collectors.toList());
   }

   public String getName() {
      return this.name;
   }

   public ColorSetting getPrimaryColor() {
      return this.primaryColor;
   }

   public ColorSetting getSecondaryPrimaryColor() {
      return this.secondaryPrimaryColor;
   }

   public ColorSetting getGlowColor1() {
      return this.glowColor1;
   }

   public ColorSetting getGlowColor2() {
      return this.glowColor2;
   }

   public ColorSetting getFriendColor() {
      return this.friendColor;
   }

   public ColorSetting getGlareColor() {
      return this.glareColor;
   }

   public ColorSetting getLeftBackground() {
      return this.leftBackground;
   }

   public ColorSetting getRightBackground() {
      return this.rightBackground;
   }

   public ColorSetting getPanelLeftBackground() {
      return this.panelLeftBackground;
   }

   public ColorSetting getSurfaceEnableBackground() {
      return this.surfaceEnableBackground;
   }

   public ColorSetting getHeaderDisableBackground() {
      return this.headerDisableBackground;
   }

   public ColorSetting getSurfaceDisableBackground() {
      return this.surfaceDisableBackground;
   }

   public ColorSetting getFieldSurfaceBackground() {
      return this.fieldSurfaceBackground;
   }

   public ColorSetting getFieldBorder() {
      return this.fieldBorder;
   }

   public ColorSetting getDisableActiveBg() {
      return this.disableActiveBg;
   }

   public ColorSetting getTextEnable() {
      return this.textEnable;
   }

   public ColorSetting getTextTertiary() {
      return this.textTertiary;
   }

   public ColorSetting getTextSecondary() {
      return this.textSecondary;
   }

   public ColorSetting getHeartActiveBg() {
      return this.heartActiveBg;
   }

   public ColorSetting getHeartIcon() {
      return this.heartIcon;
   }

   public ColorSetting getHudBackground() {
      return this.hudBackground;
   }

   public ColorSetting getHeaderHudBackground() {
      return this.headerHudBackground;
   }

   public void setName(String var1) {
      this.name = var1;
   }
}
