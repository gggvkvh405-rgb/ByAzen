package rtx.byazen.api.nav;

import net.minecraft.util.math.Vec3d;

/**
 * Путевая точка (идея №72 из IDEAS.md): имя, координаты, измерение, цвет и заметка.
 * Свойства неизменяемы, чтобы точки можно было безопасно раздавать в отрисовку.
 */
public final class Waypoint {

    private final String name;
    private final double x;
    private final double y;
    private final double z;
    private final String dimension;
    private final int color;
    private final String note;

    public Waypoint(String name, double x, double y, double z, String dimension, int color, String note) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimension = dimension == null ? "minecraft:overworld" : dimension;
        this.color = color;
        this.note = note == null ? "" : note;
    }

    public String name() {
        return this.name;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public double z() {
        return this.z;
    }

    public String dimension() {
        return this.dimension;
    }

    public int color() {
        return this.color;
    }

    public String note() {
        return this.note;
    }

    public Vec3d pos() {
        return new Vec3d(this.x, this.y, this.z);
    }

    public double distanceTo(Vec3d from) {
        return from == null ? 0.0 : Math.sqrt(from.squaredDistanceTo(this.x, this.y, this.z));
    }

    public Waypoint named(String newName) {
        return new Waypoint(newName, this.x, this.y, this.z, this.dimension, this.color, this.note);
    }

    public Waypoint withColor(int newColor) {
        return new Waypoint(this.name, this.x, this.y, this.z, this.dimension, newColor, this.note);
    }

    /** Короткое название измерения для интерфейса. */
    public String shortDimension() {
        if (this.dimension.contains("nether")) {
            return "Ад";
        }
        if (this.dimension.contains("end")) {
            return "Край";
        }
        return "Верхний мир";
    }
}
