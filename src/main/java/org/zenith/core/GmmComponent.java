package org.zenith.core;

public final class GmmComponent {
   public final int index;
   public final boolean mu;
   public final float scale;
   public final float mean;
   public final float std;

   GmmComponent(int var1, boolean var2, float var3, float var4, float var5) {
      this.index = var1;
      this.mu = var2;
      this.scale = var3;
      this.mean = var4;
      this.std = var5;
   }
}
