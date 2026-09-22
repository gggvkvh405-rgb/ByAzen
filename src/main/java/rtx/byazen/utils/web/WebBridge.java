package rtx.byazen.utils.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.impl.Utils.DeathHistoryModule;
import rtx.byazen.api.modules.impl.Utils.SessionStatsModule;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.utils.sounds.SoundManager;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Мост между веб-страницами и игрой (идеи №175 и №176 из IDEAS.md).
 * <p>
 * Страницы запрашивают данные и отправляют команды; команды попадают в очередь и выполняются
 * в игровом потоке (троттлинг раз в тик), потому что трогать игру из чужого потока нельзя.
 * Здесь же копится история для графиков и локальный рейтинг сессий — «рейтинг» из дашборда.
 */
public final class WebBridge {

    private static final WebBridge INSTANCE = new WebBridge();
    private static final int SERIES_LIMIT = 720;
    private static final int EVENT_LIMIT = 40;

    private final List<float[]> series = new ArrayList<float[]>();
    private final List<String[]> events = new ArrayList<String[]>();
    private final Map<String, Integer> toggles = new LinkedHashMap<String, Integer>();
    private final List<String[]> actions = new ArrayList<String[]>();
    private final Map<String, String> modules = new LinkedHashMap<String, String>();

    private boolean subscribed;
    private long lastSample;
    private long lastSessionSave;
    private boolean warnedLan;

    private WebBridge() {
    }

    public static WebBridge get() {
        return INSTANCE;
    }

    /** Страница просит действие: кладём в очередь, выполняем в игровом потоке. */
    public static synchronized JsonObject request(String action, String query) {
        JsonObject result = new JsonObject();
        if (action == null || action.isEmpty()) {
            result.addProperty("ok", false);
            result.addProperty("error", "нет действия");
            return result;
        }
        Map<String, String> params = WebBridge.params(query);
        INSTANCE.actions.add(new String[]{action, params.getOrDefault("do", params.getOrDefault("value", ""))});
        result.addProperty("ok", true);
        result.addProperty("queued", action);
        result.addProperty("pending", INSTANCE.actions.size());
        return result;
    }

    /** Запись события для ленты на странице (смерти, модули, музыка). */
    public static void pushEvent(String kind, String text) {
        synchronized (INSTANCE) {
            INSTANCE.events.add(0, new String[]{String.valueOf(System.currentTimeMillis()), kind, text});
            while (INSTANCE.events.size() > EVENT_LIMIT) {
                INSTANCE.events.remove(INSTANCE.events.size() - 1);
            }
        }
    }

    /** Текущее состояние игрока, музыки и клиента — то, что рисует компаньон. */
    public static JsonObject state() {
        JsonObject root = new JsonObject();
        MinecraftClient client = MinecraftClient.getInstance();
        root.addProperty("time", System.currentTimeMillis());
        root.addProperty("version", "1.7.1");
        if (client == null) {
            root.addProperty("online", false);
            return root;
        }
        root.addProperty("online", client.player != null && client.world != null);
        root.addProperty("fps", Math.round(client.getCurrentFps()));
        root.addProperty("player", client.getSession() == null ? "?" : client.getSession().getUsername());
        if (client.player != null && client.world != null) {
            JsonObject player = new JsonObject();
            player.addProperty("x", Math.round(client.player.getX()));
            player.addProperty("y", Math.round(client.player.getY()));
            player.addProperty("z", Math.round(client.player.getZ()));
            player.addProperty("health", Math.round(client.player.getHealth()));
            player.addProperty("maxHealth", Math.round(client.player.getMaxHealth()));
            player.addProperty("world", client.world.getRegistryKey().getValue().getPath());
            root.add("player_state", player);
        }
        SessionStatsModule session = ModuleManager.get().get(SessionStatsModule.class);
        if (session != null) {
            JsonObject stats = new JsonObject();
            stats.addProperty("playtimeMs", session.playtimeMs());
            stats.addProperty("distance", Math.round(session.distance()));
            stats.addProperty("blocks", session.blocks());
            stats.addProperty("kills", session.kills());
            root.add("session", stats);
        }
        MusicEngine music = MusicEngine.get();
        JsonObject track = new JsonObject();
        track.addProperty("playing", music.isPlaying());
        track.addProperty("paused", music.isPaused());
        track.addProperty("title", music.nowPlaying());
        track.addProperty("progress", Math.round(music.progress() * 100.0f));
        track.addProperty("volume", Math.round(music.volume() * 100.0f));
        track.addProperty("queue", music.queue().size());
        track.addProperty("state", music.state().name());
        root.add("music", track);
        int enabled = 0;
        for (Module module : ModuleManager.get().getAll()) {
            if (module.isEnabled()) {
                ++enabled;
            }
        }
        JsonObject modulesInfo = new JsonObject();
        modulesInfo.addProperty("enabled", enabled);
        modulesInfo.addProperty("total", ModuleManager.get().getAll().size());
        root.add("modules", modulesInfo);
        DeathHistoryModule deaths = ModuleManager.get().get(DeathHistoryModule.class);
        if (deaths != null) {
            root.addProperty("deaths", deaths.entries().size());
        }
        root.addProperty("web", LocalHttp.get().status());
        return root;
    }

