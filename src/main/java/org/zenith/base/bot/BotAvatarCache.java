package org.zenith.base.bot;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class BotAvatarCache {
   public static final String NAMESPACE = "zenith";
   public static final String PREFIX = "bot_avatar/";
   public static final String AVATAR_URL = "https://minotar.net/helm/%s/64.png";
   public static final Map<String, Identifier> nameToId = new ConcurrentHashMap<>();
   public static final Map<String, Long> loading = new ConcurrentHashMap<>();
   public static final AtomicLong generation = new AtomicLong();
   public static final AtomicLong requestIds = new AtomicLong();
   public static final ThreadPoolExecutor loader = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(128), var0 -> {
      Thread thread = new Thread(var0, "bot-avatar-loader");
      thread.setDaemon(true);
      return thread;
   });

   public static Identifier prewarm(String var0) {
      if (var0 != null && !var0.isEmpty()) {
         String s = var0.toLowerCase(Locale.ROOT);
         Identifier identifier = nameToId.get(s);
         if (identifier != null) {
            return identifier;
         }

         requestLoad(var0, s);
         return null;
      } else {
         return null;
      }
   }

   public static void requestLoad(String var0, String var1) {
      long i = generation.get();
      long j = requestIds.incrementAndGet();
      if (loading.putIfAbsent(var1, j) == null) {
         Runnable runnable = () -> {
            try {
               byte[] abyte = fetch(var0, var1);
               if (abyte == null || abyte.length == 0) {
                  loading.remove(var1, j);
                  return;
               }

               NativeImage nativeimage;
               try (ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte)) {
                  nativeimage = NativeImage.read(bytearrayinputstream);
               }

               MinecraftClient var15 = MinecraftClient.getInstance();

               try {
                  var15.execute(() -> install(var1, nativeimage, i, j));
               } catch (Throwable throwable) {
                  nativeimage.close();
                  loading.remove(var1, j);
               }
            } catch (Throwable throwable2) {
               loading.remove(var1, j);
            }
         };

         try {
            loader.execute(runnable);
         } catch (RejectedExecutionException rejectedexecutionexception) {
            loading.remove(var1, j);
         }
      }
   }

   public static void install(String var0, NativeImage var1, long var2, long var4) {
      NativeImageBackedTexture nativeimagebackedtexture = null;
      boolean flag = false;

      try {
         if (var2 == generation.get()) {
            MinecraftClient minecraftclient = MinecraftClient.getInstance();
            Identifier identifier = Identifier.of("zenith", "bot_avatar/" + Integer.toHexString(var0.hashCode()));
            nativeimagebackedtexture = new NativeImageBackedTexture(() -> "Zenith bot avatar", var1);
            minecraftclient.getTextureManager().registerTexture(identifier, nativeimagebackedtexture);
            flag = true;
            nameToId.put(var0, identifier);
            return;
         }
      } catch (Throwable throwable) {
         return;
      } finally {
         if (!flag) {
            if (nativeimagebackedtexture != null) {
               nativeimagebackedtexture.close();
            } else {
               var1.close();
            }
         }

         loading.remove(var0, var4);
      }
   }

   public static byte[] fetch(String var0, String var1) throws Exception {
      Path path = cacheDir().resolve(Integer.toHexString(var1.hashCode()) + ".png");

      try {
         if (Files.isRegularFile(path)) {
            byte[] abyte = Files.readAllBytes(path);
            if (abyte.length > 0) {
               return abyte;
            }
         }
      } catch (Exception var6) {
      }

      byte[] abyte1 = download(String.format("https://minotar.net/helm/%s/64.png", var0));
      if (abyte1 != null && abyte1.length > 0) {
         try {
            Files.createDirectories(path.getParent());
            Files.write(path, abyte1);
         } catch (Exception var5) {
         }
      }

      return abyte1;
   }

   public static Path cacheDir() {
      return MinecraftClient.getInstance().runDirectory.toPath().resolve("zenith").resolve("bot_avatars");
   }

   public static byte[] download(String var0) throws Exception {
      HttpURLConnection httpurlconnection = (HttpURLConnection)new URL(var0).openConnection();
      httpurlconnection.setConnectTimeout(5000);
      httpurlconnection.setReadTimeout(5000);
      httpurlconnection.setRequestProperty("User-Agent", "Zenith");

      try (InputStream inputstream = httpurlconnection.getInputStream()) {
         return inputstream.readAllBytes();
      } finally {
         httpurlconnection.disconnect();
      }
   }

   public static void clear() {
      generation.incrementAndGet();
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient != null && minecraftclient.getTextureManager() != null) {
         for (Identifier identifier : nameToId.values()) {
            try {
               minecraftclient.getTextureManager().destroyTexture(identifier);
            } catch (Throwable var4) {
            }
         }
      }

      nameToId.clear();
      loading.clear();
   }
}
