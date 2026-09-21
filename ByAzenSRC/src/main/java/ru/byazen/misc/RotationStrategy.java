/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import ru.byazen.misc.RotationApplyResult;
import ru.byazen.util.RotationIntent;
import ru.byazen.util.RotationState;

public interface RotationStrategy {
    public RotationApplyResult process(RotationState var1, RotationIntent var2);

    public void onDeactivated(RotationState var1);

    public void onActivated(RotationState var1);
}

