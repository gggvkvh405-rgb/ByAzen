package org.zenith.base.bot.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkingBackend;
import org.zenith.base.bot.modules.api.BotModule;
import org.zenith.base.bot.net.BotProxy;

public final class HeadlessBots {
   public static final long MASS_CONNECT_STAGGER_MS = 500L;
   public static final int MAX_MASS_CONNECT_BOTS = 256;
   public static final Map<String, BotClient> BOTS = new ConcurrentHashMap<>();
   public static final Map<String, HeadlessBots_SavedBot> SAVED = new LinkedHashMap<>();
   public static final Map<String, JsonObject> PROFILES = new ConcurrentHashMap<>();
   public static final ScheduledExecutorService CONNECT_DELAY_EXECUTOR = Executors.newSingleThreadScheduledExecutor(var0 -> {
      Thread thread = new Thread(var0, "hbot-connect-delay");
      thread.setDaemon(true);
      return thread;
   });
   public static final AtomicLong CONNECT_BATCH_GENERATION = new AtomicLong();
   private static volatile String lastAddress;
   public static final Set<String> SAVED_PROXIES = ConcurrentHashMap.newKeySet();
   public static final Map<String, HeadlessBots_ProxyPingState> PROXY_PINGS = new ConcurrentHashMap<>();
   public static final String PROXY_PING_TARGET_HOST = "mc.holyworld.ru";
   public static final int PROXY_PING_TARGET_PORT = 25565;
   public static final long PROXY_PING_INTERVAL_MS = 10000L;
   public static final long PROXY_PING_TIMEOUT_MS = 5000L;
   public static final int MAX_PENDING_PROXY_PINGS = 64;
   public static final ThreadPoolExecutor PROXY_PING_EXECUTOR = new ThreadPoolExecutor(
      1, 4, 10000L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(64), var0 -> {
         Thread thread = new Thread(var0, "hbot-connect-delay");
         thread.setDaemon(true);
         return thread;
      }
   );

   public static List<String> allNames() {
      List<String> arraylist = new ArrayList<>();
      synchronized (SAVED) {
         for (HeadlessBots_SavedBot headlessbots_savedbot : SAVED.values()) {
            arraylist.add(headlessbots_savedbot.name());
         }
      }

      for (BotClient botclient : BOTS.values()) {
         if (arraylist.stream().noneMatch(var1 -> var1.equalsIgnoreCase(botclient.getName()))) {
            arraylist.add(botclient.getName());
         }
      }

      return arraylist;
   }

   public static void setProxy(String var0, String var1) {
      String s = var1 != null && !var1.isBlank() ? var1.trim() : null;
      if (s != null) {
         addProxy(s);
      }

      synchronized (SAVED) {
         HeadlessBots_SavedBot headlessbots_savedbot = SAVED.get(key(var0));
         if (headlessbots_savedbot != null) {
            SAVED.put(key(var0), new HeadlessBots_SavedBot(headlessbots_savedbot.name(), s, headlessbots_savedbot.protocolVersion()));
         }
      }
   }

   public static List<String> getProxyPool() {
      ArrayList arraylist = new ArrayList();

      for (String s : SAVED_PROXIES) {
         addUniqueProxy(arraylist, s);
      }

      synchronized (SAVED) {
         for (HeadlessBots_SavedBot headlessbots_savedbot : SAVED.values()) {
            if (headlessbots_savedbot.proxy() != null) {
               addUniqueProxy(arraylist, headlessbots_savedbot.proxy());
            }
         }
      }

      return List.copyOf(arraylist);
   }

   public static boolean removeProxy(String var0) {
      String s = var0 == null ? "" : var0.trim();
      if (s.isBlank()) {
         return false;
      }

      boolean flag = SAVED_PROXIES.removeIf(var1x -> var1x.equalsIgnoreCase(s));
      synchronized (SAVED) {
         for (Entry<String, HeadlessBots_SavedBot> entry : SAVED.entrySet()) {
            HeadlessBots_SavedBot headlessbots_savedbot = entry.getValue();
            if (headlessbots_savedbot.proxy() != null && headlessbots_savedbot.proxy().equalsIgnoreCase(s)) {
               entry.setValue(new HeadlessBots_SavedBot(headlessbots_savedbot.name(), null, headlessbots_savedbot.protocolVersion()));
               flag = true;
            }
         }
      }

      PROXY_PINGS.remove(proxyPingKey(s));
      return flag;
   }

