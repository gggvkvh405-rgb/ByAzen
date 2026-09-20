package org.zenith.rotation;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.core.CustomInput;
import org.zenith.core.GmmModel;
import org.zenith.core.MotorPolicyNet;
import org.zenith.core.MovementController;
import org.zenith.core.NeuralProvider;
import org.zenith.module.combat.Aura;

public final class AimPolicyRotationStrategy extends RotationStrategyBase {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int int398 = Integer.MIN_VALUE;
   public static final int int399 = 59;
   public static final int int400 = 0;
   public static final int int401 = 1;
   public static final int int402 = 16;
   public static final float getChamsVar159 = 50.0F;
   public static final float float284 = 176.0F;
   public static final float float285 = 8.0F;
   public static final float float286 = 8.0F;
   public static final String[] call405 = new String[0];
   public int int403 = Integer.MIN_VALUE;
   public boolean boolean138 = false;
   public float float160 = 0.0F;
   public float float161 = 0.0F;
   public float float162 = 0.0F;
   public float float163 = 0.0F;
   public float float164 = 0.0F;
   public float float165 = 0.0F;
   public boolean boolean139 = false;
   public float float166 = 0.0F;
   public float float167 = 0.0F;
   public float float168 = 0.0F;
   public float float169 = 0.0F;
   public float float170 = 0.0F;
   public float float171 = 0.0F;
   public int int294 = 0;
   public int int295 = 0;
   public float float172 = 0.0F;
   public float float173 = 0.0F;
   public float float174 = 0.0F;
   public float float175 = 0.0F;
   public AimPolicyRotationStrategy.ModelOutput zClass022Var159 = null;
   public float[] call025 = null;
   public final NeuralProvider zClass030 = botClient2();
   float float27;

   public static NeuralProvider botClient2() {
      GmmModel l11il1i1iil1lll111l1111llliil = GmmModel.long110();
      if (l11il1i1iil1lll111l1111llliil != null) {
         return new MotorPolicyNet(l11il1i1iil1lll111l1111llliil);
      }

      System.err.println("[AimPolicy] artifact not found; TestRotationMode disabled");
      return null;
   }

   public Rotation on23(RoundedRectEasing var1, Rotation var2) {
      return this.on23(var1, var2, this.call009());
   }

