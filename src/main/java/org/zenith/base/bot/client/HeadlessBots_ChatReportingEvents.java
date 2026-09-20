package org.zenith.base.bot.client;

import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.zenith.core.StyledTextBuilder;
import org.zenith.core.TextAccent;

final class HeadlessBots_ChatReportingEvents implements BotClientEvents {
   public HeadlessBots_ChatReportingEvents() {
   }

   @Override
   public void onPhaseChanged(BotClient var1, BotPhase var2, BotPhase var3) {
      info("[бот " + var1.getName() + "] фаза: " + var3);
   }

   @Override
   public void onJoined(BotClient var1) {
      info("[бот " + var1.getName() + "] зашёл в мир");
   }

   @Override
   public void onDisconnected(BotClient var1, Text var2) {
      HeadlessBots.BOTS.remove(var1.getName().toLowerCase(Locale.ROOT), var1);
      info("[бот " + var1.getName() + "] отключён: " + var2.getString());
   }

   public static void info(String var0) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient != null) {
         minecraftclient.execute(() -> StyledTextBuilder.on23(TextAccent.call002, var0));
      }
   }
}
