package org.zenith.core;

import java.util.UUID;

public record CloudAccountDto(UUID HudPreviewItem, String HudPreviewRenderQueue) implements CloudResponse {
   @Override
   public String type() {
      return "friends.request.declined";
   }

   public UUID Event05() {
      return this.HudPreviewItem;
   }

   public String ThemeColorCycler() {
      return this.HudPreviewRenderQueue;
   }
}
