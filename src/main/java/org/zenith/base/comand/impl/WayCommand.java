package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.zenith.ZenithClient;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.base.comand.impl.args.WayArgumentType;
import org.zenith.base.filemanager.impl.way.Way;
import org.zenith.core.StyledTextBuilder;
import org.zenith.core.TextAccent;

public class WayCommand extends CommandAbstract {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public final SuggestionProvider<CommandSource> SUGGEST_X = (var0, var1) -> {
      ClientPlayerEntity clientplayerentity = MinecraftClient.getInstance().player;
      if (clientplayerentity != null) {
         BlockPos blockpos = clientplayerentity.getBlockPos();
         var1.suggest(blockpos.getX());
      }

      return var1.buildFuture();
   };
   public final SuggestionProvider<CommandSource> SUGGEST_Y = (var0, var1) -> {
      ClientPlayerEntity clientplayerentity = MinecraftClient.getInstance().player;
      if (clientplayerentity != null) {
         BlockPos blockpos = clientplayerentity.getBlockPos();
         var1.suggest(blockpos.getY());
      }

      return var1.buildFuture();
   };
   public final SuggestionProvider<CommandSource> SUGGEST_Z = (var0, var1) -> {
      ClientPlayerEntity clientplayerentity = MinecraftClient.getInstance().player;
      if (clientplayerentity != null) {
         BlockPos blockpos = clientplayerentity.getBlockPos();
         var1.suggest(blockpos.getZ());
      }

      return var1.buildFuture();
   };

   public WayCommand() {
      super("way");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(
         literal("add")
            .then(
               arg("name", StringArgumentType.word())
                  .then(
                     arg("x", IntegerArgumentType.integer())
                        .suggests(this.SUGGEST_X)
                        .then(
                           arg("y", IntegerArgumentType.integer())
                              .suggests(this.SUGGEST_Y)
                              .then(arg("z", IntegerArgumentType.integer()).suggests(this.SUGGEST_Z).executes(var1x -> {
                                 String s = (String)var1x.getArgument("name", String.class);
                                 int i = IntegerArgumentType.getInteger(var1x, "x");
                                 int j = IntegerArgumentType.getInteger(var1x, "y");
                                 int k = IntegerArgumentType.getInteger(var1x, "z");
                                 this.handleAddWay(s, i, j, k);
                                 return 1;
                              }))
                        )
                  )
            )
      );
      var1.then(literal("event").executes(var0 -> {
         ZenithClient.on23().ModuleSnapshotDto().startEventCapture();
         StyledTextBuilder.RefreshCacheEvent("Ожидание координат из следующих 2 сообщений чата");
         minecraftClient3.getNetworkHandler().sendChatCommand("event");
         return 1;
      }));
      var1.then(literal("clear").executes(var0 -> {
         ZenithClient.on23().ModuleSnapshotDto().clearList();
         return 1;
      }));
      var1.then(literal("remove").then(arg("name", WayArgumentType.create()).executes(var0 -> {
         String s = (String)var0.getArgument("name", String.class);
         ZenithClient.on23().ModuleSnapshotDto().deleteWay(s);
         StyledTextBuilder.on23(TextAccent.call002, s + " удален ");
         return 1;
      })));
      var1.then(literal("list").executes(var0 -> {
         if (!ZenithClient.on23().ModuleSnapshotDto().getItems().isEmpty()) {
            for (Way way : ZenithClient.on23().ModuleSnapshotDto().getItems()) {
               minecraftClient3.player.sendMessage(way.toText(), false);
            }
         } else {
            StyledTextBuilder.RefreshCacheEvent("Список пуст!");
         }

         return 1;
      }));
   }

   public void handleAddWay(String var1, int var2, int var3, int var4) {
      Way way = ZenithClient.on23().ModuleSnapshotDto().getWay(var1);
      if (way != null) {
         StyledTextBuilder.RotationLegitStrategy("Метка с таким именем уже есть в списке!");
         StyledTextBuilder.RefreshCacheEvent("Наведи мышку на сообщение ниже чтобы удалить ее");
         minecraftClient3.player.sendMessage(way.toText(), false);
      } else {
         String s = minecraftClient3.getNetworkHandler().getServerInfo() != null ? minecraftClient3.getNetworkHandler().getServerInfo().address : "VANILLA";
         Way way1 = new Way(var1, new BlockPos(var2, var3, var4), s);
         minecraftClient3.player.sendMessage(way1.toText(), false);
         ZenithClient.on23().ModuleSnapshotDto().addWay(way1);
      }
   }
}
