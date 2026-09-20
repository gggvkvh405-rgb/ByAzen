package org.zenith.module.misc;

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
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.util.math.BlockPos;
import org.zenith.event.EventTick;
import org.zenith.event.PacketEvent;
import org.zenith.setting.BooleanSetting;
import org.zenith.setting.NumberSetting;

@ModuleInfo(name = "FastBreak", category = Category.MISC, description = "Ускоряет добычу блоков")
public final class FastBreak extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final FastBreak fastBreak = new FastBreak();
   public final NumberSetting breakDamage = new NumberSetting("module.fastBreak.breakDamage", 0.8F, 0.1F, 1.0F, 0.1F, "module.fastBreak.breakDamage.desc", "%");
   public final BooleanSetting bypass = new BooleanSetting("module.fastBreak.bypass", "module.fastBreak.bypass.desc", true);

   @EventTarget
   public void onUpdate(EventTick var1) {
      minecraftClient3.interactionManager.blockBreakingCooldown = 0;
      if (minecraftClient3.interactionManager.currentBreakingProgress > 0.0F) {
         minecraftClient3.interactionManager.currentBreakingProgress = 1.0F;
      }
   }

   @EventTarget
   public void NbtItemSpec(PacketEvent var1) {
      if (this.bypass.isEnabled()
         && var1.ItemScroller() instanceof PlayerActionC2SPacket playeractionc2spacket
         && playeractionc2spacket.getAction() == Action.STOP_DESTROY_BLOCK) {
         BlockPos blockpos = playeractionc2spacket.getPos();
         if (blockpos != null) {
            minecraftClient3.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.ABORT_DESTROY_BLOCK, blockpos.up(), playeractionc2spacket.getDirection()));
         }
      }
   }
}
