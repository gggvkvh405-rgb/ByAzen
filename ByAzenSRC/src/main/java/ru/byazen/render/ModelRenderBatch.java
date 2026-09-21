/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package ru.byazen.render;

import java.util.List;
import org.joml.Matrix4f;
import ru.byazen.util.InlineMesh;
import ru.byazen.util.ModelRenderOptions;

public record ModelRenderBatch(List<InlineMesh> meshes, Matrix4f[] transforms, int color, int outlineColor, double x, double y, double z, double radius, ModelRenderOptions options) {
}

