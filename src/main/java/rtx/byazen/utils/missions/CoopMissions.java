package rtx.byazen.utils.missions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.impl.Utils.SessionStatsModule;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.cosmetics.Cosmetics;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;
import rtx.byazen.utils.web.WebBridge;

/**
 * Кооп-миссии клиента (идея №183 из IDEAS.md).
 * <p>
 * Клиентские челленджи для игры с друзьями: они считаются по вашему прогрессу и по времени, которое
 * вы проводите вместе (рядом есть другие игроки). Ничего не отправляется на сервер: прогресс лежит
 * локально, а кнопка «Код для друзей» собирает его в короткую строку — друг вставляет её у себя и
 * общий счёт группы складывается. За выполненные миссии выдаётся косметика.
 */
public final class CoopMissions {

    /** Одна миссия: метрика, цель и награда. */
    public record Mission(String id, String title, String hint, String metric, long goal, String reward) {
    }

    private static final CoopMissions INSTANCE = new CoopMissions();
    private static final List<Mission> LIST = new ArrayList<Mission>();
    private static final Map<String, Long> METRICS = new LinkedHashMap<String, Long>();
    private static final Set<String> DONE = new LinkedHashSet<String>();

    static {
        CoopMissions.add("together_10m", "Рядом десятку", "Проведите 10 минут рядом с другими игроками", "together", 600L, "snow");
        CoopMissions.add("together_hour", "Дружная команда", "Проведите вместе час", "together", 3600L, "neon");
        CoopMissions.add("walk_5000", "Общий путь", "Пройдите 5000 блоков", "distance", 5000L, "");
        CoopMissions.add("dig_1024", "Коллективная шахта", "Добудьте 1024 блока", "blocks", 1024L, "space");
        CoopMissions.add("kill_100", "Ночная охота", "Победите 100 мобов", "kills", 100L, "");
        CoopMissions.add("night_20m", "Ночная смена", "Сыграйте ночью 20 минут", "night", 1200L, "space");
        CoopMissions.add("high_low", "От неба до глубины", "Побывайте выше 200 и ниже 0", "extreme", 2L, "snow");
        CoopMissions.add("music_5", "Своя атмосфера", "Послушайте 5 разных треков", "tracks", 5L, "neon");
        CoopMissions.add("chat_50", "Разговорчивые", "Увидьте 50 сообщений в чате", "chat", 50L, "");
        CoopMissions.add("long_session", "Большая сессия", "Проведите в игре 2 часа за раз", "playtime", 7200000L, "neon");
    }

    private CoopMissions() {
    }

    private static void add(String id, String title, String hint, String metric, long goal, String reward) {
        LIST.add(new Mission(id, title, hint, metric, goal, reward));
    }

    public static CoopMissions get() {
        return INSTANCE;
    }

    public static List<Mission> list() {
        return new ArrayList<Mission>(LIST);
    }

    public static long progress(Mission mission) {
        return Math.min(mission.goal(), CoopMissions.metric(mission.metric()));
    }

    public static boolean done(String id) {
        CoopMissions.load();
        return DONE.contains(id);
    }

    public static Set<String> doneIds() {
        CoopMissions.load();
        return new LinkedHashSet<String>(DONE);
    }

    public static Map<String, Long> metrics() {
        CoopMissions.load();
        return new LinkedHashMap<String, Long>(METRICS);
    }

    public static int doneCount() {
        return CoopMissions.doneIds().size();
    }

    public static void ensure() {
        CoopMissions.load();
        if (!INSTANCE.subscribed) {
            INSTANCE.subscribed = true;
            try {
                EventBus.get().subscribe(INSTANCE);
            }
            catch (Throwable throwable) {
                ClientLog.warn("миссии: не удалось подписаться на события");
            }
        }
    }

