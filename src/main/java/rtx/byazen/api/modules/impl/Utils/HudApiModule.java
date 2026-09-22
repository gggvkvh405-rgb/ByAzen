package rtx.byazen.api.modules.impl.Utils;

import java.util.List;
import rtx.byazen.api.hud.HudApi;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;

/**
 * HUD API для сторонних модов (идея №178 из IDEAS.md).
 * <p>
 * Другие моды могут рисовать свои элементы прямо в HUD ByAzen: элемент регистрируется в
 * {@link HudApi}, позиция запоминается, ошибка одного элемента не ломает остальные. Здесь же —
 * простая проверка: что зарегистрировано и как это сбросить.
 */
public final class HudApiModule
extends Module {

    private final BooleanSetting allow = this.register(new BooleanSetting("Разрешить сторонним модам", "Разрешить чужим модам рисовать свои элементы в HUD ByAzen."));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Элементы HUD API"));
    private final ButtonSetting list = this.register(new ButtonSetting("Список элементов", "Кто рисует в HUD через наш API.").label("Список").onClick(HudApiModule::printList));
    private final ButtonSetting reset = this.register(new ButtonSetting("Сбросить позиции", "Вернуть элементы на правый край экрана.").label("Сбросить").onClick(HudApiModule::resetPositions));

    public HudApiModule() {
        super("HUD API", "Публичный API: сторонние моды рисуют свои элементы в HUD ByAzen.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        HudApi.setEnabled(this.allow.getValue());
    }

    @Override
    protected void onDisable() {
        HudApi.setEnabled(false);
    }

    private static void printList() {
        List<String> titles = HudApi.titles();
        if (titles.isEmpty()) {
            ChatMessage.send("§7Пока никто не зарегистрировался в HUD API");
            return;
        }
        ChatMessage.send("§bЭлементы HUD API (" + titles.size() + "):");
        for (String title : titles) {
            ChatMessage.send("§7• §f" + title);
        }
    }

    private static void resetPositions() {
        HudApi.resetPositions();
        ChatMessage.send("§bПозиции элементов HUD API сброшены");
    }
}
