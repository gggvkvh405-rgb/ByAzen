package org.zenith.utility.mixin.accessors;

import java.util.Map;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.player.PlayerSkinType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityRenderManager.class)
public interface EntityRenderManagerAccessor {
   @Accessor("playerRenderers")
   Map<PlayerSkinType, PlayerEntityRenderer<AbstractClientPlayerEntity>> zenith_getModelRenderers();
}
