/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_2596
 *  net.minecraft.class_2848
 *  net.minecraft.class_2848$class_2849
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.misc;

import net.minecraft.class_1297;
import net.minecraft.class_2596;
import net.minecraft.class_2848;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.byazen.misc.PlayerShieldStateAccessor;
import ru.byazen.misc.SprintResetMode;
import ru.byazen.module.movement.AutoSprintModule;

public class ShieldDesync {
    private boolean restartSprintNextTick;
    private SprintResetMode mode = SprintResetMode.PACKET;

    public boolean sendsSprintPackets() {
        return this.mode.isActive();
    }

    public void restoreSprint() {
        if (!this.restartSprintNextTick) {
            return;
        }
        class_746 player = class_310.method_1551().field_1724;
        if (player != null && !player.method_6115() && !((PlayerShieldStateAccessor)player).isShieldUseForced()) {
            player.field_3944.method_52787((class_2596)new class_2848((class_1297)player, class_2848.class_2849.field_12981));
            player.method_5728(true);
            ((PlayerShieldStateAccessor)player).setShieldUseForced(true);
        }
        this.restartSprintNextTick = false;
    }

    public void prepareForAttack() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null || player.method_6115() || !player.method_5624()) {
            return;
        }
        this.stopSprinting(player);
        if (this.mode == SprintResetMode.LEGIT || this.mode == SprintResetMode.SEMI_LEGIT) {
            class_310.method_1551().field_1690.field_1867.method_23481(false);
            AutoSprintModule.tick();
        }
    }

    public void setMode(SprintResetMode mode) {
        this.mode = mode;
    }

    public boolean delaysAttackUntilSprintStops() {
        return this.mode.isAvailable();
    }

    private void stopSprinting(class_746 player) {
        if (!((PlayerShieldStateAccessor)player).isShieldUseForced()) {
            return;
        }
        player.method_5728(false);
        if (this.mode != SprintResetMode.LEGIT) {
            player.field_3944.method_52787((class_2596)new class_2848((class_1297)player, class_2848.class_2849.field_12985));
            ((PlayerShieldStateAccessor)player).setShieldUseForced(false);
        }
        this.restartSprintNextTick = true;
    }
}

