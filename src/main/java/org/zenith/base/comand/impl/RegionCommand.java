package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.BlockPos;
import org.zenith.ZenithClient;
import org.zenith.base.comand.api.CommandAbstract;

public class RegionCommand extends CommandAbstract {
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

   public RegionCommand() {
      super("region");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.then(
         literal("pos1")
            .then(
               arg("x", IntegerArgumentType.integer())
                  .suggests(this.SUGGEST_X)
                  .then(
                     arg("y", IntegerArgumentType.integer())
                        .suggests(this.SUGGEST_Y)
                        .then(arg("z", IntegerArgumentType.integer()).suggests(this.SUGGEST_Z).executes(var0 -> {
                           int i = IntegerArgumentType.getInteger(var0, "x");
                           int j = IntegerArgumentType.getInteger(var0, "y");
                           int k = IntegerArgumentType.getInteger(var0, "z");
                           ZenithClient.on23().BotFeatureRegistry().on23(new BlockPos(i, j, k));
                           return 1;
                        }))
                  )
            )
      );
      var1.then(
         literal("pos2")
            .then(
               arg("x", IntegerArgumentType.integer())
                  .suggests(this.SUGGEST_X)
                  .then(
                     arg("y", IntegerArgumentType.integer())
                        .suggests(this.SUGGEST_Y)
                        .then(arg("z", IntegerArgumentType.integer()).suggests(this.SUGGEST_Z).executes(var0 -> {
                           int i = IntegerArgumentType.getInteger(var0, "x");
                           int j = IntegerArgumentType.getInteger(var0, "y");
                           int k = IntegerArgumentType.getInteger(var0, "z");
                           ZenithClient.on23().BotFeatureRegistry().UiAnimation(new BlockPos(i, j, k));
                           return 1;
                        }))
                  )
            )
      );
      var1.then(literal("clear").executes(var0 -> {
         ZenithClient.on23().BotFeatureRegistry().clear();
         return 1;
      }));
   }
}
