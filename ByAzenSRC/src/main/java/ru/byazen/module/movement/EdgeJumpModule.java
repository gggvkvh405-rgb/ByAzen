/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.module.movement;

import net.minecraft.class_1297;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BindSetting;
import ru.byazen.setting.BindSettingBuilder;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;

public class EdgeJumpModule
extends Module
implements ConfigSerializable {
    private final BindSetting key;
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();

    public EdgeJumpModule(EventBus eventBus) {
        super(eventBus, "edge_jump", "Edge Jump", "\u041f\u0440\u044b\u0436\u043e\u043a \u0441 \u043a\u0440\u0430\u044f \u0431\u043b\u043e\u043a\u0430", ModuleCategory.valueOf("MOVEMENT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.key = ((BindSettingBuilder)BindSetting.getBindSettingBuilder().keyboard(0).name("Key").id("key").description("\u041a\u043b\u0430\u0432\u0438\u0448\u0430 \u0434\u043b\u044f \u0430\u043a\u0442\u0438\u0432\u0430\u0446\u0438\u0438 \u043f\u0440\u044b\u0436\u043a\u0430")).build();
        this.registerSetting(this.key);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onClientTick);
    }

    private void onClientTick(ClientTickEvent event) {
        if (!this.enabledSetting.isEnabled() || !this.key.isPressed()) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (player == null || client.field_1687 == null) {
            return;
        }
        if (this.atEdge(player)) {
            player.method_6043();
        }
    }

    private boolean atEdge(class_746 player) {
        class_310 client = class_310.method_1551();
        return player.method_24828() && !client.field_1690.field_1903.method_1434() && client.field_1687.method_8587((class_1297)player, player.method_5829().method_989(0.0, -0.99, 0.0));
    }
}

