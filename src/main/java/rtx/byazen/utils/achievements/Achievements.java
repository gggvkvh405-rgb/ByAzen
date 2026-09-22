package rtx.byazen.utils.achievements;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Utils.AchievementsModule;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.impl.Utils.SessionStatsModule;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.config.ChangeLog;
import rtx.byazen.utils.cosmetics.Cosmetics;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.scripts.ScriptEngine;
import rtx.byazen.utils.storage.RepositoryStorage;
import rtx.byazen.utils.web.LocalHttp;
import rtx.byazen.utils.web.WebBridge;

/**
 * Достижения клиента (идея №185 из IDEAS.md).
 * <p>
 * Клиент сам считает, как вы играете и чем пользуетесь: часы в игре, пройденные блоки, добыча,
 * бои, музыка, правила, панель для телефона, косметика. За каждым достижением — понятная цель и
 * счётчик, часть достижений выдаёт косметику. Прогресс виден в хабе ByAzen, ничего никуда не
 * отправляется.
 */
public final class Achievements {

    /** Одно достижение: цель и как её считать. */
    public record Ach(String id, String title, String hint, String category, long goal, String metric, String reward) {
    }

    private static final Achievements INSTANCE = new Achievements();
    private static final List<Ach> LIST = new ArrayList<Ach>();
    private static final Set<String> UNLOCKED = new LinkedHashSet<String>();
    private static final Map<String, Long> METRICS = new LinkedHashMap<String, Long>();

    static {
        Achievements.add("first_login", "Первый вход", "Запустите клиент ByAzen", "Первые шаги", 1L, "sessions", "");
        Achievements.add("first_module", "Первый модуль", "Включите любой модуль", "Первые шаги", 1L, "toggles", "");
        Achievements.add("ten_modules", "Настройщик", "Переключите модули 10 раз", "Первые шаги", 10L, "toggles", "");
        Achievements.add("hundred_modules", "Хозяин клиента", "Переключите модули 100 раз", "Первые шаги", 100L, "toggles", "set:snow");
        Achievements.add("configs_saved", "Аккуратный", "Сохраните настройки 5 раз", "Первые шаги", 5L, "configs", "");
        Achievements.add("compat_check", "Всё по полочкам", "Откройте хаб на вкладке достижений", "Первые шаги", 1L, "hub_opened", "");

        Achievements.add("first_quest", "Первое задание", "Выполните задание дня", "Задания", 1L, "quests", "");
        Achievements.add("quest_week", "Ритм недели", "Выполните все задания 7 дней подряд", "Задания", 7L, "quest_days", "set:snow");
        Achievements.add("quest_month", "Месяц в ритме", "Выполните все задания 30 дней подряд", "Задания", 30L, "quest_days", "set:space");

        Achievements.add("hour", "Час в игре", "Проведите в игре час", "Игра", 3600000L, "playtime", "");
        Achievements.add("ten_hours", "Десять часов", "Проведите в игре 10 часов", "Игра", 36000000L, "playtime", "");
        Achievements.add("fifty_hours", "Ветеран", "Проведите в игре 50 часов", "Игра", 180000000L, "playtime", "set:neon");
        Achievements.add("walk_1k", "Пешеход", "Пройдите 1000 блоков", "Игра", 1000L, "distance", "");
        Achievements.add("walk_50k", "Путешественник", "Пройдите 50 000 блоков", "Игра", 50000L, "distance", "set:space");
        Achievements.add("dig_512", "Копатель", "Добудьте 512 блоков", "Игра", 512L, "blocks", "");
        Achievements.add("dig_10k", "Шахтёр", "Добудьте 10 000 блоков", "Игра", 10000L, "blocks", "");
        Achievements.add("kill_50", "Охотник", "Победите 50 мобов", "Игра", 50L, "kills", "");
        Achievements.add("kill_500", "Гроза мобов", "Победите 500 мобов", "Игра", 500L, "kills", "set:neon");
        Achievements.add("sky_high", "Высоко", "Побывайте выше 200 блоков", "Игра", 1L, "sky", "");
        Achievements.add("deep_low", "Глубоко", "Спуститесь ниже нуля", "Игра", 1L, "deep", "");
        Achievements.add("fight_review", "Работа над ошибками", "Получите разбор боя после смерти", "Игра", 1L, "reviews", "");

        Achievements.add("music_first", "Первый трек", "Включите музыку в клиенте", "Музыка", 1L, "tracks", "");
        Achievements.add("music_ten", "Меломан", "Послушайте 10 разных треков", "Музыка", 10L, "tracks", "set:snow");
        Achievements.add("music_night", "Ночной эфир", "Послушайте музыку в темное время", "Музыка", 1L, "night_music", "");

        Achievements.add("script_first", "Своё правило", "Загрузите хотя бы одно правило", "Клиент", 1L, "scripts", "");
        Achievements.add("script_ten", "Правитель", "Держите 10 правил сразу", "Клиент", 10L, "scripts", "set:space");
        Achievements.add("voice_learn", "Свой голос", "Обучите голосовую команду", "Клиент", 1L, "voice", "");
        Achievements.add("phone_page", "В кармане", "Откройте страницу компаньона", "Клиент", 1L, "web", "");
        Achievements.add("web_100", "Сто запросов", "Сделайте 100 запросов к панели", "Клиент", 100L, "web", "");
        Achievements.add("hud_api", "Место для друзей", "Зарегистрируйте элемент в HUD API", "Клиент", 1L, "hud", "");

        Achievements.add("cosmetic_first", "Первый образ", "Получите любую косметику", "Косметика", 1L, "cosmetics", "");
        Achievements.add("cosmetic_ten", "Гардероб", "Соберите 10 предметов косметики", "Косметика", 10L, "cosmetics", "set:neon");
        Achievements.add("season_set", "Сезонный", "Заберите сезонный набор", "Косметика", 1L, "season", "");
        Achievements.add("theme_code", "Своя тема", "Примените тему по коду", "Косметика", 1L, "theme", "");
    }

