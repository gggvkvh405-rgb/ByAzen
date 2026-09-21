/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.setting.color;

public enum ColorMode {
    STATIC,
    ASTOLFO,
    DOUBLE_COLOR;


    public static ColorMode fromOrdinal(int ordinal) {
        ColorMode[] values = ColorMode.values();
        return ordinal >= 0 && ordinal < values.length ? values[ordinal] : STATIC;
    }
}

