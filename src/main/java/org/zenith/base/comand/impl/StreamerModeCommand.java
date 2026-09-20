package org.zenith.base.comand.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.zenith.base.comand.api.CommandAbstract;
import org.zenith.core.StyledTextBuilder;
import org.zenith.module.misc.StreamerMode;

public class StreamerModeCommand extends CommandAbstract {
   public StreamerModeCommand() {
      super("streamermode");
   }

   @Override
   public void execute(LiteralArgumentBuilder<CommandSource> var1) {
      for (String s : new String[]{"parseAnarchy", "parseanarchy"}) {
         var1.then(literal(s).then(arg("anarchy", IntegerArgumentType.integer(1, 999)).executes(var0 -> {
            StreamerMode.streamerMode.VelocityChangeEvent(IntegerArgumentType.getInteger(var0, "anarchy"));
            return 1;
         })));
      }

      var1.then(literal("clear").executes(var0 -> {
         StreamerMode.streamerMode.call180();
         return 1;
      }));
      var1.executes(var0 -> {
         StyledTextBuilder.RefreshCacheEvent("Использование: .streamermode parseAnarchy <номер анки> и .streamermode clear");
         return 1;
      });
   }
}