   public Rotation on23(RoundedRectEasing var1, Rotation var2, LivingEntity var3) {
      Rotation ililiiili1ll1li11 = val002.LineShader();
      if (this.zClass030 == null) {
         return ililiiili1ll1li11;
      }

      if (var2 == null || var2.string68()) {
         this.botClient3();
         return ililiiili1ll1li11;
      }

      if (var3 == null) {
         return RotationMath.boolean122();
      }

      if (var3 != null && var3.isAlive()) {
         Box box = var3.getBoundingBox();
         if (box == null) {
            this.botClient3();
            return ililiiili1ll1li11;
         }

         int i = var3.getId();
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
         }

         float f2 = MathHelper.wrapDegrees(f - this.float160);
         float f3 = f1 - this.float161;
         Vec3d vec3d = minecraftClient3.player.getEntityPos();
         Vec3d vec3d1 = this.on23(var3, box);
         Vec3d vec3d2 = this.ColorAnimator(vec3d);
         float f4 = 0.21217355F;
         if (!this.isFinite(f4)) {
            this.call025 = null;
            this.float160 = f;
            this.float161 = f1;
            return ililiiili1ll1li11;
         }

         float[] afloat = this.on23(ililiiili1ll1li11, minecraftClient3.player.getEyePos(), var3, box, f2, f3, vec3d1, vec3d2, f4);
         this.float160 = f;
         this.float161 = f1;
         if (afloat != null && afloat.length == 59 && this.StringCodec(afloat)) {
            this.call025 = (float[])afloat.clone();
            this.zClass022Var159 = new AimPolicyRotationStrategy.ModelOutput(afloat, vec3d, box, f4);
            int[] aint = this.zClass030.on23(afloat);
            if (minecraftClient3.player.ticksSinceLastAttack < 6) {
               this.float27 = MathHelper.clamp(this.float27 - 0.3F, 0.0F, 1.0F);
            } else {
               this.float27 = MathHelper.clamp(this.float27 + 1.0F, 0.0F, 1.0F);
            }

            int[] aint1 = new int[]{(int)(this.on23(aint[0], afloat[0], 1.0F, 0.9F) * this.float27), aint[1]};
            return ililiiili1ll1li11.Event08(aint1[0] * f4, aint1[1] * f4);
         } else {
            this.call025 = null;
            this.zClass030.reset();
            return ililiiili1ll1li11;
         }
      } else {
         this.botClient3();
         return ililiiili1ll1li11;
      }
   }

   public double on23(double var1, float var3, float var4, float var5) {
      boolean flag = Math.signum(var1) == Math.signum(var3);
      float f = flag ? var4 : var5;
      return var1 * f;
   }

   public String botWorld() {
      return this.zClass030 == null ? "unavailable" : this.zClass030.list47();
   }

   public float[] long98() {
      return this.call025 == null ? null : (float[])this.call025.clone();
   }

   public String[] call454() {
      return (String[])call405.clone();
   }

   public void botPlayer() {
      this.botClient3();
   }

   public float[] on23(Rotation var1, Vec3d var2, LivingEntity var3, Box var4, float var5, float var6, Vec3d var7, Vec3d var8, float var9) {
      Vec3d[] avec3d = new Vec3d[]{
         new Vec3d(var4.minX, var4.minY, var4.minZ),
         new Vec3d(var4.minX, var4.minY, var4.maxZ),
         new Vec3d(var4.minX, var4.maxY, var4.minZ),
         new Vec3d(var4.minX, var4.maxY, var4.maxZ),
         new Vec3d(var4.maxX, var4.minY, var4.minZ),
         new Vec3d(var4.maxX, var4.minY, var4.maxZ),
         new Vec3d(var4.maxX, var4.maxY, var4.minZ),
         new Vec3d(var4.maxX, var4.maxY, var4.maxZ)
      };
      Vec3d vec3d = this.on23(var4);
      Rotation ililiiili1ll1li11 = this.on23(var2, vec3d);
      RotationDelta liiilliiilil1l1i1111li1ii11 = var1.EmoteManager(ililiiili1ll1li11);
      float f = liiilliiilil1l1i1111li1ii11.type2();
      float f1 = liiilliiilil1l1i1111li1ii11.path15();
      if (this.isFinite(f) && this.isFinite(f1)) {
         Vec3d vec3d1 = this.on23(var1, var2);
         Box box = this.on23(var3, var4, var7);
         RotationDelta liiilliiilil1l1i1111li1ii11x = var1.EmoteManager(this.on23(vec3d1, this.on23(box)));
         float f2 = liiilliiilil1l1i1111li1ii11x.type2();
         float f3 = liiilliiilil1l1i1111li1ii11x.path15();
         if (this.isFinite(f2) && this.isFinite(f3)) {
            float f4 = Float.MAX_VALUE;
            float f5 = -Float.MAX_VALUE;
            float f6 = Float.MAX_VALUE;
            float f7 = -Float.MAX_VALUE;

            for (Vec3d vec3d2 : avec3d) {
               Rotation ililiiili1ll1li111 = this.on23(var2, vec3d2);
               RotationDelta liiilliiilil1l1i1111li1ii11xx = var1.EmoteManager(ililiiili1ll1li111);
               float f8 = liiilliiilil1l1i1111li1ii11xx.type2();
               float f9 = liiilliiilil1l1i1111li1ii11xx.path15();
               if (this.isFinite(f8) && this.isFinite(f9)) {
                  f8 = this.SimpleItemBuilder(f8, f);
                  f4 = Math.min(f4, f8);
                  f5 = Math.max(f5, f8);
                  f6 = Math.min(f6, f9);
                  f7 = Math.max(f7, f9);
               }
            }

            if (f4 == Float.MAX_VALUE) {
               f4 = f;
               f5 = f;
               f6 = f1;
               f7 = f1;
            }

            f = this.SimpleItemBuilder(f, f);
            float f45 = f5 - f4;
            float f46 = f7 - f6;
            float f47 = (float)(vec3d.x - var2.x);
            float f48 = Math.abs((float)(vec3d.y - var2.y));
            float f49 = (float)(vec3d.z - var2.z);
            float f50 = (float)var2.distanceTo(vec3d);
            float f51 = this.hypot(f47, f49);
            boolean flag2 = f4 <= 0.0F && f5 >= 0.0F;
            boolean flag = f6 <= 0.0F && f7 >= 0.0F;
            float f10 = 0.0F;
            if (flag2 && Math.abs(f45) > 1.0E-6F) {
               f10 = MathHelper.clamp((0.0F - f4) / f45, 0.0F, 1.0F);
            }

            float f11 = 0.0F;
            if (flag && Math.abs(f46) > 1.0E-6F) {
               f11 = MathHelper.clamp((0.0F - f6) / f46, 0.0F, 1.0F);
            }

            boolean flag1 = flag2 && flag;
            this.float166 = flag1 ? MathHelper.clamp(this.float166 + 1.0F, 0.0F, 50.0F) : 0.0F;
            this.float167 = flag1 ? 0.0F : Math.min(this.float167 + 1.0F, 8.0F);
            float f12 = var5 - this.float162;
            float f13 = var6 - this.float163;
            float f14 = f12 - this.float164;
            float f15 = f13 - this.float165;
            float f16 = this.zClass022Var159 != null ? this.FileLogger(this.zClass022Var159.call094[0], this.zClass022Var159.float55) : f;
            float f17 = this.zClass022Var159 != null ? this.FileLogger(this.zClass022Var159.call094[1], this.zClass022Var159.float55) : f1;
            float f18 = this.zClass022Var159 != null ? this.zClass022Var159.call094[16] : f50;
            float f19 = MathHelper.wrapDegrees(f - f16);
            float f20 = f1 - f17;
            float f21 = f50 - f18;
            float f22 = f / Math.max(Math.abs(f45), 0.1F);
            float f23 = f1 / Math.max(Math.abs(f46), 0.1F);
            float f24 = (float)Math.sqrt(f * f + f1 * f1);
            float f25 = (float)Math.atan2(f1, f);
            float f26 = (float)Math.sin(f25);
            float f27 = (float)Math.cos(f25);
            float f28 = this.StringCodec(var5, var9);
            float f29 = this.StringCodec(var6, var9);
            float f30 = this.EnchantItemSpec(f, var9);
            float f31 = this.EnchantItemSpec(f1, var9);
            float f32 = 0.0F;
            float f33 = 0.0F;
            float f34 = 0.0F;
            float f35 = 0.0F;
            if (this.zClass022Var159 != null) {
               float f36 = this.zClass022Var159.call094[0];
               float f37 = this.zClass022Var159.call094[1];
               float f38 = f36 - f28;
               float f39 = f37 - f29;
               f32 = Math.abs(f36) - Math.abs(f38);
               f33 = Math.abs(f37) - Math.abs(f39);
               f34 = f30 - f38;
               f35 = f31 - f39;
            }

            this.float170 = f28 != 0.0F ? this.float170 + 1.0F : 0.0F;
            this.float171 = f29 != 0.0F ? this.float171 + 1.0F : 0.0F;
            int i = this.InventoryUtils(f28);
            int j = this.InventoryUtils(f29);
            this.float174 = this.on23(f28, i, this.int294, this.float174);
            this.float175 = this.on23(f29, j, this.int295, this.float175);
            float f52 = this.CloudApiClient(f28, this.float174);
            float f53 = this.CloudApiClient(f29, this.float175);
            this.float172 = this.on23(i, this.int294, this.float172);
            this.float173 = this.on23(j, this.int295, this.float173);
            this.int294 = i != 0 ? i : this.int294;
            this.int295 = j != 0 ? j : this.int295;
            float f40 = minecraftClient3.player.input.playerInput.forward() ? 1.0F : 0.0F;
            float f41 = minecraftClient3.player.input.playerInput.backward() ? 1.0F : 0.0F;
            float f42 = minecraftClient3.player.input.playerInput.left() ? 1.0F : 0.0F;
            float f43 = minecraftClient3.player.input.playerInput.right() ? 1.0F : 0.0F;
            float f44 = minecraftClient3.player.input.playerInput.jump() ? 1.0F : 0.0F;
            if (flag1) {
               this.float168 = MathHelper.clamp(this.boolean139 ? this.float168 + 1.0F : 1.0F, 0.0F, 176.0F);
               this.float169 = 0.0F;
            } else {
               this.float168 = 0.0F;
               this.float169 = MathHelper.clamp(this.boolean139 ? 1.0F : this.float169 + 1.0F, 0.0F, 8.0F);
            }

            float[] afloat = new float[]{
               f30,
               f31,
               this.EnchantItemSpec(f4, var9),
               this.EnchantItemSpec(f5, var9),
               this.EnchantItemSpec(f6, var9),
               this.EnchantItemSpec(f7, var9),
               this.EnchantItemSpec(f45, var9),
               this.EnchantItemSpec(f46, var9),
               f22,
               f23,
               this.EnchantItemSpec(f24, var9),
               f26,
               f27,
               f10,
               f11,
               flag1 ? 1.0F : 0.0F,
               f50,
               f51,
               f48,
               this.float166,
               this.float167,
               this.float168,
               this.float169,
               this.EnchantItemSpec(f19, var9),
               this.EnchantItemSpec(f20, var9),
               f21,
               f34,
               f35,
               this.EnchantItemSpec(f2, var9),
               this.EnchantItemSpec(f3, var9),
               (float)var7.x,
               (float)var7.y,
               (float)var7.z,
               f28,
               f29,
               f28,
               f29,
               this.StringCodec(f12, var9),
               this.StringCodec(f13, var9),
               this.StringCodec(f14, var9),
               this.StringCodec(f15, var9),
               this.float170,
               this.float171,
               this.float172,
               this.float173,
               f32,
               f33,
               f52,
               f53,
               this.float174,
               this.float175,
               f40,
               f41,
               f42,
               f43,
               f44,
               (float)var8.x,
               (float)var8.y,
               (float)var8.z
            };
            if (!this.StringCodec(afloat)) {
               return null;
            }

            this.float162 = var5;
            this.float163 = var6;
            this.float164 = f12;
            this.float165 = f13;
            this.boolean139 = flag1;
            return afloat;
         } else {
            return null;
         }
      } else {
         return null;
      }
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
      if (this.zClass022Var159 != null && var1 != null) {
         Vec3d vec3d = var1.subtract(this.zClass022Var159.vec3d16);
         if (this.Easing(vec3d) && vec3d.lengthSquared() > 1.0E-10) {
            return vec3d;
         }
      }

      Vec3d vec3d1 = minecraftClient3.player.getVelocity();
      return this.Easing(vec3d1) ? vec3d1 : Vec3d.ZERO;
   }

   public Vec3d on23(LivingEntity var1, Box var2) {
      if (this.zClass022Var159 != null && var2 != null && this.zClass022Var159.box4 != null) {
         Vec3d vec3d = this.on23(var2).subtract(this.on23(this.zClass022Var159.box4));
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

   public float hypot(float var1, float var2) {
      return (float)Math.sqrt(var1 * var1 + var2 * var2);
   }

   public float EnchantItemSpec(float var1, float var2) {
      return var1 / var2;
   }

   public float StringCodec(float var1, float var2) {
      return Math.round(var1 / var2);
   }

   public float FileLogger(float var1, float var2) {
      return this.isFinite(var2) && var2 > 0.0F ? var1 * var2 : var1;
   }

   public int InventoryUtils(float var1) {
      if (var1 > 0.0F) {
         return 1;
      } else {
         return var1 < 0.0F ? -1 : 0;
      }
   }

   public float on23(int var1, int var2, float var3) {
      return var1 != 0 && var2 != 0 && var1 != var2 ? 0.0F : var3 + 1.0F;
   }

   public float on23(float var1, int var2, int var3, float var4) {
      float f = Math.abs(var1);
      if (var2 == 0) {
         return 0.0F;
      } else {
         return var3 != 0 && var2 == var3 ? Math.max(var4, f) : f;
      }
   }

   public float CloudApiClient(float var1, float var2) {
      return var2 > 0.0F ? Math.abs(var1) / var2 : 0.0F;
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
      this.float162 = 0.0F;
      this.float163 = 0.0F;
      this.float164 = 0.0F;
      this.float165 = 0.0F;
      this.boolean139 = false;
      this.float166 = 0.0F;
      this.float167 = 0.0F;
      this.float168 = 0.0F;
      this.float169 = 0.0F;
      this.float170 = 0.0F;
      this.float171 = 0.0F;
      this.int294 = 0;
      this.int295 = 0;
      this.float172 = 0.0F;
      this.float173 = 0.0F;
      this.float174 = 0.0F;
      this.float175 = 0.0F;
      this.zClass022Var159 = null;
      this.call025 = null;
      if (this.zClass030 != null) {
         this.zClass030.reset();
      }
   }

   public boolean StringCodec(float[] var1) {
      if (var1 == null) {
         return false;
      }

      for (float f : var1) {
         if (!this.isFinite(f)) {
            return false;
         }
      }

      return true;
   }

   public boolean isFinite(float var1) {
      return !Float.isNaN(var1) && !Float.isInfinite(var1);
   }

   public boolean Easing(Vec3d var1) {
      return var1 != null && this.isFinite((float)var1.x) && this.isFinite((float)var1.y) && this.isFinite((float)var1.z);
   }


   public static final class ModelOutput {
      public final float[] call094;
      public final Vec3d vec3d16;
      public final Box box4;
      public final float float55;

      public ModelOutput(float[] var1, Vec3d var2, Box var3, float var4) {
         this.call094 = (float[])var1.clone();
         this.vec3d16 = var2;
         this.box4 = var3;
         this.float55 = var4;
      }
   }
}
