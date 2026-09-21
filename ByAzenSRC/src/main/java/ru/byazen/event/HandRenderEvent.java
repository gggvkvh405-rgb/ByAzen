/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_4587
 */
package ru.byazen.event;

import net.minecraft.class_4587;
import ru.byazen.event.Event;
import ru.byazen.event.HandRenderPhase;

public final class HandRenderEvent
implements Event {
    private final HandRenderPhase phase;
    private final class_4587 matrices;
    private final float tickDelta;

    public HandRenderEvent(HandRenderPhase phase, class_4587 matrices, float tickDelta) {
        this.phase = phase;
        this.matrices = matrices;
        this.tickDelta = tickDelta;
    }

    public HandRenderPhase phase() {
        return this.phase;
    }

    public class_4587 matrices() {
        return this.matrices;
    }

    public float tickDelta() {
        return this.tickDelta;
    }
}

