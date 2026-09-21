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
    private static final String[] STATION_OPTIONS;

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

    public final SeparatorSetting startSeparator = this.register(new SeparatorSetting("Запуск"));
    public final BooleanSetting resumeOnJoin = this.register(new BooleanSetting("Возобновлять при входе", "Включать последний трек или станцию при заходе в мир.", false));
    public final SelectSetting defaultStation = this.register(new SelectSetting("Станция по умолчанию", "Что включать, если история ещё пустая.").value(STATION_OPTIONS).selected(STATION_OPTIONS[0]));
    public final BindSetting menuKey = this.register(new BindSetting("Клавиша меню", "Клавиша открытия окна плеера.").setKey(DEFAULT_KEY));

    private final MusicComp component = new MusicComp();
    private final Set<String> refreshedStations = ConcurrentHashMap.newKeySet();
    private final AtomicBoolean refreshRunning = new AtomicBoolean();
    private boolean inWorld;
    private boolean menuKeyDown;

    public MusicPlayerModule() {
        super("Music Player", "Музыкальный плеер: интернет-радио и треки из онлайн-каталога прямо в игре, без скачивания файлов.");
    }

    public float volume() {
        return this.volume.getFloat() / 100.0f;
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
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return;
        }
        MusicPlayerScreen screen = new MusicPlayerScreen(client.currentScreen);
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
        MusicEngine.get().setVolume(this.volume());
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        MusicEngine engine = MusicEngine.get();
        engine.setVolume(this.volume());
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
