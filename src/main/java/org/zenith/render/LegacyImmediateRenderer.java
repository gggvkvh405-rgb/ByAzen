package org.zenith.render;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.nio.ByteBuffer;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.MappableRingBuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.zenith.core.ShaderWrapper;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;

/**
 * Compatibility renderer for the immediate-mode meshes used by the 1.21.4 client.
 * Minecraft 1.21.11 removed BufferRenderer and global shader selection, so every
 * mesh is submitted through an explicit RenderPipeline and RenderPass here.
 */
public final class LegacyImmediateRenderer {
   private static final Map<PipelineKey, RenderPipeline> PIPELINES = new HashMap<>();
   private static final Map<VertexFormat, MappableRingBuffer> GUI_VERTEX_BUFFERS = new HashMap<>();
   private static GpuTextureView texture;
   private static AbstractTexture managedTexture;
   private static final Map<String, GpuTextureView> namedTextures = new HashMap<>();
   private static final Map<String, FilterMode> namedTextureFilters = new HashMap<>();
   private static Framebuffer target;
   private static ShaderWrapper shader;
   private static BlendFunction blend = BlendFunction.TRANSLUCENT;
   private static final Vector4f shaderColor = new Vector4f(1.0F);
   private static boolean depthTest = true;
   private static boolean depthWrite = true;
   private static boolean clearTargetBeforeDraw;
   private static boolean deferGuiDraws;
   private static ScreenRect guiScissor;
   private static final List<PendingDraw> deferredGuiDraws = new ArrayList<>();
   private static final List<Runnable> deferredGuiOverlays = new ArrayList<>();
   private static final Matrix4f GUI_MODEL_VIEW = new Matrix4f().translation(0.0F, 0.0F, -11000.0F);
   private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();
   private static final Vector3f ZERO_VECTOR = new Vector3f();
   private static final Vector4f WHITE_COLOR = new Vector4f(1.0F);

   private LegacyImmediateRenderer() {
   }

   public static void setTexture(Identifier id) {
      managedTexture = MinecraftClient.getInstance().getTextureManager().getTexture(id);
      texture = managedTexture.getGlTextureView();
   }

   public static void setTexture(GpuTextureView view) {
      managedTexture = null;
      texture = view;
   }

   public static void setTexture(String sampler, GpuTextureView view, FilterMode filter) {
      namedTextures.put(sampler, view);
      namedTextureFilters.put(sampler, filter);
   }

   public static void clearTexture() {
      managedTexture = null;
      texture = null;
      namedTextures.clear();
      namedTextureFilters.clear();
   }

   public static void setTarget(Framebuffer framebuffer) {
      target = framebuffer;
   }

   public static void clearTarget() {
      target = null;
   }

   static Framebuffer getTargetOrMain() {
      return target != null ? target : MinecraftClient.getInstance().getFramebuffer();
   }

   public static void clearTargetBeforeDraw() {
      clearTargetBeforeDraw = true;
   }

   public static void useShader(ShaderWrapper value) {
      shader = value;
   }

   public static void clearShader() {
      shader = null;
   }

   public static void setTexture(AbstractTexture value) {
      managedTexture = value;
      texture = value == null ? null : value.getGlTextureView();
   }

   public static void setBlend(BlendFunction value) {
      blend = value;
   }

   public static void defaultBlend() {
      blend = BlendFunction.TRANSLUCENT;
   }

   public static void setShaderColor(float red, float green, float blue, float alpha) {
      shaderColor.set(red, green, blue, alpha);
   }

   public static float[] getShaderColor() {
      return new float[]{shaderColor.x, shaderColor.y, shaderColor.z, shaderColor.w};
   }

   public static void enableDepthTest() {
      depthTest = true;
   }

   public static void disableDepthTest() {
      depthTest = false;
   }

   public static void depthMask(boolean writeDepth) {
      depthWrite = writeDepth;
   }

