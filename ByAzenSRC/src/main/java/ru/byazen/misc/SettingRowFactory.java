/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.ContainerDisplay;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.ColorSetting;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.RangeSetting;
import ru.byazen.setting.Setting;
import ru.byazen.setting.TextSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.setting.SettingRow;
import ru.byazen.util.BindSettingRow;
import ru.byazen.util.BooleanSettingRow;
import ru.byazen.util.ColorSettingRow;
import ru.byazen.util.ModeSettingRow;
import ru.byazen.util.MultiSelectSettingRow;
import ru.byazen.util.NumberSettingRow;
import ru.byazen.util.RangeSettingRow;
import ru.byazen.util.TextSettingRow;

public final class SettingRowFactory {
    public static SettingRow<?> process(Setting setting, ContainerDisplay containerDisplay) {
        if (setting instanceof BindSetting) {
            BindSetting bindSetting = (BindSetting)setting;
            return new BindSettingRow(new GuiBounds(0.0f, 0.0f, 0.0f, 11.0f), bindSetting, containerDisplay);
        }
        if (setting instanceof BooleanSetting) {
            BooleanSetting booleanSetting = (BooleanSetting)setting;
            return new BooleanSettingRow(new GuiBounds(0.0f, 0.0f, 0.0f, 11.0f), booleanSetting, containerDisplay);
        }
        if (setting instanceof ColorSetting) {
            ColorSetting colorSetting = (ColorSetting)setting;
            return new ColorSettingRow(new GuiBounds(0.0f, 0.0f, 0.0f, 11.0f), colorSetting, containerDisplay);
        }
        if (setting instanceof TextSetting) {
            TextSetting textSetting = (TextSetting)setting;
            return new TextSettingRow(new GuiBounds(0.0f, 0.0f, 0.0f, 11.0f), textSetting, containerDisplay);
        }
        if (setting instanceof ModeSetting) {
            ModeSetting modeSetting = (ModeSetting)setting;
            return new ModeSettingRow(new GuiBounds(0.0f, 0.0f, 0.0f, 11.0f), modeSetting, containerDisplay);
        }
        if (setting instanceof MultiSelectSetting) {
            MultiSelectSetting multiSelectSetting = (MultiSelectSetting)setting;
            return new MultiSelectSettingRow(new GuiBounds(0.0f, 0.0f, 0.0f, 11.0f), multiSelectSetting, containerDisplay);
        }
        if (setting instanceof NumberSetting) {
            NumberSetting numberSetting = (NumberSetting)setting;
            return new NumberSettingRow(new GuiBounds(0.0f, 0.0f, 0.0f, 11.0f), numberSetting, containerDisplay);
        }
        if (setting instanceof RangeSetting) {
            RangeSetting rangeSetting = (RangeSetting)setting;
            return new RangeSettingRow(new GuiBounds(0.0f, 0.0f, 0.0f, 11.0f), rangeSetting, containerDisplay);
        }
        return null;
    }
}

