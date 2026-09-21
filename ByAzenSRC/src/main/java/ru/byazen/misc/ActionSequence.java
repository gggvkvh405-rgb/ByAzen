/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.util.List;
import ru.byazen.misc.InventoryAction;
import ru.byazen.misc.TimedAction;

public final class ActionSequence
implements InventoryAction {
    private final List<TimedAction> steps;

    public ActionSequence(List<TimedAction> steps) {
        this.steps = List.copyOf(steps);
    }

    public List<TimedAction> steps() {
        return this.steps;
    }

    public int maxDelay() {
        return this.steps.stream().mapToInt(TimedAction::delay).max().orElse(0);
    }
}

