/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LabeledGuiElement;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.NumberEditorPopup;
import ru.byazen.setting.NumberSetting;
import ru.byazen.util.CompactTextField;
import ru.byazen.util.NumericTextEditor;

public final class SingleNumberEditorPopup
extends NumberEditorPopup
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    public SingleNumberEditorPopup(NumberSetting numberSetting) {
        super(numberSetting.getDisplayName(), new LabeledGuiElement("\u0417\u043d\u0430\u0447\u0435\u043d\u0438\u0435", new CompactTextField(new NumericTextEditor(numberSetting))));
    }
}

