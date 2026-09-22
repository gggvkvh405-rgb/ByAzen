package rtx.byazen.api.modules.impl.Utils;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.DiagnosticsScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.stats.StatsExport;

/**
 * Экспорт статистики (идея №161 из IDEAS.md).
 * <p>
 * Всё, что клиент знает о сессии — время, дистанция, добытое, убийства, смертельные точки, музыка,
 * журнал настроек — выгружается в двух видах: CSV для таблиц и markdown для заметок. Файлы кладутся
 * рядом с конфигом в папку `exports`, а короткую сводку можно посмотреть в окне «Диагностика».
 */
public final class StatsExportModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Выгрузка"));
    private final BooleanSetting tellInChat = this.register(new BooleanSetting("Писать в чат", "Сообщать, куда легли файлы.").setValue(true));
    private final ButtonSetting write = this.register(new ButtonSetting("Выгрузить файлы", "Собрать CSV и markdown со статистикой сессии.").label("Выгрузить").onClick(StatsExportModule::writeFiles));
    private final ButtonSetting copy = this.register(new ButtonSetting("Скопировать markdown", "Положить отчёт текстом в буфер обмена.").label("Копировать").onClick(StatsExportModule::copyReport));
    private final ButtonSetting open = this.register(new ButtonSetting("Открыть отчёт", "Показать сводку в окне «Диагностика».").label("Открыть").onClick(() -> DiagnosticsScreen.open(DiagnosticsScreen.TAB_STATS)));

    public StatsExportModule() {
        super("Stats Export", "Выгружает статистику сессии в CSV и markdown: игра, бои, музыка, настройки.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    public static void writeFiles() {
        StatsExport.Bundle bundle = StatsExport.build();
        String path = StatsExport.write(bundle);
        StatsExportModule module = rtx.byazen.api.modules.ModuleManager.get().get(StatsExportModule.class);
        boolean tell = module == null || module.tellInChat.getValue();
        if (path.isEmpty()) {
            ChatMessage.error("Не удалось записать файлы статистики");
            return;
        }
        if (tell) {
            ChatMessage.send("Статистика выгружена: stats-" + bundle.stamp + ".md и .csv в " + path + " (строк: " + bundle.lines() + ")");
        }
    }

    public static void copyReport() {
        StatsExport.Bundle bundle = StatsExport.build();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.keyboard.setClipboard(bundle.markdown);
        }
        ChatMessage.send("Отчёт скопирован: строк " + bundle.lines());
    }
}
