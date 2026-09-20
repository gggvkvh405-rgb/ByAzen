package org.zenith.core;

public final class IntGridCell {
   public final int int153;
   public final int int154;
   public final int[] val069;
   public final int[] call106;
   public final int int155;
   public final int int156;

   public IntGridCell(int var1, int var2, int[] var3, int[] var4) {
      this.int153 = var1;
      this.int154 = var2;
      this.val069 = var3;
      this.call106 = var4;
      int i = 0;
      int j = 0;

      for (int k = 0; k < var3.length; k++) {
         i += var3[k];
         j += var4[k];
      }

      this.int155 = i;
      this.int156 = j;
   }

   public int length() {
      return this.val069.length;
   }
}
