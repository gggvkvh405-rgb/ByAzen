/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.module.misc;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class AutoConfigSaveModule
extends Module
implements ConfigSerializable {
    private static volatile AutoConfigSaveModule instance;
    private final BooleanSetting enabledSetting;

    public AutoConfigSaveModule(EventBus eventBus) {
        super(eventBus, "auto_config_save", "Auto Config Save", "\u0410\u0432\u0442\u043e\u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0438\u0435 \u043a\u043e\u043d\u0444\u0438\u0433\u0430 \u043f\u0440\u0438 \u0437\u0430\u043a\u0440\u044b\u0442\u0438\u0438 \u0438\u0433\u0440\u044b", ModuleCategory.valueOf("MISC"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Enabled").id("enabled").description("\u0410\u0432\u0442\u043e\u0441\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0438\u0435 \u043a\u043e\u043d\u0444\u0438\u0433\u0430 \u043f\u0440\u0438 \u0437\u0430\u043a\u0440\u044b\u0442\u0438\u0438 \u0438\u0433\u0440\u044b").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
    }

    public static boolean isEnabled() {
        return AutoConfigSaveModule.isActive();
    }

    public static boolean isActive() {
        AutoConfigSaveModule module = instance;
        return module == null || module.enabledSetting.isEnabled();
    }
}

