package org.zenith.base.bot.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.BlockRenderLayers;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.BlockModelRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BlockModelPart;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.chunk.WorldChunk;
import org.zenith.base.bot.world.BotWorld;

final class BotSectionMesher {
   public BotSectionMesher() {
   }

   static BotMeshResult build(BotWorld var0, long var1) {
      int i = ChunkSectionPos.unpackX(var1);
      int j = ChunkSectionPos.unpackY(var1);
      int k = ChunkSectionPos.unpackZ(var1);
      WorldChunk worldchunk = var0.getChunkManager().getChunk(i, k, null, false);
      if (worldchunk == null) {
         return new BotMeshResult(var1, List.of(), List.of(), true);
      }

      int l = var0.sectionCoordToIndex(j);
      if (l >= 0 && l < worldchunk.getSectionArray().length && !worldchunk.getSectionArray()[l].isEmpty()) {
         BlockRenderManager blockrendermanager = MinecraftClient.getInstance().getBlockRenderManager();
         BlockPos blockpos = new BlockPos(i << 4, j << 4, k << 4);
         BlockPos blockpos1 = blockpos.add(15, 15, 15);
         Map<BlockRenderLayer, BufferBuilder> hashmap = new HashMap<>();
         Map<BlockRenderLayer, BufferAllocator> hashmap1 = new HashMap<>();
         List<BlockEntity> arraylist = new ArrayList<>();
         List<BlockModelPart> modelParts = new ArrayList<>();
         MatrixStack matrixstack = new MatrixStack();
         Random random = Random.create();
         BlockModelRenderer.enableBrightnessCache();

         try {
            for (BlockPos blockpos2 : BlockPos.iterate(blockpos, blockpos1)) {
               BlockState blockstate = var0.getBlockState(blockpos2);
               if (!blockstate.isAir()) {
                  if (blockstate.hasBlockEntity()) {
                     BlockEntity blockentity = var0.getBlockEntity(blockpos2);
                     if (blockentity != null) {
                        arraylist.add(blockentity);
                     }
                  }

                  FluidState fluidstate = blockstate.getFluidState();
                  if (!fluidstate.isEmpty()) {
                     BlockRenderLayer renderlayer = BlockRenderLayers.getFluidLayer(fluidstate);
                     BufferBuilder bufferbuilder = beginLayer(hashmap, hashmap1, renderlayer);
                     blockrendermanager.renderFluid(blockpos2, var0, bufferbuilder, blockstate, fluidstate);
                  }

                  if (blockstate.getRenderType() == BlockRenderType.MODEL) {
                     BlockRenderLayer renderlayer1 = BlockRenderLayers.getBlockLayer(blockstate);
                     BufferBuilder bufferbuilder1 = beginLayer(hashmap, hashmap1, renderlayer1);
                     random.setSeed(blockstate.getRenderingSeed(blockpos2));
                     blockrendermanager.getModel(blockstate).addParts(random, modelParts);
                     matrixstack.push();
                     matrixstack.translate(blockpos2.getX() & 15, blockpos2.getY() & 15, blockpos2.getZ() & 15);
                     blockrendermanager.renderBlock(blockstate, blockpos2, var0, matrixstack, bufferbuilder1, true, modelParts);
                     matrixstack.pop();
                     modelParts.clear();
                  }
               }
            }
         } finally {
            BlockModelRenderer.disableBrightnessCache();
         }

         ArrayList var25 = new ArrayList(hashmap.size());

         for (Entry<BlockRenderLayer, BufferBuilder> entry : hashmap.entrySet()) {
            BuiltBuffer builtbuffer = entry.getValue().endNullable();
            BufferAllocator bufferallocator = hashmap1.get(entry.getKey());
            if (builtbuffer != null) {
               var25.add(new BotLayerMesh(entry.getKey(), builtbuffer, bufferallocator));
            } else {
               bufferallocator.close();
            }
         }

         return new BotMeshResult(var1, var25, arraylist, false);
      } else {
         return new BotMeshResult(var1, List.of(), List.of(), false);
      }
   }

   public static BufferBuilder beginLayer(Map<BlockRenderLayer, BufferBuilder> var0, Map<BlockRenderLayer, BufferAllocator> var1, BlockRenderLayer var2) {
      BufferBuilder bufferbuilder = var0.get(var2);
      if (bufferbuilder == null) {
         BufferAllocator bufferallocator = new BufferAllocator(var2.getBufferSize());
         var1.put(var2, bufferallocator);
         bufferbuilder = new BufferBuilder(bufferallocator, DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
         var0.put(var2, bufferbuilder);
      }

      return bufferbuilder;
   }
}
