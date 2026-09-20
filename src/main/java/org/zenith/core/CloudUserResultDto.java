package org.zenith.core;

public record CloudUserResultDto(CloudUserDto CloudResult) implements CloudResponse {
   @Override
   public String type() {
      return "config.saved";
   }

   public CloudUserDto HudHotbarPanel() {
      return this.CloudResult;
   }
}
