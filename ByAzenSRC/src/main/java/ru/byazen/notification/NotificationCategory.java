/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.notification;

public enum NotificationCategory {
    ITEM("Pickup"),
    MODULE("Function"),
    SELF_ITEM_USE("Use"),
    OTHER_ITEM_USE("Use"),
    EFFECT_EXPIRED("Effect"),
    GAMEPLAY("Use"),
    SYSTEM("Config");

    private final String settingName;

    private NotificationCategory(String settingName) {
        this.settingName = settingName;
    }

    public String settingName() {
        return this.settingName;
    }
}

