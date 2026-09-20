package org.zenith.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class MotionSampleStore {
   public static final int int331 = 11;
   public static final int int332 = 50;
   public static final String string81 = "/assets/zenith/pointclick/point_click_dqn.json";
   public static final Path path5 = Path.of("point_click_dqn.json");
   public final float[][] call235;
   public final float[] call083;
   public final float[][] call142;
   public final float[] call286;

   public MotionSampleStore(float[][] var1, float[] var2, float[][] var3, float[] var4) {
      this.call235 = var1;
      this.call083 = var2;
      this.call142 = var3;
      this.call286 = var4;
   }

   public static MotionSampleStore float20() {
      MotionSampleStore MotionSampleStore = NbtEditor(path5);
      if (MotionSampleStore != null) {
         return MotionSampleStore;
      }

      try (InputStream inputstream = MotionSampleStore.class.getResourceAsStream("/assets/zenith/pointclick/point_click_dqn.json")) {
         return inputstream == null
            ? null
            : CloudApiClient(new String(inputstream.readAllBytes(), StandardCharsets.UTF_8), "/assets/zenith/pointclick/point_click_dqn.json");
      } catch (Throwable throwable1) {
         System.err.println("[PointClick] failed to load /assets/zenith/pointclick/point_click_dqn.json: " + throwable1);
         return null;
      }
   }

   public static MotionSampleStore NbtEditor(Path var0) {
      try {
         return !Files.isRegularFile(var0) ? null : CloudApiClient(Files.readString(var0, StandardCharsets.UTF_8), var0.toAbsolutePath().toString());
      } catch (Throwable throwable) {
         System.err.println("[PointClick] failed to load " + var0 + ": " + throwable);
         return null;
      }
   }

   public static MotionSampleStore CloudApiClient(String var0, String var1) {
      JsonObject jsonobject = JsonParser.parseString(var0).getAsJsonObject();
      if (jsonobject.get("format_version").getAsInt() == 1 && "point_and_click_dqn".equals(jsonobject.get("model").getAsString())) {
         JsonObject jsonobject1 = jsonobject.getAsJsonObject("architecture");
         on23(jsonobject1.get("input").getAsInt() == 11, "input size");
         on23(jsonobject1.get("output").getAsInt() == 50, "output size");
         int i = jsonobject1.get("hidden").getAsInt();
         JsonObject jsonobject2 = jsonobject.getAsJsonObject("weights");
         MotionSampleStore MotionSampleStore = new MotionSampleStore(
            on23(jsonobject2.getAsJsonArray("hidden_kernel"), 11, i),
            UiAnimation(jsonobject2.getAsJsonArray("hidden_bias"), i),
            on23(jsonobject2.getAsJsonArray("output_kernel"), i, 50),
            UiAnimation(jsonobject2.getAsJsonArray("output_bias"), 50)
         );
         on23(MotionSampleStore, jsonobject.getAsJsonArray("parity"));
         System.out.println("[PointClick] loaded DQN " + var1 + " (11x" + i + "x50)");
         return MotionSampleStore;
      } else {
         throw new IllegalStateException("unsupported point-click policy artifact");
      }
   }

   public int ModuleSnapshotDto(float[] var1) {
      float[] afloat = this.InventoryUtils(var1);
      int i = 0;

      for (int j = 1; j < afloat.length; j++) {
         if (afloat[j] > afloat[i]) {
            i = j;
         }
      }

      return i;
   }

   public float[] InventoryUtils(float[] var1) {
      if (var1.length != 11) {
         throw new IllegalArgumentException("expected 11 inputs");
      }

      float[] afloat = new float[this.call083.length];

      for (int i = 0; i < afloat.length; i++) {
         float f = this.call083[i];

         for (int j = 0; j < var1.length; j++) {
            f += var1[j] * this.call235[j][i];
         }

         afloat[i] = Math.max(f, 0.0F);
      }

      float[] afloat1 = (float[])this.call286.clone();

      for (int k = 0; k < afloat1.length; k++) {
         for (int l = 0; l < afloat.length; l++) {
            afloat1[k] += afloat[l] * this.call142[l][k];
         }
      }

      return afloat1;
   }

   public static void on23(MotionSampleStore var0, JsonArray var1) {
      for (int i = 0; i < var1.size(); i++) {
         JsonObject jsonobject = var1.get(i).getAsJsonObject();
         float[] afloat = UiAnimation(jsonobject.getAsJsonArray("input"), 11);
         float[] afloat1 = UiAnimation(jsonobject.getAsJsonArray("q_values"), 50);
         float[] afloat2 = var0.InventoryUtils(afloat);
         float f = 0.0F;

         for (int j = 0; j < afloat2.length; j++) {
            f = Math.max(f, Math.abs(afloat2[j] - afloat1[j]));
         }

         on23(f <= 0.002F, "parity case " + i + ", error=" + f);
         on23(var0.ModuleSnapshotDto(afloat) == jsonobject.get("action").getAsInt(), "parity action " + i);
      }
   }

   public static float[] UiAnimation(JsonArray var0, int var1) {
      on23(var0.size() == var1, "vector length");
      float[] afloat = new float[var1];

      for (int i = 0; i < var1; i++) {
         afloat[i] = var0.get(i).getAsFloat();
      }

      return afloat;
   }

   public static float[][] on23(JsonArray var0, int var1, int var2) {
      on23(var0.size() == var1, "matrix rows");
      float[][] afloat = new float[var1][var2];

      for (int i = 0; i < var1; i++) {
         afloat[i] = UiAnimation(var0.get(i).getAsJsonArray(), var2);
      }

      return afloat;
   }

   public static void on23(boolean var0, String var1) {
      if (!var0) {
         throw new IllegalStateException("invalid point-click artifact: " + var1);
      }
   }
}
