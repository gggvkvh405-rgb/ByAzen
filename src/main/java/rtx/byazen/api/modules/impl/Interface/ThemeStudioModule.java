package rtx.byazen.api.modules.impl.Interface;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.ThemeStudioScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.config.ThemeCodes;

/**
 * Тема кодом и редактор темы (идеи №154 и №155 из IDEAS.md).
 * <p>
 * «Вставь код — получи мою тему»: короткий код уносит цвета, второй цвет, радугу и градиент на
 * другой компьютер или в чат другу. Редактор темы показывает палитру, позволяет взять цвет из
 * обложки играющего трека и подобрать оба оттенка прямо с экрана.
 */
public final class ThemeStudioModule
extends Module {

    private final SeparatorSetting shareGroup = this.register(new SeparatorSetting("Тема кодом"));
    private final BooleanSetting confirmInChat = this.register(new BooleanSetting("Писать в чат", "Сообщать о применении и выгрузке темы.").setValue(true));
    private final ButtonSetting export = this.register(new ButtonSetting("Скопировать код темы", "Короткий код с текущими цветами — можно отправить другу.").label("Копировать").onClick(ThemeStudioModule::export));
    private final ButtonSetting apply = this.register(new ButtonSetting("Применить код из буфера", "Взять код темы из буфера обмена и применить.").label("Применить").onClick(ThemeStudioModule::apply));
    private final ButtonSetting studio = this.register(new ButtonSetting("Редактор темы", "Пипетка по палитре, цвета из обложек музыки, предпросмотр.").label("Открыть").onClick(ThemeStudioModule::open));

    public ThemeStudioModule() {
        super("Theme Studio", "Тема коротким кодом и редактор: цвета из обложек трека и подбор оттенков.", Category.DISPLAY);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    private static void export() {
        String code = ThemeCodes.export();
        if (code.isEmpty()) {
            ChatMessage.error("Не удалось собрать код темы");
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.keyboard.setClipboard(code);
        }
        ChatMessage.send("Код темы скопирован (" + code.length() + " символов) — отправьте его другу");
    }

    private static void apply() {
        MinecraftClient client = MinecraftClient.getInstance();
        String code = client == null ? "" : client.keyboard.getClipboard();
        if (!ThemeCodes.looksLikeCode(code)) {
            ChatMessage.error("В буфере нет кода темы ByAzen (нужен код вида BZTHM1:…)");
            return;
        }
        String name = ThemeCodes.apply(code);
        if (name.isEmpty()) {
            ChatMessage.error("Код темы не подошёл");
            return;
        }
        ChatMessage.send("Тема применена: " + name);
    }

    public static void open() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new ThemeStudioScreen());
        }
    }
}
