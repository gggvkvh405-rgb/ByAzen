package org.zenith.client.screens.override.main.page;

import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.client.screens.override.main.layout.MainMenuLayout;
import org.zenith.utility.render.display.base.CornerRadiusF;
import org.zenith.utility.render.display.base.HudDrawContext;

public final class SingleWorldMenuPage implements MainMenuPage {
   @Override
   public float bodyWidth() {
      return MainMenuLayout.px(522.0F);
   }

   @Override
   public float bodyHeight() {
      return MainMenuLayout.px(398.0F);
   }

   @Override
   public float footerHeight() {
      return 0.0F;
   }

   @Override
   public void renderBody(HudDrawContext var1, float var2, float var3, CornerRadiusF var4, ZenithStyle var5) {
   }

   @Override
   public void renderFooter(HudDrawContext var1, float var2, float var3, CornerRadiusF var4, ZenithStyle var5) {
   }
}
