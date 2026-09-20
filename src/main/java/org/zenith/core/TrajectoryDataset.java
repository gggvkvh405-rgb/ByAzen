package org.zenith.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TrajectoryDataset {
   public static final int int323 = 59;
   public static final int getZClass019 = 0;
   public static final int int324 = 1;
   public static final int int325 = 2;
   public static final String string80 = "/assets/zenith/pointclick/human_burst_intent.json";
   public static Path path4;
   public final int int326;
   public final int int327;
   public final int int328;
   public final int int329;
   public final TrajectoryMeta[] call138;
   public final TrajectoryPoint[] call139;
   public final TrajectoryBatch[] call220;
   public final float[][] call211;
   public final float[] call212;
   public final float[][] call176;
   public final float[] getTicks;
   public final float[][] val337;
   public final float[] call278;
   public final float[] call179;
   public final float[] call279;
   public final float float208;
   public final float[][] call280;
   public final float[] call223;
   public final float[][] call224;
   public final float[] call225;
   public final float[][] call226;
   public final float[] call227;
   public final float[][] call222;
   public final float[] call229;
   public final float[][] call230;
   public final float[] call231;
   public final float[][] call232;
   public final float[] call183;
   public final float[][] call233;
   public final float[] call234;
   public final float[][] call141;
   public int int330;

   public TrajectoryDataset(
      int var1,
      int var2,
      int var3,
      int var4,
      TrajectoryMeta[] var5,
      TrajectoryPoint[] var6,
      TrajectoryBatch[] var7,
      float[][] var8,
      float[] var9,
      float[][] var10,
      float[] var11,
      float[][] var12,
      float[] var13,
      float[] var14,
      float[] var15,
      float var16,
      float[][] var17,
      float[] var18,
      float[][] var19,
      float[] var20,
      float[][] var21,
      float[] var22,
      float[][] var23,
      float[] var24,
      float[][] var25,
      float[] var26,
      float[][] var27,
      float[] var28,
      float[][] var29,
      float[] var30
   ) {
      this.int326 = var1;
      this.int327 = var2;
      this.int328 = var3;
      this.int329 = var4;
      this.call138 = var5;
      this.call139 = var6;
      this.call220 = var7;
      this.call211 = var8;
      this.call212 = var9;
      this.call176 = var10;
      this.getTicks = var11;
      this.val337 = var12;
      this.call278 = var13;
      this.call179 = var14;
      this.call279 = var15;
      this.float208 = var16;
      this.call280 = var17;
      this.call223 = var18;
      this.call224 = var19;
      this.call225 = var20;
      this.call226 = var21;
      this.call227 = var22;
      this.call222 = var23;
      this.call229 = var24;
      this.call230 = var25;
      this.call231 = var26;
      this.call232 = var27;
      this.call183 = var28;
      this.call233 = var29;
      this.call234 = var30;
      this.call141 = new float[var4][var1];
   }

   public static TrajectoryDataset call466() {
      return null;
   }

   public static TrajectoryDataset ItemServiceBase(Path var0) {
      try {
         return !Files.isRegularFile(var0) ? null : FileLogger(Files.readString(var0, StandardCharsets.UTF_8), var0.toAbsolutePath().toString());
      } catch (Throwable throwable) {
         System.err.println("[HumanBurst] failed to load " + var0 + ": " + throwable);
         return null;
      }
   }

   public static TrajectoryDataset FileLogger(String var0, String var1) {
      JsonObject jsonobject = JsonParser.parseString(var0).getAsJsonObject();
      on23(jsonobject.get("format_version").getAsInt() == 2, "format version");
      on23("human_burst_target_relative_gru_bump".equals(jsonobject.get("model").getAsString()), "model name");
      JsonObject jsonobject1 = jsonobject.getAsJsonObject("architecture");
      int i = jsonobject1.get("input_size").getAsInt();
      int j = jsonobject1.get("hidden_size").getAsInt();
      int k = jsonobject1.get("layers").getAsInt();
      int l = jsonobject1.get("max_horizon").getAsInt();
      int i1 = jsonobject.get("history_ticks").getAsInt();
      on23(i == 61, "input size");
      on23(j > 0 && k > 0 && l >= 2 && i1 > 0, "architecture");
      JsonArray jsonarray = jsonobject.getAsJsonArray("feature_spec");
      on23(jsonarray.size() == i, "feature count");
      TrajectoryMeta[] alilliil111i_ii1il11l111ii11iil = new TrajectoryMeta[i];

      for (int j1 = 0; j1 < i; j1++) {
         JsonObject jsonobject2 = jsonarray.get(j1).getAsJsonObject();
         alilliil111i_ii1il11l111ii11iil[j1] = new TrajectoryMeta(
            jsonobject2.get("name").getAsString(),
            jsonobject2.get("transform").getAsString(),
            jsonobject2.get("scale").getAsFloat(),
            jsonobject2.get("mean").getAsFloat(),
            jsonobject2.get("std").getAsFloat()
         );
         on23(Float.isFinite(alilliil111i_ii1il11l111ii11iil[j1].long111()) && alilliil111i_ii1il11l111ii11iil[j1].long111() > 0.0F, "feature std " + j1);
      }

      on23("target_yaw_error_sin".equals(alilliil111i_ii1il11l111ii11iil[i - 2].name()), "yaw sine feature");
      on23("target_yaw_error_cos".equals(alilliil111i_ii1il11l111ii11iil[i - 1].name()), "yaw cosine feature");
      JsonArray jsonarray1 = jsonobject.getAsJsonArray("target_spec");
      on23(jsonarray1.size() == 6, "target count");
      TrajectoryPoint[] alilliil111i_liil11l111liil1ll = new TrajectoryPoint[6];

      for (int k1 = 0; k1 < alilliil111i_liil11l111liil1ll.length; k1++) {
         JsonObject jsonobject3 = jsonarray1.get(k1).getAsJsonObject();
         alilliil111i_liil11l111liil1ll[k1] = new TrajectoryPoint(
            jsonobject3.get("transform").getAsString(),
            jsonobject3.get("scale").getAsFloat(),
            jsonobject3.get("mean").getAsFloat(),
            jsonobject3.get("std").getAsFloat()
         );
         on23(Float.isFinite(alilliil111i_liil11l111liil1ll[k1].long111()) && alilliil111i_liil11l111liil1ll[k1].long111() > 0.0F, "target std " + k1);
      }

      JsonObject jsonobject5 = jsonobject.getAsJsonObject("weights");
      JsonArray jsonarray2 = jsonobject5.getAsJsonArray("gru_layers");
      on23(jsonarray2.size() == k, "GRU layer count");
      TrajectoryBatch[] alilliil111i_illi1l1l1 = new TrajectoryBatch[k];
      int l1 = i;

      for (int i2 = 0; i2 < k; i2++) {
         JsonObject jsonobject4 = jsonarray2.get(i2).getAsJsonObject();
         alilliil111i_illi1l1l1[i2] = new TrajectoryBatch(
            ItemRegistry(jsonobject4.getAsJsonArray("weight_ih")),
            ItemRegistry(jsonobject4.getAsJsonArray("weight_hh")),
            ColorAnimator(jsonobject4.getAsJsonArray("bias_ih")),
            ColorAnimator(jsonobject4.getAsJsonArray("bias_hh"))
         );
         on23(alilliil111i_illi1l1l1[i2], l1, j, "GRU layer " + i2);
         l1 = j;
      }

      TrajectoryDataset lilliil111i = new TrajectoryDataset(
         i,
         j,
         l,
         i1,
         alilliil111i_ii1il11l111ii11iil,
         alilliil111i_liil11l111liil1ll,
         alilliil111i_illi1l1l1,
         ItemRegistry(jsonobject5.getAsJsonArray("attention_projection_weight")),
         ColorAnimator(jsonobject5.getAsJsonArray("attention_projection_bias")),
         ItemRegistry(jsonobject5.getAsJsonArray("attention_score_weight")),
         ColorAnimator(jsonobject5.getAsJsonArray("attention_score_bias")),
         ItemRegistry(jsonobject5.getAsJsonArray("trunk_weight")),
         ColorAnimator(jsonobject5.getAsJsonArray("trunk_bias")),
         ColorAnimator(jsonobject5.getAsJsonArray("layer_norm_weight")),
         ColorAnimator(jsonobject5.getAsJsonArray("layer_norm_bias")),
         jsonobject5.get("layer_norm_epsilon").getAsFloat(),
         ItemRegistry(jsonobject5.getAsJsonArray("mode_weight")),
         ColorAnimator(jsonobject5.getAsJsonArray("mode_bias")),
         ItemRegistry(jsonobject5.getAsJsonArray("horizon_weight")),
         ColorAnimator(jsonobject5.getAsJsonArray("horizon_bias")),
         ItemRegistry(jsonobject5.getAsJsonArray("motion_weight")),
         ColorAnimator(jsonobject5.getAsJsonArray("motion_bias")),
         ItemRegistry(jsonobject5.getAsJsonArray("aim_weight")),
         ColorAnimator(jsonobject5.getAsJsonArray("aim_bias")),
         ItemRegistry(jsonobject5.getAsJsonArray("terminal_weight")),
         ColorAnimator(jsonobject5.getAsJsonArray("terminal_bias")),
         ItemRegistry(jsonobject5.getAsJsonArray("residual_weight")),
         ColorAnimator(jsonobject5.getAsJsonArray("residual_bias")),
         ItemRegistry(jsonobject5.getAsJsonArray("commit_weight")),
         ColorAnimator(jsonobject5.getAsJsonArray("commit_bias"))
      );
      lilliil111i.module();
      on23(lilliil111i, jsonobject.getAsJsonArray("parity"));
      System.out.println("[HumanBurst] loaded " + var1 + " (GRU" + j + "x" + k + ", attention, history=" + i1 + ")");
      return lilliil111i;
   }

   public void reset() {
      this.int330 = 0;
   }

   public void UiAnimation(float[] var1, float var2) {
      float[] afloat = this.Easing(var1, var2);
      if (this.int330 < this.int329) {
         this.call141[this.int330++] = afloat;
      } else {
         System.arraycopy(this.call141, 1, this.call141, 0, this.int329 - 1);
         this.call141[this.int329 - 1] = afloat;
      }
   }

   public TrajectorySample call467() {
      return this.int330 == 0 ? call468() : this.on23(this.on23(this.call141, this.int330));
   }

   public static TrajectorySample call468() {
      return new TrajectorySample(0, 0, 0.0F, 0.0F, 0.5F, 0.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
   }

   public float[] Easing(float[] var1, float var2) {
      if (var1 != null && var1.length == 59) {
         on23(Float.isFinite(var2) && var2 > 0.0F, "mouse GCD");
         float[] afloat = new float[this.int326];

         for (int i = 0; i < 59; i++) {
            float f = i == 26 ? MediaTrackInfo(var1[i], var2) : var1[i];
            afloat[i] = on23(on23(f, this.call138[i]), this.call138[i], "feature " + i);
         }

         double d0 = Math.toRadians(var1[0] * var2);
         afloat[59] = on23((float)Math.sin(d0), this.call138[59], "yaw sine");
         afloat[60] = on23((float)Math.cos(d0), this.call138[60], "yaw cosine");
         return afloat;
      } else {
         throw new IllegalArgumentException("expected 59 raw features");
      }
   }

   public TrajectoryFrame on23(float[][] var1, int var2) {
      on23(var2 > 0 && var2 <= var1.length, "history length");
      float[][] afloat = new float[this.call220.length][this.int327];
      float[][] afloat1 = new float[var2][this.int327];

      for (int i = 0; i < var2; i++) {
         float[] afloat2 = var1[i];

         for (int j = 0; j < this.call220.length; j++) {
            afloat[j] = this.on23(afloat2, afloat[j], this.call220[j]);
            afloat2 = afloat[j];
         }

         afloat1[i] = (float[])afloat2.clone();
      }

      float[] afloat3 = new float[var2];

      for (int j1 = 0; j1 < var2; j1++) {
         float[] afloat5 = Easing(afloat1[j1], this.call211, this.call212);

         for (int k = 0; k < afloat5.length; k++) {
            afloat5[k] = (float)Math.tanh(afloat5[k]);
         }

         afloat3[j1] = Easing(afloat5, this.call176, this.getTicks)[0];
      }

      float[] afloat4 = CloudUserProfile(afloat3);
      float[] afloat6 = new float[this.int327];

      for (int k1 = 0; k1 < var2; k1++) {
         for (int l = 0; l < this.int327; l++) {
            afloat6[l] += afloat4[k1] * afloat1[k1][l];
         }
      }

      float[] afloat7 = new float[this.int327 * 2];
      System.arraycopy(afloat1[var2 - 1], 0, afloat7, 0, this.int327);
      System.arraycopy(afloat6, 0, afloat7, this.int327, this.int327);
      float[] afloat8 = Easing(afloat7, this.val337, this.call278);

      for (int i1 = 0; i1 < afloat8.length; i1++) {
         afloat8[i1] *= MediaTrackInfo(afloat8[i1]);
      }

      afloat8 = this.MediaTrackInfo(afloat8);
      return new TrajectoryFrame(
         Easing(afloat8, this.call280, this.call223),
         Easing(afloat8, this.call224, this.call225),
         Easing(afloat8, this.call226, this.call227),
         Easing(afloat8, this.call222, this.call229),
         Easing(afloat8, this.call230, this.call231),
         Easing(afloat8, this.call232, this.call183),
         Easing(afloat8, this.call233, this.call234)[0]
      );
   }

   public float[] on23(float[] var1, float[] var2, TrajectoryBatch var3) {
      int i = this.int327;
      float[] afloat = UiAnimation(var1, var3.call447(), var3.call448());
      float[] afloat1 = UiAnimation(var2, var3.entity(), var3.call449());
      float[] afloat2 = new float[i];

      for (int j = 0; j < i; j++) {
         float f = MediaTrackInfo(afloat[j] + afloat1[j]);
         float f1 = MediaTrackInfo(afloat[i + j] + afloat1[i + j]);
         float f2 = (float)Math.tanh(afloat[2 * i + j] + f * afloat1[2 * i + j]);
         afloat2[j] = (1.0F - f1) * f2 + f1 * var2[j];
      }

      return afloat2;
   }

   public float[] MediaTrackInfo(float[] var1) {
      double d0 = 0.0;

      for (float f : var1) {
         d0 += f;
      }

      d0 /= var1.length;
      double d2 = 0.0;

      for (float f1 : var1) {
         double d1 = f1 - d0;
         d2 += d1 * d1;
      }

      d2 /= var1.length;
      double d3 = 1.0 / Math.sqrt(d2 + this.float208);
      float[] afloat = new float[var1.length];

      for (int i = 0; i < afloat.length; i++) {
         afloat[i] = (float)((var1[i] - d0) * d3) * this.call179[i] + this.call279[i];
      }

      return afloat;
   }

   public TrajectorySample on23(TrajectoryFrame var1) {
      int i = EnchantItemSpec(var1.boolean48());
      float[] afloat = CloudUserProfile(var1.call450());
      double d0 = 0.0;

      for (int j = 0; j < afloat.length; j++) {
         d0 += afloat[j] * (j + 2);
      }

      int l = Math.max(2, Math.min(this.int328, (int)Math.round(d0)));
      if (i == 0) {
         l = 0;
      } else if (i == 1) {
         l = 1;
      }

      float[] afloat1 = new float[]{var1.call424()[0], var1.call424()[1], var1.call132()[0], var1.call132()[1], var1.call133()[0], var1.call133()[1]};
      float[] afloat2 = new float[this.call139.length];

      for (int k = 0; k < afloat2.length; k++) {
         float f = afloat1[k] * this.call139[k].long111() + this.call139[k].uUID3();
         afloat2[k] = on23(f, this.call139[k]);
      }

      return new TrajectorySample(
         i,
         l,
         afloat2[0],
         afloat2[1],
         MediaTrackInfo(var1.event36Var159()[0]),
         MediaTrackInfo(var1.event36Var159()[1]),
         afloat2[2],
         afloat2[3],
         afloat2[4],
         afloat2[5],
         MediaTrackInfo(var1.call171())
      );
   }

   public void module() {
      on23(this.call211, this.call212, this.int327, this.int327, "attention projection");
      on23(this.call176, this.getTicks, this.int327, 1, "attention score");
      on23(this.val337, this.call278, this.int327 * 2, this.int327, "trunk");
      on23(this.call179.length == this.int327 && this.call279.length == this.int327, "layer norm");
      on23(this.call280, this.call223, this.int327, 3, "mode head");
      on23(this.call224, this.call225, this.int327, this.int328 - 1, "horizon head");
      on23(this.call226, this.call227, this.int327, 2, "motion head");
      on23(this.call222, this.call229, this.int327, 2, "aim head");
      on23(this.call230, this.call231, this.int327, 2, "terminal head");
      on23(this.call232, this.call183, this.int327, 2, "residual head");
      on23(this.call233, this.call234, this.int327, 1, "commit head");
      on23(Float.isFinite(this.float208) && this.float208 > 0.0F, "layer norm epsilon");
   }

   public static void on23(TrajectoryBatch var0, int var1, int var2, String var3) {
      on23(var0.call447(), var0.call448(), var1, var2 * 3, var3 + " input");
      on23(var0.entity(), var0.call449(), var2, var2 * 3, var3 + " hidden");
   }

   public static void on23(float[][] var0, float[] var1, int var2, int var3, String var4) {
      on23(var0.length == var3 && var1.length == var3, var4 + " output size");

      for (float[] afloat : var0) {
         on23(afloat.length == var2, var4 + " input size");
      }
   }

   public static void on23(TrajectoryDataset var0, JsonArray var1) {
      for (int i = 0; i < var1.size(); i++) {
         JsonObject jsonobject = var1.get(i).getAsJsonObject();
         JsonArray jsonarray = jsonobject.getAsJsonArray("raw_history");
         float[][] afloat = new float[jsonarray.size()][];
         float f = jsonobject.get("mouse_gcd").getAsFloat();

         for (int j = 0; j < jsonarray.size(); j++) {
            afloat[j] = var0.Easing(ColorAnimator(jsonarray.get(j).getAsJsonArray()), f);
         }

         TrajectoryFrame lilliil111i_l1i1illlili = var0.on23(afloat, afloat.length);
         float f1 = 0.0F;
         f1 = Math.max(f1, Easing(lilliil111i_l1i1illlili.boolean48(), ColorAnimator(jsonobject.getAsJsonArray("mode_logits"))));
         f1 = Math.max(f1, Easing(lilliil111i_l1i1illlili.call450(), ColorAnimator(jsonobject.getAsJsonArray("horizon_logits"))));
         f1 = Math.max(f1, Easing(lilliil111i_l1i1illlili.call424(), ColorAnimator(jsonobject.getAsJsonArray("motion"))));
         f1 = Math.max(f1, Easing(lilliil111i_l1i1illlili.event36Var159(), ColorAnimator(jsonobject.getAsJsonArray("aim_logits"))));
         f1 = Math.max(f1, Easing(lilliil111i_l1i1illlili.call132(), ColorAnimator(jsonobject.getAsJsonArray("terminal"))));
         f1 = Math.max(f1, Easing(lilliil111i_l1i1illlili.call133(), ColorAnimator(jsonobject.getAsJsonArray("residual"))));
         f1 = Math.max(f1, Math.abs(lilliil111i_l1i1illlili.call171() - jsonobject.get("commit_logit").getAsFloat()));
         on23(f1 <= 0.005F, "parity case " + i + ", error=" + f1);
         TrajectorySample lilliil111i_Var160 = var0.on23(lilliil111i_l1i1illlili);
         JsonObject jsonobject1 = jsonobject.getAsJsonObject("intent");
         on23(lilliil111i_Var160.call077() == jsonobject1.get("mode").getAsInt(), "parity mode " + i);
         on23(lilliil111i_Var160.call185() == jsonobject1.get("horizon_ticks").getAsInt(), "parity horizon " + i);
      }
   }

   public static float MediaTrackInfo(float var0, float var1) {
      float f = 360.0F / var1;
      return var0 - f * (float)Math.floor((var0 + f * 0.5F) / f);
   }

   public static float on23(float var0, TrajectoryMeta var1) {
      on23(Float.isFinite(var0), "non-finite " + var1.name());
      String s = var1.jsonObject3();

      return switch (s) {
         case "signed_mu" -> (float)(Math.signum(var0) * Math.log1p(Math.abs(var0) / var1.string53()));
         case "log1p" -> (float)Math.log1p(Math.max(var0, 0.0F) / var1.string53());
         case "linear" -> var0;
         default -> throw new IllegalStateException("unsupported feature transform " + var1.jsonObject3());
      };
   }

   public static float on23(float var0, TrajectoryPoint var1) {
      String s = var1.jsonObject3();

      return switch (s) {
         case "signed_mu" -> (float)(Math.signum(var0) * Math.expm1(Math.abs(var0)) * var1.string53());
         case "log1p" -> (float)Math.max(Math.expm1(var0) * var1.string53(), 0.0);
         case "linear" -> var0;
         default -> throw new IllegalStateException("unsupported target transform " + var1.jsonObject3());
      };
   }

   public static float on23(float var0, TrajectoryMeta var1, String var2) {
      float f = (var0 - var1.uUID3()) / var1.long111();
      on23(Float.isFinite(f), "non-finite normalized " + var2);
      return f;
   }

   public static float[] UiAnimation(float[] var0, float[][] var1, float[] var2) {
      float[] afloat = new float[var2.length];

      for (int i = 0; i < afloat.length; i++) {
         double d0 = var2[i];

         for (int j = 0; j < var0.length; j++) {
            d0 += var1[i][j] * var0[j];
         }

         afloat[i] = (float)d0;
      }

      return afloat;
   }

   public static float[] Easing(float[] var0, float[][] var1, float[] var2) {
      return UiAnimation(var0, var1, var2);
   }

   public static float MediaTrackInfo(float var0) {
      if (var0 >= 0.0F) {
         double d1 = Math.exp(-var0);
         return (float)(1.0 / (1.0 + d1));
      } else {
         double d0 = Math.exp(var0);
         return (float)(d0 / (1.0 + d0));
      }
   }

   public static float[] CloudUserProfile(float[] var0) {
      float f = var0[0];

      for (int i = 1; i < var0.length; i++) {
         f = Math.max(f, var0[i]);
      }

      float[] afloat = new float[var0.length];
      double d0 = 0.0;

      for (int j = 0; j < afloat.length; j++) {
         afloat[j] = (float)Math.exp(var0[j] - f);
         d0 += afloat[j];
      }

      for (int k = 0; k < afloat.length; k++) {
         afloat[k] /= (float)d0;
      }

      return afloat;
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

   public static float Easing(float[] var0, float[] var1) {
      on23(var0.length == var1.length, "parity vector length");
      float f = 0.0F;

      for (int i = 0; i < var0.length; i++) {
         f = Math.max(f, Math.abs(var0[i] - var1[i]));
      }

      return f;
   }

   public static float[] ColorAnimator(JsonArray var0) {
      float[] afloat = new float[var0.size()];

      for (int i = 0; i < afloat.length; i++) {
         afloat[i] = var0.get(i).getAsFloat();
      }

      return afloat;
   }

   public static float[][] ItemRegistry(JsonArray var0) {
      float[][] afloat = new float[var0.size()][];

      for (int i = 0; i < afloat.length; i++) {
         afloat[i] = ColorAnimator(var0.get(i).getAsJsonArray());
      }

      return afloat;
   }

   public static void on23(boolean var0, String var1) {
      if (!var0) {
         throw new IllegalStateException("invalid human-burst artifact: " + var1);
      }
   }
}
