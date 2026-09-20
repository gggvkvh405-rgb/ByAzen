package org.zenith.core;

import project.weye.Session;

public class ClientSession {
   public static final String DISPLAY_CREDIT = "fix+ported+reade_sorce BY Lokets547";
   public static final String DISPLAY_CREDIT_FIRST_LINE = "fix+ported+reade_sorce";
   public static final String DISPLAY_CREDIT_SECOND_LINE = "BY Lokets547";
   public final String CancellableEvent;
   public final String Event08;
   public final String BotChatEvent;
   public final SessionFlag BotDisconnectEvent;

   public ClientSession() {
      String s = DISPLAY_CREDIT;
      String s1 = "1";
      String s2 = "01.01.2048";
      if (System.getProperty("DevMode") == null) {
         try {
            Session.loadWEye();
            s = Session.getUsername();
            s1 = Session.getUid();
            s2 = Session.getExpirationDate();
            if (s == null || s.isBlank()) {
               s = DISPLAY_CREDIT;
            }
         } catch (Throwable var5) {
         }
      }

      this.BotDisconnectEvent = SessionFlag.BotPacketEvent;
      this.CancellableEvent = s;
      this.Event08 = s1;
      this.BotChatEvent = s2;
   }

   public String getUsername() {
      return this.CancellableEvent;
   }

   public String CloudPoller() {
      return this.Event08;
   }

   public String EmoteMetadata() {
      return this.BotChatEvent;
   }

   public SessionFlag EmoteManager() {
      return this.BotDisconnectEvent;
   }
}
