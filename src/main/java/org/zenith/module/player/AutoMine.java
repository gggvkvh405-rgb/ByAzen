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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.core.BlockFinder;
import org.zenith.core.EffectEngine;
import org.zenith.core.RegionBounds;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.MovementInputEvent;
import org.zenith.managers.Pathfinder;
import org.zenith.managers.Pathfinder;
import org.zenith.managers.Pathfinder;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.ModeSetting;
import org.zenith.util.MathUtils;
import org.zenith.util.RaycastUtils;
import org.zenith.util.ScreenUtils;

@ModuleInfo(name = "AutoMine", category = Category.PLAYER, description = "Автоматический фарм руд в мире")
public final class AutoMine extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoMine autoMine = new AutoMine();
   public static final int int46 = 40;
   public static final Pathfinder.PathOptions zClass073Var165 = Pathfinder.PathOptions.zClass073Var1652;
   public static final Set<Item> set2 = Set.of(
      Items.NETHERITE_PICKAXE, Items.DIAMOND_PICKAXE, Items.IRON_PICKAXE, Items.GOLDEN_PICKAXE, Items.STONE_PICKAXE, Items.WOODEN_PICKAXE
   );
   public final ModeSetting mode2 = new ModeSetting(
      "module.autoMine.mode", "module.autoMine.mode.desc", "module.autoMine.modeNormal", "module.autoMine.modeNether", "module.autoMine.modeCombined"
   );
   public static final int int47 = 600;
   public final Set<BlockPos> set3 = new HashSet<>();
   public RegionBounds selectedMine;
   public BlockPos nextRouteBlock;
   public int mineDataCooldown;
   public boolean combinedMineSwitched;

   @Override
   public void onEnable() {
      this.selectedMine = null;
      this.nextRouteBlock = null;
      this.mineDataCooldown = 0;
      this.combinedMineSwitched = false;
      this.set3.clear();
      StyledTextBuilder.RefreshCacheEvent("Смените версию на 1.17.1–1.20.6 в ViaFabric, если вы этого не сделали");
      super.onEnable();
   }

   @EventTarget
   public void ItemSpec(RotationUpdateStartEvent var1) {
      this.nextRouteBlock = null;
      List<AutoMine.BlockSnapshot> list = this.onTeleportation();
      if (list != null) {
         this.onRepair();
         this.onDrop();
         this.onBreak(list);
         if (!this.onMove(list)) {
            Rotation ililiiili1ll1li11 = val002.LineShader()
               .Event08((float)MathUtils.SimpleItemBuilder(-5.0, 5.0), (float)MathUtils.SimpleItemBuilder(-5.0, 5.0));
            val002.on23(new RotationTask(ililiiili1ll1li11, () -> val001.on23(val001.HudPreviewItem(), ililiiili1ll1li11), val001.HudPreviewItem()), 7, this);
         }
      }
   }

   @EventTarget
   public void StringCodec(MovementInputEvent var1) {
      if (!val003.UiAnimation().isActive()) {
         var1.ItemSpec(true);
         if (this.nextRouteBlock != null) {
            var1.NoSlow();
            var1.on23(1.0F, 0.0F);
            var1.TextScanner(true);
            if (!(minecraftClient3.world.getBlockState(this.nextRouteBlock).getBlock() instanceof SlabBlock)
               && !(minecraftClient3.world.getBlockState(this.nextRouteBlock.down()).getBlock() instanceof SlabBlock)) {
               boolean var3 = false;
            } else {
               boolean var2 = true;
            }

            boolean var4 = this.nextRouteBlock.getY() > MathHelper.floor(minecraftClient3.player.getY() + 0.01);
         }
      }
   }

   public boolean onMove(List<AutoMine.BlockSnapshot> var1) {
      BlockPos blockpos = this.findBestMovingTarget(var1);
      if (blockpos == null) {
         return false;
      }

      Pathfinder.Path l1liiliiiil1i_liil11l111liil1ll = Pathfinder.on23(
            minecraftClient3.world, minecraftClient3.player.getBlockPos(), blockpos, zClass073Var165
         )
         .orElse(null);
      this.nextRouteBlock = l1liiliiiil1i_liil11l111liil1ll == null
         ? (!minecraftClient3.player.isOnGround() ? blockpos : null)
         : l1liiliiiil1i_liil11l111liil1ll.CloudRouter(minecraftClient3.player.getEntityPos());
      if (this.nextRouteBlock == null) {
         return false;
      }

      Vec3d vec3d = minecraftClient3.player.getCameraPosVec(1.0F);
      Vec3d vec3d1 = Pathfinder.EventInteractBlock(this.nextRouteBlock);
      Rotation ililiiili1ll1li11 = Rotation.ItemServiceBase(new Vec3d(vec3d1.x, vec3d.y, vec3d1.z), vec3d);
      Rotation ililiiili1ll1li111 = val002.LineShader();
      Rotation ililiiili1ll1li112 = ililiiili1ll1li111.on23(ililiiili1ll1li111.EmoteManager(ililiiili1ll1li11))
         .Event08((float)MathUtils.SimpleItemBuilder(-5.0, 5.0), (float)MathUtils.SimpleItemBuilder(-5.0, 5.0));
      val002.on23(new RotationTask(ililiiili1ll1li112, () -> val001.on23(val001.HudPreviewItem(), ililiiili1ll1li112), val001.HudPreviewItem()), 7, this);
      return true;
   }

   public void onDrop() {
      if (minecraftClient3.currentScreen != null && minecraftClient3.currentScreen.getTitle().getString().toLowerCase(Locale.ROOT).contains("для продажи")) {
         if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler) {
            if (this.moveBuyerResources()) {
               int i = this.getContainerSlotCount(genericcontainerscreenhandler);
               if (i > 0) {
                  ScreenUtils.on23(i - 1, 0, SlotActionType.QUICK_MOVE, false);
               }
            }

            Slot slot = this.findTrashSlot();
            if (slot != null) {
               ScreenUtils.on23(slot, 1, SlotActionType.THROW, false);
            }
         }
      } else if (minecraftClient3.player.age % 40 == 0) {
         minecraftClient3.player.networkHandler.sendChatCommand("buyer");
      }
   }

   public boolean moveBuyerResources() {
      boolean flag = false;
      int i = 0;

      for (Slot slot : minecraftClient3.player.currentScreenHandler.slots) {
         if (this.isPlayerInventorySlot(slot) && this.isBuyerResource(slot.getStack())) {
            if (i > 5) {
               break;
            }

            ScreenUtils.on23(slot, 0, SlotActionType.QUICK_MOVE, false);
            i++;
            flag = true;
         }
      }

      return flag;
   }

   public boolean isBuyerResource(ItemStack var1) {
      return !var1.isEmpty() && set2.contains(var1.getItem());
   }

   public Slot findTrashSlot() {
      return minecraftClient3.player
         .currentScreenHandler
         .slots
         .stream()
         .filter(this::isPlayerInventorySlot)
         .filter(var1 -> this.isTrash(var1.getStack()))
         .findFirst()
         .orElse(null);
   }

   public boolean isPlayerInventorySlot(Slot var1) {
      if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler) {
         int i = this.getContainerSlotCount(genericcontainerscreenhandler);
         return var1.inventory == minecraftClient3.player.getInventory() || var1.id >= i && var1.id < i + 36;
      } else {
         return var1.inventory == minecraftClient3.player.getInventory() && var1.id >= 9 && var1.id <= 44;
      }
   }

   public int getContainerSlotCount(GenericContainerScreenHandler var1) {
      return Math.min(var1.getInventory().size(), Math.max(0, minecraftClient3.player.currentScreenHandler.slots.size() - 36));
   }

   public boolean isTrash(ItemStack var1) {
      return !var1.isEmpty() && !var1.isOf(Items.PLAYER_HEAD) && !var1.isOf(Items.TRIPWIRE_HOOK) && !this.isBuyerResource(var1)
         ? var1.getItem() instanceof BlockItem blockitem && !BlockFinder.ItemRegistry(blockitem.getBlock())
         : false;
   }

   public void onBreak(List<AutoMine.BlockSnapshot> var1) {
      ItemStack itemstack = minecraftClient3.player.getMainHandStack();
      if (itemstack.isIn(ItemTags.PICKAXES) && itemstack.getMaxDamage() - itemstack.getDamage() >= 30) {
         this.set3.clear();

         for (int i = 0; i < 6; i++) {
            AutoMine.RotationTarget l1i1iiilii111iilii_l1i1illlili = this.TextScanner(var1);
            if (l1i1iiilii111iilii_l1i1illlili == null) {
               break;
            }

            EffectEngine.on23(0.0, l1i1iiilii111iilii_l1i1illlili.rotation());
            if (minecraftClient3.interactionManager
               .updateBlockBreakingProgress(l1i1iiilii111iilii_l1i1illlili.hitResult().getBlockPos(), l1i1iiilii111iilii_l1i1illlili.hitResult().getSide())) {
               this.skipDrillArea(l1i1iiilii111iilii_l1i1illlili.hitResult().getBlockPos());
            }
         }
      }
   }

   public AutoMine.RotationTarget TextScanner(List<AutoMine.BlockSnapshot> var1) {
      Vec3d vec3d = minecraftClient3.player.getCameraPosVec(1.0F);
      double d0 = minecraftClient3.player.getBlockInteractionRange();
      double d1 = 5.4;
      HashSet<BlockPos> hashset = new HashSet<>();

      for (AutoMine.BlockSnapshot l1i1iiilii111iilii_ii1il11l111ii11iil : var1) {
         if (this.on23(l1i1iiilii111iilii_ii1il11l111ii11iil)) {
            hashset.add(l1i1iiilii111iilii_ii1il11l111ii11iil.pos());
         }
      }

      AutoMine.RotationTarget l1i1iiilii111iilii_l1i1illlili = null;

      for (BlockPos blockpos : hashset) {
         if (this.isWithinMineRange(blockpos, vec3d, d1, d0)) {
            Rotation ililiiili1ll1li11 = val002.LineShader().on23(val002.LineShader().EmoteManager(Rotation.ItemServiceBase(blockpos.toCenterPos(), vec3d)));
            BlockHitResult blockhitresult = RaycastUtils.on23(vec3d, ililiiili1ll1li11, d0, var1x -> var1x != null && blockpos.equals(var1x.getBlockPos()));
            if (blockhitresult != null && blockhitresult.getType() != Type.MISS) {
               AutoMine.RotationTarget l1i1iiilii111iilii_l1i1illlilix = new AutoMine.RotationTarget(
                  ililiiili1ll1li11, blockhitresult, this.countDrillCoverage(blockpos, hashset), BlockFinder.on23(vec3d, blockpos)
               );
               if (l1i1iiilii111iilii_l1i1illlili == null
                  || l1i1iiilii111iilii_l1i1illlilix.drillCoverage() > l1i1iiilii111iilii_l1i1illlili.drillCoverage()
                  || l1i1iiilii111iilii_l1i1illlilix.drillCoverage() == l1i1iiilii111iilii_l1i1illlili.drillCoverage()
                     && l1i1iiilii111iilii_l1i1illlilix.distanceSquared() < l1i1iiilii111iilii_l1i1illlili.distanceSquared()) {
                  l1i1iiilii111iilii_l1i1illlili = l1i1iiilii111iilii_l1i1illlilix;
               }
            }
         }
      }

      return l1i1iiilii111iilii_l1i1illlili;
   }

   public BlockPos findBestMovingTarget(List<AutoMine.BlockSnapshot> var1) {
      Vec3d vec3d = minecraftClient3.player.getCameraPosVec(1.0F);
      double d0 = minecraftClient3.player.getBlockInteractionRange();
      BlockPos blockpos = null;
      double d1 = Double.MAX_VALUE;

      for (AutoMine.BlockSnapshot l1i1iiilii111iilii_ii1il11l111ii11iil : var1) {
         if (this.on23(l1i1iiilii111iilii_ii1il11l111ii11iil)
            && !(Math.abs(l1i1iiilii111iilii_ii1il11l111ii11iil.pos().getY() + 0.5 - vec3d.y) > d0)) {
            double d2 = l1i1iiilii111iilii_ii1il11l111ii11iil.pos().getX() + 0.5 - vec3d.x;
            double d3 = l1i1iiilii111iilii_ii1il11l111ii11iil.pos().getZ() + 0.5 - vec3d.z;
            double d4 = d2 * d2 + d3 * d3;
            if (d4 < d1) {
               blockpos = l1i1iiilii111iilii_ii1il11l111ii11iil.pos();
               d1 = d4;
            }
         }
      }

      return blockpos != null ? new BlockPos(blockpos.getX(), minecraftClient3.player.getBlockY(), blockpos.getZ()) : null;
   }

   public boolean on23(AutoMine.BlockSnapshot var1) {
      BlockState blockstate = var1.state();
      return !this.set3.contains(var1.pos())
         && !blockstate.isAir()
         && blockstate.getFluidState().isEmpty()
         && blockstate.getHardness(minecraftClient3.world, var1.pos()) >= 0.0F
         && !blockstate.isOf(Blocks.OBSIDIAN);
   }

   public boolean isWithinMineRange(BlockPos var1, Vec3d var2, double var3, double var5) {
      Vec3d vec3d = var1.toCenterPos();
      return Math.abs(vec3d.x - var2.x) <= var3
         && Math.abs(vec3d.y - var2.y) <= var5
         && Math.abs(vec3d.z - var2.z) <= var3;
   }

   public int countDrillCoverage(BlockPos var1, Set<BlockPos> var2) {
      int i = 0;

      for (int j = -1; j <= 1; j++) {
         for (int k = -1; k <= 1; k++) {
            for (int l = -1; l <= 1; l++) {
               if (var2.contains(var1.add(j, k, l))) {
                  i++;
               }
            }
         }
      }

      return i;
   }

   public void skipDrillArea(BlockPos var1) {
      for (BlockPos blockpos : BlockPos.iterate(var1.add(-1, -1, -1), var1.add(1, 1, 1))) {
         this.set3.add(blockpos.toImmutable());
      }
   }

   public List<AutoMine.BlockSnapshot> onTeleportation() {
      if (val003.UiAnimation().isActive()) {
         return null;
      }

      if (val003.UiAnimation().currentAnarchyHere() == -1) {
         this.reconnectToRandomAnarchy();
         return null;
      }

      if (this.isWaitingForMineData()) {
         return null;
      }

      if (this.mode2.is("module.autoMine.modeCombined")) {
         return this.selectCombinedMine();
      }

      this.combinedMineSwitched = false;
      this.selectedMine = null;
      RegionBounds i11l11llllli11i111il1_ii1il11l111ii11iil = BlockFinder.BotPacketEvent(this.mode2.is("module.autoMine.modeNether"));
      if (BlockFinder.on23(i11l11llllli11i111il1_ii1il11l111ii11iil, minecraftClient3.world)
         && !(this.getMineDistance(i11l11llllli11i111il1_ii1il11l111ii11iil) > 25.0)) {
         return this.getAvailableMine(i11l11llllli11i111il1_ii1il11l111ii11iil);
      }

      this.handleWarpTp();
      return null;
   }

   public List<AutoMine.BlockSnapshot> selectCombinedMine() {
      RegionBounds i11l11llllli11i111il1_ii1il11l111ii11iil = BlockFinder.BotPacketEvent(false);
      RegionBounds i11l11llllli11i111il1_ii1il11l111ii11iil1 = BlockFinder.BotPacketEvent(true);
      if (BlockFinder.on23(i11l11llllli11i111il1_ii1il11l111ii11iil, minecraftClient3.world)
         && BlockFinder.on23(i11l11llllli11i111il1_ii1il11l111ii11iil1, minecraftClient3.world)) {
         List<AutoMine.BlockSnapshot> list = this.getArena(i11l11llllli11i111il1_ii1il11l111ii11iil);
         List<AutoMine.BlockSnapshot> list1 = this.getArena(i11l11llllli11i111il1_ii1il11l111ii11iil1);
         boolean flag = this.isMineAvailable(list);
         boolean flag1 = this.isMineAvailable(list1);
         if (this.selectedMine != null) {
            if (this.getMineDistance(this.selectedMine) > 25.0) {
               this.handleWarpTp();
               return null;
            }

            boolean flag2 = this.selectedMine.equals(i11l11llllli11i111il1_ii1il11l111ii11iil1);
            if (flag2 && flag1) {
               return list1;
            }

            if (!flag2 && flag) {
               return list;
            }

            if (this.combinedMineSwitched) {
               this.reconnectToRandomAnarchy();
               return null;
            }
         }

         if (!flag && !flag1) {
            RegionBounds i11l11llllli11i111il1_ii1il11l111ii11iil3 = this.selectedMine == null ? i11l11llllli11i111il1_ii1il11l111ii11iil : this.selectedMine;
            if (BlockFinder.on23(i11l11llllli11i111il1_ii1il11l111ii11iil3, minecraftClient3.world)
               && !(this.getMineDistance(i11l11llllli11i111il1_ii1il11l111ii11iil3) > 25.0)) {
               this.reconnectToRandomAnarchy();
               return null;
            } else {
               this.handleWarpTp();
               return null;
            }
         } else {
            RegionBounds i11l11llllli11i111il1_ii1il11l111ii11iil2 = this.selectedMine;
            boolean flag3 = flag1 && (!flag || this.getFillPercentage(list1) > this.getFillPercentage(list));
            this.selectedMine = flag3 ? i11l11llllli11i111il1_ii1il11l111ii11iil1 : i11l11llllli11i111il1_ii1il11l111ii11iil;
            if (!this.selectedMine.equals(i11l11llllli11i111il1_ii1il11l111ii11iil2)) {
               this.combinedMineSwitched = i11l11llllli11i111il1_ii1il11l111ii11iil2 != null;
               this.sendWarpCommand();
               return null;
            } else {
               return flag3 ? list1 : list;
            }
         }
      } else {
         this.handleWarpTp();
         return null;
      }
   }

   public boolean isWaitingForMineData() {
      if (minecraftClient3.player.age >= 40 && this.mineDataCooldown <= 0) {
         return false;
      }

      if (this.hasLoadedMineBlocks(this.getExpectedMineBounds())) {
         this.mineDataCooldown = 0;
         return false;
      }

      if (this.mineDataCooldown > 0) {
         this.mineDataCooldown--;
      }

      return true;
   }

   public RegionBounds getExpectedMineBounds() {
      return this.mode2.is("module.autoMine.modeCombined") && this.selectedMine != null
         ? this.selectedMine
         : BlockFinder.BotPacketEvent(this.mode2.is("module.autoMine.modeNether"));
   }

   public boolean hasLoadedMineBlocks(RegionBounds var1) {
      for (BlockPos blockpos : BlockPos.iterate(var1.call019(), var1.minY(), var1.call020(), var1.call021(), var1.call069(), var1.call022())) {
         if (minecraftClient3.world.isChunkLoaded(blockpos) && !minecraftClient3.world.getBlockState(blockpos).isAir()) {
            return true;
         }
      }

      return false;
   }

   public void displayMineDistance(RegionBounds var1) {
      StyledTextBuilder.RefreshCacheEvent(this.getMineDistance(var1) + "");
   }

   public double getMineDistance(RegionBounds var1) {
      double d0 = (var1.call019() + var1.call021() + 1.0) * 0.5;
      double d1 = (var1.call020() + var1.call022() + 1.0) * 0.5;
      double d2 = Math.abs(minecraftClient3.player.getX() - d0);
      double d3 = Math.abs(minecraftClient3.player.getZ() - d1);
      return Math.hypot(d2, d3);
   }

   public List<AutoMine.BlockSnapshot> getAvailableMine(RegionBounds var1) {
      List<AutoMine.BlockSnapshot> list = this.getArena(var1);
      if (!this.isMineAvailable(list)) {
         this.reconnectToRandomAnarchy();
         return null;
      } else {
         return list;
      }
   }

   public boolean isMineAvailable(List<AutoMine.BlockSnapshot> var1) {
      return var1.size() - this.countAirBlocks(var1) < 600L ? false : !minecraftClient3.player.isOnGround() || this.hasReachableBlockByHeight(var1);
   }

   public boolean hasReachableBlockByHeight(List<AutoMine.BlockSnapshot> var1) {
      int i = MathHelper.floor(minecraftClient3.player.getEyeY() + minecraftClient3.player.getBlockInteractionRange() - 1.0);
      return var1.stream()
         .anyMatch(var1x -> !var1x.state().isAir() && !var1x.blockState().isOf(Blocks.OBSIDIAN) && var1x.pos().getY() <= i);
   }

   public long countAirBlocks(List<AutoMine.BlockSnapshot> var1) {
      return var1.stream().filter(var0 -> var0.state().isAir()).count();
   }

   public double getFillPercentage(List<AutoMine.BlockSnapshot> var1) {
      return var1.isEmpty() ? 0.0 : 1.0 - (double)this.countAirBlocks(var1) / var1.size();
   }

   public void onRepair() {
      if (minecraftClient3.player.age % 20 == 0) {
         ItemStack itemstack = minecraftClient3.player.getMainHandStack();
         if (itemstack.isIn(ItemTags.PICKAXES) && itemstack.getMaxDamage() - itemstack.getDamage() < 30) {
            if (minecraftClient3.player.getStackInHand(Hand.OFF_HAND).isOf(Items.EXPERIENCE_BOTTLE)) {
               EffectEngine.useItem(Hand.OFF_HAND);
            } else {
               Slot slot = ScreenUtils.SimpleItemBuilder(Items.EXPERIENCE_BOTTLE);
               if (slot == null) {
                  StyledTextBuilder.RefreshCacheEvent("AutoMine: в хотбаре закончились пузырьки опыта");
                  this.toggle();
               } else {
                  ScreenUtils.on23(slot, Hand.OFF_HAND, true);
               }
            }
         }
      }
   }

   public List<AutoMine.BlockSnapshot> getArena(RegionBounds var1) {
      return this.getBlocksInZone(new BlockPos(var1.call019(), var1.minY(), var1.call020()), new BlockPos(var1.call021(), var1.call069(), var1.call022()));
   }

   public List<AutoMine.BlockSnapshot> getBlocksInZone(BlockPos var1, BlockPos var2) {
      List<AutoMine.BlockSnapshot> arraylist = new ArrayList<>();
      int i = Math.min(var1.getX(), var2.getX());
      int j = Math.max(var1.getX(), var2.getX());
      int k = Math.min(var1.getY(), var2.getY());
      int l = Math.max(var1.getY(), var2.getY());
      int i1 = Math.min(var1.getZ(), var2.getZ());
      int j1 = Math.max(var1.getZ(), var2.getZ());

      for (int k1 = i; k1 <= j; k1++) {
         for (int l1 = k; l1 <= l; l1++) {
            for (int i2 = i1; i2 <= j1; i2++) {
               BlockPos blockpos = new BlockPos(k1, l1, i2);
               arraylist.add(new AutoMine.BlockSnapshot(blockpos, minecraftClient3.world.getBlockState(blockpos)));
            }
         }
      }

      return arraylist;
   }

   public void handleWarpTp() {
      if (minecraftClient3.player.age % 60 == 0) {
         this.sendWarpCommand();
      }
   }

   public void sendWarpCommand() {
      boolean flag = this.mode2.is("module.autoMine.modeCombined")
         ? BlockFinder.BotPacketEvent(true).equals(this.selectedMine)
         : this.mode2.is("module.autoMine.modeNether");
      minecraftClient3.getNetworkHandler().sendChatCommand(flag ? "warp mine2" : "warp mine");
      this.mineDataCooldown = 20;
   }

   public void reconnectToRandomAnarchy() {
      this.selectedMine = null;
      this.nextRouteBlock = null;
      this.mineDataCooldown = 0;
      this.combinedMineSwitched = false;
      this.set3.clear();
      int i = val003.UiAnimation().currentAnarchyHere();

      int j;
      do {
         j = ThreadLocalRandom.current().nextInt(1, 64);
      } while (j == i || j == 2 || j == 17 || j == 33 || j == 49);

      val003.UiAnimation().reconnect(j);
   }

   public ModeSetting getMineMode() {
      return this.mode2;
   }


   public record RotationTarget(Rotation var1187, BlockHitResult blockHitResult3, int int149, double double35) {
      public Rotation rotation() {
         return this.var1187;
      }

      public BlockHitResult hitResult() {
         return this.blockHitResult3;
      }

      public int drillCoverage() {
         return this.int149;
      }

      public double distanceSquared() {
         return this.double35;
      }
   }

   public record BlockSnapshot(BlockPos blockPos25, BlockState blockState) {
      public BlockPos pos() {
         return this.blockPos25;
      }

      public BlockState state() {
         return this.blockState;
      }
   }
}
