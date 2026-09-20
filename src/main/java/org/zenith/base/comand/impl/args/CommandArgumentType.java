package org.zenith.base.comand.impl.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;

public class CommandArgumentType implements ArgumentType<String> {
   public static final Collection<String> EXAMPLES = List.of("/home", "/events", "/pvp", "/call 1WantToFreak");

   public static CommandArgumentType create() {
      return new CommandArgumentType();
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      StringBuilder stringbuilder = new StringBuilder();

      while (var1.canRead()) {
         stringbuilder.append(var1.read());
      }

      return stringbuilder.toString();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return CommandSource.suggestMatching(EXAMPLES, var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
