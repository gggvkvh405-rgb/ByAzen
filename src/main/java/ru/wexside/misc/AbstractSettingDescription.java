/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.wexside.misc;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.joml.Matrix4f;
import ru.wexside.misc.ContainerDisplay;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.TextLayoutUtils;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.VisibilityAnimationCache;

public abstract class AbstractSettingDescription {
    protected final Supplier<String> supplier;
    protected final float value;
    protected List<String> wrappedLines;
    protected float value2 = -1.0f;
    protected final String string2;
    protected float value3;
    protected final float value4;
    protected final ContainerDisplay containerDisplay;
    protected final GuiBounds bounds2;
    protected final float value5;
    protected final float value6;
    protected final VisibilityAnimationCache visibilityAnimationCache = new VisibilityAnimationCache();
    protected final float value7;

    protected AbstractSettingDescription(GuiBounds bounds2, Supplier<String> supplier, String string, ContainerDisplay containerDisplay, float f, float f2, float f3) {
        this.value6 = 20.0f;
        this.value = 1.0f;
        this.value3 = Float.NaN;
        this.bounds2 = bounds2;
        this.supplier = supplier;
        this.string2 = string;
        this.containerDisplay = containerDisplay;
        this.value4 = f;
        this.value7 = f2;
        this.value5 = f3;
    }

    protected final List<String> getList() {
        if (!this.hasDescription()) {
            return List.of("");
        }
        if (this.value2 <= 0.0f) {
            if (this.value3 != -1.0f || this.wrappedLines == null) {
                this.value3 = -1.0f;
                this.wrappedLines = List.of(this.string2);
            }
            return this.wrappedLines;
        }
        if (this.wrappedLines == null || this.value3 != this.value2) {
            this.value3 = this.value2;
            this.wrappedLines = TextLayoutUtils.process2(this.string2, FontRegistry.font2, this.value7, this.value2);
        }
        return this.wrappedLines;
    }

    protected final boolean hasDescription() {
        return this.string2 != null && !this.string2.isBlank();
    }

    protected final float getFloatType() {
        return FontRegistry.font2.process4("A", this.value7);
    }

    public String getString() {
        return this.string2;
    }

    public float getFloatType2() {
        return this.value4;
    }

    public ContainerDisplay getContainerDisplay() {
        return this.containerDisplay;
    }

    protected abstract float process2(float var1);

    public void setFloatType(float f) {
        this.value2 = f;
    }

    public final float getVisibilityProgress() {
        return this.visibilityAnimationCache.process(this.isActive(), this.hasDescription(), 20.0f);
    }

    protected final void renderContent(Matrix4f matrix4f, GuiDrawApi drawApi, float f) {
        float f2 = this.getVisibilityProgress();
        float f3 = this.hasDescription() ? 1.0f - f2 : 0.0f;
        float f4 = this.bounds2.getX() + this.value5;
        float f5 = this.process2(f3);
        FontRegistry.font2.process2(matrix4f, drawApi, this.getString2(), f4, f5, this.value4, ThemeColors.textPrimary());
        if (!this.hasDescription()) {
            return;
        }
        float f6 = f2 * f;
        if (f6 <= 0.001f) {
            return;
        }
        int n = ColorUtils.withAlpha(ThemeColors.textMuted(), 255.0f * f6);
        List<String> list = this.getList();
        float f7 = this.getFloatType();
        float f8 = this.getDescriptionBaseline();
        for (int i = 0; i < list.size(); ++i) {
            float f9 = f8 + (float)i * (f7 + 1.0f);
            FontRegistry.font2.process2(matrix4f, drawApi, list.get(i), f4, f9, this.value7, n);
        }
    }

    protected final float getFloatType3() {
        if (!this.hasDescription()) {
            return 0.0f;
        }
        int n = this.getList().size();
        if (n <= 0) {
            return 0.0f;
        }
        float f = this.getFloatType();
        return (float)n * f + (float)(n - 1) * 1.0f;
    }

    public final String getString2() {
        return this.supplier.get();
    }

    protected abstract boolean isActive();

    public float getFloatType4() {
        return this.value7;
    }

    public float getContentOffset() {
        return this.value5;
    }

    protected abstract float getDescriptionBaseline();

    public float getFloatType6() {
        Objects.requireNonNull(this);
        return 20.0f;
    }

    public float getFloatType7() {
        Objects.requireNonNull(this);
        return 1.0f;
    }

    public final void process3(Matrix4f matrix4f, GuiDrawApi drawApi) {
        this.renderContent(matrix4f, drawApi, 1.0f);
    }

    public GuiBounds getBounds() {
        return this.bounds2;
    }
}

