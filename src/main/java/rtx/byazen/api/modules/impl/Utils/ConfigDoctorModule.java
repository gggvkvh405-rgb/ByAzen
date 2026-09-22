package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.config.ConfigDoctor;

/**
 * Читаемые настройки и валидатор (идея №157 из IDEAS.md).
 * <p>
 * Файл настроек можно открыть и прочитать глазами, а проверка объяснит простыми словами, если что-то
 * не так: модуля больше нет, настройка называется иначе или значение не подходит по типу. Исправить
 * можно одной кнопкой — перед этим клиент делает снимок версии, так что откат всегда под рукой.
 */
public final class ConfigDoctorModule
extends Module {

    private final BooleanSetting repair = this.register(new BooleanSetting("Исправлять сразу", "Убирать устаревшие записи и сбрасывать неподходящие значения.").setValue(true));
    private final ButtonSetting check = this.register(new ButtonSetting("Проверить настройки", "Проверить конфиг и написать отчёт в чат.").label("Проверить").onClick(ConfigDoctorModule::check));
    private final ButtonSetting readable = this.register(new ButtonSetting("Записать читаемый JSON", "Сохранить текущие настройки в файл с отступами — удобно смотреть глазами.").label("Записать").onClick(ConfigDoctorModule::readable));

    public ConfigDoctorModule() {
        super("Config Doctor", "Проверка настроек: устаревшие записи, неизвестные настройки, неподходящие значения.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    private static void check() {
        ConfigDoctorModule module = rtx.byazen.api.modules.ModuleManager.get().get(ConfigDoctorModule.class);
        boolean repair = module == null || module.repair.getValue();
        ConfigDoctor.Report report = ConfigDoctor.check(repair);
        if (report.issues.isEmpty()) {
            ChatMessage.send("Настройки в порядке: " + report.summary());
            return;
        }
        ChatMessage.send("Проверка настроек: " + report.summary());
        int shown = 0;
        for (ConfigDoctor.Issue issue : report.issues) {
            if (shown++ >= 5) {
                ChatMessage.send("…и ещё проблем: " + (report.issues.size() - 5));
                break;
            }
            ChatMessage.send("• " + issue.where + " — " + issue.what + " (" + issue.fix + ")");
        }
    }

    private static void readable() {
        ConfigDoctor.Report report = ConfigDoctor.check(false);
        String path = ConfigDoctor.writeReadable(rtx.byazen.api.config.ConfigManager.buildModuleSnapshot());
        if (path.isEmpty()) {
            ChatMessage.error("Не удалось записать читаемый файл");
            return;
        }
        ChatMessage.send("Читаемый JSON записан: " + path + " (проблем найдено: " + report.issues.size() + ")");
    }
}
