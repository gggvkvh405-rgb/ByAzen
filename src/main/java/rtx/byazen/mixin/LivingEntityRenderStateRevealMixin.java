package rtx.byazen.mixin;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rtx.byazen.api.modules.impl.Visuals.seeinvisible.RevealTintHolder;

@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateRevealMixin implements RevealTintHolder {
    @Unique
    private int byazen_revealTint = -1;

    @Override
    public int byazen$getRevealTint() {
        return this.byazen_revealTint;
    }

    @Override
    public void byazen$setRevealTint(int tint) {
        this.byazen_revealTint = tint;
    }
}
