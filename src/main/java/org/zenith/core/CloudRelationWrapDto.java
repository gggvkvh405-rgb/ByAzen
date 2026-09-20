package org.zenith.core;

public record CloudRelationWrapDto(CloudRelationDto RectBatch) implements CloudResponse {
   @Override
   public String type() {
      return "friends.request.received";
   }

   public CloudRelationDto TextSetting() {
      return this.RectBatch;
   }
}
