package org.zenith.utility.mixin.render;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.zenith.event.EventGetFogColorHook;
import org.zenith.util.ColorUtils;

@Mixin(FogRenderer.class)
public class MixinBackGroundRenderer {
   @Inject(method = "getFogColor", at = @At("HEAD"), cancellable = true)
   private void getFogColorHook(Camera camera, float tickProgress, ClientWorld world, int viewDistance, float skyDarkness, CallbackInfoReturnable<Vector4f> callback) {
      EventGetFogColorHook event = new EventGetFogColorHook();
      EventManager.call(event);
      if (event.isCancelled()) {
         callback.setReturnValue(toVector(event.ItemUseController()));
      }
   }

   @ModifyVariable(
      method = "applyFog(Ljava/nio/ByteBuffer;ILorg/joml/Vector4f;FFFFFF)V",
      at = @At("HEAD"),
      argsOnly = true,
      ordinal = 0
   )
   private Vector4f modifyFogColor(Vector4f original) {
      EventGetFogColorHook event = new EventGetFogColorHook();
      EventManager.call(event);
      return event.isCancelled() ? toVector(event.ItemUseController()) : original;
   }

   private static Vector4f toVector(int color) {
      return new Vector4f(
         ColorUtils.PacketReceiveEvent(color),
         ColorUtils.PacketSendEvent(color),
         ColorUtils.VisualSettingsStore(color),
         ColorUtils.Item(color)
      );
   }
}
