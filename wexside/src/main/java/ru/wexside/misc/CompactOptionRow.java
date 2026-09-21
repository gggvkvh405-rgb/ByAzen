/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.misc;

import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.IconOptionRow;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseHitTest;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.ThemeColors;
import ru.wexside.util.ColorUtils;

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

