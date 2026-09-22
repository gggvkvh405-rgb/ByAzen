package rtx.byazen.utils.help;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.Setting;
import rtx.byazen.api.modules.settings.impl.BindSetting;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.ColorSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.MultiSelectSetting;
import rtx.byazen.api.modules.settings.impl.SelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.modules.settings.impl.StringSetting;
import rtx.byazen.api.ui.UI;

/**
 * «Как это работает» и поиск «где найти» (идеи №189 и №197 из IDEAS.md).
 * <p>
 * По любому модулю собирается короткая человеческая инструкция: что он делает, где его искать,
 * что нажать и какие настройки важны. Тот же поиск понимает не только названия модулей, но и
 * простые слова («звук», «прицел», «миникарта») и описания настроек.
 */
public final class ModuleHelp {

    /** Русские слова, которые ищут игроки, и с чем их сопоставлять. */
    private static final Map<String, String[]> SYNONYMS = new LinkedHashMap<String, String[]>();

    static {
        ModuleHelp.synonym("звук", "sound", "music", "музык", "звон", "audio");
        ModuleHelp.synonym("музыка", "music", "плеер", "radio", "радио");
        ModuleHelp.synonym("прицел", "crosshair", "приц", "aim");
        ModuleHelp.synonym("часы", "clock", "время", "timer", "таймер");
        ModuleHelp.synonym("координаты", "coord", "позици", "position", "biome");
        ModuleHelp.synonym("миникарта", "minimap", "карта", "waypoint", "точк");
        ModuleHelp.synonym("фпс", "fps", "производительн", "профилиров", "лаг");
        ModuleHelp.synonym("чат", "chat", "сообщени", "message");
        ModuleHelp.synonym("плащ", "cape", "космет", "cosmetic");
        ModuleHelp.synonym("урон", "damage", "бой", "fight", "hit", "удар");
        ModuleHelp.synonym("питомец", "pet", "собака", "кот");
        ModuleHelp.synonym("скриншот", "screenshot", "снимок");
        ModuleHelp.synonym("интерфейс", "interface", "hud", "меню", "клик", "clickgui");
        ModuleHelp.synonym("обновление", "update", "версия", "version");
        ModuleHelp.synonym("туториал", "tutorial", "обучени", "подсказк");
    }

    private ModuleHelp() {
    }

    private static void synonym(String key, String... needles) {
        SYNONYMS.put(key, needles);
    }

    /** Короткая строка «куда смотреть»: категория, бинд, доступность. */
    public static String where(Module module) {
        if (module == null) {
            return "модуль не найден";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("раздел «").append(module.getCategory().getDisplayName()).append("»");
        if (module.getBind() != null && module.getBind().isBound()) {
            builder.append(", клавиша ").append(module.getBind().getDisplayName());
        }
        builder.append(module.isEnabled() ? ", сейчас включён" : ", сейчас выключен");
        return builder.toString();
    }

    /** Полная инструкция «как это работает» для чата и окна. */
    public static List<String> full(Module module) {
        ArrayList<String> lines = new ArrayList<String>();
        if (module == null) {
            lines.add("§7Такого модуля нет — попробуйте §f.how <часть названия>");
            return lines;
        }
        lines.add("§b" + module.getDisplayName() + " §7— " + ModuleHelp.where(module));
        String description = module.getDescription();
        if (description != null && !description.isBlank()) {
            lines.add("§7Что делает: §f" + description);
        }
        List<String> steps = ModuleHelp.steps(module);
        if (!steps.isEmpty()) {
            lines.add("§7Как пользоваться:");
            for (String step : steps) {
                lines.add("§8 • §7" + step);
            }
        }
        List<String> key = ModuleHelp.keySettings(module);
        if (!key.isEmpty()) {
            lines.add("§7Главные настройки: §f" + String.join("§7, §f", key));
        }
        lines.add("§8Открыть настройки: §7найти модуль в списке или ввести §f.find " + ModuleHelp.query(module));
        return lines;
    }

    /** Короткие подсказки по шагам: как включить и что настроить. */
    public static List<String> steps(Module module) {
        ArrayList<String> steps = new ArrayList<String>();
        if (module == null) {
            return steps;
        }
        steps.add("включить: клик по модулю в списке или назначить клавишу в его настройках");
        if (module.getBind() != null && module.getBind().isBound()) {
            steps.add("клавиша уже назначена: " + module.getBind().getDisplayName());
        }
        int buttons = ModuleHelp.count(module, ButtonSetting.class);
        if (buttons > 0) {
            steps.add("в настройках есть кнопки действий (" + buttons + ") — они делают всё сразу");
        }
        if (ModuleHelp.count(module, ModeSetting.class) + ModuleHelp.count(module, SelectSetting.class) > 0) {
            steps.add("режимы переключаются кликом по строке настроек");
        }
        if (ModuleHelp.count(module, SliderSetting.class) > 0) {
            steps.add("ползунки тянутся мышью, значение видно рядом");
        }
        return steps;
    }

    /** Самые полезные настройки: кнопки, режимы, переключатели. */
    public static List<String> keySettings(Module module) {
        ArrayList<String> names = new ArrayList<String>();
        if (module == null) {
            return names;
        }
        for (Setting setting : module.getSettings().all()) {
            if (names.size() >= 6 || setting instanceof SeparatorSetting) {
                continue;
            }
            if (setting instanceof ButtonSetting || setting instanceof ModeSetting || setting instanceof SelectSetting
                    || setting instanceof BooleanSetting || setting instanceof BindSetting) {
                names.add(setting.getName());
            }
        }
        if (names.isEmpty()) {
            for (Setting setting : module.getSettings().all()) {
                if (names.size() >= 4 || setting instanceof SeparatorSetting) {
                    continue;
                }
                names.add(setting.getName());
            }
        }
        return names;
    }

    private static int count(Module module, Class<? extends Setting> type) {
        int total = 0;
        for (Setting setting : module.getSettings().all()) {
            if (type.isInstance(setting)) {
                ++total;
            }
        }
        return total;
    }

    /** Строка для поиска в ClickGui: по ней открывается сам модуль. */
    public static String query(Module module) {
        return module == null ? "" : module.getDisplayName().split(" ")[0].toLowerCase(Locale.ROOT);
    }

    /** Поиск «где найти»: понимает названия, описания, настройки и простые слова. */
    public static Module find(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }
        String needle = query.trim().toLowerCase(Locale.ROOT);
        Module best = null;
        int bestScore = 0;
        for (Module module : ModuleManager.get().getAll()) {
            int score = ModuleHelp.score(module, needle);
            if (score > bestScore) {
                bestScore = score;
                best = module;
            }
        }
        return bestScore >= 20 ? best : null;
    }

