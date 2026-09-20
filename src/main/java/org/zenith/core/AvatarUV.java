package org.zenith.core;

public record AvatarUV(float float112, float float113, float float114, float float115) {
   public boolean PotionItemBuilder(double var1, double var3) {
      return var1 >= this.float112 && var1 <= this.float114 && var3 >= this.float113 && var3 <= this.float115;
   }

   public float x() {
      return this.float112;
   }

   public float y() {
      return this.float113;
   }

   public float float258() {
      return this.float114;
   }

   public float float259() {
      return this.float115;
   }
}
