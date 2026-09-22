package rtx.byazen.utils.cosmetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Настоящее 3D-превью косметики в каталоге (идея №121 из IDEAS.md).
 * <p>
 * Берётся та же сетка, что рисуется на игроке в мире, поворачивается на заданный угол, проецируется
 * на экран с лёгкой перспективой и заливается построчно с расчётом освещения по нормали. Поэтому в
 * каталоге видно ровно ту модель, которую получит игрок, и её можно крутить мышью, как в витрине.
 */
public final class CosmeticPreview {

    private static final Vec3d LIGHT = new Vec3d(-0.35, 0.72, 0.60).normalize();
    private static int lastTriangles;
    private static int lastRows;

    private CosmeticPreview() {
    }

    /** Сколько треугольников было в последнем кадре превью (для отладки и тестов). */
    public static int lastTriangles() {
        return lastTriangles;
    }

    /** Сколько строк заливки было в последнем кадре превью. */
    public static int lastRows() {
        return lastRows;
    }

    /**
     * Рисует модель.
     *
     * @param scale      размер модели в пикселях на блок
     * @param yaw, pitch углы поворота в градусах (крутится мышью в каталоге)
     * @param time       секунды для анимации нимба и ауры
     * @param alpha      общая прозрачность (используется при открытии каталога)
     */
    public static void draw(Cosmetic cosmetic, float centerX, float centerY, float scale, float yaw, float pitch,
                            float time, float alpha) {
        lastTriangles = 0;
        lastRows = 0;
        if (cosmetic == null || alpha <= 0.01f) {
            return;
        }
        CosmeticMesh mesh = CosmeticModels.hanging(cosmetic, 1.0f, time);
        if (mesh.isEmpty()) {
            mesh = CosmeticModels.accessory(cosmetic, 1.0f, time);
        }
        if (mesh.isEmpty()) {
            return;
        }
        List<CosmeticMesh.Tri> triangles = mesh.triangles();
        lastTriangles = triangles.size();

        // центр модели, чтобы она всегда крутилась вокруг себя
        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxZ = -Double.MAX_VALUE;
        for (CosmeticMesh.Tri tri : triangles) {
            for (Vec3d point : new Vec3d[]{tri.a, tri.b, tri.c}) {
                minX = Math.min(minX, point.x);
                maxX = Math.max(maxX, point.x);
                minY = Math.min(minY, point.y);
                maxY = Math.max(maxY, point.y);
                minZ = Math.min(minZ, point.z);
                maxZ = Math.max(maxZ, point.z);
            }
        }
        Vec3d center = new Vec3d((minX + maxX) * 0.5, (minY + maxY) * 0.5, (minZ + maxZ) * 0.5);
        double extent = Math.max(maxX - minX, Math.max(maxY - minY, maxZ - minZ));
        float fit = extent < 1.0E-4 ? 1.0f : (float)(1.35 / extent);

        CosmeticPreview.glow(cosmetic, centerX, centerY, scale, fit, alpha);

        float yawCos = MathHelper.cos((float)Math.toRadians((double)yaw));
        float yawSin = MathHelper.sin((float)Math.toRadians((double)yaw));
        float pitchCos = MathHelper.cos((float)Math.toRadians((double)pitch));
        float pitchSin = MathHelper.sin((float)Math.toRadians((double)pitch));

        ArrayList<Face> faces = new ArrayList<Face>(triangles.size());
        for (CosmeticMesh.Tri tri : triangles) {
            Vec3d a = CosmeticPreview.rotate(tri.a.subtract(center), yawCos, yawSin, pitchCos, pitchSin);
            Vec3d b = CosmeticPreview.rotate(tri.b.subtract(center), yawCos, yawSin, pitchCos, pitchSin);
            Vec3d c = CosmeticPreview.rotate(tri.c.subtract(center), yawCos, yawSin, pitchCos, pitchSin);
            Vec3d normal = b.subtract(a).crossProduct(c.subtract(a));
            if (normal.lengthSquared() < 1.0E-10) {
                continue;
            }
            normal = normal.normalize();
            float shade = 0.52f + 0.48f * (float)Math.abs(normal.dotProduct(LIGHT));
            Projected pa = CosmeticPreview.project(a, centerX, centerY, scale, fit);
            Projected pb = CosmeticPreview.project(b, centerX, centerY, scale, fit);
            Projected pc = CosmeticPreview.project(c, centerX, centerY, scale, fit);
            faces.add(new Face(pa, pb, pc, tri.ca, tri.cb, tri.cc, shade));
        }
        Collections.sort(faces, Comparator.comparingDouble(face -> -face.depth));
        for (Face face : faces) {
            CosmeticPreview.fill(face, alpha);
        }
    }

