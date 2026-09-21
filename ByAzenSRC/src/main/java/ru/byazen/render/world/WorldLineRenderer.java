/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_12249
 *  net.minecraft.class_1297
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
package ru.byazen.render.world;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_12249;
import net.minecraft.class_1297;
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

public final class WorldLineRenderer {
    private WorldLineRenderer() {
    }

    public static void draw(List<Segment> segments) {
        if (segments == null || segments.isEmpty()) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null) {
            return;
        }
        class_4184 camera = client.field_1773.method_19418();
        class_243 cameraPosition = camera.method_71156();
        Matrix4f matrix = new Matrix4f().rotation((Quaternionfc)camera.method_23767());
        class_287 buffer = class_289.method_1348().method_60827(VertexFormat.class_5596.field_29344, class_290.field_1576);
        for (Segment segment : segments) {
            WorldLineRenderer.vertex(buffer, matrix, cameraPosition, segment.start(), segment.startColor());
            WorldLineRenderer.vertex(buffer, matrix, cameraPosition, segment.end(), segment.endColor());
        }
        class_12249.method_76015().method_60895(buffer.method_60800());
    }

    public static void drawEntityBox(class_1297 entity, int color) {
        if (entity == null) {
            return;
        }
        class_238 box = entity.method_5829().method_1014(0.025);
        class_243 p000 = new class_243(box.field_1323, box.field_1322, box.field_1321);
        class_243 p001 = new class_243(box.field_1323, box.field_1322, box.field_1324);
        class_243 p010 = new class_243(box.field_1323, box.field_1325, box.field_1321);
        class_243 p011 = new class_243(box.field_1323, box.field_1325, box.field_1324);
        class_243 p100 = new class_243(box.field_1320, box.field_1322, box.field_1321);
        class_243 p101 = new class_243(box.field_1320, box.field_1322, box.field_1324);
        class_243 p110 = new class_243(box.field_1320, box.field_1325, box.field_1321);
        class_243 p111 = new class_243(box.field_1320, box.field_1325, box.field_1324);
        ArrayList<Segment> lines = new ArrayList<Segment>(12);
        WorldLineRenderer.add(lines, p000, p001, color);
        WorldLineRenderer.add(lines, p001, p101, color);
        WorldLineRenderer.add(lines, p101, p100, color);
        WorldLineRenderer.add(lines, p100, p000, color);
        WorldLineRenderer.add(lines, p010, p011, color);
        WorldLineRenderer.add(lines, p011, p111, color);
        WorldLineRenderer.add(lines, p111, p110, color);
        WorldLineRenderer.add(lines, p110, p010, color);
        WorldLineRenderer.add(lines, p000, p010, color);
        WorldLineRenderer.add(lines, p001, p011, color);
        WorldLineRenderer.add(lines, p100, p110, color);
        WorldLineRenderer.add(lines, p101, p111, color);
        WorldLineRenderer.draw(lines);
    }

    private static void add(List<Segment> lines, class_243 start, class_243 end, int color) {
        lines.add(new Segment(start, end, color, color));
    }

    private static void vertex(class_287 buffer, Matrix4f matrix, class_243 camera, class_243 point, int color) {
        buffer.method_22918((Matrix4fc)matrix, (float)(point.field_1352 - camera.field_1352), (float)(point.field_1351 - camera.field_1351), (float)(point.field_1350 - camera.field_1350)).method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24 & 0xFF);
    }

    public record Segment(class_243 start, class_243 end, int startColor, int endColor) {
    }
}

