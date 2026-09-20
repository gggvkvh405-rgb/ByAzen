package org.zenith.core;

import java.util.UUID;

public record CloudSessionAckDto(String string53, UUID uUID3, long long111) implements CloudResponse {
   @Override
   public String type() {
      return "player.offline";
   }

   public String userId() {
      return this.string53;
   }

   public UUID sessionId() {
      return this.uUID3;
   }

   public long OffHandManager() {
      return this.long111;
   }
}
