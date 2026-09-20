package org.zenith.core;

import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.PowderSnowBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import org.zenith.ZenithClient;

public class MovementController implements GameService {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final PlayerEntity playerEntity2;
   public final CustomInput var154Var159;
   public Vec3d TriggerBot;
   public Vec3d vec3d22;
   public Box box9;
   public float yaw;
   public float pitch;
   public boolean boolean91;
   public float float75;
   public int int130;
   public boolean boolean158;
   public boolean boolean159;
   public boolean onGround;
   public boolean horizontalCollision;
   public boolean boolean95;
   public boolean boolean96;
   public boolean boolean160;
   public boolean boolean98;
   public EntityPose entityPose;
   public boolean inSneakingPose;
   public boolean boolean161;
   public final Object2DoubleMap<TagKey<Fluid>> object2DoubleMap;
   public final HashSet<TagKey<Fluid>> hashSet2;
   public int int372 = 0;
   public boolean boolean162 = false;

   public MovementController(
      PlayerEntity var1,
      CustomInput var2,
      Vec3d var3,
      Vec3d var4,
      Box var5,
      float var6,
      float var7,
      boolean var8,
      float var9,
      int var10,
      boolean var11,
      boolean var12,
      boolean var13,
      boolean var14,
      boolean var15,
      boolean var16,
      boolean var17,
      boolean var18,
      EntityPose var19,
      boolean var20,
      boolean var21,
      Object2DoubleMap<TagKey<Fluid>> var22,
      HashSet<TagKey<Fluid>> var23
   ) {
      this.playerEntity2 = var1;
      this.var154Var159 = var2;
      this.TriggerBot = var3;
      this.vec3d22 = var4;
      this.box9 = var5;
      this.yaw = var6;
      this.pitch = var7;
      this.boolean91 = var8;
      this.float75 = var9;
      this.int130 = var10;
      this.boolean158 = var11;
      this.boolean159 = var12;
      this.onGround = var13;
      this.horizontalCollision = var14;
      this.boolean95 = var15;
      this.boolean96 = var16;
      this.boolean160 = var17;
      this.boolean98 = var18;
      this.entityPose = var19;
      this.inSneakingPose = var20;
      this.boolean161 = var21;
      this.object2DoubleMap = var22;
      this.hashSet2 = var23;
   }

   public static MovementController TargetAcquireEvent(int var0) {
      MovementController il11i11i111i1i1l1il = on23(CustomInput.Easing(minecraftClient3.player.input.playerInput));

      for (int i = 0; i < var0; i++) {
         il11i11i111i1i1l1il.tick();
      }

      return il11i11i111i1i1l1il;
   }

   public static MovementController ColorAnimator(PlayerEntity var0, int var1) {
      MovementController il11i11i111i1i1l1il = on23(var0, CustomInput.ItemServiceBase(var0));

      for (int i = 0; i < var1; i++) {
         il11i11i111i1i1l1il.tick();
      }

      return il11i11i111i1i1l1il;
   }

   public static MovementController on23(CustomInput var0) {
      ClientPlayerEntity clientplayerentity = minecraftClient3.player;
      return new MovementController(
         clientplayerentity,
         var0,
         clientplayerentity.getEntityPos(),
         clientplayerentity.getVelocity(),
         clientplayerentity.getBoundingBox(),
         ZenithClient.on23().CloudRouter().LineShader().GrimGlide(),
         ZenithClient.on23().CloudRouter().LineShader().GuiWalk(),
         clientplayerentity.isSprinting(),
         (float)clientplayerentity.fallDistance,
         clientplayerentity.jumpingCooldown,
         clientplayerentity.jumping,
         clientplayerentity.isGliding(),
         clientplayerentity.isOnGround(),
         clientplayerentity.horizontalCollision,
         clientplayerentity.verticalCollision,
         clientplayerentity.isTouchingWater(),
         clientplayerentity.isSwimming(),
         clientplayerentity.isSubmergedInWater(),
         clientplayerentity.getPose(),
         clientplayerentity.isInSneakingPose(),
         clientplayerentity.isCrawling(),
         new Object2DoubleArrayMap(clientplayerentity.fluidHeight),
         new HashSet<>(clientplayerentity.submergedFluidTag)
      );
   }

   public static MovementController on23(PlayerEntity var0, CustomInput var1) {
      return new MovementController(
         var0,
         var1,
         var0.getEntityPos(),
         var0.getEntityPos().subtract(new Vec3d(var0.lastX, var0.lastY, var0.lastZ)),
         var0.getBoundingBox(),
         var0.getYaw(),
         var0.getPitch(),
         var0.isSprinting(),
         (float)var0.fallDistance,
         var0.jumpingCooldown,
         var0.jumping,
         var0.isGliding(),
         var0.isOnGround(),
         var0.horizontalCollision,
         var0.verticalCollision,
         var0.isTouchingWater(),
         var0.isSwimming(),
         var0.isSubmergedInWater(),
         var0.getPose(),
         var0.isInSneakingPose(),
         var0.isCrawling(),
         new Object2DoubleArrayMap(var0.fluidHeight),
         new HashSet<>(var0.submergedFluidTag)
      );
   }

