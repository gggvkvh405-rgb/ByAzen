package rtx.byazen.api.modules.impl.Utils;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.settings.impl.BindSetting;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.TextSetting;

/**
 * Быстрые ответы и шаблоны команд (идеи №114 и №120 из IDEAS.md).
 * <p>
 * Клавиша ответа отправляет заготовку последнему, кто написал в личку (по умолчанию через {@code /r}),
 * а три свободных шаблона позволяют держать под рукой частые сообщения и команды.
 */
public final class QuickChatModule
extends Module {

    public final SeparatorSetting replySection = this.register(new SeparatorSetting("Быстрый ответ"));
    public final TextSetting replyText = this.register(new TextSetting("Текст ответа", "Что отправить в ответ на личное сообщение.")
            .setPlaceholder("Спасибо, сейчас посмотрю")
            .lengthBounds(0, 120));
    public final BooleanSetting replyAsWhisper = this.register(new BooleanSetting("Ответ в ЛС", "Отправлять как /r <текст> — последнему собеседнику.", true));
    public final BindSetting replyKey = this.register(new BindSetting("Клавиша ответа", "Свободная клавиша для быстрого ответа (по умолчанию O).").setKey(79));

    public final SeparatorSetting templates = this.register(new SeparatorSetting("Шаблоны"));
    public final TextSetting templateOne = this.register(new TextSetting("Шаблон 1", "Текст или команда (/команда).").setPlaceholder("/spawn").lengthBounds(0, 120));
    public final BindSetting keyOne = this.register(new BindSetting("Клавиша 1", "Клавиша для шаблона 1."));
    public final TextSetting templateTwo = this.register(new TextSetting("Шаблон 2", "Текст или команда (/команда).").setPlaceholder("/home").lengthBounds(0, 120));
    public final BindSetting keyTwo = this.register(new BindSetting("Клавиша 2", "Клавиша для шаблона 2."));
    public final TextSetting templateThree = this.register(new TextSetting("Шаблон 3", "Текст или команда (/команда).").setPlaceholder("gg").lengthBounds(0, 120));
    public final BindSetting keyThree = this.register(new BindSetting("Клавиша 3", "Клавиша для шаблона 3."));

    private final Set<String> pressed = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());

    public QuickChatModule() {
        super("Quick Chat", "Быстрые ответы и шаблоны команд по клавишам: ответ в ЛС, заготовки, частые команды.", Category.UTILS);
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre() || this.mc.player == null || this.mc.currentScreen != null) {
            this.pressed.clear();
            return;
        }
        long handle = this.mc.getWindow().getHandle();
        this.edge(this.replyKey, handle, this::sendReply);
        this.edge(this.keyOne, handle, () -> this.send(this.templateOne.getValue()));
        this.edge(this.keyTwo, handle, () -> this.send(this.templateTwo.getValue()));
        this.edge(this.keyThree, handle, () -> this.send(this.templateThree.getValue()));
    }

    private void edge(BindSetting setting, long handle, Runnable action) {
        boolean down = setting.isBound() && setting.getValue().isDown(handle);
        String id = setting.getValue().getDisplayName();
        if (down && this.pressed.add(id)) {
            action.run();
            return;
        }
        if (!down) {
            this.pressed.remove(id);
        }
    }

    private void sendReply() {
        String text = this.replyText.getValue();
        if (text == null || text.isBlank()) {
            NotificationsModule.notify("Сначала задайте текст ответа в настройках Quick Chat", 2200L);
            return;
        }
        this.send(this.replyAsWhisper.getValue() ? "/r " + text.trim() : text.trim());
    }

    /** Отправляет сообщение или команду: клиентские команды, серверные команды и обычный чат. */
    public void send(String text) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.player.networkHandler == null || text == null || text.isBlank()) {
            return;
        }
        String value = text.trim();
        rtx.byazen.api.chat.commands.CommandManager manager = rtx.byazen.api.chat.commands.CommandManager.get();
        if (manager != null && manager.isClientCommand(value)) {
            manager.executeRaw(value.substring(manager.getPrefix().length()));
            return;
        }
        if (value.startsWith("/")) {
            client.player.networkHandler.sendChatCommand(value.substring(1));
            return;
        }
        client.player.networkHandler.sendChatMessage(value);
    }
}
