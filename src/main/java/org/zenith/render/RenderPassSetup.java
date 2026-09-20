package org.zenith.render;

import com.mojang.blaze3d.systems.RenderPass;
import java.util.function.Consumer;
import net.minecraft.client.render.RenderLayer;

public interface RenderPassSetup {
   RenderLayer zenith$withRenderPassSetup(Consumer<RenderPass> consumer);
}
