package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.command.CommandSource;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.client.BotClientConfig;
import org.zenith.base.bot.client.HeadlessBots;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.core.StyledTextBuilder;

public class HBotCommand extends CommandAbstract {
   public HBotCommand() {
      super("hbot");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(
         literal("connect")
            .then(
               ((RequiredArgumentBuilder)arg("name", StringArgumentType.word())
                     .executes(var0 -> connect(StringArgumentType.getString(var0, "name"), null, null)))
                  .then(
                     ((RequiredArgumentBuilder)arg("address", StringArgumentType.word())
                           .executes(var0 -> connect(StringArgumentType.getString(var0, "name"), StringArgumentType.getString(var0, "address"), null)))
                        .then(
                           arg("proxy", StringArgumentType.greedyString())
                              .executes(
                                 var0 -> connect(
                                    StringArgumentType.getString(var0, "name"),
                                    StringArgumentType.getString(var0, "address"),
                                    StringArgumentType.getString(var0, "proxy")
                                 )
                              )
                        )
                  )
            )
      );
      var1.then(literal("disconnect").then(arg("name", StringArgumentType.word()).executes(var0 -> {
         String s = StringArgumentType.getString(var0, "name");
         if (s.equalsIgnoreCase("all")) {
            HeadlessBots.disconnectAll();
            StyledTextBuilder.RefreshCacheEvent("Все headless-боты отключены");
         } else if (HeadlessBots.disconnect(s)) {
            StyledTextBuilder.RefreshCacheEvent("Бот " + s + " отключён");
         } else {
            StyledTextBuilder.AimPolicyRotationStrategy("Бот " + s + " не найден");
         }

         return 1;
      })));
      var1.then(literal("list").executes(var0 -> {
         if (HeadlessBots.all().isEmpty()) {
            StyledTextBuilder.RefreshCacheEvent("Нет активных headless-ботов");
            return 1;
         }

         for (BotClient botclient : HeadlessBots.all()) {
            StyledTextBuilder.RefreshCacheEvent(botclient.getName() + " — " + botclient.getPhase());
         }

         return 1;
      }));
      var1.then(literal("say").then(arg("name", StringArgumentType.word()).then(arg("message", StringArgumentType.greedyString()).executes(var0 -> {
         BotClient botclient = requireBot(StringArgumentType.getString(var0, "name"));
         if (botclient == null) {
            return 1;
         }

         String s = StringArgumentType.getString(var0, "message");
         if (botclient.sendChat(s)) {
            StyledTextBuilder.RefreshCacheEvent("Бот " + botclient.getName() + " отправил: " + s);
         } else {
            StyledTextBuilder.AimPolicyRotationStrategy("Бот " + botclient.getName() + " ещё не в игре");
         }

         return 1;
      }))));
      var1.then(
         literal("module")
            .then(
               arg("name", StringArgumentType.word())
                  .then(arg("module", StringArgumentType.word()).then(arg("state", StringArgumentType.word()).executes(var0 -> {
                     BotClient botclient = requireBot(StringArgumentType.getString(var0, "name"));
                     if (botclient == null) {
                        return 1;
                     }

                     String s = StringArgumentType.getString(var0, "module");
                     boolean flag = StringArgumentType.getString(var0, "state").equalsIgnoreCase("on");
                     botclient.execute(() -> {
                        boolean flag1 = botclient.getModules().setEnabled(s, flag);
                        report(
                           flag1 ? "Бот " + botclient.getName() + ": модуль " + s + (flag ? " включен" : " выключен") : "Модуль " + s + " не найден",
                           flag1
                        );
                     });
                     return 1;
                  })))
            )
      );
      var1.then(literal("modules").then(arg("name", StringArgumentType.word()).executes(var0 -> {
         BotClient botclient = requireBot(StringArgumentType.getString(var0, "name"));
         if (botclient == null) {
            return 1;
         }

         for (BotModule botmodule : botclient.getModules().getModules()) {
            StyledTextBuilder.RefreshCacheEvent(botmodule.getName() + " — " + (botmodule.isEnabled() ? "вкл" : "выкл"));
         }

         return 1;
      })));
   }

   public static int connect(String var0, String var1, String var2) {
      String s = var1;
      if (var1 == null) {
         ServerInfo serverinfo = MinecraftClient.getInstance().getCurrentServerEntry();
         if (serverinfo == null) {
            StyledTextBuilder.AimPolicyRotationStrategy("Не подключён к серверу — укажи адрес: .hbot connect <ник> <host:port>");
            return 1;
         }

         s = serverinfo.address;
      }

      ServerAddress serveraddress = ServerAddress.parse(s);
      BotClient botclient = HeadlessBots.connect(
         BotClientConfig.offline(var0, serveraddress.getAddress(), serveraddress.getPort(), var2, HeadlessBots.getProtocolVersion(var0))
      );
      StyledTextBuilder.RefreshCacheEvent("Бот " + botclient.getName() + " подключается к " + s);
      return 1;
   }

   public static BotClient requireBot(String var0) {
      BotClient botclient = HeadlessBots.get(var0);
      if (botclient == null) {
         StyledTextBuilder.AimPolicyRotationStrategy("Бот " + var0 + " не найден");
      }

      return botclient;
   }

   public static void report(String var0, boolean var1) {
      MinecraftClient.getInstance().execute(() -> {
         if (var1) {
            StyledTextBuilder.RefreshCacheEvent(var0);
         } else {
            StyledTextBuilder.AimPolicyRotationStrategy(var0);
         }
      });
   }
}
