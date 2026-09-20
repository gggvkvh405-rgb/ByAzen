package org.zenith.managers;

import java.util.Random;
import org.zenith.core.CodecRow;
import org.zenith.core.IntGridCell;
import org.zenith.core.InventoryCodec;
import org.zenith.core.PermissionListCodec;
import org.zenith.core.VectorRange;

public final class MotorIntentModel {
   public static final double double119 = 0.35;
   public static final int int404 = 16;
   public static final double double120 = 0.7;
   public static final double double121 = 1.0;
   public static final double double122 = 0.0;
   public static final double double123 = 0.0;
   public static final double double124 = 1.0;
   public static final double double125 = 1.0;
   public static final double double126 = 1.0;
   public static final int int405 = 40;
   public static final double double127 = 1.0;
   public static final double double128 = 1.0;
   public static final double double129 = 0.6;
   public static final double double130 = 0.4;
   public static final double double131 = 0.5;
   public static final double double132 = 40.0;
   public static final int int406 = 2;
   public static final double double133 = 1.25;
   public static final double double134 = 25.0;
   public static final double double135 = 30.0;
   public static final double double136 = 15.0;
   public final InventoryCodec var132;
   public final PermissionListCodec var2;
   public final Random random7;
   public IntGridCell var2Var143;
   public int int407;
   public double double137;
   public boolean boolean182;
   public int int408;
   public boolean boolean183;
   public int int151;
   public int int152;
   public int int409;
   public int int410 = -1;
   public String string114 = "idle";

   public MotorIntentModel(InventoryCodec var1, PermissionListCodec var2) {
      this.var132 = var1;
      this.var2 = var2;
      this.random7 = new Random();
      this.reset();
   }

   public void reset() {
      this.var2Var143 = null;
      this.int407 = 0;
      this.double137 = 0.0;
      this.boolean182 = false;
      this.int408 = 0;
      this.boolean183 = false;
      this.int151 = 0;
      this.int152 = 0;
      this.int409 = 0;
      this.int410 = -1;
      this.string114 = "idle";
   }

   public String list47() {
      return this.string114;
   }

