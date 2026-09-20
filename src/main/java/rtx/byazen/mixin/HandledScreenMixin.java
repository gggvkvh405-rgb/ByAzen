package rtx.byazen.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.impl.inventory.HandledScreenEvent;
import rtx.byazen.api.modules.impl.Visuals.BetterMinecraft;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin {
    @Shadow
    protected int x;
    @Shadow
    protected int y;
    @Shadow
    @Nullable
    protected Slot focusedSlot;
    @Unique
    private boolean byazen_panelAnimated;

    @Inject(method="init", at={@At(value="TAIL")}, require = 0)
    private void byazen_trackInventoryOpen(CallbackInfo ci) {
        BetterMinecraft.markInventoryOpen();
    }

    @Inject(method="render", at={@At(value="HEAD")}, require = 0)
    private void byazen_animatePanel(DrawContext graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        this.byazen_panelAnimated = BetterMinecraft.inventoryAnimationEnabled();
        if (!this.byazen_panelAnimated) {
            return;
        }
        graphics.getMatrices().pushMatrix();
        graphics.getMatrices().translate(0.0f, BetterMinecraft.inventorySlideOffset());
    }

    @Inject(method="render", at={@At(value="TAIL")}, require = 0)
    private void byazen_endPanelAnimation(DrawContext graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.byazen_panelAnimated) {
            this.byazen_panelAnimated = false;
            graphics.getMatrices().popMatrix();
        }
        EventBus.get().post(new HandledScreenEvent(graphics, this.x, this.y, this.focusedSlot));
    }
}
