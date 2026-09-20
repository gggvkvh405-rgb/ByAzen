package rtx.kimiko.api.chat.irc;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import rtx.kimiko.api.chat.commands.CommandManager;
import rtx.kimiko.api.chat.irc.IrcMessage;
import rtx.kimiko.api.chat.irc.IrcPrefix;
import rtx.kimiko.utils.net.Endpoints;
import rtx.kimiko.utils.profile.ProfileIdentity;
import rtx.kimiko.utils.storage.RepositoryStorage;
import rtx.kimiko.utils.string.chat.helper.TextHelper;

public final class IrcClient {
    public static final IrcClient INSTANCE = new IrcClient();
    private static final long POLL_MS = 50L;
    private static final String STORE = "irc";
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(1L)).build();
    private final AtomicLong lastId = new AtomicLong(0L);
    private ScheduledExecutorService executor;
    private volatile boolean connected;
    private volatile boolean primed;
    private volatile int online;
    private volatile IrcPrefix prefix;
    private final Set<String> blocked = ConcurrentHashMap.newKeySet();
    private volatile boolean blockedLoaded;

    private IrcClient() {
    }

    public IrcPrefix prefix() {
        if (this.prefix == null) {
            this.loadPrefix();
        }
        return this.prefix;
    }

    public synchronized void start() {
        if (this.executor != null) {
            return;
        }
        this.loadPrefix();
        this.lastId.set(0L);
        this.primed = false;
        this.executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "kimiko-irc");
            thread.setDaemon(true);
            return thread;
        });
        this.executor.scheduleWithFixedDelay(this::poll, 0L, 50L, TimeUnit.MILLISECONDS);
    }

    public boolean unblock(String string) {
        if (string == null || string.isBlank()) {
            return false;
        }
        this.ensureBlockedLoaded();
        boolean bl = this.blocked.remove(string.trim().toLowerCase());
        if (bl) {
            this.saveStore();
        }
        return bl;
    }

    public boolean block(String string) {
        if (string == null || string.isBlank()) {
            return false;
        }
        this.ensureBlockedLoaded();
        boolean bl = this.blocked.add(string.trim().toLowerCase());
        if (bl) {
            this.saveStore();
        }
        return bl;
    }

    private void poll() {
        try {
            String string = URLEncoder.encode(this.effectiveName(), StandardCharsets.UTF_8);
            URI uRI = URI.create(Endpoints.irc() + "/api/messages?since=" + this.lastId.get() + "&me=" + string);
            HttpRequest httpRequest = HttpRequest.newBuilder(uRI).timeout(Duration.ofSeconds(2L)).header("Accept", "application/json").GET().build();
            HttpResponse<String> httpResponse = this.httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (httpResponse.statusCode() != 200) {
                this.connected = false;
                return;
            }
            JsonObject jsonObject = JsonParser.parseString((String)httpResponse.body()).getAsJsonObject();
            boolean bl = this.connected;
            this.connected = true;
            this.online = (int)IrcClient.number(jsonObject, "online", 0L);
            if (!bl) {
                this.statusLine("\u043f\u043e\u0434\u043a\u043b\u044e\u0447\u0435\u043d\u043e", Formatting.GREEN);
            }
            if (!this.primed) {
                this.lastId.set(IrcClient.number(jsonObject, "latest", 0L));
                this.primed = true;
                return;
            }
            JsonElement jsonElement = jsonObject.get("messages");
            if (jsonElement == null || !jsonElement.isJsonArray()) {
                return;
            }
            long l = this.lastId.get();
            for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
                boolean bl2;
                JsonObject jsonObject2;
                long l2;
                if (!jsonElement2.isJsonObject() || (l2 = IrcClient.number(jsonObject2 = jsonElement2.getAsJsonObject(), "id", 0L)) <= 0L) continue;
                IrcMessage ircMessage = new IrcMessage(l2, IrcClient.number(jsonObject2, "ts", System.currentTimeMillis()), IrcClient.string(jsonObject2, "user", "Player"), IrcClient.string(jsonObject2, "prefix", ""), IrcClient.string(jsonObject2, "text", ""), IrcClient.string(jsonObject2, "to", ""));
                boolean bl3 = bl2 = this.isBlocked(ircMessage.user()) && !this.isMe(ircMessage.user());
                if (!bl2 && !ircMessage.text().isBlank()) {
                    this.display(ircMessage);
                }
                if (l2 <= l) continue;
                l = l2;
            }
            this.lastId.set(l);
        }
        catch (InterruptedException interruptedException) {
            Thread.currentThread().interrupt();
        }
        catch (Exception exception) {
            this.connected = false;
        }
    }

    private static String string(JsonObject jsonObject, String string, String string2) {
        JsonElement jsonElement = jsonObject.get(string);
        if (jsonElement == null || !jsonElement.isJsonPrimitive()) {
            return string2;
        }
        return jsonElement.getAsString();
    }

    private void display(IrcMessage ircMessage) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.execute(() -> {
            if (minecraftClient.player == null) {
                return;
            }
            minecraftClient.player.sendMessage((Text)(ircMessage.isPrivate() ? this.privateLine(ircMessage) : this.publicLine(ircMessage)), false);
        });
    }

    private static long number(JsonObject jsonObject, String string, long l) {
        JsonElement jsonElement = jsonObject.get(string);
        if (jsonElement == null || !jsonElement.isJsonPrimitive() || !jsonElement.getAsJsonPrimitive().isNumber()) {
            return l;
        }
        return jsonElement.getAsLong();
    }

    public synchronized void stop() {
        if (this.executor == null) {
            return;
        }
        this.executor.shutdownNow();
        this.executor = null;
        this.connected = false;
        this.primed = false;
        this.online = 0;
    }

    public boolean isBlocked(String string) {
        if (string == null || string.isBlank()) {
            return false;
        }
        this.ensureBlockedLoaded();
        return this.blocked.contains(string.trim().toLowerCase());
    }

    private void loadPrefix() {
        try {
            JsonObject jsonObject = RepositoryStorage.readObject(STORE);
            this.prefix = jsonObject.has("prefix") ? IrcPrefix.fromId(jsonObject.get("prefix").getAsString()) : IrcPrefix.NONE;
        }
        catch (Exception exception) {
            this.prefix = IrcPrefix.NONE;
        }
    }

    private boolean isMe(String string) {
        return string != null && string.equalsIgnoreCase(this.effectiveName());
    }

    private MutableText publicLine(IrcMessage ircMessage) {
        MutableText mutableText = IrcClient.brand();
        mutableText.append(IrcPrefix.fromId(ircMessage.prefix()).component());
        mutableText.append((Text)IrcClient.nick(ircMessage.user()));
        mutableText.append((Text)Text.literal((String)" \u2192 ").formatted(Formatting.DARK_GRAY));
        mutableText.append((Text)Text.literal((String)ircMessage.text()).formatted(Formatting.GRAY));
        return mutableText;
    }

    private void saveStore() {
        this.ensureBlockedLoaded();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("prefix", this.prefix == null ? IrcPrefix.NONE.id() : this.prefix.id());
        JsonArray jsonArray = new JsonArray();
        for (String string : this.blocked) {
            jsonArray.add(string);
        }
        jsonObject.add("blocked", (JsonElement)jsonArray);
        RepositoryStorage.write(STORE, jsonObject);
    }

    private static MutableText meLabel() {
        return Text.literal((String)"\u044f").formatted(Formatting.WHITE);
    }

    public boolean isConnected() {
        return this.connected;
    }

    public boolean isRunning() {
        return this.executor != null;
    }

    public static MutableText brand() {
        return (MutableText)TextHelper.applyPredefinedGradient("[IRC] ", "orange_white", false);
    }

    public void setPrefix(IrcPrefix ircPrefix) {
        this.prefix = ircPrefix == null ? IrcPrefix.NONE : ircPrefix;
        this.saveStore();
    }

    private void statusLine(String string, Formatting formatting) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        minecraftClient.execute(() -> {
            if (minecraftClient.player == null) {
                return;
            }
            minecraftClient.player.sendMessage((Text)IrcClient.brand().append((Text)Text.literal((String)string).formatted(formatting)), false);
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void ensureBlockedLoaded() {
        if (this.blockedLoaded) {
            return;
        }
        Set<String> set = this.blocked;
        synchronized (set) {
            if (this.blockedLoaded) {
                return;
            }
            try {
                JsonObject jsonObject = RepositoryStorage.readObject(STORE);
                if (jsonObject.has("blocked") && jsonObject.get("blocked").isJsonArray()) {
                    for (JsonElement jsonElement : jsonObject.getAsJsonArray("blocked")) {
                        String string;
                        if (!jsonElement.isJsonPrimitive() || (string = jsonElement.getAsString().trim().toLowerCase()).isEmpty()) continue;
                        this.blocked.add(string);
                    }
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.blockedLoaded = true;
        }
    }

    public List<String> blockedNames() {
        this.ensureBlockedLoaded();
        ArrayList<String> arrayList = new ArrayList<String>(new LinkedHashSet<String>(this.blocked));
        arrayList.sort(String.CASE_INSENSITIVE_ORDER);
        return arrayList;
    }

    private static String serverError(String string, int n) {
        try {
            String string2;
            JsonObject jsonObject = JsonParser.parseString((String)string).getAsJsonObject();
            if (jsonObject.has("error") && jsonObject.get("error").isJsonPrimitive() && !(string2 = jsonObject.get("error").getAsString().trim()).isEmpty()) {
                return string2;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return "\u041d\u0435 \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u043e (HTTP " + n + ")";
    }

    private MutableText privateLine(IrcMessage ircMessage) {
        boolean bl = this.isMe(ircMessage.user());
        MutableText mutableText = bl ? IrcClient.meLabel() : IrcClient.nick(ircMessage.user());
        MutableText mutableText2 = bl ? IrcClient.nick(ircMessage.to()) : IrcClient.meLabel();
        MutableText mutableText3 = IrcClient.brand();
        mutableText3.append(IrcPrefix.fromId(ircMessage.prefix()).component());
        mutableText3.append((Text)Text.literal((String)"[").formatted(Formatting.LIGHT_PURPLE));
        mutableText3.append((Text)mutableText);
        mutableText3.append((Text)Text.literal((String)" \u2192 ").formatted(Formatting.LIGHT_PURPLE));
        mutableText3.append((Text)mutableText2);
        mutableText3.append((Text)Text.literal((String)"] ").formatted(Formatting.LIGHT_PURPLE));
        mutableText3.append((Text)Text.literal((String)ircMessage.text()).formatted(Formatting.GRAY));
        return mutableText3;
    }

    public String effectiveName() {
        String string = ProfileIdentity.username(null);
        if (string != null) {
            return string;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.getSession() != null && minecraftClient.getSession().getUsername() != null && !minecraftClient.getSession().getUsername().isBlank()) {
            return minecraftClient.getSession().getUsername();
        }
        return "Player";
    }

    private void notifyError(String string) {
        this.statusLine(string, Formatting.RED);
    }

    public void send(String string) {
        this.send(string, null);
    }

    public void send(String string, String string2) {
        if (string == null || string.isBlank()) {
            return;
        }
        String string3 = this.effectiveName();
        String string4 = this.prefix().id();
        String string5 = string2 == null ? "" : string2.trim();
        Thread thread = new Thread(() -> {
            try {
                HttpRequest httpRequest;
                HttpResponse<String> httpResponse;
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("user", string3);
                jsonObject.addProperty("prefix", string4);
                jsonObject.addProperty("text", string);
                int n = ProfileIdentity.uid();
                if (n > 0) {
                    jsonObject.addProperty("uid", (Number)n);
                }
                if (!string5.isEmpty()) {
                    jsonObject.addProperty("to", string5);
                }
                if ((httpResponse = this.httpClient.send(httpRequest = HttpRequest.newBuilder(URI.create(Endpoints.irc() + "/api/send")).timeout(Duration.ofSeconds(2L)).header("Content-Type", "application/json").POST(HttpRequest.BodyPublishers.ofString(jsonObject.toString(), StandardCharsets.UTF_8)).build(), HttpResponse.BodyHandlers.ofString())).statusCode() != 200) {
                    this.notifyError(IrcClient.serverError(httpResponse.body(), httpResponse.statusCode()));
                }
            }
            catch (InterruptedException interruptedException) {
                Thread.currentThread().interrupt();
            }
            catch (Exception exception) {
                this.notifyError("\u0427\u0430\u0442 \u043d\u0435\u0434\u043e\u0441\u0442\u0443\u043f\u0435\u043d (\u0437\u0430\u043f\u0443\u0441\u0442\u0438 \u0441\u0435\u0440\u0432\u0435\u0440 \u0432 Desktop/Irc)");
            }
        }, "kimiko-irc-send");
        thread.setDaemon(true);
        thread.start();
    }

    public int online() {
        return this.online;
    }

    private static MutableText nick(String string) {
        String string2 = CommandManager.get().getPrefix();
        return Text.literal((String)string).styled(style -> style.withColor(Formatting.WHITE).withClickEvent((ClickEvent)new ClickEvent.SuggestCommand(string2 + "irc pm " + string + " ")).withHoverEvent((HoverEvent)new HoverEvent.ShowText((Text)Text.literal((String)("\u041d\u0430\u043f\u0438\u0441\u0430\u0442\u044c " + string)).formatted(Formatting.GRAY))));
    }
}

