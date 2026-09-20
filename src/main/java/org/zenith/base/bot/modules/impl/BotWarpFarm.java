package org.zenith.base.bot.modules.impl;

import com.darkmagician6.eventapi.EventTarget;
import java.util.Locale;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.text.Text;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.base.bot.client.BotRctService;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.event.BotTickEvent;
import org.zenith.managers.Pathfinder;
import org.zenith.managers.Pathfinder;
import org.zenith.module.Category;
import org.zenith.module.ModuleInfo;
import org.zenith.module.player.WarpFarm;
import org.zenith.rotation.Rotation;

@ModuleInfo(name = "BotWarpFarm", category = Category.PLAYER, description = "Фармит на анархии, найденной основным WarpFarm")
public class BotWarpFarm extends BotModule {
   public static final int WARP_COOLDOWN_TICKS = 100;
   public static final int SETTLE_TICKS = 80;
   public static final int RCT_RETRY_COOLDOWN_TICKS = 40;
   public static final int PATH_FAIL_LIMIT = 5;
   public int warpCooldownTicks;
   public int settleTicks;
   public int rctRetryCooldownTicks;
   public int pathFailStreak;
   public int lastAnarchy = Integer.MIN_VALUE;
   public long observedEvacuationSequence;
   public BlockPos walkTarget;
   public BlockPos nextRouteBlock;
   public BotWorld world;
   public BotPlayer player;

   @Override
   public void onEnable() {
      this.resetState();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.world = this.bot().getWorld();
      this.player = this.bot().getPlayer();
      this.stopWalking();
      this.applyInput(false, false);
      this.resetState();
      super.onDisable();
   }

   @EventTarget
   public void onBotUpdate(BotTickEvent var1) {
      this.world = var1.getWorld();
      this.player = var1.getPlayer();
      BotPlayHandler botplayhandler = this.handler();
      if (this.world != null && this.player != null && botplayhandler != null && !this.handleEmergencyEvacuation(botplayhandler)) {
         this.tickCooldowns();
         boolean flag = this.tickFarm(botplayhandler);
         if (flag && this.nextRouteBlock != null) {
            this.rotateToRouteBlock();
            this.applyRouteMovement();
         } else {
            this.applyInput(false, false);
         }
      }
   }

   public boolean tickFarm(BotPlayHandler var1) {
      if (this.player.isDead() || this.player.getHealth() <= 0.0F) {
         this.stopWalking();
         return false;
      }

      if (var1.hasOpenScreen()) {
         return false;
      }

      int i = WarpFarm.call273();
      if (i == -1) {
         this.stopWalking();
         return false;
      }

      if (this.rct().isActive()) {
         this.stopWalking();
         return false;
      }

      int j = this.rct().currentAnarchyHere();
      if (j != this.lastAnarchy) {
         this.lastAnarchy = j;
         this.settleTicks = 80;
         this.stopWalking();
      }

      if (j != i) {
         this.stopWalking();
         if (this.rctRetryCooldownTicks > 0) {
            return false;
         }

         this.rctRetryCooldownTicks = 40;
         this.bot().systemMessage("BotWarpFarm: иду на анку №" + i + " за маином");
         this.rct().reconnect(i);
         return false;
      } else {
         double d0 = this.horizontalDistanceSq(Vec3d.ofCenter(WarpFarm.blockPos29));
         if (d0 > 40000.0) {
            this.stopWalking();
            this.sendWarpPvp(var1);
            return false;
         }

         if (this.settleTicks > 0) {
            this.stopWalking();
            return false;
         }

         Entity entity = this.findHologram();
         if (entity != null) {
            this.setWalkTarget(entity.getBlockPos());
         } else {
            if (!(d0 > 4.0)) {
               this.stopWalking();
               return false;
            }

            this.setWalkTarget(WarpFarm.blockPos29);
         }

         this.updateRoute(var1);
         return true;
      }
   }

   public boolean handleEmergencyEvacuation(BotPlayHandler var1) {
      long i = WarpFarm.call213();
      if (i == this.observedEvacuationSequence) {
         return false;
      } else {
         this.stopWalking();
         this.applyInput(false, false);
         if (!this.player.isDead() && !(this.player.getHealth() <= 0.0F)) {
            this.observedEvacuationSequence = i;
            this.rct().stop();
            var1.sendCommand("hub");
            this.settleTicks = 80;
            return true;
         } else {
            return true;
         }
      }
   }

