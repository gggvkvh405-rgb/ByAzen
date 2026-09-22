package rtx.byazen.api.modules.impl.Utils;

import com.google.gson.JsonObject;
import rtx.byazen.api.config.ConfigManager;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.config.ConfigMigrations;

/**
 * Миграция конфигов между версиями (идея №160 из IDEAS.md).
 * <p>
 * Старый файл настроек перед применением приводится к текущему формату: модули с новыми названиями
 * подхватываются, отсутствующие поля дописываются, а непонятные записи не выбрасываются молча — о них
 * сообщает доктор настроек. Здесь же видно, какой формат в файле сейчас.
 */
public final class ConfigMigrationModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Совместимость"));
    private final ButtonSetting check = this.register(new ButtonSetting("Проверить формат", "Посмотреть, что клиент сделает со старым файлом настроек.").label("Проверить").onClick(ConfigMigrationModule::check));
    private final ButtonSetting apply = this.register(new ButtonSetting("Привести к текущему", "Сразу переписать настройки в текущем формате.").label("Привести").onClick(ConfigMigrationModule::apply));

    public ConfigMigrationModule() {
        super("Config Migration", "Переносит старые конфиги в текущий формат без потери настроек.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    private static void check() {
        ConfigMigrations.Report report = ConfigMigrations.inspect(ConfigManager.buildModuleSnapshot());
        ChatMessage.send("Формат настроек: " + report.summary());
        int shown = 0;
        for (String note : report.notes) {
            if (shown++ >= 5) {
                break;
            }
            ChatMessage.send("• " + note);
        }
    }

    private static void apply() {
        JsonObject snapshot = ConfigManager.buildModuleSnapshot();
        ConfigMigrations.Report report = ConfigMigrations.migrate(snapshot);
        int applied = ConfigManager.applyModuleSnapshot(snapshot);
        ChatMessage.send(applied < 0
                ? "Привести формат не удалось"
                : "Настройки приведены к формату " + ConfigMigrations.CURRENT_FORMAT + ": " + report.summary());
    }
}
