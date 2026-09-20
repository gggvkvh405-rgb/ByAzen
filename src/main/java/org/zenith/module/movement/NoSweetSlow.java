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
import org.zenith.event.EventEntityCollision;
import org.zenith.setting.NumberSetting;

@ModuleInfo(name = "NoSweetSlow", category = Category.MOVEMENT, description = "Noweb")
public final class NoSweetSlow extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final NoSweetSlow noSweetSlow = new NoSweetSlow();
   public final NumberSetting speed5 = new NumberSetting("module.noSweetSlow.speed", 1.5F, 0.8F, 1.8F, 0.05F, "module.noSweetSlow.speed.desc", "x");

   @EventTarget
   public void UiAnimation(EventEntityCollision var1) {
      if (var1.AutoSprint() == Blocks.SWEET_BERRY_BUSH) {
         var1.setCancelled(true);
         minecraftClient3.player
            .slowMovement(minecraftClient3.world.getBlockState(var1.BoatHighJump()), new Vec3d(this.speed5.getCurrent(), 0.75, this.speed5.getCurrent()));
      }
   }
}
