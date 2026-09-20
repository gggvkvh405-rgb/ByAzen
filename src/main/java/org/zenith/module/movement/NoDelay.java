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
import java.util.List;
import java.util.Objects;
import net.minecraft.client.MinecraftClient;
import org.zenith.event.EventTick;
import org.zenith.setting.MultiSelectSetting;

@ModuleInfo(name = "NoDelay", description = "", category = Category.MOVEMENT)
public final class NoDelay extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final MultiSelectSetting modeSetting11 = MultiSelectSetting.on23(
      "module.noDelay.ignoreSetting",
      "module.noDelay.ignoreSetting.desc",
      List.of("module.noDelay.ignoreSetting.jump", "module.noDelay.ignoreSetting.use", "module.noDelay.ignoreSetting.break")
   );
   public static final NoDelay noDelay = new NoDelay();

   @EventTarget
   public void UiAnimation(EventTick var1) {
      if (this.modeSetting11.ConfigJsonUtil(2)) {
         minecraftClient3.interactionManager.blockBreakingCooldown = 0;
      }

      if (this.modeSetting11.ConfigJsonUtil(0)) {
         Objects.requireNonNull(minecraftClient3.player).jumpingCooldown = 0;
      }

      if (this.modeSetting11.ConfigJsonUtil(1)) {
         minecraftClient3.itemUseCooldown = 0;
      }
   }
}
