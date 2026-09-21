/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.misc;

import org.joml.Matrix4f;
import ru.byazen.misc.AbstractOptionRow;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
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

public class IconOptionRow
extends AbstractOptionRow
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider,
MouseHitTest {
    public IconOptionRow(String string, String string2) {
        super(string, string2);
    }

    public IconOptionRow(String string, String string2, float f) {
        super(string, string2, f);
    }

    @Override
    protected void process(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2) {
        float f = this.getFloatType2();
        if (f <= 0.01f) {
            return;
        }
        float f2 = this.getFloatType();
        int n = ColorUtils.withAlpha(ThemeColors.accent(), 255.0f * f);
        float f3 = bounds2.getX() + bounds2.getWidth() - 10.5f + (1.0f - f) * 2.0f;
        float f4 = bounds2.getY() + (bounds2.getHeight() - FontRegistry.font3.process4("T", f2)) / 2.0f;
        FontRegistry.font3.process2(matrix4f, drawApi, "T", f3, f4, f2, n);
    }
}

