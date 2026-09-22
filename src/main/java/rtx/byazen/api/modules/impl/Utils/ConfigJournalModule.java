package rtx.byazen.api.modules.impl.Utils;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.DiagnosticsScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.config.ChangeLog;

/**
 * Журнал изменений настроек (идея №159 из IDEAS.md).
 * <p>
 * Дневник «что я поменял и когда» клиент ведёт с прошлых версий: каждая настройка, которую тронули,
 * попадает в историю с датой и новым значением. Здесь журнал открывается в окне «Диагностика», оттуда
 * же копируется текстом и чистится — удобно искать, что именно испортило картинку.
 */
public final class ConfigJournalModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Журнал настроек"));
    private final SliderSetting depth = this.register(new SliderSetting("Глубина записей", "Сколько последних изменений показывать в окне.").range(20.0f, 200.0f).increment(10.0f).setValue(60.0f));
    private final ButtonSetting open = this.register(new ButtonSetting("Открыть журнал", "Список изменений с датами — в окне «Диагностика».").label("Открыть").onClick(() -> DiagnosticsScreen.open(DiagnosticsScreen.TAB_JOURNAL)));
    private final ButtonSetting copy = this.register(new ButtonSetting("Скопировать журнал", "Положить историю изменений текстом в буфер обмена.").label("Копировать").onClick(ConfigJournalModule::copy));
    private final ButtonSetting clear = this.register(new ButtonSetting("Очистить журнал", "Удалить все записи истории.").label("Очистить").onClick(ConfigJournalModule::clear));

    public ConfigJournalModule() {
        super("Config Journal", "История настроек: что, когда и на какое значение поменялось.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    /** Сколько записей показывать в окне — берётся из настроек модуля. */
    public static int depth() {
        ConfigJournalModule module = rtx.byazen.api.modules.ModuleManager.get().get(ConfigJournalModule.class);
        return module == null ? 60 : (int)module.depth.getFloat();
    }

    public static void copy() {
        String text = ChangeLog.markdown(400);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.keyboard.setClipboard(text);
        }
        ChatMessage.send("Журнал скопирован: записей " + ChangeLog.count());
    }

    public static void clear() {
        int before = ChangeLog.count();
        ChangeLog.clear();
        ChatMessage.send("Журнал настроек очищен: было " + before + " записей");
    }
}
