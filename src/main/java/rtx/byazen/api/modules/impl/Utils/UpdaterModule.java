package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.update.UpdateChecker;

/**
 * Автообновление клиента (идея №192 из IDEAS.md): проверка, скачивание и перезапуск.
 */
public final class UpdaterModule
extends Module {

    public final BooleanSetting autoCheck = this.register(new BooleanSetting("Проверять при запуске",
            "Раз в сутки смотреть манифест обновлений и сообщать в чат.", true));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Обновление"));
    private final ButtonSetting check = this.register(new ButtonSetting("Проверить обновление",
            "Проверить версию в манифесте проекта и написать результат в чат.").label("Проверить")
            .onClick(UpdaterModule::checkNow));
    private final ButtonSetting download = this.register(new ButtonSetting("Скачать обновление",
            "Скачать новый jar в папку mods (старые сборки убираются, для верности проверяется метка ByAzen).")
            .label("Скачать").onClick(UpdaterModule::downloadNow));
    private final ButtonSetting restart = this.register(new ButtonSetting("Обновить и перезапустить",
            "Скачать обновление и закрыть игру — лаунчер запустит свежую версию.").label("Обновить")
            .onClick(UpdaterModule::updateNow));
    private final ButtonSetting page = this.register(new ButtonSetting("Страница проекта",
            "Открыть в браузере страницу, где лежат версии.").label("Открыть").onClick(UpdateChecker::openPage));

    public UpdaterModule() {
        super("Updater", "Автообновление клиента: проверка версии, скачивание и перезапуск в один клик.",
                Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        if (this.autoCheck.getValue()) {
            UpdateChecker.check(false);
        }
    }

    @Override
    protected void onDisable() {
        UpdateChecker.check(false);
    }

    private static void checkNow() {
        UpdateChecker.check(true);
        ChatMessage.send("§7" + UpdateChecker.summary());
    }

    private static void downloadNow() {
        ChatMessage.send("§b" + UpdateChecker.download());
    }

    private static void updateNow() {
        ChatMessage.send("§b" + UpdateChecker.updateAndRestart());
    }
}