    private boolean subscribed;
    private boolean loaded;
    private long lastPoll;
    private String lastTrack = "";
    private double bestY = -1.0;
    private double worstY = 1.0;
    private boolean wasHigh;
    private boolean wasDeep;

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastPoll < 1000L) {
            return;
        }
        long delta = this.lastPoll == 0L ? 1000L : now - this.lastPoll;
        this.lastPoll = now;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) {
            return;
        }
        try {
            if (this.groupNearby(client)) {
                CoopMissions.add("together", delta / 1000L);
            }
            long time = client.world.getTimeOfDay() % 24000L;
            if (time > 13000L && time < 23000L) {
                CoopMissions.add("night", delta / 1000L);
            }
            this.bestY = Math.max(this.bestY, client.player.getY());
            this.worstY = Math.min(this.worstY, client.player.getY());
            if (this.bestY > 200.0 && !this.wasHigh) {
                this.wasHigh = true;
                CoopMissions.add("extreme", 1L);
            }
            if (this.worstY < 0.0 && !this.wasDeep) {
                this.wasDeep = true;
                CoopMissions.add("extreme", 1L);
            }
            ModuleManager manager = ModuleManager.get();
            SessionStatsModule session = manager == null ? null : manager.get(SessionStatsModule.class);
            if (session != null) {
                CoopMissions.set("distance", (long)session.distance());
                CoopMissions.set("blocks", session.blocks());
                CoopMissions.set("kills", session.kills());
                CoopMissions.set("playtime", session.playtimeMs());
            }
            MusicEngine music = MusicEngine.get();
            if (music.isPlaying()) {
                String track = music.nowPlaying();
                if (track != null && !track.isBlank() && !track.equals(this.lastTrack)) {
                    this.lastTrack = track;
                    CoopMissions.add("tracks", 1L);
                }
            }
            CoopMissions.set("chat", (long)rtx.byazen.utils.chat.ChatHistory.entries().size());
            this.check();
        }
        catch (Throwable throwable) {
            ClientLog.debug("миссии: " + throwable.getClass().getSimpleName());
        }
    }

    /** Рядом есть другие игроки: считаем по сущностям, чтобы не зависеть от списка игроков сервера. */
    private boolean groupNearby(MinecraftClient client) {
        int nearby = 0;
        for (Entity entity : client.world.getEntities()) {
            if (!(entity instanceof PlayerEntity) || entity == client.player) {
                continue;
            }
            if (client.player.squaredDistanceTo(entity) <= 48.0 * 48.0) {
                ++nearby;
                if (nearby >= 1) {
                    return true;
                }
            }
        }
        return false;
    }

    private void check() {
        for (Mission mission : LIST) {
            if (DONE.contains(mission.id()) || CoopMissions.metric(mission.metric()) < mission.goal()) {
                continue;
            }
            DONE.add(mission.id());
            NotificationsModule.notify("§bМиссия выполнена: §f" + mission.title(), 4500L);
            ChatMessage.send("§bКооп-миссия: §f" + mission.title() + " §7· " + CoopMissions.summary());
            WebBridge.pushEvent("mission", mission.title());
            if (!mission.reward().isEmpty()) {
                int granted = Cosmetics.grantSet(mission.reward());
                if (granted > 0) {
                    NotificationsModule.notify("§bНаграда: набор «" + mission.reward() + "» — " + granted + " предметов", 4000L);
                }
            }
            this.save();
        }
    }

    public static void set(String metric, long value) {
        METRICS.put(metric, value);
    }

    public static void add(String metric, long delta) {
        METRICS.merge(metric, delta, Long::sum);
    }

    private static long metric(String name) {
        return METRICS.getOrDefault(name, 0L);
    }

    /** Строки для хаба. */
    public static List<String> rows() {
        CoopMissions.load();
        ArrayList<String> rows = new ArrayList<String>();
        for (Mission mission : LIST) {
            boolean finished = DONE.contains(mission.id());
            long progress = CoopMissions.progress(mission);
            rows.add((finished ? "§a✔ " : "§7• ") + mission.title() + " §8— "
                    + (finished ? "выполнена" : CoopMissions.format(mission.metric(), progress) + " из "
                    + CoopMissions.format(mission.metric(), mission.goal()))
                    + " §7· " + mission.hint() + (mission.reward().isEmpty() ? "" : " §8(награда: набор " + mission.reward() + ")"));
        }
        long together = CoopMissions.metric("together");
        rows.add("§7Вместе наиграно: " + CoopMissions.format("together", together) + " §7· выполнено "
                + DONE.size() + " из " + LIST.size());
        return rows;
    }

    private static String format(String metric, long value) {
        if (metric.equals("distance") || metric.equals("blocks")) {
            return value >= 1000L ? String.format(Locale.ROOT, "%.1fк", value / 1000.0) : value + " бл";
        }
        if (metric.equals("kills")) {
            return value + " шт";
        }
        if (metric.equals("tracks")) {
            return value + " тр";
        }
        if (metric.equals("extreme")) {
            return value + " из 2 точек";
        }
        if (metric.equals("chat")) {
            return value + " сообщ";
        }
        long seconds = value / 1000L;
        if (seconds >= 3600L) {
            return String.format(Locale.ROOT, "%d ч %d мин", seconds / 3600L, seconds % 3600L / 60L);
        }
        return seconds >= 60L ? (seconds / 60L) + " мин" : seconds + " с";
    }

    public static String summary() {
        CoopMissions.load();
        return "выполнено " + DONE.size() + " из " + LIST.size() + " · вместе "
                + CoopMissions.format("together", CoopMissions.metric("together"));
    }

    /** Код для друзей: прогресс группы можно сложить, обменявшись строкой. */
    public static String code() {
        CoopMissions.load();
        JsonObject root = new JsonObject();
        JsonArray doneArray = new JsonArray();
        DONE.forEach(doneArray::add);
        root.add("done", doneArray);
        JsonObject metricsJson = new JsonObject();
        for (Map.Entry<String, Long> entry : METRICS.entrySet()) {
            metricsJson.addProperty(entry.getKey(), entry.getValue());
        }
        root.add("metrics", metricsJson);
        String raw = root.toString();
        return "BZMIS1:" + Base64.getUrlEncoder().withoutPadding().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    /** Принимает код друга: складывает прогресс и достижения. */
    public static String applyCode(String code) {
        CoopMissions.load();
        String text = code == null ? "" : code.trim();
        int index = text.indexOf("BZMIS1:");
        if (index < 0) {
            return "Это не код миссий (нужен BZMIS1:…)";
        }
        try {
            String raw = new String(Base64.getUrlDecoder().decode(text.substring(index + 7).trim()), StandardCharsets.UTF_8);
            JsonObject root = com.google.gson.JsonParser.parseString(raw).getAsJsonObject();
            int addedDone = 0;
            int addedMetrics = 0;
            if (root.has("done")) {
                for (var element : root.getAsJsonArray("done")) {
                    if (DONE.add(element.getAsString())) {
                        ++addedDone;
                    }
                }
            }
            if (root.has("metrics")) {
                for (String key : root.getAsJsonObject("metrics").keySet()) {
                    long value = root.getAsJsonObject("metrics").get(key).getAsLong();
                    long local = METRICS.getOrDefault(key, 0L);
                    if (value > local) {
                        METRICS.put(key, value);
                        ++addedMetrics;
                    }
                }
            }
            INSTANCE.save();
            return "Код принят: миссий " + addedDone + ", метрик " + addedMetrics + " · " + CoopMissions.summary();
        }
        catch (Throwable throwable) {
            return "Код не читается — проверьте, что он скопирован целиком";
        }
    }

    public static void reset() {
        METRICS.clear();
        DONE.clear();
        CoopMissions.get().save();
    }

    private static void load() {
        if (INSTANCE.loaded) {
            return;
        }
        INSTANCE.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("coop_missions");
            if (root.has("done")) {
                for (var element : root.getAsJsonArray("done")) {
                    DONE.add(element.getAsString());
                }
            }
            if (root.has("metrics")) {
                for (String key : root.getAsJsonObject("metrics").keySet()) {
                    METRICS.put(key, root.getAsJsonObject("metrics").get(key).getAsLong());
                }
            }
        }
        catch (Throwable throwable) {
            ClientLog.warn("миссии: не удалось прочитать прогресс");
        }
    }

    private void save() {
        try {
            JsonObject root = new JsonObject();
            JsonArray doneArray = new JsonArray();
            DONE.forEach(doneArray::add);
            root.add("done", doneArray);
            JsonObject metricsJson = new JsonObject();
            for (Map.Entry<String, Long> entry : METRICS.entrySet()) {
                metricsJson.addProperty(entry.getKey(), entry.getValue());
            }
            root.add("metrics", metricsJson);
            RepositoryStorage.write("coop_missions", root);
        }
        catch (Throwable throwable) {
            ClientLog.warn("миссии: не удалось сохранить прогресс");
        }
    }
}
