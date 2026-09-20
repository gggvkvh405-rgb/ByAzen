package org.zenith.core;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.module.combat.Aura;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationDelta;
import org.zenith.rotation.RotationMath;
import org.zenith.rotation.RotationStrategyBase;
import org.zenith.rotation.RoundedRectEasing;

public class RotationPredictiveStrategy extends RotationStrategyBase {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final int int430 = Integer.MIN_VALUE;
   public static final float float320 = 50.0F;
   public static final float float321 = 176.0F;
   public static final float float322 = 8.0F;
   public static final float float323 = 8.0F;
   public static final float float324 = 20.0F;
   public static final float float325 = 0.18F;
   public static final float float326 = 0.055F;
   public static final float float327 = 0.08F;
   public static final float float328 = 0.14F;
   public static final float float329 = 6.0F;
   public static final float float330 = 1.15F;
   public static final float float331 = 1.0F;
   public static final float float332 = 8.0F;
   public static final float float333 = 4.0F;
   public static final float float334 = 3.0F;
   public static final int int431 = 2;
   public static final float float335 = 6.0F;
   public static final float float336 = 1.0F;
   public static final float float337 = 2.0F;
   public static final float float338 = 0.12F;
   public static final float float339 = 18.0F;
   public static final float float340 = 10.0F;
   public boolean boolean187 = false;
   public int int403 = Integer.MIN_VALUE;
   public float[] call025 = null;
   public WorldScanService zClass016Var134 = null;
   public float float341 = 0.0F;
   public float float342 = 0.0F;
   public int int432 = 0;
   public float float343 = 0.0F;
   public float float344 = 0.0F;
   public float float345 = 0.0F;
   public float float346 = 0.0F;
   public boolean boolean138 = false;
   public float lastYaw = 0.0F;
   public float lastPitch = 0.0F;
   public float float162 = 0.0F;
   public float float163 = 0.0F;
   public float float164 = 0.0F;
   public float float165 = 0.0F;
   public boolean boolean188 = false;
   public float float347 = 0.0F;
   public float float348 = 0.0F;
   public float float349 = 0.0F;
   public float float350 = 0.0F;
   public double double139 = 0.0;
   public double double140 = 0.0;
   public double double141 = 0.0;
   public double double142 = 0.0;
   public double double143 = 0.0;
   public double double144 = 0.0;
   public boolean boolean189 = false;
   public float float166 = 0.0F;
   public float float167 = 0.0F;
   public float float168 = 0.0F;
   public float float169 = 0.0F;
   public float float351 = 0.0F;
   public float float352 = 0.0F;

   public Rotation on23(RoundedRectEasing var1, Rotation var2) {
      return this.on23(var1, var2, this.call009());
   }

