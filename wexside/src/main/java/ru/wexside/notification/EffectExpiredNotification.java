/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1291
 *  net.minecraft.class_6880
 */
package ru.wexside.notification;

import java.util.List;
import net.minecraft.class_1291;
import net.minecraft.class_6880;
import ru.wexside.misc.ThemeColors;
import ru.wexside.notification.NotificationCategory;
import ru.wexside.notification.NotificationFactory;
import ru.wexside.notification.NotificationPart;
import ru.wexside.notification.NotificationToast;

public record EffectExpiredNotification(class_6880<class_1291> effect) implements NotificationFactory
{
    @Override
    public NotificationCategory category() {
        return NotificationCategory.EFFECT_EXPIRED;
    }

    @Override
    public NotificationToast create(long durationMillis) {
        String name = ((class_1291)this.effect.comp_349()).method_5560().getString();
        return new NotificationToast(this.category(), List.of(this.category(), this.effect), "E", ThemeColors::hudTextPrimary, List.of(NotificationPart.text("\u042d\u0444\u0444\u0435\u043a\u0442 ", ThemeColors::hudTextPrimary), NotificationPart.text(name, ThemeColors::accent), NotificationPart.text(" \u0437\u0430\u043a\u043e\u043d\u0447\u0438\u043b\u0441\u044f", ThemeColors::hudTextPrimary)), durationMillis);
    }
}