    private static void glow(Cosmetic cosmetic, float centerX, float centerY, float scale, float fit, float alpha) {
        int accent = cosmetic.accent & 0xFFFFFF;
        int color = cosmetic.color & 0xFFFFFF;
        float radius = Math.max(18.0f, scale * 0.85f * fit);
        int layers = cosmetic.neon() || cosmetic.space() ? 5 : 3;
        for (int i = layers; i >= 1; --i) {
            float factor = (float)i / (float)layers;
            int packed = CosmeticPreview.rgba(cosmetic.neon() ? accent : color,
                    Math.round(10.0f * alpha * (1.0f - factor * 0.55f)));
            Render2D.circle(centerX, centerY, radius * (0.7f + factor * 0.9f), 24.0f, packed);
        }
    }

    private static int rgba(int rgb, int alpha) {
        return Math.max(0, Math.min(255, alpha)) << 24 | rgb & 0xFFFFFF;
    }

    private static Vec3d rotate(Vec3d point, float yawCos, float yawSin, float pitchCos, float pitchSin) {
        double x = point.x * (double)yawCos + point.z * (double)yawSin;
        double z = -point.x * (double)yawSin + point.z * (double)yawCos;
        double y = point.y * (double)pitchCos - z * (double)pitchSin;
        double depth = point.y * (double)pitchSin + z * (double)pitchCos;
        return new Vec3d(x, y, depth);
    }

    private static Projected project(Vec3d point, float centerX, float centerY, float scale, float fit) {
        float perspective = 1.0f / Math.max(0.45f, 1.0f + (float)point.z * 0.25f);
        float size = scale * fit * perspective;
        return new Projected(centerX + (float)point.x * size, centerY - (float)point.y * size, (float)point.z);
    }

    private static void fill(Face face, float alpha) {
        float minY = Math.min(face.a.y, Math.min(face.b.y, face.c.y));
        float maxY = Math.max(face.a.y, Math.max(face.b.y, face.c.y));
        float minX = Math.min(face.a.x, Math.min(face.b.x, face.c.x));
        float maxX = Math.max(face.a.x, Math.max(face.b.x, face.c.x));
        float height = maxY - minY;
        float widthAll = maxX - minX;
        if (widthAll < 0.35f || height < 0.35f) {
            // мелкий осколок: рисуем одной мягкой точкой
            CosmeticPreview.pixel(minX, minY, Math.max(1.0f, widthAll), face.shadedAverage(alpha));
            return;
        }
        float step = height > 70.0f ? height / 70.0f : 0.55f;
        float[] xs = new float[4];
        int[] cs = new int[4];
        for (float y = minY; y <= maxY; y += step) {
            int count = 0;
            count = CosmeticPreview.intersect(face.a, face.b, y, xs, cs, count, face.ca, face.cb);
            count = CosmeticPreview.intersect(face.b, face.c, y, xs, cs, count, face.cb, face.cc);
            count = CosmeticPreview.intersect(face.c, face.a, y, xs, cs, count, face.cc, face.ca);
            if (count < 2) {
                continue;
            }
            int leftIndex = 0;
            int rightIndex = 0;
            for (int i = 1; i < count; ++i) {
                if (xs[i] < xs[leftIndex]) {
                    leftIndex = i;
                }
                if (xs[i] > xs[rightIndex]) {
                    rightIndex = i;
                }
            }
            float width = xs[rightIndex] - xs[leftIndex];
            if (width < 0.2f) {
                continue;
            }
            CosmeticPreview.pixel(xs[leftIndex], y, width,
                    CosmeticPreview.blend(cs[leftIndex], cs[rightIndex], face.shade, alpha));
            ++lastRows;
        }
        // мягкая кромка: полупрозрачная строка снизу убирает «лестницу» по краю
        CosmeticPreview.pixel(minX, maxY, Math.max(1.0f, widthAll),
                CosmeticPreview.blend(face.ca, face.cb, face.shade, alpha * 0.26f));
    }

