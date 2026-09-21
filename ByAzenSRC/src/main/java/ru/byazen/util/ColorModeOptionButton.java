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
import ru.byazen.misc.ColorModeLabels;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.FrameInterpolator;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.ThemeColors;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;

final class ColorModeOptionButton
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final float value;
    private final float value2;
    private final float value3;
    private final ColorModeLabels colorModeLabels;
    private final float value4;
    private boolean enabled;
    private float value5;

    public ColorModeOptionButton(GuiBounds bounds2, ColorModeLabels colorModeLabels) {
        super(bounds2);
        this.value = 7.0f;
        this.value2 = 0.75f;
        this.value4 = 6.5f;
        this.value3 = 30.0f;
        this.colorModeLabels = colorModeLabels;
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void setBooleanType(boolean bl) {
        this.enabled = bl;
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        return n3 == 0 && this.getBounds().contains(n, n2);
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        this.value5 = FrameInterpolator.lerpTowards(this.value5, this.enabled ? 1.0f : 0.0f, 30.0f);
        int n = ColorUtils.withAlpha(ThemeColors.backgroundHover(), (float)Math.round(255.0f * this.value5));
        int n2 = ColorUtils.lerp(ThemeColors.textMuted(), ThemeColors.textPrimary(), this.value5);
        drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0f, n);
        drawApi.drawRoundedRectangleOutlined(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0f, 0.75f, ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0f), ThemeColors.borderPrimary());
        float f2 = FontRegistry.font3.process3(this.colorModeLabels.getString2(), 6.5f);
        float f3 = FontRegistry.font3.process4(this.colorModeLabels.getString2(), 6.5f);
        float f4 = bounds2.getX() + (bounds2.getWidth() - f2) / 2.0f;
        float f5 = bounds2.getY() + (bounds2.getHeight() - f3) / 2.0f;
        FontRegistry.font3.process5(matrix4f, drawApi, this.colorModeLabels.getString2(), f4, f5, 6.5f, n2);
        return bounds2.getX() + bounds2.getWidth();
    }

    public ColorModeLabels getColorModeLabels() {
        return this.colorModeLabels;
    }
}