   public static int getProxyUseCount(String var0) {
      String s = var0 == null ? "" : var0.trim();
      if (s.isBlank()) {
         return 0;
      }

      int i = 0;
      synchronized (SAVED) {
         for (HeadlessBots_SavedBot headlessbots_savedbot : SAVED.values()) {
            if (headlessbots_savedbot.proxy() != null && headlessbots_savedbot.proxy().equalsIgnoreCase(s)) {
               i++;
            }
         }

         return i;
      }
   }

   public static HeadlessBots_ProxyPingState requestProxyPing(String var0, boolean var1) {
      String s = var0 == null ? "" : var0.trim();
      if (s.isBlank()) {
         return new HeadlessBots_ProxyPingState();
      }

      HeadlessBots_ProxyPingState headlessbots_proxypingstate = PROXY_PINGS.computeIfAbsent(proxyPingKey(s), var0x -> new HeadlessBots_ProxyPingState());
      long i = System.currentTimeMillis();
      synchronized (headlessbots_proxypingstate) {
         if (headlessbots_proxypingstate.inFlight()) {
            return headlessbots_proxypingstate;
         }

         if (!var1 && headlessbots_proxypingstate.status() != HeadlessBots_ProxyPingStatus.UNKNOWN && i - headlessbots_proxypingstate.lastStartedAt() < 10000L) {
            return headlessbots_proxypingstate;
         }

         headlessbots_proxypingstate.inFlight = true;
         if (headlessbots_proxypingstate.status() == HeadlessBots_ProxyPingStatus.UNKNOWN) {
            headlessbots_proxypingstate.status = HeadlessBots_ProxyPingStatus.CHECKING;
         }

         headlessbots_proxypingstate.lastStartedAt = i;
      }

      try {
         PROXY_PING_EXECUTOR.execute(() -> pingProxy(s, headlessbots_proxypingstate));
      } catch (RejectedExecutionException rejectedexecutionexception) {
         synchronized (headlessbots_proxypingstate) {
            headlessbots_proxypingstate.latencyMs = -1;
            headlessbots_proxypingstate.status = HeadlessBots_ProxyPingStatus.FAILED;
            headlessbots_proxypingstate.inFlight = false;
         }
      }

      return headlessbots_proxypingstate;
   }

   public static void pingProxy(String var0, HeadlessBots_ProxyPingState var1) {
      long i = System.nanoTime();

      try {
         BotProxy botproxy = BotProxy.parse(var0);
         if (botproxy == null) {
            throw new IllegalArgumentException("Invalid proxy");
         }

         ProxyHandler proxyhandler = botproxy.createHandler();
         InetSocketAddress inetsocketaddress = InetSocketAddress.createUnresolved("mc.holyworld.ru", 25565);
         NetworkingBackend backend = NetworkingBackend.remote(false);
         EventLoopGroup eventloopgroup = backend.getEventLoopGroup();
         Bootstrap bootstrap = (Bootstrap)((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(eventloopgroup)).channel(backend.getChannelClass()))
               .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000))
            .handler(new ProxyPingChannelInitializer(proxyhandler));
         ChannelFuture channelfuture = bootstrap.connect(inetsocketaddress);
         Channel channel = channelfuture.channel();

         try {
            if (!channelfuture.await(5000L)) {
               channelfuture.cancel(true);
               throw new SocketTimeoutException("Proxy connect timeout");
            }

            if (!channelfuture.isSuccess()) {
               throw asException(channelfuture.cause());
            }

            Future future = proxyhandler.connectFuture();
            if (!future.await(5000L)) {
               throw new SocketTimeoutException("Proxy handshake timeout");
            }

            if (!future.isSuccess()) {
               throw asException(future.cause());
            }
         } finally {
            channel.close();
         }

