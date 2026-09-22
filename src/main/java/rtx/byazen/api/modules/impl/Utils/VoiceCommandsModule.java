package rtx.byazen.api.modules.impl.Utils;

import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.ui.VoiceScreen;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.voice.VoiceCommands;

/**
 * Голосовые команды (идея №181 из IDEAS.md).
 * <p>
 * Микрофон включается только по нажатию: вы обучаете команду своим голосом, а клиент узнаёт слово по
 * форме звука и выполняет клиентское действие (координаты, свет, карта, музыка). Ничего не пишется
 * в фоне и ничего не отправляется на сервер. Живой голосовой чат клиент не подменяет — если рядом
 * стоит Simple Voice Chat или Plasmo Voice, модуль это показывает и не мешает им.
 */
public final class VoiceCommandsModule
extends Module {

    private final SliderSetting confidence = this.register(new SliderSetting("Порог узнавания", "Насколько похоже должно звучать слово, чтобы команда сработала.").range(0.6f, 0.98f).increment(0.02f).setValue(0.86f));
    private final SeparatorSetting group = this.register(new SeparatorSetting("Команды"));
    private final ButtonSetting open = this.register(new ButtonSetting("Открыть окно команд", "Список команд, обучение и проверка.").label("Окно").onClick(VoiceScreen::open));
    private final ButtonSetting listen = this.register(new ButtonSetting("Слушать и выполнить", "Один раз послушать микрофон и выполнить узнанную команду.").label("Слушать").onClick(VoiceCommandsModule::listenOnce));
    private final ButtonSetting mic = this.register(new ButtonSetting("Проверить микрофон", "Показать, какой микрофон нашла система.").label("Микрофон").onClick(VoiceCommandsModule::printMic));
    private final ButtonSetting status = this.register(new ButtonSetting("Статус команд", "Что обучено и что с голосовым чатом рядом.").label("Статус").onClick(VoiceCommandsModule::printStatus));

    public VoiceCommandsModule() {
        super("Voice Commands", "Команды своим голосом: координаты, свет, карта, музыка. Микрофон — только по нажатию.", Category.UTILS);
    }

    @Override
    public boolean defaultEnabled() {
        return false;
    }

    @Override
    protected void onEnable() {
        ChatMessage.send("§bГолосовые команды: §7" + VoiceCommands.microphoneSummary());
        ChatMessage.send("§7" + VoiceCommands.chatModStatus());
    }

    public static float threshold() {
        VoiceCommandsModule module = ModuleManager.get().get(VoiceCommandsModule.class);
        return module == null ? 0.86f : module.confidence.getFloat();
    }

    private static void listenOnce() {
        ChatMessage.send("§bСлушаю…");
        ChatMessage.send("§7" + VoiceCommands.recognizeAndRun(VoiceCommandsModule.threshold()));
    }

    private static void printMic() {
        for (String device : VoiceCommands.devices()) {
            ChatMessage.send("§7• §f" + device);
        }
    }

    private static void printStatus() {
        ChatMessage.send("§bГолосовые команды: §f" + VoiceCommands.summary());
        ChatMessage.send("§7" + VoiceCommands.chatModStatus());
    }
}
