package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.DiagnosticsScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.security.PrivateData;

/**
 * Локальные данные под ключом (идея №162 из IDEAS.md).
 * <p>
 * Заметки, музыка, история смертей, профили серверов и журнал настроек лежат рядом с конфигом
 * обычными файлами. Кнопка «Закрыть под ключом» складывает их в один зашифрованный контейнер
 * (AES-256-GCM), а исходники убирает — так содержимое не прочитать в блокноте. «Открыть хранилище»
 * возвращает всё на место; перед закрытием контейнер проверяется расшифровкой.
 */
public final class PrivateDataModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Личные данные"));
    private final BooleanSetting keepCopy = this.register(new BooleanSetting("Оставлять копию", "Перед закрытием сохранить открытую копию в private/copy-…").setValue(false));
    private final BooleanSetting tellInChat = this.register(new BooleanSetting("Писать в чат", "Сообщать, что получилось.").setValue(true));
    private final ButtonSetting lock = this.register(new ButtonSetting("Закрыть под ключом", "Сложить локальные данные в зашифрованное хранилище.").label("Закрыть").onClick(PrivateDataModule::lock));
    private final ButtonSetting unlock = this.register(new ButtonSetting("Открыть хранилище", "Вернуть данные из хранилища на место.").label("Открыть").onClick(PrivateDataModule::unlock));
    private final ButtonSetting verify = this.register(new ButtonSetting("Проверить хранилище", "Убедиться, что контейнер читается и перечислить, что внутри.").label("Проверить").onClick(PrivateDataModule::verify));
    private final ButtonSetting open = this.register(new ButtonSetting("Открыть окно", "Список данных и кнопки — в окне «Диагностика».").label("Окно").onClick(() -> DiagnosticsScreen.open(DiagnosticsScreen.TAB_VAULT)));

    public PrivateDataModule() {
        super("Private Data", "Шифрует локальные данные клиента и возвращает их обратно по кнопке.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    public static void lock() {
        PrivateDataModule module = rtx.byazen.api.modules.ModuleManager.get().get(PrivateDataModule.class);
        PrivateData.Report report = PrivateData.lock(module == null || module.keepCopy.getValue());
        PrivateDataModule.tell(report.summary());
    }

    public static void unlock() {
        PrivateData.Report report = PrivateData.restore();
        PrivateDataModule.tell(report.summary());
    }

    public static void verify() {
        PrivateDataModule.tell(PrivateData.verify());
    }

    private static void tell(String text) {
        PrivateDataModule module = rtx.byazen.api.modules.ModuleManager.get().get(PrivateDataModule.class);
        if (module == null || module.tellInChat.getValue()) {
            ChatMessage.send(text);
        }
    }
}
