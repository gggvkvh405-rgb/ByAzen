package org.zenith.base.bot.modules.impl;

import com.darkmagician6.eventapi.EventTarget;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.Full;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.Hand;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.zenith.base.bot.client.BotRctService;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotInteractionManager;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotRaytracing;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.core.BlockFinder;
import org.zenith.core.RegionBounds;
import org.zenith.event.BotTickEvent;
import org.zenith.managers.Pathfinder;
import org.zenith.managers.Pathfinder;
import org.zenith.managers.Pathfinder;
import org.zenith.managers.Pathfinder;
import org.zenith.module.Category;
import org.zenith.module.ModuleInfo;
import org.zenith.rotation.Rotation;
import org.zenith.setting.ModeSetting;
import org.zenith.util.MathUtils;

@ModuleInfo(name = "BotAutoMine", category = Category.PLAYER, description = "Автоматический фарм руд в мире")
public class BotAutoMine extends BotModule {
   public static final int MINE_DATA_COOLDOWN_TICKS = 40;
   public static final Pathfinder.PathOptions MOVE_PATH_SETTINGS = new Pathfinder.PathOptions(30000, 192, 100, 130, false, true, Pathfinder.NodeType.val317);
   public static final Set<Item> BUYER_ITEMS = Set.of(
      Items.REDSTONE,
      Items.QUARTZ,
      Items.DIAMOND,
      Items.ENDER_PEARL,
      Items.END_STONE,
      Items.LAPIS_LAZULI,
      Items.IRON_INGOT,
      Items.GOLD_INGOT,
      Items.GOLD_NUGGET,
      Items.OBSIDIAN,
      Items.NETHER_BRICK,
      Items.NETHER_BRICKS,
      Items.COAL
   );
   public final ModeSetting mineMode = new ModeSetting(
      "module.autoMine.mode", "module.autoMine.mode.desc", "module.autoMine.modeNormal", "module.autoMine.modeNether", "module.autoMine.modeCombined"
   );
   public static final int airBlocks = 600;
   public final Set<BlockPos> skippedBlocks = new HashSet<>();
   public RegionBounds selectedMine;
   public BlockPos nextRouteBlock;
   public int mineDataCooldown;
   public boolean combinedMineSwitched;
   public BotWorld world;
   public BotPlayer player;
   public BotInteractionManager interaction;
   public Rotation currentRotation;

   @Override
   public void onEnable() {
      this.selectedMine = null;
      this.nextRouteBlock = null;
      this.mineDataCooldown = 0;
      this.combinedMineSwitched = false;
      this.skippedBlocks.clear();
      super.onEnable();
   }

   @Override
   public void onDisable() {
      this.bindContext();
      this.applyInput(false);
      super.onDisable();
   }

   @EventTarget
   public void onBotUpdate(BotTickEvent var1) {
      this.world = var1.getWorld();
      this.player = var1.getPlayer();
      BotPlayHandler botplayhandler = this.handler();
      this.interaction = botplayhandler == null ? null : botplayhandler.getInteractionManager();
      if (this.world != null && this.player != null && this.interaction != null && botplayhandler != null) {
         this.currentRotation = new Rotation(this.player.getYaw(), this.player.getPitch());
         this.nextRouteBlock = null;
         List<BotAutoMine_BlockMine> list = this.onTeleportation();
         if (list != null) {
            this.onRepair();
            this.onDrop();
            this.onBreak(list);
            if (!this.onMove(list)) {
               this.setRotation(this.currentRotation.Event08((float)MathUtils.SimpleItemBuilder(-5.0, 5.0), (float)MathUtils.SimpleItemBuilder(-5.0, 5.0)));
            }
         }

         this.applyInput(!this.rct().isActive());
      }
   }

   public void bindContext() {
      this.world = this.world();
      this.player = this.player();
      this.interaction = this.interaction();
   }

