package org.zenith.core;

import java.util.UUID;

public record CloudFullUserDto(UUID GradientPalette, boolean MathUtils, CloudSessionDto ScreenProjection, long RandomUtils, CloudUserDto StopWatch)
   implements CloudResponse {
   @Override
   public String type() {
      return "config.upload.ticket";
   }

   public UUID GameCoordinator() {
      return this.GradientPalette;
   }

   public boolean HudElement() {
      return this.MathUtils;
   }

   public CloudSessionDto HudTabList() {
      return this.ScreenProjection;
   }

   public long HudTextPanel() {
      return this.RandomUtils;
   }

   public CloudUserDto HudHotbarPanel() {
      return this.StopWatch;
   }
}
