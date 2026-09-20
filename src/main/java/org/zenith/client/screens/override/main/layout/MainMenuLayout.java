package org.zenith.client.screens.override.main.layout;

import org.zenith.client.screens.override.main.page.MainMenuPage;
import org.zenith.utility.render.display.base.CornerRadiusF;

public final class MainMenuLayout {
   public static final float SCALE = 0.5F;
   public static final float NAVIGATION_WIDTH = px(522.0F);
   public static final float NAVIGATION_HEIGHT = px(42.0F);
   public static float LOGO_TEXT_GAP = 8.0F;
   public static final float SECTION_GAP = px(12.0F);
   public static final float CENTER_BODY_HEIGHT = px(398.0F);
   public static final float CENTER_FOOTER_HEIGHT = px(74.0F);

   public static float px(float var0) {
      return var0 * 0.5F;
   }

   public static MainMenuLayout_Frame centered(float var0, float var1, MainMenuPage var2, float var3) {
      LOGO_TEXT_GAP = 16.0F;
      float f = var3 + 48.0F + NAVIGATION_HEIGHT + SECTION_GAP + CENTER_BODY_HEIGHT + SECTION_GAP + CENTER_FOOTER_HEIGHT;
      float f1 = Math.max(px(12.0F), (var1 - f) / 2.0F);
      CornerRadiusF l11liliill1iii1xxx = centeredRect(var0, f1, var2.headerWidth(), var3);
      CornerRadiusF l11liliill1iii1x = centeredRect(var0, l11liliill1iii1xxx.y() + l11liliill1iii1xxx.height() + 48.0F, NAVIGATION_WIDTH, NAVIGATION_HEIGHT);
      CornerRadiusF l11liliill1iii1xx = centeredRect(var0, l11liliill1iii1x.y() + l11liliill1iii1x.height() + SECTION_GAP, var2.bodyWidth(), var2.bodyHeight());
      l11liliill1iii1xxx = centeredRect(var0, l11liliill1iii1xx.y() + l11liliill1iii1xx.height() + SECTION_GAP, var2.footerWidth(), var2.footerHeight());
      return new MainMenuLayout_Frame(l11liliill1iii1xxx, l11liliill1iii1x, l11liliill1iii1xx, l11liliill1iii1xxx);
   }

   public static CornerRadiusF centeredRect(float var0, float var1, float var2, float var3) {
      return new CornerRadiusF((var0 - var2) / 2.0F, var1, var2, var3);
   }
}
