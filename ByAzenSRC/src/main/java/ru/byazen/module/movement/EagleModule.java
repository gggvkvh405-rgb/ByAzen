/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_304
 *  net.minecraft.class_310
 *  net.minecraft.class_3675
 *  net.minecraft.class_3675$class_306
 *  net.minecraft.class_3675$class_307
 *  net.minecraft.class_3736
 *  net.minecraft.class_746
 */
package ru.byazen.module.movement;

import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675;
import net.minecraft.class_3736;
import net.minecraft.class_746;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.input.InputBindings;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;

public class EagleModule
extends Module
implements ConfigSerializable {
    private boolean autoSneaking;
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();

    public EagleModule(EventBus eventBus) {
        super(eventBus, "eagle", "Eagle", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0432\u043a\u043b\u044e\u0447\u0430\u0435\u0442 \u043f\u0440\u0438\u0441\u0435\u0434\u0430\u043d\u0438\u0435 \u043d\u0430 \u043a\u0440\u0430\u044e \u0431\u043b\u043e\u043a\u0430", ModuleCategory.valueOf("MOVEMENT"), new String[0]);
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onClientTick);
    }

    private void onClientTick(ClientTickEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            if (this.autoSneaking) {
                this.restoreSneak();
                this.autoSneaking = false;
            }
            return;
        }
        this.autoSneaking = true;
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (player == null || client.field_1687 == null) {
            return;
        }
        if (player.field_3913.field_54155.comp_3163() || !player.method_24828()) {
            return;
        }
        class_2338 pos = class_2338.method_49637((double)player.method_23317(), (double)(player.method_23318() - 0.1), (double)player.method_23321());
        class_2248 block = client.field_1687.method_8320(pos).method_26204();
        client.field_1690.field_1832.method_23481(block == class_2246.field_10124 || block instanceof class_3736);
    }

    private void restoreSneak() {
        class_304 sneak = class_310.method_1551().field_1690.field_1832;
        class_3675.class_306 key = class_3675.method_15981((String)sneak.method_1428());
        boolean pressed = key.method_1442() == class_3675.class_307.field_1672 ? InputBindings.isMouseButtonPressed(key.method_1444()) : InputBindings.isKeyPressed(key.method_1444());
        sneak.method_23481(pressed);
    }
}

