/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_1309
 *  net.minecraft.class_1802
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.module.combat;

import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.byazen.config.ConfigSerializable;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.misc.AttackInvoker;
import ru.byazen.misc.ReachHelper;
import ru.byazen.misc.TargetFilter;
import ru.byazen.module.Module;
import ru.byazen.module.ModuleCategory;
import ru.byazen.setting.BooleanSetting;
import ru.byazen.setting.BooleanSettingBuilder;
import ru.byazen.setting.ModeSetting;
import ru.byazen.setting.ModeSettingBuilder;
import ru.byazen.setting.NumberSetting;
import ru.byazen.setting.NumberSettingBuilder;
import ru.byazen.util.Angle;
import ru.byazen.util.TargetSelector;

public class TridentClickerModule
extends Module
implements ConfigSerializable {
    private static final float RANGE = 3.0f;
    private static final long CLICK_INTERVAL_MS = 450L;
    private static final List<String> TARGET_TYPES = List.of("Players");
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("").withKeybind().toggle()).build();
    private final NumberSetting cps;
    private final ModeSetting sorting;
    private TargetSelector targetSelector;
    private long lastClickTime;

    public TridentClickerModule(EventBus eventBus) {
        super(eventBus, "trident_clicker", "Trident Clicker", "\u0417\u0430\u043a\u043b\u0438\u043a\u0438\u0432\u0430\u043d\u0438\u0435 \u0442\u0440\u0435\u0437\u0443\u0431\u0446\u0435\u043c", ModuleCategory.valueOf("COMBAT"), new String[0]);
        this.registerSetting(this.enabledSetting);
        this.cps = ((NumberSettingBuilder)NumberSetting.builder().range(5.0, 200.0).defaultValue(80.0).multiplier(1.0).precision(0).animationSpeed(20.0f).name("CPS").id("cps").description("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u043a\u043b\u0438\u043a\u043e\u0432 \u0432 \u0441\u0435\u043a\u0443\u043d\u0434\u0443")).build();
        this.registerSetting(this.cps);
        this.sorting = ((ModeSettingBuilder)ModeSetting.getModeSettingBuilder().options("Health", "Distance", "Crosshair").defaultOption("Distance").name("\u0421\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u043a\u0430").id("sorting").description("\u0421\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u043a\u0430 \u0446\u0435\u043b\u0435\u0439 \u0434\u043b\u044f \u0430\u0442\u0430\u043a\u0438")).build();
        this.registerSetting(this.sorting);
    }

    @Override
    protected void initialize() {
        this.targetSelector = new TargetSelector();
        this.listen(ClientTickEvent.class, event -> this.onTick());
    }

    private void onTick() {
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        if (!this.enabledSetting.isEnabled() || player == null || client.field_1687 == null) {
            this.targetSelector.update();
            return;
        }
        if (!player.method_6047().method_31574(class_1802.field_8547)) {
            this.targetSelector.update();
            return;
        }
        TargetFilter filter = new TargetFilter(TARGET_TYPES, null, 0);
        class_1309 target = this.targetSelector.process(filter, 3.0f, this.sorting.getSelectedOption(), true);
        if (target == null) {
            return;
        }
        Angle look = new Angle(player.method_36454(), player.method_36455());
        if (ReachHelper.raycastEntity((class_1297)target, look, 3.0f, true) != target) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastClickTime < 450L) {
            return;
        }
        AttackInvoker clicks = (AttackInvoker)client;
        int count = this.cps.getIntValue();
        for (int i = 0; i < count; ++i) {
            clicks.invokeAttack();
        }
        this.lastClickTime = now;
    }
}

