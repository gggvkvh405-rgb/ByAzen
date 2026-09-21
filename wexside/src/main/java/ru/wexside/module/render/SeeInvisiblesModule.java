/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.module.render;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public final class SeeInvisiblesModule
extends Module
implements ConfigSerializable {
    private final NumberSetting alpha;
    private final BooleanSetting enabledSetting;
    static volatile SeeInvisiblesModule seeInvisiblesModule2;

    @Override
    protected void initialize() {
    }

    public static boolean isEnabled() {
        SeeInvisiblesModule seeInvisiblesModule = seeInvisiblesModule2;
        return seeInvisiblesModule != null && seeInvisiblesModule.enabledSetting.isEnabled();
    }

    public static int getIntType() {
        SeeInvisiblesModule seeInvisiblesModule = seeInvisiblesModule2;
        float f = seeInvisiblesModule != null ? (float)seeInvisiblesModule.alpha.getValue() : 0.5f;
        int n = Math.max(0, Math.min(255, (int)(f * 255.0f)));
        return n << 24 | 0xFFFFFF;
    }

    public SeeInvisiblesModule(EventBus eventBus) {
        super(eventBus, "see_invisibles", "See Invisibles", "\u0412\u0438\u0434\u0435\u0442\u044c \u043d\u0435\u0432\u0438\u0434\u0438\u043c\u044b\u0445 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439", ModuleCategory.valueOf("RENDER"), new String[0]);
        NumberSetting numberSetting;
        BooleanSetting booleanSetting;
        seeInvisiblesModule2 = this;
        this.enabledSetting = booleanSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u0438\u0434\u0435\u0442\u044c \u043d\u0435\u0432\u0438\u0434\u0438\u043c\u044b\u0445 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0435\u0439").withKeybind().toggle()).build();
        this.registerSetting(booleanSetting);
        this.alpha = numberSetting = ((NumberSettingBuilder)NumberSetting.builder().range(10.0, 100.0).defaultValue(50.0).multiplier(0.01).precision(0).animationSpeed(20.0f).markers(10.0).snapTo(10.0).name("Alpha").id("alpha").description("\u041f\u0440\u043e\u0437\u0440\u0430\u0447\u043d\u043e\u0441\u0442\u044c \u043d\u0435\u0432\u0438\u0434\u0438\u043c\u043e\u0439 \u0441\u0443\u0449\u043d\u043e\u0441\u0442\u0438")).build();
        this.registerSetting(numberSetting);
    }
}

