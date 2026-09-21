package rtx.byazen.api.music;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import rtx.byazen.ByAzen;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Persistent part of the music player: favourites, user added stations and the last played tracks.
 * Stored as JSON through {@link RepositoryStorage} so it lives next to the other ByAzen configs.
 */
public final class MusicLibrary {

    private static final String FILE = "music_library";
    private static final int RECENT_LIMIT = 24;

    private static final MusicLibrary INSTANCE = new MusicLibrary();

    private final List<MusicTrack> favorites = new ArrayList<MusicTrack>();
    private final List<MusicTrack> custom = new ArrayList<MusicTrack>();
    private final List<MusicTrack> recent = new ArrayList<MusicTrack>();
    private final List<MusicTrack> disliked = new ArrayList<MusicTrack>();
    private final Map<String, String> resolvedStreams = new LinkedHashMap<String, String>();
    private volatile MusicTrack lastPlayed;

    private MusicLibrary() {
        this.load();
    }

    public static MusicLibrary get() {
        return INSTANCE;
    }

    /** Everything the player shows in the "Библиотека" tab: built-in stations, custom ones and favourites. */
    public List<MusicTrack> all() {
        ArrayList<MusicTrack> list = new ArrayList<MusicTrack>();
        list.addAll(RadioCatalog.stations());
        for (MusicTrack track : this.custom) {
            if (!containsUrl(list, track)) {
                list.add(track);
            }
        }
        for (MusicTrack track : this.favorites) {
            if (!containsUrl(list, track)) {
                list.add(track);
            }
        }
        return list;
    }

    public List<MusicTrack> favorites() {
        return Collections.unmodifiableList(new ArrayList<MusicTrack>(this.favorites));
    }

    public List<MusicTrack> custom() {
        return Collections.unmodifiableList(new ArrayList<MusicTrack>(this.custom));
    }

    public List<MusicTrack> recent() {
        return Collections.unmodifiableList(new ArrayList<MusicTrack>(this.recent));
    }

    /** Треки, которые игрок отметил как «не нравится» (идея №10). */
    public List<MusicTrack> disliked() {
        return Collections.unmodifiableList(new ArrayList<MusicTrack>(this.disliked));
    }

    public boolean isDisliked(MusicTrack track) {
        if (track == null) {
            return false;
        }
        for (MusicTrack disliked : this.disliked) {
            if (disliked.key().equals(track.key())) {
                return true;
            }
        }
        return false;
    }

    public void toggleDislike(MusicTrack track) {
        if (track == null || track.url() == null || track.url().isBlank()) {
            return;
        }
        for (int i = 0; i < this.disliked.size(); ++i) {
            if (this.disliked.get(i).key().equals(track.key())) {
                this.disliked.remove(i);
                this.save();
                return;
            }
        }
        this.disliked.add(track);
        this.favorites.removeIf(favorite -> favorite.key().equals(track.key()));
        this.save();
    }

    /** Stream url of a station after it was refreshed through the radio catalogue. */
    public String resolved(String slug) {
        return slug == null ? null : this.resolvedStreams.get(slug);
    }

    public void rememberResolved(String slug, String url) {
        if (slug == null || url == null || url.isBlank()) {
            return;
        }
        this.resolvedStreams.put(slug, url);
        this.save();
    }

    public boolean isFavorite(MusicTrack track) {
        if (track == null) {
            return false;
        }
        for (MusicTrack favorite : this.favorites) {
            if (favorite.key().equals(track.key())) {
                return true;
            }
        }
        return false;
    }

    public void toggleFavorite(MusicTrack track) {
        if (track == null || track.url() == null || track.url().isBlank()) {
            return;
        }
        for (int i = 0; i < this.favorites.size(); ++i) {
            if (this.favorites.get(i).key().equals(track.key())) {
                this.favorites.remove(i);
                this.save();
                return;
            }
        }
        this.favorites.add(track);
        this.save();
    }

    public void addCustom(String url, String name) {
        if (url == null || url.isBlank()) {
            return;
        }
        MusicTrack track = new MusicTrack(
                MusicTrack.Kind.LINK,
                name == null || name.isBlank() ? MusicTrack.describeUrl(url) : name,
                "Своя ссылка",
                url.trim(),
                null,
                "Ссылка",
                0L);
        for (int i = 0; i < this.custom.size(); ++i) {
            if (this.custom.get(i).key().equals(track.key())) {
                this.custom.set(i, track);
                this.save();
                return;
            }
        }
        this.custom.add(track);
        this.save();
    }

    /** Импорт M3U-плейлиста: все станции попадают в «Свои ссылки» (идея №20). */
    public int importPlaylist(List<MusicTrack> playlist) {
        if (playlist == null || playlist.isEmpty()) {
            return 0;
        }
        int added = 0;
        for (MusicTrack track : playlist) {
            if (track == null || track.url() == null || track.url().isBlank()) {
                continue;
            }
            this.addCustom(track.url(), track.title());
            ++added;
        }
        return added;
    }

