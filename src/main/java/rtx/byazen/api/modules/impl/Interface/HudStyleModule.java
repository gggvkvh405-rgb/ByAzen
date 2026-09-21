package rtx.byazen.api.modules.impl.Interface;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;

/**
 * Расширенная кастомизация виджетов: общая прозрачность подложек, дополнительное скругление,
 * тонкая обводка и мягкая тень — по отдельности (идея №28 из IDEAS.md).
 */
public final class HudStyleModule
extends Module {

    public final SeparatorSetting shape = this.register(new SeparatorSetting("Форма"));
    public final SliderSetting backgroundAlpha = this.register(new SliderSetting("Прозрачность, %", "Насколько плотные подложки у виджетов HUD.").range(20, 160).increment(5).setValue(100.0f));
    public final SliderSetting cornerRadius = this.register(new SliderSetting("Доп. скругление, px", "Прибавка к радиусу скругления всех клиентских панелей.").range(0, 10).increment(1).setValue(0.0f));
    public final BooleanSetting outline = this.register(new BooleanSetting("Обводка", "Тонкая светлая обводка вокруг виджетов.", false));
    public final SliderSetting outlineWidth = this.register(new SliderSetting("Толщина обводки", "Толщина обводки в пикселях.").range(0.4f, 2.0f).increment(0.1f).setValue(0.7f));
    public final BooleanSetting shadow = this.register(new BooleanSetting("Мягкая тень", "Дополнительное затемнение под панелями, чтобы HUD читался на ярком фоне.", false));

    public HudStyleModule() {
        super("HUD Style", "Кастомизация подложек HUD: прозрачность, скругление, обводка и тень — по отдельности.", Category.DISPLAY);
    }

    /** Множитель прозрачности подложек (1.0 — как обычно). */
    public static float alphaMultiplier() {
        HudStyleModule module = HudStyleModule.instance();
        if (module == null) {
            return 1.0f;
        }
        return Math.max(0.1f, module.backgroundAlpha.getFloat() / 100.0f);
    }

    /** Прибавка к радиусу скругления. */
    public static float radiusAdd() {
        HudStyleModule module = HudStyleModule.instance();
        return module == null ? 0.0f : module.cornerRadius.getFloat();
    }

    public static boolean outlineEnabled() {
        HudStyleModule module = HudStyleModule.instance();
        return module != null && module.outline.getValue();
    }

    public static float outlineWidth() {
        HudStyleModule module = HudStyleModule.instance();
        return module == null ? 0.7f : module.outlineWidth.getFloat();
    }

    public static boolean shadowEnabled() {
        HudStyleModule module = HudStyleModule.instance();
        return module != null && module.shadow.getValue();
    }

    private static HudStyleModule instance() {
        ModuleManager manager = ModuleManager.get();
        if (manager == null) {
            return null;
        }
        HudStyleModule module = manager.get(HudStyleModule.class);
        return module != null && module.isEnabled() ? module : null;
    }
}
