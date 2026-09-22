package rtx.byazen.api.modules.impl.Interface;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;

/**
 * Анимированные фоны ClickGui (идея №156 из IDEAS.md).
 * <p>
 * Меню перестаёт быть плоской панелью: по нему медленно плывёт градиент темы, поднимаются мягкие
 * частицы, а если играет музыка — фон подстраивается под цвет обложки трека. Яркость регулируется,
 * чтобы фон не мешал читать настройки.
 */
public final class ClickGuiBackdropsModule
extends Module {

    private static ClickGuiBackdropsModule instance;

    private final BooleanSetting gradient = this.register(new BooleanSetting("Градиент темы", "Плавный перелив цветов темы по панели.").setValue(true));
    private final BooleanSetting particles = this.register(new BooleanSetting("Частицы", "Мягкие точки, поднимающиеся вверх.").setValue(true));
    private final BooleanSetting coverTint = this.register(new BooleanSetting("Оттенок обложки трека", "Подкрашивать фон цветом обложки играющего трека.").setValue(true));
    private final SliderSetting strength = this.register(new SliderSetting("Яркость, %", "Насколько заметен фон.").range(20.0f, 100.0f).increment(5.0f).setValue(65.0f));

    public ClickGuiBackdropsModule() {
        super("ClickGui Backdrops", "Анимированные фоны клик-меню: градиент темы, частицы и оттенок обложки трека.", Category.DISPLAY);
        instance = this;
    }

    public static ClickGuiBackdropsModule getInstance() {
        return instance;
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    public static boolean gradientEnabled() {
        ClickGuiBackdropsModule module = ClickGuiBackdropsModule.instance;
        return module != null && module.isEnabled() && module.gradient.getValue();
    }

    public static boolean particlesEnabled() {
        ClickGuiBackdropsModule module = ClickGuiBackdropsModule.instance;
        return module != null && module.isEnabled() && module.particles.getValue();
    }

    public static boolean coverEnabled() {
        ClickGuiBackdropsModule module = ClickGuiBackdropsModule.instance;
        return module != null && module.isEnabled() && module.coverTint.getValue();
    }

    public static float strengthFactor() {
        ClickGuiBackdropsModule module = ClickGuiBackdropsModule.instance;
        return module == null ? 0.65f : module.strength.getFloat() / 100.0f;
    }
}
