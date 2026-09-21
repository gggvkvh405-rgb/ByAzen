/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.util;

import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.input.InputBindings;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.FrameInterpolator;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.ScaleSettings;
import ru.byazen.misc.ThemeColors;
import ru.byazen.setting.ColorSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.ui.SliderTrack;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.NumberFormatting;
import ru.byazen.util.SliderRenderer;

public final class ColorValueSlider
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private float animatedProgress = -1.0f;
    private final ScaleSettings scaleSettings;
    private final ColorSetting colorSetting;
    private final SliderRenderer sliderRenderer = new SliderRenderer();
    private boolean enabled2;

    public ColorValueSlider(GuiBounds bounds2, ColorSetting colorSetting) {
        super(bounds2);
        this.scaleSettings = new ScaleSettings(1.0, 5.0, 0, true, 1.0, 1.0);
        this.colorSetting = colorSetting;
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        if (n3 != 0 || !this.getBounds().contains(n, n2)) {
            return false;
        }
        SliderTrack track = this.process4(this.getBounds().getX(), this.getBounds().getY());
        if ((float)n2 < track.y() - 3.0f || (float)n2 > track.y() + this.sliderRenderer.getFloatType3() + 3.0f) {
            return false;
        }
        this.enabled2 = true;
        this.setFloatType2(n);
        return true;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        float f2 = this.getNormalizedValue();
        this.animatedProgress = this.animatedProgress < 0.0f ? f2 : FrameInterpolator.lerpTowards(this.animatedProgress, f2, this.enabled2 ? 35.0f : 20.0f);
        FontRegistry.font4.process2(matrix4f, drawApi, "\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0435\u043b\u0438\u0432\u0430\u043d\u0438\u044f \u0446\u0432\u0435\u0442\u0430", 0.0f, 0.0f, 5.5f, ThemeColors.textMuted());
        SliderTrack track = this.process4(0.0f, 0.0f);
        String string = NumberFormatting.format(this.colorSetting.getAstolfoSpeedPercent(), 0);
        String string2 = NumberFormatting.format(5.0, 0);
        String string3 = NumberFormatting.format(1.0, 0);
        String string4 = string3 + " - " + string2;
        float f3 = track.y() - FontRegistry.font2.process4("1", 6.5f) - 2.0f;
        FontRegistry.font2.process2(matrix4f, drawApi, string, track.x(), f3, 6.5f, ThemeColors.accent());
        FontRegistry.font2.process2(matrix4f, drawApi, string4, track.x() + track.width() - FontRegistry.font2.process3(string4, 6.5f), f3, 6.5f, ThemeColors.textMuted());
        int n = ThemeColors.borderSubtle();
        this.sliderRenderer.process2(matrix4f, drawApi, track, n);
        this.sliderRenderer.renderTickMarks(matrix4f, drawApi, track, this.scaleSettings, n, ThemeColors.textDisabled());
        float f4 = track.width() * this.sliderRenderer.process4(this.animatedProgress, this.scaleSettings);
        drawApi.drawRoundedRectangle(matrix4f, track.x(), track.y(), f4, this.sliderRenderer.getFloatType3(), 2.0f, ThemeColors.accent());
        this.sliderRenderer.process(matrix4f, drawApi, track.x() + f4, track.y() + this.sliderRenderer.getFloatType3() / 2.0f, ThemeColors.accent());
        return bounds2.getY() + bounds2.getHeight();
    }

    public void setFloatType(float f) {
        if (!this.enabled2) {
            return;
        }
        if (!InputBindings.isMouseButtonPressed(0)) {
            this.enabled2 = false;
            return;
        }
        this.setFloatType2(f);
    }

    private float getFloatType() {
        return FontRegistry.font4.process4("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0435\u043b\u0438\u0432\u0430\u043d\u0438\u044f \u0446\u0432\u0435\u0442\u0430", 5.5f);
    }

    private void setFloatType2(float f) {
        SliderTrack track = this.process4(this.getBounds().getX(), this.getBounds().getY());
        float f2 = Math.max(0.0f, Math.min(track.width(), f - track.x()));
        float f3 = this.sliderRenderer.process3(f2 / track.width(), this.scaleSettings);
        this.colorSetting.setAstolfoSpeedPercent((int)Math.round(1.0 + 4.0 * (double)f3));
    }

    private float getNormalizedValue() {
        return (float)(((double)this.colorSetting.getAstolfoSpeedPercent() - 1.0) / 4.0);
    }

    private SliderTrack process4(float f, float f2) {
        float f3 = f2 + this.getFloatType() + 2.5f;
        float f4 = this.sliderRenderer.getFloatType();
        GuiBounds bounds2 = new GuiBounds(f - f4, f3, this.getBounds().getWidth() + f4 * 2.0f, 0.0f);
        return this.sliderRenderer.process7(bounds2, FontRegistry.font2.process4("1", 6.5f));
    }

    public float getFloatType2() {
        return this.process4(0.0f, 0.0f).y() + this.sliderRenderer.getFloatType3() + 4.0f;
    }
}

