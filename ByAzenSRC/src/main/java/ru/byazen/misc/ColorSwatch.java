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
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.setting.ColorSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.RecentColorPalette;
import ru.byazen.util.ToggleIndicatorRenderer;

public final class ColorSwatch
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final ColorSetting colorSetting;
    private final ToggleIndicatorRenderer toggleIndicatorRenderer = new ToggleIndicatorRenderer();
    private final int slot;
    private final RecentColorPalette recentColorPalette;
    private final float value;

    public ColorSwatch(ColorSetting colorSetting, int n) {
        super(new GuiBounds(0.0f, 0.0f, 7.5f, 7.5f));
        this.value = 7.5f;
        this.colorSetting = colorSetting;
        this.recentColorPalette = colorSetting.getRecentColors();
        this.slot = n;
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
        int n4 = this.recentColorPalette.getColor(this.slot);
        this.colorSetting.setEditingColor(n4);
        return true;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        int n = this.recentColorPalette.getColor(this.slot);
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        this.toggleIndicatorRenderer.process(matrix4f, drawApi, bounds2, n, n == this.colorSetting.getColor());
        return bounds2.getX() + bounds2.getWidth();
    }

    public float getFloatType() {
        return 7.5f;
    }
}

