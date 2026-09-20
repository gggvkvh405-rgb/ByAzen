package org.zenith.core;

import java.util.UUID;

public record CloudWhoAmIDto(UUID HotbarSwapper, CloudSessionDto StyledTextBuilder, CloudUserDto MenuScreenId) implements CloudResponse {
   @Override
   public String type() {
      return "config.download.ticket";
   }

   public UUID PermissionListCodec() {
      return this.HotbarSwapper;
   }

   public CloudSessionDto HudTabList() {
      return this.StyledTextBuilder;
   }

   public CloudUserDto HudHotbarPanel() {
      return this.MenuScreenId;
   }
}
