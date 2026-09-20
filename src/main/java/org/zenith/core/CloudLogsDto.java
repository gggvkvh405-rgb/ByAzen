package org.zenith.core;

import java.util.List;

public record CloudLogsDto(long long105, List<CloudLogEntryDto> list48) implements CloudResponse {
   public CloudLogsDto {
      list48 = List.copyOf(list48);
   }

   @Override
   public String type() {
      return "player.state.batch";
   }

   public long Criticals() {
      return this.long105;
   }

   public List<CloudLogEntryDto> Reach() {
      return this.list48;
   }
}
