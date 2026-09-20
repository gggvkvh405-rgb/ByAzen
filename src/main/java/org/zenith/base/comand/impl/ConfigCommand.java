package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.io.IOException;
import java.util.List;
import net.minecraft.command.CommandSource;
import org.zenith.ZenithClient;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.CfgArgumentType;
import org.zenith.core.CloudPoller;
import org.zenith.core.StyledTextBuilder;
import org.zenith.core.TextAccent;

public class ConfigCommand extends CommandAbstract {
   public ConfigCommand() {
      super("cfg");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(((LiteralArgumentBuilder)literal("save").executes(var0 -> {
         boolean flag = ZenithClient.on23().TradeGuardService().ModuleStateStore("current_config");
         if (flag) {
            StyledTextBuilder.on23(TextAccent.call002, "Конфигурация сохранена");
         } else {
            StyledTextBuilder.on23(TextAccent.call013, "Ошибка при сохранении конфигурации");
         }

         return 1;
      })).then(arg("name", CfgArgumentType.create()).executes(var0 -> {
         boolean flag = ZenithClient.on23().TradeGuardService().ModuleStateStore((String)var0.getArgument("name", String.class));
         if (flag) {
            StyledTextBuilder.on23(TextAccent.call002, "Конфигурация сохранена");
         } else {
            StyledTextBuilder.on23(TextAccent.call013, "Ошибка при сохранении конфигурации");
         }

         return 1;
      })));
      var1.then(literal("list").executes(var0 -> {
         List<String> list = ZenithClient.on23().TradeGuardService().AutoAuth();
         if (list.isEmpty()) {
            StyledTextBuilder.RefreshCacheEvent("Список конфигов пуст!");
         } else {
            list.forEach(StyledTextBuilder::RefreshCacheEvent);
         }

         return 1;
      }));
      var1.then(((LiteralArgumentBuilder)literal("load").executes(var0 -> {
         boolean flag = ZenithClient.on23().TradeGuardService().BotFeaturesDto("current_config");
         if (flag) {
            StyledTextBuilder.on23(TextAccent.call002, "Конфигурация загружена");
         } else {
            StyledTextBuilder.on23(TextAccent.call013, "Ошибка при загрузке конфигурации");
         }

         return 1;
      })).then(arg("name", CfgArgumentType.create()).executes(var0 -> {
         boolean flag = ZenithClient.on23().TradeGuardService().BotFeaturesDto((String)var0.getArgument("name", String.class));
         if (flag) {
            StyledTextBuilder.on23(TextAccent.call002, "Конфигурация загружена");
         } else {
            StyledTextBuilder.on23(TextAccent.call013, "Ошибка при загрузке конфигурации");
         }

         return 1;
      })));
      var1.then(literal("help").executes(var0 -> {
         StyledTextBuilder.on23(TextAccent.call002, "Использование: .config <list/save/load/help>");
         StyledTextBuilder.on23(TextAccent.call002, "Команды:");
         StyledTextBuilder.on23(TextAccent.call002, ".config save - сохранить конфигурацию");
         StyledTextBuilder.on23(TextAccent.call002, ".config load - загрузить конфигурацию");
         return 1;
      }));
      var1.then(literal("dir").executes(var0 -> {
         try {
            new ProcessBuilder("explorer", CloudPoller.file7.getAbsolutePath()).start();
         } catch (IOException var2) {
         }

         return 1;
      }));
   }
}
