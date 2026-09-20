package rtx.kimiko.api.modules.impl.Utils;
import rtx.kimiko.api.events.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import rtx.kimiko.api.events.impl.game.TickEvent;
import rtx.kimiko.api.modules.Category;
import rtx.kimiko.api.modules.Module;

public final class CrystalOptimizer
extends Module {
    public CrystalOptimizer() {
        super("Crystal Optimizer", "\u0423\u0431\u0438\u0440\u0430\u0435\u0442 \u0437\u0430\u0434\u0435\u0440\u0436\u043a\u0443 \u0440\u0430\u0437\u0431\u0438\u0432\u0430\u043d\u0438\u044f \u044d\u043d\u0434-\u043a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u043e\u0432 \u043f\u0440\u0438 \u0443\u0434\u0435\u0440\u0436\u0430\u043d\u0438\u0438 \u0430\u0442\u0430\u043a\u0438.", Category.UTILS);
    }

    @EventHandler
    public void onTick(TickEvent tickEvent) {
        if (!tickEvent.isPre()) {
            return;
        }
        if (this.mc.player == null || this.mc.world == null || this.mc.interactionManager == null || this.mc.currentScreen != null) {
            return;
        }
        if (!this.mc.options.attackKey.isPressed()) {
            return;
        }
        HitResult hitResult = this.mc.crosshairTarget;
        if (!(hitResult instanceof EntityHitResult entityHitResult) || !(entityHitResult.getEntity() instanceof EndCrystalEntity endCrystalEntity)) {
            return;
        }
        this.mc.player.resetTicksSince();
        this.mc.interactionManager.attackEntity((PlayerEntity)(Object)this.mc.player, (Entity)endCrystalEntity);
        this.mc.player.swingHand(Hand.MAIN_HAND);
    }
}

