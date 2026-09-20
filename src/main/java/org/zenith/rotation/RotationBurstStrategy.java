package org.zenith.rotation;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationBurstStrategy extends RotationStrategyBase {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public int int452 = 1;
   public int int453 = 4;
   public int int454 = 6;
   public int int455 = 16;
   public int int456 = 25;
   public float float364 = 0.12F;
   public int int457 = 1;
   public int int458 = 4;
   public float float365 = 0.18F;
   public float float366 = 0.22F;
   public float float367 = 1.12F;
   public float float368 = 0.42F;
   float[] values = new float[]{0.0123967F, 0.053719F, 0.109504F, 0.17562F, 0.21281F, 0.272727F, 0.11157F, 0.0392562F, 0.0103306F, 0.00206612F};
   int index = 0;
   public final Random random10 = new Random();
   public RotationBurstStrategy.Burst zClass088Var143;
   public int int459 = Integer.MIN_VALUE;
   public int int460 = 0;
   public int int461 = 1;
   public int int462 = 0;
   public boolean boolean189 = true;

   public int[] on23(int var1, float[] var2) {
      int i = var2.length;
      int[] aint = new int[i];
      int j = Integer.compare(var1, 0);
      int k = Math.abs(var1);
      if (k == 0) {
         return aint;
      }

      float f = 0.0F;
      int l = 0;

      for (int i1 = 0; i1 < i; i1++) {
         f += var2[i1];
         int j1 = i1 == i - 1 ? k : Math.round(f * k);
         aint[i1] = j1 - l;
         l = j1;
      }

      if (j < 0) {
         for (int k1 = 0; k1 < i; k1++) {
            aint[k1] = -aint[k1];
         }
      }

      this.on23(aint);
      return aint;
   }

   public void on23(int[] var1) {
      int i = Integer.compare(this.sum(var1), 0);
      if (i != 0 && var1.length >= 3) {
         for (int j = 0; j < 2; j++) {
            for (int k = 1; k < var1.length - 1; k++) {
               int l = Math.abs(var1[k]);
               int i1 = Math.abs(var1[k - 1]);
               int j1 = Math.abs(var1[k + 1]);
               int k1 = Math.max(i1, j1);
               if (l > k1 + 4 && l > 2) {
                  int l1 = Math.min((l - k1) / 2, l - 1);
                  int i2 = i1 <= j1 ? k - 1 : k + 1;
                  var1[k] -= i * l1;
                  var1[i2] += i * l1;
               }
            }
         }
      }
   }

   public void on23(int[] var1, int[] var2) {
      for (int i = 0; i < var1.length; i++) {
         if (var1[i] == 0 && var2[i] == 0 && !this.on23(var1, i) && !this.on23(var2, i)) {
            this.on23(Math.abs(this.sum(var1)) >= Math.abs(this.sum(var2)) ? var1 : var2, i);
         }
      }
   }

   public void UiAnimation(int[] var1) {
      if (Math.abs(this.sum(var1)) >= var1.length) {
         for (int i = 0; i < var1.length; i++) {
            if (var1[i] == 0) {
               this.on23(var1, i);
            }
         }
      }
   }

   public boolean on23(int[] var1, int var2) {
      int i = -1;
      int j = 0;

      for (int k = 0; k < var1.length; k++) {
         int l = Math.abs(var1[k]);
         if (k != var2 && l > j && l > 1) {
            i = k;
            j = l;
         }
      }

      if (i < 0) {
         return false;
      }

      int i1 = var1[i] > 0 ? 1 : -1;
      var1[i] -= i1;
      var1[var2] += i1;
      return true;
   }

   public int sum(int[] var1) {
      int i = 0;

      for (int j : var1) {
         i += j;
      }

      return i;
   }

   public float[] on23(int var1, float var2) {
      float[] afloat = new float[var1];
      float f = MathHelper.clamp(0.46F + var2 + (this.random10.nextFloat() - 0.5F) * this.float366, 0.28F, 0.7F);
      float f1 = 0.18F + this.random10.nextFloat() * 0.12F;
      float f2 = (this.random10.nextFloat() * 2.0F - 1.0F) * this.float365;
      float f3 = 2.2F + this.random10.nextFloat() * 0.6F;
      float f4 = 2.1F + this.random10.nextFloat() * 0.7F;
      float f5 = 0.0F;

      for (int i = 0; i < var1; i++) {
         float f6 = (i + 0.5F) / var1;
         float f7 = (f6 - f) / f1;
         float f8 = (float)Math.exp(-0.5F * f7 * f7);
         float f9 = 30.0F * f6 * f6 * (1.0F - f6) * (1.0F - f6);
         float f10 = MathHelper.clamp(f6 * f3, 0.0F, 1.0F);
         float f11 = MathHelper.clamp((1.0F - f6) * f4, 0.0F, 1.0F);
         f2 = f2 * 0.72F + (this.random10.nextFloat() * 2.0F - 1.0F) * this.float365 * 0.28F;
         float f12 = MathHelper.clamp(1.0F + f2, 0.58F, 1.48F);
         float f13 = Math.max(0.001F, (f8 * 0.62F + f9 * 0.38F) * f10 * f11 * f12);
         afloat[i] = f13;
         f5 += f13;
      }

      if (!(f5 <= 0.0F) && this.isFinite(f5)) {
         for (int j = 0; j < var1; j++) {
            afloat[j] /= f5;
         }

         return afloat;
      } else {
         Arrays.fill(afloat, 1.0F / Math.max(1, var1));
         return afloat;
      }
   }

   public Rotation Easing(Rotation var1) {
      return var1;
   }

   public Rotation on23(Rotation var1, Rotation var2) {
      if (this.values == null) {
         this.values = new float[]{0.0123967F, 0.053719F, 0.109504F, 0.17562F, 0.21281F, 0.272727F, 0.11157F, 0.0392562F, 0.0103306F, 0.00206612F};
      }

      float f = this.values[this.index] * 2.0F;
      this.index++;
      if (this.index >= this.values.length) {
         this.index = 0;
      }

      RotationDelta liiilliiilil1l1i1111li1ii11 = new RotationDelta(
         MathHelper.wrapDegrees(var2.GrimGlide() - var1.GrimGlide()) * f, (var2.GuiWalk() - var1.GuiWalk()) * f
      );
      return var1.on23(liiilliiilil1l1i1111li1ii11);
   }

   public void var1532() {
      if (!this.boolean189 && this.int460 > 0) {
         this.int460++;
      } else {
         this.int460 = 1;
         this.int461 = this.byteMethod();
      }
   }

   public Rotation UiAnimation(Rotation var1, Rotation var2) {
      if (this.zClass088Var143 != null && !this.zClass088Var143.isDone()) {
         RotationBurstStrategy.Limiter lilili1l1iil_illi1l1l1 = this.zClass088Var143.botClient6();
         RotationDelta liiilliiilil1l1i1111li1ii11 = var1.EmoteManager(var2);
         int i = this.on23(lilili1l1iil_illi1l1l1.call273, liiilliiilil1l1i1111li1ii11.type2(), this.zClass088Var143.float125);
         int j = this.on23(lilili1l1iil_illi1l1l1.call213, liiilliiilil1l1i1111li1ii11.path15(), this.zClass088Var143.float125);
         Rotation ililiiili1ll1li11 = var1.Event08(i * this.zClass088Var143.float125, j * this.zClass088Var143.float125);
         if (this.zClass088Var143.isDone()) {
            this.botClient4();
         }

         return ililiiili1ll1li11;
      } else {
         this.botClient4();
         return var1;
      }
   }

   public int on23(int var1, float var2, float var3) {
      if (var1 != 0 && this.isFinite(var2) && this.isFinite(var3) && !(var3 <= 0.0F)) {
         boolean flag = Math.signum(var1) == Math.signum(var2);
         float f = flag ? 1.8F : 0.3F;
         int i = Math.round(var1 * f);
         if (i == 0) {
            i = var1 > 0 ? 1 : -1;
         }

         int j = Math.round(var2 / var3);
         return this.ItemRegistry(i, j);
      } else {
         return 0;
      }
   }

   public int ItemRegistry(int var1, int var2) {
      if (var1 != 0 && var2 != 0) {
         if (Integer.signum(var1) != Integer.signum(var2)) {
            return Math.abs(var1) <= 1 ? 0 : var1;
         } else {
            return Math.abs(var1) > Math.abs(var2) ? var2 : var1;
         }
      } else {
         return 0;
      }
   }

   public RotationBurstStrategy.Burst on23(Rotation var1, Rotation var2, float var3) {
      RotationDelta liiilliiilil1l1i1111li1ii11 = var1.EmoteManager(var2);
      int i = Math.round(liiilliiilil1l1i1111li1ii11.type2() / var3);
      int j = Math.round(liiilliiilil1l1i1111li1ii11.path15() / var3);
      int k = Math.abs(i) + Math.abs(j);
      if (k <= 0) {
         return null;
      }

      int l = this.botClient();
      int i1 = Math.max(1, Math.min(l, k));
      float f = (this.random10.nextFloat() * 2.0F - 1.0F) * 0.1F;
      int[] aint = this.on23(i, this.on23(i1, f));
      int[] aint1 = this.on23(j, this.on23(i1, -f * 0.75F));
      this.UiAnimation(aint);
      this.UiAnimation(aint1);
      this.on23(aint, aint1);
      return new RotationBurstStrategy.Burst(aint, aint1, var3);
   }

   public int byteMethod() {
      this.int453 = 2;
      int i = Math.max(0, Math.min(this.int452, this.int453));
      int j = Math.max(i, Math.max(this.int452, this.int453));
      return i + this.random10.nextInt(j - i + 1);
   }

   public int botClient() {
      int i = Math.max(1, Math.min(this.int454, this.int455));
      int j = Math.max(i, Math.max(this.int454, this.int455));
      if (this.random10.nextFloat() < this.float364) {
         j = Math.max(j, this.int456);
      }

      return i + this.random10.nextInt(j - i + 1);
   }

   public int text() {
      int i = Math.max(0, Math.min(this.int457, this.int458));
      int j = Math.max(i, Math.max(this.int457, this.int458));
      return i + this.random10.nextInt(j - i + 1);
   }

   public void botClient4() {
      this.zClass088Var143 = null;
      this.int460 = 0;
      this.int462 = this.text();
      this.int461 = this.byteMethod();
   }

   public RotationBurstStrategy.Smoother UiAnimation(Rotation var1, LivingEntity var2) {
      if (var1 != null && var2 != null && minecraftClient3.player != null) {
         Box box = var2.getBoundingBox();
         Vec3d vec3d = minecraftClient3.player.getEyePos();
         Vec3d vec3d1 = box.getCenter();
         Rotation ililiiili1ll1li11 = RotationMath.Event08(vec3d1.subtract(vec3d));
         RotationDelta liiilliiilil1l1i1111li1ii11x = var1.EmoteManager(ililiiili1ll1li11);
         float f = liiilliiilil1l1i1111li1ii11x.type2();
         float f1 = liiilliiilil1l1i1111li1ii11x.path15();
         if (this.isFinite(f) && this.isFinite(f1)) {
            float f2 = Float.MAX_VALUE;
            float f3 = -Float.MAX_VALUE;
            float f4 = Float.MAX_VALUE;
            float f5 = -Float.MAX_VALUE;
            Vec3d[] avec3d = new Vec3d[]{
               new Vec3d(box.minX, box.minY, box.minZ),
               new Vec3d(box.minX, box.minY, box.maxZ),
               new Vec3d(box.minX, box.maxY, box.minZ),
               new Vec3d(box.minX, box.maxY, box.maxZ),
               new Vec3d(box.maxX, box.minY, box.minZ),
               new Vec3d(box.maxX, box.minY, box.maxZ),
               new Vec3d(box.maxX, box.maxY, box.minZ),
               new Vec3d(box.maxX, box.maxY, box.maxZ)
            };

            for (Vec3d vec3d2 : avec3d) {
               Rotation ililiiili1ll1li111 = RotationMath.Event08(vec3d2.subtract(vec3d));
               RotationDelta liiilliiilil1l1i1111li1ii11x_b = var1.EmoteManager(ililiiili1ll1li111);
               float f6 = this.SimpleItemBuilder(liiilliiilil1l1i1111li1ii11x_b.type2(), f);
               float f7 = liiilliiilil1l1i1111li1ii11x_b.path15();
               if (this.isFinite(f6) && this.isFinite(f7)) {
                  f2 = Math.min(f2, f6);
                  f3 = Math.max(f3, f6);
                  f4 = Math.min(f4, f7);
                  f5 = Math.max(f5, f7);
               }
            }

            if (f2 == Float.MAX_VALUE) {
               f2 = f;
               f3 = f;
               f4 = f1;
               f5 = f1;
            }

            boolean flag = f2 <= 0.0F && f3 >= 0.0F;
            boolean flag1 = f4 <= 0.0F && f5 >= 0.0F;
            return new RotationBurstStrategy.Smoother(flag, flag1);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public float SimpleItemBuilder(float var1, float var2) {
      float f = var1;

      while (f - var2 > 180.0F) {
         f -= 360.0F;
      }

      while (f - var2 < -180.0F) {
         f += 360.0F;
      }

      return f;
   }

   public void text2() {
      this.zClass088Var143 = null;
      this.int460 = 0;
      this.int462 = 0;
      this.int461 = this.byteMethod();
      this.boolean189 = true;
      this.int459 = Integer.MIN_VALUE;
   }

   public boolean isFinite(float var1) {
      return !Float.isNaN(var1) && !Float.isInfinite(var1);
   }


   public static final class Burst {
      public final int[] val321;
      public final int[] val431;
      public final float float125;
      public int index;

      Burst(int[] var1, int[] var2, float var3) {
         this.val321 = var1;
         this.val431 = var2;
         this.float125 = var3;
      }

      Limiter botClient6() {
         Limiter lilili1l1iil_illi1l1l1 = new Limiter(this.val321[this.index], this.val431[this.index]);
         this.index++;
         return lilili1l1iil_illi1l1l1;
      }

      boolean isDone() {
         return this.index >= this.val321.length;
      }
   }

   public static final class Smoother {
      final boolean call036;

      Smoother(boolean var1, boolean var2) {
         this.call036 = var1 && var2;
      }
   }

   public static final class Limiter {
      final int call273;
      final int call213;

      Limiter(int var1, int var2) {
         this.call273 = var1;
         this.call213 = var2;
      }
   }
}
