/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.util;

import ru.byazen.misc.FrameInterpolator;
import ru.byazen.render.RenderFrameClock;

public final class VisibilityAnimationCache {
    private int slot = Integer.MIN_VALUE;
    private boolean enabled;
    private float value;

    public float process(boolean bl, boolean bl2, float f) {
        float f2;
        int n = RenderFrameClock.currentFrame();
        if (this.slot == n) {
            return this.value;
        }
        float f3 = f2 = bl2 && bl ? 1.0f : 0.0f;
        if (!this.enabled) {
            this.value = f2;
            this.enabled = true;
        } else {
            this.value = FrameInterpolator.lerpTowards(this.value, f2, f);
            if (Math.abs(f2 - this.value) <= 0.001f) {
                this.value = f2;
            }
        }
        this.slot = n;
        return this.value;
    }
}

