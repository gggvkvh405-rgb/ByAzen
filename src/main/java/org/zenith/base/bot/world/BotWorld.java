package org.zenith.base.bot.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Deque;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld.Properties;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.map.MapState;
import net.minecraft.network.packet.Packet;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.BlockParticleEffect;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.collection.WeightedPool;
import net.minecraft.world.EntityList;
import net.minecraft.world.World;
import net.minecraft.world.WorldProperties;
import net.minecraft.world.attribute.WorldEnvironmentAttributeAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.ColorResolver;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.entity.ClientEntityManager;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.minecraft.world.tick.EmptyTickSchedulers;
import net.minecraft.world.tick.QueryableTickScheduler;
import net.minecraft.world.tick.TickManager;
import org.zenith.base.bot.net.BotPlayHandler;

public final class BotWorld extends World {
   final EntityList entityList = new EntityList();
   public final ClientEntityManager<Entity> entityManager = new ClientEntityManager(Entity.class, new BotWorld_BotEntityHandler(this));
   public final BotPlayHandler networkHandler;
   public final Properties clientWorldProperties;
   public final TickManager tickManager;
   final List<PlayerEntity> players = Lists.newArrayList();
   final List<EnderDragonPart> enderDragonParts = Lists.newArrayList();
   public final Map<MapIdComponent, MapState> mapStates = Maps.newHashMap();
   public final BotChunkManager chunkManager;
   public final Deque<Runnable> chunkUpdaters = Queues.newArrayDeque();
   public int simulationDistance;
   public final BotPendingUpdateManager pendingUpdateManager = new BotPendingUpdateManager();
   public final int seaLevel;
   private final WorldEnvironmentAttributeAccess environmentAttributes;
   private final WorldBorder worldBorder = new WorldBorder();
   public boolean shouldTickTimeOfDay;
   public BotPlayer botPlayer;
   public volatile BotWorld_RenderListener renderListener;

   public BotWorld(
      BotPlayHandler var1, Properties var2, RegistryKey<World> var3, RegistryEntry<DimensionType> var4, int var5, int var6, boolean var7, long var8, int var10
   ) {
      super(var2, var3, var1.getRegistryManager(), var4, true, var7, var8, 1000000);
      this.networkHandler = var1;
      this.chunkManager = new BotChunkManager(this, var5);
      this.tickManager = new TickManager();
      this.clientWorldProperties = var2;
      this.seaLevel = var10;
      this.environmentAttributes = WorldEnvironmentAttributeAccess.builder().world(this).build();
      this.setSpawnPos(new BlockPos(8, 64, 8), 0.0F);
      this.simulationDistance = var6;
      this.calculateAmbientDarkness();
      this.initWeatherGradients();
   }

   public void setRenderListener(BotWorld_RenderListener var1) {
      this.renderListener = var1;
   }

   @Override
   public WorldBorder getWorldBorder() {
      return this.worldBorder;
   }

   public BotWorld_RenderListener getRenderListener() {
      return this.renderListener;
   }

   public void setBotPlayer(BotPlayer var1) {
      this.botPlayer = var1;
   }

   public BotPlayer getBotPlayer() {
      return this.botPlayer;
   }

   public void handlePlayerActionResponse(int var1) {
      this.pendingUpdateManager.processPendingUpdates(var1, this);
   }

   public void handleBlockUpdate(BlockPos var1, BlockState var2, int var3) {
      if (!this.pendingUpdateManager.hasPendingUpdate(var1, var2)) {
         super.setBlockState(var1, var2, var3, 512);
      }
   }

   public void processPendingUpdate(BlockPos var1, BlockState var2, Vec3d var3) {
      BlockState blockstate = this.getBlockState(var1);
      if (blockstate != var2) {
         this.setBlockState(var1, var2, 19);
         BotPlayer botplayer = this.botPlayer;
         if (botplayer != null && this == botplayer.getWorld() && botplayer.collidesWithStateAtPos(var1, var2)) {
            botplayer.updatePosition(var3.x, var3.y, var3.z);
         }
      }
   }

   public BotPendingUpdateManager getPendingUpdateManager() {
      return this.pendingUpdateManager;
   }

   public boolean setBlockState(BlockPos pos, BlockState state, int flags, int maxUpdateDepth) {
      if (this.pendingUpdateManager.hasPendingSequence() && this.botPlayer != null) {
         BlockState blockstate = this.getBlockState(pos);
         boolean flag = super.setBlockState(pos, state, flags, maxUpdateDepth);
         if (flag) {
            this.pendingUpdateManager.addPendingUpdate(pos, blockstate, this.botPlayer);
         }

         return flag;
      } else {
         return super.setBlockState(pos, state, flags, maxUpdateDepth);
      }
   }

   public void enqueueChunkUpdate(Runnable var1) {
      this.chunkUpdaters.add(var1);
   }

   public void runQueuedChunkUpdates() {
      int i = this.chunkUpdaters.size();
      int j = i < 1000 ? Math.max(10, i / 10) : i;

      for (int k = 0; k < j; k++) {
         Runnable runnable = this.chunkUpdaters.poll();
         if (runnable == null) {
            break;
         }

         runnable.run();
      }
   }

