package rtx.byazen.api.modules.impl.Interface;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import rtx.byazen.api.drags.DragSystem;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;

/**
 * Профили HUD: «PvP», «Анархия», «Стрим» и «Полный» — готовые раскладки одним нажатием
 * (идея №29 из IDEAS.md). Профиль включает нужные модули и оставляет на экране только нужные виджеты.
 */
public final class HudProfilesModule
extends Module {

    /** Виджеты, которые нужны в каждом профиле (остальные прячутся). */
    private static final Set<String> PVP_WIDGETS = new HashSet<String>(Arrays.asList("watermark", "keystrokes", "target_hud", "armor", "music"));
    private static final Set<String> ANARCHY_WIDGETS = new HashSet<String>(Arrays.asList("watermark", "keystrokes", "armor", "session_stats", "clock", "music", "death_history_hud"));
    private static final Set<String> STREAM_WIDGETS = new HashSet<String>(Arrays.asList("watermark", "music", "clock", "session_stats"));
    private static final Set<String> FULL_WIDGETS = new HashSet<String>();

    public final SeparatorSetting profiles = this.register(new SeparatorSetting("Профили"));
    public final ButtonSetting pvp = this.register(new ButtonSetting("PvP", "Минимум лишнего: списки клавиш, броня, информация о цели.")
            .label("Применить").onClick(() -> this.apply("PvP", PVP_WIDGETS, new Category[]{Category.DISPLAY})));
    public final ButtonSetting anarchy = this.register(new ButtonSetting("Анархия", "Больше информации: статистика сессии, история смертей, часы.")
            .label("Применить").onClick(() -> this.apply("Анархия", ANARCHY_WIDGETS, new Category[]{Category.DISPLAY, Category.UTILS})));
    public final ButtonSetting stream = this.register(new ButtonSetting("Стрим", "Минимум на экране: только водяной знак, музыка и часы.")
            .label("Применить").onClick(() -> this.apply("Стрим", STREAM_WIDGETS, new Category[]{Category.DISPLAY})));
    public final ButtonSetting full = this.register(new ButtonSetting("Полный", "Вернуть все клиентские виджеты на экран.")
            .label("Вернуть всё").onClick(() -> this.apply("Полный", FULL_WIDGETS, null)));

    public HudProfilesModule() {
        super("HUD Profiles", "Профили HUD одним нажатием: «PvP», «Анархия», «Стрим» и «Полный».", Category.DISPLAY);
    }

    private void apply(String name, Set<String> widgets, Category[] categories) {
        DragSystem dragSystem = DragSystem.get();
        if (dragSystem != null) {
            List<Draggable> elements = dragSystem.getAll();
            boolean everything = widgets.isEmpty();
            for (Draggable draggable : elements) {
                if (!draggable.isInteractive() && !draggable.isVisible()) {
                    continue;
                }
                String id = draggable.getId() == null ? "" : draggable.getId().toLowerCase(Locale.ROOT);
                boolean visible = everything || widgets.contains(id) || HudProfilesModule.matchesAny(id, widgets);
                draggable.setVisible(visible);
            }
        }
        if (categories != null) {
            Set<Category> wanted = new HashSet<Category>(Arrays.asList(categories));
            ModuleManager manager = ModuleManager.get();
            if (manager != null) {
                for (Module module : manager.getAll()) {
                    if (!(module instanceof HudProfilesModule) && wanted.contains(module.getCategory())) {
                        module.setEnabled(true);
                    }
                }
            }
        }
        ChatMessage.brandmessage("Профиль HUD: " + name + " применён.");
    }

    private static boolean matchesAny(String id, Set<String> widgets) {
        for (String widget : widgets) {
            if (id.contains(widget) || widget.contains(id)) {
                return true;
            }
        }
        return false;
    }
}
