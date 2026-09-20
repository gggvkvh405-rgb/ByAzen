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


import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.item.Items;
import com.darkmagician6.eventapi.EventTarget;
import java.awt.Color;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import net.minecraft.block.BeetrootsBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.block.PitcherCropBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.zenith.ZenithClient;
import org.zenith.core.EffectEngine;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.EventHookWorldRender;
import org.zenith.event.EventTick;
import org.zenith.event.MovementInputEvent;
import org.zenith.render.WorldRender;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationMath;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.MathUtils;
import org.zenith.util.RaycastUtils;
import org.zenith.util.ScreenUtils;
import org.zenith.util.TaskScheduler;

@ModuleInfo(name = "CropFarmer", category = Category.PLAYER, description = "")
public final class CropFarmer extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final CropFarmer cropFarmer = new CropFarmer();
   public static final long long81 = 1000000000L;
   public final BooleanSetting booleanSetting = new BooleanSetting("1.20.x", "module.cropFarmer.legacyBypass.desc", false);
   public final ModeSetting crop = new ModeSetting(
      "module.cropFarmer.crop",
      "module.cropFarmer.crop.desc",
      CropFarmer.CropType.call406.string16,
      CropFarmer.CropType.call432.string16,
      CropFarmer.CropType.call433.string16,
      CropFarmer.CropType.call434.string16,
      CropFarmer.CropType.call435.string16,
      CropFarmer.CropType.call436.string16,
      CropFarmer.CropType.call407.string16
   );
   public final BooleanSetting renderUnsuitableBeds = new BooleanSetting(
      "module.cropFarmer.renderUnsuitableBeds", "module.cropFarmer.renderUnsuitableBeds.desc", false, this::int462
   );
   public final BooleanSetting renderLowLightBeds = new BooleanSetting(
      "module.cropFarmer.renderLowLightBeds", "module.cropFarmer.renderLowLightBeds.desc", false, () -> this.call036().int111 > 0
   );
   public final BooleanSetting renderWaterPositions = new BooleanSetting(
      "module.cropFarmer.renderWaterPositions", "module.cropFarmer.renderWaterPositions.desc", false, this::int462
   );
   public final CooldownTimer zClass06722 = new CooldownTimer();
   public final CooldownTimer zClass06723 = new CooldownTimer();
   public final CooldownTimer zClass06724 = new CooldownTimer();
   public final Set<Integer> set8 = new HashSet<>();
   public final Map<BlockPos, CropFarmer.InventorySlot> map15 = new HashMap<>();
   public List<BlockPos> list13 = Collections.emptyList();
   public boolean boolean45;
   public BlockHitResult blockHitResult2 = null;
   public static final Comparator<BlockPos> comparator = Comparator.<BlockPos>comparingInt(Vec3i::getY)
      .thenComparingInt(Vec3i::getX)
      .thenComparingInt(Vec3i::getZ);

   @Override
   public boolean isPremium() {
      return true;
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.set8.clear();
      this.map15.clear();
      this.blockHitResult2 = null;
      this.list13 = Collections.emptyList();
      this.boolean45 = false;
   }

   @EventTarget
   public void TextScanner(EventHookWorldRender var1) {
      Item item = this.zClass088Var143();
      if (minecraftClient3.player
               .playerScreenHandler
               .slots
               .stream()
               .filter(var1x -> var1x.id != 45 && var1x.getStack().getItem() == item)
               .count()
            < 20L
         && (
            !(minecraftClient3.currentScreen instanceof GenericContainerScreen genericcontainerscreen)
               || !genericcontainerscreen.getTitle().getString().contains("Скупщик")
               || !(minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler)
         )) {
         this.set8.clear();
      } else if (this.zClass06723.EventModifyMouseRotationInput(30L)) {
         this.zClass06723.reset();
         if (!(minecraftClient3.currentScreen instanceof GenericContainerScreen) && this.zClass06722.EventModifyMouseRotationInput(3000L)) {
            minecraftClient3.getNetworkHandler().sendChatCommand("buyer");
            this.zClass06722.reset();
         } else if (minecraftClient3.currentScreen instanceof GenericContainerScreen genericcontainerscreen1
            && genericcontainerscreen1.getTitle().getString().contains("Скупщик")
            && minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler1) {
            for (Slot slot : minecraftClient3.player.currentScreenHandler.slots) {
               if (!this.set8.contains(slot.id)
                  && slot.id >= genericcontainerscreenhandler1.getInventory().size()
                  && slot.getStack().getItem() == item) {
                  this.set8.add(slot.id);
                  ScreenUtils.on23(slot, 0, SlotActionType.QUICK_MOVE, false);
                  return;
               }
            }

            ScreenUtils.on23(53, 0, SlotActionType.QUICK_MOVE, false);
            minecraftClient3.player.closeHandledScreen();
         }
      }
   }

   @EventTarget
   public void NbtItemSpec(EventHookWorldRender var1) {
      if (this.renderUnsuitableBeds.isEnabled() && this.int462()) {
         BlockPos blockpos = ZenithClient.on23().BotFeatureRegistry().BooleanValue();
         BlockPos blockpos1 = ZenithClient.on23().BotFeatureRegistry().BotFollowEntity();
         if (blockpos != null && blockpos1 != null) {
            getBlocksInZone(blockpos, blockpos1)
               .stream()
               .filter(this::BotWorldJoinEvent)
               .filter(var1x -> !this.BotPacketEvent(var1x))
               .forEach(var0 -> WorldRender.on23(new Box(var0), Color.RED.getRGB(), 1.0F));
         }
      }
   }

   @EventTarget
   public void EnchantItemSpec(EventHookWorldRender var1) {
      if (this.renderLowLightBeds.isEnabled() && this.call036().int111 != 0) {
         BlockPos blockpos = ZenithClient.on23().BotFeatureRegistry().BooleanValue();
         BlockPos blockpos1 = ZenithClient.on23().BotFeatureRegistry().BotFollowEntity();
         if (blockpos != null && blockpos1 != null) {
            getBlocksInZone(blockpos, blockpos1)
               .stream()
               .filter(this::BotWorldJoinEvent)
               .filter(var1x -> !this.BotRespawnEvent(var1x))
               .forEach(var0 -> WorldRender.on23(new Box(var0), Color.YELLOW.getRGB(), 1.0F));
         }
      }
   }

   @EventTarget
   public void SimpleItemBuilder(EventHookWorldRender var1) {
      if (this.renderWaterPositions.isEnabled() && this.int462()) {
         BlockPos blockpos = ZenithClient.on23().BotFeatureRegistry().BooleanValue();
         BlockPos blockpos1 = ZenithClient.on23().BotFeatureRegistry().BotFollowEntity();
         if (blockpos != null && blockpos1 != null) {
            if (!this.boolean45 || this.zClass06724.EventMouseButton(1000L)) {
               this.list13 = this.UiAnimation(blockpos, blockpos1);
               this.boolean45 = true;
            }

            this.list13.forEach(var0 -> WorldRender.on23(new Box(var0), new Color(0, 120, 255).getRGB(), 1.0F));
         }
      }
   }

   @EventTarget
   public void UiAnimation(RotationUpdateStartEvent var1) {
      if (ZenithClient.on23().BotFeatureRegistry().BooleanValue() == null) {
         if (minecraftClient3.player.age % 80 == 0) {
            ZenithClient.on23().ConfigJsonUtil().on23("4", Text.of("Отметьте точку .region pos1"), 4000L);
         }
      } else if (ZenithClient.on23().BotFeatureRegistry().BotFollowEntity() == null) {
         if (minecraftClient3.player.age % 80 == 0) {
            ZenithClient.on23()
               .ConfigJsonUtil()
               .on23(
                  "4",
                  Text.of("Отметьте точку .region ")
                     .copy()
                     .append(
                        Text.of("pos2")
                           .copy()
                           .setStyle(Style.EMPTY.withColor(val003.TextScanner().getCurrentStyle().getPrimaryColor().getColor().call001()))
                     ),
                  4000L
               );
         }
      } else if (!this.int459()) {
         Slot slot = ScreenUtils.on23(minecraftClient3.player.playerScreenHandler, this.random10());
         if (!this.int461() && slot != null && TaskScheduler.Easing(AutoTotem.class) && TaskScheduler.Easing(CropFarmer.class)) {
            TaskScheduler.on23(CropFarmer.class, () -> {
               if (!(minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler)) {
                  ScreenUtils.closeScreen();
               }

               ScreenUtils.on23(slot, Hand.OFF_HAND, true);
            });
         } else {
            Rotation ililiiili1ll1li11 = this.Velocity();
            if (ililiiili1ll1li11 != null) {
               if (this.booleanSetting.isEnabled()) {
                  this.float367();
               } else {
                  Rotation ililiiili1ll1li111 = this.ConfigJsonUtil(ililiiili1ll1li11);
                  ZenithClient.on23()
                     .CloudRouter()
                     .on23(new RotationTask(ililiiili1ll1li111, () -> val001.on23(val001.HudPreviewItem(), ililiiili1ll1li111), val001.RectBatch()), 7, this);
               }
            }
         }
      }
   }

   @EventTarget
   public void ItemServiceBase(EventTick var1) {
      if (!this.int459() && !this.booleanSetting.isEnabled()) {
         BlockHitResult blockhitresult = this.CloudResponse(val003.CloudRouter().LineShader());
         if (blockhitresult != null) {
            if (this.blockHitResult2 != null && this.blockHitResult2.getBlockPos().equals(blockhitresult.getBlockPos())) {
               this.blockHitResult2 = blockhitresult;
               BlockState blockstate = minecraftClient3.world.getBlockState(blockhitresult.getBlockPos());
               this.on23(blockhitresult, blockstate);
            } else {
               this.blockHitResult2 = blockhitresult;
            }
         }
      }
   }

   public void float367() {
      this.float368();
      Rotation ililiiili1ll1li11 = null;
      HashSet hashset = new HashSet<>(this.map15.keySet());

      for (int i = 0; i < 4; i++) {
         Rotation ililiiili1ll1li111 = this.UiAnimation(hashset);
         if (ililiiili1ll1li111 == null) {
            break;
         }

         BlockHitResult blockhitresult = this.on23(ililiiili1ll1li111, hashset);
         if (blockhitresult == null) {
            ililiiili1ll1li11 = ililiiili1ll1li111;
            break;
         }

         BlockPos blockpos = blockhitresult.getBlockPos();
         hashset.add(blockpos);
         BlockState blockstate = minecraftClient3.world.getBlockState(blockpos);
         CropFarmer.Action i1i11lliliil1i1illl1l1lli_l1i1illlili = this.ColorAnimator(blockpos, blockstate);
         if (i1i11lliliil1i1illl1l1lli_l1i1illlili != null) {
            EffectEngine.on23(0.0, ililiiili1ll1li111);
            this.UiAnimation(blockhitresult, blockstate);
            this.map15.put(blockpos, new CropFarmer.InventorySlot(i1i11lliliil1i1illl1l1lli_l1i1illlili, System.nanoTime()));
         }

         ililiiili1ll1li11 = ililiiili1ll1li111;
      }

      Rotation ililiiili1ll1li112 = this.UiAnimation(hashset);
      Rotation ililiiili1ll1li113 = ililiiili1ll1li112 != null
         ? ililiiili1ll1li112
         : (ililiiili1ll1li11 == null ? val003.CloudRouter().LineShader() : ililiiili1ll1li11);
      Rotation ililiiili1ll1li114 = this.ConfigJsonUtil(ililiiili1ll1li113)
         .Event08((float)MathUtils.SimpleItemBuilder(-5.0, 5.0), (float)MathUtils.SimpleItemBuilder(-5.0, 5.0));
      ZenithClient.on23()
         .CloudRouter()
         .on23(new RotationTask(ililiiili1ll1li114, () -> val001.on23(val001.HudPreviewItem(), ililiiili1ll1li114), val001.HudPreviewItem()), 7, this);
   }

   public void float368() {
      long i = System.nanoTime();
      this.map15.entrySet().removeIf(var3 -> i - var3.getValue().long85 >= 1000000000L || this.on23(var3.getKey(), var3.getValue().cropFarmerVar143));
   }

   public boolean on23(BlockPos var1, CropFarmer.Action var2) {
      BlockState blockstate = minecraftClient3.world.getBlockState(var1);
      BlockState blockstate1 = minecraftClient3.world.getBlockState(var1.up());

      return switch (var2) {
         case val374 -> !this.Easing(blockstate);
         case val375 -> !this.UiAnimation(blockstate, blockstate1);
         case val376 -> !blockstate1.isAir();
      };
   }

   public CropFarmer.Action ColorAnimator(BlockPos var1, BlockState var2) {
      if (this.Easing(var2)) {
         return CropFarmer.Action.val374;
      } else {
         BlockState blockstate = minecraftClient3.world.getBlockState(var1.up());
         if (this.int460() && this.UiAnimation(var2, blockstate)) {
            return CropFarmer.Action.val375;
         } else {
            return this.on23(var1, var2, blockstate) ? CropFarmer.Action.val376 : null;
         }
      }
   }

   public Rotation ConfigJsonUtil(Rotation var1) {
      Rotation ililiiili1ll1li11 = val003.CloudRouter().LineShader();
      return ililiiili1ll1li11.on23(ililiiili1ll1li11.EmoteManager(var1));
   }

   public Item random10() {
      return this.call036().item;
   }

   public Item zClass088Var143() {
      return this.call036().item2;
   }

   public boolean on23(BlockState var1, BlockState var2) {
      return var1.isOf(this.call036().block2) && var2.isAir();
   }

   public boolean Easing(BlockState var1) {
      return switch (this.call036()) {
         case call406 -> var1.isOf(Blocks.NETHER_WART) && var1.get(NetherWartBlock.AGE) == 3;
         case call432 -> var1.isOf(Blocks.CARROTS) && var1.get(CropBlock.AGE) == 7;
         case call433 -> var1.isOf(Blocks.POTATOES) && var1.get(CropBlock.AGE) == 7;
         case call434 -> var1.isOf(Blocks.BEETROOTS) && var1.get(BeetrootsBlock.AGE) == 3;
         case call435 -> var1.isOf(Blocks.WHEAT) && var1.get(CropBlock.AGE) == 7;
         case call436 -> var1.isOf(Blocks.TORCHFLOWER);
         case call407 -> var1.isOf(Blocks.PITCHER_CROP)
            && var1.get(PitcherCropBlock.AGE) == 4
            && var1.get(PitcherCropBlock.HALF) == DoubleBlockHalf.LOWER;
      };
   }

   public Rotation Velocity() {
      return this.UiAnimation(Collections.emptySet());
   }

   public Rotation UiAnimation(Set<BlockPos> var1) {
      List<BlockPos> list = getBlocksInZone(
         ZenithClient.on23().BotFeatureRegistry().BooleanValue(), ZenithClient.on23().BotFeatureRegistry().BotFollowEntity()
      );
      boolean flag = this.int461();
      boolean flag1 = this.int460();
      BlockPos blockpos = null;
      BlockPos blockpos1 = null;
      BlockPos blockpos2 = null;
      double d0 = Double.MAX_VALUE;
      double d1 = Double.MAX_VALUE;
      double d2 = Double.MAX_VALUE;

      for (BlockPos blockpos3 : list) {
         if (!var1.contains(blockpos3)) {
            BlockState blockstate = minecraftClient3.world.getBlockState(blockpos3);
            BlockState blockstate1 = minecraftClient3.world.getBlockState(blockpos3.up());
            if (flag && this.on23(blockpos3, blockstate, blockstate1)) {
               double d4 = minecraftClient3.player.squaredDistanceTo(Vec3d.of(blockpos3));
               if (d4 < d0) {
                  d0 = d4;
                  blockpos = blockpos3;
               }
            } else if (flag1 && this.UiAnimation(blockstate, blockstate1)) {
               double d3 = minecraftClient3.player.squaredDistanceTo(Vec3d.of(blockpos3));
               if (d3 < d1) {
                  d1 = d3;
                  blockpos1 = blockpos3;
               }
            }

            if (this.Easing(blockstate)) {
               double d5 = minecraftClient3.player.squaredDistanceTo(Vec3d.ofCenter(blockpos3));
               if (d5 < d2) {
                  d2 = d5;
                  blockpos2 = blockpos3;
               }
            }
         }
      }

      Vec3d vec3d = minecraftClient3.player.getEyePos();
      Rotation ililiiili1ll1li11;
      if (flag && blockpos != null) {
         ililiiili1ll1li11 = RotationMath.Event08(blockpos.toCenterPos().add(0.0, 0.5, 0.0).subtract(vec3d));
      } else if (blockpos1 == null || !(blockpos1.toCenterPos().squaredDistanceTo(vec3d) < 20.0) && blockpos2 != null) {
         if (blockpos2 != null) {
            ililiiili1ll1li11 = RotationMath.Event08(blockpos2.toCenterPos().add(0.0, -0.5, 0.0).subtract(vec3d));
         } else {
            ililiiili1ll1li11 = null;
         }
      } else {
         ililiiili1ll1li11 = RotationMath.Event08(blockpos1.toCenterPos().add(0.0, 0.5, 0.0).subtract(vec3d));
      }

      return ililiiili1ll1li11 == null ? null : val003.CloudRouter().LineShader().on23(val002.LineShader().EmoteManager(ililiiili1ll1li11));
   }

   public BlockHitResult CloudResponse(Rotation var1) {
      return this.on23(var1, Collections.emptySet());
   }

   public BlockHitResult on23(Rotation var1, Set<BlockPos> var2) {
      BlockHitResult blockhitresult = RaycastUtils.on23(
         minecraftClient3.player.getCameraPosVec(1.0F), var1, minecraftClient3.player.getBlockInteractionRange(), var2xx -> {
            if (var2xx == null) {
               return false;
            }

            BlockPos blockpos = var2xx.getBlockPos();
            if (var2.contains(blockpos)) {
               return false;
            }

            BlockState blockstate = minecraftClient3.world.getBlockState(blockpos);
            BlockState blockstate1 = minecraftClient3.world.getBlockState(blockpos.up());
            return this.Easing(blockstate) || this.on23(blockpos, blockstate, blockstate1) || this.int460() && this.UiAnimation(blockstate, blockstate1);
         }
      );
      return blockhitresult != null && blockhitresult.getType() != Type.MISS ? blockhitresult : null;
   }

   public void on23(BlockHitResult var1, BlockState var2) {
      if (this.Easing(var2)) {
         minecraftClient3.interactionManager.updateBlockBreakingProgress(var1.getBlockPos(), var1.getSide());
         minecraftClient3.player.swingHand(Hand.MAIN_HAND);
         this.BotChatEvent(var1.getBlockPos());
      } else if (this.UiAnimation(var2, minecraftClient3.world.getBlockState(var1.getBlockPos().up()))) {
         if (this.int460()) {
            EffectEngine.on23(var1, Hand.MAIN_HAND);
         }
      } else if (this.on23(var1.getBlockPos(), var2, minecraftClient3.world.getBlockState(var1.getBlockPos().up()))) {
         EffectEngine.on23(this.BotDisconnectEvent(var1.getBlockPos()), Hand.OFF_HAND);
      }
   }

   public void UiAnimation(BlockHitResult var1, BlockState var2) {
      if (this.Easing(var2)) {
         minecraftClient3.interactionManager.updateBlockBreakingProgress(var1.getBlockPos(), var1.getSide());
         this.BotChatEvent(var1.getBlockPos());
      } else if (this.UiAnimation(var2, minecraftClient3.world.getBlockState(var1.getBlockPos().up()))) {
         if (this.int460()) {
            EffectEngine.on23(var1, Hand.MAIN_HAND);
         }
      } else if (this.on23(var1.getBlockPos(), var2, minecraftClient3.world.getBlockState(var1.getBlockPos().up()))) {
         EffectEngine.on23(this.BotDisconnectEvent(var1.getBlockPos()), Hand.OFF_HAND);
      }
   }

   public void BotChatEvent(BlockPos var1) {
      BlockPos blockpos = var1.down();
      BlockState blockstate = minecraftClient3.world.getBlockState(blockpos);
      if (this.int461() && blockstate.isOf(this.call036().block2) && (!this.int462() || this.BotPacketEvent(blockpos))) {
         EffectEngine.on23(this.BotDisconnectEvent(blockpos), Hand.OFF_HAND);
      }
   }

   public BlockHitResult BotDisconnectEvent(BlockPos var1) {
      return new BlockHitResult(var1.toCenterPos().add(0.0, 0.5, 0.0), Direction.UP, var1, false);
   }

   public boolean int459() {
      return ZenithClient.on23().BotFeatureRegistry().BooleanValue() != null && ZenithClient.on23().BotFeatureRegistry().BotFollowEntity() != null
         ? minecraftClient3.player.isUsingItem()
            || minecraftClient3.currentScreen instanceof ChatScreen
            || minecraftClient3.currentScreen instanceof InventoryScreen
            || !(minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler)
         : true;
   }

   public boolean on23(BlockPos var1, BlockState var2, BlockState var3) {
      return this.int461() && this.on23(var2, var3) && (!this.int462() || this.BotPacketEvent(var1));
   }

   public boolean UiAnimation(BlockState var1, BlockState var2) {
      return this.int462() && var2.isAir() && this.ColorAnimator(var1);
   }

   public boolean ColorAnimator(BlockState var1) {
      return var1.isOf(Blocks.GRASS_BLOCK)
         || var1.isOf(Blocks.DIRT_PATH)
         || var1.isOf(Blocks.DIRT)
         || var1.isOf(Blocks.COARSE_DIRT)
         || var1.isOf(Blocks.ROOTED_DIRT);
   }

   public boolean BotWorldJoinEvent(BlockPos var1) {
      BlockState blockstate = minecraftClient3.world.getBlockState(var1);
      return blockstate.isOf(Blocks.FARMLAND)
         || this.ColorAnimator(blockstate) && minecraftClient3.world.getBlockState(var1.up()).isAir();
   }

   public boolean BotPacketEvent(BlockPos var1) {
      if (!this.int462()) {
         return true;
      }

      BlockPos blockpos = var1.up();
      if (!this.BotTickEvent(var1) && !minecraftClient3.world.hasRain(blockpos)) {
         return false;
      }

      BlockState blockstate = minecraftClient3.world.getBlockState(blockpos.up());
      return this.call036() != CropFarmer.CropType.call407 || blockstate.isAir() || blockstate.isOf(Blocks.PITCHER_CROP);
   }

   public boolean BotRespawnEvent(BlockPos var1) {
      return minecraftClient3.world.getBaseLightLevel(var1.up(), 0) >= this.call036().int111;
   }

   public List<BlockPos> UiAnimation(BlockPos var1, BlockPos var2) {
      TreeMap<Integer, List<BlockPos>> treemap = new TreeMap<>();

      for (BlockPos blockpos : getBlocksInZone(var1, var2)) {
         if (this.BotWorldJoinEvent(blockpos)) {
            treemap.computeIfAbsent(blockpos.getY(), var0 -> new ArrayList<>()).add(blockpos);
         }
      }

      List<BlockPos> arraylist = new ArrayList<>();

      for (List<BlockPos> list : treemap.values()) {
         arraylist.addAll(this.EnchantItemSpec(list));
      }

      return arraylist;
   }

   public List<BlockPos> EnchantItemSpec(List<BlockPos> var1) {
      List<BlockPos> list = var1.stream().filter(var1x -> !this.BotTickEvent(var1x)).sorted(comparator).toList();
      if (list.isEmpty()) {
         return Collections.emptyList();
      }

      LinkedHashMap linkedhashmap = new LinkedHashMap();

      for (BlockPos blockpos : var1.stream().sorted(comparator).toList()) {
         BitSet bitset = new BitSet(list.size());

         for (int i = 0; i < list.size(); i++) {
            BlockPos blockpos1 = list.get(i);
            if (Math.abs(blockpos.getX() - blockpos1.getX()) <= 4 && Math.abs(blockpos.getZ() - blockpos1.getZ()) <= 4) {
               bitset.set(i);
            }
         }

         if (!bitset.isEmpty()) {
            linkedhashmap.putIfAbsent(bitset, blockpos);
         }
      }

      ArrayList arraylist = new ArrayList(linkedhashmap.values());
      ArrayList arraylist1 = new ArrayList(linkedhashmap.keySet());
      CropFarmer.FarmTarget i1i11lliliil1i1illl1l1lli_Var160 = new CropFarmer.FarmTarget(arraylist, arraylist1, list.size(), System.nanoTime() + 25000000L);
      return i1i11lliliil1i1illl1l1lli_Var160.float125();
   }

   public boolean BotTickEvent(BlockPos var1) {
      for (BlockPos blockpos : BlockPos.iterate(var1.add(-4, 0, -4), var1.add(4, 1, 4))) {
         if (minecraftClient3.world.getFluidState(blockpos).isIn(FluidTags.WATER)) {
            return true;
         }
      }

      return false;
   }

   public boolean int460() {
      return minecraftClient3.player.getMainHandStack().getItem() instanceof HoeItem;
   }

   public boolean int461() {
      return minecraftClient3.player.getStackInHand(Hand.OFF_HAND).getItem() == this.random10();
   }

   public boolean int462() {
      return this.call036().block2 == Blocks.FARMLAND;
   }

   public CropFarmer.CropType call036() {
      return CropFarmer.CropType.InventoryCodec(this.crop.get());
   }

   @EventTarget(0)
   public void FileLogger(MovementInputEvent var1) {
      if (!this.int459() && this.Velocity() != null) {
         var1.ItemSpec(true);
      }
   }

   public static List<BlockPos> getBlocksInZone(BlockPos var0, BlockPos var1) {
      List<BlockPos> arraylist = new ArrayList<>();
      int i = Math.min(var0.getX(), var1.getX());
      int j = Math.max(var0.getX(), var1.getX());
      int k = Math.min(var0.getY(), var1.getY());
      int l = Math.max(var0.getY(), var1.getY());
      int i1 = Math.min(var0.getZ(), var1.getZ());
      int j1 = Math.max(var0.getZ(), var1.getZ());

      for (int k1 = i; k1 <= j; k1++) {
         for (int l1 = k; l1 <= l; l1++) {
            for (int i2 = i1; i2 <= j1; i2++) {
               arraylist.add(new BlockPos(k1, l1, i2));
            }
         }
      }

      return arraylist;
   }


   public enum Action {
      val374,
      val375,
      val376;
   }

   public enum CropType {
      call406("module.cropFarmer.crop.beetroot", Items.BEETROOT_SEEDS, Items.BEETROOT, Blocks.FARMLAND, 3),
      call432("module.cropFarmer.crop.carrot", Items.CARROT, Items.CARROT, Blocks.FARMLAND, 7),
      call433("module.cropFarmer.crop.netherWart", Items.NETHER_WART, Items.NETHER_WART, Blocks.SOUL_SAND, 3),
      call434("module.cropFarmer.crop.pitcherPlant", Items.PITCHER_POD, Items.PITCHER_PLANT, Blocks.FARMLAND, 4),
      call435("module.cropFarmer.crop.potato", Items.POTATO, Items.POTATO, Blocks.FARMLAND, 7),
      call436("module.cropFarmer.crop.torchflower", Items.TORCHFLOWER_SEEDS, Items.TORCHFLOWER, Blocks.FARMLAND, 2),
      call407("module.cropFarmer.crop.wheat", Items.WHEAT_SEEDS, Items.WHEAT, Blocks.FARMLAND, 7);

      public final String string16;
      public final Item item;
      public final Item item2;
      public final Block block2;
      public final int int111;

      CropType(String var3, Item var4, Item var5, Block var6, int var7) {
         this.string16 = var3;
         this.item = var4;
         this.item2 = var5;
         this.block2 = var6;
         this.int111 = var7;
      }

      public static CropType InventoryCodec(String var0) {
         return Arrays.stream(values()).filter(var1 -> var1.string16.equals(var0)).findFirst().orElse(call406);
      }
   }

   public static final class FarmTarget {
      public final List<BlockPos> list30;
      public final List<BitSet> list31;
      public final List<List<Integer>> list32;
      public final Map<BitSet, Integer> map18 = new HashMap<>();
      public final long long86;
      public List<Integer> list33;

      public FarmTarget(List<BlockPos> var1, List<BitSet> var2, int var3, long var4) {
         this.list30 = var1;
         this.list31 = var2;
         this.long86 = var4;
         this.list32 = new ArrayList<>(var3);

         for (int i = 0; i < var3; i++) {
            this.list32.add(new ArrayList<>());
         }

         for (int k = 0; k < var2.size(); k++) {
            for (int j = var2.get(k).nextSetBit(0); j >= 0; j = var2.get(k).nextSetBit(j + 1)) {
               this.list32.get(j).add(k);
            }
         }
      }

      public List<BlockPos> float125() {
         BitSet bitset = new BitSet(this.list32.size());
         bitset.set(0, this.list32.size());
         this.list33 = this.on23(bitset);
         this.on23(bitset, new ArrayList<>());
         return this.list33.stream().map(this.list30::get).toList();
      }

      public void on23(BitSet var1, List<Integer> var2) {
         if (System.nanoTime() < this.long86 && var2.size() < this.list33.size()) {
            if (var1.isEmpty()) {
               this.list33 = new ArrayList<>(var2);
            } else {
               Integer integer = this.map18.get(var1);
               if (integer == null || integer > var2.size()) {
                  this.map18.put((BitSet)var1.clone(), var2.size());
                  int i = 0;

                  for (BitSet bitset : this.list31) {
                     i = Math.max(i, this.on23(bitset, var1));
                  }

                  if (i != 0 && var2.size() + (var1.cardinality() + i - 1) / i < this.list33.size()) {
                     int k = this.UiAnimation(var1);
                     List<Integer> arraylist = new ArrayList<>(this.list32.get(k));
                     arraylist.sort(Comparator.<Integer>comparingInt(var2x -> this.on23(this.list31.get(var2x), var1)).reversed());

                     for (int j : arraylist) {
                        BitSet bitset1 = (BitSet)var1.clone();
                        bitset1.andNot(this.list31.get(j));
                        var2.add(j);
                        this.on23(bitset1, var2);
                        var2.remove(var2.size() - 1);
                        if (System.nanoTime() >= this.long86) {
                           return;
                        }
                     }
                  }
               }
            }
         }
      }

      public List<Integer> on23(BitSet var1) {
         BitSet bitset = (BitSet)var1.clone();
         List<Integer> arraylist = new ArrayList<>();

         while (!bitset.isEmpty()) {
            int i = -1;
            int j = 0;

            for (int k = 0; k < this.list31.size(); k++) {
               int l = this.on23(this.list31.get(k), bitset);
               if (l > j) {
                  j = l;
                  i = k;
               }
            }

            if (i == -1) {
               break;
            }

            arraylist.add(i);
            bitset.andNot(this.list31.get(i));
         }

         return arraylist;
      }

      public int UiAnimation(BitSet var1) {
         int i = var1.nextSetBit(0);
         int j = Integer.MAX_VALUE;

         for (int k = i; k >= 0; k = var1.nextSetBit(k + 1)) {
            int l = this.list32.get(k).size();
            if (l < j) {
               j = l;
               i = k;
            }
         }

         return i;
      }

      public int on23(BitSet var1, BitSet var2) {
         BitSet bitset = (BitSet)var1.clone();
         bitset.and(var2);
         return bitset.cardinality();
      }
   }

   public static final class InventorySlot {
      public final Action cropFarmerVar143;
      public final long long85;

      public InventorySlot(Action var1, long var2) {
         this.cropFarmerVar143 = var1;
         this.long85 = var2;
      }
   }
}
