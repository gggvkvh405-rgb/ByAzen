/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1531
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package ru.byazen.module.movement;

import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1531;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_638;
import net.minecraft.class_746;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;

public final class SpeedModule
extends Module
implements ConfigSerializable {
    public static volatile float value;
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0435\u043c\u0435\u0449\u0435\u043d\u0438\u044f").withKeybind().toggle()).build();
    private final ModeSetting mode;

    public SpeedModule(EventBus eventBus) {
        super(eventBus, "speed", "Speed", "\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0435\u043c\u0435\u0449\u0435\u043d\u0438\u044f", ModuleCategory.valueOf("MOVEMENT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Sticky", "Collision", "Sneak").defaultOption("Sticky").name("Mode").id("mode").description("\u041c\u0435\u0442\u043e\u0434 \u0443\u0441\u043a\u043e\u0440\u0435\u043d\u0438\u044f")).build();
        this.registerSetting(this.mode);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onClientTick);
    }

    private void onClientTick(ClientTickEvent event) {
        value = 0.0f;
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (player == null || client.field_1687 == null) {
            return;
        }
        switch (this.mode.getSelectedOption()) {
            case "Sneak": {
                this.applySneak(player);
                break;
            }
            case "Collision": {
                this.applyCollision(player, client.field_1687);
                break;
            }
            case "Sticky": {
                this.applySticky(player, client.field_1687);
            }
        }
    }

    private void applySticky(class_746 player, class_638 world) {
        class_238 box = player.method_5829().method_1014(0.3);
        int nearby = 0;
        for (class_1297 entity : world.method_18112()) {
            if (entity instanceof class_746 || !(entity instanceof class_1309) || entity instanceof class_1531 || !entity.method_5829().method_994(box)) continue;
            ++nearby;
        }
        if (nearby > 0) {
            double yaw = Math.toRadians(player.method_36454());
            double boost = 0.08 * (double)nearby;
            class_243 velocity = player.method_18798();
            player.method_18800(velocity.field_1352 + -Math.sin(yaw) * boost, velocity.field_1351, velocity.field_1350 + Math.cos(yaw) * boost);
        }
    }

    private void applyCollision(class_746 player, class_638 world) {
        class_238 box = player.method_5829().method_1014(0.1);
        int armorStands = 0;
        int living = 0;
        for (class_1297 entity : world.method_18112()) {
            if (entity.method_31481() || !entity.method_5829().method_994(box)) continue;
            if (entity instanceof class_1531) {
                ++armorStands;
            }
            if (!(entity instanceof class_1309)) continue;
            ++living;
        }
        if (armorStands <= 1 && living <= 1) {
            return;
        }
        if (player.method_24828()) {
            return;
        }
        value = armorStands > 1 ? 1.0f / (float)armorStands : 0.16f;
    }

    private void applySneak(class_746 player) {
        if (player.method_6115()) {
            class_243 velocity = player.method_18798();
            player.method_18800(velocity.field_1352 * 1.5, velocity.field_1351, velocity.field_1350 * 1.5);
        }
    }
}

