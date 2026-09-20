package org.zenith.core;

public record CloudErrorDto(String XrayBypass, String AntiInvisible) implements CloudResponse {
   @Override
   public String type() {
      return "auth.failure";
   }

   public String PlayerStateService() {
      return this.XrayBypass;
   }

   public String message() {
      return this.AntiInvisible;
   }
}
