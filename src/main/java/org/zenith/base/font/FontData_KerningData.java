package org.zenith.base.font;

import com.google.gson.annotations.SerializedName;

public final class FontData_KerningData {
   @SerializedName("unicode1")
   public int leftChar;
   @SerializedName("unicode2")
   public int rightChar;
   public float advance;

   public int leftChar() {
      return this.leftChar;
   }

   public int rightChar() {
      return this.rightChar;
   }

   public float advance() {
      return this.advance;
   }
}