   public int[] on23(MotorIntentModel.Prediction var1) {
      if (this.var2Var143 != null && this.UiAnimation(var1)) {
         this.var2Var143 = null;
         this.int407 = 0;
      }

      if (this.var2Var143 != null) {
         int l = this.var2Var143.val069[this.int407];
         int i1 = this.var2Var143.call106[this.int407];
         this.int407++;
         this.string114 = "replay:" + PermissionListCodec.call175[this.var2Var143.int154] + " " + this.int407 + "/" + this.var2Var143.length();
         if (this.int407 >= this.var2Var143.length()) {
            this.var2Var143 = null;
            this.int407 = 0;
         }

         this.ColorAnimator(l, i1);
         return new int[]{l, i1};
      } else if (this.int408 > 0) {
         this.int408--;
         if (this.int408 <= 0) {
            this.boolean183 = true;
         }

         this.string114 = "rest:" + this.int408 + " left";
         this.ColorAnimator(0, 0);
         return new int[]{0, 0};
      } else {
         CodecRow il11lill1lil1l1iill_ii1il11l111ii11iil = this.var132.SimpleItemBuilder(this.Easing(var1));
         double d0 = TextScanner((il11lill1lil1l1iill_ii1il11l111ii11iil.float64 + 0.0) / 1.0);
         boolean flag = this.boolean183 || this.random7.nextDouble() < d0;
         this.boolean183 = false;
         if (!flag) {
            int j1 = 1;
            if (var1.double23 >= 0.5) {
               j1 = this.on23(il11lill1lil1l1iill_ii1il11l111ii11iil.call267);
               if (j1 > 1) {
                  this.int408 = j1 - 1;
               } else {
                  this.boolean183 = true;
               }
            }

            this.string114 = "wait dwell=" + j1;
            this.ColorAnimator(0, 0);
            return new int[]{0, 0};
         } else {
            int i = this.on23(il11lill1lil1l1iill_ii1il11l111ii11iil.call149, 0.7);
            double[] adouble = this.UiAnimation(il11lill1lil1l1iill_ii1il11l111ii11iil.call116[i]);
            double[] adouble1 = this.on23(adouble, var1);
            VectorRange l1i1liliili_illi1l1l1 = new VectorRange();
            l1i1liliili_illi1l1l1.double36 = var1.double11;
            l1i1liliili_illi1l1l1.double37 = var1.double12;
            l1i1liliili_illi1l1l1.double38 = var1.double19;
            l1i1liliili_illi1l1l1.double39 = var1.double20;
            l1i1liliili_illi1l1l1.double40 = (var1.double14 - var1.double13) * 0.5;
            l1i1liliili_illi1l1l1.double41 = (var1.double16 - var1.double15) * 0.5;
            l1i1liliili_illi1l1l1.int151 = this.int151;
            l1i1liliili_illi1l1l1.int152 = this.int152;
            IntGridCell l1i1liliili_l1i1illlili = this.var2
               .on23(i, adouble1, l1i1liliili_illi1l1l1, this.random7, 16, 0.35, this.int410, 1.0, 1.0, 0.6, 0.4, 0.5, 40.0);
            this.int410 = l1i1liliili_l1i1illlili.int153;
            this.var2Var143 = l1i1liliili_l1i1illlili;
            this.int407 = 0;
            this.double137 = Math.hypot(var1.double11, var1.double12);
            this.boolean182 = var1.double23 >= 0.5;
            int j = l1i1liliili_l1i1illlili.val069[0];
            int k = l1i1liliili_l1i1illlili.call106[0];
            this.int407 = 1;
            this.string114 = "start:" + PermissionListCodec.call175[i] + " #" + l1i1liliili_l1i1illlili.int153 + " len=" + l1i1liliili_l1i1illlili.length();
            if (this.int407 >= l1i1liliili_l1i1illlili.length()) {
               this.var2Var143 = null;
               this.int407 = 0;
            }

            this.ColorAnimator(j, k);
            return new int[]{j, k};
         }
      }
   }

   public int on23(float[][] var1) {
      float[] afloat = new float[var1.length];

      for (int i = 0; i < var1.length; i++) {
         afloat[i] = var1[i][0];
      }

      int j = this.on23(afloat, 1.0);
      double d0 = var1[j][1];
      double d1 = ItemSpec(var1[j][2], -2.5, 0.7F);
      d0 += Math.exp(d1) * 1.0 * this.random7.nextGaussian();
      int k = (int)Math.round(Math.expm1(Math.max(d0, 0.0)));
      return Math.max(1, Math.min(k, 40));
   }

   public boolean UiAnimation(MotorIntentModel.Prediction var1) {
      if (this.int407 < 2) {
         return false;
      }

      double d0 = Math.hypot(var1.double11, var1.double12);
      boolean flag = d0 > this.double137 * 1.25 && d0 > this.double137 + 25.0;
      double d1 = 0.0;
      double d2 = 0.0;

      for (int i = this.int407; i < this.var2Var143.length(); i++) {
         d1 += this.var2Var143.val069[i];
         d2 += this.var2Var143.call106[i];
      }

      double d3 = Math.hypot(d1, d2);
      boolean flag1 = d0 >= 30.0 && d3 >= 15.0 && d1 * var1.double11 + d2 * var1.double12 < 0.0;
      boolean flag2 = this.boolean182 && var1.double23 < 0.5;
      return flag || flag1 || flag2;
   }

