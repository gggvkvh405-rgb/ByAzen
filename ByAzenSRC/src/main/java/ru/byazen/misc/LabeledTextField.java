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
import ru.byazen.misc.MutableTextBuffer;
import ru.byazen.misc.TextInputController;
import ru.byazen.misc.ThemeColors;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;

public final class LabeledTextField
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final float value;
    private final String string3;
    private final GuiBounds inputBounds = new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f);
    private final String string4;
    private final String string5;
    private final MutableTextBuffer mutableTextBuffer;
    private final TextInputController textInputController;
    private final String string6;
    private final float value2;
    private float value3;
    private final float value4;
    private boolean enabled = true;
    private final float value5;
    private final float value6;
    private final float value7;
    private final float value8;
    private final float value9;
    private final float value10;
    private final float value11;
    private final float value12;

    public LabeledTextField(String string, String string2, int n, float f) {
        super(new GuiBounds(0.0f, 0.0f, f, 0.0f));
        this.value5 = 6.5f;
        this.value7 = 6.0f;
        this.value11 = 1.5f;
        this.value2 = 4.0f;
        this.value8 = 4.0f;
        this.value12 = 14.0f;
        this.value4 = 7.0f;
        this.value9 = 5.5f;
        this.value10 = 5.0f;
        this.value = 0.75f;
        this.string6 = "\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0442\u0435\u043a\u0441\u0442...";
        this.string3 = "|";
        this.string4 = string;
        this.string5 = string2;
        this.mutableTextBuffer = new MutableTextBuffer(n);
        this.textInputController = new TextInputController(this.mutableTextBuffer);
        float f2 = string2 == null || string2.isBlank() ? 4.0f : 11.5f;
        this.value6 = 6.5f + f2 + 14.0f;
        this.getBounds().setSize(f, this.value6);
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
        if (!this.enabled) {
            return false;
        }
        return this.textInputController.onMousePressed(this.getInputBounds(), n, n2, n3);
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        int n = this.enabled ? ThemeColors.textPrimary() : ThemeColors.textMuted();
        FontRegistry.font2.process2(matrix4f, drawApi, this.string4, bounds2.getX(), bounds2.getY(), 6.5f, n);
        if (this.string5 != null && !this.string5.isBlank()) {
            int n2 = this.enabled ? ThemeColors.textMuted() : ThemeColors.textDisabled();
            FontRegistry.font2.process2(matrix4f, drawApi, this.string5, bounds2.getX(), bounds2.getY() + 6.5f + 1.5f, 6.0f, n2);
        }
        GuiBounds bounds3 = this.getInputBounds();
        int n3 = ColorUtils.withAlpha(ThemeColors.formatFieldFill(), this.enabled ? 255.0f : 120.0f);
        int n4 = ColorUtils.withAlpha(ThemeColors.borderPrimary(), this.enabled ? 255.0f : 90.0f);
        drawApi.drawRoundedRectangleOutlined(matrix4f, bounds3.getX(), bounds3.getY(), bounds3.getWidth(), bounds3.getHeight(), 7.0f, 0.75f, n3, n4);
        String string = this.textInputController.getText();
        boolean bl = string.isBlank();
        boolean bl2 = bl && !this.textInputController.isFocused();
        String string2 = bl2 ? "\u0412\u0432\u0435\u0434\u0438\u0442\u0435 \u0442\u0435\u043a\u0441\u0442..." : string;
        this.value3 = FrameInterpolator.lerpTowards(this.value3, this.textInputController.isAllSelected() ? 1.0f : 0.0f, 30.0f);
        int n5 = bl2 ? ThemeColors.textPlaceholder() : ThemeColors.textPrimary();
        int n6 = bl2 ? n5 : ColorUtils.lerp(n5, ThemeColors.adjustForTheme(n5), this.value3);
        int n7 = ColorUtils.withAlpha(n6, this.enabled ? 255.0f : 120.0f);
        float f2 = bounds3.getX() + 5.0f;
        float f3 = bounds3.getY() + (bounds3.getHeight() - FontRegistry.font2.process4(string2, 5.5f)) / 2.0f;
        drawApi.beginStencil(1);
        drawApi.drawRoundedRectangle(matrix4f, bounds3.getX() + 1.0f, bounds3.getY() + 1.0f, bounds3.getWidth() - 2.0f, bounds3.getHeight() - 2.0f, 7.0f, ColorUtils.rgba(0, 0, 0, 0));
        drawApi.applyStencilMask(1);
        FontRegistry.font2.process2(matrix4f, drawApi, string2, f2, f3, 5.5f, n7);
        if (this.textInputController.isCaretVisible()) {
            float f4 = f2 + FontRegistry.font2.process3(string, 5.5f);
            FontRegistry.font2.process2(matrix4f, drawApi, "|", f4, f3, 5.5f, ThemeColors.textPrimary());
        }
        drawApi.endStencil();
        return bounds2.getY() + bounds2.getHeight();
    }

    @Override
    public boolean onCharTyped(char c) {
        if (!this.enabled) {
            return false;
        }
        return this.textInputController.onCharTyped(c);
    }

    @Override
    public void update2() {
        this.textInputController.blur();
        super.update2();
    }

    @Override
    public boolean onKeyPressed(int n) {
        if (!this.enabled) {
            return false;
        }
        return this.textInputController.onKeyPressed(n);
    }

    public void update3() {
        this.textInputController.blur();
    }

    public void update4() {
        this.mutableTextBuffer.setText("");
        this.textInputController.blur();
    }

    public boolean isActive() {
        return this.textInputController.isFocused();
    }

    private GuiBounds getInputBounds() {
        GuiBounds componentBounds = super.getBounds();
        this.inputBounds.setPosition(componentBounds.getX(), componentBounds.getY() + componentBounds.getHeight() - 14.0f);
        this.inputBounds.setSize(componentBounds.getWidth(), 14.0f);
        return this.inputBounds;
    }

    public String getString() {
        return this.textInputController.getText();
    }

    public void setString(String string) {
        this.mutableTextBuffer.setText(string);
        this.textInputController.blur();
    }

    public float getFloatType() {
        return this.value6;
    }

    @Override
    public void setBooleanType(boolean bl) {
        this.enabled = bl;
        if (!bl) {
            this.textInputController.blur();
        }
    }

    @Override
    public boolean isActive2() {
        return this.enabled;
    }
}

