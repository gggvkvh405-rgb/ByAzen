package org.zenith.utility.mixin.input;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.input.MouseInput;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.core.ClientProvider;
import org.zenith.core.DrawContextSink;
import org.zenith.client.screens.bot.BotControlScreen;
import org.zenith.event.EventModifyMouseRotationInput;
import org.zenith.event.EventMouseButton;
import org.zenith.event.EventMouseScrollHook;
import org.zenith.event.EventTriggerKeyEvent;

@Mixin(Mouse.class)
public class MixinMouse {
   @Shadow
   private double cursorDeltaX;
   @Shadow
   private double cursorDeltaY;

   @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
   public void onMouseButton(long var1, MouseInput var2, int var3, CallbackInfo var4) {
      int button = var2.button();
      if (button != -1 && var1 == ClientProvider.minecraftClient3.getWindow().getHandle()) {
         EventManager.call(new EventTriggerKeyEvent(var3, button));
         EventMouseButton llilil1lill111 = new EventMouseButton(button, var3);
         EventManager.call(llilil1lill111);
         if (llilil1lill111.isCancelled()) {
            var4.cancel();
         }
      }
   }

   @Inject(
      method = "onMouseScroll",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;"),
      cancellable = true
   )
   public void onMouseScrollHook(long var1, double var3, double var5, CallbackInfo var7) {
      EventMouseScrollHook il11i1li1llll1i1111ll = new EventMouseScrollHook(var3, var5);
      EventManager.call(il11i1li1llll1i1111ll);
      if (il11i1li1llll1i1111ll.isCancelled()) {
         var7.cancel();
      }
   }

   @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Mouse;isCursorLocked()Z"))
   public boolean onIsCursorLocked(Mouse var1) {
      return var1.isCursorLocked() || this.isAnim();
   }

   @WrapWithCondition(
      method = "updateMouse",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"),
      require = 1,
      allow = 1
   )
   public boolean modifyMouseRotationInput(ClientPlayerEntity var1, double var2, double var4) {
      if (BotControlScreen.isControlContextActive()) {
         return false;
      }

      EventModifyMouseRotationInput i1il11ili = new EventModifyMouseRotationInput(var2, var4, this.cursorDeltaX, this.cursorDeltaY);
      EventManager.call(i1il11ili);
      if (i1il11ili.isCancelled()) {
         return false;
      }

      var1.changeLookDirection(i1il11ili.CraftingExecutor(), i1il11ili.BlockPosEntry());
      return false;
   }

   @Unique
   public boolean isAnim() {
      return MinecraftClient.getInstance().currentScreen instanceof DrawContextSink lll111ll1i1l11l1 && lll111ll1i1l11l1.zenith_betterMinecraft_isClosingAnimation();
   }
}
