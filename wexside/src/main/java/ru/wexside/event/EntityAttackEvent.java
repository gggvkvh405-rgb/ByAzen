/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 */
package ru.wexside.event;

import net.minecraft.class_1297;
import ru.wexside.event.CancellableEvent;
import ru.wexside.event.Event;

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

