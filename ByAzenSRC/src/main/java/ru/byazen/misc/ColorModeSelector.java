/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.byazen.misc;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.setting.ColorSetting;
import ru.byazen.setting.color.ColorChannel;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.SegmentedControl;
import ru.byazen.util.SegmentedControlStyle;

public final class ColorModeSelector
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final ColorSetting colorSetting;
    private final SegmentedControl segmentedControl;
    private final float value;
    private final SegmentedControlStyle segmentedControlStyle;

    public ColorModeSelector(GuiBounds bounds2, ColorSetting colorSetting) {
        super(bounds2);
        this.value = 11.0f;
        this.colorSetting = colorSetting;
        this.segmentedControlStyle = new SegmentedControlStyle().process12(7.0f).process6(6.0f);
        this.segmentedControl = new SegmentedControl(new GuiBounds(0.0f, 0.0f, 0.0f, 11.0f), this.segmentedControlStyle, "\u041f\u0435\u0440\u0432\u044b\u0439 \u0446\u0432\u0435\u0442", "\u0412\u0442\u043e\u0440\u043e\u0439 \u0446\u0432\u0435\u0442");
        this.segmentedControl.setIntConsumer(n -> colorSetting.setEditingChannel(n == 1 ? ColorChannel.SECONDARY : ColorChannel.PRIMARY));
        this.syncChannel();
        this.addChild(this.segmentedControl);
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
        this.segmentedControl.update();
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        if (!this.getBounds().contains(n, n2)) {
            return false;
        }
        int n4 = (int)((float)n - this.getBounds().getX());
        int n5 = (int)((float)n2 - this.getBounds().getY());
        return this.segmentedControl.onMousePressed(n4, n5, n3);
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        this.update4();
        this.syncChannel();
        this.segmentedControl.render(f, new Matrix4f((Matrix4fc)matrix4f).translate(this.getBounds().getX(), this.getBounds().getY(), 0.0f));
        return this.getBounds().getY() + this.getBounds().getHeight();
    }

    private void syncChannel() {
        this.segmentedControl.setIntType2(this.colorSetting.getEditingChannel() == ColorChannel.SECONDARY ? 1 : 0);
    }

    public float getFloatType2() {
        return 11.0f;
    }

    private void update4() {
        float f = this.getBounds().getWidth() / 2.0f;
        this.segmentedControlStyle.process2(f);
        this.segmentedControlStyle.process11(11.0f);
        this.segmentedControl.getBounds().setPosition(0.0f, 0.0f);
        this.segmentedControl.getBounds().setSize(this.getBounds().getWidth(), 11.0f);
        this.getBounds().setSize(this.getBounds().getWidth(), 11.0f);
    }
}

