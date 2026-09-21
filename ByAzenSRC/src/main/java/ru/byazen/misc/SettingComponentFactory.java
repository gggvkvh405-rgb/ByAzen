/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.BindSettingComponent;
import ru.byazen.misc.BooleanSettingComponent;
import ru.byazen.misc.ColorSettingComponent;
import ru.byazen.misc.CompactTextFieldStyle;
import ru.byazen.misc.ExpandedTextFieldStyle;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.ColorSetting;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.RangeSetting;
import ru.byazen.setting.Setting;
import ru.byazen.setting.TextSetting;
import ru.byazen.ui.setting.SettingComponent;
import ru.byazen.util.ModeSettingComponent;
import ru.byazen.util.MultiSelectSettingComponent;
import ru.byazen.util.NumberSettingComponent;
import ru.byazen.util.RangeSettingComponent;
import ru.byazen.util.TextSettingComponent;

public final class SettingComponentFactory {
    public static SettingComponent<?> process(Setting setting) {
        if (setting instanceof BindSetting) {
            BindSetting bindSetting = (BindSetting)setting;
            return new BindSettingComponent(bindSetting);
        }
        if (setting instanceof BooleanSetting) {
            BooleanSetting booleanSetting = (BooleanSetting)setting;
            return new BooleanSettingComponent(booleanSetting);
        }
        if (setting instanceof ColorSetting) {
            ColorSetting colorSetting = (ColorSetting)setting;
            return new ColorSettingComponent(colorSetting);
        }
        if (setting instanceof TextSetting) {
            TextSetting textSetting = (TextSetting)setting;
            return new TextSettingComponent(textSetting, textSetting.isExpanded() ? new ExpandedTextFieldStyle() : new CompactTextFieldStyle());
        }
        if (setting instanceof ModeSetting) {
            ModeSetting modeSetting = (ModeSetting)setting;
            return new ModeSettingComponent(modeSetting);
        }
        if (setting instanceof MultiSelectSetting) {
            MultiSelectSetting multiSelectSetting = (MultiSelectSetting)setting;
            return new MultiSelectSettingComponent(multiSelectSetting);
        }
        if (setting instanceof NumberSetting) {
            NumberSetting numberSetting = (NumberSetting)setting;
            return new NumberSettingComponent(numberSetting);
        }
        if (setting instanceof RangeSetting) {
            RangeSetting rangeSetting = (RangeSetting)setting;
            return new RangeSettingComponent(rangeSetting);
        }
        return null;
    }
}

