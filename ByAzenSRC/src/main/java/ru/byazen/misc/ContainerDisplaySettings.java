/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.ConfigRegistry;
import ru.byazen.misc.ContainerDisplay;

public final class ContainerDisplaySettings {
    private final ContainerDisplay containerDisplay = new ContainerDisplay();

    public ContainerDisplaySettings(ConfigRegistry configRegistry) {
        configRegistry.register(this.containerDisplay);
    }

    public ContainerDisplay getContainerDisplay() {
        return this.containerDisplay;
    }
}

