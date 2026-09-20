package org.zenith.core;

import java.util.UUID;

public record CloudLoginDto(UUID ClientProvider, CloudSessionDto ClientWindowProvider, long RenderHook) implements CloudResponse {
   @Override
   public String type() {
      return "config.preview.ticket";
   }

   public UUID PermissionListCodec() {
      return this.ClientProvider;
   }

   public CloudSessionDto HudTabList() {
      return this.ClientWindowProvider;
   }

   public long HudTextPanel() {
      return this.RenderHook;
   }
}
