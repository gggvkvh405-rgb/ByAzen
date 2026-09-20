package org.zenith.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigLoader {
   public static final String[] call242 = new String[]{
      "target_center_delta_yaw_count_units",
      "target_center_delta_pitch_count_units",
      "target_box_min_delta_yaw_count_units",
      "target_box_max_delta_yaw_count_units",
      "target_box_min_delta_pitch_count_units",
      "target_box_max_delta_pitch_count_units",
      "target_box_width_yaw_count_units",
      "target_box_height_pitch_count_units",
      "target_center_distance",
      "sim_2t_target_center_delta_yaw_count_units",
      "sim_2t_target_center_delta_pitch_count_units",
      "target_motion_x",
      "target_motion_y",
      "target_motion_z",
      "player_motion_x",
      "player_motion_y",
      "player_motion_z"
   };
   public static final String string120 = "/assets/zenith/aimpipe/burst_sum_model.json";
   public static final Path[] call158 = new Path[]{Path.of("burst_sum_model_6t.json"), Path.of("burst_sum_model.json")};
   public static final Path[] call159 = new Path[]{
      Path.of("..", "scripts", "out", "burst_sum_model_6t.json"),
      Path.of("scripts", "out", "burst_sum_model_6t.json"),
      Path.of("..", "scripts", "test", "aimpipe", "out", "burst_sum_model.json"),
      Path.of("scripts", "test", "aimpipe", "out", "burst_sum_model.json")
   };
   public final ConfigLoader.WindowBounds[] call023;
   public final float[] call186;
   public final float[] call118;
   public final float float307;
   public final float[][] call397;
   public final float[] call398;
   public final float[][] call399;
   public final float[] call400;
   public final float[][] call401;
   public final float[] call402;

   public ConfigLoader(
      ConfigLoader.WindowBounds[] var1,
      float[] var2,
      float[] var3,
      float var4,
      float[][] var5,
      float[] var6,
      float[][] var7,
      float[] var8,
      float[][] var9,
      float[] var10
   ) {
      this.call023 = var1;
      this.call186 = var2;
      this.call118 = var3;
      this.float307 = var4;
      this.call397 = var5;
      this.call398 = var6;
      this.call399 = var7;
      this.call400 = var8;
      this.call401 = var9;
      this.call402 = var10;
   }

   public int[] UiAnimation(float[] var1) {
      float[] afloat = this.Easing(var1);
      if (afloat == null) {
         return null;
      }

      float[] afloat1 = on23(afloat, this.call397, this.call398, true);
      float[] afloat2 = on23(afloat1, this.call399, this.call400, true);
      float[] afloat3 = on23(afloat2, this.call401, this.call402, false);
      int[] aint = new int[2];

      for (int i = 0; i < aint.length; i++) {
         double d0 = afloat3[i] * this.call118[i] + this.call186[i];
         d0 = Math.signum(d0) * Math.expm1(Math.abs(d0)) * this.float307;
         if (!Double.isFinite(d0) || d0 < -2.1474836E9F || d0 > 2.147483647E9) {
            return null;
         }

         aint[i] = (int)Math.round(d0);
      }

      return aint;
   }

   public float[] Easing(float[] var1) {
      if (var1 != null && var1.length == this.call023.length) {
         float[] afloat = new float[this.call023.length];

         for (int i = 0; i < afloat.length; i++) {
            float f = on23(var1[i], this.call023[i]);
            f = (f - this.call023[i].uUID3()) / this.call023[i].long111();
            if (!Float.isFinite(f)) {
               return null;
            }

            afloat[i] = f;
         }

         return afloat;
      } else {
         return null;
      }
   }

   public static ConfigLoader string46() {
      for (Path path : call158) {
         ConfigLoader illi1iiillill111l = ItemSpec(path);
         if (illi1iiillill111l != null) {
            return illi1iiillill111l;
         }
      }

      for (Path path1 : call159) {
         ConfigLoader illi1iiillill111l1 = ItemSpec(path1);
         if (illi1iiillill111l1 != null) {
            return illi1iiillill111l1;
         }
      }

      return long108();
   }

   public static ConfigLoader ItemSpec(Path var0) {
      try {
         return !Files.isRegularFile(var0) ? null : ItemServiceBase(Files.readString(var0, StandardCharsets.UTF_8), var0.toAbsolutePath().toString());
      } catch (Throwable throwable) {
         System.err.println("[BurstSumModel] failed to load " + var0 + ": " + throwable);
         return null;
      }
   }

   public static ConfigLoader long108() {
      try (InputStream inputstream = ConfigLoader.class.getResourceAsStream("/assets/zenith/aimpipe/burst_sum_model.json")) {
         return inputstream == null
            ? null
            : ItemServiceBase(new String(inputstream.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/aimpipe/burst_sum_model.json");
      } catch (Throwable throwable1) {
         System.err.println("[BurstSumModel] failed to load /assets/zenith/aimpipe/burst_sum_model.json: " + throwable1);
         return null;
      }
   }

   public static ConfigLoader ItemServiceBase(String var0, String var1) {
      JsonObject jsonobject = JsonParser.parseString(var0).getAsJsonObject();
      if (jsonobject.get("format_version").getAsInt() != 1) {
         throw new IllegalStateException("unsupported burst-sum model format");
      }

      JsonArray jsonarray = jsonobject.getAsJsonArray("feature_spec");
      if (jsonarray.size() != call242.length) {
         throw new IllegalStateException("burst-sum feature count mismatch");
      }

      ConfigLoader.WindowBounds[] ailli1iiillill111l_ii1il11l111ii11iil = new ConfigLoader.WindowBounds[jsonarray.size()];

      for (int i = 0; i < ailli1iiillill111l_ii1il11l111ii11iil.length; i++) {
         JsonObject jsonobject1 = jsonarray.get(i).getAsJsonObject();
         if (!call242[i].equals(jsonobject1.get("name").getAsString())) {
            throw new IllegalStateException("burst-sum feature order mismatch at " + i);
         }

         ailli1iiillill111l_ii1il11l111ii11iil[i] = new ConfigLoader.WindowBounds(
            jsonobject1.get("transform").getAsString(),
            jsonobject1.get("scale").getAsFloat(),
            jsonobject1.get("mean").getAsFloat(),
            jsonobject1.get("std").getAsFloat()
         );
         if (!Float.isFinite(ailli1iiillill111l_ii1il11l111ii11iil[i].long111()) || ailli1iiillill111l_ii1il11l111ii11iil[i].long111() <= 0.0F) {
            throw new IllegalStateException("invalid burst-sum feature normalization");
         }
      }

      JsonObject jsonobject6 = jsonobject.has("output_transform") ? jsonobject.getAsJsonObject("output_transform") : jsonobject.getAsJsonObject("output");
      if (jsonobject6.has("type") && !"signed_mu".equals(jsonobject6.get("type").getAsString())) {
         throw new IllegalStateException("unsupported burst-sum output transform");
      }

      float f = jsonobject6.get("scale").getAsFloat();
      float[] afloat = ColorAnimator(jsonobject6.getAsJsonArray("mean"));
      float[] afloat1 = ColorAnimator(jsonobject6.getAsJsonArray("std"));
      float[][] afloat2;
      float[] afloat3;
      float[][] afloat4;
      float[] afloat5;
      float[][] afloat6;
      float[] afloat7;
      if (jsonobject.has("network")) {
         JsonObject jsonobject2 = jsonobject.getAsJsonObject("network");
         if (!"tanh".equals(jsonobject2.get("activation").getAsString())) {
            throw new IllegalStateException("unsupported burst-sum activation");
         }

         JsonArray jsonarray1 = jsonobject2.getAsJsonArray("layers");
         if (jsonarray1.size() != 3) {
            throw new IllegalStateException("burst-sum network must contain three layers");
         }

         JsonObject jsonobject3 = jsonarray1.get(0).getAsJsonObject();
         JsonObject jsonobject4 = jsonarray1.get(1).getAsJsonObject();
         JsonObject jsonobject5 = jsonarray1.get(2).getAsJsonObject();
         afloat2 = ItemRegistry(jsonobject3.getAsJsonArray("weight"));
         afloat3 = ColorAnimator(jsonobject3.getAsJsonArray("bias"));
         afloat4 = ItemRegistry(jsonobject4.getAsJsonArray("weight"));
         afloat5 = ColorAnimator(jsonobject4.getAsJsonArray("bias"));
         afloat6 = ItemRegistry(jsonobject5.getAsJsonArray("weight"));
         afloat7 = ColorAnimator(jsonobject5.getAsJsonArray("bias"));
      } else {
         JsonObject jsonobject7 = jsonobject.getAsJsonObject("weights");
         afloat2 = ItemRegistry(jsonobject7.getAsJsonArray("layer_0_weight"));
         afloat3 = ColorAnimator(jsonobject7.getAsJsonArray("layer_0_bias"));
         afloat4 = ItemRegistry(jsonobject7.getAsJsonArray("layer_1_weight"));
         afloat5 = ColorAnimator(jsonobject7.getAsJsonArray("layer_1_bias"));
         afloat6 = ItemRegistry(jsonobject7.getAsJsonArray("layer_2_weight"));
         afloat7 = ColorAnimator(jsonobject7.getAsJsonArray("layer_2_bias"));
      }

      on23(afloat2, afloat3, ailli1iiillill111l_ii1il11l111ii11iil.length, "layer 0");
      on23(afloat4, afloat5, afloat3.length, "layer 1");
      on23(afloat6, afloat7, afloat5.length, "layer 2");
      if (afloat7.length == 2 && afloat.length == 2 && afloat1.length == 2 && Float.isFinite(f) && !(f <= 0.0F)) {
         ConfigLoader illi1iiillill111l = new ConfigLoader(
            ailli1iiillill111l_ii1il11l111ii11iil, afloat, afloat1, f, afloat2, afloat3, afloat4, afloat5, afloat6, afloat7
         );
         System.out
            .println("[BurstSumModel] loaded " + var1 + " (features=" + ailli1iiillill111l_ii1il11l111ii11iil.length + ", hidden=" + afloat3.length + ")");
         return illi1iiillill111l;
      } else {
         throw new IllegalStateException("invalid burst-sum output shape");
      }
   }

   public static void on23(float[][] var0, float[] var1, int var2, String var3) {
      if (var0.length != var1.length) {
         throw new IllegalStateException(var3 + " output size mismatch");
      }

      for (float[] afloat : var0) {
         if (afloat.length != var2) {
            throw new IllegalStateException(var3 + " input size mismatch");
         }
      }
   }

   public static float on23(float var0, ConfigLoader.WindowBounds var1) {
      String s = var1.jsonObject3();

      return switch (s) {
         case "mu" -> (float)(Math.signum(var0) * Math.log1p(Math.abs(var0) / var1.string53()));
         case "log" -> (float)Math.log1p(Math.max(var0, 0.0F));
         case "lin", "linear" -> var0;
         default -> throw new IllegalStateException("unknown burst-sum feature transform " + var1.jsonObject3());
      };
   }

   public static float[] on23(float[] var0, float[][] var1, float[] var2, boolean var3) {
      float[] afloat = new float[var2.length];

      for (int i = 0; i < afloat.length; i++) {
         double d0 = var2[i];

         for (int j = 0; j < var0.length; j++) {
            d0 += var1[i][j] * var0[j];
         }

         afloat[i] = var3 ? (float)Math.tanh(d0) : (float)d0;
      }

      return afloat;
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


   public record WindowBounds(String string35, float float71, float float72, float float73) {
      public String jsonObject3() {
         return this.string35;
      }

      public float string53() {
         return this.float71;
      }

      public float uUID3() {
         return this.float72;
      }

      public float long111() {
         return this.float73;
      }
   }
}
