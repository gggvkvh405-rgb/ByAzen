package rtx.byazen.api.modules.impl.Utils;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.combat.FightReview;

/**
 * Разбор боя после смерти (идея №179 из IDEAS.md).
 * <p>
 * Клиент держит в памяти последние полторы минуты боя: как падало здоровье, от чего, сколько ударов
 * нанесли вы, были ли тотемы, что с бронёй, эффектами и голодом. После смерти всё это превращается
 * в короткий разбор с советами: понятно, что именно пошло не так и что сделать иначе.
 */
public final class FightReviewModule
extends Module {

    private final BooleanSetting chat = this.register(new BooleanSetting("Разбор в чат", "Печатать короткий разбор сразу после смерти."));
    private final BooleanSetting full = this.register(new BooleanSetting("Полный разбор", "Печатать все пункты, а не только главные."));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Разборы"));
    private final ButtonSetting last = this.register(new ButtonSetting("Последний разбор", "Показать разбор последней смерти.").label("Последний").onClick(FightReviewModule::printLast));
    private final ButtonSetting copy = this.register(new ButtonSetting("Скопировать разбор", "Полный текст разбора — в буфер обмена.").label("Скопировать").onClick(FightReviewModule::copyLast));
    private final ButtonSetting history = this.register(new ButtonSetting("История разборов", "Последние разборы одной строкой.").label("История").onClick(FightReviewModule::printHistory));

    public FightReviewModule() {
        super("Fight Review", "Разбор боя после смерти: причина, урон, тотемы, советы.", Category.EVENTS);
    }

    @Override
    public boolean defaultEnabled() {
        return true;
    }

    @Override
    protected void onEnable() {
        FightReview.get().ensure();
    }

    /** Печатать ли разбор в чат: спрашивает сам разбор, когда соберёт отчёт. */
    public static boolean wantsChat() {
        FightReviewModule module = ModuleManager.get().get(FightReviewModule.class);
        return module != null && module.isEnabled() && module.chat.getValue();
    }

    public static boolean wantsFull() {
        FightReviewModule module = ModuleManager.get().get(FightReviewModule.class);
        return module != null && module.full.getValue();
    }

    private static void printLast() {
        FightReview.Report report = FightReview.get().last();
        if (report == null) {
            ChatMessage.send("§7Разборов пока нет — они появляются после смерти");
            return;
        }
        for (String line : report.text().split("\n")) {
            ChatMessage.send("§7" + line);
        }
    }

    private static void copyLast() {
        FightReview.Report report = FightReview.get().last();
        MinecraftClient client = MinecraftClient.getInstance();
        if (report == null || client == null) {
            ChatMessage.send("§7Разборов пока нет");
            return;
        }
        client.keyboard.setClipboard(report.text());
        ChatMessage.send("§bРазбор скопирован в буфер");
    }

    private static void printHistory() {
        List<FightReview.Report> reviews = FightReview.get().reviews();
        if (reviews.isEmpty()) {
            ChatMessage.send("§7История разборов пуста");
            return;
        }
        ChatMessage.send("§bПоследние разборы:");
        for (int i = 0; i < reviews.size() && i < 5; ++i) {
            FightReview.Report report = reviews.get(i);
            ChatMessage.send("§7• §f" + report.world + " §7" + report.coords + " §f" + report.verdict());
        }
    }
}
