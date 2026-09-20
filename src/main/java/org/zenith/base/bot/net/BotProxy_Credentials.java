package org.zenith.base.bot.net;

import java.util.Arrays;

record BotProxy_Credentials(String username, String password) {
   public static final BotProxy_Credentials EMPTY = new BotProxy_Credentials(null, null);

   public static BotProxy_Credentials parse(String var0) {
      if (var0 != null && !var0.isBlank()) {
         int i = var0.indexOf(58);
         return i < 0 ? new BotProxy_Credentials(var0, null) : new BotProxy_Credentials(var0.substring(0, i), var0.substring(i + 1));
      } else {
         return EMPTY;
      }
   }

   public static BotProxy_Credentials fromTail(String[] var0) {
      if (var0.length != 0 && !var0[0].isBlank()) {
         return var0.length == 1
            ? new BotProxy_Credentials(var0[0], null)
            : new BotProxy_Credentials(var0[0], String.join(":", Arrays.copyOfRange(var0, 1, var0.length)));
      } else {
         return EMPTY;
      }
   }
}
