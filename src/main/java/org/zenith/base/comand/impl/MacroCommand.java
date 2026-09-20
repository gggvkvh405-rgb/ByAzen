package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import org.zenith.ZenithClient;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.core.StyledTextBuilder;
import org.zenith.util.MacroManager;
import org.zenith.util.MacroManager;
import org.zenith.util.ScoreboardUtils;

public class MacroCommand extends CommandAbstract {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final SuggestionProvider<CommandSource> SUGGEST_KEYS = (var0, var1) -> {
      for (ScoreboardUtils i1lil1l1lllilll1lili1l1lil1i1 : ScoreboardUtils.values()) {
         if (!i1lil1l1lllilll1lili1l1lil1i1.string113.isEmpty()) {
            var1.suggest(i1lil1l1lllilll1lili1l1lil1i1.string113);
         }
      }

      return var1.buildFuture();
   };

   public MacroCommand() {
      super("macro");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(
         literal("add")
            .then(
               arg("name", StringArgumentType.word())
                  .then(
                     arg("key", StringArgumentType.word())
                        .suggests(this.SUGGEST_KEYS)
                        .then(arg("command", StringArgumentType.greedyString()).executes(var1x -> {
                           String s = (String)var1x.getArgument("name", String.class);
                           String s1 = (String)var1x.getArgument("key", String.class);
                           String s2 = (String)var1x.getArgument("command", String.class);
                           this.handleAddMacro(s, s1, s2);
                           return 1;
                        }))
                  )
            )
      );
      var1.then(literal("remove").then(arg("name", StringArgumentType.word()).executes(var0 -> {
         String s = (String)var0.getArgument("name", String.class);
         MacroManager.ItemDescriptor ilii1111lllilllilllii_ii1il11l111ii11iil = ZenithClient.on23().InventoryUtils().BotTickEvent(s);
         if (ilii1111lllilllilllii_ii1il11l111ii11iil == null) {
            StyledTextBuilder.RotationLegitStrategy("Макрос не найден!");
            return 1;
         } else {
            ZenithClient.on23().InventoryUtils().BotRespawnEvent(s);
            StyledTextBuilder.RefreshCacheEvent(s + " удален");
            return 1;
         }
      })));
      var1.then(literal("clear").executes(var0 -> {
         ZenithClient.on23().InventoryUtils().clear();
         StyledTextBuilder.RefreshCacheEvent("Все макросы удалены");
         return 1;
      }));
      var1.then(literal("list").executes(var0 -> {
         if (!ZenithClient.on23().InventoryUtils().getItems().isEmpty()) {
            for (MacroManager.ItemDescriptor ilii1111lllilllilllii_ii1il11l111ii11iil : ZenithClient.on23().InventoryUtils().getItems()) {
               minecraftClient3.player.sendMessage(ilii1111lllilllilllii_ii1il11l111ii11iil.toText(), false);
            }
         } else {
            StyledTextBuilder.RefreshCacheEvent("Список макросов пуст!");
         }

         return 1;
      }));
   }

   public void handleAddMacro(String var1, String var2, String var3) {
      MacroManager ilii1111lllilllilllii = ZenithClient.on23().InventoryUtils();
      int i = ScoreboardUtils.ServerTheme(var2);
      if (i == ScoreboardUtils.call065.int396) {
         StyledTextBuilder.RotationLegitStrategy("Неизвестная клавиша: " + var2);
      } else {
         MacroManager.ItemDescriptor ilii1111lllilllilllii_ii1il11l111ii11iil = ilii1111lllilllilllii.BotTickEvent(var1);
         if (ilii1111lllilllilllii_ii1il11l111ii11iil != null) {
            StyledTextBuilder.RotationLegitStrategy("Макрос с таким именем уже существует!");
            StyledTextBuilder.RefreshCacheEvent("Наведи мышку на сообщение ниже чтобы удалить его");
            minecraftClient3.player.sendMessage(ilii1111lllilllilllii_ii1il11l111ii11iil.toText(), false);
         } else {
            MacroManager.ItemDescriptor ilii1111lllilllilllii_ii1il11l111ii11iil1 = new MacroManager.ItemDescriptor(var1, i, var3);
            ilii1111lllilllilllii.on23(ilii1111lllilllilllii_ii1il11l111ii11iil1);
            minecraftClient3.player.sendMessage(ilii1111lllilllilllii_ii1il11l111ii11iil1.toText(), false);
            StyledTextBuilder.RefreshCacheEvent("Макрос добавлен");
         }
      }
   }
}
