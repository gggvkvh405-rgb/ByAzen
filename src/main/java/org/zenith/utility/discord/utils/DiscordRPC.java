package org.zenith.utility.discord.utils;

import com.sun.jna.Library;

public interface DiscordRPC extends Library {
   DiscordRPC INSTANCE = DiscordRPC_Loader.load();
   boolean AVAILABLE = DiscordRPC_Loader.available;

   void Discord_UpdateHandlers(DiscordEventHandlers var1);

   void Discord_UpdatePresence(DiscordRichPresence var1);

   void Discord_Respond(String var1, int var2);

   void Discord_Register(String var1, String var2);

   void Discord_Shutdown();

   void Discord_UpdateConnection();

   void Discord_RegisterSteamGame(String var1, String var2);

   void Discord_RunCallbacks();

   void Discord_Initialize(String var1, DiscordEventHandlers var2, boolean var3, String var4);

   void Discord_ClearPresence();
}
