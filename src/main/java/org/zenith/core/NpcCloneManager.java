package org.zenith.core;

import com.mojang.authlib.GameProfile;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.client.screens.entity.ImplOtherClientPlayerEntity;

public class NpcCloneManager {
   public static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
   public final UUID uUID4;
   public ImplOtherClientPlayerEntity implOtherClientPlayerEntity;
   public String AutoCapcha = "";
   public Vec3d TriggerBot;
   public Vec3d vec3d37 = Vec3d.ZERO;
   public float float203 = ThreadLocalRandom.current().nextFloat() * 360.0F;
   public float yaw;
   public float float204;
   public Vec3d vec3d38;
   public Vec3d vec3d39;
   public float float205;
   public float float206;
   public float float207;
   public boolean boolean146;
   public boolean boolean147;
   public boolean lastOnGround;
   public long long140 = System.currentTimeMillis();
   public double double107;
   public double double108;
   public int int322 = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
   public long long141 = 0L;
   public boolean boolean148 = false;
   public int int303 = 0;
   public static final double double109 = 0.12;
   public static final double double110 = 0.72;

   public NpcCloneManager(UUID var1) {
      this.uUID4 = UUID.nameUUIDFromBytes(("zenith-pet:" + var1.toString()).getBytes());
   }

   public void on23(ClientWorld var1, Vec3d var2) {
      this.RotationDelta();
      this.AutoCapcha = "";
      this.implOtherClientPlayerEntity = new ImplOtherClientPlayerEntity(var1, new GameProfile(this.uUID4, ""));
      this.implOtherClientPlayerEntity.setId(Integer.MAX_VALUE - ThreadLocalRandom.current().nextInt(1, 100000));
      this.implOtherClientPlayerEntity.setCustomNameVisible(false);
      this.implOtherClientPlayerEntity.setInvisible(false);
      this.implOtherClientPlayerEntity.setNoGravity(true);
      this.StringCodec(ZenithClient.on23().ItemServiceBase().TickGate());
      EntityAttributeInstance entityattributeinstance = this.implOtherClientPlayerEntity.getAttributeInstance(EntityAttributes.STEP_HEIGHT);
      if (entityattributeinstance != null) {
         entityattributeinstance.setBaseValue(0.6);
      }

      this.TriggerBot = var2;
      this.vec3d38 = var2;
      this.vec3d39 = var2;
      this.vec3d37 = Vec3d.ZERO;
      this.float205 = 0.0F;
      this.float206 = 0.0F;
      this.float207 = 0.0F;
      this.yaw = 0.0F;
      this.float204 = 0.0F;
      this.double107 = var2.x;
      this.double108 = var2.z;
      this.long140 = System.currentTimeMillis();
      this.implOtherClientPlayerEntity.updateTrackedPositionAndAngles(var2, 0.0F, 0.0F);
      this.implOtherClientPlayerEntity.setHeadYaw(0.0F);
      var1.addEntity(this.implOtherClientPlayerEntity);
   }

   public void RotationDelta() {
      if (this.implOtherClientPlayerEntity != null) {
         this.implOtherClientPlayerEntity.setRemoved(RemovalReason.KILLED);
         this.implOtherClientPlayerEntity.onRemoved();
         this.implOtherClientPlayerEntity = null;
      }

      UserdataManager.StringCodec(this.uUID4);
   }

   public boolean RotationMath() {
      if (this.implOtherClientPlayerEntity != null && !this.implOtherClientPlayerEntity.isRemoved()) {
         return minecraftClient.world == null
            ? false
            : minecraftClient.world.getEntityById(this.implOtherClientPlayerEntity.getId()) == this.implOtherClientPlayerEntity;
      } else {
         return false;
      }
   }

   public void StringCodec(float var1) {
      if (this.implOtherClientPlayerEntity != null) {
         EntityAttributeInstance entityattributeinstance = this.implOtherClientPlayerEntity.getAttributeInstance(EntityAttributes.SCALE);
         if (entityattributeinstance != null) {
            entityattributeinstance.setBaseValue(var1);
         }
      }
   }

   public void StopUsingItemEvent(String var1) {
      if (var1 == null || var1.isBlank()) {
         UserdataManager.StringCodec(this.uUID4);
         this.AutoCapcha = "";
      } else if (!var1.equals(this.AutoCapcha)) {
         this.AutoCapcha = var1;
         UserdataManager.UiAnimation(this.uUID4, var1);
      }
   }

   public void ItemRegistry(Path var1) {
      if (var1 == null) {
         UserdataManager.StringCodec(this.uUID4);
         this.AutoCapcha = "";
      } else {
         String s = UserdataManager.ProfileItemBuilder(var1);
         if (!s.equals(this.AutoCapcha)) {
            this.AutoCapcha = s;
            UserdataManager.on23(this.uUID4, var1);
         }
      }
   }

