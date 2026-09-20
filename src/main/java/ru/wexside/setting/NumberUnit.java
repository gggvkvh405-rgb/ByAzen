/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.setting;

import ru.wexside.util.RussianPluralForms;

public enum NumberUnit {
    BLOCKS("\u0431\u043b\u043e\u043a", "\u0431\u043b\u043e\u043a\u0430", "\u0431\u043b\u043e\u043a\u043e\u0432"),
    PIXELS("\u043f\u0438\u043a\u0441\u0435\u043b\u044c", "\u043f\u0438\u043a\u0441\u0435\u043b\u044f", "\u043f\u0438\u043a\u0441\u0435\u043b\u0435\u0439"),
    PERCENT("%", "%", "%");

    private final RussianPluralForms formatter;

    private NumberUnit(String singular, String paucal, String plural) {
        this.formatter = new RussianPluralForms(singular, paucal, plural);
    }

    public RussianPluralForms getFormatter() {
        return this.formatter;
    }
}

