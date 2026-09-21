/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1792
 *  net.minecraft.class_1802
 */
package ru.byazen.misc;

import net.minecraft.class_1792;
import net.minecraft.class_1802;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.ColorSetting;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.util.ColorUtils;

public final class ConsumablesEspSettings {
    private final BooleanSetting booleanSetting;
    public static final String string2 = "\u041f\u043b\u0430\u0441\u0442";
    private final BooleanSetting booleanSetting2;
    public static final String string3 = "\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c";
    public static final String string4 = "\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f";
    private final BooleanSetting booleanSetting3;
    public static final String TRAP_ITEM = "\u0422\u0440\u0430\u043f\u043a\u0430";
    private final MultiSelectSetting multiSelectSetting;
    public static final String[] string5 = new String[]{"\u0422\u0440\u0430\u043f\u043a\u0430", "\u041f\u043b\u0430\u0441\u0442", "\u0414\u0435\u0437\u043e\u0440\u0438\u0435\u043d\u0442\u0430\u0446\u0438\u044f", "\u042f\u0432\u043d\u0430\u044f \u043f\u044b\u043b\u044c", "\u0421\u043d\u0435\u0436\u043e\u043a \u0437\u0430\u043c\u043e\u0440\u043e\u0437\u043a\u0430"};
    private final ColorSetting colorSetting;
    public static final String string6 = "\u0421\u043d\u0435\u0436\u043e\u043a \u0437\u0430\u043c\u043e\u0440\u043e\u0437\u043a\u0430";
    private final MultiSelectSetting multiSelectSetting2;
    private final ColorSetting colorSetting2;

    public ConsumablesEspSettings(BooleanSetting booleanSetting, BooleanSetting toggle, MultiSelectSetting multiSelectSetting, ColorSetting colorSetting, BooleanSetting toggle2, MultiSelectSetting multi, ColorSetting colorSetting3) {
        this.booleanSetting3 = booleanSetting;
        this.booleanSetting2 = toggle;
        this.multiSelectSetting2 = multiSelectSetting;
        this.colorSetting = colorSetting;
        this.booleanSetting = toggle2;
        this.multiSelectSetting = multi;
        this.colorSetting2 = colorSetting3;
    }

    public boolean isActive() {
        return this.booleanSetting3.isEnabled();
    }

    private static String process(class_1792 iiIilIIilI2) {
        if (iiIilIIilI2 == class_1802.field_22021) {
            return TRAP_ITEM;
        }
        if (iiIilIIilI2 == class_1802.field_8614) {
            return string2;
        }
        if (iiIilIIilI2 == class_1802.field_8450) {
            return string4;
        }
        if (iiIilIIilI2 == class_1802.field_8479) {
            return string3;
        }
        if (iiIilIIilI2 == class_1802.field_8543) {
            return string6;
        }
        return null;
    }

    public ColorSetting process2(class_1792 iiIilIIilI2) {
        return this.colorSetting;
    }

    public boolean process3(class_1792 iiIilIIilI2) {
        return ConsumablesEspSettings.process4(this.multiSelectSetting2, iiIilIIilI2);
    }

    private static boolean process4(MultiSelectSetting multiSelectSetting, class_1792 iiIilIIilI2) {
        String string = ConsumablesEspSettings.process(iiIilIIilI2);
        return string != null && multiSelectSetting.getSelectedOptions().contains(string);
    }

    public boolean isActive2() {
        return this.booleanSetting2.isEnabled();
    }

    public static int process5(ColorSetting colorSetting, float f) {
        if (colorSetting.isAstolfoMode()) {
            return colorSetting.getEditingColor(f);
        }
        int n = colorSetting.getPrimaryColor();
        int n2 = colorSetting.isDoubleColorMode() ? colorSetting.getSecondaryColor() : n;
        return ColorUtils.lerp(n, n2, f);
    }

    public boolean process6(class_1792 iiIilIIilI2) {
        return ConsumablesEspSettings.process4(this.multiSelectSetting, iiIilIIilI2);
    }

    public boolean isActive3() {
        return this.booleanSetting.isEnabled();
    }

    public ColorSetting process7(class_1792 iiIilIIilI2) {
        return this.colorSetting2;
    }
}

