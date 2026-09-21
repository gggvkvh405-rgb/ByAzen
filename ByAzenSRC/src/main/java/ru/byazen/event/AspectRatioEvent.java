/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.event;

import ru.byazen.event.Event;

public class AspectRatioEvent
implements Event {
    private float aspectRatio;

    public AspectRatioEvent(float f) {
        this.aspectRatio = f;
    }

    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public float getAspectRatio() {
        return this.aspectRatio;
    }
}

