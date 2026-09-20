package org.zenith.event;

import net.minecraft.text.Text;

public class GameMessageEvent extends CancellableEvent {
   public Text text;
   public boolean boolean150;

   public GameMessageEvent(Text var1) {
      this.text = var1;
   }

   public void on23(Text var1) {
      this.boolean150 = true;
      this.text = var1;
      this.cancel();
   }

   public Text InventorySetting() {
      return this.text;
   }

   public boolean XrayBypass() {
      return this.boolean150;
   }
}
