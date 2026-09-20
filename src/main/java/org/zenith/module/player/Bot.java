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
import net.minecraft.client.MinecraftClient;
import org.zenith.ZenithClient;
import org.zenith.client.screens.bot.BotScreen;
import org.zenith.event.EventRenderScreenHook;
import org.zenith.event.EventTriggerKeyEvent;
import org.zenith.setting.KeySetting;

@ModuleInfo(name = "Bot", category = Category.PLAYER, description = "", long120 = true)
public final class Bot extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Bot bot = new Bot();
   public final KeySetting openGui = new KeySetting("module.bot.openGui", 345);

   public void float366() {
      if (!(minecraftClient3.currentScreen instanceof BotScreen)) {
         minecraftClient3.setScreen(new BotScreen());
         ZenithClient.on23().NbtItemSpec().on23(ZenithClient.on23().NbtItemSpec().soundEvent);
      }
   }

   @EventTarget
   public void on23(EventTriggerKeyEvent var1) {
      if (var1.ItemRegistry(this.openGui.getKeyCode())) {
         this.float366();
      }
   }

   @EventTarget(3)
   public void on23(EventRenderScreenHook var1) {
      if (minecraftClient3.currentScreen instanceof BotScreen botscreen) {
         botscreen.renderTop(var1.WarpFarm(), var1.WarpFarm().getMouseX(), var1.WarpFarm().getMouseY());
      }
   }
}
