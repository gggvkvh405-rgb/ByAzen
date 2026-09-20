package org.zenith.module.movement;

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
import org.zenith.event.AttackEntityEvent;
import org.zenith.event.EventTick;
import org.zenith.setting.BooleanSetting;

@ModuleInfo(name = "AutoSprint", category = Category.MOVEMENT, description = "Автоматически включает спринт")
public final class AutoSprint extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoSprint autoSprint = new AutoSprint();
   public final BooleanSetting keepSwing = new BooleanSetting("module.autoSprint.keepSwing", "module.autoSprint.keepSwing.desc", false);
   public boolean sprint = false;

   public boolean call070() {
      return this.keepSwing.isEnabled();
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @EventTarget
   public void onUpdate(EventTick var1) {
      minecraftClient3.options.sprintKey.setPressed(true);
      if (this.keepSwing.isEnabled() && minecraftClient3.player != null && minecraftClient3.player.isSubmergedInWater()) {
         minecraftClient3.player.setSwimming(true);
      }
   }

   @EventTarget
   public void Easing(AttackEntityEvent var1) {
      if (this.keepSwing.isEnabled()) {
         if (var1.ElytraTarget() == AttackEntityEvent.on23.call185) {
            this.sprint = minecraftClient3.player.isSprinting();
         } else if (this.sprint) {
            minecraftClient3.player.setSprinting(true);
         }
      }
   }

   public double call157() {
      return 1.0;
   }
}
