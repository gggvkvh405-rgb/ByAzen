/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.misc;

import java.util.function.IntSupplier;
import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.FrameInterpolator;
import ru.byazen.misc.GuiInteractionState;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseHitTest;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;

public final class StyledActionButton
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
    private final String string2;
    private final IntSupplier intSupplier;
    private final Runnable runnable;
    private final float value2;
    private final IntSupplier intSupplier2;
    public static final float value3 = 14.0f;
    private final float value4;
    private final float value5;

    public StyledActionButton(String string, IntSupplier intSupplier, IntSupplier intSupplier2, Runnable runnable) {
        super(new GuiBounds(0.0f, 0.0f, 0.0f, 14.0f));
        this.value4 = 7.0f;
        this.value2 = 6.5f;
        this.value5 = 0.1f;
        this.string2 = string;
        this.intSupplier = intSupplier;
        this.intSupplier2 = intSupplier2;
        this.runnable = runnable;
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        if (n3 != 0 || !this.getBounds().contains(n, n2)) {
            return false;
        }
        this.runnable.run();
        return true;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        GuiInteractionState guiInteractionState = GuiInteractionState.getInstance();
        this.value = FrameInterpolator.lerpTowards(this.value, this.process13(guiInteractionState.getScaledMouseX(), guiInteractionState.getScaledMouseY()) ? 1.0f : 0.0f, 20.0f);
        int n = ColorUtils.lerp(this.intSupplier.getAsInt(), ColorUtils.lerp(this.intSupplier.getAsInt(), -1, 0.1f), this.value);
        drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 7.0f, n);
        float f2 = FontRegistry.font2.process3(this.string2, 6.5f);
        float f3 = FontRegistry.font2.process4(this.string2, 6.5f);
        FontRegistry.font2.process2(matrix4f, drawApi, this.string2, bounds2.getX() + (bounds2.getWidth() - f2) / 2.0f, bounds2.getY() + (bounds2.getHeight() - f3) / 2.0f, 6.5f, this.intSupplier2.getAsInt());
        return bounds2.getY() + bounds2.getHeight();
    }

    @Override
    public boolean process13(int n, int n2) {
        GuiBounds bounds2 = this.getBounds();
        float f = this.getAbsoluteX();
        float f2 = this.getAbsoluteY();
        return (float)n >= f && (float)n <= f + bounds2.getWidth() && (float)n2 >= f2 && (float)n2 <= f2 + bounds2.getHeight();
    }
}

