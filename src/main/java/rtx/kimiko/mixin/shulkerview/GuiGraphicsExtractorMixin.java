package rtx.kimiko.mixin.shulkerview;
import net.minecraft.client.gui.DrawContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rtx.kimiko.api.mods.shulkerview.hook.ShulkerPreviewGuiGraphics;

@Mixin(net.minecraft.client.gui.DrawContext.class)

public abstract class GuiGraphicsExtractorMixin
implements ShulkerPreviewGuiGraphics {
    @Unique
    private int kimiko_shulkerPreviewMouseX = Integer.MIN_VALUE;
    @Unique
    private int kimiko_shulkerPreviewMouseY = Integer.MIN_VALUE;

    @Override
    public int kimiko_getMouseX() {
        return this.kimiko_shulkerPreviewMouseX;
    }

    @Override
    public int kimiko_getMouseY() {
        return this.kimiko_shulkerPreviewMouseY;
    }

    @Override
    public void kimiko_setMouse(int mouseX, int mouseY) {
        this.kimiko_shulkerPreviewMouseX = mouseX;
        this.kimiko_shulkerPreviewMouseY = mouseY;
    }
}

