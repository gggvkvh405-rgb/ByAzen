/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.render;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import ru.byazen.render.IconAtlasEntry;

public final class PlayerHeadRenderCache
extends LinkedHashMap<UUID, IconAtlasEntry> {
    private final int capacity;

    public PlayerHeadRenderCache(int capacity) {
        super(capacity, 0.75f, true);
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<UUID, IconAtlasEntry> eldest) {
        if (this.size() <= this.capacity) {
            return false;
        }
        eldest.getValue().update2();
        return true;
    }
}

