package org.zenith.utility.mixin.input;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.event.EventTriggerKeyEvent;

@Mixin(Keyboard.class)
public class MixinKeyboard {
   @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
   public void triggerKeyEvent(long var1, int var2, KeyInput var3, CallbackInfo var4) {
      if (var3.key() != -1) {
         EventManager.call(new EventTriggerKeyEvent(var2, var3.key()));
      }
   }
}