   public Vec3d VisualSettingsStore() {
      return this.TriggerBot;
   }

   public void tick() {
      this.int372++;
      this.boolean162 = false;
      if (!(this.TriggerBot.y <= -70.0)) {
         this.call418();
         this.call422();
         this.call165();
         this.call442();
         this.call443();
         this.var154Var159.update();
         this.call420();
         this.call419();
         if (this.int130 > 0) {
            this.int130--;
         }

         this.boolean158 = this.var154Var159.playerInput.jump();
         double d0 = this.vec3d22.x;
         double d1 = this.vec3d22.y;
         double d2 = this.vec3d22.z;
         if (Math.abs(this.vec3d22.x) < 0.003) {
            d0 = 0.0;
         }

         if (Math.abs(this.vec3d22.y) < 0.003) {
            d1 = 0.0;
         }

         if (Math.abs(this.vec3d22.z) < 0.003) {
            d2 = 0.0;
         }

         if (this.onGround) {
            this.boolean159 = false;
         }

         this.vec3d22 = new Vec3d(d0, d1, d2);
         if (this.boolean158 && this.playerEntity2.shouldSwimInFluids()) {
            double d3 = this.call214() ? this.UiAnimation(FluidTags.LAVA) : this.UiAnimation(FluidTags.WATER);
            boolean flag = this.string85() && d3 > 0.0;
            double d4 = this.long142();
            if (!flag || this.onGround && !(d3 > d4)) {
               if (!this.call214() || this.onGround && !(d3 > d4)) {
                  if ((this.onGround || flag && d3 <= d4) && this.int130 == 0) {
                     this.ItemCountUtils();
                     this.int130 = 10;
                  }
               } else {
                  this.on23(FluidTags.LAVA);
               }
            } else {
               this.on23(FluidTags.WATER);
            }
         } else {
            this.int130 = 0;
         }

         float f1 = this.var154Var159.float63 * 0.98F;
         float f = this.var154Var159.float62 * 0.98F;
         float f2 = 0.0F;
         if (this.ItemSpec(StatusEffects.SLOW_FALLING) || this.ItemSpec(StatusEffects.LEVITATION)) {
            this.call083();
         }

         this.CommandManager(new Vec3d(f1, f2, f));
      }
   }

