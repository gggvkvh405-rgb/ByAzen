/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.byazen.util;

import java.util.Arrays;
import java.util.List;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.FrameInterpolator;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.MultiSelectPopup;
import ru.byazen.misc.PopupManager;
import ru.byazen.misc.PopupOwner;
import ru.byazen.misc.TextLayoutUtils;
import ru.byazen.misc.ThemeColors;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.MultiSelectSettingBuilder;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.ui.PopupPanel;
import ru.byazen.util.GuiDrawApi;

public final class KeybindFilter
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider,
PopupOwner {
    private final MultiSelectPopup multiSelectPopup;
    private float value3;
    private PopupManager popupManager;
    private final MultiSelectSetting multiSelectSetting;

    public KeybindFilter() {
        super(new GuiBounds(0.0f, 0.0f, 90.0f, 13.5f));
        String[] stringArray = (String[])Arrays.stream(ModuleCategory.values()).map(ModuleCategory::getName).toArray(String[]::new);
        this.multiSelectSetting = ((MultiSelectSettingBuilder)((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().id("bindings.categories")).name("\u041a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0438")).options(stringArray).optionListEnabled(false).build();
        this.multiSelectPopup = new MultiSelectPopup(this.multiSelectSetting);
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
        if (this.popupManager != null) {
            this.popupManager.toggle(this);
        }
        return true;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        this.value3 = FrameInterpolator.lerpTowards(this.value3, this.multiSelectPopup.isActive2() ? 1.0f : 0.0f, 15.0f);
        drawApi.drawRoundedRectangleOutlined(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 8.0f, 0.75f, ThemeColors.controlFill(), ThemeColors.borderPrimary());
        FontRegistry.font3.process5(matrix4f, drawApi, "\u0447", bounds2.getX() + 4.0f, bounds2.getY() + 3.75f, 6.0f, ThemeColors.textSecondary());
        float f2 = FontRegistry.font3.process3("\u0447", 6.0f);
        float f3 = bounds2.getX() + 4.0f + f2 + 2.5f;
        String string = TextLayoutUtils.trimToWidth(this.getString(), FontRegistry.font4, 6.0f, 60.0f);
        float f4 = FontRegistry.font4.process4(string, 6.0f);
        float f5 = bounds2.getY() + (bounds2.getHeight() - f4) / 2.0f;
        FontRegistry.font4.process2(matrix4f, drawApi, string, f3, f5, 6.0f, ThemeColors.textSecondary());
        this.process5(matrix4f, drawApi, bounds2);
        return bounds2.getY() + bounds2.getHeight();
    }

    public List<String> getList() {
        return this.multiSelectSetting.getSelectedOptions();
    }

    private String getString() {
        List<String> list = this.multiSelectSetting.getSelectedOptions();
        if (list == null || list.isEmpty()) {
            return "\u0412\u0441\u0435 \u043a\u0430\u0442\u0435\u0433\u043e\u0440\u0438\u0438";
        }
        return String.join((CharSequence)", ", list);
    }

    private void process5(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2) {
        float f = FontRegistry.font3.process3("F", 6.5f);
        float f2 = FontRegistry.font3.process4("F", 6.5f);
        float f3 = bounds2.getX() + bounds2.getWidth() - f - 4.5f;
        float f4 = bounds2.getY() + (bounds2.getHeight() - f2) / 2.0f;
        float f5 = f3 + f / 2.0f;
        float f6 = f4 + f2 / 2.0f;
        float f7 = 90.0f - 180.0f * this.value3;
        Matrix4f matrix4f2 = new Matrix4f((Matrix4fc)matrix4f).translate(f5, f6, 0.0f).rotateZ((float)Math.toRadians(f7)).translate(-f5, -f6, 0.0f);
        FontRegistry.font3.process5(matrix4f2, drawApi, "F", f3, f4, 6.5f, ThemeColors.textMuted());
    }

    @Override
    public PopupPanel getPopup() {
        return this.multiSelectPopup;
    }

    @Override
    public boolean process6(int n, int n2) {
        return this.getBounds().contains(n, n2);
    }

    @Override
    public void update2() {
        GuiBounds bounds2 = this.getBounds();
        this.multiSelectPopup.getBounds().setPosition(bounds2.getX(), bounds2.getY() + bounds2.getHeight() + 1.0f);
    }

    @Override
    public void setPopupManager(PopupManager popupManager) {
        this.popupManager = popupManager;
    }
}

