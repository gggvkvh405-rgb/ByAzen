/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.buffers.GpuBuffer
 *  com.mojang.blaze3d.buffers.GpuBufferSlice
 *  com.mojang.blaze3d.buffers.Std140Builder
 *  com.mojang.blaze3d.systems.CommandEncoder
 *  com.mojang.blaze3d.systems.RenderPass
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.systems.RenderSystem$class_5590
 *  com.mojang.blaze3d.textures.FilterMode
 *  com.mojang.blaze3d.vertex.VertexFormat$class_5596
 *  net.minecraft.class_243
 *  net.minecraft.class_276
 *  net.minecraft.class_310
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.lwjgl.system.MemoryStack
 */
package ru.wexside.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.class_243;
import net.minecraft.class_276;
import net.minecraft.class_310;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.lwjgl.system.MemoryStack;
import ru.wexside.misc.ClientChat;
import ru.wexside.prediction.PotionImpactMarker;
import ru.wexside.render.ClientRenderPipelines;
import ru.wexside.render.RenderCamera;
import ru.wexside.render.RenderProjection;

public final class DepthSampler {
    private static final float value = 2.5f;
    private static final int slot = 8;
    private GpuBufferSlice gpuBufferSlice;
    private static final int slot2 = 336;
    private static final int slot3 = 136;
    private GpuBuffer gpuBuffer;
    private GpuBuffer gpuBuffer2;
    private static final float value2 = 0.85f;
    private static final float value3 = 0.5f;
    private final Matrix4f matrix4f = new Matrix4f();
    private static final AtomicBoolean ERROR_REPORTED = new AtomicBoolean();

    public void setList(List<PotionImpactMarker> list) {
        block22: {
            if (list.isEmpty()) {
                return;
            }
            class_310 mc = class_310.method_1551();
            class_276 iIllIIiilI2 = mc.method_1522();
            if (iIllIIiilI2 == null || iIllIIiilI2.method_71639() == null || iIllIIiilI2.method_71640() == null) {
                return;
            }
            class_243 vec = RenderCamera.position();
            if (vec == null) {
                return;
            }
            int n = Math.min(list.size(), 8);
            float f = 3.0f;
            this.matrix4f.set((Matrix4fc)RenderProjection.viewProjectionMatrix()).invert();
            try {
                this.update();
                CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
                try (MemoryStack memoryStack = MemoryStack.stackPush();){
                    int n2;
                    Std140Builder std140Builder = Std140Builder.onStack((MemoryStack)memoryStack, (int)336).putMat4f((Matrix4fc)this.matrix4f).putVec4((float)n, 0.5f, 0.85f, 2.5f);
                    for (n2 = 0; n2 < 8; ++n2) {
                        if (n2 < n) {
                            class_243 vec2 = list.get(n2).position();
                            std140Builder.putVec4((float)(vec2.field_1352 - vec.field_1352), (float)(vec2.field_1351 - vec.field_1351), (float)(vec2.field_1350 - vec.field_1350), f);
                            continue;
                        }
                        std140Builder.putVec4(0.0f, 0.0f, 0.0f, 0.0f);
                    }
                    for (n2 = 0; n2 < 8; ++n2) {
                        if (n2 < n) {
                            int n3 = list.get(n2).color();
                            std140Builder.putVec4((float)(n3 >> 16 & 0xFF) / 255.0f, (float)(n3 >> 8 & 0xFF) / 255.0f, (float)(n3 & 0xFF) / 255.0f, 1.0f);
                            continue;
                        }
                        std140Builder.putVec4(0.0f, 0.0f, 0.0f, 0.0f);
                    }
                    commandEncoder.writeToBuffer(this.gpuBufferSlice, std140Builder.get());
                }
                GpuBuffer vertexBuffer = this.getGpuBuffer();
                RenderSystem.class_5590 indexBuffer = RenderSystem.getSequentialBuffer((VertexFormat.class_5596)VertexFormat.class_5596.field_27382);
                GpuBuffer gpuBuffer = indexBuffer.method_68274(6);
                try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "wex/potion-decal", iIllIIiilI2.method_71639(), OptionalInt.empty());){
                    renderPass.setPipeline(ClientRenderPipelines.POTION_DECAL);
                    RenderSystem.bindDefaultUniforms((RenderPass)renderPass);
                    renderPass.setUniform("PotionDecal", this.gpuBufferSlice);
                    renderPass.setVertexBuffer(0, vertexBuffer);
                    renderPass.setIndexBuffer(gpuBuffer, indexBuffer.method_31924());
                    renderPass.bindTexture("DepthSampler", iIllIIiilI2.method_71640(), RenderSystem.getSamplerCache().method_75294(FilterMode.NEAREST));
                    renderPass.drawIndexed(0, 0, 6, 1);
                }
            }
            catch (Throwable throwable) {
                if (!ERROR_REPORTED.compareAndSet(false, true)) break block22;
                Throwable throwable2 = throwable.getCause() != null ? throwable.getCause() : throwable;
                String string = String.valueOf(throwable2);
                ClientChat.send("PotionDecal error: " + string);
            }
        }
    }

    private GpuBuffer getGpuBuffer() {
        if (this.gpuBuffer2 != null) {
            return this.gpuBuffer2;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(48).order(ByteOrder.nativeOrder());
        byteBuffer.putFloat(-1.0f).putFloat(-1.0f).putFloat(0.0f);
        byteBuffer.putFloat(1.0f).putFloat(-1.0f).putFloat(0.0f);
        byteBuffer.putFloat(1.0f).putFloat(1.0f).putFloat(0.0f);
        byteBuffer.putFloat(-1.0f).putFloat(1.0f).putFloat(0.0f);
        byteBuffer.flip();
        this.gpuBuffer2 = RenderSystem.getDevice().createBuffer(() -> "wex/potion-decal-quad", 32, byteBuffer);
        return this.gpuBuffer2;
    }

    private void update() {
        if (this.gpuBuffer == null) {
            this.gpuBuffer = RenderSystem.getDevice().createBuffer(() -> "wex/potion-decal-ubo", 136, 336L);
            this.gpuBufferSlice = this.gpuBuffer.slice();
        }
    }
}

