/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_12249
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.wexside.render.world;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.class_12249;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.wexside.util.ColorUtils;

public final class ConsumableAreaRenderer {
    private static final int CYLINDER_SEGMENTS = 64;

    private ConsumableAreaRenderer() {
    }

    public static void renderBox(class_238 box, int fillStart, int fillEnd, int outlineStart, int outlineEnd, long timeMillis) {
        RenderFrame frame = ConsumableAreaRenderer.beginFrame();
        if (frame == null) {
            return;
        }
        int fillColor = ConsumableAreaRenderer.animatedColor(fillStart, fillEnd, timeMillis);
        int outlineColor = ConsumableAreaRenderer.animatedColor(outlineStart, outlineEnd, timeMillis);
        class_287 fill = class_289.method_1348().method_60827(VertexFormat.class_5596.field_27382, class_290.field_1576);
        class_287 lines = class_289.method_1348().method_60827(VertexFormat.class_5596.field_29344, class_290.field_1576);
        ConsumableAreaRenderer.filledBox(fill, frame, box, fillColor);
        ConsumableAreaRenderer.outlinedBox(lines, frame, box, outlineColor);
        class_12249.method_76023().method_60895(fill.method_60800());
        class_12249.method_76015().method_60895(lines.method_60800());
    }

    public static void renderCylinder(double x, double y, double z, double radius, double height, int fillStart, int fillEnd, int outlineStart, int outlineEnd, long timeMillis) {
        RenderFrame frame = ConsumableAreaRenderer.beginFrame();
        if (frame == null) {
            return;
        }
        int fillColor = ConsumableAreaRenderer.animatedColor(fillStart, fillEnd, timeMillis);
        int outlineColor = ConsumableAreaRenderer.animatedColor(outlineStart, outlineEnd, timeMillis);
        class_287 fill = class_289.method_1348().method_60827(VertexFormat.class_5596.field_27382, class_290.field_1576);
        class_287 lines = class_289.method_1348().method_60827(VertexFormat.class_5596.field_29344, class_290.field_1576);
        for (int segment = 0; segment < 64; ++segment) {
            double a = Math.PI * 2 * (double)segment / 64.0;
            double b = Math.PI * 2 * (double)(segment + 1) / 64.0;
            double x1 = x + Math.cos(a) * radius;
            double z1 = z + Math.sin(a) * radius;
            double x2 = x + Math.cos(b) * radius;
            double z2 = z + Math.sin(b) * radius;
            ConsumableAreaRenderer.quad(fill, frame, x, y, z, x1, y, z1, x2, y, z2, x, y, z, fillColor);
            ConsumableAreaRenderer.quad(fill, frame, x, y + height, z, x, y + height, z, x2, y + height, z2, x1, y + height, z1, fillColor);
            ConsumableAreaRenderer.quad(fill, frame, x1, y, z1, x1, y + height, z1, x2, y + height, z2, x2, y, z2, fillColor);
            ConsumableAreaRenderer.line(lines, frame, x1, y, z1, x2, y, z2, outlineColor);
            ConsumableAreaRenderer.line(lines, frame, x1, y + height, z1, x2, y + height, z2, outlineColor);
            if ((segment & 7) != 0) continue;
            ConsumableAreaRenderer.line(lines, frame, x1, y, z1, x1, y + height, z1, outlineColor);
        }
        class_12249.method_76023().method_60895(fill.method_60800());
        class_12249.method_76015().method_60895(lines.method_60800());
    }

