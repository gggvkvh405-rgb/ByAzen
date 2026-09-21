/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.notification;

import ru.byazen.notification.NotificationCategory;

public interface NotificationPreferences {
    public boolean isCategoryVisible(NotificationCategory var1);

    public boolean isSoundEnabled(NotificationCategory var1);

    public float soundVolume();

    public boolean isEnabled();
}

