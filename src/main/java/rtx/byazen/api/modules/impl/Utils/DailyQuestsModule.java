package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.ByAzenHubScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.quests.DailyQuests;

/**
 * Ежедневные задания (идея №202 из IDEAS.md): три маленьких цели на день и серия дней.
 */
public final class DailyQuestsModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Задания дня"));
    private final ButtonSetting hub = this.register(new ButtonSetting("Открыть хаб",
            "Вкладка «Задания»: прогресс, серия дней и награды.").label("Хаб").onClick(() -> ByAzenHubScreen.open(7)));
    private final ButtonSetting chat = this.register(new ButtonSetting("Список в чат",
            "Показать сегодняшние задания и прогресс.").label("В чат").onClick(DailyQuestsModule::printRows));
    private final ButtonSetting reset = this.register(new ButtonSetting("Сбросить прогресс",
            "Начать задания дня заново (серия дней при этом сохраняется).").label("Сбросить").onClick(DailyQuestsModule::resetAll));

    public DailyQuestsModule() {
        super("Daily Quests", "Три задания на день по вашей же игре: время, путь, добыча, бои, музыка, чат. За серию — косметика.",
                Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        DailyQuests.ensure();
        ChatMessage.send("§bЗадания дня: §f" + DailyQuests.summary());
    }

    private static void printRows() {
        DailyQuests.ensure();
        ChatMessage.send("§bЗадания ByAzen: §f" + DailyQuests.summary());
        for (String line : DailyQuests.rows()) {
            ChatMessage.send(line.startsWith("§") ? line : "§7" + line);
        }
    }

    private static void resetAll() {
        DailyQuests.reset();
        ChatMessage.send("§bПрогресс заданий дня сброшен");
    }
}
