package org.zenith.core;

import java.util.List;

public record CloudConfigsPageDto(
   String WorldParticles, int WorldTweaks, List<CloudConfigDetailsDto> BlockOverLay, boolean BoxShaderRenderer, Integer Predictions
) implements CloudResponse {
   public CloudConfigsPageDto {
      BlockOverLay = List.copyOf(BlockOverLay);
   }

   @Override
   public String type() {
      return "config.catalog";
   }

   public String GmmModel() {
      return this.WorldParticles;
   }

   public int MotorIntentModel() {
      return this.WorldTweaks;
   }

   public List<CloudConfigDetailsDto> configs() {
      return this.BlockOverLay;
   }

   public boolean hasMore() {
      return this.BoxShaderRenderer;
   }

   public Integer HudElementMessage() {
      return this.Predictions;
   }
}
