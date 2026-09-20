package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.util.math.Vec3d;

public final class MovementSpeedEvent implements Event {
   private final float speed;
   private final Vec3d velocity;

   public MovementSpeedEvent(float speed, Vec3d velocity) {
      this.speed = speed;
      this.velocity = velocity;
   }

   public float speed() {
      return this.speed;
   }

   public Vec3d velocity() {
      return this.velocity;
   }
}
