package org.zenith.core;

public enum SessionFlag {
   BotWorldJoinEvent("User"),
   BotPacketEvent("Admin"),
   BotRespawnEvent("Alpha");

   final String BotTickEvent;

   public String getName() {
      return this.BotTickEvent;
   }

   SessionFlag(String var3) {
      this.BotTickEvent = var3;
   }
}
