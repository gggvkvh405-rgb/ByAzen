package org.zenith.event;

import net.minecraft.network.packet.Packet;

public class PacketReceiveEvent extends CancellableEvent {
   public Packet<?> packet;

   public Packet<?> ItemScroller() {
      return this.packet;
   }

   public void on23(Packet<?> var1) {
      this.packet = var1;
   }

   public PacketReceiveEvent(Packet<?> var1) {
      this.packet = var1;
   }
}
