package org.zenith.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import org.zenith.ZenithClient;
import org.zenith.util.CryptoUtils;

public class UsageStatStore {
   public static final String string127 = "protection.zenith";
   public static final String string128 = "playTimeZnth";
   public final File file6 = new File(ZenithClient.ColorAnimator, "protection.zenith");
   public final long long157 = System.currentTimeMillis();
   public long long158;
   public long long159;
   public String string129 = this.TotemPop();

   public UsageStatStore() {
      this.load();
   }

   public String TotemPop() {
      LocalDate localdate = LocalDate.now();
      WeekFields weekfields = WeekFields.of(Locale.getDefault());
      int i = localdate.get(weekfields.weekOfWeekBasedYear());
      int j = localdate.get(weekfields.weekBasedYear());
      return j + "-W" + i;
   }

   public void load() {
      if (!this.file6.exists()) {
         this.long158 = 0L;
         this.long159 = 0L;
      } else {
         try {
            try (BufferedReader bufferedreader = new BufferedReader(new FileReader(this.file6))) {
               String s = bufferedreader.readLine();
               if (s == null || s.isEmpty()) {
                  this.long158 = 0L;
                  this.long159 = 0L;
                  return;
               }

               String s1 = CryptoUtils.EmoteMetadata(s, "playTimeZnth");
               Gson gson = new Gson();
               JsonObject jsonobject = (JsonObject)gson.fromJson(s1, JsonObject.class);
               if (jsonobject == null) {
                  this.long158 = 0L;
                  this.long159 = 0L;
                  return;
               }

               this.long158 = jsonobject.has("totalSeconds") ? jsonobject.get("totalSeconds").getAsLong() : 0L;
               if (jsonobject.has("weeklyData")) {
                  JsonObject jsonobject1 = jsonobject.getAsJsonObject("weeklyData");
                  this.long159 = jsonobject1.has(this.string129) ? jsonobject1.get(this.string129).getAsLong() : 0L;
               }
            }

            return;
         } catch (Exception exception) {
            this.long158 = 0L;
            this.long159 = 0L;
         }
      }
   }

   public void save() {
      long i = this.Trails();
      long j = this.long158 + i;
      String s = this.TotemPop();
      long k;
      if (s.equals(this.string129)) {
         k = this.long159 + i;
      } else {
         k = i;
      }

      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("totalSeconds", j);
      JsonObject jsonobject1 = new JsonObject();
      jsonobject1.addProperty(s, k);
      jsonobject.add("weeklyData", jsonobject1);

      try {
         String s1 = new Gson().toJson(jsonobject);
         String s2 = CryptoUtils.CloudPoller(s1, "playTimeZnth");

         try (FileWriter filewriter = new FileWriter(this.file6)) {
            filewriter.write(s2);
         }
      } catch (Exception var17) {
      }
   }

   public long Trails() {
      return (System.currentTimeMillis() - this.long157) / 1000L;
   }

   public long ViewArmorDurability() {
      String s = this.TotemPop();
      return s.equals(this.string129) ? this.long159 + this.Trails() : this.Trails();
   }

   public long ViewModel() {
      return this.long158 + this.Trails();
   }

   public static String ItemRegistry(long var0) {
      long i = var0 / 3600L;
      long j = var0 % 3600L / 60L;
      return i > 0L ? i + "h " + j + "m" : j + "m";
   }
}
