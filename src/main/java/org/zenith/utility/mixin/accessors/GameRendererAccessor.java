package org.zenith.utility.mixin.accessors;

import net.minecraft.client.render.GameRenderer;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {
   @Invoker("renderHand")
   void zenith_renderHand(float tickProgress, boolean sleeping, Matrix4f positionMatrix);
}
