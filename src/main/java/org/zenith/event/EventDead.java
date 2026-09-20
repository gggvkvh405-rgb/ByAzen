package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.entity.LivingEntity;

public class EventDead implements Event {
   public final LivingEntity entity;

   public EventDead(LivingEntity var1) {
      this.entity = var1;
   }

   public LivingEntity entity() {
      return this.entity;
   }
}
