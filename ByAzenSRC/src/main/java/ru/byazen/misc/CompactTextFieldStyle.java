/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.TextFieldStyle;
import ru.byazen.misc.ThemeColors;

public final class CompactTextFieldStyle
implements TextFieldStyle {
    @Override
    public float longType() {
        return 5.0f;
    }

    @Override
    public int getIntType() {
        return ThemeColors.controlFill();
    }

    @Override
    public int getIntType2() {
        return ThemeColors.borderPrimary();
    }

    @Override
    public float getFloatType() {
        return 12.0f;
    }

    @Override
    public float getFloatType2() {
        return 12.0f;
    }
}