   public void on23(Vec3d var1, float var2, boolean var3, LivingEntity var4) {
      this.vec3d39 = this.vec3d38;
      this.float207 = this.float205;
      if (this.TriggerBot == null) {
         this.TriggerBot = var1;
      }

      if (this.TriggerBot.distanceTo(var1) > 10.0) {
         this.TriggerBot = var1;
         this.vec3d37 = Vec3d.ZERO;
         this.vec3d38 = this.TriggerBot;
         this.vec3d39 = this.TriggerBot;
         this.WorldUtils();
      } else {
         Vec3d vec3d;
         if (this.boolean146) {
            double d0 = var1.y - this.TriggerBot.y;
            this.vec3d37 = new Vec3d(this.vec3d37.x, d0 * 0.2, this.vec3d37.z);
            vec3d = this.TriggerBot.add(this.vec3d37);
            this.vec3d37 = new Vec3d(this.vec3d37.x, 0.0, this.vec3d37.z);
         } else {
            this.vec3d37 = this.vec3d37.add(0.0, -0.08, 0.0);
            this.vec3d37 = this.vec3d37.multiply(1.0, 0.98, 1.0);
            if (this.vec3d37.y < -0.5) {
               this.vec3d37 = new Vec3d(this.vec3d37.x, -0.5, this.vec3d37.z);
            }

            double d25 = this.vec3d37.x;
            double d1 = this.vec3d37.y;
            double d3 = this.vec3d37.z;
            double d5 = this.TriggerBot.x;
            double d6 = this.TriggerBot.y;
            double d8 = this.TriggerBot.z;
            boolean flag = false;
            if (!Easing(d5, d6 + d1, d8)) {
               d6 += d1;
            } else {
               if (d1 < 0.0) {
                  double d10 = d6 + d1;
                  double d12 = d6;

                  for (int i = 0; i < 8; i++) {
                     double d13 = (d10 + d12) * 0.5;
                     if (Easing(d5, d13, d8)) {
                        d10 = d13;
                     } else {
                        d12 = d13;
                     }
                  }

                  d6 = d12;
                  flag = true;
               }

               d1 = 0.0;
            }

            if (!flag && Easing(d5, d6 - 0.02, d8)) {
               flag = true;
            }

            if (Math.abs(d25) > 1.0E-5) {
               if (!Easing(d5 + d25, d6, d8)) {
                  d5 += d25;
               } else {
                  boolean flag1 = false;
                  if (flag) {
                     for (double d11 = 0.1; d11 <= 0.6; d11 += 0.1) {
                        if (!Easing(d5 + d25, d6 + d11, d8) && !Easing(d5, d6 + d11, d8)) {
                           d6 += d11;
                           d5 += d25;
                           flag1 = true;
                           break;
                        }
                     }
                  }

                  if (!flag1) {
                     if (flag && d1 <= 0.0 && ItemRegistry(d5 + Math.signum(d25) * 0.35, d6, d8)) {
                        d1 = 0.52;
                     }

                     d25 = 0.0;
                  }
               }
            }

            if (Math.abs(d3) > 1.0E-5) {
               if (!Easing(d5, d6, d8 + d3)) {
                  d8 += d3;
               } else {
                  boolean flag2 = false;
                  if (flag) {
                     for (double d33 = 0.1; d33 <= 0.6; d33 += 0.1) {
                        if (!Easing(d5, d6 + d33, d8 + d3) && !Easing(d5, d6 + d33, d8)) {
                           d6 += d33;
                           d8 += d3;
                           flag2 = true;
                           break;
                        }
                     }
                  }

                  if (!flag2) {
                     if (flag && d1 <= 0.0 && ItemRegistry(d5, d6, d8 + Math.signum(d3) * 0.35)) {
                        d1 = 0.52;
                     }

                     d3 = 0.0;
                  }
               }
            }

            this.vec3d37 = new Vec3d(d25, d1, d3);
            vec3d = new Vec3d(d5, d6, d8);
            this.lastOnGround = flag;
         }

         double d26 = this.lastOnGround ? 1.0 : 0.2;
         if (var4 != null && var4.isAlive()) {
            Box box = var4.getBoundingBox().expand(0.1);
            double d2 = (box.minX + box.maxX) * 0.5;
            double d30 = (box.minZ + box.maxZ) * 0.5;
            double d31 = (box.maxX - box.minX) * 0.5;
            double d7 = (box.maxZ - box.minZ) * 0.5;
            double d9 = Math.max(d31, d7) + 0.6;
            double d32 = var4.getY();
            double d34 = Math.abs(vec3d.y - d32);
            double d35 = Math.hypot(vec3d.x - d2, vec3d.z - d30);
            if (d34 > 1.0 && this.lastOnGround || d35 > 4.0) {
               double d36 = Math.atan2(var1.z - d30, var1.x - d2);
               double d37 = d2 + Math.cos(d36) * d9;
               double d38 = d30 + Math.sin(d36) * d9;
               vec3d = new Vec3d(d37, d32, d38);
               this.vec3d37 = Vec3d.ZERO;
               this.TriggerBot = vec3d;
               this.vec3d38 = vec3d;
               this.vec3d39 = vec3d;
               this.int303 = 0;
               this.WorldUtils();
               return;
            }

            double d14 = Math.atan2(vec3d.z - d30, vec3d.x - d2);
            double d15 = d14 + 0.3 * this.int322;
            double d16 = Math.atan2(var1.z - d30, var1.x - d2);
            double d17 = d15 - d16;

            while (d17 > Math.PI) {
               d17 -= Math.PI * 2;
            }

            while (d17 < -Math.PI) {
               d17 += Math.PI * 2;
            }

            double d18 = Math.PI / 2;
            if (d17 > Math.PI / 2) {
               d17 = Math.PI / 2;
               this.int322 = -this.int322;
            } else if (d17 < -Math.PI / 2) {
               d17 = -Math.PI / 2;
               this.int322 = -this.int322;
            }

            d15 = d16 + d17;
            double d19 = d2 + Math.cos(d15) * d9;
            double d20 = d30 + Math.sin(d15) * d9;
            double d21 = MathHelper.lerp(0.35, vec3d.x, d19);
            double d22 = MathHelper.lerp(0.35, vec3d.z, d20);
            double d23 = vec3d.x;
            double d24 = vec3d.z;
            if (!Easing(d21, vec3d.y, vec3d.z) && ColorAnimator(d21, vec3d.y, vec3d.z)) {
               d23 = d21;
            }

            if (!Easing(d23, vec3d.y, d22) && ColorAnimator(d23, vec3d.y, d22)) {
               d24 = d22;
            }

            vec3d = new Vec3d(d23, vec3d.y, d24);
            this.vec3d37 = new Vec3d(0.0, this.vec3d37.y, 0.0);
            if (this.implOtherClientPlayerEntity.age % 40 == 0) {
               this.int322 = -this.int322;
            }

            if (this.lastOnGround) {
               this.int303++;
               if (this.int303 >= 3) {
                  this.vec3d37 = new Vec3d(this.vec3d37.x, 0.45, this.vec3d37.z);
                  this.int303 = 0;
               }
            } else {
               this.int303 = 0;
            }

            this.boolean148 = true;
         } else {
            this.boolean148 = false;
            double d27 = vec3d.distanceTo(var1);
            if (d27 > 2.0) {
               Vec3d vec3d2 = var1.subtract(vec3d);
               double d4 = Math.min(1.0, (d27 - 2.0) / 3.0) * d26;
               Vec3d vec3d1 = vec3d2.normalize();
               this.vec3d37 = this.vec3d37.add(vec3d1.x * d4, 0.0, vec3d1.z * d4);
            }
         }

         this.on23(var1, var4);
         this.TriggerBot = vec3d;
         if (this.TriggerBot.distanceTo(var1) < 0.1) {
            this.float203 = ThreadLocalRandom.current().nextFloat() * 360.0F;
            double d28 = -Math.sin(Math.toRadians(this.float203)) * 0.1;
            double d29 = Math.cos(Math.toRadians(this.float203)) * 0.1;
            this.vec3d37 = this.vec3d37.add(d28, 0.0, d29);
         }

         float f = this.lastOnGround ? 0.6F : 0.91F;
         this.vec3d37 = new Vec3d(this.vec3d37.x * f, this.vec3d37.y, this.vec3d37.z * f);
         this.vec3d38 = this.TriggerBot;
         if (Math.abs(this.TriggerBot.x - this.double107) > 0.1 || Math.abs(this.TriggerBot.z - this.double108) > 0.1) {
            this.long140 = System.currentTimeMillis();
         }

         this.double107 = this.TriggerBot.x;
         this.double108 = this.TriggerBot.z;
         this.WorldUtils();
      }
   }

