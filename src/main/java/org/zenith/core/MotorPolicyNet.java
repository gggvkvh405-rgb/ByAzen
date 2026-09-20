package org.zenith.core;

import java.util.Arrays;
import java.util.Random;

public final class MotorPolicyNet implements NeuralProvider {
   public final GmmModel p;
   public final float[] plannerH;
   public final float[] plannerC;
   public final float[] motorH;
   public final Random random = new Random();
   public int previousYaw;
   public int previousPitch;
   public int tick;
   public float[] cachedIntent;

   public MotorPolicyNet(GmmModel var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("policy");
      }

      this.p = var1;
      this.plannerH = new float[this.p.int341];
      this.plannerC = new float[this.p.int341];
      this.motorH = new float[this.p.int342];
      this.previousYaw = this.p.int345;
      this.previousPitch = this.p.int346;
      this.cachedIntent = new float[this.p.int342];
   }

   @Override
   public String list47() {
      return "hierarchical_motor_v1";
   }

   @Override
   public int[] on23(float[] var1) {
      if (var1 == null) {
         return new int[]{0, 0};
      }

      float[] afloat = this.normalize(var1);
      int i = Math.max(1, this.p.int339);
      if (this.tick % i == 0) {
         lstm(afloat, this.plannerH, this.plannerC, this.p.call148, this.p.call053, this.p.call018, this.p.call151);
         this.cachedIntent = denseTanh(this.plannerH, this.p.call066, this.p.call067);
      }

      this.tick++;
      float[] afloat1 = this.p.call088[clamp(this.previousYaw, this.p.int343)];
      float[] afloat2 = this.p.call153[clamp(this.previousPitch, this.p.int344)];
      float[] afloat3 = concat(this.cachedIntent, afloat1, afloat2);
      float[] afloat4 = this.latent(afloat3);
      float[] afloat5 = concat(afloat3, afloat4);
      gru(afloat5, this.motorH, this.p.call113, this.p.call055, this.p.call150, this.p.call115);
      float[] afloat6 = dense(this.motorH, this.p.call054, this.p.call042);
      int j = this.sample(afloat6, this.p.float215, this.p.float216);
      float[] afloat7 = concat(this.motorH, this.p.call088[j]);
      float[] afloat8 = denseTanh(afloat7, this.p.call087, this.p.call033);
      float[] afloat9 = dense(afloat8, this.p.call154, this.p.call086);
      int k = this.sample(afloat9, this.p.float215, this.p.float216);
      this.previousYaw = j;
      this.previousPitch = k;
      return new int[]{this.pickBin(this.p.call051[0][j], this.p.call081[0][j]), this.pickBin(this.p.call051[1][k], this.p.call081[1][k])};
   }

   @Override
   public void reset() {
      Arrays.fill(this.plannerH, 0.0F);
      Arrays.fill(this.plannerC, 0.0F);
      Arrays.fill(this.motorH, 0.0F);
      Arrays.fill(this.cachedIntent, 0.0F);
      this.previousYaw = this.p.int345;
      this.previousPitch = this.p.int346;
      this.tick = 0;
   }

   public float[] normalize(float[] var1) {
      GmmComponent[] al11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil = this.p.call109;
      float[] afloat = new float[al11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.length];

      for (int i = 0; i < al11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.length; i++) {
         GmmComponent l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil = al11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil[i];
         float f = l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.index >= 0
               && l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.index < var1.length
               && Float.isFinite(var1[l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.index])
            ? var1[l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.index]
            : 0.0F;
         if (l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.mu && l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.scale > 0.0F) {
            double d0 = Math.copySign(1.0, f);
            f = (float)(
               d0
                  * Math.log1p(l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.scale * Math.abs(f))
                  / Math.log1p(l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.scale)
            );
         }

         float f1 = Math.abs(l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.std) < 1.0E-6F ? 1.0F : l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.std;
         f = (f - l11il1i1iil1lll111l1111llliil_ii1il11l111ii11iil.mean) / f1;
         afloat[i] = Math.max(-12.0F, Math.min(12.0F, f));
      }

      return afloat;
   }

   public float[] latent(float[] var1) {
      int i = Math.max(1, this.p.int348);
      if (this.p.call043 != null && this.p.call068 != null) {
         float[] afloat = denseTanh(var1, this.p.call043, this.p.call056);
         float[] afloat1 = dense(afloat, this.p.call068, this.p.call184);
         float[] afloat2 = new float[i];

         for (int j = 0; j < i; j++) {
            float f = j < afloat1.length ? afloat1[j] : 0.0F;
            float f1 = j + i < afloat1.length ? afloat1[j + i] : -20.0F;
            float f2 = (float)Math.exp(Math.max(-8.0F, Math.min(2.0F, f1)));
            afloat2[j] = f + f2 * (float)this.random.nextGaussian();
         }

         return afloat2;
      } else {
         return new float[i];
      }
   }

   public static void lstm(float[] var0, float[] var1, float[] var2, float[][] var3, float[][] var4, float[] var5, float[] var6) {
      int i = var1.length;
      float[] afloat = new float[i];
      float[] afloat1 = new float[i];

      for (int j = 0; j < i; j++) {
         float f = affine(var3[j], var0, var5[j]) + affine(var4[j], var1, var6[j]);
         float f1 = affine(var3[i + j], var0, var5[i + j]) + affine(var4[i + j], var1, var6[i + j]);
         float f2 = affine(var3[2 * i + j], var0, var5[2 * i + j]) + affine(var4[2 * i + j], var1, var6[2 * i + j]);
         float f3 = affine(var3[3 * i + j], var0, var5[3 * i + j]) + affine(var4[3 * i + j], var1, var6[3 * i + j]);
         afloat1[j] = sigmoid(f1) * var2[j] + sigmoid(f) * (float)Math.tanh(f2);
         afloat[j] = sigmoid(f3) * (float)Math.tanh(afloat1[j]);
      }

      System.arraycopy(afloat, 0, var1, 0, i);
      System.arraycopy(afloat1, 0, var2, 0, i);
   }

   public static void gru(float[] var0, float[] var1, float[][] var2, float[][] var3, float[] var4, float[] var5) {
      int i = var1.length;
      float[] afloat = new float[i];

      for (int j = 0; j < i; j++) {
         float f = sigmoid(affine(var2[j], var0, var4[j]) + affine(var3[j], var1, var5[j]));
         float f1 = sigmoid(affine(var2[i + j], var0, var4[i + j]) + affine(var3[i + j], var1, var5[i + j]));
         float f2 = (float)Math.tanh(affine(var2[2 * i + j], var0, var4[2 * i + j]) + f * affine(var3[2 * i + j], var1, var5[2 * i + j]));
         afloat[j] = (1.0F - f1) * f2 + f1 * var1[j];
      }

      System.arraycopy(afloat, 0, var1, 0, i);
   }

   public static float[] dense(float[] var0, float[][] var1, float[] var2) {
      float[] afloat = new float[var1.length];

      for (int i = 0; i < afloat.length; i++) {
         afloat[i] = affine(var1[i], var0, var2 != null && i < var2.length ? var2[i] : 0.0F);
      }

      return afloat;
   }

   public static float[] denseTanh(float[] var0, float[][] var1, float[] var2) {
      float[] afloat = dense(var0, var1, var2);

      for (int i = 0; i < afloat.length; i++) {
         afloat[i] = (float)Math.tanh(afloat[i]);
      }

      return afloat;
   }

   public int sample(float[] var1, float var2, float var3) {
      float f = Math.max(0.05F, var2);
      float f1 = -Float.MAX_VALUE;

      for (float f2 : var1) {
         f1 = Math.max(f1, f2 / f);
      }

      double[] adouble = new double[var1.length];
      double d2 = 0.0;

      for (int l = 0; l < var1.length; l++) {
         d2 += adouble[l] = Math.exp(var1[l] / f - f1);
      }

      for (int i1 = 0; i1 < adouble.length; i1++) {
         adouble[i1] /= d2;
      }

      Integer[] ainteger = new Integer[adouble.length];

      for (int i = 0; i < ainteger.length; i++) {
         ainteger[i] = i;
      }

      Arrays.sort(ainteger, (var1x, var2x) -> Double.compare(adouble[var2x], adouble[var1x]));
      double d3 = Math.max(0.01, Math.min(1.0, var3));
      double d0 = 0.0;
      int j = 0;

      while (j < ainteger.length && d0 < d3) {
         d0 += adouble[ainteger[j++]];
      }

      double d1 = this.random.nextDouble() * d0;

      for (int k = 0; k < j; k++) {
         d1 -= adouble[ainteger[k]];
         if (d1 <= 0.0) {
            return ainteger[k];
         }
      }

      return ainteger[Math.max(0, j - 1)];
   }

   public int pickBin(int var1, int var2) {
      return var2 <= var1 ? var1 : var1 + this.random.nextInt(var2 - var1 + 1);
   }

   public static float affine(float[] var0, float[] var1, float var2) {
      double d0 = var2;
      int i = Math.min(var0.length, var1.length);

      for (int j = 0; j < i; j++) {
         d0 += (double)var0[j] * var1[j];
      }

      return (float)d0;
   }

   public static float sigmoid(float var0) {
      if (var0 >= 0.0F) {
         return (float)(1.0 / (1.0 + Math.exp(-var0)));
      }

      double d0 = Math.exp(var0);
      return (float)(d0 / (1.0 + d0));
   }

   public static float[] concat(float[]... var0) {
      int i = 0;

      for (float[] afloat : var0) {
         i += afloat.length;
      }

      float[] afloat2 = new float[i];
      int j = 0;

      for (float[] afloat1 : var0) {
         System.arraycopy(afloat1, 0, afloat2, j, afloat1.length);
         j += afloat1.length;
      }

      return afloat2;
   }

   public static int clamp(int var0, int var1) {
      return Math.max(0, Math.min(var1 - 1, var0));
   }
}
