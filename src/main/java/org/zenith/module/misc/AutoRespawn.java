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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;
import org.zenith.event.EventTick;

@ModuleInfo(name = "AutoRespawn", category = Category.MISC, description = "Автоматически возрождается после смерти")
public final class AutoRespawn extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoRespawn autoRespawn = new AutoRespawn();

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null
         && minecraftClient3.world != null
         && minecraftClient3.currentScreen instanceof DeathScreen
         && minecraftClient3.player.deathTime > 5) {
         minecraftClient3.player.requestRespawn();
         minecraftClient3.setScreen(null);
      }
   }
}
