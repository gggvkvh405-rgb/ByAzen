/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_1297
 *  net.minecraft.class_238
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_4604
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector2f
 *  org.joml.Vector4f
 */
package ru.byazen.render;

import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4604;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector4f;
import ru.byazen.render.RenderFrameState;

public final class RenderProjection {
    private static final class_310 CLIENT = class_310.method_1551();

    private RenderProjection() {
    }

    public static Matrix4f viewProjectionMatrix() {
        return new Matrix4f((Matrix4fc)RenderFrameState.projectionMatrix).mul((Matrix4fc)RenderFrameState.viewMatrix);
    }

    public static class_4604 frustum() {
        class_4604 frustum = new class_4604(RenderFrameState.viewMatrix, RenderFrameState.projectionMatrix);
        class_243 camera = RenderProjection.cameraPosition();
        frustum.method_23088(camera.field_1352, camera.field_1351, camera.field_1350);
        return frustum;
    }

    public static boolean isVisible(class_1297 entity, class_4604 frustum) {
        return frustum == null || frustum.method_23093(entity.method_5829());
    }

    public static Vector2f projectEntityLabel(class_1297 entity, Matrix4f viewProjection) {
        class_238 box = entity.method_5829();
        return RenderProjection.project((box.field_1323 + box.field_1320) * 0.5, box.field_1325 + 0.25, (box.field_1321 + box.field_1324) * 0.5, viewProjection);
    }

    public static Vector2f projectEntityCenter(class_1297 entity, Matrix4f viewProjection) {
        class_238 box = entity.method_5829();
        return RenderProjection.project((box.field_1323 + box.field_1320) * 0.5, (box.field_1322 + box.field_1325) * 0.5, (box.field_1321 + box.field_1324) * 0.5, viewProjection);
    }

    public static Vector2f project(class_243 position) {
        return RenderProjection.project(position.field_1352, position.field_1351, position.field_1350, RenderProjection.viewProjectionMatrix());
    }

    public static Vector2f project(double x, double y, double z, Matrix4f viewProjection) {
        class_243 camera = RenderProjection.cameraPosition();
        Vector4f clip = new Vector4f((float)(x - camera.field_1352), (float)(y - camera.field_1351), (float)(z - camera.field_1350), 1.0f);
        viewProjection.transform(clip);
        if (clip.w <= 0.001f) {
            return null;
        }
        float normalizedX = clip.x / clip.w;
        float normalizedY = clip.y / clip.w;
        if (normalizedX < -1.2f || normalizedX > 1.2f || normalizedY < -1.2f || normalizedY > 1.2f) {
            return null;
        }
        float width = CLIENT.method_22683().method_4486();
        float height = CLIENT.method_22683().method_4502();
        return new Vector2f((normalizedX * 0.5f + 0.5f) * width, (0.5f - normalizedY * 0.5f) * height);
    }

    public static float tickProgress() {
        return CLIENT.method_61966().method_60637(false);
    }

    private static class_243 cameraPosition() {
        class_243 captured = RenderFrameState.cameraPosition;
        return captured != null ? captured : RenderProjection.CLIENT.field_1773.method_19418().method_71156();
    }
}

