package rtx.byazen.api.modules.impl.Interface;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import rtx.byazen.api.drags.components.MusicComp;
import rtx.byazen.api.events.EventHandler;
import rtx.byazen.api.events.impl.game.TickEvent;
import rtx.byazen.api.events.impl.input.HotBarScrollEvent;
import rtx.byazen.api.events.impl.input.MouseButtonEvent;
import rtx.byazen.api.modules.impl.Interface.NotificationsModule;
import rtx.byazen.api.modules.settings.impl.BindSetting;
import rtx.byazen.api.modules.settings.impl.BooleanSetting;
import rtx.byazen.api.modules.settings.impl.ButtonSetting;
import rtx.byazen.api.modules.settings.impl.SelectSetting;
import rtx.byazen.api.modules.settings.impl.SeparatorSetting;
import rtx.byazen.api.modules.settings.impl.SliderSetting;
import rtx.byazen.api.music.Equalizer;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.api.music.MusicHttp;
import rtx.byazen.api.music.MusicLibrary;
import rtx.byazen.api.music.MusicPlaylists;
import rtx.byazen.api.music.MusicTrack;
import rtx.byazen.api.music.RadioCatalog;
import rtx.byazen.api.ui.MusicPlayerScreen;
import rtx.byazen.api.ui.UI;

/**
 * ByAzen music player: streams online radio stations and catalogue tracks straight into Minecraft -
 * players never download anything, the library is already inside the client.
 * <p>
 * The module owns the HUD widget (movable through the interface editor) and the full player screen.
 */
