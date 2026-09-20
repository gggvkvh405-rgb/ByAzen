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
import net.minecraft.util.Hand;
import org.zenith.core.EffectEngine;
import org.zenith.event.ItemUseEvent;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.ModeSetting;
@ModuleInfo(name = "NoSlow", category = Category.MOVEMENT, description = "Убирает замедление во время еды")
public final class NoSlow extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final NoSlow noSlow = new NoSlow();
   public final ModeSetting mode11 = new ModeSetting("module.noSlow.mode", "module.noSlow.mode.desc");
   public final ModeSetting.Option modeSetting3Var15939 = new ModeSetting.Option(this.mode11, "module.noSlow.mode.grimNew");
   public final ModeSetting.Option modeSetting3Var15940 = new ModeSetting.Option(this.mode11, "module.noSlow.mode.grimOld").int210();
   public final BooleanSetting sprint = new BooleanSetting("module.noSlow.sprint", "module.noSlow.sprint.desc", true, this.modeSetting3Var15940::isSelected);

   @EventTarget
   public void on23(ItemUseEvent var1) {
      if (this.modeSetting3Var15939.isSelected() && minecraftClient3.player.getItemUseTime() % 2 == 0) {
         var1.setCancelled(true);
      }

      if (this.modeSetting3Var15940.isSelected()) {
         Hand hand = minecraftClient3.player.getActiveHand();
         if (this.sprint.isEnabled()) {
            minecraftClient3.player
               .setSprinting(
                  (minecraftClient3.player.getHungerManager().getFoodLevel() > 6 || minecraftClient3.player.getAbilities().allowFlying)
                     && minecraftClient3.player.input.hasForwardMovement()
                     && !minecraftClient3.player.hasStatusEffect(net.minecraft.entity.effect.StatusEffects.BLINDNESS)
                     && !minecraftClient3.player.isGliding()
                     && (!minecraftClient3.player.shouldSlowDown() || minecraftClient3.player.isSubmergedInWater())
               );
         }

         EffectEngine.useItem(hand.equals(Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND);
         var1.setCancelled(true);
      }
   }
}
