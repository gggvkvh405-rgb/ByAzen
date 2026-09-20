package org.zenith.client.screens.autosbor;

import org.zenith.ZenithClient;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.client.screens.nlgui.style.GuiStyle;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.render.ShapeRenderer;
import org.zenith.util.ArgbColor;
import org.zenith.utility.render.display.base.CornerRadius;
import org.zenith.utility.render.display.base.HudDrawContext;

public final class AutoSborStyle {
   public static ArgbColor leftBackground() {
      ZenithStyle zenithstyle = currentStyle();
      return zenithstyle == null ? GuiStyle.LEFT_BACKGROUND : zenithstyle.getLeftBackground().getColor();
   }

   public static ArgbColor rightBackground() {
      ZenithStyle zenithstyle = currentStyle();
      return zenithstyle == null ? GuiStyle.RIGHT_BACKGROUND : zenithstyle.getRightBackground().getColor();
   }

   public static ArgbColor panelBackground() {
      ZenithStyle zenithstyle = currentStyle();
      return zenithstyle == null ? GuiStyle.PANEL_LEFT_BACKGROUND : zenithstyle.getPanelLeftBackground().getColor();
   }

   public static ArgbColor surface() {
      ZenithStyle zenithstyle = currentStyle();
      return zenithstyle == null ? GuiStyle.SURFACE_ENABLE_BACKGROUND : zenithstyle.getSurfaceEnableBackground().getColor();
   }

   public static ArgbColor headerSurface() {
      ZenithStyle zenithstyle = currentStyle();
      return zenithstyle == null ? GuiStyle.HEADER_DISABLE_BACKGROUND : zenithstyle.getHeaderDisableBackground().getColor();
   }

   public static ArgbColor fieldSurface() {
      ZenithStyle zenithstyle = currentStyle();
      return zenithstyle == null ? GuiStyle.FIELD_SURFACE_BACKGROUND : zenithstyle.getFieldSurfaceBackground().getColor();
   }

   public static ArgbColor fieldBorder() {
      ZenithStyle zenithstyle = currentStyle();
      return zenithstyle == null ? GuiStyle.FIELD_BORDER : zenithstyle.getFieldBorder().getColor();
   }

   public static ArgbColor text() {
      ZenithStyle zenithstyle = currentStyle();
      return zenithstyle == null ? GuiStyle.TEXT_ENABLE : zenithstyle.getTextEnable().getColor();
   }

   public static ArgbColor textSecondary() {
      ZenithStyle zenithstyle = currentStyle();
      return zenithstyle == null ? GuiStyle.TEXT_SECONDARY : zenithstyle.getTextSecondary().getColor();
   }

   public static ArgbColor textTertiary() {
      ZenithStyle zenithstyle = currentStyle();
      return zenithstyle == null ? GuiStyle.TEXT_TERTIARY : zenithstyle.getTextTertiary().getColor();
   }

   public static ArgbColor primary() {
      ZenithStyle zenithstyle = currentStyle();
      return zenithstyle == null ? GuiStyle.PRIMARY_COLOR : zenithstyle.getPrimaryColor().getColor();
   }

   public static ArgbColor transparentText() {
      return text().EventHookWorldRender(0);
   }

   public static ArgbColor textAlpha(int var0) {
      return text().EventHookWorldRender(var0);
   }

   public static void drawBlur(HudDrawContext var0, float var1, float var2, float var3, float var4, CornerRadius var5, float var6) {
      float f = getBlurPower();
      if (!(f <= 0.0F)) {
         ShapeRenderer.on23(var0.getMatrices(), var1, var2, var3, var4, f, var5, ArgbColor.var11934.SprintStateEvent(var6), true, false);
      }
   }

   public static ZenithStyle currentStyle() {
      return ZenithClient.on23().TextScanner().getCurrentStyle();
   }

   public static float getBlurPower() {
      NLMenuScreen nlmenuscreen = ZenithClient.on23().NbtEditor();
      return nlmenuscreen == null ? 0.0F : nlmenuscreen.getBlurPower();
   }
}
