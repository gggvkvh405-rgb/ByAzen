package rtx.byazen.utils.hints;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.impl.Utils.SessionStatsModule;
import rtx.byazen.api.modules.impl.Utils.SmartHintsModule;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.compat.MixinAudit;
import rtx.byazen.utils.config.ConfigBackups;
import rtx.byazen.utils.help.TutorialState;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.profiles.BuildTier;
import rtx.byazen.utils.storage.RepositoryStorage;
import rtx.byazen.utils.update.UpdateChecker;

/**
 * Умные подсказки (идея №203 из IDEAS.md).
 * <p>
 * Клиент изредка подсказывает то, что действительно пригодится прямо сейчас: стало мало FPS —
 * можно включить облегчённый профиль, кончается память, давно не делали копию настроек, вышла новая
 * версия, микшин-конфликт с другим модом, не пройден туториал, а музыкальный плеер так и не открыли.
 * <p>
 * Подсказки не назойливы: одна за раз, не чаще одной в несколько минут, каждая — не чаще раза в
 * три дня, и всё это выключается в модуле «Smart Hints» режимом «Только важные» или «Выключены».
 */
public final class SmartHints {

    /** Подсказка: что случилось, что делать и насколько это важно. */
    public record Hint(String id, String title, String text, boolean important) {
    }

    public static final String MODE_ALL = "Все подсказки";
    public static final String MODE_IMPORTANT = "Только важные";
    public static final String MODE_OFF = "Выключены";

    private static final long COOLDOWN_MS = 259200000L;
    private static final long PAUSE_BETWEEN_MS = 180000L;
    private static final int FPS_SAMPLES = 6;
    private static final SmartHints INSTANCE = new SmartHints();

    private boolean loaded;
    private boolean playedMusic;
    private long lastShownAt;
    private final List<Integer> fpsSamples = new ArrayList<Integer>();
    private final java.util.Map<String, Long> shownAt = new java.util.LinkedHashMap<String, Long>();

    private SmartHints() {
    }

    /** Подтягивает историю показов и подписывается на тики через модуль. */
    public static void ensure() {
        SmartHints.load();
    }

    /** Режим из модуля: «Все», «Только важные» или «Выключены». */
    public static String mode() {
        SmartHintsModule module = SmartHints.module();
        return module == null ? SmartHints.MODE_IMPORTANT : module.mode.getValue();
    }

    public static boolean enabled() {
        return !SmartHints.mode().equals(SmartHints.MODE_OFF);
    }

    private static SmartHintsModule module() {
        ModuleManager manager = ModuleManager.get();
        return manager == null ? null : manager.get(SmartHintsModule.class);
    }

    private static boolean toChat() {
        SmartHintsModule module = SmartHints.module();
        return module == null || module.toChat.getValue();
    }

    /** Вызывается модулем раз в несколько секунд: собрать статистику и, если пора, показать подсказку. */
    public static void tick() {
        if (!SmartHints.enabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }
        INSTANCE.sample(client);
        if (MusicEngine.get().isPlaying()) {
            SmartHints.markMusicPlayed();
        }
        long now = System.currentTimeMillis();
        if (now - INSTANCE.lastShownAt < PAUSE_BETWEEN_MS) {
            return;
        }
        SmartHints.Hint hint = INSTANCE.pick();
        if (hint == null) {
            return;
        }
        INSTANCE.show(hint);
    }

    /** Показать подсказку немедленно — кнопка «Подсказать сейчас». */
    public static String checkNow() {
        SmartHints.Hint hint = INSTANCE.pick();
        if (hint == null) {
            for (Hint candidate : INSTANCE.evaluate()) {
                hint = candidate;
                break;
            }
        }
        if (hint == null) {
            return "Сейчас подсказывать нечего — всё в порядке";
        }
        INSTANCE.show(hint);
        return hint.title() + " — " + hint.text();
    }

    /** Что клиент мог бы подсказать прямо сейчас (без показа). */
    public static List<Hint> pending() {
        return INSTANCE.evaluate();
    }

    public static String summary() {
        SmartHints.load();
        int total = INSTANCE.shownAt.size();
        return "режим: " + SmartHints.mode() + " · уже показано подсказок: " + total + " · доступно сейчас: "
                + INSTANCE.evaluate().size();
    }

    public static void forget() {
        INSTANCE.shownAt.clear();
        INSTANCE.fpsSamples.clear();
        INSTANCE.save();
    }

    private void sample(MinecraftClient client) {
        this.fpsSamples.add(client.getCurrentFps());
        while (this.fpsSamples.size() > FPS_SAMPLES) {
            this.fpsSamples.removeFirst();
        }
    }

