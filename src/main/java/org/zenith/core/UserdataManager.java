package org.zenith.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import org.zenith.ZenithClient;

/**
 * Stores cosmetic avatar selections without depending on the obsolete Figura
 * 1.21.4 runtime. Rendering can be wired back in when a compatible bridge is
 * available; the rest of Zenith remains usable without it.
 */
public final class UserdataManager {
   private static final Set<UUID> BODY_AVATARS = ConcurrentHashMap.newKeySet();
   private static final Set<UUID> WEAPON_AVATARS = ConcurrentHashMap.newKeySet();

   private UserdataManager() {
   }

   public static void PotionItemBuilder(Path path) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (client.player != null) {
         on23(client.player.getUuid(), path);
      }
   }

   public static void UiAnimation(UUID playerId, String relativePath) {
      on23(playerId, EventUpdateHealth(relativePath));
   }

   public static void on23(UUID playerId, Path path) {
      updateSelection(BODY_AVATARS, playerId, path);
   }

   public static void Easing(UUID playerId, String relativePath) {
      Easing(playerId, EventUpdateHealth(relativePath));
   }

   public static void UiAnimation(UUID playerId, Path path) {
      updateSelection(BODY_AVATARS, playerId, path);
   }

   public static void ColorAnimator(UUID playerId, String relativePath) {
      ColorAnimator(playerId, EventUpdateHealth(relativePath));
   }

   public static void Easing(UUID playerId, Path path) {
      updateSelection(BODY_AVATARS, playerId, path);
   }

   public static void ItemRegistry(UUID playerId, String relativePath) {
      ColorAnimator(playerId, EventUpdateHealth(relativePath));
   }

   public static void ColorAnimator(UUID playerId, Path path) {
      updateSelection(WEAPON_AVATARS, playerId, path);
   }

   public static boolean NbtEditor(UUID playerId) {
      return BODY_AVATARS.contains(playerId) || WEAPON_AVATARS.contains(playerId);
   }

   public static boolean PotionItemBuilder(UUID playerId) {
      return BODY_AVATARS.contains(playerId);
   }

   public static boolean ProfileItemBuilder(UUID playerId) {
      return WEAPON_AVATARS.contains(playerId);
   }

   public static void StringCodec(UUID playerId) {
      if (playerId != null) {
         BODY_AVATARS.remove(playerId);
         WEAPON_AVATARS.remove(playerId);
      }
   }

   public static void FileLogger(UUID playerId) {
      remove(BODY_AVATARS, playerId);
   }

   public static void CloudApiClient(UUID playerId) {
      remove(WEAPON_AVATARS, playerId);
   }

   public static void MediaTrackInfo(UUID playerId) {
      remove(BODY_AVATARS, playerId);
   }

   public static void CloudUserProfile(UUID playerId) {
      remove(WEAPON_AVATARS, playerId);
   }

   public static String ProfileItemBuilder(Path avatarPath) {
      if (avatarPath == null) {
         return "";
      }

      Path root = ZenithClient.ColorAnimator.toPath().toAbsolutePath().normalize();
      Path normalized = avatarPath.toAbsolutePath().normalize();
      return normalized.startsWith(root) ? root.relativize(normalized).toString().replace('\\', '/') : "";
   }

   public static Path EventUpdateHealth(String relativePath) {
      if (relativePath == null || relativePath.isBlank()) {
         return null;
      }

      Path root = ZenithClient.ColorAnimator.toPath().toAbsolutePath().normalize();
      Path resolved = root.resolve(relativePath).normalize();
      return resolved.startsWith(root) ? resolved : null;
   }

   public static Path StringCodec(Path avatarPath) {
      if (avatarPath == null) {
         return null;
      }

      Path normalized = avatarPath.toAbsolutePath().normalize();
      return Files.isDirectory(normalized) && Files.isRegularFile(normalized.resolve("avatar.json")) ? normalized : null;
   }

   public static void ColorAnimator(Runnable task) {
      MinecraftClient client = MinecraftClient.getInstance();
      if (!client.isOnThread()) {
         client.execute(task);
      } else {
         task.run();
      }
   }

   public static void ModuleSnapshotDto(UUID playerId) {
      StringCodec(playerId);
   }

   private static void updateSelection(Set<UUID> selections, UUID playerId, Path path) {
      if (playerId == null) {
         return;
      }

      if (StringCodec(path) == null) {
         selections.remove(playerId);
      } else {
         selections.add(playerId);
      }
   }

   private static void remove(Set<UUID> selections, UUID playerId) {
      if (playerId != null) {
         selections.remove(playerId);
      }
   }
}
