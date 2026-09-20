package org.zenith.event;

import com.darkmagician6.eventapi.events.Event;
import net.minecraft.text.Text;
import org.zenith.base.bot.client.BotClient;

public class BotChatEvent implements Event {
   public final BotClient botClient;
   public final Text text;

   public BotClient getBot() {
      return this.botClient;
   }

   public Text InventorySetting() {
      return this.text;
   }

   public BotChatEvent(BotClient var1, Text var2) {
      this.botClient = var1;
      this.text = var2;
   }
}
