/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1661
 *  net.minecraft.class_2735
 *  net.minecraft.class_310
 *  net.minecraft.class_746
 */
package ru.byazen.util;

import net.minecraft.class_1661;
import net.minecraft.class_2735;
import net.minecraft.class_310;
import net.minecraft.class_746;
import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;
import ru.byazen.event.IncomingPacketEvent;

public class HotbarSlotLock {
    private long longType;
    private int slot = -1;

    public HotbarSlotLock(EventBus eventBus) {
        eventBus.subscribe(ClientTickEvent.class, ignored -> this.update2());
        eventBus.subscribe(IncomingPacketEvent.class, this::onIncomingPacket);
    }

    public boolean isActive() {
        return this.slot != -1;
    }

    public void process(int n, long l) {
        if (n < 0 || n > 8) {
            return;
        }
        this.slot = n;
        this.longType = System.currentTimeMillis() + Math.max(50L, l);
    }

    private void onIncomingPacket(IncomingPacketEvent gameEvent5) {
        if (this.slot == -1) {
            return;
        }
        if (!(gameEvent5.getPacket() instanceof class_2735)) {
            return;
        }
        class_746 player2 = class_310.method_1551().field_1724;
        if (player2 == null) {
            return;
        }
        class_1661 inv = player2.method_31548();
        if (inv.method_67532() != this.slot) {
            inv.method_61496(this.slot);
        }
    }

    public void update() {
        this.slot = -1;
        this.longType = 0L;
    }

    private void update2() {
        if (this.slot == -1) {
            return;
        }
        if (System.currentTimeMillis() >= this.longType) {
            this.update();
        }
    }
}

