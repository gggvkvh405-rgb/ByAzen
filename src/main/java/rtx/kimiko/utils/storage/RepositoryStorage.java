package rtx.kimiko.utils.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.Map;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import rtx.kimiko.Kimiko;

public final class RepositoryStorage {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_ROOT = FabricLoader.getInstance().getGameDir().resolve("kimiko").resolve("configs");
    private static final Path ROOT = CONFIG_ROOT.resolve("system");
    private static final Path LEGACY_ROOT = FabricLoader.getInstance().getGameDir().resolve("kimiko");
    private static final Map<String, String> FILE_NAMES = Map.of("waypoints", "way");

    private RepositoryStorage() {
    }

    private static String fileName(String string) {
        return FILE_NAMES.getOrDefault(string, string);
    }

    private static Path file(String string) {
        return ROOT.resolve(RepositoryStorage.fileName(string) + ".kimiko");
    }

    public static JsonObject readObject(String string) {
        Path path = RepositoryStorage.file(string);
        if (!Files.exists(path)) {
            Path prev = RepositoryStorage.previousFormatFile(string);
            if (Files.exists(prev)) {
                path = prev;
            } else {
                Path legacy = RepositoryStorage.legacyFile(string);
                if (!Files.exists(legacy)) {
                    return new JsonObject();
                }
                path = legacy;
            }
        }
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception exception) {
            Kimiko.LOGGER.error("[RepositoryStorage] Failed to read {}", string, exception);
            return new JsonObject();
        }
    }

    public static Path root() {
        return ROOT;
    }

    public static void write(String string, JsonObject jsonObject) {
        try {
            Files.createDirectories(ROOT, new FileAttribute[0]);
            Path path = RepositoryStorage.file(string);
            try (BufferedWriter bufferedWriter = Files.newBufferedWriter(path, new OpenOption[0]);){
                GSON.toJson((JsonElement)jsonObject, (Appendable)bufferedWriter);
            }
        }
        catch (Exception exception) {
            Kimiko.LOGGER.error("[RepositoryStorage] Failed to write {}", (Object)string, (Object)exception);
        }
    }

    public static Path configRoot() {
        return CONFIG_ROOT;
    }

    public static void migratePreviousFormat() {
        if (!Files.exists(CONFIG_ROOT, new LinkOption[0])) {
            return;
        }
        try (Stream<Path> stream = Files.walk(CONFIG_ROOT, new FileVisitOption[0]);){
            stream.filter(path -> Files.isRegularFile(path, new LinkOption[0])).filter(path -> path.getFileName().toString().endsWith(".tria")).forEach(RepositoryStorage::migrateFile);
        }
        catch (Exception exception) {
            Kimiko.LOGGER.error("[RepositoryStorage] Failed to migrate .tria configs", (Throwable)exception);
        }
    }

    public static void ensureObject(String string, JsonObject jsonObject) {
        Path path = RepositoryStorage.file(string);
        if (Files.exists(path, new LinkOption[0])) {
            return;
        }
        JsonObject jsonObject2 = RepositoryStorage.readObject(string);
        RepositoryStorage.write(string, jsonObject2.isEmpty() ? jsonObject : jsonObject2);
    }

    private static void migrateFile(Path path) {
        String string = path.getFileName().toString();
        Path path2 = path.resolveSibling(string.substring(0, string.length() - ".tria".length()) + ".kimiko");
        if (Files.exists(path2, new LinkOption[0])) {
            return;
        }
        try {
            Files.move(path, path2, new CopyOption[0]);
        }
        catch (Exception exception) {
            Kimiko.LOGGER.warn("[RepositoryStorage] Failed to migrate {}", (Object)path, (Object)exception);
        }
    }

    private static Path previousFormatFile(String string) {
        return ROOT.resolve(RepositoryStorage.fileName(string) + ".tria");
    }

    private static Path legacyFile(String string) {
        String string2 = RepositoryStorage.fileName(string);
        Path path = LEGACY_ROOT.resolve(string2 + ".json");
        if (Files.exists(path, new LinkOption[0])) {
            return path;
        }
        return LEGACY_ROOT.resolve(string + ".json");
    }
}
