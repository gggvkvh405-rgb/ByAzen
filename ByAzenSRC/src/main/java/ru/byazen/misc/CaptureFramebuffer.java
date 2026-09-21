/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.textures.GpuTexture
 *  net.minecraft.class_10868
 *  net.minecraft.class_276
 */
package ru.byazen.misc;

import com.mojang.blaze3d.textures.GpuTexture;
import net.minecraft.class_10868;
import net.minecraft.class_276;

public final class CaptureFramebuffer
extends class_276 {
    public CaptureFramebuffer() {
        super("wex/vanilla-capture", true);
    }

    public int getIntType() {
        int n;
        GpuTexture gpuTexture = this.method_30277();
        if (gpuTexture instanceof class_10868) {
            class_10868 iIiIIllIl2 = (class_10868)gpuTexture;
            n = iIiIIllIl2.method_68427();
        } else {
            n = 0;
        }
        return n;
    }

    public void process(int n, int n2) {
        if (n <= 0 || n2 <= 0) {
            return;
        }
        if (this.field_1482 == n && this.field_1481 == n2 && this.method_30277() != null) {
            return;
        }
        this.method_1234(n, n2);
    }
}

