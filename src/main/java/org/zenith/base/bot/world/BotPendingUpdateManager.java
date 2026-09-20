package org.zenith.base.bot.world;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

public final class BotPendingUpdateManager {
   public final Long2ObjectOpenHashMap<BotPendingUpdateManager_PendingUpdate> blockPosToPendingUpdate = new Long2ObjectOpenHashMap();
   public int sequence;
   public boolean pendingSequence;

   public void addPendingUpdate(BlockPos var1, BlockState var2, PlayerEntity var3) {
      this.blockPosToPendingUpdate
         .compute(
            var1.asLong(),
            (var3xx, var4) -> var4 != null
               ? var4.withSequence(this.sequence)
               : new BotPendingUpdateManager_PendingUpdate(this.sequence, var2, var3.getEntityPos())
         );
   }

   public boolean hasPendingUpdate(BlockPos var1, BlockState var2) {
      BotPendingUpdateManager_PendingUpdate botpendingupdatemanager_pendingupdate = (BotPendingUpdateManager_PendingUpdate)this.blockPosToPendingUpdate
         .get(var1.asLong());
      if (botpendingupdatemanager_pendingupdate == null) {
         return false;
      }

      botpendingupdatemanager_pendingupdate.setBlockState(var2);
      return true;
   }

   public void processPendingUpdates(int var1, BotWorld var2) {
      ObjectIterator objectiterator = this.blockPosToPendingUpdate.long2ObjectEntrySet().iterator();

      while (objectiterator.hasNext()) {
         Entry entry = (Entry)objectiterator.next();
         BotPendingUpdateManager_PendingUpdate botpendingupdatemanager_pendingupdate = (BotPendingUpdateManager_PendingUpdate)entry.getValue();
         if (botpendingupdatemanager_pendingupdate.sequence <= var1) {
            BlockPos blockpos = BlockPos.fromLong(entry.getLongKey());
            objectiterator.remove();
            var2.processPendingUpdate(blockpos, botpendingupdatemanager_pendingupdate.blockState, botpendingupdatemanager_pendingupdate.playerPos);
         }
      }
   }

   public BotPendingUpdateManager incrementSequence() {
      this.sequence++;
      this.pendingSequence = true;
      return this;
   }

   public void close() {
      this.pendingSequence = false;
   }

   public int getSequence() {
      return this.sequence;
   }

   public boolean hasPendingSequence() {
      return this.pendingSequence;
   }
}
