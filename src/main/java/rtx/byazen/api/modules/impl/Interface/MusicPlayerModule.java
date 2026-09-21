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
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.api.music.MusicHttp;
import rtx.byazen.api.music.MusicLibrary;
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

    public final SeparatorSetting queueSeparator = this.register(new SeparatorSetting("Очередь"));
    public final SelectSetting repeatMode = this.register(new SelectSetting("Повтор", "Что делать, когда трек закончился.")
            .value(REPEAT_OPTIONS).selected(REPEAT_OPTIONS[1]));
    public final BooleanSetting shuffleMode = this.register(new BooleanSetting("Перемешивание", "Играть треки и станции в случайном порядке.", false));
    public final BooleanSetting normalizeVolume = this.register(new BooleanSetting("Нормализация громкости", "Выравнивать громкость между станциями, чтобы не приходилось крутить ползунок.", true));

    public final SeparatorSetting startSeparator = this.register(new SeparatorSetting("Запуск"));
    public final BooleanSetting resumeOnJoin = this.register(new BooleanSetting("Возобновлять при входе", "Включать последний трек или станцию при заходе в мир.", false));
    public final SelectSetting defaultStation = this.register(new SelectSetting("Станция по умолчанию", "Что включать, если история ещё пустая.").value(STATION_OPTIONS).selected(STATION_OPTIONS[0]));
    public final BindSetting menuKey = this.register(new BindSetting("Клавиша меню", "Клавиша открытия окна плеера.").setKey(DEFAULT_KEY));
    public final BindSetting searchKey = this.register(new BindSetting("Клавиша поиска", "Клавиша мгновенного поиска музыки: открывает плеер сразу на вкладке поиска.").setKey(DEFAULT_SEARCH_KEY));
    public final BooleanSetting announceTrack = this.register(new BooleanSetting("Тост при смене трека", "Показывать всплывающее уведомление, когда начинается новый трек.", true));

    private final MusicComp component = new MusicComp();
    private final Set<String> refreshedStations = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean refreshRunning = new AtomicBoolean();
    private boolean inWorld;
    private boolean menuKeyDown;
    private boolean searchKeyDown;
    private String lastTrackKey = "";

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

    private void openScreen(boolean search) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        MusicPlayerScreen screen = new MusicPlayerScreen(client.currentScreen);
        if (search) {
            screen.focusSearchTab();
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
        engine.setVolume(this.volume());
        engine.setRepeat(this.repeatFromSettings());
        engine.setShuffle(this.shuffleFromSettings());
        engine.setNormalize(this.normalizeVolume.getValue());
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
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        if (client.player == null || client.world == null) {
            this.inWorld = false;
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
