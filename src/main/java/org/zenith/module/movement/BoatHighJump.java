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
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.util.math.Box;
import org.zenith.event.EventTick;

@ModuleInfo(name = "BoatHighJump", category = Category.MOVEMENT, description = "HighJump bypass")
public final class BoatHighJump extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final BoatHighJump boatHighJump = new BoatHighJump();
   public boolean boolean35 = false;

   @EventTarget
   public void onUpdate(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null && minecraftClient3.player.getVehicle() == null) {
         Entity entity = this.call093();
         if (minecraftClient3.player.isOnGround()) {
            if (minecraftClient3.options.jumpKey.isPressed() && entity != null && !this.boolean35) {
               minecraftClient3.player
                  .setVelocity(minecraftClient3.player.getVelocity().x, 0.82, minecraftClient3.player.getVelocity().z);
               this.boolean35 = true;
            }
         } else {
            this.boolean35 = false;
         }
      }
   }

   public Entity call093() {
      Box box = minecraftClient3.player.getBoundingBox().expand(1.0);

      for (Entity entity : minecraftClient3.world.getEntities()) {
         if (entity instanceof BoatEntity boatentity && !boatentity.hasPassenger(minecraftClient3.player) && boatentity.getBoundingBox().intersects(box)) {
            return boatentity;
         }
      }

      return null;
   }

   @Override
   public void onDisable() {
      this.boolean35 = false;
      super.onDisable();
   }
}
