/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 *  net.minecraft.class_2828$class_2831
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.wexside.module.movement;

import net.minecraft.class_243;
import net.minecraft.class_2828;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.event.OutgoingPacketEvent;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;

public class AirStuckModule
extends Module
implements ConfigSerializable {
    private static volatile AirStuckModule instance;
    private final BooleanSetting enabledSetting;

    public AirStuckModule(EventBus eventBus) {
        super(eventBus, "air_stuck", "Air Stuck", "\u041e\u0441\u0442\u0430\u043d\u043e\u0432\u043a\u0430 \u0432 \u0432\u043e\u0437\u0434\u0443\u0445\u0435", ModuleCategory.valueOf("MOVEMENT"), new String[0]);
        instance = this;
        this.enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onClientTick);
        this.listen(OutgoingPacketEvent.class, this::onOutgoingPacket);
    }

    private void onClientTick(ClientTickEvent event) {
        class_746 player = class_310.method_1551().field_1724;
        if (this.isStuck(player)) {
            player.method_18799(class_243.field_1353);
        }
    }

    private void onOutgoingPacket(OutgoingPacketEvent event) {
        if (!this.isStuck(class_310.method_1551().field_1724)) {
            return;
        }
        if (event.getPacket() instanceof class_2828.class_2831) {
            event.update();
        }
    }

    private boolean isStuck(class_746 player) {
        return this.enabledSetting.isEnabled() && player != null && !player.method_24828();
    }

    public static boolean compute2(class_746 player) {
        AirStuckModule module = instance;
        return module != null && module.isStuck(player);
    }
}