   public boolean onMove(List<BotAutoMine_BlockMine> var1) {
      BlockPos blockpos = this.findBestMovingTarget(var1);
      if (blockpos == null) {
         return false;
      }

      Pathfinder.Path l1liiliiiil1i_liil11l111liil1ll = Pathfinder.on23(this.world, this.player.getBlockPos(), blockpos, MOVE_PATH_SETTINGS).orElse(null);
      this.nextRouteBlock = l1liiliiiil1i_liil11l111liil1ll == null
         ? (!this.player.isOnGround() ? blockpos : null)
         : l1liiliiiil1i_liil11l111liil1ll.CloudRouter(this.player.getEntityPos());
      if (this.nextRouteBlock == null) {
         return false;
      }

      Vec3d vec3d = this.player.getCameraPosVec(1.0F);
      Vec3d vec3d1 = Pathfinder.EventInteractBlock(this.nextRouteBlock);
      Rotation ililiiili1ll1li11 = Rotation.ItemServiceBase(new Vec3d(vec3d1.x, vec3d.y, vec3d1.z), vec3d);
      Rotation ililiiili1ll1li111 = this.currentRotation
         .on23(this.currentRotation.EmoteManager(ililiiili1ll1li11))
         .Event08((float)MathUtils.SimpleItemBuilder(-5.0, 5.0), (float)MathUtils.SimpleItemBuilder(-5.0, 5.0));
      this.setRotation(ililiiili1ll1li111);
      return true;
   }

   public void applyInput(boolean var1) {
      if (this.player != null) {
         this.player.input.movementForward = var1 ? 1.0F : 0.0F;
         this.player.input.movementSideways = 0.0F;
         this.player.input.playerInput = new PlayerInput(var1, false, false, false, false, false, var1);
      }
   }

   public void setRotation(Rotation var1) {
      this.currentRotation = var1;
      this.player.setYaw(this.player.getYaw() + MathHelper.wrapDegrees(var1.GrimGlide() - this.player.getYaw()));
      this.player.setPitch(MathHelper.clamp(var1.GuiWalk(), -90.0F, 90.0F));
   }

   public void onDrop() {
      BotPlayHandler botplayhandler = this.handler();
      Text text = botplayhandler.getCurrentScreenTitle();
      if (text != null && text.getString().toLowerCase(Locale.ROOT).contains("для продажи")) {
         if (this.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler) {
            if (this.moveBuyerResources()) {
               int i = this.getContainerSlotCount(genericcontainerscreenhandler);
               if (i > 0) {
                  this.clickSlot(i - 1, 0, SlotActionType.QUICK_MOVE);
               }
            }

            Slot slot = this.findTrashSlot();
            if (slot != null) {
               this.clickSlot(slot.id, 1, SlotActionType.THROW);
            }
         }
      } else if (this.player.age % 40 == 0) {
         botplayhandler.sendCommand("buyer");
      }
   }

   public boolean moveBuyerResources() {
      boolean flag = false;
      int i = 0;

      for (Slot slot : this.player.currentScreenHandler.slots) {
         if (this.isPlayerInventorySlot(slot) && this.isBuyerResource(slot.getStack())) {
            if (i > 5) {
               break;
            }

            this.clickSlot(slot.id, 0, SlotActionType.QUICK_MOVE);
            i++;
            flag = true;
         }
      }

      return flag;
   }

   public boolean isBuyerResource(ItemStack var1) {
      return !var1.isEmpty() && BUYER_ITEMS.contains(var1.getItem());
   }

   public Slot findTrashSlot() {
      return this.player
         .currentScreenHandler
         .slots
         .stream()
         .filter(this::isPlayerInventorySlot)
         .filter(var1 -> this.isTrash(var1.getStack()))
         .findFirst()
         .orElse(null);
   }

   public boolean isPlayerInventorySlot(Slot var1) {
      if (this.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler) {
         int i = this.getContainerSlotCount(genericcontainerscreenhandler);
         return var1.inventory == this.player.getInventory() || var1.id >= i && var1.id < i + 36;
      } else {
         return var1.inventory == this.player.getInventory() && var1.id >= 9 && var1.id <= 44;
      }
   }

   public int getContainerSlotCount(GenericContainerScreenHandler var1) {
      return Math.min(var1.getInventory().size(), Math.max(0, this.player.currentScreenHandler.slots.size() - 36));
   }

   public boolean isTrash(ItemStack var1) {
      return !var1.isEmpty() && !var1.isOf(Items.PLAYER_HEAD) && !var1.isOf(Items.TRIPWIRE_HOOK) && !this.isBuyerResource(var1)
         ? var1.getItem() instanceof BlockItem blockitem && !BlockFinder.ItemRegistry(blockitem.getBlock())
         : false;
   }

