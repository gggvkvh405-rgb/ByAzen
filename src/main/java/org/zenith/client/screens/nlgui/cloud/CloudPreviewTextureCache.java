package org.zenith.client.screens.nlgui.cloud;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.zenith.ZenithClient;
import org.zenith.core.CloudApiClient;
import org.zenith.core.CloudSessionExtDto;

public final class CloudPreviewTextureCache {
   public static final String NAMESPACE = "zenith";
   public static final String PREFIX = "cloud_config_preview/";
   public static final int MAX_TEXTURES = 32;
   public static final int MAX_IN_FLIGHT = 6;
   public static final long RETRY_DELAY_MS = 15000L;
   public static final Map<String, Identifier> textures = new LinkedHashMap<>(16, 0.75F, true);
   public static final Set<String> inFlight = ConcurrentHashMap.newKeySet();
   public static final Map<String, Long> failures = new ConcurrentHashMap<>();
   public static Identifier draftId;
   public static int draftFingerprint;

   public static Identifier get(CloudSessionExtDto var0) {
      if (var0 == null) {
         return null;
      }

      String s = var0.RotationSnapStrategy();
      synchronized (textures) {
         Identifier identifier = textures.get(s);
         if (identifier != null) {
            return identifier;
         }
      }

      Long olong = failures.get(s);
      if (olong != null && System.currentTimeMillis() - olong < 15000L) {
         return null;
      } else {
         CloudApiClient l1i1iil111il1l1l = ZenithClient.on23().getCloudClient();
         if (l1i1iil111il1l1l != null && l1i1iil111il1l1l.isConnected() && !inFlight.contains(s) && inFlight.size() < 6 && inFlight.add(s)) {
            l1i1iil111il1l1l.on23(var0).whenComplete((var1x, var2) -> {
               try {
                  if (var2 != null || var1x == null || var1x.length == 0) {
                     failures.put(s, System.currentTimeMillis());
                     return;
                  }

                  install(s, var1x);
               } finally {
                  inFlight.remove(s);
               }
            });
            return null;
         } else {
            return null;
         }
      }
   }

   public static void prefetch(CloudSessionExtDto var0) {
      get(var0);
   }

   public static void install(String var0, byte[] var1) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient != null) {
         NativeImage nativeimage;
         try (ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(var1)) {
            nativeimage = NativeImage.read(bytearrayinputstream);
         } catch (Exception exception) {
            failures.put(var0, System.currentTimeMillis());
            return;
         }

         minecraftclient.execute(() -> {
            Identifier identifier = Identifier.of("zenith", "cloud_config_preview/" + var0);

            try {
               minecraftclient.getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(() -> "Zenith cloud preview", nativeimage));
            } catch (Exception exception1) {
               nativeimage.close();
               failures.put(var0, System.currentTimeMillis());
               return;
            }

            failures.remove(var0);
            synchronized (textures) {
               textures.put(var0, identifier);
               evictOverflow(minecraftclient);
            }
         });
      }
   }

   public static void evictOverflow(MinecraftClient var0) {
      Iterator<Entry<String, Identifier>> iterator = textures.entrySet().iterator();

      while (textures.size() > 32 && iterator.hasNext()) {
         Entry entry = iterator.next();
         iterator.remove();

         try {
            var0.getTextureManager().destroyTexture((Identifier)entry.getValue());
         } catch (Exception var4) {
         }
      }
   }

   public static synchronized Identifier draft(byte[] var0) {
      if (var0 != null && var0.length != 0) {
         int i = Arrays.hashCode(var0);
         if (draftId != null && draftFingerprint == i) {
            return draftId;
         }

         MinecraftClient minecraftclient = MinecraftClient.getInstance();
         if (minecraftclient == null) {
            return null;
         }

         clearDraft();

         try (ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(var0)) {
            NativeImage nativeimage = NativeImage.read(bytearrayinputstream);
            Identifier identifier = Identifier.of("zenith", "cloud_config_preview/draft_" + Integer.toHexString(i));
            minecraftclient.getTextureManager().registerTexture(identifier, new NativeImageBackedTexture(() -> "Zenith cloud preview", nativeimage));
            draftId = identifier;
            draftFingerprint = i;
            return identifier;
         } catch (Exception exception) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static synchronized void clearDraft() {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (draftId != null && minecraftclient != null && minecraftclient.getTextureManager() != null) {
         try {
            minecraftclient.getTextureManager().destroyTexture(draftId);
         } catch (Exception var2) {
         }
      }

      draftId = null;
      draftFingerprint = 0;
   }

   public static void clear() {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      synchronized (textures) {
         if (minecraftclient != null && minecraftclient.getTextureManager() != null) {
            for (Identifier identifier : textures.values()) {
               try {
                  minecraftclient.getTextureManager().destroyTexture(identifier);
               } catch (Exception var6) {
               }
            }
         }

         textures.clear();
      }

      failures.clear();
   }
}