    private Achievements() {
    }

    private static void add(String id, String title, String hint, String category, long goal, String metric, String reward) {
        LIST.add(new Ach(id, title, hint, category, goal, metric, reward));
    }

    public static Achievements get() {
        return INSTANCE;
    }

    public static List<Ach> list() {
        return new ArrayList<Ach>(LIST);
    }

    public static List<String> categories() {
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        for (Ach ach : LIST) {
            set.add(ach.category());
        }
        return new ArrayList<String>(set);
    }

    public static long progress(Ach ach) {
        return Math.min(ach.goal(), Achievements.metric(ach.metric()));
    }

    public static boolean unlocked(String id) {
        Achievements.load();
        return UNLOCKED.contains(id);
    }

    public static int unlockedCount() {
        Achievements.load();
        return UNLOCKED.size();
    }

    public static int total() {
        return LIST.size();
    }

    /** Очки и уровень профиля: 10 очков за достижение, уровень каждые 100 очков. */
    public static int points() {
        return Achievements.unlockedCount() * 10;
    }

    public static int level() {
        return 1 + Achievements.points() / 100;
    }

    public static String profile() {
        return "уровень " + Achievements.level() + " · очки " + Achievements.points() + " · достижений "
                + Achievements.unlockedCount() + " из " + Achievements.total();
    }

    /** Строки для хаба: «выполнено/нет, прогресс». */
    public static List<String> rows() {
        Achievements.load();
        ArrayList<String> rows = new ArrayList<String>();
        for (Ach ach : LIST) {
            long progress = Achievements.progress(ach);
            boolean done = UNLOCKED.contains(ach.id());
            rows.add((done ? "✔ " : "• ") + ach.title() + " [" + ach.category() + "] — "
                    + (done ? "получено" : progress + "/" + ach.goal()) + " · " + ach.hint()
                    + (ach.reward().isEmpty() ? "" : " · награда: " + ach.reward()));
        }
        return rows;
    }

    public static List<String> categoryRows(String category) {
        Achievements.load();
        ArrayList<String> rows = new ArrayList<String>();
        for (Ach ach : LIST) {
            if (category != null && !category.isBlank() && !ach.category().equalsIgnoreCase(category)) {
                continue;
            }
            boolean done = UNLOCKED.contains(ach.id());
            long progress = Achievements.progress(ach);
            rows.add((done ? "§a✔ " : "§7• ") + ach.title() + " §8— "
                    + (done ? "получено" : progress + "/" + ach.goal()) + " §7· " + ach.hint());
        }
        return rows;
    }

