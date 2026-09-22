package rtx.byazen.utils.cosmetics;

import net.minecraft.util.math.Vec3d;

/**
 * Геометрия моделей косметики (идеи №121, №122 и №124 из IDEAS.md).
 * <p>
 * Каждая модель собирается кодом в локальных осях: X — вправо, Y — вверх, Z — вперёд. Дальше сетка
 * переносится на игрока (или на превью в каталоге), поэтому одна и та же модель и ездит по миру, и
 * крутится в каталоге. Никаких текстур и пиксельной геометрии: только гладкие ленты, кольца и
 * цилиндры с плавным затенением по нормали.
 */
public final class CosmeticModels {

    private CosmeticModels() {
    }

    private static Vec3d p(double x, double y, double z) {
        return new Vec3d(x, y, z);
    }

    private static final Vec3d AXIS_X = p(1.0, 0.0, 0.0);
    private static final Vec3d AXIS_Y = p(0.0, 1.0, 0.0);
    private static final Vec3d AXIS_Z = p(0.0, 0.0, 1.0);

    private static int alpha(int rgb, int a) {
        return Math.max(0, Math.min(255, a)) << 24 | rgb & 0xFFFFFF;
    }

    /**
     * Модель аксессуара в локальных осях: голова, уши, лицо или аура.
     *
     * @param time секунды с начала показа — для вращения нимба и «дыхания» ауры
     */
    public static CosmeticMesh accessory(Cosmetic cosmetic, float scale, float time) {
        CosmeticMesh mesh = new CosmeticMesh();
        if (cosmetic == null) {
            return mesh;
        }
        float s = Math.max(0.4f, scale);
        switch (cosmetic.shape) {
            case Cosmetic.SHAPE_HAT:
                CosmeticModels.hat(mesh, cosmetic, s, time);
                break;
            case Cosmetic.SHAPE_CROWN:
                CosmeticModels.crown(mesh, cosmetic, s, time);
                break;
            case Cosmetic.SHAPE_HORNS:
                CosmeticModels.horns(mesh, cosmetic, s);
                break;
            case Cosmetic.SHAPE_HALO:
                CosmeticModels.halo(mesh, cosmetic, s, time);
                break;
            case Cosmetic.SHAPE_EARS_CAT:
            case Cosmetic.SHAPE_EARS_FOX:
                CosmeticModels.ears(mesh, cosmetic, s);
                break;
            case Cosmetic.SHAPE_MASK:
                CosmeticModels.mask(mesh, cosmetic, s, time);
                break;
            case Cosmetic.SHAPE_GLASSES:
                CosmeticModels.glasses(mesh, cosmetic, s);
                break;
            case Cosmetic.SHAPE_AURA:
                CosmeticModels.aura(mesh, cosmetic, s, time);
                break;
            default:
                break;
        }
        return mesh;
    }

    private static void hat(CosmeticMesh mesh, Cosmetic cosmetic, float s, float time) {
        int body = CosmeticModels.alpha(cosmetic.color, 238);
        int band = CosmeticModels.alpha(cosmetic.accent, 246);
        float brim = 0.24f * s;
        mesh.cylinder(p(0.0, 0.02 * (double)s, 0.0), AXIS_X, AXIS_Z, AXIS_Y, brim, (float)(0.30 * (double)s), 22, body, band);
        mesh.annulus(p(0.0, 0.02 * (double)s, 0.0), AXIS_X, AXIS_Z, brim, 0.36f * s, 22, band, CosmeticModels.alpha(cosmetic.accent, 120));
        // лента по низу шляпы: цвет акцента и мягкое свечение в темноте
        mesh.annulus(p(0.0, 0.05 * (double)s, 0.0), AXIS_X, AXIS_Z, brim * 1.01f, brim * 1.06f, 22,
                band, CosmeticModels.alpha(cosmetic.accent, 200));
    }

