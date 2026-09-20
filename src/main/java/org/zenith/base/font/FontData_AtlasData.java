package org.zenith.base.font;

import com.google.gson.annotations.SerializedName;

public final class FontData_AtlasData {
   @SerializedName("distanceRange")
   public float range;
   public float width;
   public float height;

   public float range() {
      return this.range;
   }

   public float width() {
      return this.width;
   }

   public float height() {
      return this.height;
   }
}
