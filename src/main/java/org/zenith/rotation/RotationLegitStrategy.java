package org.zenith.rotation;

import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public final class RotationLegitStrategy extends RotationStrategyBase {
   public static final float float309 = 1.0E-4F;
   public static final float float310 = 0.001F;
   public static final float float311 = 1.75F;
   public static final float float312 = 1.25F;
   public static final float float313 = 0.4F;
   public static final float float314 = 0.75F;
   public static final float float315 = 2.5F;
   public static final float float316 = 1.75F;
   public Rotation var11814;
   public float float317;
   public float float318;
   public int int423 = Integer.MIN_VALUE;
   public int int424 = Integer.MIN_VALUE;

   public RotationLegitStrategy.LegitRotation on23(Rotation var1, Vec3d var2, Box var3, float var4, boolean var5) {
      RotationDelta liiilliiilil1l1i1111li1ii11 = this.on23(var1, var2, var3);
      if (liiilliiilil1l1i1111li1ii11 == null) {
         return new RotationLegitStrategy.LegitRotation(var1, false, false, false);
      }

      float f = liiilliiilil1l1i1111li1ii11.type2() * 1.1F;
      float f1 = liiilliiilil1l1i1111li1ii11.path15();
      boolean flag = Math.abs(f) <= 1.0E-4F && Math.abs(f1) <= 1.0E-4F;
      float f2 = Float.isFinite(var4) ? Math.max(0.0F, var4) : 0.0F;
      boolean flag1 = Math.abs(f) <= f2 && Math.abs(f1) <= f2;
      if (flag || !flag1 && !var5) {
         return new RotationLegitStrategy.LegitRotation(var1, true, flag, flag1);
      }

      Rotation ililiiili1ll1li11 = var1.Event08(this.ProfileItemBuilder(f, f2), this.ProfileItemBuilder(f1, f2));
      return new RotationLegitStrategy.LegitRotation(ililiiili1ll1li11, true, false, flag1);
   }

   public RotationLegitStrategy.LegitRotation on23(Rotation var1, Rotation var2, Vec3d var3, Box var4, float var5, int var6, int var7) {
      RotationDelta liiilliiilil1l1i1111li1ii11xx = this.on23(var1, var3, var4);
      if (liiilliiilil1l1i1111li1ii11xx == null) {
         this.botWorld3();
         return new RotationLegitStrategy.LegitRotation(var1, false, false, false);
      }

      float f = Float.isFinite(var5) ? Math.max(0.0F, var5) : 0.0F;
      float f1 = this.ProfileItemBuilder(liiilliiilil1l1i1111li1ii11xx.type2() * 1.1F, f);
      float f2 = this.ProfileItemBuilder(liiilliiilil1l1i1111li1ii11xx.path15(), f);
      boolean flag = Math.abs(liiilliiilil1l1i1111li1ii11xx.type2() * 1.1F) <= f && Math.abs(liiilliiilil1l1i1111li1ii11xx.path15()) <= f;
      boolean flag1 = this.int423 == var6 && (this.int424 == var7 || this.int424 == var7 - 1) && this.var11814 != null;
      if (!flag1) {
         this.UiAnimation(var1, var2, f);
      }

      RotationDelta liiilliiilil1l1i1111li1ii11x = this.var11814.EmoteManager(var1);
      this.float317 = this.on23(this.float317, f1, liiilliiilil1l1i1111li1ii11x.type2(), false);
      this.float318 = 0.0F;
      this.float317 = this.ProfileItemBuilder(this.float317, f);
      this.float318 = this.ProfileItemBuilder(this.float318, f);
      Rotation ililiiili1ll1li11 = var1.Event08(this.float317, this.float318);
      RotationDelta liiilliiilil1l1i1111li1ii11xx_b = this.on23(ililiiili1ll1li11, var3, var4);
      boolean flag2 = liiilliiilil1l1i1111li1ii11xx_b != null
         && Math.abs(liiilliiilil1l1i1111li1ii11xx_b.type2()) <= 1.0E-4F
         && Math.abs(liiilliiilil1l1i1111li1ii11xx_b.path15()) <= 1.0E-4F;
      this.var11814 = var1;
      this.int423 = var6;
      this.int424 = var7;
      return new RotationLegitStrategy.LegitRotation(ililiiili1ll1li11, true, flag2, flag);
   }

   public void botWorld3() {
      this.var11814 = null;
      this.float317 = 0.0F;
      this.float318 = 0.0F;
      this.int423 = Integer.MIN_VALUE;
      this.int424 = Integer.MIN_VALUE;
   }

   public void UiAnimation(Rotation var1, Rotation var2, float var3) {
      RotationDelta liiilliiilil1l1i1111li1ii11 = var1.EmoteManager(var2);
      if (Float.isFinite(liiilliiilil1l1i1111li1ii11.type2())
         && Float.isFinite(liiilliiilil1l1i1111li1ii11.path15())
         && Math.abs(liiilliiilil1l1i1111li1ii11.type2()) <= var3
         && Math.abs(liiilliiilil1l1i1111li1ii11.path15()) <= var3) {
         this.float317 = liiilliiilil1l1i1111li1ii11.type2();
         this.float318 = liiilliiilil1l1i1111li1ii11.path15();
      } else {
         this.float317 = 0.0F;
         this.float318 = 0.0F;
      }

      this.var11814 = var1;
   }

   public float on23(float var1, float var2, float var3, boolean var4) {
      if (Math.abs(var3) <= 0.001F) {
         return var1;
      }

      float f = var2 - var1;
      if (Math.abs(f) <= 1.0E-4F) {
         return var2;
      }

      boolean flag = Math.signum(var3) == Math.signum(f);
      float f1 = flag ? (var4 ? 1.25F : 1.75F) : (var4 ? 0.75F : 0.4F);
      float f2 = 100.0F;
      float f3 = MathHelper.clamp(var3 * (f1 - 1.0F), -f2, f2);
      return this.TextScanner(var1, var1 + f3, var2);
   }

   public float TextScanner(float var1, float var2, float var3) {
      boolean flag = (var3 - var1) * (var3 - var2) < 0.0F;
      return flag ? var3 : var2;
   }

   public RotationDelta on23(Rotation var1, Vec3d var2, Box var3) {
      Vec3d vec3d = var3.getCenter();
      return var1.EmoteManager(this.on23(var2, vec3d));
   }

   public Rotation on23(Vec3d var1, Vec3d var2) {
      return RotationMath.Event08(var2.subtract(var1));
   }

   public float PotionItemBuilder(float var1, float var2) {
      return var1 > 0.0F ? var1 : Math.min(var2, 0.0F);
   }

   public float ProfileItemBuilder(float var1, float var2) {
      return MathHelper.clamp(var1, -var2, var2);
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


   public record LegitRotation(Rotation var1186, boolean boolean105, boolean boolean106, boolean boolean107) {
      public Rotation rotation() {
         return this.var1186;
      }

      public boolean botPlayer3() {
         return this.boolean105;
      }

      public boolean botClient5() {
         return this.boolean106;
      }

      public boolean packet() {
         return this.boolean107;
      }
   }
}