    private static void crown(CosmeticMesh mesh, Cosmetic cosmetic, float s, float time) {
        int gold = CosmeticModels.alpha(cosmetic.color, 244);
        int jewel = CosmeticModels.alpha(cosmetic.accent, 250);
        float radius = 0.21f * s;
        mesh.cylinder(p(0.0, 0.01 * (double)s, 0.0), AXIS_X, AXIS_Z, AXIS_Y, radius, (float)(0.075 * (double)s), 20, gold, gold);
        mesh.annulus(p(0.0, 0.085 * (double)s, 0.0), AXIS_X, AXIS_Z, radius * 0.72f, radius, 20, jewel, gold);
        for (int i = 0; i < 6; ++i) {
            double angle = (double)i / 6.0 * Math.PI * 2.0;
            Vec3d side = AXIS_X.multiply(Math.cos(angle)).add(AXIS_Z.multiply(Math.sin(angle)));
            Vec3d tangent = AXIS_X.multiply(Math.cos(angle + 1.5707963267948966)).add(AXIS_Z.multiply(Math.sin(angle + 1.5707963267948966)));
            double baseY = 0.075 * (double)s;
            double tipY = (0.28 + 0.03 * Math.sin((double)time * 1.6 + (double)i)) * (double)s;
            Vec3d tip = p(0.0, tipY, 0.0).add(side.multiply((double)radius * 0.82));
            mesh.tri(p(0.0, baseY, 0.0).add(side.multiply((double)radius * 0.9)).add(tangent.multiply(0.055 * (double)s)),
                    p(0.0, baseY, 0.0).add(side.multiply((double)radius * 0.9)).subtract(tangent.multiply(0.055 * (double)s)),
                    tip, gold, gold, jewel);
            mesh.tri(tip,
                    p(0.0, baseY, 0.0).add(side.multiply((double)radius * 0.78)).subtract(tangent.multiply(0.05 * (double)s)),
                    p(0.0, baseY, 0.0).add(side.multiply((double)radius * 0.78)).add(tangent.multiply(0.05 * (double)s)),
                    jewel, gold, gold);
        }
    }

    private static void horns(CosmeticMesh mesh, Cosmetic cosmetic, float s) {
        int base = CosmeticModels.alpha(cosmetic.color, 246);
        int tip = CosmeticModels.alpha(cosmetic.accent, 252);
        for (int side = -1; side <= 1; side += 2) {
            int points = 6;
            Vec3d[] spine = new Vec3d[points];
            float[] widths = new float[points];
            for (int i = 0; i < points; ++i) {
                float t = (float)i / (float)(points - 1);
                double x = (double)side * (0.10 + 0.085 * (double)t + 0.05 * (double)t * (double)t) * (double)s;
                double y = (0.0 + 0.34 * (double)t - 0.09 * (double)t * (double)t) * (double)s;
                double z = (-0.03 * (double)t) * (double)s;
                spine[i] = p(x, y, z);
                widths[i] = (0.052f - 0.042f * t) * s;
            }
            // крест-накрест две ленты: рог выглядит объёмным с любого ракурса
            mesh.ribbonSided(spine, widths, base, tip, 248, AXIS_Z);
            mesh.ribbonSided(spine, widths, base, tip, 236, AXIS_X);
        }
    }

    private static void halo(CosmeticMesh mesh, Cosmetic cosmetic, float s, float time) {
        int glow = CosmeticModels.alpha(cosmetic.color, 150);
        int bright = CosmeticModels.alpha(cosmetic.accent, 244);
        float tilt = (float)Math.sin((double)time * 0.9) * 0.10f;
        Vec3d axisX = p(Math.cos((double)tilt), Math.sin((double)tilt), 0.0);
        Vec3d axisZ = p(0.0, 0.0, 1.0);
        Vec3d center = p(0.0, 0.34 * (double)s, 0.0);
        mesh.annulus(center, axisX, axisZ, 0.185f * s, 0.225f * s, 30, bright, glow);
        mesh.annulus(center.add(AXIS_Y.multiply(0.012 * (double)(double)s)), axisX, axisZ, 0.225f * s, 0.265f * s, 30,
                CosmeticModels.alpha(cosmetic.accent, 90), CosmeticModels.alpha(cosmetic.color, 40));
    }

