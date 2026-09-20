package org.zenith.base.bot.world;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OperatorBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ButtonClickC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResult.PassToDefaultBlockAction;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;
import org.zenith.base.bot.net.BotPlayHandler;

public final class BotInteractionManager {
   public static final Logger LOGGER = LogUtils.getLogger();
   public final BotPlayHandler networkHandler;
   public BlockPos currentBreakingPos = new BlockPos(-1, -1, -1);
   public ItemStack selectedStack = ItemStack.EMPTY;
   public float currentBreakingProgress;
   public int blockBreakingCooldown;
   public boolean breakingBlock;
   public GameMode gameMode = GameMode.DEFAULT;
   public GameMode previousGameMode;
   public int lastSelectedSlot;

   public BotInteractionManager(BotPlayHandler var1) {
      this.networkHandler = var1;
   }

   public BotPlayer player() {
      return this.networkHandler.getPlayer();
   }

   public BotWorld world() {
      return this.networkHandler.getWorld();
   }

   public void copyAbilities(PlayerEntity var1) {
      this.gameMode.setAbilities(var1.getAbilities());
   }

   public void setGameModes(GameMode var1, GameMode var2) {
      this.gameMode = var1;
      this.previousGameMode = var2;
      BotPlayer botplayer = this.player();
      if (botplayer != null) {
         this.gameMode.setAbilities(botplayer.getAbilities());
      }
   }

   public void setGameMode(GameMode var1) {
      if (var1 != this.gameMode) {
         this.previousGameMode = this.gameMode;
      }

      this.gameMode = var1;
      BotPlayer botplayer = this.player();
      if (botplayer != null) {
         this.gameMode.setAbilities(botplayer.getAbilities());
      }
   }

   public boolean breakBlock(BlockPos var1) {
      BotPlayer botplayer = this.player();
      BotWorld botworld = this.world();
      if (botplayer == null || botworld == null) {
         return false;
      }

      if (botplayer.isBlockBreakingRestricted(botworld, var1, this.gameMode)) {
         return false;
      }

      BlockState blockstate = botworld.getBlockState(var1);
      if (!botplayer.getMainHandStack().getItem().canMine(botplayer.getMainHandStack(), blockstate, botworld, var1, botplayer)) {
         return false;
      }

      Block block = blockstate.getBlock();
      if (block instanceof OperatorBlock && !botplayer.isCreativeLevelTwoOp()) {
         return false;
      }

      if (blockstate.isAir()) {
         return false;
      }

      block.onBreak(botworld, var1, blockstate, botplayer);
      FluidState fluidstate = botworld.getFluidState(var1);
      boolean flag = botworld.setBlockState(var1, fluidstate.getBlockState(), 11);
      if (flag) {
         block.onBroken(botworld, var1, blockstate);
      }

      return flag;
   }

