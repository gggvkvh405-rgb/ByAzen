package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.ByAzenHubScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.season.SeasonEvents;

/**
 * Сезонные события (идея №184 из IDEAS.md): палитра, баннер и сезонная награда косметикой.
 */
public final class SeasonEventsModule
extends Module {

    private final BooleanSetting welcome = this.register(new BooleanSetting("Приветствие при входе", "Один раз за сезон сообщать о начале и о награде."));
    private final BooleanSetting palette = this.register(new BooleanSetting("Сезонная палитра", "Автоматически включать цвета сезона в интерфейсе."));
    private final SeparatorSetting group = this.register(new SeparatorSetting("События"));
    private final ButtonSetting hub = this.register(new ButtonSetting("Открыть хаб", "События, награда и сроки.").label("Хаб").onClick(() -> ByAzenHubScreen.open(2)));
    private final ButtonSetting claim = this.register(new ButtonSetting("Забрать награду", "Взять весь сезонный набор косметики (один раз за сезон).").label("Забрать").onClick(SeasonEvents::claim));
    private final ButtonSetting apply = this.register(new ButtonSetting("Включить палитру", "Сразу применить цвета текущего сезона.").label("Палитра").onClick(SeasonEventsModule::applyPalette));
    private final ButtonSetting chat = this.register(new ButtonSetting("Список в чат", "Что идёт сейчас и что будет дальше.").label("В чат").onClick(SeasonEventsModule::printRows));

    public SeasonEventsModule() {
        super("Season Events", "Сезоны клиента: своя палитра, поздравление и набор косметики в награду.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        if (this.welcome.getValue()) {
            SeasonEvents.welcome();
        }
        if (this.palette.getValue()) {
            ChatMessage.send("§b" + SeasonEvents.apply(null));
        }
        ChatMessage.send("§7" + SeasonEvents.summary());
    }

    private static void applyPalette() {
        ChatMessage.send("§b" + SeasonEvents.apply(null));
    }

    private static void printRows() {
        ChatMessage.send("§bСезонные события ByAzen: §f" + SeasonEvents.summary());
        for (String line : SeasonEvents.rows()) {
            ChatMessage.send("§7" + line);
        }
    }
}