    /** Ряды для графиков дашборда + локальный рейтинг сессий. */
    public static JsonObject stats() {
        JsonObject root = new JsonObject();
        JsonArray fps = new JsonArray();
        JsonArray memory = new JsonArray();
        synchronized (INSTANCE) {
            for (float[] sample : INSTANCE.series) {
                fps.add(Math.round(sample[0]));
                memory.add(Math.round(sample[1]));
            }
        }
        root.add("fps", fps);
        root.add("memory", memory);
        root.addProperty("samples", WebBridge.seriesCount());
        JsonObject rating = RepositoryStorage.readObject("web_sessions");
        root.add("rating", rating.has("sessions") ? rating.getAsJsonArray("sessions") : new JsonArray());
        JsonObject togglesJson = new JsonObject();
        synchronized (INSTANCE) {
            for (Map.Entry<String, Integer> entry : INSTANCE.toggles.entrySet()) {
                togglesJson.addProperty(entry.getKey(), entry.getValue());
            }
        }
        root.add("toggles", togglesJson);
        return root;
    }

    public static JsonObject eventsJson() {
        JsonObject root = new JsonObject();
        JsonArray array = new JsonArray();
        synchronized (INSTANCE) {
            for (String[] event : INSTANCE.events) {
                JsonObject object = new JsonObject();
                object.addProperty("time", Long.parseLong(event[0]));
                object.addProperty("kind", event[1]);
                object.addProperty("text", event[2]);
                array.add(object);
            }
        }
        root.add("events", array);
        return root;
    }

    private static int seriesCount() {
        synchronized (INSTANCE) {
            return INSTANCE.series.size();
        }
    }

    /** Подписывается на тики и разбирает очередь действий со страниц. */
    public static void ensure() {
        if (INSTANCE.subscribed) {
            return;
        }
        INSTANCE.subscribed = true;
        try {
            EventBus.get().subscribe(INSTANCE);
        }
        catch (Throwable ignored) {
        }
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        this.drainActions();
        this.sample();
    }

    private void sample() {
        long now = System.currentTimeMillis();
        if (now - this.lastSample < 1000L) {
            return;
        }
        this.lastSample = now;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        long usedMb = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L;
        synchronized (this) {
            this.series.add(new float[]{client.getCurrentFps(), usedMb});
            while (this.series.size() > SERIES_LIMIT) {
                this.series.remove(0);
            }
        }
        this.saveSession(now, client);
    }

    /** Раз в минуту кладём итоги сессии в локальный рейтинг (их показывает дашборд). */
    private void saveSession(long now, MinecraftClient client) {
        if (client.player == null || now - this.lastSessionSave < 60000L) {
            return;
        }
        this.lastSessionSave = now;
        SessionStatsModule session = ModuleManager.get().get(SessionStatsModule.class);
        if (session == null) {
            return;
        }
        JsonObject root = RepositoryStorage.readObject("web_sessions");
        JsonArray sessions = root.has("sessions") ? root.getAsJsonArray("sessions") : new JsonArray();
        JsonObject entry = new JsonObject();
        entry.addProperty("time", now);
        entry.addProperty("minutes", Math.round(session.playtimeMs() / 60000.0));
        entry.addProperty("distance", Math.round(session.distance()));
        entry.addProperty("blocks", session.blocks());
        entry.addProperty("kills", session.kills());
        sessions.add(entry);
        while (sessions.size() > 20) {
            sessions.remove(0);
        }
        JsonObject saved = new JsonObject();
        saved.add("sessions", sessions);
        RepositoryStorage.write("web_sessions", saved);
    }

