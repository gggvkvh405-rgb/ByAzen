package rtx.byazen.api.drags.components;

import java.awt.Color;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.drags.Draggable;
import rtx.byazen.api.modules.ModuleManager;
import rtx.byazen.api.modules.impl.Interface.MusicPlayerModule;
import rtx.byazen.api.music.MusicCovers;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.api.music.MusicTrack;
import rtx.byazen.api.ui.settings.Setting;
import rtx.byazen.api.ui.settings.impl.BoolSetting;
import rtx.byazen.api.ui.settings.impl.SelectSetting;
import rtx.byazen.api.ui.theme.ClientAccent;
import rtx.byazen.utils.render.others.RectUtil;
import rtx.byazen.utils.render.render2d.Render2D;
import rtx.byazen.utils.lang.Lang;

/**
 * Виджет-плашка «сейчас играет» с анимированным градиентом под цвет обложки трека
 * (идея №26 из IDEAS.md). Волна, обложка-миниатюра, бегущая строка названия и полоса прогресса.
 */
public final class MusicPillComp
extends Draggable {

    private static final String FONT_TITLE = "montserrat-semibold";
    private static final String FONT_TEXT = "montserrat-medium";
    private static final float TITLE_SIZE = 7.4f;
    private static final float SUB_SIZE = 5.8f;
    private static final float PAD_X = 7.0f;
    private static final float HEIGHT = 22.0f;
    private static final float COVER = 14.0f;
    private static final float MIN_WIDTH = 96.0f;
    private static final float MAX_WIDTH = 260.0f;
    private static final int FALLBACK_A = 0x6A5CFF;
    private static final int FALLBACK_B = 0x1B1D29;

    private float currentWidth = MIN_WIDTH;
    private float currentHeight = HEIGHT;
    private float alpha;
    private float colorShift;
    private float marquee;
    private String lastKey = "";
    private long lastNs;

    public MusicPillComp() {
        super("music_pill", 5.0f, 96.0f);
    }

    private static MusicPlayerModule module() {
        return ModuleManager.get().get(MusicPlayerModule.class);
    }

    private static MusicEngine engine() {
        return MusicEngine.get();
    }

    @Override
    public String displayName() {
        return Lang.t("Музыкальная плашка", "Music pill");
    }

    @Override
    public float width() {
        return this.currentWidth;
    }

    @Override
    public float height() {
        return this.currentHeight;
    }

    @Override
    public boolean isInteractive() {
        MusicPlayerModule module = MusicPillComp.module();
        return module != null && module.isEnabled() && module.pillWidget.getValue();
    }

    @Override
    protected List<Setting> buildHudSettings() {
        List<Setting> settings = super.buildHudSettings();
        MusicPlayerModule module = MusicPillComp.module();
        if (module != null) {
            settings.add(new BoolSetting(module.pillWidget));
            settings.add(new SelectSetting(module.pillStyle));
            settings.add(new BoolSetting(module.pillCover));
            settings.add(new BoolSetting(module.pillProgress));
            settings.add(new BoolSetting(module.pillBars));
        }
        return settings;
    }

    @Override
    protected void render(DrawContext drawContext) {
        MusicPlayerModule module = MusicPillComp.module();
        if (module == null) {
            return;
        }
        boolean visible = module.isEnabled() && module.pillWidget.getValue();
        float delta = this.deltaSeconds();
        this.alpha += ((visible ? 1.0f : 0.0f) - this.alpha) * Math.min(1.0f, delta * 8.0f);
        if (this.alpha <= 0.01f) {
            return;
        }
        MusicEngine engine = MusicPillComp.engine();
        MusicTrack track = engine.current();
        if (track == null) {
            return;
        }
        String title = track.title() == null ? "" : track.title();
        String subtitle = track.subtitle() == null || track.subtitle().isBlank()
                ? (track.badge() == null ? "ByAzen" : track.badge())
                : track.subtitle();
        boolean playing = engine.state() == MusicEngine.State.PLAYING;

        int[] palette = "Обложка".equals(module.pillStyle.getValue())
                ? MusicCovers.paletteFor(track.coverUrl())
                : null;
        int baseA = palette == null ? MusicPillComp.themeA() : palette[0];
        int baseB = palette == null ? MusicPillComp.themeB() : palette[1];
        boolean animate = !"Тема".equals(module.pillStyle.getValue());
        if (animate) {
            this.colorShift += delta * 0.35f;
            if (this.colorShift > 1.0f) {
                this.colorShift -= 1.0f;
            }
        }

        float coverSize = module.pillCover.getValue() ? COVER : 0.0f;
        float coverGap = coverSize > 0.0f ? coverSize + 6.0f : 0.0f;
        float textWidth = Math.max(
                Render2D.msdfWidth(FONT_TITLE, title, TITLE_SIZE),
                Render2D.msdfWidth(FONT_TEXT, subtitle, SUB_SIZE));
        float width = Math.min(MAX_WIDTH, Math.max(MIN_WIDTH, textWidth + coverGap + PAD_X * 2.0f + 22.0f));
        this.currentWidth = width;
        this.currentHeight = HEIGHT;

        float x = this.getX();
        float y = this.getY();
        float a = this.alpha;
        String key = track.key();
        if (!key.equals(this.lastKey)) {
            this.lastKey = key;
            this.marquee = 0.0f;
        }
        float textArea = width - coverGap - PAD_X * 2.0f - 14.0f;
        float overflow = Math.max(0.0f, Render2D.msdfWidth(FONT_TITLE, title, TITLE_SIZE) - textArea);
        if (overflow > 0.5f) {
            this.marquee += delta * 22.0f;
            if (this.marquee > overflow + 18.0f) {
                this.marquee = 0.0f;
            }
        }
        else {
            this.marquee = 0.0f;
        }

        Render2D.beginFrame(drawContext);
        // подложка
        RectUtil.drawClientRect(x, y, width, HEIGHT, HEIGHT * 0.5f, a);
        // анимированный градиент под цвет обложки
        int left = animate ? ClientAccent.mix(baseA, baseB, this.colorShift) : baseA;
        int right = animate ? ClientAccent.mix(baseB, baseA, this.colorShift) : baseB;
        float fillAlpha = 62.0f * a;
        Render2D.rect(x + 0.6f, y + 0.6f, width - 1.2f, HEIGHT - 1.2f, HEIGHT * 0.5f - 0.6f,
                ClientAccent.rgba(left, fillAlpha), ClientAccent.rgba(right, fillAlpha),
                ClientAccent.rgba(right, fillAlpha * 0.55f), ClientAccent.rgba(left, fillAlpha * 0.55f));
        Render2D.outline(x, y, width, HEIGHT, HEIGHT * 0.5f, 0.8f, ClientAccent.rgba(right, 120.0f * a));

        float cx = x + PAD_X;
        if (coverSize > 0.0f) {
            String texture = MusicCovers.textureFor(track.coverUrl());
            if (texture != null && Render2D.imageReady(texture)) {
                Render2D.image(texture, cx, y + (HEIGHT - coverSize) * 0.5f, coverSize, coverSize, 4.0f,
                        MusicPillComp.color(255, 255, 255, 245.0f * a));
            }
            else {
                Render2D.rect(cx, y + (HEIGHT - coverSize) * 0.5f, coverSize, coverSize, 4.0f,
                        ClientAccent.rgba(right, 150.0f * a));
            }
            cx += coverGap;
        }

        // эквалайзер из плавных полос
        float barsWidth = 0.0f;
        if (module.pillBars.getValue()) {
            barsWidth = 13.0f;
            MusicPillComp.drawBars(cx, y + HEIGHT * 0.5f, barsWidth, playing ? engine.level() : 0.0f, a, playing);
            cx += barsWidth + 4.0f;
        }

        float textTop = y + 4.2f;
        float subTop = y + 12.0f;
        Render2D.pushScissor(drawContext, cx, y, Math.max(8.0f, width - (cx - x) - 8.0f), HEIGHT);
        Render2D.msdfText(FONT_TITLE, title, cx - this.marquee, textTop + 0.6f, TITLE_SIZE,
                MusicPillComp.color(255, 255, 255, 242.0f * a));
        Render2D.msdfText(FONT_TEXT, subtitle, cx, subTop, SUB_SIZE,
                MusicPillComp.color(214, 220, 232, 178.0f * a));
        Render2D.popScissor(drawContext);

        // состояние справа: прогресс или «в эфире»
        float rightX = x + width - 10.0f;
        if (track.isLive()) {
            Render2D.circle(rightX, y + HEIGHT * 0.5f, 2.2f, playing ? ClientAccent.rgba(right, 235.0f * a) : ClientAccent.rgba(right, 90.0f * a));
        }
        else if (module.pillProgress.getValue()) {
            float progress = Math.max(0.0f, Math.min(1.0f, engine.progress()));
            float barWidth = 6.0f;
            Render2D.rect(rightX - barWidth * 0.5f, y + HEIGHT - 5.4f, barWidth, 1.6f, 0.8f,
                    ClientAccent.rgba(right, 90.0f * a));
            Render2D.rect(rightX - barWidth * 0.5f, y + HEIGHT - 5.4f, barWidth * progress, 1.6f, 0.8f,
                    ClientAccent.rgba(left, 240.0f * a));
        }
        if (!playing) {
            // пауза: две вертикальные полоски
            float barH = 5.0f;
            Render2D.rect(x + width - 15.0f, y + HEIGHT * 0.5f - barH * 0.5f, 1.4f, barH, 0.7f,
                    MusicPillComp.color(236, 240, 248, 210.0f * a));
            Render2D.rect(x + width - 12.4f, y + HEIGHT * 0.5f - barH * 0.5f, 1.4f, barH, 0.7f,
                    MusicPillComp.color(236, 240, 248, 210.0f * a));
        }
        Render2D.flush();
    }

    private static void drawBars(float x, float centerY, float width, float level, float alpha, boolean playing) {
        int bars = 3;
        float barWidth = 2.4f;
        float gap = (width - (float) bars * barWidth) / (float) Math.max(1, bars - 1);
        for (int i = 0; i < bars; ++i) {
            float phase = (float) System.currentTimeMillis() / 260.0f + (float) i * 1.7f;
            float wave = (float) Math.sin((double) phase) * 0.5f + 0.5f;
            float height = playing ? 3.5f + (level * 6.0f + wave * 4.0f) : 3.5f;
            height = Math.min(11.0f, height);
            float barX = x + (float) i * (barWidth + gap);
            Render2D.rect(barX, centerY - height * 0.5f, barWidth, height, barWidth * 0.5f,
                    ClientAccent.accentBright(200.0f * alpha));
        }
    }

    private float deltaSeconds() {
        long now = System.nanoTime();
        float delta = this.lastNs == 0L ? 0.016f : (float) (now - this.lastNs) / 1.0E9f;
        this.lastNs = now;
        return Math.max(0.001f, Math.min(0.1f, delta));
    }

    private static int themeA() {
        return ClientAccent.accentOpaque();
    }

    private static int themeB() {
        int[] palette = ClientAccent.currentPalette();
        if (palette != null && palette.length > 1) {
            return palette[1] & 0xFFFFFF;
        }
        return FALLBACK_B;
    }

    private static int color(int r, int g, int b, float alpha) {
        int a = Math.max(0, Math.min(255, Math.round(alpha)));
        return new Color(r, g, b, a).getRGB();
    }
}
