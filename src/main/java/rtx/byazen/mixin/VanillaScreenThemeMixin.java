package rtx.byazen.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.ui.BaseScreen;
import rtx.byazen.api.ui.theme.SurfaceTheme;
import rtx.byazen.utils.render.render2d.Render2D;

/**
 * Полные темы для ванильных экранов (идея №50 из IDEAS.md): инвентарь, сундуки, меню получают
 * мягкую заливку в тон темы клиента. Экраны ByAzen рисуют фон сами и не затрагиваются.
 */
@Mixin(net.minecraft.client.gui.screen.Screen.class)
public abstract class VanillaScreenThemeMixin {

    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("HEAD"), require = 0)
    private void byazen_themeVanillaBackground(DrawContext graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if ((Object)this instanceof BaseScreen) {
            return;
        }
        int top = SurfaceTheme.vanillaTint();
        if (top == 0) {
            return;
        }
        int bottom = SurfaceTheme.vanillaTintBottom();
        float width = rtx.byazen.api.drags.Position.screenWidth();
        float height = rtx.byazen.api.drags.Position.screenHeight();
        Render2D.beginFrame(graphics);
        Render2D.rect(0.0f, 0.0f, width, height, 0.0f, top, top, bottom, bottom);
        Render2D.flush();
    }
}
