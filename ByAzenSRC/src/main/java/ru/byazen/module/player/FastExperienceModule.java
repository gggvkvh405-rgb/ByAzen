/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1779
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.module.player;

import net.minecraft.class_1268;
import net.minecraft.class_1779;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.misc.ItemUseCooldownAccessor;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;

public final class FastExperienceModule
extends Module
implements ConfigSerializable {
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();

    public FastExperienceModule(EventBus eventBus) {
        super(eventBus, "fast_experience", "Fast Experience", "\u0423\u0441\u043a\u043e\u0440\u044f\u0435\u0442 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043f\u0443\u0437\u044b\u0440\u044c\u043a\u043e\u0432 \u043e\u043f\u044b\u0442\u0430", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        for (class_1268 hand : class_1268.values()) {
            class_1799 stack = player.method_5998(hand);
            if (!(stack.method_7909() instanceof class_1779) && !stack.method_31574(class_1802.field_8287)) continue;
            ((ItemUseCooldownAccessor)class_310.method_1551()).setItemUseCooldown(0);
            return;
        }
    }
}

