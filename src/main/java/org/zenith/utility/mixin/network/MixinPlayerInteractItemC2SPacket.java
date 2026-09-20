package org.zenith.utility.mixin.network;

import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.ZenithClient;
import org.zenith.rotation.Rotation;

@Mixin(PlayerInteractItemC2SPacket.class)
public class MixinPlayerInteractItemC2SPacket {
   @Shadow
   public float yaw;
   @Shadow
   public float pitch;

   @Inject(method = "<init>(Lnet/minecraft/util/Hand;IFF)V", at = @At("RETURN"))
   public void modifyRotation(Hand var1, int var2, float var3, float var4, CallbackInfo var5) {
      if (ZenithClient.on23().CloudRouter().ZClass092() != null) {
         Rotation ililiiili1ll1li11 = ZenithClient.on23().CloudRouter().ZClass092();
         this.yaw = ililiiili1ll1li11.GrimGlide();
         this.pitch = ililiiili1ll1li11.GuiWalk();
      }
   }
}
