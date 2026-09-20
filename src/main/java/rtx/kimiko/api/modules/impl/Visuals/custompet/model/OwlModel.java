package rtx.kimiko.api.modules.impl.Visuals.custompet.model;
import net.minecraft.util.Identifier;
import rtx.kimiko.api.mods.geckolib.model.GeoModel;
import rtx.kimiko.api.mods.geckolib.renderer.base.GeoRenderState;
import rtx.kimiko.api.modules.impl.Visuals.custompet.entity.CustomPetEntity;

public class OwlModel
extends GeoModel<CustomPetEntity> {
    private static final Identifier MODEL = Identifier.of((String)"kimiko", (String)"owl/owl_jump_rope");
    private static final Identifier ANIMATIONS = Identifier.of((String)"kimiko", (String)"owl/owl_jump_rope");
    private static final Identifier TEXTURE = Identifier.of((String)"kimiko", (String)"textures/entity/owl/owl_jump_rope.png");

    @Override
    public Identifier getTextureResource(GeoRenderState geoRenderState) {
        return TEXTURE;
    }

    @Override
    public Identifier getModelResource(GeoRenderState geoRenderState) {
        return MODEL;
    }

    public Identifier getAnimationResource(CustomPetEntity customPetEntity) {
        return ANIMATIONS;
    }
}

