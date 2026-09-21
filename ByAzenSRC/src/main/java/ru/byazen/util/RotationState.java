/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1309
 *  net.minecraft.class_243
 */
package ru.byazen.util;

import net.minecraft.class_1309;
import net.minecraft.class_243;
import ru.byazen.util.Angle;

public record RotationState(Angle appliedAngle, Angle previousAngle, Angle serverAngle, Angle playerAngle, class_1309 target, class_243 targetPosition, int ticksSinceAttack, int ticksSinceHit, float attackCooldown, boolean attackReady) {
    public Angle getAppliedAngle() {
        return this.appliedAngle;
    }

    public Angle getPlayerAngle() {
        return this.playerAngle;
    }

    public class_1309 getTarget() {
        return this.target;
    }

    public int getTicksSinceAttack() {
        return this.ticksSinceAttack;
    }

    public int getTicksSinceHit() {
        return this.ticksSinceHit;
    }

    public boolean isAttackReady() {
        return this.attackReady;
    }
}