    /** Хочет ли модуль уведомления: модуль может выключить их настройкой. */
    private static boolean notificationsWanted() {
        AchievementsModule module = ModuleManager.get().get(AchievementsModule.class);
        return module == null || !module.isEnabled() || module.wantsNotify();
    }

    public static void ensure() {
        Achievements.load();
        if (!INSTANCE.subscribed) {
            INSTANCE.subscribed = true;
            try {
                EventBus.get().subscribe(INSTANCE);
            }
            catch (Throwable throwable) {
                ClientLog.warn("достижения: не удалось подписаться на события");
            }
        }
    }

    private boolean subscribed;
    private boolean loaded;
    private long lastPoll;
    private long lastPlaytime = -1L;
    private long sessions;
    private long playtimeBase = -1L;
    private long distanceBase = -1L;
    private long blocksBase = -1L;
    private long killsBase = -1L;
    private String lastTrack = "";
    private int toggles;
    private boolean[] lastEnabled = new boolean[0];

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastPoll < 1000L) {
            return;
        }
        this.lastPoll = now;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }
        this.poll(client);
    }

    private void poll(MinecraftClient client) {
        try {
            ModuleManager manager = ModuleManager.get();
            List<Module> modules = manager == null ? List.of() : manager.getAll();
            if (modules.size() != this.lastEnabled.length) {
                this.lastEnabled = new boolean[modules.size()];
                for (int i = 0; i < modules.size(); ++i) {
                    this.lastEnabled[i] = modules.get(i).isEnabled();
                }
            }
            else {
                for (int i = 0; i < modules.size(); ++i) {
                    boolean enabled = modules.get(i).isEnabled();
                    if (enabled != this.lastEnabled[i]) {
                        this.lastEnabled[i] = enabled;
                        ++this.toggles;
                    }
                }
            }
            Achievements.set("toggles", this.toggles);

            SessionStatsModule session = manager == null ? null : manager.get(SessionStatsModule.class);
            if (session != null) {
                if (this.playtimeBase < 0L) {
                    this.playtimeBase = session.playtimeMs();
                    this.distanceBase = (long)session.distance();
                    this.blocksBase = session.blocks();
                    this.killsBase = session.kills();
                }
                Achievements.set("playtime", session.playtimeMs());
                Achievements.set("distance", (long)session.distance());
                Achievements.set("blocks", session.blocks());
                Achievements.set("kills", session.kills());
            }
            else if (this.playtimeBase < 0L) {
                this.playtimeBase = 0L;
                this.distanceBase = 0L;
                this.blocksBase = 0L;
                this.killsBase = 0L;
            }

            Achievements.set("sky", client.player.getY() > 200.0 ? 1L : 0L);
            Achievements.set("deep", client.player.getY() < 0.0 ? 1L : 0L);

            MusicEngine music = MusicEngine.get();
            if (music.isPlaying()) {
                String track = music.nowPlaying();
                if (track != null && !track.isBlank() && !track.equals(this.lastTrack)) {
                    this.lastTrack = track;
                    Achievements.add("tracks", 1L);
                }
                long time = client.world == null ? 0L : client.world.getTimeOfDay() % 24000L;
                if (time > 13000L && time < 23000L) {
                    Achievements.set("night_music", 1L);
                }
            }

            Achievements.set("scripts", (long)ScriptEngine.get().rules().size());
            Achievements.set("voice", (long)rtx.byazen.utils.voice.VoiceCommands.trainedCount());
            Achievements.set("web", (long)LocalHttp.get().requests());
            Achievements.set("hud", (long)rtx.byazen.api.hud.HudApi.count());
            Achievements.set("cosmetics", (long)Cosmetics.ownedCount());
            Achievements.set("reviews", (long)rtx.byazen.utils.combat.FightReview.get().reviews().size());
            Achievements.set("configs", (long)ChangeLog.count());
            long playtime = Achievements.metric("playtime");
            if (this.lastPlaytime >= 0L && playtime < this.lastPlaytime) {
                ++this.sessions;
            }
            this.lastPlaytime = playtime;
            Achievements.set("sessions", Math.max(1L, this.sessions));
            Achievements.set("hub_opened", rtx.byazen.api.ui.ByAzenHubScreen.opened() ? 1L : 0L);

            this.check();
        }
        catch (Throwable throwable) {
            ClientLog.debug("достижения: " + throwable.getClass().getSimpleName());
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

    /** Отмечает событие: используется кнопками и другими системами клиента. */
    public static void touch(String metric, long value) {
        Achievements.load();
        METRICS.merge(metric, value, Long::sum);
        Achievements.get().check();
        Achievements.get().save();
    }

    private void check() {
        for (Ach ach : LIST) {
            if (UNLOCKED.contains(ach.id())) {
                continue;
            }
            if (Achievements.metric(ach.metric()) >= ach.goal()) {
                this.unlock(ach);
            }
        }
    }

    private void unlock(Ach ach) {
        UNLOCKED.add(ach.id());
        if (Achievements.notificationsWanted()) {
            NotificationsModule.notify("§bДостижение: §f" + ach.title() + " §7(+10 очков)", 4000L);
            ChatMessage.send("§bДостижение получено: §f" + ach.title() + " §7· " + Achievements.profile());
        }
        WebBridge.pushEvent("achievement", ach.title());
        ClientLog.info("достижение: " + ach.title());
        if (!ach.reward().isEmpty() && ach.reward().contains(":")) {
            String[] parts = ach.reward().split(":", 2);
            if (parts[0].equals("candy") && Cosmetics.grant(parts[1], "достижение")) {
                NotificationsModule.notify("§bНаграда: косметика «" + parts[1] + "»", 3500L);
            }
            else if (parts[0].equals("set")) {
                int granted = Cosmetics.grantSet(parts[1]);
                if (granted > 0) {
                    NotificationsModule.notify("§bНаграда: набор косметики («" + granted + " предметов»)", 4000L);
                }
            }
        }
        this.save();
    }

    /** Строка состояния для команды и модуля. */
    public static String summary() {
        Achievements.load();
        long next = Long.MAX_VALUE;
        Ach nearest = null;
        for (Ach ach : LIST) {
            if (UNLOCKED.contains(ach.id())) {
                continue;
            }
            long left = ach.goal() - Achievements.progress(ach);
            if (left < next) {
                next = left;
                nearest = ach;
            }
        }
        String tail = nearest == null ? "все достижения получены" : "ближайшее: «" + nearest.title() + "» ещё " + next;
        return Achievements.profile() + " · " + tail;
    }

    /** Текст профиля для буфера обмена: делиться результатом в чате. */
    public static String shareText() {
        Achievements.load();
        StringBuilder builder = new StringBuilder("Профиль ByAzen\n");
        builder.append(Achievements.profile()).append('\n');
        for (String category : Achievements.categories()) {
            long done = 0L;
            long all = 0L;
            for (Ach ach : LIST) {
                if (!ach.category().equals(category)) {
                    continue;
                }
                ++all;
                if (UNLOCKED.contains(ach.id())) {
                    ++done;
                }
            }
            builder.append("• ").append(category).append(": ").append(done).append('/').append(all).append('\n');
        }
        return builder.toString();
    }

    public static void reset() {
        UNLOCKED.clear();
        METRICS.clear();
        Achievements.get().save();
    }

    private static void load() {
        if (INSTANCE.loaded) {
            return;
        }
        INSTANCE.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("achievements");
            if (root.has("unlocked")) {
                for (var element : root.getAsJsonArray("unlocked")) {
                    UNLOCKED.add(element.getAsString());
                }
            }
            if (root.has("metrics")) {
                for (String key : root.getAsJsonObject("metrics").keySet()) {
                    METRICS.put(key, root.getAsJsonObject("metrics").get(key).getAsLong());
                }
            }
            if (root.has("toggles")) {
                INSTANCE.toggles = root.get("toggles").getAsInt();
            }
        }
        catch (Throwable throwable) {
            ClientLog.warn("достижения: не удалось прочитать файл — начинаем заново");
        }
    }

    private void save() {
        try {
            JsonObject root = new JsonObject();
            JsonArray unlocked = new JsonArray();
            UNLOCKED.forEach(unlocked::add);
            root.add("unlocked", unlocked);
            JsonObject metrics = new JsonObject();
            for (Map.Entry<String, Long> entry : METRICS.entrySet()) {
                metrics.addProperty(entry.getKey(), entry.getValue());
            }
            root.add("metrics", metrics);
            root.addProperty("toggles", this.toggles);
            RepositoryStorage.write("achievements", root);
        }
        catch (Throwable throwable) {
            ClientLog.warn("достижения: не удалось сохранить прогресс");
        }
    }
}
