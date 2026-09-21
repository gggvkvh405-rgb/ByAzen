/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.byazen.misc;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.byazen.ByAzenClient;
import ru.byazen.input.BindInput;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.KeybindCaptureField;
import ru.byazen.misc.KeybindDescriptor;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.PotionEditorState;
import ru.byazen.misc.ThemeColors;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.GuiDrawApi;

public final class PotionBindEditor
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    public static final float value = 18.0f;
    static final String string = "\u0422\u0440\u0438\u0433\u0433\u0435\u0440 \u043a\u043d\u043e\u043f\u043a\u0430";
    static final String string2 = "\u0417\u0435\u043b\u044c\u044f \u0432\u044b\u0431\u0440\u0430\u0441\u044b\u0432\u0430\u044e\u0442\u0441\u044f \u043f\u0440\u0438 \u043d\u0430\u0436\u0430\u0442\u0438\u0438 \u0431\u0438\u043d\u0434\u0430";
    private final KeybindCaptureField keybindCaptureField;
    private final float value2;
    private final float value3;
    private final float value4;
    private final float value5;

    public PotionBindEditor(PotionEditorState potionEditorState, float f) {
        super(new GuiBounds(0.0f, 0.0f, f, 18.0f));
        this.keybindCaptureField = new KeybindCaptureField(KeybindDescriptor.process("\u0422\u0440\u0438\u0433\u0433\u0435\u0440 \u043a\u043d\u043e\u043f\u043a\u0430", "\u0417\u0435\u043b\u044c\u044f \u0432\u044b\u0431\u0440\u0430\u0441\u044b\u0432\u0430\u044e\u0442\u0441\u044f \u043f\u0440\u0438 \u043d\u0430\u0436\u0430\u0442\u0438\u0438 \u0431\u0438\u043d\u0434\u0430", () -> potionEditorState.getWorkingCopy().getBindInput(), bind -> potionEditorState.getWorkingCopy().setBindInput(bind)));
        this.value4 = 7.0f;
        this.value2 = 6.5f;
        this.value5 = 10.0f;
        this.value3 = 3.0f;
        this.addChild(this.keybindCaptureField);
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        if (!this.getBounds().contains(n, n2)) {
            return false;
        }
        this.update2();
        return super.onMousePressed((int)((float)n - this.getBounds().getX()), (int)((float)n2 - this.getBounds().getY()), n3);
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        Matrix4f matrix4f2 = new Matrix4f((Matrix4fc)matrix4f).translate(bounds2.getX(), bounds2.getY(), 0.0f);
        this.update2();
        FontRegistry.font2.process2(matrix4f2, drawApi, string, 0.0f, 0.0f, 7.0f, ThemeColors.textPrimary());
        FontRegistry.font2.process2(matrix4f2, drawApi, string2, 0.0f, 10.0f, 6.5f, ThemeColors.textMuted());
        this.keybindCaptureField.render(f, matrix4f2);
        return bounds2.getY() + 18.0f;
    }

    @Override
    public void onMouseReleased(int n, int n2, int n3) {
        this.keybindCaptureField.onMouseReleased((int)((float)n - this.getBounds().getX()), (int)((float)n2 - this.getBounds().getY()), n3);
    }

    @Override
    public boolean onKeyPressed(int n) {
        return this.keybindCaptureField.onKeyPressed(n);
    }

    @Override
    public void update2() {
        float f = this.keybindCaptureField.getFloatType();
        this.keybindCaptureField.getBounds().setSize(f, this.keybindCaptureField.getFloatType2());
        this.keybindCaptureField.getBounds().setPosition(this.getBounds().getWidth() - f, 3.0f);
    }
}

