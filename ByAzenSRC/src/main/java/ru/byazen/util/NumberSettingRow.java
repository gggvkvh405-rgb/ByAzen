/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.util;

import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.ContainerDisplay;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.setting.NumberSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.setting.SettingRow;
import ru.byazen.util.CompactSettingRow;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.NumberSettingComponent;

public class NumberSettingRow
extends SettingRow<NumberSetting>
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private CompactSettingRow compactSettingRow;

    public NumberSettingRow(GuiBounds bounds2, NumberSetting numberSetting, ContainerDisplay containerDisplay) {
        super(bounds2, numberSetting, new NumberSettingComponent(numberSetting));
        this.compactSettingRow = new CompactSettingRow(new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f), numberSetting::getDisplayName, numberSetting.getDescription(), containerDisplay);
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
        if (this.getSettingComponent() != null) {
            this.getSettingComponent().onMouseScroll(n, n2, d);
        }
    }

    @Override
    public void update() {
        if (this.getSettingComponent() != null) {
            this.getSettingComponent().update();
        }
        this.syncVisibility();
    }

    @Override
    public boolean onMousePressed(int n, int n2, int n3) {
        this.refreshLayout();
        if (this.handleRowClick(n, n2, n3)) {
            return true;
        }
        if (this.getSettingComponent() == null) {
            return false;
        }
        return this.getSettingComponent().onMousePressed(n, n2, n3);
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        this.update3();
        float f2 = this.calculateHeight();
        float f3 = this.getFloatType2();
        bounds2.setSize(bounds2.getWidth(), f3);
        this.refreshLayout();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        this.compactSettingRow.process3(matrix4f, drawApi);
        if (this.getSettingComponent() == null) {
            return bounds2.getY() + bounds2.getHeight();
        }
        this.getSettingComponent().render(f, matrix4f);
        this.renderRowDecorations(f, matrix4f);
        return bounds2.getY() + bounds2.getHeight();
    }

    @Override
    public void update2() {
        this.setFloatType(this.calculateHeight());
    }

    private float calculateHeight() {
        return this.compactSettingRow.getFloatType2();
    }

    private void setFloatType(float f) {
        if (this.getSettingComponent() == null) {
            return;
        }
        GuiBounds bounds2 = this.getBounds();
        float f2 = bounds2.getY() + f;
        this.getSettingComponent().getBounds().setPosition(bounds2.getX(), f2);
        this.getSettingComponent().getBounds().setSize(bounds2.getWidth(), this.getSettingComponent().getFloatType2());
        this.updateComponentVisibility();
    }

    private void update3() {
        GuiBounds bounds2 = this.getBounds();
        this.compactSettingRow.setFloatType(Math.max(0.0f, bounds2.getWidth() - 2.0f * this.compactSettingRow.getContentOffset()));
    }

    @Override
    public float getFloatType2() {
        this.update3();
        float f = this.calculateHeight();
        return this.getSettingComponent() == null ? f : f + this.getSettingComponent().getFloatType2();
    }

    @Override
    public void refreshLayout() {
        this.update3();
        GuiBounds bounds2 = this.getBounds();
        float f = this.calculateHeight();
        this.compactSettingRow.getBounds().setPosition(bounds2.getX(), bounds2.getY());
        this.compactSettingRow.getBounds().setSize(bounds2.getWidth(), f);
        this.setFloatType(f);
    }
}

