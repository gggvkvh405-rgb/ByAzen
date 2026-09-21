/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_465
 */
package ru.byazen.misc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.class_465;
import ru.byazen.misc.SlotHighlight;
import ru.byazen.misc.SlotHighlightProvider;

public class SlotHighlightRegistry {
    private final List<SlotHighlightProvider> callbacks = new ArrayList<SlotHighlightProvider>();
    private long cacheTime;
    private static final long CACHE_MS = 150L;
    private class_465<?> cachedScreen;
    private List<SlotHighlight> cachedHighlights = List.of();

    public void setCallback56(SlotHighlightProvider callback) {
        if (callback == null || this.callbacks.contains(callback)) {
            return;
        }
        this.callbacks.add(callback);
    }

    public void setCallback562(SlotHighlightProvider callback) {
        this.callbacks.remove(callback);
    }

    private List<SlotHighlight> collect(class_465<?> screen) {
        LinkedHashMap<Integer, Integer> merged = new LinkedHashMap<Integer, Integer>();
        for (SlotHighlightProvider callback : this.callbacks) {
            List<SlotHighlight> highlights;
            try {
                highlights = callback.process4(screen);
            }
            catch (Throwable ignored) {
                continue;
            }
            if (highlights == null) continue;
            for (SlotHighlight highlight : highlights) {
                if (highlight == null) continue;
                merged.putIfAbsent(highlight.slot(), highlight.color());
            }
        }
        if (merged.isEmpty()) {
            return List.of();
        }
        ArrayList<SlotHighlight> result = new ArrayList<SlotHighlight>(merged.size());
        merged.forEach((slot, color) -> result.add(new SlotHighlight((int)slot, (int)color)));
        return result;
    }

    public List<SlotHighlight> process2(class_465<?> screen) {
        if (screen == null || this.callbacks.isEmpty()) {
            return List.of();
        }
        long now = System.currentTimeMillis();
        if (screen == this.cachedScreen && now - this.cacheTime < 150L) {
            return this.cachedHighlights;
        }
        List<SlotHighlight> highlights = this.collect(screen);
        this.cachedScreen = screen;
        this.cacheTime = now;
        this.cachedHighlights = highlights;
        return highlights;
    }
}

