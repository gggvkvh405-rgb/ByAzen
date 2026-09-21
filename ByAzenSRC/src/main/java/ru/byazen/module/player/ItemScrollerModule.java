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

public final class ItemScrollerModule
extends Module
implements ConfigSerializable {
    private static volatile ItemScrollerModule instance;
    private final BooleanSetting enabledSetting;
    private final NumberSetting delay;
    private long lastScrollTime;

    public ItemScrollerModule(EventBus eventBus) {
        super(eventBus, "item_scroller", "Item Scroller", "\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u043f\u0435\u0440\u0435\u043c\u0435\u0449\u0430\u0442\u044c \u043f\u0440\u0435\u0434\u043c\u0435\u0442\u044b \u0441 \u0437\u0430\u0436\u0430\u0442\u043e\u0439 \u043a\u043b\u0430\u0432\u0438\u0448\u0435\u0439 \u041b\u041a\u041c", ModuleCategory.valueOf("PLAYER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.delay = ((NumberSettingBuilder)NumberSetting.builder().range(0.0, 1000.0).defaultValue(100.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("Delay").id("delay").description("\u0417\u0430\u0434\u0435\u0440\u0436\u043a\u0430 \u043c\u0435\u0436\u0434\u0443 \u043f\u0435\u0440\u0435\u043c\u0435\u0449\u0435\u043d\u0438\u044f\u043c\u0438")).build();
        this.registerSetting(this.delay);
    }

    @Override
    protected void initialize() {
    }

    public static boolean isEnabled() {
        ItemScrollerModule module = instance;
        return module != null && module.enabledSetting.isEnabled();
    }

    public static boolean isEnabled2() {
        ItemScrollerModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled()) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (now - module.lastScrollTime >= (long)module.delay.getIntValue()) {
            module.lastScrollTime = now;
            return true;
        }
        return false;
    }
}

