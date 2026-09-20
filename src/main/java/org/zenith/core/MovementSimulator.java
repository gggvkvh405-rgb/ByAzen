package org.zenith.core;

import java.util.Random;

public final class MovementSimulator {
   public static final double double159 = 0.05;
   public static final double double160 = 0.4608;
   public static final double double161 = 0.2592;
   public static final double double162 = 0.257;
   public static final int int465 = 2;
   public static final double double163 = 0.2;
   public static final double double164 = 0.02;
   public final MotionSampleStore var04;
   public final Random random11;
   public final SimPathPlanner zClass095Var165;

   public SimBoxState on23(SimPose var1) {
      float[] afloat = new float[]{
         (float)var1.double64,
         (float)var1.double65,
         (float)var1.double66,
         (float)var1.double67,
         (float)var1.double68,
         (float)var1.double69,
         (float)var1.double70,
         (float)var1.double71,
         (float)var1.double59,
         (float)var1.double72,
         (float)var1.double73
      };
      int i = this.var04.ModuleSnapshotDto(afloat);
      boolean flag = i / 25 != 0;
      int j = 2 + i % 25 * 2;
      int k = flag ? j : 2;
      SimDelta ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx = on23(2, var1.double53, -var1.double55, 0.4608, var1.double59);
      SimDelta ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilx = on23(2, var1.double54, -var1.double56, 0.2592, var1.double59);
      ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx.call007 *= -1.0;
      ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilx.call007 *= -1.0;
      double[] adouble = this.on23(ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx.call007, ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilx.call007);
      ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx.call007 = adouble[0];
      ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilx.call007 = adouble[1];
      ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx = on23(
         2, ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx.call031, ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx.call007, 0.4608, var1.double59
      );
      ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilx = on23(
         2, ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilx.call031, ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilx.call007, 0.2592, var1.double59
      );
      double d0 = ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx.call031;
      double d1 = ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilx.call031;
      double d2 = ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx.call007;
      double d3 = ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilx.call007;
      ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx = on23(
         j, ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx.call031, ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx.call007, 0.4608, var1.double59
      );
      SimDelta ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxxx = on23(
         j, ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilx.call031, ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilx.call007, 0.2592, var1.double59
      );
      double[][] adouble1 = on23(
         var1.double64,
         var1.double66,
         ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx.call031,
         ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxx.call007,
         j,
         k
      );
      double[][] adouble2 = on23(
         var1.double65,
         var1.double67,
         ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxxx.call031,
         ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxxx.call007,
         j,
         k
      );
      on23(adouble1, 0.4608);
      on23(adouble2, 0.2592);
      double[] adouble3 = (double[])adouble1[1].clone();
      double[] adouble4 = (double[])adouble2[1].clone();
      double[] adouble5 = new double[k + 1];

      for (int l = 0; l <= k; l++) {
         adouble5[l] = this.zClass095Var165.SimpleItemBuilder(Math.hypot(adouble3[l], adouble4[l]));
         if (adouble5[l] == 0.0) {
            adouble5[l] = Double.MIN_NORMAL;
         }
      }

      double d9 = 0.0;

      for (int i1 = 0; i1 < k; i1++) {
         double d4 = (adouble3[i1 + 1] / adouble5[i1 + 1] - adouble3[i1] / adouble5[i1]) / 0.05;
         double d5 = (adouble4[i1 + 1] / adouble5[i1 + 1] - adouble4[i1] / adouble5[i1]) / 0.05;
         d9 += Math.hypot(d4, d5);
      }

      double[] adouble8 = on23(adouble3, adouble5);
      double[] adouble9 = on23(adouble4, adouble5);
      SimVelocities ll1i11iili1i1i11lilil11i11ill1_Var160 = this.zClass095Var165.on23(adouble8, adouble9, var1.double62, var1.double63, adouble5);
      double d10 = ll1i11iili1i1i11lilil11i11ill1_Var160.call178 - var1.double62;
      double d6 = ll1i11iili1i1i11lilil11i11ill1_Var160.call006 - var1.double63;
      SimVectors ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx = this.on23(adouble8, adouble9, k, 0.0, 0.0, 0.2, 0.02);
      SimVelocities ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx = this.zClass095Var165
         .on23(
            (double[])ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call040.clone(),
            (double[])ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call041.clone(),
            var1.double62,
            var1.double63,
            adouble5
         );
      double[] adouble6 = UiAnimation(UiAnimation(ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call040, adouble5));
      double[] adouble7 = UiAnimation(UiAnimation(ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call041, adouble5));
      double d7 = (sum(ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx.call276) - sum(adouble6)) / (k * 0.05);
      double d8 = (sum(ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx.call119) - sum(adouble7)) / (k * 0.05);
      ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx = this.on23(
         UiAnimation(ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call040, adouble5),
         UiAnimation(ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call041, adouble5),
         k,
         d7,
         d8,
         0.0,
         0.0
      );
      on23(ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call217, ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call040, var1.double60, 0.4608);
      on23(ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call218, ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call041, var1.double61, 0.2592);
      SimDelta ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxxxx = on23(k, d0, d2, 0.4608, var1.double59);
      SimDelta ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxxxxx = on23(k, d1, d3, 0.2592, var1.double59);
      return new SimBoxState(
         i,
         k,
         ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call217,
         ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call218,
         var1.double60 + adouble1[0][k] - adouble1[0][0],
         var1.double61 + adouble2[0][k] - adouble2[0][0],
         adouble1[1][k],
         adouble2[1][k],
         ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxxxx.call031,
         ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxxxxx.call031,
         ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxxxx.call007,
         ll1i11iili1i1i11lilil11i11ill1_ii1il11l111ii11iilxxxxx.call007,
         ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx.call178,
         ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx.call006,
         var1.double62 + d10,
         var1.double63 + d6,
         d9
      );
   }

