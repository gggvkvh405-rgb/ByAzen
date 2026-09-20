package org.zenith.core;

public record CloudUserRefDto(CloudUserDto CloudScope) implements CloudResponse {
   @Override
   public String type() {
      return "config.updated";
   }

   public CloudUserDto HudHotbarPanel() {
      return this.CloudScope;
   }
}
