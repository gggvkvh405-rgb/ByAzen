package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.network.packet.Packet;
import org.zenith.base.bot.client.BotClient;

public class BotPacketEvent implements Event {
   public final Packet<?> packet;
   public final BotClient botClient5;

   public BotClient getBot() {
      return this.botClient5;
   }

   public Packet<?> ItemScroller() {
      return this.packet;
   }

   public BotPacketEvent(BotClient var1, Packet<?> var2) {
      this.botClient5 = var1;
      this.packet = var2;
   }
}
