/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1109
 *  net.minecraft.class_1113
 *  net.minecraft.class_310
 *  net.minecraft.class_3414
 *  net.minecraft.class_3417
 *  org.joml.Matrix4f
 */
package ru.byazen.notification;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;
import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import org.joml.Matrix4f;
import ru.byazen.ByAzenClient;
import ru.byazen.notification.NotificationCategory;
import ru.byazen.notification.NotificationFactory;
import ru.byazen.notification.NotificationPreferences;
import ru.byazen.notification.NotificationToast;
import ru.byazen.util.GuiDrawApi;

public final class NotificationCenter {
    private static final int MAX_VISIBLE = 5;
    private static final int MAX_RETAINED = 8;
    private static final float ROW_HEIGHT = 17.5f;
    private static final long DEFAULT_DURATION_MILLIS = 3000L;
    private final NotificationPreferences preferences;
    private final LinkedList<NotificationToast> notifications = new LinkedList();

    public NotificationCenter(NotificationPreferences preferences) {
        this.preferences = Objects.requireNonNull(preferences, "preferences");
    }

    public void push(NotificationFactory factory) {
        class_310 client = class_310.method_1551();
        if (!client.method_18854()) {
            client.execute(() -> this.push(factory));
            return;
        }
        if (client.field_1724 == null || !this.preferences.isEnabled() || !this.preferences.isCategoryVisible(factory.category())) {
            return;
        }
        NotificationToast incoming = factory.create(3000L);
        if (incoming.key() != null) {
            for (NotificationToast existing : this.notifications) {
                if (!Objects.equals(existing.key(), incoming.key())) continue;
                existing.updateContent(incoming.icon(), incoming.accentColor(), incoming.parts());
                this.playSound(factory.category());
                return;
            }
        }
        this.notifications.addLast(incoming);
        int active = 0;
        for (NotificationToast notification : this.notifications) {
            if (notification.isDismissing() || ++active <= 5) continue;
            notification.beginDismiss();
        }
        while (this.notifications.size() > 8) {
            this.notifications.removeFirst().remove();
        }
        this.playSound(factory.category());
    }

    public boolean accepts(NotificationCategory category) {
        return this.preferences.isEnabled() && this.preferences.isCategoryVisible(category);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(float tickDelta) {
        class_310 client = class_310.method_1551();
        if (client.field_1724 == null || this.notifications.isEmpty()) {
            return;
        }
        GuiDrawApi renderer = ByAzenClient.getHudRenderer();
        if (renderer == null) {
            return;
        }
        Iterator iterator = this.notifications.iterator();
        int row = 0;
        float startY = (float)client.method_22683().method_4502() * 0.5f + 20.0f * tickDelta;
        float centerX = (float)client.method_22683().method_4486() * 0.5f;
        Matrix4f matrix = new Matrix4f().scale((float)client.method_22683().method_4495());
        renderer.begin();
        try {
            while (iterator.hasNext()) {
                NotificationToast notification = (NotificationToast)iterator.next();
                notification.moveTo(startY + (float)row * 17.5f * tickDelta, tickDelta);
                if (notification.isExpired()) {
                    iterator.remove();
                    continue;
                }
                notification.render(renderer, matrix, centerX, tickDelta);
                ++row;
            }
        }
        finally {
            renderer.end();
        }
    }

    private void playSound(NotificationCategory category) {
        if (!this.preferences.isSoundEnabled(category)) {
            return;
        }
        class_310.method_1551().method_1483().method_4873((class_1113)class_1109.method_4758((class_3414)((class_3414)class_3417.field_15015.comp_349()), (float)Math.max(0.0f, Math.min(1.0f, this.preferences.soundVolume()))));
    }
}

