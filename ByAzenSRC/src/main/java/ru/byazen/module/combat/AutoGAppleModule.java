/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_1935
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.module.combat;

import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1935;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;

public class AutoGAppleModule
extends Module
implements ConfigSerializable {
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final NumberSetting health;
    private boolean eating;

    public AutoGAppleModule(EventBus eventBus) {
        super(eventBus, "auto_gapple", "Auto GApple", "\u0415\u0441\u0442 \u044f\u0431\u043b\u043e\u043a\u043e \u0438\u0437 \u043e\u0444\u0445\u0435\u043d\u0434\u0430 \u043f\u0440\u0438 \u043d\u0438\u0437\u043a\u043e\u043c HP", ModuleCategory.valueOf("COMBAT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.health = ((NumberSettingBuilder)NumberSetting.builder().range(4.0, 20.0).defaultValue(15.0).multiplier(1.0).precision(1).animationSpeed(20.0f).name("Health").id("health").description("\u041c\u0438\u043d\u0438\u043c\u0430\u043b\u044c\u043d\u044b\u0439 HP \u0434\u043b\u044f \u0430\u043a\u0442\u0438\u0432\u0430\u0446\u0438\u0438")).build();
        this.registerSetting(this.health);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        if (this.shouldEat(player)) {
            class_310.method_1551().field_1690.field_1904.method_23481(true);
            this.eating = true;
        } else if (this.eating) {
            class_310.method_1551().field_1690.field_1904.method_23481(false);
            this.eating = false;
        }
    }

    private boolean shouldEat(class_746 player) {
        if (!this.enabledSetting.isEnabled()) {
            return false;
        }
        if (!player.method_6079().method_31574(class_1802.field_8463)) {
            return false;
        }
        if (player.method_7357().method_7904(new class_1799((class_1935)class_1802.field_8463))) {
            return false;
        }
        return player.method_6032() + player.method_6067() <= this.health.getFloatValue();
    }
}