    public void removeCustom(String url) {
        if (url == null) {
            return;
        }
        this.custom.removeIf(track -> track.key().equals(url));
        this.save();
    }

    public void pushRecent(MusicTrack track) {
        if (track == null || track.url() == null || track.url().isBlank()) {
            return;
        }
        this.lastPlayed = track;
        this.recent.removeIf(existing -> existing.key().equals(track.key()));
        this.recent.add(0, track);
        while (this.recent.size() > RECENT_LIMIT) {
            this.recent.remove(this.recent.size() - 1);
        }
        this.save();
    }

    public MusicTrack lastPlayed() {
        return this.lastPlayed;
    }

    /** Keeps the length of an online track after the catalogue reported it. */
    public void updateDuration(MusicTrack track, long durationMs) {
        if (track == null || durationMs <= 0L || track.isLive()) {
            return;
        }
        replace(this.favorites, track.withDuration(durationMs));
        replace(this.recent, track.withDuration(durationMs));
        this.save();
    }

    private static void replace(List<MusicTrack> list, MusicTrack track) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).key().equals(track.key())) {
                list.set(i, track);
                return;
            }
        }
    }

    private static boolean containsUrl(List<MusicTrack> list, MusicTrack track) {
        for (MusicTrack existing : list) {
            if (existing.key().equals(track.key())) {
                return true;
            }
        }
        return false;
    }

    // ------------------------------------------------------------------ storage

    private void load() {
        try {
            JsonObject root = RepositoryStorage.readObject(FILE);
            readTracks(root.getAsJsonArray("favorites"), this.favorites);
            readTracks(root.getAsJsonArray("custom"), this.custom);
            readTracks(root.getAsJsonArray("recent"), this.recent);
            readTracks(root.getAsJsonArray("disliked"), this.disliked);
            if (root.has("resolved") && root.get("resolved").isJsonObject()) {
                for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject("resolved").entrySet()) {
                    if (entry.getValue().isJsonPrimitive()) {
                        this.resolvedStreams.put(entry.getKey(), entry.getValue().getAsString());
                    }
                }
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Music library could not be loaded: {}", throwable.toString());
        }
    }

    private static void readTracks(JsonArray array, List<MusicTrack> out) {
        if (array == null) {
            return;
        }
        for (JsonElement element : array) {
            if (!element.isJsonObject()) {
                continue;
            }
            JsonObject object = element.getAsJsonObject();
            String url = string(object, "url");
            if (url == null || url.isBlank()) {
                continue;
            }
            MusicTrack.Kind kind;
            try {
                kind = MusicTrack.Kind.valueOf(string(object, "kind", "TRACK"));
            }
            catch (IllegalArgumentException illegalArgumentException) {
                kind = MusicTrack.Kind.TRACK;
            }
            out.add(new MusicTrack(
                    kind,
                    string(object, "title", MusicTrack.describeUrl(url)),
                    string(object, "subtitle", ""),
                    url,
                    string(object, "cover"),
                    string(object, "badge", ""),
                    longValue(object, "duration"),
                    string(object, "source")));
        }
    }

    private static String string(JsonObject object, String key) {
        return string(object, key, null);
    }

    private static String string(JsonObject object, String key, String fallback) {
        if (object.has(key) && object.get(key).isJsonPrimitive()) {
            return object.get(key).getAsString();
        }
        return fallback;
    }

    private static long longValue(JsonObject object, String key) {
        try {
            return object.has(key) ? object.get(key).getAsLong() : 0L;
        }
        catch (Throwable throwable) {
            return 0L;
        }
    }

    /** Saves the library; called from background threads as well, hence the synchronization. */
    public synchronized void save() {
        try {
            JsonObject root = new JsonObject();
            root.add("favorites", writeTracks(this.favorites));
            root.add("custom", writeTracks(this.custom));
            root.add("recent", writeTracks(this.recent));
            root.add("disliked", writeTracks(this.disliked));
            JsonObject resolved = new JsonObject();
            for (Map.Entry<String, String> entry : this.resolvedStreams.entrySet()) {
                resolved.addProperty(entry.getKey(), entry.getValue());
            }
            root.add("resolved", resolved);
            RepositoryStorage.write(FILE, root);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Music library could not be saved: {}", throwable.toString());
        }
    }

    private static JsonArray writeTracks(List<MusicTrack> tracks) {
        JsonArray array = new JsonArray();
        for (MusicTrack track : tracks) {
            JsonObject object = new JsonObject();
            object.addProperty("kind", track.kind().name());
            object.addProperty("title", track.title());
            object.addProperty("subtitle", track.subtitle());
            object.addProperty("url", track.url());
            if (track.coverUrl() != null) {
                object.addProperty("cover", track.coverUrl());
            }
            object.addProperty("badge", track.badge());
            object.addProperty("duration", track.durationMs());
            if (track.sourceId() != null) {
                object.addProperty("source", track.sourceId());
            }
            array.add(object);
        }
        return array;
    }
}
