/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.event;

import ru.byazen.event.Event;

public record KeyPressedEvent(int key, int scancode) implements Event
{
}

