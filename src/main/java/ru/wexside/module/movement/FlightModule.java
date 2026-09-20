/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1268
 *  net.minecraft.class_1309
 *  net.minecraft.class_1799
 *  net.minecraft.class_2338
 *  net.minecraft.class_2350
 *  net.minecraft.class_2374
 *  net.minecraft.class_2382
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_3965
 *  net.minecraft.class_636
 *  net.minecraft.class_638
 *  net.minecraft.class_746
 */
package ru.wexside.module.movement;

import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_1799;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2374;
import net.minecraft.class_2382;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3965;
import net.minecraft.class_636;
import net.minecraft.class_638;
import net.minecraft.class_746;
import ru.wexside.WexSideClient;
import ru.wexside.config.ConfigSerializable;
import ru.wexside.event.ClientTickEvent;
import ru.wexside.event.EventBus;
import ru.wexside.misc.AttackUrgency;
import ru.wexside.misc.CorrectionMode;
import ru.wexside.module.Module;
import ru.wexside.module.ModuleCategory;
import ru.wexside.module.ModuleManager;
import ru.wexside.module.combat.AttackAuraModule;
import ru.wexside.setting.BooleanSetting;
import ru.wexside.setting.BooleanSettingBuilder;
import ru.wexside.setting.Setting;
import ru.wexside.util.Angle;
import ru.wexside.util.RotationController;
import ru.wexside.util.RotationIntent;

public final class FlightModule
extends Module
implements ConfigSerializable {
    private static final long PLACE_INTERVAL_MS = 20L;
    private final BooleanSetting enabledSetting = ((BooleanSettingBuilder)BooleanSetting.builder().value(false).defaultValue(false).name("Enabled").id("enabled").description("\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u043b\u0435\u0442\u0430\u0442\u044c \u043d\u0430 FT (\u0442\u0440\u0435\u0431\u0443\u0435\u0442\u0441\u044f \u0433\u043e\u043b\u043e\u0432\u0430 \u0432 \u0440\u0443\u043a\u0435)").withKeybind().toggle()).build();
    private long lastPlaceTime;
    private class_2338 columnPos;

    public FlightModule(EventBus eventBus) {
        super(eventBus, "flight", "Flight", "\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u043b\u0435\u0442\u0430\u0442\u044c \u043d\u0430 FT (\u0442\u0440\u0435\u0431\u0443\u0435\u0442\u0441\u044f \u0433\u043e\u043b\u043e\u0432\u0430 \u0432 \u0440\u0443\u043a\u0435)", ModuleCategory.valueOf("MOVEMENT"), new String[0]);
        this.registerSetting(this.enabledSetting);
    }

    @Override
    protected void initialize() {
        this.listen(ClientTickEvent.class, this::onClientTick);
    }

    private void onClientTick(ClientTickEvent event) {
        if (!this.enabledSetting.isEnabled()) {
            this.columnPos = null;
            this.releaseLook();
            return;
        }
        this.disableAura();
        class_310 client = class_310.method_1551();
        class_746 player = client.field_1724;
        class_638 world = client.field_1687;
        if (player == null || world == null) {
            return;
        }
        class_1799 stack = player.method_6047();
        if (stack.method_7960()) {
            this.releaseLook();
            return;
        }
        if (this.columnPos == null) {
            class_2350 facing = player.method_5735();
            class_243 ahead = new class_243(player.method_23317(), player.method_23318(), player.method_23321()).method_1031((double)facing.method_10148(), 0.0, (double)facing.method_10165());
            this.columnPos = class_2338.method_49638((class_2374)ahead);
        }
        boolean placed = false;
        class_2338 pos = this.columnPos;
        while (pos.method_10264() < world.method_31607() + world.method_31605()) {
            if (world.method_8320(pos).method_26215() && !world.method_8320(pos.method_10074()).method_26215()) {
                long now = System.currentTimeMillis();
                if (now - this.lastPlaceTime < 20L) break;
                class_243 hitPos = class_243.method_24953((class_2382)pos);
                class_3965 hit = new class_3965(hitPos, class_2350.field_11036, pos.method_10074(), false);
                this.applyLook(player, Angle.fromVectors(player.method_33571(), hitPos));
                class_636 interactions = client.field_1761;
                if (interactions != null) {
                    interactions.method_2896(player, class_1268.field_5808, hit);
                }
                this.lastPlaceTime = now;
                placed = true;
                break;
            }
            pos = pos.method_10084();
        }
        if (!placed) {
            this.releaseLook();
        }
    }

    private void applyLook(class_746 player, Angle angle) {
        RotationController rotations = WexSideClient.getRotationController();
        if (rotations == null) {
            return;
        }
        RotationIntent intent = new RotationIntent((class_1309)player, null, angle, AttackUrgency.HIT, CorrectionMode.FOCUSED, false);
        rotations.process2(intent, "Simple");
    }

    private void releaseLook() {
        RotationController rotations = WexSideClient.getRotationController();
        if (rotations == null || !rotations.isActive()) {
            return;
        }
        RotationIntent intent = rotations.empty();
        if (intent != null && intent.target() == class_310.method_1551().field_1724) {
            rotations.update3();
        }
    }

    private void disableAura() {
        ModuleManager moduleManager = WexSideClient.getInstance().getModuleManager();
        if (moduleManager == null) {
            return;
        }
        AttackAuraModule attackAura = moduleManager.getModule(AttackAuraModule.class);
        if (attackAura == null || !attackAura.isActive()) {
            return;
        }
        Setting toggle = attackAura.getToggleSetting();
        if (toggle instanceof BooleanSetting) {
            BooleanSetting enabled = (BooleanSetting)toggle;
            enabled.setEnabled(false);
        }
    }
}

