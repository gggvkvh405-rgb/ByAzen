package org.zenith.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.client.gui.screen.ingame.StructureBlockScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.PlayerScreenHandler;
import org.zenith.ZenithClient;
import org.zenith.client.screens.bot.BotScreen;
import org.zenith.client.screens.nlgui.NLMenuScreen;
import org.zenith.core.EffectEngine;
import org.zenith.core.TaskQueue;
import org.zenith.core.TaskQueueWorker;
import org.zenith.event.MovementInputEvent;
import org.zenith.module.combat.AutoTotem;
import org.zenith.module.misc.InventorySetting;

public final class TaskScheduler {
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final List<KeyBinding> list111 = List.of(
      minecraftClient3.options.forwardKey,
      minecraftClient3.options.backKey,
      minecraftClient3.options.leftKey,
      minecraftClient3.options.rightKey,
      minecraftClient3.options.jumpKey
   );
   public static final Map<Class<?>, TaskQueueWorker> map57 = new HashMap<>();

   public static void on23(Class<?> var0, Runnable var1) {
      on23(var0, var1, 0);
   }

   public static void on23(Class<?> var0, Runnable var1, int var2) {
      TaskQueue ll1ill11111i = ZenithClient.on23().FileLogger();
      TaskQueueWorker ll1ill11111i_l1i1illlili = ll1ill11111i.on23(var0)
         .on23(
            MovementInputEvent.class,
            var1xx -> {
               if (InventorySetting.inventorySetting.string104()) {
                  var1xx.NoSlow();
                  if (minecraftClient3.player.lastPlayerInput.jump()
                     || minecraftClient3.player.isSprinting()
                     || minecraftClient3.player.lastPlayerInput.forward()
                     || minecraftClient3.player.lastPlayerInput.backward()
                     || minecraftClient3.player.lastPlayerInput.left()
                     || minecraftClient3.player.lastPlayerInput.right()) {
                     return false;
                  }
               }

               var1.run();
               ScreenUtils.closeScreen();
               return true;
            },
            var2
         );
      ll1ill11111i.on23(ll1ill11111i_l1i1illlili, var2);
   }

   public static boolean ItemRegistry(Class<?> var0) {
      return ZenithClient.on23().FileLogger().Easing(var0);
   }

   public static boolean Easing(Class<?> var0) {
      return var0 == AutoTotem.class ? !AutoTotem.autoTotem.call118() : ItemRegistry(var0);
   }

   public static void double71() {
      string73();
   }

   public static void string72() {
      call204();
   }

   public static void string73() {
      list111.forEach(var0 -> var0.setPressed(false));
   }

   public static void call204() {
      list111.forEach(var0 -> var0.setPressed(InputUtil.isKeyPressed(minecraftClient3.getWindow(), var0.getDefaultKey().getCode())));
   }

   public static boolean call203() {
      if (minecraftClient3.currentScreen == null) {
         return false;
      } else if (!ZenithClient.on23().FileLogger().ImageEncoder()) {
         return false;
      } else if (EffectEngine.UiAnimation(minecraftClient3.currentScreen)) {
         return false;
      } else if (minecraftClient3.currentScreen instanceof SignEditScreen) {
         return false;
      } else if (minecraftClient3.currentScreen instanceof AnvilScreen) {
         return false;
      } else if (minecraftClient3.currentScreen instanceof AbstractCommandBlockScreen) {
         return false;
      } else if (minecraftClient3.currentScreen instanceof StructureBlockScreen) {
         return false;
      } else if (minecraftClient3.currentScreen instanceof BotScreen botscreen) {
         return !botscreen.isSearch();
      } else {
         return minecraftClient3.currentScreen instanceof NLMenuScreen nlmenuscreen
            ? !nlmenuscreen.isSearch()
            : minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler;
      }
   }

   public TaskScheduler() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
