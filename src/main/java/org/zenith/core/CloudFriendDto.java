package org.zenith.core;

public record CloudFriendDto(CloudBadgeDto RoundedRectBatch, boolean FillShader) {
   public CloudBadgeDto TargetInterpolator() {
      return this.RoundedRectBatch;
   }

   public boolean Event18Ext5() {
      return this.FillShader;
   }
}
