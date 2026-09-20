package org.zenith.utility.discord;

import net.minecraft.client.MinecraftClient;
import org.zenith.ZenithClient;
import org.zenith.utility.discord.utils.DiscordRPC;

class DiscordManager_DiscordDaemonThread extends Thread {
   public final DiscordManager this_0;

   public DiscordManager_DiscordDaemonThread(DiscordManager var1) {
      this.this_0 = var1;
   }

   @Override
   public void run() {
      this.setName("Discord-RPC");

      try {
         for (MinecraftClient minecraftclient = MinecraftClient.getInstance();
            minecraftclient == null || minecraftclient.getTextureManager() == null;
            minecraftclient = MinecraftClient.getInstance()
         ) {
            Thread.sleep(500L);
         }

         while (ZenithClient.on23().PotionItemBuilder().isRunning()) {
            DiscordRPC.INSTANCE.Discord_RunCallbacks();
            this.this_0.load();
            Thread.sleep(1500L);
         }
      } catch (Exception exception) {
         this.this_0.stopRPC();
      }

      super.run();
   }
}
