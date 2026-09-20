package org.zenith.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class InventoryCodec {
   public static final int int362 = 3;
   public static final int int363 = 4;
   public static final float float233 = -2.5F;
   public static final float float234 = 0.7F;
   public static final int int364 = 0;
   public static final int int365 = 1;
   public static final int getValue = 2;
   public String[] call180;
   public int[] call140;
   public float[] call029;
   public float[] call105;
   public float float235;
   public int int366;
   public int int341;
   public int int367;
   public int int368;
   public float[][] call228;
   public float[] call426;
   public float[] call281;
   public float[] call282;
   public float[][] call082;
   public float[] call283;
   public float[][] call284;
   public float[] call285;
   public float[][] call066;
   public float[] call067;
   public float[][] call181;
   public float[] val479;
   public float[][] call136;
   public float[] call272;
   public static final String string94 = "/assets/zenith/aimpipe/intent_selector.json";
   public static final Path path7 = Path.of("intent_selector.json");
   public static final Path[] call182 = new Path[]{
      Path.of("scripts", "aimpipe", "intent_selector.json"), Path.of("..", "scripts", "aimpipe", "intent_selector.json")
   };

   public CodecRow SimpleItemBuilder(float[] var1) {
      float[] afloat = new float[this.int366];

      for (int i = 0; i < this.int366; i++) {
         float f = var1[i];

         f = switch (this.call140[i]) {
            case 1 -> CloudApiClient(f);
            case 2 -> (float)Math.log1p(Math.max(f, 0.0F));
            default -> f;
         };
         afloat[i] = (f - this.call029[i]) / this.call105[i];
      }

      float[] afloat3 = on23(this.call228, this.call426, afloat);
      on23(afloat3, this.call281, this.call282);
      ItemServiceBase(afloat3);
      float[] afloat4 = on23(this.call082, this.call283, afloat3);
      ItemServiceBase(afloat4);
      CodecRow il11lill1lil1l1iill_ii1il11l111ii11iil = new CodecRow();
      il11lill1lil1l1iill_ii1il11l111ii11iil.float64 = on23(this.call284, this.call285, afloat4)[0];
      float[] afloat1 = on23(this.call066, this.call067, afloat4);
      System.arraycopy(afloat1, 0, il11lill1lil1l1iill_ii1il11l111ii11iil.call149, 0, afloat1.length);
      float[] afloat2 = on23(this.call181, this.val479, afloat4);
      byte b0 = 9;
      il11lill1lil1l1iill_ii1il11l111ii11iil.call116 = new float[4][this.int367][b0];
      int j = 0;

      for (int k = 0; k < 4; k++) {
         for (int l = 0; l < this.int367; l++) {
            for (int i1 = 0; i1 < b0; i1++) {
               il11lill1lil1l1iill_ii1il11l111ii11iil.call116[k][l][i1] = afloat2[j++];
            }
         }
      }

      float[] afloat5 = on23(this.call136, this.call272, afloat4);
      il11lill1lil1l1iill_ii1il11l111ii11iil.call267 = new float[this.int368][3];
      j = 0;

      for (int j1 = 0; j1 < this.int368; j1++) {
         for (int k1 = 0; k1 < 3; k1++) {
            il11lill1lil1l1iill_ii1il11l111ii11iil.call267[j1][k1] = afloat5[j++];
         }
      }

      return il11lill1lil1l1iill_ii1il11l111ii11iil;
   }

   public static float CloudApiClient(float var0) {
      return (float)Math.copySign(Math.log1p(Math.abs(var0)), var0);
   }

   public static float[] on23(float[][] var0, float[] var1, float[] var2) {
      float[] afloat = new float[var0.length];

      for (int i = 0; i < var0.length; i++) {
         float f = var1[i];
         float[] afloat1 = var0[i];

         for (int j = 0; j < var2.length; j++) {
            f += afloat1[j] * var2[j];
         }

         afloat[i] = f;
      }

      return afloat;
   }

   public static void on23(float[] var0, float[] var1, float[] var2) {
      double d0 = 0.0;

      for (float f : var0) {
         d0 += f;
      }

      d0 /= var0.length;
      double d2 = 0.0;

      for (float f1 : var0) {
         double d1 = f1 - d0;
         d2 += d1 * d1;
      }

      d2 /= var0.length;
      double d3 = 1.0 / Math.sqrt(d2 + 1.0E-5);

      for (int i = 0; i < var0.length; i++) {
         var0[i] = (float)((var0[i] - d0) * d3 * var1[i] + var2[i]);
      }
   }

   public static void ItemServiceBase(float[] var0) {
      for (int i = 0; i < var0.length; i++) {
         double d0 = var0[i];
         var0[i] = (float)(0.5 * d0 * (1.0 + NbtItemSpec(d0 / Math.sqrt(2.0))));
      }
   }

   public static double NbtItemSpec(double var0) {
      double d0 = Math.signum(var0);
      double d1 = Math.abs(var0);
      double d2 = 1.0 / (1.0 + 0.3275911 * d1);
      double d3 = 1.0 - ((((1.061405429 * d2 - 1.453152027) * d2 + 1.421413741) * d2 - 0.284496736) * d2 + 0.254829592) * d2 * Math.exp(-d1 * d1);
      return d0 * d3;
   }

   public static InventoryCodec string98() {
      InventoryCodec il11lill1lil1l1iill = SimpleItemBuilder(path7);
      if (il11lill1lil1l1iill != null) {
         return il11lill1lil1l1iill;
      }

      for (Path path : call182) {
         il11lill1lil1l1iill = SimpleItemBuilder(path);
         if (il11lill1lil1l1iill != null) {
            return il11lill1lil1l1iill;
         }
      }

      return file4();
   }

   public static InventoryCodec SimpleItemBuilder(Path var0) {
      try {
         return !Files.isRegularFile(var0) ? null : StringCodec(Files.readString(var0, StandardCharsets.UTF_8), var0.toAbsolutePath().toString());
      } catch (Throwable throwable) {
         System.err.println("[MotorIntent] failed to load " + var0 + ": " + throwable);
         return null;
      }
   }

   public static InventoryCodec file4() {
      try (InputStream inputstream = InventoryCodec.class.getResourceAsStream("/assets/zenith/aimpipe/intent_selector.json")) {
         return inputstream == null
            ? null
            : StringCodec(new String(inputstream.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/aimpipe/intent_selector.json");
      } catch (Throwable throwable1) {
         System.err.println("[MotorIntent] failed to load /assets/zenith/aimpipe/intent_selector.json: " + throwable1);
         return null;
      }
   }

   public static InventoryCodec StringCodec(String var0, String var1) {
      JsonObject jsonobject = JsonParser.parseString(var0).getAsJsonObject();
      if (!"motor_intent_selector_mlp".equals(jsonobject.get("model").getAsString())) {
         throw new IllegalStateException("unexpected selector model");
      }

      if (jsonobject.get("format_version").getAsInt() != 3) {
         throw new IllegalStateException("unsupported selector format_version " + jsonobject.get("format_version").getAsInt() + " (need 3)");
      }

      JsonObject jsonobject1 = jsonobject.getAsJsonObject("architecture");
      InventoryCodec il11lill1lil1l1iill = new InventoryCodec();
      il11lill1lil1l1iill.int366 = jsonobject1.get("input_dim").getAsInt();
      il11lill1lil1l1iill.int341 = jsonobject1.get("hidden").getAsInt();
      il11lill1lil1l1iill.int367 = jsonobject1.get("mixture_components").getAsInt();
      il11lill1lil1l1iill.int368 = jsonobject1.get("rest_mixture_components").getAsInt();
      JsonArray jsonarray = jsonobject1.getAsJsonArray("intents");
      if (jsonarray.size() != 4) {
         throw new IllegalStateException("selector intent taxonomy mismatch");
      }

      for (int i = 0; i < jsonarray.size(); i++) {
         if (!PermissionListCodec.call175[i].equals(jsonarray.get(i).getAsString())) {
            throw new IllegalStateException("selector intent order mismatch");
         }
      }

      if (jsonobject1.getAsJsonArray("param_names").size() != 4) {
         throw new IllegalStateException("selector param count mismatch");
      }

      il11lill1lil1l1iill.float235 = jsonobject.get("move_threshold").getAsFloat();
      JsonArray jsonarray1 = jsonobject.getAsJsonArray("feature_spec");
      if (jsonarray1.size() != il11lill1lil1l1iill.int366) {
         throw new IllegalStateException("feature spec size mismatch");
      }

      il11lill1lil1l1iill.call180 = new String[jsonarray1.size()];
      il11lill1lil1l1iill.call140 = new int[jsonarray1.size()];
      il11lill1lil1l1iill.call029 = new float[jsonarray1.size()];
      il11lill1lil1l1iill.call105 = new float[jsonarray1.size()];

      for (int j = 0; j < jsonarray1.size(); j++) {
         JsonObject jsonobject2 = jsonarray1.get(j).getAsJsonObject();
         il11lill1lil1l1iill.call180[j] = jsonobject2.get("name").getAsString();
         String s = jsonobject2.get("transform").getAsString();
         int[] aint = il11lill1lil1l1iill.call140;

         aint[j] = switch (s) {
            case "signed_log" -> 1;
            case "log1p" -> 2;
            case "linear" -> 0;
            default -> throw new IllegalStateException("unknown transform " + s);
         };
         il11lill1lil1l1iill.call029[j] = jsonobject2.get("mean").getAsFloat();
         il11lill1lil1l1iill.call105[j] = jsonobject2.get("std").getAsFloat();
      }

      JsonObject jsonobject3 = jsonobject.getAsJsonObject("weights");
      il11lill1lil1l1iill.call228 = PotionItemBuilder(jsonobject3, "backbone.0.weight");
      il11lill1lil1l1iill.call426 = ProfileItemBuilder(jsonobject3, "backbone.0.bias");
      il11lill1lil1l1iill.call281 = ProfileItemBuilder(jsonobject3, "backbone.1.weight");
      il11lill1lil1l1iill.call282 = ProfileItemBuilder(jsonobject3, "backbone.1.bias");
      il11lill1lil1l1iill.call082 = PotionItemBuilder(jsonobject3, "backbone.4.weight");
      il11lill1lil1l1iill.call283 = ProfileItemBuilder(jsonobject3, "backbone.4.bias");
      il11lill1lil1l1iill.call284 = PotionItemBuilder(jsonobject3, "move_head.weight");
      il11lill1lil1l1iill.call285 = ProfileItemBuilder(jsonobject3, "move_head.bias");
      il11lill1lil1l1iill.call066 = PotionItemBuilder(jsonobject3, "intent_head.weight");
      il11lill1lil1l1iill.call067 = ProfileItemBuilder(jsonobject3, "intent_head.bias");
      il11lill1lil1l1iill.call181 = PotionItemBuilder(jsonobject3, "params_head.weight");
      il11lill1lil1l1iill.val479 = ProfileItemBuilder(jsonobject3, "params_head.bias");
      il11lill1lil1l1iill.call136 = PotionItemBuilder(jsonobject3, "rest_head.weight");
      il11lill1lil1l1iill.call272 = ProfileItemBuilder(jsonobject3, "rest_head.bias");
      int k = 4 * il11lill1lil1l1iill.int367 * 9;
      if (il11lill1lil1l1iill.call181.length != k) {
         throw new IllegalStateException("params head size mismatch: " + il11lill1lil1l1iill.call181.length + " vs " + k);
      } else if (il11lill1lil1l1iill.call136.length != il11lill1lil1l1iill.int368 * 3) {
         throw new IllegalStateException("rest head size mismatch");
      } else if (il11lill1lil1l1iill.call228[0].length == il11lill1lil1l1iill.int366 && il11lill1lil1l1iill.call228.length == il11lill1lil1l1iill.int341) {
         System.out
            .println(
               "[MotorIntent] loaded selector "
                  + var1
                  + " (in="
                  + il11lill1lil1l1iill.int366
                  + ", hidden="
                  + il11lill1lil1l1iill.int341
                  + ", mixture="
                  + il11lill1lil1l1iill.int367
                  + ", threshold="
                  + il11lill1lil1l1iill.float235
                  + ")"
            );
         return il11lill1lil1l1iill;
      } else {
         throw new IllegalStateException("backbone.0 shape mismatch");
      }
   }

   public static float[][] PotionItemBuilder(JsonObject var0, String var1) {
      JsonArray jsonarray = var0.getAsJsonArray(var1);
      float[][] afloat = new float[jsonarray.size()][];

      for (int i = 0; i < jsonarray.size(); i++) {
         JsonArray jsonarray1 = jsonarray.get(i).getAsJsonArray();
         afloat[i] = new float[jsonarray1.size()];

         for (int j = 0; j < jsonarray1.size(); j++) {
            afloat[i][j] = jsonarray1.get(j).getAsFloat();
         }
      }

      return afloat;
   }

   public static float[] ProfileItemBuilder(JsonObject var0, String var1) {
      JsonArray jsonarray = var0.getAsJsonArray(var1);
      float[] afloat = new float[jsonarray.size()];

      for (int i = 0; i < jsonarray.size(); i++) {
         afloat[i] = jsonarray.get(i).getAsFloat();
      }

      return afloat;
   }
}