   public float[] Easing(MotorIntentModel.Prediction var1) {
      float[] afloat = new float[this.var132.int366];

      for (int i = 0; i < this.var132.int366; i++) {
         String s = this.var132.call180[i];

         afloat[i] = (float)(switch (s) {
            case "center_yaw" -> var1.double11;
            case "center_pitch" -> var1.double12;
            case "box_min_yaw" -> var1.double13;
            case "box_max_yaw" -> var1.double14;
            case "box_min_pitch" -> var1.double15;
            case "box_max_pitch" -> var1.double16;
            case "center_yaw_change" -> var1.double17;
            case "center_pitch_change" -> var1.double18;
            case "yaw_drift" -> var1.double19;
            case "pitch_drift" -> var1.double20;
            case "sim_2t_yaw" -> var1.double21;
            case "sim_2t_pitch" -> var1.double22;
            case "inside" -> var1.double23;
            case "distance" -> var1.double24;
            case "target_motion_x" -> var1.double25;
            case "target_motion_y" -> var1.double26;
            case "target_motion_z" -> var1.double27;
            case "player_motion_x" -> var1.double28;
            case "player_motion_y" -> var1.double29;
            case "player_motion_z" -> var1.double30;
            case "prev_cmd_yaw" -> this.int151;
            case "prev_cmd_pitch" -> this.int152;
            case "rest_run_so_far_log" -> this.int409;
            case "in_gesture" -> this.int151 == 0 && this.int152 == 0 ? 0.0 : 1.0;
            default -> throw new IllegalStateException("unknown selector input " + this.var132.call180[i]);
         });
      }

      return afloat;
   }

   public double[] UiAnimation(float[][] var1) {
      float[] afloat = new float[var1.length];

      for (int i = 0; i < var1.length; i++) {
         afloat[i] = var1[i][0];
      }

      int k = this.on23(afloat, 1.0);
      double[] adouble = new double[4];

      for (int j = 0; j < adouble.length; j++) {
         adouble[j] = var1[k][1 + j];
      }

      return adouble;
   }

   public double[] on23(double[] var1, MotorIntentModel.Prediction var2) {
      double d0 = Math.max(Math.expm1(var1[2]), 1.0);
      double d1 = Math.max(d0 - 2.0, 0.0);
      double d2 = var2.double21 + var2.double19 * d1;
      double d3 = var2.double22 + var2.double20 * d1;
      return new double[]{
         var1[0] + InventoryCodec.CloudApiClient((float)d2),
         var1[1] + InventoryCodec.CloudApiClient((float)d3),
         var1[2],
         var1[3],
         0.0,
         0.0,
         InventoryCodec.CloudApiClient((float)(var2.double19 * d0)),
         InventoryCodec.CloudApiClient((float)(var2.double20 * d0))
      };
   }

   public int on23(float[] var1, double var2) {
      double d0 = Double.NEGATIVE_INFINITY;

      for (float f : var1) {
         d0 = Math.max(d0, f);
      }

      double[] adouble = new double[var1.length];
      double d2 = 0.0;

      for (int j = 0; j < var1.length; j++) {
         adouble[j] = Math.exp((var1[j] - d0) / var2);
         d2 += adouble[j];
      }

      double d3 = this.random7.nextDouble() * d2;
      double d1 = 0.0;

      for (int i = 0; i < var1.length; i++) {
         d1 += adouble[i];
         if (d3 <= d1) {
            return i;
         }
      }

      return var1.length - 1;
   }

   public static int EnchantItemSpec(float[] var0) {
      int i = 0;

      for (int j = 1; j < var0.length; j++) {
         if (var0[j] > var0[i]) {
            i = j;
         }
      }

      return i;
   }

   public void ColorAnimator(int var1, int var2) {
      this.int151 = var1;
      this.int152 = var2;
      this.int409 = var1 == 0 && var2 == 0 ? this.int409 + 1 : 0;
   }

   public static double TextScanner(double var0) {
      return 1.0 / (1.0 + Math.exp(-var0));
   }

   public static double ItemSpec(double var0, double var2, double var4) {
      return Math.max(var2, Math.min(var4, var0));
   }


   public static final class Prediction {
      public double double11;
      public double double12;
      public double double13;
      public double double14;
      public double double15;
      public double double16;
      public double double17;
      public double double18;
      public double double19;
      public double double20;
      public double double21;
      public double double22;
      public double double23;
      public double double24;
      public double double25;
      public double double26;
      public double double27;
      public double double28;
      public double double29;
      public double double30;
   }
}
