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
import ru.byazen.misc.CompactTextFieldStyle;
import ru.byazen.misc.ContainerDisplay;
import ru.byazen.misc.ExpandedTextFieldStyle;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.setting.TextSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.setting.SettingRow;
import ru.byazen.util.CompactSettingRow;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.TextSettingComponent;

public final class TextSettingRow
extends SettingRow<TextSetting>
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final CompactSettingRow compactSettingRow;

    public TextSettingRow(GuiBounds bounds2, TextSetting textSetting, ContainerDisplay containerDisplay) {
        super(bounds2, textSetting, new TextSettingComponent(textSetting, textSetting.isExpanded() ? new ExpandedTextFieldStyle() : new CompactTextFieldStyle()));
        this.compactSettingRow = new CompactSettingRow(new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f), textSetting::getDisplayName, textSetting.getDescription(), containerDisplay);
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
        return this.getSettingComponent() != null && this.getSettingComponent().onMousePressed(n, n2, n3);
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        this.update3();
        float f2 = this.compactSettingRow.getFloatType2();
        float f3 = this.getFloatType2();
        bounds2.setSize(bounds2.getWidth(), f3);
        this.refreshLayout();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        this.compactSettingRow.process3(matrix4f, drawApi);
        if (this.getSettingComponent() != null) {
            this.getSettingComponent().render(f, matrix4f);
        }
        this.renderRowDecorations(f, matrix4f);
        return bounds2.getY() + bounds2.getHeight();
    }

    @Override
    public void onMouseReleased(int n, int n2, int n3) {
        this.refreshLayout();
        if (this.getSettingComponent() != null) {
            this.getSettingComponent().onMouseReleased(n, n2, n3);
        }
    }

    @Override
    public void update2() {
        this.setFloatType(this.compactSettingRow.getFloatType2());
    }

    private void setFloatType(float f) {
        float f2;
        float f3;
        if (this.getSettingComponent() == null) {
            return;
        }
        GuiBounds bounds2 = this.getBounds();
        float f4 = this.getSettingComponent().getFloatType();
        float f5 = this.getSettingComponent().getFloatType2();
        if (((TextSetting)this.getSetting()).isExpanded()) {
            f3 = bounds2.getX() + bounds2.getWidth() - f4 - 4.0f;
            f2 = bounds2.getY() + (f - f5) / 2.0f + 0.25f;
        } else {
            f3 = bounds2.getX() + (bounds2.getWidth() - f4) / 2.0f;
            f2 = bounds2.getY() + f;
        }
        this.getSettingComponent().getBounds().setPosition(f3, f2);
        this.getSettingComponent().getBounds().setSize(f4, f5);
        this.updateComponentVisibility();
    }

    private void update3() {
        GuiBounds bounds2 = this.getBounds();
        if (this.getSettingComponent() != null && ((TextSetting)this.getSetting()).isExpanded()) {
            float f = this.getSettingComponent().getFloatType();
            float f2 = bounds2.getX() + bounds2.getWidth() - f - 4.0f;
            float f3 = f2 - 4.0f - (bounds2.getX() + this.compactSettingRow.getFloatType5());
            this.compactSettingRow.setFloatType(Math.max(0.0f, f3));
            return;
        }
        this.compactSettingRow.setFloatType(Math.max(0.0f, bounds2.getWidth() - 2.0f * this.compactSettingRow.getContentOffset()));
    }

    @Override
    public float getFloatType2() {
        this.update3();
        float f = this.compactSettingRow.getFloatType2();
        if (this.getSettingComponent() == null || ((TextSetting)this.getSetting()).isExpanded()) {
            return f;
        }
        return f + this.getSettingComponent().getFloatType2();
    }

    @Override
    public void refreshLayout() {
        this.update3();
        GuiBounds bounds2 = this.getBounds();
        float f = this.compactSettingRow.getFloatType2();
        this.compactSettingRow.getBounds().setPosition(bounds2.getX(), bounds2.getY());
        this.compactSettingRow.getBounds().setSize(bounds2.getWidth(), f);
        this.setFloatType(f);
    }
}

