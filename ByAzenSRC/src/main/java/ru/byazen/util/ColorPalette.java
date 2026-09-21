/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.byazen.util;

import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.ColorPaletteEntry;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.setting.ColorSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;

public final class ColorPalette
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private float value = 4.0f;
    private final List<ColorPaletteEntry> entries = new ArrayList<ColorPaletteEntry>();
    private final ColorSetting colorSetting;

    public ColorPalette(GuiBounds bounds2, ColorSetting colorSetting) {
        super(bounds2);
        this.colorSetting = colorSetting;
        for (int i = 0; i < colorSetting.getColorCount(); ++i) {
            ColorPaletteEntry colorPaletteEntry = new ColorPaletteEntry(colorSetting, i);
            this.entries.add(colorPaletteEntry);
            this.addChild(colorPaletteEntry);
        }
        this.update4();
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        int n4 = (int)((float)n - this.getBounds().getX());
        int n5 = (int)((float)n2 - this.getBounds().getY());
        for (ColorPaletteEntry colorPaletteEntry : this.entries) {
            if (!colorPaletteEntry.onMousePressed(n4, n5, n3)) continue;
            this.colorSetting.setSelectedIndex(colorPaletteEntry.getIntType());
            return true;
        }
        return false;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        this.update4();
        Matrix4f matrix4f2 = new Matrix4f((Matrix4fc)matrix4f).translate(this.getBounds().getX(), this.getBounds().getY(), 0.0f);
        float f2 = this.getBounds().getX() + this.getBounds().getWidth();
        for (ColorPaletteEntry colorPaletteEntry : this.entries) {
            f2 = colorPaletteEntry.render(f, matrix4f2);
        }
        return f2;
    }

    public float getFloatType() {
        this.update4();
        return this.getBounds().getWidth();
    }

    public float getFloatType2() {
        this.update4();
        return this.getBounds().getHeight();
    }

    private void update4() {
        float f = 0.0f;
        float f2 = 0.0f;
        for (int i = 0; i < this.entries.size(); ++i) {
            ColorPaletteEntry colorPaletteEntry = this.entries.get(i);
            float f3 = colorPaletteEntry.getFloatType();
            colorPaletteEntry.getBounds().setPosition(f, 0.0f);
            colorPaletteEntry.getBounds().setSize(f3, f3);
            f += f3;
            if (i < this.entries.size() - 1) {
                f += this.value;
            }
            f2 = Math.max(f2, f3);
        }
        this.getBounds().setSize(f, f2);
    }
}

