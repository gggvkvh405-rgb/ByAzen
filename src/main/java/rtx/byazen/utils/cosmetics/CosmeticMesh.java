package rtx.byazen.utils.cosmetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.math.Vec3d;

/**
 * Сетка косметики: набор треугольников с цветом в каждой вершине (идеи №121 и №124 из IDEAS.md).
 * <p>
 * Одна и та же сетка используется дважды: в мире её рисует рендер игрока, а в каталоге — превью,
 * которое само проецирует треугольники на экран. Поэтому в каталоге видно ровно то, что появится
 * на игроке.
 */
public final class CosmeticMesh {

    /** Треугольник в абсолютных координатах с цветами вершин. */
    public static final class Tri {

        public final Vec3d a;
        public final Vec3d b;
        public final Vec3d c;
        public final int ca;
        public final int cb;
        public final int cc;

        Tri(Vec3d a, Vec3d b, Vec3d c, int ca, int cb, int cc) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.ca = ca;
            this.cb = cb;
            this.cc = cc;
        }

        public Vec3d center() {
            return new Vec3d((this.a.x + this.b.x + this.c.x) / 3.0,
                    (this.a.y + this.b.y + this.c.y) / 3.0,
                    (this.a.z + this.b.z + this.c.z) / 3.0);
        }

