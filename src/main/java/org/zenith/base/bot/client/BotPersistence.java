package org.zenith.base.bot.client;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import org.zenith.ZenithClient;
import org.zenith.util.CryptoUtils;

public final class BotPersistence {
   public static final String FILE_NAME = "bots.zenith";
   public static final String KEY = "siMunids";

   public static JsonObject load() {
      File file1 = file();
      if (!file1.exists()) {
         return null;
      }

      try {
         String s = Files.readString(file1.toPath(), StandardCharsets.UTF_8).trim();
         if (s.isBlank()) {
            return null;
         }

         byte[] abyte = Base64.getDecoder().decode(s);
         byte[] abyte1 = CryptoUtils.UiAnimation(abyte, "siMunids");
         return JsonParser.parseString(new String(abyte1, StandardCharsets.UTF_8)).getAsJsonObject();
      } catch (Exception exception) {
         return null;
      }
   }

   public static void save(JsonObject var0) {
      if (var0 != null) {
         try {
            File file1 = file();
            File file2 = file1.getParentFile();
            if (file2 != null) {
               file2.mkdirs();
            }

            String s = new GsonBuilder().setPrettyPrinting().create().toJson(var0);
            String s1 = Base64.getEncoder().encodeToString(CryptoUtils.on23(s.getBytes(StandardCharsets.UTF_8), "siMunids"));
            Files.writeString(file1.toPath(), s1, StandardCharsets.UTF_8);
         } catch (Exception var5) {
         }
      }
   }

   public static File file() {
      return new File(ZenithClient.ColorAnimator, "bots.zenith");
   }
}
