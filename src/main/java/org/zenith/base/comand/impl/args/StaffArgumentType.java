package org.zenith.base.comand.impl.args;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.zenith.ZenithClient;

public class StaffArgumentType implements ArgumentType<String> {
   public static final List<String> EXAMPLES = List.of();

   public static StaffArgumentType create() {
      return new StaffArgumentType();
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      String s = var1.readString();
      if (!ZenithClient.on23().CloudUserProfile().CrosshairTargetUpdateEvent(s)) {
         throw new DynamicCommandExceptionType(var1x -> Text.literal("Нету в стафе " + s)).create(s);
      } else {
         return s;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return CommandSource.suggestMatching(ZenithClient.on23().CloudUserProfile().getItems(), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
