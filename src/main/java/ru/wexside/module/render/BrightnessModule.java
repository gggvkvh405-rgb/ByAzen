/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.module.render;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.BrightnessEvent;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public class BrightnessModule
extends Module
implements ConfigSerializable {
    private final BooleanSetting enabledSetting;
    private final NumberSetting strength;

    @Override
    protected void initialize() {
        this.listen(BrightnessEvent.class, gameEvent3 -> {
            if (!this.enabledSetting.isEnabled()) {
                return;
            }
            gameEvent3.setBrightness(this.strength.getFloatValue());
        });
    }

    public BrightnessModule(EventBus eventBus) {
        super(eventBus, "brightness", "Brightness", "\u0418\u0437\u043c\u0435\u043d\u044f\u0435\u0442 \u044f\u0440\u043a\u043e\u0441\u0442\u044c \u0432 \u043c\u0438\u0440\u0435", ModuleCategory.valueOf("RENDER"), new String[0]);
        NumberSetting numberSetting;
        BooleanSetting booleanSetting;
        this.enabledSetting = booleanSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(booleanSetting);
        this.strength = numberSetting = ((NumberSettingBuilder)NumberSetting.builder().range(0.1, 1.0).defaultValue(0.3).multiplier(1.0).precision(1).animationSpeed(20.0f).name("Strength").id("strength").description("\u0418\u043d\u0442\u0435\u043d\u0441\u0438\u0432\u043d\u043e\u0441\u0442\u044c")).build();
        this.registerSetting(numberSetting);
    }
}

