package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;

public class BotTickEvent implements Event {
   public final BotClient botClient3;
   public final BotWorld botWorld2;
   public final BotPlayer botPlayer2;

   public BotClient getBot() {
      return this.botClient3;
   }

   public BotWorld getWorld() {
      return this.botWorld2;
   }

   public BotPlayer getPlayer() {
      return this.botPlayer2;
   }

   public BotTickEvent(BotClient var1, BotWorld var2, BotPlayer var3) {
      this.botClient3 = var1;
      this.botWorld2 = var2;
      this.botPlayer2 = var3;
   }
}
