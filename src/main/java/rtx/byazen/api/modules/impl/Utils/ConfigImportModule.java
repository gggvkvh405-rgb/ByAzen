package rtx.byazen.api.modules.impl.Utils;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.ui.ConfigImportScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.config.ForeignConfigs;

/**
 * Импорт настроек других клиентов (идея №153 из IDEAS.md).
 * <p>
 * Переезд на ByAzen занимает минуту: клиент сам находит рядом с игрой папки знакомых клиентов,
 * показывает, сколько клавиш и ползунков там нашлось, и переносит их к нам. Клавиши расставляются
 * по модулям, которые отвечают за то же действие, а FOV и дальность прорисовки применяются к игре.
 */
public final class ConfigImportModule
extends Module {

    private final BooleanSetting autoScan = this.register(new BooleanSetting("Искать при запуске", "Проверять наличие конфигов других клиентов и подсказывать в чат.").setValue(true));
    private final BooleanSetting tellInChat = this.register(new BooleanSetting("Писать в чат", "Сообщать, какие клиенты найдены.").setValue(true));
    private final ButtonSetting scan = this.register(new ButtonSetting("Найти клиенты", "Показать список найденных конфигов и что из них можно взять.").label("Найти").onClick(ConfigImportModule::open));
    private final ButtonSetting importAll = this.register(new ButtonSetting("Перенести всё найденное", "Импортировать клавиши и настройки из всех найденных конфигов сразу.").label("Перенести").onClick(ConfigImportModule::importAll));

    private boolean scanned;

    public ConfigImportModule() {
        super("Config Import", "Импорт настроек других клиентов: клавиши и основные ползунки переезжают на ByAzen.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        if (!this.autoScan.getValue() || this.scanned) {
            return;
        }
        this.scanned = true;
        int found = ForeignConfigs.detect().size();
        if (found > 0 && this.tellInChat.getValue()) {
            ChatMessage.send("Найдены конфиги других клиентов: " + found + " — перенести можно кнопкой «Найти»");
        }
    }

    public static void open() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new ConfigImportScreen());
        }
    }

    public static void importAll() {
        int binds = 0;
        int sliders = 0;
        int clients = 0;
        for (ForeignConfigs.Found found : ForeignConfigs.detect()) {
            ForeignConfigs.Report report = ForeignConfigs.apply(found);
            if (report.binds == 0 && report.sliders == 0) {
                continue;
            }
            binds += report.binds;
            sliders += report.sliders;
            ++clients;
        }
        if (clients == 0) {
            ChatMessage.error("Ничего знакомого в конфигах других клиентов не нашлось");
            return;
        }
        ChatMessage.send("Перенесено из " + clients + " конфигов: клавиш " + binds + ", настроек " + sliders);
    }
}
