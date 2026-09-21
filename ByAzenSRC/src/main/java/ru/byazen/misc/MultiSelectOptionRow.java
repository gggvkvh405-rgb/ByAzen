/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.misc;

import org.joml.Matrix4f;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.CompactOptionRow;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseHitTest;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.ThemeColors;
import ru.byazen.ui.GuiBounds;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;

public class MultiSelectOptionRow
extends CompactOptionRow
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider,
MouseHitTest {
    public MultiSelectOptionRow(String string, String string2, float f) {
        super(string, string2, f);
    }

    public MultiSelectOptionRow(String string, String string2) {
        super(string, string2);
    }

    @Override
    protected float getFloatType() {
        return 5.5f;
    }

    @Override
    protected void process(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2) {
        float f = this.getFloatType2();
        float f2 = 7.5f;
        float f3 = bounds2.getX() + bounds2.getWidth() - 12.0f;
        float f4 = bounds2.getY() + (bounds2.getHeight() - f2) / 2.0f;
        int n = ColorUtils.lerp(ThemeColors.borderStrong(), ThemeColors.accent(), f);
        drawApi.drawRoundedRectangle(matrix4f, f3, f4, f2, f2, 4.0f, n);
        float f5 = this.getFloatType();
        int n2 = ColorUtils.withAlpha(ThemeColors.backgroundControl(), 255.0f * f);
        float f6 = f3 + (f2 - FontRegistry.font3.process3("T", f5)) / 2.0f;
        float f7 = f4 + (f2 - FontRegistry.font3.process4("T", f5)) / 2.0f;
        FontRegistry.font3.process2(matrix4f, drawApi, "T", f6, f7, f5, n2);
    }
}

