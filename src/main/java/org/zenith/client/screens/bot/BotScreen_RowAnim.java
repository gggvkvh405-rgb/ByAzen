package org.zenith.client.screens.bot;

import org.zenith.core.Easing;
import org.zenith.core.UiAnimation;

final class BotScreen_RowAnim {
   final UiAnimation appear = new UiAnimation(220L, 0.0F, Easing.PreventActionEvent);
   final UiAnimation hover = new UiAnimation(150L, 0.0F, Easing.PreventActionEvent);
   final UiAnimation delete = new UiAnimation(160L, 0.0F, Easing.PreventActionEvent);
   final UiAnimation disc = new UiAnimation(220L, 0.0F, Easing.PreventActionEvent);
   final UiAnimation select = new UiAnimation(160L, 0.0F, Easing.PreventActionEvent);

   public BotScreen_RowAnim() {
   }
}
