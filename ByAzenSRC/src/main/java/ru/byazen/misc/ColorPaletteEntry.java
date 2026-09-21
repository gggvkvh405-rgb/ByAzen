/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.misc;

import java.util.Objects;
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
import ru.byazen.util.ToggleIndicatorRenderer;

public final class ColorPaletteEntry
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final ColorSetting colorSetting;
    private final float value;
    private final int slot;
    private final ToggleIndicatorRenderer toggleIndicatorRenderer = new ToggleIndicatorRenderer();

    public ColorPaletteEntry(ColorSetting colorSetting, int n) {
        super(new GuiBounds(0.0f, 0.0f, 5.0f, 5.0f));
        this.value = 5.0f;
        this.colorSetting = colorSetting;
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
        return n3 == 0 && this.getBounds().contains(n, n2);
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        boolean bl = this.colorSetting.getSelectedIndex() == this.slot;
        this.toggleIndicatorRenderer.process(matrix4f, drawApi, bounds2, this.getIntType2(), bl);
        return bounds2.getX() + bounds2.getWidth();
    }

    public int getIntType() {
        return this.slot;
    }

    public float getFloatType() {
        return 5.0f;
    }

    public ToggleIndicatorRenderer getToggleIndicatorRenderer() {
        return this.toggleIndicatorRenderer;
    }

    private int getIntType2() {
        return this.colorSetting.getPrimaryColor(this.slot);
    }

    public ColorSetting getColorSetting() {
        return this.colorSetting;
    }

    public float getFloatType2() {
        Objects.requireNonNull(this);
        return 5.0f;
    }
}

