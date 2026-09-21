/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.PopupManager;
import ru.byazen.ui.PopupPanel;

public interface PopupOwner {
    public PopupPanel getPopup();

    default public boolean process6(int n, int n2) {
        return false;
    }

    public void update2();

    default public void setPopupManager(PopupManager popupManager) {
    }
}

