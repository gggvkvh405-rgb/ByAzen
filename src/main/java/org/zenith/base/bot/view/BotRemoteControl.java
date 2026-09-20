package org.zenith.base.bot.view;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResult.Fail;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotInteractionManager;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.event.BotTickEvent;

public final class BotRemoteControl {
   public final BotClient client;
   public volatile boolean forward;
   public volatile boolean backward;
   public volatile boolean left;
   public volatile boolean right;
   public volatile boolean jump;
   public volatile boolean sneak;
   public volatile boolean sprint;
   public volatile boolean attackHeld;
   public volatile boolean useHeld;
   public final AtomicInteger attackPresses = new AtomicInteger();
   public final AtomicInteger usePresses = new AtomicInteger();
   public final AtomicInteger requestedSlot = new AtomicInteger(-1);
   public final AtomicInteger dropRequest = new AtomicInteger();
   public volatile boolean swapHandsRequested;
   public volatile boolean uiScreenOpen;
   public int attackCooldown;
   public int itemUseCooldown;
   public boolean wasMovementActive;
   public boolean wasAttackActive;
   public boolean wasUseActive;
   public volatile HitResult crosshairTarget;
   public volatile boolean attached;
   public final Object turnLock = new Object();
   public double pendingYaw;
   public double pendingPitch;
   public volatile long updateCount;

   public BotRemoteControl(BotClient var1) {
      this.client = var1;
   }

   public void attach() {
      if (!this.attached) {
         this.attached = true;
         this.client.attachRemoteControl(this);
      }
   }

   public void detach() {
      if (this.attached) {
         this.attached = false;
         this.client.detachRemoteControl(this);
         this.client.execute(() -> {
            this.releaseAll();
            BotPlayer botplayer = this.client.getPlayer();
            BotPlayHandler botplayhandler = this.client.getPlayHandler();
            if (botplayer != null && botplayhandler != null) {
               if (this.wasMovementActive) {
                  clearMovementInput(botplayer);
                  this.wasMovementActive = false;
               }

               try {
                  if (this.wasAttackActive) {
                     botplayhandler.getInteractionManager().cancelBlockBreaking();
                  }

                  if (this.wasUseActive && botplayer.isUsingItem()) {
                     botplayhandler.getInteractionManager().stopUsingItem(botplayer);
                  }
               } catch (IllegalStateException var4) {
               }

               this.wasAttackActive = false;
               this.wasUseActive = false;
            }
         });
      }
   }

   public void setMovement(boolean var1, boolean var2, boolean var3, boolean var4, boolean var5, boolean var6, boolean var7) {
      this.forward = var1;
      this.backward = var2;
      this.left = var3;
      this.right = var4;
      this.jump = var5;
      this.sneak = var6;
      this.sprint = var7;
   }

   public void releaseAll() {
      this.forward = this.backward = this.left = this.right = this.jump = this.sneak = this.sprint = false;
      this.attackHeld = false;
      this.useHeld = false;
      this.attackPresses.set(0);
      this.usePresses.set(0);
      this.dropRequest.set(0);
      this.swapHandsRequested = false;
      synchronized (this.turnLock) {
         this.pendingYaw = 0.0;
         this.pendingPitch = 0.0;
      }
   }

   public void setUiScreenOpen(boolean var1) {
      this.uiScreenOpen = var1;
   }

   public void pressAttack() {
      this.attackHeld = true;
      this.attackPresses.incrementAndGet();
   }

   public void releaseAttack() {
      this.attackHeld = false;
   }

   public void pressUse() {
      this.useHeld = true;
      this.usePresses.incrementAndGet();
   }

   public void releaseUse() {
      this.useHeld = false;
   }

   public void requestSlot(int var1) {
      if (var1 >= 0 && var1 <= 8) {
         this.requestedSlot.set(var1);
      }
   }

   public void requestDrop(boolean var1) {
      this.dropRequest.set(var1 ? 2 : 1);
   }

