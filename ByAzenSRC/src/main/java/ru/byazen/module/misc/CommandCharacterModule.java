/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.module.misc;

import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.TextSetting;
import ru.byazen.setting.TextSettingBuilder;

public final class CommandCharacterModule
extends Module
implements ConfigSerializable {
    private static final String DEFAULT = ".";
    private static volatile CommandCharacterModule instance;
    private final TextSetting character;

    public CommandCharacterModule(EventBus eventBus) {
        super(eventBus, "command_character", "Command Character", "\u0421\u0432\u043e\u0439 \u0441\u0438\u043c\u0432\u043e\u043b \u0434\u043b\u044f \u043a\u043e\u043c\u0430\u043d\u0434 \u043a\u043b\u0438\u0435\u043d\u0442\u0430", ModuleCategory.valueOf("MISC"), new String[0]);
        instance = this;
        this.character = ((TextSettingBuilder)TextSetting.getTextSettingBuilder().value(DEFAULT).maxLength(1).name("Character").id("character").description("\u0421\u0438\u043c\u0432\u043e\u043b, \u0441 \u043a\u043e\u0442\u043e\u0440\u043e\u0433\u043e \u043d\u0430\u0447\u0438\u043d\u0430\u044e\u0442\u0441\u044f \u043a\u043e\u043c\u0430\u043d\u0434\u044b \u043a\u043b\u0438\u0435\u043d\u0442\u0430")).build();
        this.registerSetting(this.character);
    }

    @Override
    protected void initialize() {
    }

    public static String getString() {
        CommandCharacterModule module = instance;
        if (module == null) {
            return DEFAULT;
        }
        String value = module.character.getValue();
        return value == null || value.isBlank() ? DEFAULT : value;
    }
}