   public void CommandManager(Vec3d var1) {
      if (this.boolean160 && !this.playerEntity2.hasVehicle()) {
         double d0 = this.call215().y;
         double d1 = d0 < -0.2 ? 0.085 : 0.06;
         BlockPos blockpos = new BlockPos(
            MathHelper.floor(this.TriggerBot.x),
            MathHelper.floor(this.TriggerBot.y + 1.0 - 0.1),
            MathHelper.floor(this.TriggerBot.z)
         );
         if (d0 <= 0.0 || this.var154Var159.playerInput.jump() || !this.playerEntity2.getEntityWorld().getBlockState(blockpos).getFluidState().isEmpty()) {
            this.vec3d22 = this.vec3d22.add(0.0, (d0 - this.vec3d22.y) * d1, 0.0);
         }
      }

      double d6 = this.vec3d22.y;
      double d7 = this.scheduledExecutorService();
      boolean flag = this.vec3d22.y <= 0.0;
      if (this.string85() && this.playerEntity2.shouldSwimInFluids()) {
         double d10 = this.TriggerBot.y;
         float f4 = this.string122() ? 0.9F : 0.8F;
         float f5 = 0.02F;
         float f6 = (float)this.NbtItemSpec(EntityAttributes.WATER_MOVEMENT_EFFICIENCY);
         if (!this.onGround) {
            f6 *= 0.5F;
         }

         if (f6 > 0.0F) {
            f4 += (0.54600006F - f4) * f6;
            f5 += (this.call235() - f5) * f6;
         }

         if (this.ItemSpec(StatusEffects.DOLPHINS_GRACE)) {
            f4 = 0.96F;
         }

         this.on23(f5, var1);
         this.ModuleStateStore(this.vec3d22);
         Vec3d vec3d3 = this.vec3d22;
         if (this.horizontalCollision && this.call142()) {
            vec3d3 = new Vec3d(vec3d3.x, 0.2, vec3d3.z);
         }

         this.vec3d22 = vec3d3.multiply(f4, 0.8, f4);
         Vec3d vec3d4 = this.playerEntity2.applyFluidMovingSpeed(d7, flag, this.vec3d22);
         this.vec3d22 = vec3d4;
         if (this.horizontalCollision && this.EnchantItemSpec(vec3d4.x, vec3d4.y + 0.6 - this.TriggerBot.y + d10, vec3d4.z)
            )
          {
            this.vec3d22 = new Vec3d(vec3d4.x, 0.3, vec3d4.z);
         }
      } else if (this.call214() && this.playerEntity2.shouldSwimInFluids()) {
         double d9 = this.TriggerBot.y;
         this.on23(0.02F, var1);
         this.ModuleStateStore(this.vec3d22);
         if (this.UiAnimation(FluidTags.LAVA) <= this.long142()) {
            this.vec3d22 = this.vec3d22.multiply(0.5, 0.8, 0.5);
            this.vec3d22 = this.playerEntity2.applyFluidMovingSpeed(d7, flag, this.vec3d22);
         } else {
            this.vec3d22 = this.vec3d22.multiply(0.5);
         }

         if (!this.playerEntity2.hasNoGravity()) {
            this.vec3d22 = this.vec3d22.add(0.0, -d7 / 4.0, 0.0);
         }

         if (this.horizontalCollision
            && this.EnchantItemSpec(this.vec3d22.x, this.vec3d22.y + 0.6 - this.TriggerBot.y + d9, this.vec3d22.z)) {
            this.vec3d22 = new Vec3d(this.vec3d22.x, 0.3, this.vec3d22.z);
         }
      } else if (this.boolean159) {
         Vec3d vec3d = this.vec3d22;
         if (vec3d.y > -0.5) {
            this.float75 = 1.0F;
         }

         Vec3d vec3d1 = this.call215();
         float f1 = this.pitch * (float) (Math.PI / 180.0);
         double d3 = Math.sqrt(vec3d1.x * vec3d1.x + vec3d1.z * vec3d1.z);
         double d4 = this.vec3d22.horizontalLength();
         double d5 = vec3d1.length();
         float f2 = MathHelper.cos(f1);
         f2 = (float)((double)f2 * f2 * Math.min(1.0, d5 / 0.4));
         vec3d = this.vec3d22.add(0.0, d7 * (-1.0 + f2 * 0.75), 0.0);
         if (vec3d.y < 0.0 && d3 > 0.0) {
            double d2 = vec3d.y * -0.1 * f2;
            vec3d = vec3d.add(vec3d1.x * d2 / d3, d2, vec3d1.z * d2 / d3);
         }

         if (f1 < 0.0F && d3 > 0.0) {
            double d8 = d4 * -MathHelper.sin(f1) * 0.04;
            vec3d = vec3d.add(-vec3d1.x * d8 / d3, d8 * 3.2, -vec3d1.z * d8 / d3);
         }

         if (d3 > 0.0) {
            vec3d = vec3d.add((vec3d1.x / d3 * d4 - vec3d.x) * 0.1, 0.0, (vec3d1.z / d3 * d4 - vec3d.z) * 0.1);
         }

         this.vec3d22 = vec3d.multiply(0.99, 0.98, 0.99);
         this.ModuleStateStore(this.vec3d22);
      } else {
         BlockPos blockpos1 = this.string84();
         float f = this.playerEntity2.getEntityWorld().getBlockState(blockpos1).getBlock().getSlipperiness();
         float f3 = this.onGround ? f * 0.91F : 0.91F;
         Vec3d vec3d2 = this.Easing(var1, f);
         double d11 = vec3d2.y;
         if (this.ItemSpec(StatusEffects.LEVITATION)) {
            StatusEffectInstance statuseffectinstance = this.TextScanner(StatusEffects.LEVITATION);
            if (statuseffectinstance != null) {
               d11 += (0.05 * (statuseffectinstance.getAmplifier() + 1) - vec3d2.y) * 0.2;
            }
         } else if (this.playerEntity2.getEntityWorld().isClient() && !this.playerEntity2.getEntityWorld().isChunkLoaded(blockpos1)) {
            d11 = this.TriggerBot.y > this.playerEntity2.getEntityWorld().getBottomY() ? -0.1 : 0.0;
         } else if (d7 != 0.0) {
            d11 -= d7;
         }

         if (this.playerEntity2.hasNoDrag()) {
            this.vec3d22 = new Vec3d(vec3d2.x, d11, vec3d2.z);
         } else {
            this.vec3d22 = new Vec3d(vec3d2.x * f3, d11 * 0.98F, vec3d2.z * f3);
         }
      }

      if (this.playerEntity2.getAbilities().flying && !this.playerEntity2.hasVehicle()) {
         this.vec3d22 = new Vec3d(this.vec3d22.x, d6 * 0.6, this.vec3d22.z);
         this.call083();
      }
   }

