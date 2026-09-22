package rtx.byazen.api.modules.impl.Utils;

import java.util.List;
import java.util.Locale;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.network.PacketEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.ModeSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.chat.ChatTranslate;

/**
 * Авто-перевод чата (идея №180 из IDEAS.md).
 * <p>
 * Входящие сообщения переводятся встроенным словарём: около двухсот самых частых слов и фраз чата
 * и игры, плюс ваши собственные пары. Перевод показывается отдельной строкой рядом с оригиналом,
 * ничего не отправляется в интернет и не искажает чужие сообщения. Если знакомых слов мало, клиент
 * молчит — лучше промолчать, чем выдать бессмыслицу.
 */
public final class ChatTranslateModule
extends Module {

    private final ModeSetting direction = this.register(new ModeSetting("Направление", "Определять по тексту или жёстко выбрать одно.", "Авто", "Авто", "Рус → англ", "Англ → рус"));
    private final SliderSetting confidence = this.register(new SliderSetting("Порог уверенности", "Сколько слов сообщения должно быть в словаре, чтобы показать перевод.").range(0.2f, 0.9f).increment(0.05f).setValue(0.4f));
    private final BooleanSetting showOriginal = this.register(new BooleanSetting("Показывать оригинал", "Печатать исходное сообщение рядом с переводом."));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Словарь"));
    private final ButtonSetting stats = this.register(new ButtonSetting("Статистика словаря", "Сколько пар знает клиент.").label("Статистика").onClick(ChatTranslateModule::printStats));
    private final ButtonSetting pairs = this.register(new ButtonSetting("Свои пары в чат", "Показать добавленные вами пары.").label("Мои пары").onClick(ChatTranslateModule::printPairs));
    private final ButtonSetting clipboard = this.register(new ButtonSetting("Перевести буфер", "Перевести текст из буфера обмена и положить перевод обратно.").label("Буфер").onClick(ChatTranslateModule::translateClipboard));

    public ChatTranslateModule() {
        super("Chat Translate", "Перевод чата встроенным словарём: рус ↔ англ, без интернета и внешних сервисов.", Category.UTILS);
    }

    private ChatTranslate.Direction currentDirection() {
        if (this.direction.is("Рус → англ")) {
            return ChatTranslate.Direction.RU_TO_EN;
        }
        if (this.direction.is("Англ → рус")) {
            return ChatTranslate.Direction.EN_TO_RU;
        }
        return ChatTranslate.Direction.AUTO;
    }

    @EventHandler
    public void onPacket(PacketEvent packetEvent) {
        if (!this.isEnabled() || !packetEvent.isReceive() || !(packetEvent.getPacket() instanceof GameMessageS2CPacket)) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) {
            return;
        }
        GameMessageS2CPacket packet = (GameMessageS2CPacket)packetEvent.getPacket();
        String text = packet.content().getString();
        if (text.isBlank() || text.length() > 256 || text.contains("§")) {
            return;
        }
        ChatTranslate.Result result = ChatTranslate.translate(text, this.currentDirection(), this.confidence.getFloat());
        if (result == null || result.translated() < 2) {
            return;
        }
        String prefix = result.direction() == ChatTranslate.Direction.RU_TO_EN ? "§8[рус→англ] " : "§8[англ→рус] ";
        ChatMessage.send(prefix + "§7" + result.text() + (this.showOriginal.getValue() ? " §8· " + text : ""));
    }

    private static void printStats() {
        ChatMessage.send("§bПеревод чата: §f" + ChatTranslate.summary());
    }

    private static void printPairs() {
        List<String> pairs = ChatTranslate.userPairs();
        if (pairs.isEmpty()) {
            ChatMessage.send("§7Свои пары ещё не добавлены");
            return;
        }
        for (String pair : pairs) {
            ChatMessage.send("§7• §f" + pair);
        }
    }

    private static void translateClipboard() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        String text = client.keyboard.getClipboard();
        if (text == null || text.isBlank()) {
            ChatMessage.send("§7Буфер обмена пуст");
            return;
        }
        ChatTranslate.Result result = ChatTranslate.translate(text, ChatTranslate.Direction.AUTO, 0.3f);
        if (result == null) {
            ChatMessage.send("§7Нечего переводить: слов в словаре слишком мало");
            return;
        }
        client.keyboard.setClipboard(result.text());
        ChatMessage.send(String.format(Locale.ROOT, "§bПереведено (%s, знакомых слов %d из %d): §f%s",
                result.label(), result.translated(), result.total(), result.text()));
    }
}
