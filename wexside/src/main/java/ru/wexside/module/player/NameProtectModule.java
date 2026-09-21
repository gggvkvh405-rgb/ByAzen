/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 */
package ru.wexside.module.player;

import net.minecraft.class_310;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.EventBus;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.TextSetting;
import ru.wexside.setting.TextSettingBuilder;

public final class NameProtectModule
extends Module
implements ConfigSerializable {
    private static volatile NameProtectModule instance;
    private final BooleanSetting enabledSetting;
    private final TextSetting fakeName;

    public NameProtectModule(EventBus eventBus) {
        super(eventBus, "name_protect", "Name Protect", "\u0412\u0438\u0437\u0443\u0430\u043b\u044c\u043d\u043e \u043c\u0435\u043d\u044f\u0435\u0442 \u0442\u0432\u043e\u0439 \u043d\u0438\u043a \u0432\u043e \u0432\u0441\u0451\u043c \u0442\u0435\u043a\u0441\u0442\u0435", ModuleCategory.valueOf("PLAYER"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0412\u043e\u0437\u043c\u043e\u0436\u043d\u043e\u0441\u0442\u044c \u0432\u0438\u0437\u0443\u0430\u043b\u044c\u043d\u043e \u043c\u0435\u043d\u044f\u0442\u044c \u043d\u0438\u043a").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.fakeName = ((TextSettingBuilder)TextSetting.getTextSettingBuilder().value("wexside").maxLength(32).name("\u041d\u0438\u043a").id("fake_name").description("\u041d\u0438\u043a\u043d\u0435\u0439\u043c, \u043a\u043e\u0442\u043e\u0440\u044b\u043c \u0437\u0430\u043c\u0435\u043d\u044f\u0435\u0442\u0441\u044f \u0442\u0432\u043e\u0439 \u043d\u0430\u0441\u0442\u043e\u044f\u0449\u0438\u0439")).build();
        this.registerSetting(this.fakeName);
    }

    @Override
    protected void initialize() {
    }

    public static boolean isEnabled() {
        NameProtectModule module = instance;
        return module != null && module.enabledSetting.isEnabled();
    }

    public static String compute(String text) {
        NameProtectModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled() || text == null || text.isEmpty()) {
            return text;
        }
        class_310 client = class_310.method_1551();
        if (client == null || client.method_1548() == null) {
            return text;
        }
        String username = client.method_1548().method_1676();
        if (username == null || username.isEmpty() || text.indexOf(username) < 0) {
            return text;
        }
        String replacement = module.fakeName.getValue();
        return text.replace(username, replacement == null ? "" : replacement);
    }

    public static String getString() {
        NameProtectModule module = instance;
        if (module == null) {
            return "";
        }
        String name = module.fakeName.getValue();
        return name == null ? "" : name;
    }
}

