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
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import org.zenith.event.EventEntityCollision;
import org.zenith.event.EventPushOutOfBlocks;
import org.zenith.setting.MultiSelectSetting;

@ModuleInfo(name = "NoPush", description = "No Push", category = Category.MOVEMENT)
public final class NoPush extends Module {
   public final MultiSelectSetting modeSetting12 = MultiSelectSetting.on23(
      "module.noPush.ignoreSetting",
      "module.noPush.ignoreSetting.desc",
      List.of(
         "module.noPush.ignoreSetting.water",
         "module.noPush.ignoreSetting.blocks",
         "module.noPush.ignoreSetting.entities",
         "module.noPush.ignoreSetting.snow",
         "module.noPush.ignoreSetting.berries"
      )
   );
   public static final NoPush noPush = new NoPush();

   @EventTarget
   public void on23(EventPushOutOfBlocks var1) {
      switch (var1.BoatLongJump()) {
         case ENTITY:
            var1.setCancelled(this.modeSetting12.ConfigJsonUtil(2));
            break;
         case FLUIDS:
            var1.setCancelled(this.modeSetting12.ConfigJsonUtil(0));
            break;
         case BLOCKS:
            var1.setCancelled(this.modeSetting12.ConfigJsonUtil(1));
      }
   }

   @EventTarget
   public void on23(EventEntityCollision var1) {
      Block block = var1.AutoSprint();
      if (block.equals(Blocks.POWDER_SNOW)) {
         var1.setCancelled(this.modeSetting12.ConfigJsonUtil(3));
      } else if (block.equals(Blocks.SWEET_BERRY_BUSH)) {
         var1.setCancelled(this.modeSetting12.ConfigJsonUtil(4));
      }
   }
}
