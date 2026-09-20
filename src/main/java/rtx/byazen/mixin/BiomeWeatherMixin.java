package rtx.byazen.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rtx.byazen.api.modules.impl.Visuals.Ambience;

@Mixin(Biome.class)
public abstract class BiomeWeatherMixin {
    @Inject(method = "hasPrecipitation()Z", at = @At("HEAD"), cancellable = true)
    private void byazen_hasAmbiencePrecipitation(CallbackInfoReturnable<Boolean> cir) {
        Ambience ambience = Ambience.getInstance();
        Biome.Precipitation precipitation = ambience != null ? ambience.precipitation() : null;
        if (precipitation != null) {
            cir.setReturnValue(precipitation != Biome.Precipitation.NONE);
        }
    }

    @Inject(method = "getPrecipitation(Lnet/minecraft/util/math/BlockPos;I)Lnet/minecraft/world/biome/Biome$Precipitation;", at = @At("HEAD"), cancellable = true)
    private void byazen_getAmbiencePrecipitationAtPos(BlockPos pos, int seaLevel, CallbackInfoReturnable<Biome.Precipitation> cir) {
        Ambience ambience = Ambience.getInstance();
        Biome.Precipitation precipitation = ambience != null ? ambience.precipitation() : null;
        if (precipitation != null) {
            cir.setReturnValue(precipitation);
        }
    }

    @Inject(method = "isCold(Lnet/minecraft/util/math/BlockPos;I)Z", at = @At("HEAD"), cancellable = true)
    private void byazen_coldEnoughForAmbienceSnow(BlockPos pos, int seaLevel, CallbackInfoReturnable<Boolean> cir) {
        Ambience ambience = Ambience.getInstance();
        Biome.Precipitation precipitation = ambience != null ? ambience.precipitation() : null;
        if (precipitation == Biome.Precipitation.SNOW) {
            cir.setReturnValue(true);
        } else if (precipitation == Biome.Precipitation.RAIN || precipitation == Biome.Precipitation.NONE) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canSetIce(Lnet/minecraft/world/WorldView;Lnet/minecraft/util/math/BlockPos;)Z", at = @At("HEAD"), cancellable = true)
    private void byazen_warmEnoughForAmbienceRain(WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        Ambience ambience = Ambience.getInstance();
        Biome.Precipitation precipitation = ambience != null ? ambience.precipitation() : null;
        if (precipitation == Biome.Precipitation.SNOW) {
            cir.setReturnValue(false);
        } else if (precipitation == Biome.Precipitation.RAIN) {
            cir.setReturnValue(true);
        }
    }
}
