package org.zenith.core;

public class SeqStepB implements SequenceStep {
   public final int int168;
   public int int169;

   public SeqStepB(int var1) {
      this.int168 = var1 - 1;
   }

   @Override
   public boolean CloudApiClient(int var1, int var2) {
      return var1 >= var2 && this.int169 < this.int168;
   }

   @Override
   public void double83() {
      this.int169++;
   }

   @Override
   public boolean ImageEncoder() {
      return this.int169 >= this.int168;
   }
}