   public void clickSlot(int var1, int var2, SlotActionType var3) {
      this.interaction.clickSlot(this.player.currentScreenHandler.syncId, var1, var2, var3, this.player);
   }

   public void onBreak(List<BotAutoMine_BlockMine> var1) {
      ItemStack itemstack = this.player.getMainHandStack();
      if (itemstack.isIn(ItemTags.PICKAXES) && itemstack.getMaxDamage() - itemstack.getDamage() >= 30) {
         this.skippedBlocks.clear();

         for (int i = 0; i < 6; i++) {
            BotAutoMine_BreakTarget botautomine_breaktarget = this.findBestBreakTarget(var1);
            if (botautomine_breaktarget == null) {
               break;
            }

            this.grimSuperBypass(0.0, botautomine_breaktarget.rotation());
            if (this.interaction
               .updateBlockBreakingProgress(botautomine_breaktarget.hitResult().getBlockPos(), botautomine_breaktarget.hitResult().getSide())) {
               this.skipDrillArea(botautomine_breaktarget.hitResult().getBlockPos());
            }
         }
      }
   }

   public BotAutoMine_BreakTarget findBestBreakTarget(List<BotAutoMine_BlockMine> var1) {
      Vec3d vec3d = this.player.getCameraPosVec(1.0F);
      double d0 = this.player.getBlockInteractionRange();
      double d1 = 5.4;
      HashSet<BlockPos> hashset = new HashSet<>();

      for (BotAutoMine_BlockMine botautomine_blockmine : var1) {
         if (this.isMineable(botautomine_blockmine)) {
            hashset.add(botautomine_blockmine.pos());
         }
      }

      BotAutoMine_BreakTarget botautomine_breaktarget1 = null;

      for (BlockPos blockpos : hashset) {
         if (this.isWithinMineRange(blockpos, vec3d, d1, d0)) {
            Rotation ililiiili1ll1li11 = this.currentRotation.on23(this.currentRotation.EmoteManager(Rotation.ItemServiceBase(blockpos.toCenterPos(), vec3d)));
            BlockHitResult blockhitresult = BotRaytracing.rayTracePos(
               this.world, this.player, vec3d, ililiiili1ll1li11, d0, var1x -> var1x != null && blockpos.equals(var1x.getBlockPos())
            );
            if (blockhitresult != null && blockhitresult.getType() != Type.MISS) {
               BotAutoMine_BreakTarget botautomine_breaktarget = new BotAutoMine_BreakTarget(
                  ililiiili1ll1li11, blockhitresult, this.countDrillCoverage(blockpos, hashset), BlockFinder.on23(vec3d, blockpos)
               );
               if (botautomine_breaktarget1 == null
                  || botautomine_breaktarget.drillCoverage() > botautomine_breaktarget1.drillCoverage()
                  || botautomine_breaktarget.drillCoverage() == botautomine_breaktarget1.drillCoverage()
                     && botautomine_breaktarget.distanceSquared() < botautomine_breaktarget1.distanceSquared()) {
                  botautomine_breaktarget1 = botautomine_breaktarget;
               }
            }
         }
      }

      return botautomine_breaktarget1;
   }

   public BlockPos findBestMovingTarget(List<BotAutoMine_BlockMine> var1) {
      Vec3d vec3d = this.player.getCameraPosVec(1.0F);
      double d0 = this.player.getBlockInteractionRange();
      BlockPos blockpos = null;
      double d1 = Double.MAX_VALUE;

      for (BotAutoMine_BlockMine botautomine_blockmine : var1) {
         if (this.isMineable(botautomine_blockmine) && !(Math.abs(botautomine_blockmine.pos().getY() + 0.5 - vec3d.y) > d0)) {
            double d2 = botautomine_blockmine.pos().getX() + 0.5 - vec3d.x;
            double d3 = botautomine_blockmine.pos().getZ() + 0.5 - vec3d.z;
            double d4 = d2 * d2 + d3 * d3;
            if (d4 < d1) {
               blockpos = botautomine_blockmine.pos();
               d1 = d4;
            }
         }
      }

      return blockpos != null ? new BlockPos(blockpos.getX(), this.player.getBlockY(), blockpos.getZ()) : null;
   }

