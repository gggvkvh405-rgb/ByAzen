/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class ToggleIndicatorRenderer {
    private float value;
    private final float value2;
    private final float value3;

    public ToggleIndicatorRenderer() {
        this.value2 = 30.0f;
        this.value3 = 0.75f;
    }

    public void process(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2, int n, boolean bl) {
        float f;
        float f2 = f = Math.min(bounds2.getWidth(), bounds2.getHeight());
        float f3 = f / 2.0f;
        float f4 = bounds2.getX() + bounds2.getWidth() / 2.0f;
        float f5 = bounds2.getY() + bounds2.getHeight() / 2.0f;
        float f6 = Math.max(1.0f, f * 0.18f);
        this.value = FrameInterpolator.lerpTowards(this.value, bl ? 1.0f : 0.0f, 30.0f);
        drawApi.drawRoundedRectangleBordered(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), f2, 0.0f, n);
        drawApi.drawCircleSector(matrix4f, f4, f5, 0.0f, 360.0f, f6, f3, ColorUtils.withAlpha(ThemeColors.backgroundControl(), 255.0f * this.value), 0.75f);
    }
}

