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
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.zenith.core.EffectEngine;
import org.zenith.event.EventTick;
import org.zenith.event.EventTriggerKeyEvent;
import org.zenith.event.PacketEvent;
import org.zenith.event.PlayerMoveEvent;
import org.zenith.setting.ModeSetting;
import org.zenith.setting.NumberSetting;
import org.zenith.util.CooldownTimer;
import org.zenith.util.ScreenUtils;

@ModuleInfo(name = "ElytraMotion", description = "", category = Category.MOVEMENT)
public final class ElytraMotion extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final ElytraMotion elytraMotion = new ElytraMotion();
   public final ModeSetting mode7 = new ModeSetting(
      "module.elytraMotion.mode", "module.elytraMotion.mode.desc", "module.elytraMotion.mode.distance", "module.elytraMotion.mode.smart"
   );
   public final NumberSetting distance5 = new NumberSetting(
      "module.elytraMotion.distance", 0.1F, 0.1F, 4.0F, 1.0F, "module.elytraMotion.distance.desc", "b", () -> this.mode7.is(0), null
   );
   public boolean boolean46 = false;
   public final CooldownTimer zClass06726 = new CooldownTimer();

   @Override
   public void onEnable() {
      this.boolean46 = false;
      super.onEnable();
   }

   @EventTarget
   public void ColorAnimator(EventTriggerKeyEvent var1) {
   }

   @EventTarget
   public void NbtItemSpec(EventTick var1) {
      if (!minecraftClient3.player.isGliding()) {
         this.boolean46 = false;
      } else if (this.call199()) {
         if (EffectEngine.double67() != null) {
            this.boolean46 = true;
         } else if (this.zClass06726.EventModifyMouseRotationInput(300L) && !this.boolean46) {
            ScreenUtils.on23(Items.FIREWORK_ROCKET, Hand.OFF_HAND);
            this.zClass06726.reset();
         }
      } else {
         this.boolean46 = false;
      }
   }

   public boolean call199() {
      Box box = ElytraTarget.elytraTarget.call084();
      if (box == null) {
         return false;
      } else {
         return this.mode7.is(0)
            ? minecraftClient3.player.squaredDistanceTo(box.getCenter()) <= this.distance5.getCurrent() * this.distance5.getCurrent()
            : minecraftClient3.player.getBoundingBox().intersects(ElytraTarget.elytraTarget.call084());
      }
   }

   @EventTarget
   public void UiAnimation(PlayerMoveEvent var1) {
      if (this.boolean46) {
         var1.on23(Vec3d.ZERO);
      }
   }

   @EventTarget
   public void ItemServiceBase(PacketEvent var1) {
   }

   public boolean call126() {
      return this.boolean46;
   }
}
