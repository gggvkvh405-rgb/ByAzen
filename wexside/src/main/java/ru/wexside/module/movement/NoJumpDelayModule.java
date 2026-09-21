/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.wexside.module.movement;

import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.LivingEntityStateAccessor;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.module.combat.AttackAuraModule;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.Setting;

public class NoJumpDelayModule
extends Module
implements ConfigSerializable {
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private AttackAuraModule attackAura;

    public NoJumpDelayModule(EventBus eventBus) {
        super(eventBus, "no_jump_delay", "No Jump Delay", "\u0423\u0431\u0438\u0440\u0430\u0435\u0442 \u0437\u0430\u0434\u0435\u0440\u0436\u043a\u0443 \u043c\u0435\u0436\u0434\u0443 \u043f\u0440\u044b\u0436\u043a\u0430\u043c\u0438", ModuleCategory.valueOf("MOVEMENT"), new String[0]);
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        if (!this.enabledSetting.isEnabled() || this.auraEnabled()) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return;
        }
        ((LivingEntityStateAccessor)player).setJumpingCooldown(0);
    }

    private boolean auraEnabled() {
        BooleanSetting enabled;
        if (this.attackAura == null && WexSideClient.getInstance() != null && WexSideClient.getInstance().getModuleManager() != null) {
            this.attackAura = WexSideClient.getInstance().getModuleManager().getModules().stream().filter(AttackAuraModule.class::isInstance).map(AttackAuraModule.class::cast).findFirst().orElse(null);
        }
        if (this.attackAura == null) {
            return false;
        }
        Setting toggle = this.attackAura.getToggleSetting();
        return toggle instanceof BooleanSetting && (enabled = (BooleanSetting)toggle).isEnabled();
    }
}

