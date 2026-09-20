package org.zenith.core;

import java.util.Random;

public class GradientPalette {
   public final int int445;
   public final int int446;
   public final Random random9 = new Random();

   public GradientPalette(int var1, int var2) {
      if (var1 > var2) {
         throw new IllegalArgumentException("Start must be less than or equal to endInclusive");
      }

      this.int445 = var1;
      this.int446 = var2;
   }

   public int var11922() {
      return this.int445 + this.random9.nextInt(this.int446 - this.int445 + 1);
   }

   @Override
   public String toString() {
      return this.int445 + ".." + this.int446;
   }

   public int var11923() {
      return this.int445;
   }

   public int var11924() {
      return this.int446;
   }
}
