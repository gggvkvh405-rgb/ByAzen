/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.minecraft.class_10017
 *  net.minecraft.class_11659
 *  net.minecraft.class_11661
 *  net.minecraft.class_11684
 *  net.minecraft.class_12075
 *  net.minecraft.class_1297
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  net.minecraft.class_4184
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4618
 *  net.minecraft.class_6367
 *  net.minecraft.class_898
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector4f
 *  org.lwjgl.system.MemoryStack
 */
package ru.wexside.render;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.List;
import net.minecraft.class_10017;
import net.minecraft.class_11659;
import net.minecraft.class_11661;
import net.minecraft.class_11684;
import net.minecraft.class_12075;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4618;
import net.minecraft.class_6367;
import net.minecraft.class_898;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;
import ru.wexside.render.GlowCompositeMode;
import ru.wexside.render.GlowOutlineCompositor;
import ru.wexside.render.RenderFrameState;
import ru.wexside.render.RenderTargetOverrideScope;

public final class GlowEspEffect {
    private GpuTextureView maskDepthView;
    private int sceneWidth;
    private GpuTextureView outlineView;
    private GpuTextureView sceneColorView;
    private class_243 cameraPosition;
    private boolean framebuffersCleared;
    private class_6367 outlineFramebuffer;
    private class_6367 horizontalOutlineFramebuffer;
    private class_898 entityRenderer;
    private GpuTextureView horizontalOutlineView;
    private final GlowOutlineCompositor compositor;
    private int sceneHeight;
    private final Matrix4f viewProjectionMatrix;
    private final long startedAtMillis;
    private GpuTextureView maskColorView;
    private float elapsedTime;
    private int effectWidth;
    private class_11684 renderDispatcher;
    private class_6367 maskFramebuffer;
    private class_9799 auxiliaryBufferAllocator;
    private GpuTextureView gradientView;
    private int effectHeight;
    private class_4618 outlineVertexConsumers;
    private boolean framePrepared;
    private final class_12075 cameraRenderState;
    private class_6367 gradientFramebuffer;
    private class_4597.class_4598 auxiliaryVertexConsumers;
    private class_4597.class_4598 entityVertexConsumers;
    private final Vector4f clipSpacePosition;
    private class_9799 entityBufferAllocator;
    private class_11661 renderQueue;
    private class_276 mainFramebuffer;
    private boolean maskRendered;
    private final class_310 client = class_310.method_1551();

    public GlowEspEffect() {
        this.compositor = new GlowOutlineCompositor();
        this.startedAtMillis = System.currentTimeMillis();
        this.viewProjectionMatrix = new Matrix4f();
        this.clipSpacePosition = new Vector4f();
        this.cameraRenderState = new class_12075();
        this.cameraPosition = class_243.field_1353;
    }

    private void clearFramebuffer(class_6367 framebuffer7) {
        if (framebuffer7 == null || framebuffer7.method_30277() == null) {
            return;
        }
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        GpuTexture gpuTexture = framebuffer7.method_30277();
        GpuTexture gpuTexture2 = framebuffer7.method_30278();
        if (gpuTexture2 != null) {
            commandEncoder.clearColorAndDepthTextures(gpuTexture, 0, gpuTexture2, 1.0);
        } else {
            commandEncoder.clearColorTexture(gpuTexture, 0);
        }
    }

    public boolean prepareFrame() {
        if (this.client.field_1687 == null) {
            this.resetFrameState();
            return false;
        }
        this.initializeFrame();
        if (!this.framePrepared || this.sceneColorView == null || this.maskColorView == null || this.maskDepthView == null || this.gradientView == null || this.horizontalOutlineView == null || this.outlineView == null) {
            this.clearFrameState();
            return false;
        }
        this.clearEffectFramebuffers();
        if (!this.framebuffersCleared) {
            this.clearFrameState();
            return false;
        }
        return true;
    }

