package org.zenith.base.bot.world;

import net.minecraft.util.math.BlockPos;

public interface BotWorld_RenderListener {
   void onBlockChanged(BlockPos var1);

   void onChunkChanged(int var1, int var2);

   void onSectionChanged(int var1, int var2, int var3);
}
