package org.zenith.core;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface TranslationKey {
   Map<Integer, ClickFxController> val158 = new ConcurrentHashMap<>();

   static void on23(int var0, ClickFxController var1) {
      if (var1 == null) {
         val158.remove(var0);
      } else {
         val158.put(var0, var1);
      }
   }

   static void EventWindowSizeChanged(int var0) {
      val158.remove(var0);
   }

   void zenith_simulate();

   static ClickFxController GuiWalkEvent(int var0) {
      return val158.get(var0);
   }
}
