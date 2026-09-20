package org.zenith.core;

public record CloudConfigMetaDto(String ViewArmorDurability, String ViewModel) {
   public String userId() {
      return this.ViewArmorDurability;
   }

   public String HudInventoryPanel() {
      return this.ViewModel;
   }
}
