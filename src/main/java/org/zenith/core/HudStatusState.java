package org.zenith.core;

import org.zenith.base.font.Font;
import org.zenith.client.screens.nlgui.style.ZenithStyle;
import org.zenith.utility.render.display.base.CustomDrawContext;

abstract class HudStatusState {
   long long142;
   final long val429;
   boolean val144 = false;
   final UiAnimation val145 = new UiAnimation(300L, Easing.StopUsingItemEvent);
   final UiAnimation val146 = new UiAnimation(300L, Easing.StopUsingItemEvent);

   HudStatusState(long var1) {
      this.long142 = System.currentTimeMillis();
      this.val429 = var1;
   }

   abstract void on23(CustomDrawContext var1, float var2, float var3, Font var4, ZenithStyle var5, float var6, HudStatusPanel var7);
}
