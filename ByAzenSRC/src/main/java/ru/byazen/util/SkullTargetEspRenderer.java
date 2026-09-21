/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 */
package ru.byazen.util;

import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_310;
import ru.byazen.animation.TimedPulse;
import ru.byazen.event.EntityAttackEvent;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.misc.LazyMeshModel;
import ru.byazen.misc.SpatialTransform;
import ru.byazen.misc.TargetEspEffect;
import ru.byazen.misc.TargetTransition;
import ru.byazen.misc.TransitionPhase;
import ru.byazen.module.render.TargetEspRenderer;
import ru.byazen.render.model.BuiltInMesh;
import ru.byazen.util.AnimationMath;
import ru.byazen.util.ModelRenderOptions;
import ru.byazen.util.ModelRenderQueue;
import ru.byazen.util.WorldMeshBatchRenderer;

public final class SkullTargetEspRenderer
extends TargetEspRenderer
implements TargetEspEffect {
    private final LazyMeshModel lazyMeshModel = LazyMeshModel.create(BuiltInMesh.SKULL);
    static final long member12318 = 240L;
    static final float value = 0.16f;
    private static final WorldMeshBatchRenderer MODEL_RENDERER = new WorldMeshBatchRenderer("target-skull");
    private final TimedPulse attackPulse;
    static final long member12527 = 320L;
    static final long member6565 = 200L;
    static final float value2 = 0.23f;
    static final int slot = -65536;
    static final float value4 = 0.34f;
    static final float value5 = 0.14f;
    static final boolean flag = true;
    static final float value6 = 1.3f;
    private final ModelRenderQueue modelRenderQueue = new ModelRenderQueue();
    static final float value7 = 0.65f;
    private final TargetTransition<class_1309> targetTransition2;
    private final ModelRenderOptions modelRenderOptions = ModelRenderOptions.process4(true).process10(false);
    static final float value8 = 0.5f;

    public SkullTargetEspRenderer() {
        this.targetTransition2 = new TargetTransition();
        this.attackPulse = new TimedPulse();
    }

    @Override
    public void setEntityAttackEvent(EntityAttackEvent iIiiiilIiIEvent) {
        if (iIiiiilIiIEvent.getEntity() instanceof class_1309) {
            this.attackPulse.trigger();
        }
    }

    @Override
    public void setWorldRenderEvent(WorldRenderEvent floatTypeEvent2) {
        class_310 mc = this.client();
        if (!this.canRender(mc)) {
            return;
        }
        this.targetTransition2.updateTarget(this.target(mc, class_1309.class));
        if (this.targetTransition2.isEmpty()) {
            return;
        }
        float f = this.getTransitionOpacity();
        class_1309 entity2 = this.targetTransition2.current();
        if (f <= 0.0f || entity2 == null) {
            return;
        }
        if (!this.lazyMeshModel.isLoaded() || this.lazyMeshModel.getMeshModel() == null || this.lazyMeshModel.getMeshModel().getList().isEmpty()) {
            return;
        }
        this.modelRenderQueue.update();
        this.process2(entity2, floatTypeEvent2.getFloatType(), this.getModelScale(), f);
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
        this.targetTransition2.reset();
        this.attackPulse.reset();
    }

    private int process3(int n, int n2, long l) {
        float f = 0.5f + 0.5f * AnimationMath.sin((float)l * 0.004f);
        return AnimationMath.lerpColor(n, n2, f * 0.36f);
    }

    private float getModelScale() {
        if (this.targetTransition2.phase() == TransitionPhase.IDLE) {
            return 0.23f;
        }
        float f = this.targetTransition2.progress(320L);
        if (this.targetTransition2.phase() == TransitionPhase.APPEARING) {
            return AnimationMath.lerp(0.34f, 0.23f, AnimationMath.easeOut(f, 1.3f));
        }
        return AnimationMath.lerp(0.23f, 0.34f, AnimationMath.smoothStep(f));
    }

    private float getTransitionOpacity() {
        if (this.targetTransition2.phase() == TransitionPhase.IDLE) {
            return 1.0f;
        }
        float f = this.targetTransition2.progress(320L);
        if (this.targetTransition2.phase() == TransitionPhase.APPEARING) {
            if (f >= 1.0f) {
                this.targetTransition2.finishAppearance();
            }
            return AnimationMath.easeInOutSine(f);
        }
        if (f >= 1.0f) {
            this.update2();
            return 0.0f;
        }
        return 1.0f - AnimationMath.smoothStep(f);
    }

    private float process(long l) {
        long l2 = this.attackPulse.elapsed(l);
        if (l2 < 240L) {
            float f = (float)l2 / 240.0f;
            float f2 = (float)Math.sin((double)f * Math.PI) * (1.0f - f);
            return -f2 * 0.16f;
        }
        return 0.0f;
    }

    private void process2(class_1309 entity2, float f, float f2, float f3) {
        long l = System.currentTimeMillis();
        class_243 vec = this.interpolatedPosition((class_1297)entity2, f);
        class_243 vec2 = this.cameraPosition();
        double d = vec.field_1352;
        double d2 = vec.field_1351 + (double)entity2.method_17682() + 0.25;
        double d3 = vec.field_1350;
        float f4 = AnimationMath.sin((float)l * 0.0045f) * 0.045f;
        float f5 = this.process(l);
        class_243 vec3 = this.process4(l);
        class_243 vec4 = new class_243(d + vec3.field_1352, d2 + (double)f4 + (double)f5, d3 + vec3.field_1350);
        double d4 = vec2.field_1352 - vec4.field_1352;
        double d5 = vec2.field_1351 - vec4.field_1351;
        double d6 = vec2.field_1350 - vec4.field_1350;
        float f6 = (float)Math.toDegrees(Math.atan2(d4, d6));
        float f7 = (float)Math.toDegrees(-Math.atan2(d5, Math.sqrt(d4 * d4 + d6 * d6)));
        float f8 = 1.0f + AnimationMath.sin((float)l * 0.004f) * 0.035f;
        float f9 = f2 * f8;
        int n = this.process3(this.primaryColor(), this.secondaryColor(), l);
        if (this.attackPulse.isActive(l, 200L)) {
            float f10 = this.attackPulse.progress(l, 200L);
            n = f10 < 0.5f ? AnimationMath.lerpColor(this.primaryColor(), -65536, f10 / 0.5f) : AnimationMath.lerpColor(-65536, this.primaryColor(), (f10 - 0.5f) / 0.5f);
        }
        this.modelRenderQueue.process7(this.lazyMeshModel.getMeshModel(), new SpatialTransform(vec4.field_1352, vec4.field_1351, vec4.field_1350, f9, f9, f9, f6, f7 + 90.0f, 0.0f), AnimationMath.applyOpacity(n, f3, 0.5f), this.modelRenderOptions);
    }

    private class_243 process4(long l) {
        long l2 = this.attackPulse.elapsed(l);
        if (l2 < 200L) {
            float f = (float)l2 / 200.0f;
            float f2 = (float)Math.sin((double)f * Math.PI * 8.0) * (1.0f - f) * 0.14f;
            return new class_243((double)f2, 0.0, (double)(f2 * 0.65f));
        }
        return class_243.field_1353;
    }
}

