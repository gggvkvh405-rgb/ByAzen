package org.zenith.rotation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.core.CustomInput;
import org.zenith.core.InventoryCodec;
import org.zenith.core.MovementController;
import org.zenith.core.PermissionListCodec;
import org.zenith.managers.MotorIntentModel;
import org.zenith.managers.MotorIntentModel;
import org.zenith.module.combat.Aura;

public final class MotorIntentRotationStrategy extends RotationStrategyBase {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int int464 = Integer.MIN_VALUE;
   public static final float float369 = 0.21217355F;
   public final MotorIntentModel zClass026 = botWorld2();
   public final RotationSmoothStrategy zClass029 = new RotationSmoothStrategy();
   public int int403 = Integer.MIN_VALUE;
   public boolean boolean138 = false;
   public float float160 = 0.0F;
   public float float161 = 0.0F;
   public boolean boolean198 = false;
   public float float370 = 0.0F;
   public float float371 = 0.0F;
   public Vec3d vec3d44 = null;
   public Box box10 = null;

   public Rotation Easing(Rotation var1) {
      Rotation ililiiili1ll1li11 = val002.LineShader();
      if (var1 != null && !var1.string68()) {
         LivingEntity livingentity = this.call009();
         if (this.zClass026 != null && livingentity != null && livingentity.isAlive()) {
            Box box = livingentity.getBoundingBox();
            if (box == null) {
               this.botClient3();
               return ililiiili1ll1li11;
            }

            int i = livingentity.getId();
            if (this.int403 != i) {
               this.botClient3();
               this.int403 = i;
            }

            float f = ililiiili1ll1li11.GrimGlide();
            float f1 = ililiiili1ll1li11.GuiWalk();
            if (!this.boolean138) {
               this.float160 = f;
               this.float161 = f1;
               this.boolean138 = true;
               return ililiiili1ll1li11;
            } else {
               float f2 = MathHelper.wrapDegrees(f - this.float160);
               float f3 = f1 - this.float161;
               int j = Math.round(f2 / 0.21217355F);
               int k = Math.round(f3 / 0.21217355F);
               this.float160 = f;
               this.float161 = f1;
               Vec3d vec3d = minecraftClient3.player.getEyePos();
               Vec3d vec3d1 = minecraftClient3.player.getEntityPos();
               Vec3d vec3d2 = this.on23(livingentity, box);
               Vec3d vec3d3 = this.ColorAnimator(vec3d1);
               MotorIntentModel.Prediction ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil = this.on23(
                  ililiiili1ll1li11, vec3d, livingentity, box, vec3d2, vec3d3, j, k
               );
               this.vec3d44 = vec3d1;
               this.box10 = box;
               if (ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil == null) {
                  this.boolean198 = false;
                  this.zClass026.reset();
                  return ililiiili1ll1li11;
               } else {
                  this.boolean198 = true;
                  this.float370 = (float)ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double11;
                  this.float371 = (float)ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double12;
                  int[] aint = this.zClass026.on23(ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil);
                  return aint[0] == 0 && aint[1] == 0 ? ililiiili1ll1li11 : ililiiili1ll1li11.Event08(aint[0] * 0.21217355F, aint[1] * 0.21217355F);
               }
            }
         } else {
            this.botClient3();
            return ililiiili1ll1li11.on23(var1, 45.0F, 30.0F);
         }
      } else {
         this.botClient3();
         return ililiiili1ll1li11;
      }
   }

