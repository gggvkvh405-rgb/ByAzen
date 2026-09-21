/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.util.function.Consumer;
import java.util.function.Supplier;
import ru.byazen.input.BindInput;
import ru.byazen.misc.BindSettingAdapter;
import ru.byazen.misc.DelegatingKeybind;
import ru.byazen.misc.SettingKeybindAdapter;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.SettingKeybind;

public interface KeybindDescriptor {
    public static KeybindDescriptor process(String string, String string2, Supplier<BindInput> supplier, Consumer<BindInput> consumer) {
        return new DelegatingKeybind(string, string2, supplier, consumer);
    }

    public String getString();

    public String getString2();

    public static KeybindDescriptor process2(BindSetting bindSetting) {
        return new BindSettingAdapter(bindSetting);
    }

    public void setBindInput(BindInput var1);

    public BindInput getBindInput();

    public static KeybindDescriptor process3(SettingKeybind settingKeybind) {
        return new SettingKeybindAdapter(settingKeybind);
    }

    default public boolean isActive() {
        return !this.getBindInput().isUnbound();
    }
}

