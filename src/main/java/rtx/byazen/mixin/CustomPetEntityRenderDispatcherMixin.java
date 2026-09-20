package rtx.byazen.mixin;

import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.equipment.EquipmentModelLoader;
import net.minecraft.client.render.entity.model.LoadedEntityModels;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.texture.AtlasManager;
import net.minecraft.client.texture.PlayerSkinCache;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rtx.byazen.api.modules.impl.Visuals.custompet.render.CustomPetRendererBridge;

@Mixin(EntityRenderManager.class)
public abstract class CustomPetEntityRenderDispatcherMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void byazen_bootstrapCustomPetRenderer(MinecraftClient minecraft, TextureManager textureManager, ItemModelManager itemModelResolver, MapRenderer mapRenderer, BlockRenderManager blockRenderDispatcher, AtlasManager atlasManager, TextRenderer font, GameOptions options, Supplier<LoadedEntityModels> modelSetSupplier, EquipmentModelLoader equipmentAssetManager, PlayerSkinCache playerSkinRenderCache, CallbackInfo ci) {
        CustomPetRendererBridge.bootstrap((EntityRenderManager)(Object)this, minecraft, blockRenderDispatcher, itemModelResolver, mapRenderer, atlasManager, font, modelSetSupplier, equipmentAssetManager, playerSkinRenderCache);
    }

    @Inject(method = "getRenderer(Lnet/minecraft/entity/Entity;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    private <T extends Entity> void byazen_useCustomPetRenderer(T entity, CallbackInfoReturnable<EntityRenderer<? super T, ?>> cir) {
        EntityRenderer<? super T, ?> renderer = CustomPetRendererBridge.getCustomRenderer(entity);
        if (renderer != null) {
            cir.setReturnValue(renderer);
        }
    }

    @Inject(method = "getRenderer(Lnet/minecraft/client/render/entity/state/EntityRenderState;)Lnet/minecraft/client/render/entity/EntityRenderer;", at = @At("HEAD"), cancellable = true)
    private <S extends EntityRenderState> void byazen_useCustomPetStateRenderer(S renderState, CallbackInfoReturnable<EntityRenderer<?, ? super S>> cir) {
        EntityRenderer<?, ? super S> renderer = CustomPetRendererBridge.getCustomRenderer(renderState);
        if (renderer != null) {
            cir.setReturnValue(renderer);
        }
    }
}
