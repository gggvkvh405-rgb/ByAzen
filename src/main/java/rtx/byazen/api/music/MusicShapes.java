package rtx.byazen.api.music;

import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Small vector icon set of the music player. Everything is built from rounded, anti-aliased shapes
 * so the icons stay crisp at every GUI scale - the client never uses bitmap/pixel icons.
 */
public final class MusicShapes {

    private MusicShapes() {
    }

    public static void play(float x, float y, float size, int color) {
        MusicShapes.triangle(x, y, size, false, color);
    }

    public static void pause(float x, float y, float size, int color) {
        float barWidth = size * 0.3f;
        float height = size * 0.92f;
        Render2D.rect(x, y + size * 0.04f, barWidth, height, barWidth * 0.45f, color);
        Render2D.rect(x + size * 0.62f, y + size * 0.04f, barWidth, height, barWidth * 0.45f, color);
    }

    public static void previous(float x, float y, float size, int color) {
        Render2D.rect(x, y + size * 0.06f, size * 0.16f, size * 0.88f, size * 0.08f, color);
        MusicShapes.triangle(x + size * 0.24f, y + size * 0.06f, size * 0.7f, true, color);
    }

    public static void next(float x, float y, float size, int color) {
        MusicShapes.triangle(x + size * 0.06f, y + size * 0.06f, size * 0.7f, false, color);
        Render2D.rect(x + size * 0.84f, y + size * 0.06f, size * 0.16f, size * 0.88f, size * 0.08f, color);
    }

    /** Triangle built from stacked rounded bars - smooth edges at any size. */
    public static void triangle(float x, float y, float size, boolean flip, int color) {
        int steps = 7;
        float step = size / (float) steps;
        for (int i = 0; i < steps; ++i) {
            float t = (float) i / (float) (steps - 1);
            float width = size * (flip ? t : 1.0f - t);
            float px = flip ? x + size - width : x;
            Render2D.rect(px, y + (float) i * step, Math.max(0.6f, width), step + 0.3f, step * 0.4f, color);
        }
    }

    public static void stop(float x, float y, float size, int color) {
        Render2D.rect(x + size * 0.08f, y + size * 0.08f, size * 0.84f, size * 0.84f, size * 0.2f, color);
    }

    /** Bookmark used as the "favourite" toggle. */
    public static void bookmark(float x, float y, float size, boolean filled, int color) {
        float width = size * 0.74f;
        float height = size * 0.94f;
        float bx = x + (size - width) * 0.5f;
        if (filled) {
            Render2D.rect(bx, y, width, height, size * 0.16f, color);
            Render2D.rect(bx + width * 0.34f, y + height * 0.62f, width * 0.32f, height * 0.5f, 0.6f, 0x00000000);
            return;
        }
        Render2D.outline(bx, y, width, height, size * 0.16f, 1.1f, color);
    }

    /** Simple music note (placeholder for missing cover art). */
    public static void note(float x, float y, float size, int color) {
        float stem = Math.max(1.2f, size * 0.11f);
        Render2D.rect(x + size * 0.22f, y + size * 0.08f, stem, size * 0.62f, stem * 0.5f, color);
        Render2D.rect(x + size * 0.74f, y + size * 0.18f, stem, size * 0.52f, stem * 0.5f, color);
        Render2D.rect(x + size * 0.22f, y + size * 0.08f, size * 0.63f, stem * 1.25f, stem * 0.6f, color);
        Render2D.circle(x + size * 0.16f, y + size * 0.72f, size * 0.17f, color);
        Render2D.circle(x + size * 0.68f, y + size * 0.72f, size * 0.17f, color);
    }

    /** Значок «не нравится»: перечёркнутый круг, нарисованный дугами окружности. */
    public static void ban(float x, float y, float size, int color) {
        Render2D.circleOutline(x + size * 0.5f, y + size * 0.5f, size * 0.44f, 1.15f, color);
        Render2D.line(x + size * 0.22f, y + size * 0.78f, x + size * 0.78f, y + size * 0.22f, 1.15f, color);
    }

    /** Динамик рядом с ползунком громкости. */
    public static void speaker(float x, float y, float size, int color) {
        float box = size * 0.4f;
        Render2D.rect(x + size * 0.06f, y + size * 0.33f, box, size * 0.38f, size * 0.08f, color);
        MusicShapes.triangle(x + size * 0.3f, y + size * 0.16f, size * 0.46f, false, color);
        Render2D.rect(x + size * 0.78f, y + size * 0.3f, size * 0.1f, size * 0.44f, size * 0.05f, color);
    }

    /** Векторная стрелка «выше/ниже» — для сортировки треков в плейлисте (идея №3). */
    public static void chevron(float x, float y, float size, boolean up, int color) {
        float thickness = Math.max(1.0f, size * 0.17f);
        float midX = x + size * 0.5f;
        float topY = up ? y + size * 0.26f : y + size * 0.74f;
        float bottomY = up ? y + size * 0.74f : y + size * 0.26f;
        Render2D.line(midX, topY, x + size * 0.08f, bottomY, thickness, color);
        Render2D.line(midX, topY, x + size * 0.92f, bottomY, thickness, color);
    }

    /** Крестик «закрыть». */
    public static void close(float x, float y, float size, int color) {
        float thickness = Math.max(1.0f, size * 0.13f);
        Render2D.line(x, y, x + size, y + size, thickness, color);
        Render2D.line(x + size, y, x, y + size, thickness, color);
    }

    /** Точка-маркер для радиальных визуализаций. */
    public static void radialBar(float centerX, float centerY, float angle, float inner, float outer, float thickness, int color) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        int steps = 5;
        float length = Math.max(0.5f, outer - inner);
        float step = length / (float) steps;
        for (int i = 0; i < steps; ++i) {
            float radius = inner + step * (float) i;
            Render2D.circle(centerX + cos * radius, centerY + sin * radius, thickness, color);
        }
    }

    /** Live level meter for the "now playing" row: three animated bars. */
    public static void equalizer(float x, float y, float size, float level, int color) {
        float phase = (float) (System.nanoTime() % 2000000000L) / 2.0E9f * 6.2831855f;
        float barWidth = size * 0.22f;
        for (int i = 0; i < 3; ++i) {
            float wave = 0.45f + 0.55f * (0.5f + 0.5f * (float) Math.sin(phase * (1.0f + 0.35f * (float) i) + (float) i * 1.3f));
            float height = size * Math.max(0.18f, Math.min(1.0f, level * wave + 0.12f));
            Render2D.rect(x + (float) i * (barWidth * 1.5f), y + size - height, barWidth, height, barWidth * 0.5f, color);
        }
    }
}
