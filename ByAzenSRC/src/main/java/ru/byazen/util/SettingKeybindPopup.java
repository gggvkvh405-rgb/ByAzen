/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.util;

import org.joml.Matrix4f;
import ru.byazen.misc.BindActivationModeSelector;
import ru.byazen.misc.BindingVisibility;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.BoundsSupplier;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.PopupManager;
import ru.byazen.misc.PopupOwner;
import ru.byazen.misc.PopupTreeBinder;
import ru.byazen.misc.SettingComponentFactory;
import ru.byazen.misc.ThemeColors;
import ru.byazen.setting.Setting;
import ru.byazen.setting.SettingKeybind;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.ui.PopupPanel;
import ru.byazen.ui.setting.SettingComponent;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.LabeledSettingComponent;

public final class SettingKeybindPopup
extends PopupPanel
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider,
BoundsSupplier {
    private final LabeledSettingComponent labeledSettingComponent;
    private final LabeledSettingComponent labeledSettingComponent2;
    private final BindActivationModeSelector activationModeSelector;
    private final BindingVisibility bindingVisibility;
    private final LabeledSettingComponent labeledSettingComponent3;
    private boolean enabled;
    private final SettingKeybind settingKeybind;
    private boolean enabled2;

    public SettingKeybindPopup(SettingKeybind settingKeybind) {
        super(new GuiBounds(0.0f, 0.0f, 96.0f, 74.0f));
        this.settingKeybind = settingKeybind;
        this.bindingVisibility = new BindingVisibility(settingKeybind);
        this.labeledSettingComponent = new LabeledSettingComponent("\u0417\u043d\u0430\u0447\u0435\u043d\u0438\u0435", this.process5(this.bindingVisibility.getSetting()));
        this.labeledSettingComponent3 = new LabeledSettingComponent("\u041a\u043d\u043e\u043f\u043a\u0430", this.process5(this.bindingVisibility.getBindSetting()));
        this.labeledSettingComponent2 = new LabeledSettingComponent("\u0412\u0438\u0434\u0438\u043c\u043e\u0441\u0442\u044c", this.process5(this.bindingVisibility.getBooleanSetting()));
        this.activationModeSelector = new BindActivationModeSelector(this.bindingVisibility.getBindActivationMode(), this.bindingVisibility::setBindActivationMode);
        this.addChild(this.labeledSettingComponent);
        this.addChild(this.labeledSettingComponent3);
        this.addChild(this.labeledSettingComponent2);
        this.addChild(this.activationModeSelector);
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
        int n3 = (int)((float)n - this.getBounds().getX());
        int n4 = (int)((float)n2 - this.getBounds().getY());
        for (GuiElement element2 : this.children) {
            element2.onMouseScroll(n3, n4, d);
        }
    }

    @Override
    public void update() {
        boolean bl = this.isActive2();
        if (bl && !this.enabled2) {
            this.bindingVisibility.loadFromKeybind();
            this.settingKeybind.markEditorInitialized();
        }
        if (bl) {
            this.bindingVisibility.saveToKeybind();
            for (GuiElement element2 : this.children) {
                element2.update();
            }
        } else if (this.enabled2) {
            this.bindingVisibility.saveToKeybind();
        }
        this.enabled2 = bl;
    }

    @Override
    public void onMouseReleased(int n, int n2, int n3) {
        int n4 = (int)((float)n - this.getBounds().getX());
        int n5 = (int)((float)n2 - this.getBounds().getY());
        super.onMouseReleased(n4, n5, n3);
    }

    @Override
    public void update2() {
        if (this.isActive2()) {
            this.bindingVisibility.saveToKeybind();
        }
        super.update2();
    }

    public void process2(PopupManager popupManager, PopupOwner callback34) {
        if (this.enabled || popupManager == null || callback34 == null) {
            return;
        }
        PopupTreeBinder.bindTree(this.labeledSettingComponent, popupManager, callback34);
        PopupTreeBinder.bindTree(this.labeledSettingComponent3, popupManager, callback34);
        PopupTreeBinder.bindTree(this.labeledSettingComponent2, popupManager, callback34);
        this.enabled = true;
    }

    @Override
    protected void updateLayout() {
        float f = Math.max(this.labeledSettingComponent.getFloatType(), Math.max(this.labeledSettingComponent3.getFloatType(), this.labeledSettingComponent2.getFloatType()));
        float f2 = Math.max(96.0f, f + 10.5f);
        float f3 = 21.0f;
        float f4 = f2 - 10.5f;
        this.process4(this.labeledSettingComponent, f4, f3);
        this.process4(this.labeledSettingComponent3, f4, f3 += this.labeledSettingComponent.getBounds().getHeight() + 4.0f);
        this.process4(this.labeledSettingComponent2, f4, f3 += this.labeledSettingComponent3.getBounds().getHeight() + 4.0f);
        this.process7(this.activationModeSelector, f4, f3 += this.labeledSettingComponent2.getBounds().getHeight() + 4.0f);
        this.getBounds().setSize(f2, f3 += this.activationModeSelector.getBounds().getHeight() + 5.0f);
    }

    @Override
    protected void renderPopup(float f, Matrix4f matrix4f, GuiDrawApi drawApi) {
        GuiBounds bounds2 = this.getBounds();
        drawApi.fillRectangle(matrix4f, 0.0f, 15.0f, bounds2.getWidth(), 0.5f, ThemeColors.borderPrimary());
        FontRegistry.font2.process2(matrix4f, drawApi, "\u0411\u0438\u043d\u0434", 4.0f, 4.0f, 5.75f, ThemeColors.textPlaceholder());
        this.renderChildren(f, matrix4f);
    }

    private void process4(LabeledSettingComponent labeledSettingComponent, float f, float f2) {
        labeledSettingComponent.getBounds().setPosition(5.25f, f2);
        labeledSettingComponent.getBounds().setSize(f, labeledSettingComponent.getFloatType2());
    }

    private SettingComponent<?> process5(Setting setting) {
        SettingComponent<?> settingComponent = SettingComponentFactory.process(setting);
        if (settingComponent == null) {
            String string = setting.getClass().getName();
            throw new IllegalStateException("Unsupported popup control setting: " + string);
        }
        return settingComponent;
    }

    private void process7(BindActivationModeSelector selector, float f, float f2) {
        selector.getBounds().setPosition(5.25f, f2);
        selector.getBounds().setSize(f, selector.getFloatType2());
    }

    @Override
    public GuiBounds getBounds() {
        return super.getBounds();
    }
}

