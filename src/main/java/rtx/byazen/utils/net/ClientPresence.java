package rtx.byazen.utils.net;
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
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.utils.net.Endpoints;
import rtx.byazen.utils.profile.ProfileIdentity;

public final class ClientPresence {
    public static final ClientPresence INSTANCE = new ClientPresence();
    private static final long POLL_MS = 2000L;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(1L)).build();
    private final Set<String> online = ConcurrentHashMap.newKeySet();
    private ScheduledExecutorService executor;

    private ClientPresence() {
    }

    public synchronized void start() {
        if (this.executor != null) {
            return;
        }
        this.executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "byazen-presence");
            thread.setDaemon(true);
            return thread;
        });
        this.executor.scheduleWithFixedDelay(this::poll, 0L, 2000L, TimeUnit.MILLISECONDS);
    }

    private void poll() {
        try {
            String string = URLEncoder.encode(this.selfName(), StandardCharsets.UTF_8);
            URI uRI = URI.create(Endpoints.irc() + "/api/presence?me=" + string);
            HttpRequest httpRequest = HttpRequest.newBuilder(uRI).timeout(Duration.ofSeconds(2L)).header("Accept", "application/json").GET().build();
            HttpResponse<String> httpResponse = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                return;
            }
            JsonObject jsonObject = JsonParser.parseString((String)httpResponse.body()).getAsJsonObject();
            JsonElement jsonElement = jsonObject.get("online");
            if (jsonElement == null || !jsonElement.isJsonArray()) {
                return;
            }
            ConcurrentHashMap.KeySetView keySetView = ConcurrentHashMap.newKeySet();
            for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
                String string2;
                if (!jsonElement2.isJsonPrimitive() || (string2 = jsonElement2.getAsString().trim().toLowerCase(Locale.ROOT)).isEmpty()) continue;
                keySetView.add(string2);
            }
            this.online.clear();
            this.online.addAll(keySetView);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public synchronized void stop() {
        if (this.executor == null) {
            return;
        }
        this.executor.shutdownNow();
        this.executor = null;
        this.online.clear();
    }

    private String selfName() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.getSession() != null && minecraftClient.getSession().getUsername() != null && !minecraftClient.getSession().getUsername().isBlank()) {
            return minecraftClient.getSession().getUsername();
        }
        String string = ProfileIdentity.username(null);
        if (string != null) {
            return string;
        }
        return "Player";
    }

    public boolean hasOnline() {
        return !this.online.isEmpty();
    }

    public boolean isByAzenUser(String string) {
        if (string == null || string.isBlank() || this.online.isEmpty()) {
            return false;
        }
        return this.online.contains(string.trim().toLowerCase(Locale.ROOT));
    }

    public boolean isByAzenDisplay(String string) {
        if (string == null || string.isBlank() || this.online.isEmpty()) {
            return false;
        }
        for (String string2 : string.toLowerCase(Locale.ROOT).split("[^a-z0-9_]+")) {
            if (string2.isEmpty() || !this.online.contains(string2)) continue;
            return true;
        }
        return false;
    }
}

