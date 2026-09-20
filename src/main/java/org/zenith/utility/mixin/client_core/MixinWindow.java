package org.zenith.utility.mixin.client_core;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.core.ClientSession;
import org.zenith.event.EventWindowSizeChanged;

@Mixin(Window.class)
public class MixinWindow {
   @Inject(method = "onWindowSizeChanged", at = @At("TAIL"))
   public void onWindowSizeChanged(long var1, int var3, int var4, CallbackInfo var5) {
      EventManager.call(new EventWindowSizeChanged());
   }

   @ModifyArg(
      method = "setTitle",
      at = @At(value = "INVOKE", target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowTitle(JLjava/lang/CharSequence;)V", remap = false),
      index = 1
   )
   public CharSequence zenith_brandWindowTitle(CharSequence var1) {
      return ClientSession.DISPLAY_CREDIT;
   }
}