   public static void clearDepth() {
      Framebuffer framebuffer = target != null ? target : MinecraftClient.getInstance().getFramebuffer();
      if (!framebuffer.useDepthAttachment) {
         return;
      }

      try (RenderPass ignored = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
         () -> "Zenith clear depth",
         framebuffer.getColorAttachmentView(),
         OptionalInt.empty(),
         framebuffer.getDepthAttachmentView(),
         OptionalDouble.of(1.0)
      )) {
      }
   }

   public static void beginGuiDeferral() {
      deferGuiDraws = true;
      guiScissor = null;
      deferredGuiDraws.clear();
      deferredGuiOverlays.clear();
   }

   public static void setGuiScissor(ScreenRect scissor) {
      guiScissor = scissor;
   }

   public static void deferGuiOverlay(Runnable overlay) {
      deferredGuiOverlays.add(overlay);
   }

   public static boolean isGuiDeferring() {
      return deferGuiDraws;
   }

   public static boolean hasDeferredGuiOverlays() {
      return !deferredGuiOverlays.isEmpty();
   }

   public static void extractDeferredGuiOverlays() {
      if (deferredGuiOverlays.isEmpty()) {
         return;
      }

      List<Runnable> overlays = List.copyOf(deferredGuiOverlays);
      deferredGuiOverlays.clear();
      for (Runnable overlay : overlays) {
         overlay.run();
      }
   }

   public static void flushGuiDeferral() {
      deferGuiDraws = false;
      if (deferredGuiDraws.isEmpty()) {
         ShaderWrapper.rotateUniformBuffers();
         return;
      }

      try {
         executeDraws(deferredGuiDraws);
      } finally {
         deferredGuiDraws.clear();
         ShaderWrapper.rotateUniformBuffers();
      }
   }

   public static void draw(BuiltBuffer buffer) {
      BuiltBuffer.DrawParameters parameters = buffer.getDrawParameters();
      ByteBuffer vertexData;
      ByteBuffer indexData;
      try (buffer) {
         vertexData = copy(buffer.getBuffer());
         indexData = buffer.getSortedBuffer() == null ? null : copy(buffer.getSortedBuffer());
      }
      VertexFormat format = parameters.format();
      ShaderWrapper selectedShader = shader;
      shader = null;
      boolean textured = format.equals(VertexFormats.POSITION_TEXTURE_COLOR);
      BlendFunction selectedBlend = blend;
      blend = BlendFunction.TRANSLUCENT;
      boolean guiDraw = deferGuiDraws;
      RenderPipeline pipeline = selectedShader != null
         ? selectedShader.pipeline()
         : pipeline(format, parameters.mode(), textured, selectedBlend, guiDraw ? false : depthTest, guiDraw ? false : depthWrite);
      Framebuffer framebuffer = target != null ? target : MinecraftClient.getInstance().getFramebuffer();

      // 1.21.11's GUI projection spans z=-1000..-11000 and vanilla submits
      // every 2D layer with this translation.  Legacy Zenith vertices use z=0,
      // so without matching it the draw succeeds but is clipped completely.
      Matrix4f modelView = guiDraw ? GUI_MODEL_VIEW : new Matrix4f(RenderSystem.getModelViewMatrix());
      Vector4f color = shaderColor.equals(WHITE_COLOR) ? WHITE_COLOR : new Vector4f(shaderColor);
      boolean dedicatedCustomUniforms = selectedShader != null
         && (!guiDraw || selectedShader.usesDedicatedUniformBuffer());
      GpuBuffer ownedCustomUniforms = dedicatedCustomUniforms ? selectedShader.createUniformBuffer() : null;
      GpuBufferSlice customUniforms = selectedShader == null
         ? null
         : dedicatedCustomUniforms ? ownedCustomUniforms.slice() : selectedShader.createUniformSlice();
      GpuTextureView primaryTexture = texture;
      AbstractTexture primaryManagedTexture = managedTexture;
      Map<String, GpuTextureView> textures = namedTextures.isEmpty() ? Map.of() : Map.copyOf(namedTextures);
      Map<String, FilterMode> filters = namedTextureFilters.isEmpty() ? Map.of() : Map.copyOf(namedTextureFilters);
      ScreenRect selectedScissor = guiScissor;
      boolean selectedClearTarget = clearTargetBeforeDraw;
      clearTargetBeforeDraw = false;

      PendingDraw draw = new PendingDraw(
         vertexData,
         indexData,
         parameters,
         format,
         pipeline,
         framebuffer,
         modelView,
         color,
         customUniforms,
         ownedCustomUniforms,
         primaryTexture,
         primaryManagedTexture,
         textures,
         filters,
         selectedScissor,
         selectedClearTarget
      );
      if (deferGuiDraws) {
         deferredGuiDraws.add(draw);
      } else {
         executeSingleDraw(draw);
      }
   }

   private static void executeDraws(List<PendingDraw> draws) {
      boolean allUnsorted = draws.size() > 1;
      for (PendingDraw draw : draws) {
         if (draw.indexData() != null) {
            allUnsorted = false;
            break;
         }
      }
      if (allUnsorted) {
         executeBatchedDraws(draws);
         return;
      }

      for (PendingDraw pending : draws) {
         executeSingleDraw(pending);
      }
   }

   private static void executeSingleDraw(PendingDraw pending) {
      PreparedDraw prepared = prepareDraw(pending);
      Framebuffer framebuffer = pending.framebuffer();
      int windowScale = MinecraftClient.getInstance().getWindow().getScaleFactor();
      try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
            () -> "Zenith immediate draw",
            framebuffer.getColorAttachmentView(),
            pending.clearTarget() ? OptionalInt.of(0) : OptionalInt.empty(),
            framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null,
            OptionalDouble.empty()
         )) {
         executeDraw(pass, prepared, windowScale);
      } finally {
         if (pending.ownedCustomUniforms() != null) {
            RenderSystem.queueFencedTask(pending.ownedCustomUniforms()::close);
         }
      }
   }

   private static void executeBatchedDraws(List<PendingDraw> draws) {
      Map<VertexFormat, Integer> requiredBytes = new IdentityHashMap<>();
      for (PendingDraw draw : draws) {
         VertexFormat format = draw.format();
         int quadAlignment = format.getVertexSize() * 4;
         int byteOffset = alignUp(requiredBytes.getOrDefault(format, 0), quadAlignment);
         requiredBytes.put(format, byteOffset + draw.vertexData().remaining());
      }

      for (Map.Entry<VertexFormat, Integer> entry : requiredBytes.entrySet()) {
         MappableRingBuffer buffer = GUI_VERTEX_BUFFERS.get(entry.getKey());
         if (buffer == null || buffer.size() < entry.getValue()) {
            if (buffer != null) {
               buffer.close();
            }
            GUI_VERTEX_BUFFERS.put(
               entry.getKey(),
               new MappableRingBuffer(() -> "Zenith GUI vertex buffer for " + entry.getKey(), GpuBuffer.USAGE_MAP_WRITE | GpuBuffer.USAGE_VERTEX, entry.getValue())
            );
         }
      }

      CommandEncoder encoder = RenderSystem.getDevice().createCommandEncoder();
      Map<VertexFormat, Integer> offsets = new IdentityHashMap<>();
      List<PreparedDraw> prepared = new ArrayList<>(draws.size());
      int windowScale = MinecraftClient.getInstance().getWindow().getScaleFactor();
      try {
         for (PendingDraw draw : draws) {
            int quadAlignment = draw.format().getVertexSize() * 4;
            int byteOffset = alignUp(offsets.getOrDefault(draw.format(), 0), quadAlignment);
            int byteCount = draw.vertexData().remaining();
            MappableRingBuffer ring = GUI_VERTEX_BUFFERS.get(draw.format());
            try (GpuBuffer.MappedView mapped = encoder.mapBuffer(ring.getBlocking().slice(byteOffset, byteCount), false, true)) {
               MemoryUtil.memCopy(draw.vertexData(), mapped.data());
            }

            GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().write(
               draw.modelView(), draw.color(), ZERO_VECTOR, IDENTITY_MATRIX
            );
            RenderSystem.ShapeIndexBuffer sequential = RenderSystem.getSequentialBuffer(draw.parameters().mode());
            GpuBuffer indexBuffer = sequential.getIndexBuffer(draw.parameters().indexCount());
            prepared.add(
               new PreparedDraw(
                  draw,
                  transforms,
                  ring.getBlocking(),
                  indexBuffer,
                  sequential.getIndexType(),
                  byteOffset / draw.format().getVertexSize()
               )
            );
            offsets.put(draw.format(), byteOffset + byteCount);
         }

         int index = 0;
         while (index < prepared.size()) {
            Framebuffer framebuffer = prepared.get(index).pending().framebuffer();
            int end = index + 1;
            while (end < prepared.size()
               && prepared.get(end).pending().framebuffer() == framebuffer
               && !prepared.get(end).pending().clearTarget()) {
               end++;
            }

            try (RenderPass pass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(
               () -> "Zenith GUI batch",
               framebuffer.getColorAttachmentView(),
               prepared.get(index).pending().clearTarget() ? OptionalInt.of(0) : OptionalInt.empty(),
               framebuffer.useDepthAttachment ? framebuffer.getDepthAttachmentView() : null,
               OptionalDouble.empty()
            )) {
               for (int drawIndex = index; drawIndex < end; drawIndex++) {
                  executeDraw(pass, prepared.get(drawIndex), windowScale);
               }
            }
            index = end;
         }
      } finally {
         for (VertexFormat format : requiredBytes.keySet()) {
            GUI_VERTEX_BUFFERS.get(format).rotate();
         }
      }
   }

   private static int alignUp(int value, int alignment) {
      return (value + alignment - 1) / alignment * alignment;
   }

   private static PreparedDraw prepareDraw(PendingDraw draw) {
      // DynamicTransforms is backed by Minecraft's per-frame ring buffer.  It
      // must be allocated immediately before this pass: the vanilla GUI pass
      // runs between extraction and our deferred draw and may reuse the slice.
      GpuBufferSlice transforms = RenderSystem.getDynamicUniforms().write(draw.modelView(), draw.color(), ZERO_VECTOR, IDENTITY_MATRIX);
      GpuBuffer vertexBuffer = draw.format().uploadImmediateVertexBuffer(draw.vertexData());
      GpuBuffer indexBuffer;
      VertexFormat.IndexType indexType;
      if (draw.indexData() == null) {
         RenderSystem.ShapeIndexBuffer sequential = RenderSystem.getSequentialBuffer(draw.parameters().mode());
         indexBuffer = sequential.getIndexBuffer(draw.parameters().indexCount());
         indexType = sequential.getIndexType();
      } else {
         indexBuffer = draw.format().uploadImmediateIndexBuffer(draw.indexData());
         indexType = draw.parameters().indexType();
      }
      return new PreparedDraw(draw, transforms, vertexBuffer, indexBuffer, indexType, 0);
   }

   private static ByteBuffer copy(ByteBuffer source) {
      ByteBuffer sourceView = source.duplicate();
      ByteBuffer copy = ByteBuffer.allocateDirect(sourceView.remaining());
      copy.put(sourceView).flip();
      return copy;
   }

   private static void executeDraw(RenderPass pass, PreparedDraw prepared, int windowScale) {
         PendingDraw draw = prepared.pending();
            pass.setPipeline(draw.pipeline());
            if (draw.scissor() != null) {
               int scissorX = draw.scissor().getLeft() * windowScale;
               int scissorY = draw.framebuffer().textureHeight - draw.scissor().getBottom() * windowScale;
               pass.enableScissor(
                  scissorX,
                  scissorY,
                  Math.max(0, draw.scissor().width() * windowScale),
                  Math.max(0, draw.scissor().height() * windowScale)
               );
            } else {
               // Render-pass state is not guaranteed to start with scissoring
               // disabled. Priority popups follow clipped module draws, so an
               // explicit reset is required to keep dropdowns stable.
               pass.disableScissor();
            }
            RenderSystem.bindDefaultUniforms(pass);
            pass.setUniform("DynamicTransforms", prepared.transforms());
            if (draw.customUniforms() != null) {
               pass.setUniform("ZenithData", draw.customUniforms());
            }
            pass.setVertexBuffer(0, prepared.vertexBuffer());
            for (String sampler : draw.pipeline().getSamplers()) {
               GpuTextureView samplerTexture = draw.textures().get(sampler);
               if (samplerTexture != null) {
                  pass.bindTexture(
                     sampler,
                     samplerTexture,
                     RenderSystem.getSamplerCache().get(draw.filters().getOrDefault(sampler, FilterMode.LINEAR))
                  );
               } else if ("Sampler0".equals(sampler) && draw.primaryTexture() != null) {
                  pass.bindTexture(
                     sampler,
                     draw.primaryTexture(),
                     draw.primaryManagedTexture() != null
                        ? draw.primaryManagedTexture().getSampler()
                        : RenderSystem.getSamplerCache().get(FilterMode.LINEAR)
                  );
               }
            }
            pass.setIndexBuffer(prepared.indexBuffer(), prepared.indexType());
            pass.drawIndexed(prepared.baseVertex(), 0, draw.parameters().indexCount(), 1);
   }

   private static RenderPipeline pipeline(
      VertexFormat format,
      VertexFormat.DrawMode mode,
      boolean textured,
      BlendFunction blendFunction,
      boolean useDepthTest,
      boolean writeDepth
   ) {
      PipelineKey stateKey = new PipelineKey(format, mode, textured, blendFunction, useDepthTest, writeDepth);
      return PIPELINES.computeIfAbsent(stateKey, ignored -> {
         RenderPipeline.Builder builder = RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
            .withLocation(Identifier.of("zenith", "pipeline/immediate_" + PIPELINES.size()))
            .withVertexShader(textured ? "core/position_tex_color" : "core/position_color")
            .withFragmentShader(textured ? "core/position_tex_color" : "core/position_color")
            .withBlend(blendFunction)
            .withDepthTestFunction(stateKey.depthTest() ? DepthTestFunction.LEQUAL_DEPTH_TEST : DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(stateKey.depthWrite())
            .withCull(false)
            .withVertexFormat(format, mode);
         if (textured) {
            builder.withSampler("Sampler0");
         }
         return RenderPipelines.register(builder.build());
      });
   }

   private record PipelineKey(VertexFormat format, VertexFormat.DrawMode mode, boolean textured, BlendFunction blend, boolean depthTest, boolean depthWrite) {
   }

   private record PendingDraw(
      ByteBuffer vertexData,
      ByteBuffer indexData,
      BuiltBuffer.DrawParameters parameters,
      VertexFormat format,
      RenderPipeline pipeline,
      Framebuffer framebuffer,
      Matrix4f modelView,
      Vector4f color,
      GpuBufferSlice customUniforms,
      GpuBuffer ownedCustomUniforms,
      GpuTextureView primaryTexture,
      AbstractTexture primaryManagedTexture,
      Map<String, GpuTextureView> textures,
      Map<String, FilterMode> filters,
      ScreenRect scissor,
      boolean clearTarget
   ) {
   }

   private record PreparedDraw(
      PendingDraw pending,
      GpuBufferSlice transforms,
      GpuBuffer vertexBuffer,
      GpuBuffer indexBuffer,
      VertexFormat.IndexType indexType,
      int baseVertex
   ) {
   }
}