   public Vec3d Easing(Vec3d var1, float var2) {
      this.on23(this.PreventActionEvent(var2), var1);
      this.vec3d22 = this.EmoteMetadata(this.vec3d22);
      this.ModuleStateStore(this.vec3d22);
      Vec3d vec3d = this.vec3d22;
      BlockPos blockpos = this.CosmeticManager(this.TriggerBot);
      BlockState blockstate = this.EventMixin_modifySetScreenArg(blockpos);
      if ((this.horizontalCollision || this.boolean158)
         && (this.call142() || blockstate != null && blockstate.isOf(Blocks.POWDER_SNOW) && PowderSnowBlock.canWalkOnPowderSnow(this.playerEntity2))) {
         vec3d = new Vec3d(vec3d.x, 0.2, vec3d.z);
      }

      return vec3d;
   }

   public void on23(float var1, Vec3d var2) {
      Vec3d vec3d = Entity.movementInputToVelocity(var2, var1, this.yaw);
      this.vec3d22 = this.vec3d22.add(vec3d);
   }

   public float PreventActionEvent(float var1) {
      return this.onGround ? this.call235() * (0.21600002F / (var1 * var1 * var1)) : this.path5();
   }

   public float path5() {
      return 0.02F;
   }

   public float call235() {
      return (float)this.NbtItemSpec(EntityAttributes.MOVEMENT_SPEED);
   }

   public void ModuleStateStore(Vec3d var1) {
      Vec3d vec3d = this.EmoteManager(var1);
      Vec3d vec3d1 = this.CloudPoller(vec3d);
      if (vec3d1.lengthSquared() > 1.0E-7) {
         this.TriggerBot = this.TriggerBot.add(vec3d1);
         this.box9 = this.box9.offset(vec3d1);
      }

      boolean flag = !MathHelper.approximatelyEquals(vec3d.x, vec3d1.x);
      boolean flag1 = !MathHelper.approximatelyEquals(vec3d.z, vec3d1.z);
      this.horizontalCollision = flag || flag1;
      this.boolean95 = vec3d.y != vec3d1.y;
      this.onGround = this.boolean95 && vec3d.y < 0.0;
      if (!this.string85()) {
         this.call418();
      }

      if (this.onGround) {
         this.call083();
      } else if (vec3d.y < 0.0) {
         this.float75 = this.float75 - (float)vec3d.y;
      }

      Vec3d vec3d2 = this.vec3d22;
      if (this.horizontalCollision || this.boolean95) {
         this.vec3d22 = new Vec3d(flag ? 0.0 : vec3d2.x, this.onGround ? 0.0 : vec3d2.y, flag1 ? 0.0 : vec3d2.z);
      }

      float f = this.var110Var159();
      this.vec3d22 = this.vec3d22.multiply(f, 1.0, f);
   }

   public Vec3d CloudPoller(Vec3d var1) {
      Box box = this.box9;
      List<VoxelShape> list = Collections.emptyList();
      Vec3d vec3d;
      if (var1.lengthSquared() == 0.0) {
         vec3d = var1;
      } else {
         vec3d = Entity.adjustMovementForCollisions(this.playerEntity2, var1, box, this.playerEntity2.getEntityWorld(), list);
      }

      boolean flag = var1.x != vec3d.x;
      boolean flag1 = var1.y != vec3d.y;
      boolean flag2 = var1.z != vec3d.z;
      boolean flag3 = this.onGround || flag1 && var1.y < 0.0;
      if (this.playerEntity2.getStepHeight() > 0.0F && flag3 && (flag || flag2)) {
         Vec3d vec3d1 = Entity.adjustMovementForCollisions(
            this.playerEntity2,
            new Vec3d(var1.x, this.playerEntity2.getStepHeight(), var1.z),
            box,
            this.playerEntity2.getEntityWorld(),
            list
         );
         Vec3d vec3d2 = Entity.adjustMovementForCollisions(
            this.playerEntity2,
            new Vec3d(0.0, this.playerEntity2.getStepHeight(), 0.0),
            box.stretch(var1.x, 0.0, var1.z),
            this.playerEntity2.getEntityWorld(),
            list
         );
         Vec3d vec3d3 = Entity.adjustMovementForCollisions(
               this.playerEntity2, new Vec3d(var1.x, 0.0, var1.z), box.offset(vec3d2), this.playerEntity2.getEntityWorld(), list
            )
            .add(vec3d2);
         if (vec3d2.y < this.playerEntity2.getStepHeight() && vec3d3.horizontalLengthSquared() > vec3d1.horizontalLengthSquared()) {
            vec3d1 = vec3d3;
         }

         if (vec3d1.horizontalLengthSquared() > vec3d.horizontalLengthSquared()) {
            return vec3d1.add(
               Entity.adjustMovementForCollisions(
                  this.playerEntity2,
                  new Vec3d(0.0, -vec3d1.y + var1.y, 0.0),
                  box.offset(vec3d1),
                  this.playerEntity2.getEntityWorld(),
                  list
               )
            );
         }
      }

      return vec3d;
   }

