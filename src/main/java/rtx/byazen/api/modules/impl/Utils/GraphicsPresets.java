package rtx.byazen.api.modules.impl.Utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.graphics.GraphicsSettings;
import rtx.byazen.utils.storage.RepositoryStorage;

/**
 * Пресеты графики (идея №116 из IDEAS.md).
 * <p>
 * Четыре готовых режима одним нажатием: «PvP 240 fps», «Баланс», «Кино 60 fps» и «Стрим». Пресет
 * выставляет и настройки самой игры (дальность, частицы, тени, сглаживание, облака), и настройки
 * тяжёлых модулей клиента. Перед применением текущий набор сохраняется, поэтому кнопка «Вернуть как
 * было» возвращает всё в исходное состояние.
 */
public final class GraphicsPresets
extends Module {

    private static final String BACKUP = "graphics_before_preset.json";
    private static final String[] PRESETS = {"PvP 240 fps", "Баланс", "Кино 60 fps", "Стрим"};

    public final SeparatorSetting presetsGroup = this.register(new SeparatorSetting("Пресеты"));
    public final ModeSetting preset = this.register(new ModeSetting("Режим графики", "Готовый набор настроек под задачу.", "Баланс", PRESETS));
    public final ButtonSetting applyPreset = this.register(new ButtonSetting("Применить пресет", "Выставить настройки игры и модулей выбранного режима.").label("Применить").onClick(this::applySelected));
    public final ButtonSetting restorePreset = this.register(new ButtonSetting("Вернуть как было", "Вернуть настройки, которые были до последнего пресета.").label("Вернуть").onClick(GraphicsPresets::restore));
    public final SeparatorSetting autoGroup = this.register(new SeparatorSetting("Автоматика"));
    public final BooleanSetting applyOnEnable = this.register(new BooleanSetting("Применять при включении", "Сразу выставлять выбранный пресет, когда модуль включается.", false));
    public final BooleanSetting tellInChat = this.register(new BooleanSetting("Писать в чат", "Сообщать, что именно изменил пресет.", true));
    public final BooleanSetting saveGameOptions = this.register(new BooleanSetting("Сохранять в файл игры", "Записывать изменённые настройки в options.txt сразу.", true));

    public GraphicsPresets() {
        super("GraphicsPresets", "Пресеты графики: «PvP 240 fps», «Баланс», «Кино 60 fps» и «Стрим» одним нажатием.", Category.UTILS);
    }

    @Override
    protected void onEnable() {
        if (this.applyOnEnable.getValue()) {
            this.applySelected();
        }
    }

    private void applySelected() {
        GraphicsPresets.apply(this.preset.getValue(), this);
    }

    /** Применяет пресет по названию. */
    public static void apply(String name, Module caller) {
        boolean tell = caller instanceof GraphicsPresets && ((GraphicsPresets)caller).tellInChat.getValue();
        boolean save = !(caller instanceof GraphicsPresets) || ((GraphicsPresets)caller).saveGameOptions.getValue();
        GraphicsPresets.capture();
        int changed = 0;
        if ("PvP 240 fps".equals(name)) {
            changed += GraphicsPresets.game(6, 5, "MINIMAL", "FAST", false, false, false, false);
            changed += GraphicsPresets.modules(false, true, false, false, false, false);
        }
        else if ("Кино 60 fps".equals(name)) {
            changed += GraphicsPresets.game(16, 12, "ALL", "FABULOUS", true, true, true, true);
            changed += GraphicsPresets.modules(true, false, true, true, true, false);
        }
        else if ("Стрим".equals(name)) {
            changed += GraphicsPresets.game(12, 10, "DECREASED", "FANCY", true, true, false, true);
            changed += GraphicsPresets.modules(false, true, true, false, false, true);
        }
        else {
            changed += GraphicsPresets.game(10, 8, "DECREASED", "FANCY", true, true, true, false);
            changed += GraphicsPresets.modules(true, true, true, false, false, false);
        }
        if (save) {
            GraphicsSettings.save();
        }
        if (tell) {
            ChatMessage.send("Пресет «" + name + "» применён: изменено настроек " + changed);
        }
    }

    private static int game(int renderDistance, int simulationDistance, String particles, String graphics, boolean clouds, boolean shadows, boolean smooth, boolean fancySky) {
        int changed = 0;
        changed += GraphicsSettings.setNumber(renderDistance, "renderDistance") ? 1 : 0;
        changed += GraphicsSettings.setNumber(simulationDistance, "simulationDistance") ? 1 : 0;
        changed += GraphicsSettings.setEnum(particles, "particles") ? 1 : 0;
        changed += GraphicsSettings.setEnum(graphics, "graphicsMode") ? 1 : 0;
        changed += GraphicsSettings.setEnum(clouds ? "FANCY" : "OFF", "clouds", "cloudRenderMode") ? 1 : 0;
        changed += GraphicsSettings.setBoolean(shadows, "entityShadows") ? 1 : 0;
        changed += GraphicsSettings.setBoolean(smooth, "ao", "ambientOcclusion") ? 1 : 0;
        changed += GraphicsSettings.setBoolean(fancySky, "viewBobbing") ? 1 : 0;
        return changed;
    }

    private static int modules(boolean optimization, boolean antialiasing, boolean bloom, boolean shadows, boolean particles, boolean streamer) {
        int changed = 0;
        changed += GraphicsPresets.set("Optimization", optimization) ? 1 : 0;
        changed += GraphicsPresets.set("AntiAliasing", antialiasing) ? 1 : 0;
        changed += GraphicsPresets.set("Bloom", bloom) ? 1 : 0;
        changed += GraphicsPresets.set("Shadows", shadows) ? 1 : 0;
        changed += GraphicsPresets.set("CustomParticles", particles) ? 1 : 0;
        changed += GraphicsPresets.set("KillFX", particles) ? 1 : 0;
        changed += GraphicsPresets.set("StreamerMode", streamer) ? 1 : 0;
        changed += GraphicsPresets.set("NoRender", optimization) ? 1 : 0;
        return changed;
    }

    private static boolean set(String moduleName, boolean enabled) {
        Module module = ModuleManager.get().findByName(moduleName);
        if (module == null || module.isEnabled() == enabled) {
            return false;
        }
        if (enabled) {
            module.enable();
        }
        else {
            module.disable();
        }
        return true;
    }

    /** Запоминает текущие настройки игры, чтобы потом можно было вернуть их обратно. */
    public static void capture() {
        try {
            JsonObject root = new JsonObject();
            for (String option : GraphicsPresets.optionNames()) {
                root.addProperty(option, GraphicsSettings.current(option));
            }
            JsonObject modules = new JsonObject();
            for (String name : new String[]{"Optimization", "AntiAliasing", "Bloom", "Shadows", "CustomParticles", "KillFX", "StreamerMode", "NoRender"}) {
                Module module = ModuleManager.get().findByName(name);
                if (module != null) {
                    modules.addProperty(name, module.isEnabled());
                }
            }
            root.add("modules", modules);
            Path path = GraphicsPresets.file();
            Files.createDirectories(path.getParent());
            Files.writeString(path, root.toString(), StandardCharsets.UTF_8);
        }
        catch (Throwable ignored) {
            // не удалось запомнить — пресет всё равно применяем
        }
    }

    /** Возвращает настройки, запомненные перед последним пресетом. */
    public static void restore() {
        try {
            Path path = GraphicsPresets.file();
            if (!Files.isRegularFile(path)) {
                ChatMessage.send("Пока нечего возвращать: пресет ещё не применялся.");
                return;
            }
            JsonElement element = JsonParser.parseString(Files.readString(path, StandardCharsets.UTF_8));
            if (element == null || !element.isJsonObject()) {
                ChatMessage.send("Запомненные настройки повреждены — верните их вручную в меню игры.");
                return;
            }
            JsonObject root = element.getAsJsonObject();
            int changed = 0;
            for (String option : GraphicsPresets.optionNames()) {
                if (!root.has(option)) {
                    continue;
                }
                String value = root.get(option).getAsString();
                if (value.isEmpty() || "недоступно".equals(value)) {
                    continue;
                }
                if (GraphicsPresets.restoreOption(option, value)) {
                    ++changed;
                }
            }
            if (root.has("modules") && root.get("modules").isJsonObject()) {
                for (Map.Entry<String, JsonElement> pair : root.getAsJsonObject("modules").entrySet()) {
                    Module module = ModuleManager.get().findByName(pair.getKey());
                    if (module == null) {
                        continue;
                    }
                    boolean enabled = pair.getValue().getAsBoolean();
                    if (module.isEnabled() != enabled) {
                        if (enabled) {
                            module.enable();
                        }
                        else {
                            module.disable();
                        }
                        ++changed;
                    }
                }
            }
            GraphicsSettings.save();
            ChatMessage.send("Настройки возвращены: изменено " + changed + " значений.");
        }
        catch (Throwable throwable) {
            ChatMessage.send("Не удалось вернуть настройки: " + throwable.getClass().getSimpleName());
        }
    }

    private static boolean restoreOption(String option, String value) {
        try {
            double numeric = Double.parseDouble(value);
            return GraphicsSettings.setNumber(numeric, option);
        }
        catch (NumberFormatException ignored) {
            // значит это перечисление или логическое значение
        }
        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
            return GraphicsSettings.setBoolean(Boolean.parseBoolean(value), option);
        }
        return GraphicsSettings.setEnum(value, option);
    }

    private static String[] optionNames() {
        return new String[]{"renderDistance", "simulationDistance", "particles", "graphicsMode", "clouds", "entityShadows", "ao", "viewBobbing"};
    }

    private static Path file() {
        return RepositoryStorage.configRoot().resolve(BACKUP);
    }

    /** Короткое описание текущих настроек: подсказка в интерфейсе. */
    public static String currentState() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        for (String option : GraphicsPresets.optionNames()) {
            map.put(option, GraphicsSettings.current(option));
        }
        return "Дальность " + map.get("renderDistance") + " · частицы " + map.get("particles")
                + " · графика " + map.get("graphicsMode") + " · облака " + map.get("clouds");
    }
}
