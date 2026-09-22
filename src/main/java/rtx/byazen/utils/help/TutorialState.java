package rtx.byazen.utils.help;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Состояние интерактивного туториала (идея №188 из IDEAS.md).
 * <p>
 * Семь коротких шагов с картинками-подсказками: что такое клиент, как включать модули, как искать
 * «где это лежит», что внутри хаба, где посмотреть железо и как обновляться. Туториал показывается
 * один раз, но его можно открыть заново или сбросить — прогресс хранится в конфиге клиента.
 */
public final class TutorialState {

    /** Шаг тура: заголовок, текст, кнопка действия. */
    public record Step(String id, String title, String body, String actionLabel, String action) {
    }

    private static final TutorialState INSTANCE = new TutorialState();
    private static final List<Step> STEPS = new ArrayList<Step>();

    static {
        TutorialState.add("hello", "Знакомство",
                "ByAzen — клиент с HUD, музыкой, косметикой и кучей удобных мелочей.\n"
                        + "Слева в хабе — миссии, достижения, события и голосование.\n"
                        + "Начать можно с любой вкладки, настройки не потеряются.",
                "Открыть хаб", "hub0");
        TutorialState.add("modules", "Модули и клавиши",
                "Модули — это отдельные функции клиента: от счётчика координат до погоды.\n"
                        + "Включить: клик по строке в списке. Клавишу можно назначить в настройках модуля.\n"
                        + "Что где лежит, показывает справочник и команда .find <слово>.",
                "Открыть справочник", "handbook");
        TutorialState.add("find", "Как искать «где найти»",
                "Не помните, где включается музыка или прицел?\n"
                        + "Напишите в чат .find звук — клиент откроет нужный модуль и подсветит его.\n"
                        + "Кнопка «Показать пример» прямо сейчас подсветит модуль звука.",
                "Показать пример", "finddemo");
        TutorialState.add("hub", "Хаб ByAzen",
                "В хабе живут кооп-миссии, достижения, сезонные события, голосование за косметику и профиль.\n"
                        + "Миссии считаются по вашей игре, а кодом можно сложить прогресс с друзьями.\n"
                        + "Профиль и достижения можно скопировать и отправить другу.",
                "Открыть хаб: события", "hub2");
        TutorialState.add("diag", "Железо, FPS и лаги",
                "Окно «Диагностика» показывает версии Java и Minecraft, память, моды и кэши.\n"
                        + "Если стало лагать — модуль Why Lag подскажет, что именно мешает.\n"
                        + "Авто-настройки под ваше железо предлагаются при первом запуске.",
                "Открыть диагностику", "diag");
        TutorialState.add("update", "Обновления и помощь",
                "Модуль Updater проверяет обновления и умеет обновить jar в один клик.\n"
                        + "Модуль Help показывает «как это работает» для любого модуля (и команда .how).\n"
                        + "Слабовидящим — режим крупного шрифта и высокого контраста в модуле Accessibility.",
                "Скопировать шпаргалку", "cheat");
        TutorialState.add("ready", "Готово!",
                "Это конец тура. Открыть его заново можно из модуля Tutorial.\n"
                        + "Приятной игры: если что-то непонятно — .how <модуль> или .find <слово>.",
                "Закончить тур", "finish");
    }

    private int index;
    private boolean finished;

    private TutorialState() {
    }

    private static void add(String id, String title, String body, String actionLabel, String action) {
        STEPS.add(new Step(id, title, body, actionLabel, action));
    }

    public static List<Step> steps() {
        return new ArrayList<Step>(STEPS);
    }

    public static Step step(int index) {
        if (index < 0 || index >= STEPS.size()) {
            return null;
        }
        return STEPS.get(index);
    }

    public static int size() {
        return STEPS.size();
    }

    public static TutorialState get() {
        TutorialState.load();
        return INSTANCE;
    }

    public int index() {
        return this.index;
    }

    public boolean finished() {
        return this.finished;
    }

    /** Первый запуск: тур ещё не проходили. */
    public static boolean firstLaunch() {
        TutorialState.load();
        return !INSTANCE.finished;
    }

    public static String progressText() {
        TutorialState.load();
        if (INSTANCE.finished) {
            return "тур пройден";
        }
        return "шаг " + (INSTANCE.index + 1) + " из " + STEPS.size();
    }

    public static void next() {
        TutorialState.load();
        if (INSTANCE.index < STEPS.size() - 1) {
            ++INSTANCE.index;
        }
        INSTANCE.save();
    }

    public static void previous() {
        TutorialState.load();
        if (INSTANCE.index > 0) {
            --INSTANCE.index;
        }
        INSTANCE.save();
    }

    public static void goTo(int index) {
        TutorialState.load();
        INSTANCE.index = Math.max(0, Math.min(STEPS.size() - 1, index));
        INSTANCE.save();
    }

    public static void finish() {
        TutorialState.load();
        INSTANCE.finished = true;
        INSTANCE.index = STEPS.size() - 1;
        INSTANCE.save();
    }

    public static void reset() {
        TutorialState.load();
        INSTANCE.finished = false;
        INSTANCE.index = 0;
        INSTANCE.save();
    }

    private static void load() {
        if (INSTANCE.loaded) {
            return;
        }
        INSTANCE.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("tutorial");
            if (root.has("index")) {
                INSTANCE.index = Math.max(0, Math.min(STEPS.size() - 1, root.get("index").getAsInt()));
            }
            if (root.has("finished")) {
                INSTANCE.finished = root.get("finished").getAsBoolean();
            }
        }
        catch (Throwable throwable) {
            ClientLog.warn("туториал: не удалось прочитать прогресс");
        }
    }

    private void save() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("index", this.index);
            root.addProperty("finished", this.finished);
            RepositoryStorage.write("tutorial", root);
        }
        catch (Throwable throwable) {
            ClientLog.warn("туториал: не удалось сохранить прогресс");
        }
    }

    private boolean loaded;

    /** Шпаргалка для буфера обмена: главные команды клиента. */
    public static String cheatSheet() {
        StringBuilder builder = new StringBuilder("ByAzen — шпаргалка\n");
        builder.append(".find <слово>  — найти модуль или настройку и подсветить\n");
        builder.append(".how <модуль>  — как это работает\n");
        builder.append(".home          — домашняя точка и путевые точки\n");
        builder.append(".hub           — хаб: миссии, достижения, события, голосование\n");
        builder.append(".diag          — диагностика: версии, память, моды\n");
        builder.append(".tutorial      — этот тур заново\n");
        return builder.toString();
    }
}

