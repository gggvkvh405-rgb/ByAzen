/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_12249
 *  net.minecraft.class_1297
 *  net.minecraft.class_1657
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_310
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597
 *  net.minecraft.class_4597$class_4598
 *  net.minecraft.class_9799
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Vector3f
 *  org.joml.Vector3fc
 */
package ru.byazen.render;

import net.minecraft.class_12249;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_9799;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import ru.byazen.event.EventBus;
import ru.byazen.event.WorldRenderEvent;
import ru.byazen.misc.ModelEspSettings;
import ru.byazen.model.esp.EspTargetClassifier;
import ru.byazen.render.RenderCamera;
import ru.byazen.util.EspFeatureRegistry;

public final class WorldSkeletonRenderer {
    private final EspFeatureRegistry espSettings;
    private final class_9799 bufferAllocator = new class_9799(65536);
    private final class_4597.class_4598 vertexConsumers = class_4597.method_22991((class_9799)this.bufferAllocator);

    public WorldSkeletonRenderer(EventBus eventBus, EspFeatureRegistry espSettings) {
        this.espSettings = espSettings;
        eventBus.subscribe(WorldRenderEvent.class, this::render);
    }

    private void render(WorldRenderEvent event) {
        class_310 client = class_310.method_1551();
        class_243 camera = RenderCamera.position();
        if (!this.espSettings.hasEnabledModelEsp() || client.field_1687 == null || client.field_1724 == null || camera == null) {
            return;
        }
        Matrix4f matrix = new Matrix4f((Matrix4fc)event.getMatrices().method_23760().method_23761());
        this.renderSkeletons(matrix, client, camera, event.getFloatType());
    }

    private void renderSkeletons(Matrix4f matrix, class_310 client, class_243 camera, float tickProgress) {
        class_1921 layer = class_12249.method_76668();
        class_4588 vertices = null;
        for (class_1657 player : client.field_1687.method_18456()) {
            ModelEspSettings settings;
            if (player == client.field_1724 || !player.method_5805() || (settings = this.settingsFor(player)) == null || !settings.isEnabled() || settings.isModelStyle()) continue;
            if (vertices == null) {
                vertices = this.vertexConsumers.method_73477(layer);
            }
            WorldSkeletonRenderer.drawPlayer(vertices, matrix, player, camera, tickProgress, settings.getOutlineColor());
        }
        if (vertices != null) {
            this.vertexConsumers.method_22994(layer);
        }
    }

    private ModelEspSettings settingsFor(class_1657 player) {
        return this.espSettings.getModelEspSettings(EspTargetClassifier.relation((class_1297)player));
    }

    private static void drawPlayer(class_4588 vertices, Matrix4f matrix, class_1657 player, class_243 camera, float tickProgress, int color) {
        class_243 position = player.method_30950(tickProgress).method_1020(camera);
        float yaw = (float)Math.toRadians(-player.method_61415(tickProgress));
        float sin = (float)Math.sin(yaw);
        float cos = (float)Math.cos(yaw);
        float lean = player.method_5715() ? 0.18f : 0.0f;
        Vector3f hips = WorldSkeletonRenderer.point(position, 0.0f, 0.72f, lean, sin, cos);
        Vector3f chest = WorldSkeletonRenderer.point(position, 0.0f, 1.35f, lean, sin, cos);
        Vector3f neck = WorldSkeletonRenderer.point(position, 0.0f, 1.52f, lean, sin, cos);
        Vector3f head = WorldSkeletonRenderer.point(position, 0.0f, 1.79f, lean, sin, cos);
        Vector3f leftShoulder = WorldSkeletonRenderer.point(position, 0.34f, 1.38f, lean, sin, cos);
        Vector3f rightShoulder = WorldSkeletonRenderer.point(position, -0.34f, 1.38f, lean, sin, cos);
        Vector3f leftHand = WorldSkeletonRenderer.point(position, 0.5f, 0.82f, lean, sin, cos);
        Vector3f rightHand = WorldSkeletonRenderer.point(position, -0.5f, 0.82f, lean, sin, cos);
        Vector3f leftHip = WorldSkeletonRenderer.point(position, 0.18f, 0.7f, lean, sin, cos);
        Vector3f rightHip = WorldSkeletonRenderer.point(position, -0.18f, 0.7f, lean, sin, cos);
        Vector3f leftFoot = WorldSkeletonRenderer.point(position, 0.2f, 0.02f, 0.0f, sin, cos);
        Vector3f rightFoot = WorldSkeletonRenderer.point(position, -0.2f, 0.02f, 0.0f, sin, cos);
        WorldSkeletonRenderer.line(vertices, matrix, hips, chest, color, 2.0f);
        WorldSkeletonRenderer.line(vertices, matrix, chest, neck, color, 2.0f);
        WorldSkeletonRenderer.line(vertices, matrix, neck, head, color, 2.0f);
        WorldSkeletonRenderer.line(vertices, matrix, leftShoulder, rightShoulder, color, 2.0f);
        WorldSkeletonRenderer.line(vertices, matrix, leftShoulder, leftHand, color, 2.0f);
        WorldSkeletonRenderer.line(vertices, matrix, rightShoulder, rightHand, color, 2.0f);
        WorldSkeletonRenderer.line(vertices, matrix, leftHip, rightHip, color, 2.0f);
        WorldSkeletonRenderer.line(vertices, matrix, leftHip, leftFoot, color, 2.0f);
        WorldSkeletonRenderer.line(vertices, matrix, rightHip, rightFoot, color, 2.0f);
    }

    private static Vector3f point(class_243 origin, float localX, float localY, float localZ, float sin, float cos) {
        float rotatedX = localX * cos - localZ * sin;
        float rotatedZ = localX * sin + localZ * cos;
        return new Vector3f((float)origin.field_1352 + rotatedX, (float)origin.field_1351 + localY, (float)origin.field_1350 + rotatedZ);
    }

    private static void line(class_4588 vertices, Matrix4f matrix, Vector3f from, Vector3f to, int color, float width) {
        Vector3f direction = new Vector3f((Vector3fc)to).sub((Vector3fc)from);
        if (direction.lengthSquared() > 0.0f) {
            direction.normalize();
        }
        vertices.method_22918((Matrix4fc)matrix, from.x, from.y, from.z).method_39415(color).method_22914(direction.x, direction.y, direction.z).method_75298(width);
        vertices.method_22918((Matrix4fc)matrix, to.x, to.y, to.z).method_39415(color).method_22914(direction.x, direction.y, direction.z).method_75298(width);
    }
}

