/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.model.esp;

public enum EspTargetType {
    PLAYERS("Players", "\u043b"),
    ENTITIES("Entities", "\u0419"),
    ITEMS("Items", "\u0423"),
    SELF("Self", "4");

    private final String title;
    private final String icon;

    private EspTargetType(String title, String icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return this.title;
    }

    public String getIcon() {
        return this.icon;
    }
}

