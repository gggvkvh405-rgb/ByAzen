package org.zenith.core;

import java.util.Arrays;
import java.util.Random;

public final class NoiseGenerator {
   public static final float float272 = -8.0F;
   public static final float float273 = 4.0F;
   public final GmmModel var126;
   public final Random random5 = new Random();
   public final float[] call045;
   public final float[] call169;
   public final float[] call044;
   public final float[] call124;
   public final float[] call046;
   public final float[] call028;
   public final float[] call012;
   public final float[] call075;
   public final float[] call076;
   public final float[] call047;
   public final float[] call125;
   public final float[] call127;
   public final float[] call037;
   public final double[] call015;
   public final int[] call097;
   public final float[] call096;
   public final float[] call059;
   public final float[] call060;
   public final float[] call058;
   public final float[] call098;
   public boolean boolean174;
   public int int392;
   public int int393;

   public NoiseGenerator(GmmModel var1) {
      this.var126 = var1;
      int i = Math.max(this.var126.int343, this.var126.int344);
      this.call045 = new float[this.var126.int341];
      this.call169 = new float[this.var126.int341];
      this.call044 = new float[this.var126.int342];
      this.call124 = new float[this.var126.int340];
      this.call046 = new float[this.var126.int342];
      this.call028 = new float[4 * this.var126.int341];
      this.call012 = new float[4 * this.var126.int341];
      this.call075 = new float[this.var126.int342 + 2 * this.var126.int347 + this.var126.int348];
      this.call076 = new float[3 * this.var126.int342];
      this.call047 = new float[3 * this.var126.int342];
      this.call125 = new float[this.var126.int342 + this.var126.int347];
      this.call127 = new float[this.var126.int342];
      this.call037 = new float[i];
      this.call015 = new double[i];
      this.call097 = new int[2 * this.var126.int339];
      this.call096 = new float[this.call097.length];
      this.call059 = new float[this.var126.int348];
      this.call060 = new float[this.var126.int342 + 2 * this.var126.int347];
      this.call058 = new float[this.var126.int349];
      this.call098 = new float[2 * this.var126.int348];
      this.reset();
   }

   public void reset() {
      Arrays.fill(this.call045, 0.0F);
      Arrays.fill(this.call169, 0.0F);
      Arrays.fill(this.call044, 0.0F);
      Arrays.fill(this.call059, 0.0F);
      this.boolean174 = false;
      this.int392 = this.var126.int345;
      this.int393 = this.var126.int346;
   }

   public int[][] ColorAnimator(float[] var1) {
      return this.on23(var1, false);
   }

   public int[][] ItemRegistry(float[] var1) {
      return this.on23(var1, true);
   }

   public int[][] on23(float[] var1, boolean var2) {
      this.ItemSpec(var1);
      this.string47();
      on23(this.var126.call066, this.call045, this.var126.call067, this.call046);
      NbtItemSpec(this.call046);
      int[][] aint = new int[this.var126.int339][2];

      for (int i = 0; i < this.var126.int339; i++) {
         int j = 0;
         System.arraycopy(this.call046, 0, this.call075, j, this.call046.length);
         j += this.call046.length;
         System.arraycopy(this.var126.call088[this.int392], 0, this.call075, j, this.var126.int347);
         j += this.var126.int347;
         System.arraycopy(this.var126.call153[this.int393], 0, this.call075, j, this.var126.int347);
         j += this.var126.int347;
         System.arraycopy(this.call059, 0, this.call075, j, this.var126.int348);
         this.uUID2();
         on23(this.var126.call054, this.call044, this.var126.call042, this.call037);
         int k = var2 ? on23(this.call037, this.var126.int343) : this.UiAnimation(this.call037, this.var126.int343);
         int l = 2 * i;
         this.call097[l] = k;
         this.call096[l] = this.call037[k];
         System.arraycopy(this.call044, 0, this.call125, 0, this.var126.int342);
         System.arraycopy(this.var126.call088[k], 0, this.call125, this.var126.int342, this.var126.int347);
         on23(this.var126.call087, this.call125, this.var126.call033, this.call127);
         NbtItemSpec(this.call127);
         on23(this.var126.call154, this.call127, this.var126.call086, this.call037);
         int i1 = var2 ? on23(this.call037, this.var126.int344) : this.UiAnimation(this.call037, this.var126.int344);
         this.call097[l + 1] = i1;
         this.call096[l + 1] = this.call037[i1];
         aint[i][0] = var2 ? this.Easing(0, k) : this.UiAnimation(0, k);
         aint[i][1] = var2 ? this.Easing(1, i1) : this.UiAnimation(1, i1);
         this.int392 = k;
         this.int393 = i1;
         boolean flag = k == this.var126.int345 && i1 == this.var126.int346;
         if (this.boolean174 && flag) {
            this.boolean174 = false;
            Arrays.fill(this.call059, 0.0F);
         } else if (!this.boolean174 && !flag) {
            this.boolean174 = true;
            this.on23(k, i1, var2);
         }
      }

      return aint;
   }

