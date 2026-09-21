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
import ru.byazen.misc.AttackUrgency;
import ru.byazen.misc.CorrectionMode;
import ru.byazen.util.Angle;

public record RotationIntent(class_1309 target, class_243 aimPoint, Angle targetAngle, AttackUrgency urgency, CorrectionMode correction, boolean changeViewLook) {
    public static RotationIntent empty() {
        return new RotationIntent(null, null, null, AttackUrgency.NONE, CorrectionMode.NONE, false);
    }

    public boolean hasTarget() {
        return this.target != null;
    }

    public boolean hasCorrection() {
        return this.correction != CorrectionMode.NONE;
    }
}

