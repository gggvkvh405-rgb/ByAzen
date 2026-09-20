package rtx.kimiko.api.mods.geckolib.renderer.base;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import rtx.kimiko.api.mods.geckolib.cache.model.GeoBone;

@FunctionalInterface
public interface PerBoneRender<R extends GeoRenderState> {
    void submitRenderTask(RenderPassInfo<R> renderPassInfo, GeoBone bone, OrderedRenderCommandQueue queue);
}
