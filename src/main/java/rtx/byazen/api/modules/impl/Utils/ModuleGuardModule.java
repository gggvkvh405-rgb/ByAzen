package rtx.byazen.api.modules.impl.Utils;

import java.util.List;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.safety.ModuleGuard;

/**
 * Анти-краш модулей (идея №169 из IDEAS.md).
 * <p>
 * Ошибка в модуле больше не превращается в бесконечный поток исключений: клиент считает сбои,
 * после порога сам выключает виновника, пишет причину в чат и в лог. Здесь настраивается порог и
 * лежат кнопки «посмотреть список проблем» и «обнулить счётчики» после исправления.
 */
public final class ModuleGuardModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Защита модулей"));
    private final SliderSetting limit = this.register(new SliderSetting("Порог отключения", "Сколько ошибок вытерпеть до авто-отключения модуля.").range(1.0f, 10.0f).increment(1.0f).setValue(3.0f));
    private final ButtonSetting problems = this.register(new ButtonSetting("Список проблем", "Модули, которые уже падали, и причины.").label("Список").onClick(ModuleGuardModule::printProblems));
    private final ButtonSetting disable = this.register(new ButtonSetting("Отключить сбоящие", "Выключить все модули, у которых были ошибки.").label("Отключить").onClick(ModuleGuardModule::disableBroken));
    private final ButtonSetting reset = this.register(new ButtonSetting("Обнулить счётчики", "Сбросить историю сбоев после исправления причины.").label("Обнулить").onClick(ModuleGuard::reset));

    public ModuleGuardModule() {
        super("Module Guard", "Ловит ошибки модулей, показывает причину и отключает виновника.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        ModuleGuard.setLimit((int)this.limit.getFloat());
        ClientLog.info("защита модулей включена: " + ModuleGuard.summary());
    }

    private static void printProblems() {
        List<String> problems = ModuleGuard.problems();
        if (problems.isEmpty()) {
            ChatMessage.send("§aСбоев модулей нет: " + ModuleGuard.statusText());
            return;
        }
        ChatMessage.send("§eСбои модулей (" + ModuleGuard.limit() + " ошибки — отключение):");
        for (String problem : problems) {
            ChatMessage.send("§7• " + problem);
        }
    }

    private static void disableBroken() {
        int disabled = ModuleGuard.disableBroken();
        ChatMessage.send(disabled == 0 ? "§aСбоящих включённых модулей нет" : "§eОтключено модулей: " + disabled);
    }
}
