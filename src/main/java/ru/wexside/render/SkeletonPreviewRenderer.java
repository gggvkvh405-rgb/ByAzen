/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_12249
 *  net.minecraft.class_1657
 *  net.minecraft.class_1921
 *  net.minecraft.class_4587
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package ru.wexside.render;

import net.minecraft.class_12249;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import ru.wexside.misc.CaptureFramebuffer;
import ru.wexside.misc.ModelEspSettings;
import ru.wexside.render.OffscreenRenderManager;

public final class SkeletonPreviewRenderer {
    private final CaptureFramebuffer framebuffer = new CaptureFramebuffer();

    public void close() {
        this.framebuffer.method_1238();
    }

    public int render(OffscreenRenderManager pipeline, class_1657 player, int framebufferWidth, int framebufferHeight, float centerX, float centerY, float scale, float verticalOffset, float bodyYaw, ModelEspSettings settings) {
        if (pipeline == null || player == null || settings == null || framebufferWidth <= 0 || framebufferHeight <= 0) {
            return 0;
        }
        int color = settings.getOutlineColor();
        pipeline.process3(this.framebuffer, framebufferWidth, framebufferHeight, centerX, centerY, scale, verticalOffset, bodyYaw, (matrices, consumers) -> SkeletonPreviewRenderer.drawSkeleton(matrices, consumers, player, color, settings.isModelStyle()));
        return this.framebuffer.getIntType();
    }

    private static void drawSkeleton(class_4587 matrices, class_4597.class_4598 consumers, class_1657 player, int color, boolean modelStyle) {
        class_1921 layer = class_12249.method_76668();
        class_4588 vertices = consumers.method_73477(layer);
        Matrix4f matrix = new Matrix4f((Matrix4fc)matrices.method_23760().method_23761());
        float bodyLean = player.method_5715() ? 0.16f : 0.0f;
        Vector3f hips = new Vector3f(0.0f, 0.72f, bodyLean);
        Vector3f chest = new Vector3f(0.0f, 1.35f, bodyLean);
        Vector3f neck = new Vector3f(0.0f, 1.52f, bodyLean);
        Vector3f head = new Vector3f(0.0f, 1.78f, bodyLean);
        Vector3f leftShoulder = new Vector3f(0.34f, 1.38f, bodyLean);
        Vector3f rightShoulder = new Vector3f(-0.34f, 1.38f, bodyLean);
        Vector3f leftHand = new Vector3f(0.48f, 0.82f, bodyLean);
        Vector3f rightHand = new Vector3f(-0.48f, 0.82f, bodyLean);
        Vector3f leftHip = new Vector3f(0.18f, 0.7f, bodyLean);
        Vector3f rightHip = new Vector3f(-0.18f, 0.7f, bodyLean);
        Vector3f leftFoot = new Vector3f(0.2f, 0.02f, 0.0f);
        Vector3f rightFoot = new Vector3f(-0.2f, 0.02f, 0.0f);
        SkeletonPreviewRenderer.line(vertices, matrix, hips, chest, color);
        SkeletonPreviewRenderer.line(vertices, matrix, chest, neck, color);
        SkeletonPreviewRenderer.line(vertices, matrix, neck, head, color);
        SkeletonPreviewRenderer.line(vertices, matrix, leftShoulder, rightShoulder, color);
        SkeletonPreviewRenderer.line(vertices, matrix, leftShoulder, leftHand, color);
        SkeletonPreviewRenderer.line(vertices, matrix, rightShoulder, rightHand, color);
        SkeletonPreviewRenderer.line(vertices, matrix, leftHip, rightHip, color);
        SkeletonPreviewRenderer.line(vertices, matrix, leftHip, leftFoot, color);
        SkeletonPreviewRenderer.line(vertices, matrix, rightHip, rightFoot, color);
        if (modelStyle) {
            SkeletonPreviewRenderer.line(vertices, matrix, leftShoulder, leftHip, color);
            SkeletonPreviewRenderer.line(vertices, matrix, rightShoulder, rightHip, color);
            SkeletonPreviewRenderer.line(vertices, matrix, leftHand, new Vector3f(0.58f, 0.55f, bodyLean), color);
            SkeletonPreviewRenderer.line(vertices, matrix, rightHand, new Vector3f(-0.58f, 0.55f, bodyLean), color);
        }
        consumers.method_22994(layer);
    }

    private static void line(class_4588 vertices, Matrix4f matrix, Vector3f from, Vector3f to, int color) {
        Vector3f direction = new Vector3f((Vector3fc)to).sub((Vector3fc)from);
        if (direction.lengthSquared() > 0.0f) {
            direction.normalize();
        }
        vertices.method_22918((Matrix4fc)matrix, from.x, from.y, from.z).method_39415(color).method_22914(direction.x, direction.y, direction.z).method_75298(2.0f);
        vertices.method_22918((Matrix4fc)matrix, to.x, to.y, to.z).method_39415(color).method_22914(direction.x, direction.y, direction.z).method_75298(2.0f);
    }
}

