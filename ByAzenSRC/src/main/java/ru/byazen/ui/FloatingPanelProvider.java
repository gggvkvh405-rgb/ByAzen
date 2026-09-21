/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.ui;

import ru.byazen.ui.FloatingPanel;
import ru.byazen.ui.FloatingPanelManager;

public interface FloatingPanelProvider {
    public FloatingPanel getFloatingPanel();

    public void updateFloatingPanelPosition();

    default public void setFloatingPanelManager(FloatingPanelManager manager) {
    }
}

