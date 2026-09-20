package rtx.byazen.mixin.accessor;

import net.minecraft.client.render.item.ItemRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderState.class)
public interface ItemStackRenderStateAccessor {
    @Accessor("layers")
    public ItemRenderState.LayerRenderState[] byazen_getLayers();

    @Accessor("layerCount")
    public int byazen_getActiveLayerCount();
}
