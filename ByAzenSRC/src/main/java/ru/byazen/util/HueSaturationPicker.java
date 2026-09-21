/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.byazen.util;

import java.awt.Color;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.PreparedLayer;
import ru.byazen.misc.ThemeColors;
import ru.byazen.setting.ColorSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.util.AbstractColorSlider;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.SliderMarkerRenderer;

public final class HueSaturationPicker
extends AbstractColorSlider
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final float value;
    private boolean enabled;
    private int slot = Integer.MIN_VALUE;
    private float value2;
    private boolean enabled2;
    private float value3;
    private final float value4;

    public HueSaturationPicker(GuiBounds bounds2, ColorSetting colorSetting, SliderMarkerRenderer sliderMarkerRenderer, float f) {
        super(bounds2, colorSetting, sliderMarkerRenderer);
        this.value = 2.0f;
        this.value4 = f;
    }

    @Override
    public void update() {
        super.update();
        this.syncFromMouse();
        if (this.enabled2) {
            this.update4();
        }
    }

    private float calculateMarkerY() {
        return this.getBounds().getY() + this.value3 * this.getBounds().getHeight();
    }

    private boolean hasExternalColorChange() {
        if (!this.enabled) {
            return true;
        }
        return this.getCurrentRgb() != this.slot;
    }

    @Override
    protected float getMarkerY() {
        if (!this.enabled) {
            this.update3();
        }
        return this.calculateMarkerY();
    }

    private int getCurrentRgb() {
        int n = this.colorSettingColorChanged() ? this.getIntType3() : this.getBaseColor();
        return n & 0xFFFFFF;
    }

    private boolean colorSettingColorChanged() {
        return this.enabled && (this.getIntType3() & 0xFFFFFF) != this.slot;
    }

    @Override
    protected void process(float f, float f2) {
        this.process2(this.process4(f, this.getBounds().getWidth()), this.process4(f2, this.getBounds().getHeight()));
        this.enabled2 = true;
    }

    private float readSaturationFromCursor() {
        float f = Math.max(0.0f, this.getBounds().getWidth() - 2.0f);
        if (f <= 0.0f) {
            return 0.0f;
        }
        float f2 = this.getSliderMarkerRenderer().getAnimatedX(this.markerX());
        return this.process8((f2 - this.getBounds().getX()) / f);
    }

    private void syncFromMouse() {
        if (this.hasExternalColorChange()) {
            this.update3();
        }
    }

    private float markerX() {
        float f = Math.max(0.0f, this.getBounds().getWidth() - 2.0f);
        return this.getBounds().getX() + this.value2 * f;
    }

    @Override
    protected void setMatrix4f(Matrix4f matrix4f) {
        this.syncFromMouse();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        GuiBounds bounds2 = this.getBounds();
        float f = bounds2.getX();
        float f2 = bounds2.getY();
        float f3 = bounds2.getWidth();
        float f4 = bounds2.getHeight();
        float[] fArray = this.getHsbComponents();
        int n = Color.HSBtoRGB(fArray[0], 1.0f, 1.0f) | 0xFF000000;
        PreparedLayer preparedLayer = drawApi.prepareDedicatedLayer(matrix4f, f, f2, f3, f4, 0.0f);
        drawApi.beginLayerFrame(preparedLayer.getTexture());
        Matrix4f matrix4f2 = new Matrix4f((Matrix4fc)preparedLayer.getContentMatrix()).translate(preparedLayer.contentX(), preparedLayer.contentY(), 0.0f);
        drawApi.fillRectangle(matrix4f2, 0.0f, 0.0f, f3, f4, ThemeColors.backgroundControl());
        drawApi.drawColorGradient(matrix4f2, 0.0f, 0.0f, f3, f4, ColorUtils.withAlpha(ThemeColors.backgroundControl(), 0.0f), n, n, ColorUtils.withAlpha(ThemeColors.backgroundControl(), 0.0f));
        drawApi.drawColorGradient(matrix4f2, 0.0f, 0.0f, f3, f4, ColorUtils.rgba(0, 0, 0, 255), ColorUtils.rgba(0, 0, 0, 255), ColorUtils.rgba(0, 0, 0, 0), ColorUtils.rgba(0, 0, 0, 0));
        drawApi.endLayerFrame();
        drawApi.drawPreparedLayerRounded(matrix4f, preparedLayer, this.value4, -1);
    }

    private void update3() {
        float[] fArray = this.getHsbComponents();
        this.value2 = this.process8(fArray[1]);
        this.value3 = this.process8(1.0f - fArray[2]);
        this.enabled = true;
        this.enabled2 = false;
        this.slot = this.getCurrentRgb();
    }

    private void update4() {
        float[] fArray = this.getHsbComponents();
        this.process6(fArray[0], this.readSaturationFromCursor(), 1.0f - this.readBrightnessFromCursor(), this.getIntType2());
        this.slot = this.getCurrentRgb();
    }

    private void process2(float f, float f2) {
        this.value2 = this.process8(f);
        this.value3 = this.process8(f2);
        this.enabled = true;
    }

    private float readBrightnessFromCursor() {
        float f = this.getBounds().getHeight();
        if (f <= 0.0f) {
            return 0.0f;
        }
        float f2 = this.getSliderMarkerRenderer().getAnimatedY(this.getMarkerY());
        return this.process8((f2 - this.getBounds().getY()) / f);
    }

    @Override
    protected float getMarkerX() {
        if (!this.enabled) {
            this.update3();
        }
        return this.markerX();
    }
}

