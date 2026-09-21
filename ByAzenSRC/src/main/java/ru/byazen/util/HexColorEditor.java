/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.util;

import java.util.function.Supplier;
import ru.byazen.misc.AbstractColorTextEditor;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.TextField;
import ru.byazen.setting.ColorSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.HexColorTextCallback;
import ru.byazen.util.AlphaPercentageTextAdapter;
import ru.byazen.util.ColorUtils;

final class HexColorEditor
extends AbstractColorTextEditor
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final float value;
    private final TextField textField;
    private final float value2;
    private final float value3;
    private final float value4;
    private final TextField textField2;
    private final ColorSetting colorSetting;

    HexColorEditor(GuiBounds bounds2, ColorSetting colorSetting) {
        super(bounds2);
        this.value = 12.0f;
        this.value4 = 1.0f;
        this.value2 = 49.5f;
        this.value3 = 20.0f;
        this.colorSetting = colorSetting;
        Supplier<String> supplier = this::getString;
        this.textField2 = new TextField(new GuiBounds(0.0f, 0.0f, 49.5f, 12.0f), new HexColorTextCallback(colorSetting));
        this.textField = new TextField(new GuiBounds(50.5f, 0.0f, 20.0f, 12.0f), new AlphaPercentageTextAdapter(this, supplier), "%");
        this.addChild(this.textField2);
        this.addChild(this.textField);
        this.getBounds().setSize(this.getFloatType(), this.getFloatType2());
    }

    @Override
    public float getFloatType() {
        return 70.5f;
    }

    int clampPercentage(int value) {
        return Math.max(0, Math.min(100, value));
    }

    private String getString() {
        String string = this.getAlphaPercentage();
        String string2 = this.colorSetting.getAstolfoHex();
        return string2 + " " + string + "%";
    }

    String getAlphaPercentage() {
        int n = ColorUtils.unpackRgba(this.colorSetting.getColor())[3];
        return String.valueOf(Math.round((float)n / 255.0f * 100.0f));
    }

    ColorSetting getColorSetting() {
        return this.colorSetting;
    }

    @Override
    public float getFloatType2() {
        return 12.0f;
    }
}

