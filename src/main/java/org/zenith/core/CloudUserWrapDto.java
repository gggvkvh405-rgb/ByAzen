package org.zenith.core;

public record CloudUserWrapDto(CloudUserDto Trails) implements CloudResponse {
   @Override
   public String type() {
      return "config.access.granted";
   }

   public CloudUserDto HudHotbarPanel() {
      return this.Trails;
   }
}
