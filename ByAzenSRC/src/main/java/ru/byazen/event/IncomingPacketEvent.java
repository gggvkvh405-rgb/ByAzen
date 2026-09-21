/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2596
 */
package ru.byazen.event;

import net.minecraft.class_2596;
import ru.byazen.event.CancellableEvent;
import ru.byazen.event.Event;

public class IncomingPacketEvent
extends CancellableEvent
implements Event {
    private final class_2596<?> packet2;

    public IncomingPacketEvent(class_2596<?> packet3) {
        this.packet2 = packet3;
    }

    public class_2596<?> getPacket() {
        return this.packet2;
    }
}

