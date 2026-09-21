package rtx.byazen.api.modules.impl.Interface;

import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.render.others.RectUtil;

/**
 * Полировка HUD: плавное появление элементов при входе в мир и магнитная сетка
 * при перетаскивании виджетов в редакторе интерфейса.
 * <p>
 * Идеи №28, №30 и №32 из IDEAS.md.
 */
public final class HudPolishModule
extends Module {

    public final SeparatorSetting appearance = this.register(new SeparatorSetting("Появление"));
    public final BooleanSetting fadeIn = this.register(new BooleanSetting("Плавное появление", "Виджеты плавно проявляются при входе в мир.", true));
    public final SliderSetting fadeDuration = this.register(new SliderSetting("Длительность, мс", "Сколько миллисекунд длится появление HUD.").range(120, 2000).increment(40).setValue(600.0f));

    public final SeparatorSetting grid = this.register(new SeparatorSetting("Перетаскивание"));
    public final BooleanSetting gridSnap = this.register(new BooleanSetting("Магнитная сетка", "Виджеты прилипают к сетке при перетаскивании.", false));
    public final SliderSetting gridStep = this.register(new SliderSetting("Шаг сетки, px", "Размер шага магнитной сетки.").range(2, 16).increment(1).setValue(6.0f));

    private long joinedAt;
    private boolean inWorld;

    public HudPolishModule() {
        super("HUD Polish", "Плавное появление HUD при входе в мир и магнитная сетка при перетаскивании виджетов.", Category.DISPLAY);
    }

    /** Шаг магнитной сетки для редактора интерфейса (0 — выравнивание выключено). */
    public static float snapStep() {
        HudPolishModule module = ModuleManager.get() == null ? null : ModuleManager.get().get(HudPolishModule.class);
        if (module == null || !module.isEnabled() || !module.gridSnap.getValue()) {
            return 0.0f;
        }
        return Math.max(2.0f, module.gridStep.getFloat());
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        if (this.mc.player == null || this.mc.world == null) {
            this.inWorld = false;
            RectUtil.setGlobalAlpha(1.0f);
            return;
        }
        if (!this.inWorld) {
            this.inWorld = true;
            this.joinedAt = System.nanoTime();
        }
        if (!this.isEnabled() || !this.fadeIn.getValue()) {
            RectUtil.setGlobalAlpha(1.0f);
            return;
        }
        float durationMs = Math.max(60.0f, this.fadeDuration.getFloat());
        float passedMs = (float) (System.nanoTime() - this.joinedAt) / 1.0E6f;
        float progress = Math.max(0.0f, Math.min(1.0f, passedMs / durationMs));
        // мягкая кривая появления: быстрый старт, плавное завершение
        RectUtil.setGlobalAlpha(1.0f - (1.0f - progress) * (1.0f - progress));
    }

    @Override
    protected void onDisable() {
        RectUtil.setGlobalAlpha(1.0f);
    }
}
