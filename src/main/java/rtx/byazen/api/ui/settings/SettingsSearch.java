package rtx.byazen.api.ui.settings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.Setting;
import rtx.byazen.utils.config.ModuleFavorites;

/**
 * Поиск по настройкам клиента (идея №40 из IDEAS.md).
 * <p>
 * Ищет по названию модуля, названию настройки и её описанию. Результат открывает окно настроек нужного
 * модуля и подсвечивает найденную строку, поэтому «где отключить эту штуку» перестаёт быть квестом.
 */
public final class SettingsSearch {

    private SettingsSearch() {
    }

    /**
     * @param module      модуль, который нужно открыть
     * @param settingName имя настройки для подсветки, либо null - открыть модуль целиком
     * @param title       подпись строки
     * @param subtitle    вторая строка: описание настройки или модуля
     * @param favorite    модуль в избранном
     */
    public record Entry(Module module, String settingName, String title, String subtitle, boolean favorite) {
    }

    public static List<Entry> search(String query, int limit) {
        ArrayList<Entry> results = new ArrayList<Entry>();
        if (query == null || query.trim().length() < 1) {
            return results;
        }
        String needle = SettingsSearch.normalize(query);
        ArrayList<Scored> scored = new ArrayList<Scored>();
        for (Module module : ModuleManager.get().getAll()) {
            boolean favorite = ModuleFavorites.isFavorite(module);
            if (SettingsSearch.normalize(module.getName()).contains(needle)) {
                String description = module.getDescription() == null ? "" : module.getDescription();
                scored.add(new Scored(
                        new SettingsSearch.Entry(module, null, module.getDisplayName(), description, favorite),
                        favorite ? 0 : 1));
            }
            for (Setting setting : module.getSettings().all()) {
                String settingName = SettingsSearch.normalize(setting.getName());
                String description = SettingsSearch.normalize(setting.getDescription());
                String subtitle = module.getDisplayName()
                        + (setting.getDescription() == null || setting.getDescription().isBlank() ? "" : " • " + setting.getDescription());
                if (settingName.contains(needle)) {
                    scored.add(new Scored(new SettingsSearch.Entry(module, setting.getName(), setting.getName(), subtitle, favorite), 2));
                }
                else if (!description.isEmpty() && description.contains(needle)) {
                    scored.add(new Scored(new SettingsSearch.Entry(module, setting.getName(), setting.getName(), subtitle, favorite), 3));
                }
            }
        }
        scored.sort((left, right) -> {
            if (left.rank != right.rank) {
                return Integer.compare(left.rank, right.rank);
            }
            return left.entry.title().compareToIgnoreCase(right.entry.title());
        });
        for (Scored item : scored) {
            if (results.size() >= limit) {
                break;
            }
            results.add(item.entry);
        }
        return results;
    }

    /** Приводит строку к виду без регистра, пробелов и разделителей - так «hudstyle» находит «HUD Style». */
    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(text.length());
        for (char symbol : text.toLowerCase(Locale.ROOT).toCharArray()) {
            if (Character.isLetterOrDigit(symbol)) {
                builder.append(symbol);
            }
        }
        return builder.toString();
    }

    private record Scored(Entry entry, int rank) {
    }
}