public final class MusicPlayerModule
extends InterfaceComponentModule {

    private static final int DEFAULT_KEY = 77; // M
    private static final int DEFAULT_SEARCH_KEY = 71; // G
    private static final String[] STATION_OPTIONS;
    private static final String[] REPEAT_OPTIONS = {"Без повтора", "Повтор списка", "Повтор трека"};
    private static final String[] CROSSFADE_OPTIONS = {"Выключен", "1 с", "2 с", "3 с", "4 с"};
    public static final String[] VISUALIZER_OPTIONS = {"Столбики", "Волна", "Круг"};

    static {
        List<MusicTrack> stations = RadioCatalog.stations();
        ArrayList<String> options = new ArrayList<String>();
        for (int i = 0; i < stations.size() && i < 12; ++i) {
            options.add(stations.get(i).title());
        }
        STATION_OPTIONS = options.toArray(new String[0]);
    }

    public final SeparatorSetting playerSeparator = this.register(new SeparatorSetting("Плеер"));
    public final ButtonSetting library = this.register(new ButtonSetting("Библиотека", "Открыть окно плеера: радио, поиск треков, избранное.").label("Открыть").onClick(this::openScreen));
    public final SliderSetting volume = this.register(new SliderSetting("Громкость", "Громкость интернет-радио и треков в процентах.").range(0.0f, 100.0f).increment(1.0f).setValue(70.0f));
    public final BooleanSetting showWidget = this.register(new BooleanSetting("Виджет в HUD", "Показывать компактный виджет плеера (перетаскивается в редакторе интерфейса).", true));

    public final SeparatorSetting pillSeparator = this.register(new SeparatorSetting("Плашка под музыку"));
    public final BooleanSetting pillWidget = this.register(new BooleanSetting("Плашка в HUD", "Виджет «сейчас играет» с анимированным градиентом (перетаскивается в редакторе интерфейса).", false));
    public final SelectSetting pillStyle = this.register(new SelectSetting("Цвет плашки", "Краски плашки: по обложке трека (с плавным переливом) или по теме клиента.")
            .value(new String[]{"Обложка", "Тема"}).selected("Обложка").visible(() -> this.pillWidget.getValue()));
    public final BooleanSetting pillCover = this.register(new BooleanSetting("Обложка трека", "Показывать миниатюру обложки в плашке.", true).visible(this.pillWidget::getValue));
    public final BooleanSetting pillProgress = this.register(new BooleanSetting("Полоса прогресса", "Показывать прогресс трека в правом нижнем углу плашки.", true).visible(this.pillWidget::getValue));
    public final BooleanSetting pillBars = this.register(new BooleanSetting("Эквалайзер", "Живые полоски уровня звука рядом с названием.", true).visible(this.pillWidget::getValue));

    public final SeparatorSetting queueSeparator = this.register(new SeparatorSetting("Очередь"));
    public final SelectSetting repeatMode = this.register(new SelectSetting("Повтор", "Что делать, когда трек закончился.")
            .value(REPEAT_OPTIONS).selected(REPEAT_OPTIONS[1]));
    public final BooleanSetting shuffleMode = this.register(new BooleanSetting("Перемешивание", "Играть треки и станции в случайном порядке.", false));
    public final BooleanSetting normalizeVolume = this.register(new BooleanSetting("Нормализация громкости", "Выравнивать громкость между станциями, чтобы не приходилось крутить ползунок.", true));

    public final SeparatorSetting behaviourSeparator = this.register(new SeparatorSetting("Поведение"));
    public final SelectSetting visualizerStyle = this.register(new SelectSetting("Визуализатор", "Стиль визуализации звука в окне плеера.")
            .value(VISUALIZER_OPTIONS).selected(VISUALIZER_OPTIONS[0]));
    public final BooleanSetting autoPause = this.register(new BooleanSetting("Автопауза", "Останавливать музыку, когда окно свёрнуто или игрок отошёл (AFK).", false));
    public final SliderSetting sleepTimer = this.register(new SliderSetting("Таймер сна", "Через сколько минут плавно выключить музыку (0 — выключено).").range(0, 180).increment(5).setValue(0.0f));
    public final BooleanSetting nowPlayingToast = this.register(new BooleanSetting("Эфир в уведомлении", "Показывать, что сейчас играет в эфире радиостанции (ICY-метаданные).", true));
    public final BooleanSetting skipDisliked = this.register(new BooleanSetting("Пропускать «не нравится»", "Автоматически переключать треки, отмеченные значком «не нравится».", true));

    public final SeparatorSetting mixSeparator = this.register(new SeparatorSetting("Переходы и звук"));
    public final SelectSetting crossfade = this.register(new SelectSetting("Кроссфейд", "Плавный переход между треками: новый трек нарастает, старый затухает (идея №4).")
            .value(CROSSFADE_OPTIONS).selected(CROSSFADE_OPTIONS[1]));
    public final BooleanSetting equalizerEnabled = this.register(new BooleanSetting("Эквалайзер", "Десятиполосный эквалайзер для радио и треков: басы, середина, верх (идея №5).", false));
    public final SelectSetting equalizerPreset = this.register(new SelectSetting("Пресет эквалайзера", "Готовые кривые: плоская, бас, вокал, ночной режим, электро.")
            .value(Equalizer.presets()).selected(Equalizer.presetName(0)).visible(this.equalizerEnabled::getValue));
    public final ButtonSetting equalizerWindow = this.register(new ButtonSetting("Полосы эквалайзера", "Открыть окно плеера на вкладке эквалайзера: десять полос тянутся мышью.").label("Открыть").onClick(this::openEqualizer));
    public final ButtonSetting playlistsWindow = this.register(new ButtonSetting("Плейлисты", "Открыть свои плейлисты: «+ Создать», треки добавляются правой кнопкой (идея №3).").label("Открыть").onClick(this::openPlaylists));

    public final SeparatorSetting keySeparator = this.register(new SeparatorSetting("Клавиши плеера"));
    public final BindSetting playPauseKey = this.register(new BindSetting("Пауза / играть", "Свободная клавиша управления музыкой (по умолчанию не назначена)."));
    public final BindSetting nextKey = this.register(new BindSetting("Следующий трек", "Свободная клавиша для переключения вперёд."));
    public final BindSetting previousKey = this.register(new BindSetting("Предыдущий трек", "Свободная клавиша для переключения назад."));
    public final BindSetting volumeUpKey = this.register(new BindSetting("Громче", "Прибавить громкость на 5%."));
    public final BindSetting volumeDownKey = this.register(new BindSetting("Тише", "Убавить громкость на 5%."));

    public final SeparatorSetting startSeparator = this.register(new SeparatorSetting("Запуск"));
    public final BooleanSetting resumeOnJoin = this.register(new BooleanSetting("Возобновлять при входе", "Включать последний трек или станцию при заходе в мир.", false));
    public final SelectSetting defaultStation = this.register(new SelectSetting("Станция по умолчанию", "Что включать, если история ещё пустая.").value(STATION_OPTIONS).selected(STATION_OPTIONS[0]));
    public final BindSetting menuKey = this.register(new BindSetting("Клавиша меню", "Клавиша открытия окна плеера.").setKey(DEFAULT_KEY));
    public final BindSetting searchKey = this.register(new BindSetting("Клавиша поиска", "Клавиша мгновенного поиска музыки: открывает плеер сразу на вкладке поиска.").setKey(DEFAULT_SEARCH_KEY));
    public final BooleanSetting announceTrack = this.register(new BooleanSetting("Тост при смене трека", "Показывать всплывающее уведомление, когда начинается новый трек.", true));

    private final MusicComp component = new MusicComp();
    private final Set<String> refreshedStations = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean refreshRunning = new AtomicBoolean();
    private final java.util.Set<String> pressedKeys = java.util.Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
    private boolean inWorld;
    private boolean menuKeyDown;
    private boolean searchKeyDown;
    private boolean autoPaused;
    private boolean fading;
    private long focusedSince;
    private long sleepStarted;
    private double lastActivityX;
    private double lastActivityY;
    private double lastActivityZ;
    private float lastActivityYaw;
    private float lastActivityPitch;
    private long lastActivityAt;
    private String lastTrackKey = "";
    private String lastNowPlaying = "";
    private String appliedPreset = Equalizer.presetName(0);
    private int appliedCrossfade = -1;
    private boolean appliedEqualizer;

    public MusicPlayerModule() {
        super("Music Player", "Музыкальный плеер: интернет-радио и треки из онлайн-каталога прямо в игре, без скачивания файлов.");
    }

    public float volume() {
        return this.volume.getFloat() / 100.0f;
    }

    /** Режим повтора, выбранный в настройках. */
    public MusicEngine.Repeat repeatFromSettings() {
        String selected = this.repeatMode.getValue();
        if (REPEAT_OPTIONS[2].equals(selected)) {
            return MusicEngine.Repeat.ONE;
        }
        if (REPEAT_OPTIONS[0].equals(selected)) {
            return MusicEngine.Repeat.OFF;
        }
        return MusicEngine.Repeat.ALL;
    }

    public boolean shuffleFromSettings() {
        return this.shuffleMode.getValue();
    }

    /** Смена режима повтора из окна плеера (с сохранением в настройках). */
    public void cycleRepeatMode() {
        MusicEngine engine = MusicEngine.get();
        engine.cycleRepeat();
        this.repeatMode.setSelected(MusicPlayerModule.label(engine.repeat()));
        NotificationsModule.notify("Повтор: " + MusicPlayerModule.label(engine.repeat()), 1500L);
    }

    public void cycleShuffleMode() {
        MusicEngine engine = MusicEngine.get();
        engine.toggleShuffle();
        this.shuffleMode.setValue(engine.shuffle());
        NotificationsModule.notify(engine.shuffle() ? "Перемешивание включено" : "Перемешивание выключено", 1500L);
    }

    public void toggleNormalize() {
        MusicEngine engine = MusicEngine.get();
        boolean value = !engine.normalize();
        engine.setNormalize(value);
        this.normalizeVolume.setValue(value);
        NotificationsModule.notify(value ? "Нормализация громкости включена" : "Нормализация выключена", 1500L);
    }

    /** Индекс выбранного стиля визуализации (идея №22). */
    public int visualizerIndex() {
        String selected = this.visualizerStyle.getValue();
        for (int i = 0; i < VISUALIZER_OPTIONS.length; ++i) {
            if (VISUALIZER_OPTIONS[i].equals(selected)) {
                return i;
            }
        }
        return 0;
    }

    public void cycleVisualizer() {
        int next = (this.visualizerIndex() + 1) % VISUALIZER_OPTIONS.length;
        this.visualizerStyle.setSelected(VISUALIZER_OPTIONS[next]);
    }

    private static String label(MusicEngine.Repeat repeat) {
        switch (repeat) {
            case ONE: {
                return REPEAT_OPTIONS[2];
            }
            case OFF: {
                return REPEAT_OPTIONS[0];
            }
            default: {
                return REPEAT_OPTIONS[1];
            }
        }
    }

    public void setVolume(float value) {
        this.volume.setValue(Math.max(0.0f, Math.min(1.0f, value)) * 100.0f);
    }

    /** Track the widget shows while nothing is playing yet. */
    public MusicTrack previewTrack() {
        MusicTrack last = MusicLibrary.get().lastPlayed();
        if (last != null) {
            return last;
        }
        MusicTrack station = this.stationByName(this.defaultStation.getValue());
        return station != null ? station : (RadioCatalog.stations().isEmpty() ? null : RadioCatalog.stations().get(0));
    }

    public MusicTrack stationByName(String name) {
        if (name == null) {
            return null;
        }
        for (MusicTrack track : RadioCatalog.stations()) {
            if (track.title().equals(name)) {
                return track;
            }
        }
        return null;
    }

    public void openScreen() {
        this.openScreen(false);
    }

    /** Мгновенный поиск: открыть плеер сразу на вкладке поиска. */
    public void openSearch() {
        this.openScreen(true);
    }

    /** Окно плеера сразу на вкладке эквалайзера (идея №5). */
    public void openEqualizer() {
        this.openScreen(2);
    }

    /** Окно плеера сразу на вкладке плейлистов (идея №3). */
    public void openPlaylists() {
        this.openScreen(3);
    }

    private void openScreen(boolean search) {
        this.openScreen(search ? 1 : 0);
    }

    private void openScreen(int tab) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        MusicPlayerScreen screen = new MusicPlayerScreen(client.currentScreen);
        if (tab == 1) {
            screen.focusSearchTab();
        }
        else if (tab == 2) {
            screen.focusEqualizerTab();
        }
        else if (tab == 3) {
            screen.focusPlaylistsTab();
        }
        if (UI.isOpen()) {
            UI.closeInto(screen);
        }
        else {
            client.setScreen(screen);
        }
    }

    @Override
    protected void onDisable() {
        MusicEngine.get().stop();
    }

    @Override
    protected void onEnable() {
        MusicEngine engine = MusicEngine.get();
        if (!this.fading) {
            engine.setVolume(this.volume());
        }
        engine.setRepeat(this.repeatFromSettings());
        engine.setShuffle(this.shuffleFromSettings());
        engine.setNormalize(this.normalizeVolume.getValue());
        this.applyMixSettings(engine);
    }

    /**
     * Переносит настройки «Музыка 2.0» в движок: кроссфейд между треками (идея №4) и десятиполосный
     * эквалайзер (№5). Эквалайзер подхватывает изменения на лету — трек перезапускать не нужно.
     */
    private void applyMixSettings(MusicEngine engine) {
        int milliseconds = this.crossfadeMilliseconds();
        if (this.appliedCrossfade != milliseconds) {
            this.appliedCrossfade = milliseconds;
            engine.setCrossfadeMs(milliseconds);
        }
        Equalizer equalizer = Equalizer.get();
        boolean enabled = this.equalizerEnabled.getValue();
        if (this.appliedEqualizer != enabled) {
            this.appliedEqualizer = enabled;
            equalizer.setEnabled(enabled);
        }
        String preset = this.equalizerPreset.getValue();
        if (preset != null && !preset.equals(this.appliedPreset)) {
            this.appliedPreset = preset;
            equalizer.applyPreset(preset);
        }
    }

    /** Кроссфейд в миллисекундах: «Выключен» → 0, «3 с» → 3000 (идея №4). */
    public int crossfadeMilliseconds() {
        String selected = this.crossfade.getValue();
        for (int i = 1; i < CROSSFADE_OPTIONS.length; ++i) {
            if (CROSSFADE_OPTIONS[i].equals(selected)) {
                return i * 1000;
            }
        }
        return 0;
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        MusicEngine engine = MusicEngine.get();
        engine.setVolume(this.volume());
        engine.setRepeat(this.repeatFromSettings());
        engine.setShuffle(this.shuffleFromSettings());
        engine.setNormalize(this.normalizeVolume.getValue());
        this.applyMixSettings(engine);
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        if (client.player == null || client.world == null) {
            this.inWorld = false;
            this.autoPaused = false;
            this.lastActivityAt = 0L;
            this.sleepStarted = 0L;
            this.fading = false;
        }
        else if (!this.inWorld) {
            this.inWorld = true;
            this.onWorldJoin(engine);
        }
        if (!this.isEnabled()) {
            if (engine.isPlaying()) {
                engine.stop();
            }
            return;
        }
        this.handleMenuKey(client, engine);
        this.handleSearchKey(client);
        this.notifyTrackChange(engine);
        this.notifyNowPlaying(engine);
        this.handleTransportKeys(client, engine);
        this.handleAutoPause(client, engine);
        this.handleSleepTimer(engine);
        if (engine.connectingForMs() > 25000L) {
            engine.fail("Сервер не отвечает");
        }
        MusicTrack current = engine.current();
        if (engine.state() == MusicEngine.State.ERROR && current != null && current.isRadio() && current.sourceId() != null) {
            this.refreshStation(current);
        }
    }

    private void onWorldJoin(MusicEngine engine) {
        if (!this.resumeOnJoin.getValue() || engine.isBusy()) {
            return;
        }
        MusicTrack track = MusicLibrary.get().lastPlayed();
        if (track == null) {
            track = this.stationByName(this.defaultStation.getValue());
        }
        if (track != null) {
            engine.play(track);
        }
    }

    private void handleMenuKey(MinecraftClient client, MusicEngine engine) {
        boolean down = this.menuKey.isBound() && this.menuKey.getValue().isDown(client.getWindow().getHandle());
        if (down && !this.menuKeyDown && client.currentScreen == null) {
            this.menuKeyDown = true;
            this.openScreen();
            return;
        }
        if (!down) {
            this.menuKeyDown = false;
        }
    }

    private void handleSearchKey(MinecraftClient client) {
        boolean down = this.searchKey.isBound() && this.searchKey.getValue().isDown(client.getWindow().getHandle());
        if (down && !this.searchKeyDown && client.currentScreen == null) {
            this.searchKeyDown = true;
            this.openSearch();
            return;
        }
        if (!down) {
            this.searchKeyDown = false;
        }
    }

    /** Всплывающее уведомление при смене трека. */
    private void notifyTrackChange(MusicEngine engine) {
        if (!this.announceTrack.getValue()) {
            return;
        }
        MusicTrack track = engine.current();
        if (track == null || engine.state() != MusicEngine.State.PLAYING) {
            return;
        }
        if (track.key().equals(this.lastTrackKey)) {
            return;
        }
        this.lastTrackKey = track.key();
        NotificationsModule.notify("▶ " + track.title() + (track.subtitle().isBlank() ? "" : " — " + track.subtitle()), 2200L);
    }

    /** Автопауза при сворачивании окна и уходе в AFK (идея №7). */
    private void handleAutoPause(MinecraftClient client, MusicEngine engine) {
        if (!this.autoPause.getValue()) {
            if (this.autoPaused) {
                this.autoPaused = false;
                engine.pause(false);
            }
            return;
        }
        boolean focused = org.lwjgl.glfw.GLFW.glfwGetWindowAttrib(client.getWindow().getHandle(), org.lwjgl.glfw.GLFW.GLFW_FOCUSED) == org.lwjgl.glfw.GLFW.GLFW_TRUE;
        if (focused) {
            this.focusedSince = System.currentTimeMillis();
        }
        if (client.player != null) {
            double x = client.player.getX();
            double y = client.player.getY();
            double z = client.player.getZ();
            float yaw = client.player.getYaw();
            float pitch = client.player.getPitch();
            if (Math.abs(x - this.lastActivityX) > 0.008 || Math.abs(y - this.lastActivityY) > 0.008
                    || Math.abs(z - this.lastActivityZ) > 0.008 || Math.abs(yaw - this.lastActivityYaw) > 0.6f
                    || Math.abs(pitch - this.lastActivityPitch) > 0.6f) {
                this.lastActivityAt = System.currentTimeMillis();
            }
            this.lastActivityX = x;
            this.lastActivityY = y;
            this.lastActivityZ = z;
            this.lastActivityYaw = yaw;
            this.lastActivityPitch = pitch;
        }
        long now = System.currentTimeMillis();
        boolean afk = this.lastActivityAt > 0L && now - this.lastActivityAt > 45000L;
        boolean shouldPause = !focused || afk;
        if (shouldPause && !this.autoPaused && engine.isPlaying()) {
            this.autoPaused = true;
            engine.pause(true);
            return;
        }
        if (!shouldPause && this.autoPaused) {
            this.autoPaused = false;
            if (this.focusedSince == 0L || now - this.focusedSince > 300L) {
                engine.pause(false);
            }
        }
    }

    /** Таймер сна: плавно гасит громкость и останавливает плеер (идея №12). */
    private void handleSleepTimer(MusicEngine engine) {
        int minutes = this.sleepTimer.getInt();
        if (minutes <= 0 || !engine.isPlaying()) {
            this.sleepStarted = 0L;
            this.fading = false;
            return;
        }
        long now = System.currentTimeMillis();
        if (this.sleepStarted == 0L) {
            this.sleepStarted = now;
            return;
        }
        long target = (long) minutes * 60000L;
        long passed = now - this.sleepStarted;
        if (passed < target - 12000L) {
            return;
        }
        this.fading = true;
        float remaining = Math.max(0.0f, (float) (target - passed) / 12000.0f);
        float base = this.volume();
        engine.setVolume(0.02f + 0.98f * Math.max(0.0f, Math.min(1.0f, remaining)) * base);
        if (passed >= target) {
            engine.stop();
            engine.setVolume(base);
            this.sleepTimer.setValue(0.0f);
            this.sleepStarted = 0L;
            this.fading = false;
            NotificationsModule.notify("Таймер сна: музыка выключена", 2500L);
        }
    }

    /** Свободные клавиши управления музыкой (идея №16) и авто-пропуск «не нравится» (идея №10). */
    private void handleTransportKeys(MinecraftClient client, MusicEngine engine) {
        if (client.currentScreen != null) {
            this.resetKeyEdges();
            return;
        }
        long handle = client.getWindow().getHandle();
        this.keyEdge(this.playPauseKey, handle, () -> {
            if (engine.current() != null || !this.isEnabled()) {
                engine.togglePause();
            }
        });
        this.keyEdge(this.nextKey, handle, engine::next);
        this.keyEdge(this.previousKey, handle, engine::previous);
        this.keyEdge(this.volumeUpKey, handle, () -> this.setVolume(this.volume() + 0.05f));
        this.keyEdge(this.volumeDownKey, handle, () -> this.setVolume(this.volume() - 0.05f));
        if (this.skipDisliked.getValue()) {
            MusicTrack current = engine.current();
            if (current != null && MusicLibrary.get().isDisliked(current) && engine.isPlaying()) {
                NotificationsModule.notify("Трек пропущен: отмечен как «не нравится»", 1500L);
                engine.next();
            }
        }
    }

    private void keyEdge(BindSetting setting, long handle, Runnable action) {
        boolean down = setting.isBound() && setting.getValue().isDown(handle);
        String id = setting.getValue().getDisplayName();
        if (down && this.pressedKeys.add(id)) {
            action.run();
            return;
        }
        if (!down) {
            this.pressedKeys.remove(id);
        }
    }

    private void resetKeyEdges() {
        this.pressedKeys.clear();
    }

    /** Уведомление о смене трека в эфире (идеи №1 и №11). */
    private void notifyNowPlaying(MusicEngine engine) {
        String playing = engine.nowPlaying();
        if (playing.isEmpty() || playing.equals(this.lastNowPlaying)) {
            return;
        }
        this.lastNowPlaying = playing;
        if (this.nowPlayingToast.getValue()) {
            NotificationsModule.notify("♫ В эфире: " + playing, 3500L);
        }
    }

    /** A curated station moved to another address - ask the radio directory for the current stream. */
    private void refreshStation(MusicTrack track) {
        if (!this.refreshedStations.add(track.sourceId())) {
            return;
        }
        if (!this.refreshRunning.compareAndSet(false, true)) {
            return;
        }
        MusicHttp.submit(() -> {
            try {
                String url = MusicHttp.resolveStationUrl(track.sourceId());
                if (url != null && !url.isBlank() && !url.equals(track.url())) {
                    MusicLibrary.get().rememberResolved(track.sourceId(), url);
                    MusicTrack fixed = track.withUrl(url);
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client != null) {
                        client.execute(() -> MusicEngine.get().play(fixed));
                    }
                }
            }
            finally {
                this.refreshRunning.set(false);
            }
        });
    }

    @EventHandler
    public void onMouse(MouseButtonEvent mouseButtonEvent) {
        if (!this.isEnabled() || mouseButtonEvent.action != MouseButtonEvent.Action.PRESS) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        boolean dragMode = client.currentScreen instanceof ChatScreen;
        if (client.currentScreen != null && !dragMode) {
            return;
        }
        if (this.component.click(mouseButtonEvent.button, dragMode)) {
            mouseButtonEvent.cancel();
        }
    }

    @EventHandler
    public void onScroll(HotBarScrollEvent hotBarScrollEvent) {
        if (!this.isEnabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || (client.currentScreen != null && !(client.currentScreen instanceof ChatScreen))) {
            return;
        }
        if (this.component.scroll(hotBarScrollEvent.getVertical())) {
            hotBarScrollEvent.cancel();
        }
    }
}
