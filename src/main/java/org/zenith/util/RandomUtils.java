package org.zenith.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtils {
   public static int CloudUserProfile(int var0, int var1) {
      if (var0 > var1) {
         throw new IllegalArgumentException("min > max");
      } else {
         return ThreadLocalRandom.current().nextInt(var0, var1 + 1);
      }
   }

   public static int ModuleSnapshotDto(int var0, int var1) {
      return CloudUserProfile(var0, var1);
   }

   public static boolean InventoryUtils(int var0, int var1) {
      int i = CloudUserProfile(var0, var1);
      return i > ThreadLocalRandom.current().nextInt(100);
   }

   public static <T> List<T> InventoryUtils(List<T> var0) {
      ArrayList arraylist = new ArrayList<>(var0);
      Collections.shuffle(arraylist, ThreadLocalRandom.current());
      return arraylist;
   }

   public static <T> void BotFeatureRegistry(List<T> var0) {
      Collections.shuffle(var0, ThreadLocalRandom.current());
   }
}
