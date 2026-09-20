package org.zenith.base.bot.view;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.ProjectionType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.DynamicUniforms;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayers;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RawProjectionMatrix;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueueImpl;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.zenith.base.bot.client.BotClient;
import org.zenith.base.bot.net.BotPlayHandler;
import org.zenith.base.bot.world.BotPlayer;
import org.zenith.base.bot.world.BotWorld;
import org.zenith.base.bot.world.BotWorld_RenderListener;

public final class BotWorldView implements BotWorld_RenderListener {
   public static final int RENDER_DISTANCE_CHUNKS = 5;
   public static final int VERTICAL_SECTION_RADIUS = 5;
   public static final int MAX_BUILDS_PER_FRAME = 1;
   public static final int MAX_PENDING_BUILDS = 1;
   public static final double ENTITY_RENDER_DISTANCE_SQ = 9216.0;
   public static final double BLOCK_ENTITY_RENDER_DISTANCE_SQ = 2304.0;
   public static final float FOV_DEGREES = 100.0F;
   public static final float HAND_FOV_DEGREES = 70.0F;
   public final BotClient client;
   public final Map<Long, BotWorldSection> sections = new HashMap<>();
   public final Set<Long> dirty = ConcurrentHashMap.newKeySet();
   public final Queue<BotMeshResult> uploads = new ConcurrentLinkedQueue<>();
   public final AtomicInteger pendingBuilds = new AtomicInteger();
   public final AtomicBoolean entitySnapshotScheduled = new AtomicBoolean();
   public final AtomicBoolean closed = new AtomicBoolean();
   public final ExecutorService meshExecutor;
   public final Map<Integer, Vec3d> itemFramePositions = new ConcurrentHashMap<>();
   public volatile List<Entity> entitySnapshot = List.of();
   public final Camera camera = new Camera();
   public final BotLightmap lightmap = new BotLightmap();
   public final BotPlayerRenderer playerRenderer = new BotPlayerRenderer();
   public final BotHeldItemRenderer heldItem = new BotHeldItemRenderer();
   public final Matrix4f projection = new Matrix4f();
   public final Matrix4f handProjection = new Matrix4f();
   public final Matrix4f positionMatrix = new Matrix4f();
   public final Quaternionf rotationConjugate = new Quaternionf();
   public final CameraRenderState cameraState = new CameraRenderState();
   public final RawProjectionMatrix projectionBuffer = new RawProjectionMatrix("Zenith bot preview");
   public SimpleFramebuffer fbo;
   public volatile BotWorld boundWorld;
   public boolean loggedRenderError;
   public volatile String lastError;
   public int lastEyeHeightAge = Integer.MIN_VALUE;
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public boolean renderToFbo(int var1, int var2) {
      BotWorld botworld = this.client.getWorld();
      BotPlayer botplayer = this.client.getPlayer();
      if (botworld != null && botplayer != null && this.client.isJoined() && var1 > 0 && var2 > 0) {
         this.bindWorld(botworld);
         this.drainUploads();
         this.refreshSections(botworld, botplayer);
         this.scheduleBuilds(botworld, botplayer);
         this.scheduleEntitySnapshot(botworld);
         this.ensureFbo(var1, var2);
         if (this.fbo == null) {
            return false;
         }

         GpuBufferSlice fog = RenderSystem.getShaderFog();
         RenderSystem.backupProjectionMatrix();
         synchronized (this.client.getRenderStateLock()) {
            float f = this.client.getTickDelta();
            this.updateCamera(botworld, botplayer, f);
            this.lightmap.update(botworld);

            try {
               float f1 = (float)var1 / var2;
               float f2 = 128.0F;
               this.projection.identity().perspective((float) (Math.PI * 5.0 / 9.0), f1, 0.05F, f2);
               this.positionMatrix.rotation(this.camera.getRotation().conjugate(this.rotationConjugate));
               RenderSystem.setProjectionMatrix(this.projectionBuffer.set(this.projection), ProjectionType.PERSPECTIVE);
               float[] afloat = skyColor(botworld, botplayer);
               int clearColor = ColorHelper.getArgb(255, (int)(afloat[0] * 255.0F), (int)(afloat[1] * 255.0F), (int)(afloat[2] * 255.0F));
               RenderSystem.getDevice()
                  .createCommandEncoder()
                  .clearColorAndDepthTextures(this.fbo.getColorAttachment(), clearColor, this.fbo.getDepthAttachment(), 1.0);
               Vec3d vec3d = this.camera.getCameraPos();
               this.drawTerrainLayer(BlockRenderLayer.SOLID, vec3d, false);
               this.drawTerrainLayer(BlockRenderLayer.CUTOUT, vec3d, false);
               this.renderEntities(botworld, botplayer, vec3d, f);
               this.renderBlockEntities(botworld, vec3d, f);
               this.drawTerrainLayer(BlockRenderLayer.TRANSLUCENT, vec3d, true);
               this.drawTerrainLayer(BlockRenderLayer.TRIPWIRE, vec3d, true);
               this.renderHand(botworld, botplayer, f, f1, f2);
            } catch (Throwable throwable) {
               this.lastError = throwable.toString();
               if (!this.loggedRenderError) {
                  this.loggedRenderError = true;
                  System.err.println("[BotWorldView] render failed:");
                  throwable.printStackTrace();
               }
            } finally {
               RenderSystem.restoreProjectionMatrix();
               RenderSystem.setShaderFog(fog);
               this.restoreDispatchers();
               RenderSystem.outputColorTextureOverride = null;
               RenderSystem.outputDepthTextureOverride = null;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public void refreshSections(BotWorld var1, BotPlayer var2) {
      int i = ChunkSectionPos.getSectionCoord(MathHelper.floor(var2.getX()));
      int j = ChunkSectionPos.getSectionCoord(MathHelper.floor(var2.getEyeY()));
      int k = ChunkSectionPos.getSectionCoord(MathHelper.floor(var2.getZ()));
      int l = ChunkSectionPos.getSectionCoord(var1.getBottomY());
      int i1 = ChunkSectionPos.getSectionCoord(var1.getTopYInclusive());
      this.sections
         .entrySet()
         .removeIf(
            var3x -> {
               long l2 = var3x.getKey();
               boolean flag = Math.abs(ChunkSectionPos.unpackX(l2) - i) > 5
                  || Math.abs(ChunkSectionPos.unpackZ(l2) - k) > 5
                  || Math.abs(ChunkSectionPos.unpackY(l2) - j) > 5;
               if (flag) {
                  var3x.getValue().closeBuffers();
               }

               return flag;
            }
         );

      for (int j1 = i - 5; j1 <= i + 5; j1++) {
         for (int k1 = k - 5; k1 <= k + 5; k1++) {
            int l1 = Math.max(j - 5, l);
            int i2 = Math.min(j + 5, i1);

            for (int j2 = l1; j2 <= i2; j2++) {
               long k2 = ChunkSectionPos.asLong(j1, j2, k1);
               if (!this.sections.containsKey(k2)) {
                  this.sections.put(k2, new BotWorldSection(k2));
                  this.dirty.add(k2);
               }
            }
         }
      }
   }

   public void scheduleBuilds(BotWorld var1, BotPlayer var2) {
      if (!this.dirty.isEmpty() && this.pendingBuilds.get() < MAX_PENDING_BUILDS) {
         List<BotWorldSection> arraylist = new ArrayList<>();

         for (Long olong : this.dirty) {
            BotWorldSection botworldview_section = this.sections.get(olong);
            if (botworldview_section == null) {
               this.dirty.remove(olong);
            } else if (!botworldview_section.building) {
               arraylist.add(botworldview_section);
            }
         }

         if (!arraylist.isEmpty()) {
            double d1 = var2.getX();
            double d2 = var2.getY();
            double d0 = var2.getZ();
            arraylist.sort((var6x, var7) -> Double.compare(var6x.squaredDistanceTo(d1, d2, d0), var7.squaredDistanceTo(d1, d2, d0)));
            int i = 0;

            for (BotWorldSection botworldview_section1 : arraylist) {
               if (i >= MAX_BUILDS_PER_FRAME || this.pendingBuilds.get() >= MAX_PENDING_BUILDS) {
                  break;
               }

               long j = botworldview_section1.pos;
               if (this.dirty.remove(j)) {
                  botworldview_section1.building = true;
                  this.pendingBuilds.incrementAndGet();
                  i++;
                   this.meshExecutor.execute(() -> {
                      try {
                         BotMeshResult botsectionmesher_meshresult = BotSectionMesher.build(var1, j);
                         if (!this.closed.get() && this.boundWorld == var1) {
                            this.uploads.add(botsectionmesher_meshresult);
                         } else {
                            discardResult(botsectionmesher_meshresult);
                         }
                      } catch (Throwable throwable) {
                         this.lastError = "mesh: " + throwable;
                         if (!this.closed.get() && this.boundWorld == var1) {
                            this.uploads.add(new BotMeshResult(j, List.of(), List.of(), true));
                            this.dirty.add(j);
                         }
                      } finally {
                         this.pendingBuilds.decrementAndGet();
                     }
                  });
               }
            }
         }
      }
   }

   public void drawTerrainLayer(BlockRenderLayer layer, Vec3d cameraPos, boolean reverseOrder) {
      List<BotWorldSection> visibleSections = new ArrayList<>();
      int maxIndexCount = 0;

      for (BotWorldSection section : this.sections.values()) {
         BotWorldSection.TerrainBuffer buffer = section.buffers.get(layer);
         if (buffer != null) {
            visibleSections.add(section);
            maxIndexCount = Math.max(maxIndexCount, buffer.indexCount());
         }
      }

      if (visibleSections.isEmpty()) {
         return;
      }

      visibleSections.sort((left, right) -> Double.compare(left.squaredDistanceTo(cameraPos.x, cameraPos.y, cameraPos.z), right.squaredDistanceTo(cameraPos.x, cameraPos.y, cameraPos.z)));
      if (reverseOrder) {
         Collections.reverse(visibleSections);
      }

      RenderPipeline pipeline = switch (layer) {
         case SOLID -> RenderPipelines.SOLID_BLOCK;
         case CUTOUT -> RenderPipelines.CUTOUT_BLOCK;
         case TRANSLUCENT -> RenderPipelines.RENDERTYPE_TRANSLUCENT_MOVING_BLOCK;
         case TRIPWIRE -> RenderPipelines.TRIPWIRE_BLOCK;
      };
      RenderSystem.ShapeIndexBuffer sequentialIndices = RenderSystem.getSequentialBuffer(VertexFormat.DrawMode.QUADS);
      GpuBuffer indexBuffer = sequentialIndices.getIndexBuffer(maxIndexCount);
      VertexFormat.IndexType indexType = sequentialIndices.getIndexType();
      GpuTextureView blockAtlas = minecraftClient3.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).getGlTextureView();
      List<TerrainDraw> terrainDraws = new ArrayList<>(visibleSections.size());

      // Dynamic uniform storage maps GPU buffers. 1.21.11 forbids mapping while
      // a render pass is open, so prepare every section transform first.
      for (BotWorldSection section : visibleSections) {
         BotWorldSection.TerrainBuffer terrainBuffer = section.buffers.get(layer);
         Matrix4f modelView = new Matrix4f(this.positionMatrix)
            .translate(
               (float)(section.originX() - cameraPos.x),
               (float)(section.originY() - cameraPos.y),
               (float)(section.originZ() - cameraPos.z)
            );
         GpuBufferSlice transforms = RenderSystem.getDynamicUniforms()
            .write(modelView, new Vector4f(1.0F), new Vector3f(), new Matrix4f());
         terrainDraws.add(new TerrainDraw(terrainBuffer, transforms));
      }

      try (RenderPass pass = RenderSystem.getDevice()
            .createCommandEncoder()
            .createRenderPass(
               () -> "Zenith bot terrain " + layer.getName(),
               this.fbo.getColorAttachmentView(),
               OptionalInt.empty(),
               this.fbo.getDepthAttachmentView(),
               OptionalDouble.empty()
            )) {
         pass.setPipeline(pipeline);
         RenderSystem.bindDefaultUniforms(pass);
         pass.bindTexture("Sampler0", blockAtlas, minecraftClient3.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).getSampler());
         pass.bindTexture("Sampler2", this.lightmap.getTextureView(), RenderSystem.getSamplerCache().get(FilterMode.LINEAR));
         pass.setIndexBuffer(indexBuffer, indexType);

         for (TerrainDraw draw : terrainDraws) {
            pass.setUniform("DynamicTransforms", draw.transforms());
            pass.setVertexBuffer(0, draw.buffer().vertexBuffer());
            pass.drawIndexed(0, 0, draw.buffer().indexCount(), 1);
         }
      }
   }

   private record TerrainDraw(BotWorldSection.TerrainBuffer buffer, GpuBufferSlice transforms) {
   }

   public void renderEntities(BotWorld var1, BotPlayer var2, Vec3d var3, float var4) {
      EntityRenderManager entityrenderdispatcher = minecraftClient3.getEntityRenderDispatcher();
      entityrenderdispatcher.configure(this.camera, null);
      OrderedRenderCommandQueueImpl queue = minecraftClient3.gameRenderer.getEntityRenderCommandQueue();
      BotPlayHandler botplayhandler = this.client.getPlayHandler();
      boolean flag = minecraftClient3.player == null;

      for (Entity entity : this.entitySnapshot) {
         if (entity != var2 && !entity.isRemoved() && entity.getEntityWorld() == var1 && !(entity.squaredDistanceTo(var3) > 9216.0)) {
            try {
               MatrixStack matrixstack = new MatrixStack();
               matrixstack.multiplyPositionMatrix(this.positionMatrix);
               if (entity instanceof PlayerEntity playerentity) {
                  int i = WorldRenderer.getLightmapCoordinates(var1, playerentity.getBlockPos());
                  this.playerRenderer
                     .render(playerentity, botplayhandler, var3.x, var3.y, var3.z, var4, matrixstack, queue, this.cameraState, i, true);
               } else if (!flag || !(entity instanceof LivingEntity)) {
                  Vec3d vec3d = entity instanceof ItemFrameEntity
                     ? this.itemFramePositions.computeIfAbsent(entity.getId(), var5 -> entity.getEntityPos())
                     : null;
                  double d0 = vec3d != null ? vec3d.x : MathHelper.lerp(var4, entity.lastRenderX, entity.getX());
                  double d1 = vec3d != null ? vec3d.y : MathHelper.lerp(var4, entity.lastRenderY, entity.getY());
                  double d2 = vec3d != null ? vec3d.z : MathHelper.lerp(var4, entity.lastRenderZ, entity.getZ());
                  EntityRenderState state = entityrenderdispatcher.getAndUpdateRenderState(entity, var4);
                  entityrenderdispatcher.render(
                     state,
                     this.cameraState,
                     d0 - var3.x,
                     d1 - var3.y,
                     d2 - var3.z,
                     matrixstack,
                     queue
                  );
               }
            } catch (Throwable throwable) {
               this.lastError = "entity: " + throwable;
               if (!this.loggedRenderError) {
                  this.loggedRenderError = true;
                  System.err.println("[BotWorldView] entity render failed for " + entity.getType() + ":");
                  throwable.printStackTrace();
               }
            }
         }
      }

      this.flushEntityCommands();
   }

   public void renderBlockEntities(BotWorld var1, Vec3d var2, float var3) {
      BlockEntityRenderManager blockentityrenderdispatcher = minecraftClient3.getBlockEntityRenderDispatcher();
      blockentityrenderdispatcher.configure(this.camera);
      OrderedRenderCommandQueueImpl queue = minecraftClient3.gameRenderer.getEntityRenderCommandQueue();

      for (BotWorldSection botworldview_section : this.sections.values()) {
         for (BlockEntity blockentity : botworldview_section.blockEntities) {
            if (!blockentity.isRemoved() && blockentity.getWorld() == var1) {
               BlockPos blockpos = blockentity.getPos();
               if (!(blockpos.getSquaredDistance(var2) > 2304.0)) {
                  try {
                     MatrixStack matrixstack = new MatrixStack();
                     matrixstack.multiplyPositionMatrix(this.positionMatrix);
                     matrixstack.push();
                     matrixstack.translate(
                        blockpos.getX() - var2.x, blockpos.getY() - var2.y, blockpos.getZ() - var2.z
                     );
                     BlockEntityRenderState state = blockentityrenderdispatcher.getRenderState(blockentity, var3, null);
                     if (state != null) {
                        blockentityrenderdispatcher.render(state, matrixstack, queue, this.cameraState);
                     }
                     matrixstack.pop();
                  } catch (Throwable var12) {
                  }
               }
            }
         }
      }

      this.flushEntityCommands();
   }

   private void flushEntityCommands() {
      RenderSystem.outputColorTextureOverride = this.fbo.getColorAttachmentView();
      RenderSystem.outputDepthTextureOverride = this.fbo.getDepthAttachmentView();
      try {
         minecraftClient3.gameRenderer.getEntityRenderDispatcher().render();
         minecraftClient3.getBufferBuilders().getEntityVertexConsumers().draw();
      } finally {
         RenderSystem.outputColorTextureOverride = null;
         RenderSystem.outputDepthTextureOverride = null;
      }
   }

   public BotWorldView(BotClient var1) {
      this.client = var1;
      ThreadFactory threadfactory = var2 -> {
         Thread thread = new Thread(var2, "bot-mesher-" + var1.getName());
         thread.setDaemon(true);
         return thread;
      };
      this.meshExecutor = Executors.newSingleThreadExecutor(threadfactory);
   }

   public GpuTextureView getColorAttachment() {
      return this.fbo != null ? this.fbo.getColorAttachmentView() : null;
   }

   public void updateCamera(BotWorld var1, BotPlayer var2, float var3) {
      boolean flag = this.camera.getFocusedEntity() != var2;
      if (!flag && var2.age != this.lastEyeHeightAge) {
         this.camera.updateEyeHeight();
         this.heldItem.tick(var2);
      }

      this.lastEyeHeightAge = var2.age;
      this.camera.update(var1, var2, false, false, var3);
      if (flag) {
         for (int i = 0; i < 24; i++) {
            this.camera.updateEyeHeight();
         }

         this.camera.update(var1, var2, false, false, var3);
         this.heldItem.snap(var2);
      }

      this.cameraState.initialized = this.camera.isReady();
      this.cameraState.pos = this.camera.getCameraPos();
      this.cameraState.blockPos = this.camera.getBlockPos();
      this.cameraState.entityPos = var2.getLerpedPos(var3);
      this.cameraState.orientation = new Quaternionf(this.camera.getRotation());
   }

   public String getDebugStatus() {
      int i = 0;

      for (BotWorldSection botworldview_section : this.sections.values()) {
         if (!botworldview_section.buffers.isEmpty()) {
            i++;
         }
      }

      StringBuilder stringbuilder = new StringBuilder("mesh ")
         .append(i)
         .append('/')
         .append(this.sections.size())
         .append(" | dirty ")
         .append(this.dirty.size())
         .append(" | pending ")
         .append(this.pendingBuilds.get());
      String s = this.lastError;
      if (s != null) {
         stringbuilder.append(" | err: ").append(s);
      }

      return stringbuilder.toString();
   }

   public void close() {
      this.closed.set(true);
      this.meshExecutor.shutdownNow();
      for (BotWorldSection botworldview_section : this.sections.values()) {
         botworldview_section.closeBuffers();
      }

      this.sections.clear();
      this.dirty.clear();
      this.itemFramePositions.clear();
      this.drainUploadsDiscarding();
      if (this.fbo != null) {
         this.fbo.delete();
         this.fbo = null;
      }

      this.lightmap.close();
      this.projectionBuffer.close();
      if (this.boundWorld != null) {
         this.boundWorld.setRenderListener(null);
         this.boundWorld = null;
      }
   }

   @Override
   public void onBlockChanged(BlockPos var1) {
      int i = ChunkSectionPos.getSectionCoord(var1.getX() - 1);
      int j = ChunkSectionPos.getSectionCoord(var1.getX() + 1);
      int k = ChunkSectionPos.getSectionCoord(var1.getY() - 1);
      int l = ChunkSectionPos.getSectionCoord(var1.getY() + 1);
      int i1 = ChunkSectionPos.getSectionCoord(var1.getZ() - 1);
      int j1 = ChunkSectionPos.getSectionCoord(var1.getZ() + 1);

      for (int k1 = i; k1 <= j; k1++) {
         for (int l1 = k; l1 <= l; l1++) {
            for (int i2 = i1; i2 <= j1; i2++) {
               this.dirty.add(ChunkSectionPos.asLong(k1, l1, i2));
            }
         }
      }
   }

   @Override
   public void onChunkChanged(int var1, int var2) {
      BotWorld botworld = this.client.getWorld();
      if (botworld != null) {
         int i = ChunkSectionPos.getSectionCoord(botworld.getBottomY());
         int j = ChunkSectionPos.getSectionCoord(botworld.getTopYInclusive());

         for (int k = i; k <= j; k++) {
            this.dirty.add(ChunkSectionPos.asLong(var1, k, var2));
            this.dirty.add(ChunkSectionPos.asLong(var1 - 1, k, var2));
            this.dirty.add(ChunkSectionPos.asLong(var1 + 1, k, var2));
            this.dirty.add(ChunkSectionPos.asLong(var1, k, var2 - 1));
            this.dirty.add(ChunkSectionPos.asLong(var1, k, var2 + 1));
         }
      }
   }

   @Override
   public void onSectionChanged(int var1, int var2, int var3) {
      this.dirty.add(ChunkSectionPos.asLong(var1, var2, var3));
      this.dirty.add(ChunkSectionPos.asLong(var1 - 1, var2, var3));
      this.dirty.add(ChunkSectionPos.asLong(var1 + 1, var2, var3));
      this.dirty.add(ChunkSectionPos.asLong(var1, var2 - 1, var3));
      this.dirty.add(ChunkSectionPos.asLong(var1, var2 + 1, var3));
      this.dirty.add(ChunkSectionPos.asLong(var1, var2, var3 - 1));
      this.dirty.add(ChunkSectionPos.asLong(var1, var2, var3 + 1));
   }

   public void bindWorld(BotWorld var1) {
      if (this.boundWorld != var1) {
         if (this.boundWorld != null) {
            this.boundWorld.setRenderListener(null);
         }

         for (BotWorldSection botworldview_section : this.sections.values()) {
            botworldview_section.closeBuffers();
         }

         this.sections.clear();
         this.dirty.clear();
         this.drainUploadsDiscarding();
         this.boundWorld = var1;
         var1.setRenderListener(this);
      }
   }

   public void drainUploads() {
      BotMeshResult botsectionmesher_meshresult;
      while ((botsectionmesher_meshresult = this.uploads.poll()) != null) {
         BotWorldSection botworldview_section = this.sections.get(botsectionmesher_meshresult.sectionPos());
         if (botworldview_section == null) {
            discardResult(botsectionmesher_meshresult);
         } else {
            botworldview_section.building = false;
            botworldview_section.closeBuffers();
            botworldview_section.blockEntities = botsectionmesher_meshresult.blockEntities();

            for (BotLayerMesh botsectionmesher_layermesh : botsectionmesher_meshresult.layers()) {
               String bufferLabel = "Zenith bot section "
                  + botsectionmesher_meshresult.sectionPos()
                  + " / "
                  + botsectionmesher_layermesh.layer().getName();
               GpuBuffer vertexbuffer = RenderSystem.getDevice()
                   .createBuffer(
                      () -> bufferLabel,
                     GpuBuffer.USAGE_VERTEX | GpuBuffer.USAGE_COPY_DST,
                     botsectionmesher_layermesh.buffer().getBuffer()
                  );
               int indexCount = botsectionmesher_layermesh.buffer().getDrawParameters().indexCount();
               botsectionmesher_layermesh.buffer().close();
               botsectionmesher_layermesh.allocator().close();
               botworldview_section.buffers.put(botsectionmesher_layermesh.layer(), new BotWorldSection.TerrainBuffer(vertexbuffer, indexCount));
            }
         }
      }
   }

   public void drainUploadsDiscarding() {
      BotMeshResult botsectionmesher_meshresult;
      while ((botsectionmesher_meshresult = this.uploads.poll()) != null) {
         discardResult(botsectionmesher_meshresult);
      }
   }

   public static void discardResult(BotMeshResult var0) {
      for (BotLayerMesh botsectionmesher_layermesh : var0.layers()) {
         botsectionmesher_layermesh.buffer().close();
         botsectionmesher_layermesh.allocator().close();
      }
   }

   public void scheduleEntitySnapshot(BotWorld var1) {
      if (this.entitySnapshotScheduled.compareAndSet(false, true)) {
         this.client.execute(() -> {
            try {
               ArrayList arraylist = new ArrayList();
               HashSet hashset = new HashSet();

               for (Entity entity : var1.getEntities()) {
                  arraylist.add(entity);
                  if (entity instanceof ItemFrameEntity) {
                     hashset.add(entity.getId());
                  }
               }

               this.entitySnapshot = arraylist;
               this.itemFramePositions.keySet().retainAll(hashset);
            } catch (Throwable var8) {
            } finally {
               this.entitySnapshotScheduled.set(false);
            }
         });
      }
   }

   public void ensureFbo(int var1, int var2) {
      if (this.fbo == null || this.fbo.textureWidth != var1 || this.fbo.textureHeight != var2) {
         if (this.fbo != null) {
            this.fbo.delete();
         }

         this.fbo = new SimpleFramebuffer("Zenith bot preview", var1, var2, true);
      }
   }

   public void renderHand(BotWorld var1, BotPlayer var2, float var3, float var4, float var5) {
      RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.fbo.getDepthAttachment(), 1.0);
      this.handProjection.identity().perspective(1.2217305F, var4, 0.05F, var5);
      RenderSystem.setProjectionMatrix(this.projectionBuffer.set(this.handProjection), ProjectionType.PERSPECTIVE);
      OrderedRenderCommandQueueImpl queue = minecraftClient3.gameRenderer.getEntityRenderCommandQueue();
      MatrixStack matrixstack = new MatrixStack();
      int i = WorldRenderer.getLightmapCoordinates(var1, BlockPos.ofFloored(var2.getEyePos()));
      this.heldItem.render(var2, this.client.getPlayHandler(), var3, matrixstack, queue, i);
      this.flushEntityCommands();
   }

   public void restoreDispatchers() {
      if (minecraftClient3.world != null) {
         Camera camerax = minecraftClient3.gameRenderer.getCamera();
         minecraftClient3.getEntityRenderDispatcher().configure(camerax, minecraftClient3.targetedEntity);
         minecraftClient3.getBlockEntityRenderDispatcher().configure(camerax);
      }
   }

   public static float[] skyColor(BotWorld var0, BotPlayer var1) {
      float f = BotLightmap.getSkyBrightness(var0);

      int i;
      try {
         i = var0.getEnvironmentAttributes().getAttributeValue(net.minecraft.world.attribute.EnvironmentAttributes.SKY_COLOR_VISUAL, var1.getBlockPos());
      } catch (Exception exception) {
         i = 7907327;
      }

      return new float[]{ColorHelper.getRed(i) / 255.0F * f, ColorHelper.getGreen(i) / 255.0F * f, ColorHelper.getBlue(i) / 255.0F * f};
   }
}