   public void on23(int var1, int var2, boolean var3) {
      int i = 0;
      System.arraycopy(this.call046, 0, this.call060, i, this.call046.length);
      i += this.call046.length;
      System.arraycopy(this.var126.call088[var1], 0, this.call060, i, this.var126.int347);
      i += this.var126.int347;
      System.arraycopy(this.var126.call153[var2], 0, this.call060, i, this.var126.int347);
      on23(this.var126.call043, this.call060, this.var126.call056, this.call058);
      NbtItemSpec(this.call058);
      on23(this.var126.call068, this.call058, this.var126.call184, this.call098);

      for (int j = 0; j < this.var126.int348; j++) {
         if (var3) {
            this.call059[j] = this.call098[j];
         } else {
            float f = Math.max(-8.0F, Math.min(4.0F, this.call098[this.var126.int348 + j]));
            this.call059[j] = this.call098[j] + (float)(Math.exp(0.5 * f) * this.random5.nextGaussian());
         }
      }
   }

   public void ItemSpec(float[] var1) {
      for (int i = 0; i < this.var126.call109.length; i++) {
         GmmComponent l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil = this.var126.call109[i];
         double d0 = var1[l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.index];
         if (l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.mu) {
            d0 = Math.signum(d0) * Math.log1p(Math.abs(d0) / l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.scale);
         }

         this.call124[i] = (float)((d0 - l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.mean) / l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.std);
      }
   }

   public void string47() {
      on23(this.var126.call148, this.call124, this.var126.call018, this.call028);
      on23(this.var126.call053, this.call045, this.var126.call151, this.call012);
      int i = this.var126.int341;

      for (int j = 0; j < i; j++) {
         double d0 = TextScanner(this.call028[j] + this.call012[j]);
         double d1 = TextScanner(this.call028[i + j] + this.call012[i + j]);
         double d2 = Math.tanh(this.call028[2 * i + j] + this.call012[2 * i + j]);
         double d3 = TextScanner(this.call028[3 * i + j] + this.call012[3 * i + j]);
         double d4 = d1 * this.call169[j] + d0 * d2;
         this.call169[j] = (float)d4;
         this.call045[j] = (float)(d3 * Math.tanh(d4));
      }
   }

   public void uUID2() {
      on23(this.var126.call113, this.call075, this.var126.call150, this.call076);
      on23(this.var126.call055, this.call044, this.var126.call115, this.call047);
      int i = this.var126.int342;

      for (int j = 0; j < i; j++) {
         double d0 = TextScanner(this.call076[j] + this.call047[j]);
         double d1 = TextScanner(this.call076[i + j] + this.call047[i + j]);
         double d2 = Math.tanh(this.call076[2 * i + j] + d0 * this.call047[2 * i + j]);
         this.call044[j] = (float)((1.0 - d1) * d2 + d1 * this.call044[j]);
      }
   }

   public int[] long109() {
      return (int[])this.call097.clone();
   }

   public float long105() {
      return this.call096[0];
   }

   public double list48() {
      return TextScanner(this.call045);
   }

