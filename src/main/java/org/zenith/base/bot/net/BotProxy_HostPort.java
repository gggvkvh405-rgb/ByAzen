package org.zenith.base.bot.net;

import java.util.Arrays;

record BotProxy_HostPort(String host, int port, String[] tail) {
   public static BotProxy_HostPort parse(String var0) {
      if (var0 == null || var0.isBlank()) {
         throw new IllegalArgumentException("Invalid proxy address");
      }

      if (var0.startsWith("[")) {
         int i = var0.indexOf(93);
         if (i >= 0 && i + 1 < var0.length() && var0.charAt(i + 1) == ':') {
            String s = var0.substring(1, i);
            String[] astring1 = var0.substring(i + 2).split(":", -1);
            return create(s, astring1);
         } else {
            throw new IllegalArgumentException("Invalid proxy address");
         }
      } else {
         String[] astring = var0.split(":", -1);
         if (astring.length < 2) {
            throw new IllegalArgumentException("Invalid proxy address");
         } else {
            return create(astring[0], Arrays.copyOfRange(astring, 1, astring.length));
         }
      }
   }

   public static BotProxy_HostPort create(String var0, String[] var1) {
      if (var0 != null && !var0.isBlank() && var1.length != 0) {
         int i;
         try {
            i = Integer.parseInt(var1[0]);
         } catch (NumberFormatException numberformatexception) {
            throw new IllegalArgumentException("Invalid proxy port", numberformatexception);
         }

         return new BotProxy_HostPort(var0, BotProxy.validatePort(i), Arrays.copyOfRange(var1, 1, var1.length));
      } else {
         throw new IllegalArgumentException("Invalid proxy address");
      }
   }
}
