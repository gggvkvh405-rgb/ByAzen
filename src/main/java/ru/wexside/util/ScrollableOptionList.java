/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.wexside.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.wexside.WexSideClient;
import ru.wexside.misc.AbstractOptionRow;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.GuiElement;
import ru.wexside.util.ClippedContentRenderer;
import ru.wexside.util.ScrollController;

public final class ScrollableOptionList
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final ScrollController scrollController;
    private float value;
    private final List<AbstractOptionRow> optionRows = new ArrayList<AbstractOptionRow>();
    private float value2;
    private final ClippedContentRenderer clippedContentRenderer;
    private float value3 = 1.5f;
    private static final float value4 = 5.5f;
    public static final float value5 = 3.0f;

    public ScrollableOptionList(GuiBounds bounds2, AbstractOptionRow ... cls0340Array) {
        super(bounds2);
        this.scrollController = new ScrollController(18.0f, 30.0f);
        this.clippedContentRenderer = new ClippedContentRenderer(3.0f, 14.0f, 14.0f, false);
        this.optionRows.addAll(Arrays.asList(cls0340Array));
        this.optionRows.forEach(arg_0 -> this.addChild((GuiElement)arg_0));
        this.update4();
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
        if (this.value <= this.value2 + 0.5f) {
            return;
        }
        this.scrollController.scrollByWheel(d, this.value2);
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        int n4 = (int)((float)n - this.getBounds().getX());
        int n5 = (int)((float)n2 - this.getBounds().getY());
        if (n5 < 0 || (float)n5 > this.value2) {
            return false;
        }
        for (AbstractOptionRow abstractOptionRow : this.optionRows) {
            if (!abstractOptionRow.onMousePressed(n4, n5, n3)) continue;
            abstractOptionRow.setBooleanType(!abstractOptionRow.isActive());
            return true;
        }
        return false;
    }

    @Override
    public float render(float f, Matrix4f matrix4f2) {
        this.update3();
        this.scrollController.update(this.value2, this.value);
        this.update2();
        GuiBounds bounds2 = this.getBounds();
        this.clippedContentRenderer.render(WexSideClient.getGuiRenderer(), matrix4f2, bounds2.getX() - 3.0f, bounds2.getY(), bounds2.getWidth() + 6.0f, this.value2 + 3.0f, this.scrollController.getOffset(), this.scrollController.getMinimumOffset(this.value2), clippedMatrix -> {
            Matrix4f contentMatrix = new Matrix4f((Matrix4fc)clippedMatrix).translate(bounds2.getX(), bounds2.getY(), 0.0f);
            for (AbstractOptionRow abstractOptionRow : this.optionRows) {
                abstractOptionRow.render(f, contentMatrix);
            }
        });
        return bounds2.getY() + this.value2;
    }

    public void setAbstractOptionRow(AbstractOptionRow ... cls0340Array) {
        this.optionRows.removeAll(this.optionRows);
        this.optionRows.clear();
        this.optionRows.addAll(Arrays.asList(cls0340Array));
        this.optionRows.forEach(arg_0 -> this.addChild((GuiElement)arg_0));
        this.scrollController.scrollToTop();
        this.update4();
    }

    public List<AbstractOptionRow> getList() {
        return this.optionRows;
    }

    public int getIntType() {
        int n = 0;
        for (AbstractOptionRow abstractOptionRow : this.optionRows) {
            if (!abstractOptionRow.isActive()) continue;
            ++n;
        }
        return n;
    }

    public float getViewportHeight() {
        return this.value2;
    }

    public void setAbstractOptionRow2(AbstractOptionRow abstractOptionRow) {
        this.optionRows.add(abstractOptionRow);
        this.addChild(abstractOptionRow);
        this.update4();
    }

    public ClippedContentRenderer getClippedContentRenderer() {
        return this.clippedContentRenderer;
    }

    public float getFloatType2() {
        return this.value3;
    }

    public ScrollController getScrollController() {
        return this.scrollController;
    }

    @Override
    public void update2() {
        float f = 14.0f + this.value3;
        float f2 = -this.scrollController.getOffset();
        float f3 = 0.0f;
        for (int i = 0; i < this.optionRows.size(); ++i) {
            AbstractOptionRow abstractOptionRow = this.optionRows.get(i);
            float f4 = abstractOptionRow.getBounds().getWidth();
            abstractOptionRow.getBounds().setPosition(0.0f, (float)i * f - f2);
            abstractOptionRow.getBounds().setSize(f4, 14.0f);
            f3 = Math.max(f3, f4);
        }
        this.getBounds().setSize(f3, this.value2);
    }

    public void setFloatType(float f) {
        this.value3 = f;
        this.update4();
    }

    private void update3() {
        this.value = this.optionRows.isEmpty() ? 0.0f : (float)this.optionRows.size() * 14.0f + (float)(this.optionRows.size() - 1) * this.value3;
        float f = 77.0f + 4.5f * this.value3;
        this.value2 = Math.min(this.value, f);
    }

    public float getContentHeight() {
        return this.value;
    }

    private void update4() {
        this.update3();
        this.update2();
    }
}

