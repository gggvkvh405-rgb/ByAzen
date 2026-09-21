/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.misc;

import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.FrameInterpolator;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.TextInputController;
import ru.byazen.misc.TextInputModel;
import ru.byazen.misc.ThemeColors;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;

public final class TextField
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final String string8;
    private final float value;
    private final TextInputController textInputController;
    private final String string9;
    private float value2;
    private final float value3;
    private final float value4;

    public TextField(GuiBounds bounds2, TextInputModel callback15) {
        this(bounds2, callback15, "");
    }

    public TextField(GuiBounds bounds2, TextInputModel callback15, String string) {
        super(bounds2);
        this.value3 = 8.0f;
        this.value4 = 0.75f;
        this.value = 5.5f;
        this.string8 = "|";
        this.string9 = string == null ? "" : string;
        this.textInputController = new TextInputController(callback15);
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
        this.textInputController.tick();
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        return this.textInputController.onMousePressed(this.getBounds(), n, n2, n3);
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        String string = this.textInputController.getText();
        String string2 = this.textInputController.isCaretVisible() ? "|" : "";
        String string3 = this.string9;
        String string4 = string2;
        String string5 = string;
        String string6 = string5 + string4 + string3;
        Object string7 = string6.isEmpty() ? "0" : string6;
        float f2 = FontRegistry.font4.process3(string6, 5.5f);
        float f3 = FontRegistry.font4.process4((String)string7, 5.5f);
        float f4 = bounds2.getX() + (bounds2.getWidth() - f2) / 2.0f;
        float f5 = bounds2.getY() + (bounds2.getHeight() - f3) / 2.0f;
        drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 8.0f, ThemeColors.formatFieldFill());
        drawApi.drawRoundedRectangleOutlined(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 8.0f, 0.75f, ColorUtils.withAlpha(ThemeColors.backgroundPrimary(), 0.0f), ThemeColors.borderPrimary());
        this.value2 = FrameInterpolator.lerpTowards(this.value2, this.textInputController.isAllSelected() ? 1.0f : 0.0f, 30.0f);
        int n = ThemeColors.textSecondary();
        int n2 = ColorUtils.lerp(n, ThemeColors.adjustForTheme(n), this.value2);
        if (!string6.isEmpty()) {
            FontRegistry.font4.process2(matrix4f, drawApi, string6, f4, f5, 5.5f, n2);
        }
        return bounds2.getY() + bounds2.getHeight();
    }

    @Override
    public void onMouseReleased(int n, int n2, int n3) {
        this.textInputController.blurIfOutside(this.getBounds(), n, n2);
    }

    @Override
    public boolean onCharTyped(char c) {
        return this.textInputController.onCharTyped(c);
    }

    @Override
    public void update2() {
        this.textInputController.blur();
    }

    @Override
    public boolean onKeyPressed(int n) {
        return this.textInputController.onKeyPressed(n);
    }
}

