package org.zenith.utility.discord.utils;

import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
import org.zenith.utility.discord.callbacks.DisconnectedCallback;
import org.zenith.utility.discord.callbacks.ErroredCallback;
import org.zenith.utility.discord.callbacks.JoinGameCallback;
import org.zenith.utility.discord.callbacks.JoinRequestCallback;
import org.zenith.utility.discord.callbacks.ReadyCallback;
import org.zenith.utility.discord.callbacks.SpectateGameCallback;

public class DiscordEventHandlers extends Structure {
   public DisconnectedCallback disconnected;
   public JoinRequestCallback joinRequest;
   public SpectateGameCallback spectateGame;
   public ReadyCallback ready;
   public ErroredCallback errored;
   public JoinGameCallback joinGame;

   protected List<String> getFieldOrder() {
      return Arrays.asList("ready", "disconnected", "errored", "joinGame", "spectateGame", "joinRequest");
   }
}