    private void drainActions() {
        List<String[]> pending;
        synchronized (this) {
            if (this.actions.isEmpty()) {
                return;
            }
            pending = new ArrayList<String[]>(this.actions);
            this.actions.clear();
        }
        MinecraftClient client = MinecraftClient.getInstance();
        for (String[] action : pending) {
            try {
                this.run(action[0], action[1], client);
            }
            catch (Throwable throwable) {
                WebBridge.pushEvent("error", "Действие «" + action[0] + "» не выполнено: " + throwable.getClass().getSimpleName());
            }
        }
    }

    private void run(String action, String value, MinecraftClient client) {
        MusicEngine music = MusicEngine.get();
        switch (action.toLowerCase(Locale.ROOT)) {
            case "music": {
                switch (value.toLowerCase(Locale.ROOT)) {
                    case "play":
                    case "toggle": {
                        music.togglePause();
                        WebBridge.pushEvent("music", "Плеер: " + (music.isPaused() ? "пауза" : "играет"));
                        break;
                    }
                    case "next": {
                        music.next();
                        WebBridge.pushEvent("music", "Следующий трек: " + music.nowPlaying());
                        break;
                    }
                    case "prev":
                    case "previous": {
                        music.previous();
                        WebBridge.pushEvent("music", "Предыдущий трек: " + music.nowPlaying());
                        break;
                    }
                    case "stop": {
                        music.stop();
                        WebBridge.pushEvent("music", "Плеер остановлен");
                        break;
                    }
                    case "volume_up": {
                        music.setVolume(Math.min(1.0f, music.volume() + 0.05f));
                        break;
                    }
                    case "volume_down": {
                        music.setVolume(Math.max(0.0f, music.volume() - 0.05f));
                        break;
                    }
                    default: {
                        break;
                    }
                }
                break;
            }
            case "module": {
                Module module = ModuleManager.get().findByName(value);
                if (module == null) {
                    WebBridge.pushEvent("error", "Модуль «" + value + "» не найден");
                    return;
                }
                module.toggle();
                this.toggles.merge(module.getDisplayName(), 1, Integer::sum);
                String state = module.isEnabled() ? "включён" : "выключен";
                WebBridge.pushEvent("module", module.getDisplayName() + " " + state + " (со страницы)");
                if (client != null) {
                    ChatMessageBridge.say("§bByAzen: " + module.getDisplayName() + " " + state + " (с телефона)");
                }
                break;
            }
            case "notify": {
                NotificationsModule.notify("ByAzen: " + value, 3000L);
                break;
            }
            case "sound": {
                SoundManager.playSound(SoundManager.BUTTON_CLICK, 0.6f, 1.0f);
                break;
            }
            case "screenshot": {
                WebBridge.pushEvent("event", "Скриншот запрошен со страницы — нажмите F2");
                break;
            }
            default: {
                WebBridge.pushEvent("error", "Неизвестное действие: " + action);
                ClientLog.warn("веб-страница: неизвестное действие «" + action + "»");
            }
        }
    }

    private static Map<String, String> params(String query) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        if (query == null) {
            return map;
        }
        for (String part : query.split("&")) {
            int index = part.indexOf(61);
            if (index <= 0) {
                continue;
            }
            map.put(part.substring(0, index), part.substring(index + 1));
        }
        return map;
    }

    /** Токен из ссылки: страница сама подставляет его в запросы. */
    public static void warnLanOnce() {
        if (INSTANCE.warnedLan) {
            return;
        }
        INSTANCE.warnedLan = true;
        ClientLog.info("веб-страница: включён доступ из домашней сети");
        NotificationsModule.notify("Компаньон открыт из домашней сети — закройте порт, если сеть чужая", 5000L);
        WebBridge.pushEvent("event", "Веб-сервер открыт для домашней сети — не публикуйте ссылку с токеном");
    }

    /** Простая обёртка, чтобы не тянуть чат в этот класс напрямую. */
    static final class ChatMessageBridge {
        private ChatMessageBridge() {
        }

        static void say(String text) {
            rtx.byazen.utils.chat.ChatMessage.send(text);
        }
    }
}
