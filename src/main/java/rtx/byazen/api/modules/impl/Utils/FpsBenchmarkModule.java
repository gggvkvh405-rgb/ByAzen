package rtx.byazen.api.modules.impl.Utils;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.render.HudRenderEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.perf.FpsBenchmark;

/**
 * Бенчмарк-режим (идея №174 из IDEAS.md): замер сцены до и после настроек.
 * <p>
 * Нажали «Начать замер» — клиент собирает кадры выбранное время, считает средний FPS, «1 % худших»
 * и минимум. Второй замер сравнивается с первым, поэтому видно, помогла ли настройка.
 */
public final class FpsBenchmarkModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Бенчмарк FPS"));
    private final SliderSetting seconds = this.register(new SliderSetting("Время замера", "Сколько секунд собирать кадры.").range(3.0f, 60.0f).increment(1.0f).setValue(10.0f));
    private final ButtonSetting start = this.register(new ButtonSetting("Начать замер", "Собирает FPS и считает среднее и 1 % худших кадров.").label("Начать").onClick(FpsBenchmarkModule::start));
    private final ButtonSetting stop = this.register(new ButtonSetting("Остановить", "Завершить замер досрочно и подвести итог.").label("Итог").onClick(FpsBenchmark::stop));
    private final ButtonSetting compare = this.register(new ButtonSetting("Сравнить с прошлым", "Показать разницу с предыдущим замером.").label("Сравнить").onClick(FpsBenchmarkModule::compare));
    private final ButtonSetting clear = this.register(new ButtonSetting("Сбросить замеры", "Убрать историю, чтобы сравнивать с чистого листа.").label("Сбросить").onClick(FpsBenchmark::clear));

    public FpsBenchmarkModule() {
        super("FPS Benchmark", "Замер кадров до и после настроек: средний FPS, 1 % худших, минимум.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    private static void start() {
        FpsBenchmarkModule module = rtx.byazen.api.modules.ModuleManager.get().get(FpsBenchmarkModule.class);
        FpsBenchmark.start(module == null ? 10 : (int)module.seconds.getFloat());
    }

    private static void compare() {
        ChatMessage.send("§b" + FpsBenchmark.compareText());
    }

    @EventHandler
    public void onHud(HudRenderEvent hudRenderEvent) {
        if (!this.isEnabled()) {
            return;
        }
        FpsBenchmark.sample(MinecraftClient.getInstance());
    }
}
