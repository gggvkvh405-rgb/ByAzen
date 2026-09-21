/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.setting;

import ru.byazen.setting.ColorSetting;
import ru.byazen.setting.SettingBuilder;
import ru.byazen.setting.color.ColorChannel;
import ru.byazen.setting.color.ColorMode;

public final class ColorSettingBuilder
extends SettingBuilder {
    private ColorMode colorMode = ColorMode.STATIC;
    private int selectedIndex;
    private ColorChannel editingChannel = ColorChannel.PRIMARY;

    public ColorSettingBuilder selectedIndex(int index) {
        this.selectedIndex = index;
        return this;
    }

    public ColorSettingBuilder mode(ColorMode mode) {
        this.colorMode = mode;
        return this;
    }

    public ColorSettingBuilder editingChannel(ColorChannel channel) {
        this.editingChannel = channel;
        return this;
    }

    public ColorSetting build() {
        return new ColorSetting(this);
    }

    ColorMode getColorMode() {
        return this.colorMode;
    }

    int getSelectedIndex() {
        return this.selectedIndex;
    }

    ColorChannel getEditingChannel() {
        return this.editingChannel;
    }

    @Override
    protected ColorSettingBuilder self() {
        return this;
    }
}

