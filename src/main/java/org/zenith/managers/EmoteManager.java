package org.zenith.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.zigythebird.playeranimcore.animation.Animation;
import com.zigythebird.playeranimcore.loading.UniversalAnimLoader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Loads and indexes the emotes shipped with Zenith. */
public final class EmoteManager {
   private static final Logger LOGGER = LoggerFactory.getLogger("Zenith/Emotes");
   private static final List<String> BUILT_IN_EMOTES = List.of(
      "backflip", "clap", "club_penguin_dance", "crying", "kazotsky_kick",
      "palm", "point", "roblox_potion_dance", "twerk", "waving"
   );
   private static final List<String> EXTRA_EMOTE_INDEXES = List.of("/assets/zenith/emotes/spemotes/index.txt");

   private final Map<String, EmoteMetadata> emotes;

   public EmoteManager() {
      Map<String, EmoteMetadata> loadedEmotes = new LinkedHashMap<>();

      for (String id : BUILT_IN_EMOTES) {
         loadEmote(id).ifPresent(emote -> loadedEmotes.put(emote.id(), emote));
      }

      for (String indexPath : EXTRA_EMOTE_INDEXES) {
         for (String id : readIndex(indexPath)) {
            loadEmote(id).ifPresent(emote -> loadedEmotes.put(emote.id(), emote));
         }
      }

      this.emotes = Collections.unmodifiableMap(loadedEmotes);
      LOGGER.info("Loaded {} built-in Zenith emotes", this.emotes.size());
   }

   public Optional<EmoteMetadata> find(String id) {
      return id == null || id.isBlank() ? Optional.empty() : Optional.ofNullable(this.emotes.get(normalizeId(id)));
   }

   public Collection<EmoteMetadata> all() {
      return this.emotes.values();
   }

   public List<String> ids() {
      return List.copyOf(this.emotes.keySet());
   }

   private static List<String> readIndex(String resourcePath) {
      try (InputStream stream = EmoteManager.class.getResourceAsStream(resourcePath)) {
         if (stream == null) {
            LOGGER.warn("Missing built-in emote index {}", resourcePath);
            return List.of();
         }

         try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            return reader.lines().map(String::trim).filter(line -> !line.isEmpty() && !line.startsWith("#")).toList();
         }
      } catch (IOException exception) {
         LOGGER.warn("Could not load built-in emote index {}", resourcePath, exception);
         return List.of();
      }
   }

   private static Optional<EmoteMetadata> loadEmote(String resourceName) {
      String resourcePath = "/assets/zenith/emotes/" + resourceName + ".json";

      try (InputStream stream = EmoteManager.class.getResourceAsStream(resourcePath)) {
         if (stream == null) {
            LOGGER.warn("Missing built-in emote resource {}", resourcePath);
            return Optional.empty();
         }

         Collection<Animation> animations = UniversalAnimLoader.loadAnimations(stream).values();
         if (animations.isEmpty()) {
            LOGGER.warn("Built-in emote {} contains no animations", resourceName);
            return Optional.empty();
         }

         Animation animation = animations.iterator().next();
         String id = normalizeId(resourceName);
         return Optional.of(new EmoteMetadata(
            id,
            animation.uuid(),
            metadataText(animation, "name", titleFromId(resourceName)),
            metadataText(animation, "author", "Unknown"),
            Identifier.of("zenith", "emotes/" + resourceName + ".png"),
            animation
         ));
      } catch (RuntimeException | IOException exception) {
         LOGGER.warn("Could not load built-in emote {}", resourceName, exception);
         return Optional.empty();
      }
   }

   private static String metadataText(Animation animation, String key, String fallback) {
      Object value = animation.data().getRaw(key);
      if (!(value instanceof String text) || text.isBlank()) {
         return fallback;
      }

      try {
         JsonElement json = JsonParser.parseString(text);
         if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            return json.getAsString();
         }
         if (json.isJsonObject()) {
            JsonObject object = json.getAsJsonObject();
            if (object.has("fallback") && object.get("fallback").isJsonPrimitive()) {
               return object.get("fallback").getAsString();
            }
         }
      } catch (RuntimeException ignored) {
         // Plain strings are the common metadata representation.
      }
      return text;
   }

   private static String normalizeId(String value) {
      return value.trim().toLowerCase(Locale.ROOT).replace(' ', '_');
   }

   private static String titleFromId(String id) {
      List<String> words = new ArrayList<>();
      for (String word : id.split("_")) {
         words.add(word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1));
      }
      return String.join(" ", words);
   }
}