   public void requestSwapHands() {
      this.swapHandsRequested = true;
   }

   public void turn(double var1, double var3) {
      if (!this.uiScreenOpen) {
         synchronized (this.turnLock) {
            this.pendingYaw += var1;
            this.pendingPitch += var3;
         }
      }
   }

   public void clickSlot(int var1, int var2, SlotActionType var3) {
      this.client.execute(() -> {
         BotPlayer botplayer = this.client.getPlayer();
         BotPlayHandler botplayhandler = this.client.getPlayHandler();
         if (botplayer != null && botplayhandler != null) {
            try {
               botplayhandler.getInteractionManager().clickSlot(botplayer.currentScreenHandler.syncId, var1, var2, var3, botplayer);
            } catch (Exception var7) {
            }
         }
      });
   }

   public void closeScreen() {
      this.client.execute(() -> {
         BotPlayer botplayer = this.client.getPlayer();
         if (botplayer != null) {
            botplayer.closeHandledScreen();
         }
      });
   }

   public HitResult getCrosshairTarget() {
      return this.crosshairTarget;
   }

   public void onBotUpdate(BotTickEvent var1) {
      this.updateCount++;
      BotWorld botworld = var1.getWorld();
      BotPlayer botplayer = var1.getPlayer();
      BotPlayHandler botplayhandler = this.client.getPlayHandler();
      if (botworld != null && botplayer != null && botplayhandler != null) {
         BotInteractionManager botinteractionmanager;
         try {
            botinteractionmanager = botplayhandler.getInteractionManager();
         } catch (IllegalStateException illegalstateexception) {
            return;
         }

         if (this.attackCooldown > 0) {
            this.attackCooldown--;
         }

         if (this.itemUseCooldown > 0) {
            this.itemUseCooldown--;
         }

         this.applyPendingTurn(botplayer);

         boolean flag = this.uiScreenOpen || botplayhandler.hasOpenScreen();
         this.crosshairTarget = findCrosshairTarget(botplayer);
         int i = this.requestedSlot.getAndSet(-1);
         if (i >= 0) {
            botplayer.getInventory().selectedSlot = i;
         }

         if (flag) {
            this.attackPresses.set(0);
            this.usePresses.set(0);
            if (this.wasMovementActive) {
               clearMovementInput(botplayer);
               this.wasMovementActive = false;
            }

            if (this.wasAttackActive) {
               botinteractionmanager.cancelBlockBreaking();
               this.wasAttackActive = false;
            }

            if (this.wasUseActive) {
               if (botplayer.isUsingItem()) {
                  botinteractionmanager.stopUsingItem(botplayer);
               }

               this.wasUseActive = false;
            }
         } else {
            boolean flag1 = this.forward || this.backward || this.left || this.right || this.jump || this.sneak || this.sprint;
            if (flag1 || this.wasMovementActive) {
               this.applyMovementInput(botplayer);
            }

            this.wasMovementActive = flag1;
            int j = this.dropRequest.getAndSet(0);
            if (j > 0 && !botplayer.isSpectator()) {
               botplayer.dropSelectedItem(j == 2);
            }

            if (this.swapHandsRequested) {
               this.swapHandsRequested = false;
               if (!botplayer.isSpectator()) {
                  botplayhandler.sendPacket(new PlayerActionC2SPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
               }
            }

            if (botplayer.isUsingItem()) {
               if (!this.useHeld && this.wasUseActive) {
                  botinteractionmanager.stopUsingItem(botplayer);
                  this.wasUseActive = false;
               }

               this.attackPresses.set(0);
               this.usePresses.set(0);
            } else {
               this.wasUseActive = false;
               int k = this.attackPresses.getAndSet(0);

               for (int l = 0; l < k; l++) {
                  this.doAttack(botworld, botplayer, botinteractionmanager);
               }

               if ((this.useHeld || this.usePresses.getAndSet(0) > 0) && this.itemUseCooldown == 0) {
                  this.doItemUse(botworld, botplayer, botinteractionmanager);
                  this.wasUseActive = true;
               }
            }

            boolean flag2 = this.attackHeld;
            if (flag2 || this.wasAttackActive) {
               this.handleBlockBreaking(botworld, botplayer, botinteractionmanager, flag2);
            }

            this.wasAttackActive = flag2;
         }
      }
   }

   public boolean isAttached() {
      return this.attached;
   }

   public long getUpdateCount() {
      return this.updateCount;
   }

   public void applyMovementInput(BotPlayer var1) {
      var1.input.playerInput = new PlayerInput(this.forward, this.backward, this.left, this.right, this.jump, this.sneak, this.sprint);
      var1.input.movementForward = movementMultiplier(this.forward, this.backward);
      var1.input.movementSideways = movementMultiplier(this.left, this.right);
   }

   public void applyPendingTurn(BotPlayer var1) {
      double d0;
      double d1;
      synchronized (this.turnLock) {
         d0 = this.pendingYaw;
         d1 = this.pendingPitch;
         this.pendingYaw = 0.0;
         this.pendingPitch = 0.0;
      }

      if (d0 != 0.0 || d1 != 0.0) {
         var1.changeLookDirection(d0, d1);
      }
   }

   public static void clearMovementInput(BotPlayer var0) {
      var0.input.playerInput = PlayerInput.DEFAULT;
      var0.input.movementForward = 0.0F;
      var0.input.movementSideways = 0.0F;
   }

   public static float movementMultiplier(boolean var0, boolean var1) {
      if (var0 == var1) {
         return 0.0F;
      } else {
         return var0 ? 1.0F : -1.0F;
      }
   }

   public void doAttack(BotWorld var1, BotPlayer var2, BotInteractionManager var3) {
      if (this.attackCooldown <= 0) {
         HitResult hitresult = this.crosshairTarget;
         if (hitresult != null && !var2.isRiding()) {
            ItemStack itemstack = var2.getStackInHand(Hand.MAIN_HAND);
            if (itemstack.isItemEnabled(var1.getEnabledFeatures())) {
               switch (hitresult.getType()) {
                  case ENTITY:
                     var3.attackEntity(var2, ((EntityHitResult)hitresult).getEntity());
                     break;
                  case BLOCK:
                     BlockHitResult blockhitresult = (BlockHitResult)hitresult;
                     BlockPos blockpos = blockhitresult.getBlockPos();
                     if (!var1.getBlockState(blockpos).isAir()) {
                        var3.attackBlock(blockpos, blockhitresult.getSide());
                     } else {
                        this.onAttackMiss(var2, var3);
                     }
                     break;
                  case MISS:
                     this.onAttackMiss(var2, var3);
               }

               var2.swingHand(Hand.MAIN_HAND);
            }
         }
      }
   }

   public void onAttackMiss(BotPlayer var1, BotInteractionManager var2) {
      if (var2.getCurrentGameMode() != GameMode.CREATIVE) {
         this.attackCooldown = 10;
      }

      var1.resetTicksSinceLastAttack();
   }

   public void doItemUse(BotWorld var1, BotPlayer var2, BotInteractionManager var3) {
      if (!var3.isBreakingBlock()) {
         this.itemUseCooldown = 4;
         if (!var2.isRiding()) {
            HitResult hitresult = this.crosshairTarget;

            for (Hand hand : Hand.values()) {
               ItemStack itemstack = var2.getStackInHand(hand);
               if (!itemstack.isItemEnabled(var1.getEnabledFeatures())) {
                  return;
               }

               if (hitresult != null) {
                  switch (hitresult.getType()) {
                     case ENTITY:
                        EntityHitResult entityhitresult = (EntityHitResult)hitresult;
                        Entity entity = entityhitresult.getEntity();
                        if (!var1.getWorldBorder().contains(entity.getBlockPos())) {
                           return;
                        }

                        ActionResult actionresult1 = var3.interactEntityAtLocation(var2, entity, entityhitresult, hand);
                        if (!actionresult1.isAccepted()) {
                           actionresult1 = var3.interactEntity(var2, entity, hand);
                        }

                        if (actionresult1 instanceof Success success1) {
                           if (success1.swingSource() == SwingSource.CLIENT) {
                              var2.swingHand(hand);
                           }

                           return;
                        }
                        break;
                     case BLOCK:
                        BlockHitResult blockhitresult = (BlockHitResult)hitresult;
                        ActionResult actionresult = var3.interactBlock(var2, hand, blockhitresult);
                        if (actionresult instanceof Success success) {
                           if (success.swingSource() == SwingSource.CLIENT) {
                              var2.swingHand(hand);
                           }

                           return;
                        }

                        if (actionresult instanceof Fail) {
                           return;
                        }
                  }
               }

               if (!itemstack.isEmpty() && var3.interactItem(var2, hand) instanceof Success success2) {
                  if (success2.swingSource() == SwingSource.CLIENT) {
                     var2.swingHand(hand);
                  }

                  return;
               }
            }
         }
      }
   }

   public void handleBlockBreaking(BotWorld var1, BotPlayer var2, BotInteractionManager var3, boolean var4) {
      if (!var4) {
         this.attackCooldown = 0;
      }

      if (this.attackCooldown <= 0 && !var2.isUsingItem()) {
         HitResult hitresult = this.crosshairTarget;
         if (var4 && hitresult != null && hitresult.getType() == Type.BLOCK) {
            BlockHitResult blockhitresult = (BlockHitResult)hitresult;
            BlockPos blockpos = blockhitresult.getBlockPos();
            if (!var1.getBlockState(blockpos).isAir() && var3.updateBlockBreakingProgress(blockpos, blockhitresult.getSide())) {
               var2.swingHand(Hand.MAIN_HAND);
            }
         } else {
            var3.cancelBlockBreaking();
         }
      }
   }

   public static HitResult findCrosshairTarget(BotPlayer var0) {
      double d0 = var0.getBlockInteractionRange();
      double d1 = var0.getEntityInteractionRange();
      double d2 = Math.max(d0, d1);
      double d3 = MathHelper.square(d2);
      Vec3d vec3d = var0.getCameraPosVec(1.0F);
      HitResult hitresult = var0.raycast(d2, 1.0F, false);
      double d4 = hitresult.getPos().squaredDistanceTo(vec3d);
      if (hitresult.getType() != Type.MISS) {
         d3 = d4;
         d2 = Math.sqrt(d4);
      }

      Vec3d vec3d1 = var0.getRotationVec(1.0F);
      Vec3d vec3d2 = vec3d.add(vec3d1.x * d2, vec3d1.y * d2, vec3d1.z * d2);
      Box box = var0.getBoundingBox().stretch(vec3d1.multiply(d2)).expand(1.0, 1.0, 1.0);
      EntityHitResult entityhitresult = ProjectileUtil.raycast(var0, vec3d, vec3d2, box, EntityPredicates.CAN_HIT, d3);
      return entityhitresult != null && entityhitresult.getPos().squaredDistanceTo(vec3d) < d4
         ? ensureTargetInRange(entityhitresult, vec3d, d1)
         : ensureTargetInRange(hitresult, vec3d, d0);
   }

   public static HitResult ensureTargetInRange(HitResult var0, Vec3d var1, double var2) {
      Vec3d vec3d = var0.getPos();
      if (!vec3d.isInRange(var1, var2)) {
         Direction direction = Direction.getFacing(
            vec3d.x - var1.x, vec3d.y - var1.y, vec3d.z - var1.z
         );
         return BlockHitResult.createMissed(vec3d, direction, BlockPos.ofFloored(vec3d));
      } else {
         return var0;
      }
   }
}
