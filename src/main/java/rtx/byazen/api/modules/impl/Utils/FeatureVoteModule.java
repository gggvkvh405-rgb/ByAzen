package rtx.byazen.api.modules.impl.Utils;

import java.util.List;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.ByAzenHubScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.vote.FeatureVote;

/**
 * Голосование за фичи внутри клиента (идея №198 из IDEAS.md).
 */
public final class FeatureVoteModule
extends Module {

    public final ModeSetting choice = this.register(new ModeSetting("За какую фичу",
            "Что хочется увидеть в клиенте следующим.", "Мини-карта 2.0", FeatureVoteModule.titles()));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Голосование"));
    private final ButtonSetting vote = this.register(new ButtonSetting("Отдать голос",
            "Один голос, его всегда можно поменять.").label("Голосовать").onClick(FeatureVoteModule::voteNow));
    private final ButtonSetting hub = this.register(new ButtonSetting("Вкладка «Фичи» в хабе",
            "Список пожеланий с голосами и своими идеями.").label("Открыть хаб")
            .onClick(() -> ByAzenHubScreen.open(5)));
    private final ButtonSetting code = this.register(new ButtonSetting("Код голосов",
            "Скопировать свой голос и счёт, чтобы сложить с друзьями.").label("Код").onClick(FeatureVoteModule::copyCode));
    private final ButtonSetting accept = this.register(new ButtonSetting("Принять код друга",
            "Взять код из буфера и сложить голоса.").label("Принять").onClick(FeatureVoteModule::acceptCode));
    private final ButtonSetting propose = this.register(new ButtonSetting("Пожелание из буфера",
            "Записать своё пожелание из буфера обмена.").label("Своё").onClick(FeatureVoteModule::proposeNow));
    private final ButtonSetting send = this.register(new ButtonSetting("Текст для отправки",
            "Скопировать итоги списком — удобно отправить в Discord или обсуждения проекта.")
            .label("Отправить").onClick(FeatureVoteModule::copySend));

    public FeatureVoteModule() {
        super("Feature Vote", "Голосование за фичи: список пожеланий, свои идеи и обмен кодами голосов.",
                Category.UTILS);
    }

    private static String[] titles() {
        List<FeatureVote.Wish> wishes = FeatureVote.wishes();
        String[] result = new String[wishes.size()];
        for (int i = 0; i < wishes.size(); ++i) {
            result[i] = wishes.get(i).title();
        }
        return result;
    }

    @Override
    protected void onEnable() {
        ChatMessage.send("§b" + FeatureVote.summary());
    }

    private static void voteNow() {
        FeatureVoteModule module = rtx.byazen.api.modules.ModuleManager.get() == null ? null
                : rtx.byazen.api.modules.ModuleManager.get().get(FeatureVoteModule.class);
        if (module == null) {
            ChatMessage.send("§7Не удалось прочитать настройку голосования");
            return;
        }
        ChatMessage.send("§b" + FeatureVote.vote(module.choice.getValue()));
    }

    private static void copyCode() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.keyboard.setClipboard(FeatureVote.code());
        }
        ChatMessage.send("§bКод голосов скопирован — отправьте его друзьям");
    }

    private static void acceptCode() {
        MinecraftClient client = MinecraftClient.getInstance();
        String code = client == null ? "" : client.keyboard.getClipboard();
        ChatMessage.send("§b" + FeatureVote.applyCode(code));
    }

    private static void proposeNow() {
        MinecraftClient client = MinecraftClient.getInstance();
        String idea = client == null ? "" : client.keyboard.getClipboard();
        if (idea == null || idea.isBlank()) {
            ChatMessage.send("§7Скопируйте пожелание в буфер обмена и нажмите ещё раз");
            return;
        }
        ChatMessage.send("§b" + FeatureVote.propose(idea));
    }

    private static void copySend() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.keyboard.setClipboard(FeatureVote.sendText());
        }
        ChatMessage.send("§bИтоги голосования скопированы — можно отправлять");
    }
}

