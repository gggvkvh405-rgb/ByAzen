/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.ui.setting;

import java.util.Objects;
import ru.byazen.setting.Setting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.GuiElement;

public class SettingComponent<T extends Setting>
extends GuiElement {
    private final T setting;

    public SettingComponent(GuiBounds bounds, Setting setting) {
        super(bounds);
        this.setting = (T)Objects.requireNonNull(setting, "setting");
    }

    public T getSetting() {
        return this.setting;
    }

    public float getFloatType() {
        return this.getBounds().getWidth();
    }

    public float getFloatType2() {
        return this.getBounds().getHeight();
    }
}

