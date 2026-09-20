package org.zenith.client.screens.emote;

class EmoteFavoriteScreen_Layout {
   public float cardWidth;
   public float closeX;
   public float closeY;
   public float contentX;
   public float gridHeight;
   public float gridWidth;
   public float gridX;
   public float gridY;
   public float panelHeight;
   public float panelWidth;
   public float panelX;
   public float panelY;
   public float previewWidth;
   public float previewX;
   public float scrollBarX;
   public float searchWidth;
   public float searchX;
   public float searchY;

   public EmoteFavoriteScreen_Layout(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17,
      float var18
   ) {
      this.panelX = var1;
      this.panelY = var2;
      this.panelWidth = var3;
      this.panelHeight = var4;
      this.contentX = var5;
      this.searchX = var6;
      this.searchY = var7;
      this.searchWidth = var8;
      this.gridX = var9;
      this.gridY = var10;
      this.gridWidth = var11;
      this.gridHeight = var12;
      this.cardWidth = var13;
      this.scrollBarX = var14;
      this.previewX = var15;
      this.previewWidth = var16;
      this.closeX = var17;
      this.closeY = var18;
   }

   public float centerX() {
      return this.panelX + this.panelWidth / 2.0F;
   }

   public float centerY() {
      return this.panelY + this.panelHeight / 2.0F;
   }
}
