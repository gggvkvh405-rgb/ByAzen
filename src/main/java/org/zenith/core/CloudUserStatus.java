package org.zenith.core;

record CloudUserStatus(CloudUserProfile KeybindsHud, boolean HudInfoBoxPrimary) {
   public CloudUserProfile AttackEntityEvent() {
      return this.KeybindsHud;
   }

   public boolean Event18Ext5() {
      return this.HudInfoBoxPrimary;
   }
}
