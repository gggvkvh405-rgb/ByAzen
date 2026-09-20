package org.zenith.base.font;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public final class FontData {
   public FontData_AtlasData atlas;
   public FontData_MetricsData metrics;
   public List<FontData_GlyphData> glyphs;
   @SerializedName("kerning")
   public List<FontData_KerningData> kernings;

   public FontData_AtlasData atlas() {
      return this.atlas;
   }

   public FontData_MetricsData metrics() {
      return this.metrics;
   }

   public List<FontData_GlyphData> glyphs() {
      return this.glyphs;
   }

   public List<FontData_KerningData> kernings() {
      return this.kernings;
   }
}
