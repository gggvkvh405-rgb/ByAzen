package org.zenith.client.screens.autocraft;

import org.zenith.core.ItemFilterRules;

final class AutoCraftEditorScreen_RibbonItemLayout {
   public final ItemFilterRules preset;
   public final String presetId;
   public final String label;
   public final float currentX;
   public final float y;
   public final float width;
   public final float height;
   public final float appearProgress;
   public final boolean inFilter;

   public AutoCraftEditorScreen_RibbonItemLayout(
      ItemFilterRules var1, String var2, String var3, float var4, float var5, float var6, float var7, float var8, boolean var9
   ) {
      this.preset = var1;
      this.presetId = var2;
      this.label = var3;
      this.currentX = var4;
      this.y = var5;
      this.width = var6;
      this.height = var7;
      this.appearProgress = var8;
      this.inFilter = var9;
   }
}
