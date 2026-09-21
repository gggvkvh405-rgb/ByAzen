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
import ru.byazen.misc.MouseHitTest;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.PotionEditorState;
import ru.byazen.misc.PotionSearchTextAdapter;
import ru.byazen.util.SearchTextField;

public final class PotionSearchField
extends SearchTextField
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider,
MouseHitTest {
    static final String string = "\u041f\u043e\u0438\u0441\u043a";
    static final float value = 105.5f;

    public PotionSearchField(PotionEditorState potionEditorState) {
        super(new PotionSearchTextAdapter(potionEditorState), () -> true, string, true);
    }

    public float getFloatType() {
        return 105.5f;
    }
}