   public boolean attackBlock(BlockPos var1, Direction var2) {
      BotPlayer botplayer = this.player();
      BotWorld botworld = this.world();
      if (botplayer == null || botworld == null) {
         return false;
      }

      if (botplayer.isBlockBreakingRestricted(botworld, var1, this.gameMode)) {
         return false;
      }

      if (!botworld.getWorldBorder().contains(var1)) {
         return false;
      }

      if (this.gameMode.isCreative()) {
         this.sendSequencedPacket(botworld, var3x -> {
            this.breakBlock(var1);
            return new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var1, var2, var3x);
         });
         this.blockBreakingCooldown = 5;
      } else if (!this.breakingBlock || !this.isCurrentlyBreaking(var1)) {
         if (this.breakingBlock) {
            this.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, var2));
         }

         BlockState blockstate = botworld.getBlockState(var1);
         this.sendSequencedPacket(botworld, var6 -> {
            boolean flag = !blockstate.isAir();
            if (flag && this.currentBreakingProgress == 0.0F) {
               blockstate.onBlockBreakStart(botworld, var1, botplayer);
            }

            if (flag && blockstate.calcBlockBreakingDelta(botplayer, botplayer.getWorld(), var1) >= 1.0F) {
               this.breakBlock(var1);
            } else {
               this.breakingBlock = true;
               this.currentBreakingPos = var1;
               this.selectedStack = botplayer.getMainHandStack();
               this.currentBreakingProgress = 0.0F;
            }

            return new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var1, var2, var6);
         });
      }

      return true;
   }

   public void cancelBlockBreaking() {
      if (this.breakingBlock) {
         this.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, this.currentBreakingPos, Direction.DOWN));
         this.breakingBlock = false;
         this.currentBreakingProgress = 0.0F;
         BotPlayer botplayer = this.player();
         if (botplayer != null) {
            botplayer.resetTicksSinceLastAttack();
         }
      }
   }

   public boolean updateBlockBreakingProgress(BlockPos var1, Direction var2) {
      BotPlayer botplayer = this.player();
      BotWorld botworld = this.world();
      if (botplayer != null && botworld != null) {
         this.syncSelectedSlot();
         if (this.blockBreakingCooldown > 0) {
            this.blockBreakingCooldown--;
            return true;
         }

         if (this.gameMode.isCreative() && botworld.getWorldBorder().contains(var1)) {
            this.blockBreakingCooldown = 5;
            this.sendSequencedPacket(botworld, var3x -> {
               this.breakBlock(var1);
               return new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var1, var2, var3x);
            });
            return true;
         }

         if (this.isCurrentlyBreaking(var1)) {
            BlockState blockstate = botworld.getBlockState(var1);
            if (blockstate.isAir()) {
               this.breakingBlock = false;
               return false;
            }

            this.currentBreakingProgress = this.currentBreakingProgress + blockstate.calcBlockBreakingDelta(botplayer, botplayer.getWorld(), var1);
            if (this.currentBreakingProgress >= 1.0F) {
               this.breakingBlock = false;
               this.sendSequencedPacket(botworld, var3x -> {
                  this.breakBlock(var1);
                  return new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, var1, var2, var3x);
               });
               this.currentBreakingProgress = 0.0F;
               this.blockBreakingCooldown = 5;
            }

            return true;
         } else {
            return this.attackBlock(var1, var2);
         }
      } else {
         return false;
      }
   }

   public void sendSequencedPacket(BotWorld var1, BotInteractionManager_SequencedPacketCreator var2) {
      BotPendingUpdateManager botpendingupdatemanager = var1.getPendingUpdateManager().incrementSequence();

      try {
         int i = botpendingupdatemanager.getSequence();
         Packet packet = var2.predict(i);
         this.networkHandler.sendPacket(packet);
      } catch (Throwable throwable1) {
         if (botpendingupdatemanager != null) {
            try {
               botpendingupdatemanager.close();
            } catch (Throwable throwable) {
               throwable1.addSuppressed(throwable);
            }
         }

         throw throwable1;
      }

      if (botpendingupdatemanager != null) {
         botpendingupdatemanager.close();
      }
   }

   public void tick() {
      this.syncSelectedSlot();
   }

   public boolean isCurrentlyBreaking(BlockPos var1) {
      BotPlayer botplayer = this.player();
      if (botplayer == null) {
         return false;
      }

      ItemStack itemstack = botplayer.getMainHandStack();
      return var1.equals(this.currentBreakingPos) && ItemStack.areItemsAndComponentsEqual(itemstack, this.selectedStack);
   }

   public void syncSelectedSlot() {
      BotPlayer botplayer = this.player();
      if (botplayer != null) {
         int i = botplayer.getInventory().selectedSlot;
         if (i != this.lastSelectedSlot) {
            this.lastSelectedSlot = i;
            this.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(this.lastSelectedSlot));
         }
      }
   }

   public ActionResult interactBlock(BotPlayer var1, Hand var2, BlockHitResult var3) {
      this.syncSelectedSlot();
      BotWorld botworld = this.world();
      if (botworld != null && botworld.getWorldBorder().contains(var3.getBlockPos())) {
         MutableObject mutableobject = new MutableObject();
         this.sendSequencedPacket(botworld, var5x -> {
            mutableobject.setValue(this.interactBlockInternal(var1, var2, var3));
            return new PlayerInteractBlockC2SPacket(var2, var3, var5x);
         });
         return (ActionResult)mutableobject.getValue();
      } else {
         return ActionResult.FAIL;
      }
   }

   public ActionResult interactBlockInternal(BotPlayer var1, Hand var2, BlockHitResult var3) {
      BotWorld botworld = Objects.requireNonNull(this.world());
      BlockPos blockpos = var3.getBlockPos();
      ItemStack itemstack = var1.getStackInHand(var2);
      if (this.gameMode == GameMode.SPECTATOR) {
         return ActionResult.CONSUME;
      }

      boolean flag = !var1.getMainHandStack().isEmpty() || !var1.getOffHandStack().isEmpty();
      boolean flag1 = var1.shouldCancelInteraction() && flag;
      if (!flag1) {
         BlockState blockstate = botworld.getBlockState(blockpos);
         if (!blockstate.getBlock().getRequiredFeatures().isSubsetOf(this.networkHandler.getEnabledFeatures())) {
            return ActionResult.FAIL;
         }

         ActionResult actionresult = blockstate.onUseWithItem(var1.getStackInHand(var2), botworld, var1, var2, var3);
         if (actionresult.isAccepted()) {
            return actionresult;
         }

         if (actionresult instanceof PassToDefaultBlockAction && var2 == Hand.MAIN_HAND) {
            ActionResult actionresult1 = blockstate.onUse(botworld, var1, var3);
            if (actionresult1.isAccepted()) {
               return actionresult1;
            }
         }
      }

      if (!itemstack.isEmpty() && !var1.getItemCooldownManager().isCoolingDown(itemstack)) {
         ItemUsageContext itemusagecontext = new ItemUsageContext(var1, var2, var3);
         ActionResult actionresult2;
         if (this.gameMode.isCreative()) {
            int i = itemstack.getCount();
            actionresult2 = itemstack.useOnBlock(itemusagecontext);
            itemstack.setCount(i);
         } else {
            actionresult2 = itemstack.useOnBlock(itemusagecontext);
         }

         return actionresult2;
      } else {
         return ActionResult.PASS;
      }
   }

   public ActionResult interactItem(BotPlayer var1, Hand var2) {
      if (this.gameMode == GameMode.SPECTATOR) {
         return ActionResult.PASS;
      }

      BotWorld botworld = this.world();
      if (botworld == null) {
         return ActionResult.PASS;
      }

      this.syncSelectedSlot();
      MutableObject mutableobject = new MutableObject();
      this.sendSequencedPacket(botworld, var4x -> {
         PlayerInteractItemC2SPacket playerinteractitemc2spacket = new PlayerInteractItemC2SPacket(var2, var4x, var1.getYaw(), var1.getPitch());
         ItemStack itemstack = var1.getStackInHand(var2);
         if (var1.getItemCooldownManager().isCoolingDown(itemstack)) {
            mutableobject.setValue(ActionResult.PASS);
            return playerinteractitemc2spacket;
         }

         ActionResult actionresult = itemstack.use(botworld, var1, var2);
         ItemStack itemstack1;
         if (actionresult instanceof Success success) {
            itemstack1 = Objects.requireNonNullElseGet(success.getNewHandStack(), () -> var1.getStackInHand(var2));
         } else {
            itemstack1 = var1.getStackInHand(var2);
         }

         if (itemstack1 != itemstack) {
            var1.setStackInHand(var2, itemstack1);
         }

         mutableobject.setValue(actionresult);
         return playerinteractitemc2spacket;
      });
      return (ActionResult)mutableobject.getValue();
   }

   public void attackEntity(BotPlayer var1, Entity var2) {
      this.syncSelectedSlot();
      this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(var2, var1.isSneaking()));
      if (this.gameMode != GameMode.SPECTATOR) {
         var1.attack(var2);
         var1.resetTicksSinceLastAttack();
      }
   }

   public ActionResult interactEntity(BotPlayer var1, Entity var2, Hand var3) {
      this.syncSelectedSlot();
      this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interact(var2, var1.isSneaking(), var3));
      return (ActionResult)(this.gameMode == GameMode.SPECTATOR ? ActionResult.PASS : var1.interact(var2, var3));
   }

   public ActionResult interactEntityAtLocation(BotPlayer var1, Entity var2, EntityHitResult var3, Hand var4) {
      this.syncSelectedSlot();
      Vec3d vec3d = var3.getPos().subtract(var2.getX(), var2.getY(), var2.getZ());
      this.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.interactAt(var2, var1.isSneaking(), var4, vec3d));
      return (ActionResult)(this.gameMode == GameMode.SPECTATOR ? ActionResult.PASS : var2.interactAt(var1, vec3d, var4));
   }

   public void clickSlot(int var1, int var2, int var3, SlotActionType var4, PlayerEntity var5) {
      ScreenHandler screenhandler = var5.currentScreenHandler;
      if (var1 != screenhandler.syncId) {
         LOGGER.warn("Ignoring click in mismatching container. Click in {}, player has {}.", var1, screenhandler.syncId);
      } else {
         DefaultedList defaultedlist = screenhandler.slots;
         int i = defaultedlist.size();
         ArrayList<Object> arraylist = Lists.newArrayListWithCapacity(i);

         for (Slot slot : (Iterable<Slot>)(Iterable<?>)defaultedlist) {
            arraylist.add(slot.getStack().copy());
         }

         screenhandler.onSlotClick(var2, var3, var4, var5);
         Int2ObjectMap<ItemStackHash> changedStacks = new Int2ObjectOpenHashMap<>();

         for (int j = 0; j < i; j++) {
            ItemStack itemstack = (ItemStack)arraylist.get(j);
            ItemStack itemstack1 = ((Slot)defaultedlist.get(j)).getStack();
            if (!ItemStack.areEqual(itemstack, itemstack1)) {
               changedStacks.put(j, ItemStackHash.fromItemStack(itemstack1, this.networkHandler.getComponentHasher()));
            }
         }

         this.networkHandler
            .sendPacket(new ClickSlotC2SPacket(
               var1,
               screenhandler.getRevision(),
               (short)var2,
               (byte)var3,
               var4,
               changedStacks,
               ItemStackHash.fromItemStack(screenhandler.getCursorStack(), this.networkHandler.getComponentHasher())
            ));
      }
   }

   public void clickButton(int var1, int var2) {
      this.networkHandler.sendPacket(new ButtonClickC2SPacket(var1, var2));
   }

   public void clickCreativeStack(ItemStack var1, int var2) {
      if (this.gameMode.isCreative() && var1.getItem().getRequiredFeatures().isSubsetOf(this.networkHandler.getEnabledFeatures())) {
         this.networkHandler.sendPacket(new CreativeInventoryActionC2SPacket(var2, var1));
      }
   }

   public void stopUsingItem(PlayerEntity var1) {
      this.syncSelectedSlot();
      this.networkHandler.sendPacket(new PlayerActionC2SPacket(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN));
      var1.stopUsingItem();
   }

   public boolean isFlyingLocked() {
      return this.gameMode == GameMode.SPECTATOR;
   }

   public GameMode getPreviousGameMode() {
      return this.previousGameMode;
   }

   public GameMode getCurrentGameMode() {
      return this.gameMode;
   }

   public boolean isBreakingBlock() {
      return this.breakingBlock;
   }

   public int getBlockBreakingProgress() {
      return this.currentBreakingProgress > 0.0F ? (int)(this.currentBreakingProgress * 10.0F) : -1;
   }
}