   public void tick(BooleanSupplier var1) {
      this.getWorldBorder().tick();
      this.calculateAmbientDarkness();
      if (this.getTickManager().shouldTick()) {
         this.tickTime();
      }

      this.chunkManager.tick(var1, true);
   }

   public void tickTime() {
      this.clientWorldProperties.setTime(this.clientWorldProperties.getTime() + 1L);
      if (this.shouldTickTimeOfDay) {
         this.clientWorldProperties.setTimeOfDay(this.clientWorldProperties.getTimeOfDay() + 1L);
      }
   }

   public void setTime(long var1, long var3, boolean var5) {
      this.clientWorldProperties.setTime(var1);
      this.clientWorldProperties.setTimeOfDay(var3);
      this.shouldTickTimeOfDay = var5;
   }

   public Iterable<Entity> getEntities() {
      return this.getEntityLookup().iterate();
   }

   public void tickEntities() {
      this.entityList.forEach(var1 -> {
         if (!var1.isRemoved() && !var1.hasVehicle() && !this.tickManager.shouldSkipTick(var1)) {
            this.tickEntity(this::tickEntity, var1);
         }
      });
      this.tickBlockEntities();
   }

   public boolean hasEntity(Entity var1) {
      return this.entityList.has(var1);
   }

   public boolean shouldUpdatePostDeath(Entity entity) {
      BotPlayer botplayer = this.botPlayer;
      return botplayer != null && entity.getChunkPos().getChebyshevDistance(botplayer.getChunkPos()) <= this.simulationDistance;
   }

   public void tickEntity(Entity var1) {
      var1.resetPosition();
      var1.age++;
      var1.tick();

      for (Entity entity : var1.getPassengerList()) {
         this.tickPassenger(var1, entity);
      }
   }

   public void tickPassenger(Entity var1, Entity var2) {
      if (!var2.isRemoved() && var2.getVehicle() == var1) {
         if (var2 instanceof PlayerEntity || this.entityList.has(var2)) {
            var2.resetPosition();
            var2.age++;
            var2.tickRiding();

            for (Entity entity : var2.getPassengerList()) {
               this.tickPassenger(var2, entity);
            }
         }
      } else {
         var2.stopRiding();
      }
   }

   public void unloadBlockEntities(WorldChunk var1) {
      var1.clear();
      this.chunkManager.getLightingProvider().setColumnEnabled(var1.getPos(), false);
      this.entityManager.stopTicking(var1.getPos());
   }

   public void resetChunkColor(ChunkPos var1) {
      this.entityManager.startTicking(var1);
      BotWorld_RenderListener botworld_renderlistener = this.renderListener;
      if (botworld_renderlistener != null) {
         botworld_renderlistener.onChunkChanged(var1.x, var1.z);
      }
   }

   public void onChunkUnload(long var1) {
      BotWorld_RenderListener botworld_renderlistener = this.renderListener;
      if (botworld_renderlistener != null) {
         botworld_renderlistener.onSectionChanged(ChunkSectionPos.unpackX(var1), ChunkSectionPos.unpackY(var1), ChunkSectionPos.unpackZ(var1));
      }
   }

   public boolean isChunkLoaded(int chunkX, int chunkZ) {
      return true;
   }

   public int getRegularEntityCount() {
      return this.entityManager.getEntityCount();
   }

   public void addEntity(Entity var1) {
      this.removeEntity(var1.getId(), RemovalReason.DISCARDED);
      this.entityManager.addEntity(var1);
   }

   public void removeEntity(int var1, RemovalReason var2) {
      Entity entity = (Entity)this.getEntityLookup().get(var1);
      if (entity != null) {
         entity.setRemoved(var2);
         entity.onRemoved();
      }
   }

   public Entity getEntityById(int id) {
      return (Entity)this.getEntityLookup().get(id);
   }

   public void disconnect() {
      this.networkHandler.getConnection().disconnect(Text.translatable("multiplayer.status.quitting"));
   }

   public void playSound(
      Entity source, double x, double y, double z, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed
   ) {
   }

   public void playSoundFromEntity(Entity source, Entity entity, RegistryEntry<SoundEvent> sound, SoundCategory category, float volume, float pitch, long seed) {
   }

   public void playSoundFromEntity(Entity entity, SoundEvent sound, SoundCategory category, float volume, float pitch) {
   }

   public void playSound(double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
   }

