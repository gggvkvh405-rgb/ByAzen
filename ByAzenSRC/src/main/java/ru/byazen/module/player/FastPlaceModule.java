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
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;

public final class FastPlaceModule
extends Module
implements ConfigSerializable {
    private static final int VANILLA_DELAY = 4;
    private static volatile FastPlaceModule instance;
    private final BooleanSetting enabledSetting;
    private final NumberSetting delay;

    public FastPlaceModule(EventBus eventBus) {
        super(eventBus, "fast_place", "Fast Place", "\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0431\u044b\u0441\u0442\u0440\u043e \u0441\u0442\u0430\u0432\u0438\u0442\u044c \u0431\u043b\u043e\u043a\u0438", ModuleCategory.valueOf("PLAYER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.delay = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 4.0).defaultValue(1.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Delay").id("delay").description("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0436\u0434\u0443 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043a\u043e\u0439 \u0431\u043b\u043e\u043a\u043e\u0432")).build();
        this.registerSetting(this.delay);
    }

    @Override
    protected void initialize() {
    }

    public static int getIntType() {
        FastPlaceModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled()) {
            return 4;
        }
        return module.delay.getIntValue();
    }
}

