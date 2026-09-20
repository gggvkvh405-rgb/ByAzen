package org.zenith.module.misc;

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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.zenith.ZenithClient;
import org.zenith.event.EventHookWorldRender;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.MathUtils;

@ModuleInfo(name = "ChestStealer", category = Category.MISC, description = "")
public final class ChestStealer extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final ModeSetting mode6 = new ModeSetting(
      "module.chestStealer.mode",
      "module.chestStealer.mode.desc",
      "module.chestStealer.mode.funTime",
      "module.chestStealer.mode.holyWorld",
      "module.chestStealer.mode.reallyWorld",
      "module.chestStealer.mode.custom"
   );
   public static final ChestStealer chestStealer = new ChestStealer();
   public final NumberSetting startDelay = new NumberSetting(
      "module.chestStealer.startDelay", 4.0F, 0.0F, 60.0F, 1.0F, "module.chestStealer.startDelay.desc", "t"
   );
   public final NumberSetting delay2 = new NumberSetting("module.chestStealer.delay", 0.0F, 0.0F, 60.0F, 1.0F, "module.chestStealer.delay.desc", "t");
   public final NumberSetting maxDelay = new NumberSetting("module.chestStealer.maxDelay", 6.0F, 0.0F, 60.0F, 1.0F, "module.chestStealer.maxDelay.desc", "t");
   public final NumberSetting closeDelay = new NumberSetting(
      "module.chestStealer.closeDelay", 4.0F, 0.0F, 60.0F, 1.0F, "module.chestStealer.closeDelay.desc", "t"
   );
   public final BooleanSetting closeScreen = new BooleanSetting("module.chestStealer.closeScreen", "module.chestStealer.closeScreen.desc", true);
   public final CooldownTimer zClass06718 = new CooldownTimer();
   public final CooldownTimer zClass06719 = new CooldownTimer();
   public final CooldownTimer zClass06720 = new CooldownTimer();
   public int int90 = -1;
   public boolean boolean41 = false;
   public boolean boolean42 = false;

   public ChestStealer() {
      this.startDelay.setVisible(() -> this.mode6.is("module.chestStealer.mode.custom"));
      this.delay2.setVisible(() -> this.mode6.is("module.chestStealer.mode.custom"));
      this.maxDelay.setVisible(() -> this.mode6.is("module.chestStealer.mode.custom"));
      this.closeDelay.setVisible(() -> this.mode6.is("module.chestStealer.mode.custom") && this.closeScreen.isEnabled());
   }

   public void resetState() {
      this.int90 = -1;
      this.boolean41 = false;
      this.boolean42 = false;
      this.zClass06719.reset();
      this.zClass06720.reset();
   }

   public float double14() {
      if (this.mode6.is("module.chestStealer.mode.funTime")) {
         return 8.0F;
      } else if (this.mode6.is("module.chestStealer.mode.holyWorld")) {
         return 1.0F;
      } else {
         return this.mode6.is("module.chestStealer.mode.reallyWorld") ? 5.0F : this.startDelay.getCurrent();
      }
   }

   public float double15() {
      if (this.mode6.is("module.chestStealer.mode.funTime")) {
         return 4.0F;
      } else if (this.mode6.is("module.chestStealer.mode.holyWorld")) {
         return 1.0F;
      } else {
         return this.mode6.is("module.chestStealer.mode.reallyWorld") ? 1.0F : this.delay2.getCurrent();
      }
   }

   public float double16() {
      if (this.mode6.is("module.chestStealer.mode.funTime")) {
         return 8.0F;
      } else if (this.mode6.is("module.chestStealer.mode.holyWorld")) {
         return 1.0F;
      } else {
         return this.mode6.is("module.chestStealer.mode.reallyWorld") ? 4.0F : this.maxDelay.getCurrent();
      }
   }

   public float double17() {
      if (this.mode6.is("module.chestStealer.mode.funTime")) {
         return 7.0F;
      } else if (this.mode6.is("module.chestStealer.mode.holyWorld")) {
         return 0.0F;
      } else {
         return this.mode6.is("module.chestStealer.mode.reallyWorld") ? 2.0F : this.closeDelay.getCurrent();
      }
   }

   @EventTarget
   public void on23(EventHookWorldRender var1) {
      if (minecraftClient3.player.currentScreenHandler instanceof GenericContainerScreenHandler genericcontainerscreenhandler && minecraftClient3.currentScreen != null) {
         if (this.int90 != genericcontainerscreenhandler.syncId) {
            this.int90 = genericcontainerscreenhandler.syncId;
            this.zClass06719.reset();
            this.zClass06720.reset();
            this.zClass06718.reset();
            this.boolean41 = false;
            this.boolean42 = false;
         }

         if (!this.boolean41) {
            if (!this.zClass06719.EventModifyMouseRotationInput((long)this.double14() * 50L)) {
               return;
            }

            this.boolean41 = true;
         }

         String s2 = minecraftClient3.currentScreen.getTitle().getString();
         String s = ZenithClient.on23().Easing().translate("module.chestStealer.title.auction");
         String s1 = ZenithClient.on23().Easing().translate("module.chestStealer.title.purchases");
         boolean flag = s2.contains(s) || s2.contains(s1);
         int i = genericcontainerscreenhandler.getInventory().size();
         List<Integer> list = IntStream.range(0, i).boxed().collect(Collectors.toList());
         Collections.shuffle(list);
         if (!flag) {
            for (int j : list) {
               Slot slot = genericcontainerscreenhandler.getSlot(j);
               if (slot.hasStack()) {
                  long k = (long)MathUtils.SimpleItemBuilder(Math.min(this.double15(), this.double16()), Math.max(this.double15(), this.double16()));
                  if (this.zClass06718.EventModifyMouseRotationInput(k * 50L)) {
                     minecraftClient3.interactionManager
                        .clickSlot(genericcontainerscreenhandler.syncId, j, 0, SlotActionType.QUICK_MOVE, minecraftClient3.player);
                     this.zClass06718.reset();
                     this.boolean42 = false;
                     break;
                  }
               }
            }
         }

         boolean flag1 = this.on23(genericcontainerscreenhandler);
         if (this.closeScreen.isEnabled() && flag1) {
            if (!this.boolean42) {
               this.boolean42 = true;
               this.zClass06720.reset();
               return;
            }

            if (this.zClass06720.EventModifyMouseRotationInput((long)this.double17() * 50L)) {
               minecraftClient3.player.closeHandledScreen();
               this.boolean42 = false;
            }
         } else {
            this.boolean42 = false;
         }
      } else {
         this.resetState();
      }
   }

   public boolean on23(GenericContainerScreenHandler var1) {
      for (int i = 0; i < (var1.getInventory().size() == 90 ? 54 : 27); i++) {
         if (var1.getSlot(i).hasStack()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.resetState();
      this.zClass06718.reset();
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.resetState();
      this.zClass06718.reset();
   }
}
