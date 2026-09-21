/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.misc;

import org.joml.Matrix4f;
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

public final class LabeledSegmentOption
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private Runnable runnable = () -> {};
    private final float titleHeight;
    private boolean enabled;
    private final float titleWidth;
    private final float optionHeight;
    private float selectionAnimation;
    private final String string3;
    private final String string4;

    public LabeledSegmentOption(String string, String string2, float f) {
        super(new GuiBounds(0.0f, 0.0f, 0.0f, f));
        this.string3 = string;
        this.string4 = string2;
        this.titleWidth = FontRegistry.font3.process3(string, 6.0f);
        this.titleHeight = FontRegistry.font3.process4(string, 6.0f);
        this.optionHeight = FontRegistry.font4.process4(string2, 6.0f);
        float f2 = FontRegistry.font4.process3(string2, 6.0f);
        float f3 = 4.0f + this.titleWidth + 2.0f + f2 + 4.0f;
        this.getBounds().setSize(f3, f);
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
        if (n3 != 0 || !this.getBounds().contains(n, n2)) {
            return false;
        }
        this.runnable.run();
        return true;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        this.selectionAnimation = FrameInterpolator.lerpTowards(this.selectionAnimation, this.enabled ? 1.0f : 0.0f, 25.0f);
        drawApi.drawRoundedRectangleOutlined(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0f, 0.75f, ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0f), ThemeColors.borderPrimary());
        if (this.selectionAnimation > 0.001f) {
            drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0f, ColorUtils.withAlpha(ThemeColors.accent(), 255.0f * this.selectionAnimation));
        }
        int n = ColorUtils.lerp(ThemeColors.textSecondary(), ThemeColors.backgroundControl(), this.selectionAnimation);
        float f2 = bounds2.getX() + 4.0f;
        float f3 = bounds2.getY() + (bounds2.getHeight() - this.titleHeight) / 2.0f;
        FontRegistry.font3.process5(matrix4f, drawApi, this.string3, f2, f3, 6.0f, n);
        float f4 = f2 + this.titleWidth + 2.0f;
        float f5 = bounds2.getY() + (bounds2.getHeight() - this.optionHeight) / 2.0f;
        FontRegistry.font4.process2(matrix4f, drawApi, this.string4, f4, f5, 6.0f, n);
        return bounds2.getY() + bounds2.getHeight();
    }

    public boolean isActive() {
        return this.enabled;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable == null ? () -> {} : runnable;
    }
}

