package rtx.byazen.api.mods.geckolib.renderer;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.state.BlockEntityRenderState;
import rtx.byazen.api.mods.geckolib.animatable.GeoBlockEntity;
import rtx.byazen.api.mods.geckolib.renderer.base.GeoRenderState;
import rtx.byazen.api.mods.geckolib.renderer.base.GeoRenderer;

public abstract class GeoBlockRenderer<T extends BlockEntity & GeoBlockEntity, R extends BlockEntityRenderState & GeoRenderState> implements GeoRenderer<T, Void, R> {
}