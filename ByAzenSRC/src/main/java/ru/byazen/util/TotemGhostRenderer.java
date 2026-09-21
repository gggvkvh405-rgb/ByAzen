/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_12249
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_310
 *  net.minecraft.class_3532
 *  net.minecraft.class_4184
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.byazen.util;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_12249;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.byazen.event.TotemPopEvent;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.misc.TotemEffectRenderer;
import ru.byazen.util.TotemEffectSettings;

public final class TotemGhostRenderer
implements TotemEffectRenderer {
    private static final long LIFETIME_MS = 900L;
    private static final int LAYERS = 3;
    private static final int MAX_GHOSTS = 16;
    private static final class_243 FALLBACK_LOOK = new class_243(0.0, 0.0, 1.0);
    private final List<TotemGhost> ghosts = new ArrayList<TotemGhost>();
    private final TotemEffectSettings settings;

    public TotemGhostRenderer(TotemEffectSettings settings) {
        this.settings = settings;
    }

    @Override
    public void renderWorld(WorldRenderEvent event) {
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || client.field_1724 == null) {
            this.update2();
            return;
        }
        if (this.ghosts.isEmpty()) {
            return;
        }
        class_4184 camera = client.field_1773.method_19418();
        class_243 cameraPosition = camera.method_71156();
        Matrix4f matrix = new Matrix4f().rotation((Quaternionfc)camera.method_23767());
        class_287 fill = class_289.method_1348().method_60827(VertexFormat.class_5596.field_27382, class_290.field_1576);
        class_287 outline = class_289.method_1348().method_60827(VertexFormat.class_5596.field_29344, class_290.field_1576);
        long now = System.currentTimeMillis();
        Iterator<TotemGhost> iterator = this.ghosts.iterator();
        while (iterator.hasNext()) {
            TotemGhost ghost = iterator.next();
            if (now - ghost.spawnedAt() >= 900L) {
                iterator.remove();
                continue;
            }
            this.drawGhost(ghost, now, cameraPosition, matrix, fill, outline);
        }
        class_12249.method_76023().method_60895(fill.method_60800());
        class_12249.method_76668().method_60895(outline.method_60800());
    }

    private void drawGhost(TotemGhost ghost, long now, class_243 camera, Matrix4f matrix, class_287 fill, class_287 outline) {
        float progress = class_3532.method_15363((float)((float)(now - ghost.spawnedAt()) / 900.0f), (float)0.0f, (float)1.0f);
        int baseColor = this.settings.getIntType3();
        class_243 side = ghost.look().method_1027() > 1.0E-6 ? new class_243(-ghost.look().field_1350, 0.0, ghost.look().field_1352).method_1029() : new class_243(1.0, 0.0, 0.0);
        for (int layer = 2; layer >= 0; --layer) {
            float delayed = Math.max(0.0f, progress - (float)layer * 0.08f);
            float fade = (1.0f - delayed) * (1.0f - delayed) * (1.0f - (float)layer * 0.18f);
            if (fade <= 0.025f) continue;
            float eased = TotemGhostRenderer.easeOutCubic(delayed);
            double travel = 0.16 + (double)eased * 0.7 + (double)layer * 0.08;
            double wobble = Math.sin((double)ghost.phase() + (double)delayed * Math.PI * 2.0 + (double)layer * 0.35) * 0.08;
            double rise = 0.16 + (double)TotemGhostRenderer.sineIn(delayed) * 0.56 + (double)layer * 0.03;
            class_243 position = ghost.origin().method_1019(ghost.look().method_1021(travel)).method_1019(side.method_1021(wobble)).method_1031(0.0, rise, 0.0);
            float scale = 1.0f + (1.0f - delayed) * 0.06f + (float)layer * 0.012f;
            class_238 box = TotemGhostRenderer.box(position, ghost.width(), ghost.height(), scale);
            TotemGhostRenderer.drawFilledBox(fill, matrix, camera, box, TotemGhostRenderer.withAlpha(baseColor, Math.round(107.0f * fade)));
            TotemGhostRenderer.drawBoxOutline(outline, matrix, camera, box, TotemGhostRenderer.withAlpha(baseColor, Math.round(230.0f * fade)));
        }
    }

    @Override
    public void setTotemPopEvent(TotemPopEvent event) {
        class_1297 entity = event.getEntity();
        if (!(entity instanceof class_1657)) {
            return;
        }
        class_1657 player = (class_1657)entity;
        class_310 client = class_310.method_1551();
        class_243 look = client.field_1724 == null ? FALLBACK_LOOK : client.field_1724.method_5828(1.0f);
        class_243 vec3d = look;
        if (look.method_1027() <= 1.0E-6) {
            look = FALLBACK_LOOK;
        }
        class_238 bounds = player.method_5829();
        this.ghosts.add(new TotemGhost(event.toVec3d(), look.method_1029(), bounds.field_1320 - bounds.field_1323, player.method_17682(), event.getLongType(), ThreadLocalRandom.current().nextFloat() * ((float)Math.PI * 2)));
        int overflow = this.ghosts.size() - 16;
        if (overflow > 0) {
            this.ghosts.subList(0, overflow).clear();
        }
    }

    @Override
    public void update2() {
        this.ghosts.clear();
    }

    private static class_238 box(class_243 position, double width, double height, float scale) {
        double halfWidth = width * (double)scale * 0.5;
        return new class_238(position.field_1352 - halfWidth, position.field_1351, position.field_1350 - halfWidth, position.field_1352 + halfWidth, position.field_1351 + height * (double)scale, position.field_1350 + halfWidth);
    }

    private static float sineIn(float value) {
        return (float)Math.sin((double)class_3532.method_15363((float)value, (float)0.0f, (float)1.0f) * Math.PI * 0.5);
    }

    private static float easeOutCubic(float value) {
        float inverse = 1.0f - class_3532.method_15363((float)value, (float)0.0f, (float)1.0f);
        return 1.0f - inverse * inverse * inverse;
    }

    private static int withAlpha(int color, int alpha) {
        return color & 0xFFFFFF | Math.clamp((long)alpha, 0, 255) << 24;
    }

    private static void drawFilledBox(class_287 vertices, Matrix4f matrix, class_243 camera, class_238 box, int color) {
        TotemGhostRenderer.quad(vertices, matrix, camera, box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1324, box.field_1323, box.field_1322, box.field_1324, color);
        TotemGhostRenderer.quad(vertices, matrix, camera, box.field_1323, box.field_1325, box.field_1321, box.field_1323, box.field_1325, box.field_1324, box.field_1320, box.field_1325, box.field_1324, box.field_1320, box.field_1325, box.field_1321, color);
        TotemGhostRenderer.quad(vertices, matrix, camera, box.field_1323, box.field_1322, box.field_1321, box.field_1323, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1321, box.field_1320, box.field_1322, box.field_1321, color);
        TotemGhostRenderer.quad(vertices, matrix, camera, box.field_1323, box.field_1322, box.field_1324, box.field_1320, box.field_1322, box.field_1324, box.field_1320, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1324, color);
        TotemGhostRenderer.quad(vertices, matrix, camera, box.field_1323, box.field_1322, box.field_1321, box.field_1323, box.field_1322, box.field_1324, box.field_1323, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1321, color);
        TotemGhostRenderer.quad(vertices, matrix, camera, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1324, box.field_1320, box.field_1322, box.field_1324, color);
    }

    private static void drawBoxOutline(class_287 vertices, Matrix4f matrix, class_243 camera, class_238 box, int color) {
        TotemGhostRenderer.line(vertices, matrix, camera, box.field_1323, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1321, color);
        TotemGhostRenderer.line(vertices, matrix, camera, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1322, box.field_1324, color);
        TotemGhostRenderer.line(vertices, matrix, camera, box.field_1320, box.field_1322, box.field_1324, box.field_1323, box.field_1322, box.field_1324, color);
        TotemGhostRenderer.line(vertices, matrix, camera, box.field_1323, box.field_1322, box.field_1324, box.field_1323, box.field_1322, box.field_1321, color);
        TotemGhostRenderer.line(vertices, matrix, camera, box.field_1323, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1321, color);
        TotemGhostRenderer.line(vertices, matrix, camera, box.field_1320, box.field_1325, box.field_1321, box.field_1320, box.field_1325, box.field_1324, color);
        TotemGhostRenderer.line(vertices, matrix, camera, box.field_1320, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1324, color);
        TotemGhostRenderer.line(vertices, matrix, camera, box.field_1323, box.field_1325, box.field_1324, box.field_1323, box.field_1325, box.field_1321, color);
        TotemGhostRenderer.line(vertices, matrix, camera, box.field_1323, box.field_1322, box.field_1321, box.field_1323, box.field_1325, box.field_1321, color);
        TotemGhostRenderer.line(vertices, matrix, camera, box.field_1320, box.field_1322, box.field_1321, box.field_1320, box.field_1325, box.field_1321, color);
        TotemGhostRenderer.line(vertices, matrix, camera, box.field_1320, box.field_1322, box.field_1324, box.field_1320, box.field_1325, box.field_1324, color);
        TotemGhostRenderer.line(vertices, matrix, camera, box.field_1323, box.field_1322, box.field_1324, box.field_1323, box.field_1325, box.field_1324, color);
    }

    private static void quad(class_287 vertices, Matrix4f matrix, class_243 camera, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, double x4, double y4, double z4, int color) {
        TotemGhostRenderer.vertex(vertices, matrix, camera, x1, y1, z1, color);
        TotemGhostRenderer.vertex(vertices, matrix, camera, x2, y2, z2, color);
        TotemGhostRenderer.vertex(vertices, matrix, camera, x3, y3, z3, color);
        TotemGhostRenderer.vertex(vertices, matrix, camera, x4, y4, z4, color);
    }

    private static void line(class_287 vertices, Matrix4f matrix, class_243 camera, double x1, double y1, double z1, double x2, double y2, double z2, int color) {
        TotemGhostRenderer.vertex(vertices, matrix, camera, x1, y1, z1, color);
        TotemGhostRenderer.vertex(vertices, matrix, camera, x2, y2, z2, color);
    }

    private static void vertex(class_287 vertices, Matrix4f matrix, class_243 camera, double x, double y, double z, int color) {
        vertices.method_22918((Matrix4fc)matrix, (float)(x - camera.field_1352), (float)(y - camera.field_1351), (float)(z - camera.field_1350)).method_1336(color >> 16 & 0xFF, color >> 8 & 0xFF, color & 0xFF, color >>> 24 & 0xFF);
    }

    private record TotemGhost(class_243 origin, class_243 look, double width, double height, long spawnedAt, float phase) {
    }
}

