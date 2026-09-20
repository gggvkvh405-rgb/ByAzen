package org.zenith.base.comand.api;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.command.CommandSource;
import org.zenith.core.GameService;

public abstract class CommandAbstract implements GameService {
   public final String command;

   protected CommandAbstract(String var1) {
      this.command = var1;
   }

   public void register(CommandDispatcher<CommandSource> var1) {
      LiteralArgumentBuilder literalargumentbuilder = LiteralArgumentBuilder.literal(this.command);
      this.execute(literalargumentbuilder);
      var1.register(literalargumentbuilder);
   }

   public abstract void execute(LiteralArgumentBuilder<CommandSource> var1);

   protected static <T> RequiredArgumentBuilder<CommandSource, T> arg(String var0, ArgumentType<T> var1) {
      return RequiredArgumentBuilder.argument(var0, var1);
   }

   protected static LiteralArgumentBuilder<CommandSource> literal(String var0) {
      return LiteralArgumentBuilder.literal(var0);
   }

   public String getCommand() {
      return this.command;
   }
}
