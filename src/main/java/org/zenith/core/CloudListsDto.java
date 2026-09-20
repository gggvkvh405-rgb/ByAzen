package org.zenith.core;

import java.util.List;

public record CloudListsDto(List<String> list52, List<String> list53) implements CloudResponse {
   public CloudListsDto {
      list52 = List.copyOf(list52);
      list53 = List.copyOf(list53);
   }

   @Override
   public String type() {
      return "player.watch.result";
   }

   public List<String> RotationRecorder() {
      return this.list52;
   }

   public List<String> TargetPearl() {
      return this.list53;
   }
}
