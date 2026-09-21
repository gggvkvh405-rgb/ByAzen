/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.util;

import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.setting.ColorSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.util.AbstractColorSlider;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.SliderMarkerRenderer;

public final class AlphaSlider
extends AbstractColorSlider
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final float value;

    public AlphaSlider(GuiBounds bounds2, ColorSetting colorSetting, SliderMarkerRenderer sliderMarkerRenderer) {
        super(bounds2, colorSetting, sliderMarkerRenderer);
        this.value = 3.0f;
    }

    @Override
    protected float getMarkerY() {
        return this.getBounds().getY() + this.getBounds().getHeight() / 2.0f;
    }

    @Override
    protected void process(float f, float f2) {
        float[] fArray = this.getHsbComponents();
        this.process6(fArray[0], fArray[1], fArray[2], Math.round(this.process8(f / this.getBounds().getWidth()) * 255.0f));
    }

    @Override
    protected void setMatrix4f(Matrix4f matrix4f) {
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        GuiBounds bounds2 = this.getBounds();
        float f = bounds2.getX();
        float f2 = bounds2.getY();
        float f3 = bounds2.getWidth();
        float f4 = bounds2.getHeight();
        int n = this.getBaseColor() | 0xFF000000;
        int n2 = n & 0xFFFFFF;
        drawApi.drawRoundedRectangleGradient(matrix4f, f, f2, f3, f4, 3.0f, n2, n, n, n2);
    }

    @Override
    protected float getMarkerX() {
        return this.getBounds().getX() + (float)this.getIntType2() / 255.0f * this.getBounds().getWidth();
    }
}

