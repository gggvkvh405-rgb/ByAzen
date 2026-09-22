package rtx.byazen.utils.perf;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.utils.compat.ModEnv;

/**
 * Экран «почему лагает» (идея №170 из IDEAS.md) — сбор причин просадок простыми словами:
 * сколько сущностей рядом, какие настройки графики стоят, сколько памяти занято, что из модов
 * перехватывает рендер и сколько модулей клиента включено.
 */
public final class LagReport {

    private LagReport() {
    }

    public record Row(String title, String value, String verdict, int severity) {
    }

    /** severity: 0 — всё хорошо, 1 — стоит посмотреть, 2 — вероятная причина лагов. */
    public static List<Row> rows() {
        ArrayList<Row> list = new ArrayList<Row>();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return list;
        }
        float fps = client.getCurrentFps();
        int severity = fps >= 60.0f ? 0 : fps >= 30.0f ? 1 : 2;
        list.add(new Row("Кадры в секунду", String.format(Locale.ROOT, "%.0f FPS", fps),
                fps >= 60.0f ? "плавно" : fps >= 30.0f ? "ниже 60 — заметны рывки" : "совсем мало — ищите причину ниже", severity));

        if (client.world != null && client.player != null) {
            int nearby = 0;
            int total = 0;
            try {
                for (Entity entity : client.world.getEntities()) {
                    ++total;
                    if (client.player.squaredDistanceTo(entity) <= 1024.0) {
                        ++nearby;
                    }
                }
            }
            catch (Throwable ignored) {
            }
            list.add(new Row("Сущности рядом (32 блока)", String.valueOf(nearby),
                    nearby > 120 ? "очень много: мобы, сундуки-поезда, фермы" : nearby > 60 ? "многовато" : "нормально",
                    nearby > 120 ? 2 : nearby > 60 ? 1 : 0));
            list.add(new Row("Всего сущностей в мире", String.valueOf(total),
                    total > 1500 ? "мир перегружен сущностями" : "в пределах разумного", total > 1500 ? 2 : 0));
            list.add(new Row("Загружено чанков", LagReport.chunkCount(client),
                    "чем больше прорисовка, тем дороже кадр", 0));
        }

        int renderDistance = LagReport.renderDistance(client);
        list.add(new Row("Дальность прорисовки", renderDistance + " чанков",
                renderDistance >= 16 ? "высокая: попробуйте 8–12 ради FPS" : "разумная", renderDistance >= 16 ? 1 : 0));

        int guiScale = LagReport.guiScale(client);
        list.add(new Row("Масштаб интерфейса", guiScale + "x",
                guiScale >= 4 ? "на 4x интерфейсу ByAzen тесно: модуль «Масштаб UI» поможет" : "нормально", guiScale >= 4 ? 1 : 0));

        long usedMb = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576L;
        long maxMb = Runtime.getRuntime().maxMemory() / 1048576L;
        boolean memoryBad = maxMb > 0L && usedMb * 100L / maxMb >= 92L;
        list.add(new Row("Память", usedMb + " из " + maxMb + " МБ",
                memoryBad ? "почти вся занята: GC вызывает рывки — выделите больше" : "есть запас", memoryBad ? 2 : 0));

        list.add(new Row("Шейдеры и моды рендера", ModEnv.summary(),
                ModEnv.shadersPossible() ? "шейдеры дороги, а модуль «Детект модов» умеет беречь визуалы" : "соседей нет",
                ModEnv.shadersPossible() ? 1 : 0));

        ModuleProfiler.Row heavy = LagReport.heaviestModule();
        list.add(new Row("Самый дорогой модуль", heavy == null ? "замеры выключены" : heavy.name,
                heavy == null ? "включите «Профайлер рендера», чтобы увидеть виновников"
                        : String.format(Locale.ROOT, "%.0f мкс на вызов — %s", heavy.microsPerCall, heavy.microsPerCall > 900.0f ? "дорого" : "не критично"),
                heavy != null && heavy.microsPerCall > 900.0f ? 2 : 0));

        int enabled = 0;
        for (Module module : ModuleManager.get().getAll()) {
            if (module.isEnabled()) {
                ++enabled;
            }
        }
        list.add(new Row("Включено модулей", enabled + " из " + ModuleManager.get().getAll().size(),
                enabled > 60 ? "много всего включено — отключайте лишнее" : "в разумных пределах", enabled > 60 ? 1 : 0));
        return list;
    }

    private static ModuleProfiler.Row heaviestModule() {
        ModuleProfiler.Row best = null;
        for (ModuleProfiler.Row row : ModuleProfiler.heavy(0.0, 20)) {
            if (best == null || row.microsPerCall > best.microsPerCall) {
                best = row;
            }
        }
        return best;
    }

    private static String chunkCount(MinecraftClient client) {
        try {
            Object manager = client.world.getClass().getMethod("getChunkManager").invoke(client.world);
            Object count = manager.getClass().getMethod("getLoadedChunkCount").invoke(manager);
            return String.valueOf(((Number)count).intValue());
        }
        catch (Throwable throwable) {
            return "неизвестно";
        }
    }

    private static int renderDistance(MinecraftClient client) {
        try {
            return client.options.getViewDistance().getValue();
        }
        catch (Throwable throwable) {
            return -1;
        }
    }

    private static int guiScale(MinecraftClient client) {
        try {
            Object option = client.options.getClass().getMethod("getGuiScale").invoke(client.options);
            Object value = option.getClass().getMethod("getValue").invoke(option);
            return ((Number)value).intValue();
        }
        catch (Throwable throwable) {
            return -1;
        }
    }

    /** Итог одной строкой: главная причина или «всё в порядке». */
    public static String summary() {
        Row worst = null;
        for (Row row : LagReport.rows()) {
            if (row.severity() < 2) {
                continue;
            }
            if (worst == null) {
                worst = row;
            }
        }
        return worst == null ? "явных причин лагов не найдено" : "главная причина: " + worst.title() + " — " + worst.value();
    }

    public static List<String> report() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("Почему лагает: " + LagReport.summary());
        for (Row row : LagReport.rows()) {
            list.add("  " + LagReport.mark(row.severity()) + " " + row.title() + ": " + row.value() + " — " + row.verdict());
        }
        list.add("Проверка миксинов: " + rtx.byazen.utils.compat.MixinAudit.summary());
        list.add("Окружение: " + rtx.byazen.utils.startup.EnvCheck.summary());
        return list;
    }

    private static String mark(int severity) {
        return severity >= 2 ? "[!]" : severity == 1 ? "[~]" : "[ok]";
    }
}
