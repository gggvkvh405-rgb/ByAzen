package rtx.byazen.utils.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import rtx.byazen.api.modules.Module;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Избранные модули (идея №41 из IDEAS.md): помеченные звёздочкой модули поднимаются в начало списка
 * и остаются под рукой в любой категории.
 */
public final class ModuleFavorites {

    private static final String FILE = "module_favorites";
    private static final Set<String> NAMES = new LinkedHashSet<String>();
    private static boolean loaded;
    private static boolean dirty;

    private ModuleFavorites() {
    }

    public static boolean isFavorite(Module module) {
        return module != null && ModuleFavorites.isFavorite(module.getName());
    }

    public static boolean isFavorite(String name) {
        ModuleFavorites.load();
        return name != null && NAMES.contains(name);
    }

    /** Переключает звёздочку, возвращает новое состояние. */
    public static boolean toggle(Module module) {
        if (module == null) {
            return false;
        }
        ModuleFavorites.load();
        boolean now;
        if (NAMES.contains(module.getName())) {
            NAMES.remove(module.getName());
            now = false;
        }
        else {
            NAMES.add(module.getName());
            now = true;
        }
        dirty = true;
        ModuleFavorites.save();
        return now;
    }

    public static List<String> names() {
        ModuleFavorites.load();
        return Collections.unmodifiableList(new ArrayList<String>(NAMES));
    }

    public static int count() {
        ModuleFavorites.load();
        return NAMES.size();
    }

    private static void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        try {
            JsonObject root = RepositoryStorage.readObject(FILE);
            JsonArray array = root.getAsJsonArray("favorites");
            if (array == null) {
                return;
            }
            for (JsonElement element : array) {
                if (element.isJsonPrimitive()) {
                    NAMES.add(element.getAsString());
                }
            }
        }
        catch (Throwable ignored) {
        }
    }

    private static void save() {
        if (!dirty) {
            return;
        }
        dirty = false;
        JsonObject root = new JsonObject();
        JsonArray array = new JsonArray();
        for (String name : NAMES) {
            array.add(name);
        }
        root.add("favorites", array);
        RepositoryStorage.write(FILE, root);
    }
}
