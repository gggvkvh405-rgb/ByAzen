/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.class_12249
 *  net.minecraft.class_1921
 *  net.minecraft.class_243
 *  net.minecraft.class_2960
 *  net.minecraft.class_310
 *  net.minecraft.class_4588
 *  net.minecraft.class_4597$class_4598
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionfc
 */
package ru.byazen.render;

import net.minecraft.class_12249;
import net.minecraft.class_1921;
import net.minecraft.class_243;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;

public final class ParticleBillboardRenderer {
    private ParticleBillboardRenderer() {
    }

    public static void draw(double x, double y, double z, float width, float height, int color, class_2960 texture, boolean cutout, float rotationDegrees, float u1, float v1, float u2, float v2) {
        class_310 client = class_310.method_1551();
        if (client.field_1687 == null || texture == null) {
            return;
        }
        class_243 cameraPosition = client.field_1773.method_19418().method_71156();
        Matrix4f transform = new Matrix4f().translation((float)(x - cameraPosition.field_1352), (float)(y - cameraPosition.field_1351), (float)(z - cameraPosition.field_1350)).rotate((Quaternionfc)client.field_1773.method_19418().method_23767()).rotateZ((float)Math.toRadians(rotationDegrees));
        class_1921 layer = cutout ? class_12249.method_75994((class_2960)texture) : class_12249.method_76000((class_2960)texture);
        class_4597.class_4598 consumers = client.method_22940().method_23000();
        class_4588 vertices = consumers.method_73477(layer);
        float halfWidth = width * 0.5f;
        float halfHeight = height * 0.5f;
        vertices.method_22918((Matrix4fc)transform, -halfWidth, -halfHeight, 0.0f).method_22913(u1, v2).method_39415(color);
        vertices.method_22918((Matrix4fc)transform, halfWidth, -halfHeight, 0.0f).method_22913(u2, v2).method_39415(color);
        vertices.method_22918((Matrix4fc)transform, halfWidth, halfHeight, 0.0f).method_22913(u2, v1).method_39415(color);
        vertices.method_22918((Matrix4fc)transform, -halfWidth, halfHeight, 0.0f).method_22913(u1, v1).method_39415(color);
        consumers.method_22994(layer);
    }
}

