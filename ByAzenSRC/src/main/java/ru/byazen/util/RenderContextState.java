/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.util;

import java.util.Deque;
import ru.byazen.misc.AlphaStack;
import ru.byazen.misc.ScissorStack;
import ru.byazen.util.StencilState;

final class RenderContextState {
    private final AlphaStack colors;
    private final ScissorStack scissors;
    private final int stencilMode;
    private final Deque<StencilState> stencilStates;

    RenderContextState(AlphaStack colors, ScissorStack scissors, int stencilMode, Deque<StencilState> stencilStates) {
        this.colors = colors;
        this.scissors = scissors;
        this.stencilMode = stencilMode;
        this.stencilStates = stencilStates;
    }

    AlphaStack colors() {
        return this.colors;
    }

    ScissorStack scissors() {
        return this.scissors;
    }

    int stencilMode() {
        return this.stencilMode;
    }

    Deque<StencilState> stencilStates() {
        return this.stencilStates;
    }
}

