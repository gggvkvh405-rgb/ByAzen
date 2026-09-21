/*
 * Decompiled with CFR 0.152.
 */
package ru.byazen.util;

import java.util.Deque;
import ru.byazen.misc.AlphaStack;
import ru.byazen.misc.ScissorStack;
import ru.byazen.util.RenderContextState;
import ru.byazen.util.StencilState;

record RenderFrameState(AlphaStack alphaStack, ScissorStack scissorStack, Deque<RenderContextState> contextStates, Deque<StencilState> stencilStates, int framebufferWidth, int framebufferHeight, int stencilMode, boolean layerFrame, float layerOriginX, float layerOriginY) {
}

