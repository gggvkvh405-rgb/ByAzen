package org.zenith.base.bot.view;

import net.minecraft.util.Hand;

enum BotHeldItemRenderer_HandRenderType {
   RENDER_BOTH_HANDS(true, true),
   RENDER_MAIN_HAND_ONLY(true, false),
   RENDER_OFF_HAND_ONLY(false, true);

   final boolean renderMainHand;
   final boolean renderOffHand;

   BotHeldItemRenderer_HandRenderType(boolean var3, boolean var4) {
      this.renderMainHand = var3;
      this.renderOffHand = var4;
   }

   static BotHeldItemRenderer_HandRenderType shouldOnlyRender(Hand var0) {
      return var0 == Hand.MAIN_HAND ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
   }
}
