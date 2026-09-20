package org.zenith.core;

public record CloudFeatureDto(String RawShaderProgram, boolean HandShaderManager) implements CloudResponse {
   @Override
   public String type() {
      return "friends.removed";
   }

   public String RoundedRectEasing() {
      return this.RawShaderProgram;
   }

   public boolean KeySetting() {
      return this.HandShaderManager;
   }
}
