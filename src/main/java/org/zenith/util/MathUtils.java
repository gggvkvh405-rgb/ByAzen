package org.zenith.util;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import org.zenith.core.ClientProvider;

public final class MathUtils implements ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static double double157;
   public static final int int463 = 65536;
   public static final double double158 = Math.PI * 2;
   public static final double[] val483 = buildSinTable();

   private static double[] buildSinTable() {
      double[] adouble = new double[65536];

      for (int i = 0; i < 65536; i++) {
         adouble[i] = Math.sin(i * (Math.PI * 2) / 65536.0);
      }

      return adouble;
   }

   public static double sin(double var0) {
      int i = (int)(var0 * 10430.378350470453) & 65535;
      return val483[i];
   }

   public static double cos(double var0) {
      int i = (int)(var0 * 10430.378350470453 + 16384.0) & 65535;
      return val483[i];
   }

   public static float ItemServiceBase(float var0, float var1, float var2) {
      float f = Math.min(var2 / var1, 1.0F);
      return var0 * (1.0F + f * f * 0.5F);
   }

   public static float EnchantItemSpec(double var0, double var2) {
      return (float)(var0 + (var2 - var0) * Math.random());
   }

   public static double Easing(double var0, double var2, double var4, double var6, double var8) {
      return Math.pow(1.0 - var0, 3.0) * var2
         + 3.0 * var0 * Math.pow(1.0 - var0, 2.0) * var4
         + 3.0 * Math.pow(var0, 2.0) * (1.0 - var0) * var6
         + Math.pow(var0, 3.0) * var8;
   }

   public static int EmoteManager(String var0, String var1) {
      int i = var0.length();
      int j = var1.length();
      int[] aint = new int[j + 1];
      int k = 0;

      while (k <= j) {
         aint[k] = k++;
      }

      for (int l1 = 1; l1 <= i; l1++) {
         int l = aint[0];
         aint[0] = l1;

         for (int i1 = 1; i1 <= j; i1++) {
            int j1 = aint[i1];
            int k1 = var0.charAt(l1 - 1) == var1.charAt(i1 - 1) ? 0 : 1;
            aint[i1] = Math.min(Math.min(aint[i1] + 1, aint[i1 - 1] + 1), l + k1);
            l = j1;
         }
      }

      return aint[j];
   }

   public static float CancellableEvent(float var0, float var1) {
      float f = (var0 - var1) % 360.0F;
      if (f < -180.0F) {
         f += 360.0F;
      } else if (f > 180.0F) {
         f -= 360.0F;
      }

      return f;
   }

   public static boolean on23(double var0, double var2, double var4, double var6, double var8, double var10) {
      return var0 >= var4 && var0 <= var4 + var8 && var2 >= var6 && var2 <= var6 + var10;
   }

   public static boolean on23(double var0, double var2, int var4, int var5, int var6, int var7) {
      return var0 >= var4 && var0 <= var6 && var2 >= var5 && var2 <= var7;
   }

   public static float SimpleItemBuilder(double var0, double var2, double var4) {
      return (float)(var0 + (var2 - var0) * var4);
   }

   public static float BotDisconnectEvent(float var0, float var1) {
      return Math.max(var0, var1) - Math.min(var0, var1);
   }

   public static double SimpleItemBuilder(double var0, double var2) {
      if (var0 == var2) {
         return var0;
      }

      if (var0 > var2) {
         double d0 = var0;
         var0 = var2;
         var2 = d0;
      }

      return ThreadLocalRandom.current().nextDouble() * (var2 - var0) + var0;
   }

   public static double round(double var0) {
      return (float)Math.round(var0 * 10.0) / 10.0F;
   }

   public static float round(float var0) {
      return Math.round(var0 * 10.0F) / 10.0F;
   }

   public static double ItemServiceBase(double var0, double var2) {
      double d0 = Math.round(var0 / var2) * var2;
      return Math.round(d0 * 100.0) / 100.0;
   }

   public static Vec3d on23(float var0, float var1, double var2) {
      float f = Math.min(var0, var1);
      float f1 = (float)(Math.cos(f * double157 / var1) * var2);
      float f2 = (float)(-Math.sin(f * double157 / var1) * var2);
      return new Vec3d(f1, 0.0, f2);
   }

   public static Vector3d on23(Vector3d var0, Vector3d var1) {
      return new Vector3d(NbtEditor(var0.x, var1.x), NbtEditor(var0.y, var1.y), NbtEditor(var0.z, var1.z));
   }

   public static Vec3d NbtEditor(Vec3d var0, Vec3d var1) {
      return new Vec3d(
         NbtEditor(var0.x, var1.x), NbtEditor(var0.y, var1.y), NbtEditor(var0.z, var1.z)
      );
   }

   public static Vec3d CloudResponse(Entity var0) {
      return var0 == null
         ? Vec3d.ZERO
         : new Vec3d(
            NbtEditor(var0.lastX, var0.getX()), NbtEditor(var0.lastY, var0.getY()), NbtEditor(var0.lastZ, var0.getZ())
         );
   }

   public static float BotWorldJoinEvent(float var0, float var1) {
      return MathHelper.lerp(minecraftClient3.getRenderTickCounter().getTickProgress(false), var0, var1);
   }

   public static double NbtEditor(double var0, double var2) {
      return MathHelper.lerp(minecraftClient3.getRenderTickCounter().getTickProgress(false), var0, var2);
   }

   public static int on23(double var0, int var2, int var3) {
      return (int)MathHelper.lerp(minecraftClient3.getRenderTickCounter().getFixedDeltaTicks() / var0, var2, var3);
   }

   public static float on23(double var0, float var2, float var3) {
      return (float)MathHelper.lerp(minecraftClient3.getRenderTickCounter().getFixedDeltaTicks() / var0, var2, var3);
   }

   public static double ItemServiceBase(double var0, double var2, double var4) {
      return MathHelper.lerp(minecraftClient3.getRenderTickCounter().getFixedDeltaTicks() / var0, var2, var4);
   }

   public static double PotionItemBuilder(Vec3d var0, Vec3d var1) {
      double d0 = var0.getX() - var1.getX();
      double d1 = var0.getY() - var1.getY();
      double d2 = var0.getZ() - var1.getZ();
      return MathHelper.sqrt((float)(d0 * d0 + d1 * d1 + d2 * d2));
   }

   public static double InventoryUtils(double var0) {
      return Math.abs(1.0 + Math.sin(var0)) / 2.0;
   }

   public static float EventClick(float var0) {
      return (float)Math.sin(var0 * Math.PI / 2.0);
   }

   public MathUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
