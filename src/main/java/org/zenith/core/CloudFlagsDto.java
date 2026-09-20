package org.zenith.core;

import java.util.UUID;

public record CloudFlagsDto(UUID BlockFinder, boolean I1Type, boolean ScoreboardHelper) implements CloudResponse {
   @Override
   public String type() {
      return "config.deleted";
   }

   public UUID PermissionListCodec() {
      return this.BlockFinder;
   }

   public boolean HudMediaPanel() {
      return this.I1Type;
   }

   public boolean HudStatusPanel() {
      return this.ScoreboardHelper;
   }
}
