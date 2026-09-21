/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1802
 *  net.minecraft.class_746
 */
package ru.byazen.misc;

import net.minecraft.class_1802;
import net.minecraft.class_746;
import ru.byazen.misc.HitCooldown;
import ru.byazen.misc.ShieldBreaker;

public final class MaceCheck {
    public boolean isHoldingMace(class_746 player) {
        return this.process(player);
    }

    public boolean process(class_746 player2) {
        return player2.method_6047().method_31574(class_1802.field_49814);
    }

    public boolean process2(class_746 player2, HitCooldown hitCooldown, float f, boolean bl) {
        boolean bl2 = player2.method_24828();
        boolean bl3 = player2.method_6101();
        double d = player2.field_6017;
        if (d > (double)f && !bl3 && !bl2) {
            return hitCooldown.process2(bl, 0.0f);
        }
        if (bl2 || bl3) {
            return false;
        }
        if (!hitCooldown.isActive()) {
            return false;
        }
        if (this.process3(f)) {
            return false;
        }
        return hitCooldown.process(bl);
    }

    private boolean process3(float f) {
        return ShieldBreaker.isActive3() && !ShieldBreaker.isAvailable() && ShieldBreaker.getDoubleType() > (double)f;
    }
}

