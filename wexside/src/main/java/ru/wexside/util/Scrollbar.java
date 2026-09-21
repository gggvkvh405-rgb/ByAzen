/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.wexside.util;

import org.joml.Matrix4f;
import ru.wexside.input.InputBindings;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.ScrollController;

public final class Scrollbar {
    private ScrollController scrollController;
    private final float value;
    private final float value2;
    private boolean dragging;
    private final float value3;
    private final float value4;
    private float value5;
    private final float value6;
    private final float BlockItem;
    private final float value7;
    private float value8;
    private float value9;
    private float value10;
    private float value11;
    private float value12;
    private final float value13;

    public Scrollbar() {
        this.value4 = 1.5f;
        this.value7 = 3.0f;
        this.value3 = 128.0f;
        this.value6 = 3.5f;
        this.value = 4.0f;
        this.BlockItem = 8.0f;
        this.value13 = 20.0f;
        this.value2 = 20.0f;
    }

    private void setFloatType(float f) {
        if (!this.dragging) {
            return;
        }
        if (!InputBindings.isMouseButtonPressed(0)) {
            this.dragging = false;
            return;
        }
        this.setFloatType2(f);
    }

    public boolean isActive() {
        return this.dragging;
    }

    private boolean isActive2() {
        return this.scrollController != null && this.scrollController.getContentHeight() > this.value12 + 0.5f && this.value12 > 8.0f;
    }

    private GuiBounds getBounds() {
        float f = 11.0f;
        return new GuiBounds(this.value10 - 3.5f - 3.0f - 4.0f, this.value5, f, this.value12);
    }

    private void setFloatType2(float f) {
        float f2 = this.value12 - this.getFloatType();
        if (f2 <= 0.0f) {
            return;
        }
        float f3 = Math.max(0.0f, Math.min(1.0f, (f - this.value9 - this.value5) / f2));
        this.scrollController.scrollTo(f3 * this.scrollController.getMinimumOffset(this.value12), this.value12);
    }

    public void process(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, ScrollController scrollController, float f4, float f5) {
        this.value10 = f;
        this.value5 = f2;
        this.value12 = f3;
        this.scrollController = scrollController;
        boolean bl = this.isActive2();
        this.value11 = FrameInterpolator.lerpTowards(this.value11, bl ? 1.0f : 0.0f, 20.0f);
        if (!bl) {
            this.dragging = false;
        }
        this.setFloatType(f5);
        boolean bl2 = this.dragging || this.getBounds().contains(f4, f5);
        this.value8 = FrameInterpolator.lerpTowards(this.value8, bl2 && bl ? 1.0f : 0.0f, 20.0f);
        if (this.value11 <= 0.01f) {
            return;
        }
        float f6 = 1.5f + 1.5f * this.value8;
        drawApi.drawRoundedRectangle(matrix4f, f - 3.5f - f6, this.getFloatType2(), f6, this.getFloatType(), 128.0f, ColorUtils.multiplyAlpha(ColorUtils.lerp(ThemeColors.borderPrimary(), ThemeColors.borderStrong(), this.value8), this.value11));
    }

    private float getFloatType() {
        return Math.max(8.0f, this.value12 * this.value12 / this.scrollController.getContentHeight());
    }

    private float getFloatType2() {
        float f = this.scrollController.getMinimumOffset(this.value12);
        float f2 = f >= -1.0E-4f ? 0.0f : Math.max(0.0f, Math.min(1.0f, this.scrollController.getOffset() / f));
        return this.value5 + (this.value12 - this.getFloatType()) * f2;
    }

    public boolean onMousePressed(int n, int n2, int n3) {
        if (n3 != 0 || !this.isActive2() || !this.getBounds().contains(n, n2)) {
            return false;
        }
        float f = this.getFloatType2();
        float f2 = this.getFloatType();
        this.dragging = true;
        this.value9 = (float)n2 >= f && (float)n2 <= f + f2 ? (float)n2 - f : f2 / 2.0f;
        this.setFloatType2(n2);
        return true;
    }
}

