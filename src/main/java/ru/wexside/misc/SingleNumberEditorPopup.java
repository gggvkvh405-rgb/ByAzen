/*
 * Decompiled with CFR 0.152.
 */
package ru.wexside.misc;

import ru.wexside.misc.BoundsProvider;
import ru.wexside.misc.CharacterInputHandler;
import ru.wexside.misc.GuiRenderable;
import ru.wexside.misc.KeyPressHandler;
import ru.wexside.misc.LabeledGuiElement;
import ru.wexside.misc.LayoutUpdater;
import ru.wexside.misc.MouseButtonHandler;
import ru.wexside.misc.MouseScrollHandler;
import ru.wexside.misc.NumberEditorPopup;
import ru.wexside.setting.NumberSetting;
import ru.wexside.util.CompactTextField;
import ru.wexside.util.NumericTextEditor;

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

