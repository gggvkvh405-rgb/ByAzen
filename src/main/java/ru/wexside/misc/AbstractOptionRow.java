/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.WexSideClient;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.FrameInterpolator;
import ru.wexside.misc.GuiInteractionState;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseHitTest;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.TextLayoutUtils;
import ru.wexside.misc.ThemeColors;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public abstract class AbstractOptionRow
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider,
MouseHitTest {
    private float value;
    private final String string3;
    private boolean enabled;
    private float value2;
    private final String string4;
    public static final float value3 = 14.0f;
    public static final float value4 = 109.0f;

    protected AbstractOptionRow(String string, String string2) {
        this(string, string2, 109.0f);
    }

    protected AbstractOptionRow(String string, String string2, float f) {
        super(new GuiBounds(0.0f, 0.0f, f, 14.0f));
        this.string4 = string;
        this.string3 = string2;
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void setBooleanType(boolean bl) {
        this.enabled = bl;
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        return n3 == 0 && this.getBounds().contains(n, n2);
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = WexSideClient.getGuiRenderer();
        GuiInteractionState guiInteractionState = GuiInteractionState.getInstance();
        boolean bl = this.process13(guiInteractionState.getScaledMouseX(), guiInteractionState.getScaledMouseY());
        this.value = FrameInterpolator.lerpTowards(this.value, bl ? 1.0f : 0.0f, 15.0f);
        this.value2 = FrameInterpolator.lerpTowards(this.value2, this.enabled ? 1.0f : 0.0f, 30.0f);
        this.process4(matrix4f, drawApi, bounds2);
        this.process5(matrix4f, drawApi, bounds2);
        this.process(matrix4f, drawApi, bounds2);
        return bounds2.getY() + bounds2.getHeight();
    }

    public String getString() {
        return this.string3;
    }

    public String getString2() {
        return this.string4;
    }

    public boolean isActive() {
        return this.enabled;
    }

    protected int getIntType() {
        return ColorUtils.lerp(ColorUtils.withAlpha(ThemeColors.borderSubtle(), 0.0f), ThemeColors.borderSubtle(), this.value);
    }

    protected float getFloatType() {
        return 7.0f;
    }

    protected float getFloatType2() {
        return this.value2;
    }

    protected float getFloatType3() {
        return 7.0f;
    }

    protected boolean hasDescription() {
        return this.string3 != null && !this.string3.isBlank();
    }

    protected int getIntType2() {
        return this.getIntType3();
    }

    protected float getFloatType4() {
        return 7.0f;
    }

    public float getFloatType5() {
        return this.value;
    }

    protected void process4(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2) {
        drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), this.getFloatType4(), this.getIntType());
    }

    protected abstract void process(Matrix4f var1, GuiDrawApi var2, GuiBounds var3);

    protected int getIntType3() {
        return ColorUtils.lerp(ThemeColors.textSecondary(), ThemeColors.textPrimary(), this.value);
    }

    public float getFloatType6() {
        return this.value2;
    }

    protected int getIntType4() {
        return this.getIntType3();
    }

    @Override
    public boolean process13(int n, int n2) {
        GuiBounds bounds2 = this.getBounds();
        float f = this.getAbsoluteX();
        float f2 = this.getAbsoluteY();
        return (float)n >= f && (float)n <= f + bounds2.getWidth() && (float)n2 >= f2 && (float)n2 <= f2 + bounds2.getHeight();
    }

    protected float getFloatType7() {
        return 14.0f;
    }

    protected void process5(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2) {
        float f = bounds2.getX() + 3.5f;
        if (this.hasDescription()) {
            FontRegistry.font3.process5(matrix4f, drawApi, this.string3, f, bounds2.getY() + 3.25f, this.getFloatType3(), this.getIntType2());
            f += 10.0f;
        }
        float f2 = bounds2.getX() + bounds2.getWidth() - this.getFloatType7() - f;
        String string = TextLayoutUtils.trimToWidth(this.string4, FontRegistry.font2, this.getFloatType8(), f2);
        FontRegistry.font2.process2(matrix4f, drawApi, string, f, bounds2.getY() + 3.0f, this.getFloatType8(), this.getIntType4());
    }

    protected float getFloatType8() {
        return 6.0f;
    }
}

