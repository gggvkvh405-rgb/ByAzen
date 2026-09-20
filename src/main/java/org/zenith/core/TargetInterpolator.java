package org.zenith.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationDelta;
import org.zenith.rotation.RotationMath;

public final class TargetInterpolator {
   public static final int int467 = 59;
   public static final int int468 = 0;
   public static final int int469 = 1;
   public static final int int470 = 16;
   public static final float float376 = 50.0F;
   public static final float float377 = 176.0F;
   public static final float float378 = 8.0F;
   public static final float float379 = 8.0F;
   public float float380;
   public float float381;
   public float float382;
   public float float383;
   public boolean boolean199;
   public float float384;
   public float float385;
   public float float386;
   public float float387;
   public float getThis;
   public float getThis2;
   public int int471;
   public int int472;
   public float float388;
   public float float389;
   public float float390;
   public float float391;
   public TargetSnapshot zClass101Var159;

   public float[] on23(MinecraftClient var1, Rotation var2, LivingEntity var3, float var4, float var5, float var6) {
      if (var1.player != null && var3 != null && var3.getBoundingBox() != null && this.isFinite(var6) && !(var6 <= 0.0F)) {
         Vec3d vec3d = var1.player.getEntityPos();
         Box box = var3.getBoundingBox();
         Vec3d vec3d1 = this.on23(var3, box);
         Vec3d vec3d2 = this.on23(var1, vec3d);
         float[] afloat = this.on23(var1, var2, var1.player.getEyePos(), var3, box, var4, var5, vec3d1, vec3d2, var6);
         if (afloat != null) {
            this.zClass101Var159 = new TargetSnapshot(afloat, vec3d, box, var6);
         }

         return afloat;
      } else {
         return null;
      }
   }

