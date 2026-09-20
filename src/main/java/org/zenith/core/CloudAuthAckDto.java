package org.zenith.core;

import java.util.UUID;

public record CloudAuthAckDto(String string47, UUID uUID2, long long109) implements CloudResponse {
   @Override
   public String type() {
      return "player.online";
   }

   public String userId() {
      return this.string47;
   }

   public UUID sessionId() {
      return this.uUID2;
   }

   public long OffHandManager() {
      return this.long109;
   }
}
