/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2596
 *  net.minecraft.class_2664
 *  net.minecraft.class_2743
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.module.combat;

import net.minecraft.class_2596;
import net.minecraft.class_2664;
import net.minecraft.class_2743;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.EventBus;
import ru.byazen.event.IncomingPacketEvent;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;

public class VelocityModule
extends Module
implements ConfigSerializable {
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final ModeSetting mode;

    public VelocityModule(EventBus eventBus) {
        super(eventBus, "velocity", "Velocity", "\u0423\u043c\u0435\u043d\u044c\u0448\u0430\u0435\u0442 \u043e\u0442\u0442\u0430\u043b\u043a\u0438\u0432\u0430\u043d\u0438\u0435 \u043e\u0442 \u0443\u0434\u0430\u0440\u043e\u0432", ModuleCategory.valueOf("COMBAT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.mode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Packet", "Legit").defaultOption("Packet").name("Mode").id("mode").description("\u041c\u0435\u0442\u043e\u0434 \u0443\u043c\u0435\u043d\u044c\u0448\u0435\u043d\u0438\u044f \u043e\u0442\u0442\u0430\u043b\u043a\u0438\u0432\u0430\u043d\u0438\u044f")).build();
        this.registerSetting(this.mode);
    }

    @Override
    protected void initialize() {
        this.listen(IncomingPacketEvent.class, this::onIncomingPacket);
    }

    private void onIncomingPacket(IncomingPacketEvent event) {
        class_2743 velocity;
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        class_2596<?> packet = event.getPacket();
        if ("Packet".equals(this.mode.getSelectedOption())) {
            class_2743 velocity2;
            if (packet instanceof class_2743 && (velocity2 = (class_2743)packet).method_11818() == player.method_5628()) {
                event.update();
            } else if (packet instanceof class_2664) {
                event.update();
            }
            return;
        }
        if ("Legit".equals(this.mode.getSelectedOption()) && packet instanceof class_2743 && (velocity = (class_2743)packet).method_11818() == player.method_5628()) {
            player.method_18799(player.method_18798().method_18805(0.6, 1.0, 0.6));
        }
    }
}

