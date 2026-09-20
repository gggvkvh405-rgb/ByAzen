package org.zenith.core;

public record CloudConfigDetailsDto(CloudUserDto PostProcessPass, CloudConfigMetaDto ShaderESP, CloudTagDto FrameGraphPass, long likeCount, boolean liked) {
   public CloudConfigDetailsDto {
      if (likeCount < 0L) {
         throw new IllegalArgumentException("likeCount must be non-negative");
      }
   }

   public CloudUserDto HudHotbarPanel() {
      return this.PostProcessPass;
   }

   public CloudConfigMetaDto HudElementValue() {
      return this.ShaderESP;
   }

   public CloudTagDto HudInfoBoxSecondary() {
      return this.FrameGraphPass;
   }

   public long HudSelectedItemPanel() {
      return this.likeCount;
   }

   public boolean HudArmorPanel() {
      return this.liked;
   }
}
