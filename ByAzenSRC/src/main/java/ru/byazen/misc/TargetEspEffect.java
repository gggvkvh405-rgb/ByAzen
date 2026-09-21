/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.event.EntityAttackEvent;
import ru.byazen.event.WorldRenderEvent;

public interface TargetEspEffect {
    public void setEntityAttackEvent(EntityAttackEvent var1);

    public void setWorldRenderEvent(WorldRenderEvent var1);

    public void update();

    default public boolean isActive() {
        return true;
    }
}

