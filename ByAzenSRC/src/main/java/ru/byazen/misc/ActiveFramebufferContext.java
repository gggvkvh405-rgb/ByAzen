/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_276
 */
package ru.byazen.misc;

import net.minecraft.class_276;

public final class ActiveFramebufferContext {
    private static class_276 framebuffer2;

    private ActiveFramebufferContext() {
    }

    public static class_276 getFramebuffer() {
        return framebuffer2;
    }

    public static void update() {
        framebuffer2 = null;
    }

    public static void setFramebuffer(class_276 iIllIIiilI2) {
        framebuffer2 = iIllIIiilI2;
    }
}

