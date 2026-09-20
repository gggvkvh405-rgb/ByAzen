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
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import org.zenith.ZenithClient;
import org.zenith.event.EventTick;
import org.zenith.setting.TextSetting;
import org.zenith.setting.MultiSelectSetting;
import org.zenith.setting.NumberSetting;

@ModuleInfo(name = "AutoLeave", category = Category.MISC, description = "")
public final class AutoLeave extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoLeave autoLeave = new AutoLeave();
   public final MultiSelectSetting modeSetting2 = MultiSelectSetting.on23(
      "leave.factor", "module.autoLeave.leaveFactor.desc", List.of("leave.health", "leave.players", "Distance")
   );
   public final NumberSetting health = new NumberSetting(
      "leave.health", 15.0F, 1.0F, 20.0F, 1.0F, "module.autoLeave.minHealth.desc", "hp", () -> this.modeSetting2.ConfigJsonUtil(0), null
   );
   public final NumberSetting radius = new NumberSetting(
      "Radius", 300.0F, 3.0F, 300.0F, 10.0F, "module.autoLeave.leaveRadius.desc", "b", () -> this.modeSetting2.ConfigJsonUtil(2), null
   );
   public final NumberSetting time = new NumberSetting("leave.time", 0.0F, 0.0F, 200.0F, 10.0F, "module.autoLeave.leaveTime.desc", "s");
   public final TextSetting command = new TextSetting("leave.command", "module.autoLeave.command.desc", "/hub", "leave.command.description");
   public int int45 = 0;

   @Override
   public void onEnable() {
      this.int45 = -1;
      super.onEnable();
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (this.call118()) {
         this.int45++;
      }

      if (this.int45 >= this.time.getCurrent()) {
         if (this.command.getValue().startsWith("/")) {
            minecraftClient3.player.networkHandler.sendChatCommand(this.command.getValue().substring(1));
         } else {
            minecraftClient3.player.networkHandler.sendChatMessage(this.command.getValue());
         }

         this.toggle();
      }
   }

   public boolean call118() {
      if (minecraftClient3.player.getHealth() < this.health.getCurrent() && this.modeSetting2.ConfigJsonUtil(0)) {
         return true;
      }

      for (PlayerEntity playerentity : minecraftClient3.world.getPlayers()) {
         if (!ZenithClient.on23().MediaTrackInfo().UiAnimation(playerentity)
            && playerentity != minecraftClient3.player
            && (
               !this.modeSetting2.ConfigJsonUtil(2)
                  || !(playerentity.squaredDistanceTo(minecraftClient3.player) > this.radius.getCurrent() * this.radius.getCurrent())
            )) {
            return true;
         }
      }

      return false;
   }
}