   public void call083() {
      this.float75 = 0.0F;
   }

   public void ItemCountUtils() {
      float f = this.string123();
      if (!(f <= 1.0E-5F)) {
         this.vec3d22 = new Vec3d(this.vec3d22.x, Math.max(f, this.vec3d22.y), this.vec3d22.z);
         if (this.string122()) {
            float f1 = (float)Math.toRadians(this.yaw);
            this.vec3d22 = this.vec3d22.add(-MathHelper.sin(f1) * 0.2, 0.0, MathHelper.cos(f1) * 0.2);
         }
      }
   }

   public Vec3d EmoteMetadata(Vec3d var1) {
      if (!this.call142()) {
         return var1;
      }

      this.call083();
      double d0 = MathHelper.clamp(var1.x, -0.15F, 0.15F);
      double d1 = MathHelper.clamp(var1.z, -0.15F, 0.15F);
      double d2 = Math.max(var1.y, -0.15F);
      if (d2 < 0.0
         && !this.EventMixin_modifySetScreenArg(this.CosmeticManager(this.TriggerBot)).isOf(Blocks.SCAFFOLDING)
         && this.playerEntity2.isHoldingOntoLadder()) {
         d2 = 0.0;
      }

      return new Vec3d(d0, d2, d1);
   }

   public boolean call142() {
      BlockPos blockpos = this.CosmeticManager(this.TriggerBot);
      BlockState blockstate = this.EventMixin_modifySetScreenArg(blockpos);
      return blockstate.isIn(BlockTags.CLIMBABLE) ? true : blockstate.getBlock() instanceof TrapdoorBlock && this.ItemSpec(blockpos, blockstate);
   }

   public boolean ItemSpec(BlockPos var1, BlockState var2) {
      if (!(Boolean)var2.get(TrapdoorBlock.OPEN)) {
         return false;
      }

      BlockState blockstate = this.playerEntity2.getEntityWorld().getBlockState(var1.down());
      return blockstate.isOf(Blocks.LADDER)
         && ((Direction)blockstate.get(LadderBlock.FACING)).equals(var2.get(TrapdoorBlock.FACING));
   }

   public Vec3d EmoteManager(Vec3d var1) {
      double d0 = this.playerEntity2.getStepHeight();
      if (var1.y <= 0.0 && !this.playerEntity2.getAbilities().flying && this.call286() && this.CloudUserProfile(d0)) {
         double d1 = var1.x;
         double d2 = var1.z;
         double d3 = 0.05;
         double d4 = Math.signum(d1) * d3;
         double d5 = Math.signum(d2) * d3;

         while (d1 != 0.0 && this.NbtItemSpec(d1, 0.0, d0)) {
            if (Math.abs(d1) <= d3) {
               d1 = 0.0;
               break;
            }

            d1 -= d4;
         }

         while (d2 != 0.0 && this.NbtItemSpec(0.0, d2, d0)) {
            if (Math.abs(d2) <= d3) {
               d2 = 0.0;
               break;
            }

            d2 -= d5;
         }

         while (d1 != 0.0 && d2 != 0.0 && this.NbtItemSpec(d1, d2, d0)) {
            if (Math.abs(d1) <= d3) {
               d1 = 0.0;
            } else {
               d1 -= d4;
            }

            if (Math.abs(d2) <= d3) {
               d2 = 0.0;
               break;
            }

            d2 -= d5;
         }

         if (var1.x != d1 || var1.z != d2) {
            this.boolean162 = true;
         }

         var1 = new Vec3d(d1, var1.y, d2);
      }

      return var1;
   }

   protected boolean call286() {
      return this.var154Var159.playerInput.sneak() || this.var154Var159.boolean79;
   }

   public boolean CloudUserProfile(double var1) {
      return this.onGround || this.float75 < var1 && !this.NbtItemSpec(0.0, 0.0, var1 - this.float75);
   }

   public boolean NbtItemSpec(double var1, double var3, double var5) {
      Box box = this.box9;
      return this.playerEntity2
         .getEntityWorld()
         .isSpaceEmpty(
            this.playerEntity2,
            new Box(
               box.minX + var1, box.minY - var5 - 1.0E-5, box.minZ + var3, box.maxX + var1, box.minY, box.maxZ + var3
            )
         );
   }

   public boolean string122() {
      return this.boolean91;
   }

   public float string123() {
      return (float)this.NbtItemSpec(EntityAttributes.JUMP_STRENGTH) * this.list105() + this.int425();
   }

