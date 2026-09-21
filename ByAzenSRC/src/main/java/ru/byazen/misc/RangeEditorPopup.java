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
import ru.byazen.setting.RangeSetting;
import ru.byazen.util.CompactTextField;
import ru.byazen.util.RangeValueTextAdapter;

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

