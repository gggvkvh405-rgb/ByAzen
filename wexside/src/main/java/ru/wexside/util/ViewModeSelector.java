/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.wexside.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joml.Matrix4f;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.ViewModeButton;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;

public final class ViewModeSelector
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private float value;
    private float value2;
    private final List<ViewModeButton> modeButtons = new ArrayList<ViewModeButton>();
    private int slot;
    private int slot2 = -1;

    public ViewModeSelector(GuiBounds bounds2, int n, ViewModeButton ... cls0735Array) {
        super(bounds2);
        this.slot = Math.max(1, n);
        this.value2 = 5.0f;
        this.value = 4.0f;
        this.modeButtons.addAll(Arrays.asList(cls0735Array));
        this.modeButtons.forEach(arg_0 -> this.addChild((GuiElement)arg_0));
        if (!this.modeButtons.isEmpty()) {
            this.setIntType2(0);
        }
        this.update4();
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        for (int i = 0; i < this.modeButtons.size(); ++i) {
            ViewModeButton viewModeButton = this.modeButtons.get(i);
            if (!viewModeButton.onMousePressed(n, n2, n3)) continue;
            this.setIntType2(i);
            return true;
        }
        return false;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        this.update4();
        float f2 = this.getBounds().getY();
        for (ViewModeButton viewModeButton : this.modeButtons) {
            f2 = viewModeButton.render(f, matrix4f);
        }
        return f2;
    }

    public float getFloatType() {
        return this.value2;
    }

    public void setViewModeButton(ViewModeButton viewModeButton) {
        this.modeButtons.add(viewModeButton);
        this.addChild(viewModeButton);
        if (this.slot2 == -1) {
            this.setIntType2(0);
        } else {
            this.update4();
        }
    }

    public void process4(float f, float f2) {
        this.value2 = f;
        this.value = f2;
        this.update4();
    }

    public int getIntType() {
        return this.slot;
    }

    public float getFloatType2() {
        return this.value;
    }

    public void setIntType(int n) {
        this.slot = Math.max(1, n);
        this.update4();
    }

    public void setIntType2(int n) {
        if (n < 0 || n >= this.modeButtons.size()) {
            return;
        }
        this.slot2 = n;
        for (int i = 0; i < this.modeButtons.size(); ++i) {
            this.modeButtons.get(i).setBooleanType(i == n);
        }
        this.update4();
    }

    public int getIntType2() {
        return this.slot2;
    }

    public List<ViewModeButton> getList() {
        return this.modeButtons;
    }

    private void update4() {
        float f;
        int n;
        int n2;
        if (this.modeButtons.isEmpty()) {
            this.getBounds().setSize(0.0f, 0.0f);
            return;
        }
        float f2 = this.getBounds().getX();
        float f3 = this.getBounds().getY();
        for (n2 = 0; n2 < this.modeButtons.size(); ++n2) {
            n = n2 % this.slot;
            int n3 = n2 / this.slot;
            f = f2 + (float)n * (50.0f + this.value2);
            float f4 = f3 + (float)n3 * (18.5f + this.value);
            this.modeButtons.get(n2).getBounds().setPosition(f, f4);
            this.modeButtons.get(n2).getBounds().setSize(50.0f, 18.5f);
        }
        n2 = Math.min(this.slot, this.modeButtons.size());
        n = (int)Math.ceil((float)this.modeButtons.size() / (float)this.slot);
        float f5 = (float)n2 * 50.0f + (float)Math.max(0, n2 - 1) * this.value2;
        f = (float)n * 18.5f + (float)Math.max(0, n - 1) * this.value;
        this.getBounds().setSize(f5, f);
    }
}