    private static RenderFrame beginFrame() {
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null) {
            return null;
        }
        class_4184 camera = client.field_1773.method_19418();
        return new RenderFrame(new Matrix4f().rotation((Quaternionfc)camera.method_23767()), camera.method_71156());
    }

    private static int animatedColor(int start, int end, long timeMillis) {
        float phase = (float)((Math.sin((double)timeMillis * 0.003) + 1.0) * 0.5);
        return ColorUtils.lerp(start, end, phase);
    }

    private static void filledBox(class_287 out, RenderFrame frame, class_238 box, int color) {
        ConsumableAreaRenderer.quad(out, frame, box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1324, box.field_1323, box.field_1322, box.field_1324, color);
        ConsumableAreaRenderer.quad(out, frame, box.field_1323, box.field_1325, box.field_1321, box.field_1323, box.field_1325, box.field_1324, box.field_1320, box.field_1325, box.field_1324, box.field_1320, box.field_1325, box.field_1321, color);
        ConsumableAreaRenderer.quad(out, frame, box.field_1323, box.field_1322, box.field_1321, box.field_1323, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1321, box.field_1320, box.field_1322, box.field_1321, color);
        ConsumableAreaRenderer.quad(out, frame, box.field_1323, box.field_1322, box.field_1324, box.field_1320, box.field_1322, box.field_1324, box.field_1320, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1324, color);
        ConsumableAreaRenderer.quad(out, frame, box.field_1323, box.field_1322, box.field_1321, box.field_1323, box.field_1322, box.field_1324, box.field_1323, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1321, color);
        ConsumableAreaRenderer.quad(out, frame, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1324, box.field_1320, box.field_1322, box.field_1324, color);
    }

    private static void outlinedBox(class_287 out, RenderFrame frame, class_238 box, int color) {
        ConsumableAreaRenderer.line(out, frame, box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1321, color);
        ConsumableAreaRenderer.line(out, frame, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1324, color);
        ConsumableAreaRenderer.line(out, frame, box.field_1320, box.field_1322, box.field_1324, box.field_1323, box.field_1322, box.field_1324, color);
        ConsumableAreaRenderer.line(out, frame, box.field_1323, box.field_1322, box.field_1324, box.field_1323, box.field_1322, box.field_1321, color);
        ConsumableAreaRenderer.line(out, frame, box.field_1323, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1321, color);
        ConsumableAreaRenderer.line(out, frame, box.field_1320, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1324, color);
        ConsumableAreaRenderer.line(out, frame, box.field_1320, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1324, color);
        ConsumableAreaRenderer.line(out, frame, box.field_1323, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1321, color);
        ConsumableAreaRenderer.line(out, frame, box.field_1323, box.field_1322, box.field_1321, box.field_1323, box.field_1325, box.field_1321, color);
        ConsumableAreaRenderer.line(out, frame, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1321, color);
        ConsumableAreaRenderer.line(out, frame, box.field_1320, box.field_1322, box.field_1324, box.field_1320, box.field_1325, box.field_1324, color);
        ConsumableAreaRenderer.line(out, frame, box.field_1323, box.field_1322, box.field_1324, box.field_1323, box.field_1325, box.field_1324, color);
    }

    private static void quad(class_287 out, RenderFrame frame, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, int color) {
        ConsumableAreaRenderer.vertex(out, frame, x1, y1, z1, color);
        ConsumableAreaRenderer.vertex(out, frame, x2, y2, z2, color);
        ConsumableAreaRenderer.vertex(out, frame, x3, y3, z3, color);
        ConsumableAreaRenderer.vertex(out, frame, x4, y4, z4, color);
    }

    private static void line(class_287 out, RenderFrame frame, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        ConsumableAreaRenderer.vertex(out, frame, x1, y1, z1, color);
        ConsumableAreaRenderer.vertex(out, frame, x2, y2, z2, color);
    }

    private static void vertex(class_287 out, RenderFrame frame, double x, double y, double z, int color) {
        class_243 camera = frame.cameraPosition();
        out.method_22918((Matrix4fc)frame.matrix(), (float)(x - camera.field_1352), (float)(y - camera.field_1351), (float)(z - camera.field_1350)).method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24 & 0xFF);
    }

    private record RenderFrame(Matrix4f matrix, class_243 cameraPosition) {
    }
}

