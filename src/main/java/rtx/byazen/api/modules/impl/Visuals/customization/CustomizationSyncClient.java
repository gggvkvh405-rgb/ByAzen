package rtx.byazen.api.modules.impl.Visuals.customization;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CustomizationSyncClient {
    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build();
    private final AtomicBoolean connected = new AtomicBoolean();
    private final AtomicBoolean connecting = new AtomicBoolean();
    private final StringBuilder packetBuffer = new StringBuilder();
    private final ConcurrentMap<String, RemoteState> remoteStates = new ConcurrentHashMap<String, RemoteState>();
    private volatile WebSocket socket;
    private volatile boolean manualClose;
    private volatile String username = "";
    private volatile String headAccessory = "crown";
    private volatile String bodyModel = "royal";
    private volatile boolean wings;

    private String limit(String string, int n) {
        if (string == null) {
            return "";
        }
        return string.length() > n ? string.substring(0, n) : string;
    }

    public void pushState() {
        this.sendPacket("u");
    }

    private boolean readBoolean(JsonObject jsonObject, String string) {
        return jsonObject.has(string) && jsonObject.get(string).isJsonPrimitive() && jsonObject.get(string).getAsBoolean();
    }

    private String readString(JsonObject jsonObject, String string) {
        return jsonObject.has(string) && jsonObject.get(string).isJsonPrimitive() ? jsonObject.get(string).getAsString() : "";
    }

    public synchronized void connect(String string, int n) {
        URI uRI;
        if (this.connected.get() || this.connecting.get() || this.username.isBlank()) {
            return;
        }
        try {
            uRI = URI.create("ws://" + string.trim() + ":" + n);
        }
        catch (Exception exception) {
            return;
        }
        this.manualClose = false;
        this.connecting.set(true);
        this.httpClient.newWebSocketBuilder().connectTimeout(Duration.ofSeconds(6L)).buildAsync(uRI, (WebSocket.Listener)new SocketListener(this)).whenComplete((webSocket, throwable) -> {
            if (throwable != null) {
                this.connecting.set(false);
                this.connected.set(false);
                this.remoteStates.clear();
            }
        });
    }

    public boolean isConnected() {
        return this.connected.get();
    }

    private void handlePacket(String string) {
        if (string == null || string.isBlank()) {
            return;
        }
        try {
            JsonObject jsonObject = JsonParser.parseString((String)string).getAsJsonObject();
            String string2 = this.readString(jsonObject, "t");
            if ("s".equals(string2)) {
                this.remoteStates.clear();
                this.applyStates(this.readArray(jsonObject, "p"));
            } else if ("d".equals(string2)) {
                this.applyStates(this.readArray(jsonObject, "p"));
                for (JsonElement jsonElement : this.readArray(jsonObject, "r")) {
                    if (!jsonElement.isJsonPrimitive()) continue;
                    this.remoteStates.remove(this.normalizeKey(jsonElement.getAsString()));
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private JsonArray readArray(JsonObject jsonObject, String string) {
        return jsonObject.has(string) && jsonObject.get(string).isJsonArray() ? jsonObject.getAsJsonArray(string) : new JsonArray();
    }

    public synchronized void disconnect(String string) {
        this.manualClose = true;
        this.connecting.set(false);
        this.connected.set(false);
        this.remoteStates.clear();
        WebSocket webSocket = this.socket;
        this.socket = null;
        if (webSocket != null) {
            try {
                webSocket.sendClose(1000, this.limit(string, 120));
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    private void sendPacket(String string) {
        WebSocket webSocket = this.socket;
        if (!this.connected.get() || webSocket == null || this.username.isBlank()) {
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("t", string);
        jsonObject.addProperty("n", this.username);
        jsonObject.addProperty("c", Boolean.valueOf("crown".equals(this.headAccessory)));
        jsonObject.addProperty("a", this.headAccessory);
        jsonObject.addProperty("w", Boolean.valueOf(this.wings));
        jsonObject.addProperty("b", this.bodyModel);
        webSocket.sendText(this.gson.toJson((JsonElement)jsonObject), true);
    }

    public boolean isConnecting() {
        return this.connecting.get();
    }

    public void setLocalState(String string, String string2, boolean bl, String string3) {
        this.username = this.normalizeName(string);
        this.headAccessory = this.normalizeAccessory(string2);
        this.wings = bl;
        this.bodyModel = bl ? this.normalizeBodyModel(string3) : "none";
    }

    private String normalizeKey(String string) {
        return this.normalizeName(string).toLowerCase(Locale.ROOT);
    }

    private void applyStates(JsonArray jsonArray) {
        String string = this.normalizeKey(this.username);
        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject;
            String string2;
            String string3;
            if (!jsonElement.isJsonObject() || (string3 = this.normalizeKey(string2 = this.normalizeName(this.readString(jsonObject = jsonElement.getAsJsonObject(), "n")))).isBlank() || string3.equals(string)) continue;
            String string4 = this.normalizeAccessory(this.readString(jsonObject, "a"));
            if (!jsonObject.has("a") && this.readBoolean(jsonObject, "c")) {
                string4 = "crown";
            }
            boolean bl = this.readBoolean(jsonObject, "w");
            String string5 = jsonObject.has("b") ? this.normalizeBodyModel(this.readString(jsonObject, "b")) : "royal";
            this.remoteStates.put(string3, new RemoteState(string2, string4, bl, bl ? string5 : "none"));
        }
    }

    private String normalizeAccessory(String string) {
        if ("crown".equalsIgnoreCase(string)) {
            return "crown";
        }
        return "hat".equalsIgnoreCase(string) ? "hat" : "none";
    }

    private String normalizeBodyModel(String string) {
        if ("kagune".equalsIgnoreCase(string)) {
            return "kagune";
        }
        return "seraph".equalsIgnoreCase(string) ? "seraph" : "royal";
    }

    public RemoteState getRemoteState(String string) {
        return (RemoteState)(Object)this.remoteStates.get(this.normalizeKey(string));
    }

    private String normalizeName(String string) {
        return this.limit(string == null ? "" : string.trim(), 32);
    }

    public static record RemoteState(String name, String headAccessory, boolean wings, String bodyModel) {
        public boolean crown() { return "crown".equalsIgnoreCase(headAccessory); }
        public boolean hat() { return "hat".equalsIgnoreCase(headAccessory); }
    }

    private class SocketListener implements java.net.http.WebSocket.Listener {
        private final StringBuilder buffer = new StringBuilder();

        SocketListener(CustomizationSyncClient client) {}

        @Override
        public void onOpen(java.net.http.WebSocket ws) {
            connected.set(true);
            connecting.set(false);
            socket = ws;
            pushState();
            ws.request(1);
        }

        @Override
        public java.util.concurrent.CompletionStage<?> onText(java.net.http.WebSocket ws, CharSequence data, boolean last) {
            buffer.append(data);
            if (last) {
                handlePacket(buffer.toString());
                buffer.setLength(0);
            }
            ws.request(1);
            return null;
        }

        @Override
        public java.util.concurrent.CompletionStage<?> onClose(java.net.http.WebSocket ws, int statusCode, String reason) {
            connected.set(false);
            connecting.set(false);
            remoteStates.clear();
            return null;
        }

        @Override
        public void onError(java.net.http.WebSocket ws, Throwable error) {
            connected.set(false);
            connecting.set(false);
            remoteStates.clear();
        }
    }
}

