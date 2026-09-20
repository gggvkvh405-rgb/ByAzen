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
import net.minecraft.util.math.Vec3d;
import org.zenith.event.MovementInputEvent;
import org.zenith.util.CooldownTimer;

@ModuleInfo(name = "Grim Glide", category = Category.MOVEMENT, description = "")
public final class GrimGlide extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final GrimGlide grimGlide = new GrimGlide();
   public final CooldownTimer zClass06729 = new CooldownTimer();
   public int int101 = 0;

   @EventTarget
   public void SimpleItemBuilder(MovementInputEvent var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && minecraftClient3.player.isGliding()) {
         this.int101++;
         Vec3d vec3d = minecraftClient3.player.getEntityPos();
         float f = minecraftClient3.player.getYaw();
         double d0 = (minecraftClient3.player.age % 2 == 0 ? 0.087 : 0.09) - 0.0;
         double d1 = -Math.sin(Math.toRadians(f)) * d0;
         double d2 = Math.cos(Math.toRadians(f)) * d0;
         minecraftClient3.player.setPosition(vec3d.getX() + d1, vec3d.getY(), vec3d.getZ() + d2);
      }
   }

   @Override
   public void onDisable() {
      this.zClass06729.reset();
      this.int101 = 0;
      super.onDisable();
   }
}
