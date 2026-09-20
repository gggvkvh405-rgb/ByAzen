package org.zenith.module.combat;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;


import java.time.Instant;
import net.minecraft.entity.EntityPose;
import com.darkmagician6.eventapi.EventTarget;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.zenith.core.CustomInput;
import org.zenith.core.MovementController;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.EventTick;
import org.zenith.event.PacketEvent;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationDelta;
import org.zenith.rotation.RotationMath;
import org.zenith.setting.BooleanSetting;

@ModuleInfo(name = "RotationRecorder", category = Category.COMBAT, description = "Records rotation data for ML training")
public final class RotationRecorder extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final RotationRecorder rotationRecorder = new RotationRecorder();
   public final BooleanSetting allowRoutingTicks = new BooleanSetting(
      "module.rotationRecorder.allowRoutingTicks", "module.rotationRecorder.allowRoutingTicks.desc", true
   );
   public static final String string76 = "rotation_dataset_v22_simpy.csv";
   public static final String string77 = "rotation_world_snapshots_v7.jsonl";
   public static final int int226 = 22;
   public static final int int227 = 1;
   public static final int int228 = 1;
   public static final int int229 = 0;
   public static final int int230 = 1;
   public static final long long125 = -1L;
   public static final int int231 = 6;
   public static final int int232 = 3;
   public static final int int233 = 5;
   public static final int int234 = 59;
   public static final int int235 = 2;
   public static final int int236 = 0;
   public static final int int237 = 1;
   public static final int int238 = 16;
   public static final int int239 = 51;
   public static final int int240 = 52;
   public static final int int241 = 53;
   public static final int int242 = 54;
   public static final int int243 = 55;
   public static final int int244 = 6;
   public static final int int245 = 7;
   public static final int int246 = 20;
   public static final int int247 = 21;
   public static final int int248 = 22;
   public static final int int249 = 23;
   public static final int int250 = 24;
   public static final int int251 = 25;
   public static final int int252 = 26;
   public static final int int253 = 39;
   public static final int int254 = 55;
   public static final int int255 = 56;
   public static final int int256 = 57;
   public static final int int257 = 58;
   public static final int int258 = 59;
   public static final int int259 = 60;
   public static final int int260 = 61;
   public static final int int261 = 62;
   public static final int int262 = 63;
   public static final int int263 = 64;
   public static final int int264 = 65;
   public static final int int265 = 66;
   public static final int int266 = 67;
   public static final int int267 = 68;
   public static final int int268 = 69;
   public static final int int269 = 70;
   public static final int int270 = 71;
   public static final int int271 = 72;
   public static final int int272 = 73;
   public static final int int273 = 74;
   public static final int int274 = 75;
   public static final int int275 = 76;
   public static final int int276 = 77;
   public static final int int277 = 78;
   public static final int int278 = 1;
   public static final int int279 = 3;
   public static final int int280 = 4;
   public static final int int281 = 5;
   public static final int int282 = 6;
   public static final int int283 = 7;
   public static final int int284 = 8;
   public static final int int285 = 9;
   public static final int int286 = 16;
   public static final int int287 = 17;
   public static final int int288 = 18;
   public static final int int289 = 19;
   public static final int int290 = 20;
   public static final int int291 = 23;
   public static final int int292 = 30;
   public static final long long126 = 128L;
   public static final float float155 = 50.0F;
   public static final float float156 = 176.0F;
   public static final float float157 = 8.0F;
   public static final float float158 = 8.0F;
   public static final Path path2 = Path.of("rotation_recordings", "rotation_dataset_v22_simpy.csv");
   public static final Path path3 = Path.of("rotation_recordings", "rotation_world_snapshots_v7.jsonl");
   public static final float float159 = 5.0F;
   public static final String[] val513 = new String[]{"mouse_degrees_per_count"};
   public static final String[] val514 = new String[]{
      "target_center_delta_yaw_count_units",
      "target_center_delta_pitch_count_units",
      "target_box_min_delta_yaw_count_units",
      "target_box_max_delta_yaw_count_units",
      "target_box_min_delta_pitch_count_units",
      "target_box_max_delta_pitch_count_units",
      "target_box_width_yaw_count_units",
      "target_box_height_pitch_count_units",
      "target_center_yaw_by_box_width",
      "target_center_pitch_by_box_height",
      "target_center_angular_distance_count_units",
      "target_center_direction_sin",
      "target_center_direction_cos",
      "aim_x_norm_in_target_box",
      "aim_y_norm_in_target_box",
      "aim_inside_target_box",
      "target_center_distance",
      "target_center_horizontal_distance",
      "target_center_vertical_distance_abs",
      "aim_inside_target_box_ticks_clamped",
      "aim_outside_target_box_ticks",
      "ticks_since_entered_target_box",
      "ticks_since_left_target_box",
      "target_center_delta_yaw_change_count_units",
      "target_center_delta_pitch_change_count_units",
      "target_center_distance_change",
      "yaw_target_drift_count_units",
      "pitch_target_drift_count_units",
      "sim_2t_target_center_delta_yaw_count_units",
      "sim_2t_target_center_delta_pitch_count_units",
      "target_motion_x",
      "target_motion_y",
      "target_motion_z",
      "yaw_mouse_count",
      "pitch_mouse_count",
      "yaw_velocity_mouse_counts",
      "pitch_velocity_mouse_counts",
      "yaw_acceleration_mouse_counts",
      "pitch_acceleration_mouse_counts",
      "yaw_jerk_mouse_counts",
      "pitch_jerk_mouse_counts",
      "mouse_dx_nonzero_streak_ticks",
      "mouse_dy_nonzero_streak_ticks",
      "yaw_sign_flip_ticks_ago",
      "pitch_sign_flip_ticks_ago",
      "yaw_mouse_static_help",
      "pitch_mouse_static_help",
      "yaw_current_to_peak_abs_ratio",
      "pitch_current_to_peak_abs_ratio",
      "yaw_peak_mouse_count_abs",
      "pitch_peak_mouse_count_abs",
      "movement_input_forward",
      "movement_input_backward",
      "movement_input_left",
      "movement_input_right",
      "movement_input_jump",
      "player_motion_x",
      "player_motion_y",
      "player_motion_z"
   };
   public static final String[] val311 = new String[]{
      "player_pos_x",
      "player_pos_y",
      "player_pos_z",
      "player_eye_x",
      "player_eye_y",
      "player_eye_z",
      "player_yaw",
      "player_pitch",
      "player_velocity_x",
      "player_velocity_y",
      "player_velocity_z",
      "player_prev_pos_x",
      "player_prev_pos_y",
      "player_prev_pos_z",
      "player_bbox_min_x",
      "player_bbox_min_y",
      "player_bbox_min_z",
      "player_bbox_max_x",
      "player_bbox_max_y",
      "player_bbox_max_z",
      "input_forward_raw",
      "input_backward_raw",
      "input_left_raw",
      "input_right_raw",
      "input_jump_raw",
      "input_sneak_raw",
      "input_sprint_raw",
      "state_on_ground",
      "state_horizontal_collision",
      "state_vertical_collision",
      "state_touching_water",
      "state_swimming",
      "state_submerged_water",
      "state_fall_flying",
      "state_in_sneaking_pose",
      "state_crawling",
      "state_pose_id",
      "state_fall_distance",
      "state_jumping_cooldown",
      "state_is_jumping",
      "state_has_vehicle",
      "state_flying",
      "state_spectator",
      "target_box_min_x",
      "target_box_min_y",
      "target_box_min_z",
      "target_box_max_x",
      "target_box_max_y",
      "target_box_max_z",
      "target_center_x",
      "target_center_y",
      "target_center_z",
      "target_motion_raw_x",
      "target_motion_raw_y",
      "target_motion_raw_z",
      "base_sim_ok",
      "base_sim_yaw_plus_ok",
      "base_sim_yaw_minus_ok",
      "base_sim_motion_x",
      "base_sim_motion_y",
      "base_sim_motion_z",
      "base_sim_pos_x",
      "base_sim_pos_y",
      "base_sim_pos_z",
      "base_sim_velocity_x",
      "base_sim_velocity_y",
      "base_sim_velocity_z",
      "base_sim_on_ground",
      "base_sim_horizontal_collision",
      "base_sim_vertical_collision",
      "base_sim_yaw_plus_motion_x",
      "base_sim_yaw_plus_motion_y",
      "base_sim_yaw_plus_motion_z",
      "base_sim_yaw_minus_motion_x",
      "base_sim_yaw_minus_motion_y",
      "base_sim_yaw_minus_motion_z",
      "base_sim_dmotion_dyaw_x",
      "base_sim_dmotion_dyaw_y",
      "base_sim_dmotion_dyaw_z",
      "sim_yaw_delta_degrees"
   };
   public static final String[] val192 = new String[]{
      "recorder_schema_version",
      "simpy_supported_v0",
      "simpy_schema_version",
      "simpy_world_mode",
      "state_sprinting",
      "state_allow_flying",
      "state_no_gravity",
      "state_no_drag",
      "state_using_item",
      "state_hunger_food_level",
      "state_step_height",
      "attr_movement_speed",
      "attr_gravity",
      "attr_jump_strength",
      "attr_sneaking_speed",
      "attr_water_movement_efficiency",
      "effect_slow_falling",
      "effect_levitation",
      "effect_jump_boost",
      "effect_blindness",
      "effect_dolphins_grace",
      "effect_levitation_amp",
      "effect_jump_boost_amp",
      "snapshot_id",
      "snapshot_origin_x",
      "snapshot_origin_y",
      "snapshot_origin_z",
      "snapshot_size_x",
      "snapshot_size_y",
      "snapshot_size_z",
      "snapshot_version"
   };
   public static final String[] val420 = new String[]{"dirty_physics_flag"};
   public static final String[] val515 = new String[]{"target_yaw_mouse_count", "target_pitch_mouse_count"};
   public BufferedWriter bufferedWriter;
   public BufferedWriter bufferedWriter2;
   public boolean boolean136 = false;
   public boolean boolean137 = false;
   public long long127 = 0L;
   public long long128 = 0L;
   public long long129 = 1L;
   public int int293 = 0;
   public LivingEntity livingEntity2 = null;
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
   public RotationRecorder.Frame rotationRecorderVar143 = null;
   public RotationRecorder.Writer rotationRecorderVar159 = null;

   @Override
   public void onEnable() {
      this.botClient3();
      this.list113();
      this.call277();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.path14();
      this.call219();
      this.int209();
      this.botClient3();
      StyledTextBuilder.RefreshCacheEvent("RotationRecorder: saved " + this.int293 + " rows");
      super.onDisable();
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.Arrows() && var1.ItemScroller() instanceof PlayerPositionLookS2CPacket) {
         this.path14();
         this.botClient3();
      }
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.isPaused()) {
         this.call221();
      } else {
         LivingEntity livingentity = this.int466();
         Box box = this.ItemRegistry(livingentity);
         if (box != null && !minecraftClient3.player.isSpectator()) {
            if (livingentity != this.livingEntity2) {
               this.call221();
               this.livingEntity2 = livingentity;
               this.rotationRecorderVar159 = new RotationRecorder.Writer(this);
            }

            float f = minecraftClient3.player.getYaw();
            float f1 = minecraftClient3.player.getPitch();
            Rotation ililiiili1ll1li11 = new Rotation(f, f1);
            if (!this.boolean138) {
               this.float160 = f;
               this.float161 = f1;
               this.boolean138 = true;
               this.rotationRecorderVar143 = null;
            } else {
               float f2 = MathHelper.wrapDegrees(f - this.float160);
               float f3 = f1 - this.float161;
               RotationRecorder.Frame illliiil11il11iiili1i11i1ii1_l1i1illlili = this.on23(
                  ililiiili1ll1li11, minecraftClient3.player.getEyePos(), livingentity, box, f2, f3
               );
               if (illliiil11il11iiili1i11i1ii1_l1i1illlili == null) {
                  this.path14();
                  this.rotationRecorderVar143 = null;
                  this.float160 = f;
                  this.float161 = f1;
                  this.boolean138 = true;
               } else {
                  if (this.rotationRecorderVar143 != null) {
                     float f4 = this.on23(this.rotationRecorderVar143);
                     float f5 = this.UiAnimation(this.rotationRecorderVar143);
                     RotationRecorder.ExportTask illliiil11il11iiili1i11i1ii1_liil11l111liil1ll = this.on23(
                        this.rotationRecorderVar143, illliiil11il11iiili1i11i1ii1_l1i1illlili, f4, f5
                     );
                     RotationRecorder.Analyzer illliiil11il11iiili1i11i1ii1_illi1l1l1 = RotationRecorder.Analyzer.on23(
                        this.rotationRecorderVar143,
                        illliiil11il11iiili1i11i1ii1_l1i1illlili,
                        illliiil11il11iiili1i11i1ii1_liil11l111liil1ll,
                        f2,
                        f3,
                        !this.allowRoutingTicks.isEnabled()
                     );
                     if (this.rotationRecorderVar159 != null) {
                        this.rotationRecorderVar159.Easing(illliiil11il11iiili1i11i1ii1_illi1l1l1);
                     } else {
                        this.on23(illliiil11il11iiili1i11i1ii1_illi1l1l1);
                     }
                  }

                  this.rotationRecorderVar143 = illliiil11il11iiili1i11i1ii1_l1i1illlili;
                  this.float160 = f;
                  this.float161 = f1;
               }
            }
         } else {
            this.call221();
         }
      }
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
      Vec3d vec3d = new Vec3d(
         (var4.minX + var4.maxX) * 0.5, (var4.minY + var4.maxY) * 0.5, (var4.minZ + var4.maxZ) * 0.5
      );
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
                  f8 = SimpleItemBuilder(f8, f);
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

            f = SimpleItemBuilder(f, f);
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
            float f16 = this.rotationRecorderVar143 != null ? this.FileLogger(this.rotationRecorderVar143.val067[0], this.rotationRecorderVar143.float76) : f;
            float f17 = this.rotationRecorderVar143 != null ? this.FileLogger(this.rotationRecorderVar143.val067[1], this.rotationRecorderVar143.float76) : f1;
            float f18 = this.rotationRecorderVar143 != null ? this.rotationRecorderVar143.val067[16] : f50;
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
            if (this.rotationRecorderVar143 != null) {
               float f36 = this.rotationRecorderVar143.val067[0];
               float f37 = this.rotationRecorderVar143.val067[1];
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

   public LivingEntity int466() {
      LivingEntity livingentity = AimAssist.aimAssist.zClass054();
      return livingentity != null && livingentity.isAlive() ? livingentity : null;
   }

   public Vec3d ColorAnimator(Vec3d var1) {
      if (this.rotationRecorderVar143 != null && var1 != null) {
         Vec3d vec3d = var1.subtract(this.rotationRecorderVar143.vec3d18);
         if (this.Easing(vec3d) && vec3d.lengthSquared() > 1.0E-10) {
            return vec3d;
         }
      }

      Vec3d vec3d1 = minecraftClient3.player.getVelocity();
      return this.Easing(vec3d1) ? vec3d1 : Vec3d.ZERO;
   }

   public Vec3d on23(LivingEntity var1, Box var2) {
      if (this.rotationRecorderVar143 != null && var2 != null && this.rotationRecorderVar143.box6 != null) {
         Vec3d vec3d = this.on23(var2).subtract(this.on23(this.rotationRecorderVar143.box6));
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

   public Box ItemRegistry(LivingEntity var1) {
      return var1 == null ? null : var1.getBoundingBox();
   }

   public void on23(RotationRecorder.Analyzer var1) {
      this.rotationRecorderVar159 = new RotationRecorder.Writer(this);
      this.rotationRecorderVar159.Easing(var1);
   }

   public void string134() {
      if (this.rotationRecorderVar159 != null) {
         this.rotationRecorderVar159.flush();
      }
   }

   public void path14() {
      if (this.rotationRecorderVar159 != null) {
         this.rotationRecorderVar159.float215();
         this.string134();
      }
   }

   public void call221() {
      this.path14();
      this.botClient3();
   }

   public void botClient3() {
      this.livingEntity2 = null;
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
      this.rotationRecorderVar143 = null;
      this.rotationRecorderVar159 = null;
   }

   public void list113() {
      this.boolean136 = false;
      this.long127 = 0L;
      this.int293 = 0;

      try {
         Files.createDirectories(path2.getParent());
         boolean flag = !Files.exists(path2) || Files.size(path2) == 0L;
         this.bufferedWriter = Files.newBufferedWriter(path2, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
         this.BotFeaturesDto(flag);
      } catch (IOException ioexception) {
         this.bufferedWriter = null;
         this.boolean136 = true;
         System.err.println("[RotationRecorder] Failed to open dataset writer: " + ioexception.getMessage());
      }
   }

   public void call219() {
      if (this.bufferedWriter != null) {
         try {
            this.bufferedWriter.flush();
            this.bufferedWriter.close();
         } catch (IOException var5) {
         } finally {
            this.bufferedWriter = null;
         }
      }
   }

   public void call277() {
      this.boolean137 = false;
      this.long128 = 0L;

      try {
         Files.createDirectories(path3.getParent());
         this.int208();
         this.bufferedWriter2 = Files.newBufferedWriter(path3, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
      } catch (IOException ioexception) {
         this.bufferedWriter2 = null;
         this.boolean137 = true;
         System.err.println("[RotationRecorder] Failed to open snapshot writer: " + ioexception.getMessage());
      }
   }

   public void int208() {
      this.long129 = 1L;
      if (Files.exists(path3)) {
         long i = 0L;

         try (BufferedReader bufferedreader = Files.newBufferedReader(path3, StandardCharsets.UTF_8)) {
            String s;
            while ((s = bufferedreader.readLine()) != null) {
               long j = this.EventTickEnd(s);
               if (j > i) {
                  i = j;
               }
            }

            this.long129 = i + 1L;
         } catch (IOException ioexception) {
            System.err.println("[RotationRecorder] Failed to scan existing snapshots; starting from snapshot_id=1: " + ioexception.getMessage());
            this.long129 = 1L;
         }
      }
   }

   public long EventTickEnd(String var1) {
      int i = var1.indexOf("\"snapshot_id\"");
      if (i < 0) {
         return -1L;
      }

      int j = var1.indexOf(58, i);
      if (j < 0) {
         return -1L;
      }

      int k = j + 1;

      while (k < var1.length() && Character.isWhitespace(var1.charAt(k))) {
         k++;
      }

      int l = k;
      if (k < var1.length() && var1.charAt(k) == '-') {
         k++;
      }

      while (k < var1.length() && Character.isDigit(var1.charAt(k))) {
         k++;
      }

      if (k <= l) {
         return -1L;
      }

      try {
         return Long.parseLong(var1.substring(l, k));
      } catch (NumberFormatException numberformatexception) {
         return -1L;
      }
   }

   public void int209() {
      if (this.bufferedWriter2 != null) {
         try {
            this.bufferedWriter2.flush();
            this.bufferedWriter2.close();
         } catch (IOException var5) {
         } finally {
            this.bufferedWriter2 = null;
         }
      }
   }

   public void on23(long var1, RotationRecorder.Analyzer var3) {
      if (this.bufferedWriter != null && !this.boolean136 && var3 != null && var3.isValid()) {
         try {
            StringBuilder stringbuilder = new StringBuilder(16384);
            stringbuilder.append(var1).append(',');
            stringbuilder.append(var3.long93).append(',');
            stringbuilder.append(var3.int129);
            stringbuilder.append(',').append(var3.float74);
            this.on23(stringbuilder, var3.val136);
            this.on23(stringbuilder, var3.val137);
            this.on23(stringbuilder, var3.val138);
            this.on23(stringbuilder, var3.val139);
            this.on23(stringbuilder, var3.val035);
            this.bufferedWriter.write(stringbuilder.toString());
            this.bufferedWriter.newLine();
            this.long127++;
            this.int293++;
            if (this.long127 % 128L == 0L) {
               this.bufferedWriter.flush();
            }
         } catch (IOException ioexception) {
            this.boolean136 = true;
         }
      }
   }

   public void on23(StringBuilder var1, float[] var2) {
      if (var2 != null) {
         for (float f : var2) {
            var1.append(',').append(f);
         }
      }
   }

   public void BotFeaturesDto(boolean var1) throws IOException {
      if (var1 && this.bufferedWriter != null) {
         StringBuilder stringbuilder = new StringBuilder(16384);
         stringbuilder.append("session_id,timestamp_ms,player_age");
         this.on23(stringbuilder, val513);
         this.on23(stringbuilder, val514);
         this.on23(stringbuilder, val311);
         this.on23(stringbuilder, val192);
         this.on23(stringbuilder, val420);
         this.on23(stringbuilder, val515);
         this.bufferedWriter.write(stringbuilder.toString());
         this.bufferedWriter.newLine();
         this.bufferedWriter.flush();
      }
   }

   public void on23(StringBuilder var1, String[] var2) {
      if (var2 != null) {
         for (String s : var2) {
            var1.append(',').append(s);
         }
      }
   }

   public static float SimpleItemBuilder(float var0, float var1) {
      while (var0 - var1 > 180.0F) {
         var0 -= 360.0F;
      }

      while (var0 - var1 < -180.0F) {
         var0 += 360.0F;
      }

      return var0;
   }

   public boolean StringCodec(float[] var1) {
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

   public boolean UiAnimation(RotationRecorder.Analyzer var1) {
      return var1 != null && var1.val035 != null && var1.val035.length == 2 ? var1.val035[0] == 0.0F && var1.val035[1] == 0.0F : false;
   }

   public RotationRecorder.Frame on23(Rotation var1, Vec3d var2, LivingEntity var3, Box var4, float var5, float var6) {
      Vec3d vec3d = minecraftClient3.player.getEntityPos();
      Vec3d vec3d1 = this.on23(var3, var4);
      Vec3d vec3d2 = this.ColorAnimator(vec3d);
      float f = Rotation.logger2();
      if (this.isFinite(f) && !(f <= 0.0F)) {
         float[] afloat = this.on23(var1, var2, var3, var4, var5, var6, vec3d1, vec3d2, f);
         if (afloat != null && afloat.length == 59 && this.StringCodec(afloat)) {
            float f1 = var1.GrimGlide();
            float f2 = var1.GuiWalk();
            RotationRecorder.Sample illliiil11il11iiili1i11i1ii1_Var160 = this.BotFeaturesDto(f1, f2);
            RotationRecorder.Sample illliiil11il11iiili1i11i1ii1_l1lll11l1l1 = this.BotFeaturesDto(f1 + 5.0F, f2);
            RotationRecorder.Sample illliiil11il11iiili1i11i1ii1_l1lll11l1l2 = this.BotFeaturesDto(f1 - 5.0F, f2);
            Vec3d vec3d3 = minecraftClient3.player.getEyePos();
            Vec3d vec3d4 = minecraftClient3.player.getVelocity();
            Box box = minecraftClient3.player.getBoundingBox();
            PlayerInput playerinput = this.UiAnimation(minecraftClient3.player.input.playerInput);
            Vec3d vec3d5 = this.rotationRecorderVar143 == null ? vec3d : this.rotationRecorderVar143.vec3d18;
            Vec3d vec3d6 = this.on23(var4);
            boolean flag = minecraftClient3.player.isOnGround();
            boolean flag1 = this.call081();
            RotationRecorder.Session illliiil11il11iiili1i11i1ii1_l1iil11li = this.Easing(box);
            float[] afloat1 = this.on23(minecraftClient3.player.isSprinting(), box, illliiil11il11iiili1i11i1ii1_l1iil11li);
            if (afloat1 != null && afloat1.length == val192.length && this.StringCodec(afloat1)) {
               float[] afloat2 = new float[]{
                  (float)vec3d.x,
                  (float)vec3d.y,
                  (float)vec3d.z,
                  (float)vec3d3.x,
                  (float)vec3d3.y,
                  (float)vec3d3.z,
                  f1,
                  f2,
                  (float)vec3d4.x,
                  (float)vec3d4.y,
                  (float)vec3d4.z,
                  (float)vec3d5.x,
                  (float)vec3d5.y,
                  (float)vec3d5.z,
                  (float)box.minX,
                  (float)box.minY,
                  (float)box.minZ,
                  (float)box.maxX,
                  (float)box.maxY,
                  (float)box.maxZ,
                  playerinput.forward() ? 1.0F : 0.0F,
                  playerinput.backward() ? 1.0F : 0.0F,
                  playerinput.left() ? 1.0F : 0.0F,
                  playerinput.right() ? 1.0F : 0.0F,
                  playerinput.jump() ? 1.0F : 0.0F,
                  playerinput.sneak() ? 1.0F : 0.0F,
                  playerinput.sprint() ? 1.0F : 0.0F,
                  flag ? 1.0F : 0.0F,
                  minecraftClient3.player.horizontalCollision ? 1.0F : 0.0F,
                  minecraftClient3.player.verticalCollision ? 1.0F : 0.0F,
                  minecraftClient3.player.isTouchingWater() ? 1.0F : 0.0F,
                  minecraftClient3.player.isSwimming() ? 1.0F : 0.0F,
                  minecraftClient3.player.isSubmergedInWater() ? 1.0F : 0.0F,
                  minecraftClient3.player.isGliding() ? 1.0F : 0.0F,
                  minecraftClient3.player.isInSneakingPose() ? 1.0F : 0.0F,
                  minecraftClient3.player.isCrawling() ? 1.0F : 0.0F,
                  minecraftClient3.player.getPose() == null ? -1.0F : minecraftClient3.player.getPose().ordinal(),
                  (float)minecraftClient3.player.fallDistance,
                  minecraftClient3.player.jumpingCooldown,
                  minecraftClient3.player.jumping ? 1.0F : 0.0F,
                  minecraftClient3.player.hasVehicle() ? 1.0F : 0.0F,
                  minecraftClient3.player.getAbilities().flying ? 1.0F : 0.0F,
                  minecraftClient3.player.isSpectator() ? 1.0F : 0.0F,
                  (float)var4.minX,
                  (float)var4.minY,
                  (float)var4.minZ,
                  (float)var4.maxX,
                  (float)var4.maxY,
                  (float)var4.maxZ,
                  (float)vec3d6.x,
                  (float)vec3d6.y,
                  (float)vec3d6.z,
                  (float)vec3d1.x,
                  (float)vec3d1.y,
                  (float)vec3d1.z,
                  illliiil11il11iiili1i11i1ii1_Var160.boolean101 ? 1.0F : 0.0F,
                  illliiil11il11iiili1i11i1ii1_l1lll11l1l1.boolean101 ? 1.0F : 0.0F,
                  illliiil11il11iiili1i11i1ii1_l1lll11l1l2.boolean101 ? 1.0F : 0.0F,
                  (float)illliiil11il11iiili1i11i1ii1_Var160.vec3d20.x,
                  (float)illliiil11il11iiili1i11i1ii1_Var160.vec3d20.y,
                  (float)illliiil11il11iiili1i11i1ii1_Var160.vec3d20.z,
                  (float)illliiil11il11iiili1i11i1ii1_Var160.vec3d21.x,
                  (float)illliiil11il11iiili1i11i1ii1_Var160.vec3d21.y,
                  (float)illliiil11il11iiili1i11i1ii1_Var160.vec3d21.z,
                  (float)illliiil11il11iiili1i11i1ii1_Var160.vec3d22.x,
                  (float)illliiil11il11iiili1i11i1ii1_Var160.vec3d22.y,
                  (float)illliiil11il11iiili1i11i1ii1_Var160.vec3d22.z,
                  illliiil11il11iiili1i11i1ii1_Var160.boolean102 ? 1.0F : 0.0F,
                  illliiil11il11iiili1i11i1ii1_Var160.boolean103 ? 1.0F : 0.0F,
                  illliiil11il11iiili1i11i1ii1_Var160.boolean104 ? 1.0F : 0.0F,
                  (float)illliiil11il11iiili1i11i1ii1_l1lll11l1l1.vec3d20.x,
                  (float)illliiil11il11iiili1i11i1ii1_l1lll11l1l1.vec3d20.y,
                  (float)illliiil11il11iiili1i11i1ii1_l1lll11l1l1.vec3d20.z,
                  (float)illliiil11il11iiili1i11i1ii1_l1lll11l1l2.vec3d20.x,
                  (float)illliiil11il11iiili1i11i1ii1_l1lll11l1l2.vec3d20.y,
                  (float)illliiil11il11iiili1i11i1ii1_l1lll11l1l2.vec3d20.z,
                  (float)((illliiil11il11iiili1i11i1ii1_l1lll11l1l1.vec3d20.x - illliiil11il11iiili1i11i1ii1_l1lll11l1l2.vec3d20.x) / 10.0),
                  (float)((illliiil11il11iiili1i11i1ii1_l1lll11l1l1.vec3d20.y - illliiil11il11iiili1i11i1ii1_l1lll11l1l2.vec3d20.y) / 10.0),
                  (float)((illliiil11il11iiili1i11i1ii1_l1lll11l1l1.vec3d20.z - illliiil11il11iiili1i11i1ii1_l1lll11l1l2.vec3d20.z) / 10.0),
                  5.0F
               };
               return afloat2.length == val311.length && this.StringCodec(afloat2)
                  ? new RotationRecorder.Frame(
                     afloat,
                     afloat2,
                     afloat1,
                     vec3d,
                     vec3d4,
                     box,
                     playerinput,
                     var4,
                     flag,
                     flag1,
                     minecraftClient3.player.isSprinting(),
                     (float)minecraftClient3.player.fallDistance,
                     minecraftClient3.player.jumpingCooldown,
                     minecraftClient3.player.jumping,
                     minecraftClient3.player.isGliding(),
                     minecraftClient3.player.horizontalCollision,
                     minecraftClient3.player.verticalCollision,
                     minecraftClient3.player.isTouchingWater(),
                     minecraftClient3.player.isSwimming(),
                     minecraftClient3.player.isSubmergedInWater(),
                     minecraftClient3.player.getPose(),
                     minecraftClient3.player.isInSneakingPose(),
                     minecraftClient3.player.isCrawling(),
                     f,
                     System.currentTimeMillis(),
                     minecraftClient3.player.age
                  )
                  : null;
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

   public float[] on23(boolean var1, Box var2, RotationRecorder.Session var3) {
      StatusEffectInstance statuseffectinstance = this.UiAnimation(StatusEffects.LEVITATION);
      StatusEffectInstance statuseffectinstance1 = this.UiAnimation(StatusEffects.JUMP_BOOST);
      boolean flag = this.Easing(StatusEffects.SLOW_FALLING);
      boolean flag1 = this.Easing(StatusEffects.BLINDNESS);
      boolean flag2 = this.Easing(StatusEffects.DOLPHINS_GRACE);
      int i = var3 == null ? (var2 == null ? 0 : MathHelper.floor(var2.minX)) : var3.int132;
      int j = var3 == null ? (var2 == null ? 0 : MathHelper.floor(var2.minY)) : var3.int133;
      int k = var3 == null ? (var2 == null ? 0 : MathHelper.floor(var2.minZ)) : var3.int134;
      int l = var3 == null ? 0 : var3.int135;
      int i1 = var3 == null ? 0 : var3.int136;
      int j1 = var3 == null ? 0 : var3.int137;
      long k1 = var3 == null ? -1L : var3.long95;
      int l1 = var3 == null ? 0 : var3.int138;
      int i2 = var3 != null && k1 >= 0L ? 1 : 0;
      return new float[]{
         22.0F,
         0.0F,
         1.0F,
         i2,
         var1 ? 1.0F : 0.0F,
         minecraftClient3.player.getAbilities().allowFlying ? 1.0F : 0.0F,
         minecraftClient3.player.hasNoGravity() ? 1.0F : 0.0F,
         minecraftClient3.player.hasNoDrag() ? 1.0F : 0.0F,
         minecraftClient3.player.isUsingItem() ? 1.0F : 0.0F,
         this.call051(),
         minecraftClient3.player.getStepHeight(),
         this.on23(EntityAttributes.MOVEMENT_SPEED, 0.1F),
         this.on23(EntityAttributes.GRAVITY, 0.08F),
         this.on23(EntityAttributes.JUMP_STRENGTH, 0.42F),
         this.on23(EntityAttributes.SNEAKING_SPEED, 0.3F),
         this.on23(EntityAttributes.WATER_MOVEMENT_EFFICIENCY, 0.0F),
         flag ? 1.0F : 0.0F,
         statuseffectinstance != null ? 1.0F : 0.0F,
         statuseffectinstance1 != null ? 1.0F : 0.0F,
         flag1 ? 1.0F : 0.0F,
         flag2 ? 1.0F : 0.0F,
         statuseffectinstance == null ? -1.0F : statuseffectinstance.getAmplifier(),
         statuseffectinstance1 == null ? -1.0F : statuseffectinstance1.getAmplifier(),
         (float)k1,
         i,
         j,
         k,
         l,
         i1,
         j1,
         l1
      };
   }

   public RotationRecorder.Session Easing(Box var1) {
      if (var1 != null && this.bufferedWriter2 != null && !this.boolean137) {
         int i = MathHelper.floor(var1.minX) - 6;
         int j = MathHelper.floor(var1.minY) - 3;
         int k = MathHelper.floor(var1.minZ) - 6;
         int l = MathHelper.floor(var1.maxX) + 6;
         int i1 = MathHelper.floor(var1.maxY) + 5;
         int j1 = MathHelper.floor(var1.maxZ) + 6;
         int k1 = l - i + 1;
         int l1 = i1 - j + 1;
         int i2 = j1 - k + 1;
         if (k1 > 0 && l1 > 0 && i2 > 0) {
            long j2 = this.long129++;
            LinkedHashMap linkedhashmap = new LinkedHashMap();
            ArrayList arraylist = new ArrayList();
            ArrayList arraylist1 = new ArrayList(k1 * l1 * i2);
            linkedhashmap.put("minecraft:air|empty", 0);
            arraylist.add(this.call109());
            Mutable mutable = new Mutable();

            for (int k2 = j; k2 <= i1; k2++) {
               for (int l2 = k; l2 <= j1; l2++) {
                  for (int i3 = i; i3 <= l; i3++) {
                     mutable.set(i3, k2, l2);
                     BlockState blockstate = minecraftClient3.world.getBlockState(mutable);
                     FluidState fluidstate = minecraftClient3.world.getFluidState(mutable);
                     String s = this.on23(blockstate, fluidstate);
                     Integer integer = (Integer)linkedhashmap.get(s);
                     if (integer == null) {
                        integer = linkedhashmap.size();
                        linkedhashmap.put(s, integer);
                        arraylist.add(this.on23(blockstate, fluidstate, mutable));
                     }

                     arraylist1.add(integer);
                  }
               }
            }

            try {
               StringBuilder stringbuilder = new StringBuilder(32768);
               stringbuilder.append('{');
               stringbuilder.append("\"snapshot_id\":").append(j2).append(',');
               stringbuilder.append("\"snapshot_version\":").append(1).append(',');
               stringbuilder.append("\"schema\":\"rotation_world_snapshot_v7\",");
               stringbuilder.append("\"session_hint\":").append(this.rotationRecorderVar159 == null ? 0L : this.rotationRecorderVar159.long92).append(',');
               stringbuilder.append("\"player_age\":").append(minecraftClient3.player.age).append(',');
               this.on23(stringbuilder, "origin", i, j, k).append(',');
               this.on23(stringbuilder, "size", k1, l1, i2).append(',');
               stringbuilder.append("\"palette\":[");

               for (int j3 = 0; j3 < arraylist.size(); j3++) {
                  if (j3 > 0) {
                     stringbuilder.append(',');
                  }

                  stringbuilder.append((String)arraylist.get(j3));
               }

               stringbuilder.append("],\"blocks_rle\":");
               this.on23(stringbuilder, arraylist1);
               stringbuilder.append('}');
               this.bufferedWriter2.write(stringbuilder.toString());
               this.bufferedWriter2.newLine();
               this.long128++;
               if (this.long128 % 128L == 0L) {
                  this.bufferedWriter2.flush();
               }
            } catch (Throwable throwable) {
               this.boolean137 = true;
               return null;
            }

            return new RotationRecorder.Session(j2, i, j, k, k1, l1, i2, 1);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public String on23(BlockState var1, FluidState var2) {
      return (var1 == null || var1.isAir()) && (var2 == null || var2.isEmpty()) ? "minecraft:air|empty" : var1 + "|" + var2;
   }

   public String call109() {
      return "{\"name\":\"minecraft:air\",\"state\":\"minecraft:air\",\"slipperiness\":0.6,\"velocity_multiplier\":1.0,\"jump_velocity_multiplier\":1.0,\"fluid_type\":\"none\",\"fluid_height\":0.0,\"fluid_velocity\":[0.0,0.0,0.0],\"climbable\":false,\"powder_snow\":false,\"collision_boxes\":[]}";
   }

   public String on23(BlockState var1, FluidState var2, BlockPos var3) {
      String s = var1 == null ? "minecraft:air" : String.valueOf(Registries.BLOCK.getId(var1.getBlock()));
      String s1 = var1 == null ? s : var1.toString();
      float f = var1 == null ? 0.6F : var1.getBlock().getSlipperiness();
      float f1 = var1 == null ? 1.0F : var1.getBlock().getVelocityMultiplier();
      float f2 = var1 == null ? 1.0F : var1.getBlock().getJumpVelocityMultiplier();
      boolean flag = var1 != null && var1.isIn(BlockTags.CLIMBABLE);
      boolean flag1 = s.equals("minecraft:powder_snow");
      String s2 = "none";
      float f3 = 0.0F;
      Vec3d vec3d = Vec3d.ZERO;
      if (var2 != null && !var2.isEmpty()) {
         if (var2.isIn(FluidTags.WATER)) {
            s2 = "water";
         } else if (var2.isIn(FluidTags.LAVA)) {
            s2 = "lava";
         } else {
            s2 = "other";
         }

         f3 = var2.getHeight(minecraftClient3.world, var3);
         vec3d = var2.getVelocity(minecraftClient3.world, var3);
      }

      StringBuilder stringbuilder = new StringBuilder(1024);
      stringbuilder.append('{');
      this.on23(stringbuilder, "name", s).append(',');
      this.on23(stringbuilder, "state", s1).append(',');
      stringbuilder.append("\"slipperiness\":").append(f).append(',');
      stringbuilder.append("\"velocity_multiplier\":").append(f1).append(',');
      stringbuilder.append("\"jump_velocity_multiplier\":").append(f2).append(',');
      this.on23(stringbuilder, "fluid_type", s2).append(',');
      stringbuilder.append("\"fluid_height\":").append(f3).append(',');
      stringbuilder.append("\"fluid_velocity\":[")
         .append((float)vec3d.x)
         .append(',')
         .append((float)vec3d.y)
         .append(',')
         .append((float)vec3d.z)
         .append("],");
      stringbuilder.append("\"climbable\":").append(flag).append(',');
      stringbuilder.append("\"powder_snow\":").append(flag1).append(',');
      stringbuilder.append("\"collision_boxes\":");
      this.on23(stringbuilder, var1, var3);
      stringbuilder.append('}');
      return stringbuilder.toString();
   }

   public void on23(StringBuilder var1, BlockState var2, BlockPos var3) {
      var1.append('[');
      if (var2 != null) {
         try {
            VoxelShape voxelshape = var2.getCollisionShape(minecraftClient3.world, var3);
            List<Box> list = voxelshape.getBoundingBoxes();

            for (int i = 0; i < list.size(); i++) {
               if (i > 0) {
                  var1.append(',');
               }

               Box box = list.get(i);
               var1.append('[')
                  .append((float)box.minX)
                  .append(',')
                  .append((float)box.minY)
                  .append(',')
                  .append((float)box.minZ)
                  .append(',')
                  .append((float)box.maxX)
                  .append(',')
                  .append((float)box.maxY)
                  .append(',')
                  .append((float)box.maxZ)
                  .append(']');
            }
         } catch (Throwable var8) {
         }
      }

      var1.append(']');
   }

   public StringBuilder on23(StringBuilder var1, String var2, String var3) {
      var1.append('"').append(var2).append("\":\"").append(this.EventGetBasicProjectionMatrixHook(var3)).append('"');
      return var1;
   }

   public String EventGetBasicProjectionMatrixHook(String var1) {
      if (var1 == null) {
         return "";
      }

      StringBuilder stringbuilder = new StringBuilder(var1.length() + 16);

      for (int i = 0; i < var1.length(); i++) {
         char c0 = var1.charAt(i);
         if (c0 == '\\' || c0 == '"') {
            stringbuilder.append('\\').append(c0);
         } else if (c0 == '\n') {
            stringbuilder.append("\\n");
         } else if (c0 == '\r') {
            stringbuilder.append("\\r");
         } else if (c0 == '\t') {
            stringbuilder.append("\\t");
         } else {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   public StringBuilder on23(StringBuilder var1, String var2, int var3, int var4, int var5) {
      var1.append('"').append(var2).append("\":[").append(var3).append(',').append(var4).append(',').append(var5).append(']');
      return var1;
   }

   public void on23(StringBuilder var1, List<Integer> var2) {
      var1.append('[');
      if (!var2.isEmpty()) {
         int i = var2.get(0);
         int j = 1;
         boolean flag = true;

         for (int k = 1; k < var2.size(); k++) {
            int l = var2.get(k);
            if (l == i) {
               j++;
            } else {
               if (!flag) {
                  var1.append(',');
               }

               var1.append('[').append(i).append(',').append(j).append(']');
               flag = false;
               i = l;
               j = 1;
            }
         }

         if (!flag) {
            var1.append(',');
         }

         var1.append('[').append(i).append(',').append(j).append(']');
      }

      var1.append(']');
   }

   public StatusEffectInstance UiAnimation(RegistryEntry<StatusEffect> var1) {
      if (var1 == null) {
         return null;
      }

      try {
         StatusEffectInstance statuseffectinstance = minecraftClient3.player.getStatusEffect(var1);
         return statuseffectinstance != null && statuseffectinstance.getDuration() > 0 ? statuseffectinstance : null;
      } catch (Throwable throwable) {
         return null;
      }
   }

   public boolean Easing(RegistryEntry<StatusEffect> var1) {
      return this.UiAnimation(var1) != null;
   }

   public float on23(RegistryEntry<EntityAttribute> var1, float var2) {
      if (var1 == null) {
         return var2;
      }

      try {
         double d0 = minecraftClient3.player.getAttributes().getValue(var1);
         return Double.isFinite(d0) ? (float)d0 : var2;
      } catch (Throwable throwable) {
         return var2;
      }
   }

   public float call051() {
      return minecraftClient3.player.getHungerManager() == null ? 20.0F : minecraftClient3.player.getHungerManager().getFoodLevel();
   }

   public PlayerInput UiAnimation(PlayerInput var1) {
      return new PlayerInput(var1.forward(), var1.backward(), var1.left(), var1.right(), var1.jump(), var1.sneak(), var1.sprint());
   }

   public boolean call081() {
      return minecraftClient3.player.hasVehicle()
         || minecraftClient3.player.getAbilities().flying
         || minecraftClient3.player.isSpectator()
         || minecraftClient3.player.isTouchingWater()
         || minecraftClient3.player.isSwimming()
         || minecraftClient3.player.isSubmergedInWater()
         || minecraftClient3.player.isGliding()
         || minecraftClient3.player.horizontalCollision;
   }

   public float on23(RotationRecorder.Frame var1) {
      return var1.val193[6];
   }

   public float UiAnimation(RotationRecorder.Frame var1) {
      return var1.val193[7];
   }

   public RotationRecorder.ExportTask on23(RotationRecorder.Frame var1, RotationRecorder.Frame var2, float var3, float var4) {
      RotationRecorder.Sample illliiil11il11iiili1i11i1ii1_Var160 = this.UiAnimation(var1, var2, var3, var4);
      RotationRecorder.Sample illliiil11il11iiili1i11i1ii1_l1lll11l1l1 = this.UiAnimation(var1, var2, var3 + 5.0F, var4);
      RotationRecorder.Sample illliiil11il11iiili1i11i1ii1_l1lll11l1l2 = this.UiAnimation(var1, var2, var3 - 5.0F, var4);
      return new RotationRecorder.ExportTask(illliiil11il11iiili1i11i1ii1_Var160, illliiil11il11iiili1i11i1ii1_l1lll11l1l1, illliiil11il11iiili1i11i1ii1_l1lll11l1l2);
   }

   public RotationRecorder.Sample BotFeaturesDto(float var1, float var2) {
      Vec3d vec3d = minecraftClient3.player.getEntityPos();
      Vec3d vec3d1 = minecraftClient3.player.getVelocity();

      try {
         MovementController il11i11i111i1i1l1il = MovementController.on23(CustomInput.Easing(minecraftClient3.player.input.playerInput));
         il11i11i111i1i1l1il.yaw = var1;
         il11i11i111i1i1l1il.pitch = var2;
         il11i11i111i1i1l1il.tick();
         Vec3d vec3d2 = il11i11i111i1i1l1il.TriggerBot.subtract(vec3d);
         return this.Easing(vec3d2) && this.Easing(il11i11i111i1i1l1il.TriggerBot) && this.Easing(il11i11i111i1i1l1il.vec3d22)
            ? new RotationRecorder.Sample(
               true,
               vec3d2,
               il11i11i111i1i1l1il.TriggerBot,
               il11i11i111i1i1l1il.vec3d22,
               il11i11i111i1i1l1il.onGround,
               il11i11i111i1i1l1il.horizontalCollision,
               il11i11i111i1i1l1il.boolean95
            )
            : RotationRecorder.Sample.Easing(vec3d, vec3d1);
      } catch (Throwable throwable) {
         return RotationRecorder.Sample.Easing(vec3d, vec3d1);
      }
   }

   public RotationRecorder.Sample UiAnimation(RotationRecorder.Frame var1, RotationRecorder.Frame var2, float var3, float var4) {
      try {
         MovementController il11i11i111i1i1l1il = MovementController.on23(new CustomInput(var2.playerInput));
         il11i11i111i1i1l1il.TriggerBot = var1.vec3d18;
         il11i11i111i1i1l1il.vec3d22 = var1.vec3d19;
         il11i11i111i1i1l1il.box9 = var1.box5;
         il11i11i111i1i1l1il.yaw = var3;
         il11i11i111i1i1l1il.pitch = var4;
         il11i11i111i1i1l1il.boolean91 = var2.boolean91;
         il11i11i111i1i1l1il.float75 = var1.float75;
         il11i11i111i1i1l1il.int130 = var1.int130;
         il11i11i111i1i1l1il.boolean158 = var1.boolean92;
         il11i11i111i1i1l1il.boolean159 = var1.boolean93;
         il11i11i111i1i1l1il.onGround = var1.boolean89;
         il11i11i111i1i1l1il.horizontalCollision = var1.boolean94;
         il11i11i111i1i1l1il.boolean95 = var1.boolean95;
         il11i11i111i1i1l1il.boolean96 = var1.boolean96;
         il11i11i111i1i1l1il.boolean160 = var1.boolean97;
         il11i11i111i1i1l1il.boolean98 = var1.boolean98;
         il11i11i111i1i1l1il.entityPose = var1.entityPose;
         il11i11i111i1i1l1il.inSneakingPose = var1.boolean99;
         il11i11i111i1i1l1il.boolean161 = var1.boolean100;
         il11i11i111i1i1l1il.tick();
         Vec3d vec3d = il11i11i111i1i1l1il.TriggerBot.subtract(var1.vec3d18);
         return this.Easing(vec3d) && this.Easing(il11i11i111i1i1l1il.TriggerBot) && this.Easing(il11i11i111i1i1l1il.vec3d22)
            ? new RotationRecorder.Sample(
               true,
               vec3d,
               il11i11i111i1i1l1il.TriggerBot,
               il11i11i111i1i1l1il.vec3d22,
               il11i11i111i1i1l1il.onGround,
               il11i11i111i1i1l1il.horizontalCollision,
               il11i11i111i1i1l1il.boolean95
            )
            : RotationRecorder.Sample.Easing(var1.vec3d18, var1.vec3d19);
      } catch (Throwable throwable) {
         return RotationRecorder.Sample.Easing(var1.vec3d18, var1.vec3d19);
      }
   }

   public boolean Easing(Vec3d var1) {
      return var1 != null && this.isFinite((float)var1.x) && this.isFinite((float)var1.y) && this.isFinite((float)var1.z);
   }


   public static final class Session {
      public final long long95;
      public final int int132;
      public final int int133;
      public final int int134;
      public final int int135;
      public final int int136;
      public final int int137;
      public final int int138;

      public Session(long var1, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
         this.long95 = var1;
         this.int132 = var3;
         this.int133 = var4;
         this.int134 = var5;
         this.int135 = var6;
         this.int136 = var7;
         this.int137 = var8;
         this.int138 = var9;
      }
   }

   public static final class Frame {
      public final float[] val067;
      public final float[] val193;
      public final float[] val013;
      public final Vec3d vec3d18;
      public final Vec3d vec3d19;
      public final Box box5;
      public final PlayerInput playerInput;
      public final Box box6;
      public final boolean boolean89;
      public final boolean boolean90;
      public final boolean boolean91;
      public final float float75;
      public final int int130;
      public final boolean boolean92;
      public final boolean boolean93;
      public final boolean boolean94;
      public final boolean boolean95;
      public final boolean boolean96;
      public final boolean boolean97;
      public final boolean boolean98;
      public final EntityPose entityPose;
      public final boolean boolean99;
      public final boolean boolean100;
      public final float float76;
      public final long long94;
      public final int int131;

      public Frame(
         float[] var1,
         float[] var2,
         float[] var3,
         Vec3d var4,
         Vec3d var5,
         Box var6,
         PlayerInput var7,
         Box var8,
         boolean var9,
         boolean var10,
         boolean var11,
         float var12,
         int var13,
         boolean var14,
         boolean var15,
         boolean var16,
         boolean var17,
         boolean var18,
         boolean var19,
         boolean var20,
         EntityPose var21,
         boolean var22,
         boolean var23,
         float var24,
         long var25,
         int var27
      ) {
         this.val067 = (float[])var1.clone();
         this.val193 = (float[])var2.clone();
         this.val013 = (float[])var3.clone();
         this.vec3d18 = var4;
         this.vec3d19 = var5;
         this.box5 = var6;
         this.playerInput = var7;
         this.box6 = var8;
         this.boolean89 = var9;
         this.boolean90 = var10;
         this.boolean91 = var11;
         this.float75 = var12;
         this.int130 = var13;
         this.boolean92 = var14;
         this.boolean93 = var15;
         this.boolean94 = var16;
         this.boolean95 = var17;
         this.boolean96 = var18;
         this.boolean97 = var19;
         this.boolean98 = var20;
         this.entityPose = var21;
         this.boolean99 = var22;
         this.boolean100 = var23;
         this.float76 = var24;
         this.long94 = var25;
         this.int131 = var27;
      }
   }

   public static final class Writer {
      public final RotationRecorder val135;
      public final List<Analyzer> list43;
      public final long long92;

      public Writer(RotationRecorder var1) {
         this.val135 = var1;
         this.list43 = new ArrayList<>();
         this.long92 = Math.max(1L, Instant.now().toEpochMilli());
      }

      public void Easing(Analyzer var1) {
         if (this.ColorAnimator(var1)) {
            this.list43.add(var1);
         } else {
            this.flush();
            this.val135.botClient3();
         }
      }

      public boolean ColorAnimator(Analyzer var1) {
         return var1 != null && var1.isValid();
      }

      public void flush() {
         int i = this.list43.size();
         if (i <= 0) {
            this.list43.clear();
            this.val135.rotationRecorderVar159 = null;
         } else {
            for (int j = 0; j < i; j++) {
               this.val135.on23(this.long92, this.list43.get(j));
            }

            StyledTextBuilder.RefreshCacheEvent("RotationRecorder: wrote " + i);
            this.list43.clear();
            this.val135.rotationRecorderVar159 = null;
         }
      }

      public void float215() {
         int i = -1;

         for (int j = this.list43.size() - 1; j >= 0; j--) {
            if (this.val135.UiAnimation(this.list43.get(j))) {
               i = j;
               break;
            }
         }

         int l = i >= 0 ? i + 1 : 0;
         if (l < this.list43.size()) {
            int k = this.list43.size() - l;
            this.list43.subList(l, this.list43.size()).clear();
            StyledTextBuilder.RefreshCacheEvent("RotationRecorder: dropped interrupted tail " + k);
         }
      }
   }

   public static final class Sample {
      public final boolean boolean101;
      public final Vec3d vec3d20;
      public final Vec3d vec3d21;
      public final Vec3d vec3d22;
      public final boolean boolean102;
      public final boolean boolean103;
      public final boolean boolean104;

      public Sample(boolean var1, Vec3d var2, Vec3d var3, Vec3d var4, boolean var5, boolean var6, boolean var7) {
         this.boolean101 = var1;
         this.vec3d20 = var2;
         this.vec3d21 = var3;
         this.vec3d22 = var4;
         this.boolean102 = var5;
         this.boolean103 = var6;
         this.boolean104 = var7;
      }

      public static Sample Easing(Vec3d var0, Vec3d var1) {
         return new Sample(false, Vec3d.ZERO, var0, var1, false, false, false);
      }
   }

   public static final class Analyzer {
      public final float[] val136;
      public final float[] val137;
      public final float[] val138;
      public final float[] val139;
      public final float[] val035;
      public final long long93;
      public final int int129;
      public final float float74;

      public Analyzer(float[] var1, float[] var2, float[] var3, float[] var4, float[] var5, long var6, int var8, float var9) {
         this.val136 = (float[])var1.clone();
         this.val137 = (float[])var2.clone();
         this.val138 = (float[])var3.clone();
         this.val139 = (float[])var4.clone();
         this.val035 = (float[])var5.clone();
         this.long93 = var6;
         this.int129 = var8;
         this.float74 = var9;
      }

      public static Analyzer on23(
         Frame var0, Frame var1, ExportTask var2, float var3, float var4, boolean var5
      ) {
         boolean flag = var5
            || var0.boolean90
            || var1.boolean90
            || !var2.rotationRecorderVar160.boolean101
            || !var2.rotationRecorderVar1602.boolean101
            || !var2.rotationRecorderVar1603.boolean101
            || var2.float216();
         float[] afloat = on23(var0, var1);
         float[] afloat1 = on23(var0, var1, var2);
         float[] afloat2 = on23(var0, var1, var2, flag);
         float[] afloat3 = new float[]{flag ? 1.0F : 0.0F};
         return new Analyzer(
            afloat,
            afloat1,
            afloat2,
            afloat3,
            new float[]{NbtItemSpec(var3, var0.float76), NbtItemSpec(var4, var0.float76)},
            var0.long94,
            var0.int131,
            var0.float76
         );
      }

      public static float NbtItemSpec(float var0, float var1) {
         return Math.round(var0 / var1);
      }

      public static float[] on23(Frame var0, Frame var1, ExportTask var2, boolean var3) {
         float[] afloat = (float[])var1.val013.clone();
         ColorAnimator(var0.val013, afloat);
         afloat[4] = var1.boolean91 ? 1.0F : 0.0F;
         boolean flag = !var3
            && var2.rotationRecorderVar160.boolean101
            && var2.rotationRecorderVar1602.boolean101
            && var2.rotationRecorderVar1603.boolean101
            && !var2.float216()
            && Easing(var0)
            && Easing(var1);
         afloat[1] = flag ? 1.0F : 0.0F;
         return afloat;
      }

      public static void ColorAnimator(float[] var0, float[] var1) {
         if (var0 != null && var1 != null && var0.length > 30 && var1.length > 30) {
            var1[3] = var0[3];

            for (int i = 23; i <= 30; i++) {
               var1[i] = var0[i];
            }
         }
      }

      public static boolean Easing(Frame var0) {
         return var0 != null
            && !var0.boolean90
            && !var0.boolean93
            && !var0.boolean96
            && !var0.boolean97
            && !var0.boolean98
            && !var0.boolean94
            && !var0.boolean99
            && !var0.boolean100
            && !var0.playerInput.sneak()
            && on23(var0.entityPose)
            && var0.val013 != null
            && var0.val013.length == RotationRecorder.val192.length
            && var0.val013[6] == 0.0F
            && var0.val013[7] == 0.0F
            && var0.val013[8] == 0.0F
            && var0.val013[16] == 0.0F
            && var0.val013[17] == 0.0F
            && var0.val013[18] == 0.0F
            && var0.val013[19] == 0.0F
            && var0.val013[20] == 0.0F
            && (!var0.boolean91 || var0.val013[9] > 6.0F);
      }

      public static boolean on23(EntityPose var0) {
         return var0 == null || var0 == EntityPose.STANDING || var0 == EntityPose.CROUCHING;
      }

      public static float[] on23(Frame var0, Frame var1) {
         float[] afloat = (float[])var0.val067.clone();
         afloat[51] = var1.playerInput.forward() ? 1.0F : 0.0F;
         afloat[52] = var1.playerInput.backward() ? 1.0F : 0.0F;
         afloat[53] = var1.playerInput.left() ? 1.0F : 0.0F;
         afloat[54] = var1.playerInput.right() ? 1.0F : 0.0F;
         afloat[55] = var1.playerInput.jump() ? 1.0F : 0.0F;
         return afloat;
      }

      public static float[] on23(Frame var0, Frame var1, ExportTask var2) {
         float[] afloat = (float[])var0.val193.clone();
         on23(afloat, var1.playerInput);
         afloat[39] = var1.playerInput.jump() ? 1.0F : 0.0F;
         on23(afloat, var2);
         return afloat;
      }

      public static void on23(float[] var0, PlayerInput var1) {
         var0[20] = var1.forward() ? 1.0F : 0.0F;
         var0[21] = var1.backward() ? 1.0F : 0.0F;
         var0[22] = var1.left() ? 1.0F : 0.0F;
         var0[23] = var1.right() ? 1.0F : 0.0F;
         var0[24] = var1.jump() ? 1.0F : 0.0F;
         var0[25] = var1.sneak() ? 1.0F : 0.0F;
         var0[26] = var1.sprint() ? 1.0F : 0.0F;
      }

      public static void on23(float[] var0, ExportTask var1) {
         Sample illliiil11il11iiili1i11i1ii1_Var160 = var1.rotationRecorderVar160;
         Sample illliiil11il11iiili1i11i1ii1_l1lll11l1l1 = var1.rotationRecorderVar1602;
         Sample illliiil11il11iiili1i11i1ii1_l1lll11l1l2 = var1.rotationRecorderVar1603;
         var0[55] = illliiil11il11iiili1i11i1ii1_Var160.boolean101 ? 1.0F : 0.0F;
         var0[56] = illliiil11il11iiili1i11i1ii1_l1lll11l1l1.boolean101 ? 1.0F : 0.0F;
         var0[57] = illliiil11il11iiili1i11i1ii1_l1lll11l1l2.boolean101 ? 1.0F : 0.0F;
         on23(var0, 58, illliiil11il11iiili1i11i1ii1_Var160.vec3d20);
         on23(var0, 61, illliiil11il11iiili1i11i1ii1_Var160.vec3d21);
         on23(var0, 64, illliiil11il11iiili1i11i1ii1_Var160.vec3d22);
         var0[67] = illliiil11il11iiili1i11i1ii1_Var160.boolean102 ? 1.0F : 0.0F;
         var0[68] = illliiil11il11iiili1i11i1ii1_Var160.boolean103 ? 1.0F : 0.0F;
         var0[69] = illliiil11il11iiili1i11i1ii1_Var160.boolean104 ? 1.0F : 0.0F;
         on23(var0, 70, illliiil11il11iiili1i11i1ii1_l1lll11l1l1.vec3d20);
         on23(var0, 73, illliiil11il11iiili1i11i1ii1_l1lll11l1l2.vec3d20);
         var0[76] = (float)((illliiil11il11iiili1i11i1ii1_l1lll11l1l1.vec3d20.x - illliiil11il11iiili1i11i1ii1_l1lll11l1l2.vec3d20.x) / 10.0);
         var0[77] = (float)((illliiil11il11iiili1i11i1ii1_l1lll11l1l1.vec3d20.y - illliiil11il11iiili1i11i1ii1_l1lll11l1l2.vec3d20.y) / 10.0);
         var0[78] = (float)((illliiil11il11iiili1i11i1ii1_l1lll11l1l1.vec3d20.z - illliiil11il11iiili1i11i1ii1_l1lll11l1l2.vec3d20.z) / 10.0);
      }

      public static void on23(float[] var0, int var1, Vec3d var2) {
         var0[var1] = (float)var2.x;
         var0[var1 + 1] = (float)var2.y;
         var0[var1 + 2] = (float)var2.z;
      }

      public boolean isValid() {
         return this.val136 != null
            && this.val137 != null
            && this.val138 != null
            && this.val139 != null
            && this.val035 != null
            && this.float74 > 0.0F
            && !Float.isNaN(this.float74)
            && !Float.isInfinite(this.float74)
            && this.val136.length == 59
            && this.val137.length == RotationRecorder.val311.length
            && this.val138.length == RotationRecorder.val192.length
            && this.val139.length == RotationRecorder.val420.length
            && this.val035.length == 2
            && BotFeatureRegistry(this.val136)
            && BotFeatureRegistry(this.val137)
            && BotFeatureRegistry(this.val138)
            && BotFeatureRegistry(this.val139)
            && BotFeatureRegistry(this.val035);
      }

      public static boolean BotFeatureRegistry(float[] var0) {
         if (var0 == null) {
            return false;
         }

         for (float f : var0) {
            if (Float.isNaN(f) || Float.isInfinite(f)) {
               return false;
            }
         }

         return true;
      }
   }

   public static final class ExportTask {
      public final Sample rotationRecorderVar160;
      public final Sample rotationRecorderVar1602;
      public final Sample rotationRecorderVar1603;

      public ExportTask(Sample var1, Sample var2, Sample var3) {
         this.rotationRecorderVar160 = var1;
         this.rotationRecorderVar1602 = var2;
         this.rotationRecorderVar1603 = var3;
      }

      public boolean float216() {
         return this.rotationRecorderVar160.boolean103 || this.rotationRecorderVar1602.boolean103 || this.rotationRecorderVar1603.boolean103;
      }
   }
}
