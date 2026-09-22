package rtx.byazen.utils.graffiti;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import net.fabricmc.loader.api.FabricLoader;
import rtx.byazen.ByAzen;

/**
 * Хранилище граффити (идея №68 из IDEAS.md): папка с PNG-слоями и список наклеек в мире.
 * <p>
 * Рисунки лежат в {@code .minecraft/byazen/graffiti/*.png}, а координаты наклеек — в
 * {@code placed.json}. Файлы читаются и пишутся аккуратно, ошибки не ломают игру.
 */
public final class GraffitiStore {

    public static final int CANVAS_SIZE = 512;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private GraffitiStore() {
    }

    /** Папка с рисунками; создаётся при первом обращении. */
    public static Path directory() {
        Path path = FabricLoader.getInstance().getGameDir().resolve("byazen").resolve("graffiti");
        try {
            Files.createDirectories(path);
        }
        catch (IOException exception) {
            ByAzen.LOGGER.warn("[ByAzen] Не удалось создать папку граффити: {}", exception.toString());
        }
        return path;
    }

    /** Все сохранённые рисунки по алфавиту. */
    public static List<Path> saved() {
        ArrayList<Path> list = new ArrayList<Path>();
        try (Stream<Path> stream = Files.list(GraffitiStore.directory())) {
            stream.filter(path -> {
                String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
                return name.endsWith(".png");
            }).sorted().forEach(list::add);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.debug("[ByAzen] Список граффити недоступен: {}", throwable.toString());
        }
        return list;
    }

    /** Свободное имя файла вида graffiti_3.png. */
    public static String nextName() {
        for (int i = 1; i < 999; ++i) {
            String name = "graffiti_" + i + ".png";
            if (!Files.exists(GraffitiStore.directory().resolve(name))) {
                return name;
            }
        }
        return "graffiti_" + System.currentTimeMillis() + ".png";
    }

    /** Сохраняет штрихи редактора в PNG: фон + линии, всё с мягким сглаживанием. */
    public static Path savePng(String fileName, String background, List<Stroke> strokes) {
        String name = fileName == null || fileName.isBlank() ? GraffitiStore.nextName() : fileName;
        if (!name.toLowerCase(Locale.ROOT).endsWith(".png")) {
            name = name + ".png";
        }
        Path path = GraffitiStore.directory().resolve(name);
        try {
            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(CANVAS_SIZE, CANVAS_SIZE, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D graphics = image.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            if (background != null && !background.isEmpty()) {
                graphics.setColor(Color.decode(background));
                graphics.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
            }
            for (Stroke stroke : strokes) {
                if (stroke == null || stroke.color == null) {
                    continue;
                }
                graphics.setColor(Color.decode(stroke.color));
                graphics.setStroke(new BasicStroke(Math.max(1.0f, stroke.width * (float)CANVAS_SIZE / 128.0f),
                        BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                graphics.draw(new Line2D.Float(stroke.x1 * (float)CANVAS_SIZE, stroke.y1 * (float)CANVAS_SIZE,
                        stroke.x2 * (float)CANVAS_SIZE, stroke.y2 * (float)CANVAS_SIZE));
            }
            graphics.dispose();
            ImageIO.write(image, "png", path.toFile());
            return path;
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Граффити не сохранилось: {}", throwable.toString());
            return null;
        }
    }

    /** Загружает список наклеек. */
    public static List<Decal> load() {
        ArrayList<Decal> list = new ArrayList<Decal>();
        Path path = GraffitiStore.directory().resolve("placed.json");
        if (!Files.exists(path)) {
            return list;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (root == null || !root.isJsonArray()) {
                return list;
            }
            JsonArray array = root.getAsJsonArray();
            for (JsonElement element : array) {
                if (!element.isJsonObject()) {
                    continue;
                }
                JsonObject object = element.getAsJsonObject();
                Decal decal = new Decal();
                decal.world = GraffitiStore.string(object, "world", "");
                decal.file = GraffitiStore.string(object, "file", "");
                decal.side = GraffitiStore.string(object, "side", "north");
                decal.x = GraffitiStore.number(object, "x", 0.0);
                decal.y = GraffitiStore.number(object, "y", 0.0);
                decal.z = GraffitiStore.number(object, "z", 0.0);
                decal.size = (float)GraffitiStore.number(object, "size", 1.0);
                decal.rotation = (float)GraffitiStore.number(object, "rotation", 0.0);
                decal.createdAt = (long)GraffitiStore.number(object, "createdAt", 0.0);
                if (!decal.file.isEmpty()) {
                    list.add(decal);
                }
            }
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Список граффити не прочитан: {}", throwable.toString());
        }
        return list;
    }

    /** Пишет список наклеек на диск. */
    public static void save(List<Decal> decals) {
        JsonArray array = new JsonArray();
        if (decals != null) {
            for (Decal decal : decals) {
                if (decal == null) {
                    continue;
                }
                JsonObject object = new JsonObject();
                object.addProperty("world", decal.world);
                object.addProperty("file", decal.file);
                object.addProperty("side", decal.side);
                object.addProperty("x", decal.x);
                object.addProperty("y", decal.y);
                object.addProperty("z", decal.z);
                object.addProperty("size", decal.size);
                object.addProperty("rotation", decal.rotation);
                object.addProperty("createdAt", decal.createdAt);
                array.add(object);
            }
        }
        Path path = GraffitiStore.directory().resolve("placed.json");
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(array, writer);
        }
        catch (Throwable throwable) {
            ByAzen.LOGGER.warn("[ByAzen] Наклейки не сохранились: {}", throwable.toString());
        }
    }

    public static Path fileOf(Decal decal) {
        if (decal == null || decal.file == null || decal.file.isEmpty()) {
            return null;
        }
        return GraffitiStore.directory().resolve(decal.file);
    }

    private static String string(JsonObject object, String key, String fallback) {
        try {
            return object.has(key) ? object.get(key).getAsString() : fallback;
        }
        catch (Throwable throwable) {
            return fallback;
        }
    }

    private static double number(JsonObject object, String key, double fallback) {
        try {
            return object.has(key) ? object.get(key).getAsDouble() : fallback;
        }
        catch (Throwable throwable) {
            return fallback;
        }
    }

    /** Один штрих редактора в координатах 0..1 — так рисунок не зависит от размера холста. */
    public static final class Stroke {

        public final float x1;
        public final float y1;
        public final float x2;
        public final float y2;
        public final float width;
        public final String color;

        public Stroke(float x1, float y1, float x2, float y2, float width, String color) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.width = width;
            this.color = color;
        }
    }

    /** Наклейка в мире: мир, позиция, грань блока, файл рисунка, размер и поворот. */
    public static final class Decal {

        public String world = "";
        public String file = "";
        public String side = "north";
        public double x;
        public double y;
        public double z;
        public float size = 1.0f;
        public float rotation;
        public long createdAt;

        public Decal copy() {
            Decal decal = new Decal();
            decal.world = this.world;
            decal.file = this.file;
            decal.side = this.side;
            decal.x = this.x;
            decal.y = this.y;
            decal.z = this.z;
            decal.size = this.size;
            decal.rotation = this.rotation;
            decal.createdAt = this.createdAt;
            return decal;
        }
    }
}
