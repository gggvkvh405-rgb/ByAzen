package rtx.byazen.api.modules.impl.Interface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.world.biome.Biome;
import rtx.byazen.api.events.impl.game.DeathScreenEvent;
import rtx.byazen.api.events.impl.network.PacketReceiveEvent;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.modules.settings.impl.BindSetting;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.ui.GalleryScreen;
import rtx.byazen.api.ui.ScreenshotAnnotatorScreen;
import rtx.byazen.api.ui.UI;
import rtx.byazen.utils.lang.Lang;
import rtx.byazen.utils.screenshot.Screenshots;

/**
 * Скриншоты и галерея (идея №45 из IDEAS.md): быстрый просмотр снимков прямо в игре, уведомление
 * о новом снимке и открытие галереи по клавише.
 * <p>
 * Идеи №109 и №110: разметка снимка прямо в клиенте и автоматические снимки на смерти, достижении,
 * закате, рассвете и при первом входе в новую местность.
 */
public final class ScreenshotsModule
extends InterfaceComponentModule {

    public final SeparatorSetting gallerySeparator = this.register(new SeparatorSetting("Галерея"));
    public final ButtonSetting openGallery = this.register(new ButtonSetting("Открыть галерею", "Просмотр снимков в игре: превью, полноэкранный вид, удаление, копирование пути.").label("Открыть").onClick(this::openGallery));
    public final BindSetting galleryKey = this.register(new BindSetting("Клавиша галереи", "Клавиша быстрого открытия галереи скриншотов."));
    public final ButtonSetting annotateLatest = this.register(new ButtonSetting("Разметить последний снимок", "Стрелки, рамки, круги, линии и подписи прямо на снимке (идея №109).")
            .label("Отметить").onClick(this::openAnnotator));
    public final BindSetting annotateKey = this.register(new BindSetting("Клавиша разметки", "Клавиша быстрой разметки последнего снимка."));
    public final SeparatorSetting notifySeparator = this.register(new SeparatorSetting("Уведомления"));
    public final BooleanSetting notifyNew = this.register(new BooleanSetting("Уведомлять о снимке", "Показывать всплывашку и сообщение в чат, когда в папке появляется новый скриншот.", true));
    public final BooleanSetting hintForF2 = this.register(new BooleanSetting("Подсказка про F2", "В уведомлении напоминать, что снимки делаются по F2.", true));
    public final SeparatorSetting autoSeparator = this.register(new SeparatorSetting("Авто-снимки (№110)"));
    public final BooleanSetting autoDeath = this.register(new BooleanSetting("Снимок при смерти", "Автоматически сохранять кадр в момент смерти — видно, где и как всё случилось.", true));
    public final BooleanSetting autoAdvancement = this.register(new BooleanSetting("Снимок при достижении", "Сохранять кадр, когда получено достижение, задание или испытание.", true));
    public final BooleanSetting autoSun = this.register(new BooleanSetting("Закат и рассвет", "Снимок на закате и на рассвете — по одному разу за игровые сутки.", false));
    public final BooleanSetting autoBiome = this.register(new BooleanSetting("Новая местность", "Снимок при первом входе в каждый биом: получится альбом путешествия.", false));
    public final SliderSetting autoGap = this.register(new SliderSetting("Пауза между снимками, сек", "Чтобы авто-снимки не заваливали папку.")
            .range(3, 120).increment(1).setValue(15));
    public final BooleanSetting autoNotice = this.register(new BooleanSetting("Сообщать о снимке", "Показывать всплывашку с причиной авто-снимка.", true));

    private final Set<String> known = new HashSet<String>();
    private boolean scanned;
    private boolean keyDown;
    private boolean annotateDown;
    private long lastCount;
    private long lastAutoMs;
    private String lastBiome = "";
    private String lastSkyMark = "";
    private final Set<String> seenBiomes = new HashSet<String>();

    public ScreenshotsModule() {
        super("Screenshots", "Скриншоты: галерея в игре, уведомления о новых снимках, быстрый просмотр.");
    }

    /** Открывает разметку последнего снимка (идея №109 из IDEAS.md). */
    public void openAnnotator() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        Path newest = Screenshots.newest();
        if (newest == null) {
            NotificationsModule.notify("Снимков пока нет — сделайте снимок по F2", 2400L);
            return;
        }
        client.setScreen(new ScreenshotAnnotatorScreen(client.currentScreen, newest));
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
        this.checkBeautifulMoment(client);
        if (!this.notifyNew.getValue()) {
            return;
        }
        this.scanFolder(client);
    }

    /** Смерть — самый полезный кадр: видно место и обстановку (идея №110). */
    @EventHandler
    public void onDeathScreen(DeathScreenEvent deathScreenEvent) {
        if (this.isEnabled() && this.autoDeath.getValue()) {
            this.autoCapture("смерть");
        }
    }

    /** Достижение приходит обычным сообщением сервера — ловим его пакетом (идея №110). */
    @EventHandler
    public void onPacket(PacketReceiveEvent packetReceiveEvent) {
        if (!this.isEnabled() || !this.autoAdvancement.getValue()) {
            return;
        }
        GameMessageS2CPacket packet = packetReceiveEvent.getPacketAs(GameMessageS2CPacket.class);
        if (packet == null || packet.overlay() || !ScreenshotsModule.looksLikeAdvancement(packet.content())) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        client.execute(() -> this.autoCapture("достижение"));
    }

    /** Закат, рассвет и первая новая местность — «красивые моменты» (идея №110). */
    private void checkBeautifulMoment(MinecraftClient client) {
        if (client.world == null || client.player == null || client.currentScreen != null) {
            return;
        }
        if (!this.autoSun.getValue() && !this.autoBiome.getValue()) {
            return;
        }
        if (client.player.age < 40 || client.player.age % 20 != 0) {
            return;
        }
        long day = client.world.getTimeOfDay() / 24000L;
        long time = client.world.getTimeOfDay() % 24000L;
        if (this.autoSun.getValue()) {
            if (time >= 12000L && time < 12600L && !this.lastSkyMark.equals("sunset-" + day)) {
                this.lastSkyMark = "sunset-" + day;
                this.autoCapture("закат");
                return;
            }
            if (time >= 22800L && time < 23400L && !this.lastSkyMark.equals("sunrise-" + day)) {
                this.lastSkyMark = "sunrise-" + day;
                this.autoCapture("рассвет");
                return;
            }
        }
        if (this.autoBiome.getValue()) {
            RegistryEntry<Biome> entry = client.world.getBiome(client.player.getBlockPos());
            String id = entry.getKey().map(key -> key.getValue().getPath()).orElse("");
            if (!id.isEmpty() && !id.equals(this.lastBiome)) {
                this.lastBiome = id;
                if (this.seenBiomes.add(id)) {
                    this.autoCapture("новое место: " + id.replace('_', ' '));
                }
            }
        }
    }

    /** Общий путь авто-снимка: пауза между кадрами и понятное уведомление. */
    private void autoCapture(String reason) {
        long now = System.currentTimeMillis();
        long gap = (long)((float)this.autoGap.getValue() * 1000.0f);
        if (now - this.lastAutoMs < gap) {
            return;
        }
        this.lastAutoMs = now;
        if (!Screenshots.capture()) {
            if (this.autoNotice.getValue()) {
                NotificationsModule.notify("Не удалось сохранить снимок (" + reason + ")", 2600L);
            }
            return;
        }
        this.known.clear();
        this.scanned = false;
        if (this.autoNotice.getValue()) {
            NotificationsModule.notify("Снимок: " + reason, 2600L);
        }
    }

    /** Достижение или задание: смотрим ключ перевода сообщения, а не язык игрока. */
    private static boolean looksLikeAdvancement(Text text) {
        if (text == null) {
            return false;
        }
        TextContent content = text.getContent();
        if (content instanceof TranslatableTextContent) {
            String key = ((TranslatableTextContent)content).getKey();
            if (key != null && key.startsWith("chat.type.advancement")) {
                return true;
            }
        }
        for (Text sibling : text.getSiblings()) {
            if (ScreenshotsModule.looksLikeAdvancement(sibling)) {
                return true;
            }
        }
        return false;
    }

    private void handleKey(MinecraftClient client) {
        boolean down = false;
        if (this.galleryKey.isBound()) {
            down = this.galleryKey.getValue().isDown(client.getWindow().getHandle());
        }
        boolean annotate = this.annotateKey.isBound() && this.annotateKey.getValue().isDown(client.getWindow().getHandle());
        if (client.currentScreen == null) {
            if (down && !this.keyDown) {
                this.keyDown = true;
                this.openGallery();
                return;
            }
            if (annotate && !this.annotateDown) {
                this.annotateDown = true;
                this.openAnnotator();
                return;
            }
        }
        if (!down) {
            this.keyDown = false;
        }
        if (!annotate) {
            this.annotateDown = false;
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
                    + (this.hintForF2.getValue() ? Lang.t(" (галерея — в модуле Screenshots, разметка — клавишей разметки)", " (gallery: module Screenshots, annotate: annotate key)") : "");
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
