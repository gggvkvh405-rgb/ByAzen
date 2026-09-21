package rtx.byazen.api.music;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import rtx.byazen.ByAzen;

/**
 * Network side of the music player: the online catalogue (Audius - free, no account, mp3 streams),
 * the public radio-browser directory and a tiny thread pool, so the render thread never blocks.
 */
public final class MusicHttp {

    public static final String APP_NAME = "ByAzen";

    private static final String[] RADIO_HOSTS = {
            "https://de1.api.radio-browser.info",
            "https://de2.api.radio-browser.info",
            "https://nl1.api.radio-browser.info"
    };
    private static final String AUDIUS = "https://api.audius.co/v1";
    private static final String USER_AGENT = "ByAzen/1.3.0 (+https://github.com/gggvkvh405-rgb/ByAzen)";

    private static final HttpClient HTTP = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final ExecutorService POOL = Executors.newFixedThreadPool(2, runnable -> {
        Thread thread = new Thread(runnable, "byazen-music-net");
        thread.setDaemon(true);
        return thread;
    });

    private MusicHttp() {
    }

    /** Runs a task off the render thread. */
    public static void submit(Runnable task) {
        try {
            POOL.submit(task);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Music task rejected: {}", throwable.toString());
        }
    }

    public static String get(String url) throws Exception {
        HttpResponse<String> response = HTTP.send(
                HttpRequest.newBuilder(URI.create(url))
                        .header("User-Agent", USER_AGENT)
                        .header("Accept", "application/json")
                        .timeout(Duration.ofSeconds(15))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IllegalStateException("HTTP " + response.statusCode());
        }
        return response.body();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value == null ? "" : value, StandardCharsets.UTF_8);
    }

    // ------------------------------------------------------------------ online tracks (Audius)

    public static List<MusicTrack> searchTracks(String query, int limit) {
        if (query == null || query.isBlank()) {
            return new ArrayList<MusicTrack>();
        }
        try {
            String body = MusicHttp.get(AUDIUS + "/tracks/search?query=" + encode(query.trim())
                    + "&app_name=" + encode(APP_NAME) + "&limit=" + Math.max(1, Math.min(50, limit)));
            return MusicHttp.parseAudius(body);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Music catalogue search failed: {}", throwable.toString());
            return new ArrayList<MusicTrack>();
        }
    }

    public static List<MusicTrack> trendingTracks(int limit) {
        try {
            String body = MusicHttp.get(AUDIUS + "/tracks/trending?app_name=" + encode(APP_NAME)
                    + "&limit=" + Math.max(1, Math.min(50, limit)));
            return MusicHttp.parseAudius(body);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Music catalogue trending failed: {}", throwable.toString());
            return new ArrayList<MusicTrack>();
        }
    }

    private static List<MusicTrack> parseAudius(String body) {
        ArrayList<MusicTrack> tracks = new ArrayList<MusicTrack>();
        try {
            JsonObject root = JsonParser.parseString(body).getAsJsonObject();
            JsonArray data = root.getAsJsonArray("data");
            if (data == null) {
                return tracks;
            }
            for (JsonElement element : data) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject object = element.getAsJsonObject();
                String id = object.has("id") ? object.get("id").getAsString() : null;
                if (id == null || id.isBlank()) {
                    continue;
                }
                String title = object.has("title") ? object.get("title").getAsString() : "Без названия";
                String artist = "";
                if (object.has("user") && object.get("user").isJsonObject()) {
                    JsonObject user = object.getAsJsonObject("user");
                    if (user.has("name")) {
                        artist = user.get("name").getAsString();
                    }
                }
                String genre = object.has("genre") && object.get("genre").isJsonPrimitive() ? object.get("genre").getAsString() : "";
                long duration = object.has("duration") && object.get("duration").isJsonPrimitive()
                        ? object.get("duration").getAsLong() * 1000L
                        : 0L;
                String cover = null;
                if (object.has("artwork") && object.get("artwork").isJsonObject()) {
                    JsonObject artwork = object.getAsJsonObject("artwork");
                    for (String size : new String[]{"480x480", "150x150", "1000x1000", "max"}) {
                        if (artwork.has(size) && artwork.get(size).isJsonPrimitive()) {
                            cover = artwork.get(size).getAsString();
                            break;
                        }
                    }
                }
                String stream = AUDIUS + "/tracks/" + id + "/stream?app_name=" + encode(APP_NAME);
                String subtitle = artist.isBlank() ? genre : artist + (genre.isBlank() ? "" : " • " + genre);
                tracks.add(new MusicTrack(MusicTrack.Kind.TRACK, title, subtitle, stream, cover, "Audius", duration));
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Music catalogue answer could not be parsed: {}", throwable.toString());
        }
        return tracks;
    }

    // ------------------------------------------------------------------ radio directory

    public static List<MusicTrack> searchStations(String query, int limit) {
        if (query == null || query.isBlank()) {
            return new ArrayList<MusicTrack>();
        }
        for (String host : RADIO_HOSTS) {
            try {
                String body = MusicHttp.get(host + "/json/stations/search?name=" + encode(query.trim())
                        + "&limit=" + Math.max(1, Math.min(50, limit))
                        + "&hidebroken=true&order=clickcount&reverse=true");
                List<MusicTrack> stations = MusicHttp.parseRadioBrowser(body);
                if (!stations.isEmpty()) {
                    return stations;
                }
            }
            catch (Throwable throwable) {
                ByAzen.LOGGER.warn("[ByAzen] Radio directory {} failed: {}", host, throwable.toString());
            }
        }
        return new ArrayList<MusicTrack>();
    }

    public static List<MusicTrack> topStations(int limit) {
        for (String host : RADIO_HOSTS) {
            try {
                return MusicHttp.parseRadioBrowser(MusicHttp.get(host + "/json/stations/topclick/" + Math.max(1, Math.min(50, limit))));
            }
            catch (Throwable throwable) {
                ByAzen.LOGGER.warn("[ByAzen] Radio directory {} failed: {}", host, throwable.toString());
            }
        }
        return new ArrayList<MusicTrack>();
    }

    /** Current stream url of a station by its radio-browser id, or null when it is unknown. */
    public static String resolveStationUrl(String slug) {
        if (slug == null || slug.isBlank()) {
            return null;
        }
        for (String host : RADIO_HOSTS) {
            try {
                String body = MusicHttp.get(host + "/json/stations/byuuid/" + encode(slug));
                List<MusicTrack> stations = MusicHttp.parseRadioBrowser(body);
                if (!stations.isEmpty()) {
                    return stations.get(0).url();
                }
            }
            catch (Throwable throwable) {
                ByAzen.LOGGER.warn("[ByAzen] Radio directory {} failed: {}", host, throwable.toString());
            }
        }
        return null;
    }

    private static List<MusicTrack> parseRadioBrowser(String body) {
        ArrayList<MusicTrack> stations = new ArrayList<MusicTrack>();
        try {
            JsonElement parsed = JsonParser.parseString(body);
            JsonArray array = parsed.isJsonArray() ? parsed.getAsJsonArray() : null;
            if (array == null) {
                return stations;
            }
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject object = element.getAsJsonObject();
                String name = object.has("name") ? object.get("name").getAsString().trim() : "";
                String url = object.has("url_resolved") && object.get("url_resolved").isJsonPrimitive()
                        ? object.get("url_resolved").getAsString()
                        : (object.has("url") ? object.get("url").getAsString() : null);
                if (name.isBlank() || url == null || url.isBlank()) {
                    continue;
                }
                String codec = object.has("codec") ? object.get("codec").getAsString() : "";
                String tags = object.has("tags") ? object.get("tags").getAsString() : "";
                String country = object.has("country") ? object.get("country").getAsString() : "";
                String favicon = object.has("favicon") ? object.get("favicon").getAsString() : null;
                String slug = object.has("stationuuid") ? object.get("stationuuid").getAsString() : null;
                String subtitle = tags.isBlank() ? country : tags.replace(',', ' ');
                if (subtitle.isBlank()) {
                    subtitle = codec.isBlank() ? "Интернет-радио" : codec;
                }
                stations.add(new MusicTrack(MusicTrack.Kind.RADIO, name, subtitle, url,
                        favicon == null || favicon.isBlank() ? null : favicon, "Радио", 0L, slug));
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Radio directory answer could not be parsed: {}", throwable.toString());
        }
        return stations;
    }
}
