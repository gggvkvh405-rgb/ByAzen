package org.zenith.utility.discord.utils;

import com.sun.jna.Native;
import java.lang.reflect.Proxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class DiscordRPC_Loader {
   private static final Logger LOGGER = LoggerFactory.getLogger("Zenith/DiscordRPC");
   static boolean available;

   private DiscordRPC_Loader() {
   }

   static DiscordRPC load() {
      try {
         DiscordRPC rpc = Native.load("discord-rpc", DiscordRPC.class);
         available = true;
         return rpc;
      } catch (Throwable error) {
         available = false;
         LOGGER.warn("Native library 'discord-rpc' is unavailable; Rich Presence is disabled ({})", error.getClass().getSimpleName());
         return unavailableClient();
      }
   }

   private static DiscordRPC unavailableClient() {
      return (DiscordRPC)Proxy.newProxyInstance(
         DiscordRPC.class.getClassLoader(),
         new Class[]{DiscordRPC.class},
         (proxy, method, arguments) -> {
            if (method.getDeclaringClass() != Object.class) {
               return null;
            }

            return switch (method.getName()) {
               case "hashCode" -> System.identityHashCode(proxy);
               case "equals" -> arguments != null && arguments.length == 1 && proxy == arguments[0];
               case "toString" -> "DiscordRPC(unavailable)";
               default -> null;
            };
         }
      );
   }
}
