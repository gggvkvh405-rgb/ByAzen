package rtx.kimiko.mixin.accessor;

import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.item.model.special.SpecialModelRenderer;
import net.minecraft.client.render.model.json.Transformation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemRenderState.LayerRenderState.class)
public interface ItemLayerRenderStateAccessor {
    @Accessor("glint")
    public ItemRenderState.Glint kimiko_getFoilType();

    @Accessor("useLight")
    public boolean kimiko_getUsesBlockLight();

    @Accessor("specialModelType")
    public SpecialModelRenderer<Object> kimiko_getSpecialRenderer();

    @Accessor("transform")
    public Transformation kimiko_getItemTransform();

    @Accessor("tints")
    public int[] kimiko_getTintLayers();
}
