/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2248
 */
package ru.byazen.event;

import net.minecraft.class_2248;
import ru.byazen.event.CancellableEvent;
import ru.byazen.event.Event;

public class BlockInteractEvent
extends CancellableEvent
implements Event {
    private final class_2248 block;

    public BlockInteractEvent(class_2248 block) {
        this.block = block;
    }

    public class_2248 getBlock() {
        return this.block;
    }
}

