package rtx.byazen.api.modules.impl.Interface;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.TutorialScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.help.TutorialState;

/**
 * Интерактивный туториал (идея №188 из IDEAS.md): тур по клиенту с первого запуска.
 */
public final class TutorialModule
extends Module {

    public final BooleanSetting hintOnStart = this.register(new BooleanSetting("Подсказка при входе",
            "Если тур ещё не пройден — напомнить о нём в чате.", true));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Тур"));
    private final ButtonSetting open = this.register(new ButtonSetting("Открыть тур",
            "Семь коротких шагов: модули, поиск, хаб, диагностика, обновления.").label("Открыть")
            .onClick(TutorialScreen::open));
    private final ButtonSetting reset = this.register(new ButtonSetting("Пройти заново",
            "Сбросить прогресс тура и начать с первого шага.").label("Сбросить")
            .onClick(TutorialModule::resetProgress));

    public TutorialModule() {
        super("Tutorial", "Интерактивный тур по клиенту: что где лежит и как этим пользоваться.",
                Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        if (this.hintOnStart.getValue() && TutorialState.firstLaunch()) {
            ChatMessage.send("§bТур по ByAzen: §7напишите §f.tutorial§7 или нажмите кнопку в модуле Tutorial");
            NotificationsModule.notify("§bТур по клиенту: .tutorial", 4000L);
        }
    }

    private static void resetProgress() {
        TutorialState.reset();
        ChatMessage.send("§bТур сброшен — откроется с первого шага");
        TutorialScreen.open();
    }
}

