package org.zenith.event;

import net.minecraft.network.packet.Packet;

public class PacketEvent extends CancellableEvent {
   public final PacketEvent.Direction var128Var159;
   public Packet<?> packet;

   public boolean AntiInvisible() {
      return this.BetterMinecraft() == PacketEvent.Direction.val450;
   }

   public boolean Arrows() {
      return this.BetterMinecraft() == PacketEvent.Direction.val451;
   }

   public PacketEvent.Direction BetterMinecraft() {
      return this.var128Var159;
   }

   public Packet<?> ItemScroller() {
      return this.packet;
   }

   public void on23(Packet<?> var1) {
      this.packet = var1;
   }

   public PacketEvent(PacketEvent.Direction var1, Packet<?> var2) {
      this.var128Var159 = var1;
      this.packet = var2;
   }


   public enum Direction {
      val450,
      val451;
   }
}
