/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.module.hud;

import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.event.WorldSessionEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public final class BetterChatModule
extends Module
implements ConfigSerializable {
    private static volatile BetterChatModule instance;
    private final BooleanSetting enabledSetting;
    private String lastMessage;
    private int repeatCount;

    public BetterChatModule(EventBus eventBus) {
        super(eventBus, "better_chat", "Better Chat", "\u0421\u0445\u043b\u043e\u043f\u044b\u0432\u0430\u0435\u0442 \u043f\u043e\u0432\u0442\u043e\u0440\u044f\u044e\u0449\u0438\u0435\u0441\u044f \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u0432 \u0447\u0430\u0442\u0435", ModuleCategory.valueOf("DISPLAY"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("Enabled").id("enabled").description("\u0421\u0445\u043b\u043e\u043f\u044b\u0432\u0430\u0435\u0442 \u043f\u043e\u0432\u0442\u043e\u0440\u044f\u044e\u0449\u0438\u0435\u0441\u044f \u0441\u043e\u043e\u0431\u0449\u0435\u043d\u0438\u044f \u0432 \u0447\u0430\u0442\u0435 \u0441\u043e \u0441\u0447\u0451\u0442\u0447\u0438\u043a\u043e\u043c").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(WorldSessionEvent.class, event -> {
            this.lastMessage = null;
            this.repeatCount = 0;
        });
    }

    public static int compute(String message) {
        BetterChatModule module = instance;
        if (module == null) {
            return 0;
        }
        if (module.enabledSetting.isEnabled() && !message.isEmpty() && message.equals(module.lastMessage)) {
            ++module.repeatCount;
            return module.repeatCount;
        }
        module.lastMessage = message;
        module.repeatCount = 1;
        return 0;
    }
}

