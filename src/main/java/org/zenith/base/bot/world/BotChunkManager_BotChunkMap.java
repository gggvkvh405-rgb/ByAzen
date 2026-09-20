package org.zenith.base.bot.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.concurrent.atomic.AtomicReferenceArray;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;

final class BotChunkManager_BotChunkMap {
   public final BotChunkManager this_0;
   final AtomicReferenceArray<WorldChunk> chunks;
   final LongOpenHashSet activeSections;
   final int radius;
   public final int diameter;
   volatile int centerChunkX;
   volatile int centerChunkZ;
   int loadedChunkCount;

   BotChunkManager_BotChunkMap(BotChunkManager var1, int var2) {
      this.this_0 = var1;
      this.activeSections = new LongOpenHashSet();
      this.radius = var2;
      this.diameter = var2 * 2 + 1;
      this.chunks = new AtomicReferenceArray<>(this.diameter * this.diameter);
   }

   int getIndex(int var1, int var2) {
      return Math.floorMod(var2, this.diameter) * this.diameter + Math.floorMod(var1, this.diameter);
   }

   void set(int var1, WorldChunk var2) {
      WorldChunk worldchunk = this.chunks.getAndSet(var1, var2);
      if (worldchunk != null) {
         this.loadedChunkCount--;
         this.unloadChunkSections(worldchunk);
         this.this_0.world.unloadBlockEntities(worldchunk);
      }

      if (var2 != null) {
         this.loadedChunkCount++;
         this.loadChunkSections(var2);
      }
   }

   void unloadChunk(int var1, WorldChunk var2) {
      if (this.chunks.compareAndSet(var1, var2, null)) {
         this.loadedChunkCount--;
         this.unloadChunkSections(var2);
      }

      this.this_0.world.unloadBlockEntities(var2);
   }

   void onSectionStatusChanged(int var1, int var2, int var3, boolean var4) {
      if (this.isInRadius(var1, var3)) {
         long i = ChunkSectionPos.asLong(var1, var2, var3);
         if (var4) {
            this.activeSections.add(i);
         } else if (this.activeSections.remove(i)) {
            this.this_0.world.onChunkUnload(i);
         }
      }
   }

   public void unloadChunkSections(WorldChunk var1) {
      ChunkSection[] achunksection = var1.getSectionArray();

      for (int i = 0; i < achunksection.length; i++) {
         ChunkPos chunkpos = var1.getPos();
         this.activeSections.remove(ChunkSectionPos.asLong(chunkpos.x, var1.sectionIndexToCoord(i), chunkpos.z));
      }
   }

   public void loadChunkSections(WorldChunk var1) {
      ChunkSection[] achunksection = var1.getSectionArray();

      for (int i = 0; i < achunksection.length; i++) {
         if (achunksection[i].isEmpty()) {
            ChunkPos chunkpos = var1.getPos();
            this.activeSections.add(ChunkSectionPos.asLong(chunkpos.x, var1.sectionIndexToCoord(i), chunkpos.z));
         }
      }
   }

   void refreshSections(WorldChunk var1) {
      ChunkPos chunkpos = var1.getPos();
      ChunkSection[] achunksection = var1.getSectionArray();

      for (int i = 0; i < achunksection.length; i++) {
         long j = ChunkSectionPos.asLong(chunkpos.x, var1.sectionIndexToCoord(i), chunkpos.z);
         if (achunksection[i].isEmpty()) {
            this.activeSections.add(j);
         } else if (this.activeSections.remove(j)) {
            this.this_0.world.onChunkUnload(j);
         }
      }
   }

   boolean isInRadius(int var1, int var2) {
      return Math.abs(var1 - this.centerChunkX) <= this.radius && Math.abs(var2 - this.centerChunkZ) <= this.radius;
   }

   WorldChunk getChunk(int var1) {
      return this.chunks.get(var1);
   }
}