    private static int score(Module module, String needle) {
        int score = 0;
        String name = module.getName() == null ? "" : module.getName().toLowerCase(Locale.ROOT);
        String display = module.getDisplayName() == null ? "" : module.getDisplayName().toLowerCase(Locale.ROOT);
        String description = module.getDescription() == null ? "" : module.getDescription().toLowerCase(Locale.ROOT);
        if (name.equals(needle) || display.equals(needle)) {
            return 120;
        }
        if (name.startsWith(needle) || display.startsWith(needle)) {
            score += 70;
        }
        else if (name.contains(needle) || display.contains(needle)) {
            score += 55;
        }
        if (description.contains(needle)) {
            score += 35;
        }
        for (Setting setting : module.getSettings().all()) {
            String settingName = setting.getName() == null ? "" : setting.getName().toLowerCase(Locale.ROOT);
            if (settingName.contains(needle)) {
                score += 18;
                break;
            }
        }
        for (String word : needle.split("[ ,]+")) {
            if (word.length() < 3) {
                continue;
            }
            if (name.contains(word) || display.contains(word)) {
                score += 25;
            }
            else if (description.contains(word)) {
                score += 12;
            }
            for (Map.Entry<String, String[]> entry : SYNONYMS.entrySet()) {
                if (!entry.getKey().startsWith(word) && !word.startsWith(entry.getKey())) {
                    continue;
                }
                for (String candidate : entry.getValue()) {
                    if (name.contains(candidate) || display.contains(candidate) || description.contains(candidate)) {
                        score += 20;
                        break;
                    }
                }
            }
        }
        return score;
    }

    /** Открывает модуль в клиенте и подсвечивает его: «вот оно где». */
    public static String locate(String query) {
        Module module = ModuleHelp.find(query);
        if (module == null) {
            return "Ничего не нашлось по запросу «" + query + "». Попробуйте другое слово — например, «звук», «прицел», «чат».";
        }
        Highlight.pulse(module);
        UI.focusModuleInGui(module);
        UI.openModuleSettings(module, null);
        return "Нашёл: " + module.getDisplayName() + " — " + ModuleHelp.where(module) + " (модуль подсвечен)";
    }

    /** Список всех модулей с биндами: для туториала, справочника и документов. */
    public static List<String> allModules() {
        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<String> categories = new ArrayList<String>(Arrays.asList("Visuals", "Display", "Utils", "Events", "Themes"));
        for (String category : categories) {
            ArrayList<String> names = new ArrayList<String>();
            for (Module module : ModuleManager.get().getAll()) {
                if (module.getCategory() != null && category.equalsIgnoreCase(module.getCategory().getDisplayName())) {
                    names.add(module.getDisplayName() + (module.isEnabled() ? " (вкл)" : ""));
                }
            }
            if (!names.isEmpty()) {
                names.sort(String::compareToIgnoreCase);
                lines.add("§b" + category + " §7(" + names.size() + "): §f" + String.join("§7, §f", names));
            }
        }
        return lines;
    }

    /** Ищет настройку по имени — для «где найти» из чата. */
    public static String findSettingName(Module module, String needle) {
        if (module == null || needle == null || needle.isBlank()) {
            return null;
        }
        String lower = needle.toLowerCase(Locale.ROOT);
        for (Setting setting : module.getSettings().all()) {
            String name = setting.getName() == null ? "" : setting.getName().toLowerCase(Locale.ROOT);
            if (name.contains(lower)) {
                return setting.getName();
            }
        }
        return null;
    }

    /** Тип настройки по-русски: для документов и подсказок. */
    public static String kind(Setting setting) {
        if (setting instanceof SeparatorSetting) {
            return "группа";
        }
        if (setting instanceof ButtonSetting) {
            return "кнопка";
        }
        if (setting instanceof BindSetting) {
            return "клавиша";
        }
        if (setting instanceof SliderSetting) {
            return "ползунок";
        }
        if (setting instanceof BooleanSetting) {
            return "переключатель";
        }
        if (setting instanceof ColorSetting) {
            return "цвет";
        }
        if (setting instanceof ModeSetting || setting instanceof SelectSetting) {
            return "режим";
        }
        if (setting instanceof MultiSelectSetting) {
            return "список";
        }
        if (setting instanceof StringSetting) {
            return "текст";
        }
        return "настройка";
    }
}

