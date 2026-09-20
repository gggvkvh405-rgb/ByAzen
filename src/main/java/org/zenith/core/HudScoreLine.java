package org.zenith.core;

import net.minecraft.text.Text;

final class HudScoreLine {
   public final Text text8;
   public final Text text9;
   public final int int207;

   public HudScoreLine(Text var1, Text var2, int var3) {
      this.text8 = var1;
      this.text9 = var2;
      this.int207 = var3;
   }

   public Text name() {
      return this.text8;
   }

   public Text score() {
      return this.text9;
   }

   public int scoreWidth() {
      return this.int207;
   }
}
