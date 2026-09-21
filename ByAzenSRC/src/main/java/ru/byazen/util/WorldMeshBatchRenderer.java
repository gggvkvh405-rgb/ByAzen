/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_12249
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 */
package ru.byazen.util;

import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import ru.byazen.misc.ModelSurfaceMode;
import ru.byazen.render.ModelRenderBatch;
import ru.byazen.util.InlineMesh;
import ru.byazen.util.ModelRenderOptions;
import ru.byazen.util.ModelRenderQueue;

public final class WorldMeshBatchRenderer {
    public WorldMeshBatchRenderer(String debugName) {
    }

    public void process10(class_4587 matrices, class_4597.class_4598 consumers, class_243 cameraPosition, ModelRenderQueue queue) {
        if (matrices == null || consumers == null || queue == null || queue.isActive()) {
            return;
        }
        class_243 camera = cameraPosition != null ? cameraPosition : class_243.field_1353;
        class_1921 fillLayer = class_12249.method_76023();
        class_1921 outlineLayer = class_12249.method_76015();
        class_4588 fill = consumers.method_73477(fillLayer);
        class_4588 outline = consumers.method_73477(outlineLayer);
        boolean hasFill = false;
        boolean hasOutline = false;
        for (ModelRenderBatch batch : queue.getBatches()) {
            ModelRenderOptions options = batch.options();
            if (options.getModelSurfaceMode() == ModelSurfaceMode.HIDDEN) continue;
            Matrix4f base = new Matrix4f((Matrix4fc)matrices.method_23760().method_23761()).translate((float)(batch.x() - camera.field_1352), (float)(batch.y() - camera.field_1351), (float)(batch.z() - camera.field_1350));
            for (int i = 0; i < batch.meshes().size(); ++i) {
                InlineMesh mesh = batch.meshes().get(i);
                Matrix4f model = new Matrix4f((Matrix4fc)base);
                if (batch.transforms() != null && i < batch.transforms().length && batch.transforms()[i] != null) {
                    model.mul((Matrix4fc)batch.transforms()[i]);
                }
                WorldMeshBatchRenderer.emitTriangles(fill, model, mesh, batch.color());
                hasFill = true;
                if (!options.isActive3()) continue;
                WorldMeshBatchRenderer.emitOutline(outline, model, mesh, options.process8(batch.outlineColor()), options.getFloatType());
                hasOutline = true;
            }
        }
        if (hasFill) {
            consumers.method_22994(fillLayer);
        }
        if (hasOutline) {
            consumers.method_22994(outlineLayer);
        }
    }

    private static void emitTriangles(class_4588 output, Matrix4f matrix, InlineMesh mesh, int color) {
        float[] positions = mesh.getFloatType();
        int[] indices = mesh.getIntType2();
        if (positions == null) {
            return;
        }
        if (indices == null) {
            for (int vertex = 0; vertex < positions.length / 3; ++vertex) {
                WorldMeshBatchRenderer.emitVertex(output, matrix, positions, vertex, color, 1.0f);
            }
            return;
        }
        for (int vertex : indices) {
            WorldMeshBatchRenderer.emitVertex(output, matrix, positions, vertex, color, 1.0f);
        }
    }

    private static void emitOutline(class_4588 output, Matrix4f matrix, InlineMesh mesh, int color, float lineWidth) {
        float[] positions = mesh.getFloatType();
        int[] edges = mesh.getIntType();
        if (positions == null || edges == null) {
            return;
        }
        for (int vertex : edges) {
            WorldMeshBatchRenderer.emitVertex(output, matrix, positions, vertex, color, lineWidth);
        }
    }

    private static void emitVertex(class_4588 output, Matrix4f matrix, float[] positions, int vertex, int color, float lineWidth) {
        int offset = vertex * 3;
        if (offset < 0 || offset + 2 >= positions.length) {
            return;
        }
        output.method_22918((Matrix4fc)matrix, positions[offset], positions[offset + 1], positions[offset + 2]).method_39415(color).method_75298(lineWidth);
    }
}

