package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;

public class BotWorldJoinEvent implements Event {
   public final BotClient botClient6;
   public final BotWorld botWorld3;
   public final BotPlayer botPlayer3;

   public BotClient getBot() {
      return this.botClient6;
   }

   public BotWorld getWorld() {
      return this.botWorld3;
   }

   public BotPlayer getPlayer() {
      return this.botPlayer3;
   }

   public BotWorldJoinEvent(BotClient var1, BotWorld var2, BotPlayer var3) {
      this.botClient6 = var1;
      this.botWorld3 = var2;
      this.botPlayer3 = var3;
   }
}
