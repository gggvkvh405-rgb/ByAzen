/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1309
 *  net.minecraft.class_310
 */
package ru.byazen.module.combat;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.class_1309;
import net.minecraft.class_310;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.AttackWindowEvent;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.misc.AttackOptions;
import ru.byazen.misc.CritCondition;
import ru.byazen.misc.CrosshairTarget;
import ru.byazen.misc.PlayerChecks;
import ru.byazen.misc.RaycastMode;
import ru.byazen.misc.SprintResetMode;
import ru.byazen.misc.TargetFilter;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.setting.MultiSelectSettingBuilder;
import ru.byazen.setting.RangeSetting;
import ru.byazen.setting.RangeSettingBuilder;
import ru.byazen.util.AuraProcessor;
import ru.byazen.util.CriticalsHandler;

public class TriggerBotModule
extends Module
implements ConfigSerializable {
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final MultiSelectSetting targets;
    private final BooleanSetting criticalsOnly;
    private final BooleanSetting toggleSprint;
    private final BooleanSetting waterCriticals;
    private final BooleanSetting spaceOnly;
    private final BooleanSetting weaponOnly;
    private final BooleanSetting stopOnEat;
    private final ModeSetting attackMode;
    private final RangeSetting cps;
    private final ModeSetting sprintReset;
    private final BooleanSetting tpsSync;
    private AuraProcessor auraProcessor;
    private CrosshairTarget crosshairTarget;
    private CritCondition critCondition;
    private class_1309 target;
    private boolean attackReady;

    public TriggerBotModule(EventBus eventBus) {
        super(eventBus, "trigger_bot", "Trigger Bot", "\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0431\u044c\u0451\u0442 \u0442\u043e\u0433\u043e, \u043d\u0430 \u043a\u043e\u0433\u043e \u0432\u044b \u0441\u043c\u043e\u0442\u0440\u0438\u0442\u0435", ModuleCategory.valueOf("COMBAT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        MultiSelectSetting targetSetting = ((MultiSelectSettingBuilder)MultiSelectSetting.getMultiSelectSettingBuilder().options("Players", "Friends", "Bots", "Naked", "Invisibles", "Animals", "Mobs", "Villagers").selectAll(false).optionListEnabled(false).name("\u0426\u0435\u043b\u0438").id("targets").description("\u0422\u0438\u043f\u044b \u0446\u0435\u043b\u0435\u0439 \u0434\u043b\u044f \u0430\u0442\u0430\u043a\u0438").withKeybind()).build();
        targetSetting.setOptions(new String[0]);
        this.targets = targetSetting;
        this.registerSetting(targetSetting);
        this.criticalsOnly = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0422\u043e\u043b\u044c\u043a\u043e \u043a\u0440\u0438\u0442\u044b").id("criticals_only").description("\u0410\u0442\u0430\u043a\u0430 \u0442\u043e\u043b\u044c\u043a\u043e \u043a\u0440\u0438\u0442\u0438\u0447\u0435\u0441\u043a\u0438\u043c \u0443\u0440\u043e\u043d\u043e\u043c").withKeybind()).build();
        this.registerSetting(this.criticalsOnly);
        this.toggleSprint = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u0421\u0431\u0440\u043e\u0441 \u0441\u043f\u0440\u0438\u043d\u0442\u0430").id("toggle_sprint").description("\u0410\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0441\u0431\u0440\u0430\u0441\u044b\u0432\u0430\u0435\u0442 \u0441\u043f\u0440\u0438\u043d\u0442 \u043f\u0440\u0438 \u0430\u0442\u0430\u043a\u0435")).build();
        this.registerSetting(this.toggleSprint);
        this.waterCriticals = ((BooleanSettingBuilder)BooleanSetting.builder().value(true).defaultValue(false).name("\u041a\u0440\u0438\u0442\u044b \u0432 \u0432\u043e\u0434\u0435").id("water_criticals").description("\u041d\u0430\u043d\u0435\u0441\u0435\u043d\u0438\u0435 \u043a\u0440\u0438\u0442\u0438\u0447\u0435\u0441\u043a\u043e\u0433\u043e \u0443\u0440\u043e\u043d\u0430 \u0432 \u0432\u043e\u0434\u043e\u043f\u0430\u0434\u0435").visibleWhen(() -> this.criticalsOnly.isEnabled())).build();
        this.registerSetting(this.waterCriticals);
        this.spaceOnly = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0422\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0438 \u043f\u0440\u044b\u0436\u043a\u0435").id("space_only").description("\u0420\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u043f\u0440\u0438 \u0437\u0430\u0436\u0430\u0442\u043e\u043c \u043f\u0440\u043e\u0431\u0435\u043b\u0435").visibleWhen(() -> this.criticalsOnly.isEnabled())).build();
        this.registerSetting(this.spaceOnly);
        this.weaponOnly = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0422\u043e\u043b\u044c\u043a\u043e \u0441 \u043e\u0440\u0443\u0436\u0438\u0435\u043c").id("weapon_only").description("\u0420\u0430\u0431\u043e\u0442\u0430\u0435\u0442 \u0442\u043e\u043b\u044c\u043a\u043e \u0441 \u043c\u0435\u0447\u043e\u043c \u0438\u043b\u0438 \u0442\u043e\u043f\u043e\u0440\u043e\u043c \u0432 \u0440\u0443\u043a\u0430\u0445")).build();
        this.registerSetting(this.weaponOnly);
        this.stopOnEat = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("\u0421\u0442\u043e\u043f \u0432\u043e \u0432\u0440\u0435\u043c\u044f \u0435\u0434\u044b").id("stop_on_eat").description("\u041d\u0435 \u0430\u0442\u0430\u043a\u0443\u0435\u0442, \u043f\u043e\u043a\u0430 \u0432\u044b \u0435\u0434\u0438\u0442\u0435 \u0438\u043b\u0438 \u043f\u044c\u0451\u0442\u0435")).build();
        this.registerSetting(this.stopOnEat);
        this.attackMode = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("1.9+", "1.8").defaultOption("1.9+").name("\u0420\u0435\u0436\u0438\u043c \u0430\u0442\u0430\u043a\u0438").id("attack_mode").description("\u0420\u0435\u0436\u0438\u043c \u0430\u0442\u0430\u043a\u0438")).build();
        this.registerSetting(this.attackMode);
        this.cps = ((RangeSettingBuilder)RangeSetting.builder().range(5.0, 20.0).defaultNormalizedRange(0.4666666666666667, 0.8666666666666667).multiplier(1.0).precision(0).name("CPS").id("cps").description("\u0414\u0438\u0430\u043f\u0430\u0437\u043e\u043d \u0441\u043b\u0443\u0447\u0430\u0439\u043d\u044b\u0445 \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0439 CPS").visibleWhen(() -> "1.8".equals(this.attackMode.getSelectedOption()))).build();
        this.registerSetting(this.cps);
        this.sprintReset = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("FT", "Packet", "Semi-Legit", "Legit").defaultOption("Packet").name("\u0420\u0435\u0436\u0438\u043c \u0441\u0431\u0440\u043e\u0441\u0430 \u0441\u043f\u0440\u0438\u043d\u0442\u0430").id("sprint_reset").description("\u0420\u0435\u0436\u0438\u043c \u0441\u0431\u0440\u043e\u0441\u0430 \u0441\u043f\u0440\u0438\u043d\u0442\u0430").visibleWhen(() -> this.toggleSprint.isEnabled())).build();
        this.registerSetting(this.sprintReset);
        this.tpsSync = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("TPS Sync").id("tps_sync").description("\u0421\u0438\u043d\u0445\u0440\u043e\u043d\u0438\u0437\u0438\u0440\u0443\u0435\u0442 \u043a\u0443\u043b\u0434\u0430\u0443\u043d \u0441 TPS \u0441\u0435\u0440\u0432\u0435\u0440\u0430")).build();
        this.registerSetting(this.tpsSync);
    }

    @Override
    protected void initialize() {
        this.crosshairTarget = new CrosshairTarget();
        this.critCondition = new CritCondition();
        this.auraProcessor = new AuraProcessor(new CriticalsHandler());
        this.listen(ClientTickEvent.class, event -> this.onTick());
        this.listen(AttackWindowEvent.class, this::onAttackWindow);
    }

    private void onTick() {
        this.auraProcessor.update();
        if (!this.enabledSetting.isEnabled() || class_310.method_1551().field_1724 == null) {
            this.clearTarget();
            return;
        }
        if (this.weaponOnly.isEnabled() && !PlayerChecks.isHoldingWeapon()) {
            this.clearTarget();
            return;
        }
        if (this.stopOnEat.isEnabled() && PlayerChecks.isUsingItem()) {
            this.clearTarget();
            return;
        }
        if (!this.critCondition.process(this.criticalsOnly.isEnabled(), this.spaceOnly.isEnabled(), this.waterCriticals.isEnabled())) {
            this.clearTarget();
            return;
        }
        TargetFilter filter = new TargetFilter(this.targets.getSelectedOptions(), null, 0);
        class_1309 entity = this.crosshairTarget.process(filter);
        if (entity == null) {
            this.clearTarget();
            return;
        }
        this.target = entity;
        this.attackReady = true;
    }

    private void onAttackWindow(AttackWindowEvent event) {
        if (!this.enabledSetting.isEnabled() || !this.attackReady || this.target == null) {
            return;
        }
        this.auraProcessor.getHitCooldown().setBooleanType(event.isActive());
        this.auraProcessor.getHitCooldown().setBooleanType3(this.tpsSync.isEnabled());
        int minCps = this.cps.getLowerIntValue();
        int maxCps = this.cps.getUpperIntValue();
        int cps = minCps >= maxCps ? minCps : ThreadLocalRandom.current().nextInt(minCps, maxCps + 1);
        AttackOptions options = new AttackOptions(null, 0.0f, cps, false, this.toggleSprint.isEnabled(), SprintResetMode.process(this.sprintReset.getSelectedOption()), false, RaycastMode.VISIBLE, "1.8".equals(this.attackMode.getSelectedOption()), 100.0, false, false, false, 1.6f);
        if (this.auraProcessor.process(this.target, options)) {
            this.attackReady = false;
        }
    }

    private void clearTarget() {
        this.target = null;
        this.attackReady = false;
    }
}

