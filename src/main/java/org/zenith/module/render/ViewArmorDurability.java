package org.zenith.module.render;

import org.zenith.module.Category;
import org.zenith.module.Module;
import org.zenith.module.ModuleInfo;
import org.zenith.module.ModuleManager;
import org.zenith.module.combat.*;
import org.zenith.module.movement.*;
import org.zenith.module.player.*;
import org.zenith.module.render.*;
import org.zenith.module.misc.*;

import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import org.zenith.setting.BooleanSetting;

@ModuleInfo(name = "View Armor Durability", category = Category.RENDER, description = "Показывает прочность брони")
public class ViewArmorDurability extends Module {
   public static final ViewArmorDurability viewArmorDurability = new ViewArmorDurability();
   public final BooleanSetting naSebe = new BooleanSetting("module.viewArmorDurability.naSebe", true);
   public final BooleanSetting naEnemies = new BooleanSetting("module.viewArmorDurability.naEnemies", true);

   public boolean on23(ItemStack var1, boolean var2) {
      if (var1 == null || var1.isEmpty()) {
         return false;
      } else if (!var1.isIn(ItemTags.ARMOR_ENCHANTABLE)) {
         return false;
      } else {
         return var2 && !this.naSebe.isEnabled() ? false : !var2 || this.naEnemies.isEnabled();
      }
   }

   public int BotChatEvent(ItemStack var1) {
      return var1.getItemBarColor();
   }
}
