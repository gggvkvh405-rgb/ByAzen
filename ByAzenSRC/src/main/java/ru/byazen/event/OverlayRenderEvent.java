/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.event;

import ru.byazen.event.CancellableEvent;
import ru.byazen.event.OverlayType;

public final class OverlayRenderEvent
extends CancellableEvent {
    private final OverlayType type;

    public OverlayRenderEvent(OverlayType type) {
        this.type = type;
    }

    public OverlayType type() {
        return this.type;
    }
}

