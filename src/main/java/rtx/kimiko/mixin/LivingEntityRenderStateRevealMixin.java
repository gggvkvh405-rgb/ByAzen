package rtx.kimiko.mixin;

import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import rtx.kimiko.api.modules.impl.Visuals.seeinvisible.RevealTintHolder;

@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateRevealMixin implements RevealTintHolder {
    @Unique
    private int kimiko_revealTint = -1;

    @Override
    public int kimiko$getRevealTint() {
        return this.kimiko_revealTint;
    }

    @Override
    public void kimiko$setRevealTint(int tint) {
        this.kimiko_revealTint = tint;
    }
}
