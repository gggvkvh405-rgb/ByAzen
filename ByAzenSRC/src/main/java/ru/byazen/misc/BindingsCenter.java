/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.ui.NavigationEntry;

public final class BindingsCenter
extends NavigationEntry
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final String string;

    public BindingsCenter() {
        super("settings.bindings", "Bindings Center", "\u041b");
        this.string = "custom:bindings-center";
    }

    @Override
    public String getString() {
        return this.string;
    }
}

