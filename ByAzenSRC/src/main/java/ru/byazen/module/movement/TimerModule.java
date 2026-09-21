/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.module.movement;

import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;

public class TimerModule
extends Module
implements ConfigSerializable {
    private static volatile TimerModule instance;
    private final BooleanSetting enabledSetting;
    private final NumberSetting speed;

    public TimerModule(EventBus eventBus) {
        super(eventBus, "timer", "Timer", "\u0418\u0437\u043c\u0435\u043d\u044f\u0435\u0442 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0442\u0430\u0439\u043c\u0435\u0440\u0430", ModuleCategory.valueOf("MOVEMENT"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.speed = ((NumberSettingBuilder)NumberSetting.builder().range(0.01, 5.0).defaultValue(1.1).multiplier(1.0).precision(2).animationSpeed(20.0f).name("Speed").id("speed").description("\u041c\u043d\u043e\u0436\u0438\u0442\u0435\u043b\u044c \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u0438 \u0442\u0430\u0439\u043c\u0435\u0440\u0430").visibleWhen(this.enabledSetting::isEnabled)).build();
        this.registerSetting(this.speed);
    }

    @Override
    protected void initialize() {
    }

    public static float getFloatType() {
        TimerModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled()) {
            return 1.0f;
        }
        return (float)Math.max(0.01, module.speed.getValue());
    }
}

