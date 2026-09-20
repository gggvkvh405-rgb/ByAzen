package org.zenith.util;

import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.event.MovementInputEvent;

public final class MovementUtils {
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static boolean double64() {
      return minecraftClient3.player.input.getMovementInput().y != 0.0F
         || minecraftClient3.player.input.getMovementInput().x != 0.0F
         || minecraftClient3.player.input.playerInput.jump();
   }

   public static double[] on23(double var0, float var2) {
      float f = minecraftClient3.player.input.getMovementInput().y;
      float f1 = minecraftClient3.player.input.getMovementInput().x;
      if (f != 0.0F) {
         if (f1 > 0.0F) {
            var2 += f > 0.0F ? -25.0F : 45.0F;
         } else if (f1 < 0.0F) {
            var2 += f > 0.0F ? 25.0F : -25.0F;
         }

         f1 = 0.0F;
         f = f > 0.0F ? 1.0F : -1.0F;
      }

      double d0 = Math.sin(Math.toRadians(var2 + 90.0F));
      double d1 = Math.cos(Math.toRadians(var2 + 90.0F));
      double d2 = f * var0 * d1 + f1 * var0 * d0;
      double d3 = f * var0 * d0 - f1 * var0 * d1;
      return new double[]{d2, d3};
   }

   public static double[] on23(double var0, float var2, Vec3d var3, double var4) {
      float f = minecraftClient3.player.input.getMovementInput().y;
      float f1 = minecraftClient3.player.input.getMovementInput().x;
      double d0 = var3.x;
      double d1 = var3.z;
      if (var4 < 1.0E-8) {
         return new double[]{0.0, 0.0};
      }

      if (f != 0.0F) {
         if (f1 > 0.0F) {
            var2 += f > 0.0F ? -45.0F : 45.0F;
         } else if (f1 < 0.0F) {
            var2 += f > 0.0F ? 45.0F : -45.0F;
         }

         f1 = 0.0F;
         f = f > 0.0F ? 1.0F : -1.0F;
      }

      double d2 = Math.sin(Math.toRadians(var2 + 90.0F));
      double d3 = Math.cos(Math.toRadians(var2 + 90.0F));
      double d4 = f * var0 * d3 + f1 * var0 * d2;
      double d5 = f * var0 * d2 - f1 * var0 * d3;
      double d6 = d0 - d4;
      double d7 = d1 - d5;
      if (Math.signum(d0) != Math.signum(d6)) {
         double d8 = Math.abs(d0) / (Math.abs(d4) + 1.0E-8);
         d4 *= d8 * 0.9;
      }

      if (Math.signum(d1) != Math.signum(d7)) {
         double d9 = Math.abs(d1) / (Math.abs(d5) + 1.0E-8);
         d5 *= d9 * 0.9;
      }

      return new double[]{d4, d5, 1.0};
   }

   public static double[] FileLogger(double var0) {
      return on23(var0, minecraftClient3.player.getYaw());
   }

   public static void CloudApiClient(double var0) {
      double[] adouble = FileLogger(var0);
      Objects.requireNonNull(minecraftClient3.player).setVelocity(adouble[0], minecraftClient3.player.getVelocity().getY(), adouble[1]);
   }

   public static void NbtItemSpec(double var0, double var2) {
      double[] adouble = FileLogger(var0);
      Objects.requireNonNull(minecraftClient3.player).setVelocity(adouble[0], var2, adouble[1]);
   }

   public static double UiAnimation(Vec3d var0, float var1) {
      float f = (float)Math.atan2(-var0.x, var0.z);
      double d0 = Math.toRadians(MathHelper.wrapDegrees(var1));
      return Math.toDegrees(MathHelper.wrapDegrees(f - d0));
   }

   public static PlayerInput on23(PlayerInput var0, double var1, float var3) {
      boolean flag = var0.forward();
      boolean flag1 = var0.backward();
      boolean flag2 = var0.left();
      boolean flag3 = var0.right();
      if (var1 >= -90.0F + var3 && var1 <= 90.0F - var3) {
         flag = true;
      } else if (var1 < -90.0F - var3 || var1 > 90.0F + var3) {
         flag1 = true;
      }

      if (var1 >= 0.0F + var3 && var1 <= 180.0F - var3) {
         flag3 = true;
      } else if (var1 >= -180.0F + var3 && var1 <= 0.0F - var3) {
         flag2 = true;
      }

      return new PlayerInput(flag, flag1, flag2, flag3, var0.jump(), var0.sneak(), var0.sprint());
   }

   public static void on23(MovementInputEvent var0, float var1, float var2) {
      float f = minecraftClient3.player.input.getMovementInput().y;
      float f1 = minecraftClient3.player.input.getMovementInput().x;
      double d0 = MathHelper.wrapDegrees(Math.toDegrees(NbtItemSpec(minecraftClient3.player.isGliding() ? var1 : var2, f, f1)));
      if (f != 0.0F || f1 != 0.0F) {
         float f2 = 0.0F;
         float f3 = 0.0F;
         float f4 = Float.MAX_VALUE;

         for (float f5 = -1.0F; f5 <= 1.0F; f5++) {
            for (float f6 = -1.0F; f6 <= 1.0F; f6++) {
               if (f6 != 0.0F || f5 != 0.0F) {
                  double d1 = MathHelper.wrapDegrees(Math.toDegrees(NbtItemSpec(var1, f5, f6)));
                  double d2 = Math.abs(d0 - d1);
                  if (d2 < f4) {
                     f4 = (float)d2;
                     f2 = f5;
                     f3 = f6;
                  }
               }
            }
         }

         var0.on23(f2, f3);
      }
   }

   public static double NbtItemSpec(float var0, float var1, float var2) {
      if (var1 < 0.0F) {
         var0 += 180.0F;
      }

      float f = 1.0F;
      if (var1 < 0.0F) {
         f = -0.5F;
      }

      if (var1 > 0.0F) {
         f = 0.5F;
      }

      if (var2 > 0.0F) {
         var0 -= 90.0F * f;
      }

      if (var2 < 0.0F) {
         var0 += 90.0F * f;
      }

      return Math.toRadians(var0);
   }

   public static PlayerInput on23(PlayerInput var0, double var1) {
      return on23(var0, var1, 20.0F);
   }

   public static double ConfigJsonUtil(Entity var0) {
      double d0 = var0.getX() - var0.lastX;
      double d1 = var0.getY() - var0.lastY;
      double d2 = var0.getZ() - var0.lastZ;
      return Math.sqrt(d0 * d0 + d2 * d2 + d1 * d1);
   }

   public MovementUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
