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
import net.minecraft.network.packet.c2s.play.CommandExecutionC2SPacket;
import org.zenith.ZenithClient;
import org.zenith.event.PacketEvent;

@ModuleInfo(name = "PvpSafe", description = "Не дает ливнуть в кт с серва", category = Category.MISC)
public final class PvpSafe extends Module {
   public static final PvpSafe pvpSafe = new PvpSafe();

   @EventTarget
   public void EnchantItemSpec(PacketEvent var1) {
      if (var1.AntiInvisible() && ZenithClient.on23().CloudApiClient().soundEvent5() && var1.ItemScroller() instanceof CommandExecutionC2SPacket commandexecutionc2spacket) {
         CommandExecutionC2SPacket commandexecutionc2spacket1 = commandexecutionc2spacket;
         String s = commandexecutionc2spacket1.command();
         if (s.contains("hub")) {
            var1.setCancelled(true);
         }
      }
   }
}
