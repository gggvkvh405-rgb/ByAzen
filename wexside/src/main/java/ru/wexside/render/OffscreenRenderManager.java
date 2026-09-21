/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.opengl.GlStateManager
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.minecraft.class_10366
 *  net.minecraft.class_1041
 *  net.minecraft.class_10444
 *  net.minecraft.class_11228
 *  net.minecraft.class_11233
 *  net.minecraft.class_11234
 *  net.minecraft.class_11235
 *  net.minecraft.class_11236
 *  net.minecraft.class_11237
 *  net.minecraft.class_11238
 *  net.minecraft.class_11246
 *  net.minecraft.class_11278
 *  net.minecraft.class_11527
 *  net.minecraft.class_11659
 *  net.minecraft.class_11661
 *  net.minecraft.class_11684
 *  net.minecraft.class_11697
 *  net.minecraft.class_11701
 *  net.minecraft.class_1799
 *  net.minecraft.class_1937
 *  net.minecraft.class_308$class_11274
 *  net.minecraft.class_310
 *  net.minecraft.class_332
 *  net.minecraft.class_4587
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4599
 *  net.minecraft.class_4608
 *  net.minecraft.class_7833
 *  net.minecraft.class_811
 *  org.joml.Quaternionfc
 *  org.lwjgl.opengl.GL11
 *  org.lwjgl.opengl.GL30
 */
package ru.wexside.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.class_10366;
import net.minecraft.class_1041;
import net.minecraft.class_10444;
import net.minecraft.class_11228;
import net.minecraft.class_11233;
import net.minecraft.class_11234;
import net.minecraft.class_11235;
import net.minecraft.class_11236;
import net.minecraft.class_11237;
import net.minecraft.class_11238;
import net.minecraft.class_11246;
import net.minecraft.class_11278;
import net.minecraft.class_11527;
import net.minecraft.class_11659;
import net.minecraft.class_11661;
import net.minecraft.class_11684;
import net.minecraft.class_11697;
import net.minecraft.class_11701;
import net.minecraft.class_1799;
import net.minecraft.class_1937;
import net.minecraft.class_308;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4599;
import net.minecraft.class_4608;
import net.minecraft.class_7833;
import net.minecraft.class_811;
import org.joml.Quaternionfc;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import ru.wexside.WexSideClient;
import ru.wexside.misc.ActiveFramebufferContext;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.CaptureFramebuffer;
import ru.wexside.misc.GuiRenderTargetAccessor;
import ru.wexside.render.IconAtlasEntry;
import ru.wexside.util.GlRenderStateSnapshot;
import ru.wexside.util.GuiDrawApi;

public final class OffscreenRenderManager {
    private class_11228 guiRenderer;
    private class_11278 itemProjectionMatrix;
    private class_11246 guiRenderState;
    private class_11684 renderDispatcher;
    private int slot;
    private final CaptureFramebuffer captureFramebuffer = new CaptureFramebuffer();
    private static final int slot2 = 18;
    private static final int slot3 = 16;
    private class_4597.class_4598 immediate2;

    public void setList(List<BakedIconEntry> list) {
        int n3;
        int n4;
        int n5;
        if (list.isEmpty()) {
            return;
        }
        class_1041 window = class_310.method_1551().method_22683();
        int n = window.method_4489();
        int n2 = window.method_4506();
        if (n <= 0 || n2 <= 0) {
            return;
        }
        int n6 = Math.max(1, window.method_4495());
        int n7 = list.size();
        int[] nArray = new int[n7];
        int[] nArray2 = new int[n7];
        int n8 = 0;
        for (n5 = 0; n5 < n7; ++n5) {
            BakedIconEntry icon = list.get(n5);
            n4 = icon.baseSize() * n6;
            n3 = icon.requestedSize() > 0 ? Math.max(1, Math.round((float)icon.requestedSize() / (float)n4)) : 1;
            nArray2[n5] = icon.baseSize() * n3;
            nArray[n5] = n4 * n3;
            n8 = Math.max(n8, nArray[n5]);
            icon.texture().setIntType(nArray[n5]);
        }
        if (n8 <= 0) {
            return;
        }
        n5 = Math.max(1, n / n8);
        int n9 = n8 / n6;
        int columns = n5;
        int logicalCellSize = n9;
        this.process4(this.captureFramebuffer, n, n2, trimToWidth -> {
            for (int i = 0; i < n7; ++i) {
                ((BakedIconEntry)list.get(i)).renderer().render((class_332)trimToWidth, i % columns * logicalCellSize, i / columns * logicalCellSize, nArray2[i]);
            }
        });
        n4 = this.captureFramebuffer.field_1481;
        for (n3 = 0; n3 < n7; ++n3) {
            IconAtlasEntry renderPipeline10 = list.get(n3).texture();
            if (!renderPipeline10.isActive()) {
                renderPipeline10.process2(n6, false);
                continue;
            }
            int n10 = n3 % n5 * n8;
            int n11 = n4 - n3 / n5 * n8 - nArray[n3];
            this.process2(renderPipeline10, nArray[n3], n10, n11);
            renderPipeline10.process2(n6, true);
        }
    }

