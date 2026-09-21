/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1799
 *  org.joml.Matrix4f
 */
package ru.wexside.misc;

import net.minecraft.class_1799;
import org.joml.Matrix4f;
import ru.wexside.misc.BakedIconEntry;
import ru.wexside.misc.FontRegistry;
import ru.wexside.misc.ThemeColors;
import ru.wexside.render.HudIconRenderer;
import ru.wexside.render.IconAtlasEntry;
import ru.wexside.util.ColorUtils;
import ru.wexside.util.GuiDrawApi;

public final class ItemStackRenderer {
    private final int slot = ColorUtils.rgba(255, 255, 255, 255);
    private final float value3;
    private final float value4;
    private class_1799 stack;
    private class_1799 stack2;
    private final IconAtlasEntry iconEntry = new IconAtlasEntry();
    private final float process2;
    private final float value5;
    private final float value6;
    private final float value7;

    public ItemStackRenderer() {
        this.process2 = 19.5f;
        this.value6 = 19.0f;
        this.value5 = 8.0f;
        this.value4 = 1.0f;
        this.value7 = 10.0f;
        this.value3 = 5.0f;
        this.stack = class_1799.field_8037;
        this.stack2 = class_1799.field_8037;
    }

    public void process(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3) {
        int n;
        float f4;
        float f5 = 19.5f * f3;
        float f6 = 19.0f * f3;
        drawApi.drawRoundedOutline(matrix4f, f, f2, f5, f6, 8.0f * f3, 1.0f * f3, ThemeColors.notificationOutline());
        if (this.stack.method_7960()) {
            return;
        }
        if (this.iconEntry.isActive()) {
            float f7 = 10.0f * f3;
            float f8 = f + (f5 - f7) / 2.0f;
            f4 = f2 + (f6 - f7) / 2.0f;
            int n2 = drawApi.bindTexture(this.iconEntry.getIntType4(), this.iconEntry.getIntType(), this.iconEntry.getIntType());
            drawApi.drawTexture(matrix4f, f8, f4, f7, f7, 0.0f, 1.0f, 1.0f, 0.0f, n2, -1);
        }
        if ((n = this.stack.method_7947()) > 1) {
            String string = String.valueOf(n);
            f4 = FontRegistry.font6.process3(string, 5.0f);
            float f9 = FontRegistry.font6.process4(string, 5.0f);
            FontRegistry.font6.process2(matrix4f, drawApi, string, f + f5 - f4 * f3 - 2.0f * f3, f2 + f6 - f9 * f3 - 1.5f * f3, 5.0f * f3, this.slot);
        }
    }

    public void setStack(class_1799 stack3) {
        this.stack = stack3;
        if (!class_1799.method_7973((class_1799)stack3, (class_1799)this.stack2)) {
            this.stack2 = stack3.method_7960() ? class_1799.field_8037 : stack3.method_7972();
            this.iconEntry.update();
        }
    }

    public float getFloatType() {
        return 19.0f;
    }

    public float getFloatType2() {
        return 19.5f;
    }

    public BakedIconEntry process3(float f) {
        if (this.stack2.method_7960()) {
            return null;
        }
        if (!this.iconEntry.process(f)) {
            return null;
        }
        class_1799 stack3 = this.stack2;
        return new BakedIconEntry(this.iconEntry, (trimToWidth, n, n2, n3) -> HudIconRenderer.drawItem(trimToWidth, stack3, n, n2, n3));
    }

    public void update() {
        this.iconEntry.update2();
    }
}

