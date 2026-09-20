package org.zenith.base.font;

public final class FontData_MetricsData {
   public float lineHeight;
   public float ascender;
   public float descender;

   public float lineHeight() {
      return this.lineHeight;
   }

   public float ascender() {
      return this.ascender;
   }

   public float descender() {
      return this.descender;
   }

   public float baselineHeight() {
      return this.lineHeight + this.descender;
   }
}
