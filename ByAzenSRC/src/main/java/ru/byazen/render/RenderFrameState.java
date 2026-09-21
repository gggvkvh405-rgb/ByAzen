/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_243
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.byazen.render;

import net.minecraft.class_243;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public final class RenderFrameState {
    public static volatile class_243 cameraPosition;
    public static final Matrix4f projectionMatrix;
    public static final Matrix4f viewMatrix;

    private RenderFrameState() {
    }

    public static void update(class_243 cameraPosition, Matrix4fc projection, Matrix4fc view) {
        RenderFrameState.cameraPosition = cameraPosition;
        projectionMatrix.set(projection);
        viewMatrix.set(view);
    }

    public static void clear() {
        cameraPosition = null;
        projectionMatrix.identity();
        viewMatrix.identity();
    }

    static {
        projectionMatrix = new Matrix4f();
        viewMatrix = new Matrix4f();
    }
}

