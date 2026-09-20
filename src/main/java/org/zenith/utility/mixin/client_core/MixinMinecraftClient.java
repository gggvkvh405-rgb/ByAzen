package org.zenith.utility.mixin.client_core;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter.Dynamic;
import net.minecraft.client.util.Window;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.ZenithClient;
import org.zenith.event.HotbarInputEvent;
import org.zenith.event.EventHookPacketProcess;
import org.zenith.event.RenderTickEvent;
import org.zenith.event.EventHookTickEvent;
import org.zenith.event.EventInjectHandleInputEvents;
import org.zenith.event.EventMixin_modifySetScreenArg;
import org.zenith.event.PreventActionEvent;
import org.zenith.event.RefreshCacheEvent;
import org.zenith.event.StopUsingItemEvent;
import org.zenith.module.misc.NoInteract;
import org.zenith.module.render.ShaderESP;
import org.zenith.util.TimerSpeed;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {
   @Shadow
   @Final
   public Window window;
   @Shadow
   @Nullable
   public ClientPlayerEntity player;
   @Shadow
   @Nullable
   public ClientWorld world;
   @Shadow
   @Nullable
   public ClientPlayerInteractionManager interactionManager;
   @Shadow
   @Final
   public GameRenderer gameRenderer;
   @Shadow
   public int itemUseCooldown;
   @Shadow
   @Final
   public GameOptions options;
   @Shadow
   @Nullable
   public Screen currentScreen;
   @Shadow
   @Final
   public Dynamic renderTickCounter;
   @Shadow
   public volatile boolean paused;
   @Unique
   public final Dynamic zenith_renderTickCounter = new Dynamic(40.0F, 0L, this::getTargetMillisPerTick);

   @Shadow
   public abstract Window getWindow();

   @Shadow
   protected abstract void doItemUse();

   @Shadow
   protected abstract boolean shouldTick();

   @Shadow
   protected abstract float getTargetMillisPerTick(float var1);

   @Inject(
      method = "setScreen",
      at = @At(
         value = "FIELD",
         target = "Lnet/minecraft/client/MinecraftClient;currentScreen:Lnet/minecraft/client/gui/screen/Screen;",
         ordinal = 3,
         shift = Shift.AFTER
      )
   )
   public void hook(Screen var1, CallbackInfo var2) {
      if (this.currentScreen instanceof TitleScreen) {
      }
   }

   @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;runTasks()V", shift = Shift.BEFORE))
   public void hookPacketProcess(CallbackInfo var1) {
      EventManager.call(new EventHookPacketProcess());
      int i = this.zenith_renderTickCounter.beginRenderTick(Util.getMeasuringTimeMs(), true);

      for (int j = 0; j < Math.min(10, i); j++) {
         EventManager.call(new RenderTickEvent());
      }
   }

   @Inject(
      method = "render",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/RenderTickCounter$Dynamic;setTickFrozen(Z)V", shift = Shift.AFTER)
   )
   public void hookTickRender(CallbackInfo var1) {
      this.zenith_renderTickCounter.tick(this.paused);
      this.zenith_renderTickCounter.setTickFrozen(!this.shouldTick());
   }

   @Inject(method = "tick", at = @At("HEAD"))
   public void hookTickEvent(CallbackInfo var1) {
      EventManager.call(new EventHookTickEvent());
   }

   @Inject(at = @At("HEAD"), method = "close")
   public void stop(CallbackInfo var1) {
      if (ZenithClient.on23 != null) {
         ZenithClient.on23().shutdown();
      }
   }

   @Inject(method = "handleInputEvents", at = @At("HEAD"), cancellable = true)
   public void zenith_skipInputWithoutPlayer(CallbackInfo var1) {
      if (this.player == null || this.interactionManager == null) {
         var1.cancel();
      }
   }

   @Redirect(
      method = "handleInputEvents",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V",
         ordinal = 0
      )
   )
   public void injectHandleInputEventss(ClientPlayNetworkHandler var1, Packet var2) {
      PreventActionEvent lll11lil1illli11lili1ililiiiil = new PreventActionEvent();
      EventManager.call(lll11lil1illli11lili1ililiiiil);
      if (!lll11lil1illli11lili1ililiiiil.isCancelled()) {
         var1.sendPacket(var2);
      }
   }

   @Redirect(
      method = "handleInputEvents",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;stopUsingItem(Lnet/minecraft/entity/player/PlayerEntity;)V",
         ordinal = 0
      )
   )
   public void injectHandleInputEventsss(ClientPlayerInteractionManager var1, PlayerEntity var2) {
      StopUsingItemEvent lil1illiii1li = new StopUsingItemEvent();
      EventManager.call(lil1illiii1li);
      if (!lil1illiii1li.isCancelled()) {
         var1.stopUsingItem(var2);
      }
   }

   @Inject(method = "tick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;overlay:Lnet/minecraft/client/gui/screen/Overlay;"))
   public void injectHandleInputEvents(CallbackInfo var1) {
      EventManager.call(new EventInjectHandleInputEvents());
      PreventActionEvent lll11lil1illli11lili1ililiiiil = new PreventActionEvent();
      EventManager.call(lll11lil1illli11lili1ililiiiil);
      if (!lll11lil1illli11lili1ililiiiil.isCancelled()) {
         RefreshCacheEvent lil11i1111lill1lill = new RefreshCacheEvent();
         EventManager.call(lil11i1111lill1lill);
      }
   }

   @ModifyReturnValue(method = "getTargetMillisPerTick", at = @At("RETURN"))
   public float zenith_applyTimerSpeed(float var1) {
      float f = TimerSpeed.float136();
      return f == 1.0F ? var1 : var1 / f;
   }

   @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Hand;values()[Lnet/minecraft/util/Hand;"), cancellable = true)
   public void doItemUseHook(CallbackInfo var1) {
      if (this.player != null && this.interactionManager != null) {
         PreventActionEvent lll11lil1illli11lili1ililiiiil = new PreventActionEvent();
         EventManager.call(lll11lil1illli11lili1ililiiiil);
         if (lll11lil1illli11lili1ililiiiil.isCancelled()) {
            var1.cancel();
         }

         if (NoInteract.noInteract.isEnabled()) {
            for (Hand hand : Hand.values()) {
               if (!this.player.getStackInHand(hand).isEmpty()) {
                  ActionResult actionresult = this.interactionManager.interactItem(this.player, hand);
                  if (actionresult.isAccepted()) {
                     if (actionresult instanceof Success success && success.swingSource().equals(SwingSource.CLIENT)) {
                        this.player.swingHand(hand);
                     }

                     this.gameRenderer.firstPersonRenderer.resetEquipProgress(hand);
                     var1.cancel();
                  }
               }
            }
         }
      } else {
         var1.cancel();
      }
   }

   @Inject(
      method = "handleInputEvents",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getInventory()Lnet/minecraft/entity/player/PlayerInventory;"),
      cancellable = true
   )
   public void handleInputEventsHook(CallbackInfo var1) {
      HotbarInputEvent ill1li1iii11i111iliil = new HotbarInputEvent();
      EventManager.call(ill1li1iii11i111iliil);
      if (ill1li1iii11i111iliil.isCancelled()) {
         var1.cancel();
      }
   }

   @Inject(method = "onResolutionChanged", at = @At("TAIL"))
   public void captureResize(CallbackInfo var1) {
      ZenithClient.on23().ModuleStateStore().executorService4();
   }

   @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;blitToScreen()V", shift = Shift.BEFORE))
   public void captureFrame(CallbackInfo var1) {
      ZenithClient.on23().ModuleStateStore().call266();
   }

   @ModifyVariable(method = "setScreen(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("HEAD"), argsOnly = true)
   public Screen mixin_modifySetScreenArg(Screen var1) {
      EventMixin_modifySetScreenArg llli1iilli1ii1 = new EventMixin_modifySetScreenArg(var1);
      EventManager.call(llli1iilli1ii1);
      return llli1iilli1ii1.AutoCraftHelper();
   }

   @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
   public void zenith_shaderEspForceOutline(Entity var1, CallbackInfoReturnable<Boolean> var2) {
      ShaderESP lii1l1ili11ill1l1 = ShaderESP.shaderESP;
      if (lii1l1ili11ill1l1 != null && lii1l1ili11ill1l1.isEnabled() && var1 != null && !var1.isRemoved() && lii1l1ili11ill1l1.BotFeatureRegistry(var1)) {
         var2.setReturnValue(true);
      }
   }
}