   public float int425() {
      if (this.ItemSpec(StatusEffects.JUMP_BOOST)) {
         StatusEffectInstance statuseffectinstance = this.TextScanner(StatusEffects.JUMP_BOOST);
         return 0.1F * (statuseffectinstance.getAmplifier() + 1);
      } else {
         return 0.0F;
      }
   }

   public float list105() {
      float f = 0.0F;
      Block block = this.EventMixin_modifySetScreenArg(this.CosmeticManager(this.TriggerBot)).getBlock();
      if (block != null) {
         f = block.getJumpVelocityMultiplier();
      }

      float f1 = 0.0F;
      Block block1 = this.EventMixin_modifySetScreenArg(this.string84()).getBlock();
      if (block1 != null) {
         f1 = block1.getJumpVelocityMultiplier();
      }

      return f == 1.0F ? f1 : f;
   }

   public boolean EnchantItemSpec(double var1, double var3, double var5) {
      return this.ItemServiceBase(this.box9.offset(var1, var3, var5));
   }

   public boolean ItemServiceBase(Box var1) {
      return this.playerEntity2.getEntityWorld().isSpaceEmpty(this.playerEntity2, var1) && !this.playerEntity2.getEntityWorld().containsFluid(var1);
   }

   public double scheduledExecutorService() {
      double d0 = this.playerEntity2.hasNoGravity() ? 0.0 : this.NbtItemSpec(EntityAttributes.GRAVITY);
      return this.vec3d22.y <= 0.0 && this.ItemSpec(StatusEffects.SLOW_FALLING) ? Math.min(d0, 0.01) : d0;
   }

   public float var110Var159() {
      BlockState blockstate = this.EventMixin_modifySetScreenArg(this.CosmeticManager(this.TriggerBot));
      float f = blockstate.getBlock().getVelocityMultiplier();
      if (!blockstate.isOf(Blocks.WATER) && !blockstate.isOf(Blocks.BUBBLE_COLUMN)) {
         return f == 1.0F ? this.EventMixin_modifySetScreenArg(this.string84()).getBlock().getVelocityMultiplier() : f;
      } else {
         return f;
      }
   }

   public void on23(TagKey<Fluid> var1) {
      this.vec3d22 = this.vec3d22.add(0.0, 0.04F, 0.0);
   }

   public BlockPos string84() {
      return BlockPos.ofFloored(this.TriggerBot.x, this.box9.minY - 0.5000001, this.TriggerBot.z);
   }

   public double long142() {
      return this.call423() < 0.4 ? 0.0 : 0.4;
   }

   public boolean string85() {
      return this.boolean96;
   }

   public boolean call214() {
      return this.object2DoubleMap.getDouble(FluidTags.LAVA) > 0.0;
   }

   public void call418() {
      if (this.playerEntity2.getVehicle() instanceof BoatEntity) {
         BoatEntity boatentity = (BoatEntity)this.playerEntity2.getVehicle();
         if (!boatentity.isSubmergedInWater()) {
            this.boolean96 = false;
            return;
         }
      }

      if (this.on23(FluidTags.WATER, 0.014)) {
         this.call083();
         this.boolean96 = true;
      } else {
         this.boolean96 = false;
      }
   }

   public void call165() {
      if (this.boolean160) {
         this.boolean160 = this.string122() && this.string85() && !this.playerEntity2.hasVehicle();
      } else {
         this.boolean160 = this.string122()
            && this.call177()
            && !this.playerEntity2.hasVehicle()
            && this.playerEntity2.getEntityWorld().getFluidState(this.CosmeticManager(this.TriggerBot)).isIn(FluidTags.WATER);
      }
   }

   public void call442() {
      if (!this.UiAnimation(EntityPose.SWIMMING)) {
         this.boolean161 = this.entityPose == EntityPose.SWIMMING && !this.string85();
      } else {
         EntityPose entitypose;
         if (this.boolean159) {
            entitypose = EntityPose.GLIDING;
         } else if (this.playerEntity2.isSleeping()) {
            entitypose = EntityPose.SLEEPING;
         } else if (this.boolean160) {
            entitypose = EntityPose.SWIMMING;
         } else if (this.playerEntity2.isUsingRiptide()) {
            entitypose = EntityPose.SPIN_ATTACK;
         } else if (this.call421() && !this.playerEntity2.getAbilities().flying) {
            entitypose = EntityPose.CROUCHING;
         } else {
            entitypose = EntityPose.STANDING;
         }

         EntityPose entitypose1;
         if (this.playerEntity2.isSpectator() || this.playerEntity2.hasVehicle() || this.UiAnimation(entitypose)) {
            entitypose1 = entitypose;
         } else if (this.UiAnimation(EntityPose.CROUCHING)) {
            entitypose1 = EntityPose.CROUCHING;
         } else {
            entitypose1 = EntityPose.SWIMMING;
         }

         this.Easing(entitypose1);
         this.boolean161 = this.entityPose == EntityPose.SWIMMING && !this.string85();
      }
   }

