/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_12249
 *  net.minecraft.class_1297
 *  net.minecraft.class_1921
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_287
 *  net.minecraft.class_289
 *  net.minecraft.class_290
 *  net.minecraft.class_310
 *  net.minecraft.class_4588
 *  net.minecraft.class_4604
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 *  net.minecraft.class_9801
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.byazen.util;

import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_12249;
import net.minecraft.class_1297;
import net.minecraft.class_1921;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_310;
import net.minecraft.class_4588;
import net.minecraft.class_4604;
import net.minecraft.class_638;
import net.minecraft.class_746;
import net.minecraft.class_9801;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.byazen.event.EventBus;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.misc.ChamsRenderer;
import ru.byazen.misc.GlowEspRenderer;
import ru.byazen.misc.WorldBoxSettings;
import ru.byazen.model.esp.EspTargetClassifier;
import ru.byazen.model.esp.EspTargetType;
import ru.byazen.render.BoxEspEntry;
import ru.byazen.render.BoxEspRenderer;
import ru.byazen.render.RenderCamera;
import ru.byazen.render.RenderProjection;
import ru.byazen.render.WorldSkeletonRenderer;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.EspFeatureRegistry;
import ru.byazen.util.NameplateRenderer;
import ru.byazen.util.ServerItemCooldownOverlay;

public final class EspRenderCoordinator {
    private final WorldSkeletonRenderer worldSkeletonRenderer;
    private final class_310 mc = class_310.method_1551();
    private final ChamsRenderer chamsRenderer;
    private final NameplateRenderer nameplateRenderer;
    private final GlowEspRenderer glowEspRenderer;
    private final EspFeatureRegistry espFeatures;
    private static final float BOX_FILL_ALPHA = 0.25f;
    private final ServerItemCooldownOverlay serverItemCooldownOverlay;

    public EspRenderCoordinator(EventBus eventBus, EspFeatureRegistry espFeatures) {
        this.espFeatures = espFeatures;
        eventBus.subscribe(WorldRenderEvent.class, this::renderWorldBoxes);
        this.nameplateRenderer = new NameplateRenderer(eventBus, espFeatures);
        this.serverItemCooldownOverlay = new ServerItemCooldownOverlay(eventBus, espFeatures);
        this.chamsRenderer = new ChamsRenderer(eventBus, espFeatures);
        this.glowEspRenderer = new GlowEspRenderer(eventBus, espFeatures);
        this.worldSkeletonRenderer = new WorldSkeletonRenderer(eventBus, espFeatures);
    }

    private void renderWorldBoxes(WorldRenderEvent event) {
        if (!this.espFeatures.hasEnabledWorldBox()) {
            return;
        }
        class_746 player2 = this.mc.field_1724;
        class_638 world2 = this.mc.field_1687;
        class_243 vec = RenderCamera.position();
        if (player2 == null || world2 == null || vec == null) {
            return;
        }
        class_4604 frustum2 = RenderProjection.frustum();
        float tickProgress = event.getFloatType();
        ArrayList<BoxEspEntry> entries = new ArrayList<BoxEspEntry>();
        for (class_1297 entity : world2.method_18112()) {
            WorldBoxSettings settings;
            EspTargetType targetType;
            if (!entity.method_5805() || (targetType = EspTargetClassifier.targetType(entity, player2)) == null || (settings = this.espFeatures.getWorldBoxSettings(targetType, EspTargetClassifier.relation(entity))) == null || !settings.isEnabled() || !RenderProjection.isVisible(entity, frustum2)) continue;
            class_238 box = this.getExpandedBoundingBox(entity, tickProgress, this.getExpansion(settings.getScale()));
            entries.add(new BoxEspEntry(box, settings.getColor(), settings.isDottedStyle(), settings.isDepthTestEnabled()));
        }
        if (entries.isEmpty()) {
            return;
        }
        Matrix4f matrix = new Matrix4f((Matrix4fc)event.getMatrices().method_23760().method_23761());
        long animationTime = System.currentTimeMillis();
        this.renderEntries(entries, true, matrix, vec, animationTime);
        this.renderEntries(entries, false, matrix, vec, animationTime);
    }

    private void renderEntries(List<BoxEspEntry> entries, boolean depthTest, Matrix4f matrix, class_243 cameraPosition, long animationTime) {
        if (entries.stream().noneMatch(entry -> entry.depthTest() == depthTest)) {
            return;
        }
        class_1921 fillLayer = class_12249.method_76023();
        class_287 fillBuffer = class_289.method_1348().method_60827(VertexFormat.class_5596.field_27382, class_290.field_1576);
        for (BoxEspEntry entry2 : entries) {
            if (entry2.depthTest() != depthTest) continue;
            BoxEspRenderer.fill((class_4588)fillBuffer, matrix, cameraPosition, entry2.box(), ColorUtils.multiplyAlpha(entry2.color(), 0.25f));
        }
        class_9801 builtFill = fillBuffer.method_60794();
        if (builtFill != null) {
            fillLayer.method_60895(builtFill);
        }
        class_1921 outlineLayer = depthTest ? class_12249.method_76668() : class_12249.method_76015();
        class_287 outlineBuffer = class_289.method_1348().method_60827(VertexFormat.class_5596.field_29344, class_290.field_1576);
        for (BoxEspEntry entry3 : entries) {
            if (entry3.depthTest() != depthTest) continue;
            if (entry3.animated()) {
                BoxEspRenderer.animatedOutline((class_4588)outlineBuffer, matrix, cameraPosition, entry3.box(), entry3.color(), animationTime);
                continue;
            }
            BoxEspRenderer.outline((class_4588)outlineBuffer, matrix, cameraPosition, entry3.box(), entry3.color());
        }
        class_9801 builtOutline = outlineBuffer.method_60794();
        if (builtOutline != null) {
            outlineLayer.method_60895(builtOutline);
        }
    }

    private class_238 getExpandedBoundingBox(class_1297 entity, float tickProgress, float expansion) {
        class_243 interpolatedPosition = entity.method_30950(tickProgress);
        class_238 box = entity.method_5829().method_989(interpolatedPosition.field_1352 - entity.method_23317(), interpolatedPosition.field_1351 - entity.method_23318(), interpolatedPosition.field_1350 - entity.method_23321());
        return expansion > 0.0f ? box.method_1014((double)expansion) : box;
    }

    private float getExpansion(int scale) {
        return (float)(Math.max(1, scale) - 1) * 0.05f;
    }
}

