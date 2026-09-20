package org.zenith.core;

import java.util.ArrayList;
import java.util.List;

final class ReconnectBackoff {
   public final String HudElementValue;
   public final List<CloudUserStatus> HudInfoBoxSecondary = new ArrayList<>();
   public final List<CloudApiClient.ItemRegistry> HudSelectedItemPanel = new ArrayList<>();
   public int HudArmorPanel;

   public ReconnectBackoff(String var1) {
      this.HudElementValue = var1;
   }
}
