package rtx.byazen.utils.quests;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.impl.Utils.SessionStatsModule;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.api.nav.WaypointStore;
import rtx.byazen.utils.achievements.Achievements;
import rtx.byazen.utils.chat.ChatHistory;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.cosmetics.Cosmetics;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;
import rtx.byazen.utils.web.WebBridge;

/**
 * Ежедневные задания (идея №202 из IDEAS.md).
 * <p>
 * Три задания на день из общего пула: они считаются по тому, что вы и так делаете — играете,
 * ходите, добываете, сражаетесь, слушаете музыку, общаетесь в чате, ставите путевые точки.
 * За каждое задание — очки в профиль достижений, за серию дней — косметика. Всё локально:
 * задания не нагружают сервер и никак не влияют на игру.
 */
public final class DailyQuests {

    /** Задание: метрика, цель и награда в очках. */
    public record Quest(String id, String title, String hint, String metric, long goal, int points) {
    }

    private static final int PER_DAY = 3;
    private static final int STREAK_REWARD = 7;
    private static final List<Quest> POOL = new ArrayList<Quest>();
    private static final Map<String, Long> METRICS = new LinkedHashMap<String, Long>();
    private static final Set<String> DONE = new LinkedHashSet<String>();
    private static final DailyQuests INSTANCE = new DailyQuests();

    static {
        DailyQuests.add("warmup", "Разминка", "Проведите в игре 10 минут", "playtime", 600000L, 10);
        DailyQuests.add("hour", "Час в деле", "Наиграйте сегодня час", "playtime", 3600000L, 20);
        DailyQuests.add("walker", "Пешеход", "Пройдите 500 блоков", "distance", 500L, 10);
        DailyQuests.add("runner", "Дальний путь", "Пройдите 2000 блоков", "distance", 2000L, 20);
        DailyQuests.add("miner", "Шахтёр", "Добудьте 128 блоков", "blocks", 128L, 10);
        DailyQuests.add("digger", "Глубокая смена", "Добудьте 512 блоков", "blocks", 512L, 20);
        DailyQuests.add("hunter", "Охотник", "Победите 25 мобов", "kills", 25L, 15);
        DailyQuests.add("slayer", "Серьёзная охота", "Победите 75 мобов", "kills", 75L, 25);
        DailyQuests.add("dj", "Ди-джей", "Послушайте 3 трека", "tracks", 3L, 10);
        DailyQuests.add("talker", "Общительный", "Увидьте 15 сообщений в чате", "chat", 15L, 10);
        DailyQuests.add("explorer", "Исследователь", "Поставьте 2 путевые точки", "points", 2L, 15);
        DailyQuests.add("night", "Ночная смена", "Проведите ночью 15 минут", "night", 900000L, 15);
    }

    private DailyQuests() {
    }

    private static void add(String id, String title, String hint, String metric, long goal, int points) {
        POOL.add(new Quest(id, title, hint, metric, goal, points));
    }

    public static DailyQuests get() {
        return INSTANCE;
    }

    private boolean subscribed;
    private boolean loaded;
    private long lastPoll;
    private long day;
    private int streak;
    private boolean lastDayDone;
    private String lastTrack = "";
    private long lastPlaytime;
    private long lastDistance;
    private long lastBlocks;
    private long lastKills;
    private long lastChat;
    private long lastPoints;
    private int rewardedStreak;

    /** Подписывается на тики и подтягивает сохранённое состояние. */
    public static void ensure() {
        DailyQuests.load();
        if (!INSTANCE.subscribed) {
            INSTANCE.subscribed = true;
            try {
                if (ModuleManager.get() == null) {
                    INSTANCE.subscribed = false;
                }
                else {
                    EventBus.get().subscribe(INSTANCE);
                }
            }
            catch (Throwable throwable) {
                ClientLog.warn("задания: не удалось подписаться на события");
            }
        }
    }

    /** Сегодняшние задания: выбираются из пула по дате, поэтому у всех в один день они совпадают. */
    public static List<Quest> today() {
        DailyQuests.rollDay();
        ArrayList<Quest> list = new ArrayList<Quest>();
        int size = POOL.size();
        for (int i = 0; i < PER_DAY; ++i) {
            int index = (int)((INSTANCE.day * 31L + (long)i * 7L) % (long)size);
            Quest quest = POOL.get(index);
            boolean unique = true;
            for (Quest taken : list) {
                if (taken.id().equals(quest.id())) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                list.add(quest);
            }
        }
        int probe = 0;
        while (list.size() < PER_DAY && probe < size) {
            Quest quest = POOL.get(probe++);
            boolean unique = true;
            for (Quest taken : list) {
                if (taken.id().equals(quest.id())) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                list.add(quest);
            }
        }
        return list;
    }

    public static long progress(Quest quest) {
        DailyQuests.load();
        return Math.min(quest.goal(), METRICS.getOrDefault(quest.metric(), 0L));
    }

    public static boolean done(String id) {
        DailyQuests.load();
        return DONE.contains(id);
    }

