/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTexture
 */
package ru.wexside.misc;

import com.mojang.blaze3d.textures.GpuTexture;
import java.util.Map;

public interface GuiRenderTargetAccessor {
    public GpuTexture getItemAtlasDepthTexture();

    public Map<Object, ?> getRenderedItems();

    public void setItemAtlasX(int var1);

    public GpuTexture getItemAtlasTexture();

    public void setItemAtlasY(int var1);
}

