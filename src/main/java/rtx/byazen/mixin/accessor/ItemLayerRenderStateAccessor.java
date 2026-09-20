package rtx.byazen.mixin.accessor;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.json.Transformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderState.LayerRenderState.class)
public interface ItemLayerRenderStateAccessor {
    @Accessor("glint")
    public ItemRenderState.Glint byazen_getFoilType();

    @Accessor("useLight")
    public boolean byazen_getUsesBlockLight();

    @Accessor("specialModelType")
    public SpecialModelRenderer<Object> byazen_getSpecialRenderer();

    @Accessor("transform")
    public Transformation byazen_getItemTransform();

    @Accessor("tints")
    public int[] byazen_getTintLayers();
}
