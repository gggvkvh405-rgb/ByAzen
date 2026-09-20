package rtx.byazen.mixin;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.modules.impl.Interface.CustomHotbar;

@Mixin(net.minecraft.client.gui.hud.InGameHud.class)

public abstract class CustomHotbarMixin {
    @Shadow
    @Final
    private static Identifier HOTBAR_TEXTURE;
    @Shadow
    @Final
    private static Identifier HOTBAR_SELECTION_TEXTURE;
    @Shadow
    @Final
    private static Identifier HOTBAR_OFFHAND_LEFT_TEXTURE;
    @Shadow
    @Final
    private static Identifier HOTBAR_OFFHAND_RIGHT_TEXTURE;

    @Inject(method="renderHotbar", at={@At(value="HEAD")}, require = 0)
    private void byazen_renderCustomHotbar(DrawContext graphics, RenderTickCounter deltaTracker, CallbackInfo ci) {
        CustomHotbar module = CustomHotbar.getInstance();
        if (module != null && module.isEnabled()) {
            module.render(graphics);
        }
    }

    @Redirect(method="renderHotbar", at=@At(value="INVOKE", target="Lnet/minecraft/client/gui/DrawContext;method_52706(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/class_2960;IIII)V"), require = 0)
    private void byazen_replaceHotbarSprites(DrawContext graphics, RenderPipeline pipeline, Identifier sprite, int x, int y, int width, int height) {
        if (CustomHotbar.isActive() && CustomHotbarMixin.isHotbarFrame(sprite)) {
            return;
        }
        graphics.drawGuiTexture(pipeline, sprite, x, y, width, height);
    }

    private static boolean isHotbarFrame(Identifier sprite) {
        return HOTBAR_TEXTURE.equals((Object)sprite) || HOTBAR_SELECTION_TEXTURE.equals((Object)sprite) || HOTBAR_OFFHAND_LEFT_TEXTURE.equals((Object)sprite) || HOTBAR_OFFHAND_RIGHT_TEXTURE.equals((Object)sprite);
    }
}

