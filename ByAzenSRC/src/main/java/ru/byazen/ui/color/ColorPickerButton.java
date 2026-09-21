/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.ui.color;

import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.ThemeColors;
import ru.byazen.setting.ColorSetting;
import ru.byazen.ui.FloatingPanel;
import ru.byazen.ui.FloatingPanelManager;
import ru.byazen.ui.FloatingPanelProvider;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.ui.color.ColorPickerPopup;
import ru.byazen.util.GuiDrawApi;

public final class ColorPickerButton
extends GuiElement
implements FloatingPanelProvider {
    private final String icon;
    private final float iconSize;
    private final ColorPickerPopup popup;
    private FloatingPanelManager manager;

    public ColorPickerButton(String icon, float iconSize, ColorSetting setting) {
        super(new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f));
        this.icon = icon;
        this.iconSize = iconSize;
        this.popup = new ColorPickerPopup(setting);
    }

    @Override
    public boolean onMousePressed(int mouseX, int mouseY, int button) {
        if (!this.getBounds().contains(mouseX, mouseY)) {
            return false;
        }
        if (button == 0 && this.manager != null) {
            this.manager.toggle(this);
        }
        return true;
    }

    @Override
    public float render(float delta, Matrix4f matrix) {
        GuiBounds bounds = this.getBounds();
        GuiDrawApi renderer = ByAzenClient.getGuiRenderer();
        if (renderer != null) {
            float width = FontRegistry.font3.process3(this.icon, this.iconSize);
            float height = FontRegistry.font3.process4(this.icon, this.iconSize);
            float x = bounds.getX() + (bounds.getWidth() - width) / 2.0f;
            float y = bounds.getY() + (bounds.getHeight() - height) / 2.0f;
            FontRegistry.font3.process5(matrix, renderer, this.icon, x, y, this.iconSize, ThemeColors.textSecondary());
        }
        return bounds.getX() + bounds.getWidth();
    }

    @Override
    public FloatingPanel getFloatingPanel() {
        return this.popup;
    }

    @Override
    public void updateFloatingPanelPosition() {
        GuiElement parent = this.getParent();
        if (parent != null) {
            this.popup.getBounds().setPosition(parent.getBounds().getWidth() + 4.0f, 0.0f);
        }
    }

    @Override
    public void setFloatingPanelManager(FloatingPanelManager manager) {
        this.manager = manager;
    }
}

