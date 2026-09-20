/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_12249
 *  net.minecraft.class_1921
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_4604
 *  net.minecraft.class_7833
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.wexside.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4604;
import net.minecraft.class_7833;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import ru.wexside.misc.SpatialTransform;
import ru.wexside.render.RenderProjection;
import ru.wexside.util.InlineMesh;
import ru.wexside.util.ModelRenderOptions;

public class InlineMeshRenderer {
    public InlineMeshRenderer(String name) {
    }

    public InlineMeshRenderer(String name, boolean cull) {
    }

    public InlineMeshRenderer(String name, class_2960 vertexShader, class_2960 fragmentShader, boolean cull) {
    }

    public InlineMeshRenderer(String name, class_2960 vertexShader, class_2960 fragmentShader) {
    }

    public void process3(class_4587 matrices, class_4597.class_4598 consumers, class_243 cameraPosition, List<InlineMesh> meshes, List<float[]> positionOverrides, List<Matrix4f> localTransforms, int color, List<SpatialTransform> transforms, ModelRenderOptions style) {
        if (meshes == null || meshes.isEmpty() || transforms == null || transforms.isEmpty()) {
            return;
        }
        class_243 camera = cameraPosition == null ? class_243.field_1353 : cameraPosition;
        ModelRenderOptions renderStyle = style == null ? ModelRenderOptions.getAlternateRenderOptions() : style;
        List<SpatialTransform> visibleTransforms = InlineMeshRenderer.visibleTransforms(transforms);
        if (visibleTransforms.isEmpty()) {
            return;
        }
        class_1921 fillLayer = class_12249.method_76023();
        class_4588 fill = consumers.method_73477(fillLayer);
        for (SpatialTransform transform : visibleTransforms) {
            matrices.method_22903();
            matrices.method_22904(transform.centerX() - camera.field_1352, transform.centerY() - camera.field_1351, transform.centerZ() - camera.field_1350);
            InlineMeshRenderer.applyRotation(matrices, transform);
            matrices.method_22905(transform.scaleX(), transform.scaleY(), transform.scaleZ());
            InlineMeshRenderer.emitFilledMeshes(fill, meshes, positionOverrides, localTransforms, (Matrix4fc)matrices.method_23760().method_23761(), color);
            matrices.method_22909();
        }
        consumers.method_22994(fillLayer);
        InlineMeshRenderer.emitTransformedOutlines(matrices, consumers, camera, meshes, positionOverrides, localTransforms, color, visibleTransforms, renderStyle);
    }

    public void process11(class_4587 matrices, class_4597.class_4598 consumers, class_243 cameraPosition, List<InlineMesh> meshes, List<float[]> positionOverrides, List<Matrix4f> localTransforms, int color, List<Matrix4f> worldTransforms, ModelRenderOptions style) {
        if (meshes == null || meshes.isEmpty() || worldTransforms == null || worldTransforms.isEmpty()) {
            return;
        }
        class_243 camera = cameraPosition == null ? class_243.field_1353 : cameraPosition;
        ModelRenderOptions renderStyle = style == null ? ModelRenderOptions.getAlternateRenderOptions() : style;
        Matrix4f base = new Matrix4f((Matrix4fc)matrices.method_23760().method_23761()).translate((float)(-camera.field_1352), (float)(-camera.field_1351), (float)(-camera.field_1350));
        class_1921 fillLayer = class_12249.method_76023();
        class_4588 fill = consumers.method_73477(fillLayer);
        for (Matrix4f worldTransform : worldTransforms) {
            if (worldTransform == null) continue;
            InlineMeshRenderer.emitFilledMeshes(fill, meshes, positionOverrides, localTransforms, (Matrix4fc)new Matrix4f((Matrix4fc)base).mul((Matrix4fc)worldTransform), color);
        }
        consumers.method_22994(fillLayer);
        if (!renderStyle.isActive3()) {
            return;
        }
        int outlineColor = renderStyle.process8(color);
        class_1921 outlineLayer = renderStyle.isActive2() ? class_12249.method_76015() : class_12249.method_76668();
        class_4588 outline = consumers.method_73477(outlineLayer);
        for (Matrix4f worldTransform : worldTransforms) {
            if (worldTransform == null) continue;
            InlineMeshRenderer.emitMeshOutlines(outline, meshes, positionOverrides, localTransforms, (Matrix4fc)new Matrix4f((Matrix4fc)base).mul((Matrix4fc)worldTransform), outlineColor, renderStyle.getFloatType());
        }
        consumers.method_22994(outlineLayer);
    }