    private void initializeRenderResources(class_310 mc) {
        class_4597.class_4598 iIiIliiliI2;
        if (this.guiRenderer != null) {
            return;
        }
        this.guiRenderState = new class_11246();
        class_4599 bufferBuilderStorage = new class_4599(1);
        this.immediate2 = iIiIliiliI2 = bufferBuilderStorage.method_23000();
        class_11697 iliIilliII2 = mc.method_72703();
        class_11661 orderedRenderCommandQueueImpl = new class_11661();
        this.renderDispatcher = new class_11684(orderedRenderCommandQueueImpl, mc.method_1541(), iIiIliiliI2, iliIilliII2, bufferBuilderStorage.method_23003(), bufferBuilderStorage.method_23001(), mc.field_1772);
        this.guiRenderer = new class_11228(this.guiRenderState, iIiIliiliI2, (class_11659)orderedRenderCommandQueueImpl, this.renderDispatcher, List.of(new class_11527(iIiIliiliI2), new class_11236(iIiIliiliI2), new class_11234(iIiIliiliI2), new class_11237(iIiIliiliI2, (class_11701)iliIilliII2), new class_11233(iIiIliiliI2, (class_11701)iliIilliII2), new class_11238(iIiIliiliI2), new class_11235(iIiIliiliI2, mc.method_1561())));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void process(class_310 mc, CaptureFramebuffer captureFramebuffer) {
        int n;
        GpuBufferSlice gpuBufferSlice = RenderSystem.getShaderFog();
        GpuTextureView gpuTextureView = RenderSystem.outputColorTextureOverride;
        GpuTextureView gpuTextureView2 = RenderSystem.outputDepthTextureOverride;
        GpuBufferSlice gpuBufferSlice2 = RenderSystem.getShaderFog();
        GpuBufferSlice gpuBufferSlice3 = RenderSystem.getShaderLights();
        RenderSystem.backupProjectionMatrix();
        GlRenderStateSnapshot glRenderStateSnapshot = GlRenderStateSnapshot.captureRenderState();
        boolean bl = GL11.glGetBoolean((int)2930);
        int n2 = GL11.glGetInteger((int)2932);
        int[] nArray = new int[12];
        try {
            ActiveFramebufferContext.setFramebuffer(captureFramebuffer);
            RenderSystem.outputColorTextureOverride = captureFramebuffer.method_71639();
            RenderSystem.outputDepthTextureOverride = captureFramebuffer.method_71640();
            this.update3();
            GlStateManager._activeTexture((int)GL11.glGetInteger((int)34016));
            for (n = 0; n < nArray.length; ++n) {
                GlStateManager._activeTexture((int)(33984 + n));
                nArray[n] = GL11.glGetInteger((int)32873);
                GlStateManager._bindTexture((int)nArray[n]);
            }
            GlStateManager._activeTexture((int)33984);
            this.guiRenderer.method_70890(gpuBufferSlice);
            this.guiRenderer.method_70879();
        }
        finally {
            for (n = 0; n < nArray.length; ++n) {
                GlStateManager._activeTexture((int)(33984 + n));
                GlStateManager._bindTexture((int)nArray[n]);
            }
            GlStateManager._activeTexture((int)33984);
            ActiveFramebufferContext.update();
            RenderSystem.restoreProjectionMatrix();
            RenderSystem.setShaderFog((GpuBufferSlice)gpuBufferSlice2);
            RenderSystem.setShaderLights((GpuBufferSlice)gpuBufferSlice3);
            RenderSystem.outputColorTextureOverride = gpuTextureView;
            RenderSystem.outputDepthTextureOverride = gpuTextureView2;
            glRenderStateSnapshot.restore();
            GL11.glDepthMask((boolean)bl);
            GL11.glDepthFunc((int)n2);
        }
    }

    private void update() {
        GuiDrawApi drawApi = WexSideClient.getHudRenderer();
        if (drawApi != null) {
            drawApi.finishIfIdle();
        }
    }

    private void process2(IconAtlasEntry renderPipeline10, int n, int n2, int n3) {
        if (this.captureFramebuffer.getIntType() <= 0) {
            return;
        }
        int n4 = GL30.glGetInteger((int)36010);
        int n5 = GL30.glGetInteger((int)36006);
        if (this.slot == 0) {
            this.slot = GL30.glGenFramebuffers();
        }
        GL30.glBindFramebuffer((int)36008, (int)this.slot);
        GL30.glFramebufferTexture2D((int)36008, (int)36064, (int)3553, (int)this.captureFramebuffer.getIntType(), (int)0);
        GL30.glBindFramebuffer((int)36009, (int)renderPipeline10.getIntType6());
        int n6 = renderPipeline10.getIntType5();
        int n7 = renderPipeline10.getIntType2();
        boolean bl = GL30.glIsEnabled((int)3089);
        GL30.glDisable((int)3089);
        GL30.glBlitFramebuffer((int)n2, (int)n3, (int)(n2 + n), (int)(n3 + n), (int)n6, (int)n7, (int)(n6 + n), (int)(n7 + n), (int)16384, (int)9728);
        if (bl) {
            GL30.glEnable((int)3089);
        }
        GL30.glBindFramebuffer((int)36008, (int)n4);
        GL30.glBindFramebuffer((int)36009, (int)n5);
    }

    public void update2() {
        if (this.guiRenderer != null) {
            this.guiRenderer.close();
            this.guiRenderer = null;
        }
        if (this.renderDispatcher != null) {
            this.renderDispatcher.close();
            this.renderDispatcher = null;
        }
        this.immediate2 = null;
        if (this.itemProjectionMatrix != null) {
            this.itemProjectionMatrix.close();
            this.itemProjectionMatrix = null;
        }
        if (this.slot != 0) {
            GL30.glDeleteFramebuffers((int)this.slot);
            this.slot = 0;
        }
        this.captureFramebuffer.method_1238();
    }

    private void update3() {
        GuiRenderTargetAccessor renderer = (GuiRenderTargetAccessor)this.guiRenderer;
        GpuTexture gpuTexture = renderer.getItemAtlasTexture();
        GpuTexture gpuTexture2 = renderer.getItemAtlasDepthTexture();
        if (gpuTexture == null || gpuTexture2 == null) {
            return;
        }
        renderer.getRenderedItems().clear();
        renderer.setItemAtlasX(0);
        renderer.setItemAtlasY(0);
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(gpuTexture, 0, gpuTexture2, 1.0);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void process3(CaptureFramebuffer captureFramebuffer, int n, int n2, float f, float f2, float f3, float f4, float f5, BiConsumer<class_4587, class_4597.class_4598> biConsumer) {
        if (biConsumer == null) {
            return;
        }
        class_310 mc = class_310.method_1551();
        captureFramebuffer.process(n, n2);
        if (captureFramebuffer.method_30277() == null) {
            return;
        }
        this.update();
        this.initializeRenderResources(mc);
        if (this.itemProjectionMatrix == null) {
            this.itemProjectionMatrix = new class_11278("wex-capture-item", -1000.0f, 1000.0f, true);
        }
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(captureFramebuffer.method_30277(), 0, captureFramebuffer.method_30278(), 1.0);
        float f6 = mc.method_22683().method_4495();
        GpuBufferSlice gpuBufferSlice = RenderSystem.getShaderFog();
        GpuTextureView gpuTextureView = RenderSystem.outputColorTextureOverride;
        GpuTextureView gpuTextureView2 = RenderSystem.outputDepthTextureOverride;
        GpuBufferSlice gpuBufferSlice2 = RenderSystem.getShaderFog();
        RenderSystem.backupProjectionMatrix();
        GlRenderStateSnapshot glRenderStateSnapshot = GlRenderStateSnapshot.captureRenderState();
        boolean bl = GL11.glGetBoolean((int)2930);
        int n3 = GL11.glGetInteger((int)2932);
        try {
            RenderSystem.outputColorTextureOverride = captureFramebuffer.method_71639();
            RenderSystem.outputDepthTextureOverride = captureFramebuffer.method_71640();
            RenderSystem.setProjectionMatrix((GpuBufferSlice)this.itemProjectionMatrix.method_71092((float)n / f6, (float)n2 / f6), (class_10366)class_10366.field_54954);
            RenderSystem.setShaderFog((GpuBufferSlice)gpuBufferSlice);
            class_4587 matrices2 = new class_4587();
            matrices2.method_46416(f, f2, 0.0f);
            matrices2.method_22905(f3, -f3, f3);
            matrices2.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(f5));
            matrices2.method_46416(0.0f, f4, 0.0f);
            biConsumer.accept(matrices2, this.immediate2);
        }
        finally {
            RenderSystem.restoreProjectionMatrix();
            RenderSystem.setShaderFog((GpuBufferSlice)gpuBufferSlice2);
            RenderSystem.outputColorTextureOverride = gpuTextureView;
            RenderSystem.outputDepthTextureOverride = gpuTextureView2;
            glRenderStateSnapshot.restore();
            GL11.glDepthMask((boolean)bl);
            GL11.glDepthFunc((int)n3);
        }
    }

    public void process4(CaptureFramebuffer captureFramebuffer, int n, int n2, Consumer<class_332> consumer) {
        class_310 mc = class_310.method_1551();
        captureFramebuffer.process(n, n2);
        if (captureFramebuffer.method_30277() == null) {
            return;
        }
        this.update();
        this.initializeRenderResources(mc);
        this.guiRenderState.method_70926();
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(captureFramebuffer.method_30277(), 0, captureFramebuffer.method_30278(), 1.0);
        consumer.accept(new class_332(mc, this.guiRenderState, 0, 0));
        this.process(mc, captureFramebuffer);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void process5(CaptureFramebuffer captureFramebuffer, int n, int n2, class_1799 stack, class_811 itemDisplayContext, float f, float f2, float f3, float f4, float f5) {
        if (stack.method_7960()) {
            return;
        }
        class_310 mc = class_310.method_1551();
        captureFramebuffer.process(n, n2);
        if (captureFramebuffer.method_30277() == null) {
            return;
        }
        this.update();
        this.initializeRenderResources(mc);
        if (this.itemProjectionMatrix == null) {
            this.itemProjectionMatrix = new class_11278("wex-capture-item", -1000.0f, 1000.0f, true);
        }
        RenderSystem.getDevice().createCommandEncoder().clearColorAndDepthTextures(captureFramebuffer.method_30277(), 0, captureFramebuffer.method_30278(), 1.0);
        class_10444 itemState = new class_10444();
        mc.method_65386().method_65598(itemState, stack, itemDisplayContext, (class_1937)mc.field_1687, null, 0);
        float f6 = mc.method_22683().method_4495();
        GpuBufferSlice gpuBufferSlice = RenderSystem.getShaderFog();
        GpuTextureView gpuTextureView = RenderSystem.outputColorTextureOverride;
        GpuTextureView gpuTextureView2 = RenderSystem.outputDepthTextureOverride;
        GpuBufferSlice gpuBufferSlice2 = RenderSystem.getShaderFog();
        RenderSystem.backupProjectionMatrix();
        GlRenderStateSnapshot glRenderStateSnapshot = GlRenderStateSnapshot.captureRenderState();
        GpuBufferSlice gpuBufferSlice3 = RenderSystem.getShaderLights();
        boolean bl = GL11.glGetBoolean((int)2930);
        int n3 = GL11.glGetInteger((int)2932);
        int[] nArray = new int[12];
        try {
            RenderSystem.outputColorTextureOverride = captureFramebuffer.method_71639();
            RenderSystem.outputDepthTextureOverride = captureFramebuffer.method_71640();
            RenderSystem.setProjectionMatrix((GpuBufferSlice)this.itemProjectionMatrix.method_71092((float)n / f6, (float)n2 / f6), (class_10366)class_10366.field_54954);
            RenderSystem.setShaderFog((GpuBufferSlice)gpuBufferSlice);
            mc.field_1773.method_71114().method_71034(class_308.class_11274.field_60027);
            GlStateManager._activeTexture((int)GL11.glGetInteger((int)34016));
            for (int i = 0; i < nArray.length; ++i) {
                GlStateManager._activeTexture((int)(33984 + i));
                nArray[i] = GL11.glGetInteger((int)32873);
                GlStateManager._bindTexture((int)nArray[i]);
            }
            GlStateManager._activeTexture((int)33984);
            class_4587 matrices2 = new class_4587();
            matrices2.method_46416(f, f2, 0.0f);
            matrices2.method_22905(f3, -f3, f3);
            if (f5 != 0.0f) {
                matrices2.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(f5));
            }
            matrices2.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(f4));
            class_11661 orderedRenderCommandQueueImpl = this.renderDispatcher.method_73003();
            itemState.method_65604(matrices2, (class_11659)orderedRenderCommandQueueImpl, 0xF000F0, class_4608.field_21444, 0);
            this.renderDispatcher.method_73002();
            this.immediate2.method_22993();
        }
        finally {
            for (int i = 0; i < nArray.length; ++i) {
                GlStateManager._activeTexture((int)(33984 + i));
                GlStateManager._bindTexture((int)nArray[i]);
            }
            GlStateManager._activeTexture((int)33984);
            RenderSystem.restoreProjectionMatrix();
            RenderSystem.setShaderFog((GpuBufferSlice)gpuBufferSlice2);
            RenderSystem.setShaderLights((GpuBufferSlice)gpuBufferSlice3);
            RenderSystem.outputColorTextureOverride = gpuTextureView;
            RenderSystem.outputDepthTextureOverride = gpuTextureView2;
            glRenderStateSnapshot.restore();
            GL11.glDepthMask((boolean)bl);
            GL11.glDepthFunc((int)n3);
        }
    }
}

