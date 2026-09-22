package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.DiagnosticsScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.startup.LazyTasks;

/**
 * Быстрый старт: ленивая загрузка тяжёлых модулей (идея №165 из IDEAS.md).
 * <p>
 * Косые плащи, просмотр шалкеров, анимации чата и головы в чате не нужны в первые секунды запуска —
 * они работают внутри мира. Их подготовка откладывается до входа в мир (или до истечения таймера),
 * поэтому клиент стартует быстрее. Здесь видно, что отложено, и можно выполнить это сразу.
 */
public final class FastStartModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Запуск игры"));
    private final BooleanSetting defer = this.register(new BooleanSetting("Откладывать интеграции", "Готовить интеграции не при запуске, а при входе в мир.").setValue(true));
    private final BooleanSetting timer = this.register(new BooleanSetting("Дожать по таймеру", "Если в мир так и не зашли — выполнить отложенное через 10 секунд.").setValue(true));
    private final ButtonSetting now = this.register(new ButtonSetting("Выполнить сейчас", "Сразу подготовить всё отложенное.").label("Выполнить").onClick(FastStartModule::runNow));
    private final ButtonSetting open = this.register(new ButtonSetting("Открыть список", "Что отложено и сколько занимает — в окне «Диагностика».").label("Открыть").onClick(() -> DiagnosticsScreen.open(DiagnosticsScreen.TAB_START)));

    public FastStartModule() {
        super("Fast Start", "Ускоряет запуск: тяжёлые интеграции готовятся при входе в мир, а не сразу.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        LazyTasks.setDeferring(this.defer.getValue());
        LazyTasks.setAutoRun(this.timer.getValue());
    }

    @Override
    protected void onDisable() {
        LazyTasks.setDeferring(false);
    }

    private static void runNow() {
        long[] result = LazyTasks.runAll();
        ChatMessage.send("Отложенное выполнено: задач " + result[0] + ", время " + result[1] + " мс");
    }
}
