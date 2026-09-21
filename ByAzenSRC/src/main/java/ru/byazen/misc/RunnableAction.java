/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.InventoryAction;

public final class RunnableAction
implements InventoryAction {
    public final Runnable runnable;

    public RunnableAction(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable runnable() {
        return this.runnable;
    }
}

