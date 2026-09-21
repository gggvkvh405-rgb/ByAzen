/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.misc;

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
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.PopupManager;
import ru.byazen.misc.PopupOwner;
import ru.byazen.misc.ThemeColors;
import ru.byazen.setting.Setting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.ui.PopupPanel;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.SettingKeybindPopup;

public final class SettingKeybindButton
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider,
PopupOwner {
    private final Setting setting;
    private final SettingKeybindPopup settingKeybindPopup;
    private final GuiBounds bounds3;
    private PopupManager popupManager;
    private final float value;
    private final GuiBounds bounds4 = new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f);
    private final String string;
    private final float value2;
    private float value3;

    public SettingKeybindButton(Setting setting) {
        super(new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f));
        this.string = "\u041b";
        this.value2 = 6.75f;
        this.value = 5.0f;
        this.bounds3 = new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f);
        this.setting = setting;
        this.settingKeybindPopup = new SettingKeybindPopup(setting.getKeybind());
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
        if (n3 == 0 && this.popupManager != null) {
            this.popupManager.toggle(this);
        }
        return true;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds3 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        boolean bl = this.isActive();
        boolean bl2 = this.settingKeybindPopup.isActive2();
        this.value3 = FrameInterpolator.lerpTowards(this.value3, bl || bl2 ? 1.0f : 0.0f, 15.0f);
        if (this.value3 <= 0.01f) {
            return bounds3.getY() + bounds3.getHeight();
        }
        float f2 = FontRegistry.font3.process3("\u041b", 6.75f);
        float f3 = FontRegistry.font3.process4("\u041b", 6.75f);
        float f4 = bounds3.getX() + (bounds3.getWidth() - f2) / 2.0f;
        float f5 = bounds3.getY() + (bounds3.getHeight() - f3) / 2.0f;
        int n = ColorUtils.withAlpha(ThemeColors.textPlaceholder(), 255.0f * this.value3);
        FontRegistry.font3.process5(matrix4f, drawApi, "\u041b", f4, f5, 6.75f, n);
        return bounds3.getY() + bounds3.getHeight();
    }

    private boolean isActive() {
        float f;
        GuiBounds bounds3 = GuiInteractionState.getInstance().getRootPanel().getBounds();
        GuiInteractionState guiInteractionState = GuiInteractionState.getInstance();
        float f2 = (float)guiInteractionState.getScaledMouseX() - bounds3.getX();
        return this.bounds4.contains(f2, f = (float)guiInteractionState.getScaledMouseY() - bounds3.getY()) || this.bounds3.contains(f2, f);
    }

    public SettingKeybindPopup getSettingKeybindPopup() {
        return this.settingKeybindPopup;
    }

    @Override
    public PopupPanel getPopup() {
        return this.settingKeybindPopup;
    }

    @Override
    public void update2() {
        float f = this.bounds4.getX() + this.bounds4.getWidth() + 4.0f;
        float f2 = this.bounds4.getY();
        this.settingKeybindPopup.getBounds().setPosition(f, f2);
    }

    @Override
    public void setPopupManager(PopupManager popupManager) {
        this.popupManager = popupManager;
        this.settingKeybindPopup.process2(popupManager, this);
    }

    public void process4(float f, float f2, float f3, float f4) {
        this.bounds3.setPosition(f, f2);
        this.bounds3.setSize(f3, f4);
    }

    @Override
    public void setBounds(GuiBounds bounds3) {
        float f = FontRegistry.font3.process3("\u041b", 6.75f);
        float f2 = FontRegistry.font3.process4("\u041b", 6.75f);
        float f3 = f * 2.5f;
        float f4 = f2 * 1.5f;
        float f5 = bounds3.getX() - 5.0f - f;
        float f6 = bounds3.getY() + (bounds3.getHeight() - f2) / 2.0f;
        this.getBounds().setPosition(f5 - (f3 - f) / 2.0f, f6 - (f4 - f2) / 2.0f);
        this.getBounds().setSize(f3, f4);
    }

    public void process5(float f, float f2) {
        this.bounds4.setPosition(f, f2);
        this.bounds4.setSize(this.getBounds().getWidth(), this.getBounds().getHeight());
    }
}

