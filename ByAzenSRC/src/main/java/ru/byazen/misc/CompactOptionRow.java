/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.IconOptionRow;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseHitTest;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.ThemeColors;
import ru.byazen.util.ColorUtils;

public class CompactOptionRow
extends IconOptionRow
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider,
MouseHitTest {
    public CompactOptionRow(String string, String string2) {
        super(string, string2);
    }

    public CompactOptionRow(String string, String string2, float f) {
        super(string, string2, f);
    }

    @Override
    protected float getFloatType() {
        return 6.5f;
    }

    @Override
    protected float getFloatType4() {
        return 6.0f;
    }

    @Override
    protected int getIntType4() {
        return ColorUtils.lerp(this.getIntType3(), ThemeColors.accent(), this.getFloatType2());
    }

    @Override
    protected float getFloatType8() {
        return 6.25f;
    }
}

