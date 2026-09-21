package rtx.byazen.api.modules.impl.Utils;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.drags.components.ClipboardComp;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.input.MouseButtonEvent;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;

/**
 * Виджет «недавние координаты» (идея №39 из IDEAS.md): клиент запоминает, что вы копировали
 * (координаты, ники, сообщения), показывает последние записи в HUD и возвращает любую в буфер по клику.
 */
public final class ClipboardModule
extends Module {

    public final SeparatorSetting behaviour = this.register(new SeparatorSetting("Недавние"));
    public final BooleanSetting showWidget = this.register(new BooleanSetting("Виджет в HUD", "Показывать недавние записи буфера обмена на экране.", true));
    public final SliderSetting limit = this.register(new SliderSetting("Хранить записей", "Сколько последних записей держать в списке.").range(2, 8).increment(1).setValue(4.0f));
    public final BooleanSetting skipShort = this.register(new BooleanSetting("Только координаты и ссылки", "Пропускать короткие записи вроде одиночных букв.", true));
    public final BooleanSetting chatNotice = this.register(new BooleanSetting("Записывать в чат", "Показывать в чате, что запись попала в список недавних.", false));

    private final ClipboardComp component = new ClipboardComp();
    private final List<String> entries = new ArrayList<String>();
    private String lastSeen = "";
    private int poll;

    public ClipboardModule() {
        super("Clipboard", "Недавние записи буфера обмена: координаты и сообщения всегда под рукой, клик возвращает их в буфер.", Category.UTILS);
    }

    /** Последние записи, новые первыми. */
    public List<String> entries() {
        synchronized (this.entries) {
            return new ArrayList<String>(this.entries);
        }
    }

    /** Скопировать запись из списка. */
    public void copy(String text) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.keyboard != null && text != null) {
            client.keyboard.setClipboard(text);
        }
    }

    @EventHandler
    public void onMouse(MouseButtonEvent mouseButtonEvent) {
        if (mouseButtonEvent.action == MouseButtonEvent.Action.PRESS && this.isEnabled()) {
            this.component.click(mouseButtonEvent.button, false);
        }
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.keyboard == null) {
            return;
        }
        if (++this.poll < 10) {
            return;
        }
        this.poll = 0;
        String clipboard;
        try {
            clipboard = client.keyboard.getClipboard();
        }
        catch (Throwable throwable) {
            return;
        }
        if (clipboard == null || clipboard.isBlank() || clipboard.equals(this.lastSeen)) {
            return;
        }
        this.lastSeen = clipboard;
        if (this.skipShort.getValue() && clipboard.trim().length() < 3) {
            return;
        }
        String value = clipboard.trim();
        synchronized (this.entries) {
            this.entries.remove(value);
            this.entries.add(0, value);
            int max = (int) this.limit.getValue();
            while (this.entries.size() > max) {
                this.entries.remove(this.entries.size() - 1);
            }
        }
        if (this.chatNotice.getValue()) {
            rtx.byazen.utils.chat.ChatMessage.brandmessage("В недавние: " + ClipboardModule.shorten(value));
        }
    }

    public static String shorten(String value) {
        if (value == null) {
            return "";
        }
        String single = value.replace('\n', ' ').trim();
        return single.length() <= 48 ? single : single.substring(0, 47) + "…";
    }

    /** Модуль доступен для виджета, даже если он выключен — виджет сам решает, показываться ли. */
    public static ClipboardModule instance() {
        ModuleManager manager = ModuleManager.get();
        return manager == null ? null : manager.get(ClipboardModule.class);
    }
}
