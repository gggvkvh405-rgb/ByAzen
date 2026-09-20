/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.minecraft.class_10017
 *  net.minecraft.class_10071
 *  net.minecraft.class_10366
 *  net.minecraft.class_1044
 *  net.minecraft.class_11278
 *  net.minecraft.class_12075
 *  net.minecraft.class_12249
 *  net.minecraft.class_1299
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_2561
 *  net.minecraft.class_276
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_327
 *  net.minecraft.class_327$class_6415
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_5348
 *  net.minecraft.class_6367
 *  net.minecraft.class_8113$class_8123
 *  net.minecraft.class_8113$class_8123$class_8124
 *  net.minecraft.class_8113$class_8123$class_8125
 *  net.minecraft.class_8113$class_8123$class_8126
 *  net.minecraft.class_8113$class_8123$class_8230
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fStack
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package ru.wexside.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.class_10017;
import net.minecraft.class_10071;
import net.minecraft.class_10366;
import net.minecraft.class_1044;
import net.minecraft.class_11278;
import net.minecraft.class_12075;
import net.minecraft.class_12249;
import net.minecraft.class_1299;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_2561;
import net.minecraft.class_276;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_327;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_5348;
import net.minecraft.class_6367;
import net.minecraft.class_8113;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.wexside.module.misc.HologramOptimizerModule;
import ru.wexside.render.FramebufferTextureAdapter;