   public Rotation on23(RoundedRectEasing var1, Rotation var2, LivingEntity var3) {
      Rotation ililiiili1ll1li11 = val002.LineShader();
      this.call268();
      if (!val014.keyframeAnimation()) {
         this.call143();
         return ililiiili1ll1li11;
      }

      if (var2 == null || var2.string68()) {
         this.call143();
         return ililiiili1ll1li11;
      }

      if (var3 != null && var3.isAlive()) {
         int i = var3.getId();
         if (this.int403 != i) {
            this.call143();
            this.int403 = i;
         }

         PredictiveTuning l11llilil1_ii1il11l111ii11iil = this.on23(ililiiili1ll1li11, var3);
         if (l11llilil1_ii1il11l111ii11iil == null) {
            this.call143();
            return ililiiili1ll1li11;
         }

         String[] astring = val014.call454();
         float[] afloat = this.on23(l11llilil1_ii1il11l111ii11iil, astring);
         int j = val014.getThis3();
         if (afloat != null && afloat.length == j && this.StringCodec(afloat)) {
            this.call025 = (float[])afloat.clone();
            WorldScanService i1illl111l11illl1il111_l1iil11li = val014.on23(
               afloat, l11llilil1_ii1il11l111ii11iil.call122, l11llilil1_ii1il11l111ii11iil.call120
            );
            if (i1illl111l11illl1il111_l1iil11li == null) {
               this.zClass016Var134 = null;
               return ililiiili1ll1li11;
            }

            PredictiveWeights l11llilil1_l1i1illlilix = this.ColorAnimator(i1illl111l11illl1il111_l1iil11li.list118(), i1illl111l11illl1il111_l1iil11li.map61());
            if (l11llilil1_l1i1illlilix == null) {
               this.zClass016Var134 = null;
               return ililiiili1ll1li11;
            }

            l11llilil1_l1i1illlilix = this.on23(l11llilil1_l1i1illlilix);
            if (l11llilil1_l1i1illlilix != null && this.isFinite(l11llilil1_l1i1illlilix.call027) && this.isFinite(l11llilil1_l1i1illlilix.call074)) {
               l11llilil1_l1i1illlilix = this.on23(l11llilil1_ii1il11l111ii11iil, l11llilil1_l1i1illlilix);
               if (l11llilil1_l1i1illlilix != null && this.isFinite(l11llilil1_l1i1illlilix.call027) && this.isFinite(l11llilil1_l1i1illlilix.call074)) {
                  this.zClass016Var134 = i1illl111l11illl1il111_l1iil11li.on23(
                     l11llilil1_l1i1illlilix.call027,
                     l11llilil1_l1i1illlilix.call074,
                     l11llilil1_ii1il11l111ii11iil.call122,
                     l11llilil1_ii1il11l111ii11iil.call120,
                     l11llilil1_l1i1illlilix.call166,
                     l11llilil1_l1i1illlilix.call168,
                     l11llilil1_l1i1illlilix.call092,
                     l11llilil1_l1i1illlilix.call091,
                     l11llilil1_l1i1illlilix.call195,
                     l11llilil1_l1i1illlilix.call259
                  );
                  this.float345 = this.float343;
                  this.float346 = this.float344;
                  this.float343 = l11llilil1_l1i1illlilix.call092;
                  this.float344 = l11llilil1_l1i1illlilix.call091;
                  Rotation ililiiili1ll1li111 = ililiiili1ll1li11.Event08(l11llilil1_l1i1illlilix.call027, l11llilil1_l1i1illlilix.call074);
                  return new Rotation(ililiiili1ll1li111.GrimGlide(), MathHelper.clamp(ililiiili1ll1li111.GuiWalk(), -90.0F, 90.0F));
               } else {
                  this.zClass016Var134 = null;
                  return ililiiili1ll1li11;
               }
            } else {
               this.zClass016Var134 = null;
               return ililiiili1ll1li11;
            }
         } else {
            this.call143();
            return ililiiili1ll1li11;
         }
      } else {
         this.call143();
         return ililiiili1ll1li11;
      }
   }

   public WorldScanService long97() {
      return this.zClass016Var134;
   }

   public float[] long98() {
      return this.call025 == null ? null : (float[])this.call025.clone();
   }

   public void var15Var160() {
      this.call143();
   }

   public float[] on23(PredictiveTuning var1, String[] var2) {
      if (var1 != null && var2 != null && var2.length != 0) {
         float[] afloat = new float[var2.length];

         for (int i = 0; i < var2.length; i++) {
            float f = this.on23(var1, var2[i]);
            if (!this.isFinite(f)) {
               return null;
            }

            afloat[i] = f;
         }

         return afloat;
      } else {
         return null;
      }
   }