   public MotorIntentModel.Prediction on23(Rotation var1, Vec3d var2, LivingEntity var3, Box var4, Vec3d var5, Vec3d var6, int var7, int var8) {
      Vec3d vec3d = this.on23(var4);
      RotationDelta liiilliiilil1l1i1111li1ii11xx = var1.EmoteManager(this.on23(var2, vec3d));
      float f = liiilliiilil1l1i1111li1ii11xx.type2();
      float f1 = liiilliiilil1l1i1111li1ii11xx.path15();
      if (this.isFinite(f) && this.isFinite(f1)) {
         float f2 = Float.MAX_VALUE;
         float f3 = -Float.MAX_VALUE;
         float f4 = Float.MAX_VALUE;
         float f5 = -Float.MAX_VALUE;

         for (Vec3d vec3d1 : this.UiAnimation(var4)) {
            RotationDelta liiilliiilil1l1i1111li1ii11x = var1.EmoteManager(this.on23(var2, vec3d1));
            float f6 = liiilliiilil1l1i1111li1ii11x.type2();
            float f7 = liiilliiilil1l1i1111li1ii11x.path15();
            if (this.isFinite(f6) && this.isFinite(f7)) {
               f6 = this.SimpleItemBuilder(f6, f);
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

         Vec3d vec3d2 = this.on23(var1, var2);
         Box box = this.on23(var3, var4, var5);
         RotationDelta liiilliiilil1l1i1111li1ii11x = var1.EmoteManager(this.on23(vec3d2, this.on23(box)));
         float f8 = liiilliiilil1l1i1111li1ii11x.type2();
         float f9 = liiilliiilil1l1i1111li1ii11x.path15();
         if (!this.isFinite(f8) || !this.isFinite(f9)) {
            f8 = f;
            f9 = f1;
         }

         float f10 = f / 0.21217355F;
         float f11 = f1 / 0.21217355F;
         boolean flag = f2 <= 0.0F && f3 >= 0.0F && f4 <= 0.0F && f5 >= 0.0F;
         MotorIntentModel.Prediction ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil = new MotorIntentModel.Prediction();
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double11 = f10;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double12 = f11;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double13 = f2 / 0.21217355F;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double14 = f3 / 0.21217355F;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double15 = f4 / 0.21217355F;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double16 = f5 / 0.21217355F;
         if (this.boolean198) {
            ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double17 = f10 - this.float370;
            ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double18 = f11 - this.float371;
            ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double19 = f10 - (this.float370 - var7);
            ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double20 = f11 - (this.float371 - var8);
         }

         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double21 = f8 / 0.21217355F;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double22 = f9 / 0.21217355F;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double23 = flag ? 1.0 : 0.0;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double24 = var2.distanceTo(vec3d);
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double25 = var5.x;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double26 = var5.y;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double27 = var5.z;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double28 = var6.x;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double29 = var6.y;
         ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil.double30 = var6.z;
         return ii1ll11lil1l1i1ll1llli1l_ii1il11l111ii11iil;
      } else {
         return null;
      }
   }

   public static MotorIntentModel botWorld2() {
      InventoryCodec il11lill1lil1l1iill = InventoryCodec.string98();
      PermissionListCodec l1i1liliili = PermissionListCodec.string49();
      if (il11lill1lil1l1iill != null && l1i1liliili != null) {
         return new MotorIntentModel(il11lill1lil1l1iill, l1i1liliili);
      }

      System.err.println("[MotorIntent] artifacts not found; TestRotationModeV2 disabled");
      return null;
   }

   public String botWorld() {
      return this.zClass026 == null ? "unavailable" : this.zClass026.list47();
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

   public Rotation on23(Vec3d var1, Vec3d var2) {
      return RotationMath.Event08(var2.subtract(var1));
   }

   public Vec3d on23(Rotation var1, Vec3d var2) {
      Vec3d vec3d = minecraftClient3.player.getEntityPos();
      Vec3d vec3d1 = var2.subtract(vec3d);

      try {
         MovementController il11i11i111i1i1l1il = MovementController.on23(CustomInput.Easing(minecraftClient3.player.input.playerInput));
         il11i11i111i1i1l1il.yaw = var1.GrimGlide();
         il11i11i111i1i1l1il.pitch = var1.GuiWalk();
         il11i11i111i1i1l1il.tick();
         il11i11i111i1i1l1il.tick();
         Vec3d vec3d2 = il11i11i111i1i1l1il.TriggerBot.add(vec3d1);
         return this.Easing(vec3d2) ? vec3d2 : var2;
      } catch (Throwable throwable) {
         return var2;
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

   public LivingEntity call009() {
      LivingEntity livingentity = Aura.aura.zClass054();
      return livingentity != null && livingentity.isAlive() ? livingentity : null;
   }

   public Vec3d ColorAnimator(Vec3d var1) {
      if (this.vec3d44 != null && var1 != null) {
         Vec3d vec3d = var1.subtract(this.vec3d44);
         if (this.Easing(vec3d) && vec3d.lengthSquared() > 1.0E-10) {
            return vec3d;
         }
      }

      Vec3d vec3d1 = minecraftClient3.player.getVelocity();
      return this.Easing(vec3d1) ? vec3d1 : Vec3d.ZERO;
   }

   public Vec3d on23(LivingEntity var1, Box var2) {
      if (this.box10 != null && var2 != null) {
         Vec3d vec3d = this.on23(var2).subtract(this.on23(this.box10));
         if (this.Easing(vec3d) && vec3d.lengthSquared() > 1.0E-10) {
            return vec3d;
         }
      }

      Vec3d vec3d1 = var1 == null ? Vec3d.ZERO : var1.getVelocity();
      return this.Easing(vec3d1) ? vec3d1 : Vec3d.ZERO;
   }

   public Vec3d on23(Box var1) {
      return new Vec3d((var1.minX + var1.maxX) * 0.5, (var1.minY + var1.maxY) * 0.5, (var1.minZ + var1.maxZ) * 0.5);
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

   public void botClient3() {
      this.int403 = Integer.MIN_VALUE;
      this.boolean138 = false;
      this.float160 = 0.0F;
      this.float161 = 0.0F;
      this.boolean198 = false;
      this.float370 = 0.0F;
      this.float371 = 0.0F;
      this.vec3d44 = null;
      this.box10 = null;
      if (this.zClass026 != null) {
         this.zClass026.reset();
      }
   }

   public boolean isFinite(float var1) {
      return !Float.isNaN(var1) && !Float.isInfinite(var1);
   }

   public boolean Easing(Vec3d var1) {
      return var1 != null && this.isFinite((float)var1.x) && this.isFinite((float)var1.y) && this.isFinite((float)var1.z);
   }
}
