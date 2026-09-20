package org.zenith.client.screens.nlgui;

import java.util.function.Supplier;
import org.zenith.ZenithClient;
import org.zenith.client.screens.nlgui.panel.api.ElementPanel;

public enum NLMenuScreen_ElementsType {
   CATEGORY("Category", "0", () -> ZenithClient.on23().NbtEditor().guiModulePanel),
   FRIENDS("Friends", "8", () -> ZenithClient.on23().NbtEditor().guiFreindsPanel),
   CONFIGS("Configs", "9", () -> ZenithClient.on23().NbtEditor().guiConfigPanel),
   INTERFACE("Interface", ":", () -> ZenithClient.on23().NbtEditor().interfacePanel),
   COSMETICS("Cosmetics", ";", () -> ZenithClient.on23().NbtEditor().cosmeticElementPanel),
   SCRIPTS("Scripts", "<", () -> ZenithClient.on23().NbtEditor().scriptsPanel);

   final String name;
   final String icon;
   final Supplier<ElementPanel> panelSupplier;

   NLMenuScreen_ElementsType(String var3, String var4, Supplier<ElementPanel> var5) {
      this.name = var3;
      this.icon = var4;
      this.panelSupplier = var5;
   }

   public String getName() {
      return this.name;
   }

   public String getIcon() {
      return this.icon;
   }

   public Supplier<ElementPanel> getPanelSupplier() {
      return this.panelSupplier;
   }
}
