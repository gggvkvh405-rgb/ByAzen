/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.byazen.util;

import net.minecraft.class_310;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.BoundsSupplier;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.FrameInterpolator;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.ModeSelectionPopup;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.PopupManager;
import ru.byazen.misc.PopupOwner;
import ru.byazen.misc.ThemeColors;
import ru.byazen.setting.ModeSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.ui.PopupPanel;
import ru.byazen.ui.setting.SettingComponent;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.PopupPlacement;

public final class ModeSettingComponent
extends SettingComponent
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider,
PopupOwner {
    private PopupManager popupManager;
    private boolean enabled;
    private float value10;
    private final ModeSelectionPopup modeSelectionPopup;
    private final PopupPlacement popupPlacement;

    public ModeSettingComponent(ModeSetting modeSetting) {
        super(new GuiBounds(0.0f, 0.0f, 55.0f, 14.0f), modeSetting);
        this.modeSelectionPopup = new ModeSelectionPopup(modeSetting);
        this.popupPlacement = new PopupPlacement(3.0f, 1.0f);
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
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        this.enabled = this.modeSelectionPopup.isActive2();
        this.value10 = FrameInterpolator.lerpTowards(this.value10, this.enabled ? 1.0f : 0.0f, 15.0f);
        drawApi.drawRoundedRectangleOutlined(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 8.0f, 1.0f, ThemeColors.controlFill(), ThemeColors.borderPrimary());
        String string = this.getString();
        float f2 = this.process4(string, bounds2);
        float f3 = bounds2.getY() + 3.25f + (FontRegistry.font4.process4(string, 6.0f) - FontRegistry.font4.process4(string, f2)) / 2.0f;
        FontRegistry.font4.process2(matrix4f, drawApi, string, bounds2.getX() + 5.0f, f3, f2, ColorUtils.withAlpha(ThemeColors.textSecondary(), 255.0f));
        this.process5(matrix4f, drawApi, bounds2);
        return bounds2.getY() + this.getFloatType2();
    }

    @Override
    public float getFloatType() {
        return 55.0f;
    }

    private GuiBounds getContainerBounds() {
        for (GuiElement element2 = this.getParent(); element2 != null; element2 = element2.getParent()) {
            if (!(element2 instanceof BoundsSupplier)) continue;
            BoundsSupplier callback13 = (BoundsSupplier)((Object)element2);
            return callback13.getBounds();
        }
        return null;
    }

    private float process4(String string, GuiBounds bounds2) {
        float f = FontRegistry.font3.process3("F", 6.5f);
        float f2 = bounds2.getX() + bounds2.getWidth() - f - 4.5f;
        float f3 = f2 - (bounds2.getX() + 5.0f) - 2.0f;
        float f4 = FontRegistry.font4.process3(string, 6.0f);
        if (f3 <= 0.0f || f4 <= f3) {
            return 6.0f;
        }
        return Math.max(4.0f, 6.0f * f3 / f4);
    }

    private void process5(Matrix4f matrix4f, GuiDrawApi drawApi, GuiBounds bounds2) {
        float f = FontRegistry.font3.process3("F", 6.5f);
        float f2 = FontRegistry.font3.process4("F", 6.5f);
        float f3 = bounds2.getX() + bounds2.getWidth() - f - 4.5f;
        float f4 = bounds2.getY() + (bounds2.getHeight() - f2) / 2.0f;
        float f5 = f3 + f / 2.0f;
        float f6 = f4 + f2 / 2.0f;
        float f7 = 90.0f - 180.0f * this.value10;
        Matrix4f matrix4f2 = new Matrix4f((Matrix4fc)matrix4f).translate(f5, f6, 0.0f).rotateZ((float)Math.toRadians(f7)).translate(-f5, -f6, 0.0f);
        FontRegistry.font3.process5(matrix4f2, drawApi, "F", f3, f4, 6.5f, ThemeColors.textMuted());
    }

    public ModeSelectionPopup getModeSelectionPopup() {
        return this.modeSelectionPopup;
    }

    @Override
    public PopupPanel getPopup() {
        return this.modeSelectionPopup;
    }

    private String getString() {
        String string = ((ModeSetting)this.getSetting()).getSelectedOption();
        if (string != null && !string.isBlank()) {
            return string;
        }
        String[] stringArray = ((ModeSetting)this.getSetting()).getOptions();
        return stringArray != null && stringArray.length > 0 ? stringArray[0] : "";
    }

    @Override
    public boolean process6(int n, int n2) {
        GuiBounds bounds2 = this.getContainerBounds();
        if (bounds2 != null) {
            return new GuiBounds(bounds2.getX() + this.getBounds().getX(), bounds2.getY() + this.getBounds().getY(), this.getBounds().getWidth(), this.getBounds().getHeight()).contains(n, n2);
        }
        return new GuiBounds(this.getAbsoluteX(), this.getAbsoluteY(), this.getBounds().getWidth(), this.getBounds().getHeight()).contains(n, n2);
    }

    @Override
    public void update2() {
        float anchorY;
        float anchorX;
        GuiBounds bounds2 = this.getContainerBounds();
        if (bounds2 != null) {
            anchorX = bounds2.getX() + this.getBounds().getX();
            anchorY = bounds2.getY() + this.getBounds().getY();
        } else {
            anchorX = this.getAbsoluteX();
            anchorY = this.getAbsoluteY();
        }
        class_310 client = class_310.method_1551();
        float viewportWidth = client.field_1755 == null ? (float)client.method_22683().method_4486() : (float)client.field_1755.field_22789;
        float viewportHeight = client.field_1755 == null ? (float)client.method_22683().method_4502() : (float)client.field_1755.field_22790;
        this.popupPlacement.place(this.modeSelectionPopup.getBounds(), anchorX, anchorY, this.getBounds().getHeight(), viewportWidth, viewportHeight);
    }

    @Override
    public void setPopupManager(PopupManager popupManager) {
        this.popupManager = popupManager;
    }

    @Override
    public float getFloatType2() {
        return 14.0f;
    }
}