   public boolean isMineable(BotAutoMine_BlockMine var1) {
      BlockState blockstate = var1.state();
      return !this.skippedBlocks.contains(var1.pos())
         && !blockstate.isAir()
         && blockstate.getFluidState().isEmpty()
         && blockstate.getHardness(this.world, var1.pos()) >= 0.0F
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
         this.skippedBlocks.add(blockpos.toImmutable());
      }
   }

   public void grimSuperBypass(double var1, Rotation var3) {
      this.handler()
         .sendPacket(
            new Full(
               this.player.getX(),
               this.player.getY() + var1,
               this.player.getZ(),
               var3.GrimGlide(),
               var3.GuiWalk(),
               this.player.isOnGround(),
               this.player.horizontalCollision
            )
         );
   }

   public List<BotAutoMine_BlockMine> onTeleportation() {
      if (this.rct().isActive()) {
         return null;
      }

      if (this.rct().currentAnarchyHere() == -1) {
         this.reconnectToRandomAnarchy();
         return null;
      }

      if (this.isWaitingForMineData()) {
         return null;
      }

      if (this.mineMode.is("module.autoMine.modeCombined")) {
         return this.selectCombinedMine();
      }

      this.combinedMineSwitched = false;
      this.selectedMine = null;
      RegionBounds i11l11llllli11i111il1_ii1il11l111ii11iil = BlockFinder.BotPacketEvent(this.mineMode.is("module.autoMine.modeNether"));
      if (BlockFinder.on23(i11l11llllli11i111il1_ii1il11l111ii11iil, this.world) && !(this.getMineDistance(i11l11llllli11i111il1_ii1il11l111ii11iil) > 25.0)) {
         return this.getAvailableMine(i11l11llllli11i111il1_ii1il11l111ii11iil);
      }

      this.handleWarpTp();
      return null;
   }

   public List<BotAutoMine_BlockMine> selectCombinedMine() {
      RegionBounds i11l11llllli11i111il1_ii1il11l111ii11iil = BlockFinder.BotPacketEvent(false);
      RegionBounds i11l11llllli11i111il1_ii1il11l111ii11iil1 = BlockFinder.BotPacketEvent(true);
      if (BlockFinder.on23(i11l11llllli11i111il1_ii1il11l111ii11iil, this.world) && BlockFinder.on23(i11l11llllli11i111il1_ii1il11l111ii11iil1, this.world)) {
         List<BotAutoMine_BlockMine> list = this.getArena(i11l11llllli11i111il1_ii1il11l111ii11iil);
         List<BotAutoMine_BlockMine> list1 = this.getArena(i11l11llllli11i111il1_ii1il11l111ii11iil1);
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
            if (BlockFinder.on23(i11l11llllli11i111il1_ii1il11l111ii11iil3, this.world)
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
      if (this.player.age >= 40 && this.mineDataCooldown <= 0) {
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
      return this.mineMode.is("module.autoMine.modeCombined") && this.selectedMine != null
         ? this.selectedMine
         : BlockFinder.BotPacketEvent(this.mineMode.is("module.autoMine.modeNether"));
   }

   public boolean hasLoadedMineBlocks(RegionBounds var1) {
      for (BlockPos blockpos : BlockPos.iterate(var1.call019(), var1.minY(), var1.call020(), var1.call021(), var1.call069(), var1.call022())) {
         if (this.world.isChunkLoaded(blockpos) && !this.world.getBlockState(blockpos).isAir()) {
            return true;
         }
      }

      return false;
   }

   public void displayMineDistance(RegionBounds var1) {
      this.bot().systemMessage(String.valueOf(this.getMineDistance(var1)));
   }

   public double getMineDistance(RegionBounds var1) {
      double d0 = (var1.call019() + var1.call021() + 1.0) * 0.5;
      double d1 = (var1.call020() + var1.call022() + 1.0) * 0.5;
      double d2 = Math.abs(this.player.getX() - d0);
      double d3 = Math.abs(this.player.getZ() - d1);
      return Math.hypot(d2, d3);
   }

   public List<BotAutoMine_BlockMine> getAvailableMine(RegionBounds var1) {
      List<BotAutoMine_BlockMine> list = this.getArena(var1);
      if (!this.isMineAvailable(list)) {
         this.reconnectToRandomAnarchy();
         return null;
      } else {
         return list;
      }
   }

   public boolean isMineAvailable(List<BotAutoMine_BlockMine> var1) {
      return var1.size() - this.countAirBlocks(var1) < 600L ? false : !this.player.isOnGround() || this.hasReachableBlockByHeight(var1);
   }

   public boolean hasReachableBlockByHeight(List<BotAutoMine_BlockMine> var1) {
      int i = MathHelper.floor(this.player.getEyeY() + this.player.getBlockInteractionRange() - 1.0);
      return var1.stream()
         .anyMatch(var1x -> !var1x.state().isAir() && !var1x.state().isOf(Blocks.OBSIDIAN) && var1x.pos().getY() <= i);
   }

   public long countAirBlocks(List<BotAutoMine_BlockMine> var1) {
      return var1.stream().filter(var0 -> var0.state().isAir()).count();
   }

   public double getFillPercentage(List<BotAutoMine_BlockMine> var1) {
      return var1.isEmpty() ? 0.0 : 1.0 - (double)this.countAirBlocks(var1) / var1.size();
   }

   public void onRepair() {
      if (this.player.age % 20 == 0) {
         ItemStack itemstack = this.player.getMainHandStack();
         if (itemstack.isIn(ItemTags.PICKAXES) && itemstack.getMaxDamage() - itemstack.getDamage() < 30) {
            if (this.player.getStackInHand(Hand.OFF_HAND).isOf(Items.EXPERIENCE_BOTTLE)) {
               this.useItem(Hand.OFF_HAND);
            } else {
               Slot slot = this.player
                  .currentScreenHandler
                  .slots
                  .stream()
                  .filter(var0 -> var0.getStack().isOf(Items.EXPERIENCE_BOTTLE))
                  .max(Comparator.comparingInt(var0 -> var0.id))
                  .orElse(null);
               if (slot == null) {
                  this.bot().systemMessage("AutoMine: в хотбаре закончились пузырьки опыта");
                  this.toggle();
               } else if (slot.inventory instanceof PlayerInventory || slot.inventory instanceof EnderChestInventory) {
                  this.clickSlot(slot.id, 40, SlotActionType.SWAP);
               }
            }
         }
      }
   }

   public void useItem(Hand var1) {
      if (this.interaction.interactItem(this.player, var1) instanceof Success success && success.swingSource() == SwingSource.CLIENT) {
         this.player.swingHand(var1);
      }
   }

   public List<BotAutoMine_BlockMine> getArena(RegionBounds var1) {
      return this.getBlocksInZone(new BlockPos(var1.call019(), var1.minY(), var1.call020()), new BlockPos(var1.call021(), var1.call069(), var1.call022()));
   }

   public List<BotAutoMine_BlockMine> getBlocksInZone(BlockPos var1, BlockPos var2) {
      List<BotAutoMine_BlockMine> arraylist = new ArrayList<>();
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
               arraylist.add(new BotAutoMine_BlockMine(blockpos, this.world.getBlockState(blockpos)));
            }
         }
      }

      return arraylist;
   }

   public void handleWarpTp() {
      if (this.player.age % 60 == 0) {
         this.sendWarpCommand();
      }
   }

   public void sendWarpCommand() {
      boolean flag = this.mineMode.is("module.autoMine.modeCombined")
         ? BlockFinder.BotPacketEvent(true).equals(this.selectedMine)
         : this.mineMode.is("module.autoMine.modeNether");
      this.handler().sendCommand(flag ? "warp mine2" : "warp mine");
      this.mineDataCooldown = 20;
   }

   public void reconnectToRandomAnarchy() {
      this.selectedMine = null;
      this.nextRouteBlock = null;
      this.mineDataCooldown = 0;
      this.combinedMineSwitched = false;
      this.skippedBlocks.clear();
      int i = this.rct().currentAnarchyHere();

      int j;
      do {
         j = ThreadLocalRandom.current().nextInt(1, 64);
      } while (j == i || j == 2 || j == 17 || j == 33 || j == 49);

      this.rct().reconnect(j);
   }

   public BotRctService rct() {
      return this.bot().getRct();
   }

   public ModeSetting getMineMode() {
      return this.mineMode;
   }
}
