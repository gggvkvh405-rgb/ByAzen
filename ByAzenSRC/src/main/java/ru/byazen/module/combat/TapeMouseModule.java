/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_310
 */
package ru.byazen.module.combat;

import net.minecraft.class_310;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.AttackWindowEvent;
import ru.byazen.event.EventBus;
import ru.byazen.misc.AttackInvoker;
import ru.byazen.misc.HitCooldown;
import ru.byazen.misc.PlayerChecks;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;

public class TapeMouseModule
extends Module
implements ConfigSerializable {
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final BooleanSetting tpsSync;
    private final BooleanSetting press;
    private final BooleanSetting stopOnEat;
    private final BooleanSetting weaponOnly;
    private HitCooldown hitCooldown;

    public TapeMouseModule(EventBus eventBus) {
        super(eventBus, "tape_mouse", "Tape Mouse", "\u0411\u0435\u0441\u043f\u0435\u0440\u0435\u0440\u044b\u0432\u043d\u0430\u044f \u0430\u0442\u0430\u043a\u0430", ModuleCategory.valueOf("COMBAT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.tpsSync = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("TPS Sync").id("tps_sync").description("\u0421\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0438\u0440\u0443\u0435\u0442 \u0440\u0430\u0431\u043e\u0442\u0443 \u043c\u043e\u0434\u0443\u043b\u044f \u0441 TPS \u0441\u0435\u0440\u0432\u0435\u0440\u0430")).build();
        this.registerSetting(this.tpsSync);
        this.press = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u041f\u0440\u0438 \u0437\u0430\u0436\u0430\u0442\u0438\u0438").id("press").description("\u0420\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0438 \u0437\u0430\u0436\u0430\u0442\u043e\u0439 \u041b\u041a\u041c")).build();
        this.registerSetting(this.press);
        this.stopOnEat = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0421\u0442\u043e\u043f \u0432\u043e \u0432\u0440\u0435\u043c\u044f \u0435\u0434\u044b").id("stop_on_eat").description("\u041d\u0435 \u0430\u0442\u0430\u043a\u0443\u0435\u0442, \u043f\u043e\u043a\u0430 \u0432\u044b \u0435\u0434\u0438\u0442\u0435 \u0438\u043b\u0438 \u043f\u044c\u0451\u0442\u0435")).build();
        this.registerSetting(this.stopOnEat);
        this.weaponOnly = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0422\u043e\u043b\u044c\u043a\u043e \u0441 \u043e\u0440\u0443\u0436\u0438\u0435\u043c").id("weapon_only").description("\u0420\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u0441 \u043c\u0435\u0447\u043e\u043c \u0438\u043b\u0438 \u0442\u043e\u043f\u043e\u0440\u043e\u043c \u0432 \u0440\u0443\u043a\u0430\u0445")).build();
        this.registerSetting(this.weaponOnly);
    }

    @Override
    protected void initialize() {
        this.hitCooldown = new HitCooldown();
        this.listen(AttackWindowEvent.class, this::onAttackWindow);
    }

    private void onAttackWindow(AttackWindowEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            return;
        }
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || client.field_1687 == null) {
            return;
        }
        if (this.weaponOnly.isEnabled() && !PlayerChecks.isHoldingWeapon()) {
            return;
        }
        if (this.stopOnEat.isEnabled() && PlayerChecks.isUsingItem()) {
            return;
        }
        if (this.press.isEnabled() && !client.field_1690.field_1886.method_1434()) {
            return;
        }
        this.hitCooldown.setBooleanType(event.isActive());
        this.hitCooldown.setBooleanType3(this.tpsSync.isEnabled());
        if (!this.hitCooldown.process(false)) {
            return;
        }
        ((AttackInvoker)client).invokeAttack();
        this.hitCooldown.setLongType(500L);
    }
}

