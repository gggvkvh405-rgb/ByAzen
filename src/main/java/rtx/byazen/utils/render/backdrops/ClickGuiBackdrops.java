package rtx.byazen.utils.render.backdrops;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import rtx.byazen.api.modules.impl.Interface.ClickGuiBackdropsModule;
import rtx.byazen.api.music.MusicCovers;
import rtx.byazen.api.music.MusicEngine;
import rtx.byazen.api.music.MusicTrack;
import rtx.byazen.api.ui.theme.ThemeManager;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Анимированные фоны ClickGui (идея №156 из IDEAS.md).
 * <p>
 * Меню не стоит на месте: по панели медленно плывёт градиент темы, поверх поднимаются мягкие
 * частицы, а если играет музыка — фон подстраивается под обложку трека. Свечение аккуратное:
 * фон остаётся фоном, текст и карточки модулей читаются так же хорошо.
 */
public final class ClickGuiBackdrops {

    private static final int PARTICLES = 26;

    private static final float[] particleX = new float[PARTICLES];
    private static final float[] particleY = new float[PARTICLES];
    private static final float[] particleR = new float[PARTICLES];
    private static final float[] particleSpeed = new float[PARTICLES];
    private static boolean seeded;
    private static long lastFrameNs;
    private static float flow;
    private static int lastCoverColor;

    private ClickGuiBackdrops() {
    }

    private static void seed() {
        if (ClickGuiBackdrops.seeded) {
            return;
        }
        ClickGuiBackdrops.seeded = true;
        java.util.Random random = new java.util.Random(0xB7A2EFL);
        for (int i = 0; i < PARTICLES; i++) {
            particleX[i] = random.nextFloat();
            particleY[i] = random.nextFloat();
            particleR[i] = 1.2f + random.nextFloat() * 2.4f;
            particleSpeed[i] = 0.012f + random.nextFloat() * 0.03f;
        }
    }

    /** Рисует фон меню: градиент темы, частицы и оттенок обложки трека. */
    public static void render(DrawContext drawContext, float alpha, float x, float y, float width, float height) {
        if (alpha <= 0.02f || width <= 1.0f || height <= 1.0f) {
            return;
        }
        ClickGuiBackdrops.seed();
        ClickGuiBackdrops.tick();
        float strength = alpha * ClickGuiBackdropsModule.strengthFactor();
        int primary = ThemeManager.accentOpaque();
        int secondary = ThemeManager.gradientB(255.0f);
        // «дыхание» по центру: очень мягкое свечение, чтобы панель не выглядела плоской
        float breathe = 0.5f + 0.5f * (float)Math.sin(ClickGuiBackdrops.flow * 0.9f);
        if (ClickGuiBackdropsModule.coverEnabled()) {
            ClickGuiBackdrops.renderCoverTint(strength, x, y, width, height);
        }
        if (ClickGuiBackdropsModule.gradientEnabled()) {
            // мягкий градиент: сверху один оттенок темы, снизу второй
            int top = ClickGuiBackdrops.mix(primary, 0x0A0C12, 0.72f, strength * 0.55f);
            int bottom = ClickGuiBackdrops.mix(secondary, 0x06070C, 0.76f, strength * 0.5f);
            Render2D.rect(x, y, width, height, 12.0f, top, top, bottom, bottom);
            int glow = ClickGuiBackdrops.mix(primary, 0xFFFFFF, 0.25f, strength * (12.0f + 10.0f * breathe));
            Render2D.rect(x + width * 0.12f, y + height * 0.06f, width * 0.52f, height * 0.26f, 24.0f, glow);
        }
        if (ClickGuiBackdropsModule.particlesEnabled()) {
            for (int i = 0; i < PARTICLES; i++) {
                float size = particleR[i] * (0.8f + 0.4f * breathe);
                float px = x + particleX[i] * width;
                float py = y + particleY[i] * height;
                int dot = ClickGuiBackdrops.mix(secondary, 0xFFFFFF, 0.35f, strength * (26.0f + 22.0f * breathe));
                Render2D.rect(px, py, size, size, size * 0.5f, dot);
            }
        }
    }

    /** Если играет трек с обложкой — мягко подкрашиваем фон её цветом. */
    private static void renderCoverTint(float alpha, float x, float y, float width, float height) {
        try {
            MusicEngine engine = MusicEngine.get();
            if (engine == null || !engine.isPlaying()) {
                return;
            }
            MusicTrack track = engine.current();
            if (track == null || track.coverUrl() == null || track.coverUrl().isBlank()) {
                return;
            }
            int[] palette = MusicCovers.paletteFor(track.coverUrl());
            if (palette == null || palette.length == 0) {
                return;
            }
            ClickGuiBackdrops.lastCoverColor = palette[0];
            int tint = ClickGuiBackdrops.mix(palette[0], 0x05060A, 0.66f, alpha * 0.5f);
            Render2D.rect(x, y + height * 0.55f, width, height * 0.45f, 12.0f, tint);
        }
        catch (Throwable ignored) {
            // музыка выключена — фон строится на теме
        }
    }

    private static void tick() {
        long now = System.nanoTime();
        float delta = ClickGuiBackdrops.lastFrameNs == 0L ? 0.016f
                : Math.min(0.1f, (float)(now - ClickGuiBackdrops.lastFrameNs) / 1.0E9f);
        ClickGuiBackdrops.lastFrameNs = now;
        ClickGuiBackdrops.flow += delta;
        for (int i = 0; i < PARTICLES; i++) {
            particleY[i] -= particleSpeed[i] * delta * 6.0f;
            if (particleY[i] < -0.05f) {
                particleY[i] = 1.05f;
            }
        }
    }

    public static void reset() {
        ClickGuiBackdrops.lastFrameNs = 0L;
        ClickGuiBackdrops.flow = 0.0f;
    }

    private static int mix(int colorA, int colorB, float amount, float alpha) {
        float value = Math.max(0.0f, Math.min(1.0f, amount));
        int red = (int)((float)(colorA >> 16 & 0xFF) * (1.0f - value) + (float)(colorB >> 16 & 0xFF) * value);
        int green = (int)((float)(colorA >> 8 & 0xFF) * (1.0f - value) + (float)(colorB >> 8 & 0xFF) * value);
        int blue = (int)((float)(colorA & 0xFF) * (1.0f - value) + (float)(colorB & 0xFF) * value);
        int a = Math.max(0, Math.min(255, (int)alpha));
        return a << 24 | red << 16 | green << 8 | blue;
    }

    /** Нужен ли фон: модуль включён и клиент в игре. */
    public static boolean active() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null && client.world != null;
    }
}
