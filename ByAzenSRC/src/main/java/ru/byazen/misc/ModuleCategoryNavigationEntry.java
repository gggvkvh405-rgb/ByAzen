/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.util.Objects;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.module.ModuleCategory;
import ru.byazen.ui.NavigationEntry;

public final class ModuleCategoryNavigationEntry
extends NavigationEntry
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private final ModuleCategory moduleCategory;

    public ModuleCategoryNavigationEntry(String string, String string2, String string3, ModuleCategory moduleCategory) {
        super(string, string2, string3);
        this.moduleCategory = Objects.requireNonNull(moduleCategory, "category");
    }

    public ModuleCategory getModuleCategory() {
        return this.moduleCategory;
    }
}

