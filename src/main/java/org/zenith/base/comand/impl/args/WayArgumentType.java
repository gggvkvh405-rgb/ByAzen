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
import org.zenith.base.filemanager.impl.way.Way;

public class WayArgumentType implements ArgumentType<String> {
   public static final List<String> EXAMPLES = List.of();

   public static WayArgumentType create() {
      return new WayArgumentType();
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      String s = var1.readString();
      if (!ZenithClient.on23().ModuleSnapshotDto().hasWay(s)) {
         throw new DynamicCommandExceptionType(var1x -> Text.literal("У тебя нет метки " + s)).create(s);
      } else {
         return s;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return CommandSource.suggestMatching(ZenithClient.on23().ModuleSnapshotDto().getItems().stream().map(Way::name), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
