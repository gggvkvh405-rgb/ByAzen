package org.zenith.module.movement;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import org.zenith.event.EventTick;

@ModuleInfo(name = "ShulkerJump", category = Category.MOVEMENT, description = "Подлетаешь вверх при взаимодействии с шалкером")
public final class ShulkerJump extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final ShulkerJump shulkerJump = new ShulkerJump();
   int int338 = 0;
   boolean val206 = false;

   @EventTarget
   public void UiAnimation(EventTick var1) {
      if (minecraftClient3.player != null && minecraftClient3.world != null) {
         if (minecraftClient3.player.currentScreenHandler instanceof ShulkerBoxScreenHandler && !this.val206) {
            minecraftClient3.options.jumpKey.setPressed(true);
            minecraftClient3.player.closeHandledScreen();
            minecraftClient3.player.addVelocity(0.0, 2.4, 0.0);
            this.val206 = true;
            this.int338 = 0;
         }

         if (this.val206) {
            if (this.int338 >= 5) {
               minecraftClient3.player.closeHandledScreen();
               minecraftClient3.options.jumpKey.setPressed(false);
               this.val206 = false;
               this.int338 = 0;
            } else {
               this.int338++;
            }
         }
      }
   }
}
