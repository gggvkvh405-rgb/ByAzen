package org.zenith.base.bot.net;

import java.util.Locale;

public enum BotProxy_Type {
   SOCKS5,
   SOCKS4,
   HTTP;

   public static BotProxy_Type fromScheme(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String s = var0.toLowerCase(Locale.ROOT);

         return switch (s) {
            case "socks", "socks5" -> SOCKS5;
            case "socks4" -> SOCKS4;
            case "http", "https" -> HTTP;
            default -> throw new IllegalArgumentException("Unsupported proxy type: " + var0);
         };
      } else {
         return SOCKS5;
      }
   }
}
