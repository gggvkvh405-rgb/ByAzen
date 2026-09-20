package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.text.Text;
import org.zenith.base.bot.client.BotClient;

public class BotDisconnectEvent implements Event {
   public final BotClient botClient4;
   public final Text text2;

   public BotClient getBot() {
      return this.botClient4;
   }

   public Text ItemDebug() {
      return this.text2;
   }

   public BotDisconnectEvent(BotClient var1, Text var2) {
      this.botClient4 = var1;
      this.text2 = var2;
   }
}
