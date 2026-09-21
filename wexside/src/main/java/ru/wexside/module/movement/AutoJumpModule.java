/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.wexside.module.movement;

import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public class AutoJumpModule
extends Module
implements ConfigSerializable {
    private static volatile AutoJumpModule instance;
    private final BooleanSetting enabledSetting;

    public AutoJumpModule(EventBus eventBus) {
        super(eventBus, "auto_jump", "Auto Jump", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0432\u044b\u043f\u043e\u043b\u043d\u044f\u0435\u0442 \u043f\u0440\u044b\u0436\u043e\u043a \u043f\u0440\u0438 \u043a\u0430\u0441\u0430\u043d\u0438\u0438 \u0437\u0435\u043c\u043b\u0438", ModuleCategory.valueOf("MOVEMENT"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
    }

    public static boolean isEnabled() {
        AutoJumpModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled()) {
            return false;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        return player != null && client.field_1687 != null && !player.method_31549().field_7479 && !player.method_5799();
    }
}

