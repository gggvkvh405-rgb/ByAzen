/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 */
package ru.byazen.render;

import net.minecraft.class_1799;
import ru.byazen.misc.BakedIconEntry;
import ru.byazen.render.HudIconRenderer;
import ru.byazen.render.IconAtlas;
import ru.byazen.render.IconAtlasEntry;

public final class BakedItemIcon {
    private final IconAtlasEntry texture;
    private class_1799 stack = class_1799.field_8037;
    private int frame;
    private long lastUsedNanos;

    public BakedItemIcon(IconAtlas atlas) {
        this.texture = new IconAtlasEntry(atlas);
    }

    public IconAtlasEntry texture() {
        return this.texture;
    }

    public int frame() {
        return this.frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public long lastUsedNanos() {
        return this.lastUsedNanos;
    }

    public void markUsed(long nowNanos) {
        this.lastUsedNanos = nowNanos;
    }

    public void setStack(class_1799 stack) {
        class_1799 safeStack;
        class_1799 class_17992 = safeStack = stack == null ? class_1799.field_8037 : stack;
        if (!class_1799.method_7973((class_1799)this.stack, (class_1799)safeStack)) {
            this.stack = safeStack.method_7960() ? class_1799.field_8037 : safeStack.method_7972();
            this.texture.update();
        }
    }

    public BakedIconEntry createBakeEntry(float scale) {
        if (this.stack.method_7960() || !this.texture.process(scale)) {
            return null;
        }
        class_1799 renderedStack = this.stack.method_7972();
        return new BakedIconEntry(this.texture, (context, x, y, size) -> HudIconRenderer.drawItem(context, renderedStack, x, y, size));
    }

    public void close() {
        this.texture.update2();
    }
}