   public void tickCooldowns() {
      if (this.warpCooldownTicks > 0) {
         this.warpCooldownTicks--;
      }

      if (this.settleTicks > 0) {
         this.settleTicks--;
      }

      if (this.rctRetryCooldownTicks > 0) {
         this.rctRetryCooldownTicks--;
      }
   }

   public void sendWarpPvp(BotPlayHandler var1) {
      if (this.warpCooldownTicks <= 0) {
         var1.sendCommand("warp pvp");
         this.warpCooldownTicks = 100;
         this.settleTicks = 80;
      }
   }

   public void setWalkTarget(BlockPos var1) {
      if (!var1.equals(this.walkTarget)) {
         this.walkTarget = var1;
         this.nextRouteBlock = null;
      }
   }

   public void updateRoute(BotPlayHandler var1) {
      this.nextRouteBlock = null;
      if (this.walkTarget != null) {
         BlockPos blockpos = WarpFarm.on23(this.world, this.player.getEntityPos(), this.player.getBlockPos());
         Pathfinder.Path l1liiliiiil1i_liil11l111liil1ll = Pathfinder.on23(this.world, blockpos, this.walkTarget, WarpFarm.zClass073Var1653).orElse(null);
         if (l1liiliiiil1i_liil11l111liil1ll == null) {
            if (!this.player.isOnGround()) {
               this.nextRouteBlock = this.walkTarget;
               this.pathFailStreak = 0;
            } else if (++this.pathFailStreak >= 5) {
               this.pathFailStreak = 0;
               this.stopWalking();
               this.sendWarpPvp(var1);
            }
         } else {
            this.pathFailStreak = 0;
            this.nextRouteBlock = WarpFarm.on23(l1liiliiiil1i_liil11l111liil1ll, this.player.getEntityPos());
         }
      }
   }

   public void rotateToRouteBlock() {
      Vec3d vec3d = Pathfinder.EventInteractBlock(this.nextRouteBlock);
      Vec3d vec3d1 = this.player.getCameraPosVec(1.0F);
      Rotation ililiiili1ll1li11 = Rotation.ItemServiceBase(new Vec3d(vec3d.x, vec3d1.y, vec3d.z), vec3d1);
      this.player
         .setYaw(this.player.getYaw() + MathHelper.wrapDegrees(ililiiili1ll1li11.GrimGlide() - this.player.getYaw()));
      this.player.setPitch(MathHelper.clamp(ililiiili1ll1li11.GuiWalk(), -90.0F, 90.0F));
   }

   public void applyRouteMovement() {
      this.applyInput(true, true);
   }

   public void applyInput(boolean var1, boolean var2) {
      if (this.player != null) {
         this.player.input.movementForward = var1 ? 1.0F : 0.0F;
         this.player.input.movementSideways = 0.0F;
         this.player.input.playerInput = new PlayerInput(var1, false, false, false, var2, false, var1);
      }
   }

   public void stopWalking() {
      this.walkTarget = null;
      this.nextRouteBlock = null;
   }

   public Entity findHologram() {
      String s = WarpFarm.float309().toLowerCase(Locale.ROOT);
      Entity entity = null;
      double d0 = 16384.0;

      for (Entity entity1 : this.world.getEntities()) {
         if (entity1 != this.player) {
            double d1 = this.player.squaredDistanceTo(entity1);
            if (!(d1 > d0)) {
               Text text = entity1 instanceof TextDisplayEntity textdisplayentity ? textdisplayentity.getText() : entity1.getCustomName();
               if (text != null && text.getString().toLowerCase(Locale.ROOT).contains(s)) {
                  entity = entity1;
                  d0 = d1;
               }
            }
         }
      }

      return entity;
   }

   public double horizontalDistanceSq(Vec3d var1) {
      double d0 = this.player.getX() - var1.x;
      double d1 = this.player.getZ() - var1.z;
      return d0 * d0 + d1 * d1;
   }

   public BotRctService rct() {
      return this.bot().getRct();
   }

   public void resetState() {
      this.warpCooldownTicks = 0;
      this.settleTicks = 0;
      this.rctRetryCooldownTicks = 0;
      this.pathFailStreak = 0;
      this.lastAnarchy = Integer.MIN_VALUE;
      this.observedEvacuationSequence = WarpFarm.call213();
      this.walkTarget = null;
      this.nextRouteBlock = null;
   }
}
