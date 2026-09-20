package org.zenith.client.screens.nlgui.panel;

public enum InterfacePanel_InterfaceCategory {
   THEME("Theme"),
   HUD("Hud");

   final String name;

   InterfacePanel_InterfaceCategory(String var3) {
      this.name = var3;
   }

   public String getName() {
      return this.name;
   }
}
