package org.zenith.utility.mixin.client_core;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.event.BlockInteractEvent;
import org.zenith.event.DataChangedEvent;
import org.zenith.event.EventClickSlotHook;
import org.zenith.event.EventInteractBlock;

@Mixin(ClientPlayerInteractionManager.class)
public class MixinClientPlayerInteractionManager {
   @Inject(method = "clickSlot", at = @At("HEAD"), cancellable = true)
   public void clickSlotHook(int var1, int var2, int var3, SlotActionType var4, PlayerEntity var5, CallbackInfo var6) {
      EventClickSlotHook iiil1lil1i1111llili1iilili = new EventClickSlotHook(var1, var2, var3, var4);
      EventManager.call(iiil1lil1i1111llili1iilili);
      if (iiil1lil1i1111llili1iilili.isCancelled()) {
         var6.cancel();
      }
   }

   @Inject(method = "updateBlockBreakingProgress", at = @At("HEAD"), cancellable = true)
   public void injectBlockBreaking(BlockPos var1, Direction var2, CallbackInfoReturnable<Boolean> var3) {
      BlockInteractEvent ilii1lii1liiill = new BlockInteractEvent(var1, var2);
      EventManager.call(ilii1lii1liiill);
      if (ilii1lii1liiill.isCancelled()) {
         var3.setReturnValue(false);
      }
   }

   @Inject(method = "interactItem", at = @At("HEAD"))
   public void interactBlock(PlayerEntity var1, Hand var2, CallbackInfoReturnable<ActionResult> var3) {
      EventManager.call(new EventInteractBlock());
   }

   @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
   public void interactBlock(ClientPlayerEntity var1, Hand var2, BlockHitResult var3, CallbackInfoReturnable<ActionResult> var4) {
      DataChangedEvent l1l1i1i1ii1il1l1li = new DataChangedEvent();
      EventManager.call(l1l1i1i1ii1il1l1li);
      if (l1l1i1i1ii1il1l1li.isCancelled()) {
         var4.setReturnValue(ActionResult.FAIL);
      }
   }
}
