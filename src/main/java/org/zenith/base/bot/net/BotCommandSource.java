package org.zenith.base.bot.net;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.command.permission.PermissionPredicate;
import net.minecraft.network.packet.c2s.play.RequestCommandCompletionsC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatSuggestionsS2CPacket.Action;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.zenith.base.bot.world.BotPlayer;

public final class BotCommandSource implements CommandSource {
   public final BotPlayHandler handler;
   public int completionId = -1;
   public CompletableFuture<Suggestions> pendingCommandCompletion;
   public final Set<String> chatSuggestions = new HashSet<>();

   BotCommandSource(BotPlayHandler var1) {
      this.handler = var1;
   }

   public Collection<String> getPlayerNames() {
      Collection<String> arraylist = new ArrayList<>();

      for (PlayerListEntry playerlistentry : this.handler.getPlayerList()) {
         arraylist.add(playerlistentry.getProfile().name());
      }

      return arraylist;
   }

   public synchronized Collection<String> getChatSuggestions() {
      if (this.chatSuggestions.isEmpty()) {
         return this.getPlayerNames();
      }

      HashSet hashset = new HashSet<>(this.getPlayerNames());
      hashset.addAll(this.chatSuggestions);
      return hashset;
   }

   public Collection<String> getTeamNames() {
      return this.handler.getScoreboard().getTeamNames();
   }

   public Stream<Identifier> getSoundIds() {
      return Registries.SOUND_EVENT.getIds().stream();
   }

   public boolean hasPermissionLevel(int level) {
      BotPlayer botplayer = this.handler.getPlayer();
      return botplayer != null ? botplayer.getPermissionLevel() >= level : level == 0;
   }

   @Override
   public PermissionPredicate getPermissions() {
      BotPlayer player = this.handler.getPlayer();
      return player == null ? PermissionPredicate.NONE : player.getPermissions();
   }

   public CompletableFuture<Suggestions> listIdSuggestions(
      RegistryKey<? extends Registry<?>> registryRef, SuggestedIdType suggestedIdType, SuggestionsBuilder builder, CommandContext<?> context
   ) {
      return this.getRegistryManager().getOptional(registryRef).map(var3 -> {
         this.suggestIdentifiers(var3, suggestedIdType, builder);
         return builder.buildFuture();
      }).orElseGet(() -> this.getCompletions(context));
   }

   public synchronized CompletableFuture<Suggestions> getCompletions(CommandContext<?> context) {
      if (this.pendingCommandCompletion != null) {
         this.pendingCommandCompletion.cancel(false);
      }

      CompletableFuture completablefuture = new CompletableFuture();
      this.pendingCommandCompletion = completablefuture;
      int i = ++this.completionId;
      this.handler.sendPacket(new RequestCommandCompletionsC2SPacket(i, context.getInput()));
      return completablefuture;
   }

   public Set<RegistryKey<World>> getWorldKeys() {
      return this.handler.getWorldKeys();
   }

   public DynamicRegistryManager getRegistryManager() {
      return this.handler.getRegistryManager();
   }

   public FeatureSet getEnabledFeatures() {
      return this.handler.getEnabledFeatures();
   }

   synchronized void onCommandSuggestions(int var1, Suggestions var2) {
      if (var1 == this.completionId && this.pendingCommandCompletion != null) {
         this.pendingCommandCompletion.complete(var2);
         this.pendingCommandCompletion = null;
         this.completionId = -1;
      }
   }

   synchronized void onChatSuggestions(Action var1, List<String> var2) {
      switch (var1) {
         case ADD:
            this.chatSuggestions.addAll(var2);
            break;
         case REMOVE:
            var2.forEach(this.chatSuggestions::remove);
            break;
         case SET:
            this.chatSuggestions.clear();
            this.chatSuggestions.addAll(var2);
      }
   }
}