   public float on23(PredictiveTuning var1, String var2) {
      if (var2 == null) {
         return Float.NaN;
      }

      switch (var2) {
         case "raw_center_Diffyaw":
            return this.EnchantItemSpec(var1.call189, var1.val005);
         case "raw_center_Diffpitch":
            return this.EnchantItemSpec(var1.call162, var1.val005);
         case "raw_min_Diffyaw":
            return this.EnchantItemSpec(var1.call090, var1.val005);
         case "raw_max_Diffyaw":
            return this.EnchantItemSpec(var1.call190, var1.val005);
         case "raw_min_Diffpitch":
            return this.EnchantItemSpec(var1.call161, var1.val005);
         case "raw_max_Diffpitch":
            return this.EnchantItemSpec(var1.call191, var1.val005);
         case "raw_width_Diffyaw":
            return this.EnchantItemSpec(var1.call192, var1.val005);
         case "raw_height_Diffpitch":
            return this.EnchantItemSpec(var1.call193, var1.val005);
         case "raw_target_x_norm":
            return var1.call245;
         case "raw_target_y_norm":
            return var1.call246;
         case "raw_distance":
            return var1.float21;
         case "raw_delta_center_Diffyaw":
            return this.EnchantItemSpec(var1.call248, var1.val005);
         case "raw_delta_center_Diffpitch":
            return this.EnchantItemSpec(var1.call249, var1.val005);
         case "raw_delta_distance":
            return var1.call250;
         case "raw_inside_box":
            return var1.call121 ? 1.0F : 0.0F;
         case "raw_inside_ticks_clamped":
            return var1.call188;
         case "outside_box_ticks":
            return var1.float385;
         case "yaw_vel":
            return this.NbtItemSpec(var1.call122, var1.val005);
         case "pitch_vel":
            return this.NbtItemSpec(var1.call120, var1.val005);
         case "yaw_acc":
            return this.NbtItemSpec(var1.call254, var1.val005);
         case "pitch_acc":
            return this.NbtItemSpec(var1.call163, var1.val005);
         case "normalized_center_yaw":
            return var1.call243;
         case "normalized_center_pitch":
            return var1.call257;
         case "yaw_jerk":
            return this.NbtItemSpec(var1.call089, var1.val005);
         case "pitch_jerk":
            return this.NbtItemSpec(var1.call160, var1.val005);
         case "ang_dist":
            return this.EnchantItemSpec(var1.call071, var1.val005);
         case "dir_sin":
            return var1.call256;
         case "dir_cos":
            return var1.call258;
         case "t_since_entered_box":
            return var1.float168;
         case "t_since_left_box":
            return var1.float169;
         case "effort_fatigue":
            return var1.float351;
         case "active_burst_ticks":
            return var1.float352;
         case "input_forward":
            return var1.call403;
         case "input_backward":
            return var1.call035;
         case "input_left":
            return var1.call164;
         case "input_right":
            return var1.val424;
         case "input_jump":
            return var1.call073;
         case "diff_y":
            return var1.call247;
         case "distance_xz":
            return var1.call187;
         case "target_motion_x":
            return var1.call251;
         case "target_motion_y":
            return var1.call026;
         case "target_motion_z":
            return var1.call252;
         case "player_motion_x":
            return var1.call253;
         case "player_motion_y":
            return var1.call244;
         case "player_motion_z":
            return var1.call255;
         case "v18_geometry_unstable":
            return var1.call167;
         case "v18_geometry_confidence":
            return var1.call198;
         case "v18_inside_confidence":
            return var1.call123;
         case "v18_close_overlap":
            return var1.call261;
         case "v18_center_absmax":
            return var1.call165;
         case "v18_prev_vel_absmax":
            return var1.call260;
         case "v18_nearest_yaw_error":
            return var1.call197;
         case "v18_nearest_pitch_error":
            return var1.call194;
         case "v18_aim_yaw_error":
            return var1.call072;
         case "v18_aim_pitch_error":
            return var1.call196;
         case "prev_action_yaw_count":
            return var1.float343;
         case "prev_action_pitch_count":
            return var1.float344;
         case "prev2_action_yaw_count":
            return var1.float345;
         case "prev2_action_pitch_count":
            return var1.float346;
         default:
            return Float.NaN;
      }
   }

