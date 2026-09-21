/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.misc;

import net.minecraft.class_1799;
import net.minecraft.class_310;
import net.minecraft.class_746;

public final class HotbarTracker {
    public class_1799 simulate(int ticksAhead) {
        class_746 player = class_310.method_1551().field_1724;
        if (player == null || ticksAhead <= 0 || !player.method_6115()) {
            return null;
        }
        class_1799 activeItem = player.method_6030();
        return activeItem.method_7960() ? null : activeItem.method_7972();
    }

    public class_1799 process(int ticksAhead) {
        return this.simulate(ticksAhead);
    }
}

