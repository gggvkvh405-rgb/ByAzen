/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.wexside.misc;

import org.joml.Matrix4f;
import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.ContainerColumnLayout;
import ru.wexside.misc.ContainerDisplay;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.IconOptionRow;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.ThemeColors;
import ru.wexside.misc.ViewModeButton;
import ru.wexside.ui.GuiBounds;
import ru.wexside.ui.PopupPanel;
import ru.wexside.util.GuiDrawApi;
import ru.wexside.util.ScrollableOptionList;
import ru.wexside.util.ViewModeSelector;

public final class Table
extends PopupPanel
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final IconOptionRow moduleDescriptionOption;
    private final ViewModeSelector viewModeSelector;
    private final ScrollableOptionList scrollableOptionList;
    private final ViewModeButton viewModeButton2;
    private final ViewModeButton viewModeButton3;
    private final ContainerDisplay containerDisplay;
    private final IconOptionRow settingDescriptionOption;

    public Table(GuiBounds bounds2, ContainerDisplay containerDisplay) {
        super(bounds2);
        this.containerDisplay = containerDisplay;
        this.viewModeButton2 = new ViewModeButton("List", "\u0421");
        this.viewModeButton3 = new ViewModeButton("Table", "\u0446");
        this.viewModeSelector = new ViewModeSelector(new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f), 2, this.viewModeButton2, this.viewModeButton3);
        this.moduleDescriptionOption = new IconOptionRow("\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435 \u043c\u043e\u0434\u0443\u043b\u044f", "\u043c", 109.0f);
        this.settingDescriptionOption = new IconOptionRow("\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435 \u043d\u0430\u0441\u0442\u0440\u043e\u0435\u043a", "g", 109.0f);
        this.scrollableOptionList = new ScrollableOptionList(new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f), this.moduleDescriptionOption, this.settingDescriptionOption);
        this.update3();
        this.addChild(this.viewModeSelector);
        this.addChild(this.scrollableOptionList);
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
    }

    @Override
    public void update() {
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        boolean bl = super.onMousePressed(n, n2, n3);
        if (bl) {
            this.update4();
        }
        return bl;
    }

    @Override
    public void update2() {
        float f = 21.0f;
        this.viewModeSelector.getBounds().setPosition(5.25f, f);
        this.scrollableOptionList.getBounds().setPosition(3.0f, f += this.viewModeSelector.getBounds().getHeight() + 3.5f);
        this.getBounds().setSize(this.getBounds().getWidth(), f += this.scrollableOptionList.getBounds().getHeight() + 3.0f);
    }

    public ScrollableOptionList getScrollableOptionList() {
        return this.scrollableOptionList;
    }

    private void update3() {
        this.viewModeSelector.setIntType2(this.containerDisplay.getContainerColumnLayout() == ContainerColumnLayout.TWO_COLUMNS ? 1 : 0);
        this.moduleDescriptionOption.setBooleanType(this.containerDisplay.isActive());
        this.settingDescriptionOption.setBooleanType(this.containerDisplay.isActive2());
    }

    public ViewModeButton getViewModeButton() {
        return this.viewModeButton3;
    }

    public int getIntType() {
        return this.containerDisplay.getIntType();
    }

    public ViewModeButton getViewModeButton2() {
        return this.viewModeButton2;
    }

    public ViewModeSelector getViewModeSelector() {
        return this.viewModeSelector;
    }

    public ContainerDisplay getContainerDisplay() {
        return this.containerDisplay;
    }

    public IconOptionRow getPrimaryOptionRow() {
        return this.settingDescriptionOption;
    }

    private void update4() {
        this.containerDisplay.setContainerColumnLayout(this.viewModeSelector.getIntType2() == 1 ? ContainerColumnLayout.TWO_COLUMNS : ContainerColumnLayout.SINGLE_COLUMN);
        this.containerDisplay.setBooleanType2(this.moduleDescriptionOption.isActive());
        this.containerDisplay.setBooleanType(this.settingDescriptionOption.isActive());
    }

    public IconOptionRow getSecondaryOptionRow() {
        return this.moduleDescriptionOption;
    }

    @Override
    protected void updateLayout() {
        this.update3();
        this.update2();
    }

    @Override
    protected void renderPopup(float f, Matrix4f matrix4f, GuiDrawApi drawApi) {
        GuiBounds bounds2 = this.getBounds();
        drawApi.fillRectangle(matrix4f, 0.0f, 15.0f, bounds2.getWidth(), 0.5f, ThemeColors.borderPrimary());
        FontRegistry.font2.process2(matrix4f, drawApi, "\u0412\u0438\u0434...", 4.0f, 4.0f, 5.75f, ThemeColors.textPlaceholder());
        this.renderChildren(f, matrix4f);
    }

    protected boolean process3(float f) {
        return this.isActive2() && f >= 0.99f;
    }
}

