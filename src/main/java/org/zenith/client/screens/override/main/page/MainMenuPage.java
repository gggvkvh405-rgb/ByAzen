package org.zenith.client.screens.override.main.page;

import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.override.main.layout.MainMenuLayout;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public interface MainMenuPage {
   default float headerWidth() {
      return MainMenuLayout.NAVIGATION_WIDTH;
   }

   float bodyWidth();

   float bodyHeight();

   default float footerWidth() {
      return this.bodyWidth();
   }

   float footerHeight();

   void renderBody(HudDrawContext var1, float var2, float var3, CornerRadiusF var4, ZenithStyle var5);

   void renderFooter(HudDrawContext var1, float var2, float var3, CornerRadiusF var4, ZenithStyle var5);

   default boolean mouseClicked(double var1, double var3, int var5) {
      return false;
   }
}
