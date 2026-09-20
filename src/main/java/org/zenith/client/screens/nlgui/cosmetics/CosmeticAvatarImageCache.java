package org.zenith.client.screens.nlgui.cosmetics;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.BufferUtils;

public final class CosmeticAvatarImageCache {
   public static final String AVATAR_PNG = "avatar.png";
   public static final String CACHE_NAMESPACE = "zenith";
   public static final String CACHE_PREFIX = "cosmetic_avatar/";
   public static final Map<String, Identifier> pathToId = new ConcurrentHashMap<>();

   public static Identifier getAvatarTextureId(Path var0) {
      if (var0 != null && Files.isDirectory(var0)) {
         Path path = var0.resolve("avatar.png");
         if (!Files.isRegularFile(path)) {
            return null;
         }

         String s = var0.normalize().toAbsolutePath().toString();
         Identifier identifier = pathToId.get(s);
         if (identifier != null) {
            return identifier;
         }

         Identifier identifier1 = loadAndRegister(path, s);
         if (identifier1 != null) {
            pathToId.put(s, identifier1);
         }

         return identifier1;
      } else {
         return null;
      }
   }

   public static Identifier loadAndRegister(Path var0, String var1) {
      try {
         Identifier identifier = Identifier.of("zenith", "cosmetic_avatar/" + Integer.toHexString(var1.hashCode()));
         byte[] abyte = Files.readAllBytes(var0);
         ByteBuffer bytebuffer = BufferUtils.createByteBuffer(abyte.length).put(abyte);
         bytebuffer.flip();
         NativeImageBackedTexture nativeimagebackedtexture = new NativeImageBackedTexture(() -> "Zenith cosmetic avatar", NativeImage.read(bytebuffer));
         MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().getTextureManager().registerTexture(identifier, nativeimagebackedtexture));
         return identifier;
      } catch (Exception exception) {
         return null;
      }
   }

   public static void clear() {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient != null && minecraftclient.getTextureManager() != null) {
         for (Identifier identifier : pathToId.values()) {
            try {
               minecraftclient.getTextureManager().destroyTexture(identifier);
            } catch (Exception var4) {
            }
         }

         pathToId.clear();
      }
   }
}
