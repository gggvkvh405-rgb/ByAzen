package org.zenith.event;

import net.minecraft.util.math.Vec3d;

public class PlayerMoveEvent extends CancellableEvent {
   public Vec3d vec3d40;

   public PlayerMoveEvent(Vec3d var1) {
      this.vec3d40 = var1;
   }

   public Vec3d NoPush() {
      return this.vec3d40;
   }

   public void on23(Vec3d var1) {
      this.vec3d40 = var1;
   }
}
