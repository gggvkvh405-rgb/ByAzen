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
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potions;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.BrewingStandScreenHandler;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.zenith.ZenithClient;
import org.zenith.core.EffectEngine;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.RotationUpdateStartEvent;
import org.zenith.event.EventTick;
import org.zenith.rotation.Rotation;
import org.zenith.rotation.RotationEasing;
import org.zenith.rotation.RotationMath;
import org.zenith.rotation.RotationTask;
import org.zenith.setting.ModeSetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.I1Type;
import org.zenith.util.RaycastUtils;
import org.zenith.util.ScreenUtils;

@ModuleInfo(name = "AutoBrewing", category = Category.PLAYER, description = "")
public final class AutoBrewing extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoBrewing autoBrewing = new AutoBrewing();
   public static final long long3 = 50L;
   public static final long long4 = 75L;
   public static final long long5 = 20000L;
   public static final long long6 = 1500L;
   public static final int int4 = 5;
   public static final int int5 = 3;
   public static final int int6 = 4;
   public static final int[] val078 = new int[0];
   public static final ModeSetting modeSetting3 = new ModeSetting("Type", "Default", "Default");
   public final CooldownTimer zClass0675 = new CooldownTimer();
   public final List<BlockPos> list2 = new ArrayList<>();
   public final List<BlockPos> list3 = new ArrayList<>();
   public List<Item> list4 = List.of();
   public Map<Item, Integer> map5 = Map.of();
   public AutoBrewing.BrewStage autoBrewingVar143 = AutoBrewing.BrewStage.val216;
   public Rotation var118;
   public BlockPos blockPos;
   public BlockPos blockPos2;
   public int int7;
   public int int8;
   public int int9;
   public int int10;
   public int int11;
   public int int12;
   public int int13 = -1;
   public int int14 = -1;
   public AutoBrewing.Mode autoBrewingVar160 = AutoBrewing.Mode.val024;
   public boolean boolean4;
   public boolean boolean5;
   public AutoBrewing.Option autoBrewingVar159 = AutoBrewing.Option.val079;

   @Override
   public void onEnable() {
      if (minecraftClient3.player != null && minecraftClient3.player.networkHandler != null) {
         this.list4 = this.float324();
         this.map5 = this.NbtItemSpec(this.list4);
         this.int7 = 0;
         this.int8 = 0;
         this.int9 = 0;
         this.int10 = 0;
         this.int11 = 0;
         this.int12 = 0;
         this.float343();
         this.boolean4 = false;
         this.boolean5 = false;
         this.autoBrewingVar159 = AutoBrewing.Option.val079;
         this.blockPos = null;
         this.blockPos2 = null;
         this.var118 = null;
         this.list2.clear();
         this.list3.clear();
         this.int430();
         this.zClass0675.reset();
         super.onEnable();
      } else {
         this.setEnabled(false);
      }
   }

   @Override
   public void onDisable() {
      this.autoBrewingVar143 = AutoBrewing.BrewStage.val216;
      this.var118 = null;
      this.blockPos = null;
      this.blockPos2 = null;
      this.list2.clear();
      this.list3.clear();
      this.int8 = 0;
      this.int9 = 0;
      this.autoBrewingVar159 = AutoBrewing.Option.val079;
      super.onDisable();
   }

   @EventTarget
   public void ItemSpec(RotationUpdateStartEvent var1) {
      if (this.var118 != null) {
         ZenithClient.on23().CloudRouter().on23(new RotationTask(this.var118, () -> {
            RotationEasing i1ii11ilil1il1ii = ZenithClient.on23().CloudRouter().int150();
            return i1ii11ilil1il1ii.on23(i1ii11ilil1il1ii.HudPreviewItem(), this.var118);
         }, ZenithClient.on23().CloudRouter().int150().HudPreviewItem()), 5, this);
      }
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && minecraftClient3.interactionManager != null) {
         switch (this.autoBrewingVar143) {
            case val216:
            default:
               break;
            case val343:
               this.float326();
               break;
            case val344:
               this.float328();
               break;
            case val345:
               this.float329();
               break;
            case val346:
               this.float330();
               break;
            case val347:
               this.float333();
               break;
            case val348:
               this.float334();
               break;
            case val108:
               this.float335();
               break;
            case val217:
               this.float340();
               break;
            case val218:
               this.boolean187();
               break;
            case val349:
               this.int403();
               break;
            case val160:
               this.call025();
               break;
            case val219:
               this.zClass016Var134();
               break;
            case val220:
               this.float341();
         }
      } else {
         this.setToggled(false);
      }
   }

   public void int430() {
      this.int7 = this.float323();
      int i = this.int7 * 3;
      if (this.int7 <= 0) {
         if (!this.boolean4) {
            this.boolean4 = true;
            this.boolean5 = false;
            this.autoBrewingVar143 = AutoBrewing.BrewStage.val343;
         } else {
            this.float325();
            this.setToggled(false);
         }
      } else {
         StyledTextBuilder.RefreshCacheEvent("Ресурсов хватает на " + i + " зелий (" + this.int7 + " варки)");
         this.autoBrewingVar143 = AutoBrewing.BrewStage.val108;
      }
   }

   public void float320() {
      this.float322();
      this.setToggled(false);
   }

   public void float321() {
      this.autoBrewingVar143 = AutoBrewing.BrewStage.val217;
      this.zClass0675.reset();
   }

   public void float322() {
      ScreenHandler screenhandler = minecraftClient3.player.currentScreenHandler;
      if (screenhandler != null && !screenhandler.getCursorStack().isEmpty()) {
         this.PotionItemBuilder(screenhandler);
         if (!screenhandler.getCursorStack().isEmpty()) {
            return;
         }
      }

      ScreenUtils.closeScreen();
   }

   public int float323() {
      int i = this.float344() / 3;

      for (Entry<Item, Integer> entry : this.map5.entrySet()) {
         i = Math.min(i, this.ItemSpec(entry.getKey()) / entry.getValue());
      }

      return i;
   }

   public List<Item> float324() {
      return I1Type.RotationSmoothStrategy(modeSetting3.get()).call238();
   }

   public Map<Item, Integer> NbtItemSpec(List<Item> var1) {
      Map<Item, Integer> linkedhashmap = new LinkedHashMap<>();
      this.on23(linkedhashmap, Items.BLAZE_POWDER, 1);

      for (Item item : var1) {
         this.on23(linkedhashmap, item, 1);
      }

      return linkedhashmap;
   }

   public void on23(Map<Item, Integer> var1, Item var2, int var3) {
      var1.put(var2, var1.getOrDefault(var2, 0) + var3);
   }

   public void float325() {
      ArrayList<String> arraylist = new ArrayList<>();
      int i = this.float344();
      if (i < 3) {
         arraylist.add(Items.POTION.getName().getString() + " " + i + "/3");
      }

      for (Entry<Item, Integer> entry : this.map5.entrySet()) {
         int j = this.ItemSpec(entry.getKey());
         int k = entry.getValue();
         if (j < k) {
            arraylist.add(entry.getKey().getName().getString() + " " + j + "/" + k);
         }
      }

      String s = modeSetting3.getValue().getName();
      if (arraylist.isEmpty()) {
         StyledTextBuilder.RefreshCacheEvent("Для зелья " + s + " ресурсов хватает");
      } else {
         StyledTextBuilder.RotationLegitStrategy("Для зелья " + s + " не хватает: " + String.join(", ", arraylist));
      }
   }

   public void float326() {
      this.float331();
      if (this.list3.isEmpty()) {
         this.float325();
         this.setToggled(false);
      } else {
         this.float327();
      }
   }

   public void float327() {
      if (this.list3.isEmpty()) {
         this.float322();
         if (!this.boolean5) {
            this.float325();
            this.setToggled(false);
         } else {
            this.int430();
         }
      } else {
         this.blockPos2 = this.list3.removeFirst();
         this.autoBrewingVar143 = AutoBrewing.BrewStage.val344;
         this.zClass0675.reset();
      }
   }

   public void float328() {
      this.on23(AutoBrewing.BrewStage.val345, this::float327);
   }

   public void float329() {
      GenericContainerScreenHandler genericcontainerscreenhandler = this.int432();
      if (genericcontainerscreenhandler == null) {
         this.float328();
      } else if (this.zClass0675.EventModifyMouseRotationInput(50L)) {
         Slot slot = this.UiAnimation(genericcontainerscreenhandler);
         if (slot == null) {
            this.float322();
            this.blockPos2 = null;
            this.float327();
         } else if (!this.ConfigJsonUtil(slot.getStack())) {
            this.float322();
            this.blockPos2 = null;
            this.float327();
         } else {
            ScreenUtils.on23(genericcontainerscreenhandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, false);
            this.boolean5 = true;
            this.zClass0675.reset();
         }
      }
   }

   public Slot UiAnimation(GenericContainerScreenHandler var1) {
      int i = var1.getInventory().size();

      for (int j = 0; j < i; j++) {
         Slot slot = var1.getSlot(j);
         if (this.AnalyticsTracker(slot.getStack())) {
            return slot;
         }
      }

      return null;
   }

   public boolean AnalyticsTracker(ItemStack var1) {
      return this.CloudResponse(var1) || this.map5.containsKey(var1.getItem());
   }

   public boolean ConfigJsonUtil(ItemStack var1) {
      for (int i = 0; i < minecraftClient3.player.getInventory().size(); i++) {
         ItemStack itemstack = minecraftClient3.player.getInventory().getStack(i);
         if (itemstack.isEmpty() || this.Easing(itemstack, var1) && itemstack.getCount() < itemstack.getMaxCount()) {
            return true;
         }
      }

      return false;
   }

   public void float330() {
      this.float331();
      this.float332();
   }

   public void float331() {
      this.list3.clear();

      for (BlockPos blockpos : EffectEngine.on23(minecraftClient3.player.getBlockPos(), 5.0F, 3.0F)) {
         BlockPos blockpos1 = blockpos.toImmutable();
         if (this.UiAnimation(minecraftClient3.world.getBlockState(blockpos1)) && this.ColorAnimator(blockpos1) != null) {
            this.list3.add(blockpos1);
         }
      }

      this.list3.sort(Comparator.comparingDouble(var0 -> var0.toCenterPos().squaredDistanceTo(minecraftClient3.player.getEyePos())));
   }

   public void float332() {
      if (this.list3.isEmpty()) {
         this.int431();
      } else {
         this.blockPos2 = this.list3.removeFirst();
         this.autoBrewingVar143 = AutoBrewing.BrewStage.val347;
         this.zClass0675.reset();
      }
   }

   public void float333() {
      this.on23(AutoBrewing.BrewStage.val348, this::float332);
   }

   public void on23(AutoBrewing.BrewStage var1, Runnable var2) {
      if (this.blockPos2 == null) {
         var2.run();
      } else if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
         this.autoBrewingVar143 = var1;
         this.var118 = null;
         this.zClass0675.reset();
      } else if (this.zClass0675.EventModifyMouseRotationInput(1500L)) {
         this.blockPos2 = null;
         var2.run();
      } else {
         BlockHitResult blockhitresult = this.ColorAnimator(this.blockPos2);
         if (blockhitresult == null) {
            this.blockPos2 = null;
            var2.run();
         } else {
            this.var118 = RotationMath.BotChatEvent(blockhitresult.getPos());
            if (this.Easing(this.blockPos2) && this.zClass0675.EventModifyMouseRotationInput(50L)) {
               EffectEngine.on23(blockhitresult, Hand.MAIN_HAND);
               this.zClass0675.reset();
            }
         }
      }
   }

   public void float334() {
      GenericContainerScreenHandler genericcontainerscreenhandler = this.int432();
      if (genericcontainerscreenhandler == null) {
         this.float333();
      } else if (this.zClass0675.EventModifyMouseRotationInput(50L)) {
         Slot slot = this.on23(genericcontainerscreenhandler, this::TradeGuardService);
         if (slot == null) {
            this.float322();
            this.int431();
         } else if (!this.on23(genericcontainerscreenhandler, slot.getStack())) {
            this.float322();
            this.blockPos2 = null;
            this.float332();
         } else {
            ScreenUtils.on23(genericcontainerscreenhandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, false);
            this.zClass0675.reset();
         }
      }
   }

   public boolean on23(GenericContainerScreenHandler var1, ItemStack var2) {
      int i = var1.getInventory().size();

      for (int j = 0; j < i; j++) {
         ItemStack itemstack = var1.getSlot(j).getStack();
         if (itemstack.isEmpty() || this.Easing(itemstack, var2) && itemstack.getCount() < itemstack.getMaxCount()) {
            return true;
         }
      }

      return false;
   }

   public void int431() {
      this.float322();
      this.blockPos2 = null;
      if (this.int7 <= 0) {
         StyledTextBuilder.RefreshCacheEvent("Автозельеварение завершено");
         this.setToggled(false);
      } else {
         this.float336();
      }
   }

   public void float335() {
      this.list2.clear();

      for (BlockPos blockpos : EffectEngine.on23(minecraftClient3.player.getBlockPos(), 5.0F, 3.0F)) {
         BlockPos blockpos1 = blockpos.toImmutable();
         if (minecraftClient3.world.getBlockState(blockpos1).isOf(Blocks.BREWING_STAND) && this.ColorAnimator(blockpos1) != null) {
            this.list2.add(blockpos1);
         }
      }

      this.list2.sort(Comparator.comparingDouble(var0 -> var0.toCenterPos().squaredDistanceTo(minecraftClient3.player.getEyePos())));
      if (this.list2.isEmpty()) {
         StyledTextBuilder.RotationLegitStrategy("Рядом нет доступных зельеварок");
         this.setToggled(false);
      } else {
         this.float336();
      }
   }

   public void float336() {
      this.int8 = Math.min(this.int7, this.list2.size());
      this.int9 = 0;
      this.int10 = 0;
      this.autoBrewingVar159 = AutoBrewing.Option.val079;
      this.float337();
   }

   public void float337() {
      if (this.int9 >= this.int8) {
         this.float339();
      } else {
         this.blockPos = this.list2.get(this.int9);
         this.int11 = 0;
         this.int12 = 0;
         this.float343();
         this.autoBrewingVar143 = AutoBrewing.BrewStage.val217;
         this.zClass0675.reset();
      }
   }

   public void float338() {
      this.float322();
      this.int9++;
      this.float337();
   }

   public void float339() {
      this.float322();
      this.int9 = 0;
      this.float343();
      if (this.autoBrewingVar159 == AutoBrewing.Option.val079) {
         this.autoBrewingVar159 = AutoBrewing.Option.val221;
         this.blockPos = null;
         this.autoBrewingVar143 = AutoBrewing.BrewStage.val219;
         this.zClass0675.reset();
      } else if (this.autoBrewingVar159 == AutoBrewing.Option.val222) {
         this.autoBrewingVar159 = AutoBrewing.Option.val221;
         this.blockPos = null;
         this.autoBrewingVar143 = AutoBrewing.BrewStage.val219;
         this.zClass0675.reset();
      } else {
         this.int7 = this.int7 - this.int8;
         this.autoBrewingVar143 = AutoBrewing.BrewStage.val346;
         this.zClass0675.reset();
      }
   }

   public void float340() {
      if (this.blockPos == null) {
         this.autoBrewingVar143 = AutoBrewing.BrewStage.val108;
      } else if (minecraftClient3.player.currentScreenHandler instanceof BrewingStandScreenHandler) {
         this.autoBrewingVar143 = switch (this.autoBrewingVar159) {
            case val079 -> AutoBrewing.BrewStage.val218;
            case val222 -> AutoBrewing.BrewStage.val160;
            case val221 -> AutoBrewing.BrewStage.val160;
            case val350 -> AutoBrewing.BrewStage.val220;
         };
         this.int11 = 0;
         this.zClass0675.reset();
         this.var118 = null;
      } else if (this.zClass0675.EventModifyMouseRotationInput(1500L)) {
         this.blockPos = null;
         this.autoBrewingVar143 = AutoBrewing.BrewStage.val108;
         this.var118 = null;
      } else {
         BlockHitResult blockhitresult = this.ColorAnimator(this.blockPos);
         if (blockhitresult == null) {
            this.blockPos = null;
            this.autoBrewingVar143 = AutoBrewing.BrewStage.val108;
            this.var118 = null;
         } else {
            this.var118 = RotationMath.BotChatEvent(blockhitresult.getPos());
            if (this.Easing(this.blockPos) && this.zClass0675.EventModifyMouseRotationInput(50L)) {
               EffectEngine.on23(blockhitresult, Hand.MAIN_HAND);
               this.zClass0675.reset();
            }
         }
      }
   }

   public void boolean187() {
      BrewingStandScreenHandler brewingstandscreenhandler = this.float342();
      if (brewingstandscreenhandler == null) {
         this.float321();
      } else if (this.zClass0675.EventModifyMouseRotationInput(75L)) {
         if (this.autoBrewingVar160 == AutoBrewing.Mode.val024 && !brewingstandscreenhandler.getCursorStack().isEmpty()) {
            this.PotionItemBuilder(brewingstandscreenhandler);
            this.zClass0675.reset();
         } else if (this.int11 >= val078.length) {
            int j = this.on23(brewingstandscreenhandler);
            if (j != -1) {
               this.int11 = j;
               this.zClass0675.reset();
            } else {
               this.autoBrewingVar143 = AutoBrewing.BrewStage.val349;
               this.zClass0675.reset();
            }
         } else {
            int i = val078[this.int11];
            if (this.autoBrewingVar160 != AutoBrewing.Mode.val024) {
               this.on23(brewingstandscreenhandler, this::CloudResponse, i);
               this.zClass0675.reset();
            } else {
               ItemStack itemstack = brewingstandscreenhandler.getSlot(i).getStack();
               if (this.CloudResponse(itemstack)) {
                  this.float343();
                  this.int11++;
                  this.zClass0675.reset();
               } else if (!itemstack.isEmpty()) {
                  this.int12 = 0;
                  this.autoBrewingVar143 = AutoBrewing.BrewStage.val220;
                  this.zClass0675.reset();
               } else if (!this.on23(brewingstandscreenhandler, this::CloudResponse, i)) {
                  StyledTextBuilder.RotationLegitStrategy("Не найдены бутылочки с водой");
                  this.float320();
               } else {
                  this.zClass0675.reset();
               }
            }
         }
      }
   }

   public void int403() {
      BrewingStandScreenHandler brewingstandscreenhandler = this.float342();
      if (brewingstandscreenhandler == null) {
         this.float321();
      } else if (this.zClass0675.EventModifyMouseRotationInput(50L)) {
         if (this.autoBrewingVar160 == AutoBrewing.Mode.val024 && !brewingstandscreenhandler.getCursorStack().isEmpty()) {
            this.PotionItemBuilder(brewingstandscreenhandler);
            this.zClass0675.reset();
         } else if (this.autoBrewingVar160 != AutoBrewing.Mode.val024) {
            this.on23(brewingstandscreenhandler, var0 -> var0.isOf(Items.BLAZE_POWDER), 4);
            this.zClass0675.reset();
         } else if (!brewingstandscreenhandler.getSlot(4).getStack().isEmpty()) {
            int i = this.on23(brewingstandscreenhandler);
            if (i != -1) {
               this.int11 = i;
               this.autoBrewingVar143 = AutoBrewing.BrewStage.val218;
               this.zClass0675.reset();
            } else {
               this.float343();
               this.autoBrewingVar143 = AutoBrewing.BrewStage.val160;
            }
         } else if (!this.on23(brewingstandscreenhandler, var0 -> var0.isOf(Items.BLAZE_POWDER), 4)) {
            StyledTextBuilder.RotationLegitStrategy("Не найдено топливо для зельеварки");
            this.float320();
         } else {
            this.zClass0675.reset();
         }
      }
   }

   public void call025() {
      BrewingStandScreenHandler brewingstandscreenhandler = this.float342();
      if (brewingstandscreenhandler == null) {
         this.float321();
      } else if (this.zClass0675.EventModifyMouseRotationInput(50L)) {
         if (this.autoBrewingVar160 == AutoBrewing.Mode.val024 && !brewingstandscreenhandler.getCursorStack().isEmpty()) {
            this.PotionItemBuilder(brewingstandscreenhandler);
            this.zClass0675.reset();
         } else if (this.int10 >= this.list4.size()) {
            this.float338();
         } else {
            Item item = this.list4.get(this.int10);
            if (this.autoBrewingVar160 != AutoBrewing.Mode.val024) {
               this.on23(brewingstandscreenhandler, var1x -> var1x.isOf(item), 3);
               this.zClass0675.reset();
            } else if (!brewingstandscreenhandler.getSlot(3).getStack().isEmpty()) {
               this.float343();
               this.float338();
            } else if (!this.on23(brewingstandscreenhandler, var1x -> var1x.isOf(item), 3)) {
               StyledTextBuilder.RotationLegitStrategy("Не найден ингредиент: " + item.getName().getString());
               this.float320();
            } else {
               this.zClass0675.reset();
            }
         }
      }
   }

   public void zClass016Var134() {
      if (this.zClass0675.EventModifyMouseRotationInput(20000L)) {
         this.int10++;
         if (this.int10 >= this.list4.size()) {
            this.autoBrewingVar159 = AutoBrewing.Option.val350;
         } else {
            this.autoBrewingVar159 = AutoBrewing.Option.val222;
         }

         this.float337();
      }
   }

   public void float341() {
      BrewingStandScreenHandler brewingstandscreenhandler = this.float342();
      if (brewingstandscreenhandler == null) {
         this.float321();
      } else if (this.zClass0675.EventModifyMouseRotationInput(50L)) {
         if (this.int12 >= val078.length) {
            this.float338();
         } else {
            int i = val078[this.int12];
            if (brewingstandscreenhandler.getSlot(i).getStack().isEmpty()) {
               this.int12++;
               this.zClass0675.reset();
            } else {
               ScreenUtils.on23(brewingstandscreenhandler.syncId, i, 0, SlotActionType.QUICK_MOVE, false);
               this.zClass0675.reset();
            }
         }
      }
   }

   public BrewingStandScreenHandler float342() {
      return minecraftClient3.player.currentScreenHandler instanceof BrewingStandScreenHandler brewingstandscreenhandler ? brewingstandscreenhandler : null;
   }

   public GenericContainerScreenHandler int432() {
      return minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler ? genericcontainerscreenhandler : null;
   }

   public int on23(BrewingStandScreenHandler var1) {
      if (var1.getCursorStack().isEmpty() && this.autoBrewingVar160 == AutoBrewing.Mode.val024) {
         for (int i = 0; i < val078.length; i++) {
            if (!this.CloudResponse(var1.getSlot(val078[i]).getStack())) {
               return i;
            }
         }

         return -1;
      } else {
         return 0;
      }
   }

   public boolean on23(ScreenHandler var1, AutoBrewing.Contract var2, int var3) {
      if (this.autoBrewingVar160 == AutoBrewing.Mode.val024) {
         Slot slot = this.on23(var1, var2);
         if (slot == null) {
            return false;
         }

         this.int13 = slot.id;
         this.int14 = var3;
         this.autoBrewingVar160 = AutoBrewing.Mode.val351;
         ScreenUtils.on23(var1.syncId, slot.id, 0, SlotActionType.PICKUP, false);
         return true;
      } else {
         if (this.int14 != var3) {
            this.float343();
            return true;
         }

         if (this.autoBrewingVar160 == AutoBrewing.Mode.val351) {
            if (var1.getCursorStack().isEmpty()) {
               this.float343();
               return true;
            } else {
               ScreenUtils.on23(var1.syncId, this.int14, 1, SlotActionType.PICKUP, false);
               this.autoBrewingVar160 = AutoBrewing.Mode.val352;
               return true;
            }
         } else if (this.autoBrewingVar160 == AutoBrewing.Mode.val352) {
            if (var1.getCursorStack().isEmpty()) {
               this.float343();
               return true;
            } else {
               this.PotionItemBuilder(var1);
               this.autoBrewingVar160 = AutoBrewing.Mode.val485;
               return true;
            }
         } else {
            if (var1.getCursorStack().isEmpty()) {
               this.float343();
            } else {
               this.PotionItemBuilder(var1);
            }

            return true;
         }
      }
   }

   public void float343() {
      this.int13 = -1;
      this.int14 = -1;
      this.autoBrewingVar160 = AutoBrewing.Mode.val024;
   }

   public void PotionItemBuilder(ScreenHandler var1) {
      if (var1.getCursorStack().isEmpty()) {
         this.float343();
      } else {
         Slot slot = this.TextScanner(var1, this.int13);
         if (this.on23(slot, var1.getCursorStack())) {
            ScreenUtils.on23(var1.syncId, slot.id, 0, SlotActionType.PICKUP, false);
         } else {
            slot = this.on23(var1, ItemStack::isEmpty);
            if (slot != null) {
               ScreenUtils.on23(var1.syncId, slot.id, 0, SlotActionType.PICKUP, false);
            } else {
               StyledTextBuilder.RotationLegitStrategy("Нет свободного слота, чтобы вернуть предмет с курсора");
            }
         }
      }
   }

   public Slot TextScanner(ScreenHandler var1, int var2) {
      return var1.slots.stream().filter(var1x -> var1x.id == var2).findFirst().orElse(null);
   }

   public boolean on23(Slot var1, ItemStack var2) {
      return var1 != null
         && var1.inventory instanceof PlayerInventory
         && (var1.getStack().isEmpty() || this.Easing(var1.getStack(), var2) && var1.getStack().getCount() < var1.getStack().getMaxCount());
   }

   public boolean Easing(ItemStack var1, ItemStack var2) {
      return ItemStack.areItemsEqual(var1, var2) && ItemStack.areEqual(var1, var2);
   }

   public Slot on23(ScreenHandler var1, AutoBrewing.Contract var2) {
      return var1.slots
         .stream()
         .filter(var0 -> var0.inventory instanceof PlayerInventory)
         .filter(var1x -> var2.matches(var1x.getStack()))
         .findFirst()
         .orElse(null);
   }

   public boolean CloudResponse(ItemStack var1) {
      if (!var1.isOf(Items.POTION)) {
         return false;
      }

      PotionContentsComponent potioncontentscomponent = (PotionContentsComponent)var1.get(DataComponentTypes.POTION_CONTENTS);
      return potioncontentscomponent != null
         && potioncontentscomponent.potion().isPresent()
         && ((RegistryEntry)potioncontentscomponent.potion().get()).equals(Potions.WATER);
   }

   public boolean TradeGuardService(ItemStack var1) {
      if (!var1.isOf(Items.POTION)) {
         return false;
      }

      PotionContentsComponent potioncontentscomponent = (PotionContentsComponent)var1.get(DataComponentTypes.POTION_CONTENTS);
      return potioncontentscomponent != null
         && potioncontentscomponent.potion().isPresent()
         && !((RegistryEntry)potioncontentscomponent.potion().get()).equals(Potions.WATER);
   }

   public int float344() {
      int i = 0;

      for (int j = 0; j < minecraftClient3.player.getInventory().size(); j++) {
         ItemStack itemstack = minecraftClient3.player.getInventory().getStack(j);
         if (this.CloudResponse(itemstack)) {
            i += itemstack.getCount();
         }
      }

      return i;
   }

   public int ItemSpec(Item var1) {
      int i = 0;

      for (int j = 0; j < minecraftClient3.player.getInventory().size(); j++) {
         if (minecraftClient3.player.getInventory().getStack(j).isOf(var1)) {
            i += minecraftClient3.player.getInventory().getStack(j).getCount();
         }
      }

      return i;
   }

   public boolean Easing(BlockPos var1) {
      Rotation ililiiili1ll1li11 = ZenithClient.on23().CloudRouter().LineShader();
      BlockHitResult blockhitresult = RaycastUtils.on23(
         minecraftClient3.player.getCameraPosVec(1.0F),
         ililiiili1ll1li11,
         minecraftClient3.player.getBlockInteractionRange(),
         var1xx -> var1xx != null && var1xx.getBlockPos().equals(var1)
      );
      return blockhitresult != null && blockhitresult.getType() != Type.MISS && blockhitresult.getBlockPos().equals(var1);
   }

   public BlockHitResult ColorAnimator(BlockPos var1) {
      BlockState blockstate = minecraftClient3.world.getBlockState(var1);
      if (!blockstate.isOf(Blocks.BREWING_STAND) && !this.UiAnimation(blockstate)) {
         return null;
      }

      Vec3d vec3d = var1.toCenterPos();
      ArrayList<Vec3d> arraylist = new ArrayList<>();
      arraylist.add(vec3d);

      for (Direction direction : Direction.values()) {
         arraylist.add(vec3d.add(Vec3d.of(direction.getVector()).multiply(0.5)));
      }

      Vec3d vec3d2 = minecraftClient3.player.getCameraPosVec(1.0F);
      double d0 = minecraftClient3.player.getBlockInteractionRange();

      for (Vec3d vec3d1 : arraylist) {
         Rotation ililiiili1ll1li11 = RotationMath.BotChatEvent(vec3d1);
         BlockHitResult blockhitresult = RaycastUtils.on23(vec3d2, ililiiili1ll1li11, d0, var1xx -> var1xx != null && var1xx.getBlockPos().equals(var1));
         if (blockhitresult != null && blockhitresult.getType() != Type.MISS && blockhitresult.getBlockPos().equals(var1)) {
            return blockhitresult;
         }
      }

      return null;
   }

   public boolean UiAnimation(BlockState var1) {
      return var1.isOf(Blocks.CHEST) || var1.isOf(Blocks.TRAPPED_CHEST) || var1.isOf(Blocks.BARREL);
   }


   public enum BrewStage {
      val216,
      val343,
      val344,
      val345,
      val346,
      val347,
      val348,
      val108,
      val217,
      val218,
      val349,
      val160,
      val219,
      val220;
   }

   public enum Option {
      val079,
      val222,
      val221,
      val350;
   }

   public enum Mode {
      val024,
      val351,
      val352,
      val485;
   }

   @FunctionalInterface
   public interface Contract {
      boolean matches(ItemStack var1);
   }
}
