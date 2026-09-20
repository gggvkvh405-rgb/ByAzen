package org.zenith.core;

public record CloudFeaturedDto(String ColorUtils, CloudRelationDto RenderCommandQueue) implements CloudResponse {
   @Override
   public String type() {
      return "friends.request.created";
   }

   public String NumberSetting() {
      return this.ColorUtils;
   }

   public CloudRelationDto TextSetting() {
      return this.RenderCommandQueue;
   }
}
