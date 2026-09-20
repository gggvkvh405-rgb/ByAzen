package org.zenith.util;

public final class TimerSpeed {
   private static volatile float val481 = 1.0F;

   public static float float136() {
      return val481;
   }

   public static void BotWorldJoinEvent(float var0) {
      if (Float.isFinite(var0) && var0 > 0.0F) {
         val481 = var0;
      }
   }
}
