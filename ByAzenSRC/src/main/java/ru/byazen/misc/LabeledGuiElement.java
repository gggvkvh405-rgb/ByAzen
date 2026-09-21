/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.ui.GuiElement;

public final class LabeledGuiElement {
    private final String string2;
    private final GuiElement element2;

    public LabeledGuiElement(String string, GuiElement element3) {
        this.string2 = string;
        this.element2 = element3;
    }

    public String ModelPartBuilder() {
        return this.string2;
    }

    public GuiElement getElement() {
        return this.element2;
    }
}

