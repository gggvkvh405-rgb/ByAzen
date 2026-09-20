package org.zenith.base.bot.view;

import java.util.List;
import net.minecraft.block.entity.BlockEntity;

record BotMeshResult(long sectionPos, List<BotLayerMesh> layers, List<BlockEntity> blockEntities, boolean chunkMissing) {
}
