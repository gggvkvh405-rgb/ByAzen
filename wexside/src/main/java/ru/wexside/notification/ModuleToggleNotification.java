/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.notification;

import java.util.List;
import ru.wexside.misc.ThemeColors;
import ru.wexside.notification.NotificationCategory;
import ru.wexside.notification.NotificationFactory;
import ru.wexside.notification.NotificationPart;
import ru.wexside.notification.NotificationToast;
import ru.wexside.util.ColorUtils;

public record ModuleToggleNotification(String moduleName, boolean enabled) implements NotificationFactory
{
    @Override
    public NotificationCategory category() {
        return NotificationCategory.MODULE;
    }

    @Override
    public NotificationToast create(long durationMillis) {
        int accent = this.enabled ? ColorUtils.rgba(38, 198, 140, 255) : ColorUtils.rgba(255, 82, 82, 255);
        return new NotificationToast(this.category(), List.of(this.category(), this.moduleName), "M", () -> accent, List.of(NotificationPart.text("\u0424\u0443\u043d\u043a\u0446\u0438\u044f ", ThemeColors::hudTextPrimary), NotificationPart.text(this.moduleName, ThemeColors::accent), NotificationPart.text(this.enabled ? " \u0432\u043a\u043b\u044e\u0447\u0435\u043d\u0430" : " \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d\u0430", ThemeColors::hudTextPrimary)), durationMillis);
    }
}

