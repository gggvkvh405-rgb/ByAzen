/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.module.hud;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class ExtraTabModule
extends Module
implements ConfigSerializable {
    private static volatile ExtraTabModule instance;
    private final BooleanSetting enabledSetting;

    public ExtraTabModule(EventBus eventBus) {
        super(eventBus, "extra_tab", "Extra Tab", "\u041f\u043e\u043b\u043d\u044b\u0439 \u0441\u043f\u0438\u0441\u043e\u043a \u0438\u0433\u0440\u043e\u043a\u043e\u0432 \u0432 \u0442\u0430\u0431\u0435", ModuleCategory.valueOf("DISPLAY"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
    }

    public static boolean isEnabled() {
        ExtraTabModule module = instance;
        return module != null && module.enabledSetting.isEnabled();
    }
}

