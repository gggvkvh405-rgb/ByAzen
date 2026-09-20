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
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.zenith.ZenithClient;
import org.zenith.event.PacketEvent;
import org.zenith.setting.BooleanSetting;

@ModuleInfo(name = "AutoAccept", category = Category.MISC, description = "Автоматически принимает телепортацию")
public final class AutoAccept extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoAccept autoAccept = new AutoAccept();
   public final BooleanSetting onlyFriend = new BooleanSetting("module.autoAccept.onlyFriend", "module.autoAccept.onlyFriend.desc", false);

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (minecraftClient3.player != null
         && minecraftClient3.world != null
         && var1.Arrows()
         && var1.ItemScroller() instanceof GameMessageS2CPacket gamemessages2cpacket) {
         String s1 = gamemessages2cpacket.content().getString().toLowerCase(Locale.ROOT);
         if (s1.contains("телепортироваться") || s1.contains("has requested teleport") || s1.contains("просит к вам телепортироваться")) {
            if (this.onlyFriend.isEnabled()) {
               boolean flag = false;

               for (String s : ZenithClient.on23().MediaTrackInfo().getItems()) {
                  if (s1.contains(s.toLowerCase(Locale.ROOT))) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return;
               }
            }

            minecraftClient3.player.networkHandler.sendChatCommand("tpaccept");
         }
      }
   }
}
