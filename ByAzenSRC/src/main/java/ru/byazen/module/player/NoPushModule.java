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
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.MultiSelectSettingBuilder;

public final class NoPushModule
extends Module
implements ConfigSerializable {
    public static final String PLAYERS = "Players";
    public static final String BLOCKS = "Blocks";
    public static final String WATER = "Water";
    private static volatile NoPushModule instance;
    private final BooleanSetting enabledSetting;
    private final MultiSelectSetting types;

    public NoPushModule(EventBus eventBus) {
        super(eventBus, "no_push", "No Push", "\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0443\u043c\u0435\u043d\u044c\u0448\u0430\u0442\u044c \u043e\u0442\u0442\u0430\u043b\u043a\u0438\u0432\u0430\u043d\u0438\u0435", ModuleCategory.valueOf("PLAYER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043a\u043b\u044e\u0447\u0438\u0442\u044c \u0443\u043c\u0435\u043d\u044c\u0448\u0435\u043d\u0438\u0435 \u043e\u0442\u0442\u0430\u043b\u043a\u0438\u0432\u0430\u043d\u0438\u044f").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        MultiSelectSetting typeSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options(PLAYERS, BLOCKS, WATER).selectAll(false).optionListEnabled(false).name("Types").id("types").description("\u0422\u0438\u043f\u044b \u043e\u0442\u0442\u0430\u043b\u043a\u0438\u0432\u0430\u043d\u0438\u044f")).build();
        typeSetting.setOptions(new String[0]);
        this.types = typeSetting;
        this.registerSetting(typeSetting);
    }

    @Override
    protected void initialize() {
    }

    public static boolean compute(String type) {
        NoPushModule module = instance;
        return module != null && module.enabledSetting.isEnabled() && module.types.getSelectedOptions().contains(type);
    }
}

