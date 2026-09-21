/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.event.ClientTickEvent;
import ru.byazen.event.EventBus;

public class TickOverride {
    private volatile boolean enabled;
    private volatile boolean overrideActive;

    public TickOverride(EventBus eventBus) {
        eventBus.subscribe(ClientTickEvent.class, ignored -> this.update());
    }

    public void setBooleanType(boolean bl) {
        this.enabled = bl;
        this.overrideActive = true;
    }

    public boolean isActive() {
        return this.overrideActive;
    }

    private void update() {
        this.overrideActive = false;
    }

    public boolean process(boolean bl) {
        return this.overrideActive ? this.enabled : bl;
    }
}

