package rtx.byazen.utils.season;

import com.google.gson.JsonObject;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.InterfaceModule;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.cosmetics.Cosmetic;
import rtx.byazen.utils.cosmetics.CosmeticRegistry;
import rtx.byazen.utils.cosmetics.Cosmetics;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.storage.RepositoryStorage;
import rtx.byazen.utils.web.WebBridge;

/**
 * Сезонные события клиента (идея №184 из IDEAS.md).
 * <p>
 * Сезоны уже живут в наборах косметики: зима (декабрь–февраль), неон (июнь–август), космос
 * (сентябрь–ноябрь). Здесь из них делаются настоящие события: своя палитра для интерфейса, баннер
 * с поздравлением при входе, сезонная награда (весь набор косметики забирается один раз за сезон)
 * и список «что идёт сейчас, что дальше». Особые дни — Новый год и Хэллоуин — отмечены отдельно.
 */
public final class SeasonEvents {

    /** Одно событие: окно по месяцам, палитра и награда. */
    public record Event(String id, String title, String note, int[] months, int color, int accent, String flavor,
                        boolean special) {
        public boolean active(int month) {
            for (int value : this.months) {
                if (value == month) {
                    return true;
                }
            }
            return false;
        }

        public String period() {
            StringBuilder builder = new StringBuilder();
            for (int value : this.months) {
                if (builder.length() > 0) {
                    builder.append(", ");
                }
                builder.append(SeasonEvents.monthName(value));
            }
            return builder.toString();
        }
    }

    private static final List<Event> EVENTS = new ArrayList<Event>();

    static {
        SeasonEvents.addFromSet(Cosmetic.FLAVOR_SNOW, "Зима", "Морозная палитра и снежный набор", true);
        SeasonEvents.addFromSet(Cosmetic.FLAVOR_NEON, "Неон", "Летняя ночная палитра и неоновый набор", false);
        SeasonEvents.addFromSet(Cosmetic.FLAVOR_SPACE, "Космос", "Осенняя палитра и космический набор", false);
        EVENTS.add(new Event("newyear", "Новый год", "Праздничная палитра, снежинки и поздравление", new int[]{12, 1},
                0xE8455F, 0xFFD166, "snow", true));
        EVENTS.add(new Event("halloween", "Хэллоуин", "Тыквы, огоньки и жуткая палитра", new int[]{10},
                0xF28F3B, 0x8A5CF0, "space", true));
    }

    private SeasonEvents() {
    }

    private static void addFromSet(String flavor, String title, String note, boolean special) {
        CosmeticRegistry.Set set = CosmeticRegistry.setOf(flavor);
        if (set == null) {
            return;
        }
        EVENTS.add(new Event("season_" + flavor, title, note, set.months, set.color, set.accent, flavor, special));
    }

    public static List<Event> events() {
        return new ArrayList<Event>(EVENTS);
    }

    private static int month() {
        return LocalDate.now().getMonthValue();
    }

    /** Что идёт сейчас: особые дни важнее обычного сезона. */
    public static Event current() {
        int month = SeasonEvents.month();
        Event best = null;
        for (Event event : EVENTS) {
            if (!event.active(month)) {
                continue;
            }
            if (best == null || event.special() && !best.special()) {
                best = event;
            }
        }
        return best;
    }

    /** Что будет следующим: ближайшее событие после текущего месяца. */
    public static Event next() {
        int month = SeasonEvents.month();
        Event best = null;
        int bestDistance = 13;
        for (Event event : EVENTS) {
            for (int value : event.months()) {
                int distance = value - month;
                if (distance <= 0) {
                    distance += 12;
                }
                if (distance < bestDistance) {
                    bestDistance = distance;
                    best = event;
                }
            }
        }
        return best;
    }

    public static String summary() {
        Event current = SeasonEvents.current();
        Event next = SeasonEvents.next();
        StringBuilder builder = new StringBuilder();
        builder.append(current == null ? "сезон не определён" : "сейчас: " + current.title() + " (" + current.period() + ")");
        if (next != null && next != current) {
            builder.append(" · дальше: ").append(next.title()).append(" (").append(next.period()).append(")");
        }
        return builder.toString();
    }

    /** Строки для хаба. */
    public static List<String> rows() {
        int month = SeasonEvents.month();
        ArrayList<String> rows = new ArrayList<String>();
        for (Event event : EVENTS) {
            boolean now = event.active(month);
            rows.add((now ? "§a▶ " : "§7• ") + event.title() + " §8— " + event.period() + " §7· " + event.note()
                    + (now ? " §a(идёт сейчас)" : ""));
        }
        rows.add("§7Сегодня: " + monthName(month) + "-й месяц · " + SeasonEvents.rewardText());
        return rows;
    }

    public static String rewardText() {
        Event event = SeasonEvents.current();
        if (event == null) {
            return "награда появится с началом сезона";
        }
        int owned = Cosmetics.ownedInSet(event.flavor());
        int total = CosmeticRegistry.ofFlavor(event.flavor()).size();
        return "награда сезона: набор «" + event.title() + "» — собрано " + owned + " из " + total;
    }

