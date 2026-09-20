package org.zenith.client.screens.bot;

import net.minecraft.text.Text;

class BotControlScreen_1SidebarRow {
   public Text name;
   public Text score;
   public int scoreWidth;

   BotControlScreen_1SidebarRow(Text var1, Text var2, int var3) {
      this.name = var1;
      this.score = var2;
      this.scoreWidth = var3;
   }

   public int scoreWidth() {
      return this.scoreWidth;
   }

   public Text name() {
      return this.name;
   }

   public Text score() {
      return this.score;
   }
}
