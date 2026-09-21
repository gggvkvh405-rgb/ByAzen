/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 */
package ru.byazen.event;

import net.minecraft.class_1297;
import ru.byazen.event.CancellableEvent;
import ru.byazen.event.Event;

public class EntityAttackEvent
extends CancellableEvent
implements Event {
    private final class_1297 entity;

    public EntityAttackEvent(class_1297 entity) {
        this.entity = entity;
    }

    public class_1297 getEntity() {
        return this.entity;
    }
}

