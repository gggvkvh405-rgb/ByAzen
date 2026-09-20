package org.zenith.base.bot.client;

public record HeadlessBots_ProxyPingSnapshot(HeadlessBots_ProxyPingStatus status, int latencyMs) {
   public String label() {
      return switch (this.status) {
         case UNKNOWN -> "--";
         case CHECKING -> this.latencyMs >= 0 ? this.latencyMs + "ms" : "--";
         case OK -> this.latencyMs + "ms";
         case FAILED -> "err";
      };
   }
}
