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
import java.util.Objects;
import java.util.function.Consumer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.ColorModeLabels;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.setting.color.ColorMode;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.ColorModeOptionButton;

public final class DoubleColor
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private ColorMode colorMode2;
    private final float value;
    private Consumer<ColorMode> consumer;
    private final List<ColorModeOptionButton> modeButtons = new ArrayList<ColorModeOptionButton>();
    private final float value2;
    private final float value3;

    public DoubleColor(GuiBounds bounds2) {
        super(bounds2);
        this.value = 12.5f;
        this.value3 = 9.5f;
        this.value2 = 2.0f;
        this.colorMode2 = ColorMode.STATIC;
        this.consumer = colorMode2 -> {};
        this.update3();
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
        if (n3 != 0 || !this.getBounds().contains(n, n2)) {
            return false;
        }
        int n4 = (int)((float)n - this.getBounds().getX());
        int n5 = (int)((float)n2 - this.getBounds().getY());
        for (ColorModeOptionButton colorModeOptionButton : this.modeButtons) {
            if (!colorModeOptionButton.onMousePressed(n4, n5, n3)) continue;
            ColorMode colorMode2 = colorModeOptionButton.getColorModeLabels().getColorMode();
            this.setColorMode(colorMode2 == this.colorMode2 ? ColorMode.STATIC : colorMode2);
            return true;
        }
        return false;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        this.update4();
        Matrix4f matrix4f2 = new Matrix4f((Matrix4fc)matrix4f).translate(this.getBounds().getX(), this.getBounds().getY(), 0.0f);
        for (ColorModeOptionButton colorModeOptionButton : this.modeButtons) {
            colorModeOptionButton.render(f, matrix4f2);
        }
        return this.getBounds().getY() + this.getBounds().getHeight();
    }

    public float getFloatType() {
        if (this.modeButtons.isEmpty()) {
            return 0.0f;
        }
        return (float)this.modeButtons.size() * 12.5f + (float)Math.max(0, this.modeButtons.size() - 1) * 2.0f;
    }

    private void syncSelection() {
        for (ColorModeOptionButton colorModeOptionButton : this.modeButtons) {
            colorModeOptionButton.setBooleanType(colorModeOptionButton.getColorModeLabels().getColorMode() == this.colorMode2);
        }
    }

    public void setColorMode(ColorMode colorMode2) {
        ColorMode colorMode3;
        ColorMode colorMode4 = colorMode3 = colorMode2 == null ? ColorMode.STATIC : colorMode2;
        if (this.colorMode2 == colorMode3) {
            return;
        }
        this.colorMode2 = colorMode3;
        this.syncSelection();
        this.consumer.accept(this.colorMode2);
    }

    public void setConsumer(Consumer<ColorMode> consumer) {
        this.consumer = consumer == null ? colorMode2 -> {} : consumer;
    }

    public float getSpacing() {
        Objects.requireNonNull(this);
        return 2.0f;
    }

    public Consumer<ColorMode> getConsumer() {
        return this.consumer;
    }

    private void update3() {
        List<ColorModeLabels> list = List.of(new ColorModeLabels(ColorMode.ASTOLFO, "Astolfo", "\u0434"), new ColorModeLabels(ColorMode.DOUBLE_COLOR, "Double Color", "V"));
        for (ColorModeLabels colorModeLabels : list) {
            ColorModeOptionButton colorModeOptionButton = new ColorModeOptionButton(new GuiBounds(0.0f, 0.0f, 12.5f, 9.5f), colorModeLabels);
            this.modeButtons.add(colorModeOptionButton);
            this.addChild(colorModeOptionButton);
        }
        this.syncSelection();
    }

    public ColorMode getColorMode() {
        return this.colorMode2;
    }

    public float getFloatType3() {
        Objects.requireNonNull(this);
        return 9.5f;
    }

    public float getFloatType4() {
        Objects.requireNonNull(this);
        return 12.5f;
    }

    public List<ColorModeOptionButton> getList() {
        return this.modeButtons;
    }

    public float getFloatType2() {
        return 9.5f;
    }

    private void update4() {
        float f = 0.0f;
        for (ColorModeOptionButton colorModeOptionButton : this.modeButtons) {
            colorModeOptionButton.getBounds().setPosition(f, 0.0f);
            colorModeOptionButton.getBounds().setSize(12.5f, 9.5f);
            f += 14.5f;
        }
        this.getBounds().setSize(this.getFloatType(), this.getFloatType2());
    }
}

