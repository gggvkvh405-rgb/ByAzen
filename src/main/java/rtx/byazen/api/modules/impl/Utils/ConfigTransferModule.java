package rtx.byazen.api.modules.impl.Utils;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BindSetting;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.ConfigTransferScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.config.ConfigTransfer;

/**
 * Обмен конфигурацией (идея №101 из IDEAS.md).
 * <p>
 * Вся конфигурация клиента собирается в один файл, а QR-код показывает профиль выбранного модуля —
 * удобно снять камерой телефона. Импорт применяет настройки сразу, без перезапуска игры; перед
 * заменой клиент делает резервную копию.
 */
public final class ConfigTransferModule
extends Module {

    public final SeparatorSetting windowGroup = this.register(new SeparatorSetting("Окно"));
    public final ButtonSetting open = this.register(new ButtonSetting("Открыть обмен конфигурацией", "Файл всей конфигурации, QR профиля модуля и импорт.")
            .label("Открыть").onClick(ConfigTransferModule::openScreen));
    public final BindSetting openKey = this.register(new BindSetting("Клавиша обмена", "Быстрое открытие окна обмена конфигурацией."));

    public final SeparatorSetting fileGroup = this.register(new SeparatorSetting("Файл"));
    public final ButtonSetting exportFile = this.register(new ButtonSetting("Сохранить файл конфигурации", "Собрать все настройки в один файл .bycfg в папке exports.")
            .label("Сохранить").onClick(this::exportFile));
    public final ButtonSetting importFile = this.register(new ButtonSetting("Импорт из последнего файла", "Применить конфигурацию из самого свежего файла экспорта.")
            .label("Импорт").onClick(this::importFile));
    public final ButtonSetting copyCode = this.register(new ButtonSetting("Скопировать код", "Скопировать код конфигурации в буфер обмена, чтобы передать текстом.")
            .label("Копировать").onClick(this::copyCode));

    public final SeparatorSetting safetyGroup = this.register(new SeparatorSetting("Осторожность"));
    public final BooleanSetting backupBeforeImport = this.register(new BooleanSetting("Копия перед импортом", "Перед заменой настроек сохранить резервную копию.", true));
    public final BooleanSetting chatNotice = this.register(new BooleanSetting("Писать в чат", "Сообщать о результатах обмена в чат.", true));

    private boolean keyDown;

    public ConfigTransferModule() {
        super("ConfigTransfer", "Обмен конфигурацией: один файл на все настройки, QR-код профиля модуля и импорт в один клик.", Category.UTILS);
    }

    /** Открывает окно обмена конфигурацией. */
    public static void openScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new ConfigTransferScreen(client.currentScreen));
        }
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost() || this.mc == null || this.mc.getWindow() == null) {
            return;
        }
        boolean down = this.openKey.isBound() && this.openKey.getValue().isDown(this.mc.getWindow().getHandle());
        if (down && !this.keyDown && this.isEnabled()) {
            ConfigTransferModule.openScreen();
        }
        this.keyDown = down;
    }

    private void exportFile() {
        ConfigTransfer.Result result = ConfigTransfer.exportToFile();
        this.notice(result.ok
                ? "Конфигурация сохранена: " + result.file.getFileName() + " (" + ConfigTransfer.sizeText(ConfigTransfer.codeBytes(result.code)) + ")"
                : result.message);
    }

    private void importFile() {
        String backup = this.backup();
        ConfigTransfer.Result result = ConfigTransfer.importLatest();
        this.notice(backup.isEmpty() ? result.message : result.message + " Резервная копия: " + backup + ".");
    }

    private void copyCode() {
        String code = ConfigTransfer.currentCode();
        MinecraftClient client = MinecraftClient.getInstance();
        if (code.isEmpty() || client == null) {
            this.notice("Не удалось собрать конфигурацию для копирования.");
            return;
        }
        client.keyboard.setClipboard(code);
        this.notice("Код конфигурации скопирован: " + ConfigTransfer.sizeText(ConfigTransfer.codeBytes(code)) + ".");
    }

    private void notice(String text) {
        if (this.chatNotice.getValue()) {
            ChatMessage.send(text);
        }
    }

    /** Копия настроек перед импортом: своя настройка важнее общего поведения модуля автобэкапов. */
    private String backup() {
        if (!this.backupBeforeImport.getValue()) {
            return "";
        }
        try {
            return ConfigBackupModule.backupNow("import");
        }
        catch (Throwable throwable) {
            return "";
        }
    }
}
