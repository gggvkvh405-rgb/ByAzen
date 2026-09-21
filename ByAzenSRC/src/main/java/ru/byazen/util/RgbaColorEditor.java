/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.util;

import java.util.function.Supplier;
import ru.byazen.misc.AbstractColorTextEditor;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.ColorChannelTextAdapter;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.TextField;
import ru.byazen.setting.ColorSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.util.ColorUtils;

public final class RgbaColorEditor
extends AbstractColorTextEditor
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final TextField textField3;
    private final TextField textField4;
    private final TextField textField5;
    private final TextField textField6;
    private final ColorSetting colorSetting;

    public RgbaColorEditor(GuiBounds bounds2, ColorSetting colorSetting) {
        super(bounds2);
        this.colorSetting = colorSetting;
        Supplier<String> supplier = this::getString;
        this.textField5 = new TextField(new GuiBounds(0.0f, 0.0f, 16.875f, 12.0f), new ColorChannelTextAdapter(this, 0, supplier));
        this.textField6 = new TextField(new GuiBounds(17.875f, 0.0f, 16.875f, 12.0f), new ColorChannelTextAdapter(this, 1, supplier));
        this.textField4 = new TextField(new GuiBounds(35.75f, 0.0f, 16.875f, 12.0f), new ColorChannelTextAdapter(this, 2, supplier));
        this.textField3 = new TextField(new GuiBounds(53.625f, 0.0f, 16.875f, 12.0f), new ColorChannelTextAdapter(this, 3, supplier));
        this.addChild(this.textField5);
        this.addChild(this.textField6);
        this.addChild(this.textField4);
        this.addChild(this.textField3);
        this.getBounds().setSize(this.getFloatType(), this.getFloatType2());
    }

    @Override
    public float getFloatType() {
        return 70.5f;
    }

    public int clampChannel(int n, int n2, int n3) {
        return Math.max(n2, Math.min(n3, n));
    }

    public ColorSetting getColorSetting() {
        return this.colorSetting;
    }

    private String getString() {
        int[] nArray = ColorUtils.unpackRgba(this.colorSetting.getColor());
        int n = nArray[3];
        int n2 = nArray[2];
        int n3 = nArray[1];
        int n4 = nArray[0];
        return "rgba(" + n4 + ", " + n3 + ", " + n2 + ", " + n + ")";
    }

    @Override
    public float getFloatType2() {
        return 12.0f;
    }
}

