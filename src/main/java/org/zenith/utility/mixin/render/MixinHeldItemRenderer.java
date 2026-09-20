package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.event.EventItemRenderHook;
import org.zenith.module.combat.Aura;
import org.zenith.module.render.HandFire;
import org.zenith.module.render.SwingAnimation;
import org.zenith.module.render.ViewModel;

@Mixin(HeldItemRenderer.class)
public abstract class MixinHeldItemRenderer {
   @Shadow
   protected abstract void swingArm(float swingProgress, MatrixStack matrices, int direction, Arm arm);

   @Inject(
      method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
      at = @At("HEAD")
   )
   public void handFireBeginFrame(float var1, MatrixStack var2, OrderedRenderCommandQueue var3, ClientPlayerEntity var4, int var5, CallbackInfo var6) {
      HandFire l1ii1iilii1i11lill1lll = HandFire.handFire;
      if (l1ii1iilii1i11lill1lll.double156()) {
         l1ii1iilii1i11lill1lll.beginQueuedCapture();
      }
   }

   @Inject(
      method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
      at = @At("TAIL")
   )
   public void handFireEndFrame(float var1, MatrixStack var2, OrderedRenderCommandQueue var3, ClientPlayerEntity var4, int var5, CallbackInfo var6) {
      HandFire l1ii1iilii1i11lill1lll = HandFire.handFire;
      if (l1ii1iilii1i11lill1lll.double156()) {
         l1ii1iilii1i11lill1lll.endQueuedCapture();
      }
   }

   @Inject(
      method = "renderFirstPersonItem",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
         ordinal = 0
      )
   )
   public void injectBeforeRenderCrossBowItem(
      AbstractClientPlayerEntity var1,
      float var2,
      float var3,
      Hand var4,
      float var5,
      ItemStack var6,
      float var7,
      MatrixStack var8,
      OrderedRenderCommandQueue var9,
      int var10,
      CallbackInfo var11
   ) {
      ViewModel il11liii1l1li = ViewModel.viewModel;
      if (il11liii1l1li.isEnabled()) {
         boolean flag = var4 == Hand.MAIN_HAND;
         Arm arm = flag ? var1.getMainArm() : var1.getMainArm().getOpposite();
         il11liii1l1li.on23(var8, arm);
      }
   }

   @Inject(
      method = "renderFirstPersonItem",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V",
         ordinal = 1
      )
   )
   public void injectBeforeRenderItem(
      AbstractClientPlayerEntity var1,
      float var2,
      float var3,
      Hand var4,
      float var5,
      ItemStack var6,
      float var7,
      MatrixStack var8,
      OrderedRenderCommandQueue var9,
      int var10,
      CallbackInfo var11
   ) {
      ViewModel il11liii1l1li = ViewModel.viewModel;
      if (il11liii1l1li.isEnabled()) {
         boolean flag = var4 == Hand.MAIN_HAND;
         Arm arm = flag ? var1.getMainArm() : var1.getMainArm().getOpposite();
         il11liii1l1li.on23(var8, arm);
      }
   }

   @Inject(
      method = "renderFirstPersonItem",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/util/math/MatrixStack;push()V", shift = Shift.AFTER, ordinal = 0)
   )
   public void injectAfterMatrixPushHandPosition(
      AbstractClientPlayerEntity var1,
      float var2,
      float var3,
      Hand var4,
      float var5,
      ItemStack var6,
      float var7,
      MatrixStack var8,
      OrderedRenderCommandQueue var9,
      int var10,
      CallbackInfo var11
   ) {
      ViewModel il11liii1l1li = ViewModel.viewModel;
      if (il11liii1l1li.isEnabled() && !var6.isEmpty() && !var6.contains(DataComponentTypes.MAP_ID)) {
         boolean flag = var4 == Hand.MAIN_HAND;
         Arm arm = flag ? var1.getMainArm() : var1.getMainArm().getOpposite();
         il11liii1l1li.UiAnimation(var8, arm);
      }
   }

   @Redirect(
      method = "renderFirstPersonItem",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;swingArm(FLnet/minecraft/client/util/math/MatrixStack;ILnet/minecraft/util/Arm;)V",
         ordinal = 2
      )
   )
   public void redirectSwingArmForCustomAnim(
      HeldItemRenderer var1,
      float var2,
      MatrixStack var4,
      int var5,
      Arm var6,
      @Local(argsOnly = true, ordinal = 3) float equipProgress
   ) {
      SwingAnimation ill11li1lilllil = SwingAnimation.swingAnimation;
      if (ill11li1lilllil.isEnabled()) {
         if (var6 == Arm.RIGHT) {
            if (ill11li1lilllil.onlyAura2.isEnabled() && Aura.aura.isEnabled() && Aura.aura.zClass054() != null) {
               ill11li1lilllil.on23(var4, var2, equipProgress, var6);
            } else if (!ill11li1lilllil.onlyAura2.isEnabled()) {
               ill11li1lilllil.on23(var4, var2, equipProgress, var6);
            } else if (ill11li1lilllil.onlyAura2.isEnabled() && !Aura.aura.isEnabled() || Aura.aura.zClass054() == null) {
               this.swingArm(var2, var4, var5, var6);
            }
         } else {
            this.swingArm(var2, var4, var5, var6);
         }
      } else {
         this.swingArm(var2, var4, var5, var6);
      }
   }

   @WrapOperation(
      method = "renderItem(FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/network/ClientPlayerEntity;I)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/render/item/HeldItemRenderer;renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;I)V"
      )
   )
   public void itemRenderHook(
      HeldItemRenderer var1,
      AbstractClientPlayerEntity var2,
      float var3,
      float var4,
      Hand var5,
      float var6,
      ItemStack var7,
      float var8,
      MatrixStack var9,
      OrderedRenderCommandQueue var10,
      int var11,
      Operation<Void> var12
   ) {
      EventItemRenderHook illli1l1llii1ii1ii1llllii1i1l = new EventItemRenderHook(var2, var7, var5);
      EventManager.call(illli1l1llii1ii1ii1llllii1i1l);
      var12.call(
         new Object[]{
            var1,
            illli1l1llii1ii1ii1llllii1i1l.AutoWarden(),
            var3,
            var4,
            illli1l1llii1ii1ii1llllii1i1l.BaseFinder(),
            var6,
            illli1l1llii1ii1ii1llllii1i1l.AutoZamok(),
            var8,
            var9,
            var10,
            var11
         }
      );
   }
}
