package org.zenith.event;

import net.minecraft.network.packet.Packet;

public class PacketSendEvent extends CancellableEvent {
   public final Packet<?> packet6;

   public Packet<?> ItemScroller() {
      return this.packet6;
   }

   public PacketSendEvent(Packet<?> var1) {
      this.packet6 = var1;
   }
}
