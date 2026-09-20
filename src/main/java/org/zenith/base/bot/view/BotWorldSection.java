package org.zenith.base.bot.view;

import com.mojang.blaze3d.buffers.GpuBuffer;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.util.math.ChunkSectionPos;

final class BotWorldSection {
   final long pos;
   final Map<BlockRenderLayer, TerrainBuffer> buffers = new EnumMap<>(BlockRenderLayer.class);
   List<BlockEntity> blockEntities = List.of();
   boolean building;

   BotWorldSection(long var1) {
      this.pos = var1;
   }

   int originX() {
      return ChunkSectionPos.unpackX(this.pos) << 4;
   }

   int originY() {
      return ChunkSectionPos.unpackY(this.pos) << 4;
   }

   int originZ() {
      return ChunkSectionPos.unpackZ(this.pos) << 4;
   }

   double squaredDistanceTo(double var1, double var3, double var5) {
      double d0 = this.originX() + 8.0 - var1;
      double d1 = this.originY() + 8.0 - var3;
      double d2 = this.originZ() + 8.0 - var5;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   void closeBuffers() {
      for (TerrainBuffer buffer : this.buffers.values()) {
         buffer.close();
      }

      this.buffers.clear();
      this.blockEntities = List.of();
   }

   record TerrainBuffer(GpuBuffer vertexBuffer, int indexCount) implements AutoCloseable {
      @Override
      public void close() {
         this.vertexBuffer.close();
      }
   }
}
