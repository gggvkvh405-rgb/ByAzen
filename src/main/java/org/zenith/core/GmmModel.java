package org.zenith.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GmmModel {
   public GmmComponent[] call109;
   public int[][] call051;
   public int[][] call081;
   public float float215;
   public float float216;
   public int int339;
   public int int340;
   public int int341;
   public int int342;
   public int int343;
   public int int344;
   public int int345;
   public int int346;
   public int int347;
   public int int348;
   public int int349;
   public float[][] call148;
   public float[][] call053;
   public float[] call018;
   public float[] call151;
   public float[][] call066;
   public float[] call067;
   public float[][] call088;
   public float[][] call153;
   public float[][] call113;
   public float[][] call055;
   public float[] call150;
   public float[] call115;
   public float[][] call054;
   public float[] call042;
   public float[][] call087;
   public float[] call033;
   public float[][] call154;
   public float[] call086;
   public float[][] call043;
   public float[] call056;
   public float[][] call068;
   public float[] call184;
   public static final String string89 = "/assets/zenith/aimpipe/aim_policy.json";
   public static final Path path6 = Path.of("aim_policy.json");
   public static final Path[] call395 = new Path[]{Path.of("scripts", "aimpipe", "aim_policy.json"), Path.of("..", "scripts", "aimpipe", "aim_policy.json")};

   public static GmmModel long110() {
      GmmModel l11il1i1iil1lll111l1111llliil = NbtItemSpec(path6);
      if (l11il1i1iil1lll111l1111llliil != null) {
         return l11il1i1iil1lll111l1111llliil;
      }

      l11il1i1iil1lll111l1111llliil = string48();
      if (l11il1i1iil1lll111l1111llliil != null) {
         return l11il1i1iil1lll111l1111llliil;
      }

      for (Path path : call395) {
         l11il1i1iil1lll111l1111llliil = NbtItemSpec(path);
         if (l11il1i1iil1lll111l1111llliil != null) {
            return l11il1i1iil1lll111l1111llliil;
         }
      }

      return null;
   }

   public static GmmModel NbtItemSpec(Path var0) {
      try {
         return !Files.isRegularFile(var0) ? null : PotionItemBuilder(Files.readString(var0, StandardCharsets.UTF_8), var0.toAbsolutePath().toString());
      } catch (Throwable throwable) {
         System.err.println("[AimPolicy] failed to load " + var0 + ": " + throwable);
         return null;
      }
   }

   public static GmmModel string48() {
      try (InputStream inputstream = GmmModel.class.getResourceAsStream("/assets/zenith/aimpipe/aim_policy.json")) {
         return inputstream == null
            ? null
            : PotionItemBuilder(new String(inputstream.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/aimpipe/aim_policy.json");
      } catch (Throwable throwable1) {
         System.err.println("[AimPolicy] failed to load /assets/zenith/aimpipe/aim_policy.json: " + throwable1);
         return null;
      }
   }

   public static GmmModel PotionItemBuilder(String var0, String var1) {
      GmmModel l11il1i1iil1lll111l1111llliil = BotFeatureRegistry(JsonParser.parseString(var0).getAsJsonObject());
      System.out
         .println(
            "[AimPolicy] loaded "
               + var1
               + " (planner="
               + l11il1i1iil1lll111l1111llliil.int341
               + ", motor="
               + l11il1i1iil1lll111l1111llliil.int342
               + ", bins="
               + l11il1i1iil1lll111l1111llliil.int343
               + "/"
               + l11il1i1iil1lll111l1111llliil.int344
               + ", cadence="
               + l11il1i1iil1lll111l1111llliil.int339
               + ", z="
               + l11il1i1iil1lll111l1111llliil.int348
               + ")"
         );
      return l11il1i1iil1lll111l1111llliil;
   }

   public static GmmModel BotFeatureRegistry(JsonObject var0) {
      int i = var0.has("format_version") ? var0.get("format_version").getAsInt() : 1;
      if (i != 5) {
         throw new IllegalStateException("unsupported artifact format_version " + i + " (expected 5; retrain and re-run policy/export.py)");
      }

      GmmModel l11il1i1iil1lll111l1111llliil = new GmmModel();
      JsonArray jsonarray = var0.getAsJsonArray("feature_spec");
      l11il1i1iil1lll111l1111llliil.call109 = new GmmComponent[jsonarray.size()];

      for (int j = 0; j < jsonarray.size(); j++) {
         JsonObject jsonobject = jsonarray.get(j).getAsJsonObject();
         l11il1i1iil1lll111l1111llliil.call109[j] = new GmmComponent(
            jsonobject.get("index").getAsInt(),
            "mu".equals(jsonobject.get("transform").getAsString()),
            jsonobject.get("scale").getAsFloat(),
            jsonobject.get("mean").getAsFloat(),
            jsonobject.get("std").getAsFloat()
         );
      }

      l11il1i1iil1lll111l1111llliil.call051 = new int[2][];
      l11il1i1iil1lll111l1111llliil.call081 = new int[2][];
      String[] astring = new String[]{"bins_yaw", "bins_pitch"};

      for (int l = 0; l < 2; l++) {
         JsonArray jsonarray1 = var0.getAsJsonArray(astring[l]);
         l11il1i1iil1lll111l1111llliil.call051[l] = new int[jsonarray1.size()];
         l11il1i1iil1lll111l1111llliil.call081[l] = new int[jsonarray1.size()];

         for (int k = 0; k < jsonarray1.size(); k++) {
            JsonObject jsonobject1 = jsonarray1.get(k).getAsJsonObject();
            l11il1i1iil1lll111l1111llliil.call051[l][k] = jsonobject1.get("lo").getAsInt();
            l11il1i1iil1lll111l1111llliil.call081[l][k] = jsonobject1.get("hi").getAsInt();
         }
      }

      JsonObject jsonobject2 = var0.getAsJsonObject("sampling");
      l11il1i1iil1lll111l1111llliil.float215 = jsonobject2.get("temperature").getAsFloat();
      l11il1i1iil1lll111l1111llliil.float216 = jsonobject2.get("top_p").getAsFloat();
      l11il1i1iil1lll111l1111llliil.int339 = var0.getAsJsonObject("planner").get("ticks").getAsInt();
      JsonObject jsonobject3 = var0.getAsJsonObject("model");
      l11il1i1iil1lll111l1111llliil.int340 = jsonobject3.get("in_dim").getAsInt();
      l11il1i1iil1lll111l1111llliil.int341 = jsonobject3.get("hidden").getAsInt();
      l11il1i1iil1lll111l1111llliil.int342 = jsonobject3.get("motor_hidden").getAsInt();
      l11il1i1iil1lll111l1111llliil.int343 = jsonobject3.get("n_bins_yaw").getAsInt();
      l11il1i1iil1lll111l1111llliil.int344 = jsonobject3.get("n_bins_pitch").getAsInt();
      l11il1i1iil1lll111l1111llliil.int345 = jsonobject3.get("zero_bin_yaw").getAsInt();
      l11il1i1iil1lll111l1111llliil.int346 = jsonobject3.get("zero_bin_pitch").getAsInt();
      l11il1i1iil1lll111l1111llliil.int347 = jsonobject3.get("bin_embed").getAsInt();
      l11il1i1iil1lll111l1111llliil.int348 = jsonobject3.get("z_dim").getAsInt();
      l11il1i1iil1lll111l1111llliil.int349 = jsonobject3.get("prior_hidden").getAsInt();
      JsonObject jsonobject4 = var0.getAsJsonObject("weights");
      l11il1i1iil1lll111l1111llliil.call148 = ItemRegistry(jsonobject4.getAsJsonArray("planner_weight_ih"));
      l11il1i1iil1lll111l1111llliil.call053 = ItemRegistry(jsonobject4.getAsJsonArray("planner_weight_hh"));
      l11il1i1iil1lll111l1111llliil.call018 = ColorAnimator(jsonobject4.getAsJsonArray("planner_bias_ih"));
      l11il1i1iil1lll111l1111llliil.call151 = ColorAnimator(jsonobject4.getAsJsonArray("planner_bias_hh"));
      l11il1i1iil1lll111l1111llliil.call066 = ItemRegistry(jsonobject4.getAsJsonArray("intent_weight"));
      l11il1i1iil1lll111l1111llliil.call067 = ColorAnimator(jsonobject4.getAsJsonArray("intent_bias"));
      l11il1i1iil1lll111l1111llliil.call088 = ItemRegistry(jsonobject4.getAsJsonArray("yaw_embedding"));
      l11il1i1iil1lll111l1111llliil.call153 = ItemRegistry(jsonobject4.getAsJsonArray("pitch_embedding"));
      l11il1i1iil1lll111l1111llliil.call113 = ItemRegistry(jsonobject4.getAsJsonArray("motor_weight_ih"));
      l11il1i1iil1lll111l1111llliil.call055 = ItemRegistry(jsonobject4.getAsJsonArray("motor_weight_hh"));
      l11il1i1iil1lll111l1111llliil.call150 = ColorAnimator(jsonobject4.getAsJsonArray("motor_bias_ih"));
      l11il1i1iil1lll111l1111llliil.call115 = ColorAnimator(jsonobject4.getAsJsonArray("motor_bias_hh"));
      l11il1i1iil1lll111l1111llliil.call054 = ItemRegistry(jsonobject4.getAsJsonArray("yaw_head_weight"));
      l11il1i1iil1lll111l1111llliil.call042 = ColorAnimator(jsonobject4.getAsJsonArray("yaw_head_bias"));
      l11il1i1iil1lll111l1111llliil.call087 = ItemRegistry(jsonobject4.getAsJsonArray("pitch_context_weight"));
      l11il1i1iil1lll111l1111llliil.call033 = ColorAnimator(jsonobject4.getAsJsonArray("pitch_context_bias"));
      l11il1i1iil1lll111l1111llliil.call154 = ItemRegistry(jsonobject4.getAsJsonArray("pitch_head_weight"));
      l11il1i1iil1lll111l1111llliil.call086 = ColorAnimator(jsonobject4.getAsJsonArray("pitch_head_bias"));
      l11il1i1iil1lll111l1111llliil.call043 = ItemRegistry(jsonobject4.getAsJsonArray("prior_weight_0"));
      l11il1i1iil1lll111l1111llliil.call056 = ColorAnimator(jsonobject4.getAsJsonArray("prior_bias_0"));
      l11il1i1iil1lll111l1111llliil.call068 = ItemRegistry(jsonobject4.getAsJsonArray("prior_weight_1"));
      l11il1i1iil1lll111l1111llliil.call184 = ColorAnimator(jsonobject4.getAsJsonArray("prior_bias_1"));
      l11il1i1iil1lll111l1111llliil.validate();
      return l11il1i1iil1lll111l1111llliil;
   }

   public void validate() {
      if (this.call109.length == this.int340 && this.int339 >= 1) {
         if (this.call051[0].length != this.int343 || this.call051[1].length != this.int344) {
            throw new IllegalStateException("bin table size mismatch");
         }

         if (this.int345 < 0 || this.int345 >= this.int343 || this.int346 < 0 || this.int346 >= this.int344) {
            throw new IllegalStateException("zero-bin index out of range");
         }

         if (this.call148.length == 4 * this.int341 && this.call113.length == 3 * this.int342) {
            if (this.int348 < 1 || this.call113[0].length != this.int342 + 2 * this.int347 + this.int348) {
               throw new IllegalStateException("motor input width must include the gesture latent");
            } else if (this.call043.length != this.int349
               || this.call043[0].length != this.int342 + 2 * this.int347
               || this.call068.length != 2 * this.int348
               || this.call068[0].length != this.int349) {
               throw new IllegalStateException("gesture prior weight shape mismatch");
            }
         } else {
            throw new IllegalStateException("recurrent weight shape mismatch");
         }
      } else {
         throw new IllegalStateException("feature/planner configuration mismatch");
      }
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
}
