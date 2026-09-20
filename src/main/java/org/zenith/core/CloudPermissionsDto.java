package org.zenith.core;

import java.util.List;

public record CloudPermissionsDto(List<String> BotGuardEntity, List<String> SelectionOutline) implements CloudResponse {
   public CloudPermissionsDto {
      BotGuardEntity = List.copyOf(BotGuardEntity);
      SelectionOutline = List.copyOf(SelectionOutline);
   }

   @Override
   public String type() {
      return "cosmetics.access";
   }

   public List<String> ColorSetting() {
      return this.BotGuardEntity;
   }

   public List<String> StringListSetting() {
      return this.SelectionOutline;
   }
}
