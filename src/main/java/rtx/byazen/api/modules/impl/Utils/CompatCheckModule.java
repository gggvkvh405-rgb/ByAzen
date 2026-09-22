package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.compat.MixinAudit;
import rtx.byazen.utils.logs.ClientLog;
import rtx.byazen.utils.startup.EnvCheck;
import rtx.byazen.utils.ui.LayoutAudit;

/**
 * Проверка окружения и совместимости (идеи №167, №171, №173 из IDEAS.md).
 * <p>
 * Три вопроса, из-за которых чаще всего «что-то не работает»: подходящая ли Java и Fabric,
 * не спорят ли миксины с соседними модами и влезают ли окна клиента при текущем масштабе.
 * Здесь на все три есть кнопка и понятный ответ.
 */
public final class CompatCheckModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Проверки"));
    private final BooleanSetting autoWarn = this.register(new BooleanSetting("Предупреждать при входе", "Один раз сообщать в чат, если что-то не так с окружением или миксинами."));
    private final ButtonSetting env = this.register(new ButtonSetting("Java / Loader / Minecraft", "Версии Java, Fabric Loader, игры, Fabric API и памяти.").label("Проверить").onClick(CompatCheckModule::printEnv));
    private final ButtonSetting mixins = this.register(new ButtonSetting("Конфликты миксинов", "Читает лог и ищет предупреждения Mixin от соседних модов.").label("Миксины").onClick(CompatCheckModule::printMixins));
    private final ButtonSetting layout = this.register(new ButtonSetting("Раскладка окон", "Влезают ли панели клиента при 1x–4x и на 4K.").label("Раскладка").onClick(CompatCheckModule::printLayout));
    private final ButtonSetting fit = this.register(new ButtonSetting("Подогнать масштаб окон", "Ужать окна клиента так, чтобы все панели влезали.").label("Подогнать").onClick(CompatCheckModule::fitZoom));

    public CompatCheckModule() {
        super("Compat Check", "Проверяет Java и Fabric, миксины и раскладку окон — до того, как что-то сломается.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        if (this.autoWarn.getValue()) {
            EnvCheck.warnIfNeeded();
            MixinAudit.warnIfNeeded();
        }
    }

    private static void printEnv() {
        ChatMessage.send("§bОкружение: " + EnvCheck.summary());
        for (String line : EnvCheck.report()) {
            ChatMessage.send("§7• " + line);
        }
        ClientLog.info("проверка окружения: " + EnvCheck.summary());
    }

    private static void printMixins() {
        ChatMessage.send("§bМиксины: " + MixinAudit.summary());
        for (String line : MixinAudit.report()) {
            ChatMessage.send("§7" + line);
        }
    }

    private static void printLayout() {
        ChatMessage.send("§bРаскладка: " + LayoutAudit.summary());
        for (String line : LayoutAudit.report().split("\n")) {
            ChatMessage.send("§7" + line);
        }
    }

    private static void fitZoom() {
        float zoom = LayoutAudit.applyFitZoom();
        ChatMessage.send("§bМасштаб окон ByAzen: " + Math.round(zoom * 100.0f) + " % · " + LayoutAudit.summary());
    }
}