   public SimBoxState on23(SimPose var1, int var2, double[][] var3, double[][] var4) {
      double[] adouble = (double[])var3[1].clone();
      double[] adouble1 = (double[])var4[1].clone();
      double[] adouble2 = new double[var2 + 1];

      for (int i = 0; i <= var2; i++) {
         adouble2[i] = this.zClass095Var165.SimpleItemBuilder(Math.hypot(adouble[i], adouble1[i]));
         if (adouble2[i] == 0.0) {
            adouble2[i] = Double.MIN_NORMAL;
         }
      }

      double d5 = 0.0;

      for (int j = 0; j < var2; j++) {
         double d0 = (adouble[j + 1] / adouble2[j + 1] - adouble[j] / adouble2[j]) / 0.05;
         double d1 = (adouble1[j + 1] / adouble2[j + 1] - adouble1[j] / adouble2[j]) / 0.05;
         d5 += Math.hypot(d0, d1);
      }

      double[] adouble5 = on23(adouble, adouble2);
      double[] adouble6 = on23(adouble1, adouble2);
      SimVelocities ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx = this.zClass095Var165.on23(adouble5, adouble6, var1.double62, var1.double63, adouble2);
      double d6 = ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx.call178 - var1.double62;
      double d2 = ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx.call006 - var1.double63;
      SimVectors ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx = this.on23(adouble5, adouble6, var2, 0.0, 0.0, 0.2, 0.02);
      ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx = this.zClass095Var165
         .on23(
            (double[])ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call040.clone(),
            (double[])ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call041.clone(),
            var1.double62,
            var1.double63,
            adouble2
         );
      double[] adouble3 = UiAnimation(UiAnimation(ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call040, adouble2));
      double[] adouble4 = UiAnimation(UiAnimation(ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call041, adouble2));
      double d3 = (sum(ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx.call276) - sum(adouble3)) / (var2 * 0.05);
      double d4 = (sum(ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx.call119) - sum(adouble4)) / (var2 * 0.05);
      ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx = this.on23(
         UiAnimation(ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call040, adouble2),
         UiAnimation(ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call041, adouble2),
         var2,
         d3,
         d4,
         0.0,
         0.0
      );
      return new SimBoxState(
         -1,
         var2,
         ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call217,
         ll1i11iili1i1i11lilil11i11ill1_liil11l111liil1llx.call218,
         var1.double60 + var3[0][var2] - var3[0][0],
         var1.double61 + var4[0][var2] - var4[0][0],
         var3[1][var2],
         var4[1][var2],
         var1.double68,
         var1.double69,
         var1.double70,
         var1.double71,
         ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx.call178,
         ll1i11iili1i1i11lilil11i11ill1_l1lll11l1lx.call006,
         var1.double62 + d6,
         var1.double63 + d2,
         d5
      );
   }

   public MovementSimulator(MotionSampleStore var1, long var2, SimPathPlanner var4) {
      this.var04 = var1;
      this.random11 = new Random(var2);
      this.zClass095Var165 = var4;
   }

