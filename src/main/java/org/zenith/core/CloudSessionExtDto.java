package org.zenith.core;

import java.util.Objects;

public record CloudSessionExtDto(CloudSessionDto WorldUtils, String DrawContextSink, long GameService) {
   public CloudSessionExtDto {
      Objects.requireNonNull(WorldUtils, "ticket");
      Objects.requireNonNull(DrawContextSink, "sha256");
   }

   public CloudSessionDto HudTabList() {
      return this.WorldUtils;
   }

   public String RotationSnapStrategy() {
      return this.DrawContextSink;
   }

   public long HudElementMessages() {
      return this.GameService;
   }
}
