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
import ru.wexside.setting.RangeSetting;
import ru.wexside.util.CompactTextField;
import ru.wexside.util.RangeValueTextAdapter;

public final class RangeEditorPopup
extends NumberEditorPopup
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    public RangeEditorPopup(RangeSetting rangeSetting) {
        super(rangeSetting.getDisplayName(), new LabeledGuiElement("\u041c\u0438\u043d. \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435", new CompactTextField(new RangeValueTextAdapter(rangeSetting, true))), new LabeledGuiElement("\u041c\u0430\u043a\u0441. \u0437\u043d\u0430\u0447\u0435\u043d\u0438\u0435", new CompactTextField(new RangeValueTextAdapter(rangeSetting, false))));
    }
}

