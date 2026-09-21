/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.event;

import ru.byazen.event.CancellableEvent;
import ru.byazen.event.Event;

public final class HandSwingSpeedEvent
extends CancellableEvent
implements Event {
    private float speedMultiplier;

    public void setSpeedMultiplier(float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    public float getSpeedMultiplier() {
        return this.speedMultiplier;
    }
}

