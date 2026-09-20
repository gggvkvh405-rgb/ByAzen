package org.zenith.utility.render.display.base;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.Vector2f;

public final class RenderMathUtils {
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static boolean UiAnimation(double var0, double var2, double var4, double var6, int var8, int var9) {
      return var8 >= var0 && var8 < var0 + var4 && var9 >= var2 && var9 < var2 + var6;
   }

   public static boolean on23(double var0, double var2, double var4, double var6, HudDrawContext var8) {
      return UiAnimation(var0, var2, var4, var6, var8.getMouseX(), var8.getMouseY());
   }

   public static boolean on23(double var0, double var2, double var4, double var6, double var8, double var10) {
      return var8 >= var0 && var8 < var0 + var4 && var10 >= var2 && var10 < var2 + var6;
   }

   public static Vector2f CloudResponse(double var0) {
      return new Vector2f((float)(minecraftClient3.mouse.getX() / var0), (float)(minecraftClient3.mouse.getY() / var0));
   }

   public RenderMathUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
