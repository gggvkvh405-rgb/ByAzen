package org.zenith.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.zenith.ZenithClient;

public class RemoteEventsPoller {
   public static final String string122 = "https://api.holyworld.me/v1/events";
   public static final String string123 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
   public static final int int425 = 5000;
   public final List<ChatTagParser> list105 = new CopyOnWriteArrayList<>();
   public final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

   public RemoteEventsPoller() {
      this.scheduledExecutorService.scheduleAtFixedRate(this::boolean163, 0L, 3L, TimeUnit.SECONDS);
   }

   public void boolean163() {
      if (!ZenithClient.on23().CloudApiClient().call003()) {
         this.list105.clear();
      } else {
         HttpURLConnection httpurlconnection = null;

         try {
            httpurlconnection = (HttpURLConnection)URI.create("https://api.holyworld.me/v1/events").toURL().openConnection();
            httpurlconnection.setRequestMethod("GET");
            httpurlconnection.setConnectTimeout(5000);
            httpurlconnection.setReadTimeout(5000);
            httpurlconnection.setRequestProperty("Accept", "application/json");
            httpurlconnection.setRequestProperty(
               "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
            );
            if (httpurlconnection.getResponseCode() == 200) {
               List list;
               try (BufferedInputStream bufferedinputstream = new BufferedInputStream(httpurlconnection.getInputStream())) {
                  list = this.Easing(bufferedinputstream);
               }

               this.list105.clear();
               this.list105.addAll(list);
               return;
            }
         } catch (Exception var13) {
            return;
         } finally {
            if (httpurlconnection != null) {
               httpurlconnection.disconnect();
            }
         }
      }
   }

   public List<ChatTagParser> Easing(InputStream var1) throws IOException {
      InputStreamReader inputstreamreader = new InputStreamReader(var1, StandardCharsets.UTF_8);

      JsonElement jsonelement;
      try {
         jsonelement = JsonParser.parseReader(inputstreamreader);
      } catch (Throwable throwable1) {
         try {
            inputstreamreader.close();
         } catch (Throwable throwable) {
            throwable1.addSuppressed(throwable);
         }

         throw throwable1;
      }

      inputstreamreader.close();
      if (!jsonelement.isJsonObject()) {
         throw new IOException("HolyWorld events response must be a JSON object");
      }

      TreeMap<Integer, JsonArray> treemap = new TreeMap<>();

      for (Entry<String, JsonElement> entry : jsonelement.getAsJsonObject().entrySet()) {
         int i = this.EventHookTickEvent(entry.getKey());
         if (i > 0 && entry.getValue().isJsonArray()) {
            treemap.put(i, entry.getValue().getAsJsonArray());
         }
      }

      LinkedHashMap<String, TreeSet<Integer>> linkedhashmap = new LinkedHashMap<>();

      for (Entry<Integer, JsonArray> entry1 : treemap.entrySet()) {
         for (JsonElement jsonelement1 : entry1.getValue()) {
            if (jsonelement1.isJsonObject()) {
               JsonObject jsonobject = jsonelement1.getAsJsonObject();
               JsonElement jsonelement2 = jsonobject.get("id");
               if (jsonelement2 != null && jsonelement2.isJsonPrimitive()) {
                  String s = jsonelement2.getAsString();
                  if (!s.isBlank()) {
                     linkedhashmap.computeIfAbsent(s, var0 -> new TreeSet<>()).add(entry1.getKey());
                  }
               }
            }
         }
      }

      List<ChatTagParser> arraylist = new ArrayList<>();

      for (Entry<String, TreeSet<Integer>> entry2 : linkedhashmap.entrySet()) {
         String s1 = this.on23(entry2.getValue());
         ChatTagParser lilli1lllliii1 = new ChatTagParser(entry2.getKey(), s1, 0L);
         if (lilli1lllliii1.float21()) {
            arraylist.add(lilli1lllliii1);
         }
      }

      return arraylist;
   }

   public int EventHookTickEvent(String var1) {
      int i = var1.lastIndexOf(95);
      if (i >= 0 && i != var1.length() - 1) {
         try {
            return Integer.parseInt(var1.substring(i + 1));
         } catch (NumberFormatException numberformatexception) {
            return -1;
         }
      } else {
         return -1;
      }
   }

   public String on23(TreeSet<Integer> var1) {
      StringBuilder stringbuilder = new StringBuilder();

      for (int i : var1) {
         if (!stringbuilder.isEmpty()) {
            stringbuilder.append(", ");
         }

         stringbuilder.append('#').append(i);
      }

      return stringbuilder.toString();
   }

   public List<ChatTagParser> getEvents() {
      return this.list105;
   }

   public void close() {
      this.scheduledExecutorService.shutdownNow();
      this.list105.clear();
   }
}
