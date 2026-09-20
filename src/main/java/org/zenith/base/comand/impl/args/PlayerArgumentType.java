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
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import org.zenith.core.ClientProvider;
import org.zenith.module.misc.StreamerMode;

public class PlayerArgumentType implements ArgumentType<String>, ClientProvider {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final Collection<String> EXAMPLES = List.of();
   public final boolean displayedNames;

   public PlayerArgumentType(boolean var1) {
      this.displayedNames = var1;
   }

   public static PlayerArgumentType create() {
      return new PlayerArgumentType(false);
   }

   public static PlayerArgumentType createDisplayed() {
      return new PlayerArgumentType(true);
   }

   public String parse(StringReader var1) throws CommandSyntaxException {
      return var1.readUnquotedString();
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      Stream<String> stream = minecraftClient3.getNetworkHandler().getPlayerList().stream().map(var0 -> var0.getProfile().name());
      if (this.displayedNames && StreamerMode.streamerMode.call082() && minecraftClient3.player != null) {
         String s = minecraftClient3.player.getNameForScoreboard();
         stream = stream.filter(var1x -> !var1x.equals(s)).map(StreamerMode.streamerMode::LocaleEntry);
      }

      return CommandSource.suggestMatching(stream, var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
