package org.zenith.core;

import java.util.UUID;

public record CloudViewDto(long Crosshair, UUID EntityESP) {
   public long RenderTickEvent() {
      return this.Crosshair;
   }

   public UUID JumpEvent() {
      return this.EntityESP;
   }
}
