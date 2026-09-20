package org.zenith.base.bot.world;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.Map;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.ChunkData.BlockEntityVisitor;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.light.LightingProvider;
import org.slf4j.Logger;

public final class BotChunkManager extends ChunkManager {
   public static final Logger LOGGER = LogUtils.getLogger();
   public final WorldChunk emptyChunk;
   public final LightingProvider lightingProvider;
   public volatile BotChunkManager_BotChunkMap chunks;
   public final BotWorld world;

   public BotChunkManager(BotWorld var1, int var2) {
      this.world = var1;
      this.emptyChunk = new EmptyChunk(var1, new ChunkPos(0, 0), var1.getRegistryManager().getOrThrow(RegistryKeys.BIOME).getOrThrow(BiomeKeys.PLAINS));
      this.lightingProvider = new LightingProvider(this, true, var1.getDimension().hasSkyLight());
      this.chunks = new BotChunkManager_BotChunkMap(this, getChunkMapRadius(var2));
   }

   public LightingProvider getLightingProvider() {
      return this.lightingProvider;
   }

   public static boolean positionEquals(WorldChunk var0, int var1, int var2) {
      if (var0 == null) {
         return false;
      }

      ChunkPos chunkpos = var0.getPos();
      return chunkpos.x == var1 && chunkpos.z == var2;
   }

   public void unload(ChunkPos var1) {
      if (this.chunks.isInRadius(var1.x, var1.z)) {
         int i = this.chunks.getIndex(var1.x, var1.z);
         WorldChunk worldchunk = this.chunks.getChunk(i);
         if (positionEquals(worldchunk, var1.x, var1.z)) {
            this.chunks.unloadChunk(i, worldchunk);
            BotWorld_RenderListener botworld_renderlistener = this.world.getRenderListener();
            if (botworld_renderlistener != null) {
               botworld_renderlistener.onChunkChanged(var1.x, var1.z);
            }
         }
      }
   }

   public WorldChunk getChunk(int var1, int var2, ChunkStatus var3, boolean var4) {
      if (this.chunks.isInRadius(var1, var2)) {
         WorldChunk worldchunk = this.chunks.getChunk(this.chunks.getIndex(var1, var2));
         if (positionEquals(worldchunk, var1, var2)) {
            return worldchunk;
         }
      }

      return var4 ? this.emptyChunk : null;
   }

   public BlockView getWorld() {
      return this.world;
   }

   public void onChunkBiomeData(int var1, int var2, PacketByteBuf var3) {
      if (!this.chunks.isInRadius(var1, var2)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", var1, var2);
      } else {
         int i = this.chunks.getIndex(var1, var2);
         WorldChunk worldchunk = this.chunks.getChunk(i);
         if (!positionEquals(worldchunk, var1, var2)) {
            LOGGER.warn("Ignoring chunk since it's not present: {}, {}", var1, var2);
         } else {
            worldchunk.loadBiomeFromPacket(var3);
         }
      }
   }

   public WorldChunk loadChunkFromPacket(int var1, int var2, PacketByteBuf var3, Map<Heightmap.Type, long[]> var4, Consumer<BlockEntityVisitor> var5) {
      if (!this.chunks.isInRadius(var1, var2)) {
         LOGGER.warn("Ignoring chunk since it's not in the view range: {}, {}", var1, var2);
         return null;
      }

      int i = this.chunks.getIndex(var1, var2);
      WorldChunk worldchunk = this.chunks.getChunk(i);
      ChunkPos chunkpos = new ChunkPos(var1, var2);
      if (!positionEquals(worldchunk, var1, var2)) {
         worldchunk = new WorldChunk(this.world, chunkpos);
         worldchunk.loadFromPacket(var3, var4, var5);
         this.chunks.set(i, worldchunk);
      } else {
         worldchunk.loadFromPacket(var3, var4, var5);
         this.chunks.refreshSections(worldchunk);
      }

      this.world.resetChunkColor(chunkpos);
      return worldchunk;
   }

   public void tick(BooleanSupplier shouldKeepTicking, boolean tickChunks) {
   }

   public void setChunkMapCenter(int var1, int var2) {
      this.chunks.centerChunkX = var1;
      this.chunks.centerChunkZ = var2;
   }

   public void updateLoadDistance(int var1) {
      int i = this.chunks.radius;
      int j = getChunkMapRadius(var1);
      if (i != j) {
         BotChunkManager_BotChunkMap botchunkmanager_botchunkmap = new BotChunkManager_BotChunkMap(this, j);
         botchunkmanager_botchunkmap.centerChunkX = this.chunks.centerChunkX;
         botchunkmanager_botchunkmap.centerChunkZ = this.chunks.centerChunkZ;

         for (int k = 0; k < this.chunks.chunks.length(); k++) {
            WorldChunk worldchunk = this.chunks.chunks.get(k);
            if (worldchunk != null) {
               ChunkPos chunkpos = worldchunk.getPos();
               if (botchunkmanager_botchunkmap.isInRadius(chunkpos.x, chunkpos.z)) {
                  botchunkmanager_botchunkmap.set(botchunkmanager_botchunkmap.getIndex(chunkpos.x, chunkpos.z), worldchunk);
               }
            }
         }

         this.chunks = botchunkmanager_botchunkmap;
      }
   }

   public static int getChunkMapRadius(int var0) {
      return Math.max(2, var0) + 3;
   }

   public String getDebugString() {
      return this.chunks.chunks.length() + ", " + this.getLoadedChunkCount();
   }

   public int getLoadedChunkCount() {
      return this.chunks.loadedChunkCount;
   }

   public void onLightUpdate(LightType type, ChunkSectionPos pos) {
      BotWorld_RenderListener botworld_renderlistener = this.world.getRenderListener();
      if (botworld_renderlistener != null) {
         botworld_renderlistener.onSectionChanged(pos.getSectionX(), pos.getSectionY(), pos.getSectionZ());
      }
   }

   public LongOpenHashSet getActiveSections() {
      return this.chunks.activeSections;
   }

   public void onSectionStatusChanged(int x, int sectionY, int z, boolean previouslyEmpty) {
      this.chunks.onSectionStatusChanged(x, sectionY, z, previouslyEmpty);
   }
}
