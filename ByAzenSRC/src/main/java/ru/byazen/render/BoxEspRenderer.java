/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_4588
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.byazen.render;

import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_4588;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class BoxEspRenderer {
    private BoxEspRenderer() {
    }

    public static void fill(class_4588 consumer, Matrix4f matrix, class_243 camera, class_238 box, int color) {
        float x0 = (float)(box.field_1323 - camera.field_1352);
        float y0 = (float)(box.field_1322 - camera.field_1351);
        float z0 = (float)(box.field_1321 - camera.field_1350);
        float x1 = (float)(box.field_1320 - camera.field_1352);
        float y1 = (float)(box.field_1325 - camera.field_1351);
        float z1 = (float)(box.field_1324 - camera.field_1350);
        BoxEspRenderer.quad(consumer, matrix, color, x0, y0, z0, x1, y0, z0, x1, y0, z1, x0, y0, z1);
        BoxEspRenderer.quad(consumer, matrix, color, x0, y1, z1, x1, y1, z1, x1, y1, z0, x0, y1, z0);
        BoxEspRenderer.quad(consumer, matrix, color, x0, y0, z1, x1, y0, z1, x1, y1, z1, x0, y1, z1);
        BoxEspRenderer.quad(consumer, matrix, color, x1, y0, z0, x0, y0, z0, x0, y1, z0, x1, y1, z0);
        BoxEspRenderer.quad(consumer, matrix, color, x0, y0, z0, x0, y0, z1, x0, y1, z1, x0, y1, z0);
        BoxEspRenderer.quad(consumer, matrix, color, x1, y0, z1, x1, y0, z0, x1, y1, z0, x1, y1, z1);
    }

    public static void outline(class_4588 consumer, Matrix4f matrix, class_243 camera, class_238 box, int color) {
        float x0 = (float)(box.field_1323 - camera.field_1352);
        float y0 = (float)(box.field_1322 - camera.field_1351);
        float z0 = (float)(box.field_1321 - camera.field_1350);
        float x1 = (float)(box.field_1320 - camera.field_1352);
        float y1 = (float)(box.field_1325 - camera.field_1351);
        float z1 = (float)(box.field_1324 - camera.field_1350);
        BoxEspRenderer.line(consumer, matrix, color, x0, y0, z0, x1, y0, z0);
        BoxEspRenderer.line(consumer, matrix, color, x1, y0, z0, x1, y0, z1);
        BoxEspRenderer.line(consumer, matrix, color, x1, y0, z1, x0, y0, z1);
        BoxEspRenderer.line(consumer, matrix, color, x0, y0, z1, x0, y0, z0);
        BoxEspRenderer.line(consumer, matrix, color, x0, y1, z0, x1, y1, z0);
        BoxEspRenderer.line(consumer, matrix, color, x1, y1, z0, x1, y1, z1);
        BoxEspRenderer.line(consumer, matrix, color, x1, y1, z1, x0, y1, z1);
        BoxEspRenderer.line(consumer, matrix, color, x0, y1, z1, x0, y1, z0);
        BoxEspRenderer.line(consumer, matrix, color, x0, y0, z0, x0, y1, z0);
        BoxEspRenderer.line(consumer, matrix, color, x1, y0, z0, x1, y1, z0);
        BoxEspRenderer.line(consumer, matrix, color, x1, y0, z1, x1, y1, z1);
        BoxEspRenderer.line(consumer, matrix, color, x0, y0, z1, x0, y1, z1);
    }

    public static void animatedOutline(class_4588 consumer, Matrix4f matrix, class_243 camera, class_238 box, int color, long timeMillis) {
        float pulse = 0.65f + 0.35f * (float)Math.sin((double)timeMillis * 0.006);
        int alpha = Math.max(0, Math.min(255, Math.round((float)(color >>> 24 & 0xFF) * pulse)));
        BoxEspRenderer.outline(consumer, matrix, camera, box, color & 0xFFFFFF | alpha << 24);
    }

    private static void quad(class_4588 consumer, Matrix4f matrix, int color, float ... xyz) {
        for (int index = 0; index < xyz.length; index += 3) {
            consumer.method_22918((Matrix4fc)matrix, xyz[index], xyz[index + 1], xyz[index + 2]).method_39415(color);
        }
    }

    private static void line(class_4588 consumer, Matrix4f matrix, int color, float x0, float y0, float z0, float x1, float y1, float z1) {
        float dx = x1 - x0;
        float dy = y1 - y0;
        float dz = z1 - z0;
        float length = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length == 0.0f) {
            return;
        }
        consumer.method_22918((Matrix4fc)matrix, x0, y0, z0).method_39415(color).method_22914(dx /= length, dy /= length, dz /= length).method_75298(1.0f);
        consumer.method_22918((Matrix4fc)matrix, x1, y1, z1).method_39415(color).method_22914(dx, dy, dz).method_75298(1.0f);
    }
}

