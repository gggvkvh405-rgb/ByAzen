package org.zenith.base.bot.world;

import net.minecraft.network.listener.ServerPlayPacketListener;
import net.minecraft.network.packet.Packet;

@FunctionalInterface
public interface BotInteractionManager_SequencedPacketCreator {
   Packet<ServerPlayPacketListener> predict(int var1);
}
