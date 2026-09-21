/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.util;

import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.input.InputBindings;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.BoundsSupplier;
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
import ru.byazen.misc.RangeEditorPopup;
import ru.byazen.misc.ScaleSettings;
import ru.byazen.misc.ThemeColors;
import ru.byazen.setting.RangeSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.ui.PopupPanel;
import ru.byazen.ui.SliderTrack;
import ru.byazen.ui.setting.SettingComponent;
import ru.byazen.util.GuiDrawApi;
import ru.byazen.util.NumberFormatting;
import ru.byazen.util.SliderRenderer;

public final class RangeSettingComponent
extends SettingComponent
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider,
PopupOwner {
    private final int slot;
    private int slot2 = -1;
    private PopupManager popupManager;
    private final RangeEditorPopup editorPopup;
    private float dragOffset;
    private float upperProgress;
    private final int slot3;
    private final SliderRenderer sliderRenderer = new SliderRenderer();
    private float lowerProgress;

    public RangeSettingComponent(RangeSetting rangeSetting) {
        super(new GuiBounds(0.0f, 0.0f, 110.0f, 0.0f), rangeSetting);
        this.slot = 0;
        this.slot3 = 1;
        this.editorPopup = new RangeEditorPopup(rangeSetting);
        this.getBounds().setSize(this.getBounds().getWidth(), this.getFloatType2());
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
        if (n3 == 1) {
            if (this.popupManager != null) {
                this.popupManager.toggle(this);
            }
            return true;
        }
        if (n3 != 0) {
            return true;
        }
        String string = NumberFormatting.format(((RangeSetting)this.getSetting()).getMinimum(), ((RangeSetting)this.getSetting()).getPrecision());
        float f = FontRegistry.font2.process4(string, 6.5f);
        SliderTrack track = this.sliderRenderer.process7(this.getBounds(), f);
        if ((float)n2 < track.y() - 3.0f || (float)n2 > track.y() + this.sliderRenderer.getFloatType3() + 3.0f) {
            return true;
        }
        this.dragOffset = (float)GuiInteractionState.getInstance().getScaledMouseX() - ((float)n - this.getBounds().getX());
        this.slot2 = this.process4(((float)n - track.x()) / track.width());
        this.setFloatType(n);
        return true;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        float f2;
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        RangeSetting rangeSetting = (RangeSetting)this.getSetting();
        if (this.slot2 != -1) {
            if (!InputBindings.isMouseButtonPressed(0)) {
                this.slot2 = -1;
            } else {
                f2 = this.getBounds().getX() + (float)GuiInteractionState.getInstance().getScaledMouseX() - this.dragOffset;
                this.setFloatType(f2);
            }
        }
        f2 = (float)rangeSetting.getLowerNormalizedValue();
        float f3 = (float)rangeSetting.getUpperNormalizedValue();
        this.lowerProgress = FrameInterpolator.lerpTowards(this.lowerProgress, f2, this.slot2 == 0 ? 35.0f : 20.0f);
        this.upperProgress = FrameInterpolator.lerpTowards(this.upperProgress, f3, this.slot2 == 1 ? 35.0f : 20.0f);
        ScaleSettings scaleSettings = this.process5(rangeSetting);
        String string = NumberFormatting.format(rangeSetting.getMinimum(), rangeSetting.getPrecision());
        float f4 = FontRegistry.font2.process4(string, 6.5f);
        float f5 = bounds2.getY() + this.sliderRenderer.getFloatType2();
        SliderTrack track = this.sliderRenderer.process7(bounds2, f4);
        String string2 = this.process6(rangeSetting);
        float f6 = bounds2.getX() + bounds2.getWidth() - this.sliderRenderer.getFloatType() - FontRegistry.font2.process3(string2, 6.5f);
        FontRegistry.font2.process2(matrix4f, drawApi, string2, f6, f5 - 3.0f, 6.5f, ThemeColors.accent());
        FontRegistry.font2.process2(matrix4f, drawApi, string, track.x(), f5 - 3.0f, 6.5f, ThemeColors.textMuted());
        int n = ThemeColors.borderSubtle();
        int n2 = ThemeColors.textDisabled();
        int n3 = ThemeColors.accent();
        this.sliderRenderer.process2(matrix4f, drawApi, track, n);
        this.sliderRenderer.renderTickMarks(matrix4f, drawApi, track, scaleSettings, n, n2);
        float f7 = this.sliderRenderer.process4(this.lowerProgress, scaleSettings);
        float f8 = this.sliderRenderer.process4(this.upperProgress, scaleSettings);
        float f9 = track.x() + track.width() * f7;
        float f10 = track.x() + track.width() * f8;
        float f11 = Math.max(0.0f, f10 - f9);
        float f12 = track.y() + this.sliderRenderer.getFloatType3() / 2.0f;
        drawApi.drawRoundedRectangleGradient(matrix4f, f9, track.y(), f11, this.sliderRenderer.getFloatType3(), 2.0f, n3, n3, n3, n3);
        this.sliderRenderer.process(matrix4f, drawApi, f9, f12, n3);
        this.sliderRenderer.process(matrix4f, drawApi, f10, f12, n3);
        return bounds2.getY() + bounds2.getHeight();
    }

    @Override
    public float getFloatType() {
        return this.getBounds().getWidth();
    }

    private GuiBounds getContainerBounds() {
        for (GuiElement element2 = this.getParent(); element2 != null; element2 = element2.getParent()) {
            if (!(element2 instanceof BoundsSupplier)) continue;
            BoundsSupplier callback13 = (BoundsSupplier)((Object)element2);
            return callback13.getBounds();
        }
        return null;
    }

    private int process4(float f) {
        RangeSetting rangeSetting = (RangeSetting)this.getSetting();
        ScaleSettings scaleSettings = this.process5(rangeSetting);
        float f2 = Math.max(0.0f, Math.min(1.0f, f));
        float f3 = this.sliderRenderer.process4((float)rangeSetting.getLowerNormalizedValue(), scaleSettings);
        float f4 = this.sliderRenderer.process4((float)rangeSetting.getUpperNormalizedValue(), scaleSettings);
        float f5 = Math.abs(f2 - f3);
        float f6 = Math.abs(f2 - f4);
        return f6 <= f5 ? 1 : 0;
    }

    private ScaleSettings process5(RangeSetting rangeSetting) {
        return new ScaleSettings(rangeSetting.getMinimum(), rangeSetting.getMaximum(), rangeSetting.getPrecision(), rangeSetting.hasMarkers(), rangeSetting.getMarkerStep(), rangeSetting.getSnapStep());
    }

    private void setFloatType(float f) {
        if (this.slot2 == -1) {
            return;
        }
        GuiBounds bounds2 = this.getBounds();
        RangeSetting rangeSetting = (RangeSetting)this.getSetting();
        ScaleSettings scaleSettings = this.process5(rangeSetting);
        float f2 = bounds2.getX() + this.sliderRenderer.getFloatType();
        float f3 = Math.max(1.0f, bounds2.getWidth() - this.sliderRenderer.getFloatType() * 2.0f);
        float f4 = Math.max(0.0f, Math.min(f3, f - f2));
        float f5 = f4 / f3;
        double d = this.sliderRenderer.process3(f5, scaleSettings);
        double d2 = rangeSetting.getMinimum() + (rangeSetting.getMaximum() - rangeSetting.getMinimum()) * d;
        if (rangeSetting.hasSnapStep()) {
            d2 = NumberFormatting.snap(d2, rangeSetting.getMinimum(), rangeSetting.getMaximum(), rangeSetting.getSnapStep());
        }
        double d3 = NumberFormatting.round(d2, rangeSetting.getPrecision());
        double d4 = rangeSetting.getMaximum() - rangeSetting.getMinimum();
        if (d4 <= 0.0) {
            return;
        }
        double d5 = (d3 - rangeSetting.getMinimum()) / d4;
        d5 = Math.max(0.0, Math.min(1.0, d5));
        if (this.slot2 == 1) {
            rangeSetting.setUpperNormalizedValue(Math.max(rangeSetting.getLowerNormalizedValue(), d5));
            return;
        }
        rangeSetting.setLowerNormalizedValue(Math.min(rangeSetting.getUpperNormalizedValue(), d5));
    }

    private String process6(RangeSetting rangeSetting) {
        String string;
        String string2 = NumberFormatting.format(rangeSetting.getUpperUnscaledValue(), rangeSetting.getPrecision());
        String string3 = NumberFormatting.format(rangeSetting.getLowerUnscaledValue(), rangeSetting.getPrecision());
        String string4 = string3 + " - " + string2;
        if (!rangeSetting.hasFormatter()) {
            return string4;
        }
        String string5 = rangeSetting.format(rangeSetting.getUpperUnscaledValue());
        if (string5.isBlank()) {
            return string4;
        }
        if ("%".equals(string5)) {
            String string6 = string5;
            String string7 = string4;
            string = string7 + string6;
        } else {
            String string8 = string5;
            String string9 = string4;
            string = string9 + " " + string8;
        }
        return string;
    }

    public RangeEditorPopup getEditorPopup() {
        return this.editorPopup;
    }

    @Override
    public PopupPanel getPopup() {
        return this.editorPopup;
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
        GuiBounds bounds2 = this.getContainerBounds();
        if (bounds2 != null) {
            float f = bounds2.getX() + this.getBounds().getX() + this.getBounds().getWidth() / 2.0f;
            float f2 = bounds2.getY() + this.getBounds().getY();
            this.editorPopup.getBounds().setPosition(f, f2);
            return;
        }
        float f = this.getAbsoluteX();
        float f3 = this.getAbsoluteY() + this.getBounds().getHeight() + 1.0f;
        this.editorPopup.getBounds().setPosition(f, f3);
    }

    @Override
    public void setPopupManager(PopupManager popupManager) {
        this.popupManager = popupManager;
    }

    @Override
    public float getFloatType2() {
        return this.sliderRenderer.process6(((RangeSetting)this.getSetting()).hasMarkers());
    }
}

