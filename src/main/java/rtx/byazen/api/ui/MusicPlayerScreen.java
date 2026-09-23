package rtx.byazen.api.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.MusicPlayerModule;
import rtx.byazen.api.music.Equalizer;
import rtx.byazen.api.music.MusicCovers;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.api.music.MusicHttp;
import rtx.byazen.api.music.MusicLibrary;
import rtx.byazen.api.music.MusicPlaylists;
import rtx.byazen.api.music.MusicShapes;
import rtx.byazen.api.music.MusicTrack;
import rtx.byazen.api.music.RadioCatalog;
import rtx.byazen.api.ui.module.SearchField;
import rtx.byazen.api.ui.theme.AccentGradient;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.render.fonts.Fonts;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.sounds.Sounds;

/**
 * Full music player window: radio library, online catalogue search, favourites and custom links.
 * <p>
 * Rendering is fully vector based - rounded panels, gradient accents, smoothly animated rows,
 * blurred backdrop and anti-aliased MSDF text. No bitmap or pixel art anywhere.
 */
public final class MusicPlayerScreen
extends BaseScreen {

    private static final float W = 430.0f;
    private static final float H = 300.0f;
    private static final float PAD = 16.0f;
    private static final float TAB_H = 20.0f;
    private static final float TAB_Y = 46.0f;
    private static final float ROW_H = 26.0f;
    private static final float ROW_STEP = 28.0f;
    private static final float FIELD_H = 20.0f;
    private static final float COVER_ROW = 20.0f;
    private static final float COVER_FOOTER = 34.0f;
    private static final float BUTTON = 26.0f;
    private static final float VOLUME_W = 96.0f;

    private static final String FONT_TITLE = "montserrat-extrabold";
    private static final String FONT_SEMI = "montserrat-semibold";
    private static final String FONT_TEXT = "montserrat-medium";

    private static final int MAX_ROWS_PER_TAB = 260;

    private enum Tab {
        LIBRARY("Библиотека"),
        PLAYLISTS("Плейлисты"),
        SEARCH("Поиск"),
        FAVORITES("Избранное"),
        LINKS("Свои ссылки"),
        EQUALIZER("Эквалайзер");

        private final String label;

        Tab(String label) {
            this.label = label;
        }
    }

    private final Screen parent;
    private final SearchField search = new SearchField();
    private final SearchField linkField = new SearchField();
    private final float[] tabHover = new float[Tab.values().length];
    private final float[] buttonHover = new float[4];
    private final MusicLibrary library = MusicLibrary.get();

    private Tab tab = Tab.LIBRARY;
    private float appear;
    private float scroll;
    private float scrollTarget;
    private float volumeHover;
    private float closeHover;
    private float progressHover;
    private final float[] modeHover = new float[3];
    private final float[] chipHover = new float[8];
    private final float[] playlistHover = new float[MusicPlaylists.MAX_PLAYLISTS + 2];
    private final float[] presetHover = new float[Equalizer.presets().length + 2];
    private final float[] bandHover = new float[Equalizer.BANDS];
    private final MusicPlaylists playlists = MusicPlaylists.get();
    private final float[] spectrum = new float[48];
    private float visualizerHover;
    private boolean draggingScroll;
    private boolean draggingVolume;
    private float scrollGrabOffset;
    private long lastNs;
    private float delta = 0.016f;
    private long lastTypeNs;
    private String toast = "";
    private float toastTime;
    private volatile List<MusicTrack> results = new ArrayList<MusicTrack>();
    private volatile boolean searching;
    private volatile String searchedQuery = "";
    private boolean popularLoaded;
    private int libraryChip;
    private float equalizerAppear;
    private int draggingBand = -1;

    public MusicPlayerScreen(Screen parent) {
        super(Text.literal("Музыкальный плеер"));
        this.parent = parent;
        this.search.placeholder("Поиск радио и треков…");
        this.linkField.placeholder("https://ссылка-на-поток или радио");
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        MusicEngine engine = MusicEngine.get();
        if (engine.isPlaying() && !engine.isPaused()) {
            MusicLibrary.get().pushRecent(engine.current());
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.setScreen(this.parent);
        }
    }

    // ------------------------------------------------------------------ geometry

    private static float panelX() {
        return (Position.screenWidth() - W) * 0.5f;
    }

    private static float panelY() {
        return (Position.screenHeight() - H) * 0.5f;
    }

    private float listTop() {
        if (this.tab == Tab.SEARCH || this.tab == Tab.LINKS) {
            return panelY() + 74.0f + FIELD_H + 8.0f;
        }
        if (this.tab == Tab.LIBRARY || this.tab == Tab.PLAYLISTS || this.tab == Tab.EQUALIZER) {
            return panelY() + 74.0f + CHIP_H + 8.0f;
        }
        return panelY() + 74.0f;
    }

    /** Полка чипов вкладки «Библиотека»: все станции, недавние и курируемые подборки. */
    private static final String[] CHIPS = {"Всё", "Недавние", "PvP", "Фарм", "Релакс", "Ночь", "Праздник", "Похожее"};
    private static final float CHIP_H = 18.0f;

    private List<MusicTrack> libraryRows() {
        switch (this.libraryChip) {
            case 1: {
                return new ArrayList<MusicTrack>(this.library.recent());
            }
            case 7: {
                // «Моя волна»: станции, похожие на текущую по жанру (идея №13).
                MusicTrack current = MusicEngine.get().current();
                return current == null ? new ArrayList<MusicTrack>() : RadioCatalog.similar(current, 14);
            }
            case 2:
            case 3:
            case 4:
            case 5:
            case 6: {
                List<RadioCatalog.Collection> collections = RadioCatalog.collections();
                int index = this.libraryChip - 2;
                return index < collections.size() ? RadioCatalog.collectionTracks(collections.get(index)) : new ArrayList<MusicTrack>();
            }
            default: {
                return new ArrayList<MusicTrack>(this.library.all());
            }
        }
    }

    private float chipWidth(int index) {
        return Render2D.msdfWidth(FONT_SEMI, CHIPS[index], 5.6f) + 16.0f;
    }

    private void drawLibraryChips(float x, float y, float a, float mx, float my, float dt) {
        if (this.tab != Tab.LIBRARY) {
            return;
        }
        float cursor = x + PAD;
        float chipY = y + 74.0f;
        for (int i = 0; i < CHIPS.length; ++i) {
            float width = this.chipWidth(i);
            boolean hot = mx >= cursor && mx <= cursor + width && my >= chipY && my <= chipY + CHIP_H;
            this.chipHover[i] += ((hot ? 1.0f : 0.0f) - this.chipHover[i]) * Math.min(1.0f, dt * 13.0f);
            boolean active = i == this.libraryChip;
            int fill = active
                    ? ClientAccent.accentSoft(48.0f * a)
                    : MusicPlayerScreen.rgba(255, 255, 255, (9.0f + 12.0f * this.chipHover[i]) * a);
            Render2D.rect(cursor, chipY, width, CHIP_H, 5.5f, fill);
            if (active) {
                Render2D.outline(cursor, chipY, width, CHIP_H, 5.5f, 0.9f, ClientAccent.accentSoft(120.0f * a));
            }
            int color = active
                    ? MusicPlayerScreen.rgba(255, 255, 255, 245.0f * a)
                    : MusicPlayerScreen.rgba(196, 203, 216, (140.0f + 80.0f * this.chipHover[i]) * a);
            MusicPlayerScreen.text(FONT_SEMI, CHIPS[i], cursor + 8.0f, chipY + CHIP_H * 0.5f, 5.6f, color);
            cursor += width + 5.0f;
        }
    }

    private boolean handleChipClick(float mx, float my, float x, float y) {
        if (this.tab != Tab.LIBRARY) {
            return false;
        }
        float cursor = x + PAD;
        float chipY = y + 74.0f;
        if (my < chipY - 2.0f || my > chipY + CHIP_H + 2.0f) {
            return false;
        }
        for (int i = 0; i < CHIPS.length; ++i) {
            float width = this.chipWidth(i);
            if (mx >= cursor && mx <= cursor + width) {
                this.libraryChip = i;
                this.scroll = 0.0f;
                this.scrollTarget = 0.0f;
                Sounds.play("select_category");
                return true;
            }
            cursor += width + 5.0f;
        }
        return false;
    }

    // ------------------------------------------------------------------ плейлисты (идея №3)

    /** Полка вкладки «Плейлисты»: создать, играть и сами плейлисты. */
    private void drawPlaylistChips(float x, float y, float a, float mx, float my, float dt) {
        if (this.tab != Tab.PLAYLISTS) {
            return;
        }
        float cursor = x + PAD;
        float chipY = y + 74.0f;
        int total = this.playlists.count() + 2;
        for (int i = 0; i < total; ++i) {
            String label = this.playlistChipLabel(i);
            float width = Render2D.msdfWidth(FONT_SEMI, label, 5.6f) + 16.0f;
            if (i > 1 && cursor + width > x + W - PAD) {
                break;
            }
            boolean hot = mx >= cursor && mx <= cursor + width && my >= chipY && my <= chipY + CHIP_H;
            this.playlistHover[Math.min(i, this.playlistHover.length - 1)] += ((hot ? 1.0f : 0.0f)
                    - this.playlistHover[Math.min(i, this.playlistHover.length - 1)]) * Math.min(1.0f, dt * 13.0f);
            float hover = this.playlistHover[Math.min(i, this.playlistHover.length - 1)];
            boolean active = i > 1 && i - 2 == this.playlists.selectedIndex();
            boolean action = i < 2;
            int fill = active || action
                    ? ClientAccent.accentSoft((active ? 48.0f : 24.0f) * a)
                    : MusicPlayerScreen.rgba(255, 255, 255, (9.0f + 12.0f * hover) * a);
            Render2D.rect(cursor, chipY, width, CHIP_H, 5.5f, fill);
            if (active) {
                Render2D.outline(cursor, chipY, width, CHIP_H, 5.5f, 0.9f, ClientAccent.accentSoft(120.0f * a));
            }
            int color = active
                    ? MusicPlayerScreen.rgba(255, 255, 255, 245.0f * a)
                    : action
                      ? ClientAccent.accentBright((170.0f + 70.0f * hover) * a)
                      : MusicPlayerScreen.rgba(196, 203, 216, (140.0f + 80.0f * hover) * a);
            MusicPlayerScreen.text(FONT_SEMI, label, cursor + 8.0f, chipY + CHIP_H * 0.5f, 5.6f, color);
            cursor += width + 5.0f;
        }
        if (this.playlists.count() == 0) {
            String hint = "Плейлистов пока нет: «+ Создать», а треки добавляются правой кнопкой мыши";
            MusicPlayerScreen.text(FONT_TEXT, hint, cursor + 4.0f, chipY + CHIP_H * 0.5f, 5.2f,
                    MusicPlayerScreen.rgba(178, 184, 196, 120.0f * a));
        }
    }

    private String playlistChipLabel(int index) {
        if (index == 0) {
            return "+ Создать";
        }
        if (index == 1) {
            return "Играть";
        }
        MusicPlaylists.Playlist playlist = this.playlists.get(index - 2);
        return playlist == null ? "" : playlist.name();
    }

    private boolean handlePlaylistChipClick(float mx, float my, float x, float y) {
        if (this.tab != Tab.PLAYLISTS) {
            return false;
        }
        float cursor = x + PAD;
        float chipY = y + 74.0f;
        if (my < chipY - 2.0f || my > chipY + CHIP_H + 2.0f) {
            return false;
        }
        int total = this.playlists.count() + 2;
        for (int i = 0; i < total; ++i) {
            float width = Render2D.msdfWidth(FONT_SEMI, this.playlistChipLabel(i), 5.6f) + 16.0f;
            if (mx >= cursor && mx <= cursor + width) {
                this.onPlaylistChip(i);
                return true;
            }
            cursor += width + 5.0f;
        }
        return false;
    }

    private void onPlaylistChip(int index) {
        Sounds.play("select_category");
        if (index == 0) {
            MusicPlaylists.Playlist playlist = this.playlists.create("");
            if (playlist == null) {
                this.toast("Предел плейлистов: " + MusicPlaylists.MAX_PLAYLISTS);
                return;
            }
            this.scroll = 0.0f;
            this.scrollTarget = 0.0f;
            this.toast("Плейлист «" + playlist.name() + "» создан");
            return;
        }
        if (index == 1) {
            this.playSelectedPlaylist();
            return;
        }
        this.playlists.select(index - 2);
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
    }

    private void playSelectedPlaylist() {
        MusicPlaylists.Playlist playlist = this.playlists.selected();
        if (playlist == null) {
            this.toast("Сначала создайте плейлист");
            return;
        }
        MusicTrack track = this.playlists.play(this.playlists.selectedIndex());
        if (track == null) {
            this.toast("Плейлист «" + playlist.name() + "» пуст");
            return;
        }
        Sounds.play("module_settings_open");
        this.toast("Играем «" + playlist.name() + "»");
    }

    /** Правый клик по треку: во вкладке плейлиста — убрать, в остальных — добавить в выбранный. */
    private boolean handleRowContext(List<MusicTrack> list, int row) {
        if (row < 0 || row >= list.size()) {
            return false;
        }
        MusicTrack track = list.get(row);
        if (this.tab == Tab.PLAYLISTS) {
            int playlist = this.playlists.selectedIndex();
            if (playlist < 0 || !this.playlists.removeTrack(playlist, row)) {
                return false;
            }
            Sounds.play("gui_close");
            this.toast("«" + track.title() + "» убран из плейлиста");
            return true;
        }
        MusicPlaylists.Playlist target = this.playlists.selected();
        if (target == null) {
            target = this.playlists.create("");
        }
        if (target == null) {
            this.toast("Предел плейлистов: " + MusicPlaylists.MAX_PLAYLISTS);
            return true;
        }
        if (this.playlists.add(this.playlists.indexOf(target.name()), track)) {
            Sounds.play("gui_open");
            this.toast("«" + track.title() + "» → «" + target.name() + "»");
        }
        else {
            this.toast("Уже есть в «" + target.name() + "»");
        }
        return true;
    }

    private float playlistRowIconX(float left, float width, int slot) {
        return left + width - 18.0f - (float) slot * 14.0f;
    }

    // ------------------------------------------------------------------ эквалайзер (идея №5)

    private float equalizerTop() {
        return this.listTop();
    }

    private float equalizerHeight() {
        return Math.max(60.0f, panelY() + H - 62.0f - this.equalizerTop());
    }

    private float bandWidth() {
        return (W - PAD * 2.0f - 4.0f * (float) (Equalizer.BANDS - 1)) / (float) Equalizer.BANDS;
    }

    private float bandCenterX(int band) {
        return panelX() + PAD + (this.bandWidth() + 4.0f) * (float) band + this.bandWidth() * 0.5f;
    }

    private float bandTrackTop() {
        return this.equalizerTop() + 26.0f;
    }

    private float bandTrackBottom() {
        return this.equalizerTop() + this.equalizerHeight() - 30.0f;
    }

    private float bandValueY(float decibels) {
        float ratio = (Equalizer.MAX_DB - decibels) / (Equalizer.MAX_DB - Equalizer.MIN_DB);
        return this.bandTrackTop() + Math.max(0.0f, Math.min(1.0f, ratio)) * (this.bandTrackBottom() - this.bandTrackTop());
    }

    private float bandYToValue(float y) {
        float span = Math.max(1.0f, this.bandTrackBottom() - this.bandTrackTop());
        float ratio = Math.max(0.0f, Math.min(1.0f, (y - this.bandTrackTop()) / span));
        float value = Equalizer.MAX_DB - ratio * (Equalizer.MAX_DB - Equalizer.MIN_DB);
        return Math.round(value * 2.0f) / 2.0f;
    }

    private int presetChipCount() {
        return Equalizer.presetCount() + 2;
    }

    private String presetChipLabel(int index) {
        if (index == 0) {
            return Equalizer.get().enabled() ? "ЭКВАЛАЙЗЕР: ВКЛ" : "ЭКВАЛАЙЗЕР: ВЫКЛ";
        }
        if (index <= Equalizer.presetCount()) {
            return Equalizer.presetName(index - 1);
        }
        return "Сброс";
    }

    private void drawPresetChips(float x, float y, float a, float mx, float my, float dt) {
        if (this.tab != Tab.EQUALIZER) {
            return;
        }
        Equalizer equalizer = Equalizer.get();
        float cursor = x + PAD;
        float chipY = y + 74.0f;
        int total = this.presetChipCount();
        for (int i = 0; i < total; ++i) {
            String label = this.presetChipLabel(i);
            float width = Render2D.msdfWidth(FONT_SEMI, label, 5.4f) + 14.0f;
            if (cursor + width > x + W - PAD) {
                break;
            }
            boolean hot = mx >= cursor && mx <= cursor + width && my >= chipY && my <= chipY + CHIP_H;
            this.presetHover[Math.min(i, this.presetHover.length - 1)] += ((hot ? 1.0f : 0.0f)
                    - this.presetHover[Math.min(i, this.presetHover.length - 1)]) * Math.min(1.0f, dt * 13.0f);
            float hover = this.presetHover[Math.min(i, this.presetHover.length - 1)];
            boolean active = i == 0
                    ? equalizer.enabled()
                    : i <= Equalizer.presetCount() && equalizer.preset().equals(this.presetChipLabel(i));
            int fill = active
                    ? ClientAccent.accentSoft(46.0f * a)
                    : MusicPlayerScreen.rgba(255, 255, 255, (9.0f + 12.0f * hover) * a);
            Render2D.rect(cursor, chipY, width, CHIP_H, 5.5f, fill);
            if (active) {
                Render2D.outline(cursor, chipY, width, CHIP_H, 5.5f, 0.9f, ClientAccent.accentSoft(120.0f * a));
            }
            int color = active
                    ? MusicPlayerScreen.rgba(255, 255, 255, 245.0f * a)
                    : MusicPlayerScreen.rgba(196, 203, 216, (140.0f + 80.0f * hover) * a);
            MusicPlayerScreen.text(FONT_SEMI, label, cursor + 7.0f, chipY + CHIP_H * 0.5f, 5.4f, color);
            cursor += width + 5.0f;
        }
    }

    private boolean handlePresetChipClick(float mx, float my, float x, float y) {
        if (this.tab != Tab.EQUALIZER) {
            return false;
        }
        float cursor = x + PAD;
        float chipY = y + 74.0f;
        if (my < chipY - 2.0f || my > chipY + CHIP_H + 2.0f) {
            return false;
        }
        int total = this.presetChipCount();
        for (int i = 0; i < total; ++i) {
            float width = Render2D.msdfWidth(FONT_SEMI, this.presetChipLabel(i), 5.4f) + 14.0f;
            if (mx >= cursor && mx <= cursor + width) {
                this.onPresetChip(i);
                return true;
            }
            cursor += width + 5.0f;
        }
        return false;
    }

    private void onPresetChip(int index) {
        Equalizer equalizer = Equalizer.get();
        Sounds.play("select_category");
        if (index == 0) {
            equalizer.setEnabled(!equalizer.enabled());
            this.toast(equalizer.enabled() ? "Эквалайзер включён" : "Эквалайзер выключен");
            return;
        }
        if (index > Equalizer.presetCount()) {
            equalizer.applyPreset(Equalizer.presetName(0));
            equalizer.setEnabled(true);
            this.toast("Полосы сброшены: «" + Equalizer.presetName(0) + "»");
            return;
        }
        String name = Equalizer.presetName(index - 1);
        equalizer.applyPreset(name);
        equalizer.setEnabled(true);
        this.toast("Пресет: " + name);
    }

    private void drawEqualizer(float x, float y, float a, float mx, float my, float dt) {
        Equalizer equalizer = Equalizer.get();
        this.equalizerAppear += (1.0f - this.equalizerAppear) * Math.min(1.0f, dt * 9.0f);
        float ea = a * this.equalizerAppear;
        float left = x + PAD;
        float width = W - PAD * 2.0f;
        float top = this.equalizerTop();
        float height = this.equalizerHeight();
        Render2D.rect(left, top, width, height, 9.0f, MusicPlayerScreen.rgba(255, 255, 255, 7.0f * ea));
        Render2D.outline(left, top, width, height, 9.0f, 1.0f, ClientAccent.accentSoft(24.0f * ea));
        float zeroY = this.bandValueY(0.0f);
        Render2D.rect(left + 8.0f, zeroY, width - 16.0f, 1.0f, 0.5f, MusicPlayerScreen.rgba(255, 255, 255, 26.0f * ea));
        for (int band = 0; band < Equalizer.BANDS; ++band) {
            float cx = this.bandCenterX(band);
            float trackTop = this.bandTrackTop();
            float trackBottom = this.bandTrackBottom();
            float decibels = equalizer.gain(band);
            float valueY = this.bandValueY(decibels);
            boolean hot = mx >= cx - this.bandWidth() * 0.5f && mx <= cx + this.bandWidth() * 0.5f
                    && my >= trackTop - 8.0f && my <= trackBottom + 8.0f;
            this.bandHover[band] += ((hot ? 1.0f : 0.0f) - this.bandHover[band]) * Math.min(1.0f, dt * 14.0f);
            float hover = this.bandHover[band];
            Render2D.rect(cx - 1.25f, trackTop, 2.5f, trackBottom - trackTop, 1.25f, MusicPlayerScreen.rgba(255, 255, 255, 20.0f * ea));
            float fillHeight = Math.abs(valueY - zeroY);
            if (fillHeight > 0.4f) {
                Render2D.rect(cx - 1.25f, Math.min(zeroY, valueY), 2.5f, fillHeight, 1.25f,
                        ClientAccent.accentSoft((140.0f + 60.0f * hover) * ea));
            }
            Render2D.circle(cx, valueY, 3.1f + 0.9f * hover, ClientAccent.accentBright((210.0f + 45.0f * hover) * ea));
            String frequency = Equalizer.label(band);
            MusicPlayerScreen.text(FONT_TEXT, frequency, cx - Render2D.msdfWidth(FONT_TEXT, frequency, 4.9f) * 0.5f,
                    trackBottom + 13.0f, 4.9f, MusicPlayerScreen.rgba(186, 192, 204, 150.0f * ea));
            String value = (decibels > 0.0f ? "+" : "") + Math.round(decibels);
            MusicPlayerScreen.text(FONT_SEMI, value, cx - Render2D.msdfWidth(FONT_SEMI, value, 4.9f) * 0.5f,
                    trackTop - 10.0f, 4.9f, Math.abs(decibels) < 0.1f
                            ? MusicPlayerScreen.rgba(170, 176, 188, 120.0f * ea)
                            : ClientAccent.accentBright(225.0f * ea));
        }
        String hint = equalizer.enabled()
                ? "Тяните полосы мышью · пресет: «" + equalizer.preset() + "»"
                : "Эквалайзер выключен — включите его чипом слева или выберите пресет";
        MusicPlayerScreen.text(FONT_TEXT, hint, left + width * 0.5f - Render2D.msdfWidth(FONT_TEXT, hint, 5.2f) * 0.5f,
                top + height - 7.0f, 5.2f, MusicPlayerScreen.rgba(178, 184, 196, 130.0f * ea));
    }

    private boolean handleEqualizerClick(float mx, float my) {
        if (this.tab != Tab.EQUALIZER) {
            return false;
        }
        float top = this.bandTrackTop();
        float bottom = this.bandTrackBottom();
        if (my < top - 10.0f || my > bottom + 10.0f) {
            return false;
        }
        for (int band = 0; band < Equalizer.BANDS; ++band) {
            float cx = this.bandCenterX(band);
            if (mx < cx - this.bandWidth() * 0.5f || mx > cx + this.bandWidth() * 0.5f) {
                continue;
            }
            Equalizer equalizer = Equalizer.get();
            equalizer.setEnabled(true);
            equalizer.setGain(band, this.bandYToValue(my));
            this.draggingBand = band;
            Sounds.play("module_settings_open");
            return true;
        }
        return false;
    }

    private float listHeight() {
        return panelY() + H - 62.0f - this.listTop();
    }

    private List<MusicTrack> rows() {
        switch (this.tab) {
            case PLAYLISTS: {
                MusicPlaylists.Playlist playlist = this.playlists.selected();
                if (playlist == null) {
                    return new ArrayList<MusicTrack>();
                }
                List<MusicTrack> tracks = new ArrayList<MusicTrack>(playlist.tracks());
                return tracks.size() > MAX_ROWS_PER_TAB ? tracks.subList(0, MAX_ROWS_PER_TAB) : tracks;
            }
            case EQUALIZER: {
                return new ArrayList<MusicTrack>();
            }
            case FAVORITES: {
                List<MusicTrack> favorites = new ArrayList<MusicTrack>(this.library.favorites());
                return favorites.size() > MAX_ROWS_PER_TAB ? favorites.subList(0, MAX_ROWS_PER_TAB) : favorites;
            }
            case LINKS: {
                List<MusicTrack> custom = new ArrayList<MusicTrack>(this.library.custom());
                return custom.size() > MAX_ROWS_PER_TAB ? custom.subList(0, MAX_ROWS_PER_TAB) : custom;
            }
            case SEARCH: {
                List<MusicTrack> found = new ArrayList<MusicTrack>(this.results);
                return found.size() > MAX_ROWS_PER_TAB ? found.subList(0, MAX_ROWS_PER_TAB) : found;
            }
            default: {
                List<MusicTrack> source = this.libraryRows();
                return source.size() > MAX_ROWS_PER_TAB ? source.subList(0, MAX_ROWS_PER_TAB) : source;
            }
        }
    }

    private float maxScroll() {
        return Math.max(0.0f, (float) this.rows().size() * ROW_STEP - this.listHeight());
    }

    private int rowIndexAt(float mouseX, float mouseY) {
        float top = this.listTop();
        float left = panelX() + PAD;
        float width = W - PAD * 2.0f;
        if (mouseX < left || mouseX > left + width || mouseY < top || mouseY > top + this.listHeight()) {
            return -1;
        }
        int index = (int) ((mouseY - top + this.scroll) / ROW_STEP);
        return index >= 0 && index < this.rows().size() ? index : -1;
    }

    // ------------------------------------------------------------------ rendering

    @Override
    protected void renderScreen(DrawContext drawContext, int mouseX, int mouseY, float deltaTicks) {
        float dt = this.updateDelta();
        float mx = Position.mouseX();
        float my = Position.mouseY();
        float x = MusicPlayerScreen.panelX();
        float y = MusicPlayerScreen.panelY();
        this.appear += (1.0f - this.appear) * Math.min(1.0f, dt * 9.0f);
        float a = this.appear;

        Render2D.rect(0.0f, 0.0f, Position.screenWidth(), Position.screenHeight(), 0.0f, MusicPlayerScreen.rgba(4, 5, 9, 150.0f * a));
        Render2D.blur(x, y, W, H, 12.0f, 14.0f, a, -1);
        RectUtil.drawClientRect(x, y, W, H, 12.0f, a);
        Render2D.outline(x, y, W, H, 12.0f, 1.0f, ClientAccent.accentSoft(38.0f * a));
        this.drawHeader(x, y, a, mx, my);
        this.drawVisualizer(x, y, a, mx, my, delta);
        this.drawTabs(x, y, a, mx, my, dt);
        this.drawLibraryChips(x, y, a, mx, my, dt);
        this.drawPlaylistChips(x, y, a, mx, my, dt);
        this.drawPresetChips(x, y, a, mx, my, dt);
        this.drawContent(drawContext, x, y, a, mx, my, dt);
        this.drawFooter(drawContext, x, y, a, mx, my, dt);
        this.drawToast(x, y, a, dt);
    }

    private float updateDelta() {
        long now = System.nanoTime();
        this.delta = this.lastNs == 0L ? 0.016f : (float) (now - this.lastNs) / 1.0E9f;
        this.lastNs = now;
        this.delta = Math.max(0.001f, Math.min(0.1f, this.delta));
        return this.delta;
    }

    private void drawHeader(float x, float y, float a, float mx, float my) {
        MusicPlayerScreen.text(FONT_TITLE, "Музыкальный плеер", x + PAD, y + 24.0f, 12.0f, MusicPlayerScreen.rgba(255, 255, 255, 245.0f * a));
        MusicPlayerScreen.text(FONT_TEXT, "Онлайн-радио и треки из каталога — без скачивания файлов", x + PAD, y + 36.5f, 6.2f,
                MusicPlayerScreen.rgba(196, 202, 214, 140.0f * a));
        float size = 18.0f;
        float bx = x + W - PAD - size;
        float by = y + 16.0f;
        boolean hot = mx >= bx && mx <= bx + size && my >= by && my <= by + size;
        this.closeHover += ((hot ? 1.0f : 0.0f) - this.closeHover) * Math.min(1.0f, this.delta * 12.0f);
        Render2D.rect(bx, by, size, size, 6.0f, MusicPlayerScreen.rgba(255, 255, 255, (10.0f + 16.0f * this.closeHover) * a));
        int ink = hot ? ClientAccent.accentBright(240.0f * a) : MusicPlayerScreen.rgba(210, 214, 224, 190.0f * a);
        float inset = 5.4f;
        Render2D.line(bx + inset, by + inset, bx + size - inset, by + size - inset, 1.4f, ink);
        Render2D.line(bx + size - inset, by + inset, bx + inset, by + size - inset, 1.4f, ink);
    }

    /** Визуализатор звука в шапке окна: столбики, волна или радиальный круг (идея №22). */
    private void drawVisualizer(float x, float y, float a, float mx, float my, float delta) {
        float width = 118.0f;
        float height = 26.0f;
        float vx = x + W - PAD - 24.0f - width;
        float vy = y + 12.0f;
        MusicEngine engine = MusicEngine.get();
        float level = engine.state() == MusicEngine.State.PLAYING && !engine.isPaused() ? engine.level() : 0.0f;
        System.arraycopy(this.spectrum, 0, this.spectrum, 1, this.spectrum.length - 1);
        this.spectrum[0] = level;
        boolean hot = mx >= vx && mx <= vx + width && my >= vy && my <= vy + height;
        this.visualizerHover += ((hot ? 1.0f : 0.0f) - this.visualizerHover) * Math.min(1.0f, delta * 12.0f);
        Render2D.rect(vx, vy, width, height, 7.0f, MusicPlayerScreen.rgba(255, 255, 255, (6.0f + 14.0f * this.visualizerHover) * a));
        int accent = ClientAccent.accentSoft(210.0f * a);
        int soft = ClientAccent.accentSoft(110.0f * a);
        int style = this.visualizerStyle();
        if (style == 1) {
            this.drawWave(vx, vy, width, height, accent);
        }
        else if (style == 2) {
            this.drawRadial(vx, vy, width, height, accent, soft);
        }
        else {
            this.drawBars(vx, vy, width, height, accent, soft);
        }
        if (this.visualizerHover > 0.05f) {
            String hint = MusicPlayerModule.VISUALIZER_OPTIONS[style];
            float hintWidth = Render2D.msdfWidth(FONT_TEXT, hint, 5.2f);
            MusicPlayerScreen.text(FONT_TEXT, hint, vx + width - hintWidth - 4.0f, vy + height - 7.0f, 5.2f,
                    MusicPlayerScreen.rgba(255, 255, 255, 200.0f * this.visualizerHover * a));
        }
    }

    private int visualizerStyle() {
        rtx.byazen.api.modules.impl.Interface.MusicPlayerModule module =
                rtx.byazen.api.modules.ModuleManager.get().get(rtx.byazen.api.modules.impl.Interface.MusicPlayerModule.class);
        return module == null ? 0 : module.visualizerIndex();
    }

    private void drawBars(float vx, float vy, float width, float height, int accent, int soft) {
        int bars = 24;
        float step = width / (float) bars;
        for (int i = 0; i < bars; ++i) {
            float value = this.spectrum[Math.min(this.spectrum.length - 1, i * 2)];
            float barHeight = 2.0f + (height - 8.0f) * Math.max(0.0f, Math.min(1.0f, value * 1.35f));
            Render2D.rect(vx + (float) i * step + step * 0.18f, vy + height - 3.0f - barHeight,
                    step * 0.64f, barHeight, step * 0.32f, i % 2 == 0 ? accent : soft);
        }
    }

    private void drawWave(float vx, float vy, float width, float height, int accent) {
        float middle = vy + height * 0.5f;
        float previousX = vx + 2.0f;
        float previousY = middle;
        int points = 32;
        for (int i = 1; i <= points; ++i) {
            float value = this.spectrum[Math.min(this.spectrum.length - 1, i)] * 2.0f - 1.0f;
            float px = vx + 2.0f + width * 0.96f * (float) i / (float) points;
            float py = middle + Math.max(-1.0f, Math.min(1.0f, value)) * (height * 0.36f);
            Render2D.line(previousX, previousY, px, py, 1.4f, accent);
            previousX = px;
            previousY = py;
        }
    }

    private void drawRadial(float vx, float vy, float width, float height, int accent, int soft) {
        float cx = vx + width * 0.5f;
        float cy = vy + height * 0.5f;
        float inner = height * 0.22f;
        Render2D.circleOutline(cx, cy, inner, 0.9f, soft);
        int bars = 28;
        for (int i = 0; i < bars; ++i) {
            float angle = (float) i / (float) bars * 6.2831855f - 1.5707964f;
            float value = this.spectrum[Math.min(this.spectrum.length - 1, i)];
            float outer = inner + 1.5f + (height * 0.5f - inner - 2.0f) * Math.max(0.05f, Math.min(1.0f, value * 1.4f));
            MusicShapes.radialBar(cx, cy, angle, inner + 1.0f, outer, 0.85f, i % 2 == 0 ? accent : soft);
        }
    }

    private void drawTabs(float x, float y, float a, float mx, float my, float dt) {
        float tx = x + PAD;
        Tab hoveredTab = this.tabAt(mx, my);
        for (Tab value : Tab.values()) {
            float labelWidth = Render2D.msdfWidth(FONT_SEMI, value.label, 6.6f);
            float width = labelWidth + 24.0f;
            boolean active = value == this.tab;
            boolean hot = value == hoveredTab;
            this.tabHover[value.ordinal()] += ((hot ? 1.0f : 0.0f) - this.tabHover[value.ordinal()]) * Math.min(1.0f, dt * 12.0f);
            if (active) {
                AccentGradient.fillHorizontal(tx, y + TAB_Y, width, TAB_H, 7.0f, 70.0f * a);
                Render2D.outline(tx, y + TAB_Y, width, TAB_H, 7.0f, 1.0f, ClientAccent.accentSoft(120.0f * a));
            }
            else if (this.tabHover[value.ordinal()] > 0.01f) {
                Render2D.rect(tx, y + TAB_Y, width, TAB_H, 7.0f, MusicPlayerScreen.rgba(255, 255, 255, 12.0f * this.tabHover[value.ordinal()] * a));
            }
            int color = active
                    ? MusicPlayerScreen.rgba(255, 255, 255, 245.0f * a)
                    : MusicPlayerScreen.rgba(198, 204, 216, (140.0f + 70.0f * this.tabHover[value.ordinal()]) * a);
            MusicPlayerScreen.text(FONT_SEMI, value.label, tx + (width - labelWidth) * 0.5f, y + TAB_Y + TAB_H * 0.5f, 6.6f, color);
            tx += width + 6.0f;
        }
    }

    private Tab tabAt(float mx, float my) {
        float tx = panelX() + PAD;
        float y = panelY() + TAB_Y;
        if (my < y || my > y + TAB_H) {
            return null;
        }
        for (Tab value : Tab.values()) {
            float width = Render2D.msdfWidth(FONT_SEMI, value.label, 6.6f) + 24.0f;
            if (mx >= tx && mx <= tx + width) {
                return value;
            }
            tx += width + 6.0f;
        }
        return null;
    }

    private void drawContent(DrawContext drawContext, float x, float y, float a, float mx, float my, float dt) {
        if (this.tab == Tab.EQUALIZER) {
            this.drawEqualizer(x, y, a, mx, my, dt);
            return;
        }
        float left = x + PAD;
        float width = W - PAD * 2.0f;
        if (this.tab == Tab.SEARCH) {
            this.search.render(drawContext, left, y + 74.0f, width, FIELD_H, a, mx, my, dt);
        }
        else if (this.tab == Tab.LINKS) {
            float fieldWidth = width - 92.0f;
            this.linkField.render(drawContext, left, y + 74.0f, fieldWidth, FIELD_H, a, mx, my, dt);
            float bx = left + fieldWidth + 8.0f;
            float bw = width - fieldWidth - 8.0f;
            boolean hot = mx >= bx && mx <= bx + bw && my >= y + 74.0f && my <= y + 74.0f + FIELD_H;
            AccentGradient.fillHorizontal(bx, y + 74.0f, bw, FIELD_H, 6.0f, (hot ? 110.0f : 70.0f) * a);
            MusicPlayerScreen.text(FONT_SEMI, "Добавить", bx + bw * 0.5f - Render2D.msdfWidth(FONT_SEMI, "Добавить", 6.2f) * 0.5f,
                    y + 74.0f + FIELD_H * 0.5f, 6.2f, MusicPlayerScreen.rgba(255, 255, 255, 240.0f * a));
        }
        this.scroll += (this.scrollTarget - this.scroll) * Math.min(1.0f, dt * 14.0f);
        this.scrollTarget = Math.max(0.0f, Math.min(this.maxScroll(), this.scrollTarget));
        List<MusicTrack> list = this.rows();
        float top = this.listTop();
        float height = this.listHeight();
        Render2D.pushScissor(drawContext, left, top, width, height);
        MusicEngine engine = MusicEngine.get();
        MusicTrack playing = engine.current();
        for (int i = 0; i < list.size(); ++i) {
            float ry = top + (float) i * ROW_STEP - this.scroll;
            if (ry + ROW_H < top - 1.0f || ry > top + height + 1.0f) {
                continue;
            }
            this.drawRow(drawContext, list, i, list.get(i), left, ry, width, a, mx, my, playing, engine);
        }
        Render2D.popScissor(drawContext);
        if (list.isEmpty()) {
            this.drawEmptyState(left, top, width, height, a);
        }
        if (this.searching) {
            MusicPlayerScreen.text(FONT_TEXT, "Поиск…", left + width * 0.5f - Render2D.msdfWidth(FONT_TEXT, "Поиск…", 7.0f) * 0.5f,
                    top + 14.0f, 7.0f, ClientAccent.accentSoft(200.0f * a));
        }
        this.drawScrollbar(left, top, width, height, a);
    }

    private void drawRow(DrawContext drawContext, List<MusicTrack> list, int index, MusicTrack track, float left, float ry,
                         float width, float a, float mx, float my, MusicTrack playing, MusicEngine engine) {
        boolean isPlaying = playing != null && playing.key().equals(track.key());
        boolean playlistRow = this.tab == Tab.PLAYLISTS;
        boolean hovered = mx >= left && mx <= left + width && my >= ry && my <= ry + ROW_H;
        float target = hovered ? 1.0f : 0.0f;
        this.rowHoverAnim(index, target);
        float hover = this.rowHover(Math.floorMod(index, this.tabHover.length));
        int base = isPlaying ? ClientAccent.accentSoft(26.0f * a) : MusicPlayerScreen.rgba(255, 255, 255, (8.0f + 12.0f * hover) * a);
        Render2D.rect(left, ry, width, ROW_H, 7.0f, base);
        if (isPlaying) {
            RectUtil.drawClientSector(left + 0.5f, ry + 6.0f, 2.2f, ROW_H - 12.0f, 1.0f, 1.0f, 1.0f, 1.0f, ClientAccent.accent(220.0f * a), 0.0f);
        }
        this.drawCover(track, left + 5.0f, ry + 3.0f, COVER_ROW, 5.0f, a, track.coverUrl() != null);
        float bookmarkX = left + width - 22.0f - (playlistRow ? 42.0f : 0.0f);
        boolean favorite = this.library.isFavorite(track);
        float textWidth = width - 31.0f - 30.0f - 44.0f - (playlistRow ? 42.0f : 0.0f);
        Render2D.pushScissor(drawContext, left + 31.0f, ry + 2.0f, Math.max(20.0f, textWidth), ROW_H - 4.0f);
        MusicPlayerScreen.text(FONT_SEMI, track.title(), left + 31.0f, ry + 10.0f, 7.0f,
                MusicPlayerScreen.rgba(255, 255, 255, (isPlaying ? 250.0f : 235.0f) * a));
        if (track.subtitle() != null && !track.subtitle().isBlank()) {
            MusicPlayerScreen.text(FONT_TEXT, track.subtitle(), left + 31.0f, ry + 19.0f, 5.7f,
                    MusicPlayerScreen.rgba(190, 196, 208, 150.0f * a));
        }
        Render2D.popScissor(drawContext);
        if (!track.badge().isBlank()) {
            float badgeWidth = Render2D.msdfWidth(FONT_TEXT, track.badge(), 5.4f) + 10.0f;
            float bx = bookmarkX - badgeWidth - 6.0f;
            Render2D.rect(bx, ry + 8.0f, badgeWidth, 10.0f, 5.0f, MusicPlayerScreen.rgba(255, 255, 255, 14.0f * a));
            MusicPlayerScreen.text(FONT_TEXT, track.badge(), bx + 5.0f, ry + 13.0f, 5.4f, MusicPlayerScreen.rgba(210, 216, 228, 170.0f * a));
        }
        float indicatorX = bookmarkX - 24.0f;
        if (isPlaying && engine.state() == MusicEngine.State.PLAYING) {
            MusicShapes.equalizer(indicatorX, ry + 7.0f, 12.0f, engine.level(), ClientAccent.accentBright(230.0f * a));
        }
        else if (hover > 0.05f) {
            Render2D.rect(indicatorX - 1.0f, ry + 5.0f, 16.0f, 16.0f, 8.0f, ClientAccent.accentSoft(46.0f * hover * a));
            MusicShapes.play(indicatorX + 4.6f, ry + 8.6f, 8.6f, ClientAccent.accentBright(235.0f * a * hover));
        }
        boolean bookHot = mx >= bookmarkX - 2.0f && mx <= bookmarkX + 20.0f && my >= ry && my <= ry + ROW_H;
        MusicShapes.bookmark(bookmarkX + 1.0f, ry + 5.0f, 16.0f, favorite,
                favorite ? ClientAccent.accent(240.0f * a) : MusicPlayerScreen.rgba(220, 226, 236, (bookHot ? 220.0f : 120.0f) * a));
        if (playlistRow) {
            for (int slot = 0; slot < 3; ++slot) {
                float iconX = this.playlistRowIconX(left, width, slot);
                boolean iconHot = mx >= iconX - 2.0f && mx <= iconX + 12.0f && my >= ry && my <= ry + ROW_H;
                int color = MusicPlayerScreen.rgba(220, 226, 236, (iconHot ? 235.0f : 120.0f) * a);
                if (slot == 0) {
                    MusicShapes.chevron(iconX, ry + 8.0f, 10.0f, true, color);
                }
                else if (slot == 1) {
                    MusicShapes.chevron(iconX, ry + 8.0f, 10.0f, false, color);
                }
                else {
                    MusicShapes.close(iconX, ry + 8.0f, 10.0f, MusicPlayerScreen.rgba(232, 138, 138, (iconHot ? 240.0f : 150.0f) * a));
                }
            }
        }
    }

    private void drawEmptyState(float left, float top, float width, float height, float a) {
        String message;
        String hint;
        switch (this.tab) {
            case FAVORITES: {
                message = "В избранном пусто";
                hint = "Нажмите на закладку справа от трека";
                break;
            }
            case LINKS: {
                message = "Своих ссылок пока нет";
                hint = "Вставьте ссылку на поток и нажмите «Добавить»";
                break;
            }
            case PLAYLISTS: {
                if (this.playlists.count() == 0) {
                    message = "Плейлистов пока нет";
                    hint = "Нажмите «+ Создать», включите станцию и добавьте её правой кнопкой";
                }
                else {
                    MusicPlaylists.Playlist playlist = this.playlists.selected();
                    message = "Плейлист «" + (playlist == null ? "" : playlist.name()) + "» пуст";
                    hint = "Правый клик по треку в «Библиотеке», «Избранном» или «Поиске» добавит его сюда";
                }
                break;
            }
            case SEARCH: {
                message = this.searchedQuery.isBlank() ? "Введите название или жанр" : "Ничего не найдено";
                hint = "Поиск идёт по онлайн-каталогу и десяткам тысяч радиостанций";
                break;
            }
            default: {
                if (this.libraryChip == 1) {
                    message = "Вы ещё ничего не слушали";
                    hint = "Недавние треки появятся здесь";
                }
                else if (this.libraryChip == 7) {
                    message = "Пока нечего подбирать";
                    hint = "Включите станцию — клиент подберёт похожие";
                }
                else if (this.libraryChip > 1) {
                    message = "В подборке нет станций";
                    hint = "Выберите другую подборку";
                }
                else {
                    message = "Библиотека загружается";
                    hint = "";
                }
                break;
            }
        }
        float centerY = top + height * 0.5f;
        MusicPlayerScreen.text(FONT_SEMI, message, left + width * 0.5f - Render2D.msdfWidth(FONT_SEMI, message, 8.0f) * 0.5f,
                centerY - 6.0f, 8.0f, MusicPlayerScreen.rgba(226, 231, 240, 200.0f * a));
        if (!hint.isBlank()) {
            MusicPlayerScreen.text(FONT_TEXT, hint, left + width * 0.5f - Render2D.msdfWidth(FONT_TEXT, hint, 6.2f) * 0.5f,
                    centerY + 9.0f, 6.2f, MusicPlayerScreen.rgba(178, 184, 196, 130.0f * a));
        }
    }

    private void drawScrollbar(float left, float top, float width, float height, float a) {
        float total = (float) this.rows().size() * ROW_STEP;
        if (total <= height || total <= 0.0f) {
            return;
        }
        float trackX = left + width - 2.0f;
        float thumbHeight = Math.max(24.0f, height * height / total);
        float max = Math.max(1.0f, total - height);
        float thumbY = top + (height - thumbHeight) * (this.scroll / max);
        Render2D.rect(trackX, top + 2.0f, 3.0f, height - 4.0f, 1.5f, MusicPlayerScreen.rgba(255, 255, 255, 18.0f * a));
        Render2D.rect(trackX - 0.5f, thumbY, 4.0f, thumbHeight, 2.0f, ClientAccent.accentSoft(150.0f * a));
    }

    private void drawFooter(DrawContext drawContext, float x, float y, float a, float mx, float my, float dt) {
        MusicEngine engine = MusicEngine.get();
        MusicTrack track = engine.current();
        float top = y + H - 58.0f;
        Render2D.rect(x + PAD, top - 4.0f, W - PAD * 2.0f, 1.0f, 0.5f, MusicPlayerScreen.rgba(255, 255, 255, 20.0f * a));
        if (track == null) {
            MusicPlayerScreen.text(FONT_TEXT, "Ничего не играет — выберите станцию или трек", x + PAD, top + 24.0f, 6.6f,
                    MusicPlayerScreen.rgba(190, 196, 208, 150.0f * a));
        }
        else {
            this.drawCover(track, x + PAD, top + 4.0f, COVER_FOOTER, 8.0f, a, track.coverUrl() != null);
            float textX = x + PAD + COVER_FOOTER + 10.0f;
            Render2D.pushScissor(drawContext, textX, top + 2.0f, 190.0f, 30.0f);
            MusicPlayerScreen.text(FONT_SEMI, track.title(), textX, top + 15.0f, 7.8f, MusicPlayerScreen.rgba(255, 255, 255, 245.0f * a));
            MusicPlayerScreen.text(FONT_TEXT, this.footerSubtitle(track, engine), textX, top + 28.0f, 6.0f,
                    MusicPlayerScreen.rgba(196, 202, 214, 165.0f * a));
            Render2D.popScissor(drawContext);
        }
        float volumeX = x + W - PAD - VOLUME_W;
        float transportRight = volumeX - 14.0f;
        float transportLeft = transportRight - (BUTTON * 3.0f + 16.0f);
        for (int i = 0; i < 3; ++i) {
            float bx = transportLeft + (float) i * (BUTTON + 8.0f);
            float by = top + 9.0f;
            boolean hot = mx >= bx && mx <= bx + BUTTON && my >= by && my <= by + BUTTON;
            this.buttonHover[i] += ((hot ? 1.0f : 0.0f) - this.buttonHover[i]) * Math.min(1.0f, dt * 14.0f);
            float hover = this.buttonHover[i];
            int fill = i == 1
                    ? ClientAccent.accentSoft((70.0f + 60.0f * hover) * a)
                    : MusicPlayerScreen.rgba(255, 255, 255, (12.0f + 16.0f * hover) * a);
            Render2D.circle(bx + BUTTON * 0.5f, by + BUTTON * 0.5f, BUTTON * 0.5f, fill);
            int ink = i == 1 ? MusicPlayerScreen.rgba(255, 255, 255, 245.0f * a)
                    : MusicPlayerScreen.rgba(228, 232, 240, (200.0f + 45.0f * hover) * a);
            switch (i) {
                case 0: {
                    MusicShapes.previous(bx + 6.0f, by + 6.5f, 13.0f, ink);
                    break;
                }
                case 1: {
                    if (engine.state() == MusicEngine.State.PLAYING) {
                        MusicShapes.pause(bx + 7.6f, by + 6.5f, 12.0f, ink);
                    }
                    else {
                        MusicShapes.play(bx + 8.4f, by + 6.5f, 12.0f, ink);
                    }
                    break;
                }
                default: {
                    MusicShapes.next(bx + 7.0f, by + 6.5f, 13.0f, ink);
                    break;
                }
            }
        }
        boolean volumeHot = mx >= volumeX - 4.0f && mx <= volumeX + VOLUME_W + 4.0f && my >= top + 12.0f && my <= top + 36.0f;
        this.volumeHover += ((volumeHot ? 1.0f : 0.0f) - this.volumeHover) * Math.min(1.0f, dt * 12.0f);
        rtx.byazen.api.modules.impl.Interface.MusicPlayerModule module =
                rtx.byazen.api.modules.ModuleManager.get().get(rtx.byazen.api.modules.impl.Interface.MusicPlayerModule.class);
        float volume = module == null ? engine.volume() : module.volume();
        MusicPlayerScreen.text(FONT_TEXT, "Громкость", volumeX, top + 12.0f, 5.4f, MusicPlayerScreen.rgba(186, 192, 204, 150.0f * a));
        String percent = Math.round(volume * 100.0f) + "%";
        MusicPlayerScreen.text(FONT_TEXT, percent, volumeX + VOLUME_W - Render2D.msdfWidth(FONT_TEXT, percent, 5.4f), top + 12.0f, 5.4f,
                MusicPlayerScreen.rgba(214, 220, 232, 190.0f * a));
        float trackY = top + 23.0f;
        Render2D.rect(volumeX, trackY, VOLUME_W, 4.0f, 2.0f, MusicPlayerScreen.rgba(255, 255, 255, 26.0f * a));
        AccentGradient.fillHorizontal(volumeX, trackY, Math.max(2.0f, VOLUME_W * volume), 4.0f, 2.0f, 220.0f * a);
        Render2D.circle(volumeX + VOLUME_W * volume, trackY + 2.0f, 3.4f, ClientAccent.accentBright(240.0f * a));

        float barX = x + PAD;
        float barY = y + H - 14.0f;
        float barWidth = W - PAD * 2.0f;
        boolean progressHot = my >= barY - 6.0f && my <= barY + 6.0f && mx >= barX && mx <= barX + barWidth;
        this.progressHover += ((progressHot ? 1.0f : 0.0f) - this.progressHover) * Math.min(1.0f, dt * 12.0f);
        Render2D.rect(barX, barY, barWidth, 3.0f, 1.5f, MusicPlayerScreen.rgba(255, 255, 255, 26.0f * a));
        String time = MusicPlayerScreen.time(engine.elapsedMs()) + (track == null || track.isLive() ? "" : " / " + MusicPlayerScreen.time(track.durationMs()));
        if (track != null) {
            if (track.isLive()) {
                float phase = (float) (System.nanoTime() % 3000000000L) / 3.0E9f;
                AccentGradient.fillHorizontal(barX, barY, barWidth, 3.0f, 1.5f, 120.0f * a);
                float head = barWidth * (0.5f + 0.5f * (float) Math.sin(phase * 6.2831855f));
                Render2D.circle(barX + head, barY + 1.5f, 2.6f + this.progressHover, ClientAccent.accentBright(230.0f * a));
            }
            else {
                float progress = Math.max(0.0f, Math.min(1.0f, engine.progress()));
                AccentGradient.fillHorizontal(barX, barY, barWidth * progress, 3.0f, 1.5f, 210.0f * a);
                Render2D.circle(barX + barWidth * progress, barY + 1.5f, 2.6f + this.progressHover * 1.2f, ClientAccent.accentBright(235.0f * a));
            }
        }
        MusicPlayerScreen.text(FONT_TEXT, time, barX, barY - 9.0f, 5.4f, MusicPlayerScreen.rgba(188, 194, 206, 150.0f * a));
        this.drawModes(x, y, a, mx, my, dt);
        if (this.progressHover > 0.05f && track != null) {
            float hintWidth = Render2D.msdfWidth(FONT_TEXT, "Нажмите, чтобы начать заново", 5.4f);
            MusicPlayerScreen.text(FONT_TEXT, "Нажмите, чтобы начать заново", this.modesLeft(x) - hintWidth - 10.0f,
                    barY - 9.0f, 5.4f, ClientAccent.accentSoft(200.0f * a * this.progressHover));
        }
    }

    /** Подписи режимов: повтор, перемешивание, нормализация громкости (идеи №8, №9, №10). */
    private String modeLabel(int index) {
        MusicEngine engine = MusicEngine.get();
        switch (index) {
            case 0: {
                switch (engine.repeat()) {
                    case ALL: {
                        return "ПОВТОР: ВСЕ";
                    }
                    case ONE: {
                        return "ПОВТОР: 1 ТРЕК";
                    }
                    default: {
                        return "ПОВТОР: ВЫКЛ";
                    }
                }
            }
            case 1: {
                return engine.shuffle() ? "МИКС: ВКЛ" : "МИКС: ВЫКЛ";
            }
            default: {
                return engine.normalize() ? "НОРМ: ВКЛ" : "НОРМ: ВЫКЛ";
            }
        }
    }

    private float modeWidth(int index) {
        return Render2D.msdfWidth(FONT_SEMI, this.modeLabel(index), 5.0f) + 12.0f;
    }

    private float modesLeft(float x) {
        float width = 0.0f;
        for (int i = 0; i < 3; ++i) {
            width += this.modeWidth(i) + 5.0f;
        }
        return x + W - PAD - width + 5.0f;
    }

    private void drawModes(float x, float y, float a, float mx, float my, float dt) {
        MusicEngine engine = MusicEngine.get();
        float barY = y + H - 14.0f;
        float pillY = barY - 9.0f - 2.0f;
        float cursor = this.modesLeft(x);
        for (int i = 0; i < 3; ++i) {
            float width = this.modeWidth(i);
            boolean hot = mx >= cursor && mx <= cursor + width && my >= pillY && my <= pillY + 12.0f;
            this.modeHover[i] += ((hot ? 1.0f : 0.0f) - this.modeHover[i]) * Math.min(1.0f, dt * 13.0f);
            boolean active = i == 0 ? engine.repeat() != MusicEngine.Repeat.OFF : i == 1 ? engine.shuffle() : engine.normalize();
            int fill = MusicPlayerScreen.rgba(255, 255, 255, (10.0f + 14.0f * this.modeHover[i]) * a);
            Render2D.rect(cursor, pillY, width, 12.0f, 6.0f, fill);
            if (active) {
                Render2D.outline(cursor, pillY, width, 12.0f, 6.0f, 1.0f, ClientAccent.accentSoft((70.0f + 60.0f * this.modeHover[i]) * a));
            }
            int color = active
                    ? MusicPlayerScreen.rgba(238, 242, 250, (170.0f + 70.0f * this.modeHover[i]) * a)
                    : MusicPlayerScreen.rgba(182, 189, 202, (120.0f + 80.0f * this.modeHover[i]) * a);
            MusicPlayerScreen.text(FONT_SEMI, this.modeLabel(i), cursor + 6.0f, pillY + 6.0f - 3.0f, 5.0f, color);
            cursor += width + 5.0f;
        }
    }

    /** Клик по «пилюлям» режимов. */
    private boolean handleModeClick(float mx, float my, float x, float y) {
        float barY = y + H - 14.0f;
        float pillY = barY - 9.0f - 2.0f;
        if (my < pillY || my > pillY + 12.0f) {
            return false;
        }
        float cursor = this.modesLeft(x);
        for (int i = 0; i < 3; ++i) {
            float width = this.modeWidth(i);
            if (mx >= cursor && mx <= cursor + width) {
                this.cycleMode(i);
                return true;
            }
            cursor += width + 5.0f;
        }
        return false;
    }

    /** Переключение режима: 0 — повтор, 1 — перемешивание, 2 — нормализация. */
    private void cycleMode(int index) {
        MusicEngine engine = MusicEngine.get();
        rtx.byazen.api.modules.impl.Interface.MusicPlayerModule module =
                rtx.byazen.api.modules.ModuleManager.get().get(rtx.byazen.api.modules.impl.Interface.MusicPlayerModule.class);
        Sounds.play("select_category");
        switch (index) {
            case 0: {
                if (module != null) {
                    module.cycleRepeatMode();
                }
                else {
                    engine.cycleRepeat();
                    this.toast("Повтор: " + engine.repeat().label());
                }
                break;
            }
            case 1: {
                if (module != null) {
                    module.cycleShuffleMode();
                }
                else {
                    engine.toggleShuffle();
                    this.toast(engine.shuffle() ? "Перемешивание включено" : "Перемешивание выключено");
                }
                break;
            }
            default: {
                if (module != null) {
                    module.toggleNormalize();
                }
                else {
                    engine.setNormalize(!engine.normalize());
                    this.toast(engine.normalize() ? "Нормализация включена" : "Нормализация выключена");
                }
                break;
            }
        }
    }

    private String footerSubtitle(MusicTrack track, MusicEngine engine) {
        if (engine.state() == MusicEngine.State.CONNECTING) {
            return "Подключение к потоку…";
        }
        if (engine.state() == MusicEngine.State.ERROR) {
            return "Ошибка: " + engine.detail();
        }
        if (!engine.detail().isBlank() && engine.state() == MusicEngine.State.PLAYING) {
            return engine.detail();
        }
        if (engine.isPaused()) {
            return "Пауза";
        }
        if (track.isRadio()) {
            return track.subtitle().isBlank() ? "Прямой эфир" : "Прямой эфир • " + track.subtitle();
        }
        return track.subtitle().isBlank() ? track.badge() : track.subtitle();
    }

    private void drawCover(MusicTrack track, float x, float y, float size, float radius, float a, boolean hasArt) {
        String texture = hasArt ? MusicCovers.textureFor(track.coverUrl()) : null;
        if (texture != null && Render2D.imageReady(texture)) {
            Render2D.image(texture, x, y, size, radius, MusicPlayerScreen.rgba(255, 255, 255, 255.0f * a));
            return;
        }
        Render2D.rect(x, y, size, size, radius, radius, radius, radius, ClientAccent.accentSoft(64.0f * a));
        Render2D.rect(x, y + size * 0.45f, size, size * 0.55f, 0.0f, 0.0f, radius, radius, ClientAccent.accentSoft(20.0f * a));
        MusicShapes.note(x + size * 0.22f, y + size * 0.2f, size * 0.56f, MusicPlayerScreen.rgba(255, 255, 255, 205.0f * a));
    }

    private void drawToast(float x, float y, float a, float dt) {
        if (this.toastTime <= 0.0f || this.toast.isBlank()) {
            return;
        }
        this.toastTime = Math.max(0.0f, this.toastTime - dt);
        float fade = Math.min(1.0f, this.toastTime * 2.5f) * Math.min(1.0f, (1.6f - this.toastTime) * 4.0f);
        if (fade <= 0.01f) {
            return;
        }
        float width = Render2D.msdfWidth(FONT_SEMI, this.toast, 6.4f) + 26.0f;
        float bx = x + (W - width) * 0.5f;
        float by = y + H - 42.0f - 22.0f * (1.0f - fade);
        RectUtil.drawClientRectFixedRadius(bx, by, width, 20.0f, 7.0f, fade * a, 0.0f);
        Render2D.outline(bx, by, width, 20.0f, 7.0f, 1.0f, ClientAccent.accentSoft(90.0f * fade * a));
        MusicPlayerScreen.text(FONT_SEMI, this.toast, bx + width * 0.5f - Render2D.msdfWidth(FONT_SEMI, this.toast, 6.4f) * 0.5f,
                by + 10.0f, 6.4f, MusicPlayerScreen.rgba(255, 255, 255, 240.0f * fade * a));
    }

    // ------------------------------------------------------------------ input

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        float mx = Position.mouseX();
        float my = Position.mouseY();
        float x = MusicPlayerScreen.panelX();
        float y = MusicPlayerScreen.panelY();
        if (this.mouseInsidePanel(mx, my)) {
            float size = 18.0f;
            if (mx >= x + W - PAD - size && mx <= x + W - PAD && my >= y + 16.0f && my <= y + 16.0f + size) {
                Sounds.play("settings_close");
                this.close();
                return true;
            }
            Tab hoveredTab = this.tabAt(mx, my);
            if (hoveredTab != null) {
                if (this.tab != hoveredTab) {
                    Sounds.play("select_category");
                    this.tab = hoveredTab;
                    this.scroll = 0.0f;
                    this.scrollTarget = 0.0f;
                    if (this.tab == Tab.EQUALIZER) {
                        this.equalizerAppear = 0.0f;
                    }
                    if (this.tab == Tab.SEARCH) {
                        this.search.focus();
                        this.lastTypeNs = System.currentTimeMillis();
                        if (!this.popularLoaded) {
                            this.publishPopular();
                        }
                    }
                    else {
                        this.search.blur();
                    }
                }
                return true;
            }
            if (this.tab == Tab.SEARCH && this.search.mouseClicked(mx, my, click.button())) {
                return true;
            }
            if (this.tab == Tab.LINKS) {
                if (this.linkField.mouseClicked(mx, my, click.button())) {
                    return true;
                }
                float fieldWidth = W - PAD * 2.0f - 92.0f;
                float bx = x + PAD + fieldWidth + 8.0f;
                float bw = W - PAD * 2.0f - fieldWidth - 8.0f;
                if (mx >= bx && mx <= bx + bw && my >= y + 74.0f && my <= y + 74.0f + FIELD_H) {
                    this.addCustomLink();
                    return true;
                }
            }
            int row = this.rowIndexAt(mx, my);
            if (row >= 0) {
                List<MusicTrack> list = this.rows();
                MusicTrack track = list.get(row);
                float left = x + PAD;
                float width = W - PAD * 2.0f;
                if (click.button() == 1) {
                    return this.handleRowContext(list, row);
                }
                if (this.tab == Tab.PLAYLISTS) {
                    for (int slot = 0; slot < 3; ++slot) {
                        float iconX = this.playlistRowIconX(left, width, slot);
                        if (mx >= iconX - 3.0f && mx <= iconX + 13.0f) {
                            int playlist = this.playlists.selectedIndex();
                            if (slot == 0) {
                                if (this.playlists.move(playlist, row, row - 1)) {
                                    Sounds.play("select_category");
                                    this.toast("Трек поднят выше");
                                }
                            }
                            else if (slot == 1) {
                                if (this.playlists.move(playlist, row, row + 1)) {
                                    Sounds.play("select_category");
                                    this.toast("Трек опущен ниже");
                                }
                            }
                            else if (this.playlists.removeTrack(playlist, row)) {
                                Sounds.play("gui_close");
                                this.toast("«" + track.title() + "» убран из плейлиста");
                            }
                            return true;
                        }
                    }
                }
                float bookmarkX = left + width - 22.0f;
                if (mx >= bookmarkX - 2.0f && mx <= bookmarkX + 20.0f) {
                    this.library.toggleFavorite(track);
                    Sounds.play(this.library.isFavorite(track) ? "gui_open" : "gui_close");
                    this.toast(this.library.isFavorite(track) ? "Добавлено в избранное" : "Убрано из избранного");
                    return true;
                }
                Sounds.play("module_settings_open");
                this.playFrom(list, row);
                return true;
            }
            if (this.handlePlaylistChipClick(mx, my, x, y)) {
                return true;
            }
            if (this.handlePresetChipClick(mx, my, x, y)) {
                return true;
            }
            if (this.handleEqualizerClick(mx, my)) {
                return true;
            }
            if (this.handleTransportClick(mx, my, x, y)) {
                return true;
            }
            if (this.mouseOverVolume(mx, my, x, y)) {
                this.draggingVolume = true;
                this.updateVolumeFromMouse(mx, x);
                return true;
            }
            if (this.handleChipClick(mx, my, x, y)) {
                return true;
            }
            float visualizerX = x + W - PAD - 24.0f - 118.0f;
            if (mx >= visualizerX && mx <= visualizerX + 118.0f && my >= y + 12.0f && my <= y + 38.0f) {
                MusicPlayerModule module = ModuleManager.get().get(MusicPlayerModule.class);
                if (module != null) {
                    module.cycleVisualizer();
                    this.toast("Визуализатор: " + MusicPlayerModule.VISUALIZER_OPTIONS[module.visualizerIndex()]);
                }
                return true;
            }
            if (this.handleModeClick(mx, my, x, y)) {
                return true;
            }
            float barX = x + PAD;
            float barY = y + H - 14.0f;
            float barWidth = W - PAD * 2.0f;
            if (my >= barY - 8.0f && my <= barY + 8.0f && mx >= barX && mx <= barX + barWidth) {
                MusicEngine.get().restart();
                Sounds.play("module_settings_open");
                return true;
            }
            float scrollbarX = x + PAD + (W - PAD * 2.0f) - 3.0f;
            if (mx >= scrollbarX - 3.0f && mx <= scrollbarX + 6.0f && this.maxScroll() > 0.0f) {
                this.draggingScroll = true;
                this.updateScrollFromMouse(my);
                return true;
            }
            this.search.blur();
            this.linkField.blur();
            return true;
        }
        this.close();
        return true;
    }

    private boolean handleTransportClick(float mx, float my, float x, float y) {
        float top = y + H - 58.0f;
        float volumeX = x + W - PAD - VOLUME_W;
        float transportRight = volumeX - 14.0f;
        float transportLeft = transportRight - (BUTTON * 3.0f + 16.0f);
        for (int i = 0; i < 3; ++i) {
            float bx = transportLeft + (float) i * (BUTTON + 8.0f);
            float by = top + 9.0f;
            if (mx < bx || mx > bx + BUTTON || my < by || my > by + BUTTON) {
                continue;
            }
            MusicEngine engine = MusicEngine.get();
            if (i == 0) {
                engine.previous();
            }
            else if (i == 1) {
                engine.togglePause();
            }
            else {
                engine.next();
            }
            Sounds.play("module_settings_open");
            return true;
        }
        return false;
    }

    private boolean mouseOverVolume(float mx, float my, float x, float y) {
        float top = y + H - 58.0f;
        float volumeX = x + W - PAD - VOLUME_W;
        return mx >= volumeX - 6.0f && mx <= volumeX + VOLUME_W + 6.0f && my >= top + 12.0f && my <= top + 34.0f;
    }

    private boolean mouseInsidePanel(float mx, float my) {
        float x = MusicPlayerScreen.panelX();
        float y = MusicPlayerScreen.panelY();
        return mx >= x && mx <= x + W && my >= y && my <= y + H;
    }

    @Override
    public boolean mouseReleased(Click click) {
        this.draggingScroll = false;
        this.draggingVolume = false;
        this.draggingBand = -1;
        this.search.mouseReleased(click.button());
        this.linkField.mouseReleased(click.button());
        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        float mx = Position.mouseX();
        float my = Position.mouseY();
        float x = MusicPlayerScreen.panelX();
        float y = MusicPlayerScreen.panelY();
        if (this.mouseOverVolume(mx, my, x, y)) {
            this.addVolume((float) verticalAmount * 0.04f);
            return true;
        }
        if (this.mouseInsidePanel(mx, my)) {
            this.scrollTarget = Math.max(0.0f, Math.min(this.maxScroll(), this.scrollTarget - (float) verticalAmount * 26.0f));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean charTyped(CharInput input) {
        if (this.tab == Tab.SEARCH && this.search.charTyped(input)) {
            this.lastTypeNs = System.currentTimeMillis();
            return true;
        }
        if (this.tab == Tab.LINKS && this.linkField.charTyped(input)) {
            return true;
        }
        return super.charTyped(input);
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        if (this.tab == Tab.SEARCH && this.search.keyPressed(input)) {
            if (input.key() == 257 || input.key() == 335) {
                this.lastTypeNs = 0L;
            }
            return true;
        }
        if (this.tab == Tab.LINKS) {
            if (this.linkField.keyPressed(input)) {
                if (input.key() == 257 || input.key() == 335) {
                    this.addCustomLink();
                }
                return true;
            }
        }
        switch (input.key()) {
            case 82: {
                this.cycleMode(0);
                return true;
            }
            case 83: {
                this.cycleMode(1);
                return true;
            }
            case 78: {
                this.cycleMode(2);
                return true;
            }
            case 70: {
                if (this.tab != Tab.SEARCH) {
                    this.search.focus();
                    this.lastTypeNs = System.currentTimeMillis();
                    if (!this.popularLoaded) {
                        this.publishPopular();
                    }
                    this.tab = Tab.SEARCH;
                    this.scroll = 0.0f;
                    this.scrollTarget = 0.0f;
                }
                this.search.focus();
                return true;
            }
            case 69: {
                if (this.tab != Tab.EQUALIZER) {
                    this.tab = Tab.EQUALIZER;
                    this.equalizerAppear = 0.0f;
                    this.scroll = 0.0f;
                    this.scrollTarget = 0.0f;
                    Sounds.play("select_category");
                    this.toast("Эквалайзер: 10 полос, пресеты — сверху");
                }
                return true;
            }
            case 80: {
                if (this.tab != Tab.PLAYLISTS) {
                    this.tab = Tab.PLAYLISTS;
                    this.scroll = 0.0f;
                    this.scrollTarget = 0.0f;
                    Sounds.play("select_category");
                    this.toast("Плейлисты: «+ Создать», добавление — правой кнопкой");
                }
                return true;
            }
            case 49:
            case 50:
            case 51:
            case 52:
            case 53: {
                if (this.tab != Tab.EQUALIZER) {
                    break;
                }
                this.onPresetChip(input.key() - 48);
                return true;
            }
            case 48: {
                if (this.tab != Tab.EQUALIZER) {
                    break;
                }
                this.onPresetChip(this.presetChipCount() - 1);
                return true;
            }
            case 256: {
                this.close();
                return true;
            }
            default:
                break;
        }
        return super.keyPressed(input);
    }

    /** Открыть плеер сразу на вкладке поиска (горячая клавиша быстрого поиска). */
    public void focusSearchTab() {
        this.tab = Tab.SEARCH;
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
        this.search.focus();
        this.lastTypeNs = System.currentTimeMillis();
        if (!this.popularLoaded) {
            this.publishPopular();
        }
    }

    /** Открыть плеер сразу на вкладке эквалайзера (идея №5). */
    public void focusEqualizerTab() {
        this.tab = Tab.EQUALIZER;
        this.equalizerAppear = 0.0f;
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
        this.search.blur();
        this.linkField.blur();
    }

    /** Открыть плеер сразу на вкладке плейлистов (идея №3). */
    public void focusPlaylistsTab() {
        this.tab = Tab.PLAYLISTS;
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
        this.search.blur();
        this.linkField.blur();
    }

    // ------------------------------------------------------------------ actions

    private void playFrom(List<MusicTrack> list, int index) {
        if (index < 0 || index >= list.size()) {
            return;
        }
        MusicTrack track = list.get(index);
        MusicEngine engine = MusicEngine.get();
        MusicEngine.State state = engine.state();
        MusicTrack current = engine.current();
        if (current != null && current.key().equals(track.key()) && state != MusicEngine.State.IDLE) {
            engine.togglePause();
        }
        else {
            engine.play(new ArrayList<MusicTrack>(list), index);
        }
        this.library.pushRecent(track);
    }

    private void addVolume(float delta) {
        rtx.byazen.api.modules.impl.Interface.MusicPlayerModule module =
                rtx.byazen.api.modules.ModuleManager.get().get(rtx.byazen.api.modules.impl.Interface.MusicPlayerModule.class);
        float current = module == null ? MusicEngine.get().volume() : module.volume();
        float next = Math.max(0.0f, Math.min(1.0f, current + delta));
        if (module != null) {
            module.setVolume(next);
        }
        MusicEngine.get().setVolume(next);
    }

    private void updateVolumeFromMouse(float mx, float x) {
        float volumeX = x + W - PAD - VOLUME_W;
        float progress = (mx - volumeX) / VOLUME_W;
        rtx.byazen.api.modules.impl.Interface.MusicPlayerModule module =
                rtx.byazen.api.modules.ModuleManager.get().get(rtx.byazen.api.modules.impl.Interface.MusicPlayerModule.class);
        float next = Math.max(0.0f, Math.min(1.0f, progress));
        if (module != null) {
            module.setVolume(next);
        }
        MusicEngine.get().setVolume(next);
    }

    private void updateScrollFromMouse(float my) {
        float top = this.listTop();
        float height = this.listHeight();
        float total = (float) this.rows().size() * ROW_STEP;
        if (total <= height) {
            return;
        }
        float thumbHeight = Math.max(24.0f, height * height / total);
        float relative = Math.max(0.0f, Math.min(1.0f, (my - top - thumbHeight * 0.5f) / Math.max(1.0f, height - thumbHeight)));
        this.scrollTarget = relative * this.maxScroll();
        this.scroll = this.scrollTarget;
    }

    private void addCustomLink() {
        String url = this.linkField.getText().trim();
        if (url.isBlank()) {
            this.toast("Вставьте ссылку на поток");
            return;
        }
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }
        String lower = url.toLowerCase(java.util.Locale.ROOT);
        if (lower.endsWith(".m3u") || lower.endsWith(".m3u8") || lower.contains(".m3u?")) {
            // Импорт чужого плейлиста одним файлом (идея №20).
            String playlistUrl = url;
            this.toast("Загружаем плейлист…");
            MusicHttp.submit(() -> {
                List<MusicTrack> playlist = MusicHttp.fetchM3U(playlistUrl);
                int added = this.library.importPlaylist(playlist);
                this.toast(added > 0 ? "Импортировано станций: " + added : "В плейлисте не нашлось ссылок");
            });
            this.linkField.setText("");
            return;
        }
        this.library.addCustom(url, MusicTrack.describeUrl(url));
        this.linkField.setText("");
        Sounds.play("gui_open");
        this.toast("Ссылка добавлена");
    }

    private void publishPopular() {
        this.popularLoaded = true;
        this.searching = true;
        MusicHttp.submit(() -> {
            ArrayList<MusicTrack> merged = new ArrayList<MusicTrack>(MusicHttp.trendingTracks(18));
            merged.addAll(MusicHttp.topStations(14));
            if (this.searchedQuery.isBlank()) {
                this.results = merged;
            }
            this.searching = false;
        });
    }

    private void startSearch(String query) {
        this.searchedQuery = query;
        this.searching = true;
        this.scroll = 0.0f;
        this.scrollTarget = 0.0f;
        MusicHttp.submit(() -> {
            ArrayList<MusicTrack> merged = new ArrayList<MusicTrack>(MusicHttp.searchTracks(query, 22));
            merged.addAll(MusicHttp.searchStations(query, 22));
            this.results = merged;
            this.searching = false;
        });
    }

    private void toast(String message) {
        this.toast = message;
        this.toastTime = 1.6f;
    }

    /** Debounced search-as-you-type. */
    private void tickSearch() {
        if (this.tab != Tab.SEARCH || this.searching) {
            return;
        }
        String query = this.search.getText().trim();
        if (query.equals(this.searchedQuery)) {
            return;
        }
        if (query.isBlank()) {
            this.searchedQuery = "";
            this.publishPopular();
            return;
        }
        if (System.currentTimeMillis() - this.lastTypeNs < 420L) {
            return;
        }
        this.startSearch(query);
    }

    @Override
    public void tick() {
        super.tick();
        this.tickSearch();
        if (this.draggingVolume) {
            this.updateVolumeFromMouse(Position.mouseX(), MusicPlayerScreen.panelX());
        }
        if (this.draggingScroll) {
            this.updateScrollFromMouse(Position.mouseY());
        }
        if (this.draggingBand >= 0 && this.tab == Tab.EQUALIZER) {
            Equalizer.get().setGain(this.draggingBand, this.bandYToValue(Position.mouseY()));
        }
        List<MusicTrack> list = this.rows();
        MusicTrack current = MusicEngine.get().current();
        if (current != null && this.tab == Tab.LIBRARY && list.size() > 6) {
            for (int i = 0; i < list.size(); ++i) {
                if (!list.get(i).key().equals(current.key())) {
                    continue;
                }
                float rowY = (float) i * ROW_STEP;
                if (rowY < this.scrollTarget || rowY > this.scrollTarget + this.listHeight() - 30.0f) {
                    this.scrollTarget = Math.max(0.0f, Math.min(this.maxScroll(), rowY - 20.0f));
                }
                break;
            }
        }
    }

    // ------------------------------------------------------------------ helpers

    private float[] hoverValues = new float[64];

    private void rowHoverAnim(int index, float target) {
        int slot = Math.floorMod(index, this.hoverValues.length);
        this.hoverValues[slot] += (target - this.hoverValues[slot]) * Math.min(1.0f, this.delta * 14.0f);
    }

    private float rowHover(int slot) {
        return this.hoverValues[slot];
    }

    private static void text(String font, String value, float x, float centerY, float size, int color) {
        Render2D.msdfText(font, value, x, centerY - 0.6f * size, size, color);
    }

    private static int rgba(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        if (a <= 0) {
            return 0;
        }
        return new Color(r, g, b, a).getRGB();
    }

    private static String time(long millis) {
        long total = Math.max(0L, millis) / 1000L;
        long minutes = total / 60L;
        long seconds = total % 60L;
        return minutes + ":" + (seconds < 10L ? "0" : "") + seconds;
    }

    /** Stations shown before the first search of this session. */
    public static List<MusicTrack> builtInStations() {
        return RadioCatalog.stations();
    }
}
