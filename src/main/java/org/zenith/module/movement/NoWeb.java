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
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import org.zenith.core.EffectEngine;
import org.zenith.event.EventEntityCollision;
import org.zenith.util.MovementUtils;

@ModuleInfo(name = "NoWeb", category = Category.MOVEMENT, description = "Noweb")
public final class NoWeb extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final NoWeb noWeb = new NoWeb();

   @EventTarget
   public void UiAnimation(EventEntityCollision var1) {
      if (var1.AutoSprint() == Blocks.COBWEB) {
         if (EffectEngine.on23(minecraftClient3.options.jumpKey.getDefaultKey())) {
            minecraftClient3.player.setVelocity(new Vec3d(0.0, 1.0, 0.0));
         } else if (EffectEngine.on23(minecraftClient3.options.sneakKey.getDefaultKey())) {
            minecraftClient3.player.setVelocity(new Vec3d(0.0, -3.0, 0.0));
         }

         if (minecraftClient3.player.input.getMovementInput().y != 0.0F || minecraftClient3.player.input.getMovementInput().x != 0.0F) {
            double[] adouble = MovementUtils.FileLogger(0.62F);
            minecraftClient3.player.setVelocity(new Vec3d(adouble[0], minecraftClient3.player.getVelocity().y, adouble[1]));
         }
      }
   }
}