    public static int doneToday() {
        DailyQuests.load();
        int count = 0;
        for (Quest quest : DailyQuests.today()) {
            if (DONE.contains(quest.id())) {
                ++count;
            }
        }
        return count;
    }

    public static int streak() {
        DailyQuests.load();
        return INSTANCE.streak;
    }

    public static int pointsToday() {
        DailyQuests.load();
        int points = 0;
        for (Quest quest : DailyQuests.today()) {
            if (DONE.contains(quest.id())) {
                points += quest.points();
            }
        }
        return points;
    }

    public static boolean allDoneToday() {
        return DailyQuests.doneToday() >= PER_DAY;
    }

    /** Строки для хаба: прогресс каждого задания и серия дней. */
    public static List<String> rows() {
        DailyQuests.load();
        ArrayList<String> rows = new ArrayList<String>();
        for (Quest quest : DailyQuests.today()) {
            boolean finished = DONE.contains(quest.id());
            rows.add((finished ? "§a✔ " : "§7• ") + quest.title() + " §8— " + quest.hint() + " §7· "
                    + (finished ? "выполнено" : DailyQuests.format(quest.metric(), DailyQuests.progress(quest)) + " из "
                    + DailyQuests.format(quest.metric(), quest.goal()))
                    + " §8(+" + quest.points() + " очков)");
        }
        rows.add("§7Сегодня выполнено " + DailyQuests.doneToday() + " из " + PER_DAY
                + " · очков " + DailyQuests.pointsToday() + " · серия " + INSTANCE.streak + " дн.");
        rows.add(INSTANCE.streak >= STREAK_REWARD
                ? "§7Косметика за серию уже получена — дальше просто держите ритм"
                : "§7Серия " + STREAK_REWARD + " дней подряд даёт косметику");
        rows.add("§8Задания выбираются по дате: у друзей сегодня те же три");
        return rows;
    }

    public static String summary() {
        DailyQuests.load();
        return "задания: " + DailyQuests.doneToday() + " из " + PER_DAY + " · серия " + INSTANCE.streak + " дн. · очков "
                + DailyQuests.pointsToday();
    }