   public void call443() {
      this.inSneakingPose = !this.playerEntity2.getAbilities().flying
         && !this.boolean160
         && !this.playerEntity2.hasVehicle()
         && this.UiAnimation(EntityPose.CROUCHING)
         && (this.call421() || !this.playerEntity2.isSleeping() && !this.UiAnimation(EntityPose.STANDING));
   }

   public void call419() {
      if (this.playerEntity2.isUsingItem() && !this.playerEntity2.hasVehicle()) {
         this.var154Var159.float63 *= 0.2F;
         this.var154Var159.float62 *= 0.2F;
      }

      if (this.shouldSlowDown()) {
         float f = (float)this.NbtItemSpec(EntityAttributes.SNEAKING_SPEED);
         this.var154Var159.float63 *= f;
         this.var154Var159.float62 *= f;
      }
   }

   public void call420() {
      if (this.shouldStopSprinting()) {
         this.boolean91 = false;
      }

      if (this.boolean91) {
         boolean flag = !this.call444() || !this.canSprint();
         boolean flag1 = flag || this.horizontalCollision && !this.playerEntity2.collidedSoftly || this.string85() && !this.call177();
         if (this.boolean160) {
            if (!this.onGround && !this.var154Var159.playerInput.sneak() && flag || !this.string85()) {
               this.boolean91 = false;
            }
         } else if (flag1) {
            this.boolean91 = false;
         }
      }
   }

   public boolean shouldStopSprinting() {
      return this.boolean159
         || this.ItemSpec(StatusEffects.BLINDNESS)
         || this.shouldSlowDown()
         || this.playerEntity2.hasVehicle() && !this.isRidingCamel()
         || this.playerEntity2.isUsingItem() && !this.playerEntity2.hasVehicle() && !this.call177();
   }

   public boolean isRidingCamel() {
      Entity entity = this.playerEntity2.getVehicle();
      return entity != null && entity.getType() == EntityType.CAMEL;
   }

   public boolean call444() {
      return this.var154Var159.float62 > 1.0E-5F;
   }

   public boolean canSprint() {
      return this.playerEntity2.hasVehicle() || this.playerEntity2.getHungerManager().getFoodLevel() > 6.0F || this.playerEntity2.getAbilities().allowFlying;
   }

   public boolean shouldSlowDown() {
      return this.inSneakingPose || this.boolean161;
   }

   public boolean call421() {
      return this.var154Var159.playerInput.sneak();
   }

   public boolean UiAnimation(EntityPose var1) {
      return this.playerEntity2
         .getEntityWorld()
         .isSpaceEmpty(this.playerEntity2, this.playerEntity2.getDimensions(var1).getBoxAt(this.TriggerBot).contract(1.0E-7));
   }

   public void Easing(EntityPose var1) {
      if (this.entityPose != var1 || !this.box9.equals(this.playerEntity2.getDimensions(var1).getBoxAt(this.TriggerBot))) {
         this.entityPose = var1;
         this.box9 = this.playerEntity2.getDimensions(var1).getBoxAt(this.TriggerBot);
      }
   }

   public void call422() {
      this.boolean98 = this.hashSet2.contains(FluidTags.WATER);
      this.hashSet2.clear();
      double d0 = this.call445() - 0.11111111F;
      if (!(
         this.playerEntity2.getVehicle() instanceof BoatEntity boatentity
            && !boatentity.isSubmergedInWater()
            && boatentity.getBoundingBox().maxY >= d0
            && boatentity.getBoundingBox().minY <= d0
      )) {
         BlockPos blockpos = BlockPos.ofFloored(this.TriggerBot.x, d0, this.TriggerBot.z);
         FluidState fluidstate = this.playerEntity2.getEntityWorld().getFluidState(blockpos);
         double d1 = blockpos.getY() + fluidstate.getHeight(this.playerEntity2.getEntityWorld(), blockpos);
         if (d1 > d0) {
            this.hashSet2.addAll(fluidstate.streamTags().toList());
         }
      }
   }

   public double call445() {
      return this.TriggerBot.y + this.call423();
   }

   public float call423() {
      return this.playerEntity2.getDimensions(this.entityPose).eyeHeight();
   }

   public boolean call177() {
      return this.boolean98 && this.string85();
   }

   public double UiAnimation(TagKey<Fluid> var1) {
      return this.object2DoubleMap.getDouble(var1);
   }

