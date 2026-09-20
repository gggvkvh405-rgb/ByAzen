package org.zenith.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class PermissionListsStore {
   public static final int int466 = 6;
   public static final String string134 = "/assets/zenith/aimpipe/aim_motion_library.json";
   public static final Path path14 = Path.of("aim_motion_library.json");
   public static final Path[] call221 = new Path[]{
      Path.of("..", "scripts", "out", "aim_motion_library_6t.json"),
      Path.of("scripts", "out", "aim_motion_library_6t.json"),
      Path.of("..", "scripts", "test", "aimpipe", "out", "aim_motion_library.json"),
      Path.of("scripts", "test", "aimpipe", "out", "aim_motion_library.json")
   };
   public final List<NumericMatrixCell> list113;

   public PermissionListsStore(List<NumericMatrixCell> var1) {
      this.list113 = List.copyOf(var1);
   }

   public List<NumericMatrixCell> list52() {
      return this.list113;
   }

   public static PermissionListsStore list53() {
      PermissionListsStore ll1iil11ii = TextScanner(path14);
      if (ll1iil11ii != null) {
         return ll1iil11ii;
      }

      for (Path path : call221) {
         ll1iil11ii = TextScanner(path);
         if (ll1iil11ii != null) {
            return ll1iil11ii;
         }
      }

      return string51();
   }

   public static PermissionListsStore TextScanner(Path var0) {
      try {
         if (!Files.isRegularFile(var0)) {
            return null;
         }

         String s = Files.readString(var0, StandardCharsets.UTF_8);
         return NbtEditor(s, var0.toAbsolutePath().toString());
      } catch (Throwable throwable) {
         System.err.println("[AimMotionLibrary] failed to load " + var0 + ": " + throwable);
         return null;
      }
   }

   public static PermissionListsStore string51() {
      try (InputStream inputstream = PermissionListsStore.class.getResourceAsStream("/assets/zenith/aimpipe/aim_motion_library.json")) {
         if (inputstream == null) {
            return null;
         }

         String s = new String(inputstream.readAllBytes(), StandardCharsets.UTF_8);
         return NbtEditor(s, "/assets/zenith/aimpipe/aim_motion_library.json");
      } catch (Throwable throwable1) {
         System.err.println("[AimMotionLibrary] failed to load /assets/zenith/aimpipe/aim_motion_library.json: " + throwable1);
         return null;
      }
   }

   public static PermissionListsStore NbtEditor(String var0, String var1) {
      JsonObject jsonobject = JsonParser.parseString(var0).getAsJsonObject();
      int i = jsonobject.get("format_version").getAsInt();
      if (i != 1) {
         throw new IllegalStateException("unsupported motion-library format_version " + i);
      }

      JsonElement jsonelement = jsonobject.get("source_gcd");
      if (jsonelement != null && !jsonelement.isJsonNull()) {
         float f = jsonelement.getAsFloat();
         if (Float.isFinite(f) && !(f <= 0.0F)) {
            JsonArray jsonarray = jsonobject.getAsJsonArray("primitives");
            ArrayList arraylist = new ArrayList(jsonarray.size());

            for (JsonElement jsonelement1 : jsonarray) {
               JsonObject jsonobject1 = jsonelement1.getAsJsonObject();
               int j = jsonobject1.get("length").getAsInt();
               if (j >= 1 && j <= 6) {
                  JsonArray jsonarray1 = jsonobject1.getAsJsonArray("commands");
                  if (jsonarray1.size() != j) {
                     throw new IllegalStateException("primitive length does not match command count");
                  }

                  int[] aint = new int[j];
                  int[] aint1 = new int[j];
                  int k = 0;
                  int l = 0;

                  for (int i1 = 0; i1 < j; i1++) {
                     JsonArray jsonarray2 = jsonarray1.get(i1).getAsJsonArray();
                     if (jsonarray2.size() != 2) {
                        throw new IllegalStateException("primitive command must contain yaw and pitch");
                     }

                     aint[i1] = jsonarray2.get(0).getAsInt();
                     aint1[i1] = jsonarray2.get(1).getAsInt();
                     if (aint[i1] == 0 && aint1[i1] == 0) {
                        throw new IllegalStateException("primitive contains an internal joint-zero command");
                     }

                     k += aint[i1];
                     l += aint1[i1];
                  }

                  int j1 = jsonobject1.get("sum_yaw").getAsInt();
                  int k1 = jsonobject1.get("sum_pitch").getAsInt();
                  if (k == j1 && l == k1) {
                     arraylist.add(new NumericMatrixCell(aint, aint1, j1, k1));
                     continue;
                  }

                  throw new IllegalStateException("primitive sum does not match commands");
               }

               throw new IllegalStateException("primitive length outside 1..6: " + j);
            }

            if (arraylist.isEmpty()) {
               throw new IllegalStateException("motion library contains no primitives");
            }

            if (jsonobject.has("count") && jsonobject.get("count").getAsInt() != arraylist.size()) {
               throw new IllegalStateException("motion-library count does not match primitive array");
            }

            PermissionListsStore ll1iil11ii = new PermissionListsStore(arraylist);
            System.out.println("[AimMotionLibrary] loaded " + var1 + " (primitives=" + arraylist.size() + ", maxTicks=6)");
            return ll1iil11ii;
         } else {
            throw new IllegalStateException("motion-library source_gcd must be finite and positive");
         }
      } else {
         throw new IllegalStateException("motion library must declare one source_gcd");
      }
   }
}
