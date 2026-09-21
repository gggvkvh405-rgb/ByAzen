/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.util;

import org.joml.Matrix4f;
import ru.byazen.util.GuiDrawApi;

public final class EspBoxBorderRenderer {
    public static final int SHADOW_COLOR = 0x40000000;

    private EspBoxBorderRenderer() {
    }

    public static void process(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2, int n3, int n4) {
        EspBoxBorderRenderer.process5(drawApi, matrix4f, f, f2, f + 1.5f, f4, 0x40000000);
        EspBoxBorderRenderer.process5(drawApi, matrix4f, f + 1.5f, f2, f3 - 1.5f, f2 + 1.5f, 0x40000000);
        EspBoxBorderRenderer.process5(drawApi, matrix4f, f3 - 1.5f, f2, f3, f4, 0x40000000);
        EspBoxBorderRenderer.process5(drawApi, matrix4f, f + 1.5f, f4 - 1.5f, f3 - 1.5f, f4, 0x40000000);
        EspBoxBorderRenderer.process2(drawApi, matrix4f, f + 0.5f, f2 + 0.5f, f + 1.0f, f4 - 0.5f, n, n4);
        EspBoxBorderRenderer.process3(drawApi, matrix4f, f + 1.0f, f2 + 0.5f, f3 - 0.5f, f2 + 1.0f, n, n2);
        EspBoxBorderRenderer.process2(drawApi, matrix4f, f3 - 1.0f, f2 + 0.5f, f3 - 0.5f, f4 - 0.5f, n2, n3);
        EspBoxBorderRenderer.process3(drawApi, matrix4f, f + 1.0f, f4 - 1.0f, f3 - 1.0f, f4 - 0.5f, n4, n3);
    }

    public static void process2(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2) {
        drawApi.drawColorGradient(matrix4f, f, f2, f3 - f, f4 - f2, n2, n2, n, n);
    }

    public static void process3(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2) {
        drawApi.drawColorGradient(matrix4f, f, f2, f3 - f, f4 - f2, n, n2, n2, n);
    }

    public static void process4(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n, int n2, int n3, int n4) {
        float f5 = f3 - f;
        float f6 = f4 - f2;
        float f7 = Math.min(f5 / 4.0f, f6 / 4.0f);
        EspBoxBorderRenderer.process5(drawApi, matrix4f, f, f2, f + 1.5f, f2 + f7, 0x40000000);
        EspBoxBorderRenderer.process5(drawApi, matrix4f, f, f2, f + f7, f2 + 1.5f, 0x40000000);
        EspBoxBorderRenderer.process5(drawApi, matrix4f, f3 - f7, f2, f3, f2 + 1.5f, 0x40000000);
        EspBoxBorderRenderer.process5(drawApi, matrix4f, f3 - 1.5f, f2, f3, f2 + f7, 0x40000000);
        EspBoxBorderRenderer.process5(drawApi, matrix4f, f, f4 - f7, f + 1.5f, f4, 0x40000000);
        EspBoxBorderRenderer.process5(drawApi, matrix4f, f, f4 - 1.5f, f + f7, f4, 0x40000000);
        EspBoxBorderRenderer.process5(drawApi, matrix4f, f3 - 1.5f, f4 - f7, f3, f4, 0x40000000);
        EspBoxBorderRenderer.process5(drawApi, matrix4f, f3 - f7, f4 - 1.5f, f3, f4, 0x40000000);
        EspBoxBorderRenderer.process2(drawApi, matrix4f, f + 0.5f, f2 + 0.5f, f + 1.0f, f2 + f7 - 0.5f, n, n4);
        EspBoxBorderRenderer.process2(drawApi, matrix4f, f + 0.5f, f2 + 0.5f, f + f7 - 0.5f, f2 + 1.0f, n, n4);
        EspBoxBorderRenderer.process2(drawApi, matrix4f, f3 - f7 + 0.5f, f2 + 0.5f, f3 - 0.5f, f2 + 1.0f, n, n2);
        EspBoxBorderRenderer.process2(drawApi, matrix4f, f3 - 1.0f, f2 + 0.5f, f3 - 0.5f, f2 + f7 - 0.5f, n, n2);
        EspBoxBorderRenderer.process2(drawApi, matrix4f, f + 0.5f, f4 - f7 + 0.5f, f + 1.0f, f4 - 0.5f, n2, n3);
        EspBoxBorderRenderer.process2(drawApi, matrix4f, f + 0.5f, f4 - 1.0f, f + f7 - 0.5f, f4 - 0.5f, n2, n3);
        EspBoxBorderRenderer.process2(drawApi, matrix4f, f3 - 1.0f, f4 - f7 + 0.5f, f3 - 0.5f, f4 - 0.5f, n4, n3);
        EspBoxBorderRenderer.process2(drawApi, matrix4f, f3 - f7 + 0.5f, f4 - 1.0f, f3 - 0.5f, f4 - 0.5f, n4, n3);
    }

    public static void process5(GuiDrawApi drawApi, Matrix4f matrix4f, float f, float f2, float f3, float f4, int n) {
        drawApi.fillRectangle(matrix4f, f, f2, f3 - f, f4 - f2, n);
    }
}