    public void composite(float outlineSize, GlowCompositeMode compositeMode) {
        if (!this.maskRendered || this.sceneColorView == null) {
            this.clearFrameState();
            return;
        }
        float scaledOutlineSize = Math.max(1.0f, (float)Math.round(outlineSize * 0.5f));
        this.compositor.applyOutline(this.horizontalOutlineView, this.gradientView, this.maskColorView, this.effectWidth, this.effectHeight, scaledOutlineSize, 1.0f, 0.0f, false);
        this.compositor.applyOutline(this.outlineView, this.horizontalOutlineView, this.gradientView, this.effectWidth, this.effectHeight, scaledOutlineSize, 0.0f, 1.0f, true);
        if (compositeMode.drawsInnerGlow()) {
            this.compositor.present(this.sceneColorView, this.gradientView, this.sceneWidth, this.sceneHeight);
        }
        if (compositeMode.drawsOuterGlow()) {
            this.compositor.present(this.sceneColorView, this.outlineView, this.sceneWidth, this.sceneHeight);
        }
        this.clearFrameState();
    }

    private void clearEffectFramebuffers() {
        if (this.framebuffersCleared) {
            return;
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            this.clearFramebuffer(this.maskFramebuffer);
            this.clearFramebuffer(this.gradientFramebuffer);
            this.clearFramebuffer(this.horizontalOutlineFramebuffer);
            this.clearFramebuffer(this.outlineFramebuffer);
            this.framebuffersCleared = true;
        }
    }

    public void resetFrameState() {
        this.clearFrameState();
    }

    public void releaseFramebuffers() {
        this.clearFrameState();
        if (this.maskFramebuffer != null) {
            this.maskFramebuffer.method_1238();
        }
        if (this.gradientFramebuffer != null) {
            this.gradientFramebuffer.method_1238();
        }
        if (this.horizontalOutlineFramebuffer != null) {
            this.horizontalOutlineFramebuffer.method_1238();
        }
        if (this.outlineFramebuffer != null) {
            this.outlineFramebuffer.method_1238();
        }
        this.maskFramebuffer = null;
        this.gradientFramebuffer = null;
        this.horizontalOutlineFramebuffer = null;
        this.outlineFramebuffer = null;
    }