        public Tri moved(Vec3d origin, Vec3d axisX, Vec3d axisY, Vec3d axisZ) {
            return new Tri(CosmeticMesh.transform(this.a, origin, axisX, axisY, axisZ),
                    CosmeticMesh.transform(this.b, origin, axisX, axisY, axisZ),
                    CosmeticMesh.transform(this.c, origin, axisX, axisY, axisZ),
                    this.ca, this.cb, this.cc);
        }
    }

    private final List<Tri> tris = new ArrayList<Tri>();

    /** Треугольник по трём точкам. */
    public void tri(Vec3d a, Vec3d b, Vec3d c, int ca, int cb, int cc) {
        this.tris.add(new Tri(a, b, c, ca, cb, cc));
    }

    /** Четырёхугольник: две треугольные половины. */
    public void quad(Vec3d a, Vec3d b, Vec3d c, Vec3d d, int ca, int cb, int cc, int cd) {
        this.tri(a, b, c, ca, cb, cc);
        this.tri(a, c, d, ca, cc, cd);
    }

    /** Плоское кольцо в плоскости осей (axisX, axisZ). */
    public void annulus(Vec3d origin, Vec3d axisX, Vec3d axisZ, float inner, float outer, int segments,
                        int innerColor, int outerColor) {
        for (int i = 0; i < segments; ++i) {
            double angleA = (double)i / (double)segments * Math.PI * 2.0;
            double angleB = (double)(i + 1) / (double)segments * Math.PI * 2.0;
            Vec3d dirA = axisX.multiply(Math.cos(angleA)).add(axisZ.multiply(Math.sin(angleA)));
            Vec3d dirB = axisX.multiply(Math.cos(angleB)).add(axisZ.multiply(Math.sin(angleB)));
            Vec3d inA = origin.add(dirA.multiply((double)inner));
            Vec3d inB = origin.add(dirB.multiply((double)inner));
            Vec3d outA = origin.add(dirA.multiply((double)outer));
            Vec3d outB = origin.add(dirB.multiply((double)outer));
            this.quad(outA, outB, inB, inA, outerColor, outerColor, innerColor, innerColor);
        }
    }

    /** Боковая поверхность цилиндра без крышек. */
    public void cylinder(Vec3d origin, Vec3d axisX, Vec3d axisZ, Vec3d axisY, float radius, float height,
                         int segments, int colorBottom, int colorTop) {
        for (int i = 0; i < segments; ++i) {
            double angleA = (double)i / (double)segments * Math.PI * 2.0;
            double angleB = (double)(i + 1) / (double)segments * Math.PI * 2.0;
            Vec3d dirA = axisX.multiply(Math.cos(angleA)).add(axisZ.multiply(Math.sin(angleA)));
            Vec3d dirB = axisX.multiply(Math.cos(angleB)).add(axisZ.multiply(Math.sin(angleB)));
            float shadeA = (float)(0.62 + 0.38 * (0.5 + 0.5 * Math.cos(angleA)));
            Vec3d lowA = origin.add(dirA.multiply((double)radius));
            Vec3d lowB = origin.add(dirB.multiply((double)radius));
            Vec3d highA = lowA.add(axisY.multiply((double)height));
            Vec3d highB = lowB.add(axisY.multiply((double)height));
            this.quad(highA, highB, lowB, lowA,
                    CosmeticMesh.shade(colorTop, shadeA), CosmeticMesh.shade(colorTop, shadeB(shadeA)),
                    CosmeticMesh.shade(colorBottom, shadeB(shadeA)), CosmeticMesh.shade(colorBottom, shadeA));
        }
    }

    private static float shadeB(float value) {
        return Math.max(0.4f, value * 0.92f);
    }

    private static int shade(int color, float factor) {
        int a = color >>> 24 & 0xFF;
        int r = Math.round((float)(color >> 16 & 0xFF) * factor);
        int g = Math.round((float)(color >> 8 & 0xFF) * factor);
        int b = Math.round((float)(color & 0xFF) * factor);
        return a << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    /** Лента вдоль нити: сторона считается либо от камеры, либо от опорного вектора. */
    public void ribbon(Vec3d[] spine, float[] widths, int colorFrom, int colorTo, int alpha, Vec3d reference) {
        for (int i = 0; i < spine.length - 1; ++i) {
            Vec3d a = spine[i];
            Vec3d b = spine[i + 1];
            if (a == null || b == null) {
                continue;
            }
            Vec3d axis = b.subtract(a);
            Vec3d sideVector = reference == null ? axis.crossProduct(new Vec3d(0.0, 1.0, 0.0)) : axis.crossProduct(a.subtract(reference));
            if (sideVector.lengthSquared() < 1.0E-8) {
                sideVector = new Vec3d(1.0, 0.0, 0.0);
            }
            sideVector = sideVector.normalize();
            float widthA = widths[Math.min(widths.length - 1, i)];
            float widthB = widths[Math.min(widths.length - 1, i + 1)];
            float t0 = (float)i / (float)Math.max(1, spine.length - 1);
            float t1 = (float)(i + 1) / (float)Math.max(1, spine.length - 1);
            int colorA = CosmeticMesh.lerp(colorFrom, colorTo, t0, alpha);
            int colorB = CosmeticMesh.lerp(colorFrom, colorTo, t1, alpha);
            Vec3d a1 = a.add(sideVector.multiply((double)widthA));
            Vec3d a2 = a.subtract(sideVector.multiply((double)widthA));
            Vec3d b1 = b.add(sideVector.multiply((double)widthB));
            Vec3d b2 = b.subtract(sideVector.multiply((double)widthB));
            this.quad(a1, b1, b2, a2, colorA, colorB, colorB, colorA);
            // вторая сторона ленты: при виде сзади шарф не исчезает
            this.quad(a2, b2, b1, a1, colorA, colorB, colorB, colorA);
        }
    }

    /** Лента с постоянной стороной: рога и другие «плоские» детали. */
    public void ribbonSided(Vec3d[] spine, float[] widths, int colorFrom, int colorTo, int alpha, Vec3d side) {
        Vec3d fixed = side == null || side.lengthSquared() < 1.0E-8 ? new Vec3d(1.0, 0.0, 0.0) : side.normalize();
        for (int i = 0; i < spine.length - 1; ++i) {
            Vec3d a = spine[i];
            Vec3d b = spine[i + 1];
            if (a == null || b == null) {
                continue;
            }
            float widthA = widths[Math.min(widths.length - 1, i)];
            float widthB = widths[Math.min(widths.length - 1, i + 1)];
            float t0 = (float)i / (float)Math.max(1, spine.length - 1);
            float t1 = (float)(i + 1) / (float)Math.max(1, spine.length - 1);
            int colorA = CosmeticMesh.lerp(colorFrom, colorTo, t0, alpha);
            int colorB = CosmeticMesh.lerp(colorFrom, colorTo, t1, alpha);
            Vec3d a1 = a.add(fixed.multiply((double)widthA));
            Vec3d a2 = a.subtract(fixed.multiply((double)widthA));
            Vec3d b1 = b.add(fixed.multiply((double)widthB));
            Vec3d b2 = b.subtract(fixed.multiply((double)widthB));
            this.quad(a1, b1, b2, a2, colorA, colorB, colorB, colorA);
            this.quad(a2, b2, b1, a1, colorA, colorB, colorB, colorA);
        }
    }

    private static int lerp(int from, int to, float t, int alpha) {
        float f = Math.max(0.0f, Math.min(1.0f, t));
        int r = Math.round((float)(from >> 16 & 0xFF) + (float)((to >> 16 & 0xFF) - (from >> 16 & 0xFF)) * f);
        int g = Math.round((float)(from >> 8 & 0xFF) + (float)((to >> 8 & 0xFF) - (from >> 8 & 0xFF)) * f);
        int b = Math.round((float)(from & 0xFF) + (float)((to & 0xFF) - (from & 0xFF)) * f);
        int a = Math.max(0, Math.min(255, alpha));
        return a << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | (b & 0xFF);
    }

    /** Сетка, повёрнутая и перенесённая в мировые координаты. */
    public CosmeticMesh moved(Vec3d origin, Vec3d axisX, Vec3d axisY, Vec3d axisZ) {
        CosmeticMesh result = new CosmeticMesh();
        for (Tri tri : this.tris) {
            result.tris.add(tri.moved(origin, axisX, axisY, axisZ));
        }
        return result;
    }

    private static Vec3d transform(Vec3d point, Vec3d origin, Vec3d axisX, Vec3d axisY, Vec3d axisZ) {
        return origin.add(axisX.multiply(point.x)).add(axisY.multiply(point.y)).add(axisZ.multiply(point.z));
    }

    public List<Tri> triangles() {
        return Collections.unmodifiableList(this.tris);
    }

    public int size() {
        return this.tris.size();
    }

    public boolean isEmpty() {
        return this.tris.isEmpty();
    }
}
