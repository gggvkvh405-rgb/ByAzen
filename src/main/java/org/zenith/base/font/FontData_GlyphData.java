package org.zenith.base.font;

public final class FontData_GlyphData {
   public int unicode;
   public float advance;
   public FontData_BoundsData planeBounds;
   public FontData_BoundsData atlasBounds;

   public int unicode() {
      return this.unicode;
   }

   public float advance() {
      return this.advance;
   }

   public FontData_BoundsData planeBounds() {
      return this.planeBounds;
   }

   public FontData_BoundsData atlasBounds() {
      return this.atlasBounds;
   }
}
