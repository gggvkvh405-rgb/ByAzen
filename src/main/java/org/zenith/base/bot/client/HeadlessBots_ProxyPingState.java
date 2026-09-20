package org.zenith.base.bot.client;

final class HeadlessBots_ProxyPingState {
   public volatile HeadlessBots_ProxyPingStatus status = HeadlessBots_ProxyPingStatus.UNKNOWN;
   public volatile int latencyMs = -1;
   public volatile long lastStartedAt;
   public volatile boolean inFlight;

   public HeadlessBots_ProxyPingState() {
   }

   public boolean inFlight() {
      return this.inFlight;
   }

   public long lastStartedAt() {
      return this.lastStartedAt;
   }

   public int latencyMs() {
      return this.latencyMs;
   }

   public HeadlessBots_ProxyPingStatus status() {
      return this.status;
   }
}
