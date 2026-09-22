package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.WhyLagScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.perf.LagReport;

/**
 * Экран «Почему лагает» (идея №170 из IDEAS.md).
 * <p>
 * Собирает причины просадок и объясняет их обычными словами: сколько сущностей рядом, какая
 * дальность прорисовки, что с памятью, кто из модов перехватывает рендер и какой модуль клиента
 * самый дорогой. Всё это — в одном окне и одной кнопкой в чат.
 */
public final class WhyLagModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Почему лагает"));
    private final ButtonSetting open = this.register(new ButtonSetting("Открыть окно", "Причины просадок простыми словами.").label("Открыть").onClick(WhyLagScreen::open));
    private final ButtonSetting summary = this.register(new ButtonSetting("Главная причина в чат", "Одна строка: что сильнее всего мешает кадрам.").label("В чат").onClick(WhyLagModule::printSummary));
    private final ButtonSetting report = this.register(new ButtonSetting("Отчёт в чат", "Полный разбор: сущности, чанки, память, моды, модули.").label("Отчёт").onClick(WhyLagModule::printReport));

    public WhyLagModule() {
        super("Why Lag", "Почему лагает: чанки, сущности, шейдеры и модули — простыми словами.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    private static void printSummary() {
        ChatMessage.send("§bПочему лагает: " + LagReport.summary());
    }

    private static void printReport() {
        for (String line : LagReport.report()) {
            ChatMessage.send(line);
        }
    }
}
