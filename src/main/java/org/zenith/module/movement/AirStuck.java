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
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import org.zenith.event.EventHookTickEvent;
import org.zenith.event.PacketEvent;
import org.zenith.event.PlayerMoveEvent;

@ModuleInfo(name = "AirStuck", description = "", category = Category.MOVEMENT)
public final class AirStuck extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AirStuck airStuck = new AirStuck();
   public Vec3d vec3d = Vec3d.ZERO;

   @EventTarget
   public void on23(PlayerMoveEvent var1) {
      var1.on23(Vec3d.ZERO);
   }

   @EventTarget
   public void on23(EventHookTickEvent var1) {
      if (minecraftClient3.player != null) {
         minecraftClient3.player.setVelocity(Vec3d.ZERO);
         minecraftClient3.player.setNoGravity(true);
      }
   }

   @EventTarget
   public void onPacket(PacketEvent var1) {
      if (minecraftClient3.player != null && var1.ItemScroller() instanceof PlayerMoveC2SPacket) {
         var1.setCancelled(true);
      }
   }

   @Override
   public void onEnable() {
      super.onEnable();
      if (minecraftClient3.player != null) {
         this.vec3d = minecraftClient3.player.getVelocity();
         minecraftClient3.player.setNoGravity(true);
      }
   }

   @Override
   public void onDisable() {
      super.onDisable();
      if (minecraftClient3.player != null) {
         if (!minecraftClient3.player.isOnGround() && this.vec3d != null) {
            minecraftClient3.player.setVelocity(this.vec3d);
         }

         minecraftClient3.player.setNoGravity(false);
      }
   }
}
