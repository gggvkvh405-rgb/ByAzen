package org.zenith.core;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.DynamicUniformStorage;
import net.minecraft.client.gl.UniformType;
import net.minecraft.util.Identifier;
import org.zenith.render.LegacyImmediateRenderer;

/** Adapts Zenith's legacy core shaders to the explicit 1.21.11 pipeline API. */
public class ShaderWrapper {
   public static final List<Runnable> shaderRendererCoreItems = new ArrayList<>();
   private static final List<ShaderWrapper> INSTANCES = new ArrayList<>();
   private final Map<String, LegacyUniform> uniforms = new LinkedHashMap<>();
   private final RenderPipeline pipeline;
   private final int uniformBufferSize;
   private final UniformSnapshot[] uniformSnapshots;
   private UniformSnapshot lastUniformSnapshot;
   private DynamicUniformStorage<UniformSnapshot> uniformStorage;
   private boolean dedicatedUniformBuffer;

   public ShaderWrapper(Identifier identifier, VertexFormat vertexFormat) {
      this(identifier, vertexFormat, loadDefinition(identifier));
   }

   public ShaderWrapper(
      Identifier identifier,
      VertexFormat vertexFormat,
      Identifier vertexShader,
      Identifier fragmentShader,
      List<String> samplers,
      UniformSpec... uniformSpecs
   ) {
      this(
         identifier,
         vertexFormat,
         new ShaderDefinition(
            vertexShader,
            fragmentShader,
            List.copyOf(samplers),
            java.util.Arrays.stream(uniformSpecs)
               .map(spec -> new LegacyUniform(spec.name(), spec.count(), spec.integer(), new float[spec.count()]))
               .toList()
         )
      );
   }

   private ShaderWrapper(Identifier identifier, VertexFormat vertexFormat, ShaderDefinition definition) {
      Std140SizeCalculator size = new Std140SizeCalculator();
      for (LegacyUniform value : definition.uniforms) {
         uniforms.put(value.name, value);
         value.addSize(size);
      }
      uniformBufferSize = (size.get() + 15) & ~15;
      int componentCount = 0;
      for (LegacyUniform value : uniforms.values()) {
         componentCount += value.count;
      }
      uniformSnapshots = new UniformSnapshot[]{
         new UniformSnapshot(this, new float[componentCount]),
         new UniformSnapshot(this, new float[componentCount])
      };

      RenderPipeline.Builder builder = RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(Identifier.of(identifier.getNamespace(), "pipeline/" + identifier.getPath().replace("/data", "")))
         .withVertexShader(definition.vertexShader)
         .withFragmentShader(definition.fragmentShader)
         .withUniform("ZenithData", UniformType.UNIFORM_BUFFER)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
         .withDepthWrite(false)
         .withCull(false)
         .withVertexFormat(vertexFormat, VertexFormat.DrawMode.QUADS);
      for (String sampler : definition.samplers) {
         builder.withSampler(sampler);
      }
      pipeline = RenderPipelines.register(builder.build());
      INSTANCES.add(this);
      shaderRendererCoreItems.add(this::float57);
      float57();
   }

   public record UniformSpec(String name, int count, boolean integer) {
      public UniformSpec(String name, int count) {
         this(name, count, false);
      }
   }

   public void float251() {
      LegacyImmediateRenderer.useShader(this);
   }

   protected void float57() {
   }

   public LegacyUniform HudArmorPanel(String name) {
      LegacyUniform value = uniforms.get(name);
      if (value == null) {
         throw new IllegalArgumentException("Unknown shader uniform " + name + " in " + pipeline.getLocation());
      }
      return value;
   }

   public RenderPipeline pipeline() {
      return pipeline;
   }

   /**
    * Keeps animated or state-heavy shaders isolated from the shared deferred
    * uniform ring. Their values must remain immutable until the GPU executes
    * the queued draw, otherwise alternating frames can observe another draw's
    * data and visibly flicker.
    */
   public ShaderWrapper useDedicatedUniformBuffer() {
      dedicatedUniformBuffer = true;
      return this;
   }

   public boolean usesDedicatedUniformBuffer() {
      return dedicatedUniformBuffer;
   }

   public GpuBufferSlice createUniformSlice() {
      if (uniformStorage == null) {
         uniformStorage = new DynamicUniformStorage<>("Zenith shader uniforms", Math.max(uniformBufferSize, 16), 4);
      }
      UniformSnapshot snapshot = this.lastUniformSnapshot == this.uniformSnapshots[0]
         ? this.uniformSnapshots[1]
         : this.uniformSnapshots[0];
      float[] components = snapshot.components;
      int offset = 0;
      for (LegacyUniform value : uniforms.values()) {
         System.arraycopy(value.values, 0, components, offset, value.count);
         offset += value.count;
      }
      GpuBufferSlice slice = uniformStorage.write(snapshot);
      if (this.lastUniformSnapshot == null || !this.lastUniformSnapshot.equals(snapshot)) {
         this.lastUniformSnapshot = snapshot;
      }
      return slice;
   }

