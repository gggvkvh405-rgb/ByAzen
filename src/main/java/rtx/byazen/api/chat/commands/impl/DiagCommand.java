package rtx.byazen.api.chat.commands.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import rtx.byazen.api.chat.commands.Command;
import rtx.byazen.api.chat.commands.helpers.TabCompleteHelper;
import rtx.byazen.api.ui.DiagnosticsScreen;
import rtx.byazen.utils.compat.ModEnv;
import rtx.byazen.utils.config.ChangeLog;
import rtx.byazen.utils.perf.ModuleProfiler;
import rtx.byazen.utils.render.cache.CacheManager;
import rtx.byazen.utils.security.PrivateData;
import rtx.byazen.utils.startup.LazyTasks;

/**
 * Команда `diag` (идеи №159, №161, №163, №164, №165, №166 из IDEAS.md).
 * <p>
 * Быстрый доступ ко всему, что собрано в окне «Диагностика»: `diag` открывает окно, `diag report`
 * выводит сводку прямо в чат, а `diag журнал`, `diag профиль`, `diag кэш`, `diag запуск`, `diag моды`,
 * `diag хранилище`, `diag статистика` — нужную вкладку.
 */
public final class DiagCommand
extends Command {

    private static final String[] TABS = {"журнал", "профиль", "кэш", "запуск", "моды", "хранилище", "статистика"};

    public DiagCommand() {
        super("diag", "Diagnostics window", "diagnostics", "диаг");
    }

    @Override
    public void execute(String prefix, String[] args) {
        String mode = args.length > 0 ? args[0].toLowerCase(Locale.ROOT) : "";
        if (mode.equals("report") || mode.equals("отчёт") || mode.equals("отчет")) {
            this.report();
            return;
        }
        int tab = 0;
        for (int i = 0; i < TABS.length; ++i) {
            if (TABS[i].equals(mode)) {
                tab = i;
                break;
            }
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new DiagnosticsScreen(tab));
        }
    }

    private void report() {
        this.logDirect("ByAzen diagnostics", Formatting.AQUA);
        this.logDirect("- журнал настроек: записей " + ChangeLog.count(), Formatting.GRAY);
        this.logDirect(String.format(Locale.ROOT, "- нагрузка модулей: %.1f мс в окне %d с (модулей в замере %d)",
                ModuleProfiler.windowMillis(), ModuleProfiler.windowAgeMs() / 1000L, ModuleProfiler.measured()), Formatting.GRAY);
        this.logDirect("- кэши: " + CacheManager.summary(), Formatting.GRAY);
        this.logDirect("- запуск: " + LazyTasks.report(), Formatting.GRAY);
        this.logDirect("- окружение: " + ModEnv.summary(), Formatting.GRAY);
        this.logDirect("- данные: " + (PrivateData.vaultExists() ? PrivateData.verify() : "открыты, файлов " + PrivateData.stores().size()), Formatting.GRAY);
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList("Opens the diagnostics window.",
                "Usage:",
                "> diag",
                "> diag report",
                "> diag journal|cache|profile|start|mods|vault|stats");
    }

    @Override
    public Stream<String> tabComplete(String prefix, String[] args) {
        if (args.length == 1) {
            return new TabCompleteHelper().append(TABS).append(new String[]{"report"}).sortAlphabetically().filterPrefix(args[0]).stream();
        }
        return Stream.empty();
    }
}