   public boolean on23(TagKey<Fluid> var1, double var2) {
      if (this.call446()) {
         return false;
      }

      Box box = this.box9.contract(0.001);
      int i = MathHelper.floor(box.minX);
      int j = MathHelper.ceil(box.maxX);
      int k = MathHelper.floor(box.minY);
      int l = MathHelper.ceil(box.maxY);
      int i1 = MathHelper.floor(box.minZ);
      int j1 = MathHelper.ceil(box.maxZ);
      double d0 = 0.0;
      boolean flag = true;
      boolean flag1 = false;
      Vec3d vec3d = Vec3d.ZERO;
      int k1 = 0;
      Mutable mutable = new Mutable();

      for (int l1 = i; l1 < j; l1++) {
         for (int i2 = k; i2 < l; i2++) {
            for (int j2 = i1; j2 < j1; j2++) {
               mutable.set(l1, i2, j2);
               FluidState fluidstate = this.playerEntity2.getEntityWorld().getFluidState(mutable);
               if (fluidstate.isIn(var1)) {
                  double d1 = i2 + fluidstate.getHeight(this.playerEntity2.getEntityWorld(), mutable);
                  if (d1 >= box.minY) {
                     flag1 = true;
                     d0 = Math.max(d1 - box.minY, d0);
                     if (flag) {
                        Vec3d vec3d1 = fluidstate.getVelocity(this.playerEntity2.getEntityWorld(), mutable);
                        if (d0 < 0.4) {
                           vec3d1 = vec3d1.multiply(d0);
                        }

                        vec3d = vec3d.add(vec3d1);
                        k1++;
                     }
                  }
               }
            }
         }
      }

      if (vec3d.length() > 0.0) {
         if (k1 > 0) {
            vec3d = vec3d.multiply(1.0 / k1);
         }

         vec3d = vec3d.multiply(var2);
         if (Math.abs(this.vec3d22.x) < 0.003 && Math.abs(this.vec3d22.z) < 0.003 && vec3d.length() < 0.0045) {
            vec3d = vec3d.normalize().multiply(0.0045);
         }

         this.vec3d22 = this.vec3d22.add(vec3d);
      }

      this.object2DoubleMap.put(var1, d0);
      return flag1;
   }

   public boolean call446() {
      Box box = this.box9.expand(1.0);
      int i = MathHelper.floor(box.minX);
      int j = MathHelper.ceil(box.maxX);
      int k = MathHelper.floor(box.minZ);
      int l = MathHelper.ceil(box.maxZ);
      return !this.playerEntity2.getEntityWorld().isRegionLoaded(i, k, j, l);
   }

   public Vec3d call215() {
      return this.CosmeticManager(this.pitch, this.yaw);
   }

   public Vec3d CosmeticManager(float var1, float var2) {
      float f = (float)(var1 * Math.PI / 180.0);
      float f1 = (float)(-var2 * Math.PI / 180.0);
      float f2 = MathHelper.cos(f1);
      float f3 = MathHelper.sin(f1);
      float f4 = MathHelper.cos(f);
      float f5 = MathHelper.sin(f);
      return new Vec3d(f3 * f4, -f5, f2 * f4);
   }

   public boolean ItemSpec(RegistryEntry<StatusEffect> var1) {
      StatusEffectInstance statuseffectinstance = this.playerEntity2.getStatusEffect(var1);
      return statuseffectinstance != null && statuseffectinstance.getDuration() >= this.int372;
   }

   public StatusEffectInstance TextScanner(RegistryEntry<StatusEffect> var1) {
      StatusEffectInstance statuseffectinstance = this.playerEntity2.getStatusEffect(var1);
      return statuseffectinstance != null && statuseffectinstance.getDuration() >= this.int372 ? statuseffectinstance : null;
   }

   public double NbtItemSpec(RegistryEntry<EntityAttribute> var1) {
      return this.playerEntity2.getAttributes().getValue(var1);
   }

   public MovementController string66() {
      return new MovementController(
         this.playerEntity2,
         this.var154Var159,
         this.TriggerBot,
         this.vec3d22,
         this.box9,
         this.yaw,
         this.pitch,
         this.boolean91,
         this.float75,
         this.int130,
         this.boolean158,
         this.boolean159,
         this.onGround,
         this.horizontalCollision,
         this.boolean95,
         this.boolean96,
         this.boolean160,
         this.boolean98,
         this.entityPose,
         this.inSneakingPose,
         this.boolean161,
         new Object2DoubleArrayMap(this.object2DoubleMap),
         new HashSet<>(this.hashSet2)
      );
   }

   public BlockPos string67() {
      return new BlockPos(
         MathHelper.floor(this.TriggerBot.x),
         MathHelper.floor(this.TriggerBot.y),
         MathHelper.floor(this.TriggerBot.z)
      );
   }

   public BlockPos CosmeticManager(Vec3d var1) {
      return new BlockPos(MathHelper.floor(var1.x), MathHelper.floor(var1.y), MathHelper.floor(var1.z));
   }

   public BlockState EventMixin_modifySetScreenArg(BlockPos var1) {
      return this.playerEntity2.getEntityWorld().getBlockState(var1);
   }
}
