/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.event;

import ru.byazen.event.DamageType;
import ru.byazen.event.Event;

public final class DamageEvent
implements Event {
    private final DamageType type;

    public DamageEvent(DamageType type) {
        this.type = type;
    }

    public DamageType type() {
        return this.type;
    }
}

