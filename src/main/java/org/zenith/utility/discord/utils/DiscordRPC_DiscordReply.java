package org.zenith.utility.discord.utils;

public enum DiscordRPC_DiscordReply {
   NO(0),
   IGNORE(2),
   YES(1);

   public final int reply;

   DiscordRPC_DiscordReply(int var3) {
      this.reply = var3;
   }

   public static DiscordRPC_DiscordReply[] getReplies() {
      return new DiscordRPC_DiscordReply[]{NO, YES, IGNORE};
   }
}
