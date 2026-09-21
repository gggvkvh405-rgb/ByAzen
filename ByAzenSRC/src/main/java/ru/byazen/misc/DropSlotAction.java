/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.InventoryAction;

public record DropSlotAction(int slot, boolean entireStack) implements InventoryAction
{
}

