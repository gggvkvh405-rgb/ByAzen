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
import com.darkmagician6.eventapi.EventTarget;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.zenith.core.BaritoneBridge;
import org.zenith.core.StyledTextBuilder;
import org.zenith.event.EventTick;
import org.zenith.event.GameMessageEvent;
import org.zenith.event.PacketEvent;
import org.zenith.setting.NumberSetting;
import org.zenith.util.AimUtils;

@ModuleInfo(name = "AutoZamok", category = Category.PLAYER, description = "Автофарм шалкербоксов на замке через Baritone")
public class AutoZamok extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoZamok autoZamok = new AutoZamok();
   public static final double[][] val038 = new double[0][];
   public static final BlockPos blockPos14 = BlockPos.ORIGIN;
   public static final String[] val497 = new String[0];
   public static final String[] val498 = new String[0];
   public static final String[] val499 = new String[0];
   public static final long long37 = 70L;
   public static final long long38 = 4000L;
   public static final long long39 = 8000L;
   public static final long long40 = 6000L;
   public static final long long41 = 100L;
   public static final long long42 = 200L;
   public static final long long43 = 500L;
   public static final long long44 = 12000L;
   public static final long long45 = 15000L;
   public static final long long46 = 60000L;
   public static final long long47 = 15000L;
   public static final double double2 = 144.0;
   public static final long long48 = 300000L;
   public static final long long49 = 20000L;
   public static final int int66 = 3;
   public static final double double3 = 2.1;
   public static final long long50 = 20000L;
   public static final int int67 = 10;
   public static final int int68 = 2;
   public static final int int69 = 200;
   public static final int int70 = 17;
   public static final long long51 = 20000L;
   public static final double double4 = 30.0;
   public static final long long52 = 50000L;
   public static final long long53 = 12000L;
   public static final long long54 = 120L;
   public static final long long55 = 180L;
   public static final float float9 = 1.35F;
   public static final double double5 = 35.0;
   public static final long long56 = 3500L;
   public static final long long57 = 20000L;
   public final NumberSetting u0411U043eU043aU0441U043eU0432U0437U0430U0432U044bU043bU0430U0437U043aU0443 = new NumberSetting(
      "Боксов за вылазку", 1.0F, 1.0F, 9.0F, 1.0F, "Сдавать после стольких выбитых боксов", "шт"
   );
   public AutoZamok.State autoZamokVar159 = AutoZamok.State.val039;
   public long long58;
   public long long59;
   public BlockPos blockPos15;
   public BlockPos blockPos16;
   public Vec3d vec3d3;
   public int int71;
   public long long60;
   public long long61;
   public int int72;
   public int int73;
   public int int74;
   public int int75;
   public int int76;
   public int int77;
   public boolean boolean26;
   public boolean boolean27;
   public boolean boolean28;
   public Vec3d vec3d4;
   public long long62;
   public long long63;
   public float float7;
   public boolean boolean29;
   public double double6;
   public double double7;
   public double double8;
   public BlockPos blockPos17;
   public long long64;
   public boolean boolean30;
   public float float10;
   public long long65;
   public BlockPos blockPos18;
   public Vec3d vec3d5;
   public long long66;
   public long long67;
   public final Map<BlockPos, Long> map13 = new HashMap<>();
   public final Map<BlockPos, Long> map14 = new HashMap<>();
   public int int63;
   public int int62;
   public boolean boolean22;
   public volatile BlockPos val087;
   public volatile long val370;
   public BlockPos blockPos19;
   public int int78;
   public int int79;
   public int int80;
   public BlockPos blockPos20;
   public long long68;
   public long long69;
   public BlockPos blockPos21;
   public long long70;
   public long long71;
   public long long72;
   public volatile long val170;
   public volatile int val113;
   public volatile long val114;
   public volatile boolean val232;
   public boolean boolean31;
   public boolean boolean32;
   public long long73;

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null
         && minecraftClient3.world != null
         && minecraftClient3.getNetworkHandler() != null
         && minecraftClient3.interactionManager != null) {
         long i = System.currentTimeMillis();
         if (this.autoZamokVar159 != AutoZamok.State.val233 && !this.boolean31 && !this.boolean32) {
            this.int452();
         }

         if (this.boolean30 && this.autoZamokVar159 != AutoZamok.State.val115) {
            this.call072();
         }

         if (this.boolean28 && this.autoZamokVar159 != AutoZamok.State.val040 && this.autoZamokVar159 != AutoZamok.State.val025) {
            this.call027();
         }

         if (this.float304()) {
            this.call026();
            this.call073();
            if (i - this.long67 >= 1000L) {
               this.long67 = i;
               minecraftClient3.player.requestRespawn();
               if (minecraftClient3.currentScreen instanceof DeathScreen) {
                  minecraftClient3.setScreen(null);
               }
            }

            if (this.autoZamokVar159 != AutoZamok.State.val039 && this.autoZamokVar159 != AutoZamok.State.val088) {
               this.log("Смерть — возвращаемся за снаряжением");
               this.float303();
               this.on23(AutoZamok.State.val039);
            }
         } else {
            float f = minecraftClient3.player.getHealth();
            if (this.float7 > 0.0F && f < this.float7 - 0.01F && this.call194()) {
               PlayerEntity playerentity = this.ProfileItemBuilder(12.0);
               if (playerentity != null) {
                  this.float7 = f;
                  this.on23(i, playerentity);
                  return;
               }
            }

            this.float7 = f;
            if (!this.StringCodec(i) && !this.NbtEditor(i)) {
               this.call165();
               if (this.CloudUserProfile(i)) {
                  this.long66 = i;
               } else {
                  this.ModuleSnapshotDto(i);
                  switch (this.autoZamokVar159) {
                     case val039:
                        if (this.CloudPoller(i) && this.on23(i, "home sklad")) {
                           this.log("/home sklad");
                           this.on23(AutoZamok.State.val088);
                        }
                        break;
                     case val088:
                        this.on23(i, AutoZamok.State.val234, AutoZamok.State.val039, true);
                        break;
                     case val234:
                        this.on23(i, val497, AutoZamok.State.val371, true);
                        break;
                     case val371:
                        this.on23(i, val498, AutoZamok.State.val233, false);
                        break;
                     case val233:
                        this.ProfileItemBuilder(i);
                        break;
                     case val041:
                        this.closeScreen();
                        if (this.CloudPoller(i) && this.on23(i, "home zamok")) {
                           this.log("/home zamok");
                           this.on23(AutoZamok.State.val116);
                        }
                        break;
                     case val116:
                        this.on23(i, AutoZamok.State.val057, AutoZamok.State.val041, false);
                        break;
                     case val057:
                        this.FileLogger(i);
                        break;
                     case val021:
                        this.MediaTrackInfo(i);
                        break;
                     case val115:
                        this.AnalyticsTracker(i);
                        break;
                     case val040:
                        this.CloudResponse(i);
                        break;
                     case val025:
                        this.ProtocolMessage(i);
                        break;
                     case val058:
                        this.ServiceException(i);
                        break;
                     case val042:
                        minecraftClient3.interactionManager.cancelBlockBreaking();
                        if (this.CloudPoller(i) && this.on23(i, "home sklad")) {
                           this.log("/home sklad (сдача)");
                           this.on23(AutoZamok.State.val117);
                        }
                        break;
                     case val117:
                        this.on23(i, AutoZamok.State.val235, AutoZamok.State.val042, true);
                        break;
                     case val235:
                        this.TradeGuardService(i);
                  }

                  if (this.call259()) {
                     this.ModuleStateStore(i);
                  }
               }
            } else {
               this.long66 = i;
            }
         }
      }
   }

   public void on23(long var1, String[] var3, AutoZamok.State var4, boolean var5) {
      if (var5 ? !this.call195() : !this.call091()) {
         if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler) {
            if (var1 - this.long59 >= 70L) {
               Slot slot = this.on23(genericcontainerscreenhandler, var5 ? this::CosmeticManager : this::EmoteManager);
               if (slot != null) {
                  minecraftClient3.interactionManager
                     .clickSlot(genericcontainerscreenhandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, minecraftClient3.player);
                  this.long59 = var1;
                  this.log("Взял предмет из сундука " + var3[0]);
                  if (var5 && !this.call195() && !this.call166()) {
                     return;
                  }
               } else if (!var5 || this.UiAnimation(this::CosmeticManager) == 0) {
                  slot = this.on23(genericcontainerscreenhandler, var0 -> true);
                  if (slot != null) {
                     minecraftClient3.interactionManager
                        .clickSlot(genericcontainerscreenhandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, minecraftClient3.player);
                     this.long59 = var1;
                     this.log("Взял предмет из сундука " + var3[0]);
                  }
               }

               this.closeScreen();
               this.on23(var4);
            }
         } else {
            if (this.blockPos15 == null) {
               this.blockPos15 = this.on23(var3, var1, false);
               if (this.blockPos15 == null) {
                  if (var1 - this.long58 > 6000L) {
                     this.log("Не найден сундук с табличкой " + var3[0]);
                     this.on23(var4);
                  }

                  return;
               }
            }

            if (var1 - this.long59 >= 70L) {
               this.CloudPoller(this.blockPos15);
               this.long59 = var1;
            }
         }
      } else {
         this.closeScreen();
         this.on23(var4);
      }
   }

   public void MediaTrackInfo(long var1) {
      BlockPos blockpos = this.BotFeaturesDto(var1);
      if (blockpos != null) {
         this.boolean26 = false;
         if (!blockpos.equals(this.blockPos17)) {
            this.blockPos17 = blockpos;
            this.long64 = var1;
         }

         if (this.CommandManager(blockpos) <= 4.41) {
            this.float303();
            this.blockPos16 = blockpos;
            this.long69 = 0L;
            this.boolean30 = false;
            this.float10 = 0.0F;
            this.long65 = 0L;
            this.call196();
            this.int79 = this.UiAnimation(null);
            this.int80 = this.UiAnimation(this::EmoteMetadata);
            this.autoZamokVar159 = AutoZamok.State.val115;
            this.long58 = var1;
         } else if (var1 - this.long64 > 15000L) {
            this.log("Бокс недоступен, пропускаю");
            this.map13.put(blockpos, var1);
            this.blockPos17 = null;
            this.long70 = 0L;
            this.float303();
         } else {
            this.ColorAnimator(blockpos, var1);
         }
      } else {
         this.blockPos17 = null;
         BlockPos blockpos1 = this.val087;
         if (blockpos1 != null) {
            if (var1 - this.val370 <= 15000L && !(minecraftClient3.player.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(blockpos1)) <= 9.0)) {
               if (!blockpos1.equals(this.blockPos19)) {
                  this.blockPos19 = blockpos1;
                  this.log("Взрыв у точки спавна — иду туда");
               }

               this.long60 = var1;
               this.ColorAnimator(blockpos1, var1);
               return;
            }

            this.val087 = null;
         }

         if (this.boolean27) {
            int i = this.call197();
            if (i == -1) {
               this.long66 = var1;
               if (var1 - this.long60 > 50000L) {
                  this.long60 = var1;
                  this.boolean26 = false;
                  this.BotFeatureRegistry(var1);
                  if (this.autoZamokVar159 == AutoZamok.State.val021) {
                     this.int71 = 0;
                     this.boolean27 = false;
                     this.log("Иду на новый обход");
                  }

                  return;
               }

               if (!this.boolean26) {
                  this.call260();
                  this.log("Боксов нет — жду спавн в укрытии");
               }

               double d1 = minecraftClient3.player.getX() - (this.int76 + 0.5);
               double d0 = minecraftClient3.player.getZ() - (this.int77 + 0.5);
               if (d1 * d1 + d0 * d0 <= 9.0) {
                  this.float303();
               } else {
                  this.on23(this.int76, this.int77, var1);
               }

               return;
            }

            if (i != this.int71) {
               this.int71 = i;
               this.long60 = var1;
            }
         }

         this.boolean26 = false;
         double[] adouble = val038[this.int71];
         BlockPos blockpos2 = BlockPos.ofFloored(adouble[0], adouble[1], adouble[2]);
         if (!(minecraftClient3.player.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(blockpos2)) <= 9.0) && var1 - this.long60 <= 12000L) {
            this.Easing(blockpos2, var1);
         } else {
            this.InventoryUtils(var1);
         }
      }
   }

   public boolean CloudUserProfile(long var1) {
      boolean flag = this.autoZamokVar159 == AutoZamok.State.val057
         || this.autoZamokVar159 == AutoZamok.State.val021
         || this.autoZamokVar159 == AutoZamok.State.val025
         || this.autoZamokVar159 == AutoZamok.State.val058;
      if (!flag) {
         this.call123();
         return false;
      }

      boolean flag1 = this.call261();
      if (this.vec3d4 == null) {
         if (!flag1 || this.blockPos20 == null) {
            return false;
         }

         this.vec3d4 = new Vec3d(this.blockPos20.getX() + 0.5, minecraftClient3.player.getY(), this.blockPos20.getZ() + 0.5);
         this.long62 = var1;
         this.float303();
      } else if (!flag1 && var1 - this.long63 > 700L) {
         this.call123();
         return false;
      }

      if (flag1) {
         this.long63 = var1;
      }

      if (var1 - this.long62 <= 20000L) {
         AimUtils.TradeGuardService(this.vec3d4);
         minecraftClient3.options.jumpKey.setPressed(true);
         return true;
      } else {
         this.log("Застрял в паутине — повторяю заход");
         this.call123();
         this.float303();
         this.on23(
            this.autoZamokVar159 != AutoZamok.State.val025 && this.autoZamokVar159 != AutoZamok.State.val058
               ? AutoZamok.State.val041
               : AutoZamok.State.val042
         );
         return false;
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.on23(AutoZamok.State.val039);
      this.long61 = 0L;
      this.val170 = 0L;
      this.float7 = 0.0F;
      this.boolean29 = false;
      this.boolean31 = false;
      this.boolean32 = false;
      this.int78 = 0;
      this.vec3d5 = null;
      this.val087 = null;
      this.blockPos19 = null;
      this.map14.clear();
      this.call035();
      AimUtils.BotRespawnEvent(true);
   }

   @Override
   public void onDisable() {
      super.onDisable();
      AimUtils.BotRespawnEvent(false);
      this.float303();
      this.call164();
      this.call026();
      this.call073();
      this.int452();
      if (minecraftClient3.options != null) {
         minecraftClient3.options.jumpKey.setPressed(false);
         if (this.boolean29) {
            minecraftClient3.options.sprintKey.setPressed(false);
            this.boolean29 = false;
         }
      }

      if (minecraftClient3.getNetworkHandler() != null) {
         this.call072();
      }

      this.boolean30 = false;
      this.call027();
      this.call123();
      this.blockPos15 = null;
      this.blockPos16 = null;
      this.vec3d3 = null;
      this.blockPos17 = null;
      this.map13.clear();
      this.map14.clear();
      this.val087 = null;
      this.blockPos19 = null;
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (var1.Arrows() && var1.ItemScroller() instanceof ExplosionS2CPacket explosions2cpacket) {
         BlockPos blockpos = BlockPos.ofFloored(explosions2cpacket.center());
         if (AnalyticsTracker(blockpos)) {
            this.val087 = blockpos;
            this.val370 = System.currentTimeMillis();
            this.long70 = 0L;
         }
      }
   }

   public static boolean AnalyticsTracker(BlockPos var0) {
      for (double[] adouble : val038) {
         if (var0.getSquaredDistance(adouble[0], adouble[1], adouble[2]) <= 144.0) {
            return true;
         }
      }

      return false;
   }

   @EventTarget
   public void onChatReceive(GameMessageEvent var1) {
      if (var1.InventorySetting() != null && this.float302()) {
         String s = var1.InventorySetting().getString().toLowerCase(Locale.ROOT);
         long i = System.currentTimeMillis();
         if (!s.contains("времени до следующей телепортаци") && (!s.contains("ошибк") || !s.contains("телепортаци"))) {
            if (s.contains("не двигайтесь") || s.contains("начнется через") || s.contains("начнётся через")) {
               this.val232 = true;
            } else if (s.contains("телепортирование") || s.contains("вы телепортированы")) {
               this.val113 = 1;
               this.val114 = i;
            }
         } else {
            long j = PermissionListCodec(s);
            this.val170 = i + (j > 0L ? j * 1000L : 1500L) + 700L;
            this.val113 = -1;
            this.val114 = i;
         }
      }
   }

   public void ProfileItemBuilder(long var1) {
      if (minecraftClient3.player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
         this.int452();
         this.on23(AutoZamok.State.val041);
      } else {
         int i = this.Easing(this::CosmeticManager);
         if (i == -1 || var1 - this.long58 > 5000L) {
            this.int452();
            this.on23(AutoZamok.State.val041);
         } else if (i > 8) {
            this.on23(i, var1);
         } else {
            this.CloseScreenEvent(i);
            this.float305();
         }
      }
   }

   public boolean StringCodec(long var1) {
      if (!this.call198() || this.boolean31) {
         this.call073();
         return false;
      }

      if (!this.boolean32 && !this.call167()) {
         return false;
      }

      if (!this.boolean32 || (this.call167() || minecraftClient3.player.isUsingItem()) && var1 - this.long73 <= 8000L) {
         int i = this.Easing(this::CosmeticManager);
         if (i == -1) {
            this.call073();
            return false;
         }

         if (!this.boolean32) {
            this.boolean32 = true;
            this.long73 = var1;
            this.log("Обновляю невидимость");
         }

         this.float303();
         if (i > 8) {
            this.on23(i, var1);
            return true;
         } else {
            this.CloseScreenEvent(i);
            this.float305();
            return true;
         }
      } else {
         this.call073();
         return false;
      }
   }

   public void call073() {
      if (this.boolean32) {
         this.int452();
         this.boolean32 = false;
      }
   }

   public boolean call167() {
      StatusEffectInstance statuseffectinstance = minecraftClient3.player.getStatusEffect(StatusEffects.INVISIBILITY);
      return statuseffectinstance == null ? true : !statuseffectinstance.isInfinite() && statuseffectinstance.getDuration() <= 200;
   }

   public boolean call198() {
      return this.autoZamokVar159 == AutoZamok.State.val057
         || this.autoZamokVar159 == AutoZamok.State.val021
         || this.autoZamokVar159 == AutoZamok.State.val115
         || this.autoZamokVar159 == AutoZamok.State.val040
         || this.autoZamokVar159 == AutoZamok.State.val025;
   }

   public boolean NbtEditor(long var1) {
      if (!this.call198()) {
         this.call026();
         return false;
      } else {
         boolean flag = minecraftClient3.player.getHungerManager().getFoodLevel() <= 17;
         if (!this.boolean31 && !flag) {
            return false;
         } else if (this.boolean31 && !flag && !minecraftClient3.player.isUsingItem()) {
            this.call026();
            return false;
         } else {
            int i = this.Easing(this::EmotePlayback);
            if (i == -1) {
               this.call026();
               return false;
            } else {
               this.boolean31 = true;
               this.float303();
               if (i > 8) {
                  this.on23(i, var1);
                  return true;
               } else {
                  this.CloseScreenEvent(i);
                  this.float305();
                  return true;
               }
            }
         }
      }
   }

   public void FileLogger(long var1) {
      if (minecraftClient3.player.getEntityPos().squaredDistanceTo(Vec3d.ofCenter(blockPos14)) <= 9.0
         || var1 - this.long58 > 2000L && this.BotFeaturesDto(var1) != null
         || var1 - this.long58 > 20000L) {
         this.CloudApiClient(var1);
      } else if (var1 - this.long58 > 1000L) {
         this.Easing(blockPos14, var1);
      }
   }

   public void CloudApiClient(long var1) {
      this.int71 = 0;
      this.long60 = var1;
      this.blockPos17 = null;
      this.boolean26 = false;
      this.boolean27 = false;
      this.on23(AutoZamok.State.val021);
   }

   public void call123() {
      if (this.vec3d4 != null) {
         this.vec3d4 = null;
         AimUtils.call007();
         minecraftClient3.options.jumpKey.setPressed(false);
      }
   }

   public boolean call261() {
      Box box = minecraftClient3.player.getBoundingBox().expand(0.05);

      for (BlockPos blockpos : BlockPos.iterate(
         MathHelper.floor(box.minX),
         MathHelper.floor(box.minY),
         MathHelper.floor(box.minZ),
         MathHelper.floor(box.maxX),
         MathHelper.floor(box.maxY),
         MathHelper.floor(box.maxZ)
      )) {
         if (minecraftClient3.world.getBlockState(blockpos).getBlock() == Blocks.COBWEB) {
            return true;
         }
      }

      return false;
   }

   public void call165() {
      boolean flag = minecraftClient3.player.isTouchingWater() && this.float164();
      if (flag) {
         minecraftClient3.options.sprintKey.setPressed(true);
         this.boolean29 = true;
      } else if (this.boolean29) {
         minecraftClient3.options.sprintKey.setPressed(false);
         this.boolean29 = false;
      }
   }

   public void ModuleSnapshotDto(long var1) {
      boolean flag = this.autoZamokVar159 == AutoZamok.State.val057
         || this.autoZamokVar159 == AutoZamok.State.val021
         || this.autoZamokVar159 == AutoZamok.State.val025;
      Vec3d vec3d = minecraftClient3.player.getEntityPos();
      if (!flag || this.vec3d5 == null || vec3d.squaredDistanceTo(this.vec3d5) > 0.25) {
         this.vec3d5 = vec3d;
         this.long66 = var1;
      } else if (var1 - this.long66 > 20000L) {
         this.log("Застрял — повторяю заход на замок");
         if (this.blockPos17 != null) {
            this.map13.put(this.blockPos17, var1);
            this.blockPos17 = null;
         }

         this.vec3d5 = null;
         this.long66 = var1;
         this.float303();
         this.on23(this.autoZamokVar159 == AutoZamok.State.val025 ? AutoZamok.State.val042 : AutoZamok.State.val041);
      }
   }

   public void InventoryUtils(long var1) {
      this.float303();
      this.long60 = var1;
      if (++this.int71 >= val038.length) {
         this.int71 = 0;
         this.boolean27 = true;
         this.BotFeatureRegistry(var1);
      }
   }

   public void BotFeatureRegistry(long var1) {
      if (this.int78 <= 0 && !this.call074()) {
         this.map13.clear();
      } else {
         this.log("Обход завершён, боксов выбито: " + this.int78);
         this.CloudRouter(var1);
      }
   }

   public void call260() {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      double d0 = -1.0;

      for (int i = 0; i < 8; i++) {
         double d1 = threadlocalrandom.nextDouble(Math.PI * 2);
         double d2 = blockPos14.getX() + 0.5 + Math.cos(d1) * 35.0;
         double d3 = blockPos14.getZ() + 0.5 + Math.sin(d1) * 35.0;
         double d4 = Double.MAX_VALUE;

         for (double[] adouble : val038) {
            double d5 = d2 - adouble[0];
            double d6 = d3 - adouble[2];
            d4 = Math.min(d4, d5 * d5 + d6 * d6);
         }

         if (d4 > d0) {
            d0 = d4;
            this.int76 = MathHelper.floor(d2);
            this.int77 = MathHelper.floor(d3);
         }
      }

      this.boolean26 = true;
   }

   public boolean EventClickSlotHook(int var1) {
      double[] adouble = val038[var1];
      return minecraftClient3.world.isChunkLoaded(MathHelper.floor(adouble[0]) >> 4, MathHelper.floor(adouble[2]) >> 4);
   }

   public int call197() {
      for (int i = 0; i < val038.length; i++) {
         int j = (this.int71 + i) % val038.length;
         if (!this.EventClickSlotHook(j)) {
            return j;
         }
      }

      return -1;
   }

   public boolean call194() {
      return switch (this.autoZamokVar159) {
         case val039, val088, val041, val116, val057, val021, val115, val040, val025, val058, val042, val117 -> true;
         default -> false;
      };
   }

   public void on23(long var1, PlayerEntity var3) {
      this.call026();
      this.call073();
      this.call072();
      if (minecraftClient3.options != null) {
         minecraftClient3.options.jumpKey.setPressed(false);
      }

      this.val087 = null;
      this.NbtItemSpec(var3);
      if (this.autoZamokVar159 != AutoZamok.State.val058) {
         this.log("Получил урон от игрока — убегаю");
         this.on23(AutoZamok.State.val058);
      } else {
         this.long58 = var1;
      }
   }

   public void NbtItemSpec(PlayerEntity var1) {
      Vec3d vec3d = minecraftClient3.player.getEntityPos();
      Vec3d vec3d1 = new Vec3d(vec3d.x - var1.getX(), 0.0, vec3d.z - var1.getZ());
      if (vec3d1.lengthSquared() < 0.01) {
         vec3d1 = new Vec3d(1.0, 0.0, 0.0);
      }

      vec3d1 = vec3d1.normalize();
      this.int74 = MathHelper.floor(vec3d.x + vec3d1.x * 25.0);
      this.int75 = MathHelper.floor(vec3d.z + vec3d1.z * 25.0);
   }

   public void ServiceException(long var1) {
      PlayerEntity playerentity = this.ProfileItemBuilder(20.0);
      if (playerentity != null && var1 - this.long58 <= 12000L) {
         double d0 = minecraftClient3.player.getX() - this.int74;
         double d1 = minecraftClient3.player.getZ() - this.int75;
         if (d0 * d0 + d1 * d1 <= 9.0) {
            this.NbtItemSpec(playerentity);
         }

         this.on23(this.int74, this.int75, var1);
      } else {
         this.float303();
         this.on23(AutoZamok.State.val042);
      }
   }

   public PlayerEntity ProfileItemBuilder(double var1) {
      PlayerEntity playerentity = null;
      double d0 = var1 * var1;

      for (PlayerEntity playerentity1 : minecraftClient3.world.getPlayers()) {
         if (playerentity1 != minecraftClient3.player && !playerentity1.isSpectator()) {
            double d1 = playerentity1.squaredDistanceTo(minecraftClient3.player);
            if (d1 < d0) {
               d0 = d1;
               playerentity = playerentity1;
            }
         }
      }

      return playerentity;
   }

   public void CloudRouter(long var1) {
      Vec3d vec3d = minecraftClient3.player.getEntityPos();
      Vec3d vec3d1 = Vec3d.ofCenter(blockPos14);
      Vec3d vec3d2 = new Vec3d(vec3d.x - vec3d1.x, 0.0, vec3d.z - vec3d1.z);
      if (vec3d2.lengthSquared() < 1.0) {
         vec3d2 = new Vec3d(1.0, 0.0, 0.0);
      }

      vec3d2 = vec3d2.normalize();
      this.int72 = MathHelper.floor(vec3d1.x + vec3d2.x * 30.0);
      this.int73 = MathHelper.floor(vec3d1.z + vec3d2.z * 30.0);
      this.log("Ухожу из замка перед телепортом");
      this.on23(AutoZamok.State.val025);
   }

   public void ProtocolMessage(long var1) {
      double d0 = minecraftClient3.player.getX() - this.int72;
      double d1 = minecraftClient3.player.getZ() - this.int73;
      if (!(d0 * d0 + d1 * d1 <= 16.0) && var1 - this.long58 <= 20000L) {
         this.on23(this.int72, this.int73, var1);
         if (!this.float164()) {
            this.boolean28 = true;
            AimUtils.TradeGuardService(new Vec3d(this.int72 + 0.5, minecraftClient3.player.getY(), this.int73 + 0.5));
         } else if (this.boolean28) {
            this.call027();
         }
      } else {
         this.float303();
         this.call027();
         this.on23(AutoZamok.State.val042);
      }
   }

   public void AnalyticsTracker(long var1) {
      BlockPos blockpos = this.blockPos16;
      if (blockpos == null) {
         this.call072();
         this.on23(AutoZamok.State.val040);
      } else {
         boolean flag = var1 - this.long58 >= 20000L;
         if (this.BotFeaturesDto(blockpos)) {
            this.long69 = 0L;
            double d0 = 2.6;
            if (this.CommandManager(blockpos) > d0 * d0) {
               this.call072();
               this.on23(AutoZamok.State.val021);
               return;
            }

            if (!this.CommandManager(var1)) {
               return;
            }

            this.PotionItemBuilder(this.CloudResponse(blockpos));
            if (!this.boolean30) {
               if (var1 >= this.long65) {
                  this.ConfigJsonUtil(blockpos);
               }
            } else {
               this.float10 = this.float10
                  + minecraftClient3.world.getBlockState(blockpos).calcBlockBreakingDelta(minecraftClient3.player, minecraftClient3.world, blockpos);
               minecraftClient3.player.swingHand(Hand.MAIN_HAND);
               minecraftClient3.world
                  .setBlockBreakingInfo(minecraftClient3.player.getId(), blockpos, MathHelper.clamp((int)(this.float10 * 10.0F), 0, 9));
               if (this.float10 >= 1.35F) {
                  this.ConfigJsonUtil(var1);
               }
            }
         } else {
            this.boolean30 = false;
            if (this.long69 == 0L) {
               this.long69 = var1;
            }

            if (var1 - this.long69 >= 100L) {
               this.vec3d3 = Vec3d.ofCenter(blockpos);
               this.on23(AutoZamok.State.val040);
               return;
            }
         }

         if (flag) {
            this.call072();
            if (this.BotFeaturesDto(blockpos)) {
               this.log("Бокс не поддаётся — пропускаю");
               this.map13.put(blockpos, var1);
               this.blockPos17 = null;
               this.long70 = 0L;
               this.on23(AutoZamok.State.val021);
            } else {
               this.vec3d3 = Vec3d.ofCenter(blockpos);
               this.on23(AutoZamok.State.val040);
            }
         }
      }
   }

   public void ConfigJsonUtil(BlockPos var1) {
      Direction direction = this.TradeGuardService(var1);
      minecraftClient3.interactionManager.sendSequencedPacket(minecraftClient3.world, var2x -> new PlayerActionC2SPacket(Action.START_DESTROY_BLOCK, var1, direction, var2x));
      this.boolean30 = true;
      this.blockPos18 = var1;
      this.float10 = 0.0F;
      minecraftClient3.player.swingHand(Hand.MAIN_HAND);
   }

   public void ConfigJsonUtil(long var1) {
      BlockPos blockpos = this.blockPos18;
      Direction direction = this.TradeGuardService(blockpos);
      minecraftClient3.interactionManager.sendSequencedPacket(minecraftClient3.world, var2 -> new PlayerActionC2SPacket(Action.STOP_DESTROY_BLOCK, blockpos, direction, var2));
      minecraftClient3.world.setBlockBreakingInfo(minecraftClient3.player.getId(), blockpos, -1);
      this.boolean30 = false;
      this.long65 = var1 + 120L + ThreadLocalRandom.current().nextLong(180L);
      this.call196();
   }

   public void call072() {
      if (this.boolean30) {
         minecraftClient3.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, this.blockPos18, Direction.DOWN));
         minecraftClient3.world.setBlockBreakingInfo(minecraftClient3.player.getId(), this.blockPos18, -1);
         this.boolean30 = false;
      }
   }

   public void call196() {
      ThreadLocalRandom threadlocalrandom = ThreadLocalRandom.current();
      this.double6 = threadlocalrandom.nextDouble(-0.18, 0.18);
      this.double7 = threadlocalrandom.nextDouble(-0.18, 0.18);
      this.double8 = threadlocalrandom.nextDouble(-0.18, 0.18);
   }

   public Vec3d CloudResponse(BlockPos var1) {
      return new Vec3d(var1.getX() + 0.5 + this.double6, var1.getY() + 0.5 + this.double7, var1.getZ() + 0.5 + this.double8);
   }

   public void PotionItemBuilder(Vec3d var1) {
      AimUtils.AnalyticsTracker(var1);
   }

   public Direction TradeGuardService(BlockPos var1) {
      Vec3d vec3d = minecraftClient3.player.getEyePos().subtract(Vec3d.ofCenter(var1));
      double d0 = Math.abs(vec3d.x);
      double d1 = Math.abs(vec3d.y);
      double d2 = Math.abs(vec3d.z);
      if (d0 >= d1 && d0 >= d2) {
         return vec3d.x >= 0.0 ? Direction.EAST : Direction.WEST;
      } else if (d2 >= d0 && d2 >= d1) {
         return vec3d.z >= 0.0 ? Direction.SOUTH : Direction.NORTH;
      } else {
         return vec3d.y >= 0.0 ? Direction.UP : Direction.DOWN;
      }
   }

   public void CloudResponse(long var1) {
      this.float303();
      boolean flag = this.UiAnimation(this::EmoteMetadata) > this.int80 || this.UiAnimation(null) > this.int79;
      if (flag) {
         this.int78++;
         this.log("Бокс выбит (" + this.int78 + "/" + (int)this.u0411U043eU043aU0441U043eU0432U0437U0430U0432U044bU043bU0430U0437U043aU0443.getCurrent() + ")");
         this.on23(var1, true);
      } else if (var1 - this.long58 >= 3500L) {
         this.log("Бокс достался не нам, продолжаю обход");
         this.on23(var1, false);
      } else if (this.vec3d3 != null) {
         double d0 = this.vec3d3.x - minecraftClient3.player.getX();
         double d1 = this.vec3d3.z - minecraftClient3.player.getZ();
         if (d0 * d0 + d1 * d1 > 0.64) {
            this.boolean28 = true;
            AimUtils.TradeGuardService(this.vec3d3);
         } else {
            this.call027();
         }
      }
   }

   public void call027() {
      if (this.boolean28) {
         this.boolean28 = false;
         AimUtils.call007();
      }
   }

   public void on23(long var1, boolean var3) {
      this.call027();
      if ((!var3 || this.int78 < (int)this.u0411U043eU043aU0441U043eU0432U0437U0430U0432U044bU043bU0430U0437U043aU0443.getCurrent())
         && !this.call166()
         && !this.call074()) {
         this.long60 = var1;
         this.blockPos17 = null;
         this.on23(AutoZamok.State.val021);
      } else {
         this.CloudRouter(var1);
      }
   }

   public boolean call074() {
      return this.Easing(this::EmoteManager) == -1 ? true : this.call167() && this.Easing(this::CosmeticManager) == -1;
   }

   public boolean call166() {
      for (int i = 0; i < 36; i++) {
         if (minecraftClient3.player.getInventory().getStack(i).isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public int UiAnimation(Predicate<ItemStack> var1) {
      int i = 0;
      int j = minecraftClient3.player.getInventory().size();

      for (int k = 0; k < j; k++) {
         ItemStack itemstack = minecraftClient3.player.getInventory().getStack(k);
         if (!itemstack.isEmpty() && (var1 == null || var1.test(itemstack))) {
            i += itemstack.getCount();
         }
      }

      return i;
   }

   public boolean EmoteMetadata(ItemStack var1) {
      return var1 != null && !var1.isEmpty() && Registries.ITEM.getId(var1.getItem()).getPath().endsWith("shulker_box");
   }

   public void TradeGuardService(long var1) {
      if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler) {
         if (var1 - this.long59 >= 70L) {
            int i = this.UiAnimation(null);
            if (this.boolean22) {
               this.boolean22 = false;
               if (i == this.int62) {
                  if (++this.int63 >= 2) {
                     this.log("Сундук забит — ищу другой");
                     this.UiAnimation(this.blockPos15, var1);
                     this.closeScreen();
                     this.on23(AutoZamok.State.val235);
                     return;
                  }
               } else {
                  this.int63 = 0;
               }
            }

            int j = genericcontainerscreenhandler.getInventory().size();

            for (Slot slot : genericcontainerscreenhandler.slots) {
               if (slot.id >= j) {
                  ItemStack itemstack = slot.getStack();
                  if (!itemstack.isEmpty() && !this.EmoteManager(itemstack) && !this.CosmeticManager(itemstack) && !this.EmotePlayback(itemstack)) {
                     minecraftClient3.interactionManager
                        .clickSlot(genericcontainerscreenhandler.syncId, slot.id, 0, SlotActionType.QUICK_MOVE, minecraftClient3.player);
                     this.long59 = var1;
                     this.int62 = i;
                     this.boolean22 = true;
                     return;
                  }
               }
            }

            this.log("Ресурсы сложены");
            this.closeScreen();
            this.call168();
         }
      } else {
         if (this.blockPos15 == null) {
            this.blockPos15 = this.on23(val499, var1, true);
            if (this.blockPos15 == null) {
               if (var1 - this.long58 > 6000L) {
                  this.log(this.map14.isEmpty() ? "Не найден сундук 'склад'" : "Все сундуки 'склад' забиты");
                  this.call168();
               }

               return;
            }
         }

         if (var1 - this.long59 >= 70L) {
            this.CloudPoller(this.blockPos15);
            this.long59 = var1;
         }
      }
   }

   public void call168() {
      this.closeScreen();
      this.int78 = 0;
      this.map13.clear();
      this.on23(AutoZamok.State.val234);
   }

   public BlockPos BotFeaturesDto(long var1) {
      if (var1 - this.long70 >= 200L) {
         this.long70 = var1;
         this.map13.values().removeIf(var2 -> var1 - var2 > 60000L);
         this.blockPos21 = this.call092();
      }

      return this.blockPos21;
   }

   public BlockPos call092() {
      byte b0 = 3;
      double d0 = b0 * 1.7321 + 1.0;
      Vec3d vec3d = minecraftClient3.player.getEyePos();
      Mutable mutable = new Mutable();
      BlockPos blockpos = null;
      double d1 = Double.MAX_VALUE;

      for (double[] adouble : val038) {
         double d2 = Math.sqrt(vec3d.squaredDistanceTo(adouble[0], adouble[1], adouble[2])) - d0;
         if (!(d2 > 0.0) || !(d2 * d2 >= d1)) {
            int i = MathHelper.floor(adouble[0]);
            int j = MathHelper.floor(adouble[1]);
            int k = MathHelper.floor(adouble[2]);

            for (int l = -b0; l <= b0; l++) {
               for (int i1 = -b0; i1 <= b0; i1++) {
                  for (int j1 = -b0; j1 <= b0; j1++) {
                     mutable.set(i + l, j + i1, k + j1);
                     if (minecraftClient3.world.getBlockState(mutable).getBlock() instanceof ShulkerBoxBlock && !this.map13.containsKey(mutable)) {
                        double d3 = vec3d.squaredDistanceTo(mutable.getX() + 0.5, mutable.getY() + 0.5, mutable.getZ() + 0.5);
                        if (d3 < d1) {
                           d1 = d3;
                           blockpos = mutable.toImmutable();
                        }
                     }
                  }
               }
            }
         }
      }

      return blockpos;
   }

   public boolean BotFeaturesDto(BlockPos var1) {
      return minecraftClient3.world.getBlockState(var1).getBlock() instanceof ShulkerBoxBlock;
   }

   public double CommandManager(BlockPos var1) {
      Vec3d vec3d = minecraftClient3.player.getEyePos();
      double d0 = Math.max(Math.max(var1.getX() - vec3d.x, 0.0), vec3d.x - (var1.getX() + 1));
      double d1 = Math.max(Math.max(var1.getY() - vec3d.y, 0.0), vec3d.y - (var1.getY() + 1));
      double d2 = Math.max(Math.max(var1.getZ() - vec3d.z, 0.0), vec3d.z - (var1.getZ() + 1));
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public BlockPos on23(String[] var1, long var2, boolean var4) {
      if (var2 - this.long71 < 500L) {
         return null;
      }

      this.long71 = var2;
      this.map14.values().removeIf(var2xx -> var2 - var2xx > 300000L);
      byte b0 = 10;
      double d0 = b0 * b0;
      BlockPos blockpos = minecraftClient3.player.getBlockPos();
      BlockPos blockpos1 = null;
      double d1 = Double.MAX_VALUE;
      int i = Integer.MAX_VALUE;
      int j = (b0 >> 4) + 1;
      int k = minecraftClient3.player.getChunkPos().x;
      int l = minecraftClient3.player.getChunkPos().z;

      for (int i1 = k - j; i1 <= k + j; i1++) {
         for (int j1 = l - j; j1 <= l + j; j1++) {
            if (minecraftClient3.world.isChunkLoaded(i1, j1)) {
               WorldChunk worldchunk = minecraftClient3.world.getChunk(i1, j1);
               if (worldchunk != null) {
                  for (BlockEntity blockentity : worldchunk.getBlockEntities().values()) {
                     if (blockentity instanceof SignBlockEntity signblockentity && !(blockpos.getSquaredDistance(signblockentity.getPos()) > d0)) {
                        int k1 = this.on23(signblockentity, var1);
                        if (k1 >= 0) {
                           if (!var4) {
                              k1 = 0;
                           }

                           BlockPos blockpos2 = this.ModuleStateStore(signblockentity.getPos());
                           if (blockpos2 != null && !this.map14.containsKey(blockpos2)) {
                              double d2 = blockpos.getSquaredDistance(blockpos2);
                              if (k1 < i || k1 == i && d2 < d1) {
                                 i = k1;
                                 d1 = d2;
                                 blockpos1 = blockpos2;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return blockpos1;
   }

   public int on23(SignBlockEntity var1, String[] var2) {
      StringBuilder stringbuilder = new StringBuilder();

      for (int i = 0; i < 4; i++) {
         stringbuilder.append(var1.getFrontText().getMessage(i, false).getString()).append(' ');
         stringbuilder.append(var1.getBackText().getMessage(i, false).getString()).append(' ');
      }

      String s1 = stringbuilder.toString().toLowerCase(Locale.ROOT);

      for (String s : var2) {
         int j = s1.indexOf(s);
         if (j != -1) {
            int k = j + s.length();

            while (k < s1.length() && !Character.isDigit(s1.charAt(k)) && !Character.isLetter(s1.charAt(k))) {
               k++;
            }

            int l = 0;
            boolean flag = false;

            while (k < s1.length() && Character.isDigit(s1.charAt(k))) {
               l = l * 10 + (s1.charAt(k) - '0');
               flag = true;
               k++;
            }

            return flag ? l : 1;
         }
      }

      return -1;
   }

   public BlockPos ModuleStateStore(BlockPos var1) {
      BlockState blockstate = minecraftClient3.world.getBlockState(var1);
      if (blockstate.getBlock() instanceof WallSignBlock) {
         BlockPos blockpos = var1.offset(((Direction)blockstate.get(WallSignBlock.FACING)).getOpposite());
         if (minecraftClient3.world.getBlockState(blockpos).getBlock() instanceof ChestBlock) {
            return blockpos;
         }
      } else if (blockstate.getBlock() instanceof SignBlock) {
         BlockPos blockpos3 = var1.down();
         if (minecraftClient3.world.getBlockState(blockpos3).getBlock() instanceof ChestBlock) {
            return blockpos3;
         }
      }

      if (blockstate.getBlock() instanceof ChestBlock) {
         return var1.toImmutable();
      }

      for (Direction direction : Direction.values()) {
         BlockPos blockpos1 = var1.offset(direction);
         if (minecraftClient3.world.getBlockState(blockpos1).getBlock() instanceof ChestBlock) {
            return blockpos1;
         }

         BlockPos blockpos2 = blockpos1.down();
         if (minecraftClient3.world.getBlockState(blockpos2).getBlock() instanceof ChestBlock) {
            return blockpos2;
         }
      }

      return null;
   }

   public void UiAnimation(BlockPos var1, long var2) {
      this.map14.put(var1, var2);
      BlockState blockstate = minecraftClient3.world.getBlockState(var1);
      if (blockstate.getBlock() instanceof ChestBlock && blockstate.get(ChestBlock.CHEST_TYPE) != ChestType.SINGLE) {
         this.map14.put(var1.offset(ChestBlock.getFacing(blockstate)), var2);
      }
   }

   public void CloudPoller(BlockPos var1) {
      Vec3d vec3d = Vec3d.ofCenter(var1);
      this.ProfileItemBuilder(vec3d);
      minecraftClient3.interactionManager.interactBlock(minecraftClient3.player, Hand.MAIN_HAND, new BlockHitResult(vec3d, Direction.UP, var1, false));
      minecraftClient3.player.swingHand(Hand.MAIN_HAND);
   }

   public Slot on23(GenericContainerScreenHandler var1, Predicate<ItemStack> var2) {
      int i = var1.getInventory().size();

      for (Slot slot : var1.slots) {
         if (slot.id >= i) {
            break;
         }

         if (slot.hasStack() && var2.test(slot.getStack())) {
            return slot;
         }
      }

      return null;
   }

   public boolean call091() {
      return this.Easing(this::EmoteManager) != -1;
   }

   public boolean EmoteManager(ItemStack var1) {
      return var1 != null && !var1.isEmpty() && Registries.ITEM.getId(var1.getItem()).getPath().contains("pickaxe");
   }

   public boolean CosmeticManager(ItemStack var1) {
      if (var1 != null && !var1.isEmpty() && var1.getItem() instanceof PotionItem) {
         PotionContentsComponent potioncontentscomponent = (PotionContentsComponent)var1.get(DataComponentTypes.POTION_CONTENTS);
         if (potioncontentscomponent == null) {
            return false;
         }

         for (StatusEffectInstance statuseffectinstance : potioncontentscomponent.getEffects()) {
            if (statuseffectinstance.getEffectType().equals(StatusEffects.INVISIBILITY)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public boolean EmotePlayback(ItemStack var1) {
      return var1 != null
         && !var1.isEmpty()
         && !var1.isOf(Items.CHORUS_FRUIT)
         && !var1.isOf(Items.SPIDER_EYE)
         && !var1.isOf(Items.POISONOUS_POTATO)
         && !var1.isOf(Items.PUFFERFISH)
         && !var1.isOf(Items.ROTTEN_FLESH)
         && var1.getUseAction() == UseAction.EAT;
   }

   public boolean call195() {
      int i = 2;
      if (!minecraftClient3.player.hasStatusEffect(StatusEffects.INVISIBILITY)) {
         i++;
      }

      return this.UiAnimation(this::CosmeticManager) >= i;
   }

   public int Easing(Predicate<ItemStack> var1) {
      int i = minecraftClient3.player.getInventory().size();

      for (int j = 0; j < 9 && j < i; j++) {
         if (var1.test(minecraftClient3.player.getInventory().getStack(j))) {
            return j;
         }
      }

      for (int k = 9; k < i; k++) {
         if (var1.test(minecraftClient3.player.getInventory().getStack(k))) {
            return k;
         }
      }

      return -1;
   }

   public boolean CommandManager(long var1) {
      int i = this.Easing(this::EmoteManager);
      if (i == -1) {
         return false;
      } else if (i <= 8) {
         this.CloseScreenEvent(i);
         return true;
      } else {
         this.on23(i, var1);
         return false;
      }
   }

   public void on23(int var1, long var2) {
      if (var2 - this.long59 >= 70L) {
         minecraftClient3.interactionManager
            .clickSlot(minecraftClient3.player.playerScreenHandler.syncId, var1, 8, SlotActionType.SWAP, minecraftClient3.player);
         this.long59 = var2;
      }
   }

   public boolean call259() {
      if (!this.boolean31 && !this.boolean32) {
         return switch (this.autoZamokVar159) {
            case val039, val088, val041, val116, val057, val021, val040, val025, val058, val042, val117 -> true;
            default -> false;
         };
      } else {
         return false;
      }
   }

   public void ModuleStateStore(long var1) {
      if (!minecraftClient3.player.getInventory().getStack(minecraftClient3.player.getInventory().selectedSlot).isEmpty()) {
         for (int i = 0; i < 9; i++) {
            if (minecraftClient3.player.getInventory().getStack(i).isEmpty()) {
               this.CloseScreenEvent(i);
               return;
            }
         }

         if (var1 - this.long59 >= 70L && this.float301()) {
            int k = -1;

            for (int j = 0; j < 9; j++) {
               ItemStack itemstack = minecraftClient3.player.getInventory().getStack(j);
               if (!this.EmoteManager(itemstack) && !this.CosmeticManager(itemstack) && !this.EmotePlayback(itemstack)) {
                  k = j;
                  break;
               }
            }

            if (k == -1) {
               for (int l = 0; l < 9; l++) {
                  if (!this.EmoteManager(minecraftClient3.player.getInventory().getStack(l))) {
                     k = l;
                     break;
                  }
               }
            }

            if (k != -1) {
               minecraftClient3.interactionManager
                  .clickSlot(minecraftClient3.player.playerScreenHandler.syncId, 36 + k, 0, SlotActionType.QUICK_MOVE, minecraftClient3.player);
               this.long59 = var1;
            }
         }
      }
   }

   public boolean float301() {
      for (int i = 9; i < 36; i++) {
         if (minecraftClient3.player.getInventory().getStack(i).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   public boolean on23(long var1, String var3) {
      if (var1 >= this.val170 && var1 - this.long61 >= 4000L) {
         this.val113 = 0;
         this.val114 = 0L;
         this.val232 = false;
         this.long72 = var1;
         this.long61 = var1;
         minecraftClient3.getNetworkHandler().sendChatCommand(var3);
         return true;
      } else {
         return false;
      }
   }

   public void on23(long var1, AutoZamok.State var3, AutoZamok.State var4, boolean var5) {
      if (this.val113 == 1) {
         if (var5) {
            minecraftClient3.options.jumpKey.setPressed(var1 - this.val114 < 300L);
         }

         if (var1 - this.val114 >= 1000L) {
            this.on23(var3);
         }
      } else if (this.val113 == -1) {
         if (var1 >= this.val170 && var1 - this.long61 >= 4000L) {
            this.on23(var4);
         }
      } else if (var1 - this.long72 > (this.val232 ? 12000L : 8000L)) {
         this.on23(var4);
      }
   }

   public boolean float302() {
      return this.autoZamokVar159 == AutoZamok.State.val088
         || this.autoZamokVar159 == AutoZamok.State.val116
         || this.autoZamokVar159 == AutoZamok.State.val117;
   }

   public boolean CloudPoller(long var1) {
      this.float303();
      Vec3d vec3d = minecraftClient3.player.getVelocity();
      return Math.abs(vec3d.x) < 0.05 && Math.abs(vec3d.z) < 0.05 || var1 - this.long58 > 2000L;
   }

   public static long PermissionListCodec(String var0) {
      long i = 0L;
      boolean flag = false;

      for (int j = 0; j < var0.length(); j++) {
         char c0 = var0.charAt(j);
         if (c0 >= '0' && c0 <= '9') {
            i = i * 10L + (c0 - '0');
            flag = true;
         } else if (flag) {
            break;
         }
      }

      return flag ? i : 0L;
   }

   public void call035() {
      BaritoneBridge.float137();
   }

   public void call164() {
      BaritoneBridge.vec3d16();
   }

   public void Easing(BlockPos var1, long var2) {
      if (!var1.equals(this.blockPos20) || !this.float164() && var2 - this.long68 >= 6000L) {
         this.call035();
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalNear(var1, 2));
         this.blockPos20 = var1;
         this.long68 = var2;
      }
   }

   public void ColorAnimator(BlockPos var1, long var2) {
      if (!var1.equals(this.blockPos20) || !this.float164() && var2 - this.long68 >= 6000L) {
         this.call035();
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalGetToBlock(var1));
         this.blockPos20 = var1;
         this.long68 = var2;
      }
   }

   public void on23(int var1, int var2, long var3) {
      BlockPos blockpos = new BlockPos(var1, 0, var2);
      if (!blockpos.equals(this.blockPos20) || !this.float164() && var3 - this.long68 >= 6000L) {
         this.call035();
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalXZ(var1, var2));
         this.blockPos20 = blockpos;
         this.long68 = var3;
      }
   }

   public void float303() {
      if (this.blockPos20 != null || this.float164()) {
         BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().onLostControl();
         this.blockPos20 = null;
      }
   }

   public boolean float164() {
      return BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing();
   }

   public void on23(AutoZamok.State var1) {
      this.autoZamokVar159 = var1;
      this.long58 = System.currentTimeMillis();
      this.blockPos15 = null;
      this.blockPos16 = null;
      this.long70 = 0L;
      this.long71 = 0L;
      this.int63 = 0;
      this.boolean22 = false;
   }

   public boolean float304() {
      return minecraftClient3.currentScreen instanceof DeathScreen || !minecraftClient3.player.isAlive();
   }

   public void float305() {
      minecraftClient3.options.sneakKey.setPressed(true);
      minecraftClient3.options.useKey.setPressed(true);
      if (!minecraftClient3.player.isUsingItem() || minecraftClient3.player.getActiveHand() != Hand.MAIN_HAND) {
         minecraftClient3.interactionManager.interactItem(minecraftClient3.player, Hand.MAIN_HAND);
      }
   }

   public void int452() {
      if (minecraftClient3.options != null) {
         minecraftClient3.options.useKey.setPressed(false);
         minecraftClient3.options.sneakKey.setPressed(false);
      }
   }

   public void call026() {
      if (this.boolean31) {
         this.int452();
         this.boolean31 = false;
      }
   }

   public void closeScreen() {
      if (minecraftClient3.player.currentScreenHandler != minecraftClient3.player.playerScreenHandler) {
         minecraftClient3.player.closeHandledScreen();
      }
   }

   public void CloseScreenEvent(int var1) {
      if (var1 >= 0 && var1 <= 8 && minecraftClient3.player.getInventory().selectedSlot != var1) {
         minecraftClient3.player.getInventory().selectedSlot = var1;
         minecraftClient3.getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(var1));
      }
   }

   public void ProfileItemBuilder(Vec3d var1) {
      AimUtils.AnalyticsTracker(var1);
   }

   public void log(String var1) {
      StyledTextBuilder.RefreshCacheEvent("AutoZamok: " + var1);
   }


   public enum State {
      val039,
      val088,
      val234,
      val371,
      val233,
      val041,
      val116,
      val057,
      val021,
      val115,
      val040,
      val025,
      val058,
      val042,
      val117,
      val235;
   }
}
