package org.zenith.client.screens.builder;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface PlayerPreview3D_PreviewOverlay {
   void render(MatrixStack var1, VertexConsumerProvider var2, int var3, LivingEntity var4, float var5);
}
