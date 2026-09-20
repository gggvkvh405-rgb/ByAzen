package org.zenith.module.player;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventTarget;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.EventTick;
import org.zenith.event.MovementInputEvent;
import org.zenith.managers.Pathfinder;
import org.zenith.managers.Pathfinder;
import org.zenith.managers.Pathfinder;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationEasing;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.CooldownTimer;

@ModuleInfo(name = "WarpFarm", category = Category.PLAYER, description = "Ищет пустую анархию и фармит на варпе pvp")
public final class WarpFarm extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final WarpFarm warpFarm = new WarpFarm();
   public static final BlockPos blockPos29 = BlockPos.ORIGIN;
   public static final double double112 = 4.0;
   public static final double double113 = 40000.0;
   public static final long long151 = 5000L;
   public static final double double114 = 16384.0;
   public static final double double115 = 900.0;
   public static final long long152 = 500L;
   public static final int int385 = 5;
   public static final int int386 = 1;
   public static final int int387 = 74;
   public static Pathfinder.PathOptions zClass073Var1653;
   private static volatile int val055;
   public static final AtomicLong atomicLong5 = new AtomicLong();
   public final ModeSetting u0420U0435U0436U0438U043c = new ModeSetting("Режим", "", "Монеты", "Опыт");
   public final NumberSetting u0421U043cU0435U0440U0442U0435U0439U0434U043eU0441U043cU0435U043dU044b = new NumberSetting(
      "Смертей до смены", 3.0F, 1.0F, 50.0F, 1.0F, "После скольких смертей искать новую анархию", ""
   );
   public final CooldownTimer zClass06742 = new CooldownTimer();
   public final CooldownTimer zClass06743 = new CooldownTimer();
   public BlockPos walkTarget;
   public BlockPos nextRouteBlock;
   public int pathFailStreak;
   public int lastAnarchy = Integer.MIN_VALUE;
   public int int388;
   public boolean boolean169;
   public int int389;
   public boolean boolean170;
   public boolean boolean171;
   public boolean boolean172;

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         if (minecraftClient3.options != null) {
            minecraftClient3.options.pauseOnLostFocus = false;
         }

         this.float310();
         if (minecraftClient3.currentScreen instanceof DeathScreen && minecraftClient3.player.deathTime > 5) {
            minecraftClient3.player.requestRespawn();
            this.stopWalking();
         } else if (minecraftClient3.player.isDead() || minecraftClient3.player.getHealth() <= 0.0F) {
            this.stopWalking();
         } else if (this.scopedRCTRepository().isActive()) {
            this.stopWalking();
         } else if (this.float311()) {
            if (this.boolean169) {
               this.float315();
            } else if (this.int389 >= (int)this.u0421U043cU0435U0440U0442U0435U0439U0434U043eU0441U043cU0435U043dU044b.getCurrent()) {
               StyledTextBuilder.RefreshCacheEvent("WarpFarm: " + this.int389 + " смертей на анке, ищу другую");
               this.float312();
            } else {
               double d0 = this.horizontalDistanceSq(Vec3d.ofCenter(blockPos29));
               if (d0 > 40000.0) {
                  this.stopWalking();
                  this.float314();
               } else if (this.var11814()) {
                  StyledTextBuilder.RefreshCacheEvent("WarpFarm: посторонний игрок у центра, всем отправлен /hub");
                  this.float313();
               } else {
                  Entity entity = this.findHologram();
                  if (entity != null) {
                     this.setWalkTarget(entity.getBlockPos());
                  } else {
                     if (!(d0 > 4.0)) {
                        this.stopWalking();
                        return;
                     }

                     this.setWalkTarget(blockPos29);
                  }

                  this.float316();
               }
            }
         }
      }
   }

   public static int call273() {
      return val055;
   }

   public static long call213() {
      return atomicLong5.get();
   }

   public static String float309() {
      return warpFarm.u0420U0435U0436U0438U043c.get();
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.stopWalking();
      this.zClass06742.EventMixin_modifySetScreenArg(0L);
      this.zClass06743.reset();
      this.lastAnarchy = Integer.MIN_VALUE;
      this.int388 = 0;
      this.boolean169 = false;
      val055 = -1;
      this.int389 = 0;
      this.boolean170 = false;
      this.pathFailStreak = 0;
      if (minecraftClient3.options != null) {
         this.boolean171 = minecraftClient3.options.pauseOnLostFocus;
         this.boolean172 = true;
         minecraftClient3.options.pauseOnLostFocus = false;
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.stopWalking();
      val055 = -1;
      if (this.boolean172 && minecraftClient3.options != null) {
         minecraftClient3.options.pauseOnLostFocus = this.boolean171;
      }

      this.boolean172 = false;
   }

   public void float310() {
      boolean flag = minecraftClient3.player.isDead() || minecraftClient3.player.getHealth() <= 0.0F;
      if (flag && !this.boolean170) {
         this.int389++;
         StyledTextBuilder.RefreshCacheEvent(
            "WarpFarm: смерть " + this.int389 + "/" + (int)this.u0421U043cU0435U0440U0442U0435U0439U0434U043eU0441U043cU0435U043dU044b.getCurrent()
         );
      }

      this.boolean170 = flag;
   }

   public boolean float311() {
      int i = this.scopedRCTRepository().currentAnarchyHere();
      if (i != this.lastAnarchy) {
         this.lastAnarchy = i;
         val055 = -1;
         this.int389 = 0;
         this.boolean170 = false;
         this.stopWalking();
         this.boolean169 = false;
         if (i != -1) {
            minecraftClient3.player.networkHandler.sendChatCommand("warp pvp");
            this.zClass06742.reset();
            this.zClass06743.reset();
            this.boolean169 = true;
         }
      }

      if (i == -1) {
         this.float312();
         return false;
      } else {
         return true;
      }
   }

   public void float312() {
      this.stopWalking();
      this.boolean169 = false;
      val055 = -1;
      this.int389 = 0;
      int i = this.scopedRCTRepository().currentAnarchyHere();

      int j;
      do {
         j = ThreadLocalRandom.current().nextInt(1, 75);
      } while (j == i || j == this.int388);

      this.int388 = j;
      this.scopedRCTRepository().reconnect(j);
   }

   public void float313() {
      val055 = -1;
      atomicLong5.incrementAndGet();
      this.stopWalking();
      minecraftClient3.player.networkHandler.sendChatCommand("hub");
      this.float312();
   }

   public void float314() {
      if (this.zClass06742.EventModifyMouseRotationInput(5000L)) {
         minecraftClient3.player.networkHandler.sendChatCommand("warp pvp");
         this.zClass06742.reset();
         this.zClass06743.reset();
      }
   }

   public void float315() {
      this.stopWalking();
      if (this.zClass06743.EventModifyMouseRotationInput(2000L)) {
         if (this.var11814()) {
            StyledTextBuilder.RefreshCacheEvent("WarpFarm: посторонний игрок у центра, всем отправлен /hub");
            this.float313();
         } else {
            Entity entity = this.findHologram();
            if (entity == null) {
               StyledTextBuilder.RefreshCacheEvent("WarpFarm: голограмма не подходит под режим «" + this.u0420U0435U0436U0438U043c.get() + "», ищу другую анку");
               this.float312();
            } else {
               this.boolean169 = false;
               val055 = this.scopedRCTRepository().currentAnarchyHere();
               StyledTextBuilder.RefreshCacheEvent("WarpFarm: анка №" + val055 + " пустая, голограмма «" + this.u0420U0435U0436U0438U043c.get() + "» подходит");
            }
         }
      }
   }

   @EventTarget
   public void ItemSpec(RotationUpdateStartEvent var1) {
      if (this.nextRouteBlock != null && minecraftClient3.player != null) {
         Vec3d vec3d = Pathfinder.EventInteractBlock(this.nextRouteBlock);
         Vec3d vec3d1 = minecraftClient3.player.getCameraPosVec(1.0F);
         Rotation ililiiili1ll1li11 = Rotation.ItemServiceBase(new Vec3d(vec3d.x, vec3d1.y, vec3d.z), vec3d1);
         this.scopedRotationManager().on23(new RotationTask(ililiiili1ll1li11, () -> {
            RotationEasing i1ii11ilil1il1ii = this.scopedRotationManager().int150();
            return i1ii11ilil1il1ii.on23(i1ii11ilil1il1ii.HudPreviewItem(), ililiiili1ll1li11);
         }, this.scopedRotationManager().int150().HudPreviewItem()), 20, this);
      }
   }

   @EventTarget
   public void onMoveInput(MovementInputEvent var1) {
      if (this.nextRouteBlock != null && minecraftClient3.player != null) {
         var1.NoSlow();
         var1.on23(1.0F, 0.0F);
         var1.TextScanner(true);
         boolean flag = minecraftClient3.world != null
            && (
               minecraftClient3.world.getBlockState(this.nextRouteBlock).getBlock() instanceof SlabBlock
                  || minecraftClient3.world.getBlockState(this.nextRouteBlock.down()).getBlock() instanceof SlabBlock
            );
         var1.EnchantItemSpec(!flag && this.nextRouteBlock.getY() > MathHelper.floor(minecraftClient3.player.getY() + 0.01));
      }
   }

   public void setWalkTarget(BlockPos var1) {
      if (!var1.equals(this.walkTarget)) {
         this.walkTarget = var1;
         this.nextRouteBlock = null;
      }
   }

   public void float316() {
      this.nextRouteBlock = null;
      if (this.walkTarget != null) {
         zClass073Var1653 = new Pathfinder.PathOptions(150000, 200, 100, 13, false, true);
         BlockPos blockpos = on23(minecraftClient3.world, minecraftClient3.player.getEntityPos(), minecraftClient3.player.getBlockPos());
         Pathfinder.Path l1liiliiiil1i_liil11l111liil1ll = Pathfinder.on23(blockpos, this.walkTarget, zClass073Var1653).orElse(null);
         System.out.println(l1liiliiiil1i_liil11l111liil1ll);
         if (l1liiliiiil1i_liil11l111liil1ll == null) {
            if (!minecraftClient3.player.isOnGround()) {
               this.nextRouteBlock = this.walkTarget;
               this.pathFailStreak = 0;
            } else if (++this.pathFailStreak >= 5) {
               this.pathFailStreak = 0;
               this.stopWalking();
               this.float314();
            }
         } else {
            this.pathFailStreak = 0;
            this.nextRouteBlock = on23(l1liiliiiil1i_liil11l111liil1ll, minecraftClient3.player.getEntityPos());
         }
      }
   }

   public void stopWalking() {
      this.walkTarget = null;
      this.nextRouteBlock = null;
   }

   public boolean var11814() {
      Vec3d vec3d = Vec3d.ofCenter(blockPos29);

      for (PlayerEntity playerentity : minecraftClient3.world.getPlayers()) {
         if (playerentity != minecraftClient3.player && !this.EnchantItemSpec(playerentity)) {
            double d0 = playerentity.getX() - vec3d.x;
            double d1 = playerentity.getZ() - vec3d.z;
            if (d0 * d0 + d1 * d1 <= 900.0) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean EnchantItemSpec(PlayerEntity var1) {
      return HeadlessBots.get(var1.getGameProfile().name()) != null;
   }

   public static BlockPos on23(World var0, Vec3d var1, BlockPos var2) {
      if (Pathfinder.on23(var0, var2, false)) {
         return var2;
      }

      BlockPos blockpos = null;
      double d0 = Double.MAX_VALUE;

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            if (i != 0 || j != 0) {
               BlockPos blockpos1 = var2.add(i, 0, j);
               if (Pathfinder.on23(var0, blockpos1, false)) {
                  double d1 = var1.x - (blockpos1.getX() + 0.5);
                  double d2 = var1.z - (blockpos1.getZ() + 0.5);
                  double d3 = d1 * d1 + d2 * d2;
                  if (d3 < d0) {
                     blockpos = blockpos1;
                     d0 = d3;
                  }
               }
            }
         }
      }

      return blockpos != null ? blockpos : var2;
   }

   public static BlockPos on23(Pathfinder.Path var0, Vec3d var1) {
      List<BlockPos> list = var0.var04();
      if (list.size() < 2) {
         return null;
      }

      int i = 0;
      double d0 = Double.MAX_VALUE;

      for (int j = 0; j < list.size(); j++) {
         BlockPos blockpos = list.get(j);
         double d1 = var1.x - (blockpos.getX() + 0.5);
         double d2 = var1.y - blockpos.getY();
         double d3 = var1.z - (blockpos.getZ() + 0.5);
         double d4 = d1 * d1 + d2 * d2 + d3 * d3;
         if (d4 <= d0) {
            i = j;
            d0 = d4;
         }
      }

      int k = i + 1;
      return k < list.size() ? list.get(k) : null;
   }

   public Entity findHologram() {
      String s = this.u0420U0435U0436U0438U043c.get().toLowerCase(Locale.ROOT);
      Entity entity = null;
      double d0 = 16384.0;

      for (Entity entity1 : minecraftClient3.world.getEntities()) {
         if (entity1 != minecraftClient3.player) {
            double d1 = minecraftClient3.player.squaredDistanceTo(entity1);
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
      double d0 = minecraftClient3.player.getX() - var1.x;
      double d1 = minecraftClient3.player.getZ() - var1.z;
      return d0 * d0 + d1 * d1;
   }
}
