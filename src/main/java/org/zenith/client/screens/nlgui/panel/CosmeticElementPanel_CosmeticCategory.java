package org.zenith.client.screens.nlgui.panel;

public enum CosmeticElementPanel_CosmeticCategory {
   HEAD("Head"),
   MODELS("Models"),
   WEAPONS("Weapons"),
   PETS("Pets"),
   SETTINGS("Settings");

   final String name;

   CosmeticElementPanel_CosmeticCategory(String var3) {
      this.name = var3;
   }

   public String getName() {
      return this.name;
   }
}
