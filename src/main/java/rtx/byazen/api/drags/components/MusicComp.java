package rtx.byazen.api.drags.components;

import java.awt.Color;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.drags.Position;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.MusicPlayerModule;
import rtx.byazen.api.music.MusicCovers;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.api.music.MusicTrack;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.theme.AccentGradient;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.render.fonts.Fonts;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Compact music widget: cover art, marquee title, live spectrum, transport controls and a volume
 * readout. Rendered smoothly (rounded vector shapes, anti-aliased MSDF text, animated bars) - no
 * pixel art anywhere.
 */
public final class MusicComp
extends Draggable {

    private static final float WIDTH = 172.0f;
    private static final float HEIGHT = 50.0f;
    private static final float PAD = 5.0f;
    private static final float COVER = 40.0f;
    private static final float TEXT_X = 51.0f;
    private static final String FONT_TITLE = "montserrat-semibold";
    private static final String FONT_TEXT = "montserrat-medium";
    private static final float TITLE_SIZE = 7.6f;
    private static final float SUBTITLE_SIZE = 6.0f;
    private static final float TITLE_Y = 13.5f;
    private static final float SUBTITLE_Y = 25.0f;
    private static final int BARS = 16;
    private static final float BAR_AREA_TOP = 30.0f;
    private static final float BAR_AREA_HEIGHT = 6.5f;
    private static final float BUTTON = 14.0f;
    private static final float BUTTON_Y = 38.0f;

    private final float[] bars = new float[BARS];
    private final float[] weights = new float[BARS];
    private float alpha;
    private float hover;
    private float volumeShow;
    private float titleScroll;
    private long lastFrameNs;

    public MusicComp() {
        super("music", 5.0f, 310.0f);
        for (int i = 0; i < BARS; ++i) {
            float t = (float) i / (float) Math.max(1, BARS - 1);
            this.weights[i] = 0.35f + 0.65f * (0.55f + 0.45f * (float) Math.sin(t * 5.1f));
        }
    }

    private static MusicPlayerModule module() {
        return ModuleManager.get().get(MusicPlayerModule.class);
    }

    @Override
    public String displayName() {
        return "Музыка";
    }

    @Override
    public float width() {
        return WIDTH;
    }

    @Override
    public float height() {
        return HEIGHT;
    }

    @Override
    public boolean isInteractive() {
        MusicPlayerModule module = MusicComp.module();
        return module != null && module.isEnabled();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> list = super.buildHudSettings();
        MusicPlayerModule module = MusicComp.module();
        if (module != null) {
            list.add(new rtx.byazen.api.ui.settings.impl.SliderSetting(module.volume));
            list.add(new rtx.byazen.api.ui.settings.impl.BoolSetting(module.showWidget));
        }
        return list;
    }

    private boolean active() {
        MusicPlayerModule module = MusicComp.module();
        return module != null && module.isEnabled();
    }

    // ------------------------------------------------------------------ interaction

    /** Index of the transport button under the cursor: 0 - previous, 1 - play/pause, 2 - next, otherwise -1. */
    public static int buttonAt(float mouseX, float mouseY, float x, float y) {
        float baseX = x + TEXT_X;
        float baseY = y + BUTTON_Y;
        for (int i = 0; i < 3; ++i) {
            float bx = baseX + (float) i * (BUTTON + 3.0f);
            if (mouseX >= bx && mouseX <= bx + BUTTON && mouseY >= baseY && mouseY <= baseY + BUTTON) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Handles a click on the widget; returns true when the click was consumed.
     *
     * @param onlyButtons in the interface editor only the small transport buttons react, so the rest
     *                    of the widget stays draggable
     */
    public boolean click(int button, boolean onlyButtons) {
        if (!this.active()) {
            return false;
        }
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = this.getX();
        float y = this.getY();
        if (!(mouseX >= x) || !(mouseX <= x + WIDTH) || !(mouseY >= y) || !(mouseY <= y + HEIGHT)) {
            return false;
        }
        MusicEngine engine = MusicEngine.get();
        int index = MusicComp.buttonAt(mouseX, mouseY, x, y);
        switch (index) {
            case 0: {
                engine.previous();
                return true;
            }
            case 1: {
                engine.togglePause();
                return true;
            }
            case 2: {
                engine.next();
                return true;
            }
            default:
                break;
        }
        if (onlyButtons) {
            return false;
        }
        MusicPlayerModule module = MusicComp.module();
        if (module != null) {
            module.openScreen();
            return true;
        }
        return false;
    }

    /** Scroll wheel over the widget changes the volume. */
    public boolean scroll(double amount) {
        if (!this.active()) {
            return false;
        }
        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        float x = this.getX();
        float y = this.getY();
        if (mouseX < x || mouseX > x + WIDTH || mouseY < y || mouseY > y + HEIGHT) {
            return false;
        }
        MusicPlayerModule module = MusicComp.module();
        if (module == null) {
            return false;
        }
        module.setVolume(module.volume() + (float) amount * 0.05f);
        this.volumeShow = 1.0f;
        return true;
    }

    // ------------------------------------------------------------------ rendering

    @Override
    protected void render(DrawContext drawContext) {
        MusicPlayerModule module = MusicComp.module();
        if (module == null) {
            return;
        }
        float delta = this.deltaSeconds();
        float target = module.isEnabled() ? 1.0f : 0.0f;
        this.alpha += (target - this.alpha) * Math.min(1.0f, delta * 9.0f);
        if (this.alpha <= 0.01f) {
            return;
        }
        MusicEngine engine = MusicEngine.get();
        MusicTrack track = engine.current() != null ? engine.current() : module.previewTrack();
        float x = this.getX();
        float y = this.getY();
        float a = this.alpha;

        float mouseX = Position.mouseX();
        float mouseY = Position.mouseY();
        boolean hovered = mouseX >= x && mouseX <= x + WIDTH && mouseY >= y && mouseY <= y + HEIGHT;
        this.hover += ((hovered ? 1.0f : 0.0f) - this.hover) * Math.min(1.0f, delta * 12.0f);
        this.volumeShow = Math.max(0.0f, this.volumeShow - delta * 0.6f);

        Render2D.beginFrame(drawContext);
        RectUtil.drawClientRect(x, y, WIDTH, HEIGHT, 9.0f, a);
        if (this.hover > 0.01f) {
            Render2D.outline(x, y, WIDTH, HEIGHT, 9.0f, 0.8f, ClientAccent.accentSoft(46.0f * a * this.hover));
        }
        this.drawCover(drawContext, x + PAD, y + PAD, a, track);
        if (track == null) {
            this.drawIdleText(x, y, a);
        }
        else {
            this.drawTexts(drawContext, x, y, a, track, engine, delta);
            this.drawSpectrum(x, y, a, engine, delta);
            this.drawControls(x, y, a, engine, mouseX, mouseY);
        }
        this.drawProgress(x, y, a, track, engine, delta);
        Render2D.flush();
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastFrameNs == 0L ? 0.016f : (float) (now - this.lastFrameNs) / 1.0E9f;
        this.lastFrameNs = now;
        return Math.max(0.001f, Math.min(0.1f, delta));
    }

    private void drawCover(DrawContext drawContext, float x, float y, float alpha, MusicTrack track) {
        String texture = track == null ? null : MusicCovers.textureFor(track.coverUrl());
        if (texture != null && Render2D.imageReady(texture)) {
            Render2D.image(texture, x, y, COVER, 8.0f, MusicComp.color(255, 255, 255, 255.0f * alpha));
            return;
        }
        int top = ClientAccent.accentSoft(70.0f * alpha);
        int bottom = ClientAccent.accentSoft(18.0f * alpha);
        Render2D.rect(x, y, COVER, COVER, 8.0f, 8.0f, 8.0f, 8.0f, top);
        Render2D.rect(x, y + COVER * 0.45f, COVER, COVER * 0.55f, 0.0f, 0.0f, 8.0f, 8.0f, bottom);
        // smooth music note
        float noteX = x + COVER * 0.38f;
        float noteY = y + COVER * 0.26f;
        float noteW = COVER * 0.26f;
        int ink = MusicComp.color(255, 255, 255, 210.0f * alpha);
        Render2D.rect(noteX, noteY, 1.4f, COVER * 0.42f, 0.7f, ink);
        Render2D.rect(noteX + noteW, noteY + 1.5f, 1.4f, COVER * 0.38f, 0.7f, ink);
        Render2D.rect(noteX, noteY, noteW + 1.4f, 1.6f, 0.8f, ink);
        Render2D.circle(noteX - 1.6f, noteY + COVER * 0.42f, 2.4f, ink);
        Render2D.circle(noteX + noteW - 0.2f, noteY + COVER * 0.38f, 2.4f, ink);
    }

    private void drawIdleText(float x, float y, float alpha) {
        float cx = x + TEXT_X + 4.0f;
        Fonts font = Fonts.MONTSERRAT_SEMIBOLD;
        font.draw("Музыка", cx, y + MusicComp.textY(TITLE_Y, TITLE_SIZE), TITLE_SIZE, MusicComp.color(255, 255, 255, 235.0f * alpha));
        Fonts.MONTSERRAT_MEDIUM.draw("Плеер не активен", cx, y + MusicComp.textY(SUBTITLE_Y, SUBTITLE_SIZE), SUBTITLE_SIZE, MusicComp.color(200, 205, 215, 150.0f * alpha));
        String hint = "Нажмите, чтобы открыть библиотеку";
        Fonts.MONTSERRAT_MEDIUM.draw(hint, cx, y + MusicComp.textY(36.0f, 5.6f), 5.6f, MusicComp.color(160, 168, 180, 120.0f * alpha));
    }

    private void drawTexts(DrawContext drawContext, float x, float y, float alpha, MusicTrack track, MusicEngine engine, float delta) {
        float textX = x + TEXT_X;
        float available = WIDTH - TEXT_X - PAD - 2.0f;
        String title = track.title();
        float titleWidth = Render2D.msdfWidth(FONT_TITLE, title, TITLE_SIZE);
        float titleDrawX = textX;
        if (titleWidth > available) {
            this.titleScroll += delta * 14.0f;
            float cycle = titleWidth + 26.0f;
            float offset = this.titleScroll % cycle;
            titleDrawX = textX - offset;
        }
        else {
            this.titleScroll = 0.0f;
        }
        Render2D.pushScissor(drawContext, textX, y + 5.0f, available, 14.0f);
        Render2D.msdfText(FONT_TITLE, title, titleDrawX, y + MusicComp.textY(TITLE_Y, TITLE_SIZE), TITLE_SIZE, MusicComp.color(255, 255, 255, 240.0f * alpha));
        if (titleWidth > available) {
            Render2D.msdfText(FONT_TITLE, title, titleDrawX + titleWidth + 26.0f, y + MusicComp.textY(TITLE_Y, TITLE_SIZE), TITLE_SIZE, MusicComp.color(255, 255, 255, 240.0f * alpha));
        }
        Render2D.popScissor(drawContext);

        String subtitle = this.statusLine(track, engine);
        Render2D.pushScissor(drawContext, textX, y + 18.0f, available, 11.0f);
        Render2D.msdfText(FONT_TEXT, subtitle, textX, y + MusicComp.textY(SUBTITLE_Y, SUBTITLE_SIZE), SUBTITLE_SIZE, MusicComp.color(208, 213, 224, 170.0f * alpha));
        Render2D.popScissor(drawContext);
    }

    private String statusLine(MusicTrack track, MusicEngine engine) {
        if (engine.state() == MusicEngine.State.CONNECTING) {
            return "Подключение…";
        }
        if (engine.state() == MusicEngine.State.ERROR) {
            return "Ошибка: " + engine.detail();
        }
        if (engine.isPaused()) {
            return "Пауза" + (track.subtitle().isBlank() ? "" : " • " + track.subtitle());
        }
        String base = track.subtitle().isBlank() ? track.badge() : track.subtitle();
        if (track.isRadio()) {
            return "В эфире" + (base.isBlank() ? "" : " • " + base);
        }
        return base;
    }

    private void drawSpectrum(float x, float y, float alpha, MusicEngine engine, float delta) {
        float areaX = x + TEXT_X;
        float areaWidth = WIDTH - TEXT_X - PAD - 30.0f;
        float barWidth = (areaWidth - (float) (BARS - 1) * 1.6f) / (float) BARS;
        float level = engine.state() == MusicEngine.State.PLAYING ? engine.level() : 0.0f;
        float phase = (float) (System.nanoTime() % 4000000000L) / 4.0E9f * 6.2831855f;
        for (int i = 0; i < BARS; ++i) {
            float wave = 0.55f + 0.45f * (float) Math.sin(phase * 1.6f + (float) i * 0.75f);
            float target = Math.min(1.0f, level * this.weights[i] * (0.55f + 0.9f * wave));
            this.bars[i] += (target - this.bars[i]) * Math.min(1.0f, delta * 11.0f);
            float height = BAR_AREA_HEIGHT * Math.max(0.06f, this.bars[i]);
            float bx = areaX + (float) i * (barWidth + 1.6f);
            float by = y + BAR_AREA_TOP + (BAR_AREA_HEIGHT - height);
            int color = ClientAccent.accentSoft((90.0f + 150.0f * this.bars[i]) * alpha);
            Render2D.rect(bx, by, barWidth, height, barWidth * 0.5f, color);
        }
    }

    private void drawControls(float x, float y, float alpha, MusicEngine engine, float mouseX, float mouseY) {
        float baseX = x + TEXT_X;
        float baseY = y + BUTTON_Y;
        int hovered = MusicComp.buttonAt(mouseX, mouseY, x, y);
        for (int i = 0; i < 3; ++i) {
            float bx = baseX + (float) i * (BUTTON + 3.0f);
            boolean hot = hovered == i;
            int fill = hot ? ClientAccent.accentSoft(52.0f * alpha) : MusicComp.color(12, 14, 18, 110.0f * alpha);
            Render2D.rect(bx, baseY, BUTTON, BUTTON, 4.5f, fill);
            int ink = hot ? ClientAccent.accentBright(240.0f * alpha) : MusicComp.color(226, 230, 238, 190.0f * alpha);
            switch (i) {
                case 0: {
                    MusicComp.drawBar(bx + 3.6f, baseY + 4.2f, 5.6f, ink);
                    MusicComp.drawTriangle(bx + 5.4f, baseY + 4.2f, 5.6f, false, ink);
                    break;
                }
                case 1: {
                    if (engine.state() == MusicEngine.State.PLAYING) {
                        MusicComp.drawBar(bx + 4.6f, baseY + 4.0f, 6.0f, ink);
                        MusicComp.drawBar(bx + 8.0f, baseY + 4.0f, 6.0f, ink);
                    }
                    else {
                        MusicComp.drawTriangle(bx + 4.8f, baseY + 4.0f, 6.2f, false, ink);
                    }
                    break;
                }
                default: {
                    MusicComp.drawTriangle(bx + 4.0f, baseY + 4.2f, 5.6f, false, ink);
                    MusicComp.drawBar(bx + 9.2f, baseY + 4.2f, 5.6f, ink);
                    break;
                }
            }
        }
        if (this.volumeShow > 0.01f) {
            MusicPlayerModule module = MusicComp.module();
            int percent = module == null ? 0 : Math.round(module.volume() * 100.0f);
            String label = "Громкость " + percent + "%";
            float width = Render2D.msdfWidth(FONT_TEXT, label, 5.8f);
            float vx = x + WIDTH - PAD - width;
            Render2D.msdfText(FONT_TEXT, label, vx, y + MusicComp.textY(BUTTON_Y + 7.0f, 5.8f), 5.8f,
                    ClientAccent.accentSoft(230.0f * alpha * Math.min(1.0f, this.volumeShow * 2.0f)));
        }
    }

    private void drawProgress(float x, float y, float alpha, MusicTrack track, MusicEngine engine, float delta) {
        float barX = x + PAD;
        float barY = y + HEIGHT - 4.6f;
        float barWidth = WIDTH - PAD * 2.0f;
        Render2D.rect(barX, barY, barWidth, 2.0f, 1.0f, MusicComp.color(255, 255, 255, 34.0f * alpha));
        if (track == null) {
            return;
        }
        if (track.isLive()) {
            float phase = (float) (System.nanoTime() % 3000000000L) / 3.0E9f;
            float head = barWidth * (0.15f + 0.7f * (0.5f + 0.5f * (float) Math.sin(phase * 6.2831855f)));
            AccentGradient.fillHorizontal(barX, barY, barWidth, 2.0f, 1.0f, 150.0f * alpha);
            Render2D.rect(barX + head, barY - 0.6f, 6.0f, 3.2f, 1.6f, ClientAccent.accentBright(220.0f * alpha));
            return;
        }
        float progress = Math.max(0.0f, Math.min(1.0f, engine.progress()));
        if (progress <= 0.001f) {
            return;
        }
        AccentGradient.fillHorizontal(barX, barY, barWidth * progress, 2.0f, 1.0f, 210.0f * alpha);
        Render2D.circle(barX + barWidth * progress, barY + 1.0f, 2.2f, ClientAccent.accentBright(230.0f * alpha));
    }

    /** Play triangle built from stacked rounded bars - crisp at any GUI scale, no bitmap icons. */
    private static void drawTriangle(float x, float y, float size, boolean flip, int color) {
        int steps = 6;
        float step = size / (float) steps;
        for (int i = 0; i < steps; ++i) {
            float t = (float) i / (float) (steps - 1);
            float width = size * (flip ? t : 1.0f - t);
            float px = flip ? x + size - width : x;
            Render2D.rect(px, y + (float) i * step, Math.max(0.6f, width), step + 0.25f, step * 0.35f, color);
        }
    }

    private static void drawBar(float x, float y, float height, int color) {
        Render2D.rect(x, y, 2.0f, height, 1.0f, color);
    }

    private static float textY(float centerY, float size) {
        return centerY - 0.6f * size;
    }

    private static int color(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return new Color(r, g, b, a).getRGB();
    }

}