    private static void emitTransformedOutlines(class_4587 matrices, class_4597.class_4598 consumers, class_243 camera, List<InlineMesh> meshes, List<float[]> positionOverrides, List<Matrix4f> localTransforms, int color, List<SpatialTransform> transforms, ModelRenderOptions style) {
        if (!style.isActive3()) {
            return;
        }
        class_1921 layer = style.isActive2() ? class_12249.method_76015() : class_12249.method_76668();
        class_4588 outline = consumers.method_73477(layer);
        int outlineColor = style.process8(color);
        for (SpatialTransform transform : transforms) {
            matrices.method_22903();
            matrices.method_22904(transform.centerX() - camera.field_1352, transform.centerY() - camera.field_1351, transform.centerZ() - camera.field_1350);
            InlineMeshRenderer.applyRotation(matrices, transform);
            matrices.method_22905(transform.scaleX(), transform.scaleY(), transform.scaleZ());
            InlineMeshRenderer.emitMeshOutlines(outline, meshes, positionOverrides, localTransforms, (Matrix4fc)matrices.method_23760().method_23761(), outlineColor, style.getFloatType());
            matrices.method_22909();
        }
        consumers.method_22994(layer);
    }

    private static void applyRotation(class_4587 matrices, SpatialTransform transform) {
        if (transform.yawDegrees() != 0.0f) {
            matrices.method_22907((Quaternionfc)class_7833.field_40716.rotationDegrees(transform.yawDegrees()));
        }
        if (transform.pitchDegrees() != 0.0f) {
            matrices.method_22907((Quaternionfc)class_7833.field_40714.rotationDegrees(transform.pitchDegrees()));
        }
        if (transform.rollDegrees() != 0.0f) {
            matrices.method_22907((Quaternionfc)class_7833.field_40718.rotationDegrees(transform.rollDegrees()));
        }
    }

    private static List<SpatialTransform> visibleTransforms(List<SpatialTransform> transforms) {
        class_4604 frustum = RenderProjection.frustum();
        if (frustum == null) {
            return transforms.stream().filter(Objects::nonNull).toList();
        }
        ArrayList<SpatialTransform> visible = new ArrayList<SpatialTransform>(transforms.size());
        for (SpatialTransform transform : transforms) {
            if (transform == null) continue;
            double radius = Math.max(Math.max(Math.abs(transform.scaleX()), Math.abs(transform.scaleY())), Math.abs(transform.scaleZ()));
            class_238 bounds = new class_238(transform.centerX() - radius, transform.centerY() - radius, transform.centerZ() - radius, transform.centerX() + radius, transform.centerY() + radius, transform.centerZ() + radius);
            if (!frustum.method_23093(bounds)) continue;
            visible.add(transform);
        }
        return visible;
    }

    private static void emitFilledMeshes(class_4588 vertices, List<InlineMesh> meshes, List<float[]> positionOverrides, List<Matrix4f> localTransforms, Matrix4fc baseMatrix, int fallbackColor) {
        for (int meshIndex = 0; meshIndex < meshes.size(); ++meshIndex) {
            InlineMesh mesh = meshes.get(meshIndex);
            if (mesh == null) continue;
            float[] positions = InlineMeshRenderer.positions(mesh, positionOverrides, meshIndex);
            int[] indices = mesh.getIntType2();
            if (positions == null || indices == null) continue;
            Matrix4f matrix = InlineMeshRenderer.localMatrix(baseMatrix, localTransforms, meshIndex);
            int index = 0;
            while (index + 2 < indices.length) {
                InlineMeshRenderer.emitVertex(vertices, (Matrix4fc)matrix, positions, mesh.getFloatType4(), indices[index], fallbackColor);
                InlineMeshRenderer.emitVertex(vertices, (Matrix4fc)matrix, positions, mesh.getFloatType4(), indices[index + 1], fallbackColor);
                InlineMeshRenderer.emitVertex(vertices, (Matrix4fc)matrix, positions, mesh.getFloatType4(), indices[index + 2], fallbackColor);
                InlineMeshRenderer.emitVertex(vertices, (Matrix4fc)matrix, positions, mesh.getFloatType4(), indices[index + 2], fallbackColor);
                index += 3;
            }
        }
    }

