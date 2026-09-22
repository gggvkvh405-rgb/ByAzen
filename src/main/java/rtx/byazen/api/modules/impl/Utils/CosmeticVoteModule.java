package rtx.byazen.api.modules.impl.Utils;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.ByAzenHubScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.vote.CosmeticVote;

/**
 * Голосование за косметику (идея №186 из IDEAS.md): голос, код для друзей и свои идеи.
 */
public final class CosmeticVoteModule
extends Module {

    private final SeparatorSetting group = this.register(new SeparatorSetting("Голосование"));
    private final ButtonSetting hub = this.register(new ButtonSetting("Открыть хаб", "Кандидаты, голосование и идеи.").label("Хаб").onClick(() -> ByAzenHubScreen.open(3)));
    private final ButtonSetting code = this.register(new ButtonSetting("Код голоса", "Скопировать свой голос и итоги, чтобы сложить с друзьями.").label("Код голоса").onClick(CosmeticVoteModule::copyCode));
    private final ButtonSetting accept = this.register(new ButtonSetting("Принять код друга", "Взять код из буфера и сложить голоса.").label("Принять").onClick(CosmeticVoteModule::acceptCode));
    private final ButtonSetting idea = this.register(new ButtonSetting("Идея из буфера", "Записать свою идею косметики из буфера обмена.").label("Идея").onClick(CosmeticVoteModule::proposeFromClipboard));
    private final ButtonSetting chat = this.register(new ButtonSetting("Итоги в чат", "Кто впереди по голосам.").label("В чат").onClick(CosmeticVoteModule::printRows));

    public CosmeticVoteModule() {
        super("Cosmetic Vote", "Голосование за следующую косметику: свои голоса, коды для друзей и идеи.", Category.UTILS);
    }

    @Override
    protected void onEnable() {
        ChatMessage.send("§bГолосование за косметику: §f" + CosmeticVote.summary());
    }

    private static void copyCode() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.keyboard.setClipboard(CosmeticVote.code());
        }
        ChatMessage.send("§bКод голоса скопирован — отправьте его друзьям");
    }

    private static void acceptCode() {
        MinecraftClient client = MinecraftClient.getInstance();
        String code = client == null ? "" : client.keyboard.getClipboard();
        ChatMessage.send("§b" + CosmeticVote.applyCode(code));
    }

    private static void proposeFromClipboard() {
        MinecraftClient client = MinecraftClient.getInstance();
        String idea = client == null ? "" : client.keyboard.getClipboard();
        if (idea.isBlank()) {
            ChatMessage.send("§7Скопируйте идею в буфер обмена и нажмите ещё раз");
            return;
        }
        CosmeticVote.propose(idea);
    }

    private static void printRows() {
        ChatMessage.send("§bГолосование за косметику: §f" + CosmeticVote.summary());
        ChatMessage.send("§7" + CosmeticVote.advice());
        for (String line : CosmeticVote.leaderboard()) {
            ChatMessage.send("§7" + line);
        }
        if (CosmeticVote.chatMentions() > 0) {
            ChatMessage.send("§8В чате про косметику говорили " + CosmeticVote.chatMentions() + " раз");
        }
    }
}
