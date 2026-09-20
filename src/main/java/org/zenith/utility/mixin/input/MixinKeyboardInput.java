package org.zenith.utility.mixin.input;

import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.zenith.client.screens.bot.BotControlScreen;
import org.zenith.event.MovementInputEvent;

@Mixin(KeyboardInput.class)
public abstract class MixinKeyboardInput extends Input {
   @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
   public void stopMainPlayerInputDuringBotControl(CallbackInfo var1) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (BotControlScreen.isControlContextActive()) {
         minecraftclient.options.forwardKey.setPressed(false);
         minecraftclient.options.backKey.setPressed(false);
         minecraftclient.options.leftKey.setPressed(false);
         minecraftclient.options.rightKey.setPressed(false);
         minecraftclient.options.jumpKey.setPressed(false);
         minecraftclient.options.sneakKey.setPressed(false);
         minecraftclient.options.sprintKey.setPressed(false);
         this.playerInput = PlayerInput.DEFAULT;
         this.movementVector = Vec2f.ZERO;
         var1.cancel();
      }
   }

   @Inject(method = "tick", at = @At("RETURN"))
   public void tickHook(CallbackInfo var1) {
      MinecraftClient minecraftclient = MinecraftClient.getInstance();
      if (minecraftclient.player != null && minecraftclient.player.input == this) {
         MovementInputEvent liil1llili1i11il1i1illll = new MovementInputEvent(this.playerInput);
         EventManager.call(liil1llili1i11il1i1illll);
         PlayerInput playerinput = liil1llili1i11il1i1illll.NoSweetSlow();
         if (playerinput != null) {
            this.playerInput = new PlayerInput(
               playerinput.forward(),
               playerinput.backward(),
               playerinput.left(),
               playerinput.right(),
               playerinput.jump(),
               playerinput.sneak(),
               playerinput.sprint()
            );
            float forward = KeyboardInput.getMovementMultiplier(playerinput.forward(), playerinput.backward());
            float sideways = KeyboardInput.getMovementMultiplier(playerinput.left(), playerinput.right());
            this.movementVector = new Vec2f(sideways, forward).normalize();
         }
      }
   }
}