    private static String format(String metric, long value) {
        if (metric.equals("playtime") || metric.equals("night")) {
            long seconds = value / 1000L;
            if (seconds >= 3600L) {
                return String.format(Locale.ROOT, "%d ч %d мин", seconds / 3600L, seconds % 3600L / 60L);
            }
            return seconds >= 60L ? (seconds / 60L) + " мин" : seconds + " с";
        }
        if (metric.equals("distance") || metric.equals("blocks")) {
            return value >= 1000L ? String.format(Locale.ROOT, "%.1fк", value / 1000.0) : value + " бл";
        }
        if (metric.equals("kills")) {
            return value + " моб";
        }
        if (metric.equals("tracks")) {
            return value + " трек";
        }
        if (metric.equals("chat")) {
            return value + " сообщ";
        }
        if (metric.equals("points")) {
            return value + " точ";
        }
        return String.valueOf(value);
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost() || ModuleManager.get() == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastPoll < 1000L) {
            return;
        }
        long delta = this.lastPoll == 0L ? 1000L : now - this.lastPoll;
        this.lastPoll = now;
        MinecraftClient client = MinecraftClient.getInstance();
        DailyQuests.rollDay();
        if (client == null || client.player == null || client.world == null) {
            return;
        }
        try {
            this.poll(client, delta);
        }
        catch (Throwable throwable) {
            ClientLog.debug("задания: " + throwable.getClass().getSimpleName());
        }
    }

    private void poll(MinecraftClient client, long delta) {
        ModuleManager manager = ModuleManager.get();
        SessionStatsModule session = manager == null ? null : manager.get(SessionStatsModule.class);
        if (session != null) {
            this.absorb("playtime", session.playtimeMs());
            this.absorb("distance", (long)session.distance());
            this.absorb("blocks", session.blocks());
            this.absorb("kills", session.kills());
        }
        long time = client.world.getTimeOfDay() % 24000L;
        if (time > 13000L && time < 23000L) {
            DailyQuests.add("night", delta);
        }
        MusicEngine music = MusicEngine.get();
        if (music.isPlaying()) {
            String track = music.nowPlaying();
            if (track != null && !track.isBlank() && !track.equals(this.lastTrack)) {
                this.lastTrack = track;
                DailyQuests.add("tracks", 1L);
            }
        }
        this.absorb("chat", (long)ChatHistory.entries().size());
        this.absorb("points", (long)WaypointStore.get().size());
        DailyQuests.check();
    }

    /** Счётчики сессии сбрасываются при выходе из мира: учитываем только рост. */
    private void absorb(String metric, long value) {
        long last;
        switch (metric) {
            case "playtime": {
                last = this.lastPlaytime;
                this.lastPlaytime = value;
                break;
            }
            case "distance": {
                last = this.lastDistance;
                this.lastDistance = value;
                break;
            }
            case "blocks": {
                last = this.lastBlocks;
                this.lastBlocks = value;
                break;
            }
            case "kills": {
                last = this.lastKills;
                this.lastKills = value;
                break;
            }
            case "chat": {
                last = this.lastChat;
                this.lastChat = value;
                break;
            }
            case "points": {
                last = this.lastPoints;
                this.lastPoints = value;
                break;
            }
            default: {
                return;
            }
        }
        long delta = value - last;
        if (delta > 0L) {
            DailyQuests.add(metric, delta);
        }
    }

    private static void check() {
        int before = DONE.size();
        for (Quest quest : DailyQuests.today()) {
            if (DONE.contains(quest.id()) || METRICS.getOrDefault(quest.metric(), 0L) < quest.goal()) {
                continue;
            }
            DONE.add(quest.id());
            Achievements.add("quests", 1L);
            NotificationsModule.notify("§bЗадание выполнено: §f" + quest.title() + " §7(+" + quest.points() + ")", 4000L);
            WebBridge.pushEvent("quest", quest.title());
            INSTANCE.save();
        }
        if (DONE.size() != before) {
            ChatMessage.send("§bЗадание дня: §f" + DailyQuests.summary());
        }
        if (DailyQuests.allDoneToday() && !INSTANCE.lastDayDone) {
            INSTANCE.lastDayDone = true;
            Achievements.add("quest_days", 1L);
            ChatMessage.send("§aВсе задания дня выполнены! §7" + DailyQuests.summary());
            INSTANCE.save();
            DailyQuests.rewardStreak();
        }
    }

    /** Косметика за серию: один раз на каждые 7 дней подряд. */
    private static void rewardStreak() {
        if (INSTANCE.streak <= 0 || INSTANCE.streak % STREAK_REWARD != 0 || INSTANCE.rewardedStreak >= INSTANCE.streak) {
            return;
        }
        INSTANCE.rewardedStreak = INSTANCE.streak;
        int granted = Cosmetics.grantSet("space");
        if (granted > 0) {
            NotificationsModule.notify("§bСерия " + INSTANCE.streak + " дней: набор «Космос» — предметов " + granted, 5000L);
        }
        else {
            ChatMessage.send("§bСерия " + INSTANCE.streak + " дней подряд — так держать!");
        }
        INSTANCE.save();
    }

    private static void rollDay() {
        DailyQuests.load();
        long today = java.time.LocalDate.now().toEpochDay();
        if (INSTANCE.day == today) {
            return;
        }
        if (INSTANCE.day != 0L) {
            INSTANCE.streak = INSTANCE.lastDayDone ? INSTANCE.streak + 1 : 0;
        }
        INSTANCE.day = today;
        INSTANCE.lastDayDone = false;
        METRICS.clear();
        DONE.clear();
        INSTANCE.lastTrack = "";
        INSTANCE.lastPlaytime = 0L;
        INSTANCE.lastDistance = 0L;
        INSTANCE.lastBlocks = 0L;
        INSTANCE.lastKills = 0L;
        INSTANCE.lastChat = 0L;
        INSTANCE.lastPoints = 0L;
        INSTANCE.save();
    }

    public static void add(String metric, long delta) {
        METRICS.merge(metric, delta, Long::sum);
    }

    public static void reset() {
        METRICS.clear();
        DONE.clear();
        INSTANCE.lastDayDone = false;
        INSTANCE.rewardedStreak = 0;
        INSTANCE.save();
    }

    private static void load() {
        if (INSTANCE.loaded) {
            return;
        }
        INSTANCE.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("daily_quests");
            if (root.has("day")) {
                INSTANCE.day = root.get("day").getAsLong();
            }
            if (root.has("streak")) {
                INSTANCE.streak = root.get("streak").getAsInt();
            }
            if (root.has("rewardedStreak")) {
                INSTANCE.rewardedStreak = root.get("rewardedStreak").getAsInt();
            }
            if (root.has("lastDayDone")) {
                INSTANCE.lastDayDone = root.get("lastDayDone").getAsBoolean();
            }
            if (root.has("metrics")) {
                for (String key : root.getAsJsonObject("metrics").keySet()) {
                    METRICS.put(key, root.getAsJsonObject("metrics").get(key).getAsLong());
                }
            }
            if (root.has("done")) {
                for (var element : root.getAsJsonArray("done")) {
                    DONE.add(element.getAsString());
                }
            }
        }
        catch (Throwable throwable) {
            ClientLog.warn("задания: не удалось прочитать сохранённое состояние");
        }
    }

    private void save() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("day", INSTANCE.day);
            root.addProperty("streak", INSTANCE.streak);
            root.addProperty("rewardedStreak", INSTANCE.rewardedStreak);
            root.addProperty("lastDayDone", INSTANCE.lastDayDone);
            JsonObject metrics = new JsonObject();
            for (Map.Entry<String, Long> entry : METRICS.entrySet()) {
                metrics.addProperty(entry.getKey(), entry.getValue());
            }
            root.add("metrics", metrics);
            com.google.gson.JsonArray done = new com.google.gson.JsonArray();
            DONE.forEach(done::add);
            root.add("done", done);
            RepositoryStorage.write("daily_quests", root);
        }
        catch (Throwable throwable) {
            ClientLog.warn("задания: не удалось сохранить состояние");
        }
    }
}