    private void ensureFramebuffers() {
        this.maskFramebuffer = this.ensureFramebufferSize(this.maskFramebuffer, "wexside_glow_esp_mask", this.effectWidth, this.effectHeight, true);
        this.gradientFramebuffer = this.ensureFramebufferSize(this.gradientFramebuffer, "wexside_glow_esp_gradient", this.effectWidth, this.effectHeight, false);
        this.horizontalOutlineFramebuffer = this.ensureFramebufferSize(this.horizontalOutlineFramebuffer, "wexside_glow_esp_outline_temp", this.effectWidth, this.effectHeight, false);
        this.outlineFramebuffer = this.ensureFramebufferSize(this.outlineFramebuffer, "wexside_glow_esp_outline", this.effectWidth, this.effectHeight, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean renderEntities(class_4587 matrices2, List<? extends class_1297> list, float f, int n, boolean bl) {
        if (matrices2 == null || list == null || list.isEmpty() || !this.framePrepared || !this.framebuffersCleared || this.client.field_1687 == null) {
            return false;
        }
        boolean bl2 = false;
        MemoryStack memoryStack = MemoryStack.stackPush();
        RenderSystem.getModelViewStack().pushMatrix();
        try (RenderTargetOverrideScope ignored = RenderTargetOverrideScope.use(this.maskColorView, this.maskDepthView);){
            for (class_1297 class_12972 : list) {
                if (class_12972 == null || !class_12972.method_5805() || !this.isEntityVisible(class_12972, f)) continue;
                bl2 |= this.renderEntity(matrices2, class_12972, f);
            }
            if (bl2) {
                this.renderDispatcher.method_73002();
                this.renderDispatcher.method_74817();
                this.entityVertexConsumers.method_22993();
                this.auxiliaryVertexConsumers.method_22993();
                this.outlineVertexConsumers.method_23285();
                this.renderQueue.method_72954();
            }
        }
        finally {
            RenderSystem.getModelViewStack().popMatrix();
            memoryStack.close();
            this.renderQueue.method_72953();
        }
        if (!bl2) {
            return false;
        }
        this.compositor.applyGradient(this.gradientView, this.maskColorView, this.effectWidth, this.effectHeight, n, bl, 0.0f, 0.0f, this.effectWidth, this.effectHeight, this.elapsedTime);
        this.maskRendered = true;
        return true;
    }

    public void close() {
        this.releaseFramebuffers();
        if (this.renderDispatcher != null) {
            this.renderDispatcher.close();
        }
        if (this.entityBufferAllocator != null) {
            this.entityBufferAllocator.close();
        }
        if (this.auxiliaryBufferAllocator != null) {
            this.auxiliaryBufferAllocator.close();
        }
        this.renderDispatcher = null;
        this.entityBufferAllocator = null;
        this.auxiliaryBufferAllocator = null;
        this.entityVertexConsumers = null;
        this.auxiliaryVertexConsumers = null;
        this.outlineVertexConsumers = null;
        this.renderQueue = null;
        this.compositor.close();
    }

    private boolean isEntityVisible(class_1297 entity2, float f) {
        if (this.sceneWidth <= 0 || this.sceneHeight <= 0) {
            return false;
        }
        class_238 box = entity2.method_5829().method_1009(0.65, 0.4, 0.65).method_997(entity2.method_30950(f).method_1023(entity2.method_23317(), entity2.method_23318(), entity2.method_23321()));
        double d = Double.POSITIVE_INFINITY;
        double d2 = Double.POSITIVE_INFINITY;
        double d3 = Double.NEGATIVE_INFINITY;
        double d4 = Double.NEGATIVE_INFINITY;
        boolean bl = false;
        for (int i = 0; i < 2; ++i) {
            double d5 = i == 0 ? box.field_1323 : box.field_1320;
            for (int j = 0; j < 2; ++j) {
                double d6 = j == 0 ? box.field_1322 : box.field_1325;
                for (int k = 0; k < 2; ++k) {
                    double d7 = k == 0 ? box.field_1321 : box.field_1324;
                    this.clipSpacePosition.set((float)(d5 - this.cameraPosition.field_1352), (float)(d6 - this.cameraPosition.field_1351), (float)(d7 - this.cameraPosition.field_1350), 1.0f);
                    this.viewProjectionMatrix.transform(this.clipSpacePosition);
                    float f2 = this.clipSpacePosition.w();
                    if (f2 <= 0.0f) continue;
                    float f3 = this.clipSpacePosition.x() / f2;
                    float f4 = this.clipSpacePosition.y() / f2;
                    if (!Float.isFinite(f3) || !Float.isFinite(f4)) continue;
                    double d8 = ((double)f3 * 0.5 + 0.5) * (double)this.sceneWidth;
                    double d9 = (1.0 - ((double)f4 * 0.5 + 0.5)) * (double)this.sceneHeight;
                    d = Math.min(d, d8);
                    d2 = Math.min(d2, d9);
                    d3 = Math.max(d3, d8);
                    d4 = Math.max(d4, d9);
                    bl = true;
                }
            }
        }
        if (!bl) {
            return false;
        }
        return d3 - d >= 1.0 && d4 - d2 >= 1.0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean renderEntity(class_4587 matrices2, class_1297 entity2, float f) {
        class_10017 renderState = this.entityRenderer.method_72977(entity2, f);
        if (renderState == null) {
            return false;
        }
        this.prepareRenderState(renderState);
        matrices2.method_22903();
        try {
            this.entityRenderer.method_72976(renderState, this.cameraRenderState, renderState.field_53325 - this.cameraPosition.field_1352, renderState.field_53326 - this.cameraPosition.field_1351, renderState.field_53327 - this.cameraPosition.field_1350, matrices2, (class_11659)this.renderQueue);
        }
        finally {
            matrices2.method_22909();
        }
        return true;
    }

    private void updateCameraState(class_4184 camera2) {
        this.cameraRenderState.field_63079 = true;
        this.cameraRenderState.field_63078 = camera2.method_71156();
        this.cameraRenderState.field_63077 = camera2.method_19328();
        this.cameraRenderState.field_63080 = this.cameraRenderState.field_63078;
        this.cameraRenderState.field_63081 = camera2.method_23767();
    }

    private void initializeFrame() {
        this.framePrepared = false;
        this.framebuffersCleared = false;
        this.maskRendered = false;
        this.mainFramebuffer = this.client.method_1522();
        if (this.mainFramebuffer == null || this.mainFramebuffer.method_71639() == null) {
            return;
        }
        this.ensureRenderDispatcher();
        this.sceneWidth = this.mainFramebuffer.field_1482;
        this.sceneHeight = this.mainFramebuffer.field_1481;
        this.effectWidth = Math.max(1, this.sceneWidth / 2);
        this.effectHeight = Math.max(1, this.sceneHeight / 2);
        this.ensureFramebuffers();
        class_4184 camera2 = this.client.field_1773.method_19418();
        this.entityRenderer = this.client.method_1561();
        if (camera2 == null || this.entityRenderer == null) {
            this.clearFrameState();
            return;
        }
        this.sceneColorView = this.mainFramebuffer.method_71639();
        this.maskColorView = this.maskFramebuffer.method_71639();
        this.maskDepthView = this.maskFramebuffer.method_71640();
        this.gradientView = this.gradientFramebuffer.method_71639();
        this.horizontalOutlineView = this.horizontalOutlineFramebuffer.method_71639();
        this.outlineView = this.outlineFramebuffer.method_71639();
        if (this.sceneColorView == null || this.maskColorView == null || this.maskDepthView == null || this.gradientView == null || this.horizontalOutlineView == null || this.outlineView == null) {
            this.clearFrameState();
            return;
        }
        this.entityRenderer.method_3941(camera2, this.client.method_1560());
        this.updateCameraState(camera2);
        this.cameraPosition = camera2.method_71156();
        this.viewProjectionMatrix.set((Matrix4fc)RenderFrameState.projectionMatrix).mul((Matrix4fc)RenderFrameState.viewMatrix);
        this.elapsedTime = (float)(System.currentTimeMillis() - this.startedAtMillis) / 700.0f;
        this.framePrepared = true;
    }

    private void clearFrameState() {
        this.framePrepared = false;
        this.framebuffersCleared = false;
        this.maskRendered = false;
        this.mainFramebuffer = null;
        this.sceneColorView = null;
        this.maskColorView = null;
        this.maskDepthView = null;
        this.gradientView = null;
        this.horizontalOutlineView = null;
        this.outlineView = null;
        this.entityRenderer = null;
        this.cameraPosition = class_243.field_1353;
        this.sceneWidth = 0;
        this.sceneHeight = 0;
        this.effectWidth = 0;
        this.effectHeight = 0;
        this.elapsedTime = 0.0f;
    }

    private class_6367 ensureFramebufferSize(class_6367 framebuffer7, String string, int n, int n2, boolean bl) {
        if (framebuffer7 == null) {
            return new class_6367(string, n, n2, bl);
        }
        if (framebuffer7.field_1482 != n || framebuffer7.field_1481 != n2) {
            framebuffer7.method_1234(n, n2);
        }
        return framebuffer7;
    }

    private void ensureRenderDispatcher() {
        if (this.renderDispatcher != null) {
            return;
        }
        this.entityBufferAllocator = new class_9799(0x100000);
        this.auxiliaryBufferAllocator = new class_9799(262144);
        this.entityVertexConsumers = class_4597.method_22991((class_9799)this.entityBufferAllocator);
        this.auxiliaryVertexConsumers = class_4597.method_22991((class_9799)this.auxiliaryBufferAllocator);
        this.outlineVertexConsumers = new class_4618();
        this.renderQueue = new class_11661();
        this.renderDispatcher = new class_11684(this.renderQueue, this.client.method_1541(), this.entityVertexConsumers, this.client.method_72703(), this.outlineVertexConsumers, this.auxiliaryVertexConsumers, this.client.field_1772);
    }

    private void prepareRenderState(class_10017 renderState) {
        renderState.field_53336 = null;
        renderState.field_53337 = null;
        renderState.field_53338 = null;
        renderState.field_53335 = false;
        renderState.field_61821 = 0;
        renderState.field_53333 = false;
        renderState.field_61822 = 0.0f;
        renderState.field_61823.clear();
    }
}

