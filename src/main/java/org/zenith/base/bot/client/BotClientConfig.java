package org.zenith.base.bot.client;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record BotClientConfig(String name, UUID uuid, String host, int port, String proxy, String brand, int protocolVersion) {
   public static final String DEFAULT_BRAND = "vanilla";
   public static final int NATIVE_PROTOCOL_VERSION = -1;

   public static BotClientConfig offline(String var0, String var1, int var2, String var3) {
      return offline(var0, var1, var2, var3, -1);
   }

   public static BotClientConfig offline(String var0, String var1, int var2, String var3, int var4) {
      return new BotClientConfig(var0, offlineUuid(var0), var1, var2, var3, "vanilla", var4);
   }

   public static UUID offlineUuid(String var0) {
      return UUID.nameUUIDFromBytes(("OfflinePlayer:" + var0).getBytes(StandardCharsets.UTF_8));
   }
}
