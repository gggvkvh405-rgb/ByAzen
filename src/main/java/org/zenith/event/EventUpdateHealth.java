package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;

public class EventUpdateHealth implements Event {
   public final EntityStatusS2CPacket entityStatusS2CPacket;

   public EventUpdateHealth(EntityStatusS2CPacket var1) {
      this.entityStatusS2CPacket = var1;
   }

   public EntityStatusS2CPacket NoDelay() {
      return this.entityStatusS2CPacket;
   }
}
