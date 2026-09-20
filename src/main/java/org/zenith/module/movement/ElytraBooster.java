package org.zenith.module.movement;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;

@ModuleInfo(name = "ElytraBooster", category = Category.MOVEMENT, description = "Усиливает ваш фейерверк")
public final class ElytraBooster extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final ElytraBooster elytraBooster = new ElytraBooster();

   public double call095() {
      float f = 1.5F;
      ElytraTarget iiilii11iililllli11iilill1ll = ElytraTarget.elytraTarget;
      int[] aint = new int[]{-45, 45, 135, -135};
      int[] aint1 = new int[]{-90, 90, 180, -180, 0};
      int[] aint2 = new int[]{-45, 45};
      float f1 = minecraftClient3.player.lastYaw;
      float f2 = minecraftClient3.player.lastPitch;
      int i = on23(f1, aint);
      float f3 = Math.abs(MathHelper.wrapDegrees(f1) - aint[i]);
      int j = on23(f1, aint1);
      float f4 = Math.abs(MathHelper.wrapDegrees(f1) - aint1[j]);
      f = 2.06F - f3 * 0.56F / 45.0F;
      if (f4 < 10.0F) {
         f += 0.1F - 0.1F * f4 / 10.0F;
      }

      int k = on23(f2, aint2);
      float f5 = Math.abs(Math.abs(f2) - Math.abs(aint2[k]));
      if (f5 < 26.0F) {
         f = Math.max(1.94F, f);
         f += 0.05F - f5 * 0.05F / 26.0F;
      }

      f = Math.min(2.045F, f);
      if (minecraftClient3.player.lastPitch > -55.0F && minecraftClient3.player.lastPitch < -19.0F) {
         f = 1.91F;
      } else if (minecraftClient3.player.lastPitch < -55.0F) {
         f = 1.54F;
      }

      if (minecraftClient3.player.lastPitch > 19.0F && minecraftClient3.player.lastPitch < 55.0F) {
         f = 1.8F;
      } else if (minecraftClient3.player.lastPitch > 55.0F) {
         f = 1.54F;
      }

      return f;
   }

   public static int on23(float var0, int[] var1) {
      int i = 0;
      int j = -1;
      float f = Float.MAX_VALUE;

      for (int k : var1) {
         float f1 = Math.abs(MathHelper.wrapDegrees(var0) - k);
         if (f1 < f) {
            f = f1;
            j = i;
         }

         i++;
      }

      return j;
   }
}
