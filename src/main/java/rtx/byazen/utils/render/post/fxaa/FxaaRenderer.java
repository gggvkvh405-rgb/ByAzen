package rtx.byazen.utils.render.post.fxaa;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.nio.ByteBuffer;
import java.util.OptionalInt;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.system.MemoryStack;
import rtx.byazen.utils.render.others.RenderSampler;

/**
 * Сглаживание кадра (идея №55 из IDEAS.md).
 * <p>
 * Полноценный пост-процесс: кадр мира копируется в отдельную текстуру, а затем по ней проходит
 * сглаживающий проход, который ищет края по яркости и мягко размывает их вдоль найденного
 * направления. Никакого «мыла» целиком: ровные области остаются резкими, лесенка на краях
 * исчезает. Есть четыре режима качества и субпиксельный режим.
 */
public final class FxaaRenderer {

    private static final Identifier PIPELINE_ID = FxaaRenderer.id("pipeline/post/fxaa/main");
    private static final Identifier VERTEX_SHADER = FxaaRenderer.id("post/fxaa/quad");
    private static final Identifier FRAGMENT_SHADER = FxaaRenderer.id("post/fxaa/main");
    private static final int UNIFORM_SIZE = 48;

    private static RenderPipeline pipeline;
    private static GpuBuffer uniformBuffer;
    private static GpuTexture sceneTexture;
    private static GpuTextureView sceneTextureView;
    private static int sceneWidth = -1;
    private static int sceneHeight = -1;
    private static boolean disabledAfterError;

    private FxaaRenderer() {
    }

    public static void clear() {
        FxaaRenderer.closeScene();
        FxaaRenderer.closeBuffer();
    }

    public static boolean isDisabledAfterError() {
        return disabledAfterError;
    }

    private static Identifier id(String string) {
        return Identifier.of((String)"byazen", (String)string);
    }

    private static void init() {
        if (disabledAfterError) {
            return;
        }
        try {
            if (pipeline == null) {
                pipeline = RenderPipelines.register(RenderPipeline.builder(new RenderPipeline.Snippet[0])
                        .withLocation(PIPELINE_ID)
                        .withVertexShader(VERTEX_SHADER)
                        .withFragmentShader(FRAGMENT_SHADER)
                        .withVertexFormat(VertexFormats.EMPTY, VertexFormat.DrawMode.TRIANGLES)
                        .withUniform("FxaaParams", UniformType.UNIFORM_BUFFER)
                        .withSampler("Source")
                        .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                        .withDepthWrite(false)
                        .withCull(false)
                        .build());
            }
            if (uniformBuffer == null || uniformBuffer.isClosed() || uniformBuffer.size() < 48L) {
                FxaaRenderer.closeBuffer();
                uniformBuffer = RenderSystem.getDevice().createBuffer(() -> "byazen:fxaa_uniforms", 136, 48L);
            }
        }
        catch (Throwable throwable) {
            disabledAfterError = true;
            pipeline = null;
            FxaaRenderer.closeBuffer();
        }
    }

    /**
     * Прогоняет сглаживание по кадру мира.
     *
     * @param mode      режим качества: 0 — низкое, 1 — среднее, 2 — высокое, 3 — морфологическое
     * @param threshold порог поиска края (0.03…0.40)
     * @param blend     сила подмешивания сглаженного пикселя (0…1)
     * @param subpixel  субпиксельная мягкость (0…1)
     * @param sharpen   добавка резкости после сглаживания (0…1)
     */
    public static void apply(Framebuffer framebuffer, int mode, float threshold, float blend, float subpixel, float sharpen) {
        if (disabledAfterError || framebuffer == null || framebuffer.getColorAttachment() == null || framebuffer.getColorAttachmentView() == null) {
            return;
        }
        if (framebuffer.textureWidth <= 0 || framebuffer.textureHeight <= 0) {
            return;
        }
        FxaaRenderer.init();
        if (pipeline == null || uniformBuffer == null) {
            return;
        }
        if (!FxaaRenderer.ensureScene(framebuffer.textureWidth, framebuffer.textureHeight)) {
            return;
        }
        try {
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            commandEncoder.copyTextureToTexture(framebuffer.getColorAttachment(), sceneTexture, 0, 0, 0, 0, 0, framebuffer.textureWidth, framebuffer.textureHeight);
            FxaaRenderer.writeUniform(commandEncoder, 1.0f / (float)framebuffer.textureWidth, 1.0f / (float)framebuffer.textureHeight,
                    (float)mode, FxaaRenderer.clamp(subpixel, 0.0f, 1.0f), FxaaRenderer.clamp(threshold, 0.0f, 0.5f),
                    FxaaRenderer.clamp(blend, 0.0f, 1.0f), FxaaRenderer.clamp(sharpen, 0.0f, 1.0f));
            try (RenderPass renderPass = commandEncoder.createRenderPass(() -> "byazen:fxaa", framebuffer.getColorAttachmentView(), OptionalInt.empty());){
                renderPass.setPipeline(pipeline);
                renderPass.setUniform("FxaaParams", uniformBuffer);
                renderPass.bindTexture("Source", sceneTextureView, RenderSampler.linear());
                renderPass.draw(0, 6);
            }
        }
        catch (Throwable throwable) {
            disabledAfterError = true;
            FxaaRenderer.closeScene();
            FxaaRenderer.closeBuffer();
        }
    }

    private static boolean ensureScene(int n, int n2) {
        GpuDevice gpuDevice = RenderSystem.tryGetDevice();
        if (gpuDevice == null) {
            return false;
        }
        if (sceneTexture != null && sceneTextureView != null && sceneWidth == n && sceneHeight == n2) {
            return true;
        }
        FxaaRenderer.closeScene();
        sceneTexture = gpuDevice.createTexture(() -> "byazen:fxaa_scene", 5, TextureFormat.RGBA8, n, n2, 1, 1);
        sceneTextureView = gpuDevice.createTextureView(sceneTexture);
        sceneWidth = n;
        sceneHeight = n2;
        return true;
    }

    private static void writeUniform(CommandEncoder commandEncoder, float f, float f2, float f3, float f4, float f5, float f6, float f7) {
        if (uniformBuffer == null || uniformBuffer.isClosed()) {
            return;
        }
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.calloc(48);
            byteBuffer.putFloat(0, f);
            byteBuffer.putFloat(4, f2);
            byteBuffer.putFloat(8, f3);
            byteBuffer.putFloat(12, f4);
            byteBuffer.putFloat(16, f5);
            byteBuffer.putFloat(20, 0.02f);
            byteBuffer.putFloat(24, f6);
            byteBuffer.putFloat(28, f7);
            byteBuffer.putFloat(32, 0.0f);
            byteBuffer.putFloat(36, 0.0f);
            byteBuffer.putFloat(40, 0.0f);
            byteBuffer.putFloat(44, 0.0f);
            byteBuffer.position(0);
            commandEncoder.writeToBuffer(uniformBuffer.slice(0L, 48L), byteBuffer);
        }
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    private static void closeScene() {
        if (sceneTextureView != null) {
            sceneTextureView.close();
            sceneTextureView = null;
        }
        if (sceneTexture != null) {
            sceneTexture.close();
            sceneTexture = null;
        }
        sceneWidth = -1;
        sceneHeight = -1;
    }

    private static void closeBuffer() {
        if (uniformBuffer != null) {
            uniformBuffer.close();
            uniformBuffer = null;
        }
    }
}
