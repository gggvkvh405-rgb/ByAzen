package rtx.byazen.api.modules.impl.Interface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.settings.impl.BindSetting;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.GalleryScreen;
import rtx.byazen.api.ui.UI;
import rtx.byazen.utils.lang.Lang;

/**
 * Скриншоты и галерея (идея №45 из IDEAS.md): быстрый просмотр снимков прямо в игре, уведомление
 * о новом снимке и открытие галереи по клавише.
 */
public final class ScreenshotsModule
extends InterfaceComponentModule {

    public final SeparatorSetting gallerySeparator = this.register(new SeparatorSetting("Галерея"));
    public final ButtonSetting openGallery = this.register(new ButtonSetting("Открыть галерею", "Просмотр снимков в игре: превью, полноэкранный вид, удаление, копирование пути.").label("Открыть").onClick(this::openGallery));
    public final BindSetting galleryKey = this.register(new BindSetting("Клавиша галереи", "Клавиша быстрого открытия галереи скриншотов."));
    public final SeparatorSetting notifySeparator = this.register(new SeparatorSetting("Уведомления"));
    public final BooleanSetting notifyNew = this.register(new BooleanSetting("Уведомлять о снимке", "Показывать всплывашку и сообщение в чат, когда в папке появляется новый скриншот.", true));
    public final BooleanSetting hintForF2 = this.register(new BooleanSetting("Подсказка про F2", "В уведомлении напоминать, что снимки делаются по F2.", true));

    private final Set<String> known = new HashSet<String>();
    private boolean scanned;
    private boolean keyDown;
    private long lastCount;

    public ScreenshotsModule() {
        super("Screenshots", "Скриншоты: галерея в игре, уведомления о новых снимках, быстрый просмотр.");
    }

    public void openGallery() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        GalleryScreen screen = new GalleryScreen(client.currentScreen);
        if (UI.isOpen()) {
            UI.closeInto(screen);
        }
        else {
            client.setScreen(screen);
        }
    }

    @Override
    protected void onEnable() {
        this.scanned = false;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre() || !this.isEnabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        this.handleKey(client);
        if (!this.notifyNew.getValue()) {
            return;
        }
        this.scanFolder(client);
    }

    private void handleKey(MinecraftClient client) {
        if (!this.galleryKey.isBound()) {
            return;
        }
        boolean down = this.galleryKey.getValue().isDown(client.getWindow().getHandle());
        if (down && !this.keyDown && client.currentScreen == null) {
            this.keyDown = true;
            this.openGallery();
            return;
        }
        if (!down) {
            this.keyDown = false;
        }
    }

    /** Ищет новые файлы в папке screenshots и сообщает о них (идея №110: подсказка о снимке). */
    private void scanFolder(MinecraftClient client) {
        Path directory = GalleryScreen.screenshotDirectory();
        if (!Files.isDirectory(directory)) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastCount < 700L) {
            return;
        }
        this.lastCount = now;
        try (Stream<Path> stream = Files.list(directory)) {
            Set<String> current = new HashSet<String>();
            String newest = null;
            long newestTime = 0L;
            for (Path path : stream.toList()) {
                if (!path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png")) {
                    continue;
                }
                String name = path.getFileName().toString();
                current.add(name);
                long modified;
                try {
                    modified = Files.getLastModifiedTime(path).toMillis();
                }
                catch (IOException exception) {
                    continue;
                }
                if (modified >= newestTime) {
                    newestTime = modified;
                    newest = name;
                }
            }
            if (!this.scanned) {
                this.known.addAll(current);
                this.scanned = true;
                return;
            }
            for (String name : current) {
                if (this.known.contains(name)) {
                    continue;
                }
                this.known.add(name);
                if (newest == null || !newest.equals(name)) {
                    continue;
                }
                String text = Lang.t("Новый скриншот: ", "New screenshot: ") + name
                    + (this.hintForF2.getValue() ? Lang.t(" (галерея — в модуле Screenshots)", " (gallery: module Screenshots)") : "");
                NotificationsModule.notify(text, 2600L);
                rtx.byazen.utils.chat.ChatMessage.brandmessage(text);
            }
            this.known.retainAll(current);
        }
        catch (IOException exception) {
            // папка занята - просто пропускаем проход
        }
    }
}
