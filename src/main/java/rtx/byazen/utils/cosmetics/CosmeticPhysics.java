package rtx.byazen.utils.cosmetics;

import net.minecraft.util.math.Vec3d;

/**
 * Физика шлейфов косметики (идеи №123, №124 и №130 из IDEAS.md).
 * <p>
 * Одна «нить» — цепочка точек на пружинах: метод Верле, как у крыльев, но универсальный. На нём
 * держатся хвосты, шарфы, плащи и следы под ногами. Нить мягко возвращается к заданному направлению,
 * поэтому не растягивается бесконечно и не дрожит при остановке.
 */
public final class CosmeticPhysics {

    private final Vec3d[] points;
    private final Vec3d[] previous;
    private final float segment;
    private boolean initialized;

    /**
     * @param count     число точек (минимум две)
     * @param totalLength общая длина нити
     */
    public CosmeticPhysics(int count, float totalLength) {
        int safeCount = Math.max(2, count);
        this.points = new Vec3d[safeCount];
        this.previous = new Vec3d[safeCount];
        this.segment = Math.max(0.005f, totalLength / (float)(safeCount - 1));
    }

    public int count() {
        return this.points.length;
    }

    public float length() {
        return this.segment * (float)(this.points.length - 1);
    }

    public Vec3d point(int index) {
        int safe = Math.max(0, Math.min(this.points.length - 1, index));
        Vec3d value = this.points[safe];
        return value == null ? Vec3d.ZERO : value;
    }

    /** Жёстко переставляет нить: используется при включении и после телепорта. */
    public void reset(Vec3d anchor, Vec3d direction) {
        Vec3d dir = CosmeticPhysics.normalize(direction, new Vec3d(0.0, -1.0, 0.0));
        for (int i = 0; i < this.points.length; ++i) {
            Vec3d value = anchor.add(dir.multiply((double)(this.segment * (float)i)));
            this.points[i] = value;
            this.previous[i] = value;
        }
        this.initialized = true;
    }

    /**
     * Шаг симуляции.
     *
     * @param anchor   точка крепления
     * @param direction куда «свисает» нить (вниз, с учётом наклона тела или спины)
     * @param wind     ветер: обычно скорость игрока, умноженная на коэффициент
     * @param gravity  ускорение вниз за кадр
     * @param damping  затухание скорости (0.75–0.95)
     */
    public void update(Vec3d anchor, Vec3d direction, Vec3d wind, float gravity, float damping) {
        if (!this.initialized) {
            this.reset(anchor, direction);
        }
        Vec3d dir = CosmeticPhysics.normalize(direction, new Vec3d(0.0, -1.0, 0.0));
        Vec3d fall = dir.multiply((double)gravity);
        Vec3d air = wind == null ? Vec3d.ZERO : wind;
        float keep = (float)Math.max(0.02, Math.min(0.35, 1.0 - (double)damping * 0.75));
        for (int i = 1; i < this.points.length; ++i) {
            Vec3d current = this.points[i];
            Vec3d last = this.previous[i];
            Vec3d velocity = current.subtract(last).multiply((double)damping);
            this.previous[i] = current;
            // мягкое возвращение к форме: тянем точку к её месту на «сухой» нити
            Vec3d rest = anchor.add(dir.multiply((double)(this.segment * (float)i)));
            Vec3d pull = rest.subtract(current).multiply((double)keep);
            this.points[i] = current.add(velocity).add(fall).add(air).add(pull);
        }
        // длина звеньев и неподвижное крепление
        this.points[0] = anchor;
        for (int i = 1; i < this.points.length; ++i) {
            Vec3d previousPoint = this.points[i - 1];
            Vec3d current = this.points[i];
            Vec3d delta = current.subtract(previousPoint);
            double length = delta.length();
            if (length < 1.0E-4) {
                delta = dir.multiply((double)this.segment);
                length = (double)this.segment;
            }
            Vec3d corrected = previousPoint.add(delta.multiply((double)this.segment / length));
            this.points[i] = corrected;
        }
    }

    private static Vec3d normalize(Vec3d value, Vec3d fallback) {
        if (value == null || value.lengthSquared() < 1.0E-8) {
            return fallback;
        }
        return value.normalize();
    }
}
