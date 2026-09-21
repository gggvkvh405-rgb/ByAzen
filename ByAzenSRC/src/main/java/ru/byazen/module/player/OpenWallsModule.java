/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.module.player;

import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;

public final class OpenWallsModule
extends Module
implements ConfigSerializable {
    private static volatile OpenWallsModule instance;
    private final BooleanSetting enabledSetting;

    public OpenWallsModule(EventBus eventBus) {
        super(eventBus, "open_walls", "Open Walls", "\u041e\u0442\u043a\u043b\u044e\u0447\u0430\u0435\u0442 \u0442\u0440\u0430\u0441\u0441\u0438\u0440\u043e\u0432\u043a\u0443 \u0431\u043b\u043e\u043a\u043e\u0432, \u0441 \u043a\u043e\u0442\u043e\u0440\u044b\u043c\u0438 \u043d\u0435\u043b\u044c\u0437\u044f \u0432\u0437\u0430\u0438\u043c\u043e\u0434\u0435\u0439\u0441\u0442\u0432\u043e\u0432\u0430\u0442\u044c", ModuleCategory.valueOf("PLAYER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
    }

    public static boolean isEnabled() {
        OpenWallsModule module = instance;
        return module != null && module.enabledSetting.isEnabled();
    }
}

