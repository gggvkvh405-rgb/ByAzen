/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.event;

import ru.byazen.event.Event;

public record WorldSessionEvent(Change change) implements Event
{

    public static enum Change {
        JOINED,
        DISCONNECTED;

    }
}

