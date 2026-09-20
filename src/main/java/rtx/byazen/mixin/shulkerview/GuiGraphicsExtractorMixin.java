package rtx.byazen.mixin.shulkerview;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rtx.byazen.api.mods.shulkerview.hook.ShulkerPreviewGuiGraphics;

@Mixin(net.minecraft.client.gui.DrawContext.class)

public abstract class GuiGraphicsExtractorMixin
implements ShulkerPreviewGuiGraphics {
    @Unique
    private int byazen_shulkerPreviewMouseX = Integer.MIN_VALUE;
    @Unique
    private int byazen_shulkerPreviewMouseY = Integer.MIN_VALUE;

    @Override
    public int byazen_getMouseX() {
        return this.byazen_shulkerPreviewMouseX;
    }

    @Override
    public int byazen_getMouseY() {
        return this.byazen_shulkerPreviewMouseY;
    }

    @Override
    public void byazen_setMouse(int mouseX, int mouseY) {
        this.byazen_shulkerPreviewMouseX = mouseX;
        this.byazen_shulkerPreviewMouseY = mouseY;
    }
}

