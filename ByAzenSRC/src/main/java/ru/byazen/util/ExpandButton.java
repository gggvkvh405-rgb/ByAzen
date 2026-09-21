/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.byazen.util;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
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

public final class ExpandButton
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private boolean expanded = false;
    private float value;
    private boolean interactive = true;

    public ExpandButton(GuiBounds bounds2) {
        super(bounds2);
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        if (!this.interactive) {
            return false;
        }
        if (!this.getBounds().contains(n, n2)) {
            return false;
        }
        if (n3 == 0) {
            this.expanded = !this.expanded;
        }
        return true;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        this.value = FrameInterpolator.lerpTowards(this.value, this.expanded ? 1.0f : 0.0f, 15.0f);
        drawApi.drawRoundedRectangleOutlined(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0f, 1.0f, ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0f), ThemeColors.borderPrimary());
        float f2 = 6.0f;
        float f3 = FontRegistry.font3.process3("F", f2);
        float f4 = FontRegistry.font3.process4("F", f2);
        float f5 = bounds2.getX() + (bounds2.getWidth() - f3) / 2.0f;
        float f6 = bounds2.getY() + (bounds2.getHeight() - f4) / 2.0f;
        float f7 = bounds2.getX() + bounds2.getWidth() / 2.0f;
        float f8 = bounds2.getY() + bounds2.getHeight() / 2.0f;
        Matrix4f matrix4f2 = new Matrix4f((Matrix4fc)matrix4f).translate(f7, f8, 0.0f).rotateZ((float)Math.toRadians(90.0f * this.value)).translate(-f7, -f8, 0.0f);
        FontRegistry.font3.process5(matrix4f2, drawApi, "F", f5, f6, f2, ThemeColors.textMuted());
        return bounds2.getY() + bounds2.getHeight();
    }

    public boolean isActive() {
        return this.expanded;
    }

    public float getFloatType() {
        return this.value;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void setInteractive(boolean interactive) {
        this.interactive = interactive;
    }

    @Override
    public boolean isActive2() {
        return this.interactive;
    }
}