   public float[] on23(
      MinecraftClient var1, Rotation var2, Vec3d var3, LivingEntity var4, Box var5, float var6, float var7, Vec3d var8, Vec3d var9, float var10
   ) {
      Vec3d vec3d = this.on23(var5);
      RotationDelta liiilliiilil1l1i1111li1ii11xx = var2.EmoteManager(this.on23(var3, vec3d));
      float f = liiilliiilil1l1i1111li1ii11xx.type2();
      float f1 = liiilliiilil1l1i1111li1ii11xx.path15();
      if (this.isFinite(f) && this.isFinite(f1)) {
         Vec3d vec3d1 = this.on23(var1, var2, var3);
         Box box = this.on23(var4, var5, var8);
         RotationDelta liiilliiilil1l1i1111li1ii11x = var2.EmoteManager(this.on23(vec3d1, this.on23(box)));
         float f2 = liiilliiilil1l1i1111li1ii11x.type2();
         float f3 = liiilliiilil1l1i1111li1ii11x.path15();
         if (this.isFinite(f2) && this.isFinite(f3)) {
            float f4 = Float.MAX_VALUE;
            float f5 = -Float.MAX_VALUE;
            float f6 = Float.MAX_VALUE;
            float f7 = -Float.MAX_VALUE;

            for (Vec3d vec3d2 : this.UiAnimation(var5)) {
               RotationDelta liiilliiilil1l1i1111li1ii11xx_b = var2.EmoteManager(this.on23(var3, vec3d2));
               float f8 = liiilliiilil1l1i1111li1ii11xx_b.type2();
               float f9 = liiilliiilil1l1i1111li1ii11xx_b.path15();
               if (this.isFinite(f8) && this.isFinite(f9)) {
                  f8 = this.SimpleItemBuilder(f8, f);
                  f4 = Math.min(f4, f8);
                  f5 = Math.max(f5, f8);
                  f6 = Math.min(f6, f9);
                  f7 = Math.max(f7, f9);
               }
            }

            if (f4 == Float.MAX_VALUE) {
               f5 = f;
               f4 = f;
               f7 = f1;
               f6 = f1;
            }

            float f38 = f5 - f4;
            float f39 = f7 - f6;
            float f40 = (float)(vec3d.x - var3.x);
            float f41 = Math.abs((float)(vec3d.y - var3.y));
            float f42 = (float)(vec3d.z - var3.z);
            float f43 = (float)var3.distanceTo(vec3d);
            float f44 = this.hypot(f40, f42);
            boolean flag = f4 <= 0.0F && f5 >= 0.0F;
            boolean flag1 = f6 <= 0.0F && f7 >= 0.0F;
            float f10 = flag && Math.abs(f38) > 1.0E-6F ? MathHelper.clamp(-f4 / f38, 0.0F, 1.0F) : 0.0F;
            float f11 = flag1 && Math.abs(f39) > 1.0E-6F ? MathHelper.clamp(-f6 / f39, 0.0F, 1.0F) : 0.0F;
            boolean flag2 = flag && flag1;
            this.float384 = flag2 ? MathHelper.clamp(this.float384 + 1.0F, 0.0F, 50.0F) : 0.0F;
            this.float385 = flag2 ? 0.0F : Math.min(this.float385 + 1.0F, 8.0F);
            float f12 = var6 - this.float380;
            float f13 = var7 - this.float381;
            float f14 = f12 - this.float382;
            float f15 = f13 - this.float383;
            float f16 = this.zClass101Var159 == null ? f : this.InventoryUtils(this.zClass101Var159.call108[0], this.zClass101Var159.float142);
            float f17 = this.zClass101Var159 == null ? f1 : this.InventoryUtils(this.zClass101Var159.call108[1], this.zClass101Var159.float142);
            float f18 = this.zClass101Var159 == null ? f43 : this.zClass101Var159.call108[16];
            float f19 = MathHelper.wrapDegrees(f - f16);
            float f20 = f1 - f17;
            float f21 = f43 - f18;
            float f22 = f / Math.max(Math.abs(f38), 0.1F);
            float f23 = f1 / Math.max(Math.abs(f39), 0.1F);
            float f24 = this.hypot(f, f1);
            float f25 = (float)Math.atan2(f1, f);
            float f26 = this.ModuleSnapshotDto(var6, var10);
            float f27 = this.ModuleSnapshotDto(var7, var10);
            float f28 = this.CloudUserProfile(f, var10);
            float f29 = this.CloudUserProfile(f1, var10);
            float f30 = 0.0F;
            float f31 = 0.0F;
            float f32 = 0.0F;
            float f33 = 0.0F;
            if (this.zClass101Var159 != null) {
               float f34 = this.zClass101Var159.call108[0];
               float f35 = this.zClass101Var159.call108[1];
               float f36 = f34 - f26;
               float f37 = f35 - f27;
               f30 = Math.abs(f34) - Math.abs(f36);
               f31 = Math.abs(f35) - Math.abs(f37);
               f32 = this.MediaTrackInfo(f28 - f36, var10);
               f33 = f29 - f37;
            }

            this.getThis = f26 != 0.0F ? this.getThis + 1.0F : 0.0F;
            this.getThis2 = f27 != 0.0F ? this.getThis2 + 1.0F : 0.0F;
            int i = this.CloudResponse(f26);
            int j = this.CloudResponse(f27);
            this.float390 = this.UiAnimation(f26, i, this.int471, this.float390);
            this.float391 = this.UiAnimation(f27, j, this.int472, this.float391);
            float f45 = this.BotFeatureRegistry(f26, this.float390);
            float f46 = this.BotFeatureRegistry(f27, this.float391);
            this.float388 = this.UiAnimation(i, this.int471, this.float388);
            this.float389 = this.UiAnimation(j, this.int472, this.float389);
            if (i != 0) {
               this.int471 = i;
            }

            if (j != 0) {
               this.int472 = j;
            }

            if (flag2) {
               this.float386 = MathHelper.clamp(this.boolean199 ? this.float386 + 1.0F : 1.0F, 0.0F, 176.0F);
               this.float387 = 0.0F;
            } else {
               this.float386 = 0.0F;
               this.float387 = MathHelper.clamp(this.boolean199 ? 1.0F : this.float387 + 1.0F, 0.0F, 8.0F);
            }

            float[] afloat = new float[]{
               f28,
               f29,
               this.CloudUserProfile(f4, var10),
               this.CloudUserProfile(f5, var10),
               this.CloudUserProfile(f6, var10),
               this.CloudUserProfile(f7, var10),
               this.CloudUserProfile(f38, var10),
               this.CloudUserProfile(f39, var10),
               f22,
               f23,
               this.CloudUserProfile(f24, var10),
               (float)Math.sin(f25),
               (float)Math.cos(f25),
               f10,
               f11,
               flag2 ? 1.0F : 0.0F,
               f43,
               f44,
               f41,
               this.float384,
               this.float385,
               this.float386,
               this.float387,
               this.CloudUserProfile(f19, var10),
               this.CloudUserProfile(f20, var10),
               f21,
               f32,
               f33,
               this.CloudUserProfile(f2, var10),
               this.CloudUserProfile(f3, var10),
               (float)var8.x,
               (float)var8.y,
               (float)var8.z,
               f26,
               f27,
               f26,
               f27,
               this.ModuleSnapshotDto(f12, var10),
               this.ModuleSnapshotDto(f13, var10),
               this.ModuleSnapshotDto(f14, var10),
               this.ModuleSnapshotDto(f15, var10),
               this.getThis,
               this.getThis2,
               this.float388,
               this.float389,
               f30,
               f31,
               f45,
               f46,
               this.float390,
               this.float391,
               var1.player.input.playerInput.forward() ? 1.0F : 0.0F,
               var1.player.input.playerInput.backward() ? 1.0F : 0.0F,
               var1.player.input.playerInput.left() ? 1.0F : 0.0F,
               var1.player.input.playerInput.right() ? 1.0F : 0.0F,
               var1.player.input.playerInput.jump() ? 1.0F : 0.0F,
               (float)var9.x,
               (float)var9.y,
               (float)var9.z
            };
            if (afloat.length == 59 && this.CloudApiClient(afloat)) {
               this.float380 = var6;
               this.float381 = var7;
               this.float382 = f12;
               this.float383 = f13;
               this.boolean199 = flag2;
               return afloat;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public void reset() {
      this.float380 = 0.0F;
      this.float381 = 0.0F;
      this.float382 = 0.0F;
      this.float383 = 0.0F;
      this.boolean199 = false;
      this.float384 = 0.0F;
      this.float385 = 0.0F;
      this.float386 = 0.0F;
      this.float387 = 0.0F;
      this.getThis = 0.0F;
      this.getThis2 = 0.0F;
      this.int471 = 0;
      this.int472 = 0;
      this.float388 = 0.0F;
      this.float389 = 0.0F;
      this.float390 = 0.0F;
      this.float391 = 0.0F;
      this.zClass101Var159 = null;
   }

   public Vec3d on23(MinecraftClient var1, Rotation var2, Vec3d var3) {
      Vec3d vec3d = var1.player.getEntityPos();
      Vec3d vec3d1 = var3.subtract(vec3d);

      try {
         MovementController il11i11i111i1i1l1il = MovementController.on23(CustomInput.Easing(var1.player.input.playerInput));
         il11i11i111i1i1l1il.yaw = var2.GrimGlide();
         il11i11i111i1i1l1il.pitch = var2.GuiWalk();
         il11i11i111i1i1l1il.tick();
         il11i11i111i1i1l1il.tick();
         Vec3d vec3d2 = il11i11i111i1i1l1il.TriggerBot.add(vec3d1);
         return this.Easing(vec3d2) ? vec3d2 : var3;
      } catch (Throwable throwable) {
         return var3;
      }
   }

   public Box on23(LivingEntity var1, Box var2, Vec3d var3) {
      if (var1 instanceof PlayerEntity playerentity) {
         try {
            MovementController il11i11i111i1i1l1il = MovementController.on23(playerentity, CustomInput.ItemServiceBase(playerentity));
            il11i11i111i1i1l1il.tick();
            il11i11i111i1i1l1il.tick();
            if (il11i11i111i1i1l1il.box9 != null && this.Easing(il11i11i111i1i1l1il.TriggerBot)) {
               return il11i11i111i1i1l1il.box9;
            }
         } catch (Throwable var6) {
         }
      }

      Vec3d vec3d = var3 == null ? Vec3d.ZERO : var3.multiply(2.0);
      return this.Easing(vec3d) ? var2.offset(vec3d) : var2;
   }

   public Vec3d on23(MinecraftClient var1, Vec3d var2) {
      if (this.zClass101Var159 != null) {
         Vec3d vec3d = var2.subtract(this.zClass101Var159.vec3d32);
         if (this.Easing(vec3d) && vec3d.lengthSquared() > 1.0E-10) {
            return vec3d;
         }
      }

      Vec3d vec3d1 = var1.player.getVelocity();
      return this.Easing(vec3d1) ? vec3d1 : Vec3d.ZERO;
   }

   public Vec3d on23(LivingEntity var1, Box var2) {
      if (this.zClass101Var159 != null && this.zClass101Var159.box7 != null) {
         Vec3d vec3d = this.on23(var2).subtract(this.on23(this.zClass101Var159.box7));
         if (this.Easing(vec3d) && vec3d.lengthSquared() > 1.0E-10) {
            return vec3d;
         }
      }

      Vec3d vec3d1 = var1.getVelocity();
      return this.Easing(vec3d1) ? vec3d1 : Vec3d.ZERO;
   }

   public Rotation on23(Vec3d var1, Vec3d var2) {
      return RotationMath.Event08(var2.subtract(var1));
   }

   public Vec3d on23(Box var1) {
      return new Vec3d((var1.minX + var1.maxX) * 0.5, (var1.minY + var1.maxY) * 0.5, (var1.minZ + var1.maxZ) * 0.5);
   }

   public Vec3d[] UiAnimation(Box var1) {
      return new Vec3d[]{
         new Vec3d(var1.minX, var1.minY, var1.minZ),
         new Vec3d(var1.minX, var1.minY, var1.maxZ),
         new Vec3d(var1.minX, var1.maxY, var1.minZ),
         new Vec3d(var1.minX, var1.maxY, var1.maxZ),
         new Vec3d(var1.maxX, var1.minY, var1.minZ),
         new Vec3d(var1.maxX, var1.minY, var1.maxZ),
         new Vec3d(var1.maxX, var1.maxY, var1.minZ),
         new Vec3d(var1.maxX, var1.maxY, var1.maxZ)
      };
   }

   public float MediaTrackInfo(float var1, float var2) {
      float f = 360.0F / var2;
      float f1 = var1 % f;
      if (f1 >= f * 0.5F) {
         f1 -= f;
      } else if (f1 < -f * 0.5F) {
         f1 += f;
      }

      return f1;
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

   public float CloudUserProfile(float var1, float var2) {
      return var1 / var2;
   }

   public float ModuleSnapshotDto(float var1, float var2) {
      return Math.round(var1 / var2);
   }

   public float InventoryUtils(float var1, float var2) {
      return var1 * var2;
   }

   public float hypot(float var1, float var2) {
      return (float)Math.sqrt(var1 * var1 + var2 * var2);
   }

   public int CloudResponse(float var1) {
      return var1 > 0.0F ? 1 : (var1 < 0.0F ? -1 : 0);
   }

   public float UiAnimation(int var1, int var2, float var3) {
      return var1 != 0 && var2 != 0 && var1 != var2 ? 0.0F : var3 + 1.0F;
   }

   public float UiAnimation(float var1, int var2, int var3, float var4) {
      if (var2 == 0) {
         return 0.0F;
      } else {
         return var3 != 0 && var2 == var3 ? Math.max(var4, Math.abs(var1)) : Math.abs(var1);
      }
   }

   public float BotFeatureRegistry(float var1, float var2) {
      return var2 > 0.0F ? Math.abs(var1) / var2 : 0.0F;
   }

   public boolean CloudApiClient(float[] var1) {
      for (float f : var1) {
         if (!this.isFinite(f)) {
            return false;
         }
      }

      return true;
   }

   public boolean isFinite(float var1) {
      return Float.isFinite(var1);
   }

   public boolean Easing(Vec3d var1) {
      return var1 != null && Double.isFinite(var1.x) && Double.isFinite(var1.y) && Double.isFinite(var1.z);
   }
}
