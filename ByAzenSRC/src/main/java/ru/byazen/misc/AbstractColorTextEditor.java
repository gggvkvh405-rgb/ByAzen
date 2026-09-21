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
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;

public abstract class AbstractColorTextEditor
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    protected AbstractColorTextEditor(GuiBounds bounds2) {
        super(bounds2);
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
        for (GuiElement element2 : this.children) {
            element2.update();
        }
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        int n4;
        if (!this.getBounds().contains(n, n2)) {
            return false;
        }
        int n5 = (int)((float)n - this.getBounds().getX());
        return super.onMousePressed(n5, n4 = (int)((float)n2 - this.getBounds().getY()), n3) || this.getBounds().contains(n, n2);
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        Matrix4f matrix4f2 = new Matrix4f((Matrix4fc)matrix4f).translate(this.getBounds().getX(), this.getBounds().getY(), 0.0f);
        for (GuiElement element2 : this.children) {
            element2.render(f, matrix4f2);
        }
        return this.getBounds().getY() + this.getBounds().getHeight();
    }

    @Override
    public void onMouseReleased(int n, int n2, int n3) {
        int n4 = (int)((float)n - this.getBounds().getX());
        int n5 = (int)((float)n2 - this.getBounds().getY());
        super.onMouseReleased(n4, n5, n3);
    }

    public abstract float getFloatType();

    public abstract float getFloatType2();
}