   public static MovementSimulator on23(MotionSampleStore var0, long var1) {
      return var0 == null ? null : UiAnimation(var0, var1);
   }

   public static MovementSimulator TextScanner(long var0) {
      return UiAnimation(null, var0);
   }

   public static MovementSimulator UiAnimation(MotionSampleStore var0, long var1) {
      try {
         return new MovementSimulator(var0, var1, SimPathPlanner.call464());
      } catch (Throwable throwable) {
         System.err.println("[PointClick] failed to load mouse calibration: " + throwable);
         return null;
      }
   }

   public SimBoxState on23(SimPose var1, int var2, double var3, double var5, double var7, double var9) {
      if (var2 >= 2 && var2 <= 50) {
         double[][] adouble = on23(var1.double64, var1.double66, var3, var7, var2, var2);
         double[][] adouble1 = on23(var1.double65, var1.double67, var5, var9, var2, var2);
         return this.on23(var1, var2, adouble, adouble1);
      } else {
         throw new IllegalArgumentException("BUMP horizon must be in [2, 50]");
      }
   }

   public double[] on23(double var1, double var3) {
      double d0 = Math.hypot(var1, var3);
      if (d0 <= 0.0) {
         d0 = Double.MIN_NORMAL;
      }

      double d1 = 2.0 * Math.toDegrees(Math.atan(d0 / 1.26));
      double d2 = Math.log(1.0 + d1 / 0.3);
      double d3 = Math.exp(d2 + 0.15 * this.random11.nextGaussian());
      double d4 = Math.max((d3 - 1.0) * 0.3, 0.0);
      double d5 = 1.26 * Math.tan(Math.toRadians(d4) / 2.0);
      return new double[]{d5 * var1 / d0, d5 * var3 / d0};
   }

   public SimVectors on23(double[] var1, double[] var2, int var3, double var4, double var6, double var8, double var10) {
      double[] adouble = (double[])var1.clone();
      double[] adouble1 = (double[])var2.clone();

      for (int i = 1; i <= var3; i++) {
         double d0 = adouble[i];
         double d1 = adouble1[i];
         double d2 = Math.hypot(d0, d1);
         double d3 = d2 == 0.0 ? 0.0 : d0 / d2;
         double d4 = d2 == 0.0 ? 0.0 : d1 / d2;
         double d5 = -d4;
         double d6 = var8 * d2 * this.random11.nextGaussian();
         double d7 = var10 * d2 * this.random11.nextGaussian();
         adouble[i] = d0 + d6 * d3 + d7 * d5 + var4;
         adouble1[i] = d1 + d6 * d4 + d7 * d3 + var6;
      }

      return new SimVectors(UiAnimation(adouble), UiAnimation(adouble1), adouble, adouble1);
   }

   public static double[][] on23(double var0, double var2, double var4, double var6, int var8, int var9) {
      double[][] adouble = new double[][]{{1.0, 0.05}, {0.0, 1.0}};
      double[] adouble1 = new double[]{0.0012500000000000002, 0.05};
      double[][] adouble2 = on23(adouble, var8);
      double[][] adouble3 = on23(on23(var8, var8, adouble, adouble1));
      double[] adouble4 = new double[]{var0, var2};
      double[] adouble5 = Easing(new double[]{var4, var6}, on23(adouble2, adouble4));
      double[] adouble6 = on23(adouble3, adouble5);
      double[][] adouble7 = new double[2][var9 + 1];

      for (int i = 0; i <= var9; i++) {
         double[] adouble8 = on23(on23(adouble, i), adouble4);
         double[] adouble9 = on23(on23(i, var8, adouble, adouble1), adouble6);
         adouble7[0][i] = adouble8[0] + adouble9[0];
         adouble7[1][i] = adouble8[1] + adouble9[1];
      }

      return adouble7;
   }

   public static double[][] on23(int var0, int var1, double[][] var2, double[] var3) {
      double[][] adouble = new double[2][2];
      double[][] adouble1 = new double[][]{{var3[0] * var3[0], var3[0] * var3[1]}, {var3[1] * var3[0], var3[1] * var3[1]}};
      double[][] adouble2 = UiAnimation(var2);

      for (int i = 0; i < var0; i++) {
         adouble = on23(adouble, UiAnimation(UiAnimation(on23(var2, i), adouble1), on23(adouble2, var1 - var0 + i)));
      }

      return adouble;
   }

