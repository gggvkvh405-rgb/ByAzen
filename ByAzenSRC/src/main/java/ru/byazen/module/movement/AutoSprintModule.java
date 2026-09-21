/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.module.movement;

import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;

public class AutoSprintModule
extends Module
implements ConfigSerializable {
    private static volatile AutoSprintModule instance;
    private final BooleanSetting enabledSetting;
    private final BooleanSetting keepSprint;
    private boolean suppressUntilNextTick;

    public AutoSprintModule(EventBus eventBus) {
        super(eventBus, "auto_sprint", "Auto Sprint", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0432\u043a\u043b\u044e\u0447\u0430\u0435\u0442 \u0441\u043f\u0440\u0438\u043d\u0442", ModuleCategory.valueOf("MOVEMENT"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
        this.keepSprint = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Keep Sprint").id("keep_sprint").description("\u0421\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0438\u0435 \u0441\u043f\u0440\u0438\u043d\u0442\u0430 \u043f\u0440\u0438 \u0430\u0442\u0430\u043a\u0435")).build();
        this.registerSetting(this.keepSprint);
    }

    @Override
    protected void initialize() {
    }

    public static boolean isEnabled() {
        AutoSprintModule module = instance;
        if (module == null || !module.enabledSetting.isEnabled()) {
            return false;
        }
        if (module.suppressUntilNextTick) {
            module.suppressUntilNextTick = false;
            return false;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return false;
        }
        if (!player.field_3913.method_20622() || player.method_6115() || player.method_5715()) {
            return false;
        }
        return module.canSprint(player);
    }

    public static boolean isEnabled2() {
        AutoSprintModule module = instance;
        return module != null && module.enabledSetting.isEnabled() && module.keepSprint.isEnabled();
    }

    public static void tick() {
        AutoSprintModule module = instance;
        if (module != null) {
            module.suppressUntilNextTick = true;
        }
    }

    private boolean canSprint(class_746 player) {
        if (player.method_5869()) {
            return true;
        }
        if (player.method_31549().field_7479) {
            return true;
        }
        return player.method_7344().method_7586() > 6;
    }
}