public final class HologramImpostorRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(HologramImpostorRenderer.class);
    private static final class_2960 CACHE_TEXTURE_ID = class_2960.method_60655((String)"wexside", (String)"hologram_impostor");
    private static final int MAX_CACHE_ENTRIES = 256;
    private static final int MAX_TEXTURE_SIZE = 2048;
    private static final int FULL_BRIGHT_LIGHT = 0xF000F0;
    private static final Matrix4f DRAW_MATRIX = new Matrix4f();
    private static final Map<CacheKey, CachedImpostor> CACHE = new HashMap<CacheKey, CachedImpostor>();
    private static final List<RenderRequest> REQUESTS = new ArrayList<RenderRequest>();
    private static final FramebufferTextureAdapter CACHE_TEXTURE = new FramebufferTextureAdapter();
    private static class_11278 projection;
    private static class_4597.class_4598 offscreenConsumers;
    private static boolean textureRegistered;
    private static boolean failed;

    private HologramImpostorRenderer() {
    }

    public static boolean captureEntityLabel(class_10017 state, class_4587 matrices, class_12075 camera) {
        if (!HologramImpostorRenderer.canCapture() || state == null || matrices == null || camera == null) {
            return false;
        }
        class_2561 text = state.field_53337;
        class_243 labelPosition = state.field_53338;
        if (text == null || labelPosition == null || !HologramImpostorRenderer.isHologramEntity(state.field_58171)) {
            return false;
        }
        if (state.field_53332 > HologramOptimizerModule.maxDistanceSquared()) {
            return true;
        }
        class_327 textRenderer = class_310.method_1551().field_1772;
        int width = Math.max(1, textRenderer.method_27525((class_5348)text));
        int height = 10;
        int background = HologramImpostorRenderer.defaultBackgroundColor();
        Matrix4f transform = new Matrix4f((Matrix4fc)matrices.method_23760().method_23761()).translate((float)labelPosition.field_1352, (float)labelPosition.field_1351 + 0.5f, (float)labelPosition.field_1350).rotate((Quaternionfc)camera.field_63081).scale(0.025f, -0.025f, 0.025f).translate((float)(-width) / 2.0f, 0.0f, 0.0f);
        TextDrawer drawer = (renderer, consumers) -> renderer.method_27522(text, 0.0f, 0.0f, -1, false, DRAW_MATRIX, consumers, class_327.class_6415.field_33993, 0, 0xF000F0);
        REQUESTS.add(new RenderRequest(transform, new CacheKey(text, background, 0), width, height, background, 255, true, drawer));
        return true;
    }

    public static boolean captureTextDisplay(class_10071 state, class_4587 matrices, float tickProgress) {
        if (!HologramImpostorRenderer.canCapture() || state == null || matrices == null) {
            return false;
        }
        class_8113.class_8123.class_8125 textLines = state.field_53589;
        class_8113.class_8123.class_8230 data = state.field_53588;
        if (textLines == null || textLines.comp_1247().isEmpty() || data == null) {
            return false;
        }
        if (state.field_53332 > HologramOptimizerModule.maxDistanceSquared()) {
            return true;
        }
        int opacity = data.comp_1336().method_48889(tickProgress) & 0xFF;
        if (opacity == 0) {
            return true;
        }
        byte flags = data.comp_1338();
        int background = (flags & 4) != 0 ? HologramImpostorRenderer.defaultBackgroundColor() : data.comp_1337().method_48889(tickProgress);
        int width = Math.max(1, textLines.comp_1248());
        int height = Math.max(1, textLines.comp_1247().size() * 10);
        Matrix4f transform = new Matrix4f((Matrix4fc)matrices.method_23760().method_23761()).rotate((float)Math.PI, 0.0f, 1.0f, 0.0f).scale(-0.025f).translate(1.0f - (float)width / 2.0f, -((float)height - 1.0f), 0.0f);
        class_8113.class_8123.class_8124 alignment = class_8113.class_8123.method_48902((byte)flags);
        boolean shadow = (flags & 1) != 0;
        List<class_8113.class_8123.class_8126> lines = List.copyOf(textLines.comp_1247());
        TextDrawer drawer = (renderer, consumers) -> {
            float y = 0.0f;
            for (class_8113.class_8123.class_8126 line : lines) {
                float x = HologramImpostorRenderer.alignedX(alignment, width, line.comp_1250());
                renderer.method_22942(line.comp_1249(), x, y, -1, shadow, DRAW_MATRIX, consumers, class_327.class_6415.field_33993, 0, 0xF000F0);
                y += 10.0f;
            }
        };
        boolean seeThrough = (flags & 2) != 0;
        REQUESTS.add(new RenderRequest(transform, new CacheKey(textLines, background, flags), width, height, background, opacity, seeThrough, drawer));
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void flush() {
        if (failed || !HologramOptimizerModule.isEnabled()) {
            HologramImpostorRenderer.clear();
            return;
        }
        if (REQUESTS.isEmpty()) {
            return;
        }
        try {
            HologramImpostorRenderer.registerTexture();
            for (RenderRequest request : REQUESTS) {
                CACHE.computeIfAbsent(request.key(), ignored -> HologramImpostorRenderer.createImpostor(request));
            }
            class_4597.class_4598 consumers = class_310.method_1551().method_22940().method_23000();
            for (RenderRequest request : REQUESTS) {
                CachedImpostor cached = CACHE.get(request.key());
                if (cached == null) continue;
                HologramImpostorRenderer.drawCached(consumers, request, cached);
            }
            HologramImpostorRenderer.evictUnusedEntries();
        }
        catch (Throwable throwable) {
            failed = true;
            LOGGER.warn("Hologram impostor renderer disabled after a rendering failure", throwable);
            HologramImpostorRenderer.clear();
        }
        finally {
            REQUESTS.clear();
        }
    }

    public static void clear() {
        for (CachedImpostor cached : CACHE.values()) {
            cached.framebuffer().method_1238();
        }
        CACHE.clear();
        REQUESTS.clear();
        failed = false;
    }

    private static boolean canCapture() {
        return !failed && HologramOptimizerModule.isEnabled() && RenderSystem.tryGetDevice() != null;
    }

    private static boolean isHologramEntity(class_1299<?> type) {
        return type == class_1299.field_6131 || type == class_1299.field_6052 || type == class_1299.field_42456;
    }

    private static void registerTexture() {
        if (textureRegistered) {
            return;
        }
        class_310.method_1551().method_1531().method_4616(CACHE_TEXTURE_ID, (class_1044)CACHE_TEXTURE);
        textureRegistered = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static CachedImpostor createImpostor(RenderRequest request) {
        int quality;
        for (quality = HologramOptimizerModule.qualityScale(); quality > 1 && ((long)request.width() * (long)quality > 2048L || (long)request.height() * (long)quality > 2048L); --quality) {
        }
        class_6367 framebuffer = new class_6367("wexside_hologram_impostor", request.width() * quality, request.height() * quality, true);
        boolean complete = false;
        try {
            CachedImpostor cachedImpostor;
            RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(framebuffer.method_30277(), 0, framebuffer.method_30278(), 1.0);
            class_4597.class_4598 consumers = HologramImpostorRenderer.offscreenConsumers();
            GpuBufferSlice projectionSlice = HologramImpostorRenderer.projection().method_71092((float)request.width(), (float)request.height());
            RenderSystem.backupProjectionMatrix();
            try {
                RenderSystem.setProjectionMatrix((GpuBufferSlice)projectionSlice, (class_10366)class_10366.field_54954);
                Matrix4fStack modelView = RenderSystem.getModelViewStack();
                modelView.pushMatrix();
                try {
                    modelView.identity();
                    GpuTextureView previousColor = RenderSystem.outputColorTextureOverride;
                    GpuTextureView previousDepth = RenderSystem.outputDepthTextureOverride;
                    RenderSystem.outputColorTextureOverride = framebuffer.method_71639();
                    RenderSystem.outputDepthTextureOverride = framebuffer.method_71640();
                    try {
                        HologramImpostorRenderer.drawBackground(consumers, request.width(), request.height(), request.backgroundColor());
                        request.drawer().draw(class_310.method_1551().field_1772, (class_4597)consumers);
                        consumers.method_22993();
                    }
                    finally {
                        RenderSystem.outputColorTextureOverride = previousColor;
                        RenderSystem.outputDepthTextureOverride = previousDepth;
                    }
                }
                finally {
                    modelView.popMatrix();
                }
            }
            finally {
                RenderSystem.restoreProjectionMatrix();
            }
            complete = true;
            CachedImpostor cachedImpostor2 = cachedImpostor = new CachedImpostor((class_276)framebuffer, request.width(), request.height());
            return cachedImpostor2;
        }
        finally {
            if (!complete) {
                framebuffer.method_1238();
            }
        }
    }

    private static void drawBackground(class_4597.class_4598 consumers, int width, int height, int color) {
        if (color >>> 24 == 0) {
            return;
        }
        class_1921 layer = class_12249.method_76023();
        class_4588 vertices = consumers.method_73477(layer);
        vertices.method_22918((Matrix4fc)DRAW_MATRIX, 0.0f, 0.0f, 0.01f).method_39415(color);
        vertices.method_22918((Matrix4fc)DRAW_MATRIX, 0.0f, (float)height, 0.01f).method_39415(color);
        vertices.method_22918((Matrix4fc)DRAW_MATRIX, (float)width, (float)height, 0.01f).method_39415(color);
        vertices.method_22918((Matrix4fc)DRAW_MATRIX, (float)width, 0.0f, 0.01f).method_39415(color);
        consumers.method_22994(layer);
    }

    private static void drawCached(class_4597.class_4598 consumers, RenderRequest request, CachedImpostor cached) {
        CACHE_TEXTURE.bind(cached.framebuffer());
        class_1921 layer = request.seeThrough() ? class_12249.method_76000((class_2960)CACHE_TEXTURE_ID) : class_12249.method_75994((class_2960)CACHE_TEXTURE_ID);
        class_4588 vertices = consumers.method_73477(layer);
        Matrix4f transform = request.transform();
        int color = request.alpha() << 24 | 0xFFFFFF;
        float width = cached.width();
        float height = cached.height();
        HologramImpostorRenderer.vertex(vertices, transform, 0.0f, 0.0f, 0.0f, 1.0f, color);
        HologramImpostorRenderer.vertex(vertices, transform, width, 0.0f, 1.0f, 1.0f, color);
        HologramImpostorRenderer.vertex(vertices, transform, width, height, 1.0f, 0.0f, color);
        HologramImpostorRenderer.vertex(vertices, transform, 0.0f, 0.0f, 0.0f, 1.0f, color);
        HologramImpostorRenderer.vertex(vertices, transform, width, height, 1.0f, 0.0f, color);
        HologramImpostorRenderer.vertex(vertices, transform, 0.0f, height, 0.0f, 0.0f, color);
        consumers.method_22994(layer);
    }

    private static void vertex(class_4588 vertices, Matrix4f matrix, float x, float y, float u, float v, int color) {
        vertices.method_22918((Matrix4fc)matrix, x, y, 0.0f).method_22913(u, v).method_39415(color);
    }

    private static void evictUnusedEntries() {
        if (CACHE.size() <= 256) {
            return;
        }
        HashSet<CacheKey> active = new HashSet<CacheKey>();
        for (RenderRequest request : REQUESTS) {
            active.add(request.key());
        }
        Iterator<Map.Entry<CacheKey, CachedImpostor>> iterator = CACHE.entrySet().iterator();
        while (CACHE.size() > 256 && iterator.hasNext()) {
            Map.Entry<CacheKey, CachedImpostor> entry = iterator.next();
            if (active.contains(entry.getKey())) continue;
            entry.getValue().framebuffer().method_1238();
            iterator.remove();
        }
    }

    private static class_11278 projection() {
        if (projection == null) {
            projection = new class_11278("wexside_hologram_projection", -1000.0f, 1000.0f, true);
        }
        return projection;
    }

    private static class_4597.class_4598 offscreenConsumers() {
        if (offscreenConsumers == null) {
            offscreenConsumers = class_4597.method_22991((class_9799)new class_9799(0x100000));
        }
        return offscreenConsumers;
    }

    private static int defaultBackgroundColor() {
        class_310 client = class_310.method_1551();
        if (client.field_1690 == null) {
            return 0;
        }
        return (int)(client.field_1690.method_19343(0.25f) * 255.0f) << 24;
    }

    private static float alignedX(class_8113.class_8123.class_8124 alignment, int lineWidth, int textWidth) {
        return switch (alignment) {
            default -> throw new MatchException(null, null);
            case class_8113.class_8123.class_8124.field_42451 -> 0.0f;
            case class_8113.class_8123.class_8124.field_42452 -> lineWidth - textWidth;
            case class_8113.class_8123.class_8124.field_42450 -> (float)(lineWidth - textWidth) / 2.0f;
        };
    }

    @FunctionalInterface
    private static interface TextDrawer {
        public void draw(class_327 var1, class_4597 var2);
    }

    private record RenderRequest(Matrix4f transform, CacheKey key, int width, int height, int backgroundColor, int alpha, boolean seeThrough, TextDrawer drawer) {
    }

    private record CacheKey(Object content, int backgroundColor, int flags) {
    }

    private record CachedImpostor(class_276 framebuffer, float width, float height) {
    }
}

