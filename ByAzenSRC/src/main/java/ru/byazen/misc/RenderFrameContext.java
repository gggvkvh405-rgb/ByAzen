/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.misc;

import java.util.Deque;
import ru.byazen.misc.StencilFrameState;
import ru.byazen.util.GlRenderStateSnapshot;

public record RenderFrameContext(GlRenderStateSnapshot stateSnapshot, boolean shaderBound, int stencilMode, Deque<StencilFrameState> stencilStates) {
}

