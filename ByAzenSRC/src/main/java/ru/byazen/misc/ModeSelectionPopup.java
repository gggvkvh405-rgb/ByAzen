/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.util.Arrays;
import ru.byazen.misc.AbstractOptionRow;
import ru.byazen.misc.BoundsProvider;
import ru.byazen.misc.CharacterInputHandler;
import ru.byazen.misc.CompactOptionRow;
import ru.byazen.misc.GuiRenderable;
import ru.byazen.misc.KeyPressHandler;
import ru.byazen.misc.LayoutUpdater;
import ru.byazen.misc.MouseButtonHandler;
import ru.byazen.misc.MouseScrollHandler;
import ru.byazen.setting.ModeSetting;
import ru.byazen.ui.GuiBounds;
import ru.byazen.ui.SelectionPopup;
import ru.byazen.util.ScrollableOptionList;

public final class ModeSelectionPopup
extends SelectionPopup
implements CharacterInputHandler,
MouseScrollHandler,
LayoutUpdater,
KeyPressHandler,
GuiRenderable,
MouseButtonHandler,
BoundsProvider {
    private static final float OPTION_WIDTH = 79.0f;
    private final ModeSetting modeSetting;
    private String[] cachedOptions;

    public ModeSelectionPopup(ModeSetting modeSetting) {
        super(new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f), ModeSelectionPopup.createOptionList(modeSetting));
        this.modeSetting = modeSetting;
        this.cachedOptions = modeSetting.getOptions();
    }

    @Override
    public void update2() {
        String[] currentOptions = this.modeSetting.getOptions();
        if (!Arrays.equals(this.cachedOptions, currentOptions)) {
            this.cachedOptions = currentOptions;
            this.optionList.setAbstractOptionRow(ModeSelectionPopup.createOptionRows(this.modeSetting));
        }
        String string = this.modeSetting.getSelectedOption();
        for (AbstractOptionRow abstractOptionRow : this.optionList.getList()) {
            abstractOptionRow.setBooleanType(abstractOptionRow.getString2().equals(string));
        }
    }

    @Override
    protected void selectOption(AbstractOptionRow abstractOptionRow) {
        this.modeSetting.setSelectedOption(abstractOptionRow.getString2());
        this.update2();
        this.setBooleanType(false);
    }

    @Override
    protected void updateSelectionState() {
        this.update2();
    }

    private static AbstractOptionRow[] createOptionRows(ModeSetting modeSetting) {
        String[] options = modeSetting.getOptions();
        AbstractOptionRow[] rows = new AbstractOptionRow[options.length];
        for (int i = 0; i < options.length; ++i) {
            rows[i] = new CompactOptionRow(options[i], null, 79.0f);
        }
        return rows;
    }

    private static ScrollableOptionList createOptionList(ModeSetting modeSetting) {
        return new ScrollableOptionList(new GuiBounds(0.0f, 0.0f, 0.0f, 0.0f), ModeSelectionPopup.createOptionRows(modeSetting));
    }
}

