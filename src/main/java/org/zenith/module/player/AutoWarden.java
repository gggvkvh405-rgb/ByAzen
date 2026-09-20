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

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalGetToBlock;
import baritone.api.pathing.goals.GoalNear;
import baritone.api.pathing.goals.GoalXZ;
import com.darkmagician6.eventapi.EventManager;
import com.darkmagician6.eventapi.EventTarget;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity.TextDisplayEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.zenith.core.BaritoneBridge;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.ChatMessageEvent;
import org.zenith.event.DataChangedEvent;
import org.zenith.event.HudRenderEvent;
import org.zenith.event.EventTick;
import org.zenith.render.ScreenProjection;
import org.zenith.setting.TextSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.setting.ButtonSetting;
import org.zenith.util.AimUtils;
import org.zenith.util.CooldownTimer;
import org.zenith.util.ScreenUtils;
import org.zenith.utility.render.display.base.CustomDrawContext;

@ModuleInfo(name = "AutoWarden", category = Category.PLAYER, description = "Циклический рейд города Вардена 1.21.4")
public class AutoWarden extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoWarden autoWarden = new AutoWarden();
   public static final Pattern pattern5 = Pattern.compile("(?!)");
   public final TextSetting u0414U043eU043c1 = new TextSetting("Дом #1", "Хранилище (зелья)", "home_storage", "home_storage");
   public final TextSetting u0414U043eU043c2 = new TextSetting("Дом #2", "Город Вардена", "home_warden", "home_warden");
   public final TextSetting u0410U043dU043aU0430U0414U043eU043cU0430 = new TextSetting("Анка Дома", "Анархия с Домом #1", "", "");
   public final TextSetting u0410U043dU043aU0430U0412U0430U0440U0434U0435U043dU0430 = new TextSetting("Анка Вардена", "Анархия с Домом #2", "", "");
   public static final long long25 = 30000L;
   public static final double doubleField = 30.0;
   public static final long long26 = 30000L;
   public static final long long27 = 30000L;
   public static final long long28 = 8000L;
   public static final int int54 = 50;
   public static final long long29 = 60000L;
   public final NumberSetting u0441U0443U043dU0434U0443U043aU043eU0432U0437U0430U0437U0430U0445U043eU0434 = new NumberSetting(
      "Макс. сундуков за заход", 3.0F, 1.0F, 10.0F, 1.0F, "Сколько сундуков лутать до сдачи", "шт"
   );
   public final ButtonSetting u0421U0443U043dU0434U0443U043aU0438 = new ButtonSetting("Сундуки", () -> autoWarden.call243());
   public AutoWarden.State autoWardenVar143 = AutoWarden.State.val165;
   public final CooldownTimer zClass06714 = new CooldownTimer();
   public BlockPos blockPos9 = null;
   public ArmorStandEntity armorStandEntity = null;
   public long long30 = 0L;
   public final CooldownTimer zClass06715 = new CooldownTimer();
   public int int55 = 0;
   public boolean boolean15 = false;
   public boolean boolean16 = false;
   public boolean boolean17 = false;
   public boolean boolean18 = false;
   public boolean boolean19 = false;
   public boolean boolean20 = false;
   public int int56 = 0;
   public final HashMap<BlockPos, Long> hashMap = new HashMap<>();
   public final HashSet<BlockPos> hashSet = new HashSet<>();
   public boolean boolean21 = false;
   public int int57 = 0;
   public int int58 = 0;
   public long long31 = 0L;
   public BlockPos blockPos10 = null;
   public BlockPos blockPos11 = null;
   public float float7;
   public String string9;
   public long long32;
   public long long33;
   public int int59;
   public int int60;
   public BlockPos blockPos12;
   public final List<BlockPos> list9 = new ArrayList<>();
   public int int61;
   public boolean boolean22;
   public int int62;
   public int int63;
   public Vec3d vec3d2;
   public int int64;
   public String string10;
   public String string11;
   public String string12;
   public AutoWarden.State autoWardenVar1432;
   public ClientWorld clientWorld;
   public boolean boolean23;
   public int int65;
   public boolean boolean24;
   public BlockPos blockPos13;
   public long long34;
   public boolean boolean25;
   public long long35;
   public long long36;
   public static final ItemStack itemStack3 = ItemStack.EMPTY;
   public static final ItemStack itemStack4 = ItemStack.EMPTY;
   public float float8;
   public final Object object2 = new AutoWarden.NetworkListener(this);
   public final Object object3 = new AutoWarden.RenderListener(this);

   public AutoWarden() {
      EventManager.register(this.object3);
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         this.call120();
         long i = System.currentTimeMillis();
         StatusEffectInstance statuseffectinstance = minecraftClient3.player.getStatusEffect(StatusEffects.INVISIBILITY);
         if (statuseffectinstance != null) {
            this.long35 = statuseffectinstance.isInfinite() ? Long.MAX_VALUE : i + statuseffectinstance.getDuration() * 50L;
         }

         StatusEffectInstance statuseffectinstance1 = minecraftClient3.player.getStatusEffect(StatusEffects.SPEED);
         if (statuseffectinstance1 != null) {
            this.long36 = statuseffectinstance1.isInfinite() ? Long.MAX_VALUE : i + statuseffectinstance1.getDuration() * 50L;
         }

         float f = minecraftClient3.player.getHealth();
         if (this.float7 > 0.0F && f < this.float7 - 0.01F && f > 0.0F && this.autoWardenVar143 != AutoWarden.State.val165) {
            PlayerEntity playerentity = this.PotionItemBuilder(12.0);
            if (playerentity != null) {
               this.string9 = playerentity.getGameProfile().name();
               this.long32 = i;
            }

            if (this.autoWardenVar143 == AutoWarden.State.val110) {
               this.long31 = i + 30000L;
            } else if (this.call244()) {
               this.float163();
               if (minecraftClient3.player.currentScreenHandler != minecraftClient3.player.playerScreenHandler) {
                  minecraftClient3.player.closeHandledScreen();
               }

               this.call089();
               if (this.blockPos9 != null) {
                  this.hashMap.put(this.blockPos9, i);
               }

               this.blockPos9 = null;
               this.notify("Получил урон — убегаю");
               this.long31 = i + 30000L;
               this.blockPos10 = null;
               this.autoWardenVar143 = AutoWarden.State.val110;
               this.zClass06714.reset();
            }
         }

         this.float7 = f;
         if (this.autoWardenVar143 == AutoWarden.State.val165
            || this.boolean15
            || !minecraftClient3.player.isDead() && !(minecraftClient3.player.getHealth() <= 0.0F)) {
            if (!this.NbtEditor(i)) {
               this.PotionItemBuilder(i);
               switch (this.autoWardenVar143) {
                  case val166:
                     this.float165();
                     break;
                  case val356:
                     this.boolean188();
                     break;
                  case val019:
                     this.double142();
                     break;
                  case val111:
                     this.double143();
                     break;
                  case val112:
                     this.double144();
                     break;
                  case val225:
                     this.boolean189();
                     break;
                  case val226:
                     this.float166();
                     break;
                  case val227:
                     this.call245();
                     break;
                  case val357:
                     this.call246();
                     break;
                  case val083:
                     this.call247();
                     break;
                  case val228:
                     this.call187();
                     break;
                  case val358:
                     this.call121();
                     break;
                  case val359:
                     this.call188();
                     break;
                  case val167:
                     this.float385();
                     break;
                  case val229:
                     this.float167();
                     break;
                  case val360:
                     this.float168();
                     break;
                  case val084:
                     this.float169();
                     break;
                  case val230:
                     this.float351();
                     break;
                  case val361:
                     this.float352();
                     break;
                  case val362:
                     this.call189();
                     break;
                  case val363:
                     this.call190();
                     break;
                  case val364:
                     this.call191();
                     break;
                  case val365:
                     this.call192();
                     break;
                  case val366:
                     this.call193();
                     break;
                  case val231:
                     this.call248();
                     break;
                  case val085:
                     this.call249();
                     break;
                  case val168:
                     this.call250();
                     break;
                  case val367:
                     this.float349();
                     break;
                  case val169:
                     this.float347();
                     break;
                  case val110:
                     this.call251();
               }
            }
         } else {
            this.boolean15 = true;
            this.long35 = 0L;
            this.long36 = 0L;
            if (this.string9 != null && i - this.long32 < 15000L) {
               minecraftClient3.player.networkHandler.sendChatCommand("report " + this.string9);
               this.notify("Репорт на " + this.string9);
               this.string9 = null;
            }

            this.notify("Смерть! Еду за зельем...");
            this.float163();
            this.autoWardenVar143 = AutoWarden.State.val357;
            this.zClass06714.reset();
         }
      }
   }

   public void UiAnimation(String var1, AutoWarden.State var2) {
      if (this.boolean24 && var1.equals(this.u0414U043eU043c2.getValue())) {
         this.boolean24 = false;
         this.float163();
         this.notify("Анка сменена — пью зелья перед домом");
         this.Event08(true);
      } else if (var1.equals(this.u0414U043eU043c1.getValue()) && this.double141()) {
         this.notify("Уже на базе — телепорт не нужен");
         this.float163();
         this.vec3d2 = minecraftClient3.player.getEntityPos();
         this.int64 = 0;
         this.autoWardenVar143 = var2;
         this.zClass06714.reset();
      } else if (var1.equals(this.u0414U043eU043c2.getValue()) && this.double140()) {
         this.notify("Уже в городе — телепорт не нужен");
         this.float163();
         this.vec3d2 = minecraftClient3.player.getEntityPos();
         this.int64 = 0;
         this.autoWardenVar143 = var2;
         this.zClass06714.reset();
      } else {
         this.float163();
         this.vec3d2 = minecraftClient3.player.getEntityPos();
         this.int64 = 0;
         minecraftClient3.player.networkHandler.sendChatCommand("home " + var1);
         this.notify("Телепорт: " + var1);
         this.autoWardenVar143 = var2;
         this.zClass06714.reset();
      }
   }

   public void float349() {
      if (!this.boolean23) {
         if (minecraftClient3.world != this.clientWorld) {
            this.boolean23 = true;
            this.zClass06714.reset();
         } else if (this.zClass06714.EventModifyMouseRotationInput(12000L)) {
            if (++this.int65 > 3) {
               this.int65 = 0;
               this.notify("Анархия не сменилась — пробую /home на месте");
               this.string10 = this.string11;
               this.UiAnimation(this.string12, this.autoWardenVar1432);
               return;
            }

            this.notify("Повтор /an" + this.string11 + " (" + this.int65 + ")");
            minecraftClient3.player.networkHandler.sendChatCommand("an" + this.string11);
            this.zClass06714.reset();
         }
      } else {
         boolean flag = minecraftClient3.world
            .isChunkLoaded(minecraftClient3.player.getBlockPos().getX() >> 4, minecraftClient3.player.getBlockPos().getZ() >> 4);
         if (flag && this.zClass06714.EventModifyMouseRotationInput(600L) || this.zClass06714.EventModifyMouseRotationInput(3000L)) {
            String s = this.float350();
            if (s != null && !s.equals(this.string11)) {
               if (++this.int65 > 3) {
                  this.int65 = 0;
                  this.notify("Анархия не подтвердилась — пробую /home");
                  this.string10 = s;
                  this.UiAnimation(this.string12, this.autoWardenVar1432);
                  return;
               }

               this.notify("Скорборд показывает " + s + " — повтор /an" + this.string11);
               this.clientWorld = minecraftClient3.world;
               this.boolean23 = false;
               minecraftClient3.player.networkHandler.sendChatCommand("an" + this.string11);
               this.zClass06714.reset();
               return;
            }

            this.string10 = this.string11;
            this.clientWorld = null;
            this.notify("Анархия " + this.string11 + " — телепорт домой");
            this.UiAnimation(this.string12, this.autoWardenVar1432);
         }
      }
   }

   public void boolean189() {
      if (this.blockPos9 == null) {
         this.autoWardenVar143 = AutoWarden.State.val019;
      } else if (this.zClass06714.var11933() >= this.call160()) {
         this.call161();
      } else if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler) {
         boolean var4 = false;

         for (int j = 0; j < genericcontainerscreenhandler.getInventory().size(); j++) {
            if (!genericcontainerscreenhandler.getSlot(j).getStack().isEmpty()) {
               var4 = true;
               break;
            }
         }

         if (!var4) {
            this.call161();
         } else {
            for (int j = this.int55; j < genericcontainerscreenhandler.getInventory().size(); j++) {
               if (!genericcontainerscreenhandler.getSlot(j).getStack().isEmpty()) {
                  minecraftClient3.interactionManager.clickSlot(genericcontainerscreenhandler.syncId, j, 0, SlotActionType.QUICK_MOVE, minecraftClient3.player);
                  this.int55 = j + 1;
                  this.boolean21 = true;
                  break;
               }
            }
         }
      } else {
         this.autoWardenVar143 = AutoWarden.State.val019;
      }
   }

   public void call189() {
      if (this.blockPos9 == null) {
         this.autoWardenVar143 = AutoWarden.State.val084;
      } else if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler) {
         if (this.zClass06714.var11933() >= this.call160() * 2L) {
            this.call162();
         } else {
            int l = genericcontainerscreenhandler.getInventory().size();
            int i = 0;
            int j = -1;

            for (int k = l; k < l + 36; k++) {
               ItemStack itemstack = genericcontainerscreenhandler.getSlot(k).getStack();
               if (!itemstack.isEmpty()) {
                  i += itemstack.getCount();
                  if (j == -1) {
                     j = k;
                  }
               }
            }

            if (j == -1) {
               this.call090();
            } else {
               if (this.boolean22 && i == this.int62) {
                  if (++this.int63 >= 2) {
                     this.call162();
                     return;
                  }
               } else {
                  this.int63 = 0;
               }

               minecraftClient3.interactionManager.clickSlot(genericcontainerscreenhandler.syncId, j, 0, SlotActionType.QUICK_MOVE, minecraftClient3.player);
               this.int62 = i;
               this.boolean22 = true;
            }
         }
      } else {
         this.call090();
      }
   }

   public void call188() {
      if (this.blockPos9 == null) {
         this.autoWardenVar143 = AutoWarden.State.val083;
      } else if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler) {
         int var5 = genericcontainerscreenhandler.getInventory().size();
         if (this.int55 < var5 && (!this.boolean16 || !this.boolean17 || !this.boolean18) && this.zClass06714.var11933() < this.call160()) {
            for (int i = this.int55; i < var5; i++) {
               ItemStack itemstack = genericcontainerscreenhandler.getSlot(i).getStack();
               if (itemstack.isEmpty()) {
                  this.int55 = i + 1;
               } else {
                  if (!this.boolean18 && this.CloudPoller(itemstack)) {
                     this.on23(genericcontainerscreenhandler, i);
                     this.boolean18 = true;
                     this.int55 = i + 1;
                     break;
                  }

                  if (!this.boolean16 && this.MediaTrackInfo(itemstack)) {
                     this.on23(genericcontainerscreenhandler, i);
                     this.boolean16 = true;
                     this.int55 = i + 1;
                     break;
                  }

                  if (!this.boolean17 && itemstack.isOf(Items.GOLDEN_CARROT)) {
                     minecraftClient3.interactionManager
                        .clickSlot(genericcontainerscreenhandler.syncId, i, 0, SlotActionType.QUICK_MOVE, minecraftClient3.player);
                     this.boolean17 = true;
                     this.int55 = i + 1;
                     break;
                  }

                  this.int55 = i + 1;
               }
            }
         } else {
            this.notify(this.boolean16 ? "Взял зелье" : "Зелье в сундуке не найдено");
            minecraftClient3.player.closeHandledScreen();
            this.autoWardenVar143 = AutoWarden.State.val167;
            this.zClass06714.reset();
         }
      } else {
         this.autoWardenVar143 = AutoWarden.State.val167;
      }
   }

   public void Event08(boolean var1) {
      minecraftClient3.options.useKey.setPressed(false);
      long i = System.currentTimeMillis();
      if (var1 && this.long35 - i > 60000L) {
         this.notify("Инвиз ещё действует — не пью");
         this.int56 = 0;
         if (!this.boolean19) {
            this.boolean19 = true;
            this.Event08(false);
         } else {
            this.boolean19 = false;
            this.on23(this.u0414U043eU043c2.getValue(), AutoWarden.State.val085);
         }
      } else if (!var1 && this.long36 - i > 60000L) {
         this.notify("Скорость ещё действует — не пью");
         this.boolean19 = false;
         this.on23(this.u0414U043eU043c2.getValue(), AutoWarden.State.val085);
      } else {
         Slot slot = ScreenUtils.ColorAnimator(var2x -> var1 ? this.MediaTrackInfo(var2x.getStack()) : this.CloudPoller(var2x.getStack()));
         if (slot == null) {
            if (var1) {
               this.notify("Зелья невидимости нет — иду без него");
               this.boolean19 = true;
            }

            this.on23(this.u0414U043eU043c2.getValue(), AutoWarden.State.val085);
         } else {
            ScreenUtils.on23(slot, Hand.OFF_HAND, true);
            this.boolean20 = var1;
            this.notify(var1 ? "Пью зелье невидимости" : "Пью зелье скорости");
            this.autoWardenVar143 = AutoWarden.State.val231;
            this.zClass06714.reset();
         }
      }
   }

   public boolean NbtEditor(long var1) {
      if (this.call252() && minecraftClient3.player.currentScreenHandler == minecraftClient3.player.playerScreenHandler && minecraftClient3.interactionManager != null) {
         boolean flag = minecraftClient3.player.getHungerManager().getFoodLevel() <= 6;
         if (!this.boolean25 && !flag) {
            return false;
         }

         if (this.boolean25 && !flag && !minecraftClient3.player.isUsingItem()) {
            this.call026();
            this.notify("Поел — бегу дальше");
            return false;
         }

         int i = this.call253();
         if (i == -1) {
            this.call026();
            return false;
         }

         if (!this.boolean25) {
            this.boolean25 = true;
            this.notify("Голод — ем на месте");
         }

         this.float163();
         if (i > 8) {
            if (var1 - this.long33 >= 250L) {
               minecraftClient3.interactionManager
                  .clickSlot(minecraftClient3.player.playerScreenHandler.syncId, i, 8, SlotActionType.SWAP, minecraftClient3.player);
               this.long33 = var1;
            }

            return true;
         } else {
            this.EventMixin_modifySetScreenArg(i);
            minecraftClient3.options.sneakKey.setPressed(true);
            minecraftClient3.options.useKey.setPressed(true);
            if (!minecraftClient3.player.isUsingItem() || minecraftClient3.player.getActiveHand() != Hand.MAIN_HAND) {
               minecraftClient3.interactionManager.interactItem(minecraftClient3.player, Hand.MAIN_HAND);
            }

            return true;
         }
      } else {
         this.call026();
         return false;
      }
   }

   public void PotionItemBuilder(long var1) {
      if (!this.boolean25
         && minecraftClient3.interactionManager != null
         && minecraftClient3.player.currentScreenHandler == minecraftClient3.player.playerScreenHandler
         && this.autoWardenVar143 != AutoWarden.State.val167
         && this.autoWardenVar143 != AutoWarden.State.val231
         && !minecraftClient3.player.getInventory().getStack(minecraftClient3.player.getInventory().selectedSlot).isEmpty()) {
         for (int i = 0; i < 9; i++) {
            if (minecraftClient3.player.getInventory().getStack(i).isEmpty()) {
               minecraftClient3.player.getInventory().selectedSlot = i;
               minecraftClient3.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(i));
               return;
            }
         }

         if (var1 - this.long33 >= 250L) {
            boolean flag = false;

            for (int j = 9; j < 36; j++) {
               if (minecraftClient3.player.getInventory().getStack(j).isEmpty()) {
                  flag = true;
                  break;
               }
            }

            if (flag) {
               for (int k = 0; k < 9; k++) {
                  ItemStack itemstack = minecraftClient3.player.getInventory().getStack(k);
                  if (!this.MediaTrackInfo(itemstack) && !itemstack.isOf(Items.GOLDEN_CARROT)) {
                     minecraftClient3.interactionManager
                        .clickSlot(minecraftClient3.player.playerScreenHandler.syncId, 36 + k, 0, SlotActionType.QUICK_MOVE, minecraftClient3.player);
                     this.long33 = var1;
                     return;
                  }
               }
            }
         }
      }
   }

   public void float163() {
      BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().onLostControl();
   }

   public boolean float164() {
      return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
   }

   public void BotFeatureRegistry(BlockPos var1) {
      this.call035();
      BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(var1));
   }

   public void ServiceException(BlockPos var1) {
      this.call035();
      BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalNear(var1, 2));
   }

   public void float165() {
      this.on23(this.u0414U043eU043c1.getValue(), AutoWarden.State.val356);
   }

   public void boolean188() {
      if (this.ItemRegistry(this.u0414U043eU043c1.getValue(), true)) {
         this.notify("Хранилище — беру зелье");
         this.autoWardenVar143 = AutoWarden.State.val083;
         this.zClass06714.reset();
      }
   }

   public void on23(String var1, AutoWarden.State var2) {
      String s = this.float350();
      if (s != null) {
         this.string10 = s;
      }

      String s1 = this.ConfigLoader(var1);
      if (s1 == null || s1.equals(this.string10)) {
         this.UiAnimation(var1, var2);
      } else if (this.float348()) {
         this.float163();
         this.string12 = var1;
         this.autoWardenVar1432 = var2;
         this.string11 = s1;
         this.long34 = System.currentTimeMillis();
         this.notify("ПвП кулдаун — жду перед сменой анки");
         this.autoWardenVar143 = AutoWarden.State.val169;
         this.zClass06714.reset();
      } else {
         this.on23(var1, var2, s1);
      }
   }

   public void on23(String var1, AutoWarden.State var2, String var3) {
      this.float163();
      this.string12 = var1;
      this.autoWardenVar1432 = var2;
      this.string11 = var3;
      this.clientWorld = minecraftClient3.world;
      this.boolean23 = false;
      this.int65 = 0;
      minecraftClient3.player.networkHandler.sendChatCommand("an" + var3);
      this.notify("Переход на анархию /an" + var3);
      this.autoWardenVar143 = AutoWarden.State.val367;
      this.zClass06714.reset();
   }

   public void float347() {
      if (this.zClass06714.EventModifyMouseRotationInput(1000L)) {
         this.zClass06714.reset();
         if (this.float348()) {
            if (System.currentTimeMillis() - this.long34 < 180000L) {
               return;
            }

            this.notify("ПвП кулдаун висит слишком долго — перехожу так");
         } else {
            this.notify("ПвП кулдаун кончился — меняю анку");
         }

         this.on23(this.string12, this.autoWardenVar1432, this.string11);
      }
   }

   public boolean float348() {
      if (minecraftClient3.inGameHud == null) {
         return false;
      }

      for (ClientBossBar clientbossbar : minecraftClient3.inGameHud.getBossBarHud().bossBars.values()) {
         String s = clientbossbar.getName().getString().toLowerCase(Locale.ROOT);
         if (s.contains("пвп") || s.contains("pvp")) {
            for (int i = 0; i < s.length(); i++) {
               if (Character.isDigit(s.charAt(i))) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public String ConfigLoader(String var1) {
      String s = var1.equals(this.u0414U043eU043c1.getValue())
         ? this.u0410U043dU043aU0430U0414U043eU043cU0430.getValue()
         : this.u0410U043dU043aU0430U0412U0430U0440U0434U0435U043dU0430.getValue();
      s = s.trim();
      return s.isEmpty() ? null : s;
   }

   public String float350() {
      String s = this.u0410U043dU043aU0430U0414U043eU043cU0430.getValue().trim();
      String s1 = this.u0410U043dU043aU0430U0412U0430U0440U0434U0435U043dU0430.getValue().trim();
      if (s.isEmpty() && s1.isEmpty()) {
         return null;
      } else {
         String s2 = this.double139();
         if (s2 == null) {
            return null;
         } else {
            boolean flag = !s.isEmpty() && ModuleStateStore(s2, s);
            boolean flag1 = !s1.isEmpty() && ModuleStateStore(s2, s1);
            if (flag == flag1) {
               return null;
            } else {
               return flag ? s : s1;
            }
         }
      }
   }

   public String double139() {
      if (minecraftClient3.world == null) {
         return null;
      }

      Scoreboard scoreboard = minecraftClient3.world.getScoreboard();
      ScoreboardObjective scoreboardobjective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
      if (scoreboardobjective == null) {
         return null;
      }

      StringBuilder stringbuilder = new StringBuilder(scoreboardobjective.getDisplayName().getString());

      for (ScoreboardEntry scoreboardentry : scoreboard.getScoreboardEntries(scoreboardobjective)) {
         Team team = scoreboard.getScoreHolderTeam(scoreboardentry.owner());
         stringbuilder.append('\n').append(Team.decorateName(team, Text.literal(scoreboardentry.owner())).getString());
         if (scoreboardentry.display() != null) {
            stringbuilder.append(' ').append(scoreboardentry.display().getString());
         }
      }

      return stringbuilder.toString();
   }

   public static boolean ModuleStateStore(String var0, String var1) {
      int i = -1;

      while ((i = var0.indexOf(var1, i + 1)) != -1) {
         boolean flag = i == 0 || !Character.isDigit(var0.charAt(i - 1));
         int j = i + var1.length();
         boolean flag1 = j >= var0.length() || !Character.isDigit(var0.charAt(j));
         if (flag && flag1) {
            return true;
         }
      }

      return false;
   }

   public boolean ItemRegistry(String var1, boolean var2) {
      if (this.CancellableEvent(var2)) {
         this.int64 = 0;
         return true;
      } else if (!this.zClass06714.EventModifyMouseRotationInput(8000L)) {
         return false;
      } else if (++this.int64 > 5) {
         this.int64 = 0;
         this.notify("Телепорт не проходит — продолжаю на месте");
         return true;
      } else {
         this.notify("Телепорт не сработал — повторяю (" + this.int64 + ")");
         minecraftClient3.player.networkHandler.sendChatCommand("home " + var1);
         this.zClass06714.reset();
         return false;
      }
   }

   public boolean CancellableEvent(boolean var1) {
      boolean flag = this.double141();
      if (var1) {
         if (flag) {
            return true;
         }
      } else {
         if (flag) {
            return false;
         }

         if (this.double140()) {
            this.blockPos13 = minecraftClient3.player.getBlockPos();
            return true;
         }
      }

      boolean flag1 = this.vec3d2 != null && minecraftClient3.player.getEntityPos().squaredDistanceTo(this.vec3d2) > 64.0;
      if (flag1 && !var1) {
         this.blockPos13 = minecraftClient3.player.getBlockPos();
      }

      return flag1;
   }

   public boolean double140() {
      return this.blockPos13 != null && minecraftClient3.player.getBlockPos().getSquaredDistance(this.blockPos13) <= 1600.0;
   }

   public boolean double141() {
      BlockPos blockpos = minecraftClient3.player.getBlockPos();
      if (this.blockPos12 != null
         && blockpos.getSquaredDistance(this.blockPos12) <= 900.0
         && minecraftClient3.world.getBlockEntity(this.blockPos12) instanceof ChestBlockEntity) {
         return true;
      }

      for (BlockPos blockpos1 : this.list9) {
         if (blockpos.getSquaredDistance(blockpos1) <= 900.0 && minecraftClient3.world.getBlockEntity(blockpos1) instanceof ChestBlockEntity) {
            return true;
         }
      }

      return false;
   }

   public void double142() {
      this.zClass06714.reset();
      AutoWarden.Target lli11l1l11_ii1il11l111ii11iil = this.call254();
      if (lli11l1l11_ii1il11l111ii11iil != null) {
         this.blockPos9 = lli11l1l11_ii1il11l111ii11iil.val086;
         if (!lli11l1l11_ii1il11l111ii11iil.val086.equals(this.blockPos11)) {
            this.notify("Найден сундук " + lli11l1l11_ii1il11l111ii11iil.val368);
            this.blockPos11 = lli11l1l11_ii1il11l111ii11iil.val086;
         }

         this.on23(lli11l1l11_ii1il11l111ii11iil.val086, lli11l1l11_ii1il11l111ii11iil.val369);
         if (this.int58 > 0) {
            this.int58--;
            if (this.int58 == 0) {
               this.notify("Возврат к ближним сундукам");
            }
         }

         this.autoWardenVar143 = AutoWarden.State.val111;
      } else if (this.int58 > 0) {
         this.int58 = 0;
         this.notify("Цикл завершён, нет доступных сундуков");
      }
   }

   public void double143() {
      if (this.blockPos9 == null) {
         this.autoWardenVar143 = AutoWarden.State.val019;
      } else {
         double d0 = minecraftClient3.player.getBlockPos().getSquaredDistance(this.blockPos9);
         if (d0 <= 144.0) {
            int i = this.CloudRouter(this.blockPos9);
            if (i > 50) {
               this.hashSet.add(this.blockPos9);
            } else if (i > 0) {
               this.hashSet.add(this.blockPos9);
               this.notify("Таймер " + i + "с, жду у сундука...");
               this.autoWardenVar143 = AutoWarden.State.val168;
               this.float163();
               return;
            }
         }

         this.int57 = 0;
         if (d0 <= 2.0) {
            this.autoWardenVar143 = AutoWarden.State.val112;
            this.int57 = 0;
            this.int58 = 0;
            this.hashSet.clear();
            this.float163();
         } else if (!this.float164()) {
            this.BotFeatureRegistry(this.blockPos9);
         }
      }
   }

   public void double144() {
      if (this.blockPos9 == null) {
         this.autoWardenVar143 = AutoWarden.State.val019;
      } else {
         double d0 = minecraftClient3.player.getBlockPos().getSquaredDistance(this.blockPos9);
         if (d0 > 2.0) {
            this.autoWardenVar143 = AutoWarden.State.val111;
         } else {
            AimUtils.CloudResponse(new Vec3d(this.blockPos9.getX() + 0.5, this.blockPos9.getY() + 0.5, this.blockPos9.getZ() + 0.5));
            if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
               this.autoWardenVar143 = AutoWarden.State.val225;
               this.int55 = 0;
               this.boolean21 = false;
               this.zClass06714.reset();
            } else if (this.zClass06714.EventModifyMouseRotationInput(1000L) && this.ProtocolMessage(this.blockPos9)) {
               this.zClass06714.reset();
            }
         }
      }
   }

   public void float166() {
      if (this.zClass06714.var11933() >= 30000L) {
         this.notify("Переход на анархию склада");
         this.autoWardenVar143 = AutoWarden.State.val229;
         this.zClass06714.reset();
      }
   }

   public void float167() {
      this.on23(this.u0414U043eU043c1.getValue(), AutoWarden.State.val360);
   }

   public void float168() {
      if (this.ItemRegistry(this.u0414U043eU043c1.getValue(), true)) {
         this.notify("База, ищу сундук Storage...");
         this.int61 = 0;
         this.boolean22 = false;
         this.int63 = 0;
         this.autoWardenVar143 = AutoWarden.State.val084;
         this.zClass06714.reset();
      }
   }

   public void float169() {
      this.zClass06714.reset();
      BlockPos blockpos = null;

      while (this.int61 < this.list9.size()) {
         BlockPos blockpos1 = this.list9.get(this.int61);
         if (minecraftClient3.world.getBlockEntity(blockpos1) instanceof ChestBlockEntity) {
            blockpos = blockpos1;
            break;
         }

         this.int61++;
      }

      if (blockpos == null && this.list9.isEmpty()) {
         blockpos = this.NoiseGenerator("storage");
      }

      if (blockpos != null) {
         this.blockPos9 = blockpos;
         this.notify("Сундук найден, иду...");
         this.autoWardenVar143 = AutoWarden.State.val230;
      } else if (!this.list9.isEmpty()) {
         this.notify("Все привязанные склады недоступны");
         this.call090();
      }
   }

   public void float351() {
      if (this.blockPos9 == null) {
         this.autoWardenVar143 = AutoWarden.State.val084;
      } else {
         double d0 = minecraftClient3.player.getBlockPos().getSquaredDistance(this.blockPos9);
         if (d0 <= 2.0) {
            this.autoWardenVar143 = AutoWarden.State.val361;
            this.float163();
         } else if (!this.float164()) {
            this.BotFeatureRegistry(this.blockPos9);
         }
      }
   }

   public void float352() {
      if (this.blockPos9 == null) {
         this.autoWardenVar143 = AutoWarden.State.val084;
      } else {
         double d0 = minecraftClient3.player.getBlockPos().getSquaredDistance(this.blockPos9);
         if (d0 > 2.0) {
            this.autoWardenVar143 = AutoWarden.State.val230;
         } else {
            AimUtils.CloudResponse(new Vec3d(this.blockPos9.getX() + 0.5, this.blockPos9.getY() + 0.5, this.blockPos9.getZ() + 0.5));
            if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
               this.autoWardenVar143 = AutoWarden.State.val362;
               this.zClass06714.reset();
            } else if (this.zClass06714.EventModifyMouseRotationInput(1000L) && this.ProtocolMessage(this.blockPos9)) {
               this.zClass06714.reset();
            }
         }
      }
   }

   public void call162() {
      minecraftClient3.player.closeHandledScreen();
      this.blockPos9 = null;
      this.boolean22 = false;
      this.int63 = 0;
      this.int61++;
      if (!this.list9.isEmpty() && this.int61 < this.list9.size()) {
         this.notify("Сундук забит — иду к складу №" + (this.int61 + 1));
         this.autoWardenVar143 = AutoWarden.State.val084;
         this.zClass06714.reset();
      } else {
         this.notify("Склады заполнены — везу остаток");
         this.call090();
      }
   }

   public void call090() {
      minecraftClient3.player.closeHandledScreen();
      this.blockPos9 = null;
      this.int61 = 0;
      this.boolean22 = false;
      this.int63 = 0;
      this.notify("Выгружено");
      this.autoWardenVar143 = AutoWarden.State.val363;
      this.zClass06714.reset();
   }

   public void call190() {
      if (this.zClass06714.EventModifyMouseRotationInput(1000L)) {
         this.int59 = 0;
         this.boolean21 = false;
         if (this.long35 - System.currentTimeMillis() < 60000L) {
            this.notify("Инвиз кончается — беру зелье");
            this.autoWardenVar143 = AutoWarden.State.val083;
         } else {
            this.notify("Возврат на город");
            this.autoWardenVar143 = AutoWarden.State.val364;
         }

         this.zClass06714.reset();
      }
   }

   public void call161() {
      this.float163();
      minecraftClient3.player.closeHandledScreen();
      this.call089();
      if (this.blockPos9 != null) {
         this.hashMap.put(this.blockPos9, System.currentTimeMillis());
      }

      this.blockPos9 = null;
      if (this.boolean21) {
         this.int59++;
      }

      if (this.int59 >= (int)this.u0441U0443U043dU0434U0443U043aU043eU0432U0437U0430U0437U0430U0445U043eU0434.getCurrent()) {
         this.notify("Слутано " + this.int59 + " — убегаю");
         this.autoWardenVar143 = AutoWarden.State.val110;
         this.long31 = System.currentTimeMillis() + 30000L;
         this.blockPos10 = null;
         PlayerEntity playerentity = this.PotionItemBuilder(30.0);
         if (playerentity != null) {
            this.TextScanner(playerentity);
         } else {
            this.blockPos10 = this.NbtItemSpec(25, 8);
            if (this.blockPos10 != null) {
               this.ServiceException(this.blockPos10);
            }
         }
      } else {
         this.notify(
            this.boolean21
               ? "Сундук слутан ("
                  + this.int59
                  + "/"
                  + (int)this.u0441U0443U043dU0434U0443U043aU043eU0432U0437U0430U0437U0430U0445U043eU0434.getCurrent()
                  + ")"
               : "Сундук пуст, ищу другой..."
         );
         this.autoWardenVar143 = AutoWarden.State.val019;
      }

      this.zClass06714.reset();
   }

   public void call191() {
      this.on23(this.u0414U043eU043c2.getValue(), AutoWarden.State.val365);
   }

   public void call192() {
      if (this.ItemRegistry(this.u0414U043eU043c2.getValue(), false)) {
         this.autoWardenVar143 = AutoWarden.State.val019;
         this.zClass06714.reset();
      }
   }

   public void call193() {
      ItemStack itemstack = minecraftClient3.player.getOffHandStack();
      if (itemstack.getItem() == Items.GLASS_BOTTLE) {
         minecraftClient3.interactionManager.clickSlot(0, 45, 1, SlotActionType.THROW, minecraftClient3.player);
      }

      this.autoWardenVar143 = AutoWarden.State.val019;
      this.zClass06714.reset();
   }

   public void call245() {
      if (this.zClass06714.var11933() >= 30000L) {
         this.autoWardenVar143 = AutoWarden.State.val166;
      } else {
         this.call122();
      }
   }

   public void call246() {
      if (minecraftClient3.player.isDead() || minecraftClient3.player.getHealth() <= 0.0F) {
         minecraftClient3.player.requestRespawn();
         if (minecraftClient3.currentScreen instanceof DeathScreen) {
            minecraftClient3.setScreen(null);
         }

         this.zClass06714.reset();
      } else if (this.zClass06714.EventModifyMouseRotationInput(1000L)) {
         this.float163();
         this.notify("Смерть — на хранилище за зельем");
         this.autoWardenVar143 = AutoWarden.State.val166;
         this.zClass06714.reset();
      }
   }

   public void call247() {
      if (this.zClass06714.EventModifyMouseRotationInput(2000L)) {
         this.zClass06714.reset();
         BlockPos blockpos = this.call255();
         if (blockpos != null) {
            this.blockPos9 = blockpos;
            this.notify("Нашёл сундук Potions");
            this.autoWardenVar143 = AutoWarden.State.val228;
         }
      }
   }

   public void call187() {
      if (this.blockPos9 == null) {
         this.autoWardenVar143 = AutoWarden.State.val083;
      } else {
         double d0 = minecraftClient3.player.getBlockPos().getSquaredDistance(this.blockPos9);
         if (d0 <= 2.0) {
            this.autoWardenVar143 = AutoWarden.State.val358;
            this.float163();
         } else if (!this.float164()) {
            this.BotFeatureRegistry(this.blockPos9);
         }
      }
   }

   public void call121() {
      if (this.blockPos9 == null) {
         this.autoWardenVar143 = AutoWarden.State.val083;
      } else {
         double d0 = minecraftClient3.player.getBlockPos().getSquaredDistance(this.blockPos9);
         if (d0 > 2.0) {
            this.autoWardenVar143 = AutoWarden.State.val228;
         } else {
            AimUtils.CloudResponse(new Vec3d(this.blockPos9.getX() + 0.5, this.blockPos9.getY() + 0.5, this.blockPos9.getZ() + 0.5));
            if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler) {
               this.autoWardenVar143 = AutoWarden.State.val359;
               long i = System.currentTimeMillis();
               this.boolean16 = this.long35 - i > 60000L;
               this.boolean17 = false;
               this.boolean18 = this.long36 - i > 60000L;
               if (this.boolean16) {
                  this.notify("Инвиз ещё активен — зелье не беру");
               }

               if (this.boolean18) {
                  this.notify("Скорость ещё активна — зелье не беру");
               }

               this.int55 = 0;
               this.zClass06714.reset();
            } else if (this.zClass06714.EventModifyMouseRotationInput(1000L) && this.ProtocolMessage(this.blockPos9)) {
               this.zClass06714.reset();
            }
         }
      }
   }

   public void float385() {
      if (this.zClass06714.EventModifyMouseRotationInput(500L)) {
         this.boolean15 = false;
         this.float163();
         this.boolean24 = true;
         this.int56 = 0;
         this.boolean19 = false;
         this.on23(this.u0414U043eU043c2.getValue(), AutoWarden.State.val085);
      }
   }

   public void call248() {
      if (this.zClass06714.var11933() >= 250L) {
         minecraftClient3.options.sneakKey.setPressed(true);
         minecraftClient3.options.useKey.setPressed(true);
         if (this.zClass06714.EventModifyMouseRotationInput(3500L)) {
            minecraftClient3.options.useKey.setPressed(false);
            minecraftClient3.options.sneakKey.setPressed(false);
            if (this.boolean20) {
               if (!minecraftClient3.player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
                  if (++this.int56 <= 3) {
                     this.notify("Инвиз не выпился — повтор " + this.int56);
                     this.Event08(true);
                     return;
                  }

                  this.notify("Не смог выпить инвиз");
               }

               this.int56 = 0;
               if (!this.boolean19) {
                  this.boolean19 = true;
                  this.Event08(false);
                  return;
               }
            }

            this.boolean19 = false;
            this.on23(this.u0414U043eU043c2.getValue(), AutoWarden.State.val085);
         }
      }
   }

   public void call249() {
      if (this.ItemRegistry(this.u0414U043eU043c2.getValue(), false)) {
         this.autoWardenVar143 = AutoWarden.State.val366;
         this.zClass06714.reset();
      }
   }

   public void call250() {
      if (this.blockPos9 == null) {
         this.autoWardenVar143 = AutoWarden.State.val019;
      } else {
         double d0 = minecraftClient3.player.getBlockPos().getSquaredDistance(this.blockPos9);
         if (d0 > 9.0) {
            if (!this.float164()) {
               this.BotFeatureRegistry(this.blockPos9);
            }
         } else {
            this.float163();
            int i = this.CloudRouter(this.blockPos9);
            if (i > 50) {
               this.notify("Таймер " + i + "с — не жду, забираю что есть");
               this.autoWardenVar143 = AutoWarden.State.val112;
            } else if (i <= 0 || this.zClass06714.var11933() >= 60000L) {
               this.notify("Таймер истёк, открываю...");
               this.autoWardenVar143 = AutoWarden.State.val112;
            }
         }
      }
   }

   public void call251() {
      if (System.currentTimeMillis() >= this.long31) {
         this.float163();
         if (this.int59 > 0) {
            this.notify("Уклонение завершено, переход на склад");
            this.autoWardenVar143 = AutoWarden.State.val229;
         } else {
            this.notify("Уклонение завершено");
            this.autoWardenVar143 = AutoWarden.State.val019;
         }

         this.zClass06714.reset();
      } else {
         PlayerEntity playerentity = this.PotionItemBuilder(30.0);
         if (playerentity != null) {
            if (!this.float164()) {
               this.TextScanner(playerentity);
            }
         } else {
            if (this.blockPos10 == null || minecraftClient3.player.getBlockPos().getSquaredDistance(this.blockPos10) <= 9.0) {
               this.blockPos10 = this.NbtItemSpec(25, 8);
            }

            if (this.blockPos10 != null && !this.float164()) {
               this.float163();
               this.ServiceException(this.blockPos10);
            }
         }
      }
   }

   public void TextScanner(PlayerEntity var1) {
      this.call035();
      Vec3d vec3d = minecraftClient3.player.getEntityPos();
      Vec3d vec3d1 = new Vec3d(vec3d.x - var1.getX(), 0.0, vec3d.z - var1.getZ());
      if (vec3d1.lengthSquared() < 0.01) {
         vec3d1 = new Vec3d(1.0, 0.0, 0.0);
      }

      vec3d1 = vec3d1.normalize();
      BaritoneAPI.getProvider()
         .getPrimaryBaritone()
         .getCustomGoalProcess()
         .setGoalAndPath(
            new GoalXZ(
               MathHelper.floor(vec3d.x + vec3d1.x * 20.0), MathHelper.floor(vec3d.z + vec3d1.z * 20.0)
            )
         );
   }

   public BlockPos NbtItemSpec(int var1, int var2) {
      BlockPos blockpos = minecraftClient3.player.getBlockPos();

      for (int i = 0; i < var2; i++) {
         int j = blockpos.getX() + (int)(Math.random() * var1 * 2.0 - var1);
         int k = blockpos.getZ() + (int)(Math.random() * var1 * 2.0 - var1);

         for (int l = -3; l <= 3; l++) {
            BlockPos blockpos1 = new BlockPos(j, blockpos.getY() + l, k);
            if (minecraftClient3.world.isChunkLoaded(blockpos1.getX() >> 4, blockpos1.getZ() >> 4)
               && minecraftClient3.world.getBlockState(blockpos1).isAir()
               && minecraftClient3.world.getBlockState(blockpos1.down()).isSolid()
               && minecraftClient3.world.getBlockState(blockpos1.up()).isAir()) {
               return blockpos1;
            }
         }
      }

      return blockpos;
   }

   public void on23(GenericContainerScreenHandler var1, int var2) {
      ItemStack itemstack = var1.getSlot(var2).getStack();
      if (!itemstack.isEmpty()) {
         if (itemstack.getCount() <= 1) {
            minecraftClient3.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.QUICK_MOVE, minecraftClient3.player);
         } else {
            int i = var1.getInventory().size();
            minecraftClient3.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, minecraftClient3.player);
            int j = -1;

            for (int k = i; k < i + 36; k++) {
               if (var1.getSlot(k).getStack().isEmpty()) {
                  j = k;
                  break;
               }
            }

            if (j != -1) {
               minecraftClient3.interactionManager.clickSlot(var1.syncId, j, 1, SlotActionType.PICKUP, minecraftClient3.player);
            }

            minecraftClient3.interactionManager.clickSlot(var1.syncId, var2, 0, SlotActionType.PICKUP, minecraftClient3.player);
         }
      }
   }

   public void call026() {
      if (this.boolean25) {
         this.boolean25 = false;
         minecraftClient3.options.useKey.setPressed(false);
         minecraftClient3.options.sneakKey.setPressed(false);
      }
   }

   public boolean call252() {
      return switch (this.autoWardenVar143) {
         case val019, val111, val226, val227, val168, val169, val110 -> true;
         default -> false;
      };
   }

   public int call253() {
      for (int i = 0; i < 9; i++) {
         if (this.ModuleStateStore(minecraftClient3.player.getInventory().getStack(i))) {
            return i;
         }
      }

      for (int j = 9; j < 36; j++) {
         if (this.ModuleStateStore(minecraftClient3.player.getInventory().getStack(j))) {
            return j;
         }
      }

      return -1;
   }

   public boolean ModuleStateStore(ItemStack var1) {
      return var1 != null
         && !var1.isEmpty()
         && !var1.isOf(Items.CHORUS_FRUIT)
         && !var1.isOf(Items.SPIDER_EYE)
         && !var1.isOf(Items.POISONOUS_POTATO)
         && !var1.isOf(Items.PUFFERFISH)
         && !var1.isOf(Items.ROTTEN_FLESH)
         && var1.getUseAction() == UseAction.EAT;
   }

   public void EventMixin_modifySetScreenArg(int var1) {
      if (var1 >= 0 && var1 <= 8 && minecraftClient3.player.getInventory().selectedSlot != var1) {
         minecraftClient3.player.getInventory().selectedSlot = var1;
         minecraftClient3.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var1));
      }
   }

   public boolean call244() {
      return switch (this.autoWardenVar143) {
         case val019, val111, val112, val225, val226, val227, val168, val169 -> true;
         default -> false;
      };
   }

   public boolean MediaTrackInfo(ItemStack var1) {
      return this.on23(var1, StatusEffects.INVISIBILITY);
   }

   public boolean CloudPoller(ItemStack var1) {
      return this.on23(var1, StatusEffects.SPEED);
   }

   public boolean on23(ItemStack var1, RegistryEntry<StatusEffect> var2) {
      if (!var1.isOf(Items.POTION)) {
         return false;
      }

      PotionContentsComponent potioncontentscomponent = (PotionContentsComponent)var1.get(DataComponentTypes.POTION_CONTENTS);
      if (potioncontentscomponent == null) {
         return false;
      }

      for (StatusEffectInstance statuseffectinstance : potioncontentscomponent.getEffects()) {
         if (statuseffectinstance.getEffectType().equals(var2)) {
            return true;
         }
      }

      return false;
   }

   public BlockPos call255() {
      if (this.blockPos12 != null && minecraftClient3.world.getBlockEntity(this.blockPos12) instanceof ChestBlockEntity) {
         return this.blockPos12;
      }

      BlockPos blockpos = this.NoiseGenerator("potion");
      if (blockpos != null) {
         return blockpos;
      }

      for (BlockEntity blockentity : this.call163()) {
         if (blockentity instanceof ChestBlockEntity chestblockentity) {
            String s = chestblockentity.getName().getString().toLowerCase(Locale.ROOT);
            if (s.contains("potion")) {
               return chestblockentity.getPos();
            }
         }
      }

      return null;
   }

   public BlockPos NoiseGenerator(String var1) {
      String s = var1.toLowerCase(Locale.ROOT);

      for (BlockEntity blockentity : this.call163()) {
         if (blockentity instanceof SignBlockEntity signblockentity) {
            SignText signtext = signblockentity.getText(true);

            for (int i = 0; i < 4; i++) {
               String s1 = signtext.getMessage(i, false).getString();
               if (s1.toLowerCase(Locale.ROOT).contains(s)) {
                  BlockPos blockpos = signblockentity.getPos();

                  for (Direction direction : Direction.values()) {
                     BlockPos blockpos1 = blockpos.offset(direction);
                     BlockEntity blockentity1 = minecraftClient3.world.getBlockEntity(blockpos1);
                     if (blockentity1 instanceof ChestBlockEntity) {
                        return blockpos1;
                     }
                  }
               }
            }
         }
      }

      return null;
   }

   public void call122() {
      PlayerEntity playerentity = this.PotionItemBuilder(30.0);
      if (playerentity != null) {
         BlockPos blockpos = this.BlockInteractEvent(30);
         if (blockpos != null) {
            this.ServiceException(blockpos);
         }
      }
   }

   public void call120() {
      if (this.armorStandEntity != null && !this.armorStandEntity.isRemoved() && this.zClass06715.EventModifyMouseRotationInput(1000L)) {
         this.zClass06715.reset();
         long i = (this.long30 - System.currentTimeMillis()) / 1000L;
         if (i <= 0L) {
            this.call089();
         } else {
            this.armorStandEntity.setCustomName(Text.literal("Обновление через " + i + " сек"));
         }
      }
   }

   public AutoWarden.Target call254() {
      AutoWarden.Target lli11l1l11_ii1il11l111ii11iil = null;
      BlockPos blockpos = minecraftClient3.player.getBlockPos();
      long i = System.currentTimeMillis();
      boolean flag = this.int58 > 0;

      for (BlockEntity blockentity : this.call163()) {
         if (blockentity instanceof ChestBlockEntity chestblockentity) {
            BlockPos blockpos1 = chestblockentity.getPos();
            if ((flag || !(blockpos.getSquaredDistance(blockpos1) > 10000.0)) && (!flag || !this.hashSet.contains(blockpos1))) {
               Long olong = this.hashMap.get(blockpos1);
               if ((olong == null || i - olong >= 120000L)
                  && (lli11l1l11_ii1il11l111ii11iil == null || blockpos.getSquaredDistance(blockpos1) < blockpos.getSquaredDistance(lli11l1l11_ii1il11l111ii11iil.val086))) {
                  lli11l1l11_ii1il11l111ii11iil = new AutoWarden.Target(blockpos1, chestblockentity.getName().getString(), i);
               }
            }
         }
      }

      return lli11l1l11_ii1il11l111ii11iil;
   }

   public List<BlockEntity> call163() {
      List<BlockEntity> arraylist = new ArrayList<>();
      int i = minecraftClient3.player.getChunkPos().x;
      int j = minecraftClient3.player.getChunkPos().z;
      int k = (Integer)minecraftClient3.options.getViewDistance().getValue();

      for (int l = i - k; l <= i + k; l++) {
         for (int i1 = j - k; i1 <= j + k; i1++) {
            if (minecraftClient3.world.isChunkLoaded(l, i1)) {
               WorldChunk worldchunk = minecraftClient3.world.getChunk(l, i1);
               if (worldchunk != null) {
                  arraylist.addAll(worldchunk.getBlockEntities().values());
               }
            }
         }
      }

      return arraylist;
   }

   public String EnchantItemSpec(Entity var1) {
      if (var1 instanceof TextDisplayEntity textdisplayentity && textdisplayentity.getText() != null && !textdisplayentity.getText().getString().isEmpty()) {
         return textdisplayentity.getText().getString();
      } else {
         return var1.getCustomName() != null ? var1.getCustomName().getString() : null;
      }
   }

   public int CloudRouter(BlockPos var1) {
      Vec3d vec3d = Vec3d.ofCenter(var1);
      int i = -1;
      double d0 = Double.MAX_VALUE;

      for (Entity entity : minecraftClient3.world.getEntities()) {
         if (entity != this.armorStandEntity) {
            Vec3d vec3d1 = entity.getEntityPos();
            double d1 = vec3d1.x - vec3d.x;
            double d2 = vec3d1.z - vec3d.z;
            double d3 = d1 * d1 + d2 * d2;
            if (!(d3 > 0.64)) {
               double d4 = vec3d1.y - vec3d.y;
               if (!(d4 < -0.5) && !(d4 > 3.5)) {
                  String s = this.EnchantItemSpec(entity);
                  if (s != null && !s.contains("Обновление")) {
                     int j = this.PermissionListsStore(s);
                     if (j >= 0) {
                        double d5 = d3 + d4 * d4;
                        if (d5 < d0) {
                           d0 = d5;
                           i = j;
                        }
                     }
                  }
               }
            }
         }
      }

      return i;
   }

   public int PermissionListsStore(String var1) {
      Matcher matcher = pattern5.matcher(var1);
      if (!matcher.find()) {
         return -1;
      }

      try {
         return Integer.parseInt(matcher.group(1)) * 60 + Integer.parseInt(matcher.group(2));
      } catch (NumberFormatException numberformatexception) {
         return -1;
      }
   }

   public boolean ProtocolMessage(BlockPos var1) {
      Vec3d vec3d = new Vec3d(var1.getX() + 0.5, var1.getY() + 0.5, var1.getZ() + 0.5);
      if (!AimUtils.on23(vec3d, 12.0F)) {
         return false;
      }

      boolean flag = minecraftClient3.options.sneakKey.isPressed();
      minecraftClient3.options.sneakKey.setPressed(true);
      BlockHitResult blockhitresult = new BlockHitResult(vec3d, Direction.UP, var1, false);
      minecraftClient3.interactionManager.interactBlock(minecraftClient3.player, Hand.MAIN_HAND, blockhitresult);
      minecraftClient3.player.swingHand(Hand.MAIN_HAND);
      minecraftClient3.options.sneakKey.setPressed(flag);
      return true;
   }

   public PlayerEntity PotionItemBuilder(double var1) {
      return minecraftClient3.world
         .getPlayers()
         .stream()
         .filter(var2 -> var2 != minecraftClient3.player && var2.distanceTo(minecraftClient3.player) <= var1)
         .min(Comparator.comparingDouble(var0 -> var0.distanceTo(minecraftClient3.player)))
         .orElse(null);
   }

   public BlockPos BlockInteractEvent(int var1) {
      List<AbstractClientPlayerEntity> list = minecraftClient3.world
         .getPlayers()
         .stream()
         .filter(var0 -> var0 != minecraftClient3.player)
         .collect(Collectors.toList());
      if (list.isEmpty()) {
         return minecraftClient3.player.getBlockPos().add(10, 0, 10);
      }

      BlockPos blockpos = null;
      double d0 = 0.0;

      for (int i = -var1; i <= var1; i += 5) {
         for (int j = -var1; j <= var1; j += 5) {
            BlockPos blockpos1 = minecraftClient3.player.getBlockPos().add(i, 0, j);
            double d1 = list.stream().mapToDouble(var1x -> var1x.getBlockPos().getSquaredDistance(blockpos1)).min().orElse(0.0);
            if (d1 > d0) {
               d0 = d1;
               blockpos = blockpos1;
            }
         }
      }

      return blockpos;
   }

   public void on23(BlockPos var1, long var2) {
      this.call089();
      ArmorStandEntity armorstandentity = new ArmorStandEntity(minecraftClient3.world, var1.getX() + 0.5, var1.getY() + 1.2, var1.getZ() + 0.5);
      armorstandentity.setInvisible(true);
      armorstandentity.setCustomNameVisible(true);
      armorstandentity.setNoGravity(true);
      minecraftClient3.world.addEntity(armorstandentity);
      this.armorStandEntity = armorstandentity;
      this.long30 = var2;
      this.zClass06715.reset();
   }

   public void call089() {
      if (this.armorStandEntity != null) {
         this.armorStandEntity.remove(RemovalReason.DISCARDED);
         this.armorStandEntity = null;
      }
   }

   public long call160() {
      return 30000L;
   }

   public void notify(String var1) {
      StyledTextBuilder.RefreshCacheEvent("Auto Warden: " + var1);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (minecraftClient3.player == null) {
         this.setToggled(false);
      } else if (!this.u0414U043eU043c1.getValue().isEmpty() && !this.u0414U043eU043c2.getValue().isEmpty()) {
         this.boolean15 = false;
         this.float163();
         this.blockPos9 = null;
         this.hashMap.clear();
         this.hashSet.clear();
         this.int57 = 0;
         this.int58 = 0;
         this.blockPos11 = null;
         this.float7 = 0.0F;
         this.string9 = null;
         this.int59 = 0;
         this.boolean21 = false;
         this.boolean25 = false;
         this.long35 = 0L;
         this.long36 = 0L;
         this.string10 = this.float350();
         if (this.string10 != null) {
            this.notify("Скорборд: анархия " + this.string10);
         }

         this.int65 = 0;
         this.boolean23 = false;
         this.boolean24 = false;
         this.call035();
         AimUtils.BotRespawnEvent(true);
         this.autoWardenVar143 = AutoWarden.State.val166;
         this.zClass06714.reset();
      } else {
         this.notify("Ошибка: не заданы имена домов!");
         this.setToggled(false);
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.autoWardenVar143 = AutoWarden.State.val165;
      this.boolean15 = false;
      this.float163();
      this.call164();
      AimUtils.BotRespawnEvent(false);
      minecraftClient3.options.useKey.setPressed(false);
      this.call089();
      this.call256();
   }

   public void call243() {
      if (minecraftClient3.player != null) {
         if (this.int60 == 0) {
            EventManager.register(this.object2);
         }

         this.int60 = 1;
         minecraftClient3.setScreen(null);
         this.call071();
         this.GmmModel("ПКМ по сундуку с зельями");
      }
   }

   public void on23(ChatMessageEvent var1) {
      if (this.int60 != 0 && var1.OpenWals() && !var1.NoInteract()) {
         String s = var1.getMessage().trim();
         if (s.equals("1")) {
            var1.cancel();
            if (this.blockPos12 == null || this.list9.isEmpty()) {
               this.GmmModel("Нужен сундук зелий и хотя бы один склад");
               return;
            }

            this.call257();
         }
      }
   }

   public void on23(DataChangedEvent var1) {
      if (this.int60 != 0) {
         BlockPos blockpos = this.call258();
         if (blockpos != null) {
            var1.cancel();
            if (this.int60 == 1) {
               this.blockPos12 = blockpos;
               this.list9.clear();
               this.int60 = 2;
               this.GmmModel("Сундук зелий выбран — теперь ПКМ по сундукам склада");
               this.call071();
            } else if (blockpos.equals(this.blockPos12)) {
               this.GmmModel("Это сундук зелий");
            } else if (this.list9.remove(blockpos)) {
               this.GmmModel("Склад снят, осталось " + this.list9.size());
               this.call071();
            } else {
               this.list9.add(blockpos);
               this.GmmModel("Склад №" + this.list9.size() + " выбран — ещё ПКМ или 1 для завершения");
               this.call071();
            }
         }
      }
   }

   public void call257() {
      this.int60 = 0;
      EventManager.unregister(this.object2);
      this.GmmModel("Настройка завершена: складов " + this.list9.size());
      this.call071();
   }

   public void call071() {
   }

   public void call256() {
   }

   public void ItemRegistry(HudRenderEvent var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         this.float8 = this.float8 + var1.getTickDelta() * 0.06F;
         if (this.blockPos12 != null) {
            this.on23(var1.Bot(), this.blockPos12, itemStack4, 0.0F);
         }

         for (int i = 0; i < this.list9.size(); i++) {
            this.on23(var1.Bot(), this.list9.get(i), itemStack3, (i + 1) * 0.7F);
         }
      }
   }

   public void on23(CustomDrawContext var1, BlockPos var2, ItemStack var3, float var4) {
      if (var1 != null) {
         double d0 = MathHelper.sin(this.float8 + var4) * 0.1;
         Vec3d vec3d = new Vec3d(var2.getX() + 0.5, var2.getY() + 1.0 + d0, var2.getZ() + 0.5);
         if (!(minecraftClient3.player.getEyePos().squaredDistanceTo(vec3d) > 4096.0)) {
            Vec3d vec3d1 = ScreenProjection.BotDisconnectEvent(vec3d);
            if (vec3d1 != null && !(vec3d1.z <= 0.0) && !(vec3d1.z >= 1.0)) {
               float f = 1.2F;
               var1.pushMatrix();
               var1.getMatrices().translate((float)vec3d1.x - 8.0F * f, (float)vec3d1.y - 8.0F * f);
               var1.getMatrices().scale(f, f);
               var1.drawItem(var3, 0, 0);
               var1.popMatrix();
            }
         }
      }
   }

   public void GmmModel(String var1) {
      val003.ConfigJsonUtil().on23(this.getCategory().getIcon(), Text.literal(var1), 7000L);
   }

   public BlockPos call258() {
      return minecraftClient3.crosshairTarget instanceof BlockHitResult blockhitresult
            && blockhitresult.getType() == Type.BLOCK
            && minecraftClient3.world.getBlockEntity(blockhitresult.getBlockPos()) instanceof ChestBlockEntity
         ? blockhitresult.getBlockPos().toImmutable()
         : null;
   }

   @Override
   public JsonObject save() {
      JsonObject jsonobject = super.save();
      if (this.blockPos12 != null) {
         jsonobject.addProperty("potionChest", this.blockPos12.getX() + " " + this.blockPos12.getY() + " " + this.blockPos12.getZ());
      }

      if (this.blockPos13 != null) {
         jsonobject.addProperty("cityPos", this.blockPos13.getX() + " " + this.blockPos13.getY() + " " + this.blockPos13.getZ());
      }

      if (!this.list9.isEmpty()) {
         StringBuilder stringbuilder = new StringBuilder();

         for (BlockPos blockpos : this.list9) {
            if (stringbuilder.length() > 0) {
               stringbuilder.append(';');
            }

            stringbuilder.append(blockpos.getX()).append(' ').append(blockpos.getY()).append(' ').append(blockpos.getZ());
         }

         jsonobject.addProperty("storageChests", stringbuilder.toString());
      }

      return jsonobject;
   }

   @Override
   public void load(JsonObject var1) {
      super.load(var1);
      this.blockPos12 = ModuleSnapshotDto(var1, "potionChest");
      this.blockPos13 = ModuleSnapshotDto(var1, "cityPos");
      this.list9.clear();
      if (var1 != null && var1.has("storageChests")) {
         for (String s : var1.get("storageChests").getAsString().split(";")) {
            BlockPos blockpos = MotorIntentModel(s);
            if (blockpos != null) {
               this.list9.add(blockpos);
            }
         }
      }
   }

   public void call403() {
      this.call071();
   }

   public static BlockPos ModuleSnapshotDto(JsonObject var0, String var1) {
      return var0 != null && var0.has(var1) ? MotorIntentModel(var0.get(var1).getAsString()) : null;
   }

   public static BlockPos MotorIntentModel(String var0) {
      String[] astring = var0.trim().split(" ");
      if (astring.length != 3) {
         return null;
      }

      try {
         return new BlockPos(Integer.parseInt(astring[0]), Integer.parseInt(astring[1]), Integer.parseInt(astring[2]));
      } catch (NumberFormatException numberformatexception) {
         return null;
      }
   }

   public void call035() {
      BaritoneBridge.float137();
   }

   public void call164() {
      BaritoneBridge.vec3d16();
   }


   public static class NetworkListener {
      public final AutoWarden val437;

      NetworkListener(AutoWarden var1) {
         this.val437 = var1;
      }

      @EventTarget
      public void UiAnimation(ChatMessageEvent var1) {
         this.val437.on23(var1);
      }

      @EventTarget
      public void UiAnimation(DataChangedEvent var1) {
         this.val437.on23(var1);
      }
   }

   public static class RenderListener {
      public final AutoWarden val511;

      RenderListener(AutoWarden var1) {
         this.val511 = var1;
      }

      @EventTarget
      public void ColorAnimator(HudRenderEvent var1) {
         this.val511.ItemRegistry(var1);
      }
   }

   public enum State {
      val165,
      val166,
      val356,
      val019,
      val111,
      val112,
      val225,
      val226,
      val227,
      val357,
      val083,
      val228,
      val358,
      val359,
      val167,
      val229,
      val360,
      val084,
      val230,
      val361,
      val362,
      val363,
      val364,
      val365,
      val366,
      val231,
      val085,
      val168,
      val367,
      val169,
      val110;
   }

   public static class Target {
      final BlockPos val086;
      final String val368;
      final long val369;

      Target(BlockPos var1, String var2, long var3) {
         this.val086 = var1;
         this.val368 = var2;
         this.val369 = var3;
      }
   }
}
