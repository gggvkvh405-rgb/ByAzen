/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 */
package ru.byazen.event;

import net.minecraft.class_1799;
import ru.byazen.event.CancellableEvent;
import ru.byazen.event.Event;

public class ItemHoverEvent
extends CancellableEvent
implements Event {
    private final class_1799 stack;

    public ItemHoverEvent(class_1799 stack) {
        this.stack = stack;
    }

    public class_1799 getStack() {
        return this.stack;
    }
}