   public PredictiveTuning on23(Rotation var1, LivingEntity var2) {
      if (var1 != null && var2 != null) {
         Box box = var2.getBoundingBox();
         Vec3d vec3d = minecraftClient3.player.getEntityPos();
         Vec3d vec3d1 = minecraftClient3.player.getEyePos();
         Vec3d vec3d2 = box.getCenter();
         Rotation ililiiili1ll1li11 = RotationMath.Event08(vec3d2.subtract(vec3d1));
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

            for (Vec3d vec3d3 : avec3d) {
               Rotation ililiiili1ll1li111 = RotationMath.Event08(vec3d3.subtract(vec3d1));
               liiilliiilil1l1i1111li1ii11x = var1.EmoteManager(ililiiili1ll1li111);
               float f6 = this.SimpleItemBuilder(liiilliiilil1l1i1111li1ii11x.type2(), f);
               float f7 = liiilliiilil1l1i1111li1ii11x.path15();
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

            float f23 = f3 - f2;
            float f24 = f5 - f4;
            boolean flag = f2 <= 0.0F && f3 >= 0.0F;
            boolean flag1 = f4 <= 0.0F && f5 >= 0.0F;
            boolean flag2 = flag && flag1;
            float f25 = 0.0F;
            if (flag && Math.abs(f23) > 1.0E-6F) {
               f25 = MathHelper.clamp((0.0F - f2) / f23, 0.0F, 1.0F);
            }

            float f26 = 0.0F;
            if (flag1 && Math.abs(f24) > 1.0E-6F) {
               f26 = MathHelper.clamp((0.0F - f4) / f24, 0.0F, 1.0F);
            }

            float f27 = 0.0F;
            float f8 = 0.0F;
            if (this.boolean138) {
               f27 = MathHelper.wrapDegrees(var1.GrimGlide() - this.lastYaw);
               f8 = var1.GuiWalk() - this.lastPitch;
            }

            float f9 = f27 - this.float162;
            float f10 = f8 - this.float163;
            float f11 = f9 - this.float164;
            float f12 = f10 - this.float165;
            this.float166 = flag2 ? MathHelper.clamp(this.float166 + 1.0F, 0.0F, 50.0F) : 0.0F;
            this.float167 = flag2 ? 0.0F : Math.min(this.float167 + 1.0F, 8.0F);
            if (flag2) {
               this.float168 = MathHelper.clamp(this.boolean189 ? this.float168 + 1.0F : 1.0F, 0.0F, 176.0F);
               this.float169 = 0.0F;
            } else {
               this.float168 = 0.0F;
               this.float169 = MathHelper.clamp(this.boolean189 ? 1.0F : this.float169 + 1.0F, 0.0F, 8.0F);
            }

            this.on23(f23, f24, f27, f8, flag2);
            float f13 = (float)(vec3d2.x - vec3d1.x);
            float f14 = Math.abs((float)(vec3d2.y - vec3d1.y));
            float f15 = (float)(vec3d2.z - vec3d1.z);
            float f16 = this.hypot(f13, f15);
            float f17 = this.hypot(f16, f14);
            float f18 = this.boolean188 ? this.hypot(this.float349, this.float350) : f17;
            float f19 = this.boolean188 ? MathHelper.wrapDegrees(f - this.float347) : 0.0F;
            float f20 = this.boolean188 ? f1 - this.float348 : 0.0F;
            float f21 = f17 - f18;
            Vec3d vec3d4 = this.boolean188 ? vec3d2.subtract(this.double139, this.double140, this.double141) : Vec3d.ZERO;
            if (!this.Easing(vec3d4) || vec3d4.lengthSquared() <= 1.0E-10) {
               Vec3d vec3d5 = var2.getVelocity();
               vec3d4 = this.Easing(vec3d5) ? vec3d5 : Vec3d.ZERO;
            }

            Vec3d vec3d7 = this.boolean188 ? vec3d.subtract(this.double142, this.double143, this.double144) : Vec3d.ZERO;
            if (!this.Easing(vec3d7) || vec3d7.lengthSquared() <= 1.0E-10) {
               Vec3d vec3d6 = minecraftClient3.player.getVelocity();
               vec3d7 = this.Easing(vec3d6) ? vec3d6 : Vec3d.ZERO;
            }

            float f28 = val014.string99();
            if (this.isFinite(f28) && !(f28 <= 0.0F)) {
               PredictiveTuning l11llilil1_ii1il11l111ii11iil = new PredictiveTuning();
               l11llilil1_ii1il11l111ii11iil.call189 = f;
               l11llilil1_ii1il11l111ii11iil.call162 = f1;
               l11llilil1_ii1il11l111ii11iil.call090 = f2;
               l11llilil1_ii1il11l111ii11iil.call190 = f3;
               l11llilil1_ii1il11l111ii11iil.call161 = f4;
               l11llilil1_ii1il11l111ii11iil.call191 = f5;
               l11llilil1_ii1il11l111ii11iil.call192 = f23;
               l11llilil1_ii1il11l111ii11iil.call193 = f24;
               l11llilil1_ii1il11l111ii11iil.call245 = f25;
               l11llilil1_ii1il11l111ii11iil.call246 = f26;
               l11llilil1_ii1il11l111ii11iil.call247 = f14;
               l11llilil1_ii1il11l111ii11iil.float21 = f17;
               l11llilil1_ii1il11l111ii11iil.call187 = f16;
               l11llilil1_ii1il11l111ii11iil.call121 = flag2;
               l11llilil1_ii1il11l111ii11iil.call188 = this.float166;
               l11llilil1_ii1il11l111ii11iil.float385 = this.float167;
               l11llilil1_ii1il11l111ii11iil.call248 = f19;
               l11llilil1_ii1il11l111ii11iil.call249 = f20;
               l11llilil1_ii1il11l111ii11iil.call250 = f21;
               l11llilil1_ii1il11l111ii11iil.call251 = (float)vec3d4.x;
               l11llilil1_ii1il11l111ii11iil.call026 = (float)vec3d4.y;
               l11llilil1_ii1il11l111ii11iil.call252 = (float)vec3d4.z;
               l11llilil1_ii1il11l111ii11iil.call253 = (float)vec3d7.x;
               l11llilil1_ii1il11l111ii11iil.call244 = (float)vec3d7.y;
               l11llilil1_ii1il11l111ii11iil.call255 = (float)vec3d7.z;
               l11llilil1_ii1il11l111ii11iil.call122 = f27;
               l11llilil1_ii1il11l111ii11iil.call120 = f8;
               l11llilil1_ii1il11l111ii11iil.call254 = f9;
               l11llilil1_ii1il11l111ii11iil.call163 = f10;
               l11llilil1_ii1il11l111ii11iil.call089 = f11;
               l11llilil1_ii1il11l111ii11iil.call160 = f12;
               l11llilil1_ii1il11l111ii11iil.call243 = f / Math.max(Math.abs(f23), 0.1F);
               l11llilil1_ii1il11l111ii11iil.call257 = f1 / Math.max(Math.abs(f24), 0.1F);
               l11llilil1_ii1il11l111ii11iil.call071 = this.hypot(f, f1);
               float f22 = (float)Math.atan2(f1, f);
               l11llilil1_ii1il11l111ii11iil.call256 = (float)Math.sin(f22);
               l11llilil1_ii1il11l111ii11iil.call258 = (float)Math.cos(f22);
               l11llilil1_ii1il11l111ii11iil.float168 = this.float168;
               l11llilil1_ii1il11l111ii11iil.float169 = this.float169;
               l11llilil1_ii1il11l111ii11iil.float351 = this.float351;
               l11llilil1_ii1il11l111ii11iil.float352 = this.float352;
               l11llilil1_ii1il11l111ii11iil.call403 = minecraftClient3.player.input.playerInput.forward() ? 1.0F : 0.0F;
               l11llilil1_ii1il11l111ii11iil.call035 = minecraftClient3.player.input.playerInput.backward() ? 1.0F : 0.0F;
               l11llilil1_ii1il11l111ii11iil.call164 = minecraftClient3.player.input.playerInput.left() ? 1.0F : 0.0F;
               l11llilil1_ii1il11l111ii11iil.val424 = minecraftClient3.player.input.playerInput.right() ? 1.0F : 0.0F;
               l11llilil1_ii1il11l111ii11iil.call073 = minecraftClient3.player.input.playerInput.jump() ? 1.0F : 0.0F;
               l11llilil1_ii1il11l111ii11iil.val005 = f28;
               l11llilil1_ii1il11l111ii11iil.float343 = this.float343;
               l11llilil1_ii1il11l111ii11iil.float344 = this.float344;
               l11llilil1_ii1il11l111ii11iil.float345 = this.float345;
               l11llilil1_ii1il11l111ii11iil.float346 = this.float346;
               this.on23(l11llilil1_ii1il11l111ii11iil);
               this.boolean138 = true;
               this.lastYaw = var1.GrimGlide();
               this.lastPitch = var1.GuiWalk();
               this.float162 = f27;
               this.float163 = f8;
               this.float164 = f9;
               this.float165 = f10;
               this.boolean188 = true;
               this.float347 = f;
               this.float348 = f1;
               this.float349 = f16;
               this.float350 = f14;
               this.double139 = vec3d2.x;
               this.double140 = vec3d2.y;
               this.double141 = vec3d2.z;
               this.double142 = vec3d.x;
               this.double143 = vec3d.y;
               this.double144 = vec3d.z;
               this.boolean189 = flag2;
               return l11llilil1_ii1il11l111ii11iil;
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

   public void on23(float var1, float var2, float var3, float var4, boolean var5) {
      float f = Math.max(Math.abs(var1) * 0.4F, 0.25F);
      float f1 = Math.max(Math.abs(var2) * 0.4F, 0.18F);
      float f2 = (float)Math.sqrt(this.CloudUserProfile(var3 / f) + this.CloudUserProfile(var4 / f1));
      f2 = Math.min(f2, 1.5F);
      if (f2 > 0.18F) {
         this.float352 = Math.min(this.float352 + 1.0F, 20.0F);
         this.float351 = Math.min(this.float351 + f2 * 0.055F, 1.0F);
      } else {
         this.float352 = Math.max(this.float352 - 2.0F, 0.0F);
         float f3 = var5 ? 0.14F : 0.08F;
         this.float351 = Math.max(this.float351 - f3, 0.0F);
      }
   }

   public PredictiveWeights ColorAnimator(float var1, float var2) {
      return this.isFinite(var1) && this.isFinite(var2) ? new PredictiveWeights(var1, var2) : null;
   }

   public PredictiveWeights on23(PredictiveWeights var1) {
      if (var1 == null) {
         return null;
      }

      float f = val014.string99();
      if (this.isFinite(f) && !(f <= 0.0F)) {
         float f1 = var1.call027 / f + this.float341;
         float f2 = var1.call074 / f + this.float342;
         if (this.isFinite(f1) && this.isFinite(f2)) {
            int i = Math.round(f1);
            int j = Math.round(f2);
            this.float341 = f1 - i;
            this.float342 = f2 - j;
            return new PredictiveWeights(i * f, j * f, f1, f2, i, j, this.float341, this.float342);
         } else {
            this.float341 = 0.0F;
            this.float342 = 0.0F;
            return null;
         }
      } else {
         this.float341 = 0.0F;
         this.float342 = 0.0F;
         return null;
      }
   }

   public PredictiveWeights on23(PredictiveTuning var1, PredictiveWeights var2) {
      if (var1 != null && var2 != null) {
         PredictiveWeights l11llilil1_l1i1illlilix = this.UiAnimation(var1, var2);
         if (l11llilil1_l1i1illlilix != null) {
            return l11llilil1_l1i1illlilix;
         }

         l11llilil1_l1i1illlilix = this.Easing(var1, var2);
         return l11llilil1_l1i1illlilix != null ? l11llilil1_l1i1illlilix : var2;
      } else {
         return var2;
      }
   }

   public PredictiveWeights UiAnimation(PredictiveTuning var1, PredictiveWeights var2) {
      if (var1 != null && var2 != null && !var1.call121 && !(var1.float385 < 3.0F)) {
         float f = var1.call197;
         float f1 = var1.call194;
         float f2 = Math.max(Math.abs(f), Math.abs(f1));
         if (this.isFinite(f2) && !(f2 < 6.0F)) {
            boolean flag = this.ItemRegistry(f, var2.call092);
            boolean flag1 = this.ItemRegistry(f1, var2.call091);
            if (!flag && !flag1) {
               this.int432 = 0;
               return null;
            }

            this.int432++;
            if (this.int432 < 2) {
               return null;
            }

            this.int432 = 0;
            float f3 = flag ? this.ItemSpec(f, 18.0F) : var2.call092;
            float f4 = flag1 ? this.ItemSpec(f1, 10.0F) : var2.call091;
            return this.TextScanner(f3, f4);
         } else {
            this.int432 = 0;
            return null;
         }
      } else {
         this.int432 = 0;
         return null;
      }
   }

   public boolean ItemRegistry(float var1, float var2) {
      if (this.isFinite(var1) && !(Math.abs(var1) < 6.0F)) {
         return this.isFinite(var2) && !(Math.abs(var2) <= 1.0F) ? Math.signum(var1) != Math.signum(var2) : true;
      } else {
         return false;
      }
   }

   public float ItemSpec(float var1, float var2) {
      float f = MathHelper.clamp(Math.abs(var1) * 0.12F, 2.0F, var2);
      return Math.copySign(Math.round(f), var1);
   }

   public PredictiveWeights Easing(PredictiveTuning var1, PredictiveWeights var2) {
      if (var1 != null && var2 != null && var1.call121 && !(var1.call188 < 6.0F)) {
         float f = var2.call092;
         float f1 = var2.call091;
         boolean flag = false;
         if (this.on23(f, var2.call166, var1.float343, 1.15F, 8.0F)) {
            f = 0.0F;
            flag = true;
         }

         if (this.on23(f1, var2.call168, var1.float344, 1.0F, 4.0F)) {
            f1 = 0.0F;
            flag = true;
         }

         return flag ? this.TextScanner(f, f1) : null;
      } else {
         return null;
      }
   }

   public boolean on23(float var1, float var2, float var3, float var4, float var5) {
      return this.isFinite(var1) && this.isFinite(var2) && this.isFinite(var3) && Math.abs(var1) == 1.0F && Math.abs(var2) <= var4 && Math.abs(var3) <= var5;
   }

   public PredictiveWeights TextScanner(float var1, float var2) {
      float f = val014.string99();
      if (this.isFinite(f) && !(f <= 0.0F) && this.isFinite(var1) && this.isFinite(var2)) {
         var1 = Math.round(var1);
         var2 = Math.round(var2);
         this.float341 = 0.0F;
         this.float342 = 0.0F;
         return new PredictiveWeights(var1 * f, var2 * f, var1, var2, var1, var2, 0.0F, 0.0F);
      } else {
         this.float341 = 0.0F;
         this.float342 = 0.0F;
         return null;
      }
   }

   public float NbtItemSpec(float var1, float var2) {
      return this.isFinite(var1) && this.isFinite(var2) && !(var2 <= 0.0F) ? Math.round(var1 / var2) : 0.0F;
   }

   public float EnchantItemSpec(float var1, float var2) {
      return this.isFinite(var1) && this.isFinite(var2) && !(var2 <= 0.0F) ? var1 / var2 : 0.0F;
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

   public LivingEntity call009() {
      LivingEntity livingentity = Aura.aura.zClass054();
      return livingentity != null && livingentity.isAlive() ? livingentity : null;
   }

   public void call268() {
      if (!val014.isLoaded() && !this.boolean187) {
         this.boolean187 = true;
         val014.scheduledExecutorService2();
      }
   }

   public void call143() {
      val014.call107();
      this.int403 = Integer.MIN_VALUE;
      this.call025 = null;
      this.zClass016Var134 = null;
      this.float341 = 0.0F;
      this.float342 = 0.0F;
      this.int432 = 0;
      this.float343 = 0.0F;
      this.float344 = 0.0F;
      this.float345 = 0.0F;
      this.float346 = 0.0F;
      this.boolean138 = false;
      this.lastYaw = 0.0F;
      this.lastPitch = 0.0F;
      this.float162 = 0.0F;
      this.float163 = 0.0F;
      this.float164 = 0.0F;
      this.float165 = 0.0F;
      this.boolean188 = false;
      this.float347 = 0.0F;
      this.float348 = 0.0F;
      this.float349 = 0.0F;
      this.float350 = 0.0F;
      this.double139 = 0.0;
      this.double140 = 0.0;
      this.double141 = 0.0;
      this.double142 = 0.0;
      this.double143 = 0.0;
      this.double144 = 0.0;
      this.boolean189 = false;
      this.float166 = 0.0F;
      this.float167 = 0.0F;
      this.float168 = 0.0F;
      this.float169 = 0.0F;
      this.float351 = 0.0F;
      this.float352 = 0.0F;
   }

   public void on23(PredictiveTuning var1) {
      if (var1 != null) {
         float f = val014.uUID5();
         float f1 = val014.string100();
         float f2 = val014.string101();
         float f3 = Math.max(0.05F, val014.identifier9());
         float f4 = this.EnchantItemSpec(var1.call189, var1.val005);
         float f5 = this.EnchantItemSpec(var1.call162, var1.val005);
         float f6 = this.EnchantItemSpec(var1.call090, var1.val005);
         float f7 = this.EnchantItemSpec(var1.call190, var1.val005);
         float f8 = this.EnchantItemSpec(var1.call161, var1.val005);
         float f9 = this.EnchantItemSpec(var1.call191, var1.val005);
         float f10 = Math.abs(this.EnchantItemSpec(var1.call192, var1.val005));
         float f11 = Math.abs(this.EnchantItemSpec(var1.call193, var1.val005));
         float f12 = Math.max(Math.abs(f4), Math.abs(f5));
         float f13 = Math.max(Math.abs(this.NbtItemSpec(var1.call122, var1.val005)), Math.abs(this.NbtItemSpec(var1.call120, var1.val005)));
         boolean flag = var1.call121;
         float f14 = var1.call187;
         float f15 = MathHelper.clamp(f / Math.max(f10, 1.0E-6F), 0.0F, 1.0F);
         float f16 = MathHelper.clamp(f1 / Math.max(f11, 1.0E-6F), 0.0F, 1.0F);
         float f17 = flag ? MathHelper.clamp(f2 / Math.max(f12, 1.0E-6F), 0.0F, 1.0F) : 1.0F;
         float f18 = MathHelper.clamp(f14 / Math.max(f3, 1.0E-6F), 0.0F, 1.0F);
         float f19 = Math.min(Math.min(f15, f16), Math.min(f17, f18));
         boolean flag1 = f19 < 0.999F;
         boolean flag2 = f14 < f3 || flag && flag1;
         float f20 = this.ItemServiceBase(f6, f7);
         float f21 = this.ItemServiceBase(f8, f9);
         float f22 = flag2 ? MathHelper.clamp(f4, -f2, f2) : f4;
         float f23 = flag2 ? MathHelper.clamp(f5, -f2, f2) : f5;
         var1.call167 = flag1 ? 1.0F : 0.0F;
         var1.call198 = f19;
         var1.call123 = (flag ? 1.0F : 0.0F) * f19;
         var1.call261 = flag2 ? 1.0F : 0.0F;
         var1.call165 = f12;
         var1.call260 = f13;
         var1.call197 = f20;
         var1.call194 = f21;
         var1.call072 = f22;
         var1.call196 = f23;
      }
   }

   public float ItemServiceBase(float var1, float var2) {
      if (var1 <= 0.0F && var2 >= 0.0F) {
         return 0.0F;
      } else {
         return Math.abs(var1) <= Math.abs(var2) ? var1 : var2;
      }
   }

   public float hypot(float var1, float var2) {
      return (float)Math.sqrt(var1 * var1 + var2 * var2);
   }

   public float CloudUserProfile(float var1) {
      return var1 * var1;
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
}
