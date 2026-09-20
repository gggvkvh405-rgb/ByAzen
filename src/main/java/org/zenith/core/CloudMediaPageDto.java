package org.zenith.core;

import java.util.List;

public record CloudMediaPageDto(List<MediaTrackInfo> FireWorkESP, boolean HandFire, CloudViewDto HitParticles) implements CloudResponse {
   public CloudMediaPageDto {
      FireWorkESP = List.copyOf(FireWorkESP);
   }

   @Override
   public String type() {
      return "chat.history";
   }

   public List<MediaTrackInfo> ChatTagParser() {
      return this.FireWorkESP;
   }

   public boolean hasMore() {
      return this.HandFire;
   }

   public CloudViewDto ProfileCacheStore() {
      return this.HitParticles;
   }
}
