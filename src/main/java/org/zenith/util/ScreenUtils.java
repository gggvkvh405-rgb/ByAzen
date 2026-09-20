package org.zenith.util;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.IntPredicate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.zenith.ZenithClient;
import org.zenith.core.EffectEngine;
import org.zenith.core.GameService;
import org.zenith.core.TaskQueueWorker;
import org.zenith.event.EventInjectHandleInputEvents;
import org.zenith.event.MovementInputEvent;
import org.zenith.event.RefreshCacheEvent;
import org.zenith.module.combat.AutoTotem;
import org.zenith.module.misc.ElytraHelper;
import org.zenith.module.misc.InventorySetting;

public final class ScreenUtils implements GameService {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final List<KeyBinding> list102 = List.of(
      minecraftClient3.options.forwardKey,
      minecraftClient3.options.backKey,
      minecraftClient3.options.leftKey,
      minecraftClient3.options.rightKey,
      minecraftClient3.options.jumpKey
   );

   public static void call216() {
      if (InventorySetting.inventorySetting.path12()) {
         ScreenHandler screenhandler = minecraftClient3.player.currentScreenHandler;
         ItemStack itemstack = ((Item)Registries.ITEM.get((int)MathUtils.SimpleItemBuilder(0.0, 100.0))).getDefaultStack();
         ItemStackHash itemStackHash = ItemStackHash.fromItemStack(itemstack, minecraftClient3.player.networkHandler.getComponentHasher());
         minecraftClient3.player
            .networkHandler
            .sendPacket(
               new ClickSlotC2SPacket(
                  screenhandler.syncId,
                  screenhandler.getRevision(),
                  (short)0,
                  (byte)0,
                  SlotActionType.PICKUP_ALL,
                  Int2ObjectMaps.singleton(0, itemStackHash),
                  itemStackHash
               )
            );
      }
   }

