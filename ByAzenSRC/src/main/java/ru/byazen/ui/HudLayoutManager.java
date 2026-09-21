/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import ru.byazen.misc.ConfigRegistry;
import ru.byazen.ui.HudElementLayout;

public final class HudLayoutManager {
    private final ConfigRegistry configRegistry;
    private final List<HudElementLayout> layouts = new ArrayList<HudElementLayout>();

    public HudLayoutManager(ConfigRegistry configRegistry) {
        this.configRegistry = configRegistry;
    }

    public HudElementLayout register(String name) {
        HudElementLayout existing = this.find(name);
        if (existing != null) {
            return existing;
        }
        HudElementLayout layout = new HudElementLayout(name);
        this.layouts.add(layout);
        this.configRegistry.register(layout);
        return layout;
    }

    public HudElementLayout find(String name) {
        for (HudElementLayout layout : this.layouts) {
            if (!layout.getName().equals(name)) continue;
            return layout;
        }
        return null;
    }

    public List<HudElementLayout> getLayouts() {
        return Collections.unmodifiableList(this.layouts);
    }
}

