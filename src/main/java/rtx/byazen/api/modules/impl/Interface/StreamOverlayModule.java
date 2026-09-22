package rtx.byazen.api.modules.impl.Interface;

import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.modules.Category;
import rtx.byazen.api.modules.Module;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.utils.chat.ChatMessage;
import rtx.byazen.utils.web.LocalHttp;
import rtx.byazen.utils.web.WebService;

/**
 * Оверлей для стрима (идея №182 из IDEAS.md).
 * <p>
 * Тот же локальный сервер, что и у компаньона, отдаёт страницу {@code /overlay} с прозрачным фоном:
 * её добавляют в OBS как источник «Браузер». На ней — чат, счётчики (FPS, координаты, время сессии),
 * музыка и лента событий; что показывать, настраивается здесь, а ключ доступа защищает страницу.
 */
public final class StreamOverlayModule
extends Module {

    public final BooleanSetting showChat = this.register(new BooleanSetting("Чат", "Показывать последние сообщения чата."));
    public final BooleanSetting showCounters = this.register(new BooleanSetting("Счётчики", "FPS, память, координаты и время сессии."));
    public final BooleanSetting showMusic = this.register(new BooleanSetting("Музыка", "Текущий трек и громкость."));
    public final BooleanSetting showEvents = this.register(new BooleanSetting("Лента событий", "Смерти, модули, достижения, сезоны."));
    private final BooleanSetting bigFont = this.register(new BooleanSetting("Крупный шрифт", "Шрифт крупнее — читается в записи стрима."));
    private final SeparatorSetting group = this.register(new SeparatorSetting("OBS"));
    private final ButtonSetting link = this.register(new ButtonSetting("Ссылка для OBS", "Скопировать адрес страницы-оверлея с ключом.").label("Ссылка").onClick(StreamOverlayModule::copyLink));
    private final ButtonSetting help = this.register(new ButtonSetting("Как подключить", "Короткая инструкция в чат.").label("Как настроить").onClick(StreamOverlayModule::printHelp));

    public StreamOverlayModule() {
        super("Stream Overlay", "Слой для OBS: чат, счётчики, музыка и события на прозрачном фоне.", Category.DISPLAY);
    }

    @Override
    protected void onEnable() {
        WebService.refresh();
        ChatMessage.send("§bОверлей для OBS: §f" + StreamOverlayModule.overlayUrl());
    }

    @Override
    protected void onDisable() {
        WebService.refresh();
    }

    /** Что показывать: передаётся странице списком включённых блоков. */
    public String blocks() {
        StringBuilder builder = new StringBuilder();
        if (this.showChat.getValue()) {
            builder.append("chat,");
        }
        if (this.showCounters.getValue()) {
            builder.append("counters,");
        }
        if (this.showMusic.getValue()) {
            builder.append("music,");
        }
        if (this.showEvents.getValue()) {
            builder.append("events,");
        }
        builder.append(this.bigFont.getValue() ? "big" : "normal");
        return builder.toString();
    }

    public static String overlayUrl() {
        LocalHttp http = LocalHttp.get();
        rtx.byazen.api.modules.ModuleManager manager = rtx.byazen.api.modules.ModuleManager.get();
        StreamOverlayModule module = manager == null ? null : manager.get(StreamOverlayModule.class);
        String show = module == null ? "" : "&show=" + module.blocks();
        return http.baseUrl() + "/overlay?t=" + http.token() + show;
    }

    private static void copyLink() {
        if (!LocalHttp.get().running()) {
            WebService.refresh();
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.keyboard.setClipboard(StreamOverlayModule.overlayUrl());
        }
        ChatMessage.send("§bСсылка оверлея скопирована");
    }

    private static void printHelp() {
        ChatMessage.send("§bОверлей для OBS:");
        ChatMessage.send("§71. Включите модуль Stream Overlay (панель уже поднята).");
        ChatMessage.send("§72. Скопируйте ссылку кнопкой «Ссылка».");
        ChatMessage.send("§73. В OBS: Источники → + → Браузер → вставьте ссылку.");
        ChatMessage.send("§74. Размер 1920×1080, фон прозрачный — слой ляжет поверх игры.");
        ChatMessage.send("§75. Показывайте только то, что нужно: переключатели в модуле.");
    }
}