    private static void ears(CosmeticMesh mesh, Cosmetic cosmetic, float s) {
        boolean fox = Cosmetic.SHAPE_EARS_FOX.equals(cosmetic.shape);
        int outer = CosmeticModels.alpha(cosmetic.color, 250);
        int outerDark = CosmeticModels.alpha(cosmetic.color, 196);
        int inner = CosmeticModels.alpha(cosmetic.accent, 236);
        float height = (fox ? 0.34f : 0.24f) * s;
        float halfWidth = (fox ? 0.085f : 0.065f) * s;
        float halfDepth = (fox ? 0.055f : 0.045f) * s;
        for (int side = -1; side <= 1; side += 2) {
            double baseX = (double)side * (fox ? 0.135 : 0.115) * (double)s;
            double lean = (double)side * (fox ? 0.055 : 0.035) * (double)s;
            Vec3d base = p(baseX, 0.015 * (double)s, 0.0);
            Vec3d tip = p(baseX + lean, 0.015 * (double)s + (double)height, -0.01 * (double)s);
            Vec3d frontLeft = base.add(AXIS_X.multiply((double)(halfWidth * (float)side))).add(AXIS_Z.multiply((double)halfDepth));
            Vec3d frontRight = base.add(AXIS_X.multiply((double)(-halfWidth * (float)side))).add(AXIS_Z.multiply((double)halfDepth));
            Vec3d backLeft = base.add(AXIS_X.multiply((double)(halfWidth * (float)side))).subtract(AXIS_Z.multiply((double)halfDepth));
            Vec3d backRight = base.add(AXIS_X.multiply((double)(-halfWidth * (float)side))).subtract(AXIS_Z.multiply((double)halfDepth));
            mesh.tri(frontLeft, frontRight, tip, outer, outer, CosmeticModels.alpha(cosmetic.accent, 250));
            mesh.tri(backRight, backLeft, tip, outerDark, outerDark, outer);
            mesh.quad(frontLeft, tip, backLeft, backLeft, outer, outer, outerDark, outerDark);
            mesh.quad(frontRight, backRight, tip, tip, outer, outerDark, outer, outer);
            // подкладка уха: тёплый акцент внутри
            Vec3d innerTip = tip.add(AXIS_Y.multiply(-0.09 * (double)s));
            mesh.tri(frontLeft.add(AXIS_Z.multiply(0.006)), frontRight.add(AXIS_Z.multiply(0.006)), innerTip, inner, inner, inner);
        }
    }

    private static void mask(CosmeticMesh mesh, Cosmetic cosmetic, float s, float time) {
        int body = CosmeticModels.alpha(cosmetic.color, 246);
        int eye = CosmeticModels.alpha(cosmetic.accent, 252);
        float pulse = 0.85f + 0.15f * (float)Math.sin((double)time * 2.2);
        double z = 0.128 * (double)s;
        Vec3d topLeft = p(-0.17 * (double)s, 0.15 * (double)s, z);
        Vec3d topRight = p(0.17 * (double)s, 0.15 * (double)s, z - 0.004);
        Vec3d bottomRight = p(0.15 * (double)s, -0.15 * (double)s, z);
        Vec3d bottomLeft = p(-0.15 * (double)s, -0.15 * (double)s, z - 0.004);
        mesh.quad(topLeft, topRight, bottomRight, bottomLeft, body, body, body, body);
        // вторая пластина чуть позади: у маски появляется толщина
        Vec3d backOffset = AXIS_Z.multiply(-0.028 * (double)s);
        mesh.quad(topLeft.add(backOffset), backOffset.add(topLeft), topRight.add(backOffset), topRight, body, body, body, body);
        for (int side = -1; side <= 1; side += 2) {
            Vec3d center = p((double)side * 0.078 * (double)s, 0.045 * (double)s, z + 0.012);
            mesh.quad(center.add(AXIS_X.multiply(0.055 * (double)s)).add(AXIS_Y.multiply(0.022 * (double)s)),
                    center.subtract(AXIS_X.multiply(0.055 * (double)s)).add(AXIS_Y.multiply(0.022 * (double)s)),
                    center.subtract(AXIS_X.multiply(0.055 * (double)s)).subtract(AXIS_Y.multiply(0.022 * (double)s)),
                    center.add(AXIS_X.multiply(0.055 * (double)s)).subtract(AXIS_Y.multiply(0.022 * (double)s)),
                    eye, eye, eye, eye);
        }
        // тонкая линия улыбки, светится мягче глаз
        int line = CosmeticModels.alpha(cosmetic.accent, Math.round(140.0f * pulse));
        Vec3d mouth = p(0.0, -0.085 * (double)s, z + 0.012);
        mesh.quad(mouth.add(AXIS_X.multiply(0.07 * (double)s)),
                mouth.subtract(AXIS_X.multiply(0.07 * (double)s)),
                mouth.subtract(AXIS_X.multiply(0.05 * (double)s)).subtract(AXIS_Y.multiply(0.014 * (double)s)),
                mouth.add(AXIS_X.multiply(0.05 * (double)s)).subtract(AXIS_Y.multiply(0.014 * (double)s)),
                line, line, line, line);
    }

