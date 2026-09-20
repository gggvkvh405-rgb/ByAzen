package rtx.kimiko.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import rtx.kimiko.api.modules.impl.Visuals.Ambience;
import rtx.kimiko.api.modules.impl.Visuals.FogBlur;
import rtx.kimiko.api.modules.impl.Visuals.KillEffect;

@Mixin(FogRenderer.class)
public abstract class FogRendererAmbienceMixin {
    @ModifyArg(method="applyFog", at=@At(value="INVOKE", target="Lnet/minecraft/client/render/fog/FogRenderer;method_71110(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"), index=2, require = 0)
    private Vector4f kimiko_modifyAmbienceFogColor(Vector4f color) {
        return FogRendererAmbienceMixin.applyAmbienceToFog(color);
    }

    @ModifyArgs(method="applyFog", at=@At(value="INVOKE", target="Lnet/minecraft/client/render/fog/FogRenderer;method_71110(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V"), require = 0)
    private void kimiko_scaleFogDistances(Args args) {
        FogBlur fogBlur = FogBlur.getInstance();
        if (fogBlur != null && fogBlur.hasCustomFogDistance()) {
            float factor = fogBlur.getFogDistanceFactor();
            for (int i = 3; i <= 8; ++i) {
                float value = ((Float)args.get(i)).floatValue();
                args.set(i, (Object)Float.valueOf(value * factor));
            }
        }
    }

    @Inject(method="applyFog", at={@At(value="RETURN")}, cancellable=true, require = 0)
    private void kimiko_setupAmbienceFog(Camera camera, int renderDistance, RenderTickCounter deltaTracker, float tickProgress, ClientWorld level, CallbackInfoReturnable<Vector4f> cir) {
        Vector4f modified = FogRendererAmbienceMixin.applyAmbienceToFog((Vector4f)cir.getReturnValue());
        if (modified != null && modified != cir.getReturnValue()) {
            cir.setReturnValue(modified);
        }
    }

    private static Vector4f applyAmbienceToFog(Vector4f fogColor) {
        float saturation;
        boolean fogBlurColor;
        if (fogColor == null) {
            return null;
        }
        Ambience ambience = Ambience.getInstance();
        FogBlur fogBlur = FogBlur.getInstance();
        boolean ambienceOn = ambience != null && ambience.isEnabled();
        boolean bl = fogBlurColor = fogBlur != null && fogBlur.hasCustomFogColor();
        if (!ambienceOn && !fogBlurColor) {
            return fogColor;
        }
        float r = fogColor.x;
        float g = fogColor.y;
        float b = fogColor.z;
        boolean changed = false;
        if (fogBlurColor) {
            int customColor = fogBlur.getCustomFogColor();
            r = (float)(customColor >> 16 & 0xFF) / 255.0f;
            g = (float)(customColor >> 8 & 0xFF) / 255.0f;
            b = (float)(customColor & 0xFF) / 255.0f;
            changed = true;
        }
        if (Float.isFinite(saturation = KillEffect.getWorldSaturationMultiplier() * (ambienceOn ? ambience.getSaturationFactor() : 1.0f)) && Math.abs(saturation - 1.0f) > 5.0E-4f) {
            float lum = r * 0.2126f + g * 0.7152f + b * 0.0722f;
            r = MathHelper.clamp((float)(lum + (r - lum) * saturation), (float)0.0f, (float)1.0f);
            g = MathHelper.clamp((float)(lum + (g - lum) * saturation), (float)0.0f, (float)1.0f);
            b = MathHelper.clamp((float)(lum + (b - lum) * saturation), (float)0.0f, (float)1.0f);
            changed = true;
        }
        return changed ? new Vector4f(r, g, b, fogColor.w) : fogColor;
    }
}
