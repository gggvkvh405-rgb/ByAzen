/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.event;

import ru.byazen.event.Event;

public final class BrightnessEvent
implements Event {
    private float brightness;

    public BrightnessEvent(float f) {
        this.brightness = f;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public float getBrightness() {
        return this.brightness;
    }
}

