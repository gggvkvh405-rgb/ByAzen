package org.zenith.core;

class HudInfoBoxSecondaryState {
   public final String string30;
   public final boolean boolean78;

   public HudInfoBoxSecondaryState(String var1, boolean var2) {
      this.string30 = var1;
      this.boolean78 = var2;
   }

   public static HudInfoBoxSecondaryState PlayerMoveEvent(String var0) {
      return new HudInfoBoxSecondaryState(var0, false);
   }

   public static HudInfoBoxSecondaryState MovementInputEvent(String var0) {
      return new HudInfoBoxSecondaryState(var0, true);
   }
}
