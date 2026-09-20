package org.zenith.core;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.Packet;

@Deprecated
public class PacketDispatcher {
   public static final List<Packet<?>> list94 = new ArrayList<>();
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static void SimpleItemBuilder(Packet<?> var0) {
      list94.add(var0);

      try {
         minecraftClient3.getNetworkHandler().sendPacket(var0);
      } finally {
         list94.remove(var0);
      }
   }

   public static void sendPacket(Packet<?> var0) {
      minecraftClient3.getNetworkHandler().sendPacket(var0);
   }

   public static List<Packet<?>> float130() {
      return list94;
   }

   public static void float131() {
      list94.clear();
   }
}
