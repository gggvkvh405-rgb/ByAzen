/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.notification;

import java.util.List;
import java.util.function.IntSupplier;
import ru.byazen.notification.NotificationCategory;
import ru.byazen.notification.NotificationFactory;
import ru.byazen.notification.NotificationPart;
import ru.byazen.notification.NotificationToast;

public record TextNotification(NotificationCategory category, Object key, String icon, String text, IntSupplier color) implements NotificationFactory
{
    public TextNotification(NotificationCategory category, Object key, String icon, String text) {
        this(category, key, icon, text, () -> -1);
    }

    @Override
    public NotificationToast create(long durationMillis) {
        return new NotificationToast(this.category, this.key, this.icon, this.color, List.of(NotificationPart.text(this.text, this.color)), durationMillis);
    }
}

