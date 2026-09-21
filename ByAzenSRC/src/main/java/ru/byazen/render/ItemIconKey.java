/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 */
package ru.byazen.render;

import net.minecraft.class_1799;

public final class ItemIconKey {
    private final class_1799 stack;

    public ItemIconKey(class_1799 stack) {
        this.stack = stack == null || stack.method_7960() ? class_1799.field_8037 : stack.method_7972();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object object) {
        if (!(object instanceof ItemIconKey)) return false;
        ItemIconKey other = (ItemIconKey)object;
        if (!class_1799.method_7973((class_1799)this.stack, (class_1799)other.stack)) return false;
        return true;
    }

    public int hashCode() {
        return 31 * System.identityHashCode(this.stack.method_7909()) + this.stack.method_57353().hashCode();
    }
}