   public GpuBuffer createUniformBuffer() {
      ByteBuffer bytes = ByteBuffer.allocateDirect(Math.max(uniformBufferSize, 16)).order(ByteOrder.nativeOrder());
      Std140Builder writer = Std140Builder.intoBuffer(bytes);
      for (LegacyUniform value : uniforms.values()) {
         value.write(writer);
      }
      writer.align(16);
      return RenderSystem.getDevice().createBuffer(() -> "Zenith direct shader uniforms", GpuBuffer.USAGE_UNIFORM, writer.get());
   }

   public static void rotateUniformBuffers() {
      for (ShaderWrapper instance : INSTANCES) {
         if (instance.uniformStorage != null) {
            instance.uniformStorage.clear();
            instance.lastUniformSnapshot = null;
         }
      }
   }

   public static void float245() {
      shaderRendererCoreItems.forEach(Runnable::run);
   }

   private static ShaderDefinition loadDefinition(Identifier id) {
      String shaderPath = id.getPath();
      if (shaderPath.startsWith("core/")) {
         shaderPath = shaderPath.substring("core/".length());
      }

      String path = "/assets/" + id.getNamespace() + "/shaders/core/" + shaderPath + ".json";
      try (var stream = ShaderWrapper.class.getResourceAsStream(path)) {
         if (stream == null) {
            throw new IllegalStateException("Missing shader definition " + path);
         }
         JsonObject root = JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
         Identifier vertex = Identifier.of(root.get("vertex").getAsString());
         Identifier fragment = Identifier.of(root.get("fragment").getAsString());
         List<String> samplers = new ArrayList<>();
         if (root.has("samplers")) {
            for (var element : root.getAsJsonArray("samplers")) {
               samplers.add(element.getAsJsonObject().get("name").getAsString());
            }
         }
         List<LegacyUniform> uniforms = new ArrayList<>();
         for (var element : root.getAsJsonArray("uniforms")) {
            JsonObject uniform = element.getAsJsonObject();
            String name = uniform.get("name").getAsString();
            if (name.equals("ModelViewMat") || name.equals("ProjMat")) {
               continue;
            }
            int count = uniform.get("count").getAsInt();
            boolean integer = uniform.get("type").getAsString().equals("int");
            float[] values = new float[count];
            JsonArray defaults = uniform.getAsJsonArray("values");
            for (int i = 0; i < Math.min(count, defaults.size()); i++) {
               values[i] = defaults.get(i).getAsFloat();
            }
            uniforms.add(new LegacyUniform(name, count, integer, values));
         }
         return new ShaderDefinition(vertex, fragment, samplers, uniforms);
      } catch (Exception exception) {
         throw new IllegalStateException("Unable to load shader " + id, exception);
      }
   }

   private static final class UniformSnapshot implements DynamicUniformStorage.Uploadable {
      private final ShaderWrapper owner;
      private final float[] components;

      private UniformSnapshot(ShaderWrapper owner, float[] components) {
         this.owner = owner;
         this.components = components;
      }

      @Override
      public void write(ByteBuffer buffer) {
         Std140Builder writer = Std140Builder.intoBuffer(buffer);
         int offset = 0;
         for (LegacyUniform value : this.owner.uniforms.values()) {
            if (value.integer) writer.putInt((int)this.components[offset]);
            else if (value.count == 1) writer.putFloat(this.components[offset]);
            else if (value.count == 2) writer.putVec2(this.components[offset], this.components[offset + 1]);
            else if (value.count == 3) writer.putVec3(this.components[offset], this.components[offset + 1], this.components[offset + 2]);
            else writer.putVec4(this.components[offset], this.components[offset + 1], this.components[offset + 2], this.components[offset + 3]);
            offset += value.count;
         }
         writer.align(16);
      }

      @Override
      public boolean equals(Object other) {
         return this == other
            || other instanceof UniformSnapshot snapshot
               && this.owner == snapshot.owner
               && Arrays.equals(this.components, snapshot.components);
      }

      @Override
      public int hashCode() {
         return 31 * System.identityHashCode(this.owner) + Arrays.hashCode(this.components);
      }
   }

   private record ShaderDefinition(Identifier vertexShader, Identifier fragmentShader, List<String> samplers, List<LegacyUniform> uniforms) {
   }
}

