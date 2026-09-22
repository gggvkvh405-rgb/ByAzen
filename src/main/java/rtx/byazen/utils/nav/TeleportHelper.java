package rtx.byazen.utils.nav;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.api.nav.Waypoint;
import rtx.byazen.api.nav.WaypointStore;
import rtx.byazen.utils.handbook.Handbook;

/**
 * Помощник телепортов (идея №199 из IDEAS.md).
 * <p>
 * Держит «домашнюю» точку, показывает до неё расстояние, направление и сколько идти пешком,
 * подсказывает ближайшие путевые точки и объясняет, что можно, а что нельзя: клиент не телепортирует
 * сам — он отправляет на сервер команду {@code home} и заранее рассказывает про права и правила.
 */
public final class TeleportHelper {

    /** Имя домашней точки. */
    public static final String HOME = "Дом";

    private TeleportHelper() {
    }

    public static Waypoint home() {
        return WaypointStore.get().find(TeleportHelper.HOME);
    }

    /** Ставит домашнюю точку там, где стоит игрок. */
    public static String setHome() {
        Vec3d pos = WaypointStore.playerPos();
        if (pos == null) {
            return "Нет игрока в мире — точку поставить нельзя";
        }
        WaypointStore.get().put(new Waypoint(TeleportHelper.HOME, Math.floor(pos.x) + 0.5, Math.floor(pos.y),
                Math.floor(pos.z) + 0.5, WaypointStore.dimensionId(), 0x7FB2FF, "домашняя точка"));
        WaypointStore.get().setActive(TeleportHelper.HOME);
        return "Домашняя точка сохранена: " + TeleportHelper.coords(pos);
    }

    /** Описание домашней точки: координаты, дистанция, направление, время в пути. */
    public static List<String> describe() {
        ArrayList<String> lines = new ArrayList<String>();
        Waypoint point = TeleportHelper.home();
        if (point == null) {
            lines.add("§7Домашняя точка не поставлена: встаньте где нужно и введите §f.home set");
            return lines;
        }
        lines.add("§bДом: §f" + TeleportHelper.coords(point));
        Vec3d pos = WaypointStore.playerPos();
        if (pos == null) {
            return lines;
        }
        double dx = point.x() - pos.x;
        double dy = point.y() - pos.y;
        double dz = point.z() - pos.z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (!WaypointStore.dimensionId().equals(point.dimension())) {
            lines.add("§7Точка в другом измерении: §f" + point.dimension() + "§7 — пешком не дойти, нужен портал");
            return lines;
        }
        lines.add("§7До дома: §f" + Math.round(distance) + " блоков §7в сторону §f"
                + Handbook.direction(dx, dz) + "§7, пешком примерно §f" + Handbook.walkTime(distance));
        lines.add("§7Высота: §f" + (dy >= 0.0 ? "выше на " + Math.round(dy) : "ниже на " + Math.round(-dy)));
        return lines;
    }

    private static String coords(Waypoint point) {
        return Math.round(point.x()) + ", " + Math.round(point.y()) + ", " + Math.round(point.z())
                + " (" + point.dimension() + ")";
    }

    private static String coords(Vec3d pos) {
        return Math.round(pos.x) + ", " + Math.round(pos.y) + ", " + Math.round(pos.z);
    }

    /**
     * Просит сервер телепортировать домой: клиент сам не перемещает игрока, он отправляет команду.
     * Если на сервере такой команды нет — сервер честно ответит в чате.
     */
    public static String goHome() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return "Нет игрока — телепорт невозможен";
        }
        try {
            client.player.networkHandler.sendChatCommand("home");
            return "Отправил на сервер команду home — если она там есть, вас перенесёт к дому";
        }
        catch (Throwable throwable) {
            return "Сервер не принял команду home: " + throwable.getClass().getSimpleName();
        }
    }

    /** Что можно на этом сервере: одиночная игра, локальный сервер или правила сервера. */
    public static List<String> permissions() {
        ArrayList<String> lines = new ArrayList<String>();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            lines.add("§7Вы не в игре — проверить права не получится.");
            return lines;
        }
        if (client.isInSingleplayer()) {
            lines.add("§7Одиночная игра: телепорт доступен командой §f/tp x y z§7 или §f/home§7 при модах.");
            lines.add("§7Своя точка «Дом» ставится командой §f.home set");
            return lines;
        }
        String server = client.getCurrentServerEntry() == null ? "сервер" : client.getCurrentServerEntry().address;
        lines.add("§7Сервер: §f" + server);
        lines.add("§7Клиент не телепортирует сам — это запрещено правилами и невозможно технически.");
        lines.add("§7Проверьте, какие команды разрешены: §f/home§7, §f/spawn§7, §f/tpa§7 — помощь по командам "
                + "покажет §f/home go§7 (клиент просто отправит её на сервер).");
        lines.add("§7Свои точки клиент показывает как обычные путевые: §f.wp go Дом§7 — маршрут по координатам.");
        return lines;
    }

    /** Ближайшие путевые точки: удобно, когда дом не поставлен. */
    public static List<String> nearest(int limit) {
        ArrayList<String> lines = new ArrayList<String>();
        List<Waypoint> list = WaypointStore.get().nearest(limit);
        if (list.isEmpty()) {
            lines.add("§7Путевых точек нет — добавьте их командой §f.wp add имя");
            return lines;
        }
        for (Waypoint point : list) {
            Vec3d pos = WaypointStore.playerPos();
            double distance = pos == null ? 0.0
                    : point.distanceTo(pos);
            lines.add("§7• §f" + point.name() + " §8— " + Math.round(distance) + " блоков, "
                    + Math.round(point.x()) + ", " + Math.round(point.z()));
        }
        return lines;
    }

    public static String summary() {
        Waypoint point = TeleportHelper.home();
        if (point == null) {
            return "домашняя точка не поставлена";
        }
        return "дом: " + Math.round(point.x()) + ", " + Math.round(point.z()) + " (" + point.dimension().toLowerCase(Locale.ROOT) + ")";
    }
}