         int var23 = Math.max(1, Math.round((float)(System.nanoTime() - i) / 1000000.0F));
         synchronized (var1) {
            var1.latencyMs = var23;
            var1.status = HeadlessBots_ProxyPingStatus.OK;
            var1.inFlight = false;
         }
      } catch (Exception exception) {
         synchronized (var1) {
            var1.latencyMs = -1;
            var1.status = HeadlessBots_ProxyPingStatus.FAILED;
            var1.inFlight = false;
         }
      }
   }

   public static JsonObject saveState() {
      JsonObject jsonobject = new JsonObject();
      JsonArray jsonarray = new JsonArray();
      synchronized (SAVED) {
         for (HeadlessBots_SavedBot headlessbots_savedbot : SAVED.values()) {
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("name", headlessbots_savedbot.name());
            if (headlessbots_savedbot.proxy() != null) {
               jsonobject1.addProperty("proxy", headlessbots_savedbot.proxy());
            }

            if (headlessbots_savedbot.protocolVersion() > 0) {
               jsonobject1.addProperty("version", headlessbots_savedbot.protocolVersion());
            }

            jsonarray.add(jsonobject1);
         }
      }

      jsonobject.add("savedBots", jsonarray);
      JsonObject jsonobject2 = new JsonObject();

      for (Entry<String, JsonObject> entry : PROFILES.entrySet()) {
         jsonobject2.add(entry.getKey(), (JsonElement)entry.getValue());
      }

      jsonobject.add("botProfiles", jsonobject2);
      JsonArray jsonarray1 = new JsonArray();

      for (String s : getProxyPool()) {
         jsonarray1.add(s);
      }

      jsonobject.add("proxyPool", jsonarray1);
      jsonobject.addProperty("lastAddress", lastAddress);
      return jsonobject;
   }

   public static BotClient connect(String var0, String var1, int var2) {
      add(var0);
      setLastAddress(var1 + (var2 == 25565 ? "" : ":" + var2));
      return connect(BotClientConfig.offline(var0, var1, var2, getProxy(var0), getProtocolVersion(var0)));
   }

   public static BotClient connect(BotClientConfig var0) {
      String s = key(var0.name());
      BotClient botclient = BOTS.get(s);
      if (botclient != null && botclient.getPhase() != BotPhase.DISCONNECTED) {
         return botclient;
      }

      BotClient botclient1 = BotClient.start(var0, new HeadlessBots_ChatReportingEvents());
      BOTS.put(s, botclient1);
      applyProfile(botclient1);
      return botclient1;
   }

   public static void connectAll(Collection<String> var0, String var1, int var2) {
      if (var0 != null && !var0.isEmpty()) {
         long i = CONNECT_BATCH_GENERATION.incrementAndGet();
         ArrayList arraylist = new ArrayList();

         for (String s : var0) {
            if (arraylist.size() >= 256) {
               break;
            }

            if (s != null && !s.isBlank()) {
               arraylist.add(s.trim());
            }
         }

         if (!arraylist.isEmpty()) {
            connectMassBatch(arraylist, 0, var1, var2, i);
         }
      }
   }

   public static void connectMassBatch(List<String> var0, int var1, String var2, int var3, long var4) {
      if (CONNECT_BATCH_GENERATION.get() == var4 && var1 < var0.size()) {
         connect(var0.get(var1), var2, var3);
         int i = var1 + 1;
         if (i < var0.size()) {
            CONNECT_DELAY_EXECUTOR.schedule(() -> connectMassBatch(var0, i, var2, var3, var4), 500L, TimeUnit.MILLISECONDS);
         }
      }
   }

   public static BotClient get(String var0) {
      return BOTS.get(key(var0));
   }

   public static Collection<BotClient> all() {
      return BOTS.values();
   }

   public static boolean isOnline(String var0) {
      BotClient botclient = get(var0);
      return botclient != null && botclient.getPhase() != BotPhase.DISCONNECTED;
   }

   public static boolean disconnect(String var0) {
      BotClient botclient = BOTS.remove(key(var0));
      if (botclient == null) {
         return false;
      }

      botclient.disconnect("disconnected by user");
      return true;
   }

   public static void disconnectAll() {
      CONNECT_BATCH_GENERATION.incrementAndGet();

      for (BotClient botclient : BOTS.values()) {
         botclient.disconnect("disconnected by user");
      }

      BOTS.clear();
   }

   public static boolean add(String var0) {
      if (var0 != null && !var0.isBlank()) {
         String s = var0.trim();
         synchronized (SAVED) {
            HeadlessBots_SavedBot headlessbots_savedbot = SAVED.putIfAbsent(key(s), new HeadlessBots_SavedBot(s, null, -1));
            return headlessbots_savedbot == null;
         }
      } else {
         return false;
      }
   }

   public static void remove(String var0) {
      disconnect(var0);
      synchronized (SAVED) {
         SAVED.remove(key(var0));
      }

      PROFILES.remove(key(var0));
   }

   public static List<HeadlessBots_SavedBot> savedBots() {
      synchronized (SAVED) {
         return List.copyOf(SAVED.values());
      }
   }

   public static String getProxy(String var0) {
      synchronized (SAVED) {
         HeadlessBots_SavedBot headlessbots_savedbot = SAVED.get(key(var0));
         return headlessbots_savedbot == null ? null : headlessbots_savedbot.proxy();
      }
   }

   public static boolean hasProxy(String var0) {
      String s = getProxy(var0);
      return s != null && !s.isBlank();
   }

   public static void setProtocolVersion(String var0, int var1) {
      int i = var1 <= 0 ? -1 : var1;
      synchronized (SAVED) {
         HeadlessBots_SavedBot headlessbots_savedbot = SAVED.get(key(var0));
         if (headlessbots_savedbot != null) {
            SAVED.put(key(var0), new HeadlessBots_SavedBot(headlessbots_savedbot.name(), headlessbots_savedbot.proxy(), i));
         }
      }
   }

   public static int getProtocolVersion(String var0) {
      synchronized (SAVED) {
         HeadlessBots_SavedBot headlessbots_savedbot = SAVED.get(key(var0));
         return headlessbots_savedbot == null ? -1 : headlessbots_savedbot.protocolVersion();
      }
   }

   public static boolean addProxy(String var0) {
      String s = var0 == null ? "" : var0.trim();
      if (!s.isBlank() && !containsProxy(SAVED_PROXIES, s)) {
         SAVED_PROXIES.add(s);
         requestProxyPing(s, true);
         return true;
      } else {
         return false;
      }
   }

   public static HeadlessBots_ProxyPingSnapshot getProxyPing(String var0) {
      String s = var0 == null ? "" : var0.trim();
      if (s.isBlank()) {
         return new HeadlessBots_ProxyPingSnapshot(HeadlessBots_ProxyPingStatus.UNKNOWN, -1);
      }

      HeadlessBots_ProxyPingState headlessbots_proxypingstate = requestProxyPing(s, false);
      return new HeadlessBots_ProxyPingSnapshot(headlessbots_proxypingstate.status(), headlessbots_proxypingstate.latencyMs());
   }

   public static Exception asException(Throwable var0) {
      return var0 instanceof Exception ? (Exception)var0 : new Exception(var0);
   }

   public static String proxyPingKey(String var0) {
      return var0 == null ? "" : var0.trim().toLowerCase(Locale.ROOT);
   }

   public static void addUniqueProxy(List<String> var0, String var1) {
      String s = var1 == null ? "" : var1.trim();
      if (!s.isBlank() && !containsProxy(var0, s)) {
         var0.add(s);
      }
   }

   public static boolean containsProxy(Collection<String> var0, String var1) {
      for (String s : var0) {
         if (s.equalsIgnoreCase(var1)) {
            return true;
         }
      }

      return false;
   }

   public static String getLastAddress() {
      return lastAddress;
   }

   public static void setLastAddress(String var0) {
      lastAddress = var0 == null ? "" : var0.trim();
   }

   public static void applyProfile(BotClient var0) {
      JsonObject jsonobject = PROFILES.get(key(var0.getName()));
      if (jsonobject != null) {
         var0.execute(() -> {
            for (BotModule botmodule : var0.getModules().getModules()) {
               if (jsonobject.has(botmodule.getName()) && jsonobject.get(botmodule.getName()).isJsonObject()) {
                  botmodule.load(jsonobject.getAsJsonObject(botmodule.getName()));
               }
            }
         });
      }
   }

   public static void persistProfile(BotClient var0) {
      JsonObject jsonobject = new JsonObject();

      for (BotModule botmodule : var0.getModules().getModules()) {
         jsonobject.add(botmodule.getName(), botmodule.save());
      }

      PROFILES.put(key(var0.getName()), jsonobject);
   }

   public static void setModuleEnabled(String var0, String var1, boolean var2) {
      BotClient botclient = get(var0);
      if (botclient != null && botclient.getPhase() != BotPhase.DISCONNECTED) {
         botclient.execute(() -> {
            botclient.getModules().setEnabled(var1, var2);
            persistProfile(botclient);
         });
      } else {
         JsonObject jsonobject = PROFILES.computeIfAbsent(key(var0), var0x -> new JsonObject());
         JsonObject jsonobject1 = jsonobject.has(var1) && jsonobject.get(var1).isJsonObject() ? jsonobject.getAsJsonObject(var1) : new JsonObject();
         jsonobject1.addProperty("enabled", var2);
         jsonobject.add(var1, jsonobject1);
      }
   }

   public static boolean isModuleEnabledInProfile(String var0, String var1) {
      JsonObject jsonobject = PROFILES.get(key(var0));
      if (jsonobject != null && jsonobject.has(var1) && jsonobject.get(var1).isJsonObject()) {
         JsonObject jsonobject1 = jsonobject.getAsJsonObject(var1);
         return jsonobject1.has("enabled") && jsonobject1.get("enabled").getAsBoolean();
      } else {
         return false;
      }
   }

   public static void loadState(JsonObject var0) {
      if (var0 != null) {
         if (var0.has("BotData") && var0.get("BotData").isJsonObject()) {
            var0 = var0.getAsJsonObject("BotData");
         }

         if (var0.has("savedBots") && var0.get("savedBots").isJsonArray()) {
            JsonArray jsonarray = var0.getAsJsonArray("savedBots");
            synchronized (SAVED) {
               for (int i = 0; i < jsonarray.size(); i++) {
                  try {
                     if (jsonarray.get(i).isJsonObject()) {
                        JsonObject jsonobject = jsonarray.get(i).getAsJsonObject();
                        String s = jsonobject.get("name").getAsString();
                        String s1 = jsonobject.has("proxy") ? jsonobject.get("proxy").getAsString() : null;
                        int j = jsonobject.has("version") ? jsonobject.get("version").getAsInt() : -1;
                        if (s != null && !s.isBlank()) {
                           SAVED.putIfAbsent(key(s), new HeadlessBots_SavedBot(s.trim(), s1, j));
                           if (s1 != null && !s1.isBlank()) {
                              addProxy(s1);
                           }
                        }
                     } else {
                        String s3 = jsonarray.get(i).getAsString();
                        if (s3 != null && !s3.isBlank()) {
                           SAVED.putIfAbsent(key(s3), new HeadlessBots_SavedBot(s3.trim(), null, -1));
                        }
                     }
                  } catch (Exception var10) {
                  }
               }
            }
         }

         if (var0.has("botProfiles") && var0.get("botProfiles").isJsonObject()) {
            JsonObject jsonobject1 = var0.getAsJsonObject("botProfiles");

            for (String s2 : jsonobject1.keySet()) {
               if (jsonobject1.get(s2).isJsonObject()) {
                  PROFILES.put(s2.toLowerCase(Locale.ROOT), jsonobject1.getAsJsonObject(s2));
               }
            }
         }

         if (var0.has("proxyPool") && var0.get("proxyPool").isJsonArray()) {
            JsonArray jsonarray1 = var0.getAsJsonArray("proxyPool");

            for (int k = 0; k < jsonarray1.size(); k++) {
               try {
                  addProxy(jsonarray1.get(k).getAsString());
               } catch (Exception var9) {
               }
            }
         }

         if (var0.has("lastAddress")) {
            setLastAddress(var0.get("lastAddress").getAsString());
         }
      }
   }

   public static void loadPersistentState() {
      loadState(BotPersistence.load());
   }

   public static void savePersistentState() {
      try {
         BotPersistence.save(saveState());
      } catch (Exception var1) {
      }
   }

   public static String key(String var0) {
      return var0.toLowerCase(Locale.ROOT);
   }

   private static final class ProxyPingChannelInitializer extends ChannelInitializer<Channel> {
      private final ProxyHandler proxyHandler;

      private ProxyPingChannelInitializer(ProxyHandler proxyHandler) {
         this.proxyHandler = proxyHandler;
      }

      @Override
      protected void initChannel(Channel channel) {
         channel.pipeline().addLast("timeout", new ReadTimeoutHandler((int)Math.ceil(5.0)));
         channel.pipeline().addLast("proxy", this.proxyHandler);
      }
   }
}
