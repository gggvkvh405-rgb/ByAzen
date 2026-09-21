/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.util.Arrays;
import java.util.List;
import ru.byazen.misc.AbstractOptionRow;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.misc.MultiSelectOptionRow;
import ru.byazen.setting.MultiSelectSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.SelectionPopup;
import ru.byazen.util.ScrollableOptionList;

public final class MultiSelectPopup
extends SelectionPopup
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private static final float OPTION_WIDTH = 79.0f;
    private String[] cachedOptions;
    private final MultiSelectSetting multiSelectSetting;

    public MultiSelectPopup(MultiSelectSetting multiSelectSetting) {
        super(new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f), MultiSelectPopup.createOptionList(multiSelectSetting));
        this.multiSelectSetting = multiSelectSetting;
        this.cachedOptions = multiSelectSetting.getOptions();
    }

    private static AbstractOptionRow[] createOptionRows(MultiSelectSetting multiSelectSetting) {
        String[] options = multiSelectSetting.getOptions();
        AbstractOptionRow[] rows = new AbstractOptionRow[options.length];
        for (int i = 0; i < options.length; ++i) {
            rows[i] = new MultiSelectOptionRow(options[i], null, 79.0f);
        }
        return rows;
    }

    @Override
    public void update2() {
        String[] currentOptions = this.multiSelectSetting.getOptions();
        if (!Arrays.equals(this.cachedOptions, currentOptions)) {
            this.cachedOptions = currentOptions;
            this.optionList.setAbstractOptionRow(MultiSelectPopup.createOptionRows(this.multiSelectSetting));
        }
        List<String> list = this.multiSelectSetting.getSelectedOptions();
        for (AbstractOptionRow abstractOptionRow : this.optionList.getList()) {
            abstractOptionRow.setBooleanType(list.contains(abstractOptionRow.getString2()));
        }
    }

    @Override
    protected void selectOption(AbstractOptionRow abstractOptionRow) {
        String string;
        List<String> list = this.multiSelectSetting.getSelectedOptions();
        if (list.contains(string = abstractOptionRow.getString2())) {
            list.remove(string);
        } else {
            list.add(string);
        }
        this.update2();
    }

    @Override
    protected void updateSelectionState() {
        this.update2();
    }

    private static ScrollableOptionList createOptionList(MultiSelectSetting multiSelectSetting) {
        return new ScrollableOptionList(new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f), MultiSelectPopup.createOptionRows(multiSelectSetting));
    }
}

