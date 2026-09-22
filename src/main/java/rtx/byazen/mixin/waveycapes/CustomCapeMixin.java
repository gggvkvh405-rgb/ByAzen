package rtx.byazen.mixin.waveycapes;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.SkinTextures;
import net.minecraft.util.AssetInfo;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rtx.byazen.api.modules.impl.Visuals.Customization;
import rtx.byazen.utils.render.cape.CapeGradient;

@Mixin(AbstractClientPlayerEntity.class)
public class CustomCapeMixin {
    @Unique
    private static final Identifier BYAZEN_CAPE_ASSET_ID = Identifier.of("byazen", "capes/cape");
    @Unique
    private static final Identifier BYAZEN_CAPE_TEXTURE = Identifier.of("byazen", "textures/capes/cape.png");
    @Unique
    private static final Identifier VANILLA_ELYTRA_ASSET_ID = Identifier.of("minecraft", "entity/equipment/wings/elytra");
    @Unique
    private static final Identifier VANILLA_ELYTRA_TEXTURE = Identifier.of("minecraft", "textures/entity/equipment/wings/elytra.png");
    @Unique
    private static final AssetInfo.TextureAsset BYAZEN_CAPE_ASSET = new AssetInfo.TextureAssetInfo(BYAZEN_CAPE_ASSET_ID, BYAZEN_CAPE_TEXTURE);
    @Unique
    private static final AssetInfo.TextureAsset VANILLA_ELYTRA_ASSET = new AssetInfo.TextureAssetInfo(VANILLA_ELYTRA_ASSET_ID, VANILLA_ELYTRA_TEXTURE);

    @Inject(method="getSkinTextures", at={@At(value="RETURN")}, cancellable=true, require = 0)
    private void byazen_replaceCape(CallbackInfoReturnable<SkinTextures> cir) {
        AbstractClientPlayerEntity player = (AbstractClientPlayerEntity)(Object)this;
        if (!CapeGradient.isEnabled()) {
            return;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || !CustomCapeMixin.byazen_shouldUseCustomCape(player, (AbstractClientPlayerEntity)client.player)) {
            return;
        }
        Customization customization = Customization.getInstance();
        if (customization != null && customization.wingsEnabledFor(player)) {
            return;
        }
        CapeGradient.tick();
        SkinTextures skin = (SkinTextures)cir.getReturnValue();
        if (skin != null) {
            cir.setReturnValue(new SkinTextures(skin.body(), CapeGradient.asset(), skin.elytra() == null ? VANILLA_ELYTRA_ASSET : skin.elytra(), skin.model(), skin.secure()));
        }
    }

    @Unique
    private static boolean byazen_shouldUseCustomCape(AbstractClientPlayerEntity player, AbstractClientPlayerEntity localPlayer) {
        return player.getUuid().equals(localPlayer.getUuid());
    }
}
