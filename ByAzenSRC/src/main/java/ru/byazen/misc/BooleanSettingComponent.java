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
import ru.byazen.misc.FrameInterpolator;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.ThemeColors;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.setting.SettingComponent;
import ru.byazen.util.ColorUtils;
import ru.byazen.util.GuiDrawApi;

public final class BooleanSettingComponent
extends SettingComponent
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    static final float value = 10.0f;
    private float value2;
    static final float value3 = 15.0f;

    public BooleanSettingComponent(BooleanSetting booleanSetting) {
        super(new GuiBounds(0.0f, 0.0f, 15.0f, 10.0f), booleanSetting);
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
        ((BooleanSetting)this.getSetting()).setEnabled(!((BooleanSetting)this.getSetting()).isEnabled());
        return true;
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        this.value2 = FrameInterpolator.lerpTowards(this.value2, ((BooleanSetting)this.getSetting()).isEnabled() ? 1.0f : 0.0f, 15.0f);
        int n = ColorUtils.lerp(ThemeColors.borderPrimary(), ThemeColors.accent(), this.value2);
        int n2 = ColorUtils.lerp(ThemeColors.textMuted(), ThemeColors.backgroundControl(), this.value2);
        float f2 = 7.0f;
        float f3 = 1.5f;
        float f4 = bounds2.getWidth() - f2 - f3 * 2.0f;
        float f5 = bounds2.getX() + f3 + f4 * this.value2;
        float f6 = bounds2.getY() + (bounds2.getHeight() - f2) / 2.0f;
        drawApi.drawRoundedRectangle(matrix4f, bounds2.getX(), bounds2.getY(), bounds2.getWidth(), bounds2.getHeight(), 16.0f, n);
        drawApi.drawRoundedRectangle(matrix4f, f5, f6, f2, f2, 16.0f, n2);
        return bounds2.getY() + bounds2.getHeight();
    }

    @Override
    public float getFloatType() {
        return 15.0f;
    }

    @Override
    public float getFloatType2() {
        return 10.0f;
    }
}

