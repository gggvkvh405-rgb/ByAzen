/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.misc;

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
import ru.byazen.misc.ModuleBrowser;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.ThemeColors;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;

public final class SidebarToggleButton
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private float value;
    private final ModuleBrowser moduleBrowser;
    private final String string;
    private boolean enabled;
    private float value2;
    private final String string2;

    public SidebarToggleButton(GuiBounds bounds2, ModuleBrowser moduleBrowser) {
        super(bounds2);
        this.string2 = "w";
        this.string = "\u0421\u0432\u0435\u0440\u043d\u0443\u0442\u044c \u0432\u0441\u0451";
        this.moduleBrowser = moduleBrowser;
        this.value = moduleBrowser.isActive2() ? 1.0f : 0.0f;
        this.getBounds().setSize(this.getFloatType(), 11.5f);
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        if (!this.moduleBrowser.isActive2()) {
            return false;
        }
        if (!this.getBounds().contains(n, n2)) {
            return false;
        }
        if (n3 == 0) {
            this.enabled = true;
            this.moduleBrowser.update3();
        }
        return true;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        if (this.enabled && !InputBindings.isMouseButtonPressed(0)) {
            this.enabled = false;
        }
        this.value = FrameInterpolator.lerpTowards(this.value, this.moduleBrowser.isActive2() ? 1.0f : 0.0f, 45.0f);
        this.value2 = FrameInterpolator.lerpTowards(this.value2, this.enabled ? 1.0f : 0.0f, 30.0f);
        if (this.value <= 0.01f) {
            return bounds2.getY() + bounds2.getHeight();
        }
        int n = (int)(255.0f * this.value);
        int n2 = (int)(255.0f * this.value2 * this.value);
        int n3 = ColorUtils.withAlpha(ThemeColors.backgroundHover(), (float)n2);
        drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0f, n3);
        drawApi.drawRoundedRectangleOutlined(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0f, 1.0f, ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0f), ColorUtils.withAlpha(ThemeColors.borderPrimary(), (float)n));
        float f2 = 5.75f;
        float f3 = 5.75f;
        float f4 = FontRegistry.font3.process3("w", f2);
        float f5 = FontRegistry.font2.process3("\u0421\u0432\u0435\u0440\u043d\u0443\u0442\u044c \u0432\u0441\u0451", f3);
        float f6 = f4 + 3.0f + f5;
        float f7 = bounds2.getX() + (bounds2.getWidth() - f6) / 2.0f;
        float f8 = bounds2.getY() + (bounds2.getHeight() - FontRegistry.font3.process4("w", f2)) / 2.0f;
        float f9 = f7 + f4 + 3.0f;
        float f10 = bounds2.getY() + (bounds2.getHeight() - FontRegistry.font2.process4("\u0421\u0432\u0435\u0440\u043d\u0443\u0442\u044c \u0432\u0441\u0451", f3)) / 2.0f;
        FontRegistry.font3.process5(matrix4f, drawApi, "w", f7, f8, f2, ColorUtils.withAlpha(ThemeColors.textSecondary(), (float)n));
        FontRegistry.font2.process2(matrix4f, drawApi, "\u0421\u0432\u0435\u0440\u043d\u0443\u0442\u044c \u0432\u0441\u0451", f9, f10, f3, ColorUtils.withAlpha(ThemeColors.textSecondary(), (float)n));
        return bounds2.getY() + bounds2.getHeight();
    }

    public float getFloatType() {
        float f = 5.75f;
        float f2 = 5.75f;
        float f3 = FontRegistry.font3.process3("w", f) + 3.0f + FontRegistry.font2.process3("\u0421\u0432\u0435\u0440\u043d\u0443\u0442\u044c \u0432\u0441\u0451", f2);
        return f3 + 6.0f;
    }
}

