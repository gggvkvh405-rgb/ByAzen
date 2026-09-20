package org.zenith.core;

public record CloudCodeDto(int CooldownTimer, String BooleanValue) implements CloudResponse {
   @Override
   public String type() {
      return "connection.close";
   }

   public int code() {
      return this.CooldownTimer;
   }

   public String Category() {
      return this.BooleanValue;
   }
}
