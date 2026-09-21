/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 */
package ru.byazen.util;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_310;
import ru.byazen.animation.TimedPulse;
import ru.byazen.event.EntityAttackEvent;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.misc.LazyMeshModel;
import ru.byazen.misc.PreviousValueTransition;
import ru.byazen.misc.SpatialTransform;
import ru.byazen.misc.TargetChange;
import ru.byazen.misc.TargetEspEffect;
import ru.byazen.misc.TargetTransition;
import ru.byazen.misc.TransitionPhase;
import ru.byazen.module.render.TargetEspRenderer;
import ru.byazen.render.model.BuiltInMesh;
import ru.byazen.util.AnimationMath;
import ru.byazen.util.ModelRenderOptions;
import ru.byazen.util.ModelRenderQueue;
import ru.byazen.util.WorldMeshBatchRenderer;

public final class MarkerTargetEspRenderer
extends TargetEspRenderer
implements TargetEspEffect {
    private final LazyMeshModel lazyMeshModel = LazyMeshModel.create(BuiltInMesh.MARKER);
    static final long member5632 = 420L;
    private final PreviousValueTransition<TargetMarkerState> previousState;
    static final float value4 = 0.4f;
    private static final WorldMeshBatchRenderer MODEL_RENDERER = new WorldMeshBatchRenderer("target-marker");
    static final float value5 = 1.5f;
    static final float value6 = 0.42f;
    static final float value7 = 0.25f;
    private final ModelRenderOptions modelRenderOptions;
    private final TargetTransition<class_1297> targetTransition;
    private TargetMarkerState lastState;
    static final long member8917 = 320L;
    static final long member12527 = 300L;
    static final int slot = -65476;
    static final float value8 = 1.28f;
    private final ModelRenderQueue modelRenderQueue = new ModelRenderQueue();
    static final float value9 = 0.5f;
    private final TimedPulse attackPulse;
    static final float value10 = 1.16f;

    public MarkerTargetEspRenderer() {
        this.modelRenderOptions = ModelRenderOptions.process4(true).process10(false);
        this.targetTransition = new TargetTransition();
        this.attackPulse = new TimedPulse();
        this.previousState = new PreviousValueTransition();
    }

    @Override
    public void setEntityAttackEvent(EntityAttackEvent iIiiiilIiIEvent) {
        if (iIiiiilIiIEvent.getEntity() != null) {
            this.attackPulse.trigger();
        }
    }

    @Override
    public void setWorldRenderEvent(WorldRenderEvent floatTypeEvent2) {
        class_310 mc = this.client();
        if (!this.canRender(mc)) {
            return;
        }
        if (!this.lazyMeshModel.isLoaded() || this.lazyMeshModel.getMeshModel() == null || this.lazyMeshModel.getMeshModel().getList().isEmpty()) {
            return;
        }
        this.process2(this.target(mc, class_1297.class), floatTypeEvent2.getFloatType());
        if (this.targetTransition.isEmpty()) {
            return;
        }
        float f = this.getTransitionOpacity();
        class_1297 target = this.targetTransition.current();
        if (f <= 0.0f || target == null) {
            return;
        }
        TargetMarkerState state = this.captureState(target, floatTypeEvent2.getFloatType());
        if (state != null) {
            this.lastState = state;
        } else {
            state = this.lastState;
        }
        if (state == null) {
            this.update2();
            return;
        }
        this.modelRenderQueue.update();
        this.renderMarker(this.interpolatePreviousState(state), this.getMarkerScale(), f);
        if (!this.modelRenderQueue.isActive()) {
            MODEL_RENDERER.process10(floatTypeEvent2.getMatrices(), mc.method_22940().method_23000(), this.cameraPosition(), this.modelRenderQueue);
        }
    }

    @Override
    public void update() {
        this.update2();
        this.modelRenderQueue.update();
    }

    private void update2() {
        this.targetTransition.reset();
        this.attackPulse.reset();
        this.previousState.reset();
        this.lastState = null;
    }

    private void renderMarker(TargetMarkerState state, float f, float f2) {
        float f3;
        long l = System.currentTimeMillis();
        class_243 vec = this.cameraPosition();
        double d = state.position().field_1352;
        double d2 = state.position().field_1351 + state.height() * 0.65;
        double d3 = state.position().field_1350;
        double d4 = vec.field_1352 - d;
        double d5 = vec.field_1351 - d2;
        double d6 = vec.field_1350 - d3;
        float f4 = (float)Math.toDegrees(Math.atan2(d4, d6));
        float f5 = (float)Math.toDegrees(-Math.atan2(d5, Math.sqrt(d4 * d4 + d6 * d6)));
        float f6 = 1.0f + AnimationMath.sin((float)l * 0.004f) * 0.035f;
        float f7 = AnimationMath.sin((float)l * 0.005f) * 0.035f;
        f *= f6;
        int n = this.process3(this.primaryColor(), this.secondaryColor(), l);
        if (this.attackPulse.isActive(l, 320L)) {
            f3 = this.attackPulse.progress(l, 320L);
            float f8 = f3 < 0.5f ? f3 / 0.5f : (f3 - 0.5f) / 0.5f;
            float f9 = AnimationMath.easeInOutSine(f8);
            f = f3 < 0.5f ? AnimationMath.lerp(f, 0.4f, f9) : AnimationMath.lerp(0.4f, f, f9);
            n = f3 < 0.5f ? AnimationMath.lerpColor(n, -65476, f9) : AnimationMath.lerpColor(-65476, n, f9);
        }
        f3 = (float)Math.sin((double)System.currentTimeMillis() / 666.6666666666666) * 360.0f;
        this.modelRenderQueue.process7(this.lazyMeshModel.getMeshModel(), new SpatialTransform(d, d2 + (double)f7, d3, f, f, f, f4, f5, f3), AnimationMath.applyOpacity(n, f2, 0.5f), this.modelRenderOptions);
    }

    private void process2(class_1297 entity2, float f) {
        TargetChange<class_1297> targetChange = this.targetTransition.updateTarget(entity2);
        if (!targetChange.changed()) {
            return;
        }
        TargetMarkerState oldState = this.lastState;
        TargetMarkerState newState = this.captureState(this.targetTransition.current(), f);
        if (oldState != null && newState != null) {
            this.previousState.remember(oldState);
        }
    }

    private int process3(int n, int n2, long l) {
        float f = 0.5f + 0.5f * AnimationMath.sin((float)l * 0.004f);
        return AnimationMath.lerpColor(n, n2, f * 0.45f);
    }

    private TargetMarkerState captureState(class_1297 entity2, float f) {
        if (entity2 == null || !entity2.method_5805()) {
            return null;
        }
        return new TargetMarkerState(this.interpolatedPosition(entity2, f), entity2.method_17682());
    }

    private TargetMarkerState interpolatePreviousState(TargetMarkerState state) {
        if (!this.previousState.hasValue()) {
            return state;
        }
        float f = this.previousState.progress(420L);
        if (f >= 1.0f) {
            this.previousState.clear();
            return state;
        }
        float f2 = AnimationMath.easeOut(f, 1.16f);
        TargetMarkerState previous = this.previousState.get();
        class_243 start = previous.position();
        class_243 end = state.position();
        return new TargetMarkerState(new class_243((double)AnimationMath.lerp((float)start.field_1352, (float)end.field_1352, f2), (double)AnimationMath.lerp((float)start.field_1351, (float)end.field_1351, f2), (double)AnimationMath.lerp((float)start.field_1350, (float)end.field_1350, f2)), AnimationMath.lerp((float)previous.height(), (float)state.height(), f2));
    }

    private float getMarkerScale() {
        if (this.targetTransition.phase() == TransitionPhase.IDLE) {
            return 0.25f;
        }
        float f = this.targetTransition.progress(300L);
        if (this.targetTransition.phase() == TransitionPhase.APPEARING) {
            return AnimationMath.lerp(0.42f, 0.25f, AnimationMath.easeOut(f, 1.28f));
        }
        return AnimationMath.lerp(0.25f, 0.42f, AnimationMath.smoothStep(f));
    }

    private float getTransitionOpacity() {
        if (this.targetTransition.phase() == TransitionPhase.IDLE) {
            return 1.0f;
        }
        float f = this.targetTransition.progress(300L);
        if (this.targetTransition.phase() == TransitionPhase.APPEARING) {
            if (f >= 1.0f) {
                this.targetTransition.finishAppearance();
            }
            return AnimationMath.easeInOutSine(f);
        }
        if (f >= 1.0f) {
            this.update2();
            return 0.0f;
        }
        return 1.0f - AnimationMath.smoothStep(f);
    }

    private record TargetMarkerState(class_243 position, double height) {
    }
}

