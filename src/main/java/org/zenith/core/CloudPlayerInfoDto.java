package org.zenith.core;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record CloudPlayerInfoDto(UUID Arrows, CloudBadgeDto BetterMinecraft, long BlockESP, List<String> CameraTweaks, CloudPermissionsDto Cape)
   implements CloudResponse {
   public CloudPlayerInfoDto {
      Objects.requireNonNull(Arrows, "sessionId");
      Objects.requireNonNull(BetterMinecraft, "user");
      CameraTweaks = List.copyOf(CameraTweaks);
      Objects.requireNonNull(Cape, "cosmetics");
   }

   @Override
   public String type() {
      return "auth.success";
   }

   public UUID sessionId() {
      return this.Arrows;
   }

   public CloudBadgeDto TargetInterpolator() {
      return this.BetterMinecraft;
   }

   public long TrajectoryDataset() {
      return this.BlockESP;
   }

   public List<String> MovementSimulator() {
      return this.CameraTweaks;
   }

   public CloudPermissionsDto MotionSampleStore() {
      return this.Cape;
   }
}
