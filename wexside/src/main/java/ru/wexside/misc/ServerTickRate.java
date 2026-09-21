/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_2761
 */
package ru.wexside.misc;

import net.minecraft.class_2761;
import ru.wexside.event.EventBus;
import ru.wexside.event.IncomingPacketEvent;

public class ServerTickRate {
    private float value;
    private long lastTimeUpdateMillis;
    static final int slot = 20;
    static final float value2 = 20.0f;
    static final float value3 = 0.85f;

    public ServerTickRate(EventBus eventBus) {
        eventBus.subscribe(IncomingPacketEvent.class, this::onIncomingPacket);
    }

    public float process(float f) {
        if (this.value <= 0.0f) {
            return f;
        }
        return f * (20.0f / this.value);
    }

    private void onIncomingPacket(IncomingPacketEvent gameEvent5) {
        long l;
        if (!(gameEvent5.getPacket() instanceof class_2761)) {
            return;
        }
        long l2 = System.currentTimeMillis();
        if (this.lastTimeUpdateMillis > 0L && (l = l2 - this.lastTimeUpdateMillis) > 0L && l < 5000L) {
            float f = 20000.0f / (float)l;
            if (f > 22.0f) {
                f = 22.0f;
            }
            if (f < 1.0f) {
                f = 1.0f;
            }
            this.value = this.value * 0.85f + f * 0.14999998f;
        }
        this.lastTimeUpdateMillis = l2;
    }

    public float getFloatType() {
        return this.value;
    }
}

