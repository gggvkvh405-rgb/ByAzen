/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2246
 *  net.minecraft.class_2248
 *  net.minecraft.class_2338
 *  net.minecraft.class_310
 *  net.minecraft.class_636
 *  net.minecraft.class_746
 */
package ru.wexside.module.player;

import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_310;
import net.minecraft.class_636;
import net.minecraft.class_746;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.BlockBreakingAccessor;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.NumberSetting;
import ru.wexside.setting.NumberSettingBuilder;

public class FastBreakModule
extends Module
implements ConfigSerializable {
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final NumberSetting speed;

    public FastBreakModule(EventBus eventBus) {
        super(eventBus, "fast_break", "Fast Break", "\u0423\u0432\u0435\u043b\u0438\u0447\u0438\u0432\u0430\u0435\u0442 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043b\u043e\u043c\u0430\u043d\u0438\u044f \u0431\u043b\u043e\u043a\u043e\u0432", ModuleCategory.valueOf("PLAYER"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.speed = ((NumberSettingBuilder)NumberSetting.builder().range(10.0, 100.0).defaultValue(50.0).multiplier(1.0).precision(0).animationSpeed(20.0f).showMarkers().name("Speed").id("speed").description("\u0423\u0441\u043a\u043e\u0440\u0435\u043d\u0438\u0435 (\u0432 % \u043e\u0442 \u0438\u0437\u043d\u0430\u0447\u0430\u043b\u044c\u043d\u043e\u0439 \u0441\u043a\u043e\u0440\u043e\u0441\u0442\u0438)")).build();
        this.registerSetting(this.speed);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.setFloatType());
    }

    private void setFloatType() {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        class_636 interactions = client.field_1761;
        if (player == null || client.field_1687 == null || interactions == null) {
            return;
        }
        BlockBreakingAccessor breaking = (BlockBreakingAccessor)interactions;
        breaking.setBreakingCooldown(0);
        float speedBonus = (float)this.speed.getIntValue() / 100.0f;
        float progress = breaking.getBreakingProgress();
        if (this.blockAt(0.0, player.method_18798().field_1351, 0.0) != class_2246.field_10343 && !player.method_24828()) {
            breaking.setBreakingProgress(progress *= 5.0f);
            speedBonus -= 0.8f;
        }
        if (progress > 1.0f - speedBonus && progress < 0.99f) {
            breaking.setBreakingProgress(0.99f);
        }
    }

    private class_2248 blockAt(double offsetX, double offsetY, double offsetZ) {
        class_746 player = class_310.method_1551().field_1724;
        class_2338 pos = class_2338.method_49637((double)(player.method_23317() + offsetX), (double)(player.method_23318() + offsetY), (double)(player.method_23321() + offsetZ));
        return class_310.method_1551().field_1687.method_8320(pos).method_26204();
    }
}