   public double string45() {
      return TextScanner(this.call044);
   }

   public double long106() {
      return TextScanner(this.call059);
   }

   public boolean jsonObject2() {
      return this.boolean174;
   }

   public static double TextScanner(float[] var0) {
      double d0 = 0.0;

      for (float f : var0) {
         d0 += (double)f * f;
      }

      return Math.sqrt(d0);
   }

   public static void on23(float[][] var0, float[] var1, float[] var2, float[] var3) {
      for (int i = 0; i < var0.length; i++) {
         double d0 = var2[i];

         for (int j = 0; j < var0[i].length; j++) {
            d0 += var0[i][j] * var1[j];
         }

         var3[i] = (float)d0;
      }
   }

   public static void NbtItemSpec(float[] var0) {
      for (int i = 0; i < var0.length; i++) {
         var0[i] = (float)Math.tanh(var0[i]);
      }
   }

   public static double TextScanner(double var0) {
      return 1.0 / (1.0 + Math.exp(-var0));
   }

   public static int on23(float[] var0, int var1) {
      int i = 0;

      for (int j = 1; j < var1; j++) {
         if (var0[j] > var0[i]) {
            i = j;
         }
      }

      return i;
   }

   public int UiAnimation(float[] var1, int var2) {
      double d0 = Math.max(this.var126.float215, 1.0E-6);
      double d1 = Double.NEGATIVE_INFINITY;

      for (int i = 0; i < var2; i++) {
         d1 = Math.max(d1, var1[i] / d0);
      }

      double d3 = 0.0;

      for (int j = 0; j < var2; j++) {
         this.call015[j] = Math.exp(var1[j] / d0 - d1);
         d3 += this.call015[j];
      }

      for (int l = 0; l < var2; l++) {
         this.call015[l] = this.call015[l] / d3;
      }

      if (this.var126.float216 < 1.0F) {
         this.CloudApiClient(var2);
      }

      double d4 = this.random5.nextDouble();
      double d2 = 0.0;

      for (int k = 0; k < var2; k++) {
         d2 += this.call015[k];
         if (d4 <= d2) {
            return k;
         }
      }

      return var2 - 1;
   }

   public void CloudApiClient(int var1) {
      Integer[] ainteger = new Integer[var1];

      for (int i = 0; i < var1; i++) {
         ainteger[i] = i;
      }

      Arrays.sort(ainteger, (var1x, var2x) -> Double.compare(this.call015[var2x], this.call015[var1x]));
      double d0 = 0.0;
      boolean flag = false;
      Integer[] ainteger1 = ainteger;
      int j = ainteger.length;

      for (int k = 0; k < j; k++) {
         int l = ainteger1[k];
         if (flag) {
            this.call015[l] = 0.0;
         } else {
            d0 += this.call015[l];
            if (d0 >= this.var126.float216) {
               flag = true;
            }
         }
      }

      for (int i1 = 0; i1 < var1; i1++) {
         this.call015[i1] = this.call015[i1] / d0;
      }
   }

   public int UiAnimation(int var1, int var2) {
      int i = this.var126.call051[var1][var2];
      int j = this.var126.call081[var1][var2];
      if (i == j) {
         return i;
      }

      int k = Math.min(Math.abs(i), Math.abs(j));
      int l = Math.max(Math.abs(i), Math.abs(j));
      double d0 = Math.log(k) + this.random5.nextDouble() * (Math.log(l + 1.0) - Math.log(k));
      int i1 = Math.max(k, Math.min(l, (int)Math.exp(d0)));
      return i > 0 ? i1 : -i1;
   }

   public int Easing(int var1, int var2) {
      int i = this.var126.call051[var1][var2];
      int j = this.var126.call081[var1][var2];
      if (i == j) {
         return i;
      }

      int k = Math.min(Math.abs(i), Math.abs(j));
      int l = Math.max(Math.abs(i), Math.abs(j));
      int i1 = Math.max(k, Math.min(l, (int)Math.round(Math.sqrt((double)k * l))));
      return i > 0 ? i1 : -i1;
   }
}
