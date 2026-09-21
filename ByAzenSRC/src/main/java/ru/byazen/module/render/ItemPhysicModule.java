/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.module.render;

import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;

public final class ItemPhysicModule
extends Module
implements ConfigSerializable {
    private final BooleanSetting enabledSetting;
    static volatile ItemPhysicModule itemPhysicModule2;

    @Override
    protected void initialize() {
    }

    public static boolean isEnabled() {
        ItemPhysicModule itemPhysicModule = itemPhysicModule2;
        return itemPhysicModule != null && itemPhysicModule.enabledSetting.isEnabled();
    }

    public ItemPhysicModule(EventBus eventBus) {
        super(eventBus, "item_physic", "Item Physic", "\u0424\u0438\u0437\u0438\u043a\u0430 \u0432\u044b\u043f\u0430\u0432\u0448\u0438\u0445 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432", ModuleCategory.valueOf("RENDER"), new String[0]);
        BooleanSetting booleanSetting;
        itemPhysicModule2 = this;
        this.enabledSetting = booleanSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0444\u0438\u0437\u0438\u043a\u0443 \u0432\u044b\u043f\u0430\u0432\u0448\u0438\u0445 \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u043e\u0432").withKeybind().toggle()).build();
        this.registerSetting(booleanSetting);
    }
}

