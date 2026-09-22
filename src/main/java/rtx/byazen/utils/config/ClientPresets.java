package rtx.byazen.utils.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.impl.Utils.GraphicsPresets;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.profiles.BuildTier;
import rtx.byazen.utils.web.WebBridge;

/**
 * Готовые пресеты клиента (идея №200 из IDEAS.md).
 * <p>
 * Пять наборов под разные задачи: «Анархия», «PvP», «Выживание», «Стример» и «Минимум».
 * Пресет включает нужные модули, выключает лишние, ставит подходящий графический пресет и,
 * если нужно, облегчает клиент. Только визуал и информация: никакой автоматизации боя и ESP.
 */
public final class ClientPresets {

    /** Пресет: название, для чего он и что делает. */
    public record Preset(String id, String title, String note) {
    }

    private static final List<Preset> PRESETS = new ArrayList<Preset>();
    private static final Map<String, String[]> ENABLE = new LinkedHashMap<String, String[]>();
    private static final Map<String, String[]> DISABLE = new LinkedHashMap<String, String[]>();
    private static final Map<String, String> GRAPHICS = new LinkedHashMap<String, String>();

    static {
        ClientPresets.add("anarchy", "Анархия", "боевая информация и метки для серверов без правил");
        ClientPresets.add("pvp", "PvP", "максимум FPS и информация для боёв");
        ClientPresets.add("survival", "Выживание", "координаты, точки, статистика и уютные визуалы");
        ClientPresets.add("streamer", "Стример", "чистая картинка для стрима и оверлей OBS");
        ClientPresets.add("minimal", "Минимум", "самый лёгкий режим для слабых ПК");

        ClientPresets.enable("anarchy", "Hit Color", "NameTags", "Armor", "HP Counter", "Cooldowns", "Potions",
                "Waypoints", "Death Coords", "Info", "Session Stats");
        ClientPresets.disable("anarchy", "Streamer Mode", "Emotions", "Footprints", "China Hat");

        ClientPresets.enable("pvp", "Optimization", "Hit Color", "Hit Sound", "HP Counter", "Armor", "Cooldowns",
                "KeyStrokes", "Crosshair", "Damage Effects");
        ClientPresets.disable("pvp", "Cosmetics 3D", "Bloom", "Shadows", "Footprints", "Emotions", "China Hat");

        ClientPresets.enable("survival", "Waypoints", "Death Coords", "Clock", "Session Stats", "Notes", "Reminders",
                "Trajectories", "Item Physics", "Minimap");
        ClientPresets.disable("survival", "Streamer Mode", "Kill Effect 3D");

        ClientPresets.enable("streamer", "Streamer Mode", "Stream Overlay", "Quick Hide", "Notifications",
                "Session Stats", "Music Player", "Chat Translate");
        ClientPresets.disable("streamer", "Kill Effect 3D", "Footprints", "Hit Particles", "Jump Circle");

        ClientPresets.disable("minimal", "Cosmetics 3D", "Bloom", "Shadows", "Footprints", "Emotions",
                "Kill Effect 3D", "Custom Particles", "Animated Capes", "China Hat", "Trails", "Wings",
                "World Particles", "Pet Style");

        GRAPHICS.put("anarchy", "Баланс");
        GRAPHICS.put("pvp", "PvP 240 fps");
        GRAPHICS.put("survival", "Баланс");
        GRAPHICS.put("streamer", "Стрим");
        GRAPHICS.put("minimal", "PvP 240 fps");
    }

    private ClientPresets() {
    }

    private static void add(String id, String title, String note) {
        PRESETS.add(new Preset(id, title, note));
    }

    private static void enable(String id, String... names) {
        ENABLE.put(id, names);
    }

    private static void disable(String id, String... names) {
        DISABLE.put(id, names);
    }

    public static List<Preset> presets() {
        return new ArrayList<Preset>(PRESETS);
    }

    public static Preset preset(String id) {
        for (Preset preset : PRESETS) {
            if (preset.id().equalsIgnoreCase(id) || preset.title().equalsIgnoreCase(id)) {
                return preset;
            }
        }
        return null;
    }

