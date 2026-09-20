package org.zenith.core;

import baritone.api.BaritoneAPI;
import baritone.api.Settings;
import baritone.api.utils.IInputOverrideHandler;
import baritone.api.utils.input.Input;
import com.darkmagician6.eventapi.EventManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public final class BaritoneBridge {
   public static final Input[] val482 = new Input[]{
      Input.MOVE_FORWARD, Input.MOVE_BACK, Input.MOVE_LEFT, Input.MOVE_RIGHT, Input.JUMP, Input.SNEAK, Input.SPRINT
   };
   public static Boolean boolean192;
   public static Boolean boolean193;
   public static Boolean boolean194;
   public static Integer integer;
   public static Boolean boolean195;
   public static Double double149;
   public static Double double150;
   public static boolean boolean196;
   public static MinecraftClient minecraftClient3 = MinecraftClient.getInstance();

   public static void float137() {
      Settings settings = BaritoneAPI.getSettings();
      if (settings != null) {
         if (boolean192 == null) {
            boolean192 = (Boolean)settings.allowBreak.value;
            boolean193 = (Boolean)settings.allowPlace.value;
            boolean194 = (Boolean)settings.allowParkourPlace.value;
            integer = (Integer)settings.maxFallHeightNoWater.value;
            boolean195 = (Boolean)settings.remainWithExistingLookDirection.value;
            double149 = (Double)settings.randomLooking.value;
            double150 = (Double)settings.randomLooking113.value;
         }

         settings.allowBreak.value = Boolean.FALSE;
         settings.allowPlace.value = Boolean.FALSE;
         settings.allowParkourPlace.value = Boolean.FALSE;
         settings.maxFallHeightNoWater.value = 6;
         settings.remainWithExistingLookDirection.value = Boolean.FALSE;
         settings.randomLooking.value = 0.6;
         settings.randomLooking113.value = 0.0;
      }
   }

   public static void vec3d16() {
      Settings settings = BaritoneAPI.getSettings();
      if (settings != null && boolean192 != null) {
         settings.allowBreak.value = boolean192;
         settings.allowPlace.value = boolean193;
         settings.allowParkourPlace.value = boolean194;
         settings.maxFallHeightNoWater.value = integer;
         settings.remainWithExistingLookDirection.value = boolean195;
         settings.randomLooking.value = double149;
         settings.randomLooking113.value = double150;
      }

      boolean192 = null;
      boolean193 = null;
      boolean194 = null;
      integer = null;
      boolean195 = null;
      double149 = null;
      double150 = null;
   }

   public static void float138() {
      if (minecraftClient3.player != null && minecraftClient3.options != null && BaritoneAPI.getProvider() != null) {
         if (!BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().isPathing()) {
            if (boolean196) {
               boolean196 = false;

               for (Input input1 : val482) {
                  KeyBinding keybinding1 = on23(input1);
                  if (keybinding1 != null) {
                     keybinding1.setPressed(false);
                  }
               }
            }
         } else {
            IInputOverrideHandler iinputoverridehandler = BaritoneAPI.getProvider().getPrimaryBaritone().getInputOverrideHandler();
            boolean196 = true;

            for (Input input : val482) {
               if (input != Input.SPRINT || !minecraftClient3.player.isTouchingWater()) {
                  KeyBinding keybinding = on23(input);
                  if (keybinding != null) {
                     keybinding.setPressed(iinputoverridehandler.isInputForcedDown(input));
                  }
               }
            }
         }
      }
   }

   public static KeyBinding on23(Input var0) {
      return switch (var0) {
         case MOVE_FORWARD -> minecraftClient3.options.forwardKey;
         case MOVE_BACK -> minecraftClient3.options.backKey;
         case MOVE_LEFT -> minecraftClient3.options.leftKey;
         case MOVE_RIGHT -> minecraftClient3.options.rightKey;
         case JUMP -> minecraftClient3.options.jumpKey;
         case SNEAK -> minecraftClient3.options.sneakKey;
         case SPRINT -> minecraftClient3.options.sprintKey;
         default -> null;
      };
   }

   static {
      EventManager.register(new BaritoneGuard());
   }
}
