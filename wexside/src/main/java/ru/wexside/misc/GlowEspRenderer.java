/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_310
 *  net.minecraft.class_4587
 */
package ru.wexside.misc;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_310;
import net.minecraft.class_4587;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldRenderEvent;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.misc.GlowEspSettings;
import ru.wexside.model.esp.EspRelation;
import ru.wexside.model.esp.EspTargetClassifier;
import ru.wexside.render.GlowCompositeMode;
import ru.wexside.render.GlowEspEffect;
import ru.wexside.render.RenderCamera;
import ru.wexside.render.RenderProjection;
import ru.wexside.util.EspFeatureRegistry;

public final class GlowEspRenderer {
    private final EspFeatureRegistry espFeatures;
    private static volatile GlowEspRenderer instance;
    private final EnumMap<EspRelation, List<class_1297>> entitiesByRelation;
    private final GlowEspEffect glowEspEffect;

    public GlowEspRenderer(EventBus eventBus, EspFeatureRegistry espFeatures) {
        this.espFeatures = espFeatures;
        this.glowEspEffect = new GlowEspEffect();
        this.entitiesByRelation = new EnumMap(EspRelation.class);
        for (EspRelation relation : EspRelation.values()) {
            this.entitiesByRelation.put(relation, new ArrayList());
        }
        instance = this;
        eventBus.subscribe(WorldRenderEvent.class, this::collectVisiblePlayers);
        eventBus.subscribe(WorldSessionEvent.class, this::onWorldSessionChanged);
    }

    private void onWorldSessionChanged(WorldSessionEvent event) {
        this.glowEspEffect.releaseFramebuffers();
    }

    private void renderQueuedEntities() {
        class_310 mc = class_310.method_1551();
        if (mc.field_1687 == null || mc.field_1724 == null || RenderCamera.position() == null) {
            this.clearEntityQueue();
            this.glowEspEffect.resetFrameState();
            return;
        }
        LinkedHashMap<Float, List> relationsByRadius = new LinkedHashMap<Float, List>();
        for (EspRelation relation : EspRelation.values()) {
            GlowEspSettings settings = this.espFeatures.getGlowSettings(relation);
            if (this.entitiesByRelation.get((Object)relation).isEmpty() || settings == null || !settings.isEnabled()) continue;
            relationsByRadius.computeIfAbsent(Float.valueOf(settings.getRadius()), ignored -> new ArrayList()).add(relation);
        }
        boolean renderedAnyGroup = false;
        float tickProgress = RenderProjection.tickProgress();
        for (Map.Entry entry : relationsByRadius.entrySet()) {
            if (!this.glowEspEffect.prepareFrame()) continue;
            boolean renderedGroup = false;
            List relations = (List)entry.getValue();
            for (int index = relations.size() - 1; index >= 0; --index) {
                EspRelation relation = (EspRelation)((Object)relations.get(index));
                renderedGroup |= this.glowEspEffect.renderEntities(new class_4587(), this.entitiesByRelation.get((Object)relation), tickProgress, this.espFeatures.getGlowSettings(relation).getColor(), false);
            }
            if (renderedGroup) {
                this.glowEspEffect.composite(((Float)entry.getKey()).floatValue(), GlowCompositeMode.BOTH);
                renderedAnyGroup = true;
                continue;
            }
            this.glowEspEffect.resetFrameState();
        }
        if (!renderedAnyGroup) {
            this.glowEspEffect.resetFrameState();
        }
        this.clearEntityQueue();
    }

    public static void renderPendingGlow() {
        GlowEspRenderer renderer = instance;
        if (renderer == null || !renderer.espFeatures.hasEnabledGlow()) {
            return;
        }
        renderer.renderQueuedEntities();
    }

    private void collectVisiblePlayers(WorldRenderEvent event) {
        this.clearEntityQueue();
        class_310 mc = class_310.method_1551();
        if (!this.espFeatures.hasEnabledGlow() || mc.field_1687 == null || mc.field_1724 == null || RenderCamera.position() == null) {
            this.glowEspEffect.resetFrameState();
            return;
        }
        for (class_1297 entity : mc.field_1687.method_18112()) {
            EspRelation relation;
            GlowEspSettings settings;
            if (!(entity instanceof class_1657)) continue;
            class_1657 player = (class_1657)entity;
            if (entity == mc.field_1724 || !player.method_5805() || (settings = this.espFeatures.getGlowSettings(relation = EspTargetClassifier.relation(entity))) == null || !settings.isEnabled()) continue;
            double maximumDistance = settings.getMaximumDistance();
            if (mc.field_1724.method_73189().method_1025(entity.method_73189()) > maximumDistance * maximumDistance) continue;
            this.entitiesByRelation.get((Object)relation).add(entity);
        }
    }

    private void clearEntityQueue() {
        for (List<class_1297> entities : this.entitiesByRelation.values()) {
            entities.clear();
        }
    }
}