    private int averageFps() {
        if (this.fpsSamples.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (int value : this.fpsSamples) {
            sum += value;
        }
        return sum / this.fpsSamples.size();
    }

    private long playtimeMs() {
        ModuleManager manager = ModuleManager.get();
        SessionStatsModule session = manager == null ? null : manager.get(SessionStatsModule.class);
        return session == null ? 0L : session.playtimeMs();
    }

    /** Собирает подсказки, которые ещё не показывали недавно. */
    private List<Hint> evaluate() {
        ArrayList<Hint> hints = new ArrayList<Hint>();
        MinecraftClient client = MinecraftClient.getInstance();
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        double memory = runtime.maxMemory() <= 0L ? 0.0 : (double)used / (double)runtime.maxMemory();
        int fps = this.averageFps();
        long playtime = this.playtimeMs();

        if (fps > 0 && fps < 45 && !BuildTier.lite()) {
            hints.add(new Hint("fps", "Мало FPS",
                    "Сейчас около " + fps + " кадров в секунду. Модуль «Build Tier» → «Профиль Lite» выключит тяжёлые визуалы.", true));
        }
        if (memory > 0.9) {
            hints.add(new Hint("memory", "Кончается память",
                    "Занято " + Math.round(memory * 100.0) + "% выделенной памяти. Помогает перезапуск игры и меньший набор визуалов.", true));
        }
        if (ConfigBackups.list().isEmpty() && playtime > 7200000L) {
            hints.add(new Hint("backup", "Нет копии настроек",
                    "Клиент давно настроен, а копий нет: модуль «ConfigBackups» сделает копию конфига в один клик.", false));
        }
        UpdateChecker.Result update = UpdateChecker.last();
        if (update != null && update.newer()) {
            hints.add(new Hint("update", "Вышла версия " + update.latest(),
                    "В модуле «Updater» есть кнопка «Обновить и перезапустить» — обновление поставится само.", true));
        }
        if (!MixinAudit.scan().isEmpty()) {
            hints.add(new Hint("mixins", "Возможен конфликт с модом",
                    "В логе есть предупреждения микшинов. Подробности — «Diagnostics» → вкладка «Моды».", true));
        }
        if (TutorialState.firstLaunch()) {
            hints.add(new Hint("tutorial", "Туториал ещё не пройден",
                    "Семь коротких шагов покажут главное: команда .tutorial или вкладка «Гайд» в хабе.", false));
        }
        if (playtime > 3600000L && !this.playedMusic) {
            hints.add(new Hint("music", "Плеер ещё не включали",
                    "Модуль «Music Player» — музыка прямо в клиенте, есть «моя волна» и подборки.", false));
        }
        return hints;
    }

    private Hint pick() {
        SmartHints.load();
        boolean onlyImportant = SmartHints.mode().equals(SmartHints.MODE_IMPORTANT);
        long now = System.currentTimeMillis();
        List<Hint> available = new ArrayList<Hint>();
        for (Hint hint : this.evaluate()) {
            if (onlyImportant && !hint.important()) {
                continue;
            }
            Long shown = this.shownAt.get(hint.id());
            if (shown != null && now - shown < COOLDOWN_MS) {
                continue;
            }
            available.add(hint);
        }
        if (available.isEmpty()) {
            return null;
        }
        available.sort(Comparator.comparing(Hint::important).reversed());
        return available.getFirst();
    }

    private void show(Hint hint) {
        this.shownAt.put(hint.id(), System.currentTimeMillis());
        this.lastShownAt = System.currentTimeMillis();
        this.save();
        NotificationsModule.notify("§bПодсказка ByAzen: §f" + hint.title(), 5000L);
        if (SmartHints.toChat()) {
            ChatMessage.send("§bПодсказка: §f" + hint.title() + " §7— " + hint.text());
            ChatMessage.send("§8Отключить или оставить только важные: модуль «Smart Hints»");
        }
        ClientLog.info("подсказка: " + hint.id());
    }

    private static void load() {
        if (INSTANCE.loaded) {
            return;
        }
        INSTANCE.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("smart_hints");
            if (root.has("shown")) {
                for (String key : root.getAsJsonObject("shown").keySet()) {
                    INSTANCE.shownAt.put(key, root.getAsJsonObject("shown").get(key).getAsLong());
                }
            }
            if (root.has("playedMusic")) {
                INSTANCE.playedMusic = root.get("playedMusic").getAsBoolean();
            }
        }
        catch (Throwable throwable) {
            ClientLog.warn("подсказки: не удалось прочитать историю");
        }
    }

    private void save() {
        try {
            JsonObject root = new JsonObject();
            JsonObject shown = new JsonObject();
            for (java.util.Map.Entry<String, Long> entry : this.shownAt.entrySet()) {
                shown.addProperty(entry.getKey(), entry.getValue());
            }
            root.add("shown", shown);
            root.addProperty("playedMusic", this.playedMusic);
            RepositoryStorage.write("smart_hints", root);
        }
        catch (Throwable throwable) {
            ClientLog.warn("подсказки: не удалось сохранить историю");
        }
    }

    /** Музыку отметили как прослушанную — вызывается из тика, когда плеер играет. */
    public static void markMusicPlayed() {
        if (INSTANCE.playedMusic) {
            return;
        }
        INSTANCE.playedMusic = true;
        INSTANCE.save();
    }
}
