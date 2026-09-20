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
import java.util.Comparator;
import java.util.Objects;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.zenith.core.EffectEngine;
import org.zenith.event.BlockInteractEvent;
import org.zenith.event.EventMouseScrollHook;
import org.zenith.event.EventTick;
import org.zenith.util.ScreenUtils;
import org.zenith.util.StopWatch;
import org.zenith.util.TaskScheduler;

@ModuleInfo(name = "AutoTool", category = Category.MISC, description = "Выбирает лучший инструмент для добычи блоков")
public final class AutoTool extends Module {
   public static final MinecraftClient minecraftClient3 = MinecraftClient.getInstance();
   public static final AutoTool autoTool = new AutoTool();
   public final StopWatch stopWatch = new StopWatch();
   public Slot slot = null;

   @EventTarget
   public void on23(EventMouseScrollHook var1) {
      if (this.slot != null) {
         var1.setCancelled(true);
      }
   }

   @EventTarget
   public void on23(BlockInteractEvent var1) {
      this.stopWatch.reset();
      if (!Objects.requireNonNull(minecraftClient3.player).isCreative()) {
         Slot slot = this.NbtItemSpec(var1.WaypointData());
         if (slot != null && slot != ScreenUtils.call119()) {
            if (this.slot == null) {
               this.slot = slot;
            }

            if (TaskScheduler.Easing(AutoTool.class)) {
               TaskScheduler.on23(AutoTool.class, () -> {
                  if (!(minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler)) {
                     ScreenUtils.closeScreen();
                  }

                  ScreenUtils.on23(slot, Hand.MAIN_HAND, true);
               });
            }
         }
      }
   }

   @EventTarget
   public void ItemSpec(EventTick var1) {
      if (TaskScheduler.Easing(AutoTool.class)
         && this.slot != null
         && this.stopWatch.BotFeatureRegistry(400.0)
         && !minecraftClient3.options.attackKey.isPressed()) {
         Slot slot = this.slot;
         TaskScheduler.on23(AutoTool.class, () -> {
            if (!(minecraftClient3.player.currentScreenHandler instanceof PlayerScreenHandler)) {
               ScreenUtils.closeScreen();
            }

            ScreenUtils.on23(slot, Hand.MAIN_HAND, true);
         });
         this.slot = null;
      }
   }

   public Slot NbtItemSpec(BlockPos var1) {
      BlockState blockstate = minecraftClient3.world.getBlockState(var1);
      return EffectEngine.ItemSpec(blockstate)
         ? ScreenUtils.call119()
         : minecraftClient3.player
            .playerScreenHandler
            .slots
            .stream()
            .sorted(Comparator.comparing(var0 -> var0.equals(ScreenUtils.call119())))
            .filter(var1x -> var1x.getStack().getMiningSpeedMultiplier(blockstate) != 1.0F)
            .max(Comparator.comparingDouble(var1x -> var1x.getStack().getMiningSpeedMultiplier(blockstate)))
            .orElse(null);
   }
}
