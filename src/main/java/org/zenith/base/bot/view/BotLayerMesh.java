package org.zenith.base.bot.view;

import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.util.BufferAllocator;

record BotLayerMesh(BlockRenderLayer layer, BuiltBuffer buffer, BufferAllocator allocator) {
}
