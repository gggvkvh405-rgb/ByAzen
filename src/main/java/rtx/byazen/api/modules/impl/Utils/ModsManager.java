package rtx.byazen.api.modules.impl.Utils;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.ModsScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.mods.ModsIndex;

/**
 * Менеджер модов (идея №100 из IDEAS.md).
 * <p>
 * Список загруженных модов с поиском, клавиши вместе с модом-владельцем, поиск по содержимому
 * jar-файлов — по названию кнопки сразу видно, какой мод её добавил, — и предупреждение о совпадающих
 * клавишах при запуске игры.
 */
public final class ModsManager
extends Module {

    public final SeparatorSetting windowGroup = this.register(new SeparatorSetting("Окно"));
    public final ButtonSetting openWindow = this.register(new ButtonSetting("Открыть менеджер модов", "Список модов, клавиши, поиск по содержимому и конфликты.")
            .label("Открыть").onClick(ModsManager::openScreen));
    public final SeparatorSetting reportGroup = this.register(new SeparatorSetting("Отчёты"));
    public final ButtonSetting printSummary = this.register(new ButtonSetting("Показать сводку", "Вывести в чат количество модов и найденные конфликты клавиш.")
            .label("Сводка").onClick(ModsManager::printSummary));
    public final BooleanSetting warnConflicts = this.register(new BooleanSetting("Предупреждать о конфликтах", "Один раз за запуск сообщать о совпадающих клавишах.", true));
    public final BooleanSetting deepSearch = this.register(new BooleanSetting("Искать внутри файлов", "Просматривать языковые файлы внутри jar — медленнее, но точнее.", true));

    private boolean warned;

    public ModsManager() {
        super("ModsManager", "Менеджер модов: список, клавиши с модом-владельцем, поиск по содержимому и конфликты.", Category.UTILS);
    }

    public static void openScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(new ModsScreen(client.currentScreen));
        }
    }

    @Override
    protected void onEnable() {
        this.warned = false;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPost() || !this.isEnabled() || this.mc == null || this.mc.player == null || this.warned) {
            return;
        }
        this.warned = true;
        if (!this.warnConflicts.getValue()) {
            return;
        }
        List<String> conflicts = ModsIndex.keyConflicts();
        if (!conflicts.isEmpty()) {
            ChatMessage.send("Конфликты клавиш: " + conflicts.size() + ". Подробности — в менеджере модов.");
        }
    }

    /** Сводка по модам в чат: используется кнопкой и командой. */
    public static void printSummary() {
        ChatMessage.send(ModsIndex.summary());
        List<ModsIndex.Conflict> conflicts = ModsIndex.conflicts();
        for (ModsIndex.Conflict conflict : conflicts) {
            ChatMessage.send("Конфликт: " + conflict.what + " — " + conflict.who);
        }
        List<String> keys = ModsIndex.keyConflicts();
        for (int i = 0; i < Math.min(5, keys.size()); ++i) {
            ChatMessage.send("Клавиша: " + keys.get(i));
        }
    }

    /** Поиск мода по надписи: подсказка для чата. */
    public static void findOwner(String query) {
        List<ModsIndex.Hit> hits = ModsIndex.search(query, true);
        if (hits.isEmpty()) {
            ChatMessage.send("Ничего не нашлось по запросу «" + query + "».");
            return;
        }
        for (int i = 0; i < Math.min(6, hits.size()); ++i) {
            ModsIndex.Hit hit = hits.get(i);
            ChatMessage.send(hit.mod.display() + " " + hit.mod.version + " — " + hit.where);
        }
    }
}
