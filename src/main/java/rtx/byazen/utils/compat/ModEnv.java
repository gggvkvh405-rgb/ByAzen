package rtx.byazen.utils.compat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.MinecraftClient;

/**
 * Детект Iris / OptiFine / Sodium и других соседей (идея №166 из IDEAS.md).
 * <p>
 * Клиенту важно знать, что стоит рядом: с шейдерами часть наших визуалов дороже, с OptiFine или
 * Sodium меняется отрисовка мира. Клиент сам находит такие моды, считает установленные наборы
 * шейдеров и предупреждает там, где надо; тяжёлые визуалы можно заранее не включать.
 */
public final class ModEnv {

    /** Найденный сосед: подпись и заметка. */
    public static final class Row {

        public final String id;
        public final String label;
        public final String version;
        public final boolean present;
        public final String note;

        Row(String id, String label, String version, boolean present, String note) {
            this.id = id;
            this.label = label;
            this.version = version;
            this.present = present;
            this.note = note;
        }
    }

    private static final String[][] KNOWN = {
            {"iris", "Iris", "шейдеры: часть визуалов дороже, свечение может считаться дважды"},
            {"sodium", "Sodium", "свой рендер мира: настройки графики могут не совпадать"},
            {"embeddium", "Embeddium", "рендер мира заменён — графика настраивается у него"},
            {"rubidium", "Rubidium", "рендер мира заменён"},
            {"optifine", "OptiFine", "переопределяет рендер и шейдеры"},
            {"optifabric", "OptiFabric", "OptiFine через Fabric: возможны конфликты миксинов"},
            {"distanthorizons", "Distant Horizons", "дальний рельеф: свой проход по глубине"},
            {"vulkanmod", "VulkanMod", "другой графический бекенд"},
            {"nvidium", "Nvidium", "ускорение ханков, визуалы могут отличаться"},
            {"lithium", "Lithium", "оптимизация серверной логики (безопасно)"},
            {"ferritecore", "FerriteCore", "экономия памяти (безопасно)"},
            {"indium", "Indium", "слой совместимости для Sodium"},
            {"irisflw", "Iris Flywheel", "шейдерный мост"},
            {"flywheel", "Flywheel", "свой конвейер отрисовки"},
            {"entityculling", "Entity Culling", "часть сущностей может не отрисовываться"},
    };

    private static boolean scanned;
    private static boolean shadersSeen;
    private static int shaderPacks;
    private static List<ModEnv.Row> cached = new ArrayList<ModEnv.Row>();

    private ModEnv() {
    }

    /** Сбрасывает кэш сканирования: вызвать после установки модов на ходу. */
    public static synchronized void rescan() {
        ModEnv.scanned = false;
        cached = new ArrayList<ModEnv.Row>();
        ModEnv.rows();
    }

    public static boolean isLoaded(String id) {
        try {
            return FabricLoader.getInstance().isModLoaded(id);
        }
        catch (Throwable throwable) {
            return false;
        }
    }

    private static String version(String id) {
        try {
            return FabricLoader.getInstance().getModContainer(id)
                    .map(container -> ((ModContainer)container).getMetadata().getVersion().getFriendlyString())
                    .orElse("");
        }
        catch (Throwable throwable) {
            return "";
        }
    }

    public static synchronized List<ModEnv.Row> rows() {
        if (!ModEnv.scanned) {
            ModEnv.scanned = true;
            ArrayList<ModEnv.Row> list = new ArrayList<ModEnv.Row>();
            for (String[] entry : KNOWN) {
                boolean present = ModEnv.isLoaded(entry[0]);
                list.add(new ModEnv.Row(entry[0], entry[1], ModEnv.version(entry[0]), present, entry[2]));
            }
            cached = list;
            ModEnv.shaderPacks = ModEnv.countShaderPacks();
            ModEnv.shadersSeen = ModEnv.isLoaded("iris") || ModEnv.isLoaded("optifine") || ModEnv.isLoaded("optifabric");
        }
        return cached;
    }

    public static List<ModEnv.Row> presentRows() {
        ArrayList<ModEnv.Row> list = new ArrayList<ModEnv.Row>();
        for (ModEnv.Row row : ModEnv.rows()) {
            if (row.present) {
                list.add(row);
            }
        }
        return list;
    }

    /** Шейдеры могут быть подключены: Iris/OptiFine стоят и есть хотя бы один набор. */
    public static boolean shadersPossible() {
        ModEnv.rows();
        return ModEnv.shadersSeen;
    }

    public static int shaderPackCount() {
        ModEnv.rows();
        return ModEnv.shaderPacks;
    }

    private static int countShaderPacks() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            Path gameDir = client == null ? null : client.runDirectory.toPath();
            if (gameDir == null) {
                return 0;
            }
            Path folder = gameDir.resolve("shaderpacks");
            if (!Files.isDirectory(folder)) {
                return 0;
            }
            int count = 0;
            try (java.util.stream.Stream<Path> stream = Files.list(folder)) {
                for (Path path : (Iterable<Path>)stream::iterator) {
                    String name = path.getFileName().toString().toLowerCase(java.util.Locale.ROOT);
                    if (name.endsWith(".zip") || Files.isDirectory(path)) {
                        ++count;
                    }
                }
            }
            return count;
        }
        catch (Throwable throwable) {
            return 0;
        }
    }

    /** Короткая сводка для чата и окна диагностики. */
    public static String summary() {
        StringBuilder builder = new StringBuilder();
        for (ModEnv.Row row : ModEnv.presentRows()) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(row.label);
        }
        if (builder.length() == 0) {
            builder.append("рядом только ванильная игра");
        }
        if (ModEnv.shaderPacks > 0) {
            builder.append(" · наборов шейдеров: ").append(ModEnv.shaderPacks);
        }
        return builder.toString();
    }

    /** Советы под найденное окружение. */
    public static List<String> notes() {
        ArrayList<String> notes = new ArrayList<String>();
        ModEnv.rows();
        if (ModEnv.shadersPossible() && ModEnv.shaderPacks > 0) {
            notes.add("Найдены наборы шейдеров — в тяжёлых визуалах (следы, свечение) стоит ограничить размер");
        }
        if (ModEnv.isLoaded("optifine") || ModEnv.isLoaded("optifabric")) {
            notes.add("OptiFine задаёт свой рендер: если что-то мигает, выключите перекрытия в «Шейдеры/Свечение»");
        }
        if (ModEnv.isLoaded("sodium") || ModEnv.isLoaded("embeddium") || ModEnv.isLoaded("rubidium")) {
            notes.add("Рендер мира заменён оптимизатором: наши пресеты графики меняют только общие настройки");
        }
        if (ModEnv.isLoaded("entityculling")) {
            notes.add("Entity Culling может прятать чужих питомцев и косметику — проверьте, что косметика видна");
        }
        if (notes.isEmpty()) {
            notes.add("Конфликтов по окружению не видно");
        }
        return notes;
    }
}
