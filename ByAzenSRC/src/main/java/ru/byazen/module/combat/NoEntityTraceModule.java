/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.module.combat;

import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;

public final class NoEntityTraceModule
extends Module
implements ConfigSerializable {
    private static volatile NoEntityTraceModule instance;
    private final BooleanSetting enabledSetting;

    public NoEntityTraceModule(EventBus eventBus) {
        super(eventBus, "no_entity_trace", "No Entity Trace", "\u041e\u0442\u043a\u043b\u044e\u0447\u0430\u0435\u0442 \u0442\u0440\u0430\u0441\u0441\u0438\u0440\u043e\u0432\u043a\u0443 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439, \u043f\u043e\u0437\u0432\u043e\u043b\u044f\u044f \u0432\u0437\u0430\u0438\u043c\u043e\u0434\u0435\u0439\u0441\u0442\u0432\u043e\u0432\u0430\u0442\u044c \u0441\u043a\u0432\u043e\u0437\u044c \u043d\u0438\u0445", ModuleCategory.valueOf("COMBAT"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u041e\u0442\u043a\u043b\u044e\u0447\u0430\u0435\u0442 \u0442\u0440\u0430\u0441\u0441\u0438\u0440\u043e\u0432\u043a\u0443 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439, \u043f\u043e\u0437\u0432\u043e\u043b\u044f\u044f \u0432\u0437\u0430\u0438\u043c\u043e\u0434\u0435\u0439\u0441\u0442\u0432\u043e\u0432\u0430\u0442\u044c \u0441\u043a\u0432\u043e\u0437\u044c \u043d\u0438\u0445").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
    }

    public static boolean isEnabled() {
        NoEntityTraceModule module = instance;
        return module != null && module.enabledSetting.isEnabled();
    }
}

