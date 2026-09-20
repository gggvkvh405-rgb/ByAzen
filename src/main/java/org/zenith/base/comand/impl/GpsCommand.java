package org.zenith.base.comand.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.core.StyledTextBuilder;

public class GpsCommand extends CommandAbstract {
   public GpsCommand() {
      super("gps");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      var1.executes(var0 -> {
         StyledTextBuilder.RefreshCacheEvent("Используй .way");
         return 1;
      });
      var1.then(literal("help").executes(var0 -> {
         StyledTextBuilder.RefreshCacheEvent("Используй .way");
         return 1;
      }));
   }
}
