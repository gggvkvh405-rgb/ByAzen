package org.zenith.utility.discord;

import java.io.IOException;
import org.zenith.ZenithClient;
import org.zenith.core.ClientSession;
import org.zenith.utility.discord.utils.DiscordEventHandlers;
import org.zenith.utility.discord.utils.DiscordEventHandlers_Builder;
import org.zenith.utility.discord.utils.DiscordRPC;

public class DiscordManager {
   public final DiscordManager_DiscordDaemonThread discordDaemonThread = new DiscordManager_DiscordDaemonThread(this);
   public boolean running = true;
   public DiscordManager_DiscordInfo info = new DiscordManager_DiscordInfo("Unknown", "", "");

   public void init() {
      String s = System.getProperty("os.name").toLowerCase();
      if (!s.contains("linux") && DiscordRPC.AVAILABLE) {
         DiscordEventHandlers discordeventhandlers = new DiscordEventHandlers_Builder()
            .ready(
               var0 -> {
                  ZenithClient.on23()
                     .PotionItemBuilder()
                     .setInfo(
                        new DiscordManager_DiscordInfo(
                           var0.username, "https://cdn.discordapp.com/avatars/" + var0.userId + "/" + var0.avatar + ".png", var0.userId
                        )
                     );
                  ClientSession ii1il11l111ii11iil_ii1il11l111ii11iil = ZenithClient.on23().CommandManager();
                  String s1 = ii1il11l111ii11iil_ii1il11l111ii11iil.CloudPoller();
                  String s2 = ii1il11l111ii11iil_ii1il11l111ii11iil.EmoteManager().getName();
                  String s3 = ii1il11l111ii11iil_ii1il11l111ii11iil.getUsername();
               }
            )
            .build();
         DiscordRPC.INSTANCE.Discord_Initialize("1524110600555331776", discordeventhandlers, true, "");
         this.discordDaemonThread.start();
      }
   }

   public void stopRPC() {
      if (DiscordRPC.AVAILABLE) {
         DiscordRPC.INSTANCE.Discord_Shutdown();
      }

      this.running = false;
   }

   public void load() throws IOException {
   }

   public void setRunning(boolean var1) {
      this.running = var1;
   }

   public void setInfo(DiscordManager_DiscordInfo var1) {
      this.info = var1;
   }

   public DiscordManager_DiscordDaemonThread getDiscordDaemonThread() {
      return this.discordDaemonThread;
   }

   public boolean isRunning() {
      return this.running;
   }

   public DiscordManager_DiscordInfo getInfo() {
      return this.info;
   }
}
