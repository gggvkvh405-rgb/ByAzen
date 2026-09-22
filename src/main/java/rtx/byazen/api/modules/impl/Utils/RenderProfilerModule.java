package rtx.byazen.api.modules.impl.Utils;

import java.util.List;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.ui.DiagnosticsScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.perf.ModuleProfiler;

/**
 * Профайлер рендера с авто-выключением прожорливых (идея №163 из IDEAS.md).
 * <p>
 * Клиент замеряет, сколько времени каждый модуль тратит в своих обработчиках событий, и раз в пару
 * секунд сводит это в отчёт: время на вызов и доля от общей работы модулей. Если модуль стабильно
 * дороже порога, его можно выключить автоматически — с записью в чат и в лог, чтобы было понятно,
 * куда делись кадры.
 */
public final class RenderProfilerModule
extends Module {

    private static final String[] PROTECTED = {"ClickGui", "ClickGui Backdrops", "Render Profiler", "Profiler", "Config Journal", "Theme Studio", "Diagnostics"};

    private final SliderSetting threshold = this.register(new SliderSetting("Порог на вызов, мс", "Сколько модуль может тратить в одном обработчике.").range(0.2f, 5.0f).increment(0.1f).setValue(0.8f));
    private final SliderSetting minCalls = this.register(new SliderSetting("Минимум вызовов", "Ниже этого числа вызовов модуль не считаем проблемным.").range(20.0f, 400.0f).increment(10.0f).setValue(120.0f));
    private final BooleanSetting autoDisable = this.register(new BooleanSetting("Выключать прожорливые", "Автоматически выключать модули, которые стабильно съедают кадр.").setValue(true));
    private final BooleanSetting tellInChat = this.register(new BooleanSetting("Писать в чат", "Сообщать о найденных и выключенных модулях.").setValue(true));
    private final ButtonSetting open = this.register(new ButtonSetting("Открыть отчёт", "Таблица модулей по времени — в окне «Диагностика».").label("Открыть").onClick(() -> DiagnosticsScreen.open(DiagnosticsScreen.TAB_PROFILE)));
    private final ButtonSetting report = this.register(new ButtonSetting("Список в чат", "Вывести топ модулей по нагрузке в чат.").label("В чат").onClick(RenderProfilerModule::report));

    private long nextRoll;

    public RenderProfilerModule() {
        super("Render Profiler", "Замеряет нагрузку модулей и выключает те, что съедают кадры.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        ModuleProfiler.reset();
        ModuleProfiler.setEnabled(true);
    }

    @Override
    protected void onDisable() {
        ModuleProfiler.setEnabled(false);
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (!event.isPost() || !ModuleProfiler.enabled()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (this.nextRoll == 0L) {
            this.nextRoll = now + 3000L;
            return;
        }
        if (now < this.nextRoll) {
            return;
        }
        this.nextRoll = now + 3000L;
        if (this.autoDisable.getValue()) {
            this.considerAutoDisable();
        }
        ModuleProfiler.rollWindow();
    }

    private void considerAutoDisable() {
        List<ModuleProfiler.Row> heavy = ModuleProfiler.heavy((double)this.threshold.getFloat() * 1000.0, (long)this.minCalls.getFloat());
        for (ModuleProfiler.Row row : heavy) {
            Module module = ModuleManager.get().findByName(row.name);
            if (module == null || !module.isEnabled() || RenderProfilerModule.protectedModule(module)) {
                continue;
            }
            module.disable();
            ClientLog.warn("модуль " + module.getDisplayName() + " выключен профайлером: " + row.microsText() + " на вызов, " + row.shareText());
            if (this.tellInChat.getValue()) {
                ChatMessage.send("Профайлер выключил «" + module.getDisplayName() + "»: " + row.microsText() + " на вызов");
            }
        }
    }

    private static boolean protectedModule(Module module) {
        if (module.getCategory() == Category.DISPLAY) {
            return true;
        }
        for (String name : PROTECTED) {
            if (module.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public static void report() {
        List<ModuleProfiler.Row> rows = ModuleProfiler.snapshot();
        if (rows.isEmpty()) {
            return;
        }
        ChatMessage.send("Нагрузка модулей (окно " + ModuleProfiler.windowAgeMs() / 1000L + " с, всего " +
                String.format(java.util.Locale.ROOT, "%.1f мс", ModuleProfiler.windowMillis()) + "):");
        int shown = 0;
        for (ModuleProfiler.Row row : rows) {
            if (shown++ >= 6) {
                break;
            }
            ChatMessage.send("• " + row.name + " — " + row.microsText() + " на вызов, " + row.shareText() + " работы");
        }
    }
}
