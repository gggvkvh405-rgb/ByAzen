package org.zenith.client.screens.override.main.component;

import org.zenith.core.Easing;
import org.zenith.core.UiAnimation;
import org.zenith.utility.render.display.base.AnimationValue;

final class MenuNavigationBar_Entry {
   public final MenuNavigationBar_Item item;
   public final AnimationValue bounds = new AnimationValue(0.0F, 0.0F, 0.0F, 0.0F);
   public final UiAnimation activity = new UiAnimation(220L, Easing.EventMixin_modifySetScreenArg);
   public final UiAnimation hover = new UiAnimation(180L, Easing.HotbarInputEvent);

   public MenuNavigationBar_Entry(MenuNavigationBar_Item var1) {
      this.item = var1;
   }
}
