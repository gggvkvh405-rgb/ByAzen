/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTexture
 *  com.mojang.blaze3d.textures.GpuTextureView
 */
package ru.wexside.render;

import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;

final class MaskTexture
implements AutoCloseable {
    final GpuTexture texture;
    final GpuTextureView view;
    final int width;
    final int height;
    int lastUsedFrame;

    MaskTexture(GpuTexture texture, GpuTextureView view, int width, int height) {
        this.texture = texture;
        this.view = view;
        this.width = width;
        this.height = height;
    }

    @Override
    public void close() {
        this.view.close();
        this.texture.close();
    }
}

