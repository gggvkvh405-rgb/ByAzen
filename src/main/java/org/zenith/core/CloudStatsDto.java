package org.zenith.core;

import java.util.List;

public record CloudStatsDto(long long103, List<CloudStatEntryDto> list47) implements CloudResponse {
   public CloudStatsDto {
      list47 = List.copyOf(list47);
   }

   @Override
   public String type() {
      return "player.inventory.batch";
   }

   public long Criticals() {
      return this.long103;
   }

   public List<CloudStatEntryDto> FakeLag() {
      return this.list47;
   }
}
