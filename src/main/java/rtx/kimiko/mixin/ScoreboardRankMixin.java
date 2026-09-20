package rtx.kimiko.mixin;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import rtx.kimiko.api.modules.impl.Utils.StreamerMode;

@Mixin(net.minecraft.client.gui.hud.InGameHud.class)

public abstract class ScoreboardRankMixin {
    @WrapOperation(method="renderScoreboardSidebar", at={@At(value="INVOKE", target="Lnet/minecraft/client/gui/DrawContext;method_51439(Lnet/minecraft/class_327;Lnet/minecraft/class_2561;IIIZ)V")}, require = 0)
    private void kimiko_scoreboardRank(DrawContext graphics, TextRenderer font, Text text, int x, int y, int color, boolean shadow, Operation<Void> original) {
        original.call(new Object[]{graphics, font, StreamerMode.applySelfRank(text), x, y, color, shadow});
    }
}