   public void addParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
   }

   public void addParticle(
      ParticleEffect parameters, boolean force, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ
   ) {
   }

   public void addImportantParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
   }

   public void addImportantParticle(ParticleEffect parameters, boolean force, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
   }

   public void updateListeners(BlockPos pos, BlockState oldState, BlockState newState, int flags) {
      BotWorld_RenderListener botworld_renderlistener = this.renderListener;
      if (botworld_renderlistener != null) {
         botworld_renderlistener.onBlockChanged(pos);
      }
   }

   public void setBlockBreakingInfo(int entityId, BlockPos pos, int progress) {
   }

   public void syncGlobalEvent(int eventId, BlockPos pos, int data) {
   }

   public void syncWorldEvent(Entity source, int eventId, BlockPos pos, int data) {
   }

   public void addBlockBreakParticles(BlockPos pos, BlockState state) {
   }

   public void emitGameEvent(RegistryEntry<GameEvent> event, Vec3d emitterPos, Emitter emitter) {
   }

   public void createExplosion(
      Entity entity,
      DamageSource damageSource,
      ExplosionBehavior behavior,
      double x,
      double y,
      double z,
      float power,
      boolean createFire,
      ExplosionSourceType explosionSourceType,
      ParticleEffect smallParticle,
      ParticleEffect largeParticle,
      WeightedPool<BlockParticleEffect> blockParticles,
      RegistryEntry<SoundEvent> soundEvent
   ) {
   }

   public void sendPacket(Packet<?> packet) {
      this.networkHandler.sendPacket(packet);
   }

   public RecipeManager getRecipeManager() {
      return this.networkHandler.getRecipeManager();
   }

   public TickManager getTickManager() {
      return this.tickManager;
   }

   public QueryableTickScheduler<Block> getBlockTickScheduler() {
      return EmptyTickSchedulers.getClientTickScheduler();
   }

   public QueryableTickScheduler<Fluid> getFluidTickScheduler() {
      return EmptyTickSchedulers.getClientTickScheduler();
   }

   public BotChunkManager getChunkManager() {
      return this.chunkManager;
   }

   public MapState getMapState(MapIdComponent id) {
      return this.mapStates.get(id);
   }

   public void putClientsideMapState(MapIdComponent var1, MapState var2) {
      this.mapStates.put(var1, var2);
   }

   public void putMapState(MapIdComponent id, MapState state) {
   }

   public MapIdComponent increaseAndGetMapId() {
      return new MapIdComponent(0);
   }

   public Scoreboard getScoreboard() {
      return this.networkHandler.getScoreboard();
   }

   public List<PlayerEntity> getPlayers() {
      return this.players;
   }

   public List<EnderDragonPart> getEnderDragonParts() {
      return this.enderDragonParts;
   }

   public RegistryEntry<Biome> getGeneratorStoredBiome(int biomeX, int biomeY, int biomeZ) {
      return this.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS);
   }

   public float getBrightness(Direction direction, boolean shaded) {
      if (!shaded) {
         return 1.0F;
      }

      return switch (direction) {
         case DOWN -> 0.5F;
         case UP -> 1.0F;
         case NORTH, SOUTH -> 0.8F;
         case WEST, EAST -> 0.6F;
         default -> throw new MatchException(null, null);
      };
   }

   public int getColor(BlockPos pos, ColorResolver colorResolver) {
      return colorResolver.getColor((Biome)this.getBiome(pos).value(), pos.getX(), pos.getZ());
   }

   public void setSpawnPos(BlockPos var1, float var2) {
      this.setSpawnPoint(WorldProperties.SpawnPoint.create(this.getRegistryKey(), var1, var2, 0.0F));
   }

   @Override
   public void setSpawnPoint(WorldProperties.SpawnPoint spawnPoint) {
      this.clientWorldProperties.setSpawnPoint(spawnPoint);
   }

   @Override
   public WorldProperties.SpawnPoint getSpawnPoint() {
      return this.clientWorldProperties.getSpawnPoint();
   }

   public BlockPos getSpawnPos() {
      return this.getSpawnPoint().getPos();
   }

   public float getSpawnAngle() {
      return this.getSpawnPoint().yaw();
   }

   public String toString() {
      return "BotLevel";
   }

   public Properties getLevelProperties() {
      return this.clientWorldProperties;
   }

   public Map<MapIdComponent, MapState> getMapStates() {
      return Map.copyOf(this.mapStates);
   }

   public void putMapStates(Map<MapIdComponent, MapState> var1) {
      this.mapStates.putAll(var1);
   }

   protected EntityLookup<Entity> getEntityLookup() {
      return this.entityManager.getLookup();
   }

   public String asString() {
      return "Chunks[Bot] W: " + this.chunkManager.getDebugString() + " E: " + this.entityManager.getDebugString();
   }

   public void setSimulationDistance(int var1) {
      this.simulationDistance = var1;
   }

   public int getSimulationDistance() {
      return this.simulationDistance;
   }

   public FeatureSet getEnabledFeatures() {
      return this.networkHandler.getEnabledFeatures();
   }

   public BrewingRecipeRegistry getBrewingRecipeRegistry() {
      return this.networkHandler.getBrewingRecipeRegistry();
   }

   public FuelRegistry getFuelRegistry() {
      return this.networkHandler.getFuelRegistry();
   }

   public int getSeaLevel() {
      return this.seaLevel;
   }

   @Override
   public WorldEnvironmentAttributeAccess getEnvironmentAttributes() {
      return this.environmentAttributes;
   }
}
