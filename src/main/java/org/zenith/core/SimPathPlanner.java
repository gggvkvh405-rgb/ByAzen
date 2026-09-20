package org.zenith.core;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

final class SimPathPlanner {
   public static final String string72 = "/assets/zenith/pointclick/mouse_gain.txt";
   public static final String string73 = "/assets/zenith/pointclick/rotation_map_rad.csv";
   public final double[] call204;
   public final double[] call203;
   public final double[][] call216;

   public SimPathPlanner(double[] var1, double[] var2, double[][] var3) {
      this.call204 = var1;
      this.call203 = var2;
      this.call216 = var3;
   }

   static SimPathPlanner call464() throws Exception {
      ArrayList arraylist = new ArrayList();
      ArrayList arraylist1 = new ArrayList();

      String s;
      try (BufferedReader bufferedreader = EventInjectAddEntity("/assets/zenith/pointclick/mouse_gain.txt")) {
         while ((s = bufferedreader.readLine()) != null) {
            String[] astring = s.trim().split(":\\s*");
            if (astring.length == 2) {
               double d0 = Double.parseDouble(astring[0]);
               double d1 = Double.parseDouble(astring[1]);
               double d2 = 0.0254 * (d0 / 400.0) * 125.0;
               double d3 = 0.0254 * (d1 / 110.0) * 125.0;
               arraylist.add(d3);
               arraylist1.add(d2 == 0.0 ? 0.0 : d3 / d2);
            }
         }
      }

      double[][] adouble = new double[31][31];

      try (BufferedReader bufferedreader1 = EventInjectAddEntity("/assets/zenith/pointclick/rotation_map_rad.csv")) {
         for (int i = 0; i < adouble.length; i++) {
            String s1 = bufferedreader1.readLine();
            if (s1 == null) {
               throw new IllegalStateException("rotation map has fewer than 31 rows");
            }

            String[] astring1 = s1.split(",");
            if (astring1.length != 31) {
               throw new IllegalStateException("rotation map row has " + astring1.length + " columns");
            }

            for (int j = 0; j < astring1.length; j++) {
               adouble[i][j] = Double.parseDouble(astring1[j]);
            }
         }
      }

      return new SimPathPlanner(ColorAnimator(arraylist), ColorAnimator(arraylist1), adouble);
   }

   double SimpleItemBuilder(double var1) {
      return on23(this.call204, this.call203, var1);
   }

   SimVelocities on23(double[] var1, double[] var2, double var3, double var5, double[] var7) {
      double[] adouble = MovementSimulator.UiAnimation(var1);
      double[] adouble1 = MovementSimulator.UiAnimation(var2);
      double d0 = this.UiAnimation(var3, var5);

      for (int i = 0; i < adouble.length; i++) {
         if (adouble[i] != 0.0 || adouble1[i] != 0.0) {
            double d1 = var3;
            double d2 = var5;
            double d3 = Math.cos(-d0);
            double d4 = Math.sin(-d0);
            double d5 = d3 * adouble[i] - d4 * adouble1[i];
            double d6 = d4 * adouble[i] + d3 * adouble1[i];
            var3 += d5;
            var5 += d6;
            double[] adouble2 = this.on23(d1, d2, var3, var5, var7[i]);
            adouble[i] = adouble2[0];
            adouble1[i] = adouble2[1];
         }
      }

      return new SimVelocities(adouble, adouble1, var3, var5);
   }

   public double[] on23(double var1, double var3, double var5, double var7, double var9) {
      double d0 = this.UiAnimation(var5, var7);
      double d1 = this.UiAnimation(var1, var3);
      double d2 = Math.hypot(var5 - var1, var7 - var3);
      double d3 = d0 - d1;
      if (d3 == 0.0) {
         d3 = Double.MIN_NORMAL;
      }

      double d4 = var9 * d2 * 2.0 * Math.sin(d3 / 2.0) / d3;
      double d5 = d2 == 0.0 ? Double.MIN_NORMAL : d2;
      double d6 = (var5 - var1) / d5;
      double d7 = (var7 - var3) / d5;
      double d8 = d3 / 2.0 + d1;
      return new double[]{(d6 * Math.cos(d8) - d7 * Math.sin(d8)) * d4, (d6 * Math.sin(d8) + d7 * Math.cos(d8)) * d4};
   }

   public double UiAnimation(double var1, double var3) {
      double d0 = var1 / 0.257;
      double d1 = var3 / 0.257;
      double d2 = (d0 + 1.5) / 0.1;
      double d3 = (1.5 - d1) / 0.1;
      return on23(this.call216, d3, d2);
   }

   public static double on23(double[][] var0, double var1, double var3) {
      int i = (int)Math.floor(var1);
      int j = (int)Math.floor(var3);
      double d0 = var1 - i;
      double d1 = var3 - j;
      double[] adouble = new double[4];

      for (int k = -1; k <= 2; k++) {
         int l = clamp(i + k, 0, var0.length - 1);
         adouble[k + 1] = UiAnimation(
            var0[l][clamp(j - 1, 0, var0[l].length - 1)],
            var0[l][clamp(j, 0, var0[l].length - 1)],
            var0[l][clamp(j + 1, 0, var0[l].length - 1)],
            var0[l][clamp(j + 2, 0, var0[l].length - 1)],
            d1
         );
      }

      return UiAnimation(adouble[0], adouble[1], adouble[2], adouble[3], d0);
   }

   public static double UiAnimation(double var0, double var2, double var4, double var6, double var8) {
      return 0.5
         * (
            2.0 * var2
               + (-var0 + var4) * var8
               + (2.0 * var0 - 5.0 * var2 + 4.0 * var4 - var6) * var8 * var8
               + (-var0 + 3.0 * var2 - 3.0 * var4 + var6) * var8 * var8 * var8
         );
   }

   public static double on23(double[] var0, double[] var1, double var2) {
      int i = 1;

      while (i < var0.length && var0[i] < var2) {
         i++;
      }

      if (i >= var0.length) {
         i = var0.length - 1;
      }

      int j = Math.max(i - 1, 0);
      if (var0[i] == var0[j]) {
         return var1[j];
      }

      double d0 = (var2 - var0[j]) / (var0[i] - var0[j]);
      return var1[j] + d0 * (var1[i] - var1[j]);
   }

   public static BufferedReader EventInjectAddEntity(String var0) {
      InputStream inputstream = MovementSimulator.class.getResourceAsStream(var0);
      if (inputstream == null) {
         throw new IllegalStateException("missing resource " + var0);
      } else {
         return new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
      }
   }

   public static double[] ColorAnimator(List<Double> var0) {
      double[] adouble = new double[var0.size()];

      for (int i = 0; i < adouble.length; i++) {
         adouble[i] = var0.get(i);
      }

      return adouble;
   }

   public static int clamp(int var0, int var1, int var2) {
      return Math.max(var1, Math.min(var0, var2));
   }
}