    private static int intersect(Projected from, Projected to, float y, float[] xs, int[] cs, int count, int colorFrom,
                                 int colorTo) {
        float low = Math.min(from.y, to.y);
        float high = Math.max(from.y, to.y);
        if (y < low || y > high || count >= xs.length) {
            return count;
        }
        float span = high - low;
        float t = span < 1.0E-4f ? 0.0f : (y - low) / span;
        float x = from.y <= to.y ? from.x + (to.x - from.x) * t : to.x + (from.x - to.x) * t;
        int insert = 0;
        while (insert < count && xs[insert] <= x) {
            ++insert;
        }
        for (int i = count; i > insert; --i) {
            xs[i] = xs[i - 1];
            cs[i] = cs[i - 1];
        }
        xs[insert] = x;
        cs[insert] = CosmeticPreview.mixColor(colorFrom, colorTo, t);
        return count + 1;
    }

    private static int mixColor(int from, int to, float t) {
        float f = MathHelper.clamp(t, 0.0f, 1.0f);
        int r = Math.round((float)(from >> 16 & 0xFF) + (float)((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * f);
        int g = Math.round((float)(from >> 8 & 0xFF) + (float)((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * f);
        int b = Math.round((float)(from & 0xFF) + (float)((to & 0xFF) - (from & 0xFF)) * f);
        int a = Math.max(from >>> 24 & 0xFF, to >>> 24 & 0xFF);
        return a << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    private static void pixel(float x, float y, float width, int color) {
        Render2D.rect(x, y, width, 0.6f, 0.0f, color);
    }

    private static int blend(int from, int to, float shade, float alpha) {
        int r = Math.round((float)((from >> 16 & 0xFF) + (to >> 16 & 0xFF)) * 0.5f * shade);
        int g = Math.round((float)((from >> 8 & 0xFF) + (to >> 8 & 0xFF)) * 0.5f * shade);
        int b = Math.round((float)((from & 0xFF) + (to & 0xFF)) * 0.5f * shade);
        int fromAlpha = from >>> 24 & 0xFF;
        int toAlpha = to >>> 24 & 0xFF;
        int a = Math.round((float)(fromAlpha + toAlpha) * 0.5f * MathHelper.clamp(alpha, 0.0f, 1.0f));
        return Math.max(0, Math.min(255, a)) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    private static final class Projected {

        final float x;
        final float y;
        final float depth;

        Projected(float x, float y, float depth) {
            this.x = x;
            this.y = y;
            this.depth = depth;
        }
    }

    private static final class Face {

        final Projected a;
        final Projected b;
        final Projected c;
        final int ca;
        final int cb;
        final int cc;
        final float shade;
        final float depth;

        Face(Projected a, Projected b, Projected c, int ca, int cb, int cc, float shade) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.ca = ca;
            this.cb = cb;
            this.cc = cc;
            this.shade = shade;
            this.depth = (a.depth + b.depth + c.depth) / 3.0f;
        }

        int shadedAverage(float alpha) {
            int r = Math.round((float)((this.ca >> 16 & 0xFF) + (this.cb >> 16 & 0xFF) + (this.cc >> 16 & 0xFF)) / 3.0f * this.shade);
            int g = Math.round((float)((this.ca >> 8 & 0xFF) + (this.cb >> 8 & 0xFF) + (this.cc >> 8 & 0xFF)) / 3.0f * this.shade);
            int b = Math.round((float)((this.ca & 0xFF) + (this.cb & 0xFF) + (this.cc & 0xFF)) / 3.0f * this.shade);
            int a = Math.round((float)((this.ca >>> 24 & 0xFF) + (this.cb >>> 24 & 0xFF) + (this.cc >>> 24 & 0xFF)) / 3.0f
                    * MathHelper.clamp(alpha, 0.0f, 1.0f));
            return Math.max(0, Math.min(255, a)) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
        }
    }
}
