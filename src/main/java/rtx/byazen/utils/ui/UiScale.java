package rtx.byazen.utils.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.UiScaleModule;
import rtx.byazen.api.ui.BaseScreen;

/**
 * Отдельный от ванильного масштаб интерфейса клиента (идея №34 из IDEAS.md).
 * <p>
 * Экраны ByAzen рисуются в матрице с этим коэффициентом, а координаты мыши и размеры
 * виртуального экрана пересчитываются в {@code Position}, поэтому вся существующая
 * вёрстка остаётся центрированной и клики попадают точно.
 */
public final class UiScale {

    private UiScale() {
    }

    /** Коэффициент масштаба экранов ByAzen (1.0 - как обычно). */
    private static UiScaleModule module() {
        return ModuleManager.get().get(UiScaleModule.class);
    }

    /** Коэффициент масштаба экранов ByAzen (1.0 - как обычно). */
    public static float value() {
        UiScaleModule module = UiScale.module();
        if (module == null) {
            return 1.0f;
        }
        return Math.max(0.5f, Math.min(2.0f, module.scale.getValue()));
    }

    /**
     * Масштаб, действующий прямо сейчас: только для экранов клиента и только вне режима
     * расстановки HUD (там мышь должна совпадать с настоящими координатами виджетов).
     */
    public static float screenScale() {
        float scale = UiScale.value();
        if (scale == 1.0f) {
            return 1.0f;
        }
        UiScaleModule module = UiScale.module();
        if (module == null || !module.applyToClickGui.getValue()) {
            return 1.0f;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.currentScreen == null) {
            return 1.0f;
        }
        if (!(client.currentScreen instanceof BaseScreen)) {
            return 1.0f;
        }
        if (module().resetOnDrag.getValue() && rtx.byazen.api.drags.DragSystem.get().isDragModeActive()) {
            return 1.0f;
        }
        return scale;
    }

    /** Настройки изменились: пересобрать кэш размеров. */
    public static void invalidate() {
    }

    public static boolean active() {
        return UiScale.screenScale() != 1.0f;
    }

    /** Не масштабировать этот экран (ванильные остаются как есть). */
    public static boolean skip(Screen screen) {
        return !(screen instanceof BaseScreen);
    }
}
