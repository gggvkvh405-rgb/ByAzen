package org.zenith.base.bot.world;

import java.util.Objects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractBoatEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket.Mode;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.OnGroundOnly;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.PositionAndOnGround;
import net.minecraft.network.packet.c2s.play.UpdatePlayerAbilitiesC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Direction.AxisDirection;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.core.ItemRegistry;

public class BotPlayer extends PlayerEntity {
   public final BotPlayHandler networkHandler;
   public double lastX;
   public double lastBaseY;
   public double lastZ;
   public float lastYaw;
   public float lastPitch;
   public boolean lastOnGround;
   public boolean lastHorizontalCollision;
   public boolean inSneakingPose;
   public boolean lastSneaking;
   public boolean lastSprinting;
   public int ticksSinceLastPositionPacketSent;
   public boolean healthInitialized;
   public BotInput input = new BotInput();
   public PlayerInput lastPlayerInput = PlayerInput.DEFAULT;
   public int clientPermissionLevel = 0;
   public boolean usingItem;
   public Hand activeHand;
   public boolean riding;
   public boolean falling;
   private boolean loaded;

   public BotPlayer(BotWorld var1, BotPlayHandler var2, boolean var3, boolean var4) {
      super(var1, var2.getProfile());
      this.networkHandler = var2;
      this.lastSneaking = var3;
      this.lastSprinting = var4;
   }

   public BotWorld getBotWorld() {
      return this.getWorld();
   }

   /** Keeps the bot code independent from Mojang's renamed Entity world accessor. */
   public BotWorld getWorld() {
      return (BotWorld)this.getEntityWorld();
   }

   public boolean isLoaded() {
      return this.loaded;
   }

   public void setLoaded(boolean loaded) {
      this.loaded = loaded;
   }

   @Override
   public GameMode getGameMode() {
      return this.networkHandler.getInteractionManager().getCurrentGameMode();
   }

   public void heal(float amount) {
   }

   public void dismountVehicle() {
      super.dismountVehicle();
      this.riding = false;
   }

   public float getPitch(float tickDelta) {
      return this.getPitch();
   }

   public float getYaw(float tickDelta) {
      return this.hasVehicle() ? super.getYaw(tickDelta) : this.getYaw();
   }

   public void tick() {
      if (this.isLoaded()) {
         super.tick();
         this.sendSneakingPacket();
         if (!this.lastPlayerInput.equals(this.input.playerInput)) {
            this.networkHandler.sendPacket(new PlayerInputC2SPacket(this.input.playerInput));
            this.lastPlayerInput = this.input.playerInput;
         }

         if (this.hasVehicle()) {
            this.networkHandler.sendPacket(new LookAndOnGround(this.getYaw(), this.getPitch(), this.isOnGround(), this.horizontalCollision));
            Entity entity = this.getRootVehicle();
            if (entity != this && entity.isLogicalSideForUpdatingMovement()) {
               this.networkHandler.sendPacket(VehicleMoveC2SPacket.fromVehicle(entity));
               this.sendSprintingPacket();
            }
         } else {
            this.sendMovementPackets();
         }
      }
   }

   public void sendMovementPackets() {
      this.sendSprintingPacket();
      double d0 = this.getX() - this.lastX;
      double d1 = this.getY() - this.lastBaseY;
      double d2 = this.getZ() - this.lastZ;
      double d3 = this.getYaw() - this.lastYaw;
      double d4 = this.getPitch() - this.lastPitch;
      this.ticksSinceLastPositionPacketSent++;
      boolean flag = MathHelper.squaredMagnitude(d0, d1, d2) > MathHelper.square(2.0E-4) || this.ticksSinceLastPositionPacketSent >= 20;
      boolean flag1 = d3 != 0.0 || d4 != 0.0;
      if (flag && flag1) {
         this.networkHandler
            .sendPacket(
               new Full(
                  this.getX(), this.getY(), this.getZ(), this.getYaw(), this.getPitch(), this.isOnGround(), this.horizontalCollision
               )
            );
      } else if (flag) {
         this.networkHandler.sendPacket(new PositionAndOnGround(this.getX(), this.getY(), this.getZ(), this.isOnGround(), this.horizontalCollision));
      } else if (flag1) {
         this.networkHandler.sendPacket(new LookAndOnGround(this.getYaw(), this.getPitch(), this.isOnGround(), this.horizontalCollision));
      } else if (this.lastOnGround != this.isOnGround() || this.lastHorizontalCollision != this.horizontalCollision) {
         this.networkHandler.sendPacket(new OnGroundOnly(this.isOnGround(), this.horizontalCollision));
      }

      if (flag) {
         this.lastX = this.getX();
         this.lastBaseY = this.getY();
         this.lastZ = this.getZ();
         this.ticksSinceLastPositionPacketSent = 0;
      }

      if (flag1) {
         this.lastYaw = this.getYaw();
         this.lastPitch = this.getPitch();
      }

      this.lastOnGround = this.isOnGround();
      this.lastHorizontalCollision = this.horizontalCollision;
   }