   public void on23(Vec3d var1, LivingEntity var2) {
      Vec3d vec3d = var2 != null && var2.isAlive() ? var2.getEntityPos() : var1;
      double d0 = vec3d.x - this.TriggerBot.x;
      double d1 = vec3d.z - this.TriggerBot.z;
      float f = (float)Math.toDegrees(Math.atan2(d1, d0)) - 90.0F;
      this.yaw = f;
      this.float204 = f;
      this.float205 = f;
      this.float206 = f;
   }

   public void ItemCountUtils() {
   }

   public void on23(Vec3d var1, float var2, boolean var3) {
      this.vec3d39 = this.vec3d38;
      this.float207 = this.float205;
      this.TriggerBot = var1;
      this.vec3d38 = var1;
      this.float205 = var2;
      this.float206 = var2;
      this.float204 = var2;
      this.WorldUtils();
   }

   public void on23(PlayerEntity var1) {
      if (this.implOtherClientPlayerEntity != null && !this.implOtherClientPlayerEntity.isRemoved() && var1 != null) {
         int selectedSlot = var1.getInventory().getSelectedSlot();
         this.implOtherClientPlayerEntity.getInventory().setSelectedSlot(selectedSlot);
         this.implOtherClientPlayerEntity.getInventory().getMainStacks().set(
            selectedSlot, var1.getInventory().getMainStacks().get(selectedSlot).copy()
         );
         this.implOtherClientPlayerEntity.equipStack(EquipmentSlot.OFFHAND, var1.getEquippedStack(EquipmentSlot.OFFHAND).copy());
         for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD}) {
            this.implOtherClientPlayerEntity.equipStack(slot, var1.getEquippedStack(slot).copy());
         }
      }
   }

   public Vec3d FileLogger(float var1) {
      return this.vec3d39 != null && this.vec3d38 != null
         ? new Vec3d(
            MathHelper.lerp(var1, this.vec3d39.x, this.vec3d38.x),
            MathHelper.lerp(var1, this.vec3d39.y, this.vec3d38.y),
            MathHelper.lerp(var1, this.vec3d39.z, this.vec3d38.z)
         )
         : this.vec3d38;
   }

   public void WorldUtils() {
      if (this.implOtherClientPlayerEntity != null && !this.implOtherClientPlayerEntity.isRemoved()) {
         this.implOtherClientPlayerEntity.updateTrackedPositionAndAngles(this.vec3d38, this.float205, 0.0F);
         this.implOtherClientPlayerEntity.updateTrackedHeadRotation(this.float206, 3);
         this.implOtherClientPlayerEntity.updateTrackedPosition(this.vec3d38.x, this.vec3d38.y, this.vec3d38.z);
         this.implOtherClientPlayerEntity.setOnGround(this.lastOnGround);
         this.implOtherClientPlayerEntity.setVelocity(this.vec3d37);
      }
   }

   public static boolean on23(double var0, double var2, double var4) {
      if (minecraftClient.world == null) {
         return false;
      }

      BlockPos blockpos = BlockPos.ofFloored(var0, var2, var4);
      return minecraftClient.world.getBlockState(blockpos).isSolid();
   }

   public static Box UiAnimation(double var0, double var2, double var4) {
      return new Box(var0 - 0.12, var2, var4 - 0.12, var0 + 0.12, var2 + 0.72, var4 + 0.12);
   }

   public static boolean Easing(double var0, double var2, double var4) {
      return minecraftClient.world != null && !minecraftClient.world.isSpaceEmpty(UiAnimation(var0, var2, var4));
   }

   public static boolean ColorAnimator(double var0, double var2, double var4) {
      if (minecraftClient.world == null) {
         return true;
      }

      for (double d0 = 0.01; d0 <= 1.5; d0 += 0.25) {
         if (!minecraftClient.world.isSpaceEmpty(UiAnimation(var0, var2 - d0, var4))) {
            return true;
         }
      }

      return false;
   }

   public static boolean ItemRegistry(double var0, double var2, double var4) {
      return minecraftClient.world == null ? false : !Easing(var0, var2 + 1.0, var4);
   }

   public static float ItemRegistry(float var0, float var1, float var2) {
      return var0 + MathHelper.wrapDegrees(var1 - var0) * var2;
   }

   public UUID DrawContextSink() {
      return this.uUID4;
   }

   public ImplOtherClientPlayerEntity GameService() {
      return this.implOtherClientPlayerEntity;
   }

   public String ClientProvider() {
      return this.AutoCapcha;
   }

   public Vec3d ClientWindowProvider() {
      return this.vec3d38;
   }

   public Vec3d RenderHook() {
      return this.vec3d39;
   }

   public float CloudResult() {
      return this.float205;
   }

   public float CloudScope() {
      return this.float206;
   }

   public float GradientPalette() {
      return this.float207;
   }

   public boolean MathUtils() {
      return this.boolean146;
   }

   public void FileLogger(boolean var1) {
      this.boolean146 = var1;
   }
}