   public static void closeScreen() {
      if (minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler) {
         minecraftClient3.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(minecraftClient3.player.currentScreenHandler.syncId));
      } else {
         minecraftClient3.player.closeHandledScreen();
      }
   }

   public static void on23(Slot var0, Hand var1, boolean var2) {
      if (var0 != null
         && var0.id != -1
         && (!var1.equals(Hand.OFF_HAND) || var0.inventory instanceof PlayerInventory || var0.inventory instanceof EnderChestInventory)) {
         int i = var1.equals(Hand.MAIN_HAND) ? minecraftClient3.player.getInventory().selectedSlot : 40;
         on23(var0, i, var2);
      }
   }

   public static void on23(Slot var0, int var1, boolean var2) {
      on23(var0, var1, SlotActionType.SWAP, false);
      if (var2) {
         call216();
      }
   }

   public static void UiAnimation(Slot var0, int var1) {
      on23(var0, var1, SlotActionType.SWAP, false);
   }

   public static void on23(Slot var0, int var1, SlotActionType var2, boolean var3) {
      if (var0 != null) {
         on23(var0.id, var1, var2, var3);
      }
   }

   public static void on23(int var0, int var1, SlotActionType var2, boolean var3) {
      on23(minecraftClient3.player.currentScreenHandler.syncId, var0, var1, var2, var3);
   }

   public static void on23(int var0, int var1, int var2, SlotActionType var3, boolean var4) {
      minecraftClient3.interactionManager.clickSlot(var0, var1, var2, var3, minecraftClient3.player);
      if (var4) {
         minecraftClient3.player.currentScreenHandler.onSlotClick(var1, var2, var3, minecraftClient3.player);
      }
   }

   public static Slot SimpleItemBuilder(Item var0) {
      return on23(var0, var0x -> true);
   }

   public static Slot on23(Item var0, Predicate<Slot> var1) {
      return on23(var0, Comparator.comparingInt(var0x -> var0x.id), var1);
   }

   public static Slot on23(ScreenHandler var0, Item var1) {
      return var0.slots.stream().filter(var1xx -> var1xx.getStack().getItem() == var1).findFirst().orElse(null);
   }

   public static Slot on23(ScreenHandler var0, Predicate<Slot> var1) {
      return var0.slots.stream().filter(var1).findFirst().orElse(null);
   }

   public static Slot ColorAnimator(Predicate<Slot> var0) {
      return call006().filter(var0).findFirst().orElse(null);
   }

   public static Slot on23(Predicate<Slot> var0, Comparator<Slot> var1) {
      return call006().filter(var0).max(var1).orElse(null);
   }

   public static Slot on23(ScreenHandler var0, Item var1, Comparator<Slot> var2, Predicate<Slot> var3) {
      return var0.slots.stream().filter(var1xx -> var1xx.getStack().getItem().equals(var1)).filter(var3).max(var2).orElse(null);
   }

   public static Slot on23(Item var0, Comparator<Slot> var1, Predicate<Slot> var2) {
      return call006().filter(var1x -> var1x.getStack().getItem().equals(var0)).filter(var2).max(var1).orElse(null);
   }

   public static Slot call276() {
      return call006()
         .filter(
            var0 -> var0.getStack().get(DataComponentTypes.FOOD) != null
               && !((FoodComponent)var0.getStack().get(DataComponentTypes.FOOD)).canAlwaysEat()
         )
         .max(Comparator.comparingDouble(var0 -> ((FoodComponent)var0.getStack().get(DataComponentTypes.FOOD)).saturation()))
         .orElse(null);
   }

   public static Slot MediaTrackInfo(List<Item> var0) {
      return call006().filter(var1 -> var0.contains(var1.getStack().getItem())).findFirst().orElse(null);
   }

   public static Slot on23(List<Item> var0, Comparator<Slot> var1) {
      return call006().filter(var1x -> var0.contains(var1x.getStack().getItem())).max(var1).orElse(null);
   }

   public static Slot ItemRegistry(RegistryEntry<StatusEffect> var0) {
      return call006()
         .filter(
            var1 -> {
               PotionContentsComponent potioncontentscomponent = (PotionContentsComponent)var1.getStack().get(DataComponentTypes.POTION_CONTENTS);
               return potioncontentscomponent == null
                  ? false
                  : StreamSupport.<StatusEffectInstance>stream(potioncontentscomponent.getEffects().spliterator(), false)
                     .anyMatch(var1x -> var1x.getEffectType().equals(var0));
            }
         )
         .findFirst()
         .orElse(null);
   }

   public static boolean on23(ItemStack var0, String var1) {
      if (var0.isEmpty()) {
         return false;
      }

      NbtComponent nbtcomponent = (NbtComponent)var0.get(DataComponentTypes.CUSTOM_DATA);
      return nbtcomponent == null ? false : nbtcomponent.copyNbt().toString().contains(var1);
   }

   public static Slot on23(StatusEffectCategory var0) {
      return call006()
         .filter(
            var1 -> {
               ItemStack itemstack = var1.getStack();
               PotionContentsComponent potioncontentscomponent = (PotionContentsComponent)itemstack.get(DataComponentTypes.POTION_CONTENTS);
               if (itemstack.getItem().equals(Items.SPLASH_POTION) && potioncontentscomponent != null) {
                  StatusEffectCategory statuseffectcategory = var0.equals(StatusEffectCategory.BENEFICIAL) ? StatusEffectCategory.HARMFUL : StatusEffectCategory.BENEFICIAL;
                  long i = StreamSupport.<StatusEffectInstance>stream(potioncontentscomponent.getEffects().spliterator(), false)
                     .filter(var1x -> ((StatusEffect)var1x.getEffectType().value()).getCategory().equals(var0))
                     .count();
                  long j = StreamSupport.<StatusEffectInstance>stream(potioncontentscomponent.getEffects().spliterator(), false)
                     .filter(var1x -> ((StatusEffect)var1x.getEffectType().value()).getCategory().equals(statuseffectcategory))
                     .count();
                  return i >= j;
               } else {
                  return false;
               }
            }
         )
         .findFirst()
         .orElse(null);
   }

   public static int ItemSpec(Item var0) {
      return IntStream.range(0, 45)
         .filter(var1 -> Objects.requireNonNull(minecraftClient3.player).getInventory().getStack(var1).getItem().equals(var0))
         .map(var0x -> minecraftClient3.player.getInventory().getStack(var0x).getCount())
         .sum();
   }

   public static int CloudUserProfile(List<Item> var0) {
      return IntStream.range(0, 9)
         .filter(var1 -> var0.contains(minecraftClient3.player.getInventory().getStack(var1).getItem()))
         .findFirst()
         .orElse(-1);
   }

   public static int on23(IntPredicate var0) {
      return IntStream.range(0, 9).filter(var0).findFirst().orElse(-1);
   }

   public static int ItemRegistry(Predicate<Slot> var0) {
      return call006().filter(var0).mapToInt(var0x -> var0x.getStack().getCount()).sum();
   }

   public static Slot call119() {
      long i = call006().count();
      int j = i == 46L ? 10 : 9;
      return call006().toList().get(Math.toIntExact(i - j + minecraftClient3.player.getInventory().selectedSlot));
   }

   public static boolean call178() {
      return call006().toList().size() != 46;
   }

   public static Stream<Slot> call006() {
      return minecraftClient3.player.currentScreenHandler.slots.stream();
   }

   public static void ItemServiceBase(Item var0) {
      UiAnimation(var0, var0x -> true);
   }

   public static void UiAnimation(Item var0, Predicate<Slot> var1) {
      on23(var0, var1, Hand.MAIN_HAND);
   }

   public static void on23(Item var0, Hand var1) {
      on23(var0, var0x -> true, var1);
   }

   public static void on23(Item var0, Predicate<Slot> var1, Hand var2) {
      float f = minecraftClient3.player.getItemCooldownManager().getCooldownProgress(var0.getDefaultStack(), 0.0F);
      if (f > 0.0F) {
         String s = MathUtils.ItemServiceBase(f, 0.1) + "с";
         ZenithClient.on23()
            .ConfigJsonUtil()
            .on23(
               "N",
               Text.of(
                  var0.getName()
                     .copy()
                     .setStyle(Style.EMPTY.withColor(val003.TextScanner().getCurrentStyle().getPrimaryColor().getColor().call001()))
                     .append(
                        Text.of(" находиться в кд")
                           .copy()
                           .setStyle(Style.EMPTY.withColor(val003.TextScanner().getCurrentStyle().getTextEnable().getColor().call001()))
                     )
               )
            );
      } else {
         Slot slot = on23(var0, var1);
         if (slot == null) {
            ZenithClient.on23()
               .ConfigJsonUtil()
               .on23(
                  "M",
                  Text.of(
                     var0.getName()
                        .copy()
                        .setStyle(Style.EMPTY.withColor(val003.TextScanner().getCurrentStyle().getPrimaryColor().getColor().call001()))
                        .append(
                           Text.of(" не найден")
                              .copy()
                              .setStyle(Style.EMPTY.withColor(val003.TextScanner().getCurrentStyle().getTextEnable().getColor().call001()))
                        )
                  )
               );
         } else if (minecraftClient3.player.isUsingItem() && ZenithClient.on23().CloudApiClient().call003()) {
            ZenithClient.on23()
               .ConfigJsonUtil()
               .on23(
                  "M",
                  Text.of(
                     Text.of("Хавать")
                        .copy()
                        .setStyle(Style.EMPTY.withColor(val003.TextScanner().getCurrentStyle().getPrimaryColor().getColor().call001()))
                        .append(
                           Text.of(" нельзя")
                              .copy()
                              .setStyle(Style.EMPTY.withColor(val003.TextScanner().getCurrentStyle().getTextEnable().getColor().call001()))
                        )
                  )
               );
         } else {
            TaskQueueWorker ll1ill11111i_l1i1illlili = new TaskQueueWorker();
            if (!InventorySetting.inventorySetting.string104() && InventorySetting.inventorySetting.call099()) {
               ll1ill11111i_l1i1illlili.on23(RefreshCacheEvent.class, var2xx -> {
                  if (var2xx.isCancelled()) {
                     return false;
                  }

                  on23(slot, var2);
                  closeScreen();
                  return true;
               });
            } else {
               int i = Math.toIntExact(call006().count());
               int j = slot.id - 36;
               if (var2 == Hand.MAIN_HAND && j >= 0 && j <= 8 && i == 46) {
                  AtomicInteger atomicinteger = new AtomicInteger(minecraftClient3.player.inventory.selectedSlot);
                  if (!InventorySetting.inventorySetting.call099()) {
                     ll1ill11111i_l1i1illlili.on23(RefreshCacheEvent.class, var2x -> {
                        atomicinteger.set(minecraftClient3.player.inventory.selectedSlot);
                        minecraftClient3.player.inventory.setSelectedSlot(j);
                        return true;
                     });
                     ll1ill11111i_l1i1illlili.on23(RefreshCacheEvent.class, var1x -> {
                        if (var1x.isCancelled()) {
                           return false;
                        }

                        EffectEngine.useItem(Hand.MAIN_HAND);
                        if (ZenithClient.on23().CloudApiClient().call003()) {
                           minecraftClient3.interactionManager.stopUsingItem(minecraftClient3.player);
                        }

                        minecraftClient3.player.inventory.setSelectedSlot(atomicinteger.get());
                        var1x.cancel();
                        return true;
                     });
                  } else {
                     ll1ill11111i_l1i1illlili.on23(RefreshCacheEvent.class, var1x -> {
                        if (var1x.isCancelled()) {
                           return false;
                        }

                        int i1 = minecraftClient3.player.inventory.selectedSlot;
                        minecraftClient3.player.inventory.setSelectedSlot(j);
                        EffectEngine.useItem(Hand.MAIN_HAND);
                        minecraftClient3.player.inventory.setSelectedSlot(i1);
                        var1x.cancel();
                        return true;
                     });
                  }
               } else if (!InventorySetting.inventorySetting.call099()) {
                  if (TaskScheduler.Easing(AutoTotem.class) || var2 != Hand.OFF_HAND) {
                     int k = var2.equals(Hand.MAIN_HAND) ? minecraftClient3.player.getInventory().selectedSlot : 40;
                     int l = minecraftClient3.player.getInventory().selectedSlot;
                     ll1ill11111i_l1i1illlili.on23(
                        EventInjectHandleInputEvents.class,
                        var2x -> {
                           if (InventorySetting.inventorySetting.string104()
                              && (
                                 minecraftClient3.player.lastPlayerInput.jump()
                                    || minecraftClient3.player.isSprinting()
                                    || minecraftClient3.player.lastPlayerInput.forward()
                                    || minecraftClient3.player.lastPlayerInput.backward()
                                    || minecraftClient3.player.lastPlayerInput.left()
                                    || minecraftClient3.player.lastPlayerInput.right()
                              )) {
                              return false;
                           }

                           on23(slot, k, false);
                           closeScreen();
                           return true;
                        }
                     );
                     ll1ill11111i_l1i1illlili.on23(RefreshCacheEvent.class, var3x -> {
                        if (var3x.isCancelled()) {
                           return false;
                        }

                        if (var2 == Hand.MAIN_HAND) {
                           minecraftClient3.player.getInventory().setSelectedSlot(k);
                           EffectEngine.useItem(var2);
                           if (ZenithClient.on23().CloudApiClient().call003()) {
                              EffectEngine.useItem(var2);
                           }

                           minecraftClient3.player.getInventory().setSelectedSlot(l);
                           if (ZenithClient.on23().CloudApiClient().call003()) {
                              minecraftClient3.interactionManager.stopUsingItem(minecraftClient3.player);
                           }
                        } else {
                           EffectEngine.useItem(var2);
                           if (ZenithClient.on23().CloudApiClient().call003()) {
                              EffectEngine.useItem(var2);
                           }
                        }

                        var3x.cancel();
                        return true;
                     });
                     ll1ill11111i_l1i1illlili.on23(
                        EventInjectHandleInputEvents.class,
                        var2x -> {
                           if (InventorySetting.inventorySetting.string104()
                              && (
                                 minecraftClient3.player.lastPlayerInput.jump()
                                    || minecraftClient3.player.isSprinting()
                                    || minecraftClient3.player.lastPlayerInput.forward()
                                    || minecraftClient3.player.lastPlayerInput.backward()
                                    || minecraftClient3.player.lastPlayerInput.left()
                                    || minecraftClient3.player.lastPlayerInput.right()
                              )) {
                              return false;
                           }

                           on23(slot, k, false);
                           closeScreen();
                           return true;
                        }
                     );
                     if (InventorySetting.inventorySetting.string104()) {
                        ll1ill11111i_l1i1illlili.UiAnimation(MovementInputEvent.class, var0x -> {
                           var0x.NoSlow();
                           return true;
                        });
                     }
                  }
               } else {
                  ll1ill11111i_l1i1illlili.on23(
                     RefreshCacheEvent.class,
                     var2xx -> {
                        if (var2xx.isCancelled()) {
                           return false;
                        }

                        if (InventorySetting.inventorySetting.string104()
                           && (
                              minecraftClient3.player.lastPlayerInput.jump()
                                 || minecraftClient3.player.isSprinting()
                                 || minecraftClient3.player.lastPlayerInput.forward()
                                 || minecraftClient3.player.lastPlayerInput.backward()
                                 || minecraftClient3.player.lastPlayerInput.left()
                                 || minecraftClient3.player.lastPlayerInput.right()
                           )) {
                           return false;
                        }

                        on23(slot, var2);
                        closeScreen();
                        return true;
                     }
                  );
                  if (InventorySetting.inventorySetting.string104()) {
                     ll1ill11111i_l1i1illlili.UiAnimation(MovementInputEvent.class, var0x -> {
                        var0x.NoSlow();
                        return true;
                     });
                  }
               }
            }

            ZenithClient.on23().FileLogger().on23(ll1ill11111i_l1i1illlili);
         }
      }
   }

   public static void on23(Slot var0, Hand var1) {
      on23(var0, var1, false);
      closeScreen();
      EffectEngine.useItem(var1);
      on23(var0, var1, true);
   }

   public static void Easing(Slot var0, int var1) {
      if (var0 != null) {
         on23(var0.id, var1, false, false);
      }
   }

   public static void UiAnimation(Slot var0, int var1, boolean var2) {
      on23(var0, var1, var2, false);
   }

   public static void on23(Slot var0, int var1, boolean var2, boolean var3) {
      if (var0 != null) {
         on23(var0.id, var1, var2, var3);
      }
   }

   public static void on23(int var0, int var1, boolean var2, boolean var3) {
      if (var0 != var1 && var0 != -1) {
         int i = Math.toIntExact(call006().count());
         int j = var0 - 36;
         if (j >= 0 && j <= 8 && i == 46) {
            if (var2) {
               TaskScheduler.on23(ScreenUtils.class, () -> on23(var1, j, SlotActionType.SWAP, false));
            } else {
               on23(var1, j, SlotActionType.SWAP, false);
               closeScreen();
            }
         } else if (!ElytraHelper.elytraHelper.isEnabled() || var1 != 6 || !ElytraHelper.elytraHelper.double26()) {
            if (var2) {
               if (InventorySetting.inventorySetting.call099()) {
                  TaskScheduler.on23(ScreenUtils.class, () -> UiAnimation(var0, var1, var3));
               } else {
                  TaskQueueWorker ll1ill11111i_l1i1illlili = ZenithClient.on23()
                     .FileLogger()
                     .on23(ScreenUtils.class)
                     .on23(
                        MovementInputEvent.class,
                        var1x -> {
                           if (InventorySetting.inventorySetting.string104()) {
                              var1x.NoSlow();
                              if (minecraftClient3.player.lastPlayerInput.jump()
                                 || minecraftClient3.player.isSprinting()
                                 || minecraftClient3.player.lastPlayerInput.forward()
                                 || minecraftClient3.player.lastPlayerInput.backward()
                                 || minecraftClient3.player.lastPlayerInput.left()
                                 || minecraftClient3.player.lastPlayerInput.right()) {
                                 return false;
                              }
                           }

                           on23(var0, 0, SlotActionType.SWAP, false);
                           closeScreen();
                           return true;
                        },
                        0
                     )
                     .on23(MovementInputEvent.class, var0x -> true)
                     .on23(
                        MovementInputEvent.class,
                        var2x -> {
                           if (InventorySetting.inventorySetting.string104()) {
                              var2x.NoSlow();
                              if (minecraftClient3.player.lastPlayerInput.jump()
                                 || minecraftClient3.player.isSprinting()
                                 || minecraftClient3.player.lastPlayerInput.forward()
                                 || minecraftClient3.player.lastPlayerInput.backward()
                                 || minecraftClient3.player.lastPlayerInput.left()
                                 || minecraftClient3.player.lastPlayerInput.right()) {
                                 return false;
                              }
                           }

                           on23(var1, 0, SlotActionType.SWAP, false);
                           closeScreen();
                           on23(var0, 0, SlotActionType.SWAP, false);
                           closeScreen();
                           return true;
                        }
                     );
                  ZenithClient.on23().FileLogger().on23(ll1ill11111i_l1i1illlili, 0);
               }
            } else {
               UiAnimation(var0, var1, var3);
               closeScreen();
            }
         }
      }
   }

   public static void UiAnimation(int var0, int var1, boolean var2) {
      on23(var0, 0, SlotActionType.SWAP, false);
      closeScreen();
      on23(var1, 0, SlotActionType.SWAP, false);
      on23(var0, 0, SlotActionType.SWAP, false);
      if (var2) {
         call216();
      }
   }

   public ScreenUtils() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
