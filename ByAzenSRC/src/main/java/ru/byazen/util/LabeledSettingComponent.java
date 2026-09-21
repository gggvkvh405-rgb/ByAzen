/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.util;

import java.util.Objects;
import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.FontRegistry;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.ThemeColors;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;
import ru.byazen.ui.setting.SettingComponent;
import ru.byazen.util.GuiDrawApi;

public final class LabeledSettingComponent
extends GuiElement
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final String string2;
    private final SettingComponent<?> settingComponent;
    private final float value2;
    private final float value3;

    public LabeledSettingComponent(String string, SettingComponent<?> settingComponent2) {
        super(new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f));
        this.value2 = 6.0f;
        this.value3 = 6.0f;
        this.string2 = string;
        this.settingComponent = settingComponent2;
        this.addChild(settingComponent2);
    }

    @Override
    public void onMouseScroll(int n, int n2, double d) {
        this.settingComponent.onMouseScroll(n, n2, d);
    }

    @Override
    public void update() {
        this.settingComponent.update();
    }

    @Override
    public float render(float f, Matrix4f matrix4f) {
        GuiBounds bounds2 = this.getBounds();
        GuiDrawApi drawApi = ByAzenClient.getGuiRenderer();
        float f2 = bounds2.getX() + bounds2.getWidth() - this.settingComponent.getFloatType();
        float f3 = bounds2.getY() + (bounds2.getHeight() - this.settingComponent.getFloatType2()) / 2.0f;
        this.settingComponent.getBounds().setPosition(f2, f3);
        this.settingComponent.getBounds().setSize(this.settingComponent.getFloatType(), this.settingComponent.getFloatType2());
        float f4 = bounds2.getY() + (bounds2.getHeight() - FontRegistry.font2.process4(this.string2, 6.0f)) / 2.0f;
        FontRegistry.font2.process2(matrix4f, drawApi, this.string2, bounds2.getX(), f4, 6.0f, ThemeColors.textPrimary());
        this.settingComponent.render(f, matrix4f);
        return bounds2.getY() + bounds2.getHeight();
    }

    public float getFloatType() {
        return FontRegistry.font2.process3(this.string2, 6.0f) + 6.0f + this.settingComponent.getFloatType();
    }

    public String getString2() {
        return this.string2;
    }

    public float getSpacing() {
        Objects.requireNonNull(this);
        return 6.0f;
    }

    public float getFloatType3() {
        Objects.requireNonNull(this);
        return 6.0f;
    }

    public SettingComponent<?> getSettingComponent() {
        return this.settingComponent;
    }

    public float getFloatType2() {
        float f = FontRegistry.font2.process4(this.string2, 6.0f);
        return Math.max(f, this.settingComponent.getFloatType2());
    }
}

