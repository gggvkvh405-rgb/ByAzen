package org.zenith.utility.discord.utils;

import org.zenith.utility.discord.callbacks.DisconnectedCallback;
import org.zenith.utility.discord.callbacks.ErroredCallback;
import org.zenith.utility.discord.callbacks.JoinGameCallback;
import org.zenith.utility.discord.callbacks.JoinRequestCallback;
import org.zenith.utility.discord.callbacks.ReadyCallback;
import org.zenith.utility.discord.callbacks.SpectateGameCallback;

public class DiscordEventHandlers_Builder {
   public final DiscordEventHandlers handlers = new DiscordEventHandlers();

   public DiscordEventHandlers build() {
      return this.handlers;
   }

   public DiscordEventHandlers_Builder disconnected(DisconnectedCallback var1) {
      this.handlers.disconnected = var1;
      return this;
   }

   public DiscordEventHandlers_Builder errored(ErroredCallback var1) {
      this.handlers.errored = var1;
      return this;
   }

   public DiscordEventHandlers_Builder ready(ReadyCallback var1) {
      this.handlers.ready = var1;
      return this;
   }

   public DiscordEventHandlers_Builder joinRequest(JoinRequestCallback var1) {
      this.handlers.joinRequest = var1;
      return this;
   }

   public DiscordEventHandlers_Builder joinGame(JoinGameCallback var1) {
      this.handlers.joinGame = var1;
      return this;
   }

   public DiscordEventHandlers_Builder spectateGame(SpectateGameCallback var1) {
      this.handlers.spectateGame = var1;
      return this;
   }
}
