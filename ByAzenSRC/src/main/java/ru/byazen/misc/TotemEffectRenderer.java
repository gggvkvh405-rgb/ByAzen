/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.event.TotemPopEvent;
import ru.byazen.event.WorldRenderEvent;

public interface TotemEffectRenderer {
    public void renderWorld(WorldRenderEvent var1);

    default public void render(WorldRenderEvent event) {
        this.renderWorld(event);
    }

    public void setTotemPopEvent(TotemPopEvent var1);

    public void update2();
}

