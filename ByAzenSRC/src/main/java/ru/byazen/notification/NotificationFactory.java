/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.notification;

import ru.byazen.notification.NotificationCategory;
import ru.byazen.notification.NotificationToast;

public interface NotificationFactory {
    default public String soundId() {
        return null;
    }

    public NotificationCategory category();

    public NotificationToast create(long var1);
}

