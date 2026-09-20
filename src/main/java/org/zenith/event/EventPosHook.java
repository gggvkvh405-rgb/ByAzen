package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.util.math.Vec3d;

public class EventPosHook implements Event {
   public Vec3d TriggerBot;

   public Vec3d WallBypass() {
      return this.TriggerBot;
   }

   public void UiAnimation(Vec3d var1) {
      this.TriggerBot = var1;
   }

   public EventPosHook(Vec3d var1) {
      this.TriggerBot = var1;
   }
}
