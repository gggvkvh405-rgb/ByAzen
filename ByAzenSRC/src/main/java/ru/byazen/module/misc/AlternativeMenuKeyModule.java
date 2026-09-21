/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.module.misc;

import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.BindSettingBuilder;

public final class AlternativeMenuKeyModule
extends Module
implements ConfigSerializable {
    private static volatile AlternativeMenuKeyModule instance;
    private final BindSetting key;

    public AlternativeMenuKeyModule(EventBus eventBus) {
        super(eventBus, "alternative_menu_key", "Alternative Menu Key", "\u0414\u043e\u043f. \u043a\u043b\u0430\u0432\u0438\u0448\u0430 \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f \u043c\u0435\u043d\u044e", ModuleCategory.valueOf("MISC"), new String[0]);
        instance = this;
        this.key = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).name("Key").id("key").description("\u0414\u043e\u043f\u043e\u043b\u043d\u0438\u0442\u0435\u043b\u044c\u043d\u0430\u044f \u043a\u043b\u0430\u0432\u0438\u0448\u0430 \u043e\u0442\u043a\u0440\u044b\u0442\u0438\u044f \u043c\u0435\u043d\u044e")).build();
        this.registerSetting(this.key);
    }

    @Override
    protected void initialize() {
    }

    public static boolean process(int keyCode) {
        AlternativeMenuKeyModule module = instance;
        return module != null && module.key.getBindInput().matchesKeyboard(keyCode);
    }
}

