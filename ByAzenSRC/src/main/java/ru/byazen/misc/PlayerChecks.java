/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 *  net.minecraft.class_1802
 *  net.minecraft.class_310
 *  net.minecraft.class_3489
 *  net.minecraft.class_746
 */
package ru.byazen.misc;

import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_310;
import net.minecraft.class_3489;
import net.minecraft.class_746;

public final class PlayerChecks {
    private PlayerChecks() {
    }

    public static boolean isHoldingWeapon() {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null) {
            return false;
        }
        class_1799 stack = player.method_6047();
        return stack.method_31573(class_3489.field_42611) || stack.method_31573(class_3489.field_42612) || stack.method_31574(class_1802.field_49814) || stack.method_31574(class_1802.field_8547);
    }

    public static boolean isUsingItem() {
        class_746 player = class_310.method_1551().field_1724;
        return player != null && player.method_6115();
    }
}