    private static void emitMeshOutlines(class_4588 vertices, List<InlineMesh> meshes, List<float[]> positionOverrides, List<Matrix4f> localTransforms, Matrix4fc baseMatrix, int color, float lineWidth) {
        for (int meshIndex = 0; meshIndex < meshes.size(); ++meshIndex) {
            InlineMesh mesh = meshes.get(meshIndex);
            if (mesh == null) continue;
            float[] positions = InlineMeshRenderer.positions(mesh, positionOverrides, meshIndex);
            int[] edges = mesh.getIntType();
            if (positions == null) continue;
            if (edges == null || edges.length < 2) {
                edges = InlineMeshRenderer.triangleEdges(mesh.getIntType2());
            }
            Matrix4f matrix = InlineMeshRenderer.localMatrix(baseMatrix, localTransforms, meshIndex);
            int index = 0;
            while (index + 1 < edges.length) {
                InlineMeshRenderer.emitLine(vertices, (Matrix4fc)matrix, positions, edges[index], edges[index + 1], color, lineWidth);
                index += 2;
            }
        }
    }

    private static float[] positions(InlineMesh mesh, List<float[]> overrides, int index) {
        float[] override;
        if (overrides != null && index < overrides.size() && (override = overrides.get(index)) != null && override.length >= 3) {
            return override;
        }
        return mesh.getFloatType();
    }

    private static Matrix4f localMatrix(Matrix4fc base, List<Matrix4f> transforms, int index) {
        Matrix4f matrix = new Matrix4f(base);
        if (transforms != null && index < transforms.size() && transforms.get(index) != null) {
            matrix.mul((Matrix4fc)transforms.get(index));
        }
        return matrix;
    }

    private static void emitVertex(class_4588 vertices, Matrix4fc matrix, float[] positions, float[] colors, int vertexIndex, int fallbackColor) {
        int offset = vertexIndex * 3;
        if (offset < 0 || offset + 2 >= positions.length) {
            return;
        }
        vertices.method_22918(matrix, positions[offset], positions[offset + 1], positions[offset + 2]).method_39415(InlineMeshRenderer.vertexColor(colors, vertexIndex, fallbackColor));
    }

    private static int vertexColor(float[] colors, int vertexIndex, int fallbackColor) {
        if (colors == null) {
            return fallbackColor;
        }
        int offset = vertexIndex * 4;
        if (offset + 3 >= colors.length) {
            return fallbackColor;
        }
        int red = Math.round(Math.clamp(colors[offset], 0.0f, 1.0f) * 255.0f);
        int green = Math.round(Math.clamp(colors[offset + 1], 0.0f, 1.0f) * 255.0f);
        int blue = Math.round(Math.clamp(colors[offset + 2], 0.0f, 1.0f) * 255.0f);
        int alpha = Math.round(Math.clamp(colors[offset + 3], 0.0f, 1.0f) * 255.0f);
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    private static void emitLine(class_4588 vertices, Matrix4fc matrix, float[] positions, int first, int second, int color, float width) {
        int a = first * 3;
        int b = second * 3;
        if (a < 0 || b < 0 || a + 2 >= positions.length || b + 2 >= positions.length) {
            return;
        }
        float dx = positions[b] - positions[a];
        float dy = positions[b + 1] - positions[a + 1];
        float dz = positions[b + 2] - positions[a + 2];
        float length = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (length > 0.0f) {
            dx /= length;
            dy /= length;
            dz /= length;
        }
        vertices.method_22918(matrix, positions[a], positions[a + 1], positions[a + 2]).method_39415(color).method_22914(dx, dy, dz).method_75298(width);
        vertices.method_22918(matrix, positions[b], positions[b + 1], positions[b + 2]).method_39415(color).method_22914(dx, dy, dz).method_75298(width);
    }

    private static int[] triangleEdges(int[] triangles) {
        if (triangles == null) {
            return new int[0];
        }
        int[] edges = new int[triangles.length / 3 * 6];
        int target = 0;
        int index = 0;
        while (index + 2 < triangles.length) {
            int a = triangles[index];
            int b = triangles[index + 1];
            int c = triangles[index + 2];
            edges[target++] = a;
            edges[target++] = b;
            edges[target++] = b;
            edges[target++] = c;
            edges[target++] = c;
            edges[target++] = a;
            index += 3;
        }
        return edges;
    }
}

