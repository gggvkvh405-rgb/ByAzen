package rtx.byazen.api.modules.impl.Visuals;

import net.minecraft.client.gl.Framebuffer;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.render.post.fxaa.FxaaRenderer;

/**
 * Сглаживание картинки (идея №55 из IDEAS.md).
 * <p>
 * Настоящий пост-процесс по кадру мира: края находятся по перепаду яркости и размываются строго
 * вдоль найденного направления, поэтому лесенка на границах блоков и моделей исчезает, а ровные
 * поверхности остаются резкими. Четыре режима качества, субпиксельная мягкость и отдельная
 * резкость для любителей хрустящей картинки.
 */
public final class AntiAliasing
extends Module {

    private static final String MODE_LOW = "Низкое";
    private static final String MODE_MEDIUM = "Среднее";
    private static final String MODE_HIGH = "Высокое";
    private static final String MODE_MORPH = "Морфологическое";

    private static AntiAliasing instance;

    private final SeparatorSetting mainSeparator = this.register(new SeparatorSetting("Сглаживание"));
    public final ModeSetting quality = this.register(new ModeSetting("Качество", "Сколько выборок делает сглаживание. Выше — мягче края и чуть дороже кадр.", MODE_MEDIUM, MODE_LOW, MODE_MEDIUM, MODE_HIGH, MODE_MORPH));
    public final SliderSetting threshold = this.register(new SliderSetting("Порог края, %", "Насколько сильным должен быть перепад яркости, чтобы это считалось краем.").range(3.0f, 40.0f).increment(1.0f).setValue(25.0f));
    public final SliderSetting strength = this.register(new SliderSetting("Сила, %", "Насколько сильно подмешивать сглаженный пиксель.").range(20.0f, 100.0f).increment(5.0f).setValue(80.0f));
    public final SliderSetting subpixel = this.register(new SliderSetting("Субпиксельная мягкость, %", "Убирает остаточное дрожание тонких линий и далёких блоков.").range(0.0f, 100.0f).increment(5.0f).setValue(45.0f));

    private final SeparatorSetting detailSeparator = this.register(new SeparatorSetting("Детализация"));
    public final SliderSetting sharpen = this.register(new SliderSetting("Резкость, %", "Лёгкое подчёркивание деталей после сглаживания. 0 — выключено.").range(0.0f, 60.0f).increment(5.0f).setValue(0.0f));
    public final BooleanSetting skipGui = this.register(new BooleanSetting("Не трогать интерфейс", "Сглаживание применяется только к миру, меню остаётся как есть.", true));

    public AntiAliasing() {
        super("Anti Aliasing", "Сглаживает края картинки пост-процессом: четыре режима качества и субпиксельный режим.", Category.VISUALS);
        instance = this;
    }

    public static AntiAliasing getInstance() {
        if (instance == null) {
            instance = ModuleManager.get().get(AntiAliasing.class);
        }
        return instance;
    }

    @Override
    protected void onDisable() {
        FxaaRenderer.clear();
    }

    private int modeIndex() {
        if (this.quality.is(MODE_LOW)) {
            return 0;
        }
        if (this.quality.is(MODE_HIGH)) {
            return 2;
        }
        if (this.quality.is(MODE_MORPH)) {
            return 3;
        }
        return 1;
    }

    /** Проход сглаживания по готовому кадру мира. */
    public void onAfterWorld(Framebuffer framebuffer) {
        if (this.mc.player == null || this.mc.world == null) {
            return;
        }
        if (FxaaRenderer.isDisabledAfterError()) {
            return;
        }
        FxaaRenderer.apply(framebuffer, this.modeIndex(), this.threshold.getValue() / 100.0f,
                this.strength.getValue() / 100.0f, this.subpixel.getValue() / 100.0f, this.sharpen.getValue() / 100.0f);
    }
}