   public final void sendSneakingPacket() {
      boolean flag = this.isSneaking();
      if (flag != this.lastSneaking) {
         this.lastSneaking = flag;
      }
   }

   public final void sendSprintingPacket() {
      boolean flag = this.isSprinting();
      if (flag != this.lastSprinting) {
         Mode mode = flag ? Mode.START_SPRINTING : Mode.STOP_SPRINTING;
         this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, mode));
         this.lastSprinting = flag;
      }
   }

   public boolean dropSelectedItem(boolean var1) {
      Action action = var1 ? Action.DROP_ALL_ITEMS : Action.DROP_ITEM;
      ItemStack itemstack = this.getInventory().dropSelectedItem(var1);
      this.networkHandler.sendPacket(new PlayerActionC2SPacket(action, BlockPos.ORIGIN, Direction.DOWN));
      return !itemstack.isEmpty();
   }

   public void swingHand(Hand hand) {
      super.swingHand(hand);
      this.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
   }

   public void requestRespawn() {
      this.networkHandler.sendPacket(new ClientStatusC2SPacket(net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket.Mode.PERFORM_RESPAWN));
   }

   public void closeHandledScreen() {
      this.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(this.currentScreenHandler.syncId));
      this.closeScreen();
   }

   public void closeScreen() {
      super.closeHandledScreen();
      this.networkHandler.clearOpenScreen();
   }

   public void updateHealth(float var1) {
      if (this.healthInitialized) {
         float f = this.getHealth() - var1;
         if (f <= 0.0F) {
            this.setHealth(var1);
            if (f < 0.0F) {
               this.timeUntilRegen = 10;
            }
         } else {
            this.lastDamageTaken = f;
            this.timeUntilRegen = 20;
            this.setHealth(var1);
            this.maxHurtTime = 10;
            this.hurtTime = this.maxHurtTime;
         }
      } else {
         this.setHealth(var1);
         this.healthInitialized = true;
      }
   }

   public void sendAbilitiesUpdate() {
      this.networkHandler.sendPacket(new UpdatePlayerAbilitiesC2SPacket(this.getAbilities()));
   }

   public boolean isMainPlayer() {
      return true;
   }

   public boolean isHoldingOntoLadder() {
      return !this.getAbilities().flying && super.isHoldingOntoLadder();
   }

   public boolean shouldSpawnSprintingParticles() {
      return false;
   }

   public int getPermissionLevel() {
      return this.clientPermissionLevel;
   }

   public void setClientPermissionLevel(int var1) {
      this.clientPermissionLevel = var1;
   }

   public void sendMessage(Text message, boolean overlay) {
      this.networkHandler.getClient().onChat(message);
   }

   public void pushOutOfBlocks(double var1, double var3) {
      BlockPos blockpos = BlockPos.ofFloored(var1, this.getY(), var3);
      if (this.wouldCollideAt(blockpos)) {
         double d0 = var1 - blockpos.getX();
         double d1 = var3 - blockpos.getZ();
         Direction direction = null;
         double d2 = Double.MAX_VALUE;
         Direction[] adirection = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

         for (Direction direction1 : adirection) {
            double d3 = direction1.getAxis().choose(d0, 0.0, d1);
            double d4 = direction1.getDirection() == AxisDirection.POSITIVE ? 1.0 - d3 : d3;
            if (d4 < d2 && !this.wouldCollideAt(blockpos.offset(direction1))) {
               d2 = d4;
               direction = direction1;
            }
         }

         if (direction != null) {
            Vec3d vec3d = this.getVelocity();
            if (direction.getAxis() == Axis.X) {
               this.setVelocity(0.1 * direction.getOffsetX(), vec3d.y, vec3d.z);
            } else {
               this.setVelocity(vec3d.x, vec3d.y, 0.1 * direction.getOffsetZ());
            }
         }
      }
   }

   public boolean wouldCollideAt(BlockPos var1) {
      Box box = this.getBoundingBox();
      Box box1 = new Box(
            var1.getX(), box.minY, var1.getZ(), var1.getX() + 1.0, box.maxY, var1.getZ() + 1.0
         )
         .contract(1.0E-7);
      return this.getWorld().canCollide(this, box1);
   }

   public void setExperience(float var1, int var2, int var3) {
      this.experienceProgress = var1;
      this.totalExperience = var2;
      this.experienceLevel = var3;
   }

   public void handleStatus(byte status) {
      if (status >= 24 && status <= 28) {
         this.setClientPermissionLevel(status - 24);
      } else {
         super.handleStatus(status);
      }
   }

   public void playSound(ItemRegistry sound, float volume, float pitch) {
   }

   public void playSoundToPlayer(ItemRegistry sound, SoundCategory category, float volume, float pitch) {
   }

   public boolean canMoveVoluntarily() {
      return true;
   }

   public void setCurrentHand(Hand hand) {
      ItemStack itemstack = this.getStackInHand(hand);
      if (!itemstack.isEmpty() && !this.isUsingItem()) {
         super.setCurrentHand(hand);
         this.usingItem = true;
         this.activeHand = hand;
      }
   }

   public boolean isUsingItem() {
      return this.usingItem;
   }

   public void clearActiveItem() {
      super.clearActiveItem();
      this.usingItem = false;
   }

   public Hand getActiveHand() {
      return Objects.requireNonNullElse(this.activeHand, Hand.MAIN_HAND);
   }

   public void onTrackedDataSet(TrackedData<?> data) {
      super.onTrackedDataSet(data);
      if (LIVING_FLAGS.equals(data)) {
         boolean flag = ((Byte)this.dataTracker.get(LIVING_FLAGS) & 1) > 0;
         Hand hand = (this.dataTracker.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
         if (flag && !this.usingItem) {
            this.setCurrentHand(hand);
         } else if (!flag && this.usingItem) {
            this.clearActiveItem();
         }
      }
   }

   public JumpingMount getJumpingMount() {
      return this.getControllingVehicle() instanceof JumpingMount jumpingmount && jumpingmount.canJump() ? jumpingmount : null;
   }

   public boolean isSneaking() {
      return this.input.playerInput.sneak();
   }

   public boolean isInSneakingPose() {
      return this.inSneakingPose;
   }

   public boolean shouldSlowDown() {
      return this.isInSneakingPose() || this.isCrawling();
   }

   public void tickNewAi() {
      super.tickNewAi();
      this.sidewaysSpeed = this.input.movementSideways;
      this.forwardSpeed = this.input.movementForward;
      this.jumping = this.input.playerInput.jump();
   }

   public void init() {
      this.setPose(EntityPose.STANDING);
      if (this.getWorld() != null) {
         for (double d0 = this.getY(); d0 > this.getWorld().getBottomY() && d0 <= this.getWorld().getTopYInclusive(); d0++) {
            this.setPosition(this.getX(), d0, this.getZ());
            if (this.getWorld().isSpaceEmpty(this)) {
               break;
            }
         }

         this.setVelocity(Vec3d.ZERO);
         this.setPitch(0.0F);
      }

      this.setHealth(this.getMaxHealth());
      this.deathTime = 0;
   }

   public void tickMovement() {
      this.tickPortalCooldown();
      boolean flag = this.input.playerInput.jump();
      boolean flag1 = this.input.playerInput.sneak();
      boolean flag2 = this.isWalking();
      PlayerAbilities playerabilities = this.getAbilities();
      this.inSneakingPose = !playerabilities.flying
         && !this.isSwimming()
         && !this.hasVehicle()
         && this.canChangeIntoPose(EntityPose.CROUCHING)
         && (this.isSneaking() || !this.isSleeping() && !this.canChangeIntoPose(EntityPose.STANDING));
      this.input.tick();
      if (this.shouldStopSprinting()) {
         this.setSprinting(false);
      }

      if (this.isUsingItem() && !this.hasVehicle()) {
         this.input.movementSideways *= 0.2F;
         this.input.movementForward *= 0.2F;
      }

      if (this.shouldSlowDown()) {
         float f = (float)this.getAttributeValue(EntityAttributes.SNEAKING_SPEED);
         this.input.movementSideways *= f;
         this.input.movementForward *= f;
      }

      if (!this.noClip) {
         this.pushOutOfBlocks(this.getX() - this.getWidth() * 0.35, this.getZ() + this.getWidth() * 0.35);
         this.pushOutOfBlocks(this.getX() - this.getWidth() * 0.35, this.getZ() - this.getWidth() * 0.35);
         this.pushOutOfBlocks(this.getX() + this.getWidth() * 0.35, this.getZ() - this.getWidth() * 0.35);
         this.pushOutOfBlocks(this.getX() + this.getWidth() * 0.35, this.getZ() + this.getWidth() * 0.35);
      }

      boolean flag7 = this.canStartSprinting();
      boolean flag3 = this.hasVehicle() ? this.getVehicle().isOnGround() : this.isOnGround();
      boolean flag4 = !flag1 && !flag2;
      if ((flag3 || this.isSubmergedInWater()) && flag4 && flag7 && this.input.playerInput.sprint()) {
         this.setSprinting(true);
      }

      if ((!this.isTouchingWater() || this.isSubmergedInWater()) && flag7 && this.input.playerInput.sprint()) {
         this.setSprinting(true);
      }

      if (this.isSprinting()) {
         boolean flag5 = !this.input.hasForwardMovement() || !this.canSprint();
         boolean flag6 = flag5 || this.horizontalCollision && !this.collidedSoftly || this.isTouchingWater() && !this.isSubmergedInWater();
         if (this.isSwimming()) {
            if (!this.isOnGround() && !this.input.playerInput.sneak() && flag5 || !this.isTouchingWater()) {
               this.setSprinting(false);
            }
         } else if (flag6) {
            this.setSprinting(false);
         }
      }

      boolean flag8 = false;
      if (playerabilities.allowFlying) {
         if (this.networkHandler.getInteractionManager().isFlyingLocked()) {
            if (!playerabilities.flying) {
               playerabilities.flying = true;
               flag8 = true;
               this.sendAbilitiesUpdate();
            }
         } else if (!flag && this.input.playerInput.jump()) {
            if (this.abilityResyncCountdown == 0) {
               this.abilityResyncCountdown = 7;
            } else if (!this.isSwimming()) {
               playerabilities.flying = !playerabilities.flying;
               if (playerabilities.flying && this.isOnGround()) {
                  this.jump();
               }

               flag8 = true;
               this.sendAbilitiesUpdate();
               this.abilityResyncCountdown = 0;
            }
         }
      }

      if (this.input.playerInput.jump() && !flag8 && !flag && !this.isClimbing() && this.checkGliding()) {
         this.networkHandler.sendPacket(new ClientCommandC2SPacket(this, Mode.START_FALL_FLYING));
      }

      this.falling = this.isGliding();
      if (this.isTouchingWater() && this.input.playerInput.sneak() && this.shouldSwimInFluids()) {
         this.knockDownwards();
      }

      if (playerabilities.flying) {
         int i = 0;
         if (this.input.playerInput.sneak()) {
            i--;
         }

         if (this.input.playerInput.jump()) {
            i++;
         }

         if (i != 0) {
            this.setVelocity(this.getVelocity().add(0.0, i * playerabilities.getFlySpeed() * 3.0F, 0.0));
         }
      }

      super.tickMovement();
      if (this.isOnGround() && playerabilities.flying && !this.networkHandler.getInteractionManager().isFlyingLocked()) {
         playerabilities.flying = false;
         this.sendAbilitiesUpdate();
      }
   }

   public boolean shouldStopSprinting() {
      return this.isGliding()
         || this.isBlind()
         || this.shouldSlowDown()
         || this.hasVehicle() && !this.isRidingCamel()
         || this.isUsingItem() && !this.hasVehicle() && !this.isSubmergedInWater();
   }

   public boolean isRidingCamel() {
      return this.getVehicle() != null && this.getVehicle().getType() == EntityType.CAMEL;
   }

   public final boolean isBlind() {
      return this.hasStatusEffect(StatusEffects.BLINDNESS);
   }

   protected void updatePostDeath() {
      this.deathTime++;
      if (this.deathTime == 20) {
         this.remove(RemovalReason.KILLED);
      }
   }

   public void tickRiding() {
      super.tickRiding();
      this.riding = false;
      if (this.getControllingVehicle() instanceof AbstractBoatEntity abstractboatentity) {
         abstractboatentity.setInputs(
            this.input.playerInput.left(), this.input.playerInput.right(), this.input.playerInput.forward(), this.input.playerInput.backward()
         );
         this.riding = this.riding
            | (
               this.input.playerInput.left()
                  || this.input.playerInput.right()
                  || this.input.playerInput.forward()
                  || this.input.playerInput.backward()
            );
      }
   }

   public boolean isRiding() {
      return this.riding;
   }

   protected boolean hasCollidedSoftly(Vec3d adjustedMovement) {
      float f = this.getYaw() * (float) (Math.PI / 180.0);
      double d0 = MathHelper.sin(f);
      double d1 = MathHelper.cos(f);
      double d2 = this.sidewaysSpeed * d1 - this.forwardSpeed * d0;
      double d3 = this.forwardSpeed * d1 + this.sidewaysSpeed * d0;
      double d4 = MathHelper.square(d2) + MathHelper.square(d3);
      double d5 = MathHelper.square(adjustedMovement.x) + MathHelper.square(adjustedMovement.z);
      if (!(d4 < 1.0E-5F) && !(d5 < 1.0E-5F)) {
         double d6 = d2 * adjustedMovement.x + d3 * adjustedMovement.z;
         double d7 = Math.acos(d6 / Math.sqrt(d4 * d5));
         return d7 < 0.13962634F;
      } else {
         return false;
      }
   }

   public boolean canStartSprinting() {
      return !this.isSprinting()
         && this.isWalking()
         && this.canSprint()
         && !this.isUsingItem()
         && !this.isBlind()
         && (!this.hasVehicle() || this.canVehicleSprint(this.getVehicle()))
         && !this.isGliding()
         && (!this.shouldSlowDown() || this.isSubmergedInWater());
   }

   public boolean canVehicleSprint(Entity var1) {
      return var1.canSprintAsVehicle() && var1.isLogicalSideForUpdatingMovement();
   }

   public final boolean isWalking() {
      return this.isSubmergedInWater() ? this.input.hasForwardMovement() : this.input.movementForward >= 0.8;
   }

   public final boolean canSprint() {
      return this.hasVehicle() || this.getHungerManager().getFoodLevel() > 6.0F || this.getAbilities().allowFlying;
   }

   public void onGameModeChanged(GameMode var1) {
      if (var1 == GameMode.SPECTATOR) {
         this.setVelocity(this.getVelocity().withAxis(Axis.Y, 0.0));
      }
   }

   public boolean isSubmergedInWater() {
      return this.isSubmergedInWater;
   }

   protected boolean updateWaterSubmersionState() {
      super.updateWaterSubmersionState();
      return this.isSubmergedInWater;
   }

   public float getBodyYaw() {
      return this.getYaw();
   }

   public boolean isSpectator() {
      return this.networkHandler.getInteractionManager().getCurrentGameMode() == GameMode.SPECTATOR;
   }

   public boolean isCreative() {
      return this.networkHandler.getInteractionManager().getCurrentGameMode() == GameMode.CREATIVE;
   }

   public boolean isSubmergedInWaterFlag() {
      return this.isSubmergedIn(FluidTags.WATER);
   }
}