    private static void glasses(CosmeticMesh mesh, Cosmetic cosmetic, float s) {
        int frame = CosmeticModels.alpha(cosmetic.color, 236);
        int glass = CosmeticModels.alpha(cosmetic.accent, 150);
        double z = 0.125 * (double)s;
        Vec3d planeZ = p(0.0, 0.0, z);
        for (int side = -1; side <= 1; side += 2) {
            Vec3d center = p((double)side * 0.095 * (double)s, 0.045 * (double)s, z);
            mesh.annulus(center, AXIS_X, AXIS_Y, 0.058f * s, 0.078f * s, 20, frame, frame);
            mesh.annulus(center, AXIS_X, AXIS_Y, 0.0f, 0.056f * s, 20, glass, glass);
        }
        // переносица и дужки
        mesh.quad(planeZ.add(p(-0.05 * (double)s, 0.052 * (double)s, 0.0)), planeZ.add(p(0.05 * (double)s, 0.052 * (double)s, 0.0)),
                planeZ.add(p(0.05 * (double)s, 0.038 * (double)s, 0.0)), planeZ.add(p(-0.05 * (double)s, 0.038 * (double)s, 0.0)),
                frame, frame, frame, frame);
        for (int side = -1; side <= 1; side += 2) {
            Vec3d armStart = p((double)side * 0.17 * (double)s, 0.05 * (double)s, z);
            Vec3d armEnd = p((double)side * 0.185 * (double)s, 0.075 * (double)s, -0.13 * (double)s);
            mesh.ribbonSided(new Vec3d[]{armStart, armEnd}, new float[]{0.012f * s, 0.010f * s}, frame, frame, 236, AXIS_Y);
        }
    }

    private static void aura(CosmeticMesh mesh, Cosmetic cosmetic, float s, float time) {
        float pulse = 0.9f + 0.1f * (float)Math.sin((double)time * 1.8);
        int bright = CosmeticModels.alpha(cosmetic.accent, Math.round(210.0f * pulse));
        int soft = CosmeticModels.alpha(cosmetic.color, Math.round(120.0f * pulse));
        Vec3d center = p(0.0, 0.035, 0.0);
        mesh.annulus(center, AXIS_X, AXIS_Z, 0.42f * s * pulse, 0.5f * s * pulse, 34, bright, soft);
        float tilt = (float)((double)time * 0.5);
        Vec3d axisX = p(Math.cos((double)tilt), 0.0, Math.sin((double)tilt));
        Vec3d axisZ = p(-Math.sin((double)tilt), 0.0, Math.cos((double)tilt));
        mesh.annulus(center.add(AXIS_Y.multiply(0.30)), axisX, axisZ, 0.30f * s, 0.33f * s, 26, soft, CosmeticModels.alpha(cosmetic.accent, 60));
    }

    /**
     * Подвесные аксессуары (шарф и хвост) для превью в каталоге: та же лента, но по заранее
     * рассчитанной провисающей нити, чтобы в каталоге было видно, как предмет ложится.
     */
    public static CosmeticMesh hanging(Cosmetic cosmetic, float scale, float time) {
        CosmeticMesh mesh = new CosmeticMesh();
        if (cosmetic == null || !(Cosmetic.SHAPE_SCARF.equals(cosmetic.shape) || Cosmetic.SHAPE_TAIL.equals(cosmetic.shape))) {
            return mesh;
        }
        boolean scarf = Cosmetic.SHAPE_SCARF.equals(cosmetic.shape);
        int points = scarf ? 9 : 8;
        float length = (scarf ? 0.95f : 0.78f) * Math.max(0.4f, scale);
        float halfWidth = (scarf ? 0.075f : 0.095f) * Math.max(0.4f, scale);
        Vec3d[] spine = new Vec3d[points];
        float[] widths = new float[points];
        for (int i = 0; i < points; ++i) {
            float t = (float)i / (float)(points - 1);
            double sway = Math.sin((double)t * 3.1 + (double)time * 1.4) * (scarf ? 0.09 : 0.16) * (double)t;
            double drop = -(double)length * (double)t + (scarf ? 0.10 : 0.0) * Math.sin((double)t * 3.14);
            spine[i] = p(sway, drop, -0.05 - 0.16 * (double)t);
            widths[i] = halfWidth * (1.0f - 0.55f * t * t);
        }
        mesh.ribbonSided(spine, widths, cosmetic.color, cosmetic.accent, 245, AXIS_X);
        mesh.ribbonSided(spine, widths, cosmetic.color, cosmetic.accent, 235, AXIS_Z);
        return mesh;
    }

    /** Центр крепления аксессуара: голова, лицо, уши — сверху; аура — у ног. */
    public static boolean isHeadSlot(Cosmetic cosmetic) {
        return cosmetic != null && (Cosmetic.SLOT_HEAD.equals(cosmetic.slot) || Cosmetic.SLOT_EARS.equals(cosmetic.slot)
                || Cosmetic.SLOT_FACE.equals(cosmetic.slot));
    }
}
