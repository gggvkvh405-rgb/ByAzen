package rtx.byazen.mixin;

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
import rtx.byazen.api.events.EventBus;
import rtx.byazen.api.events.impl.inventory.ClickSlotEvent;
import rtx.byazen.api.events.impl.player.AttackEntityEvent;
import rtx.byazen.api.events.impl.player.InteractEntityEvent;
import rtx.byazen.api.modules.impl.Visuals.custompet.entity.CustomPetEntity;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class MultiPlayerGameModeMixin {
    @Inject(method="clickSlot", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void byazen_clickSlotHook(int containerId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ClickSlotEvent event = EventBus.get().post(new ClickSlotEvent(containerId, slotId, button, actionType));
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    // breakBlock возвращает boolean, поэтому обработчику нужен CallbackInfoReturnable:
    // с обычным CallbackInfo игра падает при загрузке класса («CallbackInfoReturnable is required»).
    @Inject(method="breakBlock", at={@At(value="HEAD")}, require = 0)
    private void byazen_postBlockBreak(net.minecraft.util.math.BlockPos blockPos, CallbackInfoReturnable<Boolean> cir) {
        net.minecraft.client.MinecraftClient client = net.minecraft.client.MinecraftClient.getInstance();
        if (client == null || client.world == null || blockPos == null) {
            return;
        }
        EventBus.get().post(new rtx.byazen.api.events.impl.player.BlockBreakEvent(blockPos, client.world.getBlockState(blockPos)));
    }

    @Inject(method="attackEntity", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void byazen_onAttack(PlayerEntity player, Entity target, CallbackInfo ci) {
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
    private void byazen_cancelInteract(PlayerEntity player, Entity target, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        this.byazen_cancelEntityInteraction(target, cir);
    }

    @Inject(method="interactEntityAtLocation", at={@At(value="HEAD")}, cancellable=true, require = 0)
    private void byazen_cancelInteractAt(PlayerEntity player, Entity target, EntityHitResult hitResult, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        this.byazen_cancelEntityInteraction(target, cir);
    }

    private void byazen_cancelEntityInteraction(Entity target, CallbackInfoReturnable<ActionResult> cir) {
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
