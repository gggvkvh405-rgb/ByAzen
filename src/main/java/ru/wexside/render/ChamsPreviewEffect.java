/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 *  net.minecraft.class_10868
 *  net.minecraft.class_6367
 */
package ru.wexside.render;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.class_10868;
import net.minecraft.class_6367;
import ru.wexside.misc.CaptureFramebuffer;
import ru.wexside.render.ChamsCompositor;
import ru.wexside.render.ChamsSettings;

final class ChamsPreviewEffect {
    private final ChamsCompositor compositor = new ChamsCompositor();
    private final long startedAtMillis = System.currentTimeMillis();
    private class_6367 sceneFramebuffer;
    private class_6367 effectFramebuffer;
    private int width;
    private int height;

    ChamsPreviewEffect() {
    }

    int render(CaptureFramebuffer sourceFramebuffer, int width, int height, int previewSize, ChamsSettings settings) {
        if (sourceFramebuffer == null || settings == null || width <= 0 || height <= 0 || previewSize <= 0 || !settings.isVisibleFillEnabled() && !settings.isHiddenFillEnabled()) {
            return 0;
        }
        GpuTextureView sourceColorView = sourceFramebuffer.method_71639();
        GpuTextureView sourceDepthView = sourceFramebuffer.method_71640();
        if (sourceColorView == null || sourceDepthView == null) {
            return 0;
        }
        this.ensureFramebuffers(width, height);
        ChamsPreviewEffect.clearFramebuffer(this.effectFramebuffer);
        ChamsPreviewEffect.clearFramebuffer(this.sceneFramebuffer);
        GpuTextureView sceneDepthView = this.sceneFramebuffer.method_71640();
        GpuTextureView effectView = this.effectFramebuffer.method_71639();
        if (sceneDepthView == null || effectView == null) {
            return 0;
        }
        float elapsed = (float)(System.currentTimeMillis() - this.startedAtMillis) / 700.0f;
        this.compositor.composite(effectView, sourceColorView, sourceDepthView, sourceColorView, sceneDepthView, width, height, settings.getVisibleColor(), settings.getHiddenColor(), settings.isVisibleFillEnabled(), settings.isHiddenFillEnabled(), settings.getMaterialModeIndex(), 0.0f, height - previewSize, previewSize, previewSize, elapsed);
        return ChamsPreviewEffect.getColorTextureId(this.effectFramebuffer);
    }

    void releaseFramebuffers() {
        if (this.sceneFramebuffer != null) {
            this.sceneFramebuffer.method_1238();
            this.sceneFramebuffer = null;
        }
        if (this.effectFramebuffer != null) {
            this.effectFramebuffer.method_1238();
            this.effectFramebuffer = null;
        }
        this.width = 0;
        this.height = 0;
    }

    private void ensureFramebuffers(int width, int height) {
        if (this.sceneFramebuffer != null && this.effectFramebuffer != null && this.width == width && this.height == height) {
            return;
        }
        this.releaseFramebuffers();
        this.sceneFramebuffer = new class_6367("wexside_chams_preview_scene", width, height, true);
        this.effectFramebuffer = new class_6367("wexside_chams_preview_effect", width, height, false);
        this.width = width;
        this.height = height;
    }

    private static void clearFramebuffer(class_6367 framebuffer) {
        GpuTexture colorTexture = framebuffer.method_30277();
        if (colorTexture == null) {
            return;
        }
        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
        GpuTexture depthTexture = framebuffer.method_30278();
        if (depthTexture != null) {
            commandEncoder.clearColorAndDepthTextures(colorTexture, 0, depthTexture, 1.0);
        } else {
            commandEncoder.clearColorTexture(colorTexture, 0);
        }
    }

    private static int getColorTextureId(class_6367 framebuffer) {
        int n;
        GpuTexture texture = framebuffer.method_30277();
        if (texture instanceof class_10868) {
            class_10868 glTexture = (class_10868)texture;
            n = glTexture.method_68427();
        } else {
            n = 0;
        }
        return n;
    }
}

