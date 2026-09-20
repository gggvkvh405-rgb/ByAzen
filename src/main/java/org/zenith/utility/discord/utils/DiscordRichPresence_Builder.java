package org.zenith.utility.discord.utils;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DiscordRichPresence_Builder {
   public final DiscordRichPresence essencePresence = new DiscordRichPresence();

   public DiscordRichPresence_Builder setSmallImage(String var1) {
      return this.setSmallImage(var1, "");
   }

   public DiscordRichPresence_Builder setState(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.essencePresence.state = var1.substring(0, Math.min(var1.length(), 128));
      }

      return this;
   }

   public DiscordRichPresence_Builder setDetails(String var1) {
      if (var1 != null && !var1.isEmpty()) {
         this.essencePresence.details = var1.substring(0, Math.min(var1.length(), 128));
      }

      return this;
   }

   public DiscordRichPresence_Builder setLargeImage(String var1, String var2) {
      this.essencePresence.largeImageKey = var1;
      this.essencePresence.largeImageText = var2;
      return this;
   }

   public DiscordRichPresence_Builder setInstance(boolean var1) {
      if ((this.essencePresence.button_label_1 == null || !this.essencePresence.button_label_1.isEmpty())
         && (this.essencePresence.button_label_2 == null || !this.essencePresence.button_label_2.isEmpty())) {
         this.essencePresence.instance = var1 ? 1 : 0;
      }

      return this;
   }

   public DiscordRichPresence_Builder setButtons(RPCButton var1) {
      return this.setButtons(Collections.singletonList(var1));
   }

   public DiscordRichPresence_Builder setSmallImage(String var1, String var2) {
      this.essencePresence.smallImageKey = var1;
      this.essencePresence.smallImageText = var2;
      return this;
   }

   public DiscordRichPresence_Builder setButtons(List<RPCButton> var1) {
      if (var1 != null && !var1.isEmpty()) {
         int i = Math.min(var1.size(), 2);
         this.essencePresence.button_label_1 = var1.get(0).getLabel();
         this.essencePresence.button_url_1 = var1.get(0).getUrl();
         if (i == 2) {
            this.essencePresence.button_label_2 = var1.get(1).getLabel();
            this.essencePresence.button_url_2 = var1.get(1).getUrl();
         }
      }

      return this;
   }

   public DiscordRichPresence_Builder setStartTimestamp(OffsetDateTime var1) {
      this.essencePresence.startTimestamp = var1.toEpochSecond();
      return this;
   }

   public DiscordRichPresence_Builder setSecrets(String var1, String var2, String var3) {
      if ((this.essencePresence.button_label_1 == null || !this.essencePresence.button_label_1.isEmpty())
         && (this.essencePresence.button_label_2 == null || !this.essencePresence.button_label_2.isEmpty())) {
         this.essencePresence.matchSecret = var1;
         this.essencePresence.joinSecret = var2;
         this.essencePresence.spectateSecret = var3;
      }

      return this;
   }

   public DiscordRichPresence_Builder setButtons(RPCButton var1, RPCButton var2) {
      this.setButtons(Arrays.asList(var1, var2));
      return this;
   }

   public DiscordRichPresence_Builder setStartTimestamp(long var1) {
      this.essencePresence.startTimestamp = var1;
      return this;
   }

   public DiscordRichPresence_Builder setSecrets(String var1, String var2) {
      if ((this.essencePresence.button_label_1 == null || !this.essencePresence.button_label_1.isEmpty())
         && (this.essencePresence.button_label_2 == null || !this.essencePresence.button_label_2.isEmpty())) {
         this.essencePresence.joinSecret = var1;
         this.essencePresence.spectateSecret = var2;
      }

      return this;
   }

   public DiscordRichPresence_Builder setEndTimestamp(long var1) {
      this.essencePresence.endTimestamp = var1;
      return this;
   }

   public DiscordRichPresence_Builder setEndTimestamp(OffsetDateTime var1) {
      this.essencePresence.endTimestamp = var1.toEpochSecond();
      return this;
   }

   public DiscordRichPresence_Builder setLargeImage(String var1) {
      return this.setLargeImage(var1, "");
   }

   public DiscordRichPresence build() {
      return this.essencePresence;
   }
}
