/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1661
 *  net.minecraft.class_1799
 *  net.minecraft.class_746
 */
package ru.byazen.misc;

import java.util.function.Predicate;
import net.minecraft.class_1661;
import net.minecraft.class_1799;
import net.minecraft.class_746;
import ru.byazen.misc.BundleUse;
import ru.byazen.misc.ClickPolicy;
import ru.byazen.misc.InventoryTask;
import ru.byazen.misc.SwapTiming;
import ru.byazen.misc.TaskFlag;
import ru.byazen.misc.TaskPriority;
import ru.byazen.util.InventoryController;

public final class Bundles {
    private Bundles() {
    }

    public static boolean contains(class_1661 inventory, Predicate<class_1799> predicate) {
        if (inventory == null) {
            return false;
        }
        for (int slot = 0; slot < inventory.method_5439(); ++slot) {
            if (!predicate.test(inventory.method_5438(slot))) continue;
            return true;
        }
        return false;
    }

    public static int[] findInBundle(class_1661 inventory, Predicate<class_1799> predicate) {
        if (inventory == null) {
            return null;
        }
        for (int slot = 0; slot < inventory.method_5439(); ++slot) {
            if (!predicate.test(inventory.method_5438(slot))) continue;
            return new int[]{slot, 0};
        }
        return null;
    }

    public static void useFromBundle(class_746 player, InventoryController inventory, String owner, int slot, int nestedSlot, boolean funtime, Runnable use) {
        if (inventory == null || player == null) {
            return;
        }
        inventory.submit(InventoryTask.builder().action(inventory.process2(slot < 9 ? slot + 36 : slot, player.method_31548().method_67532(), use, funtime ? SwapTiming.FUNTIME : SwapTiming.DEFAULT)).owner(owner).flag(TaskFlag.DEFAULT).policy(ClickPolicy.SILENT).priority(TaskPriority.NORMAL).build());
    }

    public static boolean useFromBundle(class_746 player, InventoryController inventory, String owner, int slot, int nestedSlot, int destinationSlot, boolean funtime) {
        Bundles.useFromBundle(player, inventory, owner, slot, nestedSlot, funtime, inventory::update3);
        return true;
    }

    public static BundleUse useFromBundle(class_746 player, InventoryController inventory, String owner, int slot, int nestedSlot, boolean funtime) {
        Bundles.useFromBundle(player, inventory, owner, slot, nestedSlot, funtime, inventory::update3);
        return new BundleUse(slot);
    }
}