    /** Забирает сезонную награду (один раз за сезон) и включает сезонную палитру. */
    public static String claim() {
        Event event = SeasonEvents.current();
        if (event == null) {
            return "Сейчас нет активного сезона";
        }
        int granted = Cosmetics.claimSet(event.flavor());
        SeasonEvents.apply(event);
        SeasonEvents.markClaimed(event);
        if (granted > 0) {
            NotificationsModule.notify("§bСезон «" + event.title() + "»: получено предметов " + granted, 4500L);
            WebBridge.pushEvent("season", "Сезонная награда: " + event.title() + " (" + granted + ")");
        }
        return granted > 0 ? "Получено предметов косметики: " + granted : "Весь набор уже собран — палитра включена";
    }

    /** Включает сезонную палитру интерфейса. */
    public static String apply(Event event) {
        if (event == null) {
            event = SeasonEvents.current();
        }
        if (event == null) {
            return "Нет активного сезона";
        }
        ModuleManager manager = ModuleManager.get();
        InterfaceModule module = manager == null ? null : manager.get(InterfaceModule.class);
        if (module == null) {
            return "Модуль Interface не найден";
        }
        module.rectColor.value(event.color());
        module.rectSecondColor.value(event.accent());
        module.rectUseSecondColor.setValue(true);
        return "Палитра «" + event.title() + "» включена";
    }

    /** Приветствие при входе: один раз на событие. */
    public static void welcome() {
        Event event = SeasonEvents.current();
        if (event == null) {
            return;
        }
        SeasonEvents.load();
        if (event.id().equals(SeasonEvents.INSTANCE.lastWelcome)) {
            return;
        }
        SeasonEvents.INSTANCE.lastWelcome = event.id();
        SeasonEvents.save();
        ChatMessage.send("§bСезон ByAzen: §f" + event.title() + " §7— " + event.note());
        ChatMessage.send("§7" + SeasonEvents.rewardText() + " §8· забрать: кнопка «Забрать награду» в хабе или §f.season");
        NotificationsModule.notify("Сезон: " + event.title(), 4000L);
        WebBridge.pushEvent("season", "Начался сезон: " + event.title());
        if (event.special()) {
            ChatMessage.send("§dСегодня особый день — праздничная палитра доступна всем");
        }
    }

    public static List<String> claimed() {
        SeasonEvents.load();
        return new ArrayList<String>(List.of(SeasonEvents.INSTANCE.lastClaimed));
    }

    private static void markClaimed(Event event) {
        SeasonEvents.load();
        String joined = String.join(",", SeasonEvents.INSTANCE.lastClaimed);
        if (!joined.contains(event.id())) {
            String[] next = new String[SeasonEvents.INSTANCE.lastClaimed.length + 1];
            System.arraycopy(SeasonEvents.INSTANCE.lastClaimed, 0, next, 0, SeasonEvents.INSTANCE.lastClaimed.length);
            next[next.length - 1] = event.id();
            SeasonEvents.INSTANCE.lastClaimed = next;
        }
        SeasonEvents.save();
    }

    private static final SeasonEvents INSTANCE = new SeasonEvents();

    private String lastWelcome = "";
    private String[] lastClaimed = new String[0];

    private static void load() {
        if (SeasonEvents.INSTANCE.loaded) {
            return;
        }
        SeasonEvents.INSTANCE.loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject("season_state");
            if (root.has("welcome")) {
                SeasonEvents.INSTANCE.lastWelcome = root.get("welcome").getAsString();
            }
            if (root.has("claimed")) {
                StringBuilder builder = new StringBuilder();
                for (var element : root.getAsJsonArray("claimed")) {
                    if (builder.length() > 0) {
                        builder.append(',');
                    }
                    builder.append(element.getAsString());
                }
                SeasonEvents.INSTANCE.lastClaimed = builder.length() == 0 ? new String[0] : builder.toString().split(",");
            }
        }
        catch (Throwable throwable) {
            ClientLog.warn("сезоны: не удалось прочитать состояние");
        }
    }

    private static void save() {
        try {
            JsonObject root = new JsonObject();
            root.addProperty("welcome", SeasonEvents.INSTANCE.lastWelcome);
            var array = new com.google.gson.JsonArray();
            for (String item : SeasonEvents.INSTANCE.lastClaimed) {
                array.add(item);
            }
            root.add("claimed", array);
            RepositoryStorage.write("season_state", root);
        }
        catch (Throwable throwable) {
            ClientLog.warn("сезоны: не удалось сохранить состояние");
        }
    }

    private boolean loaded;

    public static String monthName(int month) {
        String[] names = {"", "январь", "февраль", "март", "апрель", "май", "июнь", "июль", "август", "сентябрь",
                "октябрь", "ноябрь", "декабрь"};
        return month >= 1 && month <= 12 ? names[month] : "?";
    }
}
