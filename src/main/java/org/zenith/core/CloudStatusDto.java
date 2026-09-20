package org.zenith.core;

public record CloudStatusDto(String ImageEncoder, boolean BlurRenderer) implements CloudResponse {
   @Override
   public String type() {
      return "friends.remove.completed";
   }

   public String RoundedRectEasing() {
      return this.ImageEncoder;
   }

   public boolean KeySetting() {
      return this.BlurRenderer;
   }
}