    /** Что именно сделает пресет: показываем до применения. */
    public static List<String> plan(String id) {
        Preset preset = ClientPresets.preset(id);
        ArrayList<String> plan = new ArrayList<String>();
        if (preset == null) {
            plan.add("§7Пресет не найден");
            return plan;
        }
        plan.add("§b" + preset.title() + " §7— " + preset.note());
        String graphics = GRAPHICS.get(preset.id());
        if (graphics != null) {
            plan.add("§7графика: §f" + graphics);
        }
        String[] on = ENABLE.get(preset.id());
        if (on != null && on.length > 0) {
            plan.add("§7включим: §f" + String.join("§7, §f", on));
        }
        String[] off = DISABLE.get(preset.id());
        if (off != null && off.length > 0) {
            plan.add("§7выключим: §f" + String.join("§7, §f", off));
        }
        if (preset.id().equals("minimal")) {
            plan.add("§7плюс облегчённый профиль Lite: тяжёлые визуалы и кэши");
        }
        plan.add("§8Боевую автоматику и ESP пресеты не включают — только визуал и информация");
        return plan;
    }

    /** Применяет пресет и рассказывает, что поменялось. */
    public static String apply(String id) {
        Preset preset = ClientPresets.preset(id);
        if (preset == null) {
            return "Такого пресета нет";
        }
        ModuleManager manager = ModuleManager.get();
        int enabled = 0;
        int disabled = 0;
        String[] on = ENABLE.get(preset.id());
        if (on != null && manager != null) {
            for (String name : on) {
                Module module = manager.findByName(name);
                if (module != null && !module.isEnabled()) {
                    module.enable();
                    ++enabled;
                }
            }
        }
        String[] off = DISABLE.get(preset.id());
        if (off != null && manager != null) {
            for (String name : off) {
                Module module = manager.findByName(name);
                if (module != null && module.isEnabled()) {
                    module.disable();
                    ++disabled;
                }
            }
        }
        String graphics = GRAPHICS.get(preset.id());
        if (graphics != null) {
            GraphicsPresets.apply(graphics, null);
        }
        int lite = preset.id().equals("minimal") ? BuildTier.applyLite() : 0;
        String text = "Пресет «" + preset.title() + "»: включено " + enabled + ", выключено " + disabled
                + (graphics == null ? "" : ", графика «" + graphics + "»")
                + (lite > 0 ? ", облегчено модулей " + lite : "");
        ChatMessage.send("§b" + text);
        NotificationsModule.notify("§bПресет «" + preset.title() + "» применён", 4000L);
        WebBridge.pushEvent("preset", preset.title());
        return text;
    }

    /** Короткий код пресета: отправить другу, чтобы у него был такой же набор. */
    public static String shareCode(String id) {
        Preset preset = ClientPresets.preset(id);
        if (preset == null) {
            return "";
        }
        JsonObject root = new JsonObject();
        root.addProperty("preset", preset.id());
        root.addProperty("title", preset.title());
        root.addProperty("note", preset.note());
        String raw = root.toString();
        return "BZPRESET1:" + Base64.getUrlEncoder().withoutPadding()
                .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    /** Принимает код пресета от друга и применяет его. */
    public static String applyCode(String code) {
        String text = code == null ? "" : code.trim();
        int index = text.indexOf("BZPRESET1:");
        if (index < 0) {
            return "Это не код пресета (нужен BZPRESET1:…)";
        }
        try {
            String raw = new String(Base64.getUrlDecoder().decode(text.substring(index + 10).trim()), StandardCharsets.UTF_8);
            String id = JsonParser.parseString(raw).getAsJsonObject().get("preset").getAsString();
            if (ClientPresets.preset(id) == null) {
                return "В коде неизвестный пресет — обновите клиент";
            }
            return "Пресет из кода: " + ClientPresets.apply(id);
        }
        catch (Throwable throwable) {
            return "Код не читается — проверьте, что он скопирован целиком";
        }
    }

    /** Строки для окна и чата. */
    public static List<String> rows() {
        ArrayList<String> rows = new ArrayList<String>();
        for (Preset preset : PRESETS) {
            rows.add("§b" + preset.title() + " §8— " + preset.note()
                    + " §7(графика: " + GRAPHICS.getOrDefault(preset.id(), "как есть") + ")");
        }
        rows.add("§8Пресеты трогают только визуал и информацию: автоматизации боя нет");
        return rows;
    }

    public static String summary() {
        StringBuilder builder = new StringBuilder("Пресеты: ");
        for (int i = 0; i < PRESETS.size(); ++i) {
            if (i > 0) {
                builder.append(", ");
            }
            builder.append(PRESETS.get(i).title());
        }
        return builder.toString();
    }
}

