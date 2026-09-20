package rtx.kimiko.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rtx.kimiko.api.events.EventBus;
import rtx.kimiko.api.events.impl.inventory.ClickSlotEvent;
import rtx.kimiko.api.events.impl.player.AttackEntityEvent;
import rtx.kimiko.api.events.impl.player.InteractEntityEvent;
import rtx.kimiko.api.modules.impl.Visuals.custompet.entity.CustomPetEntity;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MultiPlayerGameModeMixin {
    @Inject(method="clickSlot", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void kimiko_clickSlotHook(int containerId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ClickSlotEvent event = EventBus.get().post(new ClickSlotEvent(containerId, slotId, button, actionType));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method="attackEntity", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void kimiko_onAttack(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (target instanceof CustomPetEntity) {
            ci.cancel();
            return;
        }
        AttackEntityEvent attackEvent = EventBus.get().post(new AttackEntityEvent(target));
        InteractEntityEvent interactEvent = EventBus.get().post(new InteractEntityEvent(target));
        if (attackEvent.isCancelled() || interactEvent.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method="interactEntity", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void kimiko_cancelInteract(PlayerEntity player, Entity target, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        this.kimiko_cancelEntityInteraction(target, cir);
    }

    @Inject(method="interactEntityAtLocation", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void kimiko_cancelInteractAt(PlayerEntity player, Entity target, EntityHitResult hitResult, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        this.kimiko_cancelEntityInteraction(target, cir);
    }

    private void kimiko_cancelEntityInteraction(Entity target, CallbackInfoReturnable<ActionResult> cir) {
        if (target instanceof CustomPetEntity) {
            cir.setReturnValue(ActionResult.PASS);
            return;
        }
        InteractEntityEvent event = EventBus.get().post(new InteractEntityEvent(target));
        if (event.isCancelled()) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
