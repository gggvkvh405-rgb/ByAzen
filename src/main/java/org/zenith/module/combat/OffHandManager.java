package org.zenith.module.combat;

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
import java.util.Comparator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import org.zenith.event.MovementInputEvent;
import org.zenith.setting.ModeSetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.ScreenUtils;
import org.zenith.util.TaskScheduler;

@ModuleInfo(name = "OffHandManager", category = Category.COMBAT, description = "")
public final class OffHandManager extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final OffHandManager offHandManager = new OffHandManager();
   public final ModeSetting swapHealMode = new ModeSetting(
      "module.offhand.swapHealMode",
      "module.offhand.swapHealMode.desc",
      "module.offhand.mode.mythic",
      "module.offhand.mode.legendary",
      "module.offhand.mode.talisman",
      "module.offhand.mode.none"
   );
   public final ModeSetting swapEnemyMode = new ModeSetting(
      "module.offhand.swapEnemyMode",
      "module.offhand.swapEnemyMode.desc",
      "module.offhand.mode.cerberus",
      "module.offhand.mode.mythic",
      "module.offhand.mode.legendary",
      "module.offhand.mode.talisman",
      "module.offhand.mode.none"
   );
   public final CooldownTimer zClass06730 = new CooldownTimer();

   public void reset() {
      this.zClass06730.reset();
   }

   @EventTarget
   public void ItemRegistry(MovementInputEvent var1) {
      LivingEntity livingentity = Aura.aura.zClass054();
      if (this.int393()) {
         Slot slot;
         if (!this.swapEnemyMode.is(4) && livingentity != null && this.ColorAnimator(livingentity)) {
            slot = this.UiAnimation(this.swapEnemyMode);
         } else if (minecraftClient3.player.isUsingItem() && !this.swapHealMode.is(3)) {
            slot = this.UiAnimation(this.swapHealMode);
            if (slot == null) {
               return;
            }

            if (!minecraftClient3.player.getOffHandStack().equals(slot.getStack())
               && TaskScheduler.Easing(AutoTotem.class)
               && TaskScheduler.Easing(AutoSwap.class)
               && TaskScheduler.Easing(OffHandManager.class)) {
               TaskScheduler.on23(OffHandManager.class, () -> {
                  if (TaskScheduler.Easing(AutoTotem.class)) {
                     ScreenUtils.on23(slot, Hand.OFF_HAND, true);
                  }
               });
            }
         } else {
            slot = null;
         }

         if (slot != null && TaskScheduler.Easing(AutoTotem.class) && TaskScheduler.Easing(AutoSwap.class) && TaskScheduler.Easing(OffHandManager.class)) {
            TaskScheduler.on23(OffHandManager.class, () -> {
               if (TaskScheduler.Easing(AutoTotem.class)) {
                  ScreenUtils.on23(slot, Hand.OFF_HAND, true);
               }
            });
         }
      }
   }

   public boolean int393() {
      return this.zClass06730.EventModifyMouseRotationInput(1000L);
   }

   public Slot UiAnimation(ModeSetting var1) {
      try {
         if (var1.get().equals("module.offhand.mode.talisman")) {
            return ScreenUtils.on23(
               minecraftClient3.player.playerScreenHandler,
               Items.TOTEM_OF_UNDYING,
               Comparator.<Slot, Boolean>comparing(var0 -> !var0.getStack().hasEnchantments()).thenComparing((var0, var1x) -> {
                  NbtComponent nbtcomponent = (NbtComponent)var0.getStack().get(DataComponentTypes.CUSTOM_DATA);
                  NbtComponent nbtcomponent1 = (NbtComponent)var1x.getStack().get(DataComponentTypes.CUSTOM_DATA);
                  if (nbtcomponent == null && nbtcomponent1 != null) {
                     return -1;
                  }

                  if (nbtcomponent != null && nbtcomponent1 == null) {
                     return 1;
                  }

                  if (nbtcomponent == null) {
                     return 0;
                  }

                  boolean flag = nbtcomponent.copyNbt().contains("sphereEffect");
                  boolean flag1 = nbtcomponent1.copyNbt().contains("sphereEffect");
                  if (flag && !flag1) {
                     return 1;
                  }

                  if (!flag && flag1) {
                     return -1;
                  }

                  if (!flag) {
                     return 0;
                  }

                  String s1 = nbtcomponent.copyNbt().get("sphereEffect").toString();
                  String s2 = nbtcomponent1.copyNbt().get("sphereEffect").toString();
                  return Integer.compare(s2.length(), s1.length());
               }).thenComparingInt(var0 -> var0.id).reversed(),
               var0 -> true
            );
         }

         String s = this.EventTick(var1.get());
         return ScreenUtils.on23(
            Items.PLAYER_HEAD,
            var1x -> {
               if (!var1x.getStack().isEmpty()) {
                  NbtComponent nbtcomponent = (NbtComponent)var1x.getStack().get(DataComponentTypes.CUSTOM_DATA);
                  if (nbtcomponent != null
                     && nbtcomponent.copyNbt().contains("SkullOwner")
                     && nbtcomponent.copyNbt().get("SkullOwner").toString().contains(s)) {
                     return true;
                  }
               }

               return false;
            }
         );
      } catch (Exception exception) {
         exception.printStackTrace();
         return null;
      }
   }

   public String EventTick(String var1) {
      return switch (var1) {
         case "module.offhand.mode.cerberus" -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjA5NWE3ZmQ5MGRhYTFiYmU3MDY5MDg5NzQwZTA1ZDBiZmM2NjI5NmVlM2M0MGVlNzFhNGUwYTY2MTZiMmJiYyJ9fX0=";
         case "module.offhand.mode.mythic" -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZmFmZjJlYjQ5OGU1YzZhMDQ0ODRmMGM5Zjc4NWI0NDg0NzlhYjIxM2RmOTVlYzkxMTc2YTMwOGExMmFkZDcwIn19fQ==";
         case "module.offhand.mode.legendary" -> "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM5MzY1NjQyYzZlZGRjZmVkZjViNWUxNGUyYmM3MTI1N2Q5ZTRhMzM2M2QxMjNjNmYzM2M1NWNhZmJmNmQifX19";
         default -> throw new RuntimeException("Unknown key: " + var1);
      };
   }

   public boolean ColorAnimator(LivingEntity var1) {
      if (var1.isUsingItem()) {
         return true;
      } else {
         return var1.getMainHandStack().isIn(ItemTags.SWORDS)
            ? false
            : var1.getOffHandStack().getItem() != Items.PLAYER_HEAD || this.NbtEditor(var1.getOffHandStack());
      }
   }

   public boolean NbtEditor(ItemStack var1) {
      NbtComponent nbtcomponent = (NbtComponent)var1.get(DataComponentTypes.CUSTOM_DATA);
      return nbtcomponent != null
         && nbtcomponent.copyNbt().contains("SkullOwner")
         && nbtcomponent.copyNbt()
            .get("SkullOwner")
            .toString()
            .contains(
               "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGM5MzY1NjQyYzZlZGRjZmVkZjViNWUxNGUyYmM3MTI1N2Q5ZTRhMzM2M2QxMjNjNmYzM2M1NWNhZmJmNmQifX19"
            );
   }
}