   public static SimDelta on23(int var0, double var1, double var3, double var5, double var7) {
      double d0 = var5 - var7;
      double d1 = var7;

      for (int i = 0; i < var0; i++) {
         double d2 = var1 + 0.05 * var3;
         if (d2 >= d1 && d2 <= d0) {
            var1 = d2;
         } else if (d2 > d0) {
            var1 = 2.0 * d0 - d2;
            var3 *= -1.0;
         } else {
            var1 = 2.0 * d1 - d2;
            var3 *= -1.0;
         }
      }

      return new SimDelta(var1, var3);
   }

   public static void on23(double[][] var0, double var1) {
      for (int i = 0; i < var0[0].length; i++) {
         if (var0[0][i] <= 0.0) {
            var0[0][i] = Double.MIN_NORMAL;
            var0[1][i] = 0.0;
         } else if (var0[0][i] >= var1) {
            var0[0][i] = var1;
            var0[1][i] = 0.0;
         }
      }
   }

   public static void on23(double[] var0, double[] var1, double var2, double var4) {
      double d0 = 0.0;

      for (int i = 0; i < var0.length; i++) {
         double d1 = var2 + d0 + var0[i];
         if (d1 <= 0.0) {
            var0[i] = -(var2 + d0);
            var1[i + 1] = 0.0;
         } else if (d1 >= var4) {
            var0[i] = var4 - (var2 + d0);
            var1[i + 1] = 0.0;
         }

         d0 += var0[i];
      }
   }

   public static double[] on23(double[] var0, double[] var1) {
      double[] adouble = new double[var0.length];

      for (int i = 0; i < adouble.length; i++) {
         adouble[i] = var0[i] / var1[i];
      }

      return adouble;
   }

   public static double[] UiAnimation(double[] var0, double[] var1) {
      double[] adouble = new double[var0.length];

      for (int i = 0; i < adouble.length; i++) {
         adouble[i] = var0[i] * var1[i];
      }

      return adouble;
   }

   public static double[] UiAnimation(double[] var0) {
      double[] adouble = new double[var0.length - 1];

      for (int i = 0; i < adouble.length; i++) {
         adouble[i] = (var0[i + 1] + var0[i]) * 0.5 * 0.05;
      }

      return adouble;
   }

   public static double sum(double[] var0) {
      double d0 = 0.0;

      for (double d1 : var0) {
         d0 += d1;
      }

      return d0;
   }

   public static double[][] on23(double[][] var0, int var1) {
      double[][] adouble = new double[][]{{1.0, 0.0}, {0.0, 1.0}};
      double[][] adouble1 = var0;

      for (int i = var1; i > 0; i >>= 1) {
         if ((i & 1) != 0) {
            adouble = UiAnimation(adouble, adouble1);
         }

         adouble1 = UiAnimation(adouble1, adouble1);
      }

      return adouble;
   }

   public static double[][] on23(double[][] var0) {
      double d0 = var0[0][0] * var0[1][1] - var0[0][1] * var0[1][0];
      if (Math.abs(d0) < 1.0E-18) {
         throw new IllegalStateException("singular BUMP grammian");
      } else {
         return new double[][]{{var0[1][1] / d0, -var0[0][1] / d0}, {-var0[1][0] / d0, var0[0][0] / d0}};
      }
   }

   public static double[][] UiAnimation(double[][] var0) {
      return new double[][]{{var0[0][0], var0[1][0]}, {var0[0][1], var0[1][1]}};
   }

   public static double[][] on23(double[][] var0, double[][] var1) {
      return new double[][]{{var0[0][0] + var1[0][0], var0[0][1] + var1[0][1]}, {var0[1][0] + var1[1][0], var0[1][1] + var1[1][1]}};
   }

   public static double[][] UiAnimation(double[][] var0, double[][] var1) {
      return new double[][]{
         {var0[0][0] * var1[0][0] + var0[0][1] * var1[1][0], var0[0][0] * var1[0][1] + var0[0][1] * var1[1][1]},
         {var0[1][0] * var1[0][0] + var0[1][1] * var1[1][0], var0[1][0] * var1[0][1] + var0[1][1] * var1[1][1]}
      };
   }

   public static double[] on23(double[][] var0, double[] var1) {
      return new double[]{var0[0][0] * var1[0] + var0[0][1] * var1[1], var0[1][0] * var1[0] + var0[1][1] * var1[1]};
   }

   public static double[] Easing(double[] var0, double[] var1) {
      return new double[]{var0[0] - var1[0], var0[1] - var1[1]};
   }
}
